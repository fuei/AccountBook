package org.fuei.app.accountbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import org.fuei.app.accountbook.util.VariableUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by fuei on 2016/6/17.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "org.fuei.app.accountbook.date";

    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        //Create a Calendar to get the year, month and day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //Translate year, month, day into a Date object using a calendar
                mDate = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();

                //Update argument to preserve selected value on rotation
                //getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VariableUtils.SetDATADATE(mDate);
                                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                if (navigationView != null) {
                                    MenuItem subItem = navigationView.getMenu().findItem(R.id.settings)
                                            .getSubMenu().findItem(R.id.date_manage);
                                    subItem.setTitle("日期：" + VariableUtils.DataDateFormat(VariableUtils.DATADATE));
                                }
                            }
                        }
                )
                .create();
    }
}
