package com.ydp.ballgame;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;

public class BallGameView extends Activity{
	WakeLock wakeLock;
    MyView mAnimView = null;
    Handler myHandler;
    
    /**ʹ��SharedPreferences ���������ȡ����**/
    SharedPreferences mShared = null;

    /**�����п���ͬʱ���ڶ��SharedPreferences���ݣ� ����SharedPreferences�����ƾͿ����õ�����**/
    public final static String SHARED_MAIN = "ydp_BallGame";
   
    /**SharedPreferences�д������ݵ�Key����**/
    public final static String KEY_NAME = "Ball_score";
    
    /**SharedPreferences�д������ݵ�·��**/
    public final static String DATA_URL = "/data/data/";
    public final static String SHARED_MAIN_XML = "main.xml";
    private int []data_score;
    private final static int RANK_NUMBER = 5;
	private int mScreenWidth;
	private int mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		// ȫ����ʾ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//ǿ�ƺ��� 
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	
		/**��ȡ��Ļ���**/
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        
        //���ñ���͸��Ϊ�ޣ�������Ӧʱ��
		getWindow().setBackgroundDrawable(null);
		
		// ��ʾ�Զ������ϷView	
		mAnimView = new MyView(this);
		setContentView(mAnimView);
		//����Ļ�ϵ��һ�¾Ϳ�ʼ��Ϸ
		mAnimView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mAnimView.mRunning == false)
					mAnimView.mRunning = true;
			}
		});
		//ʵ����һ��handler�������߳��ﴫ��Ϣ����
		myHandler = new MyHandler();
    }
   
    public class MyView extends SurfaceView implements Callback,Runnable ,SensorEventListener{
		
    	public int TIME_IN_FRAME = 25; 
		
		/** ��Ϸ���� **/
		Paint mPaint = null;
		Paint mTextPaint = null;
		SurfaceHolder mSurfaceHolder = null;

		/** ������Ϸ����ѭ�� **/
		boolean mRunning = false;
	
		/** ��Ϸ���� **/
		Canvas mCanvas = null;
	
		/**������Ϸѭ��**/
		boolean mIsRunning = false;
		
		/**SensorManager������**/
		private SensorManager mSensorMgr = null;    
		Sensor mSensor = null;    
				
		/**С����**/
		private int mBallWidth = 0;
		private int mBallHeight = 0;
		
		/**ľ����**/
		private int mBoardWidth = 0;
		private int mBoardHeight = 0;
			
		/**С����Դ�ļ�Խ������**/
		private int mScreenBallWidth = 0;
		private int mScreenBallHeight = 0;
		
		/**ľ����Դ�ļ�Խ������**/
		private int mScreenBoardWidth = 0;
		private int mScreenBoardHeight = 0;
		
		/**��Ϸ�����ļ�**/
		private Bitmap mbitmapBg;
		
		/**С����Դ�ļ�**/
		private Bitmap mbitmapBall;
		private Bitmap mbitmapBoard;
		
		/**С�������λ��**/
		private float mPosX = 200;
		private float mPosY = 0;
		private float mPosX1 = 200;
		private float mPosY1 = mScreenBallHeight - 80;
		
		/**������ӦX�� Y�� Z�������ֵ**/
		private float mGX = 0;
		private float mGY = 0;
		private float mGZ = 0;
		
		/**״̬���߶�ֵ**/
		private int stateusBarH = 100;
		
		/**������**/
		private int mSquareWidth = 0;
		private int mSquareHeight = 0;
		
		/**�������и���**/
		private int mSquareRow = 0;
		private int mSquareColumn = 0;
		private int mSquareAll = 0;
		
		/**�����������**/
		private int mSquareLeft = 0;
		private int mSquareTop = 0;
		
		/**����״̬����**/
		private int [][]square_state; 
		
		/**��Ļ��ԵԤ��**/
		private final static int leftboard = 15;
		
		/**�÷�**/
		private int score = 0;
		
		/**��ʱ�Ľ������**/
		private boolean tmpOver;
		
		/**�ƶ��Ĳ���**/
		private float moveX = 7f;
		private float moveY = 7f;
		
		/**������ɫ����**///	
		private final int[] SquareColor = {Color.LTGRAY,Color.GRAY, Color.BLACK, 
				Color.rgb(64, 128, 255), Color.BLUE, Color.MAGENTA, Color.rgb(255, 128, 64), 
				Color.rgb(255, 192, 64), Color.RED,Color.CYAN, Color.GREEN};
		
		/**��������ļ��**/
		private int LenBetwSquare = 0;
		
		/**��Ϸ�ȼ�**/
		private int level = 1;
		private int speed = 0;
		
		/**ʤ������ʧ��**/
		private boolean WorF = false;
		
		/**���ж�����**/
		private int life = 3;
		
		public MyView(Context context) {
		    super(context);
		    
		    /** ���õ�ǰViewӵ�п��ƽ��� **/
		    this.setFocusable(true);
		    /** ���õ�ǰViewӵ�д����¼� **/
		    this.setFocusableInTouchMode(true);
		    /** �õ�SurfaceHolder���� **/
		    mSurfaceHolder = this.getHolder();
		    /** ��mSurfaceHolder��ӵ�Callback�ص������� **/
		    mSurfaceHolder.addCallback(this);
		    /** �������� **/
		    mCanvas = new Canvas();
		    /** �������߻��� **/
		    mPaint = new Paint();
		    mPaint.setColor(Color.WHITE);
		    /**����С����Դ**/
		    mbitmapBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball);
		    mbitmapBoard = BitmapFactory.decodeResource(this.getResources(), R.drawable.board);
		   		    
		    /**������Ϸ����**/
		    mbitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
		    mbitmapBg = Bitmap.createScaledBitmap(mbitmapBg, mScreenWidth, mScreenHeight, true);
		    
		    /**�õ�SensorManager����**/
		    mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);   
		    mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   
		    // ע��listener�������������Ǽ��ľ�ȷ��  
	            //SENSOR_DELAY_FASTEST ������ ��Ϊ̫����û��Ҫʹ��
	            //SENSOR_DELAY_GAME    ��Ϸ������ʹ��
	            //SENSOR_DELAY_NORMAL  �����ٶ�
	            //SENSOR_DELAY_UI 	       �������ٶ�
		    mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);  
		}
		private void init(){
			/**��ʼ��Ϸ��ѭ���߳�**/
		    mIsRunning = true;
		    mRunning = false;
		    new Thread(this).start();
		    
		    /**�õ�С����**/
		    mBallWidth = mbitmapBall.getWidth();
		    mBallHeight = mbitmapBall.getHeight();
		    
		    /**�õ�ľ����**/
		    mBoardWidth = mbitmapBoard.getWidth();
		    mBoardHeight = mbitmapBoard.getHeight();
		    
		    /**�õ�С��Խ������**/
		    mScreenBallWidth = mScreenWidth - mBallWidth - leftboard;
		    mScreenBallHeight = mScreenHeight - mBallHeight - leftboard;
		        
		    /**�õ�ľ��Խ������**/
		    mScreenBoardWidth = mScreenWidth - mBoardWidth - leftboard;
		    mScreenBoardHeight = mScreenHeight - mBoardHeight - leftboard;
		    
		    /**�õ�ľ���λ��**/
		    mPosX1 = mScreenWidth / 2 - mBoardWidth / 2;
		    mPosY1 = mScreenBoardHeight;
		    
		    /**�õ�С���λ��**/
		    mPosX = mScreenWidth / 2 - mBallWidth / 2;
		    mPosY = mScreenBoardHeight - mBallHeight;
	
			/**��������ļ��**/
			LenBetwSquare = 18;
			
			/**�õ��������и���**/
			mSquareColumn = 6;
			mSquareRow = level + 5;
			mSquareAll = 10;
			
			/**������**/
			mSquareWidth = 40;
			mSquareHeight = 20;
			mSquareWidth = (mScreenWidth - leftboard * 2 - mBallWidth * 4 - LenBetwSquare * (mSquareColumn - 1)) / mSquareColumn; 
			mSquareHeight = (mScreenHeight * 2 / 3 - leftboard - stateusBarH - 2 * mBallHeight - LenBetwSquare * (mSquareAll - 1)) / mSquareAll;
			
			/**�����������**/
			mSquareLeft = (mScreenWidth - mSquareColumn * mSquareWidth - (mSquareColumn - 1) * LenBetwSquare) / 2;
			mSquareTop = stateusBarH + leftboard + mBallHeight * 2;
			
			/**��ʼ��ֵ**/
			tmpOver = false;			
			moveX = 4.8f;
			moveY = 5.3f;			
			TIME_IN_FRAME = 25 - ((level - 1) / 2) * 3;
			
			/**��ʼ�������״̬**/			
			square_state = new int[mSquareRow][mSquareColumn];
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					square_state[i][j] = mSquareRow - i;
				}
			}
		}  
		//�����ڻ����ϵĺ���
		private void Draw() {
		    /**������Ϸ����**/
		    drawBg();
		    /**������Ϸ�߿�**/
		    drawGameBoard();
		    /**����С��**/
		    mCanvas.drawBitmap(mbitmapBall, mPosX,mPosY, mPaint);
		    mCanvas.drawBitmap(mbitmapBoard, mPosX1,mPosY1, mPaint);
		    Log.i("test", mPosX + "   "+ mPosY);
		    /**���Ʒ���**/
		    drawSquare();
		    /**����״̬��**/
		    drawStatusBar();
		    if(mRunning == false){
		    	drawStr();
		    }		    
		}
		//��������ͼƬ
		private void drawBg(){
			mCanvas.drawBitmap(mbitmapBg,0,0, mPaint);
		}
		//������ʼ��Ϸ֮ǰ����ʾ��Ϣ
		private void drawStr(){
			mCanvas.drawText("�����Ļ����ط���ʼ��Ϸ��", mScreenWidth / 15 , mScreenHeight * 4 / 5, mPaint);
		}
		//��������
		private void drawSquare(){
			int tmpColor = mPaint.getColor();
			//���û��ʻ������Ķ�����ʵ�ĵ�
			mPaint.setStyle(Style.FILL);
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					if(square_state[i][j] > 0){
						//��ͬ����Ҫ��ͬ����ɫ
						mPaint.setColor(SquareColor[square_state[i][j] - 1]);
						mCanvas.drawRect(mSquareLeft + j * (mSquareWidth + LenBetwSquare), mSquareTop + i * (mSquareHeight + LenBetwSquare), 
								mSquareLeft + j * (mSquareWidth + LenBetwSquare) + mSquareWidth, 
								mSquareTop + i * (mSquareHeight + LenBetwSquare) + mSquareHeight, mPaint);
					}
				}
			}
			//��ԭ���� ����ɫ
			mPaint.setColor(tmpColor);
		}
		//����״̬��
		private void drawStatusBar(){
			mPaint.setTextSize(30);
			mCanvas.drawText("�÷� ��  " + score, leftboard + mScreenWidth / 15, leftboard + mScreenHeight / 15, mPaint);
			mCanvas.drawText("�ȼ� ��  " + level, leftboard + mScreenWidth * 3 / 5, leftboard + mScreenHeight / 18, mPaint);
			mCanvas.drawText("���� ��  " + life, leftboard + mScreenWidth * 3 / 5, leftboard + mScreenHeight * 2 / 18, mPaint);
		}
		//������Ϸ�ķ�Χ
		private void drawGameBoard(){
			mPaint.setColor(Color.BLACK);
		    mPaint.setStyle(Style.STROKE);
		    mCanvas.drawRect(new RectF(leftboard, leftboard, mScreenWidth - leftboard , mScreenHeight - leftboard), mPaint);
		    mCanvas.drawRect(new RectF(leftboard, leftboard + stateusBarH, mScreenWidth - leftboard , mScreenHeight - leftboard), mPaint);
			
		}
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	
		}
	
		public void surfaceCreated(SurfaceHolder holder) {
		    init();
		}
		
		private void setLevel(int value){
			this.level = value;
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
		    mIsRunning = false;
		    //ж�ص�֮ǰע��Ĵ��������
		    mSensorMgr.unregisterListener(this); 
		}
	    //���̲߳��ϵ�ѭ��
		public void run() {
		    while (mIsRunning) {
	
				/** ȡ�ø�����Ϸ֮ǰ��ʱ�� **/
				long startTime = System.currentTimeMillis();
		
				/** ����������̰߳�ȫ�� **/
				synchronized (mSurfaceHolder) {
				    /** �õ���ǰ���� Ȼ������ **/
				    mCanvas = mSurfaceHolder.lockCanvas();
				    if(mRunning){
				    	change();
				    }
				    Draw();
				    /** ���ƽ����������ʾ����Ļ�� **/
				    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
				}
		
				/** ȡ�ø�����Ϸ������ʱ�� **/
				long endTime = System.currentTimeMillis();
		
				/** �������Ϸһ�θ��µĺ����� **/
				int diffTime = (int) (endTime - startTime);
		
				/** ȷ��ÿ�θ���ʱ��Ϊ50֡ **/
				while (diffTime <= TIME_IN_FRAME) {
				    diffTime = (int) (System.currentTimeMillis() - startTime);
				    /** �̵߳ȴ� **/
				    Thread.yield();
				}
		    }	    
		}
		//������ÿһ��ˢ��ǰλ�õ��޸�
		private void change(){
			
			mPosX += moveX;
			mPosY += moveY;
			
			//�������С�����ײ
			solve_SquareBallCol();
			//����ľ���С�����ײ
			solve_BoardBallCol();
			//����ľ���λ��
			update_BoardPos();
			//�ж��Ƿ���Ϸ������
			solve_isOver();
		}
		//�ж��Ƿ���Ϸ������
		private void solve_isOver(){
			if(isOver()){
				setOverStr();
				mIsRunning = false;					
				sendOverInf();
			}
		}
		//�������С�����ײ
		private void solve_SquareBallCol(){
			//�ж���ײ�ڷ����ϵ���һ��
			switch(isSBCol()){
			case 1:
			case 3:
				moveY = -moveY;
				break;
			case 2:
			case 4:
				moveX = -moveX;
				break;
			}
		}
		//����ľ���С�����ײ
		private void solve_BoardBallCol(){
			if(mPosX >= mScreenBallWidth){
				mPosX = mScreenBallWidth;
				moveX = -moveX;
			}else if(mPosY >= mScreenBallHeight - mBoardHeight){			
				if(!tmpOver && isCollision()){
					mPosY = mScreenBallHeight - mBoardHeight;
					if(moveX * mGX > 0){
						moveY = (float) (-moveY - mGX * 0.1);//����
						moveX -= mGX * 0.4;
					}else{
						moveY = (float) (-moveY - mGX * 0.4);//����
						moveX -= mGX * 0.2;
					}
				}else{
					tmpOver = true;
				}
			}else if(mPosX <= leftboard){
				mPosX = leftboard;
				moveX = -moveX;
			}else if(mPosY <= leftboard + stateusBarH){
				mPosY = leftboard + stateusBarH;
				moveY = -moveY;
			}
		}
		//����ľ���λ��
		private void update_BoardPos(){
			//Ϊ�˼ӿ��ٶȳ���2
			mPosX1 -= mGX * 2;
		    if (mPosX1 < leftboard) {
		    	mPosX1 = leftboard;
		    } else if (mPosX1 > mScreenBoardWidth) {
		    	mPosX1 = mScreenBoardWidth; 
		    }
		}
		//������Ϸ��������Ϣ
		private void sendOverInf(){
			 Message msg = new Message();  
	         Bundle b = new Bundle();// �������  
	         b.putInt("score", score);  
	         b.putInt("level", level);
	         //ʧ�ܵĻ���ʤ����
	         b.putBoolean("WorF", WorF);
	         
	         msg.setData(b);  
	         //���߳��������淢����Ϣ
	         BallGameView.this.myHandler.sendMessage(msg);
		}
		//������Ϸ����ʱ��ʾ������
		private void setOverStr(){
			float tmpTextSize = mPaint.getTextSize();
			int tmpColor = mPaint.getColor();
			mPaint.setTextSize(40);
			mPaint.setColor(Color.GREEN);
			mCanvas.drawText("������Ϸ�ѽ�����" + score, leftboard + mBallWidth * 2, mScreenHeight / 2, mPaint);
			mPaint.setTextSize(tmpTextSize);
			mPaint.setColor(tmpColor);
			draw(mCanvas);			
		}
	
		//��Ϸ���¿�ʼ
		private void reset(){
			init();
		}
		//�ж��Ƿ������
		private boolean isOver(){
			//С�������Ļ���·�
			if(mPosY > mScreenHeight){				
				
				if(life > 1){
					life--;
					mRunning = false;
				    /**����ľ���λ��**/
				    mPosX1 = mScreenWidth / 2 - mBoardWidth / 2;
				    mPosY1 = mScreenBoardHeight;
				    /**�ƶ�����**/
				    moveX = 4.8f;
					moveY = 5.3f;
				    /**����С���λ��**/
				    mPosX = mScreenWidth / 2 - mBallWidth / 2;
				    mPosY = mScreenBoardHeight - mBallHeight;
				    tmpOver = false;
					Log.i("test", mPosY+"");
				    return false;
				}
				WorF = false;
				return true;
			}
			//�ж��ǲ������еķ��鶼���ɵ���
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					if(square_state[i][j] > 0){
						return false;
					}
				}
			}
			WorF = true;
			return true;
		}
		//�ж��Ƿ�С���뷽����ײ���ֱ�����������ĸ������������
		private int isSBCol(){
			int SpointL , SpointT;
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					if(square_state[i][j] == 0) continue;
					SpointL = mSquareLeft + j * (mSquareWidth + LenBetwSquare);
					SpointT = mSquareTop + i * (mSquareHeight + LenBetwSquare);
					if(mPosX > SpointL){//С�����ұ�
						if(mPosY < SpointT){//С�����ϱ�
							if((mPosX - SpointL) < mSquareWidth && -(mPosY - SpointT) < mSquareHeight){
								score += 100;
								square_state[i][j]--;	
						    	if(moveX > 0 && moveY > 0) return 1;//top
						    	else if(moveX < 0 && moveY < 0) return 2;//right
						    	else if(mBallHeight - (SpointT - mPosY) > mSquareWidth - (mPosX - SpointL)){
						    		return 2;//right
						    	}else {
						    		return 1;//top
						    	}
							}
						}else{//С�����±�
							if((mPosX - SpointL) < mSquareWidth && (mPosY - SpointT) < mSquareHeight){
								score += 100;
								square_state[i][j]--;
						    	if(moveX > 0 && moveY < 0) return 3;//bottom
						    	else if(moveX < 0 && moveY > 0) return 2;//right
						    	else if(mSquareHeight + (SpointT - mPosY) > mSquareWidth - (mPosX - SpointL)){
						    		return 2;//right
						    	}else {
						    		return 3;//bottom
						    	}
							}
						}
					}else{//С�������
						if(mPosY < SpointT){//С�����ϱ�
							if(-(mPosX - SpointL) < mBallWidth && -(mPosY - SpointT) < mSquareHeight){
								score += 100;
								square_state[i][j]--;							
						    	if(moveX > 0 && moveY < 0) return 4;//left
						    	else if(moveX < 0 && moveY > 0) return 1;//top
						    	else if(mBallHeight - (SpointT - mPosY) > mBallWidth + (mPosX - SpointL)){
						    		return 4;//left
						    	}else {
						    		return 1;//top
						    	}
							}
						}else{//С�����±�						
					    	if(-(mPosX - SpointL) < mBallWidth && (mPosY - SpointT) < mSquareHeight){
					    		score += 100;
								square_state[i][j]--;
						    	if(moveX > 0 && moveY > 0) return 4;//left
						    	else if(moveX < 0 && moveY < 0) return 3;//bottom
						    	else if(mSquareHeight + (SpointT - mPosY) > mBallWidth + (mPosX - SpointL)){
						    		return 4;//left
						    	}else {
						    		return 3;//bottom
						    	}
							}
						}
					}
				}
			}
			return -1;
		}
		//�ж��Ƿ�С����ǽ����ײ��
		private boolean isCollision(){	
			if(mPosX > mPosX1){
				return mPosX - mPosX1 <= mBoardWidth;
			}else{
				return mPosX1 - mPosX <= mBallWidth;
			}		
		}
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		    // TODO Auto-generated method stub
		}
		//������Ӧ����õ�xyz����ֵ
		public void onSensorChanged(SensorEvent event) {
		    mGX = event.values[SensorManager.DATA_X];
		    mGY = event.values[SensorManager.DATA_Y];
		    mGZ = event.values[SensorManager.DATA_Z];
		}
    }
    /**  
	* ������Ϣ,������Ϣ ,��Handler���뵱ǰ���߳�һ������  
	* */ 
	class MyHandler extends Handler {  
	    public MyHandler() {  
	    }  
	    public MyHandler(Looper L) {  
	    	super(L);  
		}  
	    // ���������д�˷���,��������  
	    @Override 
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub  
	        Log.d("test", "handleMessage......");  
	        super.handleMessage(msg);  
	        // �˴����Ը���UI  
	        Bundle b = msg.getData();   
	        int score = b.getInt("score");
	        int level = b.getInt("level");
	        boolean WorF = b.getBoolean("WorF");
	        
	        udateRank(score);
	        
	        if(WorF){
	        	showWinDialog(score, level);
	        }else{
	        	showFailDialog(score, level);
	        }
	    }  
    } 
	private void udateRank(int Dscore){
		/**�õ�������SHARED_MAIN ��SharedPreferences����**/
    	mShared = getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
    	/**�õ�SharedPreferences�б������ֵ �ڶ�������Ϊ���SharedPreferences��û�б���͸�һ��Ĭ��ֵ**/
    	data_score = new int[6];
    	for(int i = 1;i <= RANK_NUMBER;i++){
    		data_score[i - 1] = mShared.getInt(KEY_NAME + i, -1);
    		Log.i("test3", "i ++  " + data_score[i - 1]);
    	}
    	for(int i = 1;i <= RANK_NUMBER;i++){
    		if(Dscore > data_score[i - 1]){
    			for(int j = RANK_NUMBER - 1;j >= i;j--)
    				data_score[j] = data_score[j - 1];
    			data_score[i - 1] = Dscore;
    			break;
    		}
    	}
    	/**��ʼ������SharedPreferences**/
    	Editor editor = mShared.edit();
    	for(int i = 1;i <= RANK_NUMBER;i++){
    		editor.putInt(KEY_NAME + i, data_score[i - 1]);
    	}    	
    	/**put��ϱ���Ҫcommit()�����޷�����**/
    	editor.commit();
	}
	//��Ϸʧ�ܵĶԻ���
	private void showFailDialog(int Dscore, int Dlevel){
		AlertDialog.Builder builder = new AlertDialog.Builder(BallGameView.this);
		builder.setTitle("������Ϸʧ�ܣ�");
		builder.setIcon(R.drawable.ball1);
		builder.setMessage("��Ϸ�ۼƵ÷֣�" + Dscore + "\n��ǰ�ȼ�" + Dlevel + "\n�Ƿ�������ս��");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub			
				mAnimView.level = 1;
				mAnimView.score = 0;
				mAnimView.life = 3;
				mAnimView.reset();
			}			
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(BallGameView.this, MainGameMenu.class);
				startActivity(mIntent);
			}
		});
		builder.create().show();
	}
	//��Ϸʤ����ĶԻ���
	private void showWinDialog(int Dscore, int Dlevel){
		AlertDialog.Builder builder = new AlertDialog.Builder(BallGameView.this);
		builder.setTitle("������Ϸʤ����");
		builder.setIcon(R.drawable.ball1);
		if(Dlevel < 5){
			builder.setMessage("��Ϸ�ۼƵ÷֣�" + Dscore + "\n��ǰ��ɵȼ�" + Dlevel + "\n�Ƿ������ս��");
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub			
					
					mAnimView.level++;					
					mAnimView.reset();
				}			
			});
			builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(BallGameView.this, MainGameMenu.class);
					startActivity(mIntent);
				}
			});
		}else if(Dlevel == 5){
			builder.setMessage("��Ϸ�ۼƵ÷֣�" + Dscore + "\n��ǰ��ɵȼ�" + Dlevel + "\n   ��ϲ���ˣ���ͨ�أ�\n�뷵�����˵�");
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub			
					Intent mIntent = new Intent(BallGameView.this, MainGameMenu.class);
					startActivity(mIntent);
				}			
			});
		}		
		builder.create().show();
	}	
	public void onResume(){
		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
		newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "BallGameView");

		wakeLock.acquire();
		super.onResume();
	}
	public void onPause(){
		if (wakeLock != null) {
			wakeLock.release();
		}
		super.onPause();
	}
}