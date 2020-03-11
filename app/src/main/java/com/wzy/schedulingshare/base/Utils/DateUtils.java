package com.wzy.schedulingshare.base.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName DateUtils
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */
public class DateUtils {

    private static SimpleDateFormat mSimpleDateFormat = null;

    //获取系统时间
    public static String getCurrentDate() {
        Date d = new Date();
        mSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒SSS毫秒", Locale.getDefault());
        return mSimpleDateFormat.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return mSimpleDateFormat.format(d);
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        try {
            date = mSimpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*从字符串获取获取Date*/
    public static Date getString2Date(String time){
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        try {
            date = mSimpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}