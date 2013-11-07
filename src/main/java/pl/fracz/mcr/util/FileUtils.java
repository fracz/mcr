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

    /**
     * Returns the extension of the file, based on its name.
     *
     * @param filename file name (or path)
     * @return extension of the file or NULL if the file does not have an extension
     */
    public static String getExtension(String filename) {
        int extensionPos = filename.lastIndexOf(".");
        if (extensionPos >= 0)
            return filename.substring(extensionPos + 1);
        return null;
    }
}
