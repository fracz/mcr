package pl.fracz.mcr.source;

import android.app.Activity;
import android.view.Display;

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

    public File get(Activity activity) {
        File destination = createTemporaryFile();
        try {
            packComments(destination, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }

    private void packComments(File destination, Activity activity) throws IOException {
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
        zout.putNextEntry(new ZipEntry(sourceDir.getName() + File.separator + "device_data.txt"));
        addDeviceData(zout, activity);
        zout.close();
    }

    private void addDeviceData(ZipOutputStream zout, Activity activity) throws IOException {
        Display display = activity.getWindowManager().getDefaultDisplay();
        String screenSize = String.format("Screen size: %d x %d\nRotation: %d\nOrientation: %d",
                display.getWidth(), display.getHeight(), display.getRotation(), display.getOrientation());
        zout.write(screenSize.getBytes(), 0, screenSize.length());
    }

    private File createTemporaryFile() {
        String filename = String.format("%s.zip", sourceFileIdentifier);
        File destination = new File(ApplicationSettings.getTemporaryDirectory(), filename);
        if (destination.exists())
            destination.delete();
        return destination;
    }
}
