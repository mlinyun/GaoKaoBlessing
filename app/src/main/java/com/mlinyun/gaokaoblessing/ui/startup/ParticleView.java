package com.mlinyun.gaokaoblessing.ui.startup;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.mlinyun.gaokaoblessing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 粒子动画效果View
 * 实现启动页的浮动粒子背景效果
 */
public class ParticleView extends View {

    private static final int PARTICLE_COUNT = 8;
    private static final int MIN_SIZE = 6;
    private static final int MAX_SIZE = 18;
    private static final int ANIMATION_DURATION = 8000;

    private List<Particle> particles = new ArrayList<>();
    private Paint paint;
    private Random random = new Random();
    private ValueAnimator animator;

    public ParticleView(Context context) {
        super(context);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        // 创建粒子
        createParticles();

        // 启动动画
        startAnimation();
    }

    private void createParticles() {
        particles.clear();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle particle = new Particle();
            particle.x = random.nextFloat() * getWidth();
            particle.y = getHeight() + random.nextFloat() * 200; // 从底部开始
            particle.size = MIN_SIZE + random.nextFloat() * (MAX_SIZE - MIN_SIZE);
            particle.speed = 0.5f + random.nextFloat() * 1.5f;
            particle.alpha = 0.3f + random.nextFloat() * 0.5f;
            particle.delay = random.nextInt(ANIMATION_DURATION);

            // 设置渐变色
            int goldColor = getContext().getColor(R.color.accent_gold);
            int orangeColor = getContext().getColor(R.color.accent_orange);
            particle.color1 = goldColor;
            particle.color2 = orangeColor;

            particles.add(particle);
        }
    }

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIMATION_DURATION);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            updateParticles(progress);
            invalidate();
        });

        animator.start();
    }

    private void updateParticles(float progress) {
        for (Particle particle : particles) {
            // 计算粒子的生命周期进度
            float particleProgress = (progress * ANIMATION_DURATION - particle.delay) / (ANIMATION_DURATION - particle.delay);

            if (particleProgress < 0) {
                particle.currentAlpha = 0;
                continue;
            }

            if (particleProgress > 1) {
                particleProgress = particleProgress - (int) particleProgress;
            }

            // 更新位置
            particle.currentY = getHeight() - particleProgress * (getHeight() + particle.size);
            particle.currentX = particle.x + (float) Math.sin(particleProgress * Math.PI * 4) * 30;

            // 更新透明度（fade in -> 保持 -> fade out）
            if (particleProgress < 0.1f) {
                particle.currentAlpha = particle.alpha * (particleProgress / 0.1f);
            } else if (particleProgress > 0.9f) {
                particle.currentAlpha = particle.alpha * ((1f - particleProgress) / 0.1f);
            } else {
                particle.currentAlpha = particle.alpha;
            }

            // 更新旋转
            particle.rotation = particleProgress * 360f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Particle particle : particles) {
            if (particle.currentAlpha <= 0) continue;

            // 创建径向渐变
            RadialGradient gradient = new RadialGradient(
                    particle.currentX,
                    particle.currentY,
                    particle.size / 2,
                    particle.color1,
                    particle.color2,
                    Shader.TileMode.CLAMP
            );

            paint.setShader(gradient);
            paint.setAlpha((int) (255 * particle.currentAlpha));

            // 绘制粒子
            canvas.save();
            canvas.rotate(particle.rotation, particle.currentX, particle.currentY);
            canvas.drawCircle(particle.currentX, particle.currentY, particle.size / 2, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 重新创建粒子以适应新的尺寸
        if (w > 0 && h > 0) {
            createParticles();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    /**
     * 粒子类
     */
    private static class Particle {
        float x, y; // 初始位置
        float currentX, currentY; // 当前位置
        float size; // 粒子大小
        float speed; // 移动速度
        float alpha; // 最大透明度
        float currentAlpha; // 当前透明度
        float rotation; // 旋转角度
        int delay; // 动画延迟
        int color1, color2; // 渐变颜色
    }
}
