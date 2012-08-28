package com.ydp.ballgame;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class ImageButton {
  
    /**��ťͼƬ**/
    private Bitmap mBitButton = null;
    
    /**ͼƬ���Ƶ�XY����**/
    private int mPosX =0;
    private int mPosY =0;
    
    /**ͼƬ���ƵĿ��**/
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
     * ����ͼƬ��ť
     * @param canvas
     * @param paint
     */
    public void DrawImageButton(Canvas canvas, Paint paint) {
    	canvas.drawBitmap(mBitButton, mPosX, mPosY, paint);
    }
    
    /**
     * �ж��Ƿ����ͼƬ��ť
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
     * ��ȡͼƬ��Դ
     * @param context
     * @param resId
     * @return
     */
    public void ReadBitMap(Context context, int resId) {  	
    	mBitButton = BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
