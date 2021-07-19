package com.darktornado.msgutils;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * 왜인지는 모르겠는데, onNotificationPosted에서는 토스트가 안뜸;;
 */


public class ToastService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = intent.getStringExtra("msg");
        if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        /*
        이미지 수신 테스트용 소스 코드
        byte[] arr = intent.getByteArrayExtra("image");
        if (arr != null) {
            Toast toast = new Toast(this);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            ImageView iv = new android.widget.ImageView(this);
            iv.setImageBitmap(bitmap);
            toast.setView(iv);
            toast.show();
        }
        */

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
