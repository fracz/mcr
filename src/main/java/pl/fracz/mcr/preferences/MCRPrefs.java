package pl.fracz.mcr.preferences;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MCRPrefs {
    String lastFile();
}
