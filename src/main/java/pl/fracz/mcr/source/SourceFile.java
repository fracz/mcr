package pl.fracz.mcr.source;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class SourceFile {
    private static final String LF = "\n";

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

    private Line selectedLine;

    private final String sourceCode;

    private String highlightedSourceCode;

    private SourceFile(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    private String getHighlightedSourceCode() {
        if (highlightedSourceCode == null) {
            highlightedSourceCode = SYNTAX_HIGHLIGHTER.highlight(sourceCode);
        }
        return highlightedSourceCode;
    }

    public Collection<Line> getLines(Context context) {
        StringTokenizer tokenizer = new StringTokenizer(getHighlightedSourceCode(), LF);
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
        InputStream fileInputStream = new FileInputStream(sourceFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line).append(LF);
        return createFromString(sb.toString());
    }
}
