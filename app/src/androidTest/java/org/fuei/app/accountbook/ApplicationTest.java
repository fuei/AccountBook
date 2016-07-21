package org.fuei.app.accountbook;

import android.app.Application;
import android.content.res.Resources;
import android.test.ApplicationTestCase;

import org.fuei.app.accountbook.util.ExportExcel;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        try {
            Resources r = Resources.getSystem();
            ExportExcel.read(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}