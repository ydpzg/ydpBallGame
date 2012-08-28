package com.ydp.ballgame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class Instruction extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// 全屏显示窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置背景图片
		setBackgroudPic();
		setContentView(R.layout.instruction);
		TextView title = (TextView)findViewById(R.id.instruction_title);
		title.setText("游戏说明");
		TextView text = (TextView)findViewById(R.id.instruction_text);
		text.setText("使用重力感应控制木板的移动，小球每次撞到方块就加100分，" +
				"此游戏一共有5个等级，等级越高越难。特别的，不能让小球掉到屏幕的下方，" +
				"一旦掉到下方，就Game Over了。");
		super.onCreate(savedInstanceState);
    }
    //设置背景图片
    private void setBackgroudPic(){
    	Bitmap backg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);			
		backg = Bitmap.createScaledBitmap(backg, getWindowManager().getDefaultDisplay().getWidth(), 
				getWindowManager().getDefaultDisplay().getHeight(), true);
		getWindow().setBackgroundDrawable(new BitmapDrawable(backg));
    }
}
