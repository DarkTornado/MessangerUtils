package com.darktornado.msgutils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SimpleLayout extends BaseLayout {

    public LinearLayout view;

    private ArrayAdapter adapter;
    private ArrayList<String> names = new ArrayList<>();

    public SimpleLayout(Activity ctx) {
        super(ctx);
        init();
    }

    private void init() {
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
//                    editDialog(index);
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

}
