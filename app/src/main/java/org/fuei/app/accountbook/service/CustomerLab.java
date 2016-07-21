package org.fuei.app.accountbook.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fuei.app.accountbook.MainActivity;
import org.fuei.app.accountbook.po.Customer;
import org.fuei.app.accountbook.util.VariableUtils;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/5/23.
 */
public class CustomerLab {
    private ArrayList<Customer> mCustomers;

    private static CustomerLab sCustomerLab;
    private Context mAppContext;

    private CustomerLab(Context appContext) {
        mAppContext = appContext;
        mCustomers = new ArrayList<Customer>();

        //select
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(MainActivity.DBFILE, null);
        String selectSql = "select * from t_customer";
        Cursor cursor = sqLiteDatabase.rawQuery(selectSql, null);

        ArrayList<Customer> tradeCustomers;
        tradeCustomers = new TradeRecordLab().findTradeCustomers(VariableUtils.APPTYPE);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));

            int flag = 0;
            for(Customer c: tradeCustomers) {
                if (c.getId() == id) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                Customer customer = new Customer();
                customer.setId(id);
                customer.setName(cursor.getString(cursor.getColumnIndex("name")));
                customer.setType(cursor.getInt(cursor.getColumnIndex("type")));
                mCustomers.add(customer);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public static CustomerLab get(Context c) {
//        if (sCustomerLab == null) {
            sCustomerLab = new CustomerLab(c.getApplicationContext());
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

    public void addCustomer(Customer c) {
        mCustomers.add(c);
    }

    public void deleteCustomer(Customer c) {
        mCustomers.remove(c);
    }
}
