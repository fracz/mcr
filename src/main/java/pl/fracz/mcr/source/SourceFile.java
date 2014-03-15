package pl.fracz.mcr.source;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import pl.fracz.mcr.MCR;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.comment.Comments;
import pl.fracz.mcr.comment.VoiceComment;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import pl.fracz.mcr.util.FileUtils;
import pl.fracz.mcr.util.StringUtils;

public class SourceFile {
    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new PrettifyHighlighter();

    private final View.OnClickListener lineHighlighter = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (selectedLine != null)
                selectedLine.setBackgroundColor(Color.TRANSPARENT);
            selectedLine = (Line) v;
            selectedLine.setBackgroundColor(Color.parseColor("#444444"));
            ((MCR) selectedLine.getContext()).onLineSelected(selectedLine);
        }
    };

    private final String sourceCode;

    private final String identifier;

    private final Comments comments;

    private final String language;

    private Line selectedLine;

    private String highlightedSourceCode;

    private SourceFile(String sourceCode, String language) {
        this.sourceCode = sourceCode;
        this.language = language;
        this.identifier = calculateSourceChecksum();
        this.comments = new Comments(this);
    }

    private String getHighlightedSourceCode() {
        if (highlightedSourceCode == null) {
            highlightedSourceCode = highlightSourceCode();
        }
        return highlightedSourceCode;
    }

    private String highlightSourceCode() {
        String code = replaceTabs();
        if (ApplicationSettings.highlightSources()) {
            return SYNTAX_HIGHLIGHTER.highlight(code, language);
        } else {
            return StringUtils.encode(code);
        }
    }

    private String replaceTabs() {
        StringBuilder tabReplacement = new StringBuilder();
        for (int i = 0; i < ApplicationSettings.getTabSize(); i++)
            tabReplacement.append(" ");
        return sourceCode.replace("\t", tabReplacement.toString());
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
            Line line = new Line(context, this, lines.size() + 1, tokenizer.nextToken());
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

    public void addTextComment(String comment) throws CommentNotAddedException {
        ensureLineIsSelected();
        getSelectedLine().addTextComment(comment);
    }

    public void addVoiceComment(File recordedFile) throws CommentNotAddedException {
        ensureLineIsSelected();
        File moved = VoiceComment.moveRecordedFile(this, recordedFile);
        getSelectedLine().addVoiceComment(moved);
    }

    Comments getComments() {
        return comments;
    }

    public void ensureLineIsSelected() throws NoSelectedLineException {
        if (selectedLine == null)
            throw new NoSelectedLineException();
    }

    public static SourceFile createFromString(String sourceCode, String language) {
        return new SourceFile(sourceCode, language);
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
        return createFromString(sourceCode, FileUtils.getExtension(sourceFile.getName()));
    }

    public File getReviewsDirectory() {
        return getReviewsDirectory(getIdentifier());
    }

    public static File getReviewsDirectory(String sourceFileIdentifier) {
        File dir = new File(ApplicationSettings.getReviewsDirectory(), sourceFileIdentifier + "/");
        dir.mkdirs();
        return dir;
    }
}
