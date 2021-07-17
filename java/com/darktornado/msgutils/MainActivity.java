package com.darktornado.msgutils;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    SimpleLayout simple;
    ScriptLayout script;
    LogLayout log;
    SettingsLayout settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simple = new SimpleLayout(this);
        script = new ScriptLayout(this);
        log = new LogLayout(this);
        settings = new SettingsLayout(this);
    }
}