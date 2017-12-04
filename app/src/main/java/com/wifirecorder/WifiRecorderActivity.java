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
	private Button btnRefresh;
	private Button btnRecord;
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
		btnRecord = (Button)findViewById(R.id.btnRecord);
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
		btnRecord.setOnClickListener(btnListener);
		btnExit.setOnClickListener(btnListener);
		btnLocate.setOnClickListener(btnListener);
		//设定ListView选取事件
		listWifiResult.setOnItemClickListener(listListener);
		listWifiResult.setOnItemLongClickListener(listLongListener);
	}


	@Override
	protected void onStart() {
		super.onStart();
		Log.d(msg, "The onStart() event");
	}

	/** Called when the activity has become visible. */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(msg, "The onResume() event");
	}

	/** Called when another activity is taking focus. */
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(msg, "The onPause() event");
	}

	/** Called when the activity is no longer visible. */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(msg, "The onStop() event");
	}

	/** Called just before the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(msg, "The onDestroy() event");
	}

	//测试：启动服务
	public void startService(View view){
		startService(new Intent(getBaseContext(), MyService.class));
	}

	public void stopService(View view){
		stopService(new Intent(getBaseContext(), MyService.class));
	}

	// 测试：数据库
	public void onClickAddName(View view) {
		// Add a new student record
		ContentValues values = new ContentValues();
		values.put(StudentsProvider.NAME,
				((EditText)findViewById(R.id.editText2)).getText().toString());

		values.put(StudentsProvider.GRADE,
				((EditText)findViewById(R.id.editText3)).getText().toString());

		Uri uri = getContentResolver().insert(
				StudentsProvider.CONTENT_URI, values);

		Toast.makeText(getBaseContext(),
				uri.toString(), Toast.LENGTH_LONG).show();
	}
	public void onClickRetrieveStudents(View view) {
		// Retrieve student records
		String URL = "content://com.wifirecorder.StudentsProvider";

		Uri students = Uri.parse(URL);
		Cursor c = managedQuery(students, null, null, null, "name");

		if (c.moveToFirst()) {
			do{
				Toast.makeText(this,
						c.getString(c.getColumnIndex(StudentsProvider._ID)) +
								", " +  c.getString(c.getColumnIndex( StudentsProvider.NAME)) +
								", " + c.getString(c.getColumnIndex( StudentsProvider.GRADE)),
						Toast.LENGTH_SHORT).show();
			} while (c.moveToNext());
		}
	}
	private Button.OnClickListener btnListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btnRefresh:
					//取得Wifi列表
					GetWifiList();
					break;
				case R.id.btnRecord:
					RecordCheckWindow();
					break;
				case R.id.btnExit:
					CloseWifi();
					finish();
					break;
                case R.id.btnLocate:
                    Intent myIntent = new Intent(v.getContext(), LocateMeActivity.class);
                    startActivityForResult(myIntent, 0);
			}
		}
	};


	private ListView.OnItemClickListener listListener = new ListView.OnItemClickListener()
	{		
		int ItemSelectedInVector;
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			
			//如果被勾选就加入Vector
			if(listWifiResult.isItemChecked(position))
				WifiSelectedItem.add(WifiInfo[position]);
			//如果被取消勾选就从Vector移除
			else
			{
				//取得目前勾选项目在Vector中的位置
				for(int i=0;i<WifiSelectedItem.size();i++)	
					if(WifiSelectedItem.get(i).equals(WifiInfo[position]))
						ItemSelectedInVector = i; 
				WifiSelectedItem.remove(ItemSelectedInVector);
			}
		}
		
	};
	private ListView.OnItemLongClickListener listLongListener = new ListView.OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			// TODO Auto-generated method stub
			WifiInfo(position);
			return false;
		}
	};
	private void RecordCheckWindow()
	{
		final EditText edtFileName = new EditText(WifiRecorderActivity.this);
		new AlertDialog.Builder(WifiRecorderActivity.this)
		.setTitle("储存到文件")
		.setIcon(R.drawable.qiandaijun)
		.setMessage("请命名文件:")
		.setView(edtFileName)
		.setNegativeButton("取消", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		})
		.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//将选取的list记录并生成档案
				DataFormer(edtFileName.getText().toString());
			}
		}).show();
	}
    // TODO: 生成地图界面。第一步先将建筑布置图作为背景图
	private void LocationMap()
    {

    }
	private void WifiInfo(int index)
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
			Toast.makeText(WifiRecorderActivity.this
							,FileName+".txt is successfully saved",Toast.LENGTH_LONG).show();
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

	public double getPicLen(double rssi) {
        double f=(-rssi-50)/20.0;
        return Math.pow(10,f);
    }

	private void GetWifiList()
	{
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
		//将Wifi信息放入打印阵列中
		for(int i=0;i<WifiList.size();i++) {
            //todo: 计算距离
            //dist = String.valueOf(getPicLen(WifiList.get(i).level));
            dist = String.valueOf(Math.floor(getPicLen(WifiList.get(i).level) * 100) / 100);
            Wifis[i] = WifiList.get(i).SSID  //SSID
                    + "\r\r" + WifiList.get(i).BSSID + "\r\r"//MAC地址
                    + WifiList.get(i).level + "dBm" + "\r\r"//信号强弱、
                    + dist+"m\r\r"+
            + WifiList.get(i).frequency; //信道
        }
        //将WifiSelectedItem中暂存的资料清空
		WifiSelectedItem.removeAllElements();
		//设定Wifi清单
		SetWifiList(Wifis);
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
		
		for(int i=0;i<WifiList.size();i++)
			WifiInfo[i] = "SSID:"+WifiList.get(i).SSID +"\r\n"      //SSID
						+"BSSID:"+WifiList.get(i).BSSID+"\r\n"   //BSSID
						+"RSSI："+WifiList.get(i).level+"dBm"+"\r\n" //信号强弱
						+"Frequency:"+WifiList.get(i).frequency+"MHz"+"\r\n"; //信道频率
	}
	

}
