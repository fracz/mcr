package pl.fracz.mcr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import pl.fracz.mcr.util.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EActivity
public class FileChooser extends SherlockListActivity {

    public static final int OPEN_OK = 2;
    public static final int OPEN_FAIL = 1;
    public static final String OPENED_FILE_EXTRA_KEY = "FILE";

    @StringArrayRes
    String[] allowedExtensions;

    private Collection<String> ALLOWED_EXTENSIONS;

    @InstanceState
    File currentPath = Environment.getExternalStorageDirectory();

    ListAdapter currentFileList;

    private final FilenameFilter FILTER = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String filename) {
            File file = new File(dir, filename);
            if (file.isHidden()) return false;
            if (file.isDirectory()) return true;
            if (file.isFile()) return ALLOWED_EXTENSIONS.contains(FileUtils.getExtension(filename));
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALLOWED_EXTENSIONS = Arrays.asList(allowedExtensions);
        fillListWithFiles();
    }

    protected void fillListWithFiles() {
        String[] files = currentPath.list(FILTER);
        List<Item> items = new ArrayList<FileChooser.Item>(files.length);
        if (!isRootDir())
            items.add(new Item(currentPath, ".."));
        for (String file : files)
            items.add(new Item(currentPath, file));
        currentFileList = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(currentFileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Item clicked = (Item) currentFileList.getItem(position);
        if (clicked.isDir())
            handleDirectoryClick(clicked);
        else
            handleFileClick(clicked);
    }

    private void handleDirectoryClick(Item clicked) {
        currentPath = clicked.getFile();
        fillListWithFiles();
    }

    private void handleFileClick(Item clicked) {
        Intent data = new Intent();
        int result = OPEN_FAIL;
        try {
            data.putExtra(OPENED_FILE_EXTRA_KEY, clicked.getFile());
            result = OPEN_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(result, data);
        finish();
    }

    private boolean isRootDir() {
        return !new Item(currentPath, "..").getFile().exists();
    }

    private static class Item {

        private final File path;

        Item(File dir, String file) {
            this.path = new File(dir.getAbsolutePath() + "/" + file);
        }

        boolean isDir() {
            return path.isDirectory();
        }

        File getFile() {
            return path;
        }

        @Override
        public String toString() {
            return path.getName();
        }
    }
}
