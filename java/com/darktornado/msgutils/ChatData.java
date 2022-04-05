package com.darktornado.msgutils;

public class ChatData {
    String room;
    String msg;
    String sender;
    String time;
    int profile;
    int type;
    long id;

    public ChatData(String room, String msg, String sender, int profile, String time, int type, long id) {
        this.room = room;
        this.msg = msg;
        this.sender = sender;
        this.time = time;
        this.profile = profile;
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return "보낸 사람 : " + sender + "\n" +
                "내용 : " + msg + "\n" +
                "시간 : " + time;
    }

}
