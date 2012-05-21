package jp.morishi.lifegame.wallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ColorPalleteView extends View{
//	private int colors[] = new int[] {
//			0xFF000000, 0xFF000033, 0xFF000066, 0xFF000099, 0xFF0000cc, 0xFF0000ff, //0
//			0xFF003300, 0xFF003333, 0xFF003366, 0xFF003399, 0xFF0033cc, 0xFF0033ff, //1
//			0xFF006600, 0xFF006633, 0xFF006666, 0xFF006699, 0xFF0066cc, 0xFF0066ff, //2
//			0xFF009900, 0xFF009933, 0xFF009966, 0xFF009999, 0xFF0099cc, 0xFF0099ff, //3
//			0xFF00cc00, 0xFF00cc33, 0xFF00cc66, 0xFF00cc99, 0xFF00cccc, 0xFF00ccff, //4
//			0xFF00ff00, 0xFF00ff33, 0xFF00ff66, 0xFF00ff99, 0xFF00ffcc, 0xFF00ffff, //5
//			0xFF330000, 0xFF330033, 0xFF330066, 0xFF330099, 0xFF3300cc, 0xFF3300ff, //6
//			0xFF333300, 0xFF333333, 0xFF333366, 0xFF333399, 0xFF3333cc, 0xFF3333ff, //7
//			0xFF336600, 0xFF336633, 0xFF336666, 0xFF336699, 0xFF3366cc, 0xFF3366ff, //8
//			0xFF339900, 0xFF339933, 0xFF339966, 0xFF339999, 0xFF3399cc, 0xFF3399ff, //9
//			0xFF33cc00, 0xFF33cc33, 0xFF33cc66, 0xFF33cc99, 0xFF33cccc, 0xFF33ccff, //10
//			0xFF33ff00, 0xFF33ff33, 0xFF33ff66, 0xFF33ff99, 0xFF33ffcc, 0xFF33ffff, //11
//			0xFF660000, 0xFF660033, 0xFF660066, 0xFF660099, 0xFF6600cc, 0xFF6600ff, //12
//			0xFF663300, 0xFF663333, 0xFF663366, 0xFF663399, 0xFF6633cc, 0xFF6633ff, //13
//			0xFF666600, 0xFF666633, 0xFF666666, 0xFF666699, 0xFF6666cc, 0xFF6666ff, //14
//			0xFF669900, 0xFF669933, 0xFF669966, 0xFF669999, 0xFF6699cc, 0xFF6699ff, //15
//			0xFF66cc00, 0xFF66cc33, 0xFF66cc66, 0xFF66cc99, 0xFF66cccc, 0xFF66ccff, //16
//			0xFF66ff00, 0xFF66ff33, 0xFF66ff66, 0xFF66ff99, 0xFF66ffcc, 0xFF66ffff, //17
//			0xFF990000, 0xFF990033, 0xFF990066, 0xFF990099, 0xFF9900cc, 0xFF9900ff, //18
//			0xFF993300, 0xFF993333, 0xFF993366, 0xFF993399, 0xFF9933cc, 0xFF9933ff, //19
//			0xFF996600, 0xFF996633, 0xFF996666, 0xFF996699, 0xFF9966cc, 0xFF9966ff, //20
//			0xFF999900, 0xFF999933, 0xFF999966, 0xFF999999, 0xFF9999cc, 0xFF9999ff, //21
//			0xFF99cc00, 0xFF99cc33, 0xFF99cc66, 0xFF99cc99, 0xFF99cccc, 0xFF99ccff, //22
//			0xFF99ff00, 0xFF99ff33, 0xFF99ff66, 0xFF99ff99, 0xFF99ffcc, 0xFF99ffff, //23
//			0xFFcc0000, 0xFFcc0033, 0xFFcc0066, 0xFFcc0099, 0xFFcc00cc, 0xFFcc00ff, //24
//			0xFFcc3300, 0xFFcc3333, 0xFFcc3366, 0xFFcc3399, 0xFFcc33cc, 0xFFcc33ff, //25
//			0xFFcc6600, 0xFFcc6633, 0xFFcc6666, 0xFFcc6699, 0xFFcc66cc, 0xFFcc66ff, //26
//			0xFFcc9900, 0xFFcc9933, 0xFFcc9966, 0xFFcc9999, 0xFFcc99cc, 0xFFcc99ff, //27
//			0xFFcccc00, 0xFFcccc33, 0xFFcccc66, 0xFFcccc99, 0xFFcccccc, 0xFFccccff, //28
//			0xFFccff00, 0xFFccff33, 0xFFccff66, 0xFFccff99, 0xFFccffcc, 0xFFccffff, //29
//			0xFFff0000, 0xFFff0033, 0xFFff0066, 0xFFff0099, 0xFFff00cc, 0xFFff00ff, //30
//			0xFFff3300, 0xFFff3333, 0xFFff3366, 0xFFff3399, 0xFFff33cc, 0xFFff33ff, //31
//			0xFFff6600, 0xFFff6633, 0xFFff6666, 0xFFff6699, 0xFFff66cc, 0xFFff66ff, //32
//			0xFFff9900, 0xFFff9933, 0xFFff9966, 0xFFff9999, 0xFFff99cc, 0xFFff99ff, //33
//			0xFFffcc00, 0xFFffcc33, 0xFFffcc66, 0xFFffcc99, 0xFFffcccc, 0xFFffccff, //34
//			0xFFffff00, 0xFFffff33, 0xFFffff66, 0xFFffff99, 0xFFffffcc, 0xFFffffff, //35
//			0xFF000000, 0xFF333333, 0xFF666666, 0xFF999999, 0xFFcccccc, 0xFFffffff, //36
//	};
	private int colors[] = new int[] {
			0xFF000000, 0xFF000033, 0xFF000066, 0xFF000099, 0xFF0000cc, 0xFF0000ff, //0
			0xFF003300, 0xFF003333, 0xFF003366, 0xFF003399, 0xFF0033cc, 0xFF0033ff, //1
			0xFF006600, 0xFF006633, 0xFF006666, 0xFF006699, 0xFF0066cc, 0xFF0066ff, //2

			0xFF330000, 0xFF330033, 0xFF330066, 0xFF330099, 0xFF3300cc, 0xFF3300ff, //6
			0xFF333300, 0xFF333333, 0xFF333366, 0xFF333399, 0xFF3333cc, 0xFF3333ff, //7
			0xFF336600, 0xFF336633, 0xFF336666, 0xFF336699, 0xFF3366cc, 0xFF3366ff, //8
			
			0xFF660000, 0xFF660033, 0xFF660066, 0xFF660099, 0xFF6600cc, 0xFF6600ff, //12
			0xFF663300, 0xFF663333, 0xFF663366, 0xFF663399, 0xFF6633cc, 0xFF6633ff, //13
			0xFF666600, 0xFF666633, 0xFF666666, 0xFF666699, 0xFF6666cc, 0xFF6666ff, //14
			
			0xFF990000, 0xFF990033, 0xFF990066, 0xFF990099, 0xFF9900cc, 0xFF9900ff, //18
			0xFF993300, 0xFF993333, 0xFF993366, 0xFF993399, 0xFF9933cc, 0xFF9933ff, //19
			0xFF996600, 0xFF996633, 0xFF996666, 0xFF996699, 0xFF9966cc, 0xFF9966ff, //20

			0xFFcc0000, 0xFFcc0033, 0xFFcc0066, 0xFFcc0099, 0xFFcc00cc, 0xFFcc00ff, //24
			0xFFcc3300, 0xFFcc3333, 0xFFcc3366, 0xFFcc3399, 0xFFcc33cc, 0xFFcc33ff, //25
			0xFFcc6600, 0xFFcc6633, 0xFFcc6666, 0xFFcc6699, 0xFFcc66cc, 0xFFcc66ff, //26

			0xFFff0000, 0xFFff0033, 0xFFff0066, 0xFFff0099, 0xFFff00cc, 0xFFff00ff, //30
			0xFFff3300, 0xFFff3333, 0xFFff3366, 0xFFff3399, 0xFFff33cc, 0xFFff33ff, //31
			0xFFff6600, 0xFFff6633, 0xFFff6666, 0xFFff6699, 0xFFff66cc, 0xFFff66ff, //32
			
			
			0xFFff9900, 0xFFff9933, 0xFFff9966, 0xFFff9999, 0xFFff99cc, 0xFFff99ff, //33
			0xFFffcc00, 0xFFffcc33, 0xFFffcc66, 0xFFffcc99, 0xFFffcccc, 0xFFffccff, //34
			0xFFffff00, 0xFFffff33, 0xFFffff66, 0xFFffff99, 0xFFffffcc, 0xFFffffff, //35

			0xFFcc9900, 0xFFcc9933, 0xFFcc9966, 0xFFcc9999, 0xFFcc99cc, 0xFFcc99ff, //27
			0xFFcccc00, 0xFFcccc33, 0xFFcccc66, 0xFFcccc99, 0xFFcccccc, 0xFFccccff, //28
			0xFFccff00, 0xFFccff33, 0xFFccff66, 0xFFccff99, 0xFFccffcc, 0xFFccffff, //29
			
			0xFF999900, 0xFF999933, 0xFF999966, 0xFF999999, 0xFF9999cc, 0xFF9999ff, //21
			0xFF99cc00, 0xFF99cc33, 0xFF99cc66, 0xFF99cc99, 0xFF99cccc, 0xFF99ccff, //22
			0xFF99ff00, 0xFF99ff33, 0xFF99ff66, 0xFF99ff99, 0xFF99ffcc, 0xFF99ffff, //23

			0xFF669900, 0xFF669933, 0xFF669966, 0xFF669999, 0xFF6699cc, 0xFF6699ff, //15
			0xFF66cc00, 0xFF66cc33, 0xFF66cc66, 0xFF66cc99, 0xFF66cccc, 0xFF66ccff, //16
			0xFF66ff00, 0xFF66ff33, 0xFF66ff66, 0xFF66ff99, 0xFF66ffcc, 0xFF66ffff, //17
			
			0xFF339900, 0xFF339933, 0xFF339966, 0xFF339999, 0xFF3399cc, 0xFF3399ff, //9
			0xFF33cc00, 0xFF33cc33, 0xFF33cc66, 0xFF33cc99, 0xFF33cccc, 0xFF33ccff, //10
			0xFF33ff00, 0xFF33ff33, 0xFF33ff66, 0xFF33ff99, 0xFF33ffcc, 0xFF33ffff, //11

			0xFF009900, 0xFF009933, 0xFF009966, 0xFF009999, 0xFF0099cc, 0xFF0099ff, //3
			0xFF00cc00, 0xFF00cc33, 0xFF00cc66, 0xFF00cc99, 0xFF00cccc, 0xFF00ccff, //4
			0xFF00ff00, 0xFF00ff33, 0xFF00ff66, 0xFF00ff99, 0xFF00ffcc, 0xFF00ffff, //5
			
			0xFF000000, 0xFF333333, 0xFF666666, 0xFF999999, 0xFFcccccc, 0xFFffffff, //36
	};
	private static final int PIXSIZE = 24;
	private static final int COLNUM = 18;
	private int selectColor;
	private OnColorChangedListener onColorChangedListener;
	public interface OnColorChangedListener {
        void colorChanged(int color);
        void colorTracking(int color);
    }
    public ColorPalleteView(Context context, OnColorChangedListener l, int color) {
		super(context);
		this.selectColor = color;
		this.onColorChangedListener = l;
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if(colors.length % COLNUM == 0) {
            setMeasuredDimension(PIXSIZE * COLNUM, (colors.length / COLNUM) * PIXSIZE);
    	}
    	else {
            setMeasuredDimension(PIXSIZE * COLNUM, (colors.length / COLNUM + 1) * PIXSIZE);
    	}
    		
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	int y = 0;
    	int x = 0;
		Paint p = new Paint();
    	for(int i = 0; i < colors.length; i++) {
    		p.setColor(colors[i]);
    		x = (i % COLNUM) * PIXSIZE;
    		canvas.drawRect(x, y, x + PIXSIZE, y + PIXSIZE, p);
    		if(i % COLNUM == COLNUM - 1) {
    			y = y + PIXSIZE;
    		}
    	}
    	p.setColor(selectColor ^ 0x00FFFFFF);
    	p.setStyle(Paint.Style.STROKE);
    	p.setStrokeWidth(2);
    	int index = findColorIndex(selectColor);
    	x = (index % COLNUM) * PIXSIZE;
    	y = (index / COLNUM) * PIXSIZE;
		canvas.drawRect(x, y, x + PIXSIZE, y + PIXSIZE, p);
    }
    private int findColorIndex(int color) {
    	for(int i = 0; i < colors.length; i++) {
    		if(color == colors[i]) {
    			return i;
    		}
    	}
    	return colors.length - 1;
    }
    private int findColor(float x, float y) {
    	int index = (int)Math.floor(x/PIXSIZE) + COLNUM * (int)Math.floor(y/PIXSIZE);
    	if(index < colors.length) {
        	return colors[index];
    	}
    	else 
    	{
        	return colors[colors.length - 1];
    	}
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	int color = 0;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	selectColor =  findColor(event.getX(), event.getY());
        	if(onColorChangedListener != null) {
    			onColorChangedListener.colorTracking(selectColor);
        		onColorChangedListener.colorChanged(selectColor);
        	}
        	invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
        	color = findColor(event.getX(), event.getY());
        	if(color != selectColor) {
        		if(onColorChangedListener != null) {
        			onColorChangedListener.colorTracking(color);
        		}
        		selectColor = color;
        	}
            break;
        case MotionEvent.ACTION_UP:
        	selectColor =  findColor(event.getX(), event.getY());
        	if(onColorChangedListener != null) {
    			onColorChangedListener.colorTracking(selectColor);
        		onColorChangedListener.colorChanged(selectColor);
        	}
        	invalidate();
            break;
	    }
	    return false;
    }

}
