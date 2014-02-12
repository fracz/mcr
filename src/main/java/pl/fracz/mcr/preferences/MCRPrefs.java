package pl.fracz.mcr.preferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MCRPrefs {
    String lastFile();
}
