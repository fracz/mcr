package pl.fracz.mcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.*;
import pl.fracz.mcr.dialog.ProgressFragment;
import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import pl.fracz.mcr.view.Line;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockFragmentActivity {

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
	String sourceCode = "// Przykładowy kod do Code Review\n    Kasia kasia = new Kasia(\"Jest Fajna\");";

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

    ProgressFragment progress;

    @UiThread
	protected void displaySource() {
        progress = ProgressFragment.newInstance("Ładowanie pliku...");
        progress.show(getSupportFragmentManager(), "loading");
		buildSourceToReview();
	}

    @Background
    protected void buildSourceToReview(){
        String highlighted = highlighter.highlight(sourceCode);
        StringTokenizer tokenizer = new StringTokenizer(highlighted, "\n");
        Collection<Line> lines = new ArrayList<>();
        int lineNum = 1;
        while (tokenizer.hasMoreTokens()) {
            Line line = new Line(this, lineNum++, tokenizer.nextToken());
            line.setOnClickListener(lineHighlighter);
            lines.add(line);
        }
        attachSourceLines(lines);
    }

    @UiThread
    protected void attachSourceLines(Collection<Line> lines){
        contents.removeAllViews();
        for(Line line : lines)
            contents.addView(line);
        progress.dismiss();
    }
}
