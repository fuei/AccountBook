package org.fuei.app.accountbook.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/16.
 */
public class VegetableService {
    private ArrayList<Vegetable> mVegetables;

    private static VegetableService sVegetableService;
    private Context mAppContext;

    private VegetableService(Context appContext) {
        mAppContext = appContext;
        mVegetables = new ArrayList<Vegetable>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        String selectSql = "select * from t_vegetable";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        ArrayList<TradeRecord> tradeRecords;
        tradeRecords = new TradeRecordService().findTradeRecords(VariableUtils.CUSTOMERID);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));

            int flag = 0;
            for(TradeRecord t: tradeRecords) {
                if (t.getVegetableId() == id) {
                    flag = 1;
                }
            }

            if (flag == 0) {
                Vegetable v = new Vegetable();
                v.setId(id);
                v.setName(cursor.getString(cursor.getColumnIndex("name")));
                v.setUnitPrice(cursor.getFloat(cursor.getColumnIndex("unit_price")));
                mVegetables.add(v);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public static VegetableService get(Context c) {
//        if (sVegetableService == null) {
            sVegetableService = new VegetableService(c.getApplicationContext());
//        }
        return sVegetableService;
    }

    public ArrayList<Vegetable> getVegetables() {
        return mVegetables;
    }

    public Vegetable getVegetable(int id) {
        for (Vegetable v : mVegetables) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public void addVegetable(Vegetable v) {
        mVegetables.add(v);
    }

    public void deleteVegetable(Vegetable v) {
        mVegetables.remove(v);
    }
}
