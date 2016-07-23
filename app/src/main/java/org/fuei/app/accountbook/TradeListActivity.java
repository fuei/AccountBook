package org.fuei.app.accountbook;

import android.support.v4.app.Fragment;

/**
 * Created by fuei on 2016/6/1.
 */
public class TradeListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment(int flag) {
        switch (flag) {
            case 1:
                int customId = (int)getIntent().getSerializableExtra(TradeListFragment.EXTRA_CUSTOMER_ID);
                return TradeListFragment.newInstance(customId);
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
        }
        return null;
    }
}
