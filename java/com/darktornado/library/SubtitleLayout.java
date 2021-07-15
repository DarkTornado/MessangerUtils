package com.darktornado.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubtitleLayout extends LinearLayout {

    private Context ctx;
    private LinearLayout layout;

    public SubtitleLayout(Context context) {
        super(context);
        init(context);
    }

    public SubtitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
        setOrientation(1);
        layout = new LinearLayout(ctx);
        layout.setOrientation(0);
        addView(layout);
        layout.setElevation(dip2px(5));
        int pad = dip2px(10);
        layout.setPadding(pad, pad, pad, pad);
    }

    public TextView addSubTitle(String text, View.OnClickListener listener) {
        return addSubTitle(text, 15, Color.GRAY, Color.LTGRAY, listener, false);
    }

    public TextView addSubTitle(String text, int color, int focusedColor, View.OnClickListener listener) {
        return addSubTitle(text, 15, color, focusedColor, listener, false);
    }

    public TextView addSubTitle(String text, int color, int focusedColor, View.OnClickListener listener, boolean focused) {
        return addSubTitle(text, 15, color, focusedColor, listener, focused);
    }

    public TextView addSubTitle(String text, float size, int color, int focusedColor, View.OnClickListener listener) {
        return addSubTitle(text, size, color, focusedColor, listener, false);
    }

    public TextView addSubTitle(String text, float size, int color, int focusedColor, View.OnClickListener listener, boolean focused) {
        final TextView txt = new TextView(ctx);
        txt.setText(text);
        txt.setTextSize(size);
        if(focused) txt.setTextColor(focusedColor);
        else txt.setTextColor(color);
        txt.setGravity(Gravity.CENTER);
        txt.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        txt.setOnClickListener(listener);
        layout.addView(txt);
        layout.setWeightSum(layout.getChildCount());
        txt.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mv) {
                if (mv.getAction() == MotionEvent.ACTION_UP) {
                    for (int n = 0; n < layout.getChildCount(); n++) {
                        ((TextView) layout.getChildAt(n)).setTextColor(color);
                    }
                    txt.setTextColor(focusedColor);
                }
                return false;
            }
        });
        return txt;
    }

    public void replace(View view) {
        removeAllViews();
        addView(layout);
        addView(view);
    }

    public void replace(View view, Animation ani) {
        removeAllViews();
        addView(layout);
        view.startAnimation(ani);
        addView(view);
    }

    public void setSubtitleBackgroundColor(int color) {
        layout.setBackgroundColor(color);
    }

    public void setSubtitleBackground(Drawable drawable) {
        layout.setBackground(drawable);
    }

    public void setSubtitleBackgroundDrawable(Drawable drawable) {
        layout.setBackgroundDrawable(drawable);
    }

    public void setFocus(TextView subtitle, int color, int focusedColor) {
        for (int n = 0; n < layout.getChildCount(); n++) {
            ((TextView) layout.getChildAt(n)).setTextColor(color);
        }
        subtitle.setTextColor(focusedColor);
    }

    private void setTopPadding(int p1, int p2, int p3, int p4) {
        layout.setPadding(p1, p2, p3, p4);
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
