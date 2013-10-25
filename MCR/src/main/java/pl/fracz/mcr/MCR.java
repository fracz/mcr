package pl.fracz.mcr;

import android.content.Intent;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MCR extends SherlockActivity {

	static String contents = null;

	@ViewById
	TextView hello;

	@Override
	protected void onStart() {
		super.onStart();
		if (contents != null) {
			hello.setText(contents);
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.open_file:
			startActivity(new Intent(this, FileExplore.class));
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
