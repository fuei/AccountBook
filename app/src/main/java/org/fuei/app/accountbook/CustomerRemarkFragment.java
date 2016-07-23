package org.fuei.app.accountbook;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.fuei.app.accountbook.po.CustomerRemark;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.service.CustomerRemarkLab;
import org.fuei.app.accountbook.service.TradeRecordLab;
import org.fuei.app.accountbook.util.VariableUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/17.
 */
public class CustomerRemarkFragment extends Fragment {
    public static final String EXTRA_CR_ID = "org.fuei.app.accountbook.customerId";

    private TextView mWFrameGoTxt;
    private TextView mGFrameGoTxt;
    private EditText mWFrameComeTxt;
    private EditText mGFrameComeTxt;
    private EditText mOweMoneyTxt;
    private TextView mAllMoneyTxt;
    private ImageButton mAddVegComeBtn;
    private ImageButton mDeleteVegComeBtn;

    private int mCustomerId;
    private CustomerRemark mCustomerRemark;
    private JSONArray mVegComeList = null;
    private TradeRecord mVegComeObject;

    public static CustomerRemarkFragment newInstance(int crId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CR_ID, crId);
        CustomerRemarkFragment fragment = new CustomerRemarkFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomerId = (int)getArguments().getSerializable(EXTRA_CR_ID);
        mCustomerRemark = new CustomerRemarkLab().findRecordByCustomerId(mCustomerId);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.customer_remark_details, container, false);

        final ActionBar actionBar = (ActionBar)((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("备注");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null && actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mWFrameGoTxt = (TextView)view.findViewById(R.id.textView_wFraCount_go);
        mWFrameGoTxt.setText(mCustomerRemark.getWhiteGo()+"");
        mGFrameGoTxt = (TextView)view.findViewById(R.id.textView_gFraCount_go);
        mGFrameGoTxt.setText(mCustomerRemark.getGreenGo()+"");

        mWFrameComeTxt = (EditText) view.findViewById(R.id.editText_wFraCount_come);
        mWFrameComeTxt.setText(mCustomerRemark.getWhiteCome()+"");
        mGFrameComeTxt = (EditText) view.findViewById(R.id.editText_gFraCount_come);
        mGFrameComeTxt.setText(mCustomerRemark.getGreenCome()+"");
        mOweMoneyTxt = (EditText) view.findViewById(R.id.editText_oweMoney);
        mOweMoneyTxt.setText(mCustomerRemark.getOweMoney()+"");
        mAllMoneyTxt = (TextView) view.findViewById(R.id.textView_allMoney);
        mAllMoneyTxt.setText(mCustomerRemark.getAllMoney()+"");

        final LinearLayout linearLayoutRoot = (LinearLayout)view.findViewById(R.id.linear_vegCome_list);

        // 添加退菜列表
        try {
            mVegComeList = mCustomerRemark.getVegetableCome();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mVegComeList != null) {
            for (int i = 0; i < mVegComeList.length(); i++) {
                try {
                    JSONObject jsonObject = mVegComeList.getJSONObject(i);
                    View viewAdd = getActivity().getLayoutInflater().inflate(R.layout.list_vegetable_come,null);
                    linearLayoutRoot.addView(viewAdd);

                    TextView vegComeNameTxt = (TextView)viewAdd.findViewById(R.id.textView_vegComeName);
                    vegComeNameTxt.setText(jsonObject.get("name").toString()+" : ");
                    TextView vegComePriceTxt = (TextView)viewAdd.findViewById(R.id.textView_vegComePrice);
                    vegComePriceTxt.setText(jsonObject.get("price").toString());
                    TextView vegComeWeightTxt = (TextView)viewAdd.findViewById(R.id.textView_vegComeWeight);
                    vegComeWeightTxt.setText(jsonObject.get("weight").toString());
                    TextView vegComeSumPriceTxt = (TextView)viewAdd.findViewById(R.id.textView_vegComeSumPrice);
                    vegComeSumPriceTxt.setText(jsonObject.get("sumPrice").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mAddVegComeBtn = (ImageButton)view.findViewById(R.id.imgBtn_addVegCome);
        mAddVegComeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewDialogAdd = getActivity().getLayoutInflater().inflate(R.layout.dialog_vegcome_add,null);
                final Spinner nameSpinner = (Spinner)viewDialogAdd.findViewById(R.id.spinner_dialog_name);
                final EditText priceEditText = (EditText)viewDialogAdd.findViewById(R.id.editText_dialog_price);
                final EditText weightEditText = (EditText)viewDialogAdd.findViewById(R.id.editText_dialog_weight);

                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle("退菜").setView(viewDialogAdd)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mVegComeObject == null) return;
                                //添加退菜视图
                                View v = getActivity().getLayoutInflater().inflate(R.layout.list_vegetable_come,null);
                                linearLayoutRoot.addView(v);
                                //控件赋值
                                TextView vegComeNameTxt = (TextView)v.findViewById(R.id.textView_vegComeName);
                                vegComeNameTxt.setText(mVegComeObject.getVegetableName()+" : ");

                                TextView vegComePriceTxt = (TextView)v.findViewById(R.id.textView_vegComePrice);
                                String unitPriceStr = priceEditText.getText().toString();
                                if (unitPriceStr==null || unitPriceStr.trim().equals("")) {
                                    unitPriceStr = priceEditText.getHint().toString();
                                    if (unitPriceStr==null || unitPriceStr.trim().equals("")) {
                                        priceEditText.setError("请输入单价！");
                                        return;
                                    }
                                }
                                vegComePriceTxt.setText(unitPriceStr);

                                TextView vegComeWeightTxt = (TextView)v.findViewById(R.id.textView_vegComeWeight);
                                String weightStr = weightEditText.getText().toString();
                                if (weightStr==null || weightStr.trim().equals("")) {
                                    weightStr = weightEditText.getHint().toString();
                                    if (weightStr==null || weightStr.trim().equals("")) {
                                        weightEditText.setError("请输入重量！");
                                        return;
                                    }
                                }
                                vegComeWeightTxt.setText(weightStr);
                                TextView vegComeSumPriceTxt = (TextView)v.findViewById(R.id.textView_vegComeSumPrice);
                                String sumPrice = VariableUtils.SaveOneNum(Float.parseFloat(priceEditText.getText().toString())*Float.parseFloat(weightEditText.getText().toString()));
                                vegComeSumPriceTxt.setText(sumPrice);
                                //转为JSON对象，存入JSON数组
                                String jsonStr = "{"
                                        + " \"name\": \"" + mVegComeObject.getVegetableName() + "\", "
                                        + " \"price\": \"" + priceEditText.getText() + "\", "
                                        + " \"weight\": \"" + weightEditText.getText() + "\", "
                                        + " \"sumPrice\": \"" + sumPrice + "\" "
                                        + "}";
                                try {
                                    JSONObject jsonObject = (JSONObject) new JSONTokener(jsonStr).nextValue();
                                    if (mVegComeList == null) mVegComeList = new JSONArray();
                                    mVegComeList.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", null);

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        final ArrayList<TradeRecord> tradeRecords = new TradeRecordLab().findVegetableList(mCustomerId, VariableUtils.DATADATE);
                        ArrayAdapter<TradeRecord> adapter = new ArrayAdapter<TradeRecord>(getActivity(),android.R.layout.simple_spinner_item, tradeRecords);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        nameSpinner.setAdapter(adapter);
                        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mVegComeObject = tradeRecords.get(position);
                                priceEditText.setText(mVegComeObject.getUnitPrice()+"");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });

                dialog.show();
            }
        });

        mDeleteVegComeBtn = (ImageButton)view.findViewById(R.id.imgBtn_deleteVegCome);
        mDeleteVegComeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutRoot.getChildCount() > 0) {
                    linearLayoutRoot.removeView(linearLayoutRoot.getChildAt(linearLayoutRoot.getChildCount()-1));
                    mVegComeList.remove(mVegComeList.length()-1);
                }
            }
        });

        return view;
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
                //计算总价
                //菜总价
                float tradeSumPrice = new TradeRecordLab().findSumPrice(mCustomerId, VariableUtils.DATADATE);
                //退白筐数
                String wFrameComeStr = mWFrameComeTxt.getText().toString();
                if (wFrameComeStr==null || wFrameComeStr.trim().equals("")) {
                    wFrameComeStr = mWFrameComeTxt.getHint().toString();
                    if (wFrameComeStr==null || wFrameComeStr.trim().equals("")) {
                        wFrameComeStr = "0";
                        return false;
                    }
                }
                int whiteComeNum = Integer.parseInt(wFrameComeStr);
                //退绿筐数
                String gFrameComeStr = mGFrameComeTxt.getText().toString();
                if (gFrameComeStr==null || gFrameComeStr.trim().equals("")) {
                    gFrameComeStr = mGFrameComeTxt.getHint().toString();
                    if (gFrameComeStr==null || gFrameComeStr.trim().equals("")) {
                        gFrameComeStr = "0";
                        return false;
                    }
                }
                int greenComeNum = Integer.parseInt(gFrameComeStr);
                //白筐总价
                float whitePrice = (Integer.parseInt(mWFrameGoTxt.getText().toString()) - whiteComeNum) * VariableUtils.WHITE_FRMAE_PRICE;
                //绿筐总价
                float greenPrice = (Integer.parseInt(mGFrameGoTxt.getText().toString()) - greenComeNum) * VariableUtils.GREEN_FRAME_PRICE;
                //筐总价
                float framePrice = whitePrice + greenPrice;
                //欠款
                String owePriceStr = mOweMoneyTxt.getText().toString();
                if (owePriceStr==null || owePriceStr.trim().equals("")) {
                    owePriceStr = mOweMoneyTxt.getHint().toString();
                    if (owePriceStr==null || owePriceStr.trim().equals("")) {
                        owePriceStr = "0";
                        return false;
                    }
                }
                float owePrice = Float.parseFloat(owePriceStr);
                //退菜总价
                float vegComePrice = 0;
                for (int i = 0; i < mVegComeList.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) mVegComeList.get(i);
                        vegComePrice += Float.parseFloat(jsonObject.get("sumPrice").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //总价
                float sumPrice = tradeSumPrice + framePrice + owePrice - vegComePrice;
                //界面显示
                mAllMoneyTxt.setText(VariableUtils.SaveOneNum(sumPrice));
                //数据入库
                mCustomerRemark.setWhiteCome(whiteComeNum);
                mCustomerRemark.setGreenCome(greenComeNum);
                mCustomerRemark.setVegetableCome(mVegComeList.toString());
                mCustomerRemark.setOweMoney(owePrice);
                mCustomerRemark.setAllMoney(sumPrice);

                int flag = new CustomerRemarkLab().updateRecord(mCustomerRemark);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
