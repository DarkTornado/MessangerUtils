package com.darktornado.listview;

import android.graphics.drawable.Drawable;

public class Item {
    public String title, subtitle;
    public Drawable icon;

    public Item(String title, String subtitle, Drawable icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }
}
