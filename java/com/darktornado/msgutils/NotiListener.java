package com.darktornado.msgutils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

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
                    String msg = bundle.getString("android.text");
                    String room = bundle.getString(Build.VERSION.SDK_INT > 23 ? "android.summaryText" : "android.subText");
                    boolean isGroupChat = room != null;
                    if (room == null) room = sender;
                    Replier replier = new Replier(this, sbn.getNotification().actions, act);
                    chatHook(room, msg, sender, isGroupChat, replier);
                }
            }
        }
    }

    private void chatHook(String room, String msg, String sender, boolean isGroupChat, Replier replier) {

    }

    public static class Replier {

        private Context ctx;
        private Notification.Action[] actions;
        private Notification.Action action;

        public Replier(Context ctx, Notification.Action[] actions, Notification.Action action) {
            this.ctx = ctx;
            this.actions = actions;
            this.action = action;
        }

        public void reply(String value) {
            Intent sendIntent = new Intent();
            Bundle msg = new Bundle();
            for (RemoteInput inputable : action.getRemoteInputs()) {
                msg.putCharSequence(inputable.getResultKey(), value);
            }
            RemoteInput.addResultsToIntent(action.getRemoteInputs(), sendIntent, msg);
            try {
                action.actionIntent.send(ctx, 0, sendIntent);
            } catch (PendingIntent.CanceledException e) {

            }
        }

        public boolean reply(String room, String msg) {
            return false;
        }

        public boolean markAsRead() {
            try {
                if (actions == null) return false;
                actions[0].actionIntent.send(ctx, 1, new Intent());
                return true;
            } catch (PendingIntent.CanceledException e) {
                return false;
            }
        }

    }

}
