package pl.fracz.mcr.syntax;


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
     *            @param language source language
     * @return highlighted source code
	 */
    String highlight(String sourceCode, String language);
}
