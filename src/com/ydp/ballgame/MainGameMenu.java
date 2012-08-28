package com.ydp.ballgame;

import java.util.Timer;
import java.util.TimerTask;
import com.ydp.ballgame.ImageButton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View.OnTouchListener;

public class MainGameMenu extends Activity {
    /** Called when the activity is first created. */
	private int mScreenWidth;
	private int mScreenHeight;
	myView MenuView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	// 全屏显示窗口
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    		WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	//设置背景
        getWindow().setBackgroundDrawable(null);
        //得到屏幕大宽高
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        MenuView = new myView(this);
        setContentView(MenuView);
    }
    //处理屏幕的触摸事件
    public boolean onTouchEvent(MotionEvent event) {
    	int x = (int)event.getX();
		int y = (int)event.getY(); 
		switch (event.getAction()) {
		// 触摸屏幕时刻
		case MotionEvent.ACTION_DOWN:
			MenuView.UpdateTouchEvent(x, y, MotionEvent.ACTION_DOWN);			
		    break;
		// 触摸并移动时刻
		case MotionEvent.ACTION_MOVE:
		    break;
		// 终止触摸时刻
		case MotionEvent.ACTION_UP:
			MenuView.UpdateTouchEvent(x, y, MotionEvent.ACTION_UP);
		    break;
		}
		return false;
    }
    public class myView extends View{
    	private Paint mPaint;
    	private Bitmap mBall;
    	private Bitmap backbg;
    	private Bitmap sample;
    	private Bitmap menutitle;
    	ImageButton ksyxMenuButton;
    	ImageButton phbMenuButton;
    	ImageButton yxsmMenuButton;
    	Context mContext;
    	Canvas mCanvas;
		public myView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			mContext = context;
			mPaint = new Paint();
			//把图片资源读取进来
			menutitle = BitmapFactory.decodeResource(getResources(), R.drawable.menutitle);
			mBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball1);
			sample = BitmapFactory.decodeResource(getResources(), R.drawable.yxsm1);
			//读取背景图片并缩小到屏幕实际的大小
			backbg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
			backbg = Bitmap.createScaledBitmap(backbg, mScreenWidth, mScreenHeight, true);
			//可点击的图片类，读取图片资源
			ksyxMenuButton = new ImageButton(context,R.drawable.ksyx1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 7 / 15);
			phbMenuButton = new ImageButton(context,R.drawable.phb1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 9 / 15);
			yxsmMenuButton = new ImageButton(context,R.drawable.yxsm1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 11 / 15);
			
		}
		//view就是靠这个函数进行画东西上去的
		public void onDraw(Canvas canvas){
			mCanvas = canvas;
			
			mCanvas.drawBitmap(backbg, 0, 0, mPaint);
			mCanvas.drawBitmap(mBall, mScreenWidth / 2 - mBall.getWidth() / 2, mScreenHeight / 6, mPaint);
			mCanvas.drawBitmap(menutitle, mScreenWidth / 2 - menutitle.getWidth() / 2, mScreenHeight * 3 / 10, mPaint);
			//调用可点击的图片类里的方法画出来
			ksyxMenuButton.DrawImageButton(mCanvas, mPaint);
			phbMenuButton.DrawImageButton(mCanvas, mPaint);
			yxsmMenuButton.DrawImageButton(mCanvas, mPaint);
		}  
		//根据触摸的不同地方判断放置那张item图片
		public void UpdateTouchEvent(int x, int y, int state) {		   
			if(ksyxMenuButton.IsClick(x, y)) {
			   //教学图片按钮被按下
				if(state == 0) {
					ksyxMenuButton.ReadBitMap(mContext, R.drawable.ksyx2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, BallGameView.class);
				startActivity(mIntent);
			}else if(phbMenuButton.IsClick(x, y)) {
			   //设置图片按钮被按下   
				if(state == 0) {
					phbMenuButton.ReadBitMap(mContext, R.drawable.phb2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, Rank.class);
				startActivity(mIntent);
			}else if(yxsmMenuButton.IsClick(x, y)) {
			   //设置图片按钮被按下   
				if(state == 0) {
					yxsmMenuButton.ReadBitMap(mContext, R.drawable.yxsm2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, Instruction.class);
				startActivity(mIntent);
			}
			//触摸松开的时候恢复原来显示的图片
			if(state == 1){
				ksyxMenuButton.ReadBitMap(mContext, R.drawable.ksyx1);
				phbMenuButton.ReadBitMap(mContext, R.drawable.phb1);
				yxsmMenuButton.ReadBitMap(mContext, R.drawable.yxsm1);
				//异步更新，调用onDraw函数
				postInvalidate();
			}
		}
    }
    private static Boolean isExit = false;
    //捕捉按键
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub  
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {  
        	exitBy2Click();//调用双击退出函数
        }                          
        return false;  
    }
    //按返回键两次就退出
    private void exitBy2Click()
    {  
    	Timer tExit = null;
    	if(isExit == false ) 
        {  
    		isExit = true;  //准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
            tExit = new Timer(); 
            tExit.schedule(new TimerTask() {            
            	@Override  
	            public void run() {  
	            	isExit = false;  //取消退出             
	            }  
            }, 2000); //如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务 
       
        }else{  
        	//结束掉该activity，并退出进程
            finish();  
            System.exit(0);         
        }     
    }   
}