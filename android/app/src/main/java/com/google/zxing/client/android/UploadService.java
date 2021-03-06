package com.google.zxing.client.android;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;


/**
 * Created by DongJu on 15. 8. 25..
 */
public class UploadService extends Service{
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("slog", "uploadStart");
        super.onStart(intent, startId);
        Upload(loadData(getAllOutputData()));
    }

    private List<OutputData> getAllOutputData()
    {
        List<OutputData> result = new ArrayList<OutputData>();
        String selectQuery = "SELECT * FROM attend";
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.google.zxing.client.android/databases/data.db", null, 1);

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst())
        {
            do{
                OutputData output = new OutputData();
                output.setName(cursor.getString(0));
                output.setCode(cursor.getString(1));
                output.setQrTime(cursor.getString(2));
                output.setPhone(cursor.getString(3));
                output.setCurrentTime(cursor.getString(4));
                result.add(output);
            }while(cursor.moveToNext());
        }
        return result;
    }
    private JSONArray loadData(List<OutputData> output){
        JSONArray result = new JSONArray();
        for(OutputData data:output)
        {
            try {
                JSONObject out = new JSONObject();
                out.put("name", data.getName());
                out.put("code", data.getCode());
                out.put("qrtime", data.getCurrentTime());
                out.put("phone", data.getPhone());
                out.put("time",data.getCurrentTime());
                result.put(out.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private void Upload(JSONArray data) {
        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        URL url = null;
        try {
            url = new URL("https://203.246.112.206:8000");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException k)
        {
            k.printStackTrace();
        }

        conn.setConnectTimeout(10*1000); // 10 second
        conn.setReadTimeout(10*1000); // 10 second

        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Cache-Control","no-cache");
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestProperty("Accept","application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<data.length();i++)
        {
            JSONObject job = null;
            try {
                job = data.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                os.write(job.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            os.flush();
        }catch(IOException e){
            e.printStackTrace();
        }

        stopService(new Intent("com.google.zxing.client.android"));
    }
}

