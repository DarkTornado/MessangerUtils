package com.darktornado.msgutils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.darktornado.library.CodeEditor;

public class ScriptLayout extends BaseLayout {

    public FrameLayout view;
    private CodeEditor editor;

    public ScriptLayout(Activity ctx) {
        super(ctx);
        init();
    }

    public void onOptionsItemSelected(int id) {
        switch (id) {
            case 0:
                String src = editor.getText().toString();
                String result = Utils.rootSave(ctx, "response.js", src);
                if (result == null) toast("저장되었어요.");
                else toast("저장되지 않았어요.\n오류: " + result);
                break;
            case 1:

                break;
        }
    }

    public void updateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, 0, 0, "저장").setIcon(R.drawable.save).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "리로드").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    private void init() {
        view = new FrameLayout(ctx);
        try {
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(1);

            editor = new CodeEditor(ctx);
            layout.addView(editor);
            layout.setPadding(dip2px(8), 0, 0, dip2px(30));
            ScrollView scroll = new ScrollView(ctx);
            scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            scroll.addView(layout);

            view.addView(scroll);
            view.addView(createHotKeys());
            view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            scroll.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

            new Thread(() -> {
                String src = Utils.rootRead(ctx, "response.js");
                ctx.runOnUiThread(() -> editor.setText(src));
            }).start();

        } catch (Exception e) {
            toast("Error in JS Layout.\n" + e.toString());
        }
    }

    private HorizontalScrollView createHotKeys() {
        HorizontalScrollView scroll = new HorizontalScrollView(ctx);
        try {
            LinearLayout layout = new LinearLayout(ctx);
            layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(dip2px(30), dip2px(35));
            int mar = dip2px(1);
            margin.setMargins(mar, mar, mar, mar);

            final char[] texts = "←→{}();=.'\"+-*/\\:[]&|<>".toCharArray();
            TextView[] txts = new TextView[texts.length];
            for (int n = 0; n < texts.length; n++) {
                txts[n] = new TextView(ctx);
                txts[n].setText(texts[n] + "");
                txts[n].setTextSize(15);
                txts[n].setTextColor(Color.WHITE);
                txts[n].setId(n);
                txts[n].setLayoutParams(margin);
                txts[n].setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                txts[n].setBackgroundColor(Color.parseColor("#5AF48FB1"));
                txts[n].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case 0:
                                if (!editor.undo()) toast("더 이상 되돌릴 내용이 없어요.");
                                break;
                            case 1:
                                if (!editor.redo()) toast("더 이상 다시 실행할 내용이 없어요.");
                                break;
                            default:
                                editor.insertWord(texts[view.getId()] + "");
                                break;
                        }
                    }
                });
                layout.addView(txts[n]);
            }

            layout.setBackgroundColor(Color.parseColor("#5AFCE4EC"));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER;
            scroll.setLayoutParams(params);
            scroll.setHorizontalScrollBarEnabled(false);
            scroll.addView(layout);
        } catch (Exception e) {
            toast(e.toString());
        }
        return scroll;
    }

}
