package com.wifirecorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.*;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class WifiRecorderActivity extends Activity 
{
	//RSSI to Distance parameters
	private static double coef = 23.5;
	private static double A0 = -35;

	private Button btnRefresh;
//	private Button btnRecord;
	private Button btnExit;
	private Button btnLocate;
	private TextView txtTime;
	private Calendar time;
	private ListView listWifiResult;
	private List<ScanResult> WifiList;
	private WifiManager mWifiMngr;
	private String[] WifiInfo;
	private String curTime;
	private Vector<String> WifiSelectedItem = new Vector<String>();
	String msg = "Android : ";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//取得界面资源
		btnRefresh = (Button)findViewById(R.id.btnRefresh);
//		btnRecord = (Button)findViewById(R.id.btnRecord);
		btnExit = (Button)findViewById(R.id.btnExit);
		btnLocate = (Button)findViewById(R.id.btnLocate);
		txtTime = (TextView)findViewById(R.id.txtTime);
		listWifiResult = (ListView)findViewById(R.id.listResult);
		//设定wifi装置
		mWifiMngr = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);//
		//启用Wifi裝置
		OpenWifi();
		//取得Wifi列表
		GetWifiList();
		//设定按钮功能
		btnRefresh.setOnClickListener(btnListener);
//		btnRecord.setOnClickListener(btnListener);
		btnExit.setOnClickListener(btnListener);
		btnLocate.setOnClickListener(btnListener);
		//设定ListView选取事件
		listWifiResult.setOnItemLongClickListener(listLongListener);
	}

	private Button.OnClickListener btnListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btnRefresh:  //刷新
					//取得Wifi列表
					GetWifiList();
					break;
//				case R.id.btnRecord:
//					//RecordCheckWindow();   //手动输入文件名
//					AutoSaveData();   //自动保存为rssidata.txt
//					break;
				case R.id.btnExit:   //退出
					CloseWifi();
					finish();
					break;
                case R.id.btnLocate:   //定位
                    Intent locateIntent = new Intent(v.getContext(), LocateMeActivity.class);
                    startActivityForResult(locateIntent, 0);
			}
		}
	};

	private ListView.OnItemLongClickListener listLongListener = new ListView.OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			// TODO Auto-generated method stub
			ShowWifiInfo(position);
			return false;
		}
	};

	private void AutoSaveData(){
		DataFormer("rssidata");
	}

