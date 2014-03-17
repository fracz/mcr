package pl.fracz.mcr.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.androidannotations.annotations.EFragment;

import java.io.File;
import java.io.IOException;

import pl.fracz.mcr.MCR;
import pl.fracz.mcr.R;
import pl.fracz.mcr.comment.CommentNotAddedException;

@EFragment
public class RecordCommentPrompt extends SherlockDialogFragment {

    private MediaRecorder recorder;

    private File recordedFile;

    public static RecordCommentPrompt newInstance() {
        return new RecordCommentPrompt();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MCR activity = (MCR) getActivity();
        return new AlertDialog.Builder(getActivity())
                // .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(getActivity().getString(R.string.record_comment))
                .setPositiveButton(getActivity().getString(R.string.record_done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecording();
                        try {
                            activity.getSourceFile().addVoiceComment(recordedFile);
                        } catch (CommentNotAddedException e) {
                            e.printStackTrace();
                            recordedFile.delete();
                            activity.showAlert(getActivity().getString(R.string.unexpectedCommentError));
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.record_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecording();
                        recordedFile.delete();
                        dismiss();
                    }
                }).create();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startRecording();
    }

    private void startRecording() {
        MCR activity = (MCR) getActivity();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recordedFile = new File(activity.getSourceFile().getReviewsDirectory(), "temp.3gp");
            recorder.setOutputFile(recordedFile.getAbsolutePath());
            recorder.prepare();
        } catch (IOException e) {
            Log.e("A", "prepare() failed", e);
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}
