package pl.fracz.mcr.syntax;

import android.text.Spanned;

/**
 * Type definition for all services that are capable of highlighting code
 * syntax.
 */
public interface SyntaxHighlighter {
	/**
	 * Generates a {@code <font color="...">...</font>} html highlighted item
	 * from the given source code.
	 * 
	 * @param sourceCode
	 *            source code to highlight
	 * @return highlighted source code
	 */
	Spanned highlight(String sourceCode);
}
