package com.darktornado.msgutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SimpleLayout extends BaseLayout {

    public LinearLayout view;

    private ArrayAdapter adapter;
    private ArrayList<String> names = new ArrayList<>();
    private ChatReplyData[] data;

    public SimpleLayout(Activity ctx) {
        super(ctx);
        init();
    }

    public void onOptionsItemSelected(int id){
        switch (id){
            case 0:
                editDialog(-1);
                break;
            case 1:

                break;
        }
    }

    private void init() {
        updateData();
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        try {
            final EditText txt = new EditText(ctx);
            txt.setHint("검색어를 입력하세요...");
            layout.addView(txt);
            adapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, names);
            ListView list = new ListView(ctx);
            list.setAdapter(adapter);
            list.setFastScrollEnabled(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    int index = 0;
                    String text = ((TextView) view).getText().toString();
                    for (int n = 0; n < names.size(); n++) {
                        if (text.equals(names.get(n))) {
                            index = n;
                            break;
                        }
                    }
                    editDialog(index);
                }
            });
            txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        adapter.getFilter().filter(s.toString());
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (txt.getText().toString().length() == 0)
                            adapter.getFilter().filter(null);
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }
            });
            layout.addView(list);
            int pad = dip2px(20);
            layout.setPadding(pad, pad, pad, pad);
            view = layout;
        } catch (Exception e) {
            toast("Error in Simple Layout.\n" + e.toString());
        }
    }

    private void updateData() {
        try {
            String data1 = Utils.rootRead(ctx, "reply_data.json");
            names.clear();
            if (data1 == null) {
                data = new ChatReplyData[0];
            } else {
                JSONArray data2 = new JSONArray(data1);
                data = new ChatReplyData[data2.length()];
                for (int n = 0; n < data2.length(); n++) {
                    JSONObject datum = data2.getJSONObject(n);
                    String input = datum.getString("input");
                    String output = datum.getString("output");
                    data[n] = new ChatReplyData(input, output, datum.getInt("type"), datum.getInt("roomType"));
                    if (input.length() > 10) input = input.substring(0, 10) + "...";
                    if (output.length() > 10) output = output.substring(0, 10) + "...";
                    names.add(input.replace("\n", " ") + " → " + output.replace("\n", " "));
                }
            }
        } catch (Exception e) {
            toast("자동 응답 데이터 정보 로딩 실패\n" + e.toString());
        }
    }

    private void editDialog(final int pos) {
        try {
            String title;
            final ChatReplyData datum;
            if (pos == -1) {
                title = "새 항목 추가";
                datum = new ChatReplyData(null, null, 0, 0);
            } else {
                title = names.get(pos);
                datum = data[pos].copy();
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle(title);
            final LinearLayout layout = new android.widget.LinearLayout(ctx);
            layout.setOrientation(1);
            final TextView txt1 = new TextView(ctx);
            final EditText txt2 = new EditText(ctx);
            TextView txt3 = new TextView(ctx);
            final EditText txt4 = new EditText(ctx);
            TextView txt0 = new TextView(ctx);
            txt0.setText("채팅방 종류 : ");
            txt0.setTextSize(17);
            txt0.setTextColor(Color.BLACK);
            txt0.setPadding(0, dip2px(10), 0, 0);
            layout.addView(txt0);
            RadioGroup radios = new RadioGroup(ctx);
            radios.setOrientation(1);
            String[] menuR = {"모두 작동", "1:1 채팅에서만 작동", "단체 채팅에서만 작동"};
            RadioButton[] radio = new RadioButton[menuR.length];
            for (int n = 0; n < menuR.length; n++) {
                radio[n] = new RadioButton(ctx);
                radio[n].setText(menuR[n]);
                radio[n].setTextSize(16);
                radio[n].setTextColor(Color.BLACK);
                radio[n].setId(n);
                radios.addView(radio[n]);
            }
            radio[datum.roomType].setChecked(true);
            radios.setPadding(0, 0, 0, dip2px(5));
            radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    datum.roomType = checkedId;
                }
            });
            layout.addView(radios);
            String[] modes = {"채팅 내용이 일치", "시작 부분이 일치", "채팅 내용이 포함"};
            Spinner spin = new Spinner(ctx);
            final ArrayAdapter adapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_1, modes);
            spin.setAdapter(adapter);
            spin.setSelection(datum.type);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    datum.type = (int) id;
                    switch (datum.type) {
                        case 0:
                            txt1.setText("\n이 말을 하면...");
                            break;
                        case 1:
                            txt1.setText("\n이 말로 시작하면...");
                            break;
                        case 2:
                            txt1.setText("\n이 말이 포함되어 있으면...");
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            layout.addView(spin);
            txt1.setText("\n이 말을 하면...");
            txt1.setTextSize(18);
            txt1.setTextColor(Color.BLACK);
            txt2.setHint("내용을 입력하세요...");
            txt2.setTextColor(Color.BLACK);
            txt2.setHintTextColor(Color.GRAY);
            txt3.setText("\n이렇게 대답...");
            txt3.setTextSize(18);
            txt3.setTextColor(Color.BLACK);
            txt4.setHint("내용을 입력하세요...");
            txt4.setTextColor(Color.BLACK);
            txt4.setHintTextColor(Color.GRAY);
            if (datum.input != null) txt2.setText(datum.input);
            if (datum.output != null) txt4.setText(datum.output);
            layout.addView(txt1);
            layout.addView(txt2);
            layout.addView(txt3);
            layout.addView(txt4);
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(ctx);
            scroll.addView(layout);
            dialog.setView(scroll);
            dialog.setNegativeButton("취소", null);
            if (pos != -1) dialog.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StringBuilder src = new StringBuilder();
                    boolean added = false;
                    for (int n = 0; n < data.length; n++) {
                        if (pos == n) continue;
                        if (added) src.append(",");
                        src.append(data[n].toJson());
                        added = true;
                    }
                    Utils.rootSave(ctx, "reply_data.json", "[" + src.toString() + "]");
                    toast("삭제되었습니다.");
                    updateData();
                    SimpleLayout.this.adapter.notifyDataSetChanged();
                }
            });
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String que = txt2.getText().toString();
                        String ans = txt4.getText().toString();
                        if (que.equals("") || ans.equals("")) {
                            toast("입력되지 않은 값이 있습니다.");
                            editDialog(pos);
                        } else {
                            datum.input = que;
                            datum.output = ans;
                            if (pos == -1) {
                                StringBuilder src = new StringBuilder();
                                boolean added = false;
                                for (int n = 0; n < data.length; n++) {
                                    if (n > 0) src.append(",");
                                    src.append(data[n].toJson());
                                    added = true;
                                }
                                if (added) src.append(",");
                                src.append(datum.toJson());
                                Utils.rootSave(ctx, "reply_data.json", "[" + src.toString() + "]");
                            } else {
                                data[pos] = datum;
                                StringBuilder src = new StringBuilder();
                                for (int n = 0; n < data.length; n++) {
                                    if (n > 0) src.append(",");
                                    src.append(data[n].toJson());
                                }
                                Utils.rootSave(ctx, "reply_data.json", "[" + src.toString() + "]");
                            }
                            toast("저장되었습니다.");
                            updateData();
                            SimpleLayout.this.adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

}
