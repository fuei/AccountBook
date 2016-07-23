package org.fuei.app.accountbook;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by fuei on 2016/7/15.
 */
public class TradeRecordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int tradeId = (int)getIntent().getSerializableExtra(TradeRecordFragment.EXTRA_TR_ID);
        return TradeRecordFragment.newInstance(tradeId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem addBtn = menu.findItem(R.id.action_add);
        addBtn.setVisible(false);
        MenuItem saveBtn = menu.findItem(R.id.action_save);
        saveBtn.setVisible(true);

        return true;
    }
}
