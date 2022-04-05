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
import android.widget.AbsListView;
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

    private String room;
    private ListView list;
    private SQLManager sql;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (Api.markAsRead(room)) toast("읽음처리 되었어요.");
                else toast("읽음처리를 하지 못했어요.");
                break;
            case 1:
                inputChat();
                break;
            case 2:
                loadAllLog();
                break;
            case 3:
                exportLog();
                break;
            default:
                deleteDialog(item.getTitle().toString(), item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "읽음처리");
        menu.add(0, 1, 0, "응답 전송");
        menu.add(0, 2, 0, "모두 불러오기");
        menu.add(0, 3, 0, "대화내용 내보내기");
        menu.add(0, 4, 0, "프로필 사진 삭제");
        menu.add(0, 5, 0, "사진 파일 삭제");
        menu.add(0, 6, 0, "대화 내용 삭제");
        menu.add(0, 7, 0, "채팅방 삭제");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room = getIntent().getStringExtra("room");

        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        Toolbar toolbar = Utils.createToolBar(this, room);
        setActionBar(toolbar);
        layout0.addView(toolbar);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        sql = new SQLManager(this, room);
        final ArrayList<ChatData> data = sql.get300();
        final boolean[] flag = {false};

        ArrayList<Item> items = new ArrayList<>();
        for (ChatData datum : data) {
            Bitmap profile = BitmapFactory.decodeFile(SQLManager.PATH + "/profiles/" + Utils.encode(room) + "/" + datum.profile + ".png");
            items.add(new Item(datum.sender, datum.msg, new BitmapDrawable(profile)));
        }

        list = new ListView(this);
        list.setFastScrollEnabled(true);
        list.setDivider(null);
        list.setDividerHeight(dip2px(5));
        final ListAdapter adapter = new ListAdapter();
        adapter.setItems(items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> {
            chatInfo(data.get(pos));
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (flag[0]) return;
                if (!list.canScrollVertically(-1)) {
                    flag[0] = true;
                    ChatData datum = sql.getOne();
                    if (datum == null) return;
                    data.add(0, datum);
                    Bitmap profile = BitmapFactory.decodeFile(SQLManager.PATH + "/profiles/" + Utils.encode(room) + "/" + datum.profile + ".png");
                    adapter.addItem(new Item(datum.sender, datum.msg, new BitmapDrawable(profile)));
                }
                adapter.notifyDataSetChanged();
                flag[0] = false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
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

    private void deleteDialog(String title, int type) {
        final String[] msgs = new String[6];
        msgs[2] = " 대화 기록을 저장할 때 해당 채팅방 참여자들의 프로필 사진이 저장돼요. 저장된 프로필 사진들을 삭제하실건가요?\n" +
                " 삭제해도 대화 내용은 유지되지만, 삭제한 이후에 채팅을 보낸 적이 없는 사람들은 프로필 사진이 뜨지 않아요.\n" +
                " 여기서 무언가를 지우면 메신저 도구에만 반영되고, 카카오톡에는 반영되지는 않아요.";
        msgs[3] = " 대화 기록을 저장할 때 해당 채팅방 참여자들이 보낸 사진들도 같이 저장돼요. 저장된 사진들을 삭제하실건가요?\n" +
                " 삭제해도 대화 내용은 유지되지만, 상대방이 보냈던 사진들은 뜨지 않아요." +
                " 여기서 무언가를 지우면 메신저 도구에만 반영되고, 카카오톡에는 반영되지는 않아요.";
        msgs[4] = " 메신저 도구에서 기록된 이 채팅방의 대화 기록을 삭제하실건가요? 메신저 도구에만 반영되고, 카카오톡에는 반영되지는 않아요.";
        msgs[5] = " 메신저 도구에서 기록된 이 채팅방의 대화 기록을 삭제하고, 채팅방 목록에서 이 채팅방을 삭제하실건가요? 메신저 도구에만 반영되고, 카카오톡에는 반영되지는 않아요.";
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(msgs[type]);
        dialog.setNegativeButton("취소", null);
        dialog.setPositiveButton("확인", (_dialog, which) -> {
            SQLManager sql;
            File[] files;
            switch (type) {
                case 2:
                    files = new File(SQLManager.PATH + "/profiles/" + Utils.encode(room) + "/").listFiles();
                    for (File file : files) {
                        file.delete();
                    }
                    toast("프로필 사진들이 삭제되었어요.");
                    break;
                case 3:
                    files = new File(SQLManager.PATH + "/images/" + Utils.encode(room) + "/").listFiles();
                    for (File file : files) {
                        file.delete();
                    }
                    toast("저장된 사진들이 삭제되었어요.");
                    break;
                case 4:
                    sql = new SQLManager(this, room);
                    sql.deleteAll();
                    recreate();
                    toast("메신저 도구에 저장된 대화 내용이 삭제되었어요.");
                    break;
                case 5:
                    sql = new SQLManager(this, room);
                    sql.deleteAll();
                    finish();
                    toast("메신저 도구에 저장된 해당 채팅방 관련 정보가 삭제되었어요.");
                    break;
            }
        });
        dialog.show();
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

    private void loadAllLog() {
        sql.close();
        sql = new SQLManager(this, room);
        final ChatData[] data = sql.getAll();
        ArrayList<Item> items = new ArrayList<>();
        for (ChatData datum : data) {
            Bitmap profile = BitmapFactory.decodeFile(SQLManager.PATH + "/profiles/" + Utils.encode(room) + "/" + datum.profile + ".png");
            items.add(new Item(datum.sender, datum.msg, new BitmapDrawable(profile)));
        }
        final ListAdapter adapter = new ListAdapter();
        adapter.setItems(items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> {
            chatInfo(data[pos]);
        });
        list.setOnScrollListener(null);
    }

    private void exportLog() {
        sql.close();
        sql = new SQLManager(this, room);
        final ChatData[] data = sql.getAll();
        StringBuilder str = new StringBuilder("대화내용 내보내기 - ");
        str.append(room).append("\n");
        for (ChatData datum : data) {
            str.append("--------------------\n")
                    .append("보낸사람 : ").append(datum.sender).append("\n")
                    .append("내용 : ").append(datum.msg).append("\n")
                    .append("시간 : ").append(datum.time).append("\n")
                    .append("profile_hash : ").append(datum.profile).append("\n")
                    .append("log_id : ").append(datum.id).append("\n");
        }
        Utils.saveFile(SQLManager.sdcard+"/MU/export.txt", str.toString());
        toast("저장되었어요.");
    }

    @Override
    public void onDestroy(){
        sql.close();
        super.onDestroy();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}