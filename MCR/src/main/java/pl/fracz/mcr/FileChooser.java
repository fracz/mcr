package pl.fracz.mcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.InstanceState;

@EActivity
public class FileChooser extends SherlockListActivity {

	public static final int OPEN_OK = 2;
	public static final int OPEN_FAIL = 1;

	@InstanceState
	File currentPath = Environment.getExternalStorageDirectory();

	ListAdapter currentFileList;

	private static final FilenameFilter FILTER = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String filename) {
			File file = new File(dir, filename);
			return (file.isDirectory() || file.isFile()) && !file.isHidden();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			String contents = convertStreamToString(new FileInputStream(clicked.getFile()));
			data.setData(Uri.parse(contents));
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

	private String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
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
