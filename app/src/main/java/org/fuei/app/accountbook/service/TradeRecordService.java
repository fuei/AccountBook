package org.fuei.app.accountbook.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.fuei.app.accountbook.MainActivity;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/11.
 */
public class TradeRecordService {
    private ArrayList<TradeRecord> mTradeRecords;

    private Context mAppContext;
    private int mCustomerId;

    public TradeRecordService(){}

    public TradeRecordService(Context appContext, int customerId) {
        mAppContext = appContext;
        mCustomerId = customerId;
    }

    public ArrayList<TradeRecord> findTradeRecords(int customerId) {
        mTradeRecords = new ArrayList<TradeRecord>();
        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        String selectSql = "SELECT t.id, t.vegetable_id, t.customer_id, t.gross_weight, t.net_weight, t.white_frame_count, t.green_frame_count, t.frame_weight, t.unit_price, t.sum_price,t.data_date, v.name FROM t_trade_record t LEFT OUTER JOIN t_vegetable v ON t.vegetable_id = v.id where t.customer_id = "+ customerId +" AND t.data_date = " + VariableUtils.DATADATE;
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        while (cursor.moveToNext()) {
            TradeRecord t = new TradeRecord();

            t.setId(cursor.getInt(cursor.getColumnIndex("id")));
            t.setVegetableId(cursor.getInt(cursor.getColumnIndex("vegetable_id")));
            t.setVegetableName(cursor.getString(cursor.getColumnIndex("name")));
            t.setCustomerId(cursor.getInt(cursor.getColumnIndex("customer_id")));
            t.setGrossWeight(cursor.getFloat(cursor.getColumnIndex("gross_weight")));
            t.setNetWeight(cursor.getFloat(cursor.getColumnIndex("net_weight")));
            t.setWhiteFrameCount(cursor.getInt(cursor.getColumnIndex("white_frame_count")));
            t.setGreenFrameCount(cursor.getInt(cursor.getColumnIndex("green_frame_count")));
            t.setFrameWeight(cursor.getFloat(cursor.getColumnIndex("frame_weight")));
            t.setUnitPrice(cursor.getFloat(cursor.getColumnIndex("unit_price")));
            t.setSumPrice(cursor.getFloat(cursor.getColumnIndex("sum_price")));
            t.setDataDate(cursor.getInt(cursor.getColumnIndex("data_date")));

            mTradeRecords.add(t);
        }
        cursor.close();
        sqLiteDatabase.close();

        return mTradeRecords;
    }

    public TradeRecord findRecordByVegId(int vegId, int isNew) {
        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String unitPriceSql = "";
        if (isNew == 1) {
            unitPriceSql = "v.unit_price";
        } else {
            unitPriceSql = "t.unit_price";
        }

        String selectSql = "SELECT t.id, t.vegetable_id, t.customer_id, t.gross_weight, t.net_weight, t.white_frame_count, t.green_frame_count, t.frame_weight, " + unitPriceSql + ", t.sum_price,t.data_date, v.name FROM t_trade_record t LEFT OUTER JOIN t_vegetable v ON t.vegetable_id = v.id where t.customer_id = "+ mCustomerId +" AND t.vegetable_id = "+ vegId + " AND t.data_date = " + VariableUtils.DATADATE;
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        TradeRecord t = null;
        if (cursor.moveToNext()) {
            t = new TradeRecord();

            t.setId(cursor.getInt(cursor.getColumnIndex("id")));
            t.setVegetableId(cursor.getInt(cursor.getColumnIndex("vegetable_id")));
            t.setVegetableName(cursor.getString(cursor.getColumnIndex("name")));
            t.setCustomerId(cursor.getInt(cursor.getColumnIndex("customer_id")));
            t.setGrossWeight(cursor.getFloat(cursor.getColumnIndex("gross_weight")));
            t.setNetWeight(cursor.getFloat(cursor.getColumnIndex("net_weight")));
            t.setWhiteFrameCount(cursor.getInt(cursor.getColumnIndex("white_frame_count")));
            t.setGreenFrameCount(cursor.getInt(cursor.getColumnIndex("green_frame_count")));
            t.setFrameWeight(cursor.getFloat(cursor.getColumnIndex("frame_weight")));
            t.setUnitPrice(cursor.getFloat(cursor.getColumnIndex("unit_price")));
            t.setSumPrice(cursor.getFloat(cursor.getColumnIndex("sum_price")));
            t.setDataDate(cursor.getInt(cursor.getColumnIndex("data_date")));

        }
        cursor.close();
        sqLiteDatabase.close();

        return t;
    }

