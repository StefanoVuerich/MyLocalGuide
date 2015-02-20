package com.lqc.mylocalguide.customsviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class TriangleButton extends View {

	public TriangleButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TriangleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TriangleButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(android.graphics.Color.BLACK);
		canvas.drawPaint(paint);

		Point a = new Point(0, 0);
		Point b = new Point(0, 100);
		Point c = new Point(87, 50);

		Path path = new Path();
		path.setFillType(FillType.EVEN_ODD);
		path.moveTo(a.x, a.y);
		path.lineTo(b.x, b.y);
		path.moveTo(b.x, b.y);
		path.lineTo(c.x, c.y);
		path.moveTo(c.x, c.y);
		path.lineTo(a.x, a.y);
		path.close();

		canvas.drawPath(path, paint);

	}

}
