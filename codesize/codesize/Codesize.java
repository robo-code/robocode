package codesize;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import de.fub.bytecode.classfile.*;

public class Codesize
{
  static boolean verbose;
  static byte buf[] = new byte[2048];
  static ByteArrayOutputStream bufOutputStream = new ByteArrayOutputStream();

  Codesize() {}

  public static class Item implements Comparable {
    File location;
    int nClassFiles, ttlClassSize, ttlCodeSize;

    Item(File location, int nClassFiles, int ttlClassSize, int ttlCodeSize) {
      this.location = location;
      this.nClassFiles = nClassFiles;
      this.ttlClassSize = ttlClassSize;
      this.ttlCodeSize = ttlCodeSize;
    }

    public File getLocation() { return location; }
    public int getNClassFiles() { return nClassFiles; }
    public int getClassSize() { return ttlClassSize; }
    public int getCodeSize() { return ttlCodeSize; }
    public int compareTo(Object o) { return ttlCodeSize - ((Item)o).ttlCodeSize; }
  }

  static ArrayList processCmdLine(String args[])
  {
    ArrayList result = new ArrayList();

    File file;
    Item item;

    for(int i=0; i<args.length; i++) {
      if(args[i].equals("-v")) {
        verbose = true;
      } else if(args[i].equals("-r")) {
        File repository = new File(args[++i]);
        String files[] = repository.list();
        for(int j=0; j<files.length; j++) {
          file = new File(repository, files[j]);
          if(files[j].toLowerCase().endsWith(".class"))
            item = processClassFile(file);
          else
            item = processZipFile(file);
          if(item != null)
            result.add(item);
        }
      } else {
        file = new File(args[i]);
        if(file.isDirectory())
          item = processDirectory(file);
        else if(args[i].toLowerCase().endsWith(".class"))
          item = processClassFile(file);
        else
          item = processZipFile(file);
        if(item != null)
          result.add(item);
      }
    }

    Collections.sort(result);
    return result;
  }

  static void deepListClassFiles(File directory, ArrayList result)
  {
    String files[] = directory.list();
    for(int i=0; i<files.length; i++) {
      File file = new File(directory, files[i]);
      if(file.isDirectory())
        deepListClassFiles(file, result);
      else if(files[i].toLowerCase().endsWith(".class"))
        result.add(file);
    }
  }

  static String stripFilename(File file)
  {
    String result = file.toString();
    if(result.indexOf(File.separator) > -1)
      result = result.substring(result.lastIndexOf(File.separator) + 1);
    return result;
  }

  static void help()
  {
    Package p = Codesize.class.getPackage();
    System.err.println(
      p.getImplementationTitle() + " " +
      p.getImplementationVersion() +
      " - http://user.cs.tu-berlin.de/~lulli/codesize/"
    );
    System.err.println("SYNTAX:");
    System.err.println();
    System.err.println("  codesize [-v] [<class-file> | <zip-file> | <directory> | -r <repository>]+");
    System.err.println();
    System.err.println("- <class-file> is a single .class file");
    System.err.println("- <zip-file> is a zip compressed file (or a .jar file)");
    System.err.println("- <directory> is treated like an uncompressed <zip-file>,");
    System.err.println("  recursively processing any subdirectories");
    System.err.println("- <repository> is a directory like '<robocode>/robots':");
    System.err.println("  - any class file in it is treated like a <class-file>");
    System.err.println("  - any zip file in it is treated like a <zip-file>");
    System.err.println("  - any subdirectory is ignored (can't distinguish different robots here)");
    System.err.println("- specify -v for verbose output");
  }

  static int processClassInputStream(InputStream inputStream, String filename)
    throws IOException
  {
    int result = 0;

    ClassParser classParser = new ClassParser(inputStream, filename);
    Method methods[] = classParser.parse().getMethods();
    for(int i=0; i<methods.length; i++) {
      Code code = methods[i].getCode();
      if(code != null)
        result += code.getCode().length;
    }

    if(verbose)
      System.out.println(filename + " code size: " + result);

    return result;
  }

  public static Item processClassFile(File classFile)
  {
    try {
      InputStream inputStream = new BufferedInputStream(new FileInputStream(classFile));
      try {
        return new Item(
          classFile,
          1,
          (int)classFile.length(),
          processClassInputStream(inputStream, classFile.getName())
        );
      } finally {
        inputStream.close();
      }
    } catch(IOException e) {
      System.err.println("Ignoring " + stripFilename(classFile) + ": " + e.getMessage());
    }

    return null;
  }

  public static Item processDirectory(File directory)
  {
    int ttlClassSize = 0, ttlCodeSize = 0;

    ArrayList classFiles = new ArrayList();
    deepListClassFiles(directory, classFiles);

    for(int i=0; i<classFiles.size(); i++) {
      Item item = processClassFile((File)classFiles.get(i));
      ttlClassSize += item.ttlClassSize;
      ttlCodeSize += item.ttlCodeSize;
    }

    return new Item(directory, classFiles.size(), ttlClassSize, ttlCodeSize);
  }

  public static Item processZipFile(File zipFile)
  {
    if(verbose)
      System.out.println("Processing zip file " + zipFile.getName());

    try {
      ZipInputStream inputStream = new ZipInputStream(
        new BufferedInputStream(new FileInputStream(zipFile))
      );
      try {
        return processZipFile(zipFile, inputStream);
      } finally {
        inputStream.close();
      }
    } catch(IOException e) {
      System.err.println("Ignoring " + stripFilename(zipFile) + ": " + e.getMessage());
    }
    return null;
  }

  public static Item processZipFile(File zipFile, ZipInputStream inputStream)
    throws IOException
  {
    int nClassFiles = 0, ttlClassSize = 0, ttlCodeSize = 0;

    ZipEntry zipEntry;
    while((zipEntry = inputStream.getNextEntry()) != null) {
      if(zipEntry.getName().toLowerCase().endsWith(".class")) {
      bufOutputStream.reset();
        int nRead;
        while((nRead = inputStream.read(buf, 0, buf.length)) > -1)
          bufOutputStream.write(buf, 0, nRead);

        ttlCodeSize += processClassInputStream(
          new ByteArrayInputStream(bufOutputStream.toByteArray()),
          zipEntry.getName()
        );
        ttlClassSize += bufOutputStream.size();
        nClassFiles++;
      }
    }

    if(ttlCodeSize == 0)
      throw new IOException("total code size is 0");

    return new Item(zipFile, nClassFiles, ttlClassSize, ttlCodeSize);
  }

  public static void dump(ArrayList items, PrintStream target)
  {
    target.println("\tCode\tClass\tClass");
    target.println("Nr\tsize\tsize\tfiles\tLocation");
    target.println("--------------------------------------------------------------------");

    for(int i=0; i<items.size(); i++) {
      Item item = (Item)items.get(i);
      target.println(
        "" + (i+1) +
        "\t" + item.ttlCodeSize +
        "\t" + item.ttlClassSize +
        "\t" + item.nClassFiles +
        "\t" + stripFilename(item.location)
      );
    }
  }

  public static void main(String args[])
  {
    ArrayList items = processCmdLine(args);
    if(items.size() == 0)
      help();
    else
      dump(items, System.out);
  }
}
