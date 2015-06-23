package com.stoianovici.horatiu.kontrol.graphics.mainscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.tcpcommunications.HRequest;
import com.stoianovici.horatiu.kontrol.views.TouchPoint;

public class TouchpadGraphics extends HBaseGraphics {
	private RectF touchpadSize;
	private Drawable touchpad;
	private final int touchRadius = 50;
	private Paint touchPaint;
	private SparseArray<TouchPoint> points = new SparseArray<TouchPoint>();
	private VelocityTracker velocityTracker;
	private GestureDetector gestureDetector;
	private int mouseMoveTriggeredById = -1;

	public TouchpadGraphics(Context context){
		touchpad = (Drawable) context.getResources().getDrawable(
				R.drawable.square_touchpad);

		touchPaint = new Paint();
		touchPaint.setFlags(touchPaint.getFlags() | Paint.ANTI_ALIAS_FLAG);
		touchPaint.setColor(context.getResources().getColor(R.color.red2));
		gestureDetector = new GestureDetector(context, gestureListener);
	}

	@Override
	public boolean onMotionEvent(MotionEvent event, float offset) {
		if(touchpadSize == null)
			return false;

		// Get the pointer ID
		int activePointerIndex = event.getActionIndex();
		int activePointerId = event.getPointerId(activePointerIndex);
		float x, y;
		TouchPoint point;

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			x = event.getX(activePointerIndex);
			y = event.getY(activePointerIndex);

			if(rectWithOffset(touchpadSize, offset).contains((int)x, (int) y)) {
				points.append(activePointerId, new TouchPoint(x, y,
						activePointerId));
				if (points.size() == 1) {
					mouseMoveTriggeredById = activePointerId;
					velocityTracker = VelocityTracker.obtain();
				} else if (points.size() == 2) {
					mouseMoveTriggeredById = -1;
					stopScrolling(); //stops the animated scroll
				}

				gestureDetector.onTouchEvent(event);
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (points.get(activePointerId) != null) {
				points.remove(activePointerId);

				// if only one point remains, make the mouse move
				if (points.size() == 1) {
					point = points.get(points.keyAt(0));
					mouseMoveTriggeredById = point.getId();
					point.refreshUid(); //as if there is a new movement engaged
					velocityTracker.computeCurrentVelocity(10);
					scrollWithVelocity(velocityTracker.getXVelocity(activePointerId),velocityTracker
							.getYVelocity(activePointerId));
				} else if (points.size() == 0) { // if no point remains, do not
					// move/scroll
					mouseMoveTriggeredById = -1;
					velocityTracker.recycle();
					velocityTracker = null;
				}

				gestureDetector.onTouchEvent(event);
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getPointerCount(); i++) {
				if (points.indexOfKey(event.getPointerId(i)) >= 0) {
					point = points.get(event.getPointerId(i));
					point.setX(event.getX(i));
					point.setY(event.getY(i));
				}
			}

			if(velocityTracker != null){
				velocityTracker.addMovement(event);
			}

			// if there is a touch that moves the mouse
			if (mouseMoveTriggeredById != -1) {
				point = points.get(mouseMoveTriggeredById);

				moveMouse((int) point.getX(), (int) point.getY(),
						point.getUid());
			}
			gestureDetector.onTouchEvent(event);
			break;
		}

		return false;
	}

	@Override
	public void draw(Canvas canvas, float offset) {

		if(touchpadSize == null){
			touchpadSize = new RectF(0, 0, canvas.getWidth(),
					5 * canvas.getHeight() / 6);
		}

		drawButton(canvas, touchpad, rectWithOffset(touchpadSize, offset));
		for (int i = 0; i < points.size(); i++) {
			TouchPoint point = points.get(points.keyAt(i));
			canvas.drawCircle(point.getX(), point.getY(), touchRadius, touchPaint);
		}
	}

	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		public boolean onSingleTapUp(MotionEvent e) {
			onLeftClickDown();
			onLeftClickUp();
			return true;
		};

		public boolean onDoubleTap(MotionEvent e) {
			onSingleTapUp(e);
			return true;
		};

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			int pcount = 0;
			for(int i = 0; i < e2.getPointerCount(); i++){
				if(points.get(e2.getPointerId(i)) != null)
					pcount++;
			}
			if(pcount > 1){
				scroll(distanceX, distanceY);
				return true;
			}
			return false;
		};

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return true;
		};
	};

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


	/**
	 * Sends request that moves the mouse
	 * 
	 * @param posX
	 * @param posY
	 */
	private void moveMouse(final int posX, final int posY, final String uid) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("mouse-move");
				request.addParameter(String.valueOf(posX));
				request.addParameter(String.valueOf(posY));
				request.addParameter(uid);
				try {
					request.sendNonPriority();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}

	private void scrollWithVelocity(final float xVelocity, final float yVelocity) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("scroll");
				request.addParameter(String.valueOf(2));
				request.addParameter(String.valueOf(xVelocity));
				request.addParameter(String.valueOf(yVelocity));
				try {
					request.sendPriority();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}

	private void scroll(final float amountX, final float amountY) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("scroll");
				request.addParameter(String.valueOf(1));
				request.addParameter(String.valueOf(amountX));
				request.addParameter(String.valueOf(amountY));
				try {
					request.sendNonPriority();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}

	private void stopScrolling(){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HRequest request = new HRequest("scroll");
				request.addParameter(String.valueOf(3));
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
