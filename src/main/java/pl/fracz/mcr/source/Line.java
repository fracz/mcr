package pl.fracz.mcr.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

    private final TextView lineNumber;

    public Line(Context context, int lineNum, String lineOfCode) {
		super(context);
		this.lineNum = lineNum;
		this.lineOfCode = lineOfCode;
		setOrientation(LinearLayout.HORIZONTAL);
        lineNumber = new TextView(getContext());
        addLineNumber();
		addLineContent();
	}

    public int getNumber() {
        return lineNum;
    }

    public void setHasComments(boolean hasComments) {
        lineNumber.setBackgroundColor(hasComments ? Color.BLUE : Color.TRANSPARENT);
    }

    private void addLineNumber() {
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
