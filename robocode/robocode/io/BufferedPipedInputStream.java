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

import java.io.IOException;
import java.io.InputStream;

public class BufferedPipedInputStream extends InputStream {

	private BufferedPipedOutputStream out = null;
	
	protected BufferedPipedInputStream(BufferedPipedOutputStream out)
	{
		this.out = out;
	}
	
	/*
	 * @see InputStream#read()
	 */
	public int read() throws IOException {
		return out.read();
	}
	
	public synchronized int read(byte b[], int off, int len) throws IOException {
		return out.read(b,off,len);
	}
	
	public int available() {
		return out.available();
	}

}

