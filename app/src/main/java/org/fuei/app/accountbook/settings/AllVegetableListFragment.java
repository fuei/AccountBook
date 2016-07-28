package org.fuei.app.accountbook.settings;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
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
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/24.
 */
public class AllVegetableListFragment extends ListFragment {
    private static final String TAG = "AllVegetableListFragment";

    private ArrayList<Vegetable> mVeges;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("商品管理");
        mVeges = VegetableLab.get(getActivity()).getVeges();

        VegeAdapter adapter = new VegeAdapter(mVeges);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((VegeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();
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
                            VegeAdapter adapter = (VegeAdapter)getListAdapter();
                            VegetableLab vegetableLab = VegetableLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    vegetableLab.deleteVegetable(adapter.getItem(i));
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
                    if (actionBar != null) {
                        actionBar.show();
                    }
                }
            });
        }

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //从adapter中获取Crime
        final Vegetable vegetable = ((VegeAdapter)getListAdapter()).getItem(position);
        final View viewDialogAdd = getActivity().getLayoutInflater().inflate(R.layout.dialog_update_vegetable,null);
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("修改商品单价").setView(viewDialogAdd)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText priceTxt = (EditText) viewDialogAdd.findViewById(R.id.editText_updateVegePrice);
                        String price = priceTxt.getText().toString();
                        if (price==null || price.trim().equals("")) {
                            priceTxt.setError("请输入单价！");
                            return;
                        }
                        vegetable.setUnitPrice(Float.parseFloat(price));
                        VegetableLab.get(getActivity()).updateVegetable(vegetable);
                        VegeAdapter adapter = (VegeAdapter)getListAdapter();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                TextView nameTxt = (TextView) viewDialogAdd.findViewById(R.id.textView_vegeName);
                nameTxt.setText(vegetable.getName());
                EditText priceTxt = (EditText) viewDialogAdd.findViewById(R.id.editText_updateVegePrice);
                priceTxt.setHint(vegetable.getUnitPrice()+"");
            }
        });

        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                final View viewDialogAdd = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_vegetable,null);
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle("添加商品").setView(viewDialogAdd)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText nameTxt = (EditText) viewDialogAdd.findViewById(R.id.editText_addVege);
                                EditText priceTxt = (EditText) viewDialogAdd.findViewById(R.id.editText_addVegePrice);
                                String name = nameTxt.getText().toString();
                                if (name==null || name.trim().equals("")) {
                                    nameTxt.setError("请输入菜名！");
                                    return;
                                }
                                String price = priceTxt.getText().toString();
                                if (price==null || price.trim().equals("")) {
                                    priceTxt.setError("请输入单价！");
                                    return;
                                }
                                Vegetable v = new Vegetable();
                                v.setName(name);
                                v.setUnitPrice(Float.parseFloat(price));
                                for (Vegetable tempV: mVeges) {
                                    if (name.equals(tempV.getName())) {
                                        Toast.makeText(getActivity(), "该商品已存在！", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }

                                VegetableLab.get(getActivity()).addVegetable(v);
                                VegeAdapter adapter = (VegeAdapter)getListAdapter();
                                adapter.add(v);
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
        VegeAdapter adapter = (VegeAdapter)getListAdapter();
        Vegetable c = (Vegetable) adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                VegetableLab.get(getActivity()).deleteVegetable(c);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private class VegeAdapter extends ArrayAdapter<Vegetable> {

        public VegeAdapter(ArrayList<Vegetable> vegetables) {
            super(getActivity(), 0, vegetables);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_allitem_customer, null);
            }

            Vegetable c = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.textView_customerName);
            titleTextView.setText(c.getName() + ", " + c.getUnitPrice() + "元/斤");

            return convertView;

        }
    }
}
