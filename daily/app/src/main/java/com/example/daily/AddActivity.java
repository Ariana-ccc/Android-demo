package com.example.daily;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button btn_sure, btn_cancel;
    private EditText et_title, et_content;
    private SQLiteDatabase mDatabase;
    private int[] idlist;
    private int id;
    private String title, createtime, content, dateStr;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView picture;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mDatabase = new DBHelper(this).getWritableDatabase();
        initView();
        initEvent();
    }

    public void initView() {
        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    public void initEvent() {
        btn_sure.setOnClickListener(AddActivity.this);
        btn_cancel.setOnClickListener(AddActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sure) {
            Toast toast = Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG);
            toast.show();
            Date date = new Date();
            date.getTime();
            dateStr = sdf.format(date);
            title = et_title.getText().toString();
            content = et_content.getText().toString();
            insertData(title, dateStr, content);
            //跳转
            startActivity(new Intent(this, MainActivity.class));
            //跳转完以后关闭本界面
            finish();
        }
        if (view.getId() == R.id.btn_cancel) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    private void insertData(String title, String createtime, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("createtime", createtime);
        contentValues.put("content", content);
        mDatabase.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void queryTitle() {
        Cursor cursor1 = mDatabase.rawQuery("select count(2) from " + DBHelper.TABLE_NAME, null);
        cursor1.moveToFirst();
        long count = cursor1.getLong(0);
        int num = (int) count;
        idlist = new int[num];
        cursor1.close();
        Cursor cursor;
        cursor = mDatabase.query(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMNS, null, null, null, null, null);
        int i = 0;
        while (cursor != null && cursor.moveToNext()) {
            idlist[i] = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            i += 1;
        }
        id=idlist[num-1];
        cursor.close();
    }
}
