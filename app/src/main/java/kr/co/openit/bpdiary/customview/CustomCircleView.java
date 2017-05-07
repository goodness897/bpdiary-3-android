package kr.co.openit.bpdiary.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import kr.co.openit.bpdiary.R;

/**
 * Created by Mu on 2017-01-04.
 */

public class CustomCircleView extends View
{
    private static final int DEFAULT_CIRCLE_COLOR = Color.RED;

    private int circleColor;

    private Paint paint;

    public CustomCircleView(Context context)
    {
        super(context);
        init(context, null);
    }

    public CustomCircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        paint = new Paint();
        paint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleView);

        circleColor = a.getColor(R.styleable.CustomCircleView_circle_color, DEFAULT_CIRCLE_COLOR);
    }

    public void setCircleColor(int circleColor)
    {
        this.circleColor = circleColor;
        invalidate();
    }

    public int getCircleColor()
    {
        return circleColor;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);

        paint.setColor(circleColor);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}