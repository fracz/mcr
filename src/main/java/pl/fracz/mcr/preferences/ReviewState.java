package pl.fracz.mcr.preferences;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface ReviewState {
    @DefaultBoolean(false)
    boolean sent();

    long reviewTime();
}
