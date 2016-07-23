package org.fuei.app.accountbook.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.MainActivity;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.CustomerRemark;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/17.
 */
public class CustomerRemarkLab {
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
            customerRemark.setOweMoney(cursor.getInt(cursor.getColumnIndex("owe_money")));
            customerRemark.setAllMoney(cursor.getInt(cursor.getColumnIndex("all_money")));
            customerRemark.setSumFrame(cursor.getInt(cursor.getColumnIndex("sum_frame")));
            customerRemark.setDataDate(cursor.getInt(cursor.getColumnIndex("data_date")));
        }
        cursor.close();
        sqLiteDatabase.close();

        return customerRemark;
    }

    public void insertRecord(CustomerRemark c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

//        String insertSql = "insert into t_customer_remark(customer_id, white_go, white_come, green_go, green_come, vegetable_come, owe_money, all_money, sum_frame, data_date) values(?,?,?,?,?,?,?,?,?,?)";
//
//        sqLiteDatabase.execSQL(insertSql, new Object[]{c.getCustomerId(), c.getWhiteGo(), c.getWhiteCome(), c.getGreenGo(), c.getGreenCome(), c.getVegetableCome(), c.getOweMoney(), c.getAllMoney(), c.getSumFrame(), c.getDataDate()});

        sqLiteDatabase.insert("t_customer_remark", null, createValues(c));

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

        ContentValues cv = new ContentValues();
        String[] args = {String.valueOf(customerId), String.valueOf(VariableUtils.DATADATE)};
        int flag = sqLiteDatabase.delete("t_customer_remark", "customer_id = ? AND data_date = ?", args);

        sqLiteDatabase.close();

        return flag;
    }

    public void insertOrUpdateFrameCount(int customerId) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("select id from t_customer_remark where customer_id = "+ customerId +" AND data_date = " + VariableUtils.DATADATE, null);
        int id = 0;
        if(cursor.moveToFirst())
            id = cursor.getInt(0);

        Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT sum(white_frame_count) w, sum(green_frame_count) g FROM t_trade_record WHERE customer_id = " + customerId + " AND data_date = " + VariableUtils.DATADATE, null);
        int wFrameCount = 0;
        int gFrameCount = 0;
        if (cursor1.moveToFirst()) {
            wFrameCount = cursor1.getInt(cursor1.getColumnIndex("w"));
            gFrameCount = cursor1.getInt(cursor1.getColumnIndex("g"));
        }

        ContentValues vals = new ContentValues();
        vals.put("white_go", wFrameCount);
        vals.put("green_go", gFrameCount);
        vals.put("sum_frame", wFrameCount + gFrameCount);
        if (id == 0) {
            // insert
            sqLiteDatabase.insert("t_customer_remark", null, vals);
        } else {
            // update
            sqLiteDatabase.update("t_customer_remark", vals, "id = " + id, null);
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
