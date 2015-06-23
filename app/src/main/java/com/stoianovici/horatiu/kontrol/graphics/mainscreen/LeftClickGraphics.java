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

public class LeftClickGraphics extends HBaseGraphics {
	private RectF leftClickSize;
	private Drawable leftClickButton, leftClickButton_pressed;
	private boolean isLeftClickPressed;
	private int leftClickTriggeredById = -1;

	public LeftClickGraphics(Context context){

		leftClickButton = (Drawable) context.getResources().getDrawable(
				R.drawable.square_leftclick);
		leftClickButton_pressed = (Drawable) context.getResources()
				.getDrawable(R.drawable.square_leftclick_pressed);

	}

	@Override
	public boolean onMotionEvent(MotionEvent event, float offset) {
		if(leftClickSize == null){
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
			if (rectWithOffset(leftClickSize, offset).contains(x, y)) {
				isLeftClickPressed = true;
				leftClickTriggeredById = activePointerId;
				onLeftClickDown();
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// if it's the touch that triggered the left/right click
			if (activePointerId == leftClickTriggeredById) {
				isLeftClickPressed = false;
				leftClickTriggeredById = -1;
				onLeftClickUp();
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas, float offset) {
		if (leftClickSize == null) {
			leftClickSize = new RectF(0, 5 * canvas.getHeight() / 6,
					canvas.getWidth() / 2, canvas.getHeight());
		}

		if (isLeftClickPressed) {
			drawButton(canvas, leftClickButton_pressed, rectWithOffset(leftClickSize, offset));
		} else {
			drawButton(canvas, leftClickButton, rectWithOffset(leftClickSize, offset));
		}
	}


	/**
	 * Left click start
	 */
	private void onLeftClickDown() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("mouse-click");
				request.addParameter("0");
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
	 * Left click end
	 */
	private void onLeftClickUp() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("mouse-click");
				request.addParameter("1");
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
