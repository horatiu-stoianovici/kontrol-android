package com.stoianovici.horatiu.kontrol.graphics.presentationscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.utils.PresentationController;


public class PresentationPrevious extends HBaseGraphics {

	private Drawable buttonDrawable, pressedDrawable;
	private RectF position;
	private final int size = 200;
	private int pressedId = -1;
	private Context context;

	public PresentationPrevious(Context context){
		buttonDrawable = context.getResources().getDrawable(R.drawable.down_arrow);
		pressedDrawable = context.getResources().getDrawable(R.drawable.down_arrow_pressed);
		this.context = context;
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

				PresentationController.getInstance(context).previous();
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas, float offset) {
		if(position == null){
			position = new RectF((canvas.getWidth()-size)/2 , canvas.getHeight()*3/4 - size/2 ,(canvas.getWidth()+size)/2 , canvas.getHeight()*3/4 + size/2);
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
		return HContext.Presentation;
	}

}
