/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.parsers;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Class used for parsing a .classpath file in an Eclipse project.
 *
 * @author Flemming N. Larsen (original)
 */
public class ClasspathFileParser {

	private ClasspathHandler classpathHandler = new ClasspathHandler();

	public void parse(URL url) {
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(url.toString(), classpathHandler);
		} catch (SAXException ignore) {} catch (IOException ignore) {} catch (ParserConfigurationException ignore) {}
	}

	public String[] getSourcePaths() {
		return classpathHandler.sourcePaths.toArray(new String[] {});
	}

	public String getClassPath() {
		return classpathHandler.outputPath;
	}

	private static class ClasspathHandler extends DefaultHandler {
		String outputPath = null;
		List<String> sourcePaths = new ArrayList<String>();		

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ("classpathentry".equals(qName)) {
				String kind = attributes.getValue("kind");

				if ("src".equals(kind)) {
					sourcePaths.add(attributes.getValue("path"));
				} else if ("output".equals(kind)) {
					outputPath = attributes.getValue("path");
				}
			}
		}			
	}
}
