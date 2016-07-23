package org.fuei.app.accountbook;

import android.support.v4.app.Fragment;

/**
 * Created by fuei on 2016/6/1.
 */
public class TradeListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        int customId = (int)getIntent().getSerializableExtra(TradeListFragment.EXTRA_CUSTOMER_ID);
        return TradeListFragment.newInstance(customId);
    }
}