//	private void RecordCheckWindow()
//	{
//		final EditText edtFileName = new EditText(WifiRecorderActivity.this);
//		new AlertDialog.Builder(WifiRecorderActivity.this)
//		.setTitle("储存到文件")
//		.setIcon(R.drawable.qiandaijun)
//		.setMessage("请命名文件:（不加.txt）")
//		.setView(edtFileName)
//		.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//
//			}
//
//		})
//		.setPositiveButton("确认",new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				//将选取的list记录并生成档案
//				DataFormer(edtFileName.getText().toString());
//			}
//		}).show();
//	}

	private void ShowWifiInfo(int index)   //长按的效果
	{
		new AlertDialog.Builder(WifiRecorderActivity.this)
		.setTitle("Details")
		.setIcon(R.drawable.ic_launcher)
		.setMessage(WifiInfo[index])
		.setNeutralButton("Yes", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		})
		.show();
	}

	private void DataFormer(String FileName)
	{

		// 所有的WifiInfo加入WifiSelectedItem
		//todo: 限定强度最大的几个值
		Vector<String> WifiSelectedItem = new Vector<String>();
		for(int i=0;i<WifiInfo.length;i++)
			WifiSelectedItem.add(WifiInfo[i]);
		String WifiDatas = curTime+"\r\n";
		//将Wifi信息存进WifDatas
		File directory = new File(WifiRecorderActivity.this.getFilesDir()+File.separator+"WifiDatas");
		for(int i=0;i<WifiSelectedItem.size();i++)
			WifiDatas += WifiSelectedItem.elementAt(i).toString()+"\r\n";
		//建立本机储存目录
		if(!directory.exists())//如果没此文件夹则建立
			directory.mkdir();
		try {
			File gpxfile = new File(directory, FileName);
			FileWriter fw = new FileWriter(gpxfile);
			//FileWriter fw = new FileWriter(WifiRecorderActivity.this.getFilesDir()+"/WifiData/"+FileName+".txt",false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(WifiDatas);
//			Toast.makeText(WifiRecorderActivity.this
//							,FileName+" is successfully saved",Toast.LENGTH_LONG).show();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(WifiRecorderActivity.this
							,"Failed!",Toast.LENGTH_LONG).show();
		}
	}

	// Open WiFi
	private void OpenWifi()
	{
		//当Wifi关闭时将它启动
		if(!mWifiMngr.isWifiEnabled()){
			mWifiMngr.setWifiEnabled(true);
			Toast.makeText(WifiRecorderActivity.this,"WiFi is launching. Please wait..."
						   ,Toast.LENGTH_LONG).show();
			Toast.makeText(WifiRecorderActivity.this,"Press Refresh"
					,Toast.LENGTH_LONG).show();
		}
	}
	// Shut down Wifi
	private void CloseWifi()
	{
		//当Wifi开启时将它关闭
		if(mWifiMngr.isWifiEnabled())
			mWifiMngr.setWifiEnabled(false);
	}

	public double getRealLen(double rssi) {
		double f=(-rssi+A0)/coef;
		return Math.pow(10,f);
	}

	public double getPicLen(double rssi) {
        double f=(-rssi+A0)/coef;
        double dist =  Math.pow(10,f);
        return dist*30;
    }

	private void GetWifiList()
	{
		int nAP=3;

		while(nAP<5){
			//开始扫描Wifi热点
			mWifiMngr.startScan();
			//得到扫描结果
			WifiList = mWifiMngr.getScanResults();

			//将WifiList根据信号强度由强到弱排序
			Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
				@Override
				public int compare(ScanResult lhs, ScanResult rhs) {
					return (lhs.level >rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
				}
			};
			Collections.sort(WifiList, comparator);

			//移除rssi小于-65的信号
			//移除所有2.4GHz
			for (int i =WifiList.size()-1;i>=0;i--){
				if(WifiList.get(i).level<-65||WifiList.get(i).frequency<5000)
					WifiList.remove(i);
//			else
//				break;
			}
			nAP=WifiList.size();
			Toast.makeText(WifiRecorderActivity.this
					,"正在扫描...接收到"+String.valueOf(nAP)+"个AP的信号",Toast.LENGTH_LONG).show();
		}
		Toast.makeText(WifiRecorderActivity.this
				,"可以定位",Toast.LENGTH_LONG).show();


        //设定wifi打印阵列
		String[] Wifis = new String[WifiList.size()];
		//取得目前时间
		time = Calendar.getInstance();
		curTime = (time.get(Calendar.YEAR))+"/"  
				+(time.get(Calendar.MONTH)+1)+"/"  
				+(time.get(Calendar.DAY_OF_MONTH))+"  "	
				+time.get(Calendar.HOUR_OF_DAY)+":"  
				+time.get(Calendar.MINUTE)+":"	
				+time.get(Calendar.SECOND);
		txtTime.setText("Time:"+curTime);
		String dist;
		//将Wifi信息放入main.xml打印阵列中     Wifis是一个string矩阵
		for(int i=0;i<WifiList.size();i++) {
            //todo: 计算距离
			dist = String.valueOf(Math.floor(getRealLen(WifiList.get(i).level) * 100) / 100);
			Wifis[i] = WifiList.get(i).SSID  //SSID
					+ "\r\r" + WifiList.get(i).BSSID + "\r\r"//MAC地址
					+ WifiList.get(i).level + "dBm" + "\r\r"//信号强弱、
					+ dist+"m\r\r"+ //距离
					+ WifiList.get(i).frequency; //信道
        }
        //将WifiSelectedItem中暂存的资料清空
		WifiSelectedItem.removeAllElements();
		//设定地图页面的Wifi清单
		SetWifiList(Wifis);
		//直接储存
		DataFormer("rssidata");
	}

	private void SetWifiList(String[] Wifis)
	{
	    //建立ArrayAdapter
		 ArrayAdapter<String> adapterWifis = new ArrayAdapter<String>(WifiRecorderActivity.this
						,android.R.layout.simple_list_item_checked,Wifis);
        //设定ListView为多选
		listWifiResult.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		//设定ListView来源
		listWifiResult.setAdapter(adapterWifis);
        //初始化WifiInfo阵列
		WifiInfo = null;
        //设定Wifi信息放入阵列中(记录用)
		WifiInfo = new String[WifiList.size()];
		String dist;
		for(int i=0;i<WifiList.size();i++) {
			dist = String.valueOf(Math.floor(getPicLen(WifiList.get(i).level) * 100) / 100);
			WifiInfo[i] = WifiList.get(i).BSSID + "  " + dist;
		}
	}
	

}
