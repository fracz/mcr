package pl.fracz.mcr;

import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import pl.fracz.mcr.comment.Comments;

@EActivity(R.layout.complete)
public class Complete extends SherlockFragmentActivity {

    @Extra
    long reviewTime;

    @Extra
    String fileIdentifier;

    @AfterViews
    void init() {
        Comments comments = new Comments(fileIdentifier);
        Toast.makeText(this, "Comments: " + comments.getAllComments().size(), Toast.LENGTH_LONG).show();
    }
}
