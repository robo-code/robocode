/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import pimods.Animator;
import pimods.MVCManager;
import pimods.MainFrame;
import pimods.robocode.animators.NetXMLAnimator4Robocode;
import pimods.robocode.animators.RobocodeLiveAnimator;
import pimods.robocode.animators.XMLFileAnimator4Robocode;

/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Pimods4Robocode {
	
	private static final int FILE_XML = 0;
	private static final int LOCAL = 1;
	private static final int NET = 2;
	private static String xmlFile = "misc/battle4pimods.xml";
	
	public static void main( String[] args ){
		MainFrame mFrame = new MainFrame();
		MVCManager4Robocode mvcManager = new MVCManager4Robocode( mFrame );
		OptionFrame4Robocode oFrame = new OptionFrame4Robocode( mvcManager );
		mvcManager.setOptionFrame( oFrame );		
		oFrame.setLocation( 0, mFrame.getHeight()+10);
		mFrame.setVisible( true );
		oFrame.setVisible( true );
		
		
		// OPTIONS SETTINGS
		Animator animator = null;
		// Choose online or offline version
		int op = showOptions();
		switch( op ){
		case FILE_XML:
			animator = offlineVersion( mvcManager );
			break;
		case LOCAL:
			animator = new RobocodeLiveAnimator( mvcManager );
			break;
		case NET:
			animator = netVersion( mvcManager );
			break;	
		default:
			System.exit( 0 );
		}
		
		mvcManager.setup( new GraphicListener4Robocode(), animator );
		
		mvcManager.run();
	}
	
	private static int showOptions(){
		Object[] possibilities = {"XML-file", "locale Robocode", "remote Robocode"};
		String s = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Run from: ",
		                    "Options",
		                    JOptionPane.QUESTION_MESSAGE,
		                    null,
		                    possibilities,
		                    "File XML");

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
		    if( s.equals("XML-file") ) return FILE_XML;
		    if( s.equals("locale Robocode") ) return LOCAL;
		    if( s.equals("remote Robocode") ) return NET;
		}else{
			return -1;
		}
		return -1;
	}
	
	private static Animator offlineVersion( MVCManager mvcManager ) {
		final JFileChooser fc = new JFileChooser( "misc" );
		fc.setFileFilter( new FileNameExtensionFilter("XML file", "xml") );
		int returnVal = fc.showOpenDialog( null );
		if( returnVal == JFileChooser.APPROVE_OPTION ){
			File f = fc.getSelectedFile();
			if( f.getAbsolutePath().endsWith("xml") && f.exists() ){
					xmlFile = f.getAbsolutePath();
			}else{
				JOptionPane.showMessageDialog( null, "Wrong file.\nI proceed with default file.");
			}
			return new XMLFileAnimator4Robocode( mvcManager, xmlFile );
		}
		return null;
	}
	
	private static Animator netVersion( MVCManager mvcManager ){
		int port = 4444;
		String input = JOptionPane.showInputDialog(null, 
				"Select port number (by default is 4444)", 
				port);
		try{
			port = Integer.parseInt( input );
		}catch( NumberFormatException e){
			System.err.println("Bad port number!");
			port = 4444;
		}
		return new NetXMLAnimator4Robocode( mvcManager, port );
	}

}