    public void insertRecord(int vegId) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String insertSql = "insert into t_trade_record(vegetable_id, customer_id, data_date) values(?,?,?)";
        sqLiteDatabase.execSQL(insertSql, new Object[]{vegId, mCustomerId, VariableUtils.DATADATE});

//        Cursor cursor = sqLiteDatabase.rawQuery("select last_insert_rowid() from t_trade_record", null);
//        int strid = 0;
//        if(cursor.moveToFirst())
//            strid = cursor.getInt(0);

        sqLiteDatabase.close();
    }

    public int updateRecord(TradeRecord t) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        ContentValues updateValues = new ContentValues();
        updateValues.put("gross_weight", t.getGrossWeight());
        updateValues.put("net_weight", t.getNetWeight());
        updateValues.put("white_frame_count", t.getWhiteFrameCount());
        updateValues.put("green_frame_count", t.getGreenFrameCount());
        updateValues.put("frame_weight", t.getFrameWeight());
        updateValues.put("unit_price", t.getUnitPrice());
        updateValues.put("sum_price", t.getSumPrice());

        int flag = sqLiteDatabase.update("t_trade_record", updateValues, "id = " + t.getId(), null);

        sqLiteDatabase.close();

        return flag;
    }

    public int deleteRecord(int id) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        ContentValues cv = new ContentValues();
        String[] args = {String.valueOf(id)};
        int flag = sqLiteDatabase.delete("t_trade_record", "id = ?", args);

        sqLiteDatabase.close();

        return flag;
    }

    public int deleteRecordByCustomerId(int customerId) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        ContentValues cv = new ContentValues();
        String[] args = {String.valueOf(customerId), String.valueOf(VariableUtils.DATADATE)};
        int flag = sqLiteDatabase.delete("t_trade_record", "customer_id = ? AND data_date = ?", args);

        sqLiteDatabase.close();
        return flag;
    }

    public ArrayList<Customer> findTradeCustomers(int customerType) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String sql = "SELECT DISTINCT t.customer_id, c.name, c.type FROM t_trade_record t, t_customer c WHERE c.type = "+ customerType +" AND t.customer_id = c.id AND t.data_date = " + VariableUtils.DATADATE;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        ArrayList<Customer> customers = new ArrayList<Customer>();

        while (cursor.moveToNext()) {
            Customer customer = new Customer();
            customer.setId(cursor.getInt(cursor.getColumnIndex("customer_id")));
            customer.setName(cursor.getString(cursor.getColumnIndex("name")));
            customer.setType(cursor.getInt(cursor.getColumnIndex("type")));

            customers.add(customer);
        }
        cursor.close();
        sqLiteDatabase.close();

        return customers;
    }

    public void deleteTradeRecord(TradeRecord t) {
        mTradeRecords.remove(t);
    }

    /**
     * 查询昨天顾客所买菜的名称价格列表
     * @param customerId 顾客ID
     * @param dataDate 数据日期
     * @return 菜列表
     */
    public ArrayList<TradeRecord> findVegetableList(int customerId, int dataDate) {

        ArrayList<TradeRecord> vegetableList = new ArrayList<TradeRecord>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        String selectSql = "SELECT t.vegetable_id, v.name, t.unit_price FROM t_trade_record t LEFT OUTER JOIN t_vegetable v ON t.vegetable_id = v.id WHERE t.customer_id = " + customerId + " AND t.data_date = " + dataDate;
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        TradeRecord t = null;
        while (cursor.moveToNext()) {
            t = new TradeRecord();

            t.setVegetableId(cursor.getInt(cursor.getColumnIndex("vegetable_id")));
            t.setVegetableName(cursor.getString(cursor.getColumnIndex("name")));
            t.setUnitPrice(cursor.getFloat(cursor.getColumnIndex("unit_price")));

            vegetableList.add(t);
        }
        cursor.close();
        sqLiteDatabase.close();

        return vegetableList;
    }

    public float findSumPrice(int customerId, int dataDate) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT sum(sum_price) FROM t_trade_record where customer_id = "+ customerId +" AND data_date = " + dataDate, null);
        float sumPrice = 0;
        if(cursor.moveToFirst())
            sumPrice = cursor.getInt(0);

        sqLiteDatabase.close();

        return sumPrice;
    }
}
