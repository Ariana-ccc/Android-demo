package com.example.daily;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper { //新建数据库
    private static final String DATABASE_NAME = "Database.db";//数据库名字//
    private static final int DATABASE_VERSION = 1;//数据库版本//
    public static final String TABLE_NAME = "daily";//日记名称//
    public static final String COLUMN_ID = "id";//日记id//
    public static final String COLUMN_TITLE = "title";//日记标题//
    public static final String COLUMN_CREATETIME = "createtime";//创建时间//
    public static final String COLUMN_CONTENT = "content";//日记内容//
    public static final String COLUMN_IMAGE = "image";//添加的图片//
    public static final String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_CREATETIME,
            COLUMN_CONTENT
    };

    public DBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " VARCHAR(32),"
                + COLUMN_CREATETIME + " VARCHAR(32), "
                + COLUMN_CONTENT + " VARCHAR(200),"
                + COLUMN_IMAGE + " blob) ";

        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}