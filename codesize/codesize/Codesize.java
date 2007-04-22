/*******************************************************************************
 * Copyright (c) 2002, 2007 Christian D. Schnell, Flemming N. Larsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Christian D. Schnell
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Changed to a dynamic buffer when processing .jar files based on the size
 *       of the .jar file
 *     - Added the getByteArrayOutputStream() as helper method
 *     - Extended the processZipFile(File, ZipInputStream) to take nested .jar
 *       files into account
 *     - Added Javadoc comments to all classes and methods
 *******************************************************************************/
package codesize;


import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.bcel.classfile.*;


/**
 * Codesize is a tool for calculating the code size of a Java class file or Java
 * archive (JAR).
 *
 * @author Christian D. Schnell (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Codesize {
	private final static int DEFAULT_BUFFER_SIZE = 512 * 1024; // 512 KB

	private static boolean verbose;

	private Codesize() {}

	/**
	 * Container which keeps information extracted by Codesize.
	 *
	 * @author Christian D. Schnell
	 * 
	 * @see Codesize#processClassFile(File)
	 * @see Codesize#processDirectory(File)
	 * @see Codesize#processZipFile(File)
	 * @see Codesize#processZipFile(File, ZipInputStream)
	 */
	public static class Item implements Comparable {
		private File location;
		private int nClassFiles, ttlClassSize, ttlCodeSize;

		Item(File location, int nClassFiles, int ttlClassSize, int ttlCodeSize) {
			this.location = location;
			this.nClassFiles = nClassFiles;
			this.ttlClassSize = ttlClassSize;
			this.ttlCodeSize = ttlCodeSize;
		}

		/**
		 * Returns the file location of the item.
		 *
		 * @return a File containing the file location
		 */
		public File getLocation() {
			return location;
		}

		/**
		 * Returns the number of found class files.
		 *
		 * @return the number of found class files
		 */
		public int getNClassFiles() {
			return nClassFiles;
		}

		/**
		 * Returns the total size of all found class files.
		 *
		 * @return the total size of all found class files
		 */
		public int getClassSize() {
			return ttlClassSize;
		}

		/**
		 * Returns the total code size of all found class files.
		 *
		 * @return the total code size of all found class files
		 */
		public int getCodeSize() {
			return ttlCodeSize;
		}

		/**
		 * Compares this item with another item based on their code sizes.
		 *
		 * @param item the item to be compared 
		 *
		 * @return a negative integer, zero, or a positive integer as this
		 * item is less than, equal to, or greater than the specified item. 
		 */
		public int compareTo(Object item) {
			return ttlCodeSize - ((Item) item).ttlCodeSize;
		}
	}

	/**
	 * Processes the arguments given from a command line.
	 *
	 * @param args the arguments given from the command line
	 *
	 * @return a list of retrived Codesize items
	 */
	private static List processCmdLine(String args[]) {
		ArrayList result = new ArrayList();

		File file;
		Item item;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v")) {
				verbose = true;
			} else if (args[i].equals("-r")) {
				File repository = new File(args[++i]);
				String files[] = repository.list();

				for (int j = 0; j < files.length; j++) {
					file = new File(repository, files[j]);
					if (files[j].toLowerCase().endsWith(".class")) {
						item = processClassFile(file);
					} else {
						item = processZipFile(file);
					}
					if (item != null) {
						result.add(item);
					}
				}
			} else {
				file = new File(args[i]);
				if (file.isDirectory()) {
					item = processDirectory(file);
				} else if (args[i].toLowerCase().endsWith(".class")) {
					item = processClassFile(file);
				} else {
					item = processZipFile(file);
				}
				if (item != null) {
					result.add(item);
				}
			}
		}

		Collections.sort(result);
		return result;
	}

	/**
	 * Adds all class files that exists under the specified directory and all
	 * it's subdirectories to the specified list of class files.
	 *
	 * @param directory the directory containing the class files to add
	 * @param result the list to add all found class files to
	 */
	private static void deepListClassFiles(File directory, List result) {
		String files[] = directory.list();

		for (int i = 0; i < files.length; i++) {
			File file = new File(directory, files[i]);

			if (file.isDirectory()) {
				deepListClassFiles(file, result);
			} else if (files[i].toLowerCase().endsWith(".class")) {
				result.add(file);
			}
		}
	}

	/**
	 * Returns the filename of the specified file. 
	 *
	 * @param file the file to extract the filename from
	 *
	 * @return the filename of the specified file 
	 */
	private static String stripFilename(File file) {
		String result = file.toString();

		if (result.indexOf(File.separator) > -1) {
			result = result.substring(result.lastIndexOf(File.separator) + 1);
		}
		return result;
	}

	/**
	 * Prints out the help information for Codesize.
	 */
	private static void help() {
		Package p = Codesize.class.getPackage();

		System.out.println(
				p.getImplementationTitle() + " " + p.getImplementationVersion()
				+ " - http://user.cs.tu-berlin.de/~lulli/codesize/");
		System.out.println("SYNTAX:");
		System.out.println();
		System.out.println("  codesize [-v] [<class-file> | <zip-file> | <directory> | -r <repository>]+");
		System.out.println();
		System.out.println("- <class-file> is a single .class file");
		System.out.println("- <zip-file> is a zip compressed file (or a .jar file)");
		System.out.println("- <directory> is treated like an uncompressed <zip-file>,");
		System.out.println("  recursively processing any subdirectories");
		System.out.println("- <repository> is a directory like '<robocode>/robots':");
		System.out.println("  - any class file in it is treated like a <class-file>");
		System.out.println("  - any zip file in it is treated like a <zip-file>");
		System.out.println("  - any subdirectory is ignored (can't distinguish different robots here)");
		System.out.println("- specify -v for verbose output");
	}

	/**
	 * Returns the code size of a class from an InputStream.
	 *
	 * @param inputStream the input stream of the class
	 * @param filename the filename of the class
	 *
	 * @return the code size of the class
	 *
	 * @throws IOException
	 */
	private static int processClassInputStream(InputStream inputStream, String filename) throws IOException {
		int result = 0;

		ClassParser classParser = new ClassParser(inputStream, filename);
		Method methods[] = classParser.parse().getMethods();

		for (int i = 0; i < methods.length; i++) {
			Code code = methods[i].getCode();

			if (code != null) {
				result += code.getCode().length;
			}
		}

		if (verbose) {
			System.out.println(filename + " code size: " + result);
		}

		return result;
	}

	/**
	 * Extracts code size information for a class file.
	 *
	 * @param classFile the filename of the class file
	 *
	 * @return the extracted Codesize information for the class file
	 */
	public static Item processClassFile(File classFile) {
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(classFile));

			try {
				return new Item(classFile, 1, (int) classFile.length(),
						processClassInputStream(inputStream, classFile.getName()));
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			System.err.println("Ignoring " + stripFilename(classFile) + ": " + e.getMessage());
		}

		return null;
	}

	/**
	 * Extracts code size information for a directory.
	 *
	 * @param directory the filename of the directory
	 *
	 * @return the extracted Codesize information about the directory
	 */
	public static Item processDirectory(File directory) {
		int ttlClassSize = 0, ttlCodeSize = 0;

		ArrayList classFiles = new ArrayList();

		deepListClassFiles(directory, classFiles);

		for (int i = 0; i < classFiles.size(); i++) {
			Item item = processClassFile((File) classFiles.get(i));

			ttlClassSize += item.ttlClassSize;
			ttlCodeSize += item.ttlCodeSize;
		}

		return new Item(directory, classFiles.size(), ttlClassSize, ttlCodeSize);
	}

	/**
	 * Extracts code size information for a zip file.
	 *
	 * @param zipFile the filename of the zip file
	 *
	 * @return the extracted Codesize information for the zip file
	 */
	public static Item processZipFile(File zipFile) {
		if (verbose) {
			System.out.println("Processing zip file " + zipFile.getName());
		}

		try {
			ZipInputStream inputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));

			try {
				return processZipFile(zipFile, inputStream);
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			System.err.println("Ignoring " + stripFilename(zipFile) + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * Extracts code size information for a zip file given a ZipInputStream.
	 *
	 * @param zipFile the filename of the zip file
	 * @param inputStream the input stream of the zip file
	 *
	 * @return the extracted Codesize information for the zip file
	 */
	public static Item processZipFile(File zipFile, ZipInputStream inputStream)
		throws IOException {
		int nClassFiles = 0, ttlClassSize = 0, ttlCodeSize = 0;

		ZipEntry zipEntry;

		while ((zipEntry = inputStream.getNextEntry()) != null) {
			String lcName = zipEntry.getName().toLowerCase();

			if (lcName.endsWith(".class")) {
				ByteArrayOutputStream baos = getByteArrayOutputStream(inputStream, (int) zipFile.length());

				ttlCodeSize += processClassInputStream(new ByteArrayInputStream(baos.toByteArray()), zipEntry.getName());
				ttlClassSize += baos.size();
				nClassFiles++;
			} else if (lcName.endsWith(".jar")) {
				ByteArrayOutputStream baos = getByteArrayOutputStream(inputStream, (int) zipFile.length());
				ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));

				try {
					Item item = processZipFile(zipFile, zis);

					ttlCodeSize += item.ttlCodeSize;
					ttlClassSize += item.ttlClassSize;
				} finally {
					zis.close();
				}
			}
		}

		if (ttlCodeSize == 0) {
			throw new IOException("total code size is 0");
		}

		return new Item(zipFile, nClassFiles, ttlClassSize, ttlCodeSize);
	}

	/**
	 * Reads all bytes from ZipInputStream and returns a ByteArrayOutputStream
	 * containing all read bytes that can be read.
	 *
	 * @param zis the ZipInputStream to read from
	 * @param length the length of the ZipInputStream, or -1 if the length is
	 *    unknown
	 * @return a ByteArrayOutputStream containing all bytes from the
	 *    ZipInputStream
	 * @throws IOException
	 */
	private static ByteArrayOutputStream getByteArrayOutputStream(ZipInputStream zis, int length) throws IOException {
		if (length < 0) {
			length = DEFAULT_BUFFER_SIZE;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nRead;

		byte buf[] = new byte[length];

		while ((nRead = zis.read(buf, 0, length)) > -1) {
			baos.write(buf, 0, nRead);
		}

		return baos;
	}

	/**
	 * Dumps a list of Codesize items to the specified PrintStream.
	 *
	 * @param items the list of items to print out
	 * @param target the PrintStream to print the items to
	 */
	public static void dump(List items, PrintStream target) {
		target.println("\tCode\tClass\tClass");
		target.println("Nr\tsize\tsize\tfiles\tLocation");
		target.println("--------------------------------------------------------------------");

		for (int i = 0; i < items.size(); i++) {
			Item item = (Item) items.get(i);

			target.println(
					"" + (i + 1) + "\t" + item.ttlCodeSize + "\t" + item.ttlClassSize + "\t" + item.nClassFiles + "\t"
					+ stripFilename(item.location));
		}
	}

	/**
	 * The main entry for running the Codesize tool from the command line.
	 *
	 * @param args the arguments given from the command line
	 */
	public static void main(String args[]) {
		List items = processCmdLine(args);

		if (items.size() == 0) {
			help();
		} else {
			dump(items, System.out);
		}
	}
}
