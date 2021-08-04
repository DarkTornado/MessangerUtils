package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class LogLayout extends BaseLayout {

    public LinearLayout view;

    public void onOptionsItemSelected(int id){
        switch (id){
            case 0:
                view.removeAllViews();
                init();
                break;
        }
    }

    public void updateOptionsMenu(Menu menu){
        menu.clear();
        menu.add(0, 0, 0, "새로 고침").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    public LogLayout(Activity ctx) {
        super(ctx);
        view = new LinearLayout(ctx);
        view.setOrientation(1);
        init();
    }

    private void init() {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        try {
            SQLManager sql = new SQLManager(ctx, null);
            final String[] rooms = sql.getAllTables();
            Arrays.sort(rooms);

            final EditText txt = new EditText(ctx);
            txt.setHint("검색어를 입력하세요...");
            layout.addView(txt);
            ListView list = new ListView(ctx);
            final ArrayAdapter adapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, rooms);
            list.setAdapter(adapter);
            list.setFastScrollEnabled(true);
            list.setOnItemClickListener((adapterView, view, pos, id) -> {
                int index = 0;
                String text = ((TextView) view).getText().toString();
                for (int n = 0; n < rooms.length; n++) {
                    if (text.equals(rooms[n])) {
                        index = n;
                        break;
                    }
                }
                chatDialog2(rooms[index]);
            });
            txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        adapter.getFilter().filter(s.toString());
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (txt.getText().toString().length() == 0)
                            adapter.getFilter().filter(null);
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }
            });
            layout.addView(list);
            int pad = dip2px(20);
            layout.setPadding(pad, pad, pad, pad);
            view.addView(layout);
        } catch (Exception e) {
            toast("Error in Log Layout.\n" + e.toString());
        }
    }

    private void chatDialog2(final String room) {
        Intent intent = new Intent(ctx, ChatlogActivity.class);
        intent.putExtra("room", room);
        ctx.startActivity(intent);
    }

    public void removeDialog(String room, String str) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle("채팅 로그 삭제");
            dialog.setMessage(str + room + "에서 기록된 채팅 로그를 삭제하시겠습니까?");
            dialog.setNegativeButton("아니요", null);
            dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                }
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

}
