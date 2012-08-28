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
    
    /**使用SharedPreferences 来储存与读取数据**/
    SharedPreferences mShared = null;

    /**程序中可以同时存在多个SharedPreferences数据， 根据SharedPreferences的名称就可以拿到对象**/
    public final static String SHARED_MAIN = "ydp_BallGame";
   
    /**SharedPreferences中储存数据的Key名称**/
    public final static String KEY_NAME = "Ball_score";
    
    /**SharedPreferences中储存数据的路径**/
    public final static String DATA_URL = "/data/data/";
    public final static String SHARED_MAIN_XML = "main.xml";
    private int []data_score;
    private final static int RANK_NUMBER = 5;
	private int mScreenWidth;
	private int mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		// 全屏显示窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//强制横屏 
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	
		/**收取屏幕宽高**/
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        
        //设置背景透明为无，加速响应时间
		getWindow().setBackgroundDrawable(null);
		
		// 显示自定义的游戏View	
		mAnimView = new MyView(this);
		setContentView(mAnimView);
		//朝屏幕上点击一下就开始游戏
		mAnimView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mAnimView.mRunning == false)
					mAnimView.mRunning = true;
			}
		});
		//实例化一个handler，用于线程里传消息传来
		myHandler = new MyHandler();
    }
   
    public class MyView extends SurfaceView implements Callback,Runnable ,SensorEventListener{
		
    	public int TIME_IN_FRAME = 25; 
		
		/** 游戏画笔 **/
		Paint mPaint = null;
		Paint mTextPaint = null;
		SurfaceHolder mSurfaceHolder = null;

		/** 控制游戏更新循环 **/
		boolean mRunning = false;
	
		/** 游戏画布 **/
		Canvas mCanvas = null;
	
		/**控制游戏循环**/
		boolean mIsRunning = false;
		
		/**SensorManager管理器**/
		private SensorManager mSensorMgr = null;    
		Sensor mSensor = null;    
				
		/**小球宽高**/
		private int mBallWidth = 0;
		private int mBallHeight = 0;
		
		/**木板宽高**/
		private int mBoardWidth = 0;
		private int mBoardHeight = 0;
			
		/**小球资源文件越界区域**/
		private int mScreenBallWidth = 0;
		private int mScreenBallHeight = 0;
		
		/**木板资源文件越界区域**/
		private int mScreenBoardWidth = 0;
		private int mScreenBoardHeight = 0;
		
		/**游戏背景文件**/
		private Bitmap mbitmapBg;
		
		/**小球资源文件**/
		private Bitmap mbitmapBall;
		private Bitmap mbitmapBoard;
		
		/**小球的坐标位置**/
		private float mPosX = 200;
		private float mPosY = 0;
		private float mPosX1 = 200;
		private float mPosY1 = mScreenBallHeight - 80;
		
		/**重力感应X轴 Y轴 Z轴的重力值**/
		private float mGX = 0;
		private float mGY = 0;
		private float mGZ = 0;
		
		/**状态栏高度值**/
		private int stateusBarH = 100;
		
		/**方块宽高**/
		private int mSquareWidth = 0;
		private int mSquareHeight = 0;
		
		/**方块行列个数**/
		private int mSquareRow = 0;
		private int mSquareColumn = 0;
		private int mSquareAll = 0;
		
		/**方块行列起点**/
		private int mSquareLeft = 0;
		private int mSquareTop = 0;
		
		/**方块状态数组**/
		private int [][]square_state; 
		
		/**屏幕边缘预留**/
		private final static int leftboard = 15;
		
		/**得分**/
		private int score = 0;
		
		/**暂时的结束标记**/
		private boolean tmpOver;
		
		/**移动的步数**/
		private float moveX = 7f;
		private float moveY = 7f;
		
		/**方块颜色数组**///	
		private final int[] SquareColor = {Color.LTGRAY,Color.GRAY, Color.BLACK, 
				Color.rgb(64, 128, 255), Color.BLUE, Color.MAGENTA, Color.rgb(255, 128, 64), 
				Color.rgb(255, 192, 64), Color.RED,Color.CYAN, Color.GREEN};
		
		/**两个方块的间隔**/
		private int LenBetwSquare = 0;
		
		/**游戏等级**/
		private int level = 1;
		private int speed = 0;
		
		/**胜利还是失败**/
		private boolean WorF = false;
		
		/**还有多少命**/
		private int life = 3;
		
		public MyView(Context context) {
		    super(context);
		    
		    /** 设置当前View拥有控制焦点 **/
		    this.setFocusable(true);
		    /** 设置当前View拥有触摸事件 **/
		    this.setFocusableInTouchMode(true);
		    /** 拿到SurfaceHolder对象 **/
		    mSurfaceHolder = this.getHolder();
		    /** 将mSurfaceHolder添加到Callback回调函数中 **/
		    mSurfaceHolder.addCallback(this);
		    /** 创建画布 **/
		    mCanvas = new Canvas();
		    /** 创建曲线画笔 **/
		    mPaint = new Paint();
		    mPaint.setColor(Color.WHITE);
		    /**加载小球资源**/
		    mbitmapBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball);
		    mbitmapBoard = BitmapFactory.decodeResource(this.getResources(), R.drawable.board);
		   		    
		    /**加载游戏背景**/
		    mbitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
		    mbitmapBg = Bitmap.createScaledBitmap(mbitmapBg, mScreenWidth, mScreenHeight, true);
		    
		    /**得到SensorManager对象**/
		    mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);   
		    mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   
		    // 注册listener，第三个参数是检测的精确度  
	            //SENSOR_DELAY_FASTEST 最灵敏 因为太快了没必要使用
	            //SENSOR_DELAY_GAME    游戏开发中使用
	            //SENSOR_DELAY_NORMAL  正常速度
	            //SENSOR_DELAY_UI 	       最慢的速度
		    mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);  
		}
		private void init(){
			/**开始游戏主循环线程**/
		    mIsRunning = true;
		    mRunning = false;
		    new Thread(this).start();
		    
		    /**得到小球宽高**/
		    mBallWidth = mbitmapBall.getWidth();
		    mBallHeight = mbitmapBall.getHeight();
		    
		    /**得到木板宽高**/
		    mBoardWidth = mbitmapBoard.getWidth();
		    mBoardHeight = mbitmapBoard.getHeight();
		    
		    /**得到小球越界区域**/
		    mScreenBallWidth = mScreenWidth - mBallWidth - leftboard;
		    mScreenBallHeight = mScreenHeight - mBallHeight - leftboard;
		        
		    /**得到木板越界区域**/
		    mScreenBoardWidth = mScreenWidth - mBoardWidth - leftboard;
		    mScreenBoardHeight = mScreenHeight - mBoardHeight - leftboard;
		    
		    /**得到木板的位置**/
		    mPosX1 = mScreenWidth / 2 - mBoardWidth / 2;
		    mPosY1 = mScreenBoardHeight;
		    
		    /**得到小球的位置**/
		    mPosX = mScreenWidth / 2 - mBallWidth / 2;
		    mPosY = mScreenBoardHeight - mBallHeight;
	
			/**两个方块的间隔**/
			LenBetwSquare = 18;
			
			/**得到方块行列个数**/
			mSquareColumn = 6;
			mSquareRow = level + 5;
			mSquareAll = 10;
			
			/**方块宽高**/
			mSquareWidth = 40;
			mSquareHeight = 20;
			mSquareWidth = (mScreenWidth - leftboard * 2 - mBallWidth * 4 - LenBetwSquare * (mSquareColumn - 1)) / mSquareColumn; 
			mSquareHeight = (mScreenHeight * 2 / 3 - leftboard - stateusBarH - 2 * mBallHeight - LenBetwSquare * (mSquareAll - 1)) / mSquareAll;
			
			/**方块行列起点**/
			mSquareLeft = (mScreenWidth - mSquareColumn * mSquareWidth - (mSquareColumn - 1) * LenBetwSquare) / 2;
			mSquareTop = stateusBarH + leftboard + mBallHeight * 2;
			
			/**初始化值**/
			tmpOver = false;			
			moveX = 4.8f;
			moveY = 5.3f;			
			TIME_IN_FRAME = 25 - ((level - 1) / 2) * 3;
			
			/**初始化方块的状态**/			
			square_state = new int[mSquareRow][mSquareColumn];
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					square_state[i][j] = mSquareRow - i;
				}
			}
		}  
		//处理画在画布上的函数
		private void Draw() {
		    /**绘制游戏背景**/
		    drawBg();
		    /**绘制游戏边框**/
		    drawGameBoard();
		    /**绘制小球**/
		    mCanvas.drawBitmap(mbitmapBall, mPosX,mPosY, mPaint);
		    mCanvas.drawBitmap(mbitmapBoard, mPosX1,mPosY1, mPaint);
		    Log.i("test", mPosX + "   "+ mPosY);
		    /**绘制方块**/
		    drawSquare();
		    /**绘制状态栏**/
		    drawStatusBar();
		    if(mRunning == false){
		    	drawStr();
		    }		    
		}
		//画出背景图片
		private void drawBg(){
			mCanvas.drawBitmap(mbitmapBg,0,0, mPaint);
		}
		//画出开始游戏之前的提示信息
		private void drawStr(){
			mCanvas.drawText("点击屏幕任意地方开始游戏！", mScreenWidth / 15 , mScreenHeight * 4 / 5, mPaint);
		}
		//画出方块
		private void drawSquare(){
			int tmpColor = mPaint.getColor();
			//设置画笔画出来的东西是实心的
			mPaint.setStyle(Style.FILL);
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					if(square_state[i][j] > 0){
						//不同行需要不同的颜色
						mPaint.setColor(SquareColor[square_state[i][j] - 1]);
						mCanvas.drawRect(mSquareLeft + j * (mSquareWidth + LenBetwSquare), mSquareTop + i * (mSquareHeight + LenBetwSquare), 
								mSquareLeft + j * (mSquareWidth + LenBetwSquare) + mSquareWidth, 
								mSquareTop + i * (mSquareHeight + LenBetwSquare) + mSquareHeight, mPaint);
					}
				}
			}
			//还原画笔 的颜色
			mPaint.setColor(tmpColor);
		}
		//画出状态栏
		private void drawStatusBar(){
			mPaint.setTextSize(30);
			mCanvas.drawText("得分 ：  " + score, leftboard + mScreenWidth / 15, leftboard + mScreenHeight / 15, mPaint);
			mCanvas.drawText("等级 ：  " + level, leftboard + mScreenWidth * 3 / 5, leftboard + mScreenHeight / 18, mPaint);
			mCanvas.drawText("生命 ：  " + life, leftboard + mScreenWidth * 3 / 5, leftboard + mScreenHeight * 2 / 18, mPaint);
		}
		//画出游戏的范围
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
		    //卸载掉之前注册的传感器组件
		    mSensorMgr.unregisterListener(this); 
		}
	    //主线程不断的循环
		public void run() {
		    while (mIsRunning) {
	
				/** 取得更新游戏之前的时间 **/
				long startTime = System.currentTimeMillis();
		
				/** 在这里加上线程安全锁 **/
				synchronized (mSurfaceHolder) {
				    /** 拿到当前画布 然后锁定 **/
				    mCanvas = mSurfaceHolder.lockCanvas();
				    if(mRunning){
				    	change();
				    }
				    Draw();
				    /** 绘制结束后解锁显示在屏幕上 **/
				    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
				}
		
				/** 取得更新游戏结束的时间 **/
				long endTime = System.currentTimeMillis();
		
				/** 计算出游戏一次更新的毫秒数 **/
				int diffTime = (int) (endTime - startTime);
		
				/** 确保每次更新时间为50帧 **/
				while (diffTime <= TIME_IN_FRAME) {
				    diffTime = (int) (System.currentTimeMillis() - startTime);
				    /** 线程等待 **/
				    Thread.yield();
				}
		    }	    
		}
		//用来做每一次刷新前位置的修改
		private void change(){
			
			mPosX += moveX;
			mPosY += moveY;
			
			//处理方块和小球的碰撞
			solve_SquareBallCol();
			//处理木板和小球的碰撞
			solve_BoardBallCol();
			//更新木板的位置
			update_BoardPos();
			//判断是否游戏结束了
			solve_isOver();
		}
		//判断是否游戏结束了
		private void solve_isOver(){
			if(isOver()){
				setOverStr();
				mIsRunning = false;					
				sendOverInf();
			}
		}
		//处理方块和小球的碰撞
		private void solve_SquareBallCol(){
			//判断是撞在方块上的哪一边
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
		//处理木板和小球的碰撞
		private void solve_BoardBallCol(){
			if(mPosX >= mScreenBallWidth){
				mPosX = mScreenBallWidth;
				moveX = -moveX;
			}else if(mPosY >= mScreenBallHeight - mBoardHeight){			
				if(!tmpOver && isCollision()){
					mPosY = mScreenBallHeight - mBoardHeight;
					if(moveX * mGX > 0){
						moveY = (float) (-moveY - mGX * 0.1);//减速
						moveX -= mGX * 0.4;
					}else{
						moveY = (float) (-moveY - mGX * 0.4);//加速
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
		//更新木板的位置
		private void update_BoardPos(){
			//为了加快速度乘以2
			mPosX1 -= mGX * 2;
		    if (mPosX1 < leftboard) {
		    	mPosX1 = leftboard;
		    } else if (mPosX1 > mScreenBoardWidth) {
		    	mPosX1 = mScreenBoardWidth; 
		    }
		}
		//传出游戏结束的信息
		private void sendOverInf(){
			 Message msg = new Message();  
	         Bundle b = new Bundle();// 存放数据  
	         b.putInt("score", score);  
	         b.putInt("level", level);
	         //失败的还是胜利的
	         b.putBoolean("WorF", WorF);
	         
	         msg.setData(b);  
	         //在线程里向外面发出信息
	         BallGameView.this.myHandler.sendMessage(msg);
		}
		//设置游戏结束时显示的文字
		private void setOverStr(){
			float tmpTextSize = mPaint.getTextSize();
			int tmpColor = mPaint.getColor();
			mPaint.setTextSize(40);
			mPaint.setColor(Color.GREEN);
			mCanvas.drawText("本次游戏已结束！" + score, leftboard + mBallWidth * 2, mScreenHeight / 2, mPaint);
			mPaint.setTextSize(tmpTextSize);
			mPaint.setColor(tmpColor);
			draw(mCanvas);			
		}
	
		//游戏重新开始
		private void reset(){
			init();
		}
		//判断是否结束了
		private boolean isOver(){
			//小球掉到屏幕的下方
			if(mPosY > mScreenHeight){				
				
				if(life > 1){
					life--;
					mRunning = false;
				    /**重设木板的位置**/
				    mPosX1 = mScreenWidth / 2 - mBoardWidth / 2;
				    mPosY1 = mScreenBoardHeight;
				    /**移动方向**/
				    moveX = 4.8f;
					moveY = 5.3f;
				    /**重设小球的位置**/
				    mPosX = mScreenWidth / 2 - mBallWidth / 2;
				    mPosY = mScreenBoardHeight - mBallHeight;
				    tmpOver = false;
					Log.i("test", mPosY+"");
				    return false;
				}
				WorF = false;
				return true;
			}
			//判断是不是所有的方块都被干掉了
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
		//判断是否小球与方块碰撞，分别从左右上下四个方向进行讨论
		private int isSBCol(){
			int SpointL , SpointT;
			for(int i = 0;i < mSquareRow;i++){
				for(int j = 0;j < mSquareColumn;j++){
					if(square_state[i][j] == 0) continue;
					SpointL = mSquareLeft + j * (mSquareWidth + LenBetwSquare);
					SpointT = mSquareTop + i * (mSquareHeight + LenBetwSquare);
					if(mPosX > SpointL){//小球在右边
						if(mPosY < SpointT){//小球在上边
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
						}else{//小球在下边
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
					}else{//小球在左边
						if(mPosY < SpointT){//小球在上边
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
						}else{//小球在下边						
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
		//判断是否小球与墙壁碰撞了
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
		//重力感应器获得的xyz的数值
		public void onSensorChanged(SensorEvent event) {
		    mGX = event.values[SensorManager.DATA_X];
		    mGY = event.values[SensorManager.DATA_Y];
		    mGZ = event.values[SensorManager.DATA_Z];
		}
    }
    /**  
	* 接受消息,处理消息 ,此Handler会与当前主线程一块运行  
	* */ 
	class MyHandler extends Handler {  
	    public MyHandler() {  
	    }  
	    public MyHandler(Looper L) {  
	    	super(L);  
		}  
	    // 子类必须重写此方法,接受数据  
	    @Override 
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub  
	        Log.d("test", "handleMessage......");  
	        super.handleMessage(msg);  
	        // 此处可以更新UI  
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
		/**拿到名称是SHARED_MAIN 的SharedPreferences对象**/
    	mShared = getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
    	/**拿到SharedPreferences中保存的数值 第二个参数为如果SharedPreferences中没有保存就赋一个默认值**/
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
    	/**开始保存入SharedPreferences**/
    	Editor editor = mShared.edit();
    	for(int i = 1;i <= RANK_NUMBER;i++){
    		editor.putInt(KEY_NAME + i, data_score[i - 1]);
    	}    	
    	/**put完毕必需要commit()否则无法保存**/
    	editor.commit();
	}
	//游戏失败的对话框
	private void showFailDialog(int Dscore, int Dlevel){
		AlertDialog.Builder builder = new AlertDialog.Builder(BallGameView.this);
		builder.setTitle("本轮游戏失败！");
		builder.setIcon(R.drawable.ball1);
		builder.setMessage("游戏累计得分：" + Dscore + "\n当前等级" + Dlevel + "\n是否重新挑战？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub			
				mAnimView.level = 1;
				mAnimView.score = 0;
				mAnimView.life = 3;
				mAnimView.reset();
			}			
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(BallGameView.this, MainGameMenu.class);
				startActivity(mIntent);
			}
		});
		builder.create().show();
	}
	//游戏胜利后的对话框
	private void showWinDialog(int Dscore, int Dlevel){
		AlertDialog.Builder builder = new AlertDialog.Builder(BallGameView.this);
		builder.setTitle("本轮游戏胜利！");
		builder.setIcon(R.drawable.ball1);
		if(Dlevel < 5){
			builder.setMessage("游戏累计得分：" + Dscore + "\n当前完成等级" + Dlevel + "\n是否继续挑战？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub			
					
					mAnimView.level++;					
					mAnimView.reset();
				}			
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(BallGameView.this, MainGameMenu.class);
					startActivity(mIntent);
				}
			});
		}else if(Dlevel == 5){
			builder.setMessage("游戏累计得分：" + Dscore + "\n当前完成等级" + Dlevel + "\n   恭喜达人，已通关！\n请返回主菜单");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
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