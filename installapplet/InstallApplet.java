/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
/**
 * Insert the type's description here.
 * Creation date: (11/4/2001 7:44:32 PM)
 * @author: Administrator
 */
public class InstallApplet extends java.applet.Applet implements Runnable {
	Thread runThread = null;

	String line[] = new String[10];
	String bottomLine = "";

	Image osi = null;
	Graphics osg = null;
	
/**
 * InstallApplet constructor comment.
 */
public InstallApplet() {
	super();
	//add(new JLabel("Congratulations!  You are ready to install Robocode."));
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2001 8:24:26 PM)
 * @param g java.awt.Graphics
 * @param s java.lang.String
 */
public void centerString(Graphics g, String s) {

	int left, top, descent;
	int width, height;
	FontMetrics fm = getFontMetrics(getFont());
	width = fm.stringWidth(s);
	height = fm.getHeight();
	descent = fm.getDescent();


	left = bounds().width /2 - width / 2;
	top = bounds().height /2 - height / 2;

	if (left + width > bounds().width)
	{
		left = getWidth() - width;
	}
	if (top + height >= bounds().height)
	{
		top = getHeight() - height - 1;
	}
	if (left < 0)
	{
		left = 0;
	}
	if (top < 0)
	{
	  top = 0;
	}
	g.drawString(s,left,top + height - descent);
}
/**
 * Insert the method's description here.
 * Creation date: (11/8/2001 12:41:48 AM)
 * @return java.awt.Point
 * @param s java.lang.String
 * @param line int
 */
public Point getStartPosition(Graphics g, String s, int line) {
	FontMetrics fm = g.getFontMetrics();
	int l = (bounds().width - fm.stringWidth(s)) / 2;
	int t = (1+line) * fm.getHeight();
	return new Point(l,t);
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2001 8:06:53 PM)
 */
public void init() {
	line[0] = "Initializing...";
	setBackground(Color.white);
	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2001 8:00:44 PM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g) {
	if (osi == null || osg == null)
	{	
		if (osi == null)
			osi = createImage(bounds().width,bounds().height);
		osg = osi.getGraphics();
		if (osg == null)
		{
			g.setColor(Color.white);
			g.fillRect(0,0,bounds().width,bounds().height);
		}
	}
	osg.setColor(Color.white);
	osg.fillRect(0,0,bounds().width,bounds().height);
	osg.setColor(Color.black);
	for (int i = 0; i < 10; i++)
	{
		if (line[i] != null)
		{
			Point p = getStartPosition(osg,line[i],i);
			osg.drawString(line[i],p.x,p.y);
		}
	}
	Point p = getStartPosition(osg,bottomLine,0);
	osg.drawString(bottomLine,p.x,bounds().height-5);
	g.drawImage(osi,0,0,this);
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2001 8:06:53 PM)
 */
public void run() {
	bottomLine = "";//** Applet running, you may ignore the following instructions **";
	setBackground(Color.white);
	line[0] = "Determining Java version...";
	repaint();
	String javaVersion = System.getProperty("java.version");
	String osName = System.getProperty("os.name");

	line[0] = "Java version " + javaVersion;
	line[1] = "Operating system: " + osName;
	boolean greenLight = false;
	boolean oldMac = false;
	boolean is14 = (javaVersion.compareTo("1.4") >= 0);
	boolean easyroad = false;
	boolean upgrade = false;
	
//	String query = getDocumentBase().getQuery();
	String query = getParameter("query");
	if (query == null)
		query = "";
	if (query.indexOf("easyroad") != -1)
		easyroad = true;
	if (query.indexOf("upgrade") != -1)
		upgrade = true;
	
	if (javaVersion.compareTo("1.3") >= 0)
	{
		line[3] = "Congratulations!  Your system is ready for Robocode.";
		greenLight = true;
	}
	else
	{
		if (osName.indexOf("Mac") == 0)
		{
			line[3] = "Sorry, you must be running at least Mac OS X to run Robocode.";
			line[4] = "(" + javaVersion + " is too old)";
			line[5] = "Please upgrade to OS X and come back!";
			oldMac = true;
		}
		else
		{
			line[3] = "Sorry, unable to determine if Robocode will run on your system...";
			line[4] = "(" + javaVersion + " is too old)";
			line[5] = "You have two options:";
			line[6] = "1. Click \"Back\" and select the \"Advanced Users\" option.";
			line[7] = "2. Try another web browser, or upgrade this one and come back!.";
			line[9] = "For more help, try the Discussion Forum at http://sourceforge.net/forum/?group_id=37202";
		}
		if (osName.indexOf("Win") == 0)
		{
			line[1] += " (or newer) ";
		}
	}
	repaint();

	if (greenLight)
	{
		for (int i = 10; i >= 0; i--)
		{
			line[5] = "Proceeding in " + i;
			repaint();
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
		}
	}
	try {
		if (greenLight)
		{
			if (osName.indexOf("Win") == 0)
			{
				if (is14 || easyroad)
					getAppletContext().showDocument(new URL(getDocumentBase(),"install_win.html"));
				else if (upgrade)
					getAppletContext().showDocument(new URL(getDocumentBase(),"upgraded_check.html?install_win.html"));
				else
					getAppletContext().showDocument(new URL(getDocumentBase(),"upgrade_check.html?install_win.html"));
			}
			else if (osName.indexOf("Mac") == 0)
				getAppletContext().showDocument(new URL(getDocumentBase(),"install_mac.html"));
			else if (osName.indexOf("Linux") == 0)
			{
				if (is14 || easyroad)
					getAppletContext().showDocument(new URL(getDocumentBase(),"install_linux.html"));
				else if (upgrade)
					getAppletContext().showDocument(new URL(getDocumentBase(),"upgraded_check.html?install_linux.html"));
				else
					getAppletContext().showDocument(new URL(getDocumentBase(),"upgrade_check.html?install_linux.html"));
			}
			else
				getAppletContext().showDocument(new URL(getDocumentBase(),"install_other.html"));
		}
		/*else
		{
			if (!oldMac)
				getAppletContext().showDocument(new URL(getDocumentBase(),"detectjavaversion_manual.html"));
			else
				getAppletContext().showDocument(new URL(getDocumentBase(),"needmacosx.html"));
				
		}*/
	} catch (MalformedURLException e) {
		line[5] = "Unable to proceed: " + e;
		repaint();
	}


	
	//add(new Label("You are using Java vendor " + System.getProperty("java.vendor")));
	//add(new Label("You are on OS:  " + ));
	//add(new Label("You are on arch: " + System.getProperty("os.arch")));
	//add(new Label("You are on OS version: " + System.getProperty("os.version")));

	/*
	try {
		getAppletContext().showDocument(new URL("http://www.bugg.com"));
	} catch (MalformedURLException e) {
	}
	*/
	/*
	String result = "No result";
	try {
		Process p = Runtime.getRuntime().exec("java -version");
		DataInputStream in = new DataInputStream(p.getInputStream());
		result = in.readLine();
	} catch (Exception e) {
		result = e.toString();
	}
	add(new Label(result));
	*/
	validate();
	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2001 8:06:53 PM)
 */
public void start() {

	runThread = new Thread(this);
	runThread.start();
	
}
}
