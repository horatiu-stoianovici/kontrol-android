package com.stoianovici.horatiu.kontrol.graphics.mainscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.views.TouchpadView;


public class PresentationModeSwitch extends HBaseGraphics {

	private Drawable buttonDrawable, pressedDrawable;
	private RectF position;
	private final int size = 100;
	private int pressedId = -1;
	private TouchpadView delegate;

	public PresentationModeSwitch(Context context, TouchpadView view){
		buttonDrawable = context.getResources().getDrawable(R.drawable.presentation);
		pressedDrawable = context.getResources().getDrawable(R.drawable.presentation_pressed);
		delegate = view;
		view.setFocusableInTouchMode(true);
	}

	@Override
	public boolean onMotionEvent(MotionEvent event, float offset) {
		if(position == null){
			return false;
		}
		// Get the pointer ID
		int activePointerIndex = event.getActionIndex();
		int activePointerId = event.getPointerId(activePointerIndex);
		float x, y;

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			x = event.getX(activePointerIndex);
			y = event.getY(activePointerIndex);

			// if the current touch is inside the left click button
			if (rectWithOffset(position, offset).contains((int) x, (int) y)) {
				pressedId = activePointerId;
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if(activePointerId == pressedId){
				pressedId = -1;
				delegate.switchToContext(HContext.Presentation);
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas, float offset) {
		if(position == null){
			position = new RectF((canvas.getWidth() - 2*size) / 4 , 0,(canvas.getWidth() + 2*size) / 4 , size);
		}
		
		RectF pos = rectWithOffset(position, offset);
		
		pressedDrawable.setBounds((int)pos.left, (int)pos.top, (int)pos.right, (int)pos.bottom);
		buttonDrawable.setBounds((int)pos.left, (int)pos.top, (int)pos.right, (int)pos.bottom);
		if(pressedId == -1){
			buttonDrawable.draw(canvas);
		}
		else{
			pressedDrawable.draw(canvas);
		}
	}
	 @Override
	public int getDrawZIndex() {
		return 100;
	}
	 
	@Override
	public int getTouchEventZIndex() {
		return 100;
	}
	
	@Override
	public int getContext() {
		return HContext.TouchpadScreen;
	}
	
}
