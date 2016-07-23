package org.fuei.app.accountbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.fuei.app.accountbook.settings.AllCustomerListActivity;
import org.fuei.app.accountbook.settings.AllVegetableListActivity;
import org.fuei.app.accountbook.util.VariableUtils;

import java.text.ParseException;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置应用的日期
        VariableUtils.SetDATADATE(new Date());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new CustomerListFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.outClass);
            setSupportActionBar(toolbar);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            MenuItem subItem = navigationView.getMenu().findItem(R.id.settings)
                                    .getSubMenu().findItem(R.id.date_manage);
            subItem.setTitle("日期：" + VariableUtils.DataDateFormat(VariableUtils.DATADATE));
        }

        //添加数据库
        VariableUtils.CREATEDB(getResources());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem btn = menu.findItem(R.id.export);
        btn.setVisible(true);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ActionBar actionBar = getSupportActionBar();
        switch (id) {
            case R.id.nav_out:
                VariableUtils.APPTYPE = VariableUtils.APP_TYPE.OUT.getAppType();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.outClass);
                }
                this.closeDrawer(new CustomerListFragment());

                break;
            case R.id.nav_in:
                VariableUtils.APPTYPE = VariableUtils.APP_TYPE.IN.getAppType();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.inClass);
                }
                this.closeDrawer(new CustomerListFragment());

                break;
            case R.id.nav_farmer:
                VariableUtils.APPTYPE = VariableUtils.APP_TYPE.FAMER.getAppType();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.famerClass);
                }
                this.closeDrawer(new CustomerListFragment());

                break;
            case R.id.date_manage:
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment dialog = null;
                try {
                    dialog = DatePickerFragment.newInstance(VariableUtils.GetDATE());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (dialog != null) {
                    dialog.show(fm, "date");
                }
                break;
            case R.id.customer_manage:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("客户类型")
                        .setItems(R.array.apptype, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getApplicationContext(), which+"", Toast.LENGTH_LONG).show();
                                VariableUtils.APPTYPE = which + 1;
                                Intent i = new Intent(getApplicationContext(), AllCustomerListActivity.class);
                                startActivity(i);
                            }
                        });

                AlertDialog ad = builder.create();
                ad.show();

                break;
            case R.id.vegetable_manage:
                Intent i = new Intent(getApplicationContext(), AllVegetableListActivity.class);
                startActivity(i);

                break;
        }
        return true;
    }

    private void closeDrawer(Fragment currentFrag) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
            if (fragment != null) {
                fm.beginTransaction().remove(fragment).commit();
            }
            fragment = currentFrag;
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

}
