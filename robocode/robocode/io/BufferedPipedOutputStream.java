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
import java.io.OutputStream;

public class BufferedPipedOutputStream extends OutputStream {

	byte buf[] = null; //new byte[8192];
	int readIndex = 0;
	int writeIndex = 0;
	boolean waiting = false;
	private BufferedPipedInputStream in = null;
	private boolean closed = false;
	private boolean skipLines = false;
	private boolean blocking = false;
	
	private BufferedPipedOutputStream()
	{
	}
	
	public BufferedPipedOutputStream(int bufferSize,boolean skipLines, boolean blocking)
	{
		this.buf = new byte[bufferSize];
		this.skipLines = skipLines;
		this.blocking = blocking;
		
	}
	/*
	 * @see OutputStream#write(int)
	 */
	public synchronized void write(int b) throws IOException {
		if (closed)
			throw new IOException("Stream is closed.");
			
		buf[writeIndex++] = (byte)(b & 0xff);
		if (writeIndex == buf.length)
			writeIndex = 0;
		if (writeIndex == readIndex)
		{
			// skipping a line!
			if (skipLines)
				setReadIndexToNextLine();
			else
				throw new IOException("Buffer is full.");
		}
		if (waiting)
			notify();
	}
	
	private void setReadIndexToNextLine() {
		while (buf[readIndex] != '\n')
		{
			readIndex++;
			if (readIndex == buf.length)
				readIndex = 0;
			if (readIndex == writeIndex)
				return;
		}
		readIndex++;
		if (readIndex == buf.length)
			readIndex = 0;
	}
	
	protected synchronized int read() throws IOException {
		if (readIndex == writeIndex)
		{
			waiting = true;
			try {
				if (!closed)
					wait(10000);
				if (closed)
				{
					return -1;
				}
			} catch (InterruptedException e) {
				throw new IOException("read interrupted");
			}
		}
		int result = (int)buf[readIndex++];
		if (readIndex == buf.length)
			readIndex = 0;
		
		return result;
	}
	
	public synchronized int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
		    throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) ||
			   ((off + len) > b.length) || ((off + len) < 0)) {
		    throw new IndexOutOfBoundsException();
		} else if (len == 0) {
		    return 0;
		}

/*
		if (readIndex == writeIndex && blocking == false)
		{
			System.out.println("read returning 0.");
			return 0;
		}
*/			
		int first = read();
		if (first == -1)
			return -1;
		b[off] = (byte)(first & 0xff);
		int count = 1;
		
		for (int i = 1; readIndex != writeIndex && i < len; i++)
		{
			b[off + i] = buf[readIndex++];
			count++;
			if (readIndex == buf.length)
				readIndex = 0;
		}
		return count;
	}	
	
	protected synchronized int available() {
		if (writeIndex == readIndex)
		{
			return 0;
		}
		else if (writeIndex > readIndex)
		{
			return writeIndex - readIndex;
		}
		else
			return buf.length - readIndex + writeIndex;
	}
	
	public BufferedPipedInputStream getInputStream() {
		if (in == null)
		{
			in = new BufferedPipedInputStream(this);
		}
		return in;
	}
	
	public synchronized void close() {
		this.closed = true;
		if (waiting)
			notify();
	}

}

