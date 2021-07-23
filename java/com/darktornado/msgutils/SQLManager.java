package com.darktornado.msgutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.ArrayList;

public class SQLManager extends SQLiteOpenHelper {

    final static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final String SENDER = "sender";
    private static final String MSG = "msg";
    private static final String ID = "chat_log_id";

    private String tableName;

    public SQLManager(Context context, String tableName) {
//        super(context, "chat_log.db", null, 1);
        super(context, sdcard + "/dev/chat_log.db", null, 1); //원활한 디버깅을 위해 임시로 내장메모리에 저장
        if (tableName == null) return;
        tableName = "'" + tableName.replace("'", "''") + "'"; //띄어쓰기 대응
        this.tableName = tableName;
        getReadableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (" + ID + " INTEGER," + SENDER + " TEXT," + MSG + " TEXT)");
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
            cursor.moveToNext();
        }
        return list.toArray(new String[0]);
    }

    public void insert(long chatId, String sender, String msg) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, chatId);
        values.put(SENDER, sender);
        values.put(MSG, msg);
        db.insert(tableName, null, values);
    }

    public void deleteAll() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public String getAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        StringBuilder result = new StringBuilder();
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(MSG));
            result.append(sender).append(": ").append(msg).append("\n");
        }
        cursor.close();
        return result.toString().trim();
    }

}
