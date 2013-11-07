package pl.fracz.mcr.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View that represents one line of code.
 */
@SuppressLint("ViewConstructor")
public class Line extends LinearLayout {

	private final int lineNum;

	private final String lineOfCode;

	public Line(Context context, int lineNum, String lineOfCode) {
		super(context);
		this.lineNum = lineNum;
		this.lineOfCode = lineOfCode;
		setOrientation(LinearLayout.HORIZONTAL);
		addLineNumber();
		addLineContent();
	}

	private void addLineNumber() {
		TextView lineNumber = new TextView(getContext());
		lineNumber.setText(String.format("%d.", lineNum));
		lineNumber.setSingleLine();
		lineNumber.setWidth(30);
		addView(lineNumber);
	}

	private void addLineContent() {
		TextView lineContent = new TextView(getContext());
		lineContent.setText(Html.fromHtml(lineOfCode));
        lineContent.setTypeface(Typeface.MONOSPACE);
		addView(lineContent);
	}

}
