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
	   
    /**使用SharedPreferences 来储存与读取数据**/
    SharedPreferences mShared = null;

    /**程序中可以同时存在多个SharedPreferences数据， 根据SharedPreferences的名称就可以拿到对象**/
    public final static String SHARED_MAIN = "ydp_BallGame";
   
    /**SharedPreferences中储存数据的Key名称**/
    public final static String KEY_NAME = "Ball_score";
    
    /**SharedPreferences中储存数据的路径**/
    public final static String DATA_URL = "/data/data/";
    public final static String SHARED_MAIN_XML = "main.xml";
    
    /**SharedPreferences中储存得分的值**/
    private int []data_score;
    
    ListView mListView = null;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    
    /**排行榜显示的行数**/
    private final static int RANK_NUMBER = 5;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// 全屏显示窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//背景
		getWindow().setBackgroundDrawableResource(R.drawable.bg);
		
		setContentView(R.layout.rank);
		mListView = (ListView)findViewById(R.id.rank_lv);
		
		/**拿到名称是SHARED_MAIN 的SharedPreferences对象**/
		mShared = getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
		
		data_score = new int[6];
		for(int i = 1;i <= RANK_NUMBER;i++){
			/**拿到SharedPreferences中保存的数值 第二个参数为如果SharedPreferences中没有保存就赋一个默认值**/
			data_score[i - 1] = mShared.getInt(KEY_NAME + i, -1);
		}
		//遍历一些要显示出来的东西
		for(int i = 0;i < RANK_NUMBER;i++){
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("image", R.drawable.reward);
			item.put("number", i + 1);
			if(data_score[i] == -1){
				item.put("score", "没有此记录");	
			}else{
				item.put("score", data_score[i]);
			}
			mData.add(item);
		}
		//定义一个listview
		mListView.setAdapter(new SimpleAdapter(this, mData, R.layout.rank_listview, 
				new String[]{"image", "number", "score"}, 
				new int[]{R.id.L_image, R.id.L_number, R.id.L_score}));
		super.onCreate(savedInstanceState);
    }
}
