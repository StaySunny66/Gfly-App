package cn.shilight.gfly.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.shilight.gfly.R;


public class DashboardView extends View {
    private Paint circlePaint;
    private Paint scalePaint;
    private Paint pointerPaint;

    private int circleColor;
    private int scaleColor;
    private int pointerColor;

    private float pointerAngle = 45f; // 初始指针角度

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashboardView);
        circleColor = typedArray.getColor(R.styleable.DashboardView_circleColor, Color.GRAY);
        scaleColor = typedArray.getColor(R.styleable.DashboardView_scaleColor, Color.BLACK);
        pointerColor = typedArray.getColor(R.styleable.DashboardView_pointerColor, Color.RED);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;


        // 画刻度
        scalePaint.setColor(scaleColor);
        scalePaint.setStrokeWidth(5f);

        for (int i = 180; i < 360; i += 10) {
            float startX = width / 2f + (float) (radius * 0.9 * Math.cos(Math.toRadians(i)));
            float startY = height / 2f + (float) (radius * 0.9 * Math.sin(Math.toRadians(i)));

            float stopX = width / 2f + (float) (radius * Math.cos(Math.toRadians(i)));
            float stopY = height / 2f + (float) (radius * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, scalePaint);
        }

        // 画指针
        pointerPaint.setColor(pointerColor);
        pointerPaint.setStrokeWidth(8f);

        float pointerX = width / 2f + (float) (radius * 0.7 * Math.cos(Math.toRadians(pointerAngle)));
        float pointerY = height / 2f + (float) (radius * 0.7 * Math.sin(Math.toRadians(pointerAngle)));

        canvas.drawLine(width / 2f, height / 2f, pointerX, pointerY, pointerPaint);
    }

    // 设置指针角度
    public void setPointerAngle(float angle) {
        this.pointerAngle = angle;
        invalidate(); // 通知 View 重新绘制
    }
}
