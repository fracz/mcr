package pl.fracz.mcr.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

import pl.fracz.mcr.MCR;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.source.SourceFile;

public class TextCommentPrompt extends SherlockDialogFragment {
    public static final String COMMENT_ARG = "comment";

    private EditText commentInput;

    public static TextCommentPrompt newInstance() {
        return new TextCommentPrompt();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        commentInput = new EditText(getActivity());
        if (savedInstanceState != null) {
            String comment = savedInstanceState.getString(COMMENT_ARG);
            if (comment != null)
                commentInput.setText(comment);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle("Enter comment")
                .setView(commentInput)
                .setPositiveButton("Add comment", new PositiveButtonListener())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(COMMENT_ARG, getEnteredComment());
        super.onSaveInstanceState(outState);
    }

    private String getEnteredComment() {
        return commentInput.getText().toString();
    }

    private class PositiveButtonListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                SourceFile file = ((MCR) getActivity()).getSourceFile();
                file.addTextComment(getEnteredComment());
            } catch (CommentNotAddedException e) {
                Log.e(TextCommentPrompt.class.getSimpleName(), "Could not add custom comment.", e);
            }
            dismiss();
        }
    }
}
