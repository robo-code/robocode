/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;

import java.io.*;
import java.util.Vector;

import robocode.peer.RobotPeer;
import robocode.RobocodeFileOutputStream;
import robocode.util.*;
/**
 * Insert the type's description here.
 * Creation date: (9/27/2001 6:56:34 PM)
 * @author: Administrator
 */
public class RobotFileSystemManager {
	RobotPeer robotPeer;
	public long quotaUsed = 0;
	public boolean quotaMessagePrinted = false;
	public Vector streams = new Vector();
	long maxQuota = 0;
/**
 * RobotFileSystemHandler constructor comment.
 */
public RobotFileSystemManager(RobotPeer robotPeer, long maxQuota) {
	this.robotPeer = robotPeer;
	this.maxQuota = maxQuota;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized void addStream(RobocodeFileOutputStream s) throws IOException {
	if (s == null)
			throw new SecurityException("You may not add a null stream.");
	if (!streams.contains(s))
	{
		if (streams.size() < 5)
			streams.add(s);
		else
			throw new IOException("You may only have 5 streams open at a time.\n Make sure you call close() on your streams when you are finished with them.");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized void adjustQuota(long len) throws IOException
{
	quotaUsed += len;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized void checkQuota() throws IOException
{
	checkQuota(0);
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized void checkQuota (long numBytes) throws IOException
{
	if (numBytes < 0)
		throw new IndexOutOfBoundsException("checkQuota on negative numBytes!");
	if (quotaUsed + numBytes <= maxQuota)
	{
		quotaUsed += numBytes;
		return;
	}
	if (!quotaMessagePrinted)
	{
		robotPeer.out.println("SYSTEM: You have reached your filesystem quota of: " + maxQuota + " bytes.");
		quotaMessagePrinted = true;
	}
	throw new IOException("You have reached your filesystem quota of: " + maxQuota + " bytes.");
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized long getMaxQuota()
{
	return maxQuota;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized long getQuotaUsed()
{
	return quotaUsed;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 4:19:41 PM)
 */
public File getReadableDirectory() {
	if (robotPeer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory() == null)
	{
		return null;
	}
	try {
		return new File(robotPeer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory()).getCanonicalFile();
	} catch (java.io.IOException e) {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 4:19:41 PM)
 */
public File getWritableDirectory() {
	if (robotPeer.getRobotClassManager().getRobotClassLoader().getClassDirectory() == null)
	{
		return null;
	}
	try {
		File dir = new File(robotPeer.getRobotClassManager().getRobotClassLoader().getClassDirectory(),robotPeer.getRobotClassManager().getClassNameManager().getShortClassName() + ".data").getCanonicalFile();
		return dir;
	} catch (java.io.IOException e) {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:30:49 PM)
 */
public void initializeQuota() {
	File dataDirectory = getWritableDirectory();
	if (dataDirectory == null)
	{
		//log("Cannot initialize quota, no writable directory.  Writing disabled.");
		quotaUsed = maxQuota;
		return;
	}
	if (!dataDirectory.exists())
	{
		//log("Data directory does not exists, 0 quota used.");
		this.quotaUsed = 0;
		return;
	}
	quotaMessagePrinted = false;
	File[] dataFiles = dataDirectory.listFiles();
	quotaUsed = 0;
	for (int i = 0; i < dataFiles.length; i++)
	{
		quotaUsed += dataFiles[i].length();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 4:19:41 PM)
 */
public boolean isReadable(String fileName) {
	File allowedDirectory = getReadableDirectory();
	if (allowedDirectory == null)
		return false;

	File attemptedFile = null;
	try {
		attemptedFile = new File(fileName).getCanonicalFile();
	} catch (java.io.IOException e) {
		return false;
	}

	//log("Read: Testing if " + attemptedFile.getParent() + " contains " + allowedDirectory);
	if (attemptedFile.getParent().indexOf(allowedDirectory.toString()) == 0)
	{
		String fs = attemptedFile.toString();
		int dataIndex = fs.indexOf(".data",allowedDirectory.toString().length());
		if (dataIndex >= 0)
		{
			if (isWritable(fileName) || attemptedFile.equals(getWritableDirectory()))
				return true;
			else
				throw new java.security.AccessControlException("Preventing " + Thread.currentThread().getName() + " from access to: " + fileName + ": You may not read another robot's data directory.");
		}
		return true;
	}
		
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 4:19:41 PM)
 */
public boolean isWritable(String fileName) {
	File allowedDirectory = getWritableDirectory();
	if (allowedDirectory == null)
		return false;

	File attemptedFile = null;
	try {
		attemptedFile = new File(fileName).getCanonicalFile();
	} catch (java.io.IOException e) {
		return false;
	}

	if (attemptedFile.getParentFile().equals(allowedDirectory))
	{
		return true;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(String s) {
	Utils.log(s);
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(Throwable e) {
	Utils.log(e);
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 6:22:55 PM)
 * @param s robocode.RobocodeFileOutputStream
 */
public synchronized void removeStream(RobocodeFileOutputStream s) {
	if (s == null)
		throw new SecurityException("You may not remove a null stream.");
	if (streams.contains(s))
	{
		streams.remove(s);
	}
}
}
