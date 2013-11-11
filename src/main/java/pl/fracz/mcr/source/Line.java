package pl.fracz.mcr.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.fracz.mcr.comment.Comment;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.comment.TextComment;

import java.util.List;

/**
 * View that represents one line of code.
 */
@SuppressLint("ViewConstructor")
public class Line extends LinearLayout {

    private final int lineNumber;

    private final String lineOfCode;

    private final TextView lineNumberView;

    private final SourceFile sourceFile;

    private List<Comment> comments;

    public Line(Context context, SourceFile sourceFile, int lineNumber, String lineOfCode) {
        super(context);
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
        this.lineOfCode = lineOfCode;
        setOrientation(LinearLayout.HORIZONTAL);
        lineNumberView = new TextView(getContext());
        addLineNumber();
        addLineContent();
        fetchComments();
    }

    public int getNumber() {
        return lineNumber;
    }

    public void addTextComment(String comment) throws CommentNotAddedException {
        sourceFile.getComments().addComment(this, new TextComment(comment));
        fetchComments();
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    private void fetchComments() {
        this.comments = sourceFile.getComments().getComments(this);
        markLineIfHasComments();
    }

    private void markLineIfHasComments() {
        if (comments.size() > 0) {
            lineNumberView.setBackgroundColor(Color.parseColor("#008000"));
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
