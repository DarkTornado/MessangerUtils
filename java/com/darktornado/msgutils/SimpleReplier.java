package com.darktornado.msgutils;

import android.content.Context;

import com.darktornado.msgutils.botapi.Replier;

import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleReplier {

    private final Context ctx;

    public SimpleReplier(Context ctx) {
        this.ctx = ctx;
    }

    public String execute(String room, String msg, String sender, boolean isGroupChat, Replier replier) {
        String data0 = Utils.rootRead(ctx, "reply_data.json");
        if (data0 == null) return null;
        try {
            JSONArray data = new JSONArray(data0);
            for (int n = 0; n < data.length(); n++) {
                JSONObject datum = data.getJSONObject(n);
                if (!checkInput(datum.getString("input"), msg, datum.getInt("type"))) continue;
                if (!checkRoom(isGroupChat, datum.getInt("roomType"))) continue;
                String chat = datum.getString("output");
                chat = chat.replace("[[보낸사람]]", sender).replace("[[내용]]", msg).replace("[[방]]", room);
                replier.reply(chat);
            }
            return null;
        } catch (Exception e) {
            return e.toString();
        }
    }

    private boolean checkInput(String input, String msg, int type) {
        if (msg.equals(input) && type == 0) return true;
        if (msg.startsWith(input) && type == 1) return true;
        if (msg.contains(input) && type == 2) return true;
        return false;
    }

    private boolean checkRoom(boolean isGroupChat, int type) {
        if (type == 0) return true;
        if (!isGroupChat && type == 1) return true;
        if (isGroupChat && type == 2) return true;
        return false;
    }
}
