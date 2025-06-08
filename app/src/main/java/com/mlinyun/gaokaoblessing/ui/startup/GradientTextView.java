package com.mlinyun.gaokaoblessing.ui.startup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * 渐变文字TextView
 * 用于实现启动页标题的渐变文字效果
 */
public class GradientTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint textPaint;
    private LinearGradient linearGradient;
    private boolean gradientCreated = false;

    public GradientTextView(Context context) {
        super(context);
        init();
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint(getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!gradientCreated) {
            createGradient();
        }

        if (linearGradient != null) {
            textPaint.setShader(linearGradient);

            // 绘制渐变文字
            String text = getText().toString();
            float x = getPaddingLeft();
            float y = getBaseline();

            canvas.drawText(text, x, y, textPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    private void createGradient() {
        if (getWidth() > 0) {
            int startColor = getResources().getColor(com.mlinyun.gaokaoblessing.R.color.accent_gold, null);
            int endColor = getResources().getColor(com.mlinyun.gaokaoblessing.R.color.accent_orange, null);

            linearGradient = new LinearGradient(
                    0, 0, getWidth(), 0,
                    new int[]{startColor, endColor},
                    null,
                    Shader.TileMode.CLAMP
            );

            gradientCreated = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0) {
            gradientCreated = false;
            invalidate();
        }
    }
}
