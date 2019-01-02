/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;


import java.io.IOException;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.StringCharacterIterator;
import java.util.Locale;
import java.util.Stack;


/**
 * @author Pavel Savara (original)
 */
public class XmlWriter {
	private static final DecimalFormat decimalFormat = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
	private final Writer writer;
	private final Stack<String> elements = new Stack<String>();
	private boolean headClosed = true;
	private boolean innerElement = false;
	private boolean indent = true;

	public XmlWriter(Writer writer, boolean indent) {
		this.writer = writer;
		this.indent = indent;
	}

	public void startDocument() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	}

	public void startElement(String name) throws IOException {
		closeHead();
		indent(elements.size());
		elements.push(name);
		writer.write('<');
		writer.write(encode(name));
		headClosed = false;
		innerElement = false;
	}

	public void writeAttribute(String name, String value) throws IOException {
		if (value != null) {
			writer.write(' ');
			writer.write(encode(name));
			writer.write("=\"");
			writer.write(encode(value));
			writer.write('"');
		}
	}

	public void writeAttribute(String name, boolean value) throws IOException {
		writeAttribute(name, Boolean.toString(value));
	}

	public void writeAttribute(String name, long value) throws IOException {
		writeAttribute(name, Long.toString(value));
	}

	public void writeAttribute(String name, double value, boolean trim) throws IOException {
		if (trim) {
			writeAttribute(name, decimalFormat.format(value));
		} else {
			writeAttribute(name, Double.toString(value));
		}
	}

	public void endElement() throws IOException {
		String name = elements.pop();

		if (innerElement || headClosed) {
			closeHead();
			indent(elements.size());
			writer.write("</");
			writer.write(encode(name));
			writer.write(">");
		} else {
			writer.write("/>");
			headClosed = true;
		}
		newline();
		innerElement = true;
	}

	private void newline() throws IOException {
		if (indent) {
			writer.write("\n");
		}
	}

	private void closeHead() throws IOException {
		if (!headClosed) {
			writer.write('>');
			newline();
			headClosed = true;
		}
	}

	private void indent(int level) throws IOException {
		if (indent) {
			for (int i = 0; i < level; i++) {
				writer.write("\t");
			}
		}
	}

	private static String encode(String text) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();

		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '&') {
				result.append("&amp;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\n') {
				result.append("&#xA;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

}
