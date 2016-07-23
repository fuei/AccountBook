package org.fuei.app.accountbook;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by fuei on 2016/7/17.
 */
public class CustomerRemarkActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int crId = (int)getIntent().getSerializableExtra(CustomerRemarkFragment.EXTRA_CR_ID);
        return CustomerRemarkFragment.newInstance(crId);
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
