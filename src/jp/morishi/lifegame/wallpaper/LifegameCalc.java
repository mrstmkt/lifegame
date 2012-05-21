package jp.morishi.lifegame.wallpaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.MediaStore;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.Log;
import android.view.SurfaceHolder;


public class LifegameCalc implements Runnable{
	private int _prevCount;
	private List<byte[]> _prevDataList;
	private int _width;
	private int _height;
	private byte[] _data;
	private Context context;
	private Engine _engine;
	private int _interval;
	private int _pxSize;
	private boolean _threadStop = false;
	private boolean _threadPause = false;
	private int _state;
	private int _battery;
	private int _baseColor;
	private Path _linePath;
	private BackPattern _backPattern;
	private int _speed;
	private boolean _reverse;
	private static Object syncObj = new Object();
	private boolean _configNow;
	private List<Long> _thumnailIds;
	private List<Bitmap> _bitmapList;
	private static HashMap<String, Integer> colorMap = new HashMap<String, Integer>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("White", Color.WHITE);
			put("Magenta", Color.MAGENTA);
			put("Cyan", Color.CYAN);
			put("Black", Color.BLACK);
			put("Blue", Color.BLUE);
			put("DarkGray", Color.DKGRAY);
			put("Gray", Color.GRAY);
			put("Green", Color.GREEN);
			put("LightGray", Color.LTGRAY);
			put("Red", Color.RED);
			put("Yellow", Color.YELLOW);
		}
	};
	public static int getColorFromString(String key) {
		if(colorMap.containsKey(key)) {
			return colorMap.get(key);
		}
		else
		{
			return Color.WHITE;
		}
	}
	public LifegameCalc(Context context, int width, int height, int pxSize, int interval, int baseColor,int speed,boolean reverse, Engine e)  {
		this._engine = e;
		this.context = context;
		this._prevCount = 3;
		this._prevDataList = new ArrayList<byte[]>();
		this._width = width - (width % 8);
		this._height = height;
		this._pxSize = pxSize;
		this._data = new byte[(_width >> 3) * height];
		this._threadStop = false;
		this._threadPause = false;
		this._linePath = null;
		setState(0);
		this._battery = 100;
		this._speed = speed;
		this._reverse = reverse;
		this.setConfigNow(false);
		this._backPattern = new BackPattern(BackPattern.PLAIN, this._width, this._height);
		loadThumbnailIds();
		loadThumbnailBitmap();
		setBaseColor(baseColor);
		makeLinePath();
		this.setInterval(interval);
	}
	private void loadThumbnailIds() {
		this._thumnailIds = new ArrayList<Long>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, null, null, null);
		while(cursor.moveToNext()) {
			int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
			this._thumnailIds.add(cursor.getLong(fieldIndex));
		}
		cursor.close();
	}
	private void loadThumbnailBitmap() {
		if(this._thumnailIds.size() == 0) {
			return;
		}
		if(this._bitmapList != null){
			for(int i = 0; i < this._bitmapList.size(); i++) {
				this._bitmapList.get(i).recycle();
			}
			this._bitmapList.clear();
		}
		else {
			this._bitmapList = new ArrayList<Bitmap>();
		}
		Random rand = new Random();
		for(int i = 0; i< this._height; i = i + 4) {
			for(int j = 0; j< this._height; j = j + 4) {
				int index = rand.nextInt(this._thumnailIds.size());
				Bitmap bmp =  MediaStore.Images.Thumbnails.getThumbnail(this.context.getContentResolver(), this._thumnailIds.get(index),
														MediaStore.Images.Thumbnails.MICRO_KIND,
														null);
				int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
				bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
				for(int k = 0; k < pixels.length; k++) {
					pixels[k] = Color.argb(128, Color.red(pixels[k]), Color.green(pixels[k]), Color.blue(pixels[k]));
				}
				Bitmap bmp2 = bmp.copy(Bitmap.Config.ARGB_8888, true);
				bmp2.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
				bmp.recycle();
				bmp = null;
				this._bitmapList.add(bmp2);
			}
		}
	}
	private void drawThumbnailBitmap(Canvas c) {
		if(this._bitmapList == null || this._bitmapList.size() == 0) {
			return;
		}
		int index = 0;
		
		for(int i = 0; i< this._height; i = i + 4) {
			for(int j = 0; j< this._height; j = j + 4) {
				c.save();
				Bitmap bmp = this._bitmapList.get(index);
				Path roundRectPath = new Path();
				roundRectPath.addRoundRect(new RectF( j * _pxSize + 2, i * _pxSize + 2, j * _pxSize + _pxSize * 4 - 2, i * _pxSize + _pxSize * 4 - 2), 4, 4, Path.Direction.CW);
				c.clipPath(roundRectPath);
				c.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()),
						new RectF( j * _pxSize, i * _pxSize, j * _pxSize + 4 * _pxSize, i * _pxSize + 4* _pxSize), null);
				c.restore();
				index = (index + 1) % this._bitmapList.size();
			}
		}
	}
	public void recycleBitmapList() {
		if(this._bitmapList != null){
			for(int i = 0; i < this._bitmapList.size(); i++) {
				this._bitmapList.get(i).recycle();
			}
			this._bitmapList.clear();
		}
	}
	public void initData(){
		synchronized (syncObj) {
			for(int j = 0; j < this._data.length; j++) {
				this._data[j] = 0;
			}
		}
		setState(0);
	}
	public byte[] getData() {
		return _data;
	}
	public void pushPrevData(byte[] data) {
		this._prevDataList.add(0, data);
		if(this._prevDataList.size() > this._prevCount) {
			this._prevDataList.remove(_prevDataList.size() - 1);
		}
	}
	public byte[] cloneData(byte[] data) {
		if(data != null) {
			return data.clone();
		}
		else {
			return null;
		}
	}
	public void setData(byte[] data) {
		byte[] prevData = this.cloneData(this._data);
		if(prevData == null) {
			return;
		}
		this.pushPrevData(prevData);
		for(int i = 0; i < this._data.length && i < data.length; i++) {
			this._data[i] = data[i];
		}
		setState(0);
	}
	public void setOrData(byte[] data) {
		byte[] prevData = this.cloneData(this._data);
		if(prevData == null) {
			return;
		}
		this.pushPrevData(prevData);
		for(int i = 0; i < this._data.length && i < data.length; i++) {
			this._data[i] =(byte)( this._data[i] | data[i]);
		}
		setState(0);
	}
	public void setAndData(byte[] data) {
		byte[] prevData = this.cloneData(this._data);
		if(prevData == null) {
			return;
		}
		this.pushPrevData(prevData);
		for(int i = 0; i < this._data.length && i < data.length; i++) {
			this._data[i] = (byte)(this._data[i] & data[i]);
		}
		setState(0);
	}
	public void calc() {
		synchronized (syncObj) {
			byte[] prevData = cloneData(this._data);
			if(prevData == null) {
				return;
			}
			pushPrevData(prevData);
			byte[] data = nextLifeArray(this._data, this._width, this._height);
			this._data = data;
		}
		draw();
	}
	public void draw() {
		final SurfaceHolder holder = this._engine.getSurfaceHolder();
		Canvas c = null;
		ColorInfo colorInfo = getColor(this._baseColor, this._state, this._battery, this._reverse);
		try {
			c = holder.lockCanvas();
			int width = this._width;
			int height = this._height;
			int pxSize = this._pxSize;
			Paint paint = new Paint();
			
			byte[] prevData = null;
			if(this._prevDataList.size() > 0) {
				prevData = this._prevDataList.get(0);
			}
			c.drawColor(colorInfo.backColor);
			int mask = 0x80;
			byte[] dataArray = this._data;
			Path livePath = new Path();
			Path deadPath = new Path();
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					mask = 0x80;
					mask = mask >> (j % 8);
					int index =((width*i) >> 3) + (j >> 3);
					if( (dataArray[index] & mask) > 0) {
						livePath.addRoundRect(new RectF(j * pxSize + 2, i * pxSize + 2, j * pxSize + pxSize - 2, i * pxSize + pxSize - 2), 2, 2, Path.Direction.CW);
					}
					else if((dataArray[index] & mask) != (prevData[index] & mask)) {
						deadPath.addRoundRect(new RectF(j * pxSize + 2, i * pxSize + 2, j * pxSize + pxSize - 2, i * pxSize + pxSize - 2), 2, 2, Path.Direction.CW);
					}
				}
			}
			livePath.close();
			deadPath.close();
			
			if(this._backPattern.isBitmap() == false) {
				for(int i = 0; i < this._backPattern.getPathList().size(); i++) {
					paint.setColor(this._backPattern.makeColor(colorInfo.getBackColor()).get(i));
					c.drawPath(this._backPattern.getPathList().get(i), paint);
				}
			}
			else {
				drawThumbnailBitmap(c);
			}
			if(this._configNow == false && this._battery < 15) {
				drawBattery(c, Color.RED, colorInfo.getBackColor());
			}
			
			paint.setColor(colorInfo.getCellColor());
			c.drawPath(livePath, paint);
			paint.setColor(colorInfo.getDeadColor());
			c.drawPath(deadPath, paint);
			if(this._backPattern.isDrawLine() == true) {
				paint.setColor(colorInfo.getLineColor());
				paint.setStrokeWidth(1);
				paint.setStyle(Paint.Style.STROKE);
				c.drawPath(this._linePath, paint);
			}
			incrementState();
			holder.unlockCanvasAndPost(c);
		}
		catch(Exception e) {
			if(holder != null && c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	private void drawBattery(Canvas c, int color, int backColor) {
//		float lineWidth =  this._pxSize*4/10.0f;
		float lineWidth =  this._pxSize*4/15.0f;
		float spaceWidth = 2;
		float height = (this._pxSize * 4 -  lineWidth * 2 - spaceWidth) - (5 + lineWidth * 2 + spaceWidth);
		float clipHeight = height -  height * this._battery / 100.0f;
		Paint paint = new Paint();
		for(int i = 0; i < this._height; i = i + 4) {
			for(int j = 0; j < this._height; j = j + 4) {
				paint.setColor(color);
				c.drawRoundRect(new RectF((this._pxSize * (j + 2)) - this._pxSize*4/10.0f,
											(this._pxSize * i) + 5,
											(this._pxSize * (j + 2)) + this._pxSize*4/10.0f,
											(this._pxSize * i) + 5 + lineWidth + spaceWidth),
											2, 2, paint);
				c.drawRoundRect(new RectF((this._pxSize * (j + 2)) - this._pxSize*4/5.0f,
											(this._pxSize * i) + 5 + lineWidth,
											(this._pxSize * (j + 2)) + this._pxSize*4/5.0f,
											(this._pxSize * (i + 4)) - lineWidth),
											2, 2, paint);
				paint.setColor(backColor);
				c.drawRect(new RectF((this._pxSize * (j + 2)) - this._pxSize*4/5.0f + lineWidth,
						(this._pxSize * i) +  5 + lineWidth * 2,
						(this._pxSize * (j + 2)) + this._pxSize*4/5.0f - lineWidth,
						(this._pxSize * (i + 4)) - lineWidth * 2),
						paint);
				paint.setColor(color);
				c.save();
				c.clipRect((this._pxSize * (j + 2)) - this._pxSize*4/4.0f + lineWidth,
						(this._pxSize * i) +  5 + lineWidth * 2 + spaceWidth  + clipHeight,
						(this._pxSize * (j + 2)) + this._pxSize*4/4.0f - lineWidth,
						(this._pxSize * (i + 4)) - lineWidth * 2 - spaceWidth);
				c.drawRoundRect(new RectF((this._pxSize * (j + 2)) - this._pxSize*4/5.0f + lineWidth + spaceWidth,
						(this._pxSize * i) +  5 + lineWidth * 2 + spaceWidth,
						(this._pxSize * (j + 2)) + this._pxSize*4/5.0f - lineWidth - spaceWidth,
						(this._pxSize * (i + 4)) - lineWidth * 2 - spaceWidth),
						2, 2, paint);
				c.restore();
			}
		}
	}
	
	private byte[] nextLifeArray(byte[] srcArray,int width,int height) {
		byte[] arr = new byte[srcArray.length];
		int mask = 0;
		int val = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				byte bits = this.getEightBits(srcArray, i * width + j, width, height);
				int c = this.numofbits(bits);
				mask = 0x80;
				val = 0x80;
				mask = mask >> (j % 8);
				int index = (width >> 3 )* i + (j >> 3); 
				if((srcArray[index] & mask) > 0) {
					if(c == 2 || c == 3) {
						val = val >> (j % 8);
						arr[index] = (byte)(arr[index] | val);
					}
				}
				else {
					if(c == 3) {
						val = val >> (j % 8);
						arr[index] = (byte)(arr[index] | val);
					}
				}
			}
		}
		return arr;
	}
	private byte getEightBits(byte[] byteArray, int bitPos,int bitWidth,int bitHeight){
		int ret = 0;
		int line1= 0;
		int line2= 0;
		int line3= 0;
		int temp = 0;
		int ph = ((bitWidth * bitHeight + bitPos - bitWidth)/bitWidth) % bitHeight;
		int ch = ((bitWidth * bitHeight + bitPos)/bitWidth) % bitHeight;
		int nh = ((bitWidth * bitHeight + bitPos + bitWidth)/bitWidth) % bitHeight;
		int byteWidth = bitWidth >> 3;
		int iPrev = byteWidth * ph + (((bitPos)%bitWidth) >> 3);
		int iPos = byteWidth * ch + (((bitPos)%bitWidth) >> 3);
		int iNext = byteWidth * nh + (((bitPos)%bitWidth) >> 3);

		int pLine1 = ((iPrev - byteWidth * ph + byteWidth - 1) % byteWidth) + byteWidth * ph;
		int cLine1 = iPrev;
		int nLine1 = ((iPrev - byteWidth * ph + byteWidth + 1) % byteWidth) + byteWidth * ph;

		int pLine2 = ((iPos - byteWidth * ch + byteWidth - 1) % byteWidth) + byteWidth * ch;
		int cLine2 = iPos;
		int nLine2 = ((iPos - byteWidth * ch + byteWidth + 1) % byteWidth) + byteWidth * ch;

		int pLine3 = ((iNext - byteWidth * nh + byteWidth - 1) % byteWidth) + byteWidth * nh;
		int cLine3 = iNext;
		int nLine3 = ((iNext - byteWidth * nh + byteWidth + 1) % byteWidth) + byteWidth * nh;

		int mask = 0x01C000;
		mask = mask >> (bitPos % 8);
		
		if(pLine1 < cLine1) {
			temp = ((0x0001 & byteArray[pLine1]) << 16);
		}
		else {
			temp = ((0x0080 & byteArray[pLine1]) << 9);
		}
		
		temp = temp | ((0x00FF & byteArray[cLine1]) << 8);
		
		if(nLine1 > cLine1) {
			temp = temp | (0x0080 & byteArray[nLine1]);
		}
		else {
			temp = temp | ((0x0001 & byteArray[nLine1]) << 7);
		}
		
		temp = temp & mask;
		line1 = temp >> (14 - (bitPos % 8));

		
		if(pLine2 < cLine2) {
			temp = ((0x0001 & byteArray[pLine2]) << 16);
		}
		else {
			temp = ((0x0080 & byteArray[pLine2]) << 9);
		}
		
		temp = temp | ((0x00FF & byteArray[cLine2]) << 8);
		
		if(nLine2 > cLine2) {
			temp = temp | (0x0080 & byteArray[nLine2]);
		}
		else {
			temp = temp | ((0x0001 & byteArray[nLine2]) << 7);
		}
		
		temp = temp & mask;
		line2 = temp >> (14 - (bitPos % 8));
		
		if(pLine3 < cLine3) {
			temp = ((0x0001 & byteArray[pLine3]) << 16);
		}
		else {
			temp = ((0x0080 & byteArray[pLine3]) << 9);
		}
		
		temp = temp | ((0x00FF & byteArray[cLine3]) << 8);
		
		if(nLine3 > cLine3) {
			temp = temp | (0x0080 & byteArray[nLine3]);
		}
		else {
			temp = temp | ((0x0001 & byteArray[nLine3]) << 7);
		}

		temp = temp & mask;
		line3 = temp >> (14 - (bitPos % 8));
		ret = line1 << 5;
		ret = ret | (line3 << 2);
		if((line2 &0x04) > 0) {
			ret = ret | 0x02;
		}
		if((line2 & 0x01) > 0) {
			ret = ret | 0x01;
		}
		return (byte)ret;
	}
	private byte numofbits(byte bits) {
		int iBits = bits;
		iBits = (iBits & 0x55) + ((iBits >> 1) & 0x55);
		iBits = (iBits & 0x33) + ((iBits >> 2) & 0x33);
		iBits = (iBits & 0x0f) + ((iBits >> 4) & 0x0f);
		iBits = (iBits & 0xff) + ((iBits >> 8) & 0xff);
		return (byte)iBits;
	}
	public void setPattern(Pattern p, int x,  int y ) {
		synchronized (syncObj) {
			int bitWidth = this._width;
			int bitHeight = this._height;
			byte[] pattern = p.getBytedata();
			int patWidth = p.getByteWidth();
			int patHeight = p.getByteHeight();
			byte[] data = cloneData(this._data);
			if(data == null) {
				return;
			}
			int px = x;
			int py = y;
			if(bitWidth - x < patWidth * 8) {
				px = bitWidth - patWidth * 8;
			} 
			if(bitHeight - y < patHeight) {
				py = bitHeight - patHeight;
			}
			int index = (bitWidth >> 3)* py + (px >> 3);
			for(int j = 0; j < patHeight; j++) {
				for(int k = 0; k < patWidth; k++) {
					data[index] = pattern[patWidth * j + k];
					index++;
				}
				index = index + ((bitWidth >> 3) - patWidth);
			}
			this.setData(data);
		}
		draw();
	}
	private byte[] copyPattern(byte[] srcdata, int x, int y , int bitWidth, int bitHeight, Pattern p) {
		byte[] data = cloneData(srcdata);
		if(data == null) {
			return null;
		}
		byte[] pattern = p.getBytedata();
		int index = 0;
		int j = 0;
		int k = 0;
		try {
			if(data != null && data.length > 0 &&
				pattern != null && pattern.length > 0) {
				int patWidth = p.getByteWidth();
				int patHeight = p.getByteHeight();
				int px = x;
				int py = y;
				if(bitWidth - x < patWidth * 8) {
					px = bitWidth - patWidth * 8;
				} 
				if(bitHeight - y < patHeight) {
					py = bitHeight - patHeight;
				}
				index = (bitWidth >> 3)* py + (px >> 3);
				for(j = 0; j < patHeight; j++) {
					for(k = 0; k < patWidth; k++) {
						if(index < data.length && (patWidth * j + k) < pattern.length) {
							if(index >= 0) {
								data[index] = pattern[patWidth * j + k];
							}
							index++;
						}
						else
						{
							break;
						}
					}
					index = index + ((bitWidth >> 3) - patWidth);
				}
			}
			return data;
		}
		catch(Exception e) {
			if(e != null && e.getMessage() != null) 
			{
				Log.w("Lifegame", e.getMessage());
			}
			if(data != null) {
				Log.w("Lifegame", "data.length:" + data.length);
			}
			if(pattern != null) {
				Log.w("Lifegame", "pattern.length:" + pattern.length);
			}
			Log.w("Lifegame", "index:" + index);
			Log.w("Lifegame", "j:" + j);
			Log.w("Lifegame", "j:" + k);
			return null;
		}
	}
	public void addRandomPatterns(int num) {
		synchronized (syncObj) {
			Random r = new Random();
			int width = this._width;
			int height = this._height;
			byte[] data = cloneData(this._data);
			if(data == null) {
				return;
			}
			Patterns patterns = new Patterns(context);
			for(int i= 0; i < num; i++) {
				Pattern p = patterns.getRandomPattern();
				int x = r.nextInt(width);
				int y = r.nextInt(height);
				data = this.copyPattern(data, x, y , width, height,p);
			}
			if(data != null) 
			{
				this.setData(data);
			}
		}
		draw();
	}
	public void setInterval(int _interval) {
		this._interval = _interval;
	}
	public int getInterval() {
		return _interval;
	}
	
	public void run() {
		while(this._threadStop == false) {
			try {
				int interval = this._interval + (10 - this._speed) * 100;
				if(this._threadPause == false || this._configNow == true) {
					if(_state == 0) {
						calc();
						Thread.sleep(interval);
					}
					else {
						draw();
						Thread.sleep(interval/10);
					}
				}
				else {
					Thread.sleep(3000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void stopThread() {
		this._threadStop = true;
	}
	public void setPause(boolean pause) {
		this._threadPause = pause;
	}
	synchronized public void onTouch(float x, float y) {
		synchronized (syncObj) {
			Patterns patterns = new Patterns(context);
			Pattern p = patterns.getRandomPattern();
			byte[] data = copyPattern(this._data, ((int)x)/this._pxSize, ((int)y)/this._pxSize, this._width, this._height, p);
			if(data != null) {
				setData(data);
			}
		}
		draw();
	}
	public ColorInfo getColor(int baseColor, int state, int battery, boolean reverse) {
		ColorInfo c = new ColorInfo(baseColor, state, battery, reverse);
		return c;
	}
	public void setBaseColor(int _baseColor) {
		this._baseColor = _baseColor;
		
	}
	private void makeLinePath() {
		this._linePath = new Path();
		for(int i = 0; i < this._height; i++) {
			this._linePath.moveTo(0, i * this._pxSize);
			this._linePath.lineTo(this._width * this._pxSize, i * this._pxSize);
		}
		for(int i = 0; i < this._width; i++) {
			this._linePath.moveTo(i * this._pxSize, 0);
			this._linePath.lineTo(i * this._pxSize, this._height* this._pxSize);
		}
	}
	public int getBaseColor() {
		return _baseColor;
	}
	private void setState(int state) {
		this._state = state % 8;
	}
	private void incrementState() {
		this._state = (this._state + 1) % 8;
	}
	public void setBattery(int scale, int level) {
		this._battery = level * 100 / scale;
	}
	synchronized public void setBackPattern(int pattern) {
		if(pattern != this._backPattern.getPatternId()) {
			this._backPattern = new BackPattern(pattern, this._width, this._height);
		}
	}
	public void setSpeed(int _speed) {
		this._speed = _speed;
	}
	public int getSpeed() {
		return _speed;
	}
	public void setReverse(boolean _reverse) {
		this._reverse = _reverse;
	}
	public boolean isReverse() {
		return _reverse;
	}
	public void setConfigNow(boolean _configNow) {
		this._configNow = _configNow;
	}
	public boolean isConfigNow() {
		return _configNow;
	}
	class ColorInfo {
		private int backColor;
		private int cellColor;
		private int deadColor;
		private int lineColor;
		private List<Integer> backPathColors;
		ColorInfo(int baseColor, int state, int battery, boolean reverse) {
			float backHsv[] = new float[3];
			Color.RGBToHSV(Color.red(baseColor),Color.green(baseColor), Color.blue(baseColor), backHsv);
			if(backHsv[2] > 0.5) {
				backHsv[2] = (float)(backHsv[2] * 0.7);
			}
			else {
				backHsv[2] = (float)((backHsv[2] + 0.1) * 1.3);
			}
			int deadAlpha = 0x0040 >> state;
			int baseCellColor = baseColor;
			if(reverse == false) {
				this.backColor = Color.HSVToColor(backHsv);
			}
			else {
				backHsv[0] = ((backHsv[0] + 180) > 360 ? (backHsv[0] - 180): (backHsv[0] + 180)); 
				backHsv[2] = 1.0f - backHsv[2];
				this.backColor = Color.HSVToColor(backHsv);
			}
			this.cellColor = Color.argb(200, Color.red(baseCellColor), Color.green(baseCellColor), Color.blue(baseCellColor));
			this.deadColor = Color.argb(deadAlpha, Color.red(baseCellColor), Color.green(baseCellColor), Color.blue(baseCellColor));
			this.lineColor = Color.argb(200, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
			this.backPathColors = new ArrayList<Integer>();
			
			this.backPathColors.add(0, Color.argb(100, 255, 255, 255));
			this.backPathColors.add(1, Color.argb(100, 128, 128, 128));
			this.backPathColors.add(2, Color.argb(100, 50, 50, 50));
			this.backPathColors.add(3, Color.argb(100, 128, 128, 128));
		}
		public void setBackColor(int backColor) {
			this.backColor = backColor;
		}
		public int getBackColor() {
			return backColor;
		}
		public void setCellColor(int cellColor) {
			this.cellColor = cellColor;
		}
		public int getCellColor() {
			return cellColor;
		}
		public void setLineColor(int lineColor) {
			this.lineColor = lineColor;
		}
		public int getLineColor() {
			return lineColor;
		}
		public void setDeadColor(int deadColor) {
			this.deadColor = deadColor;
		}
		public int getDeadColor() {
			return deadColor;
		}
		public List<Integer> getBackPathColors() {
			return backPathColors;
		}
	}
	class BackPattern{
		public static final int PLAIN = 0;
		public static final int NOLINE = 1;
		public static final int PYRAMID = 2;
		public static final int TILE = 3;
		public static final int CHECKER = 4;
		public static final int LCHECKER = 5;
		public static final int HOUNDSTOOTH = 6;
		public static final int STRIPE = 7;
		public static final int TRIANGLE = 8;
		public static final int THUMNAILS = 9;
		private List<Path> pathList;
		private int pattern;
		private boolean drawLine;
		private boolean bitmap;
		public BackPattern(int pattern, int width, int height) {
			this.pattern = pattern;
			this.setBitmap(false);
			pathList = new ArrayList<Path>();
			makePattern(width, height);
			switch(pattern){
			case PLAIN:
				setDrawLine(true);
				break;
			case NOLINE:
				setDrawLine(false);
				break;
			case PYRAMID:
				setDrawLine(false);
				break;
			case CHECKER:
				setDrawLine(false);
				break;
			case LCHECKER:
				setDrawLine(false);
				break;
			case HOUNDSTOOTH:
				setDrawLine(false);
				break;
			case STRIPE:
				setDrawLine(false);
				break;
			case TRIANGLE:
				setDrawLine(false);
				break;
			case THUMNAILS:
				setDrawLine(false);
				setBitmap(true);
				break;
			}
		}
		public int getPatternId() {
			return pattern;
		}
		public void makePattern(int width, int height) {
			Path path1 = new Path();
			Path path2 = new Path();
			Path path3 = new Path();
			Path path4 = new Path();
			switch(pattern){
			case PYRAMID:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						path1.moveTo(j * _pxSize, i * _pxSize);
						path1.lineTo(j * _pxSize  + _pxSize, i * _pxSize);
						path1.lineTo(j * _pxSize  + _pxSize/2 , i * _pxSize +  _pxSize/2);
						path1.lineTo(j * _pxSize, i * _pxSize);
						
						path2.moveTo(j * _pxSize + _pxSize, i * _pxSize);
						path2.lineTo(j * _pxSize  + _pxSize/2, i * _pxSize + _pxSize/2);
						path2.lineTo(j * _pxSize  + _pxSize, i * _pxSize + _pxSize);
						path2.lineTo(j * _pxSize + _pxSize, i * _pxSize);

						path3.moveTo(j * _pxSize, i * _pxSize + _pxSize);
						path3.lineTo(j * _pxSize  + _pxSize/2, i * _pxSize + _pxSize/2);
						path3.lineTo(j * _pxSize  + _pxSize, i * _pxSize + _pxSize);
						path3.lineTo(j * _pxSize, i * _pxSize + _pxSize);

						path4.moveTo(j * _pxSize, i * _pxSize);
						path4.lineTo(j * _pxSize  + _pxSize/2, i * _pxSize + _pxSize/2);
						path4.lineTo(j * _pxSize, i * _pxSize +  _pxSize);
						path4.lineTo(j * _pxSize, i * _pxSize);
					}
				}
				path1.close();
				path2.close();
				path3.close();
				path4.close();
				this.pathList.add(path1);
				this.pathList.add(path2);
				this.pathList.add(path3);
				this.pathList.add(path4);
				break;
			case TILE:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						path1.addRoundRect(new RectF(j * _pxSize + 2,i * _pxSize + 2, j * _pxSize  + _pxSize - 2,  i * _pxSize + _pxSize - 2), 2,2, Path.Direction.CW);
					}
				}
				path1.close();
				this.pathList.add(path1);
				break;
			case CHECKER:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						path1.addRect(j * _pxSize,i * _pxSize, j * _pxSize  + _pxSize/2,  i * _pxSize + _pxSize/2, Path.Direction.CW);
						path1.addRect(j * _pxSize + _pxSize/2,i * _pxSize + _pxSize/2 , j * _pxSize  + _pxSize,  i * _pxSize + _pxSize, Path.Direction.CW);
					}
				}
				path1.close();
				this.pathList.add(path1);
				break;
			case LCHECKER:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						if(i % 2 == 0) {
							if(j % 2 > 0) {
								path1.addRect(j * _pxSize,i * _pxSize, j * _pxSize  + _pxSize,  i * _pxSize + _pxSize, Path.Direction.CW);
							}
						}
						else {
							if(j % 2 == 0) {
								path1.addRect(j * _pxSize,i * _pxSize, j * _pxSize  + _pxSize,  i * _pxSize + _pxSize, Path.Direction.CW);
							}
						}
					}
				}
				path1.close();
				this.pathList.add(path1);
				break;
			case HOUNDSTOOTH:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						if(j % 2 == 0) {
							if(i % 2 != 0) {
								path1.moveTo(j * _pxSize,i * _pxSize);
								path1.lineTo(j * _pxSize + _pxSize/2, i * _pxSize);
								path1.lineTo(j * _pxSize, i * _pxSize  + _pxSize/2);
								path1.lineTo(j * _pxSize,i * _pxSize);
								path1.moveTo(j * _pxSize + _pxSize,i * _pxSize);
								path1.lineTo(j * _pxSize + _pxSize,i * _pxSize + _pxSize/2);
								path1.lineTo(j * _pxSize + _pxSize/2,i * _pxSize + _pxSize);
								path1.lineTo(j * _pxSize,i * _pxSize + _pxSize);
								path1.lineTo(j * _pxSize + _pxSize,i * _pxSize);
							}
						}
						else {
							if(i % 2 == 0) {
								path1.moveTo(j * _pxSize + _pxSize/2,i * _pxSize);
								path1.lineTo(j * _pxSize + _pxSize, i * _pxSize);
								path1.lineTo(j * _pxSize, i * _pxSize  + _pxSize);
								path1.lineTo(j * _pxSize,i * _pxSize + _pxSize/2);
								path1.lineTo(j * _pxSize + _pxSize/2,i * _pxSize);
								path1.moveTo(j * _pxSize + _pxSize,i * _pxSize + _pxSize/2);
								path1.lineTo(j * _pxSize + _pxSize,i * _pxSize + _pxSize);
								path1.lineTo(j * _pxSize + _pxSize/2,i * _pxSize + _pxSize);
								path1.lineTo(j * _pxSize + _pxSize,i * _pxSize + _pxSize/2);
							}
							else {
								path1.addRect(j * _pxSize,i * _pxSize, j * _pxSize  + _pxSize,  i * _pxSize + _pxSize, Path.Direction.CW);
							}
						}
					}
				}
				path1.close();
				this.pathList.add(path1);
				break;
			case STRIPE:
				for(int j = 0; j < width; j++) {
					path1.addRect(j * _pxSize, 0, j * _pxSize + _pxSize/2, height * _pxSize, Path.Direction.CW);
				}
				path1.close();
				this.pathList.add(path1);
				break;
			case TRIANGLE:
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width; j++) {
						if(i % 2 == 0) {
							path1.moveTo(j * _pxSize + _pxSize/2,i * _pxSize);
							path1.lineTo(j * _pxSize + _pxSize, i * _pxSize + _pxSize);
							path1.lineTo(j * _pxSize, i * _pxSize  + _pxSize);
							path1.lineTo(j * _pxSize + _pxSize/2,i * _pxSize);
						}
						else {
							path1.moveTo(j * _pxSize, i * _pxSize);
							path1.lineTo(j * _pxSize + _pxSize/2, i * _pxSize + _pxSize);
							path1.lineTo(j * _pxSize, i * _pxSize  + _pxSize);
							path1.lineTo(j * _pxSize, i * _pxSize);
							path1.moveTo(j * _pxSize + _pxSize, i * _pxSize);
							path1.lineTo(j * _pxSize + _pxSize, i * _pxSize + _pxSize);
							path1.lineTo(j * _pxSize + _pxSize/2, i * _pxSize + _pxSize);
							path1.lineTo(j * _pxSize + _pxSize, i * _pxSize);
						}
					}
				}
				path1.close();
				this.pathList.add(path1);
				break;
			}
		}
		public List<Integer> makeColor(int color) {
			List<Integer> colorList = new ArrayList<Integer>();
			switch(pattern){
			case PYRAMID:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				colorList.add(1, Color.argb(0, 0, 0, 0));
				colorList.add(2, Color.argb(128, 102, 102, 102));
				colorList.add(3, Color.argb(128, 128, 128, 128));
				break;
			case TILE:
				colorList.add(0, Color.argb(50, 255, 255, 255));
				break;
			case CHECKER:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				break;
			case LCHECKER:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				break;
			case HOUNDSTOOTH:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				break;
			case STRIPE:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				break;
			case TRIANGLE:
				colorList.add(0, Color.argb(128, 153, 153, 153));
				break;
			}
			return colorList;
		}
		public List<Path> getPathList() {
			return this.pathList;
		}
		public void setDrawLine(boolean drawLine) {
			this.drawLine = drawLine;
		}
		public boolean isDrawLine() {
			return drawLine;
		}
		public void setBitmap(boolean bitmap) {
			this.bitmap = bitmap;
		}
		public boolean isBitmap() {
			return bitmap;
		}
	}

}
