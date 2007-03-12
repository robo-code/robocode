package roborumble.netengine;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;


/**
 * copy or download a file.
 * To read or write from the client's local hard disk, you will need a signed Applet and
 * security clearance. see Signed Applet in the Java glossary.
 * To read a files from the server, the file must be given public read access, usually
 * the default.
 * To write a file to the server, you server will have to support CGI-PUT with public access.
 * This is unusual to find.  Normally you upload files with FTP.  See FTP in the Java glossary.
 *
 * @author  copyright (c) 1999-2003 Roedy Green, Canadian Mind Products
 * may be copied and used freely for any purpose but military.
 *
 * Roedy Green
 * Canadian Mind Products
 * #327 - 964 Heywood Avenue
 * Victoria, BC Canada V8V 2Y5
 * tel: (250) 361-9093
 * mailto:roedy@mindprod.com
 * http://mindprod.com
 *
 * Version 1.2 1999 Oct 26
 *                  - split off from FileTransfer
 * Version 1.6 2002 April 22
 *                  - conforming package name.
 */
public class FileTransfer extends MiniFileTransfer {

	/**
	 * constructor
	 *
	 * @param buffSize how big the i/o chunks are to copy files.
	 */
	public FileTransfer(int buffSize) {
		super(buffSize);

	}

	/**
	 * constructor
	 */
	public FileTransfer() {
		super();
	}

	/**
	 * Copy an InputStream to an OutputStream.
	 *
	 * @param source InputStream, left closed.
	 *
	 * @param target OutputStream, left closed.
	 *
	 * @param length how many bytes to copy, -1 if you don't know.
	 *
	 * @return true if the copy was successful.
	 */
	public boolean copy(java.io.InputStream source,
			java.io.OutputStream target,
			long length) {

		if (length <= 0) {
			return copy(source, target);
		}
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}
		try {

			// R E A D / W R I T E by chunks

			int chunkSize = (int) Math.min(buffSize, length);
			long chunks = length / chunkSize;
			int lastChunkSize = (int) (length % chunkSize);
			// code will work even when chunkSize = 0 or chunks = 0;
			byte[] ba = new byte[chunkSize];

			for (long i = 0; i < chunks; i++) {
				int bytesRead = readBlocking(source, ba, 0, chunkSize);

				if (bytesRead != chunkSize) {
					throw new IOException();
				}
				target.write(ba);
			} // end for

			// R E A D / W R I T E last chunk, if any
			if (lastChunkSize > 0) {
				int bytesRead = readBlocking(source, ba, 0/* offset in ba */, lastChunkSize);

				if (bytesRead != lastChunkSize) {
					throw new IOException();
				}
				target.write(ba, 0/* offset in ba */, lastChunkSize/* bytes to write */);
			} // end if

			// C L O S E
			source.close();
			target.close();
			return true;

		} catch (IOException e) {
			return false;
		}

	} // end copy

	/**
	 * Copy a file from one spot on hard disk to another.
	 *
	 * @param source file to copy on local hard disk.
	 *
	 * @param target new file to be created on local hard disk.
	 *
	 * @return true if the copy was successful.
	 */
	public boolean copy(java.io.File source, java.io.File target) {
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}

		FileInputStream is = null;
		FileOutputStream os = null;

		try {
			// O P E N
			is = new FileInputStream(source);
			os = new FileOutputStream(target);

			// C O P Y  S O U R C E  T O  T A R G E T
			long fileLength = source.length();
			boolean success = copy(is, os, fileLength);

			// C L O S E
			// handled by inner copy
			return success;

		} catch (IOException e) {
			return false;
		}
	} // end copy

	/**
	 * Copy a file from a remote URL to a local file on hard disk.
	 *
	 * @param source remote URL to copy. e.g.
	 * new URL("http://www.billabong.com:80/songs/lyrics.txt")
	 *
	 * @param target new file to be created on local hard disk.
	 *
	 * @return true if the copy was successful.
	 */
	public boolean download(java.net.URL source, java.io.File target) {
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}
		URLConnection urlc = null;
		InputStream is = null;
		FileOutputStream os = null;

		try {

			// O P E N  S O U R C E
			urlc = source.openConnection();
			// The following line is there to allow downloads from the Robocode Repository
			urlc.setRequestProperty("Referer",
					"http://www.robocoderepository.com/BotSearch.jsp?botName=&authorName=&uploadDate=");
			urlc.setAllowUserInteraction(false);
			urlc.setDoInput(true);
			urlc.setDoOutput(false);
			urlc.setUseCaches(false);
			urlc.connect();
			long length = urlc.getContentLength(); // -1 if not available

			is = urlc.getInputStream();

			// O P E N  T A R G E T
			os = new FileOutputStream(target);

			// C O P Y  S O U R C E  T O  T A R G E T
			boolean success = copy(is, os, length);

			// C L O S E
			// handled by copy
			return success;

		} catch (IOException e) {
			return false;
		}
	} // end download

} // end class FileTransfer

