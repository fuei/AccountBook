package org.fuei.app.accountbook;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.service.CustomerRemarkService;
import org.fuei.app.accountbook.service.TradeRecordService;
import org.fuei.app.accountbook.util.VariableUtils;

/**
 * Created by fuei on 2016/5/21.
 */
public class TradeRecordFragment extends Fragment {
    public static final String EXTRA_TR_ID = "org.fuei.app.accountbook.tradeRecord_id";

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    private TradeRecord mTradeRecord;

    private EditText mUnitPriceTxt;
    private EditText mGrossWeightTxt;
    private EditText mWFrameCountTxt;
    private EditText mGFrameCountTxt;
    private TextView mFrameWeightTxt;
    private TextView mNetWeightTxt;
    private TextView mSumPriceTxt;

    public static TradeRecordFragment newInstance(int tradeRecordId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TR_ID, tradeRecordId);
        TradeRecordFragment fragment = new TradeRecordFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int vegetableId = (int)getArguments().getSerializable(EXTRA_TR_ID);
        TradeRecordService recordLab = new TradeRecordService(getActivity(), TradeListFragment.sCustomerId);
        mTradeRecord = recordLab.findRecordByVegId(vegetableId, 0);
        if (mTradeRecord == null) {
            recordLab.insertRecord(vegetableId);
            mTradeRecord = new TradeRecordService(getActivity(), TradeListFragment.sCustomerId).findRecordByVegId(vegetableId, 1);

        }

        setHasOptionsMenu(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trade_details, container, false);

        final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(mTradeRecord.getVegetableName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mUnitPriceTxt = (EditText)v.findViewById(R.id.editText_unitPrice);
        mUnitPriceTxt.setHint(mTradeRecord.getUnitPrice()+"");
        mGrossWeightTxt = (EditText)v.findViewById(R.id.editText_grossWeight);
        //region 设置焦点
        mGrossWeightTxt.setFocusable(true);
        mGrossWeightTxt.requestFocus();
        mGrossWeightTxt.setFocusableInTouchMode(true);
        //endregion
        mWFrameCountTxt = (EditText)v.findViewById(R.id.editText_wFrameCount);
        mGFrameCountTxt = (EditText)v.findViewById(R.id.editText_gFrameCount);
        mFrameWeightTxt = (TextView)v.findViewById(R.id.textView_frameWeight);
        mNetWeightTxt = (TextView)v.findViewById(R.id.textView_netWeight);
        mSumPriceTxt = (TextView)v.findViewById(R.id.textView_sumPrice);

        if (mTradeRecord.getGrossWeight() != 0) {
            mUnitPriceTxt.setHint(mTradeRecord.getUnitPrice()+"");
            mGrossWeightTxt.setHint(mTradeRecord.getGrossWeight()+"");
            mWFrameCountTxt.setHint(mTradeRecord.getWhiteFrameCount()+"");
            mGFrameCountTxt.setHint(mTradeRecord.getGreenFrameCount()+"");
            mFrameWeightTxt.setText(mTradeRecord.getFrameWeight()+"");
            mNetWeightTxt.setText(mTradeRecord.getNetWeight()+"");
            mSumPriceTxt.setText(mTradeRecord.getSumPrice()+"");
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    getActivity().finish();
                }
                return true;
            case R.id.action_save:
                //单价
                String unitPriceStr = mUnitPriceTxt.getText().toString();
                if (unitPriceStr==null || unitPriceStr.trim().equals("")) {
                    unitPriceStr = mUnitPriceTxt.getHint().toString();
                    if (unitPriceStr==null || unitPriceStr.trim().equals("")) {
                        mUnitPriceTxt.setError("请输入单价！");
                        return false;
                    }
                }
                float unitPrice = Float.parseFloat(unitPriceStr);
                mTradeRecord.setUnitPrice(unitPrice);

                //毛重
                String grossWeightStr = mGrossWeightTxt.getText().toString();
                if(grossWeightStr==null || grossWeightStr.trim().equals("")) {
                    grossWeightStr = mGrossWeightTxt.getHint().toString();
                    if (grossWeightStr == null || grossWeightStr.trim().equals("")) {
                        mGrossWeightTxt.setError("请输入毛重！");
                        return false;
                    }
                }
                float grossWeight = Float.parseFloat(grossWeightStr);
                mTradeRecord.setGrossWeight(grossWeight);

                //白筐数
                String wFrameCountStr = mWFrameCountTxt.getText().toString();
                int wFrameCount = 0;
                if(wFrameCountStr==null || wFrameCountStr.trim().equals("")) {
                    wFrameCountStr = mWFrameCountTxt.getHint().toString();
                    if (wFrameCountStr==null || wFrameCountStr.trim().equals(""))
                        wFrameCount = 0;
                    else
                        wFrameCount = Integer.parseInt(wFrameCountStr);
                } else {
                    wFrameCount = Integer.parseInt(wFrameCountStr);
                }
                mTradeRecord.setWhiteFrameCount(wFrameCount);

                //绿筐数
                String gFrameCountStr = mGFrameCountTxt.getText().toString();
                int gFrameCount = 0;
                if(gFrameCountStr==null || gFrameCountStr.trim().equals("")) {
                    gFrameCountStr = mGFrameCountTxt.getHint().toString();
                    if(gFrameCountStr==null || gFrameCountStr.trim().equals(""))
                        gFrameCount = 0;
                    else
                        gFrameCount = Integer.parseInt(gFrameCountStr);
                } else {
                    gFrameCount = Integer.parseInt(gFrameCountStr);
                }
                mTradeRecord.setGreenFrameCount(gFrameCount);

                //筐重
                float frameWeight = (wFrameCount + gFrameCount) * VariableUtils.UNIT_FRAME_WEIGHT;
                mFrameWeightTxt.setText(frameWeight+"" );
                mTradeRecord.setFrameWeight(frameWeight);
                //净重
                float netWeight = grossWeight - frameWeight;
                mNetWeightTxt.setText(netWeight+"");
                mTradeRecord.setNetWeight(netWeight);
                //总价
                float sumPrice = netWeight * unitPrice;

                if (VariableUtils.APPTYPE == VariableUtils.ENUM_APP_TYPE.FAMER.getAppType()) {
                    String sumPriceAdjust = (int)sumPrice + "";
                    mSumPriceTxt.setText(sumPriceAdjust);
                    mTradeRecord.setSumPrice((int)sumPrice);
                } else {
                    String sumPriceAdjust = sumPrice + "";
                    mSumPriceTxt.setText(sumPriceAdjust);
                    mTradeRecord.setSumPrice(sumPrice);
                }

                new TradeRecordService().updateRecord(mTradeRecord);

                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
