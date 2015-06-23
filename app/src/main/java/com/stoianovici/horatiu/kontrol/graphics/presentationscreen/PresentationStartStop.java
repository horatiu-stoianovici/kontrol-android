package com.stoianovici.horatiu.kontrol.graphics.presentationscreen;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.TimePicker;

import com.stoianovici.horatiu.kontrol.MyFonts;
import com.stoianovici.horatiu.kontrol.graphics.CustomTimePickerDialog;
import com.stoianovici.horatiu.kontrol.graphics.HBaseGraphics;
import com.stoianovici.horatiu.kontrol.graphics.HContext;
import com.stoianovici.horatiu.kontrol.utils.PresentationController;


public class PresentationStartStop extends HBaseGraphics {

	private RectF position;
	private final int size = 100;
	private int pressedId = -1;
	private int textWidth = 0;
	private Context context;
	private int textWidth1 = 0;

	public PresentationStartStop(Context context){
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
				
				if(PresentationController.getInstance(context).isStarted()){
					PresentationController.getInstance(context).stop();
				}
				else {
					//show dialog to select time
					TimePickerDialog dialog = new TimePickerDialog(context, timeSetListener, 0, 10, true);
					dialog.setTitle("Presentation duration in hours and minutes");
					dialog.show();
				}
				
				return true;
			}
			break;
		}
		return false;
	}
	


	private TimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
		    @Override
		    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				PresentationController.getInstance(context).setDurationMs((hourOfDay * 60 + minute) * 60 * 1000);
				PresentationController.getInstance(context).start();
		    }
	 };
 
	@Override
	public void draw(Canvas canvas, float offset) {
		if(position == null){
			position = new RectF((canvas.getWidth()-size*2)/2 , (canvas.getHeight()-size)/2,(canvas.getWidth()+size*2)/2 , (canvas.getHeight()+size)/2);
		}
		
		//if the presentation is started, paint the remaining time
		if(PresentationController.getInstance(context).isStarted()){
			Paint textPaint = new Paint();
			textPaint.setFlags(Paint.ANTI_ALIAS_FLAG );
			textPaint.setTextSize(canvas.getHeight() / 8 );
			textPaint.setTypeface(MyFonts.lightFont);
			
			//get position text in center
			int xPos = (canvas.getWidth() / 2);
			int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) + (int)offset ; 
		
			double time = PresentationController.getInstance(context).getRemainingTimeMs();
			double minutes = (time/(60 * 1000));
			double seconds = ((time % (60*1000)) / 1000);
			double milliseconds = (int)((time % (60*1000)) % 1000);
		    
			//flash opacity when under 5 minutes / yellow to red when 
			if(minutes < 5){
				if(minutes < 1){
					int r = 255;
					int g = (int)(255 * minutes);
					int b = 0;
					double opacity = milliseconds / 1000;
					opacity = opacity < 0.5 ? opacity / 0.5 : (1 - opacity) / 0.5;
					
					textPaint.setARGB(100 + (int)(155 * opacity), r, g, b);
				}
				else {
					double opacity = milliseconds / 1000;
					opacity = opacity < 0.5 ? opacity / 0.5 : (1 - opacity) / 0.5;
					
					textPaint.setARGB(100 + (int)(155 * opacity), 255, 255, 255);
				}
			}
			else {
				textPaint.setARGB(255, 255, 255, 255);
			}
			//text to draw
		    String toDraw = String.valueOf((int)minutes) + ":" + String.valueOf((int)seconds / 10) + String.valueOf((int)seconds % 10) + ":" + String.valueOf((int)milliseconds / 100) + String.valueOf(((int)milliseconds % 100) / 10) + String.valueOf((int)milliseconds % 10);
		    
		    if(textWidth == 0){
		    	//calculate the position of the text, so that is centered on screen
				Rect textBounds = new Rect();
			    textPaint.getTextBounds(toDraw, 0, toDraw.length(), textBounds);
			    textWidth = textBounds.width();
		    }
		    
		    textWidth1 = 0;
			canvas.drawText(toDraw, xPos - textWidth / 2, yPos, textPaint);
		}
		else {
			Paint textPaint = new Paint();
			textPaint.setFlags(Paint.ANTI_ALIAS_FLAG );
			textPaint.setTextSize(canvas.getHeight() / 9 );
			textPaint.setTypeface(MyFonts.lightFont);
			
			//get position text in center
			int xPos = (canvas.getWidth() / 2);
			int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) + (int)offset ;
			
			if(pressedId == -1){
				textPaint.setARGB(255, 255, 255, 255);
			}
			else {
				textPaint.setARGB(255, 192, 57, 43);
			}
			
			//text to draw
		    String toDraw = "Start";
		    
		    if(textWidth1 == 0){
		    	//calculate the position of the text, so that is centered on screen
				Rect textBounds = new Rect();
			    textPaint.getTextBounds(toDraw, 0, toDraw.length(), textBounds);
			    textWidth1 = textBounds.width();
		    }
			canvas.drawText(toDraw, xPos - textWidth1 / 2, yPos, textPaint);
			textWidth = 0;
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
