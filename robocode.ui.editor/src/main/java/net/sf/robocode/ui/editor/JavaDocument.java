package net.sf.robocode.ui.editor;


import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.*;


// FIXME: 'package mypackage'.. "package" in mypackage is higlighted!

// TODO: Make it configurable to extend the Java keywords.
// TODO: Highlight methods from Robocode API?
// TODO: Highlight numbers?
// TODO: Method names and method invocations in bold?
// TODO: Trim trailing white-spaces from all lines?

/**
 * Represents a styled Java document used for syntax high-lightning.
 * 
 * @author Flemming N. Larsen
 */
@SuppressWarnings("serial")
public class JavaDocument extends DefaultStyledDocument {

	/** The text pane this document is used with necessary for setting the caret position when auto indenting */
	private final JTextPane textPane;

	// Indentation //

	/** Flag specifying if auto indentation is enabled */
	private boolean isAutoIndentEnabled = true;

	/** Flag defining if spaces must be used for indentation; otherwise tabulation characters are being used */
	private boolean useSpacesForIndentation = false;

	/** Tab size (column width) */
	private int tabSize = 4; // Default is every 4th column

	/** Java language delimiter characters represented in a string */
	private static final String DELIMITERS = ";:{}()[]+-/%<=>!&|^~*";

	/** Java language quote delimiter characters represented in a string */
	private static final String QUOTE_DELIMITERS = "\"'";

	/** Java language keywords */
	private static final Set<String> KEYWORDS = new HashSet<String>(
			Arrays.asList(
					new String[] {
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
		"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto",
		"if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
		"protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
		"throw", "throws", "transient", "try", "void", "volatile", "while" }));

	/** Normal text attribute set */
	private final MutableAttributeSet normalAttrSet;

	/** Quoted text attribute set */
	private final MutableAttributeSet quoteAttrSet;

	/** Keyword attribute set */
	private final MutableAttributeSet keywordAttrSet;

	/** Annotation attribute set */
	private final MutableAttributeSet annotationAttrSet;

	/** Comment attribute set */
	private final MutableAttributeSet commentAttrSet;

	/** String buffer holding only space characters for fast replacement of tabulator characters */
	private String spaceBuffer;

	/**
	 * Constructor that creates a Java document.
	 * 
	 * @param textPane
	 *            is the text pane that this Java documents must apply to.
	 */
	public JavaDocument(JTextPane textPane) {
		super();
		this.textPane = textPane;

		// Setup the styled attribute sets

		normalAttrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(normalAttrSet, new Color(0x00, 0x00, 0x00, 0xFF));

		quoteAttrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(quoteAttrSet, new Color(0x7F, 0x00, 0x00, 0xFF));

		keywordAttrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(keywordAttrSet, new Color(0x00, 0x00, 0xAF, 0xFF));
		StyleConstants.setBold(keywordAttrSet, true);

		annotationAttrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(annotationAttrSet, new Color(0x7F, 0x7F, 0x7F, 0xFF));

		commentAttrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(commentAttrSet, new Color(0x00, 0xAF, 0x00, 0xFF));
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		// Process auto indentation on inserted string
		Indentation indent = applyAutoIndentation(offset, str);

		// Replace indentation tabulation characters with spaces if spaces must be used instead of tabulation characters
		str = replaceTabulatorCharacters(getElementFromOffset(offset).getEndOffset(), indent.text);

		// Insert the modified string using the original method for inserting string into this document
		super.insertString(offset, str, a);

		// Set the new caret position on the text pane if the caret position has been set
		if (indent.caretPos >= 0) {
			textPane.setCaretPosition(indent.caretPos);
		}
		// Apply syntax highlighting from the current offset
		applySyntaxHighlighting(offset);
	}

	@Override
	public void remove(int offset, int length) throws BadLocationException {
		// Call the original method
		super.remove(offset, length);

		// Apply syntax highlighting from the current offset
		applySyntaxHighlighting(offset);
	}

	/**
	 * Sets the tabulation column size.
	 * 
	 * @param tabSize
	 *            is the new tabulation column size, which must be >= 1.
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	/**
	 * Returns the element index from an offset.
	 * 
	 * @param offset
	 *            is the offset to retrieve the element index from.
	 * @return the element index >= 0.
	 */
	private int getElementIndex(int offset) {
		return getDefaultRootElement().getElementIndex(offset);
	}

