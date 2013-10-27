package pl.fracz.mcr;

import android.content.Intent;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockActivity {

	private static final int OPEN_FILE = 1;

	@ViewById
	TextView hello;

	@OptionsItem
	void openFileSelected(){
		startActivityForResult(new Intent(this, FileExplore.class), OPEN_FILE);
	}

	@OnActivityResult(OPEN_FILE)
	void handleOpenFile(int resultCode, Intent data){
		hello.setText(data.getDataString());
	}
}
