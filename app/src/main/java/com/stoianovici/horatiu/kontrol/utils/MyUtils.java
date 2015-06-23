package com.stoianovici.horatiu.kontrol.utils;

import android.content.Context;
import android.os.Vibrator;

public class MyUtils {
	
	/**
	 * Vibrates the phone
	 * @param ctx - context
	 * @param durationMs - duration of vibration
	 */
	public static void vibrate(Context ctx, int durationMs){
		Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 500 milliseconds
		 v.vibrate(durationMs);
	}
}
