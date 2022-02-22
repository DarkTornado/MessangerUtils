package com.darktornado.msgutils.botapi;

import android.app.Person;
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
import android.util.Pair;

import java.io.ByteArrayOutputStream;

public class ImageDB {

    private final Bitmap icon, bitmap, room;
    private final String icon64, base64;

    public ImageDB(Context ctx, StatusBarNotification sbn) {
        Pair<Bitmap, Bitmap> icon = getLargeIcon(ctx, sbn);
        this.icon = icon.first;
        this.room = icon.second;
        this.bitmap = getImageBitmap(sbn.getNotification().extras);
        icon64 = encodeImage(icon.first);
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

    public String getRoomImage() {
        return encodeImage(room);
    }

    public Bitmap getRoomBitmap() {
        return room;
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

    private Pair<Bitmap, Bitmap> getLargeIcon(Context ctx, StatusBarNotification sbn) {
        /* 기존 방식대로 아이콘을 가지고 옴 */
        Bitmap icon;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Icon _icon = sbn.getNotification().getLargeIcon();
            icon = icon2Bitmap(ctx, _icon);
        } else {
            icon = sbn.getNotification().largeIcon;
        }

        /* 새로운 방식으로 아이콘을 가지고 옴 (안드11 & 카톡9.7.0 대응) */
        Bundle bundle = sbn.getNotification().extras;
        Person person = bundle.getParcelable("android.messagingUser");

        /* 없으면 그냥 기존꺼 반환
        * 안드/카톡 버전 확인 안해도 알아서 됨 */
        if (person == null) return new Pair<>(icon, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //Person.getIcon(); 때문에 넣음
            return new Pair<>(icon2Bitmap(ctx, person.getIcon()), icon);
        }
        return new Pair<>(icon, null);
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
