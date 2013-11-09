package pl.fracz.mcr.util;

import android.text.TextUtils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String encode(String string) {
        string = TextUtils.htmlEncode(string);
        return string.replace(" ", "&nbsp;");
    }
}
