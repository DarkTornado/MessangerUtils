package com.darktornado.msgutils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class Utils {
    public static final int TYPE_SIMPLE = 1;
    public static final int TYPE_JS = 2;
    public static final int TYPE_CHAT_LOG = 3;
    public static final int TYPE_SETTINGS = 4;

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


    public static String rootSave(Context ctx, String name, String value) {
        String root = ctx.getFilesDir().getPath();
        return saveFile(root + "/" + name, value);
    }

    public static String rootSave(Context ctx, String name, boolean settings) {
        String root = ctx.getFilesDir().getPath();
        return saveFile(root + "/" + name, String.valueOf(settings));
    }

    public static String rootRead(Context ctx, String name) {
        String root = ctx.getFilesDir().getPath();
        return readFile(root + "/" + name);
    }

    public static boolean rootLoad(Context ctx, String name, boolean defaultSettings) {
        String data = rootRead(ctx, name);
        if (data == null) return defaultSettings;
        return data.equals("true");
    }

    public static int rootLoad(Context ctx, String name, int defaultSettings) {
        String data = rootRead(ctx, name);
        if (data == null) return defaultSettings;
        try {
            return Integer.parseInt(data);
        } catch (Exception e) {
            return defaultSettings;
        }
    }

    public static String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                str += "\n" + line;
            }
            fis.close();
            isr.close();
            br.close();
            return str;
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static String saveFile(String path, String value) {
        try {
            File file = new File(path);
            FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(value.getBytes());
            fos.close();
            return null;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static void copyToClipboard(Context ctx, String value) {
        ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("label", value));
    }

    public static String getPackage(Context ctx) {
        String data = rootRead(ctx, "packageName");
        if (data == null) return "com.kakao.talk";
        return data;
    }

    private static int dip2px(Context ctx, int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
