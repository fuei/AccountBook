package org.fuei.app.accountbook.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import org.fuei.app.accountbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by fuei on 2016/7/15.
 */
public class VariableUtils {
    //全局时间，默认当天
    public static int DATADATE;
    public static void SetDATADATE(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DATADATE = Integer.parseInt(sdf.format(date));
    }
    public static Date GetDATE() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.parse(DATADATE+"");
    }

    //全局app类型，默认外市场
    public static int APPTYPE = APP_TYPE.OUT.getAppType();

    //数据库全路径
    public static String DBFILE = "";

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

    /**
     * 保留一位小数
     * @param value
     * @return
     */
    public static String SaveOneNum(float value) {
        DecimalFormat decimalFormat=new DecimalFormat(".0");

        return decimalFormat.format(value);
    }

    /**
     * 日期格式化，如:2016.07.15
     * @param dataDate 日期
     * @return 格式化之后的日期
     */
    public static String DataDateFormat(int dataDate) {
        if (dataDate == 0) return null;
        String tempDateStr = (dataDate+"");
        return tempDateStr.substring(0,4) + "."
                + tempDateStr.substring(4,6) + "."
                + tempDateStr.substring(6,8);
    }

    /**
     * 文件导出到手机存储
     * @param context
     * @param appType
     * @param dataDateStr
     * @param customerName
     * @return 要保存的文件
     */
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

            return new File(docPath, excelFileName);
        }

        return null;
    }

    public enum SheetColumnIndexs {
        NAME(0),GROSS(1),FRAMECOUNT(2),FRAMEWEIGHT(3),NET(4),UNITPRICE(5),PRICE(6);

        int index;
        SheetColumnIndexs(int i) {
            this.index = i;
        }
        public int getIndex() {
            return index;
        }
    }

    public enum SheetRowIndexs {
        WHITEGO(20),GREENGO(21),WHITECOME(22),GREENCOME(23),SUMGO(24),OWE(25),ALLMONEY(26);

        int index;
        SheetRowIndexs(int i) {
            this.index = i;
        }
        public int getIndex() {
            return index;
        }
    }

    /**
     * 创建数据库
     * @param resources app资源
     */
    public static void CREATEDB(Resources resources) {
        // com.test.db 是程序的包名，请根据自己的程序调整
        // /data/data/com.test.db/
        // databases 目录是准备放 SQLite 数据库的地方，也是 Android 程序默认的数据库存储目录
        // 数据库名为 test.db
        String DB_PATH = "/data/data/org.fuei.app.accountbook/databases/";
        String DB_NAME = "account.db";
        DBFILE = DB_PATH + DB_NAME;

        // 检查 SQLite 数据库文件是否存在
        if (!(new File(DBFILE)).exists()) {
            // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
            File f = new File(DB_PATH);
            // 如 database 目录不存在，新建该目录
            if (!f.exists()) {
                f.mkdir();
            }

            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = resources.openRawResource(R.raw.account);
                // 输出流
                OutputStream os = new FileOutputStream(DBFILE);

                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                // 关闭文件流
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
