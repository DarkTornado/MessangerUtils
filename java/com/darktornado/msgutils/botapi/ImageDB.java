package com.darktornado.msgutils.botapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageDB {

    private final Bitmap icon, bitmap;
    private final String icon64, base64;

    public ImageDB(Context ctx, StatusBarNotification sbn) {
        this.icon = getLargeIcon(ctx, sbn);
        this.bitmap = getImageBitmap(sbn.getNotification().extras);
        icon64 = encodeImage(icon);
        base64 = encodeImage(bitmap);
    }

    public String getImage() {
        return base64;
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

    public String getProfileImage() {
        return icon64;
    }

    public Bitmap getProfileBitmap() {
        return icon;
    }

    public int getProfileHash() {
        return icon64.hashCode();
    }


    private Bitmap getImageBitmap(Bundle bundle) {
        bundle = bundle.getBundle("android.wearable.EXTENSIONS");
        if (bundle == null) return null;

        Parcelable parc = bundle.getParcelable("background");
        if (parc != null) {
            return (Bitmap) parc;
        }

        return null;
    }

    @Override
    public String toString() {
        String bitmap = "null";
        if (this.bitmap != null) bitmap = this.bitmap.toString();
        String icon = "null";
        if (this.icon != null) bitmap = this.icon.toString();
        return "ImageDB{image: " + bitmap + ", profile: " + icon + "}";
    }

    private String encodeImage(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT).trim();
    }

    private Bitmap getLargeIcon(Context ctx, StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Icon icon = sbn.getNotification().getLargeIcon();
            return icon2Bitmap(ctx, icon);
        } else {
            return sbn.getNotification().largeIcon;
        }
    }

    private Bitmap icon2Bitmap(Context ctx, Icon icon) {
        if (icon == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable drawable = icon.loadDrawable(ctx);
            final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
}
