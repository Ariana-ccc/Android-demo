package com.example.daily;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button btn_back,btn_delete,btn_edit,take_photo,choose_from_album;
    private TextView tv_createtime;
    private EditText tv_content,tv_title;
    private SQLiteDatabase mDatabase;
    private int []idlist;
    private int id;
    private String title, createtime, content, dateStr;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView picture;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);//与activity_detail关联//
        Button takePhoto=(Button)findViewById(R.id.take_photo);//传入拍照按钮
        Button chooseFromAlbum=(Button) findViewById(R.id.choose_from_album);//传入相册按钮
        Button saveData=(Button)findViewById(R.id.save_date);//传入存储按钮
        Button restoreData=(Button)findViewById(R.id.restore_date);传入restore按钮
                picture=(ImageView) findViewById(R.id.picture);//用于将图片显示出来
        mDatabase = new DBHelper(this).getWritableDatabase();//存入数据库
        queryTitle();
        initView();
        initEvent();
    }


    @SuppressLint("WrongViewCast")
    public void initView(){
        tv_title = (EditText) findViewById(R.id.tv_title);
        tv_createtime = (TextView) findViewById(R.id.tv_createtime);
        tv_content = (EditText) findViewById(R.id.tv_content);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(DetailActivity.this);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        take_photo= (Button) findViewById(R.id.take_photo);

    }//用于从界面中得到用户输入的信息并设置变量存储


    public void initEvent(){
        Bundle b=getIntent().getExtras();
        //获取Bundle的信息，Bundle用于在活动间传输大量的数据
        int pos=b.getInt("id");
        id=idlist[pos];
        System.out.println("id:"+id);
        Cursor cursor= mDatabase.query(DBHelper.TABLE_NAME,DBHelper.TABLE_COLUMNS,"id=?",new String[]{id+""},null,null,null); //将日记的id存入数据库
        while (cursor != null && cursor.moveToNext()) {
            tv_title.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE)));
            tv_createtime.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CREATETIME)));
            tv_content.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT)));
        }
        cursor.close();
        btn_delete.setOnClickListener(DetailActivity.this);
        btn_edit.setOnClickListener(DetailActivity.this);
    }//当打开某一日记时，从数据库中调入数据，并将系统时间，日记标题，日记内容显示出来，然后关闭cursor//


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (view.getId() == R.id.btn_delete) {
            deleteData(id);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (view.getId() == R.id.btn_edit) {
            Date date = new Date();
            date.getTime();
            dateStr = sdf.format(date);
            dateStr = tv_createtime.getText().toString();
            title = tv_title.getText().toString();
            content = tv_content.getText().toString();
            Update(id);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
//创建File对象，用于存储拍照后的图片
        if (view.getId() == R.id.take_photo) {
            File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();//处理异常
            }
            if (Build.VERSION.SDK_INT >= 24) {//判断SDK的版本是否大于24，如果大于24就用getUriFile()//
                imageUri = FileProvider.getUriForFile(DetailActivity.this, "com.example.daily.fileprovider", outputImage);

            } else {
                imageUri = Uri.fromFile(outputImage);
            }



            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//跳到相机程序
            startActivityForResult(intent, TAKE_PHOTO);

        }
        if (view.getId() == R.id.choose_from_album) {
            if(ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                    PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(DetailActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else{
                openAlbum();//打开相册
            }
        }
        if (view.getId() == R.id.save_date) {
            SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("name","ice");//向对象添加数据
            editor.putInt("age",21);
            editor.apply();//将数据提交，完成数据存储
        }
        if (view.getId() == R.id.restore_date) {
            SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
            String name=pref.getString("name","");//读取字符串，第一个参数是键，第二个参数是默认值，当找不到对应值返回默认值
            int age=pref.getInt("age",0);
            Log.d("DetailActivity","name is:"+name);
            Log.d("DetailActivity","age is:"+age);//添加日志，便于debug

        }
    }

    public void  openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    public void onRequestPermissionsReault(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permisssion",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                       /* mDatabase = new DBHelper(this).getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        mDatabase.update(DBHelper.TABLE_NAME,contentValues,"id = ?",new String[]{id+""});
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                        contentValues.put("image", outputStream.toByteArray());
                        picture.setImageBitmap(bitmap);

                        */
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                   if(Build.VERSION.SDK_INT>=19){
                       //4.4及以上系统使用这个方法处理图片
                       handleImageOnKtKat(data);
                   }else{
                       //4.4以下系统使用这个方法处理图片
                       handleImageBeforeKtKat(data);
                   }
                }
                break;
            default:
                break;
        }
    }
public void handleImageOnKtKat(Intent date){
        Uri uri=date.getData();
        String imagePath=null;
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数据格式的id
                String selection=MediaStore.Images.Media._ID + "=" + id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);

            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath=getImagePath(uri,null);
    }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();

    }
        displayImage(imagePath);//根据图片路径显示图片
}

private void handleImageBeforeKtKat(Intent data){
    Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    
}
public String getImagePath(Uri uri,String selection){
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
}
private void displayImage(String imagePath){
        if(imagePath!=null) {
            //Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            //picture.setImageBitmap(bitmap);
            mDatabase = new DBHelper(this).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            contentValues.put("image", outputStream.toByteArray());
            mDatabase.update(DBHelper.TABLE_NAME,contentValues,"id = ?",new String[]{id+""});

        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
}
    //用光标遍历数据库
    private void queryTitle() {
        Cursor cursor1= mDatabase.rawQuery("select count(2) from "+DBHelper.TABLE_NAME,null);//创建光标对象遍历数据库中的NAME//
        cursor1.moveToFirst();//移光标到第一行
        long count = cursor1.getLong(0);
        int num=(int) count;
        idlist=new int[num];//Id存入数组
        cursor1.close();
        Cursor cursor;
        cursor = mDatabase.query(DBHelper.TABLE_NAME,DBHelper.TABLE_COLUMNS,null,null,null,null,null);
        int i=0;
        while (cursor != null && cursor.moveToNext()) {//当光标不为空，移动到下一行
            idlist[i]=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            i+=1;
        }
        cursor.close();
    }

    private void deleteData(int id) {
        mDatabase.delete(DBHelper.TABLE_NAME,"id = ?",new String[]{id+""});
    }
    //对数据进行更新
    private void Update(int id) {
        mDatabase = new DBHelper(this).getWritableDatabase();//从数据库获取数据
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);
        mDatabase.update(DBHelper.TABLE_NAME,contentValues,"id = ?",new String[]{id+""});//重建更新内容


    }

}
