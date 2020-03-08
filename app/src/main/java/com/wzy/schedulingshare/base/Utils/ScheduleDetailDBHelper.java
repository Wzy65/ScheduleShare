package com.wzy.schedulingshare.base.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName ScheduleDetailDBHelper
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class ScheduleDetailDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "ScheduleDetailLocal.db";
    public static final String LocalDetailTable = "LocalDetailTable";  //信息保存表
    public static final String TempDetailTable = "TempDetailTable";  //临时信息


    public ScheduleDetailDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * 负责数据库的创建和初始化，只在第一次生成数据库的时候
     * 会被调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + LocalDetailTable + "(" +
                "_id integer primary key autoincrement, " +
                "objectId text unique," +  //唯一，对应Bomb云
                "title text ," +   //标题
                "content text ," +   //图文消息内容
                "brief text ," +   //简略消息内容
                "updateAt text ," +  //更新日期
                "startAt text ," +   //事件开始日期
                "endAt text , " +    //事件结束日期
                "auth text ," +      //作者，这里保存作者的objectId
                "status text " +    //状态，备用
                ")"
        );
        /*创建临时状态下保存的表，例如app的onStop()周期时可以保存临时信息，避免数据丢失*/
        db.execSQL("create table if not exists " + TempDetailTable + "(" +
                "auth text primary key ," +      //作者，这里保存作者的objectId
                "title text ," +   //标题
                "content text ," +   //图文消息内容
                "updateAt text ," +  //更新日期
                "startAt text ," +   //事件开始日期
                "endAt text , " +    //事件结束日期
                "status text " +    //状态，备用
                ")"
        );
    }

    /**
     * 数据库升级的时候才会调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
