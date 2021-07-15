package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

public class BaseLayout {
    protected Activity ctx;

    public BaseLayout(Activity ctx) {
        this.ctx = ctx;
    }

    protected void showDialog(String title, String msg) {
        ctx.runOnUiThread(() -> {
            try {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle(title);
                dialog.setMessage(msg);
                dialog.setNegativeButton("닫기", null);
                dialog.show();
            } catch (Exception e) {
                toast(e.toString());
            }
        });
    }

    protected void toast(final String msg) {
        ctx.runOnUiThread(() -> {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        });
    }

    protected int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
