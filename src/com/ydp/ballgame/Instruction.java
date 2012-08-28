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
    	// ȫ����ʾ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//���ñ���ͼƬ
		setBackgroudPic();
		setContentView(R.layout.instruction);
		TextView title = (TextView)findViewById(R.id.instruction_title);
		title.setText("��Ϸ˵��");
		TextView text = (TextView)findViewById(R.id.instruction_text);
		text.setText("ʹ��������Ӧ����ľ����ƶ���С��ÿ��ײ������ͼ�100�֣�" +
				"����Ϸһ����5���ȼ����ȼ�Խ��Խ�ѡ��ر�ģ�������С�������Ļ���·���" +
				"һ�������·�����Game Over�ˡ�");
		super.onCreate(savedInstanceState);
    }
    //���ñ���ͼƬ
    private void setBackgroudPic(){
    	Bitmap backg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);			
		backg = Bitmap.createScaledBitmap(backg, getWindowManager().getDefaultDisplay().getWidth(), 
				getWindowManager().getDefaultDisplay().getHeight(), true);
		getWindow().setBackgroundDrawable(new BitmapDrawable(backg));
    }
}
