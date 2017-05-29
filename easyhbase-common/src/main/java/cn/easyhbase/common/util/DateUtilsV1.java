package cn.easyhbase.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilsV1 {

    private DateUtilsV1() {
    }

    private static final String[] FORMATS = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "HH:mm", "HH:mm:ss", "yyyy-MM"};

    /**
     * 取得系统当前时间
     *
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String getCurDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = simpleDateFormat.format(date);
        return time;
    }
    public static Date addDay(Date date, int days) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, days);
        return startDT.getTime();
    }
}
