package pl.fracz.mcr;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import pl.fracz.mcr.comment.Comments;
import pl.fracz.mcr.preferences.ApplicationSettings;

@EActivity(R.layout.bootstrap)
public class Bootstrap extends SherlockFragmentActivity {

    public static final String CURRENT_TASK_FILE = "review/Review1.java";

    private static final String CURRENT_TASK_ID = "46ce9836d5c680b3434a116f82c05006b3e67511";

    public static final long CURRENT_TASK_TIME_LIMIT = 5L * 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationSettings.setContext(this);
    }

    @AfterViews
    void init() {
        Comments comments = new Comments(CURRENT_TASK_ID);
        if (comments.getAllComments().size() > 0) {
            Complete_.intent(this).fileIdentifier(CURRENT_TASK_ID).start();
            finish();
        }
    }

    @Click
    void startBtn(){
        startActivity(new Intent(this, MCR_.class));
        finish();
    }
}
