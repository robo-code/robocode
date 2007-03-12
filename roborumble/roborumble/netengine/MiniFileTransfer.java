package roborumble.netengine;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * download a file, fishing it out of either this or another jar.
 * To read or write from the client's local hard disk, you will need a signed Applet and
 * security clearance. see Signed Applet in the Java glossary.
 * To read a files from the server, the file must be given public read access, usually
 * the default.
 *
 * If you make more than casual use, please sent a registration fee of
 * $10 either US or Canadian to:
 *
 * FILETRANSFER REGISTRATIONS
 * Roedy Green
 * Canadian Mind Products
 * #327 - 964 Heywood Avenue
 * Victoria, BC Canada V8V 2Y5
 * mailto:roedy@mindprod.com
 * http://mindprod.com
 *
 * "More that casual use" would include using FileTransfer in a commercial product or
 * using it in a project deployed on more than 5 computers.
 * The registration requirement is not onerous.
 * You need register only one copy.
 *
 * Make sure you have the latest version from http://mindprod.com.
 *
 * @author  copyright (c) 1997-2003 Roedy Green, Canadian Mind Products
 * Shareware that may be copied and used freely for any purpose but military.
 *
 * version 1.0 1999 Sept 5
 *        - implement download and copy.
 *        - use of readBlocking to replace readFully and read.
 *
 * version 1.1 1999 october 5
 *        - implement upload with PUT, and a status check.
 * version 1.2 1999 October 26
 *        - split into MiniFileTransfer, FileTransfer, and MaxiFileTransfer
 *        - added support for ZipFile entry download.
 * version 1.3 1999 October 28
 *        - add safety code when length specified is -1 or 0, to copy as if the length
 *          were unknown.
 * version 1.4 1999 October 29
 *        - ensure every file closed, including ZipFile
 *        - safety check for null parms.
 * version 1.5 2001 Jan 23
 *        - use more elegant reads in the while loops instead of breaks.
 *
 * Version 1.6 2002 April 22
 *                  - conforming package name.
 */
public class MiniFileTransfer {

	static final boolean DEBUGGING = false;

	static final int BUFFSIZE = 63 * 1024;

	int buffSize;

	/**
	 * constructor
	 *
	 * @param buffSize how big the i/o chunks are to copy files.
	 */
	public MiniFileTransfer(int buffSize) {
		if (buffSize < 512) {
			buffSize = 512;
		}
		this.buffSize = buffSize;
	}

	/**
	 * constructor
	 */
	public MiniFileTransfer() {
		this.buffSize = BUFFSIZE;
	}

	/**
	 * Copy an InputStream to an OutputStream, until EOF.
	 * Use only when you don't know the length of the transfer
	 * ahead of time. Otherwise use FileTransfer.copy.
	 *
	 * @param source InputStream, left closed.
	 *
	 * @param target OutputStream, left closed
	 *
	 * @return true if the copy was successful.
	 */
	public boolean copy(java.io.InputStream source,
			java.io.OutputStream target) {
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}
		try {

			// R E A D / W R I T E by chunks

			int chunkSize = buffSize;
			// code will work even when chunkSize = 0 or chunks = 0;
			// Even for small files, we allocate a big buffer, since we
			// don't know the size ahead of time.
			byte[] ba = new byte[chunkSize];

			// keep reading till hit eof
			int bytesRead;

			while ((bytesRead = readBlocking(source, ba, 0, chunkSize)) > 0) {
				target.write(ba, 0/* offset in ba */, bytesRead/* bytes to write */);
			} // end while

			// C L O S E
			source.close();
			target.close();

		} catch (IOException e) {
			return false;
		}

		// all was ok
		return true;
	} // end copy

	/**
	 * Download a file from a resource in the archive jar file to a local file on hard disk.
	 *
	 * @param source resource as stream e.g. this.class.getResourceAsStream("lyrics.ram");
	 * Netscape interferes with extensions *.exe, *.dll etc.  So use *.ram for your resources.
	 *
	 * @param target new file to be created on local hard disk.
	 *
	 * @return true if the copy was successful.
	 */
	public boolean download(InputStream source, java.io.File target) {
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}
		FileOutputStream os = null;
		InputStream is = source;

		try {

			// O P E N  T A R G E T
			os = new FileOutputStream(target);

			// C O P Y  S O U R C E  T O  T A R G E T
			boolean success = copy(source, os);

			// C L O S E
			// handled by copy.

			return success;

		} catch (IOException e) {
			return false;
		}

	} // end download

	/**
	 * Copy a file from a resource in some a local jar file, not the archive, to a local file on hard disk.
	 * To deal with remote jars in JDK 1.1 download the entire jar, then copy the various
	 * contents to their resting places, then delete the jar.
	 * To deal with remote jars in JDK 1.2 use the new jar URL syntax, that lets you treat
	 * a member as if it were an separate file,
	 * jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class
	 *
	 * @param sourceJar ZipFile e.g. new ZipFile("stuff.jar")
	 * @param zipEntryString fully qualified name of ZipEntry e.g. "com/mindprod/mypack/Stuff.html".
	 * Note this is a String, not a ZipEntry.
	 *
	 * @param target new file to be created on local hard disk.
	 *
	 * @return true if the copy was successful.
	 */
	public boolean copy(ZipFile sourceJar, String zipEntryString, java.io.File target) {
		if (sourceJar == null) {
			return false;
		}
		if (zipEntryString == null) {
			return false;
		}
		if (target == null) {
			return false;
		}

		try {
			ZipEntry zipEntry = sourceJar.getEntry(zipEntryString);

			if (zipEntry == null) {
				return false;
			}

			InputStream is = sourceJar.getInputStream(zipEntry);

			if (is == null) {
				return false;
			}

			boolean success = download(is, target);

			// C L O S E
			// download closes is and target
			sourceJar.close();
			return success;

		} catch (IOException e) {
			return false;
		}

	} // end download

	/**
	 * Reads exactly len bytes from the input stream
	 * into the byte array. This method reads repeatedly from the
	 * underlying stream until all the bytes are read.
	 * InputStream.read is often documented to block like this, but in actuality it
	 * does not always do so, and returns early with just a few bytes.
	 * readBlockiyng blocks until all the bytes are read,
	 * the end of the stream is detected,
	 * or an exception is thrown. You will always get as many bytes as you
	 * asked for unless you get an eof or other exception.
	 * Unlike readFully, you find out how many bytes you did get.
	 *
	 * @param b the buffer into which the data is read.
	 * @param off the start offset of the data in the array,
	 * not offset into the file!
	 * @param len the number of bytes to read.
	 * @return number of bytes actually read.
	 * @exception IOException if an I/O error occurs.
	 *
	 */
	public static final int readBlocking(InputStream in, byte b[], int off, int len) throws IOException {

		int totalBytesRead = 0;
		int bytesRead = 0;

		while (totalBytesRead < len && (bytesRead = in.read(b, off + totalBytesRead, len - totalBytesRead)) >= 0) {
			totalBytesRead += bytesRead;
		}
		return totalBytesRead;
	} // end readBlocking

} // end class MiniFileTransfer