	/**
	 * Returns the child element at the given element index.
	 * 
	 * @param index
	 *            is the element index.
	 * @return the child element.
	 */
	private Element getElement(int index) {
		return getDefaultRootElement().getElement(index);
	}

	/**
	 * Returns the child element at the given document offset.
	 * 
	 * @param offset
	 *            is the document offset.
	 * @return the child element.
	 */
	private Element getElementFromOffset(int offset) {
		return getElement(getElementIndex(offset));
	}

	/**
	 * Applies indentation for an inserted string at a given offset if auto indentation is enabled.
	 * 
	 * @param offset
	 *            is the offset of the inserted string.
	 * @param str
	 *            is the inserted string.
	 * @return an Indentation container with indentation details.
	 * @throws BadLocationException
	 */
	private Indentation applyAutoIndentation(int offset, String str) throws BadLocationException {
		// Prepare indentation container
		Indentation indentation = new Indentation();

		indentation.caretPos = -1; // Meaning that caret position is not changed
		indentation.text = str;

		// Apply auto indentation if it is enabled, and the new line character has been entered
		if (isAutoIndentEnabled && str.equals("\n")) {

			// Save the current indentation for later, as it might change from here on
			int currentIndentation = getIndentationLengthFromOffset(offset);

			// Read the line content from the offset
			String line = getLineFromOffset(offset);

			// Calculate the end offset of the line
			int lineEndOffset = getElementFromOffset(offset).getStartOffset() + line.length();

			// Continue if the line has content and the offset is after the line end offset
			if (line.length() > 0 && offset >= lineEndOffset) {

				// Check/compose and return a body start indentation a '{' is found
				if (composeBodyStartIndentation(offset, line, indentation)) {
					return indentation;
				}
				// Check/compose and return a multiline comment start indentation if a '/*' is found
				if (composeMultilineCommentStartIndentation(offset, line, indentation)) {
					return indentation;
				}
				// Check/compose and return a continued multiline comment if a '*' is found inside a multiline comment
				if (composeMultilineCommentContinuedIndentation(offset, line, indentation)) {
					return indentation;
				}
				// Check/compose and return a multiline comment end indentation if a '*/' is found
				if (composeMultilineCommentEndIndentation(offset, line, indentation)) {
					return indentation;
				}
				// Extend the current indentation text based on the current indentation
				// Note: Will replace tabulator characters with spaces etc. if this must be done.
				indentation.text += getStartIndentation(currentIndentation);
			}
		}
		return indentation;
	}

	/**
	 * Returns a string where tabulator characters in the given string is replaced with spaces, but only if these must
	 * be replaced with spaces by configuration.
	 * 
	 * @param startIndex
	 *            is the start index of the string in a line used for determine the current tabulator column.
	 * @param str
	 *            is the string containing tabulator characters that might need to be replaced with spaces.
	 * @return a string where tabulator characters might have been replaced with spaces.
	 */
	private String replaceTabulatorCharacters(int startIndex, String str) {
		// Check if tabulator characters needs to be replaced with space characters
		if (useSpacesForIndentation) {
			// Prepare string buffer containing replaced text
			StringBuilder sb = new StringBuilder();

			int tabIndex; // Index of current tabulator character

			// Run loop as long as we find a tabulator character from the start index
			while ((tabIndex = str.indexOf('\t', startIndex)) >= 0) {
				// Put the current text (non tabulator characters) into the string buffer
				sb.append(str.substring(startIndex, tabIndex));

				// Calculate the number of spaces that remain before the next tabulator column
				int numSpaces = tabSize - (sb.length() % tabSize);

				// Run loop while the follower character is a tabulator character
				while (++tabIndex < str.length() && str.charAt(tabIndex) == '\t') {
					// Increment the number of spaces that to use to replace tabulator characters with the tabulator
					// column size.
					numSpaces += tabSize;
				}
				// Append the calculated number of space characters to replace the tabulator characters
				sb.append(getSpaces(numSpaces));

				// The new start index is the current tabulator index
				startIndex = tabIndex;
			}
			// Append the text that remain to the string buffer from the current start index
			sb.append(str.substring(startIndex));

			// Set the result to the build string from the string buffer
			str = sb.toString();
		}
		// Return the resulting string
		return str;
	}

