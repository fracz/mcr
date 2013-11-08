package pl.fracz.mcr.preferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface ApplicationSettings {
    @DefaultString("John Doe")
    String author();

    String styleComments();
}
