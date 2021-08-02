package com.darktornado.msgutils.scriptapi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class Utils extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Utils";
    }

    @JSStaticFunction
    public static String getWebText(String url) {
        try {
            return Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).get().html();
        } catch (Exception e) {
            return null;
        }
    }

    @JSStaticFunction
    public static Document parse(String url) {
        try {
            return Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).get();
        } catch (Exception e) {
            return null;
        }
    }

}
