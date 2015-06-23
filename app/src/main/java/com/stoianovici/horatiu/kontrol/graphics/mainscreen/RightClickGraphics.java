package com.stoianovici.horatiu.kontrol.graphics.mainscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.MotionEvent;

import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.tcpcommunications.HRequest;

public class RightClickGraphics extends HBaseGraphics {
	private RectF rightClickSize;
	private Drawable rightClickButton, rightClickButton_pressed;
	private boolean isRightClickPressed = false;
	private int rightClickTriggeredById = -1;
	
	public RightClickGraphics(Context context){
		rightClickButton = (Drawable)context.getResources().getDrawable(R.drawable.square_rightclick);
		rightClickButton_pressed = (Drawable)context.getResources().getDrawable(R.drawable.square_rightclick_pressed);
	}
	
	@Override
	public boolean onMotionEvent(MotionEvent event, float offset) {
		if(rightClickSize == null){
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
			if (rectWithOffset(rightClickSize, offset).contains((int) x, (int) y)) {
				isRightClickPressed = true;
				rightClickTriggeredById = activePointerId;
				onRightClickDown();
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// if it's the touch that triggered the left/right click
			if (activePointerId == rightClickTriggeredById) {
				isRightClickPressed = false;
				rightClickTriggeredById = -1;
				onRightClickUp();
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas, float offset) {
		if (rightClickSize == null) {
			rightClickSize = new RectF(canvas.getWidth() / 2,
					5 * canvas.getHeight() / 6, canvas.getWidth(),
					canvas.getHeight());
		}

		
		if (isRightClickPressed) {
			drawButton(canvas, rightClickButton_pressed, rectWithOffset(rightClickSize, offset));
		} else {
			drawButton(canvas, rightClickButton, rectWithOffset(rightClickSize, offset));
		}
	}
	
	/**
	 * Right click start
	 */
	private void onRightClickDown() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("mouse-click");
				request.addParameter("2");
				try {
					request.sendPriority();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}

	/**
	 * Right click end
	 */
	private void onRightClickUp() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("mouse-click");
				request.addParameter("3");
				try {
					request.sendPriority();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}
	
	@Override
	public int getContext() {
		return HContext.TouchpadScreen;
	}
}