	/**
	 * Returns a string containing a specific number of spaces only.
	 * 
	 * @param count
	 *            is the number of spaces the the string should contain.
	 * @return a string containing only the given number of space characters.
	 */
	private String getSpaces(int count) {
		if (count == 0) {
			return "";
		}
		// Determine the current buffer size
		int bufferSize = (spaceBuffer == null) ? 0 : spaceBuffer.length();

		// Check if we need to reallocate the buffer to accommodate the given number of spaces
		if (count > bufferSize) {
			// Determine the new buffer size, which is set to twice the number of spaces, but minimum 100 characters
			bufferSize = Math.max(2 * count, 100);
			// Create a new string containing spaces with the new buffer size
			char[] chars = new char[bufferSize];

			Arrays.fill(chars, ' ');
			spaceBuffer = new String(chars);
		}
		// Return a string containing the given number of spaces
		return spaceBuffer.substring(0, count);
	}

	/**
	 * Composes a body start indentation block starting with a '{' and ending with a '}' and update the caret position.
	 * 
	 * @param offset
	 *            is the offset of the inserted string.
	 * @param line
	 *            is the line, where the indentation block must be appended to.
	 * @param indentation
	 *            is containing the current indentation date.
	 * @return true if a body start indentation block should be inserted after the given line; false otherwise.
	 * @throws BadLocationException
	 */
	private boolean composeBodyStartIndentation(int offset, String line, Indentation indentation)
		throws BadLocationException {
		// Check if the given line ends with a body start character, i.e. '{'
		if (line.endsWith("{")) {
			// Calculated current start indentation length from the given offset
			int startIndentLen = getIndentationLengthFromOffset(offset);
			// Calculate the start indentation string
			String startIndent = getStartIndentation(startIndentLen);
			// Prapare buffer for containing the indentation block
			StringBuilder sb = new StringBuilder("\n");

			// Append new indented line to the buffer, that is indented based on the start indentation
			sb.append(getStartIndentation(startIndentLen + tabSize)).append('\n');
			// Update the caret position to be placed in the end of the new indented line
			indentation.caretPos = offset + sb.toString().length() - 1;
			// Append the body end character on a new line
			sb.append(startIndent).append('}');
			// Set the indentation block text to the string containing from the buffer
			indentation.text = sb.toString();
			// Indentation block was created
			return true;
		}
		// Indentation block was not created
		return false;
	}

	/**
	 * Composes a multiline comment start indentation block.
	 * 
	 * @param offset
	 *            is the offset of the inserted string.
	 * @param line
	 *            is the line, where the indentation block must be appended to.
	 * @param indentation
	 *            is containing the current indentation date.
	 * @return true if a multiline comment start indentation block should be inserted after the given line; false
	 *         otherwise.
	 * @throws BadLocationException
	 */
	private boolean composeMultilineCommentStartIndentation(int offset, String line, Indentation indentation)
		throws BadLocationException {

		// Check if the given line contains a multiline start string, i.e. '/*'
		if (line.trim().startsWith("/*")) {
			// Calculated current start indentation length from the given offset
			int startIndentLen = getIndentationLengthFromOffset(offset);
			// Calculate the start indentation string
			String startIndent = getStartIndentation(startIndentLen);
			// Prepare buffer for containing the indentation block
			StringBuilder sb = new StringBuilder("\n");

			// Append new indented multiline comment line to the buffer, that is indented based on the start indentation
			sb.append(startIndent).append(" * \n");
			// Update the caret position to be placed in the end of the new indented line
			indentation.caretPos = offset + sb.toString().length() - 1;
			// Append the multiline comment end character on a new line
			sb.append(startIndent).append(" */");
			// Set the indentation block text to the string containing from the buffer
			indentation.text = sb.toString();
			// Indentation block was created
			return true;
		}
		// Indentation block was not created
		return false;
	}

	/**
	 * Composes a continued multiline comment indentation block.
	 * 
	 * @param offset
	 *            is the offset of the inserted string.
	 * @param line
	 *            is the line, where the indentation block must be appended to.
	 * @param indentation
	 *            is containing the current indentation date.
	 * @return true if a continued multiline comment indentation block should be inserted after the given line; false
	 *         otherwise.
	 * @throws BadLocationException
	 */
	private boolean composeMultilineCommentContinuedIndentation(int offset, String line, Indentation indentation)
		throws BadLocationException {

		// Check if the given line contains a '*' and is located inside of a multiline comment
		if (line.trim().startsWith("*") && isInMultilineComment(offset)) {
			// Calculated current start indentation length from the given offset
			int startIndentLen = getIndentationLengthFromOffset(offset);
			// Calculate the start indentation string
			String startIndent = getStartIndentation(startIndentLen);
			// Prepare buffer for containing the indentation block
			StringBuilder sb = new StringBuilder("\n");

			// Append new indented multiline comment line to the buffer, that is indented based on the start indentation
			sb.append(startIndent).append("* ");
			// Update the caret position to be placed in the end of the new indented line
			indentation.caretPos = offset + sb.toString().length();
			// Set the indentation block text to the string containing from the buffer
			indentation.text = sb.toString();
			// Indentation block was created
			return true;
		}
		// Indentation block was not created
		return false;
	}

