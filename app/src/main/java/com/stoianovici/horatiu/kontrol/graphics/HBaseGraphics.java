package com.stoianovici.horatiu.kontrol.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

public abstract class HBaseGraphics {
	
	/**
	 * Should return true if it consumed the motion event, false otherwise
	 * @param event = the event object
	 * @return whether the event is consumed or not
	 */
	public abstract boolean onMotionEvent(MotionEvent event, float offset);
	
	/**
	 * Called when the element needs to be drawn
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas, float offset);
	
	
	public static RectF rectWithOffset(RectF r, float offset){
		return new RectF(r.left, r.top + offset, r.right, r.bottom + offset);
	}
	
	public static void drawButton(Canvas canvas, Drawable buttonShape, RectF rect) {
		buttonShape.setBounds(new Rect((int)rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom));
		buttonShape.draw(canvas);
	}
	
	public int getDrawZIndex(){
		return 0;
	}
	
	public int getTouchEventZIndex(){
		return 0;
	}
	
	public abstract int getContext();
}
