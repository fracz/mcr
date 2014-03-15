package pl.fracz.mcr;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.bootstrap)
public class Bootstrap extends SherlockFragmentActivity {

    @Click
    void startBtn(){
        startActivity(new Intent(this, MCR_.class));
        finish();
    }
}
