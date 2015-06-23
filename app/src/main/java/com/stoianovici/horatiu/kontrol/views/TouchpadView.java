package com.stoianovici.horatiu.kontrol.views;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.stoianovici.horatiu.kontrol.animating.AnimationTransitions;
import com.stoianovici.horatiu.kontrol.carpeli.EditableAccomodatingLatinIMETypeNullIssues;
import com.stoianovici.horatiu.kontrol.carpeli.InputConnectionAccomodatingLatinIMETypeNullIssues;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.graphics.mainscreen.KeyboardButtonSwitch;
import com.stoianovici.horatiu.kontrol.graphics.mainscreen.LeftClickGraphics;
import com.stoianovici.horatiu.kontrol.graphics.mainscreen.PresentationModeSwitch;
import com.stoianovici.horatiu.kontrol.graphics.mainscreen.RightClickGraphics;
import com.stoianovici.horatiu.kontrol.graphics.mainscreen.TouchpadGraphics;
import com.stoianovici.horatiu.kontrol.graphics.presentationscreen.NormalModeSwitch;
import com.stoianovici.horatiu.kontrol.graphics.presentationscreen.PresentationNext;
import com.stoianovici.horatiu.kontrol.graphics.presentationscreen.PresentationPrevious;
import com.stoianovici.horatiu.kontrol.graphics.presentationscreen.PresentationStartStop;
import com.stoianovici.horatiu.kontrol.tcpcommunications.HRequest;

public class TouchpadView extends SurfaceView implements Runnable {
	double offset = 0;
	private float targetOffset = 0;
	double startingOffset = 0;
	private volatile boolean isTransitioning = false;
	@SuppressWarnings("unused")
	private int canvasWidth, canvasHeight;
	private volatile long transitionStartTime = 0;
	private static final long transitionDuration = 500000000;
	private int transitionFrames;
	private SurfaceHolder myHolder;
	private boolean isRunning = false;
	//private int currentContext = HContext.TouchpadScreen;

	ArrayList<HBaseGraphics> graphicElements = new ArrayList<HBaseGraphics>();
	ArrayList<HBaseGraphics> drawGraphicElements, eventGraphicElements;

	public TouchpadView(Context context) {
		super(context);

		init(context);
	}

	public TouchpadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public TouchpadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}

	public void onPause(){
		isRunning = false;
	}

	public void onResume(){
		if(!isRunning){
			isRunning = true;
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		while (isRunning) {

			if (!myHolder.getSurface().isValid())
				continue;
			Canvas canvas = myHolder.lockCanvas();
			if(canvas == null){
				continue;
			}
			canvas.drawColor(Color.BLACK);

			canvasWidth = canvas.getWidth();
			canvasHeight = canvas.getHeight();

			if(isTransitioning){
				long currentElapsed = System.nanoTime() - transitionStartTime;
				double percentage = currentElapsed * 1.0f / transitionDuration;
				if(percentage >= 1.0f){
					isTransitioning = false;
					Log.d("Kontrol", String.valueOf(transitionFrames));
					offset = targetOffset;
				}
				else {
					offset = AnimationTransitions.EaseIn(currentElapsed, startingOffset, targetOffset, transitionDuration);
					transitionFrames++;
				}
			}

			for (HBaseGraphics element : drawGraphicElements) {
				float myOffset = (float)offset;
				switch(element.getContext())
				{
				case HContext.Presentation:
					myOffset -= canvasHeight;
					break;
				case HContext.TouchpadScreen:
					break;
				}
				element.draw(canvas, (float)myOffset);
			}
			myHolder.unlockCanvasAndPost(canvas);

		}
	}
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		  InputConnectionAccomodatingLatinIMETypeNullIssues baseInputConnection = 
		    new InputConnectionAccomodatingLatinIMETypeNullIssues(this, false);
		   outAttrs.actionLabel = null;
		   outAttrs.inputType = InputType.TYPE_NULL;
		   outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;

		   return baseInputConnection;	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		for(HBaseGraphics element : eventGraphicElements){
			float myOffset = (float)offset;
			switch(element.getContext())
			{
			case HContext.Presentation:
				myOffset -= canvasHeight;
				break;
			case HContext.TouchpadScreen:
				break;
			}
			if(element.onMotionEvent(event, myOffset)){
				break;
			}
		}

		invalidate();
		return true;
	}

	/**
	 * Initializes items depended to context or generally used
	 * 
	 * @param context
	 */
	private void init(Context context) {
		graphicElements.add(new LeftClickGraphics(context));
		graphicElements.add(new RightClickGraphics(context));
		graphicElements.add(new KeyboardButtonSwitch(context, this));
		graphicElements.add(new TouchpadGraphics(context));
		graphicElements.add(new PresentationModeSwitch(context,  this));
		graphicElements.add(new NormalModeSwitch(context, this));		
		graphicElements.add(new PresentationStartStop(context));
		graphicElements.add(new PresentationNext(context)); 
		graphicElements.add(new PresentationPrevious(context));
		
		drawGraphicElements = new ArrayList<HBaseGraphics>(graphicElements);
		eventGraphicElements = new ArrayList<HBaseGraphics>(graphicElements);

		Collections.sort(drawGraphicElements, new Comparator<HBaseGraphics>() {

			@Override
			public int compare(HBaseGraphics lhs, HBaseGraphics rhs) {
				return lhs.getDrawZIndex() - rhs.getDrawZIndex();
			}
		});

		Collections.sort(eventGraphicElements, new Comparator<HBaseGraphics>(){
			@Override
			public int compare(HBaseGraphics lhs, HBaseGraphics rhs) {
				return rhs.getTouchEventZIndex() - lhs.getTouchEventZIndex();
			}
		});

		myHolder = getHolder();
	}

	/**
	 * Swithces to a different screen animated
	 */
	public void switchToContext(int context){
		switch(context)
		{
		case HContext.TouchpadScreen:
			startTransition(0, transitionDuration);
			break;

		case HContext.Presentation:
			startTransition(canvasHeight + 1, transitionDuration);
			break;
		}
	}

	/**
	 * Animated transition
	 * @param target - the target offset
	 * @param time - time of the transition
	 */
	private void startTransition(float target, long time){
		startingOffset = offset;
		targetOffset = target;
		transitionFrames = 0;
		transitionStartTime = System.nanoTime();
		isTransitioning = true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		List<String> params = new ArrayList<String>();
		int keyCode;
		if(event.getAction() == KeyEvent.ACTION_UP){
			if(event.isPrintingKey()){
				keyCode = event.getUnicodeChar(event.getMetaState());
			}
			else if(event.getKeyCode() == KeyEvent.KEYCODE_DEL){
				keyCode = 8;
			}
			else if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
				keyCode = 13;
			}
			else if(event.getKeyCode() == KeyEvent.KEYCODE_SPACE){
				keyCode = 32;
			}
			else {
				return false;
			}
			if(event.getUnicodeChar() == 
		       (int)EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER.charAt(0))
		     {
		       //We are ignoring this character, and we want everyone else to ignore it, too, so 
		       // we return true indicating that we have handled it (by ignoring it).   
		       return true; 
		     }
			params.add(String.valueOf(keyCode));
			final HRequest req = new HRequest("keyboard", params);
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						req.sendPriority();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
		}
		else {
			Log.d("Kontrol", "key event unknown");
		}
		return super.dispatchKeyEvent(event);
	}
}
