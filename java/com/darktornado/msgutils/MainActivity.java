package com.darktornado.msgutils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.darktornado.library.BottomNavigationLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {

    private Toolbar toolbar;
    private SimpleLayout simple;
    private ScriptLayout script;
    private LogLayout log;
    private SettingsLayout settings;
    private int type = Utils.TYPE_SIMPLE;
    private Menu menu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (type) {
            case Utils.TYPE_SIMPLE:
                simple.onOptionsItemSelected(item.getItemId());
                break;
            case Utils.TYPE_JS:

                break;
            case Utils.TYPE_MSG:

                break;
            case Utils.TYPE_SETTINGS:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "추가");
        menu.add(0, 1, 0, "모두 삭제");
        this.menu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        toolbar = Utils.createToolBar(this, "메신저 도구");
        Switch on = new Switch(this);
        on.setChecked(Utils.rootLoad(this, "all_on", false));
        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton swit, boolean onoff) {
                Utils.rootSave(MainActivity.this, "all_on", onoff);
            }
        });
        toolbar.addView(on, new Toolbar.LayoutParams(-2, -2, Gravity.RIGHT));

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
                menu.clear();
                menu.add(0, 0, 0, "추가");
                menu.add(0, 1, 0, "모두 삭제");
            }
        });
        layout.addBottomButton("챗봇 (JS)", R.drawable.reply_js, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(script.view);
                menu.clear();
            }
        });
        layout.addBottomButton("채팅 기록", R.drawable.chat_log, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(log.view);
                menu.clear();
            }
        });
        layout.addBottomButton("설정", R.drawable.settings, Utils.getRipple(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.replace(settings.view);
                menu.clear();
            }
        });

        layout0.addView(layout);
        layout0.setBackgroundColor(Color.WHITE);
        setContentView(layout0);
    }

}