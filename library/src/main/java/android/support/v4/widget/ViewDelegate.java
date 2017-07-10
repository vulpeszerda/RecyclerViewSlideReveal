package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vulpes on 2017. 7. 10..
 */

public class ViewDelegate {

    public int getLeft(@NonNull View view) {
        return view.getLeft();
    }

    public int getTop(@NonNull View view) {
        return view.getTop();
    }

    public int getRight(@NonNull View view) {
        return view.getRight();
    }

    public int getBottom(@NonNull View view) {
        return view.getBottom();
    }

    public void offsetLeftAndRight(@NonNull View view, int offset) {
        ViewCompat.offsetLeftAndRight(view, offset);
    }

    public void offsetTopAndBottom(@NonNull View view, int offset) {
        ViewCompat.offsetTopAndBottom(view, offset);
    }

    public boolean canScrollHorizontally(@NonNull View view, int dx) {
        return ViewCompat.canScrollHorizontally(view, dx);
    }

    public boolean canScrollVertically(@NonNull View view, int dx) {
        return ViewCompat.canScrollVertically(view, dx);
    }

    public int getScrollX(@NonNull View view) {
        return view.getScrollX();
    }

    public int getScrollY(@NonNull View view) {
        return view.getScrollY();
    }

    public int getWidth(@NonNull View view) {
        return view.getWidth();
    }

    public int getVisibility(View view) {
        return view.getVisibility();
    }

    public void setVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public int getLeftMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) params).leftMargin;
        }
        return 0;
    }

    public int getRightMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) params).leftMargin;
        }
        return 0;
    }

    public int getTopMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) params).leftMargin;
        }
        return 0;
    }

    public int getBottomMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) params).leftMargin;
        }
        return 0;
    }

    public int getMeasuredWidth(View view) {
        return view.getMeasuredWidth();
    }

    public int getMeasuredHeight(View view) {
        return view.getMeasuredHeight();
    }

    public int getLayoutWidth(View view) {
        return view.getLayoutParams().width;
    }

    public int getLayoutHeight(View view) {
        return view.getLayoutParams().height;
    }
}
