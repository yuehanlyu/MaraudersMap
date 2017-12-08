package com.wifirecorder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuehan on 08/12/2017.
 */

public class DrawtestActivity extends Activity{

    private FrameLayout bg;
    private Button btnDrawPoint;
    private ImageView imgv;
    private int f_x;
    private int f_y;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myframelayout);  //使用有办公区背景图的layout

        //取得界面资源
        btnDrawPoint = (Button)findViewById(R.id.btnDrawPoint);
        bg = (FrameLayout)findViewById(R.id.bg);
        //设定按钮功能
        btnDrawPoint.setOnClickListener(btnListener);
    }

    public Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.btnDrawPoint:
                    onClickDrawPoint(v);
            }
        }
    };

    public void onClickDrawPoint(View view){
        //customView = (CustomView)findViewById(R.id.custom_view);

        //暂时给f_x, f_y赋值测试画圈；实际应该用底下onClickShowCoordinates的结果
        f_x = 200;
        f_y = 500;
        String str1 = "test1";

        //todo: setLayoutParams()
        FrameLayout.LayoutParams lytp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        lytp.leftMargin =f_x;
        lytp.topMargin=f_y;
        if(imgv == null) {
            imgv = new ImageView(DrawtestActivity.this);
            imgv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.red_circle));
        }
        if(bg.getChildCount() == 3) {
            bg.removeViewAt(2);
        }
        bg.addView(imgv, 2, lytp);
//        lytp .gravity = Gravity.CENTER;
//        btn.setLayoutParams(lytp);

    }

}
