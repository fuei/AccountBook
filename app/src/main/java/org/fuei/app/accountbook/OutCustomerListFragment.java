package org.fuei.app.accountbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.service.CustomerLab;
import org.fuei.app.accountbook.service.TradeRecordLab;
import org.fuei.app.accountbook.util.ExportExcel;
import org.fuei.app.accountbook.util.VariableUtils;

import java.io.IOException;
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
            try {
                ExportExcel.read(getResources());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
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
//            TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
//            dateTextView.setText(c.getAddress().toString());

            return convertView;

        }
    }
}
