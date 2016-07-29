package org.fuei.app.accountbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.service.CustomerService;
import org.fuei.app.accountbook.service.VegetableService;
import org.fuei.app.accountbook.settings.CustomerLab;
import org.fuei.app.accountbook.settings.VegetableLab;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/15.
 */
public class TypePickerFragment extends DialogFragment {
    public static final String EXTRA_DIALOG_TYPE = "org.fuei.app.accountbook.picker.type";
    public static final String EXTRA_LIST_TYPE = "org.fuei.app.accountbook.list.type";
    public static final String EXTRA_DIALOG_ID = "org.fuei.app.accountbook.picker.id";

    private int mDialogType;
    private int mListType;
    private int mObjectId;
    private Activity mAct;

    private RadioGroup mRadioGroup;

    /**
     * 实例化类型选择对话框
     * @param flag 对话框类型
     * @param listType 列表内容的类型
     * @return TypePickerFragment实例对象
     */
    public static TypePickerFragment newInstance(int flag, int listType) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DIALOG_TYPE, flag);
        args.putSerializable(EXTRA_LIST_TYPE, listType);

        TypePickerFragment fragment = new TypePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAct = getActivity();

        mDialogType = (int)getArguments().getSerializable(EXTRA_DIALOG_TYPE);
        mListType = (int)getArguments().getSerializable(EXTRA_LIST_TYPE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.customer_picker, null);

        mRadioGroup = (RadioGroup)view.findViewById(R.id.list_radiogroup);

        int firstFlag = 0;
        if (mDialogType == VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType()) {
            ArrayList<Customer> customers = CustomerService.get(getActivity()).getCustomers();
            for (Customer c: customers) {
                RadioButton tempButton = (RadioButton) getActivity().getLayoutInflater().inflate(R.layout.myradiobutton, null);
                tempButton.setText(c.getName());
                tempButton.setId(c.getId());

                if (firstFlag == 0) {
                    tempButton.setChecked(true);
                    mObjectId = tempButton.getId();
                }
                mRadioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                firstFlag++;
            }
        } else if (mDialogType == VariableUtils.DIALOG_TYPE.VEGETABLE.getDialogType()) {
            ArrayList<Vegetable> vegetables = VegetableService.get(getActivity()).getVegetables();
            for (Vegetable v: vegetables) {
                RadioButton tempButton = (RadioButton) getActivity().getLayoutInflater().inflate(R.layout.myradiobutton, null);
                tempButton.setText(v.getName());
                tempButton.setId(v.getId());
                if (firstFlag == 0) {
                    tempButton.setChecked(true);
                    mObjectId = tempButton.getId();
                }
                mRadioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                firstFlag++;
            }
        }



        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton checkedBtn = (RadioButton)group.findViewById(checkedId);
                mObjectId = checkedId;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("请选择")
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mObjectId == 0) {
                                    dialog.cancel();
                                } else {
                                    sendResult();
                                }
                            }
                        }
                )
                .setNegativeButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                )
                .setNeutralButton(
                        "添加新选项",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mDialogType == VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType()) {
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

                                                    ArrayList<Customer> customers = CustomerLab.get().getCustomers();
                                                    for (Customer tempC: customers) {
                                                        if (cName.equals(tempC.getName()) && (VariableUtils.APPTYPE == tempC.getType())) {
                                                            Toast.makeText(mAct, "该客户已存在！", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                    }

                                                    mObjectId = CustomerLab.get().addCustomer(c);
                                                    sendResult();
                                                }
                                            })
                                            .setNegativeButton("取消", null);
                                    // 3. Get the AlertDialog from create()
                                    AlertDialog tempDialog = builder.create();
                                    tempDialog.show();
                                } else {
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

                                                    ArrayList<Vegetable> veges = VegetableLab.get().getVeges();
                                                    for (Vegetable tempV: veges) {
                                                        if (name.equals(tempV.getName())) {
                                                            Toast.makeText(mAct, "该商品已存在！", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                    }

                                                    mObjectId = VegetableLab.get().addVegetable(v);
                                                    sendResult();

                                                }
                                            })
                                            .setNegativeButton("取消", null);
                                    // 3. Get the AlertDialog from create()
                                    AlertDialog tempDialog = builder.create();
                                    tempDialog.show();
                                }
                            }
                        }
                )
                .create();
    }

    private void sendResult() {
        Intent i = null;
        if (mDialogType == VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType()) {
            //启动该客户的交易列表Activity
            i = new Intent(mAct, TradeListActivity.class);
            //传参
            i.putExtra(TradeListFragment.EXTRA_CUSTOMER_ID, mObjectId);
        } else if (mDialogType == VariableUtils.DIALOG_TYPE.VEGETABLE.getDialogType()) {
            //启动该客户的交易列表Activity
            i = new Intent(mAct, TradeRecordActivity.class);
            //传参
            i.putExtra(TradeRecordFragment.EXTRA_TR_ID, mObjectId);
        }
        mAct.startActivity(i);

    }
}
