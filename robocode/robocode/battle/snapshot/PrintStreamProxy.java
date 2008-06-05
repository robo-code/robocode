/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.snapshot;


import java.io.*;
import java.util.Locale;


/**
 * The PrintStreamProxy is a PrintStream proxy class which queues up output
 * to be processed later by a real PrintStream instance. All methods on this
 * class will call an internal PrintStream, which will buffer all output until
 * this proxy is processed by the {@link #processTo(PrintStream)} method.
 * </p>
 * Example:
 * <pre>
 *    // Create the PrintStream proxy which works on a real PrintStream object
 *    // declared earlier as 'out'.
 *    PrintStream psProxy = new PrintStreamProxy(out);
 *    ...
 *    // Call some standard PrintStream methods
 *    psProxy.println("Hello World!");
 *    psProxy.flush();
 *    ...
 *    psProxy.println("Another line");
 *    ...
 *    // Process all pending output to the real PrintStream object
 *    psProxy.processTo(out); // where 'out' is the real PrintStream object
 *    ...
 *    // Now, the real PrintStream 'out' will contain the two lines
 *    // "Hello World!" and "Another line", and the output in the psProxy
 *    // has been cleared when processed.
 * </pre>
 *
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public class PrintStreamProxy extends PrintStream {

	// Output stream for the internal output that is buffered up in this object
	private final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

	// Buffered output stream for the internal output stream
	private final BufferedOutputStream bufferedOut = new BufferedOutputStream(bytesOut);

	// Internal print stream that outputs to the buffered output stream, and hence the internal output stream
	private final PrintStream printStream = new PrintStream(bufferedOut);

	// --------------------------------------------------------------------------
	// PrintStream constructors
	// --------------------------------------------------------------------------

	public PrintStreamProxy(OutputStream out) {
		super(out);
	}

	public PrintStreamProxy(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public PrintStreamProxy(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}

	public PrintStreamProxy(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public PrintStreamProxy(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public PrintStreamProxy(File file) throws FileNotFoundException {
		super(file);
	}

	public PrintStreamProxy(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	// --------------------------------------------------------------------------
	// Finalizer
	// --------------------------------------------------------------------------
	/* Causes trouble!!
	 public void finalize() throws Throwable {
	 super.finalize();

	 close();
	 }
	 */

	// --------------------------------------------------------------------------
	// Copying
	// --------------------------------------------------------------------------

	/**
	 * Deep copies this PrintStreamProxy.
	 *
	 * @return a deep copy of this PrintStreamProxy.
	 */
	public PrintStreamProxy copy() {
		PrintStreamProxy copy = new PrintStreamProxy(out);

		try {
			// Flush the BufferedOutputStream to the ByteArrayOutputStream
			bufferedOut.flush();

			// Write the bytes from ByteArrayOutputStream to the BufferedOutputStream of the copy
			copy.bufferedOut.write(bytesOut.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return copy;
	}
	
	// --------------------------------------------------------------------------
	// FilterOutputStream methods
	// --------------------------------------------------------------------------

	public void write(byte[] b) throws IOException {
		printStream.write(b);
	}

	// --------------------------------------------------------------------------
	// PrintStream methods
	// --------------------------------------------------------------------------

	public void flush() {
		printStream.flush();
	}

	public void close() {
		super.close();

		printStream.close();
		try {
			bufferedOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bytesOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean checkError() {
		return printStream.checkError();
	}

	public void write(int b) {
		printStream.write(b);
	}

	public void write(byte[] buf, int off, int len) {
		printStream.write(buf, off, len);
	}

	public void print(boolean b) {
		printStream.print(b);
	}

	public void print(char c) {
		printStream.print(c);
	}

	public void print(int i) {
		printStream.print(i);
	}

	public void print(long l) {
		printStream.print(l);
	}

	public void print(float f) {
		printStream.print(f);
	}

	public void print(double d) {
		printStream.print(d);
	}

	public void print(char[] s) {
		printStream.print(s);
	}

	public void print(String s) {
		printStream.print(s);
	}

	public void print(Object obj) {
		printStream.print(obj);
	}

	public void println() {
		printStream.println();
	}

	public void println(boolean x) {
		printStream.println(x);
	}

	public void println(char x) {
		printStream.println(x);
	}

	public void println(int x) {
		printStream.println(x);
	}

	public void println(long x) {
		printStream.println(x);
	}

	public void println(float x) {
		printStream.println(x);
	}

	public void println(double x) {
		printStream.println(x);
	}
	
	public void println(char[] x) {
		printStream.println(x);
	}

	public void println(String x) {
		printStream.println(x);
	}

	public void println(Object x) {
		printStream.println(x);
	}
	
	public PrintStream printf(String format, Object... args) {
		printStream.printf(format, args);
		return this;
	}

	public PrintStream printf(Locale l, String format, Object... args) {
		printStream.printf(l, format, args);
		return this;
	}

	public PrintStream format(String format, Object... args) {
		printStream.format(format, args);
		return this;
	}

	public PrintStream format(Locale l, String format, Object... args) {
		printStream.format(l, format, args);
		return this;
	}

	public PrintStream append(CharSequence csq) {
		printStream.append(csq);
		return this;
	}

	public PrintStream append(CharSequence csq, int start, int end) {
		printStream.append(csq, start, end);
		return this;
	}

	public PrintStream append(char c) {
		printStream.append(c);
		return this;
	}

	// --------------------------------------------------------------------------
	// Processing of this PrintStreamProxy to a real PrintStream
	// --------------------------------------------------------------------------

	/**
	 * Process the output in this PrintStream proxy to the real output stream.
	 * The specified PrintStream is automatically flushed, and the output
	 * buffered in this PrintStream proxy will be cleared.
	 *
	 * @throws IOException if an I/O exception occurs
	 */
	public void process() throws IOException {
		// Flush the internal PrintStream to the BufferedOutputStream
		printStream.flush();

		// Flush the BufferedOutputStream to the ByteArrayOutputStream
		bufferedOut.flush();

		// Write output from ByteArrayOutputStream to the real out thru a PrintStream
		PrintStream ps = new PrintStream(out);

		ps.write(bytesOut.toByteArray());
		ps.flush();

		bytesOut.reset();
	}

	/*
	 // For testing purposes

	 public static void main(String... args) {
	 final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	 final BufferedOutputStream bufferedOut = new BufferedOutputStream(bytesOut);

	 PrintStream out = new PrintStream(bufferedOut);

	 PrintStreamProxy proxy = new PrintStreamProxy(out);

	 proxy.println("1st");
	 proxy.println("2nd");
	 
	 PrintStreamProxy proxyCopy = proxy.copy();

	 try {
	 proxy.process();
	 } catch (IOException e) {
	 e.printStackTrace();
	 }

	 // proxy.flush();
	 // proxy.close();
	 
	 // System.out.println("proxyCopy error: " + proxyCopy.checkError());

	 proxy.println("3rd");
	 proxy.println("4th");
	 // System.out.println("proxy error: " + proxy.checkError());
	 try {
	 proxy.process();
	 } catch (IOException e) {
	 e.printStackTrace();
	 }

	 System.out.print(bytesOut);
	 bytesOut.reset();
	 System.out.println("----");

	 try {
	 proxyCopy.process();
	 } catch (IOException e) {
	 e.printStackTrace();
	 }

	 System.out.print(bytesOut);
	 bytesOut.reset();
	 
	 proxy.close();
	 proxy.close(); // for testing what happens when done twice
	 }*/
}
