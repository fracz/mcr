package pl.fracz.mcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.*;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.preferences.Preferences_;
import pl.fracz.mcr.source.Line;
import pl.fracz.mcr.source.NoSelectedLineException;
import pl.fracz.mcr.source.SourceFile;

import java.io.File;
import java.io.IOException;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockFragmentActivity {

    private static final int OPEN_FILE = 1;

    @ViewById
    LinearLayout contents;

    @NonConfigurationInstance
    SourceFile currentFile = SourceFile.createFromString("// Przykładowy kod do Code Review\nKasia kasia = new Kasia(\"Jest Fajna\");", "java");

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
        if (item.getItemId() == 0) {
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
        displaySource();
    }

    @OnActivityResult(OPEN_FILE)
    void handleOpenFile(int resultCode, Intent data) {
        if (resultCode == FileChooser.OPEN_OK) {
            File openedFile = (File) data.getExtras().get(FileChooser.OPENED_FILE_EXTRA_KEY);
            try {
                currentFile = SourceFile.createFromFile(openedFile);
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
}
