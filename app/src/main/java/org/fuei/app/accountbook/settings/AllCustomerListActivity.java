package org.fuei.app.accountbook.settings;

import android.support.v4.app.Fragment;

import org.fuei.app.accountbook.SingleFragmentActivity;

/**
 * Created by fuei on 2016/6/1.
 */
public class AllCustomerListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AllCustomerListFragment();
    }
}
