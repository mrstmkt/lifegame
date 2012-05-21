package jp.morishi.lifegame.wallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ColorView extends View {

	private int color;
	private Paint paint;
	private Paint linePaint;
	public ColorView(Context context, int color) {
		super(context);
		this.color = color;
		this.paint = new Paint();
		this.paint.setColor(color);
		this.linePaint = new Paint();
		this.linePaint.setStyle(Paint.Style.STROKE);
		this.linePaint.setStrokeWidth(3);
		this.linePaint.setColor(color ^ 0x00FFFFFF);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(this.color);
		linePaint.setColor(this.color ^ 0x00FFFFFF);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		canvas.drawRect(0, 0, getWidth(), getHeight(), linePaint);
	}
	public void setColor(int color) {
		this.color = color;
		invalidate();
	}
}
