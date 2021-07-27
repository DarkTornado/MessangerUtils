package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsLayout extends BaseLayout {

    public ScrollView view;

    public SettingsLayout(Activity ctx) {
        super(ctx);
        init();
    }

    public void updateOptionsMenu(Menu menu) {
        menu.clear();
    }

    private void init() {
        ScrollView scroll = new ScrollView(ctx);
        try {
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(1);

            int mar = dip2px(10);
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
            margin.setMargins(mar, mar, mar, mar);
            final LinearLayout[] lays = new LinearLayout[4];
            for (int n = 0; n < lays.length; n++) {
                lays[n] = new LinearLayout(ctx);
                lays[n].setOrientation(1);
                lays[n].setLayoutParams(margin);
                lays[n].setBackgroundColor(Color.WHITE);
                lays[n].setElevation(dip2px(3));
                layout.addView(lays[n]);
            }

            lays[0].addView(makeTitle("기능 사용"));
            addFuncSettings(lays[0]);
            lays[1].addView(makeTitle("단순 자동응답 설정"));
            addSimpleSettings(lays[1]);
            lays[2].addView(makeTitle("채팅 기록"));
            addLogSettings(lays[2]);
            lays[3].addView(makeTitle("공통 설정"));
            addGlobalSettings(lays[3]);

            int pad = dip2px(20);
            layout.setPadding(pad, pad, pad, pad);
            scroll.addView(layout);

            view = scroll;
        } catch (Exception e) {
            toast("Error in Settings Layout.\n" + e.toString());
        }
    }

    private TextView makeTitle(String txt) {
        TextView title = new TextView(ctx);
        title.setTextSize(21);
        title.setText(txt);
        title.setTextColor(Color.WHITE);
        title.setBackgroundColor(Color.parseColor("#F8BBD0"));
        int pad = dip2px(10);
        title.setPadding(pad, pad, pad, pad);
        return title;
    }

    private void addFuncSettings(LinearLayout layout0) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);

        int pad = dip2px(1);
        int pad2 = dip2px(5);
        String[] menuS = {"단순 자동응답", "JS 챗봇", "채팅 기록"};
        Switch[] sws = new Switch[menuS.length];
        for (int n = 0; n < menuS.length; n++) {
            sws[n] = new Switch(ctx);
            sws[n].setText(menuS[n]);
            sws[n].setTextSize(17);
            sws[n].setTextColor(Color.BLACK);
            sws[n].setId(n);
            sws[n].setPadding(pad, pad2, pad, pad2);
            sws[n].setChecked(Utils.rootLoad(ctx, "on" + n, true));
            sws[n].setOnCheckedChangeListener((swit, onoff) -> Utils.rootSave(ctx, "on" + swit.getId(), onoff));
            layout.addView(sws[n]);
        }

        pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);
        layout0.addView(layout);
    }

    private void addSimpleSettings(LinearLayout layout0) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);

        int pad = dip2px(1);
        int pad2 = dip2px(5);
        String[] menuS = {"도배 방지"};
        Switch[] sws = new Switch[menuS.length];
        String[] bools = {"preventCover"};
        for (int n = 0; n < menuS.length; n++) {
            sws[n] = new Switch(ctx);
            sws[n].setText(menuS[n]);
            sws[n].setTextSize(17);
            sws[n].setTextColor(Color.BLACK);
            sws[n].setId(n);
            sws[n].setPadding(pad, pad2, pad, pad2);
            sws[n].setChecked(Utils.rootLoad(ctx, bools[n], true));
            sws[n].setOnCheckedChangeListener((swit, onoff) -> {
                Utils.rootSave(ctx, bools[swit.getId()], onoff);
                switch (swit.getId()) {
                    case 0:
                        toast("같은 채팅이 연속으로 수신되면 가볍게 무시하는 기능입니다.");
                        break;
                }
            });
            layout.addView(sws[n]);
        }

        TextView txt3 = new TextView(ctx);
        txt3.setText("반응할 방 설정 : ");
        txt3.setTextSize(17);
        txt3.setTextColor(Color.BLACK);
        layout.addView(txt3);
        RadioGroup radios = new RadioGroup(ctx);
        radios.setOrientation(1);
        final String[] menuR = {"모든 방(들)에서 작동", "특정 방(들)에서만 작동", "특정 방(들)을 제외하고 작동"};
        RadioButton[] radio = new RadioButton[menuR.length];
        for (int n = 0; n < menuR.length; n++) {
            radio[n] = new RadioButton(ctx);
            radio[n].setText(menuR[n]);
            radio[n].setTextSize(16);
            radio[n].setTextColor(Color.BLACK);
            radio[n].setId(n);
            radios.addView(radio[n]);
        }
        radio[Utils.rootLoad(ctx, "roomType1", 0)].setChecked(true);
        radios.setOnCheckedChangeListener((group, checkedId) -> {
            Utils.rootSave(ctx, "roomType1", String.valueOf(checkedId));
            if (checkedId > 0) inputRooms(1);
        });
        layout.addView(radios);

        pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);
        layout0.addView(layout);
    }

    private void addLogSettings(LinearLayout layout0) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);

        TextView txt3 = new TextView(ctx);
        txt3.setText("반응할 방 설정 : ");
        txt3.setTextSize(17);
        txt3.setTextColor(Color.BLACK);
        layout.addView(txt3);
        RadioGroup radios = new RadioGroup(ctx);
        radios.setOrientation(1);
        final String[] menuR = {"모든 방(들)에서 작동", "특정 방(들)에서만 작동", "특정 방(들)을 제외하고 작동"};
        RadioButton[] radio = new RadioButton[menuR.length];
        for (int n = 0; n < menuR.length; n++) {
            radio[n] = new RadioButton(ctx);
            radio[n].setText(menuR[n]);
            radio[n].setTextSize(16);
            radio[n].setTextColor(Color.BLACK);
            radio[n].setId(n);
            radios.addView(radio[n]);
        }
        radio[Utils.rootLoad(ctx, "roomType3", 0)].setChecked(true);
        radios.setOnCheckedChangeListener((group, checkedId) -> {
            Utils.rootSave(ctx, "roomType3", String.valueOf(checkedId));
            if (checkedId > 0) inputRooms(3);
        });
        layout.addView(radios);

        int pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);
        layout0.addView(layout);
    }

    private void addGlobalSettings(LinearLayout layout0) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);

        int pad = dip2px(1);
        int pad2 = dip2px(5);
        String[] menuS = {"백그라운드에서 실행"};
        Switch[] sws = new Switch[menuS.length];
        String[] bools = {"useBackground"};
        for (int n = 0; n < menuS.length; n++) {
            sws[n] = new Switch(ctx);
            sws[n].setText(menuS[n]);
            sws[n].setTextSize(17);
            sws[n].setTextColor(Color.BLACK);
            sws[n].setId(n);
            sws[n].setPadding(pad, pad2, pad, pad2);
            sws[n].setChecked(Utils.rootLoad(ctx, bools[n], false));
            sws[n].setOnCheckedChangeListener((swit, onoff) -> {
                Utils.rootSave(ctx, bools[swit.getId()], onoff);
                switch (swit.getId()) {
                    case 0:
                        Intent intent = new Intent(ctx, NotiListener.class);
                        if (onoff) {
                            intent.putExtra("foreground", "stop");
                            toast("상단바에 알림이 뜨지 않지만, 서비스가 갑자기 죽었다가 살아날 수도 있습니다.");
                        } else {
                            intent.putExtra("foreground", "start");
                            toast("서비스가 죽을 일은 딱히 없지만, 상단바에 알림이 뜹니다.");
                        }
                        ctx.startService(intent);
                        break;
                }
            });
            layout.addView(sws[n]);
        }

        String[] menus = {"알림 접근 허용", "Wear OS 설치", "세션 초기화", "반응할 앱 설정", "태그 목록", "API 목록", "기능 정보 / 도움말", "라이선스 정보"};
        Button[] btns = new Button[menus.length];
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2, 1);
        int mar = dip2px(1);
        margin.setMargins(mar, mar, mar, mar);
        LinearLayout[] lays = new LinearLayout[(menus.length / 2)];
        for (int n = 0; n < menus.length; n++) {
            if (n % 2 == 0) {
                lays[n / 2] = new LinearLayout(ctx);
                lays[n / 2].setOrientation(0);
                lays[n / 2].setWeightSum(2);
                layout.addView(lays[n / 2]);
            }
            btns[n] = new Button(ctx);
            btns[n].setText(menus[n]);
            btns[n].setId(n);
            btns[n].setTransformationMethod(null);
            btns[n].setLayoutParams(margin);
            btns[n].setBackgroundColor(Color.parseColor("#FCE4EC"));
            btns[n].setOnClickListener(v -> {
                try {
                    Intent intent;
                    switch (v.getId()) {
                        case 0:
                            ctx.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            toast("알림 접근 허용 창으로 이동합니다.");
                            break;
                        case 1:
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.wearable.app");
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            toast("Play 스토어로 이동합니다.");
                            break;
                        case 2:
//                                NotiListener.sessions.clear();
                            toast("봇이 작동하면서 저장된 세션들이 초기화되었습니다.");
                            break;
                        case 3:
                            inputPackageName();
                            break;
                        case 4:
                            showDialog("단순 자동응답 태그 목록", "[[보낸사람]]\n" +
                                    "  -> 보낸 사람의 이름을 인용합니다.\n\n" +
                                    "[[내용]]\n" +
                                    "  -> 수신된 채팅의 내용을 인용합니다.\n\n" +
                                    "[[방]]\n" +
                                    "  -> 채팅이 수신된 방의 이름을 인용합니다.");
                            break;
                        case 5:

                            break;
                        case 6:

                            break;
                        case 7:

                            break;
                    }
                } catch (Exception e) {
                    toast(e.toString());
                }
            });
            lays[n / 2].addView(btns[n]);
        }

        pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);
        layout0.addView(layout);
    }

    private void inputPackageName() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle("패키지명 입력");
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(1);
            final EditText txt = new EditText(ctx);
//            txt.setText(Utils.getPackage(ctx));
            txt.setSingleLine(true);
            layout.addView(txt);
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(ctx);
            scroll.addView(layout);
            dialog.setView(scroll);
            dialog.setNegativeButton("취소", null);
            dialog.setPositiveButton("확인", (dialog1, which) -> {
                String input = txt.getText().toString();
                Utils.rootSave(ctx, "packageName", input);
                toast("저장되었습니다.");
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void inputRooms(int type) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle("방 이름 입력");
            LinearLayout layout = new LinearLayout(ctx);
            layout.setOrientation(1);
            final EditText txt = new EditText(ctx);
            String list = Utils.rootRead(ctx, "roomList" + type);
            if (list != null) txt.setText(list);
            txt.setHint("방 이름 입력...");
            txt.setOnClickListener(view -> toast("방 이름 구분은 엔터입니다..."));
            layout.addView(txt);
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(ctx);
            scroll.addView(layout);
            dialog.setView(scroll);
            dialog.setNegativeButton("취소", null);
            dialog.setPositiveButton("확인", (dialog1, which) -> {
                String input = txt.getText().toString();
                Utils.rootSave(ctx, "roomList" + type, input);
                toast("저장되었습니다.");
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

}
