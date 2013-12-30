package pl.fracz.mcr.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.io.Serializable;

public class TextCommentPrompt extends SherlockDialogFragment {
    private static final String LISTENER_ARG = "listener";

    private EditText commentInput;

    public static TextCommentPrompt newInstance(CommentAddedListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(LISTENER_ARG, listener);
        TextCommentPrompt prompt = new TextCommentPrompt();
        prompt.setArguments(args);
        prompt.setCancelable(true);
        return prompt;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CommentAddedListener onCommentAdded = (CommentAddedListener) getArguments().getSerializable(LISTENER_ARG);
        commentInput = new EditText(getActivity());
        return new AlertDialog.Builder(getActivity())
                // .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Enter comment")
                .setView(commentInput)
                .setPositiveButton("Add comment", new PositiveButtonListener(onCommentAdded))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    public static interface CommentAddedListener extends Serializable {
        void onCommentAdded(String text);
    }

    private class PositiveButtonListener implements DialogInterface.OnClickListener {
        private final CommentAddedListener listener;

        PositiveButtonListener(CommentAddedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String comment = commentInput.getText().toString();
            listener.onCommentAdded(comment);
            dismiss();
        }
    }
}
