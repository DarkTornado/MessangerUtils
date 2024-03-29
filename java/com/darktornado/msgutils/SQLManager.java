package com.darktornado.msgutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Date;

public class SQLManager extends SQLiteOpenHelper {

    final static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String PATH = sdcard + "/dev/";  //원활한 디버깅을 위해 임시로 내장메모리에 저장
    public static final int TYPE_MSG = 0;
    public static final int TYPE_IMAGE = 1;

    private final String SENDER = "sender";
    private final String PROFILE = "profile";
    private final String MSG = "msg";
    private final String ID = "chat_log_id";
    private final String TIME = "time";
    private final String TYPE = "type";
    private final String MISC = "misc";

    private String tableName;
    private Cursor cursor;

    public SQLManager(Context context, String tableName) {
        super(context, PATH + "chat_log.db", null, 1);
        if (tableName == null) return;
        tableName = "'" + tableName.replace("'", "''") + "'";  //띄어쓰기 대응
        this.tableName = tableName;
        getReadableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (" + ID + " INTEGER," + PROFILE + " INTEGER," + SENDER + " TEXT," + MSG + " TEXT," + TIME + " TEXT," + TYPE + " INTEGER," + MISC + " TEXT)");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public String[] getAllTables() {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (!name.equals("android_metadata")) list.add(name);
        }
        cursor.close();
        return list.toArray(new String[0]);
    }

    public void insert(long chatId, String sender, int profile, String msg, int type) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, chatId);
        values.put(SENDER, sender);
        values.put(PROFILE, profile);
        values.put(MSG, msg);
        values.put(TYPE, type);
        values.put(MISC, "");
        values.put(TIME, DateFormat.format("yyyy.MM.dd HH:mm:ss", new Date()).toString());
        db.insert(tableName, null, values);
    }

    public void deleteAll() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public ChatData[] getAll() {
        ArrayList<ChatData> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(TIME));
            int profile = cursor.getInt(cursor.getColumnIndexOrThrow(PROFILE));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(TYPE));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
            list.add(new ChatData(tableName, msg, sender, profile, time, type, id));
        }
        cursor.close();
        return list.toArray(new ChatData[0]);
    }

    public ArrayList<ChatData> get300() {
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.query(tableName, null, null, null, null, null, null);
        cursor.moveToLast();
        ArrayList<ChatData> list = new ArrayList<>();
        for (int n = 0; n < 300; n++) {
            if (!cursor.moveToPrevious()) break;
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(TIME));
            int profile = cursor.getInt(cursor.getColumnIndexOrThrow(PROFILE));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(TYPE));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
            list.add(0, new ChatData(tableName, msg, sender, profile, time, type, id));
        }
        return list;
    }

    public ChatData getOne() {
        if (!cursor.moveToPrevious()) return null;
        String sender = cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
        String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(TIME));
        int profile = cursor.getInt(cursor.getColumnIndexOrThrow(PROFILE));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(TYPE));
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        return new ChatData(tableName, msg, sender, profile, time, type, id);
    }

    public void close() {
        if (cursor != null) cursor.close();
    }

}
