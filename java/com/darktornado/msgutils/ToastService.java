package com.darktornado.msgutils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/*
 * 왜인지는 모르겠는데, onNotificationPosted에서는 토스트가 안뜸;;
 */


public class ToastService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = intent.getStringExtra("msg");
        if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
