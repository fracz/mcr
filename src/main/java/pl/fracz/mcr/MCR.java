package pl.fracz.mcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.comment.Comments;
import pl.fracz.mcr.db.DatabaseHelper;
import pl.fracz.mcr.db.OpenedFile;
import pl.fracz.mcr.db.OpenedFileDao;
import pl.fracz.mcr.fragment.CommentsPreview;
import pl.fracz.mcr.fragment.FilePreview;
import pl.fracz.mcr.fragment.RecordCommentPrompt;
import pl.fracz.mcr.fragment.TextCommentPrompt;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.source.Line;
import pl.fracz.mcr.source.NoSelectedLineException;
import pl.fracz.mcr.source.SourceFile;

@EActivity(R.layout.mcr)
@OptionsMenu(R.menu.mcr)
public class MCR extends SherlockFragmentActivity {

    private static final int OPEN_FILE = 1;
    private static final int PREDEFINED_COMMENT_OPTION = -1;
    private static final int TEXT_COMMENT_OPTION = 123;
    private static final int VOICE_COMMENT_OPTION = 124;
    private static final int SHARE_COMMENTS_OPTION = 125;
    private static final int OPEN_RECENT_OPTION = 126;

    @FragmentById
    FilePreview filePreview;

    @FragmentById
    CommentsPreview commentsPreview;

    @ViewById
    FrameLayout commentsPreviewContainer;

    @NonConfigurationInstance
    SourceFile currentFile;

    @StringArrayRes
    String[] styleComments;
    @StringArrayRes
    String[] errorComments;
    @StringArrayRes
    String[] otherComments;

    @OrmLiteDao(helper = DatabaseHelper.class, model = OpenedFile.class)
    OpenedFileDao openedFileDao;

    @Extra
    String fileToOpen = null;

    @InstanceState
    long reviewStarted = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationSettings.setContext(this);
    }

    @Override
    public void onBackPressed() {
        if (isCommentsPreviewOpened()) {
            closeCommentsPreview();
        }
    }

    private boolean isCommentsPreviewOpened() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) commentsPreviewContainer.getLayoutParams();
        return params.weight > 0;
    }

    private void closeCommentsPreview() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) commentsPreviewContainer.getLayoutParams();
        params.weight = 0;
        commentsPreviewContainer.setLayoutParams(params);
    }

    @AfterViews
    void initializeSourceComponent() {
        if (!hasSourceFile()) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(Bootstrap.CURRENT_TASK_FILE)));

                // do reading, usually loop until end of file reading
                String mLine = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (mLine != null) {
                    sb.append(mLine + "\n");
                    mLine = reader.readLine();
                }

                reader.close();
                currentFile = SourceFile.createFromString(sb.toString(), "java");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        filePreview.displaySourceFile(currentFile);
    }

    @OptionsItem
    public void finishReview() {
        Comments comments = new Comments(currentFile);
        if (comments.getAllComments().size() > 0) {
            long reviewTime = System.currentTimeMillis() - reviewStarted;
            Complete_.intent(this).reviewTime(reviewTime).fileIdentifier(currentFile.getIdentifier()).start();
        } else {
            Toast.makeText(this, getString(R.string.no_comments_added), Toast.LENGTH_LONG).show();
            Bootstrap_.intent(this).start();
        }
        finish();
    }

    public void onLineSelected(Line line) {
        commentsPreview.displayComments(line);
        if (isCommentsPreviewOpened() && line.getComments().isEmpty()) {
            closeCommentsPreview();
        }
        finishIfTimeOut();
    }

    public SourceFile getSourceFile() {
        return currentFile;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        try {
            if (item.getItemId() == SHARE_COMMENTS_OPTION) {
                shareComments();
                return true;
            }
            try {
                return handleCommentAdd(item) || super.onMenuItemSelected(featureId, item);
            } catch (NoSelectedLineException e) {
                showAlert(getString(R.string.chooseLineToComment));
            } catch (CommentNotAddedException e) {
                showAlert(getString(R.string.unexpectedCommentError));
            }
            return true;
        } finally {
            finishIfTimeOut();
        }
    }

    private void finishIfTimeOut() {
        if (timeOut())
            finishReview();
    }

    private void shareComments() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/zip");
//        CommentsArchive archive = new CommentsArchive(getSourceFile());
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(archive.get()));
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (hasSourceFile()) {
            addCommentsMenu(menu, R.string.styleComments, styleComments, R.drawable.ic_action_warning);
            addCommentsMenu(menu, R.string.errorComments, errorComments, R.drawable.ic_action_error);
            addCommentsMenu(menu, R.string.otherComments, otherComments, R.drawable.ic_action_help);
            MenuItem textComment = menu.add(Menu.NONE, TEXT_COMMENT_OPTION, Menu.FIRST, R.string.textComment);
            textComment.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            textComment.setIcon(R.drawable.ic_action_chat);
            MenuItem voiceComment = menu.add(Menu.NONE, VOICE_COMMENT_OPTION, Menu.FIRST, R.string.voiceComment);
            voiceComment.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            voiceComment.setIcon(R.drawable.ic_action_mic);
//            MenuItem share = menu.add(Menu.NONE, SHARE_COMMENTS_OPTION, Menu.FIRST, "Share comments");
//            share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//            share.setIcon(android.R.drawable.ic_menu_share);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void addCommentsMenu(Menu menu, int titleRes, String[] comments, int icon) {
        SubMenu commentsMenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.FIRST, titleRes);
        menu.getItem(menu.size() - 1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        commentsMenu.setIcon(icon);
        for (String comment : comments)
            commentsMenu.add(0, PREDEFINED_COMMENT_OPTION, 0, comment);
    }

    private boolean timeOut() {
        return System.currentTimeMillis() > reviewStarted + Bootstrap.CURRENT_TASK_TIME_LIMIT;
    }

    private void openFile(String absoluteFilePath) throws IOException {
        File file = new File(absoluteFilePath);
        currentFile = SourceFile.createFromFile(file);
        openedFileDao.registerFileOpen(currentFile, file);
        invalidateOptionsMenu();
        filePreview.displaySourceFile(currentFile);
    }

    public void showAlert(String info) {
        new AlertDialog.Builder(this).setMessage(info).setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private boolean hasSourceFile() {
        return currentFile != null;
    }

    private boolean handleCommentAdd(MenuItem item) throws CommentNotAddedException {
        switch (item.getItemId()) {
            case PREDEFINED_COMMENT_OPTION:
                currentFile.addTextComment(item.getTitle().toString());
                break;
            case TEXT_COMMENT_OPTION:
                currentFile.ensureLineIsSelected();
                TextCommentPrompt.newInstance().show(getSupportFragmentManager(), "textComment");
                break;
            case VOICE_COMMENT_OPTION:
                currentFile.ensureLineIsSelected();
                RecordCommentPrompt.newInstance().show(getSupportFragmentManager(), "voiceComment");
                break;
            default:
                return false;
        }
        return true;
    }

}
