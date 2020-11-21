package com.example.lwq.tianqi;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity implements View.OnClickListener{
    TextView Textshow;
    String researchcitycode;
    Button Concern,refresh;
    String CityshowString;

    private String city;
    private App.forecast forecast0,forecast1,forecast2,forecast3,forecast4;
    private String cityId;
    private String parent;
    private String updateTime;
    private String time;
    private String date;
    private String message;
    private String status;
    private String shidu;
    private String pm25;
    private String pm10;
    private String quality;
    private String wendu;
    private String ganmao;
    private List<App.forecast> forecasts;
    private String ymd;
    private String date_1;
    private String week;
    private String sunrise;
    private String high;
    private String low;
    private String sunset;
    private String aqi;
    private String fx;
    private String fl;
    private String type;
    private String notice;
    //今天和未来四天的天气情况
    private String date0,date1,date2,date3,date4;
    private String ymd0,ymd1,ymd2,ymd3,ymd4;
    private String week0,week1,week2,week3,week4;
    private String sunrise0,sunrise1,sunrise2,sunrise3,sunrise4;
    private String high0,high1,high2,high3,high4;
    private String low0,low1,low2,low3,low4;
    private String sunset0,sunset1,sunset2,sunset3,sunset4;
    private String aqi0,aqi1,aqi2,aqi3,aqi4;
    private String fx0,fx1,fx2,fx3,fx4;
    private String fl0,fl1,fl2,fl3,fl4;
    private String type0,type1,type2,type3,type4;
    private String notice0,notice1,notice2,notice3,notice4;
    int databaseid;
    String databasedata;
    int sign = 1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Textshow = (TextView)findViewById(R.id.TextView);
        Concern = (Button)findViewById(R.id.concern1);
        Concern.setOnClickListener(this);
        refresh = (Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        researchcitycode = extras.getString("trancitycode");


        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Weather.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Weather",new String[]{"id","data"},"id=?",new String[]{researchcitycode+""},null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                databaseid = cursor.getInt(cursor.getColumnIndex("id"));
                databasedata = cursor.getString(cursor.getColumnIndex("data"));
            } while (cursor.moveToNext());
        }
        int tranformat = 0;
        tranformat = Integer.parseInt(researchcitycode);

        if(databaseid ==  tranformat ){
            sign = 1;
            showResponse(databasedata);
        }else {
            sign = 0;
            sendRequestWithOkHttp();
        }

    }


    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://t.weather.sojson.com/api/weather/city/"+researchcitycode)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("data is", responseData);
                    showResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }).start();
    }




    private void parseJSONWithFastJSON(String jsonData){
        if(jsonData.length()<100){
            Log.d("M","城市ID不存在");
            Toast.makeText(this,"城市ID不存在，请重新输入",Toast.LENGTH_LONG).show();
            Weather.this.setResult(RESULT_OK,getIntent());
            Weather.this.finish();
        }
        else {
            App app = JSON.parseObject(jsonData, App.class);
            time = app.getTime();
            message = app.getMessage();
            status = app.getStatus();
            date = app.getDate();


            App.CityInfo cityInfo = app.getCityInfo();
            city = cityInfo.getCity();
            cityId = cityInfo.getCityId();
            parent = cityInfo.getParent();
            updateTime = cityInfo.getUpdateTime();


            App.data data = app.getData();
            shidu = data.getShidu();
            pm10 = data.getPm10();
            pm25 = data.getPm25();
            quality = data.getQuality();
            ganmao = data.getGanmao();
            wendu = data.getWendu();
            forecasts = data.getForecast();
//今天和未来四天的天气情况
            forecast0 = forecasts.get(0);
            date0 = forecast0.getDate();
            high0 = forecast0.getHigh();
            low0 = forecast0.getLow();
            week0 = forecast0.getWeek();
            sunrise0 = forecast0.getSunrise();
            ymd0 = forecast0.getYmd();
            sunset0 = forecast0.getSunset();
            aqi0 = forecast0.getAqi();
            fx0 = forecast0.getFx();
            fl0 = forecast0.getFl();
            notice0 = forecast0.getNotice();
            type0 = forecast0.getType();

            forecast1 = forecasts.get(1);
            date1 = forecast1.getDate();
            high1 = forecast1.getHigh();
            low1 = forecast1.getLow();
            week1 = forecast1.getWeek();
            sunrise1 = forecast1.getSunrise();
            ymd1 = forecast1.getYmd();
            sunset1 = forecast1.getSunset();
            aqi1 = forecast1.getAqi();
            fx1 = forecast1.getFx();
            fl1 = forecast1.getFl();
            notice1 = forecast1.getNotice();
            type1 = forecast1.getType();


            forecast2 = forecasts.get(2);
            date2 = forecast2.getDate();
            high2 = forecast2.getHigh();
            low2 = forecast2.getLow();
            week2 = forecast2.getWeek();
            sunrise2 = forecast2.getSunrise();
            ymd2 = forecast2.getYmd();
            sunset2 = forecast2.getSunset();
            aqi2 = forecast2.getAqi();
            fx2 = forecast2.getFx();
            fl2 = forecast2.getFl();
            notice2 = forecast2.getNotice();
            type2 = forecast2.getType();

            forecast3 = forecasts.get(3);
            date3 = forecast3.getDate();
            high3 = forecast3.getHigh();
            low3 = forecast3.getLow();
            week3 = forecast3.getWeek();
            sunrise3 = forecast3.getSunrise();
            ymd3 = forecast3.getYmd();
            sunset3 = forecast3.getSunset();
            aqi3 = forecast3.getAqi();
            fx3 = forecast3.getFx();
            fl3 = forecast3.getFl();
            notice3 = forecast3.getNotice();
            type3 = forecast3.getType();


            forecast4 = forecasts.get(4);
            date4 = forecast4.getDate();
            high4 = forecast4.getHigh();
            low4 = forecast4.getLow();
            week4 = forecast4.getWeek();
            sunrise4 = forecast4.getSunrise();
            ymd4 = forecast4.getYmd();
            sunset4 = forecast4.getSunset();
            aqi4 = forecast4.getAqi();
            fx4 = forecast4.getFx();
            fl4 = forecast4.getFl();
            notice4 = forecast4.getNotice();
            type4 = forecast4.getType();


            App.data.yesterday yesterday = data.getYesterday();
            ymd = yesterday.getYmd();
            week = yesterday.getWeek();
            sunrise = yesterday.getSunrise();
            high = yesterday.getHigh();
            low = yesterday.getLow();
            sunset = yesterday.getSunset();
            aqi = yesterday.getAqi();
            fl = yesterday.getFl();
            fx = yesterday.getFx();
            notice = yesterday.getNotice();
            type = yesterday.getType();
            date_1 = yesterday.getDate();

            if (sign == 0) {
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", researchcitycode);
                values.put("data", jsonData);
                db.insert("Weather", null, values);
                Log.d("MainActivity", "数据库写入成功");
            } else if (sign == 1) {
                Log.d("数据库写入失败：", "数据已存在");

            } else {
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", researchcitycode);
                values.put("data", jsonData);
                db.update("Weather", values, "id=?", new String[]{researchcitycode + ""});
                Log.d("MainActivity", "数据库更新成功");

            }

        }
    }



    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parseJSONWithFastJSON(response);
                String CityshowString;
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd0+"\n"+week0+"\n"+"日出时间:"+sunrise0+"\n"+"最高温度:"+high0+"\n"+"最低温度:"+low0+"\n"+"日落时间："+sunset0+"\n"+"空气指数："+aqi0+"\n"+"风力："+fl0+"\n"+"风向："+fx0+"\n"+"提示:"+notice0+"\n"+"天气:"+type0;
                Textshow.setText(CityshowString);
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.concern1:
                /*
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("citycode",researchcitycode);
                editor.apply();
                Toast.makeText(this, "关注成功！", Toast.LENGTH_LONG).show();
                */
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Concern.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("city_code", researchcitycode);
                values.put("city_name", city);
                db.insert("Concern", null, values);
                Toast.makeText(this, "关注成功！", Toast.LENGTH_LONG).show();
                break;
            case  R.id.refresh:
                sign = 3;
                sendRequestWithOkHttp();
                Log.d("MainActivity","数据库刷新成功");
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.yesterday:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd+"\n"+week+"\n"+"日出时间:"+sunrise+"\n"+"最高温度:"+high+"\n"+"最低温度:"+low+"\n"+"日落时间："+sunset+"\n"+"空气指数："+aqi+"\n"+"风力："+fl+"\n"+"风向："+fx+"\n"+"提示:"+notice+"\n"+"天气:"+type;

                Textshow.setText(CityshowString);
                break;
            case  R.id.today:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd0+"\n"+week0+"\n"+"日出时间:"+sunrise0+"\n"+"最高温度:"+high0+"\n"+"最低温度:"+low0+"\n"+"日落时间："+sunset0+"\n"+"空气指数："+aqi0+"\n"+"风力："+fl0+"\n"+"风向："+fx0+"\n"+"提示:"+notice0+"\n"+"天气:"+type0;

                Textshow.setText(CityshowString);
                break;
            case R.id.forecast1:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd1+"\n"+week1+"\n"+"日出时间:"+sunrise1+"\n"+"最高温度:"+high1+"\n"+"最低温度:"+low1+"\n"+"日落时间："+sunset1+"\n"+"空气指数："+aqi1+"\n"+"风力："+fl1+"\n"+"风向："+fx1+"\n"+"提示:"+notice1+"\n"+"天气:"+type1;
                Textshow.setText(CityshowString);
                break;
            case  R.id.forecast2:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd2+"\n"+week2+"\n"+"日出时间:"+sunrise2+"\n"+"最高温度:"+high2+"\n"+"最低温度:"+low2+"\n"+"日落时间："+sunset2+"\n"+"空气指数："+aqi2+"\n"+"风力："+fl2+"\n"+"风向："+fx2+"\n"+"提示:"+notice2+"\n"+"天气:"+type2;
                Textshow.setText(CityshowString);
                break;
            case  R.id.forecast3:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd3+"\n"+week3+"\n"+"日出时间:"+sunrise3+"\n"+"最高温度:"+high3+"\n"+"最低温度:"+low3+"\n"+"日落时间："+sunset3+"\n"+"空气指数："+aqi3+"\n"+"风力："+fl3+"\n"+"风向："+fx3+"\n"+"提示:"+notice3+"\n"+"天气:"+type3;
                Textshow.setText(CityshowString);
                break;
            case R.id.forecast4:
                CityshowString = "数据更新时间:"+time+"\n"+"当前状态："+message+"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+updateTime;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ganmao+"\n"+"当前温度"+wendu+"\n";
                CityshowString = CityshowString+"当前日期:"+ymd4+"\n"+week4+"\n"+"日出时间:"+sunrise4+"\n"+"最高温度:"+high4+"\n"+"最低温度:"+low4+"\n"+"日落时间："+sunset4+"\n"+"空气指数："+aqi4+"\n"+"风力："+fl4+"\n"+"风向："+fx4+"\n"+"提示:"+notice4+"\n"+"天气:"+type4;
                Textshow.setText(CityshowString);
                break;
            case R.id.cancel_concern:
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern.db",null,1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Concern","city_code=?",new String[]{researchcitycode+""});
                Toast.makeText(this,"取消关注成功",Toast.LENGTH_LONG).show();
                Weather.this.setResult(RESULT_OK,getIntent());
                Weather.this.finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
