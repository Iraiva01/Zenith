package com.zenith.launcher.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class ClockArcView extends View {
    private Paint paint;
    private RectF rectF;
    private boolean isRunning = true;

    public ClockArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setAntiAlias(true);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        
        float padding = paint.getStrokeWidth() / 2;
        rectF.set(padding, padding, size - padding, size - padding);
        
        Calendar calendar = Calendar.getInstance();
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        
        // Calculate sweep angle based on seconds (smooth transition)
        float sweepAngle = ((seconds + millis / 1000f) / 60f) * 360f;
        
        // Start from top (-90 degrees)
        canvas.drawArc(rectF, -90, sweepAngle, false, paint);
        
        if (isRunning) {
            postInvalidateDelayed(16); // ~60fps
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isRunning = true;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }
}
