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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LocateMeActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officemap);  //使用有办公区背景图的layout
        //todo: 计算坐标， 直接显示位置

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
                text.append(line);
                text.append('\n');
            }
            br.close();
            Toast.makeText(this,
                    text,
                    Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
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