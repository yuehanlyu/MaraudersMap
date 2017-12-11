package com.wifirecorder;

/**
 * Created by yuehan on 30/11/2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.math3.stat.regression.SimpleRegression;


public class LocateMeActivity extends Activity{

    /**
     * Called when the activity is first created.
     */
    private Button btnDrawCircles;
    private Button btnshowcoordinates;
    private ImageView imgv;
    private FrameLayout bg;
    private int f_x;
    private int f_y;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myframelayout);  //使用有办公区背景图的layout

        //取得界面资源
        btnDrawCircles = (Button)findViewById(R.id.btnDrawCircles);
        btnshowcoordinates = (Button)findViewById(R.id.btnshowcoordinates);
        bg = (FrameLayout)findViewById(R.id.bg);
        //设定按钮功能
        btnDrawCircles.setOnClickListener(btnListener);
        btnshowcoordinates.setOnClickListener(btnListener);
    }

    public Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.btnshowcoordinates:  //计算坐标
                    onClickShowCoordinates(v);
            }
        }
    };


    public void onClickDrawCircles(View view){

        FrameLayout.LayoutParams lytp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        lytp.leftMargin =f_y;
        lytp.topMargin=f_x;
        if(imgv == null) {
            imgv = new ImageView(LocateMeActivity.this);
            imgv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.red_circle));
        }
        if(bg.getChildCount() == 4) {
            bg.removeViewAt(3);
        }
        bg.addView(imgv, 3, lytp);
    }

    public void onClickShowCoordinates(View view){

        //读入的mac地址和对应的像素坐标
        InputStream inputStream = getResources().openRawResource(R.raw.ap_mac);
        CSVFile csvFile = new CSVFile(inputStream);
        Map apmacMap =csvFile.readasMap();

        double [] distance = new double[apmacMap.size()]; //按照apmac的大小建立；所以最后尾部可能有空缺
        double [][] xy = new double[apmacMap.size()][2];
        int receivedAP = 0;

        try {
            File directory = new File(LocateMeActivity.this.getFilesDir()+File.separator+"WifiDatas");
            File file = new File(directory,"rssidata");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) { //解析rssidata,
                String[] splited = line.split("\\s+");
                //splited[0]: MAC地址  splited[1]：距离
                if(apmacMap.containsKey(splited[0])){
                    distance[receivedAP]=Double.parseDouble(splited[1]);
                    xy[receivedAP] = (double[]) apmacMap.get(splited[0]); //object to double[]
                    receivedAP++;
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //receivedAP is also the size of p,q list
        double [][] pq = new double[receivedAP-1][2];

        for(int i=0;i<pq.length;i++){
            //p[i]对应xy[i+1]; distance[i+1]
            pq[i][0] = (xy[i+1][1]-xy[0][1])/(xy[0][0]-xy[i+1][0]);
            pq[i][1] = (Math.pow(distance[i+1],2)-Math.pow(distance[0],2)+Math.pow(xy[0][0],2)+Math.pow(xy[0][1],2)-Math.pow(xy[i+1][0],2)-Math.pow(xy[i+1][1],2))/(2*(xy[0][0]-xy[i+1][0]));
        }
        SimpleRegression reg=new SimpleRegression(true);  //least square
        reg.addData(pq);
        f_x =  (int)reg.getIntercept();
        f_y = (int)-reg.getSlope();

//        Toast.makeText(this,
//                String.valueOf((int)f_x)+", "+String.valueOf((int)f_y),
//                Toast.LENGTH_SHORT).show();

        FrameLayout.LayoutParams lytp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        lytp.leftMargin =f_y;
        lytp.topMargin=f_x;
        if(imgv == null) {
            imgv = new ImageView(LocateMeActivity.this);
            imgv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.red_circle));
        }
        if(bg.getChildCount() == 4) {
            bg.removeViewAt(3);
        }
        bg.addView(imgv, 3, lytp);


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
}