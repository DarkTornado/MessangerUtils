package com.darktornado.msgutils.scriptapi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.darktornado.library.PrimitiveWrapFactory;
import com.darktornado.msgutils.NotiListener;
import com.darktornado.msgutils.botapi.Replier;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class Api extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Api";
    }

    @JSStaticFunction
    public static Context getContext() {
        return NotiListener.ctx;
    }

    @JSStaticFunction
    public static void showToast(final String msg, final int leng){
        NotiListener.runOnUiThread(() -> Toast.makeText(NotiListener.ctx, msg, leng).show());
    }

    @JSStaticFunction
    public static boolean replyRoom(String room, String msg){
        Replier replier = NotiListener.session.get(room);
        if (replier == null) return false;
        replier.reply(msg);
        return true;
    }

    @JSStaticFunction
    public static boolean markAsRead(String room){
        Replier replier = NotiListener.session.get(room);
        if (replier == null) return false;
        return replier.markAsRead();
    }

    @JSStaticFunction
    public static void UIThread(final Function func) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
            rhino.setOptimizationLevel(-1);
            rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
            rhino.setWrapFactory(new PrimitiveWrapFactory());
            func.call(rhino, NotiListener.scope, NotiListener.scope, new Object[]{});
            org.mozilla.javascript.Context.exit();
        }, 0);
    }

}
