package com.wifirecorder;

/**
 * Created by yuehan on 30/11/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocateMeActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officemap);  //使用有办公区背景图的layout
        //todo: 读入ap_mac.csv


    }

    public void onClickReadCSV(View view){
        InputStream inputStream = getResources().openRawResource(R.raw.ap_mac);
        CSVFile csvFile = new CSVFile(inputStream);
        Map apmacMap =csvFile.readasMap(); // hashmap - 类似python的字典：匹配AP的mac地址和坐标

        Iterator it = apmacMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Toast.makeText(this,
                    pair.getKey() + " = " + pair.getValue().toString(),
                    Toast.LENGTH_SHORT).show();
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    public void onClickShowDistance(View view){

//        //列出目录下所有的RSSI文件，如果报错查一下文件是否储存
//        String path = LocateMeActivity.this.getFilesDir()+File.separator+"WifiDatas";
//        Log.d("Files", "Path: " + path);
//        File directory1 = new File(path);
//        File[] files = directory1.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            Log.d("Files", "FileName:" + files[i].getName());
//        }
//
        //Read text from file
        StringBuilder text = new StringBuilder();
        //Get the text file
        try {
            File directory = new File(LocateMeActivity.this.getFilesDir()+File.separator+"WifiDatas");
            File file = new File(directory,"rssidata");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] splited = line.split("\\s+");
//                text.append(line);
//                text.append('\n');
                Toast.makeText(this,
                        splited[0]+splited[1],
                        Toast.LENGTH_SHORT).show();
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    //todo：rssidata转化为dataframe dataframe在java里是什么
    //todo: 读入ap_mac.csv，
    //todo:
    public void onClickShowCoordinates(View view){
        Toast.makeText(this,
                "the coordinates",
                Toast.LENGTH_SHORT).show();
    }
}