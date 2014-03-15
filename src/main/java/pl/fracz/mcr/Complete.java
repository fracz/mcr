package pl.fracz.mcr;

import android.view.Display;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;

import pl.fracz.mcr.preferences.ReviewState_;
import pl.fracz.mcr.source.CommentsArchive;

@EActivity(R.layout.complete)
public class Complete extends SherlockFragmentActivity {

    @ViewById
    View progress;

    @ViewById
    View thankYou;

    @ViewById
    View thankYouLater;

    @Extra
    long reviewTime;

    @Extra
    String fileIdentifier;

    @InstanceState
    String extraInfo;

    @InstanceState
    boolean uploading = false;

    @Pref
    ReviewState_ state;

    @AfterViews
    void init() {
        if (reviewTime > 0)
            state.reviewTime().put(reviewTime);
        if (state.sent().get()) {
            thankYou();
        } else if (!uploading) {
            if (extraInfo == null)
                createExtraReviewInfo();
            CommentsArchive archive = new CommentsArchive(fileIdentifier);
            uploading = true;
            uploadReviewResult(archive.get(extraInfo));
        }
    }

    @UiThread
    public void thankYou() {
        progress.setVisibility(View.GONE);
        thankYou.setVisibility(View.VISIBLE);
        state.sent().put(true);
    }

    @UiThread
    public void thankYouLater() {
        uploading = false;
        progress.setVisibility(View.GONE);
        thankYouLater.setVisibility(View.VISIBLE);
    }

    @Background
    public void uploadReviewResult(File file) {
        String url = "http://www.mgr.fracz.pl/upload.php";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            //Do something with response...
            thankYou();
        } catch (Exception e) {
            thankYouLater();
        }
    }

    private void createExtraReviewInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        String created = extraInfo = String.format("Screen size: %d x %d\r\nRotation: %d\r\nOrientation: %d\r\nReview time: %ds\r\nFileID: %s",
                display.getWidth(), display.getHeight(), display.getRotation(), display.getOrientation(), state.reviewTime().get() / 1000,
                fileIdentifier);
        extraInfo = state.extraInfo().getOr(created);
        state.extraInfo().put(extraInfo);
    }
}
