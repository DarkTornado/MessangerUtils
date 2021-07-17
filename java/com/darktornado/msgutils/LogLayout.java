package com.darktornado.msgutils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LogLayout extends BaseLayout {

    public LinearLayout view;

    public LogLayout(Activity ctx) {
        super(ctx);
        view = new LinearLayout(ctx);
    }

}
