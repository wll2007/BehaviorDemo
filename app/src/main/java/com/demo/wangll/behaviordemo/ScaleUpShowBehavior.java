package com.demo.wangll.behaviordemo;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class ScaleUpShowBehavior extends FloatingActionButton.Behavior {

    private boolean isAnimatingOut = false;
    public ScaleUpShowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e("onNestedScroll",dyConsumed+"*****"+dyUnconsumed);
        if (((dyConsumed > 0 && dyUnconsumed == 0)||(dyConsumed == 0 && dyUnconsumed > 0))&&child.getVisibility()!=View.VISIBLE) {
            AnimatorUtil.scaleShow(child,null);
        }

        if ((/*(dyConsumed < 0 && dyUnconsumed == 0)||*/(dyConsumed == 0 && dyUnconsumed <= 0))&&child.getVisibility()!=View.GONE&&!isAnimatingOut) {
                AnimatorUtil.scaleHide(child, viewPropertyAnimatorListener);
        }
    }


    private ViewPropertyAnimatorListener viewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {

        @Override
        public void onAnimationStart(View view) {
            isAnimatingOut = true;
        }

        @Override
        public void onAnimationEnd(View view) {
            isAnimatingOut = false;
            view.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(View arg0) {
            isAnimatingOut = false;
        }
    };

}
