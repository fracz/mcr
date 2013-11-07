package pl.fracz.mcr.source;

import android.content.Context;
import com.googlecode.androidannotations.annotations.EBean;

import java.io.*;

@EBean
public class SourceFile {

    private static final String LF = "\n";

    private final Context context;

    protected SourceFile(Context context) {
        this.context = context;
    }

    public static SourceFile createFromString(String sourceCode) {
        return null;
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
        String line = null;
        while ((line = reader.readLine()) != null)
            sb.append(line).append(LF);
        return createFromString(sb.toString());
    }
}
