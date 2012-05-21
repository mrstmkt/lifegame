package jp.morishi.lifegame.wallpaper;

import jp.morishi.lifegame.wallpaper.ColorPalleteView.OnColorChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

public class PrefActivity extends Activity {
    /** Called when the activity is first created. */
	CheckBox reverseCheckBox;
	SeekBar speedSeekBar;
	SharedPreferences setting;
	SharedPreferences.Editor editor;
	ColorView colorView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref);
        
        setting = PreferenceManager.getDefaultSharedPreferences(this);
        int baseColor = setting.getInt("colorInt", Color.WHITE);
        boolean reverse = setting.getBoolean("reverse", false);
        int speed = setting.getInt("speed", 5);
        int shape = setting.getInt("shape", LifegameCalc.BackPattern.PLAIN);
        reverseCheckBox = (CheckBox)findViewById(R.id.reverseCheckBox);
        reverseCheckBox.setChecked(reverse);
        reverseCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = setting.edit();
				editor.putBoolean("reverse", reverseCheckBox.isChecked());
				editor.commit();
			}
		});
        speedSeekBar = (SeekBar)findViewById(R.id.speedSeekBar);
        if(speed > speedSeekBar.getMax())
        {
        	speed = speedSeekBar.getMax();
        }
        speedSeekBar.setProgress(speed);
        speedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt("speed", speedSeekBar.getProgress());
				editor.commit();
			}
		});
        ColorPalleteView pallete = new ColorPalleteView(this, new OnColorChangedListener() {
			
			public void colorChanged(int color) {
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt("colorInt", color);
				editor.commit();
				
			}

			public void colorTracking(int color) {
				colorView.setColor(color);
			}
		}, baseColor);
        LinearLayout layout = (LinearLayout)findViewById(R.id.colorLayout);
        layout.addView(pallete, 0);
        
        LinearLayout selectColorLayout =(LinearLayout)findViewById(R.id.selectColorLayout);
        colorView = new ColorView(this, baseColor);
        selectColorLayout.addView(colorView);
        colorView.getLayoutParams().width = 60;
        colorView.getLayoutParams().height = 60;
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.PLAIN));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.NOLINE));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.PYRAMID));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.TILE));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.CHECKER));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.LCHECKER));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.HOUNDSTOOTH));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.STRIPE));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.TRIANGLE));
        adapter.add(cnvShapeIntToStr(LifegameCalc.BackPattern.THUMNAILS));
        final Spinner spinner = (Spinner) findViewById(R.id.shapeSpinner);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        spinner.setSelection(shape);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String selected = (String)spinner.getSelectedItem();
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt("shape", cnvShapeStrToInt(selected));
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
        FrameLayout transLayout = (FrameLayout)findViewById(R.id.transLayout);
        transLayout.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
		        Intent intent = new Intent(LifegameWallpaperService.INTENT_CONFIG);
		        intent.putExtra(LifegameWallpaperService.EXTRA_NAME_COMMAND, LifegameWallpaperService.EXTRA_CONFIG_TOUCH);
		        sendBroadcast(intent);
				return false;
			}
		});
        Intent intent = new Intent(LifegameWallpaperService.INTENT_CONFIG);
        intent.putExtra(LifegameWallpaperService.EXTRA_NAME_COMMAND, LifegameWallpaperService.EXTRA_CONFIG_START);
        sendBroadcast(intent);
        Intent intent2 = new Intent(LifegameWallpaperService.INTENT_CONFIG);
        intent2.putExtra(LifegameWallpaperService.EXTRA_NAME_COMMAND, LifegameWallpaperService.EXTRA_CONFIG_TOUCH);
        sendBroadcast(intent2);
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent(LifegameWallpaperService.INTENT_CONFIG);
        intent.putExtra(LifegameWallpaperService.EXTRA_NAME_COMMAND, LifegameWallpaperService.EXTRA_CONFIG_END);
        sendBroadcast(intent);
    	super.onDestroy();
    }
    private int cnvShapeStrToInt(String shape) {
    	if(shape.equals("Plain")){
    		return LifegameCalc.BackPattern.PLAIN;
    	}
    	else if (shape.equals("No Line")){
    		return LifegameCalc.BackPattern.NOLINE;
    	}
    	else if (shape.equals("Pyramid")){
    		return LifegameCalc.BackPattern.PYRAMID;
    	}
    	else if (shape.equals("Tile")){
    		return LifegameCalc.BackPattern.TILE;
    	}
    	else if (shape.equals("Checker(S)")){
    		return LifegameCalc.BackPattern.CHECKER;
    	}
    	else if (shape.equals("Checker(L)")){
    		return LifegameCalc.BackPattern.LCHECKER;
    	}
    	else if (shape.equals("HoundsTooth")){
    		return LifegameCalc.BackPattern.HOUNDSTOOTH;
    	}
    	else if (shape.equals("Stripe")){
    		return LifegameCalc.BackPattern.STRIPE;
    	}
    	else if (shape.equals("Triangle")){
    		return LifegameCalc.BackPattern.TRIANGLE;
    	}
    	else if (shape.equals("Thumnails")){
    		return LifegameCalc.BackPattern.THUMNAILS;
    	}
    	else {
    		return LifegameCalc.BackPattern.PLAIN;
    	}
    }
    private String cnvShapeIntToStr(int shape) {
    	switch(shape) {
    	case LifegameCalc.BackPattern.PLAIN:
    		return "Plain";
    	case LifegameCalc.BackPattern.NOLINE:
    		return "No Line";
    	case LifegameCalc.BackPattern.PYRAMID:
    		return "Pyramid";
    	case LifegameCalc.BackPattern.TILE:
    		return "Tile";
    	case LifegameCalc.BackPattern.CHECKER:
    		return "Checker(S)";
    	case LifegameCalc.BackPattern.LCHECKER:
    		return "Checker(L)";
    	case LifegameCalc.BackPattern.HOUNDSTOOTH:
    		return "HoundsTooth";
    	case LifegameCalc.BackPattern.STRIPE:
    		return "Stripe";
    	case LifegameCalc.BackPattern.TRIANGLE:
    		return "Triangle";
    	case LifegameCalc.BackPattern.THUMNAILS:
    		return "Thumnails";
		default:
    		return "Plain";
    	}
    }
}