package org.fuei.app.accountbook.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * Created by fuei on 2016/7/15.
 */
public class VariableUtils {
    //全局时间，默认当天
    public static int DATADATE = 20160715;

    //全局app类型，默认外市场
    public static int APPTYPE = APP_TYPE.OUT.getAppType();

    //当前客户id
    public static int CUSTOMERID = 0;

    public enum APP_TYPE {
        OUT(1), IN(2), FAMER(3);

        int app_type;
        APP_TYPE(int app_type) {
            this.app_type = app_type;
        }

        public int getAppType() {
            return app_type;
        }
    }

    /**
     * RadioButton对话框类型
     */
    public enum DIALOG_TYPE{
        CUSTOMER(1), VEGETABLE(2);

        int dialog_type;
        DIALOG_TYPE(int dialog_type) {
            this.dialog_type = dialog_type;
        }
        public int getDialogType() {
            return dialog_type;
        }
    }

    //单个筐重
    public static float UNIT_FRAME_WEIGHT = 4;
    //白筐单价
    public static float WHITE_FRMAE_PRICE = 30;
    //绿筐单价
    public static float GREEN_FRAME_PRICE = 15;

    public static String SaveOneNum(float value) {
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        String sumPriceAdjust = decimalFormat.format(value);

        return sumPriceAdjust;
    }

    public static File ExportExcel2SDCard(Context context, String appType, String dataDateStr, String customerName) {
        //判断sd卡是否存在
        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

        if (sdCardExist) {
            String parentPath = appType + "/" + dataDateStr + "/";
            String excelFileName = customerName + ".xls";
            File docPath = new File(context.getExternalFilesDir(null).getAbsolutePath() + parentPath);
            if (docPath != null && !docPath.exists()) {
                docPath.mkdirs();
            }

            File file = new File(docPath, excelFileName);
            return file;
        }

        return null;
    }
}
