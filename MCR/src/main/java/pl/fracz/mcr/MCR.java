package pl.fracz.mcr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MCR extends SherlockActivity {

	private static final int OPEN_FILE = 1;

	private static final String[] STYLES = new String[] { "source.css", "google-code-prettify/prettify.css" };

	private static final String[] SCRIPTS = new String[] { "source.js", "google-code-prettify/prettify.js" };

	private WebView webView;

	@ViewById
	TextView source;

	@InstanceState
	String sourceCode = "Kasia kasia = new Kasia();";

	@OptionsItem
	void openFileSelected() {
		startActivityForResult(new Intent(this, FileChooser_.class), OPEN_FILE);
	}

	@AfterViews
	@SuppressLint("SetJavaScriptEnabled")
	void initializeSourceComponent() {
		webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.addJavascriptInterface(new MCRJavascriptInterface(this), "MCR");
		// displayHighlighted("DUP");
		displaySource();
	}

	@OnActivityResult(OPEN_FILE)
	void handleOpenFile(int resultCode, Intent data) {
		if (resultCode == FileChooser.OPEN_OK) {
			sourceCode = data.getDataString();
			displaySource();
		}
	}

	@Click
	void source() {
		source.setText(Html.fromHtml(sourceCode));
	}

	void displayHighlighted(String text) {
		sourceCode = text;
	}

	private void displaySource() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head>");
		for (String style : STYLES) {
			sb.append(String.format("<link href=\"%s\" rel=\"stylesheet\" type=\"text/css\" />", style));
		}
		for (String script : SCRIPTS) {
			sb.append(String.format("<script src=\"%s\" type=\"text/javascript\"></script>", script));
		}
		sb.append("</head><body onload='initializeSource()'>");
		sb.append(String.format("<pre id='source' class='prettyprint'>%s</pre>", sourceCode));
		sb.append("</body></html>");
		webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "UTF-8", null);
	}

}
