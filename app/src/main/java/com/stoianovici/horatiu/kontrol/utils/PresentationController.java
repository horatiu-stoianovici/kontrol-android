package com.stoianovici.horatiu.kontrol.utils;

import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.stoianovici.horatiu.kontrol.tcpcommunications.HRequest;

public class PresentationController {
	private static PresentationController instance = null;
	
	private Date startDate;
	private long duration;
	private boolean isStarted;
	private boolean didVibratePhase1 = false, didVibratePhase2 = false;
	private Context context;
	
	private PresentationController(Context ctx){
		this.context = ctx;
	}
	
	/**
	 * @return - the instance of the singleton
	 */
	public static PresentationController getInstance(Context ctx){
		if(instance == null){
			synchronized(PresentationController.class){
				if(instance == null){
					instance = new PresentationController(ctx);
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Gets the start date of the presentation
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date of the presentation
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the duration of the presentation in milliseconds
	 */
	public long getDurationMs() {
		return duration;
	}
	
	/**
	 * Sets the duration of the presentation
	 */
	public void setDurationMs(long duration) {
		this.duration = duration;
	}
	
	/**
	 * Gets how much time there is until the end of the presentation
	 */
	public long getRemainingTimeMs(){
		long time = duration - (new Date().getTime() - startDate.getTime());
		if(time < 5 * 60 *1000 && !didVibratePhase1){
			didVibratePhase1 = true;
			MyUtils.vibrate(context, 500);
		}
		if(time < 60 * 1000 && !didVibratePhase2){
			didVibratePhase2 = true;
			MyUtils.vibrate(context, 1000);
		}
		return time;
	}
	
	/**
	 * Gets how much time elapsed since the start of the presentation
	 */
	public long getElapsedTimeMs(){
		return (new Date().getTime() - startDate.getTime());
	}
	
	/**
	 * Starts the presentation
	 */
	public void start(){
		startDate = new Date();
		isStarted = true;
		didVibratePhase1 = didVibratePhase2 = false;
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				HRequest req = new HRequest("presentation");
				req.addParameter(String.valueOf(PresentationControls.StartPresentation));
				try {
					req.sendPriority();
				} catch (IOException e) {
					Log.e("Kontrol", e.getStackTrace().toString());
				}
				return null;
			}
			
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * @return - true if the presentation is started
	 */
	public boolean isStarted(){
		return isStarted && (getRemainingTimeMs() > 0);
	}
	
	/**
	 * Next slide/animation
	 */
	public void next(){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				HRequest req = new HRequest("presentation");
				req.addParameter(String.valueOf(PresentationControls.Next));
				try {
					req.sendPriority();
				} catch (IOException e) {
					Log.e("Kontrol", e.getStackTrace().toString());
				}
				return null;
			}
			
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * Previous slide/animation
	 */
	public void previous(){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				HRequest req = new HRequest("presentation");
				req.addParameter(String.valueOf(PresentationControls.Previous));
				try {
					req.sendPriority();
				} catch (IOException e) {
					Log.e("Kontrol", e.getStackTrace().toString());
				}
				return null;
			}
			
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * Stops the presentation
	 */
	public void stop(){
		isStarted = false;
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {HRequest req = new HRequest("presentation");
				req.addParameter(String.valueOf(PresentationControls.StopPresentation));
				try {
					req.sendPriority();
				} catch (IOException e) {
					Log.e("Kontrol", e.getStackTrace().toString());
				}
				return null;
			}
			
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	
	private class PresentationControls{
		public static final int StartPresentation = 0,
				StopPresentation = 1,
				Next = 2,
				Previous = 3;
	}
}
