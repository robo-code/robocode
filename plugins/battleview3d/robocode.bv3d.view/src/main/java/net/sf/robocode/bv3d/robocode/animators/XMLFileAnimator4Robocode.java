/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode.animators;


import java.io.FileReader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

// import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import net.sf.robocode.bv3d.MVCManager;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

// TODO this needs to be revisited, probably rewritten from scratch or deleted
// TODO we no longer use XML
public class XMLFileAnimator4Robocode extends NetXMLAnimator4Robocode {
	
	private Document doc;
	private Node battle;
	private Node round;
	private Node settings;
	private Node turn;
	
	public XMLFileAnimator4Robocode(MVCManager manager, String xmlFile) {
		super(manager);
		this.setFPS(manager.DESIRED_FPS);
		
		// DOMParser parser = new DOMParser();
		try {
			FileReader fr = new FileReader(xmlFile);
			InputSource source = new InputSource(fr);

			// parser.parse(source);
			fr.close();
		} catch (Exception e) {}
		// doc = parser.getDocument();
		createBattle();
	}
	
	private void createBattle() {
		battle = doc.getFirstChild();
		settings = battle.getFirstChild();
		round = battle.getLastChild();
		turn = round.getFirstChild();
	}
	
	@Override
	protected void setup() {
		processXMLNode(settings);
		super.displayMessage("New battle is now displaying");
	}
	
	@Override
	public void updateScene() {
		turn = turn.getNextSibling();
		processXMLNode(turn);
		if (turn.getNextSibling() == null) {
			this.updateCondition = false;
		}
	}

}
