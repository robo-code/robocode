/*
File Transfer classes to copy, and download files.

 copyright (c) 1999-2007 Roedy Green, Canadian Mind Products
 May be copied and used freely for any purpose but military.
  Roedy Green
  Canadian Mind Products <dr>[withheld] <d9>Victoria, BC
  Canada [withdrawn]
  tel: (250) 361-9093
  mailto:roedyg@mindprod.com
  http://mindprod.com

 version history

1.2 1999 Oct 26 - split off from FileTransfer
1.6 2002 April 22 - conforming package name.
1.9 2006-02-05 reformat with IntelliJ, add Javadoc

*/
package com.mindprod.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * copy or download a file. To read or write from the client's local hard disk,
 * you will need a signed Applet and security clearance. see Signed Applet in
 * the Java glossary. To read a files from the server, the file must be given
 * public read access, usually the default. To write a file to the server, you
 * server will have to support CGI-PUT with public access. This is unusual to
 * find. Normally you upload files with FTP. See FTP in the Java glossary.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.9, 2006-03-05
 */
public class FileTransfer extends MiniFileTransfer {

    // ------------------------------ FIELDS ------------------------------

    /**
     * undisplayed copyright notice
     */
    public static final String embeddedCopyright =
            "copyright (c) 1999-2007 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * embedded version string.
     */
    public static final String versionString = "1.9";

    private static final String releaseDate = "2006-03-06";

    // --------------------------- CONSTRUCTORS ---------------------------

    /**
     * constructor
     */
    public FileTransfer()
        {
        super();
        }

    /**
     * constructor
     *
     * @param buffSize how big the i/o chunks are to copy files.
     */
    public FileTransfer( int buffSize )
        {
        super( buffSize );
        }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Copy a file from one spot on hard disk to another.
     *
     * @param source file to copy on local hard disk.
     * @param target new file to be created on local hard disk.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( File source, File target )
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
            // O P E N
            FileInputStream is = new FileInputStream( source );
            FileOutputStream os = new FileOutputStream( target );

            // C O P Y S O U R C E T O T A R G E T
            long fileLength = source.length();

            // C L O S E
            // handled by inner copy
            return copy( is, os, fileLength, true );
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end copy

    /**
     * Copy a file to an outputstream
     *
     * @param source      file to copy on local hard disk.
     * @param target      OutputStream to copy the file to.
     * @param closeTarget true if the target OutputStream should be closed when we are done.
     *                    false if the target OutputStream should be left open for further
     *                    output when done.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( File source, OutputStream target, boolean closeTarget )
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
            // O P E N
            FileInputStream is = new FileInputStream( source );

            // C O P Y S O U R C E T O T A R G E T
            long fileLength = source.length();

            // C L O S E
            // handled by inner copy
            return copy( is, target, fileLength, closeTarget );
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end copy

    /**
     * Copy an InputStream to an OutputStream when you know the length in
     * advance.
     *
     * @param source      InputStream, left closed.
     * @param target      OutputStream, left closed.
     * @param length      how many bytes to copy, -1 if you don't know.
     * @param closeTarget true if you want the the target stream closed when done. false if
     *                    you want to the target stream left open for further output.
     *
     * @return true if the copy was successful.
     */
    public boolean copy( InputStream source,
                         OutputStream target,
                         long length,
                         boolean closeTarget )
        {
        if ( length <= 0 )
            {
            // indeterminate length
            return copy( source, target, closeTarget );
            }
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
            // we know length > 0
            int chunkSize = (int) Math.min( buffSize, length );
            long chunks = length / chunkSize;
            int lastChunkSize = (int) ( length % chunkSize );
            // code will work even when lastChunkSize = 0 or chunks = 0;
            byte[] ba = new byte[ chunkSize ];

            for ( long i = 0; i < chunks; i++ )
                {
                int bytesRead = readBlocking( source, ba, 0, chunkSize );
                if ( bytesRead != chunkSize )
                    {
                    throw new IOException();
                    }
                target.write( ba );
                } // end for

            // R E A D / W R I T E last chunk, if any
            if ( lastChunkSize > 0 )
                {
                int bytesRead = readBlocking( source,
                                              ba,
                                              0
                                              /* offset in ba */,
                                              lastChunkSize );
                if ( bytesRead != lastChunkSize )
                    {
                    throw new IOException();
                    }
                target.write( ba, 0 /* offset in ba */, lastChunkSize /*
                                                                         * bytes
                                                                         * to
                                                                         * write
                                                                         */ );
                } // end if

            // C L O S E
            source.close();
            if ( closeTarget )
                {
                target.close();
                }
            return true;
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end copy

    /**
     * Copy a file from a remote URL to a local file on hard disk. To use this
     * method with a file that requires userid/password to access it, see
     * http://mindprod.com/jgloss/authentication.html
     *
     * @param source remote URL to copy. e.g. new
     *               URL("http://www.billabong.com:80/songs/lyrics.txt")
     * @param target new file to be created on local hard disk.
     *
     * @return true if the copy was successful.
     */
    public boolean download( URL source, File target )
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
            // O P E N _ S O U R C E
            URLConnection urlc = source.openConnection();
            if ( urlc == null )
                {
                throw new IOException( "Unable to make a connection." );
                }
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( false );
            urlc.setUseCaches( false );
            urlc.connect();
            long length = urlc.getContentLength(); // -1 if not available
            InputStream is = urlc.getInputStream();

            // O P E N _ T A R G E T
            FileOutputStream os = new FileOutputStream( target );

            // C O P Y _ S O U R C E _ T O _ T A R G E T

            // C L O S E
            // handled by copy
            return copy( is, os, length, true /* close target */ );
            }
        catch ( IOException e )
            {
            return false;
            }
        } // end download

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
                        "FileTransfer is not intended to be run from the command line. See Download." );
        }
} // end class FileTransfer
