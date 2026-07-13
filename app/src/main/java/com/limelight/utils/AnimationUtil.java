package com.limelight.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.WeakHashMap;

public class AnimationUtil {
    private static WeakHashMap<View, ObjectAnimator> hashMap = new WeakHashMap<>();

    public static void startRotateView(View view){
        startRotateView(view, 1000);
    }

    public static void startRotateView(View view, int time){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 359);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(time);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hashMap.remove(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                hashMap.remove(view);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        hashMap.put(view, objectAnimator);
    }

    public static void stopRotateView(View view){
        ObjectAnimator objectAnimator = hashMap.get(view);
        if(objectAnimator != null){
            objectAnimator.cancel();
            hashMap.remove(view);
        }
    }

    public static boolean isViewAnimating(View view){
        ObjectAnimator objectAnimator = hashMap.get(view);
        if(objectAnimator != null){
            return objectAnimator.isRunning();
        }
        return false;
    }

}
