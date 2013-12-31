package pl.fracz.mcr.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

import pl.fracz.mcr.R;

public class ApplicationSettings {

    private static SharedPreferences prefs;

    private static File applicationDirectory;

    public static void setContext(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initialieApplicationDirectoryPath(context);
    }

    public static String getAuthorName() {
        return prefs.getString("author", "John Doe");
    }

    public static boolean highlightSources() {
        return prefs.getBoolean("highlightSources", true);
    }

    public static File getApplicationDirectory() {
        return applicationDirectory;
    }

    public static File getReviewsDirectory() {
        File directory = new File(ApplicationSettings.getApplicationDirectory(), "reviews/");
        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }

    public static int getTabSize() {
        try {
            return Integer.valueOf(prefs.getString("tabSize", "4"));
        } catch (NumberFormatException e) {
            return 4;
        }
    }

    private static void initialieApplicationDirectoryPath(Context context) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            applicationDirectory = context.getExternalFilesDir(null);
        } else {
            applicationDirectory = context.getFilesDir();
        }
    }
}
