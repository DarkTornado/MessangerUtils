package com.darktornado.msgutils.botapi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.darktornado.msgutils.NotiListener;

public class Replier {

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
        Replier replier = NotiListener.session.get(room);
        if (replier == null) return false;
        replier.reply(msg);
        return true;
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
