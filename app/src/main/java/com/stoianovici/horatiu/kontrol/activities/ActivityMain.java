package com.stoianovici.horatiu.kontrol.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.utils.BluetoothService;
import com.stoianovici.horatiu.kontrol.utils.Constants;
import com.stoianovici.horatiu.kontrol.views.TouchpadView;


public class ActivityMain extends Activity {
	private TouchpadView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_main);
		
		view = (TouchpadView)findViewById(R.id.view1);
		BluetoothService.getInstance(this, mHandler);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		view.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		view.onResume();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothService.IS_ACTIVE = false;
        BluetoothService.getInstance(null, null).stop();
    }

    /**
	 * The Handler that gets information back from the BluetoothService
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case BluetoothService.STATE_LISTEN:
						case BluetoothService.STATE_NONE:
							finish();
							BluetoothService.IS_ACTIVE = false;
							break;
					}
					break;
			}
		}
	};
}
