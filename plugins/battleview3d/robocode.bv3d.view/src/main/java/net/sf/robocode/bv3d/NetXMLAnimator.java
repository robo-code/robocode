/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * This is a special Animator that run in a Server-mode.
 * During the {@link NetXMLAnimator#setup()} method it waits a connection on a port specified in {@link NetXMLAnimator#PORT}.
 * When connection is established, in a {@link NetXMLAnimator#updateScene()} the Socket is read.
 * The XML-formed line just read is processed by {@link NetXMLAnimator#processXMLNode(Node)}.
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */
// TODO this needs to be revisited, probably rewritten from scratch or deleted
public abstract class NetXMLAnimator extends Animator {

	private int port = 4444;
	protected boolean diplayConfigInfo;
	private BufferedReader in;
	// private PrintWriter out;
	private ServerSocket serverSocket;
	
	public NetXMLAnimator(MVCManager manager) {
		super(manager);
		diplayConfigInfo = false;
	}
	
	public NetXMLAnimator(MVCManager manager, int portNumber) {
		this(manager);
		this.port = portNumber;
	}
	
	/**
	 * This method blocks the execution until receives a connection on port specified in {@link NetXMLAnimator#PORT}.
	 * Socket is configured.
	 */
	protected void setup() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(1);
		}

		Socket clientSocket = null;

		try {
			super.displayMessage("Waiting for the client...");
			if (this.diplayConfigInfo) {
				displayConfigInfo();
			}
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}
		try {
			// out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (Exception e) {
			System.err.println("Cannot create PrintWriter and BufferedReader");
		}
	}
	
	/**
	 * Each time this method is called, it reads a expected XML-formed line from the Socket, and passed it at the {@link NetXMLAnimator#processXMLNode(Node)}
	 */
	protected void updateScene() {
		try {
			String inputLine = null;

			if (!serverSocket.isClosed()) {
				inputLine = in.readLine();
			}
			if (inputLine != null) {
				Document XMLDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
						new InputSource(new StringReader(inputLine)));
				Node el = XMLDoc.getFirstChild();

				processXMLNode(el);
			}
		} catch (SocketException e) {
			displayAlert("Il client ha chiuso la connessione,\nl'applicazione verra` chiusa", "Chiusura connessione");
			System.exit(1);
		} // catch (Exception e) {
		// System.err.println("Exception in update() of NetXMLAnimator.\n "+e.getClass()+" - "+e.getMessage());
		// }
		// outputLine = kkp.processInput(inputLine);
		// out.println(outputLine);
		catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method must implement the real process-logic for each XML-formed-text read.
	 * @param el The XML-formed-text read, dressed like a Node.
	 */
	protected abstract void processXMLNode(Node el);
	
	protected void displayConfigInfo() throws UnknownHostException {
		String message = "Client must be configured to connect to:\n\n";

		message += "IP: " + InetAddress.getLocalHost() + "\n";
		message += "PORT: " + port;
		String title = "Client configuration";

		super.displayAlert(message, title);
	}

}
