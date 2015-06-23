package com.stoianovici.horatiu.kontrol.graphics;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;


public class CustomTimePickerDialog extends TimePickerDialog{

	public static final int TIME_PICKER_INTERVAL=15;
	private boolean mIgnoreEvent=false;

	public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
	}
}