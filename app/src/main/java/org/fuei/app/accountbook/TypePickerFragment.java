package org.fuei.app.accountbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.service.CustomerLab;
import org.fuei.app.accountbook.service.VegetableLab;
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
        mDialogType = (int)getArguments().getSerializable(EXTRA_DIALOG_TYPE);
        mListType = (int)getArguments().getSerializable(EXTRA_LIST_TYPE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.customer_picker, null);

        mRadioGroup = (RadioGroup)view.findViewById(R.id.list_radiogroup);

        int firstFlag = 0;
        if (mDialogType == VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType()) {
            ArrayList<Customer> customers = CustomerLab.get(getActivity()).getCustomers();
            for (Customer c: customers) {
                RadioButton tempButton = new RadioButton(getActivity());
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
            ArrayList<Vegetable> vegetables = VegetableLab.get(getActivity()).getVegetables();
            for (Vegetable v: vegetables) {
                RadioButton tempButton = new RadioButton(getActivity());
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
                .setTitle(R.string.customer_picker)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        }
                )
                .create();
    }

    private void sendResult(int resultCode) {
        Intent i = null;
        if (mDialogType == VariableUtils.DIALOG_TYPE.CUSTOMER.getDialogType()) {
            //启动该客户的交易列表Activity
            i = new Intent(getActivity(), TradeListActivity.class);
            //传参
            i.putExtra(OutTradeListFragment.EXTRA_CUSTOMER_ID, mObjectId);
        } else if (mDialogType == VariableUtils.DIALOG_TYPE.VEGETABLE.getDialogType()) {
            //启动该客户的交易列表Activity
            i = new Intent(getActivity(), TradeRecordActivity.class);
            //传参
            i.putExtra(TradeRecordFragment.EXTRA_TR_ID, mObjectId);
        }
        startActivity(i);
    }
}
