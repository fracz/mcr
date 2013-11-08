package pl.fracz.mcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.preferences.Preferences_;
import pl.fracz.mcr.source.Line;
import pl.fracz.mcr.source.NoSelectedLineException;
import pl.fracz.mcr.source.SourceFile;

import java.io.File;
import java.io.IOException;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.mcr)
public class MCR extends SherlockFragmentActivity {

    private static final int OPEN_FILE = 1;
    private static final int PREDEFINED_COMMENT_OPTION = -1;

    @ViewById
    LinearLayout contents;

    @StringArrayRes
    String[] styleComments;
    @StringArrayRes
    String[] errorComments;
    @StringArrayRes
    String[] otherComments;

    @NonConfigurationInstance
    SourceFile currentFile;

    @OptionsItem
    void openFileSelected() {
        startActivityForResult(new Intent(this, FileChooser_.class), OPEN_FILE);
    }

    @OptionsItem
    void openPreferencesSelected() {
        startActivity(new Intent(this, Preferences_.class));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == -1) {
            try {
                currentFile.addTextComment(item.getTitle().toString());
                return true;
            } catch (NoSelectedLineException e) {
                showAlert("Wybierz najpierw linię, którą chcesz skomentować.");
            } catch (CommentNotAddedException e) {
                showAlert("Wystąpił nieoczekiwany błąd, komentarz nie został zapisany.");
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @AfterViews
    void initializeSourceComponent() {
        if (hasSourceFile())
            displaySource();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (hasSourceFile()) {
            addCommentsMenu(menu, R.string.styleComments, styleComments, R.drawable.ic_action_warning);
            addCommentsMenu(menu, R.string.errorComments, errorComments, R.drawable.ic_action_error);
            addCommentsMenu(menu, R.string.otherComments, otherComments, R.drawable.ic_action_help);
            MenuItem textComment = menu.add(Menu.NONE, 123, Menu.FIRST, R.string.textComment);
            textComment.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            textComment.setIcon(R.drawable.ic_action_chat);
            MenuItem voiceComment = menu.add(Menu.NONE, 124, Menu.FIRST, R.string.voiceComment);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            displaySource();
        }
    }

    protected void displaySource() {
        contents.removeAllViews();
        for (Line line : currentFile.getLines(this))
            contents.addView(line);
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
}
