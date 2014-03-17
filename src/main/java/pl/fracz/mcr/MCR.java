package pl.fracz.mcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.db.DatabaseHelper;
import pl.fracz.mcr.db.OpenedFile;
import pl.fracz.mcr.db.OpenedFileDao;
import pl.fracz.mcr.fragment.CommentsPreview;
import pl.fracz.mcr.fragment.FilePreview;
import pl.fracz.mcr.fragment.RecordCommentPrompt;
import pl.fracz.mcr.fragment.TextCommentPrompt;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.preferences.Preferences_;
import pl.fracz.mcr.source.CommentsArchive;
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
        OpenedFile lastOpened = openedFileDao.findLastOpened();
        if (fileToOpen != null) {
            try {
                lastOpened = openedFileDao.queryForId(fileToOpen);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (lastOpened != null) {
            try {
                openFile(lastOpened.getPath());
            } catch (IOException e) {
                Log.w("MCR", "Could not open last file", e);
            }
        } else if (hasSourceFile())
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
        if (item.getItemId() == SHARE_COMMENTS_OPTION) {
            shareComments();
            return true;
        }
        if (item.getItemId() == OPEN_RECENT_OPTION) {
            try {
                openFile(item.getTitle().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
    }

    private void shareComments() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/zip");
        CommentsArchive archive = new CommentsArchive(getSourceFile());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(archive.get()));
        startActivity(intent);
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
            MenuItem share = menu.add(Menu.NONE, SHARE_COMMENTS_OPTION, Menu.FIRST, "Share comments");
            share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            share.setIcon(android.R.drawable.ic_menu_share);
        }
        MenuItem item = menu.findItem(R.id.open_recent);
        SubMenu recent = item.getSubMenu();
        List<OpenedFile> recentHistory = openedFileDao.findLastOpened(10);
        if (recentHistory.size() > 1) {
            // remove first one as it is the current file
            recentHistory.remove(0);
            for (OpenedFile file : recentHistory) {
                recent.add(Menu.NONE, OPEN_RECENT_OPTION, Menu.FIRST, file.getPath());
            }
        } else {
            item.setVisible(false);
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
                openFile(openedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
