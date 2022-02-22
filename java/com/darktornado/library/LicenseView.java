package com.darktornado.library;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LicenseView extends LinearLayout {

    private final Context ctx;
    private TextView title, subtitle, license;

    public LicenseView(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        init();
    }

    public void setTitle(String txt) {
        title.setText(Html.fromHtml("<b>" + txt + "<b>"));
    }

    public void setSubtitle(String txt) {
        subtitle.setText(txt);
    }

    public void setLicense(final String name, String path) {
        final String value = loadLicense(path);
        if (value.length() > 1500) {
            license.setText(Html.fromHtml(value.substring(0, 1500).replace("\n", "<br>") + "...<font color='#757575'><b>[Show All]</b></font>"));
            license.setOnClickListener(v -> showDialog(name, value));
        } else {
            license.setText(value);
        }
    }

    private void init() {
        setOrientation(1);
        int pad = dip2px(10);
        title = new TextView(ctx);
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        title.setPadding(pad, 0, pad, dip2px(1));
        addView(title);
        subtitle = new TextView(ctx);
        subtitle.setTextSize(20);
        subtitle.setTextColor(Color.BLACK);
        subtitle.setPadding(pad, 0, pad, pad);
        addView(subtitle);

        license = new TextView(ctx);
        license.setTextSize(17);
        license.setTextColor(Color.BLACK);
        license.setPadding(pad, pad, pad, pad);
        license.setBackgroundColor(Color.argb(50, 0, 0, 0));
        addView(license);

        setPadding(pad, pad, pad, dip2px(20));
    }

    private String loadLicense(String path) {
        try {
            InputStreamReader isr = new InputStreamReader(ctx.getAssets().open(path));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder str = new StringBuilder(br.readLine());
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append("\n").append(line);
            }
            isr.close();
            br.close();
            return str.toString();
        } catch (Exception e) {
            return "Failed to load License";
        }
    }


    private void showDialog(String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setNegativeButton("닫기", null);
        dialog.show();
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
