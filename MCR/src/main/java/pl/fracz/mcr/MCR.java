package pl.fracz.mcr;

import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Spanned;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockActivity {

	private static final int OPEN_FILE = 1;

	@Bean(PrettifyHighlighter.class)
	protected SyntaxHighlighter highlighter;

	@ViewById
	TextView source;

	@InstanceState
	String sourceCode = "// Przykladowy kod do Code Review\nKasia kasia = new Kasia(\"Jest Fajna\");";

	@OptionsItem
	void openFileSelected() {
		startActivityForResult(new Intent(this, FileChooser_.class), OPEN_FILE);
	}

	@AfterViews
	@SuppressLint("SetJavaScriptEnabled")
	void initializeSourceComponent() {
		displaySource();
	}

	@OnActivityResult(OPEN_FILE)
	void handleOpenFile(int resultCode, Intent data) {
		if (resultCode == FileChooser.OPEN_OK) {
			sourceCode = data.getDataString();
			displaySource();
		}
	}

	protected void displaySource() {
		Spanned highlighted = highlighter.highlight(sourceCode);
		source.setText(highlighted);
	}
}
