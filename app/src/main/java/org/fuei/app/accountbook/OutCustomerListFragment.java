package org.fuei.app.accountbook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
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

import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.CustomerRemark;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.service.CustomerLab;
import org.fuei.app.accountbook.service.CustomerRemarkLab;
import org.fuei.app.accountbook.service.TradeRecordLab;
import org.fuei.app.accountbook.util.ExportExcel;
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
public class OutCustomerListFragment extends ListFragment {

    private static final String TAG = "OutCustomerListFragment";


    private static final String DIALOG_CUSTOMER = "customerDialog";
    private static final int REQUEST_CUSTOMER  = 0;

    private ArrayList<Customer> mCustomers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCustomers = new TradeRecordLab(getActivity(), 0).findTradeCustomers(VariableUtils.APP_TYPE.OUT.getAppType());

        CustomerAdapter adapter = new CustomerAdapter(mCustomers);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();

            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    actionBar.hide();
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
                                    int success = new TradeRecordLab().deleteRecordByCustomerId(c.getId());
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
                    actionBar.show();
                }
            });
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCustomers = new TradeRecordLab(getActivity(), 0).findTradeCustomers(VariableUtils.APP_TYPE.OUT.getAppType());

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
        i.putExtra(OutTradeListFragment.EXTRA_CUSTOMER_ID, customer.getId());
        Log.d(TAG, "customId: " + customer.getId());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            // 弹出选择客户的对话框
            FragmentManager fm = getActivity().getSupportFragmentManager();
            TypePickerFragment dialog = TypePickerFragment.newInstance(VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType(),VariableUtils.APP_TYPE.OUT.getAppType());

