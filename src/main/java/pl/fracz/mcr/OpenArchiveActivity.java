package pl.fracz.mcr;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import pl.fracz.mcr.db.DatabaseHelper;
import pl.fracz.mcr.db.OpenedFile;
import pl.fracz.mcr.db.OpenedFileDao;
import pl.fracz.mcr.preferences.ApplicationSettings;

@EActivity(R.layout.open_archive)
public class OpenArchiveActivity extends SherlockFragmentActivity implements Button.OnClickListener {

    private String fileId;

    @ViewById
    Button open;

    @ViewById
    Button exit;

    @ViewById
    TextView fileName;

    @OrmLiteDao(helper = DatabaseHelper.class, model = OpenedFile.class)
    OpenedFileDao openedFileDao;

    @AfterViews
    public void init() {
        if (getIntent().getData() != null) {
            try {
                fileId = handleZipFile(getIntent().getData().getPath());
                open.setOnClickListener(this);
                exit.setOnClickListener(this);
                OpenedFile file = openedFileDao.queryForId(fileId);
                fileName.setText(file.getPath());
                return;
            } catch (IOException e) {
                Log.e("MCR", "Could not open shared comments", e);
            } catch (SQLException e) {
                Log.e("MCR", "Could not open file", e);
            }
        }
        MCR_.intent(this).start();
        finish();
    }


    private String handleZipFile(String filePath) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(filePath)));
        ZipEntry entry;
        String fileId = "";
        while ((entry = zis.getNextEntry()) != null) {
            String path = ApplicationSettings.getReviewsDirectory() + File.separator + entry.getName();
            File unzipFile = new File(path);
            if (fileId.equals("")) {
                unzipFile.getParentFile().mkdirs();
                fileId = unzipFile.getParentFile().getName();
            }
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
        return fileId;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(open)) {
            MCR_.intent(this).fileToOpen(fileId).start();
        }
        finish();
    }
}
