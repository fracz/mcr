package pl.fracz.mcr;

import java.util.regex.Pattern;

import android.webkit.JavascriptInterface;

public class MCRJavascriptInterface {
	MCR mContext;

	public MCRJavascriptInterface(MCR c) {
		mContext = c;
	}

	@JavascriptInterface
	public void setHighlihtedSource(String source) {
		source = source.replaceAll("<span class=\"typ\">(.+?)</span>", "<font color=\"#87cefa\">$1</font>");
		mContext.displayHighlighted(source);
	}
}