	/**
	 * Composes a multiline end start indentation block.
	 * 
	 * @param offset
	 *            is the offset of the inserted string.
	 * @param line
	 *            is the line, where the indentation block must be appended to.
	 * @param indentation
	 *            is containing the current indentation date.
	 * @return true if a multiline comment end indentation block should be inserted after the given line; false
	 *         otherwise.
	 * @throws BadLocationException
	 */
	private boolean composeMultilineCommentEndIndentation(int offset, String line, Indentation indentation)
		throws BadLocationException {

		// Check if the given line contains a multiline end string, i.e. '*/'
		if (line.trim().endsWith("*/")) {
			// Get the line index from the current offset
			int lineIndex = getElementIndex(offset);

			// Run loop as long as the line index is still positive (till the start of the document text)
			while (lineIndex >= 0) {
				// Check if the current line line is not in a multiline comment
				int lineOffset = getElement(lineIndex).getStartOffset();

				if (!isInMultilineComment(lineOffset)) {
					// Append indentation that matches the start offset of the current line
					indentation.text += getStartIndentation(getIndentationLengthFromOffset(lineOffset));
					break;
				}
				// Move to previous line
				lineIndex--;
			}
			// Indentation block was created
			return true;
		}
		// Indentation block was not created
		return false;
	}

	/**
	 * Returns line from the specified offset where trailing white spaces are removed. Note: The white spaces in the
	 * beginning of the line is not removed as these serve to determine start indentation for inserted lines following
	 * this line.
	 * 
	 * @param offset
	 *            is the offset to retrieve the line from.
	 * @return the line at the given offset where trailing white spaces have been trimmed.
	 * @throws BadLocationException
	 */
	private String getLineFromOffset(int offset) throws BadLocationException {
		// Get the start and end index of the line
		Element element = getElementFromOffset(offset);
		int start = element.getStartOffset();
		int end = element.getEndOffset();

		// Get the line content
		String origLine = getText(start, end - start - 1);
		// Trim trailing white spaces
		String trimmedLine = origLine.replaceAll("\\s*$", "");

		// Return the trimmed line, but only if it did not contain only white spaces as we need the white spaces for
		// indentation for lines inserted after this line.
		return trimmedLine.length() > 0 ? trimmedLine : origLine;
	}

	/**
	 * Calculates and returns the indentation length (number of space characters) of the line at the given offset.
	 * 
	 * @param offset
	 *            is the document offset of the line.
	 * @return the calculated indentation length of the line at the given offset.
	 * @throws BadLocationException
	 */
	private int getIndentationLengthFromOffset(int offset) throws BadLocationException {
		// Gets the start offset of the line at the given offset
		int startOffset = getElementFromOffset(offset).getStartOffset();

		// Run loop while the next character is a space of tabulator character.
		int length = 0;

		for (;;) {
			// Get the next character
			char ch = getText(startOffset++, 1).charAt(0);

			if (ch == ' ') {
				// Increment the length by 1 if the character is a space
				length++;
			} else if (ch == '\t') {
				// Increment the length by the number of spaces the remain in order to reach the next tabulator column,
				// if the character is a tabulator character.
				length = tabSize * (length / tabSize + 1);
			} else {
				// Otherwise, stop looping
				break;
			}
		}
		// Return the calculated indentation length
		return length;
	}

	/**
	 * Returns a start indentation string of the given length.
	 * 
	 * @param length
	 *            is the length of the indentation measured in number of spaces.
	 * @return a string to apply to the start of a line in order to indent the line.
	 */
	private String getStartIndentation(int length) {
		if (length == 0) {
			return "";
		}
		
		// Prepare string buffer for containing the indentation string
		StringBuilder sb = new StringBuilder();

		// Check if tabulator characters must be used for indentation
		if (!useSpacesForIndentation) {
			// Append as many tabulator characters to the indentation string that fits into the given length
			for (int i = length / tabSize; i > 0; i--) {
				sb.append('\t');
			}
			// Set the length to the spaces remaining to reach the input length
			length %= tabSize;
		}
		// Apply spaces to the string buffer (might be the spaces that remain to fit the given length)
		sb.append(getSpaces(length));
		// Return the indentation string contained in the string buffer
		return sb.toString();
	}

