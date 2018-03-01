package com.demo.wangll.behaviordemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.ViewGroupUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/2/8 0008.
 */

public class BasicBehavior<T extends View> extends CoordinatorLayout.Behavior<T> {

    private static final boolean AUTO_HIDE_DEFAULT = true;

    private Rect mTmpRect;
    private FloatingActionButton.OnVisibilityChangedListener mInternalAutoHideListener;
    private boolean mAutoHideEnabled;

    public BasicBehavior() {
        super();
        mAutoHideEnabled = AUTO_HIDE_DEFAULT;
    }

    public BasicBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                android.support.design.R.styleable.FloatingActionButton_Behavior_Layout);
        mAutoHideEnabled = a.getBoolean(
                android.support.design.R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide,
                AUTO_HIDE_DEFAULT);
        a.recycle();
    }

    /**
     * Sets whether the associated FloatingActionButton automatically hides when there is
     * not enough space to be displayed. This works with {@link AppBarLayout}
     * and {@link BottomSheetBehavior}.
     *
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     * @param autoHide true to enable automatic hiding
     */
    public void setAutoHideEnabled(boolean autoHide) {
        mAutoHideEnabled = autoHide;
    }

    /**
     * Returns whether the associated FloatingActionButton automatically hides when there is
     * not enough space to be displayed.
     *
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     * @return true if enabled
     */
    public boolean isAutoHideEnabled() {
        return mAutoHideEnabled;
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
            // we dodge any Snackbars
            lp.dodgeInsetEdges = Gravity.BOTTOM;
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, T child,
                                          View dependency) {
        if (dependency instanceof AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the FAB is anchored to the AppBarLayout
            updateFabVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child);
        } else if (isBottomSheet(dependency)) {
            updateFabVisibilityForBottomSheet(dependency, child);
        }
        return false;
    }

    private static boolean isBottomSheet(@NonNull View view) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof CoordinatorLayout.LayoutParams) {
            return ((CoordinatorLayout.LayoutParams) lp)
                    .getBehavior() instanceof BottomSheetBehavior;
        }
        return false;
    }

    @VisibleForTesting
    void setInternalAutoHideListener(FloatingActionButton.OnVisibilityChangedListener listener) {
        mInternalAutoHideListener = listener;
    }

    private boolean shouldUpdateVisibility(View dependency, T child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (!mAutoHideEnabled) {
            return false;
        }

        if (lp.getAnchorId() != dependency.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the FAB
            return false;
        }

        //noinspection RedundantIfStatement
        if (child.getVisibility() != View.VISIBLE) {
            // The view isn't set to be visible so skip changing its visibility
            return false;
        }

        return true;
    }

    private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout parent,
                                                       AppBarLayout appBarLayout, FloatingActionButton child) {
        if (!shouldUpdateVisibility(appBarLayout, child)) {
            return false;
        }

        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }

        // First, let's get the visible rect of the dependency
        final Rect rect = mTmpRect;
        ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);

        if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
            // If the anchor's bottom is below the seam, we'll animate our FAB out
            child.hide(mInternalAutoHideListener, false);
        } else {
            // Else, we'll animate our FAB back in
            child.show(mInternalAutoHideListener, false);
        }
        return true;
    }

    private boolean updateFabVisibilityForBottomSheet(View bottomSheet,
                                                      FloatingActionButton child) {
        if (!shouldUpdateVisibility(bottomSheet, child)) {
            return false;
        }
        CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (bottomSheet.getTop() < child.getHeight() / 2 + lp.topMargin) {
            child.hide(mInternalAutoHideListener, false);
        } else {
            child.show(mInternalAutoHideListener, false);
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child,
                                 int layoutDirection) {
        // First, let's make sure that the visibility of the FAB is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout) {
                if (updateFabVisibilityForAppBarLayout(
                        parent, (AppBarLayout) dependency, child)) {
                    break;
                }
            } else if (isBottomSheet(dependency)) {
                if (updateFabVisibilityForBottomSheet(dependency, child)) {
                    break;
                }
            }
        }
        // Now let the CoordinatorLayout lay out the FAB
        parent.onLayoutChild(child, layoutDirection);
        // Now offset it if needed
        offsetIfNeeded(parent, child);
        return true;
    }

    @Override
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout parent,
                                     @NonNull FloatingActionButton child, @NonNull Rect rect) {
        // Since we offset so that any internal shadow padding isn't shown, we need to make
        // sure that the shadow isn't used for any dodge inset calculations
        final Rect shadowPadding = child.mShadowPadding;
        rect.set(child.getLeft() + shadowPadding.left,
                child.getTop() + shadowPadding.top,
                child.getRight() - shadowPadding.right,
                child.getBottom() - shadowPadding.bottom);
        return true;
    }

    /**
     * Pre-Lollipop we use padding so that the shadow has enough space to be drawn. This method
     * offsets our layout position so that we're positioned correctly if we're on one of
     * our parent's edges.
     */
    private void offsetIfNeeded(CoordinatorLayout parent, FloatingActionButton fab) {
        final Rect padding = fab.mShadowPadding;

        if (padding != null && padding.centerX() > 0 && padding.centerY() > 0) {
            final CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

            int offsetTB = 0, offsetLR = 0;

            if (fab.getRight() >= parent.getWidth() - lp.rightMargin) {
                // If we're on the right edge, shift it the right
                offsetLR = padding.right;
            } else if (fab.getLeft() <= lp.leftMargin) {
                // If we're on the left edge, shift it the left
                offsetLR = -padding.left;
            }
            if (fab.getBottom() >= parent.getHeight() - lp.bottomMargin) {
                // If we're on the bottom edge, shift it down
                offsetTB = padding.bottom;
            } else if (fab.getTop() <= lp.topMargin) {
                // If we're on the top edge, shift it up
                offsetTB = -padding.top;
            }

            if (offsetTB != 0) {
                ViewCompat.offsetTopAndBottom(fab, offsetTB);
            }
            if (offsetLR != 0) {
                ViewCompat.offsetLeftAndRight(fab, offsetLR);
            }
        }
    }

}