//            dialog.setTargetFragment(OutTradeListFragment.class, REQUEST_CUSTOMER);
            dialog.show(fm, DIALOG_CUSTOMER);

            return true;
        } else if (id == R.id.export) {

            //获取某个市场类型的客户列表，根据客户循环导出excel
            ArrayList<Customer> customers = new TradeRecordLab().findTradeCustomers(VariableUtils.APPTYPE);
            for (Customer customer: customers) {
                data2Excel(customer);
            }
            Toast.makeText(getActivity(), "导出成功! \n 路径：Android/data/org.fuei.app.accountbook/files1", Toast.LENGTH_LONG).show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean data2Excel(Customer customer) {
        String tempDateStr = (VariableUtils.DATADATE+"");
        String dateStr = tempDateStr.substring(0,4) + "."
                + tempDateStr.substring(4,6) + "."
                + tempDateStr.substring(6,8);
        Log.d("日期：", dateStr);

        InputStream fis = getResources().openRawResource(R.raw.template);;
        HSSFWorkbook wb = null;
        OutputStream fos = null;
        try {
            wb = new HSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            HSSFSheet sheet = null;
            if (wb == null) {
                Toast.makeText(getActivity(), "模板工作簿不存在，导出失败", Toast.LENGTH_LONG);
                return true;
            }
            sheet = wb.getSheetAt(0);
            //表格复制
            setCellValue(sheet, customer, dateStr);


            //创建表格
            File file = VariableUtils.ExportExcel2SDCard(getContext(), VariableUtils.APPTYPE+"", dateStr, customer.getName());
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
                    fos.close();
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
    private void setCellValue(HSSFSheet sheet, Customer customer, String dateStr) {
        HSSFCell customerNameCell = sheet.getRow(1).getCell(2);
        HSSFCell dataDateCell = sheet.getRow(1).getCell(5);

        HSSFRow whiteGoRow = sheet.getRow(VariableUtils.SheetRowIndexs.WHITEGO.getIndex());
        HSSFRow greenGoRow = sheet.getRow(VariableUtils.SheetRowIndexs.GREENGO.getIndex());
        HSSFRow whiteComeRow = sheet.getRow(VariableUtils.SheetRowIndexs.WHITECOME.getIndex());
        HSSFRow greenComeRow = sheet.getRow(VariableUtils.SheetRowIndexs.GREENCOME.getIndex());
        HSSFCell frameGoSumCell = sheet.getRow(VariableUtils.SheetRowIndexs.SUMGO.getIndex())
                .getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex());

        HSSFCell oweMoneyCell = sheet.getRow(VariableUtils.SheetRowIndexs.OWE.getIndex())
                .getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex());
        HSSFCell allMoneyCell = sheet.getRow(VariableUtils.SheetRowIndexs.ALLMONEY.getIndex())
                .getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex());

        customerNameCell.setCellValue(customer.getName());
        dataDateCell.setCellValue(dateStr);

        CustomerRemark customerRemark = new CustomerRemarkLab().findRecordByCustomerId(customer.getId());
        whiteGoRow.getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex())
                .setCellValue(customerRemark.getWhiteGo());
        if (customerRemark.getWhiteGo() != 0) {
            whiteGoRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                    .setCellValue(VariableUtils.WHITE_FRMAE_PRICE);
            whiteGoRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                    .setCellValue(customerRemark.getWhiteGo() * VariableUtils.WHITE_FRMAE_PRICE);
        }
        greenGoRow.getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex())
                .setCellValue(customerRemark.getGreenGo());
        if (customerRemark.getGreenGo() != 0) {
            greenGoRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                    .setCellValue(VariableUtils.GREEN_FRAME_PRICE);
            greenGoRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                    .setCellValue(customerRemark.getGreenGo() * VariableUtils.GREEN_FRAME_PRICE);
        }

        whiteComeRow.getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex())
                .setCellValue(customerRemark.getWhiteCome());
        if (customerRemark.getWhiteCome() != 0) {
            whiteComeRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                    .setCellValue(VariableUtils.WHITE_FRMAE_PRICE);
            whiteComeRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                    .setCellValue(customerRemark.getWhiteCome() * VariableUtils.WHITE_FRMAE_PRICE);
        }
        greenComeRow.getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex())
                .setCellValue(customerRemark.getGreenCome());
        if (customerRemark.getGreenCome() != 0) {
            greenComeRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                    .setCellValue(VariableUtils.GREEN_FRAME_PRICE);
            greenComeRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                    .setCellValue(customerRemark.getGreenCome() * VariableUtils.GREEN_FRAME_PRICE);
        }

        frameGoSumCell.setCellValue(customerRemark.getWhiteGo() + customerRemark.getGreenGo());
        oweMoneyCell.setCellValue(customerRemark.getOweMoney());
        allMoneyCell.setCellValue(customerRemark.getAllMoney());
        //退菜
        try {
            JSONArray vegComes = customerRemark.getVegetableCome();
            int vegComeRowNumber = 17;
            for (int i = 0; i < vegComes.length(); i++) {
                JSONObject vegComeObj = vegComes.getJSONObject(i);
                HSSFRow vegComeRow = sheet.getRow(vegComeRowNumber);
                vegComeRow.getCell(VariableUtils.SheetColumnIndexs.NAME.getIndex())
                        .setCellValue(vegComeObj.getString("name"));
                vegComeRow.getCell(VariableUtils.SheetColumnIndexs.NET.getIndex())
                        .setCellValue(vegComeObj.getString("weight"));
                vegComeRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                        .setCellValue(vegComeObj.getString("price"));
                vegComeRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                        .setCellValue(vegComeObj.getString("sumPrice"));
                vegComeRowNumber++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //菜列表
        ArrayList<TradeRecord> tradeRecords = new TradeRecordLab().findTradeRecords(customer.getId());
        int tradeRecordRowNum = 3;
        for (TradeRecord tradeRecord: tradeRecords) {
            HSSFRow tradeRow = sheet.getRow(tradeRecordRowNum);
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.NAME.getIndex())
                    .setCellValue(tradeRecord.getVegetableName());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.GROSS.getIndex())
                    .setCellValue(tradeRecord.getGrossWeight());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.FRAMECOUNT.getIndex())
                    .setCellValue(tradeRecord.getWhiteFrameCount()+tradeRecord.getGreenFrameCount());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.FRAMEWEIGHT.getIndex())
                    .setCellValue(tradeRecord.getFrameWeight());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.NET.getIndex())
                    .setCellValue(tradeRecord.getNetWeight());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.UNITPRICE.getIndex())
                    .setCellValue(tradeRecord.getUnitPrice());
            tradeRow.getCell(VariableUtils.SheetColumnIndexs.PRICE.getIndex())
                    .setCellValue(tradeRecord.getSumPrice());

            tradeRecordRowNum++;
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

            TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getName());

            return convertView;

        }
    }


}
