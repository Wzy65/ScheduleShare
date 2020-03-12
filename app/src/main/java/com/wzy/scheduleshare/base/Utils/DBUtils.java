package com.wzy.scheduleshare.base.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.base.modle.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DBUtils
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class DBUtils {
    private volatile static DBUtils INSTANCE;
    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;

    private DBUtils(Context context) {
        mHelper = new DBHelper(context);
        mDatabase = mHelper.getReadableDatabase();
    }

    public static DBUtils getINSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (DBUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBUtils(context);
                }
            }
        }
        return INSTANCE;
    }

    /*查询所有user有关的记录（本地）*/
    public List<ScheduleDetail> getAllDetail(String auth) {
        List<ScheduleDetail> list = null;
        Cursor cursor = mDatabase.query(DBHelper.LocalDetailTable,  //要操作的表名
                null,      //要返回的列的名字的数组,返回整条信息就null
                "auth=?",   //查询条件,where后的条件语句,不带内容 例如:id>? and name=?
                new String[]{auth},  //查询条件对应的内容 ,用于替换上一个参数中的 ？ ,顺序对应selection中？的顺序
                null,      //相当于SQL语句中的GROUP BY 关键字
                null,    //相当于SQL语句中的HAVING关键字
                "cast(updateAt as '9999')" + "desc",   //相当于SQL语句中的“ORDER BY”关键字
                null   //limit 例如3,4，从第3条开始查询4个
        );

        Logger.i("Local表查询到" + cursor.getCount() + "条数据");

        if (cursor.getCount() > 0) {
            ScheduleDetail detail;
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                detail = parseDetail(cursor);
                Logger.i("Local的createTime是：" + detail.getCreateTime());
                list.add(detail);
            }
        }
        cursor.close();
        return list;
    }

    /*读取三日内即将发生的*/
    public List<ScheduleDetail> getHappen(String str) {
        List<ScheduleDetail> list = null;
        long now = System.currentTimeMillis();//一天86400秒;
        long next = System.currentTimeMillis() + 86400000 * 3;//一天86400秒;
        Cursor cursor = mDatabase.query(DBHelper.LocalDetailTable,  //要操作的表名
                null,      //要返回的列的名字的数组,返回整条信息就null
                "auth=? and startAt>? and startAt<?",   //查询条件,where后的条件语句,不带内容 例如:id>? and name=?
                new String[]{str, String.valueOf(now), String.valueOf(next)},  //查询条件对应的内容 ,用于替换上一个参数中的 ？ ,顺序对应selection中？的顺序
                null,      //相当于SQL语句中的GROUP BY 关键字
                null,    //相当于SQL语句中的HAVING关键字
                "cast(startAt as '9999')" + "asc",   //相当于SQL语句中的“ORDER BY”关键字
                null   //limit 例如3,4，从第3条开始查询4个
        );
        Logger.i("Local表查询到三天内发生的" + cursor.getCount() + "条数据");
        if (cursor.getCount() > 0) {
            ScheduleDetail detail;
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                detail = parseDetail(cursor);
                Logger.i("Local的startTime是：" + detail.getCreateTime());
                list.add(detail);
            }
        }
        cursor.close();

        return list;
    }

    /*查询临时记录*/
    public ScheduleDetail getTempDetail(String auth) {
        ScheduleDetail detail = null;
        Cursor cursor = mDatabase.query(DBHelper.TempDetailTable,  //要操作的表名
                null,      //要返回的列的名字的数组,返回整条信息就null
                "auth=?",   //查询条件,where后的条件语句,不带内容 例如:id>? and name=?
                new String[]{auth},  //查询条件对应的内容 ,用于替换上一个参数中的 ？ ,顺序对应selection中？的顺序
                null,      //相当于SQL语句中的GROUP BY 关键字
                null,    //相当于SQL语句中的HAVING关键字
                null,//相当于SQL语句中的“ORDER BY”关键字
                null   //limit 例如3,4，从第4条开始查询4个
        );   //相当于SQL语句中的“ORDER BY”关键字

        Logger.i("Temp表查询到" + cursor.getCount() + "条数据");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                detail = parseDetailTemp(cursor);
            }
        }
        cursor.close();
        return detail;
    }


    /*插入新的数据（已保存）*/
    public void insert2Local(ScheduleDetail detail) {
        //mDatabase.beginTransaction();  //开始一个新的事务，这里可以省略
        ContentValues values = new ContentValues();

        values.put("createTime", detail.getCreateTime());
        values.put("objectId", detail.getObjectId());
        values.put("title", detail.getTitle());
        values.put("content", detail.getContent());
        values.put("brief", detail.getBrief());
        values.put("updateAt", detail.getUpdatedAt());
        values.put("startAt", detail.getStartAt());
        values.put("endAt", detail.getEndAT());
        values.put("auth", detail.getAuth().getObjectId());
        values.put("status", detail.getStatus());
        long id = mDatabase.replace(DBHelper.LocalDetailTable, null, values);
        //mDatabase.endTransaction();
        Logger.i("Local表插入一条数据" + "\n" + "createTime:" + detail.getCreateTime() + "\n" + detail.getObjectId() + "\n" + "title:" + detail.getTitle());
    }

    /*插入新的数据（临时）*/
    public void insert2Temp(ScheduleDetail detail) {
        //mDatabase.beginTransaction();  //开始一个新的事务，这里可以省略
        ContentValues values = new ContentValues();
        values.put("title", detail.getTitle());
        values.put("content", detail.getContent());
        values.put("updateAt", detail.getUpdatedAt());
        values.put("startAt", detail.getStartAt());
        values.put("endAt", detail.getEndAT());
        values.put("auth", detail.getAuth().getObjectId());
        values.put("status", detail.getStatus());
        long id = mDatabase.replace(DBHelper.TempDetailTable, null, values);
        //mDatabase.endTransaction();
        Logger.i("Temp表插入一条数据" + "\n" + "id:" + id + "\n" + detail.getAuth().getObjectId() + "\n" + "title:" + detail.getTitle());
    }

    /*删除一条临时数据*/
    public void deleteTemp(String auth) {
        mDatabase.delete(DBHelper.TempDetailTable, "auth=?", new String[]{auth});
        Logger.i("Temp表删除一条数据" + "\n" + "id:" + auth);

    }

    /*删除一条数据*/
    public void deleteDetail(String createTime) {
        mDatabase.delete(DBHelper.LocalDetailTable, "createTime=?", new String[]{String.valueOf(createTime)});
        Logger.i("Temp表删除一条数据" + "\n" + "createTime:" + createTime);

    }

    private ScheduleDetail parseDetail(Cursor cursor) {
        ScheduleDetail detail = new ScheduleDetail();
        detail.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
        detail.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
        detail.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        detail.setContent(cursor.getString(cursor.getColumnIndex("content")));
        detail.setBrief(cursor.getString(cursor.getColumnIndex("brief")));
        detail.setUpdatedAt(cursor.getString(cursor.getColumnIndex("updateAt")));
        detail.setStartAt(cursor.getString(cursor.getColumnIndex("startAt")));
        detail.setEndAT(cursor.getString(cursor.getColumnIndex("endAt")));
        User user = new User();
        user.setObjectId(cursor.getString(cursor.getColumnIndex("auth")));
        detail.setAuth(user);
        detail.setStatus(cursor.getString(cursor.getColumnIndex("status")));

        return detail;
    }

    private ScheduleDetail parseDetailTemp(Cursor cursor) {
        ScheduleDetail detail = new ScheduleDetail();
        detail.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        detail.setContent(cursor.getString(cursor.getColumnIndex("content")));
        detail.setUpdatedAt(cursor.getString(cursor.getColumnIndex("updateAt")));
        detail.setStartAt(cursor.getString(cursor.getColumnIndex("startAt")));
        detail.setEndAT(cursor.getString(cursor.getColumnIndex("endAt")));
        User user = new User();
        user.setObjectId(cursor.getString(cursor.getColumnIndex("auth")));
        detail.setAuth(user);
        detail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        return detail;
    }

}
