package com.stoianovici.horatiu.kontrol;

import android.content.Context;
import android.graphics.Typeface;

public class MyFonts {

	public static Typeface lightFont, normalFont, boldFont;
	
	public static void Initialize(
			Context context) {
		lightFont = Typeface.createFromAsset(context.getAssets(), "Fonts/RobotoCondensed-Light.ttf");
		normalFont = Typeface.createFromAsset(context.getAssets(), "Fonts/RobotoCondensed-Regular.ttf");
		boldFont = Typeface.createFromAsset(context.getAssets(), "Fonts/RobotoCondensed-Bold.ttf");
	}
	
}
