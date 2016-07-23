package org.fuei.app.accountbook.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.po.Vegetable;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/24.
 */
public class VegetableLab {
    private ArrayList<Vegetable> mVeges;

    private static VegetableLab sVegeLab;
    private Context mAppContext;

    private VegetableLab(Context appContext) {
        mAppContext = appContext;
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

    public static VegetableLab get(Context c) {
//        if (sVegeLab == null) {
        sVegeLab = new VegetableLab(c.getApplicationContext());
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

    public void addVegetable(Vegetable c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String insertSql = "insert into t_vegetable(name, unit_price) values(?,?)";
        sqLiteDatabase.execSQL(insertSql, new Object[]{c.getName(), c.getUnitPrice()});
        sqLiteDatabase.close();

        mVeges.add(c);
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
