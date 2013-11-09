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

    private final int lineNumber;

    private final String lineOfCode;

    private final TextView lineNumberView;

    public Line(Context context, int lineNumber, String lineOfCode) {
        super(context);
        this.lineNumber = lineNumber;
        this.lineOfCode = lineOfCode;
		setOrientation(LinearLayout.HORIZONTAL);
        lineNumberView = new TextView(getContext());
        addLineNumber();
		addLineContent();
	}

    public int getNumber() {
        return lineNumber;
    }

    public void setHasComments(boolean hasComments) {
        if (hasComments) {
            lineNumberView.setBackgroundColor(Color.parseColor("#008000"));
        } else {
            lineNumberView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void addLineNumber() {
        lineNumberView.setText(formatLineNumber());
        lineNumberView.setSingleLine();
        lineNumberView.setWidth(30);
        addView(lineNumberView);
    }

    private String formatLineNumber() {
        return String.format("%d.", lineNumber);
    }

    private void addLineContent() {
		TextView lineContent = new TextView(getContext());
		lineContent.setText(Html.fromHtml(lineOfCode));
        lineContent.setTypeface(Typeface.MONOSPACE);
		addView(lineContent);
	}
}
