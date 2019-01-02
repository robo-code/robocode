/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * @author Pavel Savara (original)
 */
public class XmlReader {

	private SAXParser parser;

	private final InputStream input;
	private final Stack<Element> elements = new Stack<Element>();
	private final Stack<IXmlSerializable> items = new Stack<IXmlSerializable>();
	private final Stack<Map<String, Element>> elementNames = new Stack<Map<String, Element>>();
	private final Stack<Map<String, Attribute>> attributeNames = new Stack<Map<String, Attribute>>();
	private IXmlSerializable result;
	private Map<String, Object> context = new HashMap<String, Object>();

	private XmlReader(InputStream input) throws SAXException, ParserConfigurationException {
		this.input = input;
		SAXParserFactory factory = SAXParserFactory.newInstance();

		parser = factory.newSAXParser();
	}

	private Object deserialize(IXmlSerializable prototype) throws IOException, SAXException {
		elementNames.push(new HashMap<String, Element>());
		attributeNames.push(new HashMap<String, Attribute>());
		items.push(null);
		elements.push(new ListElement() {
			public IXmlSerializable read(XmlReader reader) {
				return null;
			}

			public void add(IXmlSerializable child) {
				result = child;
			}

			public void close() {}
		});
		prototype.readXml(this);
		parser.parse(input, new Handler(this));
		items.pop();
		elements.pop();
		elementNames.pop();
		attributeNames.pop();

		return result;
	}

	private class Handler extends DefaultHandler {
		final XmlReader parent;

		public Handler(XmlReader parent) {
			this.parent = parent;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			final Map<String, Element> names = XmlReader.this.elementNames.peek();
			final Element element = names == null ? null : names.get(qName);

			if (element != null) {
				elements.push(element);
				XmlReader.this.elementNames.push(new HashMap<String, Element>());
				attributeNames.push(new HashMap<String, Attribute>());
				final IXmlSerializable item = element.read(parent);

				item.readXml(parent);
				for (int i = 0; i < attributes.getLength(); i++) {
					Attribute attribute = attributeNames.peek().get(attributes.getQName(i));

					if (attribute != null) {
						attribute.read(attributes.getValue(i));
					}
				}
				items.push(item);
			} else {
				items.push(null);
				elements.push(null);
				XmlReader.this.elementNames.push(null);
				attributeNames.push(null);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			elements.pop();
			final IXmlSerializable item = items.peek();
			final Element parentElement = elements.peek();

			if (parentElement instanceof ListElement) {
				ListElement le = (ListElement) parentElement;

				le.add(item);
			}
			items.pop();
			elementNames.pop();
			attributeNames.pop();
			final Map<String, Element> names = XmlReader.this.elementNames.peek();
			final Element element = names == null ? null : names.get(qName);

			if (element != null) {
				if (element instanceof ElementClose) {
					ElementClose ec = (ElementClose) element;

					ec.close();
				}
			}
		}

	}

	public Map<String, Object> getContext() {
		return context;
	}

	public Element expect(String name, Element element) {
		elementNames.peek().put(name, element);
		return element;
	}

	public Element expect(String name, String altName, Element element) {
		elementNames.peek().put(name, element);
		elementNames.peek().put(altName, element);
		return element;
	}

	public Attribute expect(String name, Attribute attribute) {
		attributeNames.peek().put(name, attribute);
		return attribute;
	}

	public Attribute expect(String name, String altName, Attribute attribute) {
		attributeNames.peek().put(name, attribute);
		attributeNames.peek().put(altName, attribute);
		return attribute;
	}

	public interface Element {
		IXmlSerializable read(XmlReader reader);
	}


	public interface ElementClose extends Element {
		void close();
	}


	public interface ListElement extends ElementClose {
		void add(IXmlSerializable child);
	}


	public interface Attribute {
		void read(String value);
	}

	public static Object deserialize(InputStream input, IXmlSerializable prototype) throws IOException {
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
