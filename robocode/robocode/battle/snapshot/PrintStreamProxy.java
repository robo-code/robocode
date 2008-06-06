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
 *    // Create a PrintStream proxy
 *    PrintStream psProxy = new PrintStreamProxy();
 *    ...
 *    // Call some standard PrintStream methods
 *    psProxy.println("Hello World!");
 *    psProxy.flush();
 *    ...
 *    psProxy.println("Another line");
 *    ...
 *    // Process all pending output to the real output stream
 *    psProxy.processTo(System.out); // Here the output stream is System.out
 *    ...
 *    // Now, the output stream 'out' will contain the two lines "Hello World!"
 *    // and "Another line", and the output in the psProxy has been cleared
 *    // when processed.
 * </pre>
 *
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public class PrintStreamProxy implements Serializable {

	private static final long serialVersionUID = 1L;

	// Output stream for the internal output that is buffered up in this object
	private transient ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

	// Buffered output stream for the internal output stream
	private transient BufferedOutputStream bufferedOut = new BufferedOutputStream(bytesOut);

	// Internal print stream that outputs to the buffered output stream, and hence the internal output stream
	private transient PrintStream printStream = new PrintStream(bufferedOut);

	// --------------------------------------------------------------------------
	// Serialization
	// --------------------------------------------------------------------------

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// Flush the internal PrintStream to the BufferedOutputStream
		printStream.flush();

		// Flush the BufferedOutputStream to the ByteArrayOutputStream
		bufferedOut.flush();

		// Write the output from ByteArrayOutputStream to the object output stream 
		out.write(bytesOut.toByteArray());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		bytesOut = new ByteArrayOutputStream();
		bufferedOut = new BufferedOutputStream(bytesOut);
		printStream = new PrintStream(bufferedOut);

		// Read all bytes from input object stream into the ByteArrayOutputStream
		byte[] bytes = new byte[1024];
		int len;

		do {
			len = in.read(bytes);
			if (len > 0) {
				bytesOut.write(bytes, 0, len);
			}
		} while (len >= 0);
	}

	// --------------------------------------------------------------------------
	// Copying
	// --------------------------------------------------------------------------

	/**
	 * Deep copies this PrintStreamProxy.
	 *
	 * @return a deep copy of this PrintStreamProxy.
	 */
	public PrintStreamProxy copy() {
		PrintStreamProxy copy = new PrintStreamProxy();

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
		return printStream;
	}

	public PrintStream printf(Locale l, String format, Object... args) {
		printStream.printf(l, format, args);
		return printStream;
	}

	public PrintStream format(String format, Object... args) {
		printStream.format(format, args);
		return printStream;
	}

	public PrintStream format(Locale l, String format, Object... args) {
		printStream.format(l, format, args);
		return printStream;
	}

	public PrintStream append(CharSequence csq) {
		printStream.append(csq);
		return printStream;
	}

	public PrintStream append(CharSequence csq, int start, int end) {
		printStream.append(csq, start, end);
		return printStream;
	}

	public PrintStream append(char c) {
		printStream.append(c);
		return printStream;
	}

	// --------------------------------------------------------------------------
	// Processing of this PrintStreamProxy to a real PrintStream
	// --------------------------------------------------------------------------

	/**
	 * Process the output in this PrintStream proxy to the real output stream.
	 * The specified PrintStream is automatically flushed, and the output
	 * buffered in this PrintStream proxy will be cleared.
	 *
	 * @param out the output stream to process the buffered content of this
	 *            print stream.
	 * @throws IOException if an I/O exception occurs.
	 */
	public void processTo(OutputStream out) throws IOException {
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

	 // For testing purposes
	/*
		 public static void main(String... args) {
			 PrintStreamProxy proxy = new PrintStreamProxy();
		
			 proxy.println("1st");
			 proxy.println("2nd");
			 
			 PrintStreamProxy proxyCopy = proxy.copy();
		
			 try {
				 proxy.processTo(System.out);
			 } catch (IOException e) {
				 e.printStackTrace();
				 System.exit(-1);
			 }

			 // proxy.flush();
			 // proxy.close();
			 
			 // System.out.println("proxyCopy error: " + proxyCopy.checkError());
		
			 proxy.println("3rd");
			 proxy.println("4th");
			 // System.out.println("proxy error: " + proxy.checkError());
			 try {
				 proxy.processTo(System.out);
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		
			 System.out.println("----");
		
			 try {
				 proxyCopy.processTo(System.out);
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 
			 proxy.close();
			 proxy.close(); // for testing what happens when done twice
		 }
	*/
	/*
		public static void main(String... args) {
			 PrintStreamProxy proxy = new PrintStreamProxy();

			 proxy.println("Hello World!");

			 final String filename = "C:/temp/tmp.bin";
			 
			 try {
				 FileOutputStream fos = new FileOutputStream(filename);
				 ObjectOutputStream oos = new ObjectOutputStream(fos);
				 oos.writeObject(proxy);
				 oos.close();
			 } catch (Exception e) {
				 e.printStackTrace();
				 System.exit(-1);
			 }
			 
			 PrintStreamProxy proxy2 = null;
			 try {
				 FileInputStream fis = new FileInputStream(filename);
				 ObjectInputStream ois = new ObjectInputStream(fis);
				 proxy2 = (PrintStreamProxy) ois.readObject();
				 ois.close();
			 } catch (Exception e) {
				 e.printStackTrace();
				 System.exit(-2);
			 }
			 
			 try {
				proxy2.processTo(System.out);
			} catch (IOException e) {
				e.printStackTrace();
				 System.exit(-3);
			}
		}*/
}
