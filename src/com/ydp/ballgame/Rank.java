package com.ydp.ballgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Rank extends Activity {
	   
    /**ʹ��SharedPreferences ���������ȡ����**/
    SharedPreferences mShared = null;

    /**�����п���ͬʱ���ڶ��SharedPreferences���ݣ� ����SharedPreferences�����ƾͿ����õ�����**/
    public final static String SHARED_MAIN = "ydp_BallGame";
   
    /**SharedPreferences�д������ݵ�Key����**/
    public final static String KEY_NAME = "Ball_score";
    
    /**SharedPreferences�д������ݵ�·��**/
    public final static String DATA_URL = "/data/data/";
    public final static String SHARED_MAIN_XML = "main.xml";
    
    /**SharedPreferences�д���÷ֵ�ֵ**/
    private int []data_score;
    
    ListView mListView = null;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    
    /**���а���ʾ������**/
    private final static int RANK_NUMBER = 5;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// ȫ����ʾ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//����
		getWindow().setBackgroundDrawableResource(R.drawable.bg);
		
		setContentView(R.layout.rank);
		mListView = (ListView)findViewById(R.id.rank_lv);
		
		/**�õ�������SHARED_MAIN ��SharedPreferences����**/
		mShared = getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
		
		data_score = new int[6];
		for(int i = 1;i <= RANK_NUMBER;i++){
			/**�õ�SharedPreferences�б������ֵ �ڶ�������Ϊ���SharedPreferences��û�б���͸�һ��Ĭ��ֵ**/
			data_score[i - 1] = mShared.getInt(KEY_NAME + i, -1);
		}
		//����һЩҪ��ʾ�����Ķ���
		for(int i = 0;i < RANK_NUMBER;i++){
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("image", R.drawable.reward);
			item.put("number", i + 1);
			if(data_score[i] == -1){
				item.put("score", "û�д˼�¼");	
			}else{
				item.put("score", data_score[i]);
			}
			mData.add(item);
		}
		//����һ��listview
		mListView.setAdapter(new SimpleAdapter(this, mData, R.layout.rank_listview, 
				new String[]{"image", "number", "score"}, 
				new int[]{R.id.L_image, R.id.L_number, R.id.L_score}));
		super.onCreate(savedInstanceState);
    }
}
