package com.darktornado.msgutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.darktornado.library.BottomNavigationLayout;

public class MainActivity extends Activity {

    private Toolbar toolbar;
    private SimpleLayout simple;
    private ScriptLayout script;
    private LogLayout log;
    private SettingsLayout settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        toolbar = Utils.createToolBar(this, "메신저 도구");
        setActionBar(toolbar);
        layout0.addView(toolbar);

        simple = new SimpleLayout(this);
        script = new ScriptLayout(this);
        log = new LogLayout(this);
        settings = new SettingsLayout(this);

        final BottomNavigationLayout layout = new BottomNavigationLayout(this);
        layout.setBottomBackgroundColor(Color.parseColor("#FCE4EC"));
        layout.addView(simple.view);

        layout.addBottomButton("자동 응답", R.drawable.reply_simple, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(simple.view);

            }
        });
        layout.addBottomButton("챗봇 (JS)", R.drawable.reply_js, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(script.view);

            }
        });
        layout.addBottomButton("채팅 기록", R.drawable.chat_log, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(log.view);

            }
        });
        layout.addBottomButton("설정", R.drawable.settings, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(settings.view);

            }
        });

        layout0.addView(layout);
        layout0.setBackgroundColor(Color.WHITE);
        setContentView(layout0);
    }

}