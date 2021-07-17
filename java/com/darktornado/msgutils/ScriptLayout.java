package com.darktornado.msgutils;

import android.app.Activity;
import android.widget.LinearLayout;

public class ScriptLayout extends BaseLayout {

    public LinearLayout view;

    public ScriptLayout(Activity ctx) {
        super(ctx);
        view = new LinearLayout(ctx);
    }
}
