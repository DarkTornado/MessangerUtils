package com.darktornado.msgutils;

import android.graphics.Bitmap;

public class ChatData {
    String room;
    String msg;
    String sender;
    String time;
    Bitmap image;

    public ChatData(String room, String msg, String sender, String time, Bitmap image) {
        this.room = room;
        this.msg = msg;
        this.sender = sender;
        this.time = time;
        this.image = image;
    }

    @Override
    public String toString() {
        return "보낸 사람 : " + sender + "\n" +
                "내용 : " + msg + "\n" +
                "시간 : " + time;
    }

}
