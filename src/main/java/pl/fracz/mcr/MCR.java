package pl.fracz.mcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

import java.io.File;
import java.io.IOException;

import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.fragment.CommentsPreview;
import pl.fracz.mcr.fragment.FilePreview;
import pl.fracz.mcr.fragment.RecordCommentPrompt;
import pl.fracz.mcr.fragment.TextCommentPrompt;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.preferences.Preferences_;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationSettings.setContext(this);
    }

    @OptionsItem
    void openFileSelected() {
        startActivityForResult(new Intent(this, FileChooser_.class), OPEN_FILE);
    }

    @Override
    public void onBackPressed() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) commentsPreviewContainer.getLayoutParams();
        if (params.weight > 0) {
            params.weight = 0;
            commentsPreviewContainer.setLayoutParams(params);
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem
    void openPreferencesSelected() {
        startActivity(new Intent(this, Preferences_.class));
    }

    @AfterViews
    void initializeSourceComponent() {
        if (hasSourceFile())
            filePreview.displaySourceFile(currentFile);
    }

    public void onLineSelected(Line line) {
        commentsPreview.displayComments(line);
    }

    public SourceFile getSourceFile() {
        return currentFile;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        try {
            return handleCommentAdd(item) || super.onMenuItemSelected(featureId, item);
        } catch (NoSelectedLineException e) {
            showAlert(getString(R.string.chooseLineToComment));
        } catch (CommentNotAddedException e) {
            showAlert(getString(R.string.unexpectedCommentError));
        }
        return true;
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

    @OnActivityResult(OPEN_FILE)
    void handleOpenFile(int resultCode, Intent data) {
        if (resultCode == FileChooser.OPEN_OK) {
            File openedFile = (File) data.getExtras().get(FileChooser.OPENED_FILE_EXTRA_KEY);
            try {
                currentFile = SourceFile.createFromFile(openedFile);
                invalidateOptionsMenu();
                filePreview.displaySourceFile(currentFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String info) {
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
