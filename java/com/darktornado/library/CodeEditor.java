package com.darktornado.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Stack;

public class CodeEditor extends EditText {

    private Context ctx;
    private Rect rect;
    private Paint paint;
    private boolean block = false;
    private Stack<History> before;
    private Stack<History> after;
    private String search;
    private int searchIndex = 0;

    public CodeEditor(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(dip2px(13));
        before = new Stack<>();
        after = new Stack<>();
        setTextSize(15);
        setTextColor(Color.BLACK);
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "RobotoMono-Regular.ttf");
        setTypeface(font);
        initTextWatcher(ctx);
    }

    private void initTextWatcher(Context ctx){
        final String[] blueData = new String[]{"function", "return", "var", "let", "const", "if", "else", "switch", "for", "while", "do", "break", "continue", "case", "in", "with", "true", "false", "new", "null", "undefined", "typeof", "delete", "try", "catch", "finally", "prototype", "this", "super", "default", "indexOf", "length"};
        final String[] redData = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."};

        final CharSequence[] data = new CharSequence[2];
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (block) return;
                data[0] = s.subSequence(start, start + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (block) return;
                data[1] = s.subSequence(start, start + count);
                CodeEditor.this.before.push(new History(data, start));
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    codeHighlight(s, blueData, redData);
                } catch (Exception e) {
                    Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean undo() {
        if (before.empty()) return false;
        History data = before.pop();
        int start = data.index;
        int end = start + data.after.length();
        block = true;
        getEditableText().replace(start, end, data.before);
        after.push(data);
        block = false;
        return true;
    }

    public boolean redo() {
        if (after.empty()) return false;
        History data = after.pop();
        int start = data.index;
        int end = start + data.before.length();
        block = true;
        getEditableText().replace(start, end, data.after);
        before.push(data);
        block = false;
        return true;
    }

    public void insertWord(String word) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        getEditableText().replace(start, end, word);
    }

    public void clearHistory(){
        before.clear();
        after.clear();
    }

    public String getLastSearchedWord(){
        return search;
    }

    public boolean searchWord(String word) {
        if (search == null) searchIndex = 0;
        else if (!search.equals(word)) searchIndex = 0;
        search = word;
        int pos = getText().toString().indexOf(word, searchIndex);
        if (pos == -1) return false;
        int end = pos + word.length();
        setSelection(pos, end);
        searchIndex = end;
        return true;
    }


    public void codeHighlight(Editable s, String[] blueData, String[] redData) {
        String str = s.toString();
        if (str.length() == 0) return;
        ForegroundColorSpan spans[] = s.getSpans(0, s.length(), ForegroundColorSpan.class);
        for (int n = 0; n < spans.length; n++) {
            s.removeSpan(spans[n]);
        }
        highlightForJS(s, str);
        for (int n = 0; n < blueData.length; n++) {
            int start = 0;
            while (start >= 0) {
                int index = str.indexOf(blueData[n], start);
                int end = index + blueData[n].length();
                if (index >= 0) {
                    if (s.getSpans(index, end, ForegroundColorSpan.class).length == 0 && isSeperated(str, index, end - 1))
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 21, 101, 192)),
                                index, end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    end = -1;
                }
                start = end;
            }
        }
        for (int n = 0; n < redData.length; n++) {
            int start = 0;
            while (start >= 0) {
                int index = str.indexOf(redData[n], start);
                int end = index + 1;
                if (index >= 0) {
                    if (s.getSpans(index, end, ForegroundColorSpan.class).length == 0 && checkNumber(str, index))
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 191, 54, 12)),
                                index, end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    end = -1;
                }
                start = end;
            }
        }
    }

    private void highlightForJS(Editable s, String str) {
        int start = 0;
        while (start >= 0) {
            int index = str.indexOf("/*", start);
            int end = str.indexOf("*/", index + 2);
            if (index >= 0 && end >= 0) {
                s.setSpan(new ForegroundColorSpan(Color.argb(255, 139, 195, 74)),
                        index, end + 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                end = -5;
            }
            start = end + 2;
        }

        start = 0;
        while (start >= 0) {
            int index = str.indexOf("//", start);
            int end = str.indexOf("\n", index + 1);
            if (index >= 0 && end >= 0) {
                s.setSpan(new ForegroundColorSpan(Color.argb(255, 139, 195, 74)),
                        index, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                end = -1;
            }
            start = end;
        }

        start = 0;
        while (start >= 0) {
            int index = str.indexOf("\"", start);
            ForegroundColorSpan[] span = s.getSpans(index, index + 1, ForegroundColorSpan.class);
            while (index > 0 && str.charAt(index - 1) == '\\' || span.length > 0) {
                index = str.indexOf("\"", index + 1);
                span = s.getSpans(index, index + 1, ForegroundColorSpan.class);
            }

            int end = str.indexOf("\"", index + 1);
            while (end > 0 && str.charAt(end - 1) == '\\') {
                end = str.indexOf("\"", end + 1);
            }

            if (index >= 0 && end >= 0) {
                span = s.getSpans(index, end + 1, ForegroundColorSpan.class);
                if (span.length > 0) {
                    if (str.substring(index + 1, end).contains("/*") && str.substring(index + 1, end).contains("*/")) {
                        for (int n = 0; n < span.length; n++) {
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (str.substring(index + 1, end).contains("//")) {
                        span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan.class);
                        for (int n = 0; n < span.length; n++) {
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                            index, end + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                end = -5;
            }
            start = end + 1;
        }
        start = 0;
        while (start >= 0) {

            int index = str.indexOf("'", start);
            ForegroundColorSpan[] span = s.getSpans(index, index + 1, ForegroundColorSpan.class);
            while (index > 0 && str.charAt(index - 1) == '\\' || span.length > 0) {
                index = str.indexOf("'", index + 1);
                span = s.getSpans(index, index + 1, ForegroundColorSpan.class);
            }

            int end = str.indexOf("'", index + 1);
            while (end > 0 && str.charAt(end - 1) == '\\') {
                end = str.indexOf("'", end + 1);
            }
            if (index >= 0 && end >= 0) {
                span = s.getSpans(index, end + 1, ForegroundColorSpan.class);
                if (span.length > 0) {
                    if (str.substring(index + 1, end).contains("/*") && str.substring(index + 1, end).contains("*/")) {
                        for (int n = 0; n < span.length; n++) {
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (str.substring(index + 1, end).contains("//")) {
                        span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan.class);
                        for (int n = 0; n < span.length; n++) {
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 160, 0)),
                            index, end + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                end = -5;
            }
            start = end + 1;
        }
    }

    public boolean isNumber(String value) {
        try {
            double a = Double.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkNumber(String str, int index) {
        int start = getStartPos(str, index);
        int end = getEndPos(str, index);
        if (str.charAt(end - 1) == '.') return false;
        if (start == 0) {
            if (str.charAt(start) == '.') return false;
            return isNumber(str.substring(start, end));
        } else {
            if (str.charAt(start + 1) == '.') return false;
            return isNumber(str.substring(start + 1, end));
        }
    }

    private boolean isSplitPoint(char ch) {
        if (ch == '\n') return true;
        return " []{}()=-*/%&|!?:;,<>=^~".contains(ch + "");
    }

    private int getStartPos(String str, int index) {
        while (index > 0) {
            if (isSplitPoint(str.charAt(index))) return index;
            index--;
        }
        return 0;
    }

    private int getEndPos(String str, int index) {
        while (str.length() > index) {
            if (isSplitPoint(str.charAt(index))) return index;
            index++;
        }
        return str.length();
    }

    private boolean isSeperated(String str, int start, int end) {
        boolean front = false;
        char[] points = " []{}()+-*/%&|!?:;,<>=^~.".toCharArray();
        if (start == 0) {
            front = true;
        } else if (str.charAt(start - 1) == '\n') {
            front = true;
        } else {
            for (int n = 0; n < points.length; n++) {
                if (str.charAt(start - 1) == points[n]) {
                    front = true;
                    break;
                }
            }
        }
        if (front) {
            try {
                if (str.charAt(end + 1) == '\n') {
                    return true;
                } else {
                    for (int n = 0; n < points.length; n++) {
                        if (str.charAt(end + 1) == points[n]) return true;
                    }
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        int num = 1;
        for (int n = 0; n < count; n++) {
            int baseline = getLineBounds(n, null);
            if (n == 0||getText().charAt(getLayout().getLineStart(n) - 1) == '\n') {
                canvas.drawText(num+"", rect.left | rect.centerY(), baseline, paint);
                num++;
            }
        }
        int pad = String.valueOf(num - 1).length() * 8;
        int pad2 = dip2px(9);
        setPadding(dip2px(pad + 2), pad2, pad2, pad2);
        super.onDraw(canvas);
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

    public static class History {
        public CharSequence before;
        public CharSequence after;
        public int index;

        public History(CharSequence[] data, int index) {
            this.before = data[0];
            this.after = data[1];
            this.index = index;
        }
    }

}
