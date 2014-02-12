package pl.fracz.mcr.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.googlecode.androidannotations.annotations.EFragment;

import java.io.File;
import java.io.IOException;

import pl.fracz.mcr.MCR;
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
        return new AlertDialog.Builder(getActivity())
                // .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Record a comment")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecording();
                        try {
                            ((MCR) getActivity()).getSourceFile().addVoiceComment(recordedFile);
                        } catch (CommentNotAddedException e) {
                            e.printStackTrace();
                            recordedFile.delete();
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recordedFile = File.createTempFile("mcr", "commentrecord");
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
