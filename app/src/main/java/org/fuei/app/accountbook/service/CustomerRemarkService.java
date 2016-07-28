package org.fuei.app.accountbook.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.po.CustomerRemark;
import org.fuei.app.accountbook.util.VariableUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fuei on 2016/7/17.
 */
public class CustomerRemarkService {
    public CustomerRemark findRecordByCustomerId(int customerId) {

        CustomerRemark customerRemark = new CustomerRemark();

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String sql = "SELECT * FROM t_customer_remark WHERE customer_id = " + customerId + " AND data_date = " + VariableUtils.DATADATE;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            customerRemark.setId(cursor.getInt(cursor.getColumnIndex("id")));
            customerRemark.setCustomerId(cursor.getInt(cursor.getColumnIndex("customer_id")));
            customerRemark.setWhiteCome(cursor.getInt(cursor.getColumnIndex("white_come")));
            customerRemark.setWhiteGo(cursor.getInt(cursor.getColumnIndex("white_go")));
            customerRemark.setGreenGo(cursor.getInt(cursor.getColumnIndex("green_go")));
            customerRemark.setGreenCome(cursor.getInt(cursor.getColumnIndex("green_come")));
            customerRemark.setVegetableCome(cursor.getString(cursor.getColumnIndex("vegetable_come")));
            customerRemark.setOweMoney(cursor.getFloat(cursor.getColumnIndex("owe_money")));
            customerRemark.setAllMoney(cursor.getFloat(cursor.getColumnIndex("all_money")));
            customerRemark.setSumFrame(cursor.getInt(cursor.getColumnIndex("sum_frame")));
            customerRemark.setDataDate(cursor.getInt(cursor.getColumnIndex("data_date")));
        }
        cursor.close();
        sqLiteDatabase.close();

        return customerRemark;
    }

    public void insertRecord(int customerId) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        Cursor cursor = sqLiteDatabase.rawQuery("select id from t_customer_remark where customer_id = "+ customerId +" AND data_date = " + VariableUtils.DATADATE, null);
        int id = 0;
        if(cursor.moveToFirst())
            id = cursor.getInt(0);
        if (id != 0) {
            sqLiteDatabase.close();
            return;
        }

        ContentValues vals = new ContentValues();
        vals.put("customer_id", customerId);
        vals.put("data_date", VariableUtils.DATADATE);

        sqLiteDatabase.insert("t_customer_remark", null, vals);
        sqLiteDatabase.close();
    }

    public int updateRecord(CustomerRemark c) {

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        int flag = sqLiteDatabase.update("t_customer_remark", createValues(c), "id = " + c.getId(), null);

        sqLiteDatabase.close();

        return flag;
    }

    public int deleteRecord(int customerId) {

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String[] args = {String.valueOf(customerId), String.valueOf(VariableUtils.DATADATE)};
        int flag = sqLiteDatabase.delete("t_customer_remark", "customer_id = ? AND data_date = ?", args);

        sqLiteDatabase.close();

        return flag;
    }

    public void defaultUpdateRecord(int customerId) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        CustomerRemark cr = findRecordByCustomerId(customerId);
        if (cr != null) {
            Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT sum(white_frame_count) w, sum(green_frame_count) g FROM t_trade_record WHERE customer_id = " + customerId + " AND data_date = " + VariableUtils.DATADATE, null);

            if (cursor1.moveToFirst()) {
                cr.setWhiteGo(cursor1.getInt(cursor1.getColumnIndex("w")));
                cr.setGreenGo(cursor1.getInt(cursor1.getColumnIndex("g")));
            }
            float tradeSumPrice = new TradeRecordService().findSumPrice(customerId, VariableUtils.DATADATE);
            float vegComePrice = 0;
            try {
                JSONArray jsonArray = null;
                jsonArray = cr.getVegetableCome();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        vegComePrice += Float.parseFloat(jsonObject.get("sumPrice").toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //总价
            float sumPrice = 0;
            if (VariableUtils.APPTYPE == VariableUtils.ENUM_APP_TYPE.OUT.getAppType()) {

                //筐钱
                float framePrice = (cr.getWhiteGo()-cr.getWhiteCome()) * VariableUtils.WHITE_FRMAE_PRICE + (cr.getGreenGo()-cr.getGreenCome()) * VariableUtils.GREEN_FRAME_PRICE;
                sumPrice = tradeSumPrice + framePrice + cr.getOweMoney() - vegComePrice;

            } else if (VariableUtils.APPTYPE == VariableUtils.ENUM_APP_TYPE.IN.getAppType()) {

                sumPrice = tradeSumPrice + cr.getOweMoney() - vegComePrice;

            } else {

                sumPrice = (int)(tradeSumPrice + cr.getOweMoney() - vegComePrice);
            }

            cr.setAllMoney(sumPrice);

            updateRecord(cr);
        }
        sqLiteDatabase.close();
    }

    private ContentValues createValues(CustomerRemark c) {
        ContentValues vals = new ContentValues();
        vals.put("customer_id", c.getCustomerId());
        vals.put("white_go",  c.getWhiteGo());
        vals.put("white_come", c.getWhiteCome());
        vals.put("green_go", c.getGreenGo());
        vals.put("green_come", c.getGreenCome());
        vals.put("vegetable_come", c.getVegetableComeStr());
        vals.put("owe_money", c.getOweMoney());
        vals.put("all_money", c.getAllMoney());
        vals.put("sum_frame", c.getSumFrame());
        vals.put("data_date", c.getDataDate());

        return vals;
    }
}
