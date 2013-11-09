package pl.fracz.mcr.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import pl.fracz.mcr.R;

public class ApplicationSettings {

    private static SharedPreferences prefs;

    public static void setContext(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getAuthorName() {
        return prefs.getString("author", "John Doe");
    }

    public static boolean highlightSources() {
        return prefs.getBoolean("highlightSources", true);
    }

    public static int getTabSize() {
        try {
            return Integer.valueOf(prefs.getString("tabSize", "4"));
        } catch (NumberFormatException e) {
            return 4;
        }
    }
}
