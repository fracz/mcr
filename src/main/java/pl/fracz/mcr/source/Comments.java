package pl.fracz.mcr.source;

import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.fracz.mcr.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Comments {

    private final SourceFile sourceFile;

    private final File commentsFile;

    private final JSONArray commentsData;

    public Comments(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
        this.commentsFile = new File(Environment.getExternalStorageDirectory() + "/MCR/reviews/" + sourceFile.getIdentifier() + "/comments.json");
        this.commentsData = createCommentsData();
    }

    public void addComment(Line line, String comment) throws CommentNotAddedException {
        try {
            commentsData.put(new CommentItem(line, comment));
            save();
        } catch (JSONException e) {
            throw new CommentNotAddedException();
        } catch (IOException e) {
            throw new CommentNotAddedException();
        }
    }

    private boolean fileExists() {
        return commentsFile.exists();
    }

    private JSONArray createCommentsData() {
        if (fileExists()) {
            try {
                String contents = FileUtils.read(commentsFile);
                return new JSONArray(contents);
            } catch (IOException e) {
            } catch (JSONException e) {
            }
        }
        return new JSONArray();
    }

    private void save() throws IOException {
        if (!fileExists())
            commentsFile.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(commentsFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(commentsData.toString());
        bw.close();
    }

    private static class CommentItem extends JSONObject {
        public CommentItem(Line line, String comment) throws JSONException {
            put("line", line.getNumber());
            put("comment", comment);
        }
    }
}
