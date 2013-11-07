package pl.fracz.mcr.util;

import java.io.*;

public final class FileUtils {
    private FileUtils() {
    }

    public static final String LF = "\n";

    public static String read(File file) throws IOException {
        InputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line).append(LF);
        return sb.toString();
    }
}
