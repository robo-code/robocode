/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.editor;


import javax.swing.text.Segment;
import java.io.IOException;


/**
 * @author Mathew A. Nelson (original)
 */
public class SegmentInputStream extends java.io.InputStream {
	public javax.swing.text.Segment segment;
	public int index;

	/**
	 * SegmentInputStream constructor
	 */
	public SegmentInputStream(Segment segment) {
		super();
		this.segment = segment;
		this.index = segment.offset;
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an {@code int} in the range 0 to 255. If no byte is available
	 * because the end of the stream has been reached, the value -1 is returned.
	 * This method blocks until input data is available, the end of the stream
	 * is detected, or an exception is thrown.
	 * <p/>
	 * <p> A subclass must provide an implementation of this method.
	 *
	 * @return the next byte of data, or -1 if the end of the stream is reached.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public int read() throws java.io.IOException {
		if (index >= segment.offset + segment.count) {
			return -1;
		}
		return segment.array[index++];
	}
}
