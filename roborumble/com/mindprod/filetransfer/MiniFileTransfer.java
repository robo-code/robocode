package com.mindprod.filetransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * download a file, fishing it out of either this or another jar. To read or
 * write from the client's local hard disk, you will need a signed Applet and
 * security clearance. see Signed Applet in the Java glossary. To read a files
 * from the server, the file must be given public read access, usually the
 * default. If you make more than casual use, please sent a registration fee of
 * $10 either US or Canadian to: FILETRANSFER REGISTRATIONS Roedy Green Canadian
 * Mind Products [withheld] Victoria, BC Canada [withdrawn]
 * mailto:roedyg@mindprod.com http://mindprod.com "More that casual use" would
 * include using FileTransfer in a commercial product or using it in a project
 * deployed on more than 5 computers. The registration requirement is not
 * onerous. You need register only one copy. Make sure you have the latest
 * version from http://mindprod.com.
 *
 * @author copyright (c) 1999-2007 Roedy Green, Canadian Mind Products may be
 *         copied and used freely for any purpose but military. <br>
 *         Roedy Green<br>
 *         Canadian Mind Products <dr>[withheld] <d9>Victoria, BC
 *         Canada [withdrawn]<br>
 *         tel: (250) 361-9093 <br>
 *         mailto:roedyg@mindprod.com<br>
 *         http://mindprod.com <br>
 *         version 1.0 1999 Sept 5 <br>- implement download and copy. <br>-
 *         use of readBlocking to replace readFully and read. <br>
 *         version 1.1 1999 october 5 - implement upload with PUT, and a status
 *         check. <br>
 *         version 1.2 1999 October 26 - split into MiniFileTransfer,
 *         FileTransfer, and MaxiFileTransfer - added support for ZipFile entry
 *         download. version 1.3 <br>
 *         1999 October 28 - add safety code when length specified is -1 or 0,
 *         to copy as if the length were unknown. <br>
 *         version 1.4 1999 October 29 - ensure every file closed, including
 *         ZipFile - safety check for null parms. <br>
 *         version 1.5 2001 Jan 23 - use more elegant reads in the while loops
 *         instead of breaks.<br>
 *         Version 1.6 2002 April 22 - conforming package name. <br>
 *         Version 1.7 2003 September 15 - add closeTarget parameter to many
 *         methods - rename download( InputStream, File ) to copy( InputStream ,
 *         File ) - add copy( File, Outputstream ) Version 1.8 2006-01-10 add
 *         Download class.
 */
public class MiniFileTransfer {

    // ------------------------------ FIELDS ------------------------------

    /**
     * default size of chunks to transfer at a time.
     */
    static final int BUFFSIZE = 63 * 1024;

    /**
     * true if want debugging output
     */
    static final boolean DEBUGGING = false;

    /**
     * size of chunks to transfer at a time.
     */
    int buffSize;

    // -------------------------- STATIC METHODS --------------------------

    /**
     * Reads exactly len bytes from the input stream into the byte array. This
     * method reads repeatedly from the underlying stream until all the bytes
     * are read. InputStream.read is often documented to block like this, but in
     * actuality it does not always do so, and returns early with just a few
     * bytes. readBlocking blocks until all the bytes are read, the end of the
     * stream is detected, or an exception is thrown. You will always get as
     * many bytes as you asked for unless you get an eof or other exception.
     * Unlike readFully, you find out how many bytes you did get.
     *
     * @param in  stream to read
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data in the array, not offset into the
     *            file!
     * @param len the number of bytes to read.
     *
     * @return number of bytes actually read.
     *
     * @throws IOException if an I/O error occurs.
     */
    public static final int readBlocking( InputStream in,
                                          byte b[],
                                          int off,
                                          int len ) throws IOException
        {
        int totalBytesRead = 0;
        int bytesRead = 0;
        while ( totalBytesRead < len
                && ( bytesRead =
                in.read( b, off + totalBytesRead, len - totalBytesRead ) )
                   >= 0 )
            {
            totalBytesRead += bytesRead;
            }
        return totalBytesRead;
        } // end readBlocking

    // --------------------------- CONSTRUCTORS ---------------------------

