package com.darktornado.msgutils;

import android.app.Activity;
import android.widget.LinearLayout;

public class SettingsLayout extends BaseLayout {

    public LinearLayout view;

    public SettingsLayout(Activity ctx) {
        super(ctx);
        view = new LinearLayout(ctx);
    }
}
