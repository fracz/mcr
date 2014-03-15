package pl.fracz.mcr;

import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.complete)
public class Complete extends SherlockFragmentActivity {

    @Extra
    long reviewTime;

    @Extra
    String fileIdentifier;

    @AfterViews
    void init() {
        Toast.makeText(this, String.valueOf(reviewTime), Toast.LENGTH_LONG).show();
    }
}
