package com.darktornado.msgutils;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.darktornado.msgutils.botapi.ImageDB;
import com.darktornado.msgutils.botapi.Replier;

public class NotiListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!Utils.rootLoad(this, "all_on", false)) return;
        Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
        for (Notification.Action act : wExt.getActions()) {
            if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
                if (act.title.toString().toLowerCase().contains("reply") ||
                        act.title.toString().toLowerCase().contains("답장")) {
                    Bundle bundle = sbn.getNotification().extras;
                    String sender = bundle.getString("android.title");
                    String msg = bundle.get("android.text").toString();
                    String room = bundle.getString(Build.VERSION.SDK_INT > 23 ? "android.summaryText" : "android.subText");
                    boolean isGroupChat = room != null;
                    if (room == null) room = sender;
                    Replier replier = new Replier(this, sbn.getNotification().actions, act);
                    ImageDB imageDB = new ImageDB(this, sbn);
                    long chatLogId = bundle.getLong("chatLogId");
                    chatHook(room, msg, sender, isGroupChat, replier, imageDB, chatLogId);
                }
            }
        }
    }

    private void chatHook(String room, String msg, String sender, boolean isGroupChat, Replier replier, ImageDB imageDB, long chatLogId) {
        /*
        채팅 & 이미지 수신 테스트용 소스 코드
        if (imageDB.getImage() != null) printImage(imageDB);
        else toast("room: " + room + "\nmsg: " + msg + "\nsender: " + sender + "\nisGroupChat: " + isGroupChat);
        */

        try {
            SQLManager sql = new SQLManager(this, room);
            sql.insert(chatLogId, sender, msg);
        }catch (Exception e){
            toast(e.toString());
        }
    }

    /*
    이미지 수신 테스트용 소스 코드
    private void printImage(ImageDB imageDB) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageDB.getImageBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);

        Intent intent = new Intent(this, ToastService.class);
        intent.putExtra("image", stream.toByteArray());
        startService(intent);
    }
    */

    private void toast(String msg) {
        Intent intent = new Intent(this, ToastService.class);
        intent.putExtra("msg", msg);
        startService(intent);
    }

}
