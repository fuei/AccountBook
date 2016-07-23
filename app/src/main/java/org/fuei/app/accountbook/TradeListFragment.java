package org.fuei.app.accountbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.service.CustomerService;
import org.fuei.app.accountbook.service.CustomerRemarkService;
import org.fuei.app.accountbook.service.TradeRecordService;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/5/21.
 */
public class TradeListFragment extends ListFragment {
    public static final String TAG = "TradeListFragment";
    public static final String EXTRA_CUSTOMER_ID = "org.fuei.app.accountbook.customer_id";
    private static final String DIALOG_VEGETABLE = "vegetableDialog";

    public static int sCustomerId;

    private ArrayList<TradeRecord> mTradeRecords;

    private Customer mCustomer;
    private EditText mCNameField;

    public static TradeListFragment newInstance(int customerId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CUSTOMER_ID, customerId);
        TradeListFragment fragment = new TradeListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        int customId = (int)getArguments().getSerializable(TradeListFragment.EXTRA_CUSTOMER_ID);
        sCustomerId = customId;
        VariableUtils.CUSTOMERID = customId;
        Log.d(TAG, "customId: " + customId);

        mTradeRecords = new TradeRecordService(getActivity(), customId).findTradeRecords(customId);
        Log.d("ReacordCount: ", mTradeRecords.size()+"");

        TradeRecordAdapter adapter = new TradeRecordAdapter(mTradeRecords);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTradeRecords = new TradeRecordService(getActivity(), VariableUtils.CUSTOMERID).findTradeRecords(VariableUtils.CUSTOMERID);
        Log.d("ReacordCount: ", mTradeRecords.size()+"");

        TradeRecordAdapter adapter = new TradeRecordAdapter(mTradeRecords);
        setListAdapter(adapter);
        ((TradeRecordAdapter)getListAdapter()).notifyDataSetChanged();

        //汇总客户的筐数
        new CustomerRemarkService().insertOrUpdateFrameCount(VariableUtils.CUSTOMERID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);

        final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();

        final int customId = (int)getArguments().getSerializable(TradeListFragment.EXTRA_CUSTOMER_ID);
        Log.d(TAG, "customId: " + customId);

        Customer customer = CustomerService.get(getActivity()).getCustomer(customId);

        if (customer != null) {
            actionBar.setTitle(customer.getName());
        } else {
            ArrayList<Customer> tradeCustomers;
            tradeCustomers = new TradeRecordService().findTradeCustomers(VariableUtils.APPTYPE);
            for (Customer c : tradeCustomers) {
                if (c.getId() == customId) {
                    actionBar.setTitle(c.getName());
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动CrimePagerActivity
                Intent i = new Intent(getActivity(), CustomerRemarkActivity.class);
                //传递附加参数
                i.putExtra(CustomerRemarkFragment.EXTRA_CR_ID, sCustomerId);
                startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
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
                            TradeRecordAdapter adapter = (TradeRecordAdapter)getListAdapter();
                            TradeRecordService tradeRecordService = new TradeRecordService(getActivity(), customId);
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    tradeRecordService.findTradeRecords(customId);
                                    TradeRecord t = adapter.getItem(i);
                                    int success = tradeRecordService.deleteRecord(t.getId());
                                    if (success == 1) {
                                        tradeRecordService.deleteTradeRecord(t);
                                        adapter.remove(t);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        //从adapter中获取Crime
        TradeRecord tradeRecord = ((TradeRecordAdapter)getListAdapter()).getItem(position);

        //启动CrimePagerActivity
        Intent i = new Intent(getActivity(), TradeRecordActivity.class);
        //传递附加参数
        i.putExtra(TradeRecordFragment.EXTRA_TR_ID, tradeRecord.getVegetableId());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // 弹出选择客户的对话框
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TypePickerFragment dialog = TypePickerFragment.newInstance(VariableUtils.DIALOG_TYPE.VEGETABLE.getDialogType(),VariableUtils.APP_TYPE.OUT.getAppType());

//            dialog.setTargetFragment(TradeListFragment.class, REQUEST_CUSTOMER);
                dialog.show(fm, DIALOG_VEGETABLE);
                return true;
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
//                    NavUtils.navigateUpFromSameTask(getActivity());
                    getActivity().finish();
                }
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
        TradeRecordAdapter adapter = (TradeRecordAdapter) getListAdapter();
        TradeRecord tradeRecord = (TradeRecord) adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                TradeRecordService t = new TradeRecordService(getActivity(), mCustomer.getId());
                t.findTradeRecords(mCustomer.getId());
                t.deleteTradeRecord(tradeRecord);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }





    private class TradeRecordAdapter extends ArrayAdapter<TradeRecord> {

        public TradeRecordAdapter(ArrayList<TradeRecord> tradeRecords) {
            super(getActivity(), 0, tradeRecords);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_customer, null);
            }

            TradeRecord t = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(t.getVegetableName());
//            TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
//            dateTextView.setText(c.getAddress().toString());

            return convertView;

        }
    }
}
