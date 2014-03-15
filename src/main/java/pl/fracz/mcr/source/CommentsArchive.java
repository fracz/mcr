package pl.fracz.mcr.source;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.fracz.mcr.preferences.ApplicationSettings;

public class CommentsArchive {
    private static final int BUFFER_SIZE = 1024;

    private final String sourceFileIdentifier;

    public CommentsArchive(SourceFile sourceFile) {
        this(sourceFile.getIdentifier());
    }

    public CommentsArchive(String sourceFileIdentifier) {
        this.sourceFileIdentifier = sourceFileIdentifier;
    }

    public File get(String extraInfo) {
        File destination = createTemporaryFile();
        try {
            packComments(destination, extraInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }

    private void packComments(File destination, String extraInfo) throws IOException {
        File sourceDir = SourceFile.getReviewsDirectory(sourceFileIdentifier);
        ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destination)));
        byte data[] = new byte[BUFFER_SIZE];
        for (File file : sourceDir.listFiles()) {
            ZipEntry entry = new ZipEntry(sourceDir.getName() + File.separator + file.getName());
            zout.putNextEntry(entry);
            BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            int count;
            while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
                zout.write(data, 0, count);
            origin.close();
        }
        zout.putNextEntry(new ZipEntry(sourceDir.getName() + File.separator + "extra_info.txt"));
        zout.write(extraInfo.getBytes(), 0, extraInfo.length());
        zout.close();
    }

    private File createTemporaryFile() {
        String filename = String.format("%s.zip", sourceFileIdentifier);
        File destination = new File(ApplicationSettings.getTemporaryDirectory(), filename);
        if (destination.exists())
            destination.delete();
        return destination;
    }
}