    /**
     * constructor
     */
    public MiniFileTransfer()
        {
        this.buffSize = BUFFSIZE;
        }

    /**
     * constructor
     *
     * @param buffSize how big the i/o chunks are to copy files.
     */
    public MiniFileTransfer( int buffSize )
        {
        if ( buffSize < 512 )
            {
            buffSize = 512;
            }
        this.buffSize = buffSize;
        }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * copy a file from a stream, typically a resource in the archive jar file
     * to a local file on hard disk.
     *
     * @param source resource as stream e.g.
     *               this.class.getResourceAsStream("lyrics.ram"); Netscape interferes
     *               with extensions *.exe, *.dll etc. So use *.ram for your resources.
     * @param target new file to be created on local hard disk.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( InputStream source, File target )
        {
        if ( source == null )
            {
            return false;
            }
        if ( target == null )
            {
            return false;
            }
        FileOutputStream os = null;
        try
            {
            // O P E N T A R G E T
            os = new FileOutputStream( target );

            // C O P Y S O U R C E T O T A R G E T
            boolean success = copy( source, os, true );

            // C L O S E
            // handled by copy.

            return success;
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end download

    /**
     * Copy an InputStream to an OutputStream, until EOF. Use only when you
     * don't know the length of the transfer ahead of time. Otherwise use
     * FileTransfer.copy.
     *
     * @param source      InputStream, always left closed
     * @param target      OutputStream
     * @param closeTarget true if you want target stream closed when done. false, leave the
     *                    target open for more I/O.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( InputStream source,
                         OutputStream target,
                         boolean closeTarget )
        {
        if ( source == null )
            {
            return false;
            }
        if ( target == null )
            {
            return false;
            }
        try
            {
            // R E A D / W R I T E by chunks

            int chunkSize = buffSize;
            // code will work even when chunkSize = 0 or chunks = 0;
            // Even for small files, we allocate a big buffer, since we
            // don't know the size ahead of time.
            byte[] ba = new byte[ chunkSize ];

            // keep reading till hit eof
            int bytesRead;
            while ( ( bytesRead = readBlocking( source, ba, 0, chunkSize ) )
                    > 0 )
                {
                target.write( ba, 0 /* offset in ba */, bytesRead /*
                                                                     * bytes to
                                                                     * write
                                                                     */ );
                } // end while

            // C L O S E
            source.close();
            if ( closeTarget )
                {
                target.close();
                }
            }
        catch ( IOException e )
            {
            return false;
            }

        // all was ok
        return true;
        } // end copy

    /**
     * Copy a file from a resource in some a local jar file, not the archive, to
     * a local file on hard disk. To deal with remote jars in JDK 1.1 download
     * the entire jar, then copy the various contents to their resting places,
     * then delete the jar. To deal with remote jars in JDK 1.2 use the new jar
     * URL syntax, that lets you treat a member as if it were an separate file,
     * jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class
     *
     * @param sourceJar      ZipFile e.g. new ZipFile("stuff.jar"), left open.
     * @param zipEntryString fully qualified name of ZipEntry e.g.
     *                       "com/mindprod/mypack/Stuff.html". Note this is a String, not a
     *                       ZipEntry.
     * @param target         new file to be created on local hard disk.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( ZipFile sourceJar, String zipEntryString, File target )
        {
        if ( sourceJar == null )
            {
            return false;
            }
        if ( zipEntryString == null )
            {
            return false;
            }
        if ( target == null )
            {
            return false;
            }

        try
            {
            ZipEntry zipEntry = sourceJar.getEntry( zipEntryString );
            if ( zipEntry == null )
                {
                return false;
                }

            InputStream is = sourceJar.getInputStream( zipEntry );
            if ( is == null )
                {
                return false;
                }

            boolean success = copy( is, target );

            // C L O S E
            // download closes is and target
            // don't close entire zip with sourceJar.close();
            return success;
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end copy

    // --------------------------- main() method ---------------------------

    /**
     * dummy main
     *
     * @param args not used source url, target file
     */
    public static void main( String[] args )
        {
        System.err
                .println(
                        "MiniFileTransfer is not intended to be run from the command line. See Download." );
        }
} // end class MiniFileTransfer
