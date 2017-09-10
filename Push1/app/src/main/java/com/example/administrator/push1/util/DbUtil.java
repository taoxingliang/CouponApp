package com.example.administrator.push1.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.administrator.push1.bean.UserInfo;
import com.example.administrator.push1.sqllite.SqlLiteHelper;

/**
 * Created by Administrator on 2017/7/9.
 */

public class DbUtil {

    public static SqlLiteHelper helper;

    public static void initDb(Context context) {
        helper = new SqlLiteHelper(context, "push", null, 1);
    }

    public static UserInfo getLocalUser() {

        Cursor cursor = helper.getReadableDatabase().query ("account",null,null,null,null,null,null);
        int prority = -1;
        if(cursor.moveToFirst()) {
            UserInfo info = new UserInfo();
            for(int i=0;i<cursor.getCount();i++){

                cursor.move(i);
                if (cursor.getInt(0) > prority) {
                    prority = cursor.getInt(0);
                    info.setAccount(cursor.getString(0));
                    info.setPassword(cursor.getString(1));
                }

            }
            return info;
        }
        return null;
    }

    public static UserInfo queryUser(UserInfo userInfo) {
        Cursor cursor = helper.getReadableDatabase().query ("account",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            UserInfo info = new UserInfo();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                if (userInfo.getAccount().equals(cursor.getInt(0))) {
                    info.setAccount(cursor.getString(0));
                    info.setPassword(cursor.getString(1));
                    return info;
                }
            }
        }
        return null;
    }

    public static void insertUser(UserInfo info) {
        Cursor cursor = helper.getReadableDatabase().query ("account",null,null,null,null,null,null);
        int prority = -1;
        if(cursor.moveToFirst()) {
            for(int i=0;i<cursor.getCount();i++) {
                cursor.move(i);
                if (cursor.getInt(0) > prority) {
                    prority = cursor.getInt(0);
                }
            }
        }
        ContentValues cValue = new ContentValues();
        cValue.put("account",info.getAccount());
        cValue.put("password",info.getPassword());
        cValue.put("priority",prority + 1);
        helper.getWritableDatabase().insert("account",null,cValue);
    }

    public static void updateUser(UserInfo info) {
        Cursor cursor = helper.getReadableDatabase().query ("account",null,null,null,null,null,null);
        int prority = -1;
        if(cursor.moveToFirst()) {
            for(int i=0;i<cursor.getCount();i++) {
                cursor.move(i);
                if (cursor.getInt(0) > prority) {
                    prority = cursor.getInt(0);
                }
            }
        }
        ContentValues cValue = new ContentValues();
        cValue.put("account",info.getAccount());
        cValue.put("password",info.getPassword());
        cValue.put("priority",prority + 1);
        String whereClause = "account=?";
        String[] whereArgs={info.getAccount()};
        helper.getWritableDatabase().update("account",cValue,whereClause,whereArgs);
    }

    public static void deleteUser(UserInfo info) {
        Cursor cursor = helper.getReadableDatabase().query ("account",null,null,null,null,null,null);
        int prority = -1;
        if(cursor.moveToFirst()) {
            for(int i=0;i<cursor.getCount();i++) {
                cursor.move(i);
                if (cursor.getInt(0) > prority) {
                    prority = cursor.getInt(0);
                }
            }
        }
        if (prority >= 0) {
            String whereClause = "account=?";
            String[] whereArgs={info.getAccount()};
            helper.getWritableDatabase().delete("account",whereClause,whereArgs);
        }
    }
}
