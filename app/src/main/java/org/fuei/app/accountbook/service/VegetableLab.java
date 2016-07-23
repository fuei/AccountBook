package org.fuei.app.accountbook.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.MainActivity;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.TradeRecord;
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/16.
 */
public class VegetableLab {
    private ArrayList<Vegetable> mVegetables;

    private static VegetableLab sVegetableLab;
    private Context mAppContext;

    private VegetableLab(Context appContext) {
        mAppContext = appContext;
        mVegetables = new ArrayList<Vegetable>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(MainActivity.DBFILE, null);
        String selectSql = "select * from t_vegetable";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        ArrayList<TradeRecord> tradeRecords;
        tradeRecords = new TradeRecordLab().findTradeRecords(VariableUtils.CUSTOMERID);
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
                v.setUnitPrice(cursor.getInt(cursor.getColumnIndex("unit_price")));
                mVegetables.add(v);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public static VegetableLab get(Context c) {
//        if (sVegetableLab == null) {
            sVegetableLab = new VegetableLab(c.getApplicationContext());
//        }
        return sVegetableLab;
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
