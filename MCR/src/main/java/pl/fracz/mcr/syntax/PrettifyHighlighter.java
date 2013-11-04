package pl.fracz.mcr.syntax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;

import com.googlecode.androidannotations.annotations.EBean;

/**
 * Code highlighter that uses the Google Prettify javascript library to tokenize
 * the source code, and then highlight it with appropriate colors.
 */
@EBean
public class PrettifyHighlighter implements SyntaxHighlighter {

	private static final String[] STYLES = new String[] { "google-code-prettify/prettify.css" };

	private static final String[] SCRIPTS = new String[] { "PrettifyHighlighter.js", "google-code-prettify/prettify.js" };

	private static final Map<String, String> COLORS = buildColorsMap();

	private final WebView webView;

	private String spannedSource;

	private CountDownLatch lock;

	public PrettifyHighlighter(Context context) {
		this.webView = new WebView(context);
		initializeWebView();
	}

	@Override
	public synchronized Spanned highlight(String sourceCode) {
		spannedSource = sourceCode;
		String content = buildWebViewContent(sourceCode);
		try {
			fetchSpannedSource(content);
			colorize();
			return Html.fromHtml(spannedSource);
		} finally {
			spannedSource = null;
		}
	}

	private void colorize() {
		for (Map.Entry<String, String> color : COLORS.entrySet()) {
			String search = String.format("<span class=\"%s\">(.+?)</span>", color.getKey());
			String replace = String.format("<font color=\"#%s\">$1</font>", color.getValue());
			spannedSource = spannedSource.replaceAll(search, replace);
		}
		spannedSource = spannedSource.replaceAll("\n", "<br>");
	}

	private String buildWebViewContent(String sourceCode) {
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
		return sb.toString();
	}

	private void fetchSpannedSource(String webViewContent) {
		lock = new CountDownLatch(1);
		webView.loadDataWithBaseURL("file:///android_asset/", webViewContent, "text/html", "UTF-8", null);
		try {
			lock.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initializeWebView() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new HighlihtedSourceGrabber(), "PrettifyHighlighter");
	}

	private class HighlihtedSourceGrabber {
		// method is called from JavaScript
		@SuppressWarnings("unused")
		public void setSpannedSource(String spanned) {
			PrettifyHighlighter.this.spannedSource = spanned;
			lock.countDown();
		}
	}

	private static Map<String, String> buildColorsMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("typ", "87cefa");
		map.put("kwd", "00ff00");
		map.put("lit", "ffff00");
		map.put("com", "999999");
		map.put("str", "ff4500");
		map.put("pun", "eeeeee");
		map.put("pln", "ffffff");
		return map;
	}
}
