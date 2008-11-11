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


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;


/**
 * @author Pavel Savara (original)
 */
public class XmlReader {

	SAXParser parser;

	InputStream input;
	Stack<Element> elements = new Stack<Element>();
	Stack<Dictionary<String, Element>> elementNames = new Stack<Dictionary<String, Element>>();
	Stack<Dictionary<String, Attribute>> attributeNames = new Stack<Dictionary<String, Attribute>>();
	XmlSerializable result;

	public XmlReader(InputStream input) throws SAXException, ParserConfigurationException {
		this.input = input;
		SAXParserFactory factory = SAXParserFactory.newInstance();

		parser = factory.newSAXParser();
	}

	private Object deserialize(XmlSerializable prototype) throws IOException, SAXException {
		elementNames.push(new Hashtable<String, Element>());
		attributeNames.push(new Hashtable<String, Attribute>());
		elements.push(new ListElement() {
			public XmlSerializable read(XmlReader reader) {
				return null;
			}

			public void add(XmlSerializable child) {
				result = child;
			}
		});
		prototype.readXml(this);
		parser.parse(input, new Ha(this));
		elements.pop();
		elementNames.pop();
		attributeNames.pop();

		return result;
	}

	private class Ha extends DefaultHandler {
		XmlReader parent;

		public Ha(XmlReader parent) {
			this.parent = parent;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			final Dictionary<String, Element> elementNames = XmlReader.this.elementNames.peek();
			final Element element = elementNames == null ? null : elementNames.get(qName);
			final Element parentElement = elements.peek();

			if (element != null) {
				elements.push(element);
				XmlReader.this.elementNames.push(new Hashtable<String, Element>());
				attributeNames.push(new Hashtable<String, Attribute>());
				final XmlSerializable item = element.read(parent);

				if (parentElement instanceof ListElement) {
					ListElement le = (ListElement) parentElement;

					le.add(item);
				}
				item.readXml(parent);
				for (int i = 0; i < attributes.getLength(); i++) {
					Attribute attribute = attributeNames.peek().get(attributes.getQName(i));

					if (attribute != null) {
						attribute.read(attributes.getValue(i));
					}
				}
			} else {
				elements.push(null);
				XmlReader.this.elementNames.push(null);
				attributeNames.push(null);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			elements.pop();
			elementNames.pop();
			attributeNames.pop();
		}

	}

	public Element expect(String name, Element element) {
		elementNames.peek().put(name, element);
		return element;
	}

	public Attribute expect(String name, Attribute attribute) {
		attributeNames.peek().put(name, attribute);
		return attribute;
	}

	public interface Element {
		XmlSerializable read(XmlReader reader);
	}


	public interface ListElement extends Element {
		void add(XmlSerializable child);
	}


	public interface Attribute {
		void read(String value);
	}

	public static Object deserialize(InputStream input, XmlSerializable prototype) throws IOException {
		try {
			XmlReader xr = new XmlReader(input);

			return xr.deserialize(prototype);
		} catch (SAXException e) {
			throw new IOException();
		} catch (ParserConfigurationException e) {
			throw new IOException();
		}
	}
}
