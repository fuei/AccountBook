package org.fuei.app.accountbook;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.fuei.app.accountbook.util.ExportExcel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() throws Exception{
        super(Application.class);

//        try {
//            Resources r = Resources.getSystem();
//            ExportExcel.read(r);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        InputStream fis = Resources.getSystem().openRawResource(R.raw.template);;
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        try {
            HSSFSheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

            HSSFCell customerNameCell = sheet.getRow(1).getCell(1);
            customerNameCell.setCellValue("8810");
            Log.d("客户：", customerNameCell.getStringCellValue());

            boolean sdCardExist = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
            boolean canWrite = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).canWrite();
            String excelFileName =  "8810.xls";
//            File path = Environment.getDataDirectory();

//            if (path.exists()) {
            if (sdCardExist) {
                // String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/accountdata/";
                File docPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                if (!docPath.exists()) {
                    docPath.mkdir();
                }
                docPath.setWritable(true);
//                String excelAllPath = path + excelFileName;
                String excelAllPath = docPath.getPath() + excelFileName;

                //File excelFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "8810.xls");
//                File excelFile = new File(excelAllPath);
//                if (!excelFile.exists()) {
//                    excelFile.createNewFile();
//                    excelFile.setWritable(Boolean.TRUE);
//                }

//                OutputStream os = new FileOutputStream(excelAllPath);
                OutputStream os = getContext().openFileOutput(excelFileName, Context.MODE_WORLD_READABLE);
                wb.write(os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            fis.close();
        }
    }
}