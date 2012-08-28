package com.ydp.ballgame;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class ImageButton {
  
    /**按钮图片**/
    private Bitmap mBitButton = null;
    
    /**图片绘制的XY坐标**/
    private int mPosX =0;
    private int mPosY =0;
    
    /**图片绘制的宽高**/
    private int mWidth =0;
    private int mHeight =0;
  
    public ImageButton(Context context, int frameBitmapID, int x, int y) {
		ReadBitMap(context,frameBitmapID);
		mPosX = x;
		mPosY = y;
		mWidth = mBitButton.getWidth();
		mHeight = mBitButton.getHeight();
    }

    /**
     * 绘制图片按钮
     * @param canvas
     * @param paint
     */
    public void DrawImageButton(Canvas canvas, Paint paint) {
    	canvas.drawBitmap(mBitButton, mPosX, mPosY, paint);
    }
    
    /**
     * 判断是否点中图片按钮
     * @param x
     * @param y
     */
    public boolean IsClick(int x, int y) {
		boolean isClick = false;
		if (x >= mPosX && x <= mPosX + mWidth && y >= mPosY	&& y <= mPosY + mHeight) {
		    isClick = true;
		}
		return isClick;
    }
    
    /**
     * 读取图片资源
     * @param context
     * @param resId
     * @return
     */
    public void ReadBitMap(Context context, int resId) {  	
    	mBitButton = BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
