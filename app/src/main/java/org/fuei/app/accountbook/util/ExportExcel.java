package org.fuei.app.accountbook.util;

import android.content.res.Resources;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.fuei.app.accountbook.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.LongBuffer;

/**
 * Created by fuei on 2016/7/21.
 */
public class ExportExcel {

    public static void read(Resources res) throws IOException{

        InputStream fis = res.openRawResource(R.raw.template);;
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        try {
            HSSFSheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

            HSSFCell customerNameCell = sheet.getRow(1).getCell(1);
            customerNameCell.setCellValue("8810");
            Log.d("客户：", customerNameCell.getStringCellValue());

            OutputStream os = new FileOutputStream("/data/data/org.fuei.app.accountbook/files/8810.xls");
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            fis.close();
        }


    }
}
