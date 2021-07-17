package com.darktornado.msgutils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class Utils {
    public static final int TYPE_SIMPLE = 1;
    public static final int TYPE_JS = 2;
    public static final int TYPE_MSG = 3;
    public static final int TYPE_MISC = 4;

    public static Toolbar createToolBar(final Activity ctx, String title) {
        Toolbar toolbar = new Toolbar(ctx);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#F48FB1"));
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
        margin.setMargins(0, 0, 0, dip2px(ctx, 8));
        toolbar.setLayoutParams(margin);
        toolbar.setElevation(dip2px(ctx, 5));
        return toolbar;
    }

    public static Drawable getRipple() {
        int color = Color.parseColor("#FCE4EC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(Color.WHITE), new ColorDrawable(color), null);
        } else {
            return new ColorDrawable(color);
        }
    }

    private static int dip2px(Context ctx, int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
