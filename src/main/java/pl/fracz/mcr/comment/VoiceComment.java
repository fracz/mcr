package pl.fracz.mcr.comment;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import pl.fracz.mcr.R;
import pl.fracz.mcr.source.SourceFile;

public class VoiceComment extends Comment {

    private static final String FILE = "file";

    VoiceComment(JSONObject jsonObject, SourceFile sourceFile) throws JSONException {
        super(jsonObject, sourceFile);
        safePut(FILE, jsonObject.get(FILE));
    }

    public VoiceComment(File recordedFile) {
        safePut(FILE, recordedFile.getName());
    }

    @Override
    public View getView(Context context) {
        View view = super.getView(context);
        try {
            PlayRecord playRecord = new PlayRecord(view);
            view.findViewById(R.id.play).setOnClickListener(playRecord);
            view.findViewById(R.id.stop).setOnClickListener(playRecord);
        } catch (IOException e) {
            Log.e("VoiceComment", "Could not open voice comment.", e);
            view.findViewById(R.id.play).setVisibility(View.GONE);
            view.findViewById(R.id.error).setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.comment_voice;
    }

    public static File moveRecordedFile(SourceFile sourceFile, File recordedFile) {
        String destinationFileName = String.format("%d_%d.3gp", sourceFile.getSelectedLine().getNumber(), System.currentTimeMillis());
        File destination = new File(sourceFile.getReviewsDirectory(), destinationFileName);
        recordedFile.renameTo(destination);
        return destination;
    }

    private File getRecordFile() {
        return new File(sourceFile.getReviewsDirectory(), String.valueOf(safeGet(FILE)));
    }

    private class PlayRecord implements View.OnClickListener, MediaPlayer.OnCompletionListener {

        private final MediaPlayer mediaPlayer = new MediaPlayer();

        private final View view;

        public PlayRecord(View view) throws IOException {
            this.view = view;
            mediaPlayer.setDataSource(getRecordFile().getAbsolutePath());
            mediaPlayer.setOnCompletionListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.play) {
                play();
            } else {
                stop();
            }

        }

        private void play() {
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            view.findViewById(R.id.play).setVisibility(View.GONE);
            view.findViewById(R.id.stop).setVisibility(View.VISIBLE);
        }

        private void stop() {
            onCompletion(mediaPlayer);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.stop();
            ;
            view.findViewById(R.id.play).setVisibility(View.VISIBLE);
            view.findViewById(R.id.stop).setVisibility(View.GONE);
        }
    }
}
