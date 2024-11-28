package com.payby.pos.common.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPagerUtils {

    private static int previousValue;

    public static void setCurrentItem(final ViewPager2 pager, int item, long duration) {
        previousValue = 0;
        int pagePxWidth = pager.getWidth();
        int currentItem = pager.getCurrentItem();
        int pxToDrag = pagePxWidth * (item - currentItem);
        final ValueAnimator animator = ValueAnimator.ofInt(0, pxToDrag);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                        int currentValue = (int) valueAnimator.getAnimatedValue();
                        float currentPxToDrag = (float) (currentValue - previousValue);
                        pager.fakeDragBy(-currentPxToDrag);
                        previousValue = currentValue;
                    }

                }
        );
        animator.addListener(
                new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                        pager.beginFakeDrag();
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        pager.endFakeDrag();
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }

                }
        );
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.start();
    }

}
