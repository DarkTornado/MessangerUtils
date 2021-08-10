package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.darktornado.listview.Item;
import com.darktornado.listview.ListAdapter;
import com.darktornado.msgutils.scriptapi.Api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ChatlogActivity extends Activity {

    private Toolbar toolbar;
    private String room;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLManager sql;
        switch (item.getItemId()) {
            case 0:
                if (Api.markAsRad(room)) toast("읽음처리 되었어요.");
                else toast("읽음처리를 하지 못했어요.");
                break;
            case 1:
                inputChat();
                break;
            case 2:
                sql = new SQLManager(this, room);
                sql.deleteAll();
                recreate();
                toast("메신저 도구에 저장된 대화 내용이 삭제되었어요.");
                break;
            case 3:
                sql = new SQLManager(this, room);
                sql.deleteAll();
                finish();
                toast("메신저 도구에 저장된 해당 채팅방 관련 정보가 삭제되었어요.");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "읽음처리");
        menu.add(0, 1, 0, "응답 전송");
        menu.add(0, 2, 0, "대화 내용 삭제");
        menu.add(0, 3, 0, "채팅방 삭제");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room = getIntent().getStringExtra("room");

        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        toolbar = Utils.createToolBar(this, room);
        setActionBar(toolbar);
        layout0.addView(toolbar);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        SQLManager sql = new SQLManager(this, room);
        final ChatData[] data = sql.getAll();

        final ArrayList<Item> items = new ArrayList<>();
        for (ChatData datum : data) {
            Bitmap profile = BitmapFactory.decodeFile(SQLManager.PATH + "/profiles/" + Utils.encode(room) + "/" + datum.profile + ".png");
            items.add(new Item(datum.sender, datum.msg, new BitmapDrawable(profile)));
        }

        ListView list = new ListView(this);
        list.setFastScrollEnabled(true);
        list.setDivider(null);
        list.setDividerHeight(dip2px(5));
        ListAdapter adapter = new ListAdapter();
        adapter.setItems(items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> {
            chatInfo(data[pos]);
        });
        list.setSelection(adapter.getCount() - 1);
        layout.addView(list);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);

        layout0.addView(layout);
        layout0.setBackgroundColor(Color.WHITE);
        setContentView(layout0);
    }

    private void chatInfo(final ChatData data) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("채팅 정보 보기");

        StringBuilder result = new StringBuilder();
        result.append("<meta name='viewport' content='user-scalable=no width=device-width' />");
        result.append("<style>td{padding:5px;border-bottom: 1px solid #000000;}td.left{padding:5px;border-right: 1px solid #000000;}table{border-top: 1px solid #000000;border-collapse: collapse;}</style>");
        result.append("<table width=100%>");
        result.append("<tr align=center><td class=left width=20%><b>보낸사람</b></td><td>" + data.sender + "</td></tr>");
        result.append("<tr align=center><td class=left><b>방</b></td><td>" + room + "</td></tr>");
        result.append("<tr align=center><td class=left><b>내용</b></td><td>" + data.msg + "</td></tr>");
        result.append("<tr align=center><td class=left><b>시간</b></td><td>" + data.time + "</td></tr>");
        switch (data.type) {
            case SQLManager.TYPE_IMAGE:
                result.append("<tr align=center><td class=left><b>사진</b></td><td><img width=100% src='" + new File(SQLManager.PATH + "images/" + Utils.encode(room) + "/" + data.id + ".png").toURI().toString() + "'/></td></tr>");
                dialog.setNeutralButton("저장", (_dialog, which) -> {
                    saveImage(data);
                });
                break;
        }
        result.append("</table>");

        final String info = "보낸사람 : " + data.sender + "\n" +
                "방 : " + room + "\n" +
                "내용 : " + data.msg + "\n" +
                "시간 : " + data.time;

        WebView web = new WebView(this);
        if (Build.VERSION.SDK_INT > 23) {
            web.loadDataWithBaseURL(null, result.toString(), "text/html; charset=UTF-8", null, null);
        } else {
            web.loadData(result.toString(), "text/html; charset=UTF-8", null);
        }
        dialog.setView(web);
        dialog.setNegativeButton("닫기", null);
        dialog.setPositiveButton("복사", (_dialog, which) -> {
            Utils.copyToClipboard(ChatlogActivity.this, info);
            toast("클립보드로 복사되었어요.");
        });
        dialog.show();
    }

    private void saveImage(ChatData data) {
        try {
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= 30) {
                ContentResolver resolver = getContentResolver();
                ContentValues value = new ContentValues();
                value.put(MediaStore.MediaColumns.DISPLAY_NAME, data.id + ".png");
                value.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                value.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MsgUtils/");
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
                fos = resolver.openOutputStream(uri);
            } else {
                File file = new File(SQLManager.sdcard + "/DCIM/MsgUtils/", data.id + ".png");
                fos = new FileOutputStream(file);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(SQLManager.PATH + "images/" + data.id + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            toast("/내장메모리/DCIM/MsgUtils/ 폴더에 저장되었어요.");
        } catch (Exception e) {
            toast("이미지 저장 실패\n" + e.toString());
        }
    }

    private void inputChat() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("채팅 전송");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        final EditText txt = new EditText(this);
        txt.setHint("보낼 내용 입력...");
        layout.addView(txt);
        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        dialog.setView(scroll);
        dialog.setNegativeButton("취소", null);
        dialog.setPositiveButton("확인", (_dialog, which) -> {
            String msg = txt.getText().toString();
            if (Api.replyRoom(room, msg)) toast("응답을 보냈어요.");
            else toast("응답을 보내지 못했어요.");
        });
        dialog.show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}