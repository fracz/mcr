package pl.fracz.mcr;

import android.content.Intent;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.*;
import pl.fracz.mcr.source.Line;
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
    SourceFile currentFile = SourceFile.createFromString("// Przykładowy kod do Code Review\n    Kasia kasia = new Kasia(\"Jest Fajna\");");

    @OptionsItem
    void openFileSelected() {
		startActivityForResult(new Intent(this, FileChooser_.class), OPEN_FILE);
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
}
