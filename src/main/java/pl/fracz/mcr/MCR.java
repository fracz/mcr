package pl.fracz.mcr;

import java.util.StringTokenizer;

import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import pl.fracz.mcr.view.Line;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

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

	private final View.OnClickListener lineHighlighter = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (currentLine != null)
				currentLine.setBackgroundColor(Color.TRANSPARENT);
			currentLine = v;
			v.setBackgroundColor(Color.parseColor("#444444"));
		}
	};

	private View currentLine;

	@Bean(PrettifyHighlighter.class)
	protected SyntaxHighlighter highlighter;

	@ViewById
	LinearLayout contents;

	@InstanceState
	String sourceCode = "// Przykladowy kod do Code Review\n    Kasia kasia = new Kasia(\"Jest Fajna\");";

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
			sourceCode = data.getDataString();
			displaySource();
		}
	}

	protected void displaySource() {
		String highlighted = highlighter.highlight(sourceCode);
		StringTokenizer tokenizer = new StringTokenizer(highlighted, "\n");
		contents.removeAllViews();
		int lineNum = 1;
		while (tokenizer.hasMoreTokens()) {
			Line line = new Line(this, lineNum++, tokenizer.nextToken());
			line.setOnClickListener(lineHighlighter);
			contents.addView(line);
			// String lineOfCode = (lineNum++) + ". " + tokenizer.nextToken();
			// TextView line = new TextView(this);
			// line.setTypeface(Typeface.MONOSPACE);
			// line.setText(Html.fromHtml(lineOfCode));
			// line.setOnClickListener(lineHighlighter);
			// contents.addView(line);
		}
	}
}
