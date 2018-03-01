package com.demo.wangll.behaviordemo;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class ScaleDownShowBehavior extends FloatingActionButton.Behavior {

    public interface OnStateChangedListener {
        void onChanged(boolean isShow);
    }


    private OnStateChangedListener mOnStateChangedListener;

    public void setOnStateChangedListener(OnStateChangedListener mOnStateChangedListener) {
        this.mOnStateChangedListener = mOnStateChangedListener;
    }

    private boolean isAnimatingOut = false;
    public ScaleDownShowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e("onNestedScroll",dyConsumed+"*****"+dyUnconsumed);
        if ((dyConsumed > 0 || dyUnconsumed > 0) && !isAnimatingOut
                && child.getVisibility() == View.VISIBLE) {// 手指上滑，隐藏FAB
            AnimatorUtil.scaleHide(child, listener);
            if (mOnStateChangedListener != null) {
                mOnStateChangedListener.onChanged(false);
            }
        } else if ((dyConsumed < 0 || dyUnconsumed < 0) && child.getVisibility() != View.VISIBLE) {
            AnimatorUtil.scaleShow(child, null);// 手指下滑，显示FAB
            if (mOnStateChangedListener != null) {
                mOnStateChangedListener.onChanged(true);
            }
        }
    }

    public static <V extends View> ScaleDownShowBehavior from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("这个View不是CoordinatorLayout的子View");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof ScaleDownShowBehavior)) {
            throw new IllegalArgumentException("这个View的Behaviro不是ScaleDownShowBehavior");
        }
        return (ScaleDownShowBehavior) behavior;
    }

    private ViewPropertyAnimatorListener listener = new ViewPropertyAnimatorListener() {
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
