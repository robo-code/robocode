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

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.core.Container;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.recording.BattleRecordFormat;
import net.sf.robocode.recording.DirectPlayer;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.security.HiddenAccess;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.channels.NonWritableChannelException;

public class MiningMain {
    public static void main(String[] args){
        System.setProperty("NOSECURITY", "true");
        HiddenAccess.init();

	    DirectPlayer dp= new DirectPlayer();
	    MiningListener miningListener = new MiningListener ();
	    BattleEventDispatcher eventDispatcher=new BattleEventDispatcher();
	    eventDispatcher.addListener(miningListener);

	    String path=args[0];
	    File pathF = new File(path);
	    File[] files = pathF.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			    return name.toLowerCase().endsWith(".xml.zip");
		    }
	    });

	    for(File rec:files){
		    System.out.println(rec.getAbsolutePath());
		    File done = new File(rec.getParentFile(), "done\\" + rec.getName());
		    rec.renameTo(done);

		    File csv = new File(rec.getParentFile(), "csv\\" + rec.getName() + ".csv");
		    miningListener.outputFileName =csv.getAbsolutePath();

		    dp.playRecord(done.getAbsolutePath(), BattleRecordFormat.XML_ZIP, eventDispatcher);
	    }
    }
}
