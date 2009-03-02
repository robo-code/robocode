/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */
public abstract class DirectAnimator extends Animator {

	// private int port = 4444;
	// protected boolean diplayConfigInfo;
	// private BufferedReader in;
	// //	private PrintWriter out;
	
	private DataStore ds;
	
	public DirectAnimator(MVCManager manager, DataStore ds) {
		super(manager);
		this.ds = ds;
		// diplayConfigInfo = false;
	}
	
	protected void setup() {}
	
	/**
	 * Each time this method is called, it reads a expected XML-formed line from the Socket, and passed it at the {@link DirectAnimator#processXMLNode(Node)}
	 */
	protected void updateScene() {
		String data;

		if ((data = ds.getData()) != null) {
			Document XMLDoc = null;

			try {
				XMLDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
						new InputSource(new StringReader(data)));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			Node el = XMLDoc.getFirstChild();

			// System.out.println(el.toString());
			processXMLNode(el);
		}
	}
	
	/**
	 * This method must implement the real process-logic for each XML-formed-text read.
	 * @param el The XML-formed-text read, dressed like a Node.
	 */
	protected abstract void processXMLNode(Node el);

}
