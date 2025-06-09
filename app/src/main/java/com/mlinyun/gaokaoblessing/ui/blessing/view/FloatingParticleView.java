package com.mlinyun.gaokaoblessing.ui.blessing.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.mlinyun.gaokaoblessing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 浮动粒子效果View
 * 实现3D背景浮动金色粒子效果
 */
public class FloatingParticleView extends View {

    private static final int PARTICLE_COUNT = 15; // 粒子数量
    private static final long ANIMATION_DURATION = 8000; // 动画时长

    private List<Particle> particles;
    private Paint particlePaint;
    private Random random;
    private boolean isAnimating;

    public FloatingParticleView(Context context) {
        super(context);
        init();
    }

    public FloatingParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        random = new Random();
        particles = new ArrayList<>();

        // 初始化画笔
        particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        particlePaint.setColor(getContext().getColor(R.color.golden_yellow));
        particlePaint.setAlpha(120); // 半透明效果

        // 创建粒子
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 重新初始化粒子位置
        for (Particle particle : particles) {
            particle.reset(w, h);
        }

        // 开始动画
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制粒子
        for (Particle particle : particles) {
            // 根据深度调整透明度和大小
            float alpha = 60 + (particle.depth * 100);
            particlePaint.setAlpha((int) Math.min(alpha, 255));

            float size = particle.size * (0.5f + particle.depth * 0.5f);
            canvas.drawCircle(particle.x, particle.y, size, particlePaint);

            // 绘制光晕效果
            particlePaint.setAlpha((int) (alpha * 0.3f));
            canvas.drawCircle(particle.x, particle.y, size * 2, particlePaint);
        }
    }

    /**
     * 开始粒子动画
     */
    public void startAnimation() {
        if (isAnimating) return;

        isAnimating = true;

        for (Particle particle : particles) {
            animateParticle(particle);
        }
    }

    /**
     * 停止粒子动画
     */
    public void stopAnimation() {
        isAnimating = false;
        clearAnimation();
    }

    private void animateParticle(Particle particle) {
        if (!isAnimating) return;

        // 随机生成终点位置
        float endX = random.nextFloat() * getWidth();
        float endY = random.nextFloat() * getHeight();

        // X轴动画
        ObjectAnimator animX = ObjectAnimator.ofFloat(particle, "x", particle.x, endX);
        animX.setDuration(ANIMATION_DURATION + random.nextInt(2000));
        animX.setInterpolator(new LinearInterpolator());

        // Y轴动画
        ObjectAnimator animY = ObjectAnimator.ofFloat(particle, "y", particle.y, endY);
        animY.setDuration(ANIMATION_DURATION + random.nextInt(2000));
        animY.setInterpolator(new LinearInterpolator());

        // 深度动画（产生3D效果）
        ValueAnimator depthAnim = ValueAnimator.ofFloat(particle.depth, random.nextFloat());
        depthAnim.setDuration(ANIMATION_DURATION);
        depthAnim.addUpdateListener(animation -> {
            particle.depth = (float) animation.getAnimatedValue();
            invalidate();
        });

        // 动画结束后重新开始
        animX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimating) {
                    particle.reset(getWidth(), getHeight());
                    postDelayed(() -> animateParticle(particle), random.nextInt(1000));
                }
            }
        });

        // 启动动画
        animX.start();
        animY.start();
        depthAnim.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    /**
     * 粒子类
     */
    private class Particle {
        float x, y;          // 位置
        float size;          // 大小
        float depth;         // 深度（0-1，用于3D效果）
        float speed;         // 速度

        void reset(int width, int height) {
            x = random.nextFloat() * width;
            y = random.nextFloat() * height;
            size = 3 + random.nextFloat() * 8; // 3-11px
            depth = random.nextFloat();
            speed = 0.5f + random.nextFloat() * 1.5f;
        }

        // Getter和Setter方法用于ObjectAnimator
        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
