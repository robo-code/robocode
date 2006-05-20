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
package robocode.io;


import java.io.*;


public class TeamMessageSizer extends OutputStream {
	
	private long count = 0;
	private long MAX = 32768;
	
	public synchronized void write(int b) throws IOException {
		count++;
		if (count > MAX) {
			throw new IOException("You have exceeded " + MAX + " bytes this turn.");
		}
	}
	
	public synchronized void resetCount() {
		count = 0;
	}
	
	public synchronized long getCount() {
		return count;
	}

}