	/**
	 * Apply syntax highlighting to the document from the given document offset.
	 * 
	 * @param offset
	 *            is the offset from where to apply the syntax highligthing.
	 * @throws BadLocationException
	 */
	private void applySyntaxHighlighting(int offset) throws BadLocationException {
		// Get start and end line
		int startLine = getElementIndex(offset);
		int endLine = getElementIndex(getLength());

		// Process each changed line one by one from the start line to the end line
		for (int line = startLine; line <= endLine; line++) {
			processChangedLine(line);
		}
	}

	/**
	 * Process changed line by applying syntax highlighting on the entire line at the given index.
	 * 
	 * @param lineIndex
	 *            is the index of the line.
	 * @throws BadLocationException
	 */
	private void processChangedLine(int lineIndex) throws BadLocationException {
		// Process the syntax tokens on the given line
		Element element = getElement(lineIndex);

		processLineTokens(element.getStartOffset(), element.getEndOffset() - 1);
	}

	/**
	 * Process the syntax tokens contained in a line.
	 * 
	 * @param startOffset
	 *            is the document start offset of the line to process.
	 * @param endOffset
	 *            is the document end offset of the line to process.
	 * @throws BadLocationException
	 */
	private void processLineTokens(int startOffset, int endOffset) throws BadLocationException {
		// Calculate the length of the line based on the given start and end offset
		int len = endOffset - startOffset;

		// Reset to normal text style for the processed line
		setCharacterAttributes(startOffset, len, normalAttrSet, true);

		// Process tokens one by one
		String textFragment = getText(startOffset, len);
		int index = 0;

		while (index < len) {
			index += processToken(textFragment.substring(index), startOffset + index);
		}
	}

	/**
	 * Process the syntax token contained in a text fragment.
	 * 
	 * @param textFragment
	 *            is the text fragment.
	 * @param startOffset
	 *            is the start offset of the text fragment to process.
	 * @return the number of processed characters.
	 * @throws BadLocationException
	 */
	private int processToken(final String textFragment, final int startOffset) throws BadLocationException {
		// Process quote token if the first character in the text fragment is a quote delimiter
		if (isQuoteDelimiter(textFragment.charAt(0))) {
			return processQuoteToken(textFragment, startOffset);
		}
		int len;

		// Check if the token is a single line comment
		// Note: Single line comment has higher precedence than a multiline comment.
		if (textFragment.startsWith("//")) {
			len = textFragment.length();
			setCharacterAttributes(startOffset, len, commentAttrSet, false);
			return len;
		}
		// Check if the token is a multiline comment
		if (textFragment.startsWith("/*")) {
			return processStartMultilineCommentToken(textFragment, startOffset);
		}
		// Check if the token is in the middle of a multiline comment
		if (isInMultilineComment(startOffset)) {
			// Check if the token contains the end mark of the multiline comment
			int endCommentIndex = textFragment.indexOf("*/");

			if (endCommentIndex >= 0) {
				len = endCommentIndex + 2; // Limit length to the end mark
			} else {
				len = textFragment.length(); // Use the whole token
			}
			setCharacterAttributes(startOffset, len, commentAttrSet, false);
			return len;
		}

		// Skip delimiter characters
		len = 1;
		while (len < textFragment.length()) {
			if (isDelimiter(textFragment.charAt(len))) {
				break;
			}
			len++;
		}

		// Limit token to current length, e.g. if it contained delimiter characters
		String token = textFragment.substring(0, len);

		// Check if the token is a keyword or an annotation
		if (startOffset == 0 || isDelimiter(getText(startOffset - 1, 1).charAt(0))) {
			if (isKeyword(token)) {
				setCharacterAttributes(startOffset, len, keywordAttrSet, false);
			} else if (isAnnotation(token)) {
				setCharacterAttributes(startOffset, len, annotationAttrSet, false);
			} else {
				len = 1;
			}
		} else {
			len = 1;
		}
		// Return the number of processed characters
		return len;
	}

