package pl.fracz.mcr;

import android.content.Intent;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockActivity {

	private static final int OPEN_FILE = 1;

	static String contents = null;

	@ViewById
	TextView hello;

	@OptionsItem
	void openFileSelected(){
		startActivityForResult(new Intent(this, FileExplore.class), OPEN_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case OPEN_FILE:
			hello.setText(data.getDataString());
			break;
		}
	}
}
