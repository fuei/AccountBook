package org.fuei.app.accountbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.CustomerRemark;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.service.CustomerRemarkService;
import org.fuei.app.accountbook.service.TradeRecordService;
import org.fuei.app.accountbook.util.VariableUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/11.
 */
public class CustomerListFragment extends ListFragment {

    private static final String TAG = "CustomerListFragment";


    private static final String DIALOG_CUSTOMER = "customerDialog";
    private static final int REQUEST_CUSTOMER  = 0;

    private ArrayList<Customer> mCustomers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCustomers = new TradeRecordService().findTradeCustomers(VariableUtils.APPTYPE);

        CustomerAdapter adapter = new CustomerAdapter(mCustomers);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = null;
        if (v != null) {
            listView = (ListView)v.findViewById(android.R.id.list);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (listView != null) {
                registerForContextMenu(listView);
            }
        } else {
            final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(VariableUtils.GetDateStr());
            }

            if (listView != null) {
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        if (actionBar != null) {
                            actionBar.hide();
                        }
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.list_item_context, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete:
                                CustomerAdapter adapter = (CustomerAdapter)getListAdapter();
                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                    if (getListView().isItemChecked(i)) {
                                        Customer c = adapter.getItem(i);
                                        int success = new TradeRecordService().deleteRecordByCustomerId(c.getId());
                                        if (success == 1) {
                                            mCustomers.remove(c);
                                            adapter.remove(c);
                                        }
                                    }
                                }
                                mode.finish();
                                adapter.notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        if (actionBar != null) {
                            actionBar.show();
                        }
                    }
                });
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCustomers = new TradeRecordService(getActivity(), 0).findTradeCustomers(VariableUtils.APPTYPE);

        CustomerAdapter adapter = new CustomerAdapter(mCustomers);
        setListAdapter(adapter);
        ((CustomerAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //获取点击的客户
        Customer customer = ((CustomerAdapter)getListAdapter()).getItem(position);

        //启动该客户的交易列表Activity
        Intent i = new Intent(getActivity(), TradeListActivity.class);
        //传参
        i.putExtra(TradeListFragment.EXTRA_CUSTOMER_ID, customer.getId());
        Log.d(TAG, "customId: " + customer.getId());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            // 弹出选择客户的对话框
            FragmentManager fm = getActivity().getSupportFragmentManager();
            TypePickerFragment dialog = TypePickerFragment.newInstance(VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType(), VariableUtils.ENUM_APP_TYPE.OUT.getAppType());

//            dialog.setTargetFragment(TradeListFragment.class, REQUEST_CUSTOMER);
            dialog.show(fm, DIALOG_CUSTOMER);

            return true;
        } else if (id == R.id.export) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("是否导出账单？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //获取某个市场类型的客户列表，根据客户循环导出excel
                            ArrayList<Customer> customers = new TradeRecordService().findTradeCustomers(VariableUtils.APPTYPE);
                            for (Customer customer: customers) {
                                data2Excel(customer);
                            }
                            Toast.makeText(getActivity(), "导出成功! \n 路径：Android/data/org.fuei.app.accountbook/files", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean data2Excel(Customer customer) {
        String dateStr = VariableUtils.DataDateFormat(VariableUtils.DATADATE);

        InputStream fis = getResources().openRawResource(R.raw.template);;
        Workbook wb = null;
        OutputStream fos = null;
        try {
            wb = new HSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Sheet sheet = null;
            if (wb == null) {
                Toast.makeText(getActivity(), "模板工作簿不存在，导出失败", Toast.LENGTH_LONG);
                return true;
            }
            sheet = wb.getSheetAt(0);
            //表格复制
            setCellValue(wb, sheet, customer, dateStr);

            //创建表格
            File file = VariableUtils.ExportExcel2SDCard(getContext(), VariableUtils.APPTYPE, dateStr, customer.getName());
            if (file == null) {
                Toast.makeText(getActivity(), "导出失败", Toast.LENGTH_LONG);
                return true;
            }
            fos = new FileOutputStream(file);
            wb.write(fos);

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close() ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (wb != null) {
                    wb.close();
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 单元格赋值
     * @param sheet 工作表
     */
    private void setCellValue(Workbook wb, Sheet sheet, Customer customer, String dateStr) {
        VariableUtils.SetSingleCellValue(wb, sheet, "customer_name", customer.getName());
        VariableUtils.SetSingleCellValue(wb, sheet, "data_date", dateStr);

        CustomerRemark customerRemark = new CustomerRemarkService().findRecordByCustomerId(customer.getId());
        if (customerRemark.getWhiteGo() != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "white_go", customerRemark.getWhiteGo()+"");
        }
        if (customerRemark.getGreenGo() != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "green_go", customerRemark.getGreenGo()+"");
        }
        if (customerRemark.getWhiteCome() != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "aaa", customerRemark.getWhiteCome()+"");
        }
        if (customerRemark.getGreenCome() != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "green_come", customerRemark.getGreenCome()+"");
        }

        if (VariableUtils.APPTYPE == VariableUtils.ENUM_APP_TYPE.OUT.getAppType()) {
            if (customerRemark.getWhiteGo() != 0) {
                VariableUtils.SetSingleCellValue(wb, sheet, "w_go_unitprice", VariableUtils.FloatToStr(VariableUtils.WHITE_FRMAE_PRICE));
                VariableUtils.SetSingleCellValue(wb, sheet, "w_go_sumprice", VariableUtils.FloatToStr((customerRemark.getWhiteGo() * VariableUtils.WHITE_FRMAE_PRICE)));
            }

            if (customerRemark.getGreenGo() != 0) {
                VariableUtils.SetSingleCellValue(wb, sheet, "g_go_unitprice", VariableUtils.FloatToStr(VariableUtils.GREEN_FRAME_PRICE));
                VariableUtils.SetSingleCellValue(wb, sheet, "g_go_sumprice", VariableUtils.FloatToStr((customerRemark.getGreenGo() * VariableUtils.GREEN_FRAME_PRICE)));
            }

            if (customerRemark.getWhiteCome() != 0) {
                VariableUtils.SetSingleCellValue(wb, sheet, "w_come_unitprice", VariableUtils.FloatToStr(VariableUtils.WHITE_FRMAE_PRICE));
                VariableUtils.SetSingleCellValue(wb, sheet, "w_come_sumprice", VariableUtils.FloatToStr((customerRemark.getWhiteCome() * VariableUtils.WHITE_FRMAE_PRICE)));
            }

            if (customerRemark.getGreenCome() != 0) {
                VariableUtils.SetSingleCellValue(wb, sheet, "g_come_unitprice", VariableUtils.FloatToStr(VariableUtils.GREEN_FRAME_PRICE));
                VariableUtils.SetSingleCellValue(wb, sheet, "g_come_sumprice", VariableUtils.FloatToStr((customerRemark.getGreenCome() * VariableUtils.GREEN_FRAME_PRICE)));
            }
        }

        int sumFrameCount = customerRemark.getWhiteGo()+customerRemark.getGreenGo();
        if (sumFrameCount != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "sum_frame", sumFrameCount+"");
        }
        if (customerRemark.getOweMoney() != 0) {
            VariableUtils.SetSingleCellValue(wb, sheet, "owe_money", VariableUtils.FloatToStr(customerRemark.getOweMoney()));
        }
        VariableUtils.SetSingleCellValue(wb, sheet, "all_money", VariableUtils.FloatToStr(customerRemark.getAllMoney()));

        //退菜
        try {
            JSONArray vegComes = customerRemark.getVegetableCome();
            if (vegComes != null) {
                CellReference[] vegComeNames = VariableUtils.GetAreaReferenc(wb, "vege_name_come").getAllReferencedCells();
                CellReference[] vegComeNets = VariableUtils.GetAreaReferenc(wb, "vege_net_come").getAllReferencedCells();
                CellReference[] vegComeUnitPrices = VariableUtils.GetAreaReferenc(wb, "vege_unitprice_come").getAllReferencedCells();
                CellReference[] vegComeSumPrices = VariableUtils.GetAreaReferenc(wb, "vege_sumprice_come").getAllReferencedCells();
                for (int i = 0; i < vegComes.length(); i++) {
                    JSONObject vegComeObj = vegComes.getJSONObject(i);

                    Cell nameCell = sheet.getRow(vegComeNames[i].getRow()).getCell(vegComeNames[i].getCol());
                    nameCell.setCellValue(vegComeObj.getString("name"));
                    Cell weightCell = sheet.getRow(vegComeNets[i].getRow()).getCell(vegComeNets[i].getCol());
                    weightCell.setCellValue(vegComeObj.getString("weight"));
                    Cell priceCell = sheet.getRow(vegComeUnitPrices[i].getRow()).getCell(vegComeUnitPrices[i].getCol());
                    priceCell.setCellValue(vegComeObj.getString("price"));
                    Cell sumPriceCell = sheet.getRow(vegComeSumPrices[i].getRow()).getCell(vegComeSumPrices[i].getCol());
                    sumPriceCell.setCellValue(vegComeObj.getString("sumPrice"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //菜列表
        ArrayList<TradeRecord> tradeRecords = new TradeRecordService().findTradeRecords(customer.getId());
        if (tradeRecords.size() > 0) {
            CellReference[] vegNames = VariableUtils.GetAreaReferenc(wb, "vegetable_name").getAllReferencedCells();
            CellReference[] grossWeights = VariableUtils.GetAreaReferenc(wb, "gross_weight").getAllReferencedCells();
            CellReference[] frameCounts = VariableUtils.GetAreaReferenc(wb, "frame_count").getAllReferencedCells();
            CellReference[] frameWeights = VariableUtils.GetAreaReferenc(wb, "frame_weight").getAllReferencedCells();
            CellReference[] netWeights = VariableUtils.GetAreaReferenc(wb, "net_weight").getAllReferencedCells();
            CellReference[] unitPrices = VariableUtils.GetAreaReferenc(wb, "unit_price").getAllReferencedCells();
            CellReference[] sumPrices = VariableUtils.GetAreaReferenc(wb, "sum_price").getAllReferencedCells();

            for (int i = 0; i < tradeRecords.size(); i++) {
                sheet.getRow(vegNames[i].getRow()).getCell(vegNames[i].getCol()).setCellValue(tradeRecords.get(i).getVegetableName());
                sheet.getRow(grossWeights[i].getRow()).getCell(grossWeights[i].getCol()).setCellValue(tradeRecords.get(i).getGrossWeight());
                sheet.getRow(frameCounts[i].getRow()).getCell(frameCounts[i].getCol()).setCellValue(tradeRecords.get(i).getWhiteFrameCount()+tradeRecords.get(i).getGreenFrameCount());
                sheet.getRow(frameWeights[i].getRow()).getCell(frameWeights[i].getCol()).setCellValue(tradeRecords.get(i).getFrameWeight());
                sheet.getRow(netWeights[i].getRow()).getCell(netWeights[i].getCol()).setCellValue(tradeRecords.get(i).getNetWeight());
                sheet.getRow(unitPrices[i].getRow()).getCell(unitPrices[i].getCol()).setCellValue(tradeRecords.get(i).getUnitPrice());
                sheet.getRow(sumPrices[i].getRow()).getCell(sumPrices[i].getCol()).setCellValue(tradeRecords.get(i).getSumPrice());
            }
        }
    }


    private class CustomerAdapter extends ArrayAdapter<Customer> {

        public CustomerAdapter(ArrayList<Customer> customers) {
            super(getActivity(), 0, customers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_customer, null);
            }

            Customer c = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.customer_list_item_titleTextView);
            titleTextView.setText(c.getName());

            return convertView;

        }
    }


}
