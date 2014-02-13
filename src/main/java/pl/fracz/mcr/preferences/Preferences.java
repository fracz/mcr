package pl.fracz.mcr.preferences;

import android.os.Bundle;
import android.preference.Preference;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import org.androidannotations.annotations.EActivity;

import pl.fracz.mcr.R;

@EActivity
public class Preferences extends SherlockPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initializeValidationListeners();
    }

    private void initializeValidationListeners() {
        findPreference("author").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null && newValue.toString().length() > 0)
                    return true;
                return false;
            }
        });
        findPreference("tabSize").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    if (newValue != null) {
                        Integer.valueOf(newValue.toString());
                        return true;
                    }
                } catch (NumberFormatException e) {
                }
                return false;
            }
        });
    }
}
