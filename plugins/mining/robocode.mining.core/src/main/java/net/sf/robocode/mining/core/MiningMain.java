/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/

package net.sf.robocode.mining.core;

import net.sf.robocode.core.Container;
import net.sf.robocode.core.RobocodeMainBase;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.recording.BattleRecordFormat;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.security.HiddenAccess;

import java.io.File;
import java.io.FilenameFilter;

public class MiningMain {
    public static void main(String[] args){
        System.setProperty("NOSECURITY", "true");
        HiddenAccess.init();
	    IRecordManager recordManager = Container.getComponent(IRecordManager.class);

	    String path="d:\\Robocode\\battles\\";
	    File pathF = new File(path);
	    File[] files = pathF.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			    return name.toLowerCase().endsWith(".xml.zip");
		    }
	    });

	    for(File rec:files){
		    recordManager.loadRecord(rec.getAbsolutePath(), BattleRecordFormat.XML_ZIP);
		    BattlePlayer player=Container.createComponent(BattlePlayer.class);
	        player.run();
		    player.cleanup();
	    }
    }
}
