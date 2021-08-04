package com.darktornado.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darktornado.msgutils.R;
import com.darktornado.msgutils.SQLManager;

import java.io.File;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Item> list = new ArrayList<>();
    private int size = -1;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int index) {
        return list.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        Context ctx = parent.getContext();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_item, parent, false);
        }

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);

        Item item = list.get(pos);
        icon.setImageDrawable(item.icon);
        title.setText(item.title);
        subtitle.setText(item.subtitle);

        if (size > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size, 1);
            params.gravity = Gravity.START | Gravity.CENTER;
            icon.setLayoutParams(params);
            view.setLayoutParams(new LinearLayout.LayoutParams(-1, dip2px(ctx, 45), 1));
        }

        return view;
    }

    public void setIconSize(int size) {
        this.size = size;
    }

    public void setItems(ArrayList<Item> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    private int dip2px(Context ctx, int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }
}