	/**
	 * Process a quote token.
	 * 
	 * @param textFragment
	 *            is the text fragment.
	 * @param startOffset
	 *            is the start offset of the text fragment to process.
	 * @return the number of processed characters.
	 * @throws BadLocationException
	 */
	private int processQuoteToken(final String textFragment, final int startOffset) {
		char quoteDelimiter = textFragment.charAt(0);

		// Find end quote if it exists. Ignore escaped quotes
		int indexQuote = 1;
		int quoteEndIndex = -1;

		for (;;) {
			indexQuote = textFragment.indexOf(quoteDelimiter, indexQuote);
			if (indexQuote <= 0 || indexQuote > textFragment.length()) {
				break;
			}
			if (textFragment.charAt(indexQuote - 1) != '\\') {
				quoteEndIndex = indexQuote;
				break;
			}
			indexQuote++;
		}

		// Set length of token, if end quote was found
		int len = quoteEndIndex >= 0 ? quoteEndIndex + 1 : textFragment.length();

		// Set quote attribute set
		setCharacterAttributes(startOffset, len, quoteAttrSet, false);

		return len;
	}

	/**
	 * Process a multiline comment token.
	 * 
	 * @param textFragment
	 *            is the text fragment.
	 * @param startOffset
	 *            is the start offset of the text fragment to process.
	 * @return the number of processed characters.
	 * @throws BadLocationException
	 */
	private int processStartMultilineCommentToken(final String textFragment, final int startOffset) {
		int endIndex = textFragment.indexOf("*/", 1);
		int len = endIndex >= 0 ? endIndex + 2 : textFragment.length();

		setCharacterAttributes(startOffset, len, commentAttrSet, false);

		return len;
	}

	/**
	 * Checks if the current text offset is within a multiline comment.
	 * 
	 * @param offset
	 *            is the text offset.
	 * @return true if the given offset is within a multiline comment; false otherwise.
	 * @throws BadLocationException
	 */
	private boolean isInMultilineComment(final int offset) throws BadLocationException {
		return isInMultilineComment(getElementIndex(offset), offset);
	}

	/**
	 * Checks if the current text offset is within a multiline comment.
	 * 
	 * @oaram lineIndex is the element index of a line.
	 * @param offset
	 *            is the text offset.
	 * @return true if the given line element index and offset is within a multiline comment; false otherwise.
	 * @throws BadLocationException
	 */
	private boolean isInMultilineComment(final int lineIndex, final int offset) throws BadLocationException {
		int startOffset = getElement(lineIndex).getStartOffset();
		String lineText = getLineFromOffset(startOffset);
		int len = Math.min((offset - startOffset), lineText.length());

		lineText = lineText.substring(0, len);

		int commentStart = lineText.lastIndexOf("/*");
		int commentEnd = lineText.lastIndexOf("*/");

		if (commentStart >= 0) {
			if (commentEnd >= 0) {
				if (commentEnd > commentStart) {
					return (len < commentEnd);
				}
			} else {
				return true;
			}
		}
		if (commentEnd >= 0) {
			return false;
		}

		int prevLine = lineIndex - 1;

		return (prevLine >= 0) ? isInMultilineComment(prevLine, offset) : false;
	}

	/**
	 * Checks if a character is a delimiter character.
	 * 
	 * @param ch
	 *            is the character.
	 * @return true if the given character is a delimiter character; false otherwise.
	 */
	private static boolean isDelimiter(char ch) {
		return (Character.isWhitespace(ch) || DELIMITERS.indexOf(ch) >= 0);
	}

	/**
	 * Checks if a character is a quote delimiter character.
	 * 
	 * @param ch
	 *            is the character.
	 * @return true if the given character is a quote delimiter character; false otherwise.
	 */
	private static boolean isQuoteDelimiter(char ch) {
		return QUOTE_DELIMITERS.indexOf(ch) >= 0;
	}

	/**
	 * Checks if a token is a keyword.
	 * 
	 * @param token
	 *            is the token.
	 * @return true if the given token is a keyword; false otherwise.
	 */
	private static boolean isKeyword(String token) {
		return KEYWORDS.contains(token);
	}

	/**
	 * Checks if a token is an annotation.
	 * 
	 * @param token
	 *            is the token.
	 * @return true if the given token is an annotation; false otherwise.
	 */
	private static boolean isAnnotation(String token) {
		return token.matches("@\\w.*");
	}

	/**
	 * Class containing data for indentation.
	 */
	private class Indentation {
		// Is the new caret position after the indentation has been made. -1 means no change.
		int caretPos;
		// Is the indentation text
		String text;
	}
}
