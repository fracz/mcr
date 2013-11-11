package pl.fracz.mcr.comment;

import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.source.Line;
import pl.fracz.mcr.source.SourceFile;
import pl.fracz.mcr.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Comments {

    private final SourceFile sourceFile;

    private final File commentsFile;

    private final JSONArray commentsData;

    public Comments(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
        this.commentsFile = new File(Environment.getExternalStorageDirectory() + "/MCR/reviews/" + sourceFile.getIdentifier() + "/comments.json");
        this.commentsData = createCommentsData();
    }

    public void addComment(Line line, Comment comment) throws CommentNotAddedException {
        try {
            comment.setCreationTime();
            comment.setLineNumber(line.getNumber());
            comment.setAuthor(ApplicationSettings.getAuthorName());
            commentsData.put(comment);
            save();
        } catch (IOException e) {
            throw new CommentNotAddedException();
        }
    }

    public List<Comment> getComments(Line line) {
        List<Comment> lineComments = new LinkedList<>();
        for (int i = 0; i < commentsData.length(); i++) {
            try {
                Comment comment = Comment.fromJSONObject(commentsData.getJSONObject(i));
                if (comment.getLineNumber() == line.getNumber()) {
                    lineComments.add(comment);
                }
            } catch (JSONException e) {
            }
        }
        return lineComments;
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
}
