package jp.morishi.lifegame.wallpaper;

//import android.graphics.Canvas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LifegameWallpaperService extends WallpaperService {
	public static final String INTENT_CONFIG = "jp.morishi.filegame.INTENT_CONFIG";
	public static final String EXTRA_NAME_COMMAND = "command";
	public static final String EXTRA_CONFIG_START = "Start";
	public static final String EXTRA_CONFIG_END = "End";
	public static final String EXTRA_CONFIG_TOUCH = "Touch";

	@Override
	public Engine onCreateEngine() {
		getApplicationContext();
		return new LifegameEngine();
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	class LifegameEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		
		Thread lifeThread;
		LifegameCalc calc;
		LifegameEngine() {
			calc = null;
			lifeThread = null;
			SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			setting.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(setting, "colorInt");
			onSharedPreferenceChanged(setting, "speed");
			onSharedPreferenceChanged(setting, "reverse");
			onSharedPreferenceChanged(setting, "shape");
		}
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(broadcastReceiver_, intentFilter);
			IntentFilter configIntentFilter = new IntentFilter();
			configIntentFilter.addAction(INTENT_CONFIG);
			registerReceiver(configBroadcastReceiver_, configIntentFilter);
			super.onCreate(surfaceHolder);
		}
		@Override
		public void onDestroy() {
			if(calc != null) {
				calc.stopThread();
				calc.recycleBitmapList();
			}
			unregisterReceiver(broadcastReceiver_);
			unregisterReceiver(configBroadcastReceiver_);
			super.onDestroy();
		}
		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if(calc != null) {
				if(visible == true) {
					calc.setPause(false);
				}
				else {
					calc.setPause(true);
				}
			}
		}
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			if(calc == null) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				int baseColor = sharedPref.getInt("colorInt", Color.WHITE);
				int speed = sharedPref.getInt("speed", 5);
				if(speed > 10) {
					speed = 10;
				}
				boolean reverse = sharedPref.getBoolean("reverse", false);
				int shape = sharedPref.getInt("shape", LifegameCalc.BackPattern.PLAIN);
				int pxSize = 20;
				int bitWidth =  width/pxSize;
				bitWidth = bitWidth +  (8 - bitWidth % 8);
				int bitHeight = height/pxSize;
				bitHeight = bitHeight +  (8 - bitHeight % 8);
				if(bitWidth < bitHeight) {
					bitWidth = bitHeight;
				}
				else {
					bitHeight = bitWidth;
				}
					
				calc = new LifegameCalc(getApplicationContext(),
										bitWidth, bitHeight, pxSize, 150,
										baseColor, speed,reverse, this);
				calc.setBackPattern(shape);
				calc.initData();
				calc.addRandomPatterns(10);
				
				lifeThread = new Thread(calc);
				lifeThread.start();
			}
		}
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
		}
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
		}
		@Override
		public void onTouchEvent(MotionEvent event) {
			if(calc != null) {
				calc.onTouch(event.getX(), event.getY());
			}
			super.onTouchEvent(event);
		}
		
		private BroadcastReceiver broadcastReceiver_ = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
	                if(calc != null) {
	                	calc.setBattery(intent.getIntExtra("scale", 0), intent.getIntExtra("level", 0));
	                }
	            }
	        }
	    };
	    private BroadcastReceiver configBroadcastReceiver_ = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if(calc != null) {
					String command = intent.getStringExtra("command");
					if(command != null) {
						if(command.equals(EXTRA_CONFIG_START)) {
							calc.setConfigNow(true);
						}
						else if(command.equals(EXTRA_CONFIG_END)) {
							calc.setConfigNow(false);
						}
						else if(command.equals(EXTRA_CONFIG_TOUCH)) {
							calc.onTouch(0, 5);
						}
					}
				}
			}
		};
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if(calc != null) {
				if(key.equals("colorInt")) {
					int color = sharedPreferences.getInt(key, Color.WHITE);
					calc.setBaseColor(color);
					calc.draw();
				}
				else if(key.equals("speed")) {
					int speed = sharedPreferences.getInt(key, 5);
					calc.setSpeed(speed);
				}
				else if(key.equals("reverse")) {
					boolean reverse = sharedPreferences.getBoolean(key, false);
					calc.setReverse(reverse);
					calc.draw();
				}
				else if(key.equals("shape")) {
					int shape = sharedPreferences.getInt(key, LifegameCalc.BackPattern.PLAIN);
					calc.setBackPattern(shape);
					calc.draw();
				}
			}
			
		}
	}

}
