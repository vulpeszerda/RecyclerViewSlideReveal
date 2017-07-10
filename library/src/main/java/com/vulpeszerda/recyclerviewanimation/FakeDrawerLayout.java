package com.vulpeszerda.recyclerviewanimation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDelegate;
import android.support.v4.widget.ViewDelegateDrawerLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vulpes on 2017. 7. 10..
 */

public class FakeDrawerLayout extends ViewDelegateDrawerLayout {

    @Nullable
    private View fakeTarget;

    public FakeDrawerLayout(Context context) {
        super(context);
    }

    public FakeDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FakeDrawerLayout(Context context,
                            AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFakeTarget(@Nullable View view) {
        fakeTarget = view;
    }

    @Override
    public ViewDelegate createViewDelegate() {
        return new ViewDelegate() {

            private int fakeLeft = 0;
            private int fakeVisibility = View.VISIBLE;

            @Override
            public int getLeft(@NonNull View view) {
                if (view == fakeTarget) {
                    return fakeLeft + FakeDrawerLayout.this.getWidth();
                }
                return super.getLeft(view);
            }

            @Override
            public void offsetLeftAndRight(@NonNull View view, int offset) {
                if (view == fakeTarget) {
                    fakeLeft += offset;
                    if (fakeLeft < 0) {
                        if (view.getLeft() != 0) {
                            ViewCompat.offsetLeftAndRight(view, -view.getLeft());
                        }
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                } else {
                    super.offsetLeftAndRight(view, offset);
                }
            }

            @Override
            public void setVisibility(View view, int visibility) {
                if (view == fakeTarget) {
                    fakeVisibility = visibility;
                    return;
                }
                super.setVisibility(view, visibility);
            }

            @Override
            public int getVisibility(View view) {
                if (view == fakeTarget) {
                    return fakeVisibility;
                }
                return super.getVisibility(view);
            }
        };
    }
}
