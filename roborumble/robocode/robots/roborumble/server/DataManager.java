import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * by Peter Stromberg aka PEZ
 * Created: September 7 2003
 * 
 * Singleton for reading and writing data for the RoboRumbleAtHome servlets
 *
 * Todo: Actually implement the locking mechanism.
 */

public class DataManager {
    // There can only be one!
    public static final DataManager INSTANCE = new DataManager();

    private static final long READ_WAIT_TIMEOUT = 30; // seconds
    private static final long WRITE_WAIT_TIMEOUT = 30; // seconds

    private Map readingTable = Collections.synchronizedMap(new HashMap()); 
    private Map writingTable = Collections.synchronizedMap(new HashMap()); 

    // There can only be one!
    private DataManager() {
    }

    Properties getData(String path, String identifier) throws DataManagerException, IOException {
	Properties data = new Properties();

	getReadLock(identifier);
        try {
            data.load(new FileInputStream(path + identifier + ".txt"));
        }
        finally {
            releaseReadLock(identifier);
        }

	return data;
    }

    void storeData(Properties data, String path, String identifier, String header) throws DataManagerException, IOException {
	getWriteLock(identifier);
        try {
            data.store(new FileOutputStream(path + identifier +  ".txt"), header);
        }
        finally {
           releaseWriteLock(identifier);
        }
    }

    void appendToLog(String data, String path, String identifier) throws DataManagerException, IOException {
        getWriteLock(identifier);
        try {
            PrintStream outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(path + identifier + ".txt", true)), true);
            outtxt.println(data);
            outtxt.close(); 
        }
        finally {
           releaseWriteLock(identifier);
        }
    }

    String[] getFiles(String path) { return (new File(path)).list(); }

    private synchronized void getWriteLock(String identifier) throws DataManagerException {
	int waits = 0;
	try {
	    while (waits++ < WRITE_WAIT_TIMEOUT * 10) {
                synchronized (readingTable) {
                    synchronized (writingTable) {
                        if ((!isReading(identifier)) && (!isWriting(identifier))) {
                            registerWrite(identifier);
                            break;
                        }
                    }
                }
		Thread.sleep(100);
	    }
	}
	catch (InterruptedException e) {
	    throw new DataManagerException("Interrupted while waiting for permission to write data: " +
		identifier + ". (" + e + ")");
	}
	if (waits >= WRITE_WAIT_TIMEOUT) {
	    throw new DataManagerException("Timeout while waiting for permission to write data: " + identifier);
	}
    }

    private synchronized void releaseWriteLock(String identifier) {
        deregisterWrite(identifier);
    }

    private synchronized void getReadLock(String identifier) throws DataManagerException {
	int waits = 0;
	try {
	    while (waits++ < READ_WAIT_TIMEOUT * 10) {
                synchronized (readingTable) {
                    synchronized (writingTable) {
                        if (!isWriting(identifier)) {
                            registerRead(identifier);
                            break;
                        }
                    }
                }
		Thread.sleep(100);
	    }
	}
	catch (InterruptedException e) {
	    throw new DataManagerException("Interrupted while waiting for permission to read data: " +
		identifier + ". (" + e + ")");
	}
	if (waits >= READ_WAIT_TIMEOUT) {
	    throw new DataManagerException("Timeout while waiting for permission to read data: " + identifier);
	}
    }
    
    private synchronized void releaseReadLock(String identifier) {
        deregisterRead(identifier);
    }

    private synchronized boolean isReading(String identifier) {
        Integer i = (Integer)readingTable.get(identifier);
        if (i==null) return false; else return i.intValue() > 0;
    }

    private synchronized boolean isWriting(String identifier) {
        return writingTable.containsKey(identifier);
    }

    private synchronized void registerWrite(String identifier) {
        writingTable.put(identifier, identifier);
    }

    private synchronized void deregisterWrite(String identifier) {
        writingTable.remove(identifier);
    }

    private synchronized void registerRead(String identifier) {
        if (readingTable.containsKey(identifier)) {
            readingTable.put(identifier, (new Integer(((Integer)readingTable.get(identifier)).intValue() + 1)));
        }
        else {
            //readingTable.put(identifier, new Integer(0));
 	readingTable.put(identifier, new Integer(1));
        }
    }

    private synchronized void deregisterRead(String identifier) {
        int istored = 0;
        Integer Istored = (Integer)readingTable.get(identifier);	
        if (Istored != null) istored = Math.max(Istored.intValue() - 1, 0);
        readingTable.put(identifier, new Integer(istored));
        //readingTable.put(identifier, (new Integer(((Integer)readingTable.get(identifier)).intValue() - 1)));
    }
}
