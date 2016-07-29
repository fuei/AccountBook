package org.fuei.app.accountbook.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/24.
 */
public class VegetableLab {
    private ArrayList<Vegetable> mVeges;

    private static VegetableLab sVegeLab;

    private VegetableLab() {
        mVeges = new ArrayList<Vegetable>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        String selectSql = "select * from t_vegetable";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
            Vegetable vegetable = new Vegetable();
            vegetable.setId(cursor.getInt(cursor.getColumnIndex("id")));
            vegetable.setName(cursor.getString(cursor.getColumnIndex("name")));
            vegetable.setUnitPrice(cursor.getFloat(cursor.getColumnIndex("unit_price")));
            mVeges.add(vegetable);

        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public static VegetableLab get() {
//        if (sVegeLab == null) {
        sVegeLab = new VegetableLab();
//        }
        return sVegeLab;
    }

    public ArrayList<Vegetable> getVeges() {
        return mVeges;
    }

    public Vegetable getVegetable(int id) {
        for (Vegetable c : mVeges) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public int addVegetable(Vegetable c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String insertSql = "insert into t_vegetable(name, unit_price) values(?,?)";
        sqLiteDatabase.execSQL(insertSql, new Object[]{c.getName(), c.getUnitPrice()});

        Cursor cursor = sqLiteDatabase.rawQuery("select last_insert_rowid() from t_vegetable", null);
        int strid = 0;
        if(cursor.moveToFirst())
            strid = cursor.getInt(0);

        sqLiteDatabase.close();

        mVeges.add(c);

        return strid;
    }

    public void deleteVegetable(Vegetable c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String[] args = {String.valueOf(c.getId())};
        int flag = sqLiteDatabase.delete("t_vegetable", "id = ?", args);
        sqLiteDatabase.close();

        if (flag == 1) {
            mVeges.remove(c);
        }
    }

    public void updateVegetable(Vegetable c) {

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put("unit_price", c.getUnitPrice());
        int flag = sqLiteDatabase.update("t_vegetable", contentValues, "id = " + c.getId(), null);

        sqLiteDatabase.close();
    }
}
