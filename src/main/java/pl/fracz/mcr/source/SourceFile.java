package pl.fracz.mcr.source;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import pl.fracz.mcr.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class SourceFile {
    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new PrettifyHighlighter();

    private final View.OnClickListener lineHighlighter = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (selectedLine != null)
                selectedLine.setBackgroundColor(Color.TRANSPARENT);
            selectedLine = (Line) v;
            v.setBackgroundColor(Color.parseColor("#444444"));
        }
    };

    private final String sourceCode;

    private final String identifier;

    private final Comments comments;

    private Line selectedLine;

    private String highlightedSourceCode;

    private SourceFile(String sourceCode) {
        this.sourceCode = sourceCode;
        this.identifier = calculateSourceChecksum();
        this.comments = new Comments(this);
    }

    private String getHighlightedSourceCode() {
        if (highlightedSourceCode == null) {
            highlightedSourceCode = SYNTAX_HIGHLIGHTER.highlight(sourceCode);
        }
        return highlightedSourceCode;
    }

    private String calculateSourceChecksum() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(sourceCode.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    public Collection<Line> getLines(Context context) {
        StringTokenizer tokenizer = new StringTokenizer(getHighlightedSourceCode(), FileUtils.LF);
        Collection<Line> lines = new ArrayList<Line>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            Line line = new Line(context, lines.size() + 1, tokenizer.nextToken());
            line.setOnClickListener(lineHighlighter);
            lines.add(line);
        }
        return lines;
    }

    public Line getSelectedLine() {
        return selectedLine;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addComment(String comment) throws CommentNotAddedException {
        ensureLineIsSelected();
        comments.addComment(getSelectedLine(), comment);
    }

    private void ensureLineIsSelected() throws NoSelectedLineException {
        if (selectedLine == null)
            throw new NoSelectedLineException();
    }

    public static SourceFile createFromString(String sourceCode) {
        return new SourceFile(sourceCode);
    }

    /**
     * Creates source file based on a file reference.
     *
     * @param sourceFile
     * @return
     * @throws IOException
     */
    public static SourceFile createFromFile(File sourceFile) throws IOException {
        String sourceCode = FileUtils.read(sourceFile);
        return createFromString(sourceCode);
    }
}
