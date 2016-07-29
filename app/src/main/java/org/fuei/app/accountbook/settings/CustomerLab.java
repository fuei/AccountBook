package org.fuei.app.accountbook.settings;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/5/23.
 */
public class CustomerLab {
    private ArrayList<Customer> mCustomers;

    private static CustomerLab sCustomerLab;

    private CustomerLab() {
        mCustomers = new ArrayList<Customer>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);
        String selectSql = "select * from t_customer where type = " + VariableUtils.APPTYPE;
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
            Customer customer = new Customer();
            customer.setId(cursor.getInt(cursor.getColumnIndex("id")));
            customer.setName(cursor.getString(cursor.getColumnIndex("name")));
            customer.setType(cursor.getInt(cursor.getColumnIndex("type")));
            mCustomers.add(customer);

        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public static CustomerLab get() {
//        if (sCustomerLab == null) {
            sCustomerLab = new CustomerLab();
//        }
        return sCustomerLab;
    }

    public ArrayList<Customer> getCustomers() {
        return mCustomers;
    }

    public Customer getCustomer(int id) {
        for (Customer c : mCustomers) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public int addCustomer(Customer c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String insertSql = "insert into t_customer(name, type) values(?,?)";
        sqLiteDatabase.execSQL(insertSql, new Object[]{c.getName(), c.getType()});

        Cursor cursor = sqLiteDatabase.rawQuery("select last_insert_rowid() from t_customer", null);
        int strid = 0;
        if(cursor.moveToFirst())
            strid = cursor.getInt(0);


        sqLiteDatabase.close();

        mCustomers.add(c);

        return strid;
    }

    public void deleteCustomer(Customer c) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(VariableUtils.DBFILE, null);

        String[] args = {String.valueOf(c.getId())};
        int flag = sqLiteDatabase.delete("t_customer", "id = ?", args);
        sqLiteDatabase.close();

        if (flag == 1) {
            mCustomers.remove(c);
        }
    }
}
