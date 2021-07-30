package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        menu.add(0, 0, 0, "새로 고침").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);;
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
                chatDialog(rooms[index]);
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

    private void chatDialog(final String room) {
        try {
            SQLManager sql = new SQLManager(ctx, room);
            final ChatData[] data = sql.getAll();
            final int count = data.length;
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle(room + " (" + count + "개)");
            String[] names = {"최근 채팅 10개 시간순으로 보기", "모든 채팅 시간순으로 보기", "모든 채팅 시간 역순으로 보기", "채팅 기록 삭제"};
            dialog.setItems(names, (dialogInterface, which) -> {
                String[] result;
                Bitmap[] image;
                switch (which) {
                    case 0:
                        if (count < 10) {
                            result = new String[count];
                            image = new Bitmap[count];
                            for (int n = 0; n < count; n++) {
                                result[n] = data[n].toString();
                                image[n] = data[n].image;
                            }
                        } else {
                            result = new String[10];
                            image = new Bitmap[count];
                            for (int n = count - 10, m = 0; n < count; n++, m++) {
                                result[m] = data[n].toString();
                                image[m] = data[n].image;
                            }
                        }
                        showChatList(room, result, image);
                        break;
                    case 1:
                        result = new String[count];
                        image = new Bitmap[count];
                        for (int n = 0; n < count; n++) {
                            result[n] = data[n].toString();
                            image[n] = data[n].image;
                        }
                        showChatList(room, result, image);
                        break;
                    case 2:
                        result = new String[count];
                        image = new Bitmap[count];
                        for (int n = count - 1, m = 0; n >= 0; n--, m++) {
                            result[m] = data[n].toString();
                            image[m] = data[n].image;
                        }
                        showChatList(room, result, image);
                        break;
                    case 3:
                        removeDialog(room, "");
                        break;
                }
            });
            dialog.setNegativeButton("취소", null);
            dialog.show();
        } catch (Exception e) {
//            toast(e.toString());
            removeDialog(room, "해당 채팅방의 채팅 로그를 읽을 수 없습니다. ");
        }
    }

    public void showChatList(final String room, final String[] log, final Bitmap[] images) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle(room);
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(1);
            final EditText txt = new EditText(ctx);
            txt.setHint("검색어를 입력하세요...");
            layout.addView(txt);
            ArrayAdapter adapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, log);
            ListView list = new ListView(ctx);
            list.setAdapter(adapter);
            list.setFastScrollEnabled(true);
            list.setOnItemClickListener((adapterView, view, pos, id) -> {
                if (images[pos] == null) {
                    toast("길게 누르면 복사됩니다...");
                } else {
                    Toast toast = new Toast(ctx);
                    ImageView iv = new android.widget.ImageView(ctx);
                    iv.setImageBitmap(images[pos]);
                    toast.setView(iv);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
            list.setOnItemLongClickListener((adapterView, view, pos, id) -> {
                String text = ((TextView) view).getText().toString();
                Utils.copyToClipboard(ctx, text);
                toast("클립보드로 복사되었습니다...");
                return true;
            });
            txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            dialog.setView(layout);
            dialog.setNegativeButton("닫기", null);

            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
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
