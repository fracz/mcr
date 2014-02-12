package pl.fracz.mcr.source;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import pl.fracz.mcr.preferences.ApplicationSettings;

public class CommentsArchive {
    private static final int BUFFER_SIZE = 1024;

    private final SourceFile sourceFile;

    public CommentsArchive(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public File get() {
        File destination = createTemporaryFile();
        try {
            packComments(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }

    private void packComments(File destination) throws IOException {
        File sourceDir = sourceFile.getReviewsDirectory();
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
        zout.close();
    }

    private File createTemporaryFile() {
        String filename = String.format("MCR_%s.zip", sourceFile.getIdentifier());
        File destination = new File(ApplicationSettings.getTemporaryDirectory(), filename);
        if (destination.exists())
            destination.delete();
        return destination;
    }

    public static void handleZipFile(String filePath) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(filePath)));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String path = ApplicationSettings.getReviewsDirectory() + File.separator + entry.getName();
            if (entry.isDirectory()) {
                File unzipFile = new File(path);
                if (!unzipFile.isDirectory()) {
                    unzipFile.mkdirs();
                }
            } else {
                FileOutputStream fout = new FileOutputStream(path, false);
                try {
                    for (int c = zis.read(); c != -1; c = zis.read()) {
                        fout.write(c);
                    }
                    zis.closeEntry();
                } finally {
                    fout.close();
                }
            }
        }
    }
}
