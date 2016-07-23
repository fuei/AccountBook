package org.fuei.app.accountbook.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.fuei.app.accountbook.R;

import java.io.File;
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

            boolean sdCardExist = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
            boolean canWrite = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).canWrite();
            String excelFileName =  "/8810.xls";
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

                OutputStream os = new FileOutputStream(excelAllPath);
//                OutputStream os =
                wb.write(os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            fis.close();
        }


    }

    /**
     *
     * @param path 文件夹路径
     */
    private static void getOutExcelPath(String path) {
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getOutExcelDir(String dir) {
        File file = new File(dir);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }
}
