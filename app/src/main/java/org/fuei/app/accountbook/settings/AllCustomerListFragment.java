package org.fuei.app.accountbook.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.fuei.app.accountbook.R;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/5/23.
 */
public class AllCustomerListFragment extends ListFragment {

    private static final String TAG = "AllCustomerListFragment";

    private ArrayList<Customer> mCustomers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("客户管理");
        mCustomers = CustomerLab.get(getActivity()).getCustomers();

        CustomerAdapter adapter = new CustomerAdapter(mCustomers);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CustomerAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();
        assert v != null;
        ListView listView = (ListView)v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                            CustomerLab customerLab = CustomerLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    customerLab.deleteCustomer(adapter.getItem(i));
                                    adapter.remove(adapter.getItem(i));
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

                }
            });
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                final View viewDialogAdd = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_cutomer,null);
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle("添加客户").setView(viewDialogAdd)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText nameTxt = (EditText) viewDialogAdd.findViewById(R.id.editText_addCustomer);

                                String cName = nameTxt.getText().toString();
                                if (cName==null || cName.trim().equals("")) {
                                    nameTxt.setError("请输入客户名！");
                                    return;
                                }
                                Customer c = new Customer();
                                c.setName(cName);
                                c.setType(VariableUtils.APPTYPE);

                                for (Customer tempC: mCustomers) {
                                    if (cName.equals(tempC.getName()) && (VariableUtils.APPTYPE == tempC.getType())) {
                                        Toast.makeText(getActivity(), "该客户已存在！", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }

                                CustomerLab.get(getActivity()).addCustomer(c);
                                CustomerAdapter adapter = (CustomerAdapter)getListAdapter();
                                adapter.add(c);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null);

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = adapterContextMenuInfo.position;
        CustomerAdapter adapter = (CustomerAdapter)getListAdapter();
        Customer c = (Customer)adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                CustomerLab.get(getActivity()).deleteCustomer(c);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private class CustomerAdapter extends ArrayAdapter<Customer> {

        public CustomerAdapter(ArrayList<Customer> customers) {
            super(getActivity(), 0, customers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_allitem_customer, null);
            }

            Customer c = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.textView_customerName);
            titleTextView.setText(c.getName());

            return convertView;

        }
    }
}
