package com.darktornado.msgutils;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.darktornado.library.PrimitiveWrapFactory;
import com.darktornado.msgutils.botapi.ImageDB;
import com.darktornado.msgutils.botapi.Replier;
import com.darktornado.msgutils.scriptapi.Api;

import org.json.JSONObject;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class NotiListener extends NotificationListenerService {

    private final HashMap<String, String> preChat = new HashMap<>();

    public static Context ctx;
    public static HashMap<String, Replier> session = new HashMap<>();
    public static ScriptableObject scope;
    private static Handler handler;
    private static String jsApi = null;

    /* 알림 구조가 바뀌면 사용자가 직접 파싱 방식을 수정 가능하게 변경 */
    public static String KEY_MSG = "android.text";
    public static String KEY_MSG_ALTER = "";
    public static String KEY_ROOM = "android.summaryText";
    public static String KEY_ROOM_ALTER = "android.subText";
    public static String KEY_SENDER = "android.title";
    public static String KEY_SENDER_ALTER = "";
    public static boolean KEY_IGC_ROOM = true;
    public static String KEY_IGC = "android.isGroupConversation";

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
        handler = new Handler();
        updateNotiParseData(this);
    }

    public static void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!Utils.rootLoad(this, "all_on", false)) return;
        if (!Utils.getPackage(ctx).equals(sbn.getPackageName())) return;
        Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
        for (Notification.Action act : wExt.getActions()) {
            if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
//                if (act.title.toString().toLowerCase().contains("reply") || 없어도 되는 것 같아서 일단 주석처리
//                        act.title.toString().toLowerCase().contains("답장")) {
//                    toast(bundle.toString());
                Bundle bundle = sbn.getNotification().extras;

                /* 수신된 채팅 내용 */
                Object _msg = bundle.get(KEY_MSG);
                if (_msg == null) _msg = bundle.get(KEY_MSG_ALTER);
                String msg = _msg == null ? null : _msg.toString();

                /* 채팅 보낸 사람 */
                String sender = bundle.getString(KEY_SENDER);
                if (sender == null) sender = bundle.getString(KEY_SENDER_ALTER);

                /* 채팅이 온 방 */
                String room = bundle.getString(KEY_ROOM);
                if (room == null) room = bundle.getString(KEY_ROOM_ALTER);

                /* 단톡/갠톡 구분 */
                boolean isGroupChat;
                if (KEY_IGC_ROOM) {
                    isGroupChat = room != null;
                    if (room == null) room = sender;
                } else {
                    isGroupChat = bundle.getBoolean(KEY_IGC);
                }

                Replier replier = new Replier(this, sbn.getNotification().actions, act);
                ImageDB imageDB = new ImageDB(this, sbn);
                long chatLogId = bundle.getLong("chatLogId");

                chatHook(room, msg, sender, isGroupChat, replier, imageDB, chatLogId);
            }
        }
    }

    public static void updateNotiParseData(Context ctx) {
        try {
            String data0 = Utils.rootRead(ctx, "notiParse.json");
            if (data0 == null) return;
            JSONObject data = new JSONObject(data0);
            KEY_MSG = data.getString("msg1");
            KEY_MSG_ALTER = data.getString("msg2");
            KEY_ROOM = data.getString("room1");
            KEY_ROOM_ALTER = data.getString("room2");
            KEY_SENDER = data.getString("sender1");
            KEY_SENDER_ALTER = data.getString("sender2");
            KEY_IGC_ROOM = data.getBoolean("igc_room");
            KEY_IGC = data.getString("igc");
        } catch (Exception e) {
            Log.i(Utils.DEBUG, "Failed to update noti parsing data.\n" + e.toString());
        }
    }

    private void chatHook(String room, String msg, String sender, boolean isGroupChat, Replier replier, ImageDB imageDB, long chatLogId) {
        /*
        채팅 & 이미지 수신 테스트용 소스 코드
        if (imageDB.getImage() != null) printImage(imageDB);
        else toast("room: " + room + "\nmsg: " + msg + "\nsender: " + sender + "\nisGroupChat: " + isGroupChat);
        */
        session.put(room, replier);

        /* 단순 자동응답 */
        if (Utils.rootLoad(this, "on0", true)) {

            //특정 방에서만 작동
            if (!roomCheck(room, 0)) return;

            //도배 방지
            if (Utils.rootLoad(this, "preventCover", true)) {
                String preChat = this.preChat.get(room);
                if (msg.equals(preChat)) return;
                this.preChat.put(room, msg);
            }

            //기능 실행
            SimpleReplier simple = new SimpleReplier(this);
            String result = simple.execute(room, msg, sender, isGroupChat, replier);
            if (result != null) toast("단순 자동응답 기능 실행 실패\n" + result);
        }

        /* 자바스크립트 */
        if (Utils.rootLoad(this, "on1", true)) {
            final Object[] args = {room, msg, sender, isGroupChat, replier, imageDB};
            new Thread(() -> {
                String result = callScriptMethod("response", args);
                if (result != null) toast(result);
            }).start();
        }

        /* 채팅 기록 */
        if (Utils.rootLoad(this, "on2", true)) {
            if (!roomCheck(room, 2)) return;
            try {
                int profile = imageDB.getProfileHash();
                SQLManager sql = new SQLManager(this, room);
                if (imageDB.getImage() == null) {
                    sql.insert(chatLogId, sender, profile, msg, SQLManager.TYPE_MSG);
                } else {
                    sql.insert(chatLogId, sender, profile, msg, SQLManager.TYPE_IMAGE);
                    File dir = new File(SQLManager.PATH + "images/" + Utils.encode(room) + "/");
                    dir.mkdirs();
                    File file = new File(dir, chatLogId + ".png");
                    FileOutputStream fos = new FileOutputStream(file);
                    imageDB.getImageBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                }
                File dir = new File(SQLManager.PATH + "profiles/" + Utils.encode(room) + "/");
                dir.mkdirs();
                File file = new File(dir, profile + ".png");
                if (!file.exists()) {
                    FileOutputStream fos = new FileOutputStream(file);
                    imageDB.getProfileBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                }
            } catch (Exception e) {
                toast("채팅 기록 저장 실패\n" +
                        "room: " + room + "\n" +
                        "msg: " + msg + "\n" +
                        "sender: " + sender + "\n" +
                        "오류: " + e.toString());
            }
        }

    }

    private boolean roomCheck(String room, int type) {
        int roomType = Utils.rootLoad(this, "roomType" + type, 0);
        if (roomType == 0) return true;
        String cache = Utils.rootRead(this, "roomList" + type);
        if (cache == null) return roomType == 2;
        if (cache.equals("")) return roomType == 2;
        String[] list = cache.split("\n");
        for (String rr : list) {
            if (rr.equals(room)) {
                if (roomType == 1) return true;
                if (roomType == 2) return false;
            }
        }
        return roomType == 2;
    }

    public static String callScriptMethod(final String event, final Object[] args) {
        if (scope == null) return "Scope is not prepared.";
        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);
        rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
        rhino.setWrapFactory(new PrimitiveWrapFactory());
        try {
            Function func = (Function) scope.get(event, scope);
            func.call(rhino, scope, scope, args);
        } catch (ClassCastException e) {
//            toast("이벤트 리스너(" + event + ") 호출 실패\n" + e.toString());
        } catch (Exception e) {
            org.mozilla.javascript.Context.exit();
            return "이벤트 리스너(" + event + ") 호출 실패\n" + e.toString();
        }
        org.mozilla.javascript.Context.exit();
        return null;
    }

    public static String loadScript(String source) {
        try {
            callScriptMethod("onStartCompile", new Object[0]);
            org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
            rhino.setOptimizationLevel(-1);
            rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
            rhino.setWrapFactory(new PrimitiveWrapFactory());
            scope = new ImporterTopLevel(rhino);
            ScriptableObject.defineClass(scope, Api.class);
            ScriptableObject.defineClass(scope, com.darktornado.msgutils.scriptapi.Utils.class);
            rhino.evaluateString(scope, loadJSApi(ctx), "JavaScript", 1, null);
            rhino.evaluateString(scope, source, "JavaScript", 1, null);
            org.mozilla.javascript.Context.exit();
            return null;
        } catch (Exception e) {
            org.mozilla.javascript.Context.exit();
            return e.toString();
        }
    }

    private static String loadJSApi(Context ctx) {
        try {
            if (jsApi != null) return jsApi;
            jsApi = Utils.readStream(ctx.getAssets().open("JavascriptApi.js"));
            return jsApi;
        }catch (Exception e) {
//            toast("Cannot load Javascript API.\n" + e.toString());
        }
        return "";
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
