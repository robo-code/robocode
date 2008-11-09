/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.util;


import java.io.Writer;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;


/**
 * @author Pavel Savara (original)
 */

public class XmlWriter {
	Writer writer;
	Stack<String> elements = new Stack<String>();
	boolean headClosed = true;
	boolean indent = true;

	public XmlWriter(Writer writer) {
		this.writer = writer;
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
	}

	public void writeAttribute(String name, String value) throws IOException {
		writer.write(' ');
		writer.write(encode(name));
		writer.write("=\"");
		writer.write(encode(value));
		writer.write('"');
	}

	public void writeAttribute(String name, long value) throws IOException {
		writeAttribute(name, Long.toString(value));
	}

	public void endElement() throws IOException {
		closeHead();
		String name = elements.pop();

		indent(elements.size());
		writer.write("</");
		writer.write(encode(name));
		writer.write(">\n");
	}

	private void closeHead() throws IOException {
		if (!headClosed) {
			writer.write('>');
			writer.write('\n');
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

	private String encode(String text) {
		// TODO encode special chracters
		return text;
	}

	public static void serialize(Writer writer, XmlSerializable tree) throws IOException {
		XmlWriter xw = new XmlWriter(writer);

		xw.startDocument();
		tree.writeXml(xw);
		writer.flush();
	}
}
