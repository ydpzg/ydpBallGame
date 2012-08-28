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
    	// ȫ����ʾ����
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    		WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	//���ñ���
        getWindow().setBackgroundDrawable(null);
        //�õ���Ļ����
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        MenuView = new myView(this);
        setContentView(MenuView);
    }
    //������Ļ�Ĵ����¼�
    public boolean onTouchEvent(MotionEvent event) {
    	int x = (int)event.getX();
		int y = (int)event.getY(); 
		switch (event.getAction()) {
		// ������Ļʱ��
		case MotionEvent.ACTION_DOWN:
			MenuView.UpdateTouchEvent(x, y, MotionEvent.ACTION_DOWN);			
		    break;
		// �������ƶ�ʱ��
		case MotionEvent.ACTION_MOVE:
		    break;
		// ��ֹ����ʱ��
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
			//��ͼƬ��Դ��ȡ����
			menutitle = BitmapFactory.decodeResource(getResources(), R.drawable.menutitle);
			mBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball1);
			sample = BitmapFactory.decodeResource(getResources(), R.drawable.yxsm1);
			//��ȡ����ͼƬ����С����Ļʵ�ʵĴ�С
			backbg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
			backbg = Bitmap.createScaledBitmap(backbg, mScreenWidth, mScreenHeight, true);
			//�ɵ����ͼƬ�࣬��ȡͼƬ��Դ
			ksyxMenuButton = new ImageButton(context,R.drawable.ksyx1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 7 / 15);
			phbMenuButton = new ImageButton(context,R.drawable.phb1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 9 / 15);
			yxsmMenuButton = new ImageButton(context,R.drawable.yxsm1,mScreenWidth / 2 - sample.getWidth() / 2,mScreenHeight * 11 / 15);
			
		}
		//view���ǿ�����������л�������ȥ��
		public void onDraw(Canvas canvas){
			mCanvas = canvas;
			
			mCanvas.drawBitmap(backbg, 0, 0, mPaint);
			mCanvas.drawBitmap(mBall, mScreenWidth / 2 - mBall.getWidth() / 2, mScreenHeight / 6, mPaint);
			mCanvas.drawBitmap(menutitle, mScreenWidth / 2 - menutitle.getWidth() / 2, mScreenHeight * 3 / 10, mPaint);
			//���ÿɵ����ͼƬ����ķ���������
			ksyxMenuButton.DrawImageButton(mCanvas, mPaint);
			phbMenuButton.DrawImageButton(mCanvas, mPaint);
			yxsmMenuButton.DrawImageButton(mCanvas, mPaint);
		}  
		//���ݴ����Ĳ�ͬ�ط��жϷ�������itemͼƬ
		public void UpdateTouchEvent(int x, int y, int state) {		   
			if(ksyxMenuButton.IsClick(x, y)) {
			   //��ѧͼƬ��ť������
				if(state == 0) {
					ksyxMenuButton.ReadBitMap(mContext, R.drawable.ksyx2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, BallGameView.class);
				startActivity(mIntent);
			}else if(phbMenuButton.IsClick(x, y)) {
			   //����ͼƬ��ť������   
				if(state == 0) {
					phbMenuButton.ReadBitMap(mContext, R.drawable.phb2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, Rank.class);
				startActivity(mIntent);
			}else if(yxsmMenuButton.IsClick(x, y)) {
			   //����ͼƬ��ť������   
				if(state == 0) {
					yxsmMenuButton.ReadBitMap(mContext, R.drawable.yxsm2);
				}
				postInvalidate();
				Intent mIntent = new Intent(MainGameMenu.this, Instruction.class);
				startActivity(mIntent);
			}
			//�����ɿ���ʱ��ָ�ԭ����ʾ��ͼƬ
			if(state == 1){
				ksyxMenuButton.ReadBitMap(mContext, R.drawable.ksyx1);
				phbMenuButton.ReadBitMap(mContext, R.drawable.phb1);
				yxsmMenuButton.ReadBitMap(mContext, R.drawable.yxsm1);
				//�첽���£�����onDraw����
				postInvalidate();
			}
		}
    }
    private static Boolean isExit = false;
    //��׽����
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub  
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {  
        	exitBy2Click();//����˫���˳�����
        }                          
        return false;  
    }
    //�����ؼ����ξ��˳�
    private void exitBy2Click()
    {  
    	Timer tExit = null;
    	if(isExit == false ) 
        {  
    		isExit = true;  //׼���˳�
            Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
            tExit = new Timer(); 
            tExit.schedule(new TimerTask() {            
            	@Override  
	            public void run() {  
	            	isExit = false;  //ȡ���˳�             
	            }  
            }, 2000); //���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е����� 
       
        }else{  
        	//��������activity�����˳�����
            finish();  
            System.exit(0);         
        }     
    }   
}