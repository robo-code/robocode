/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;


import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Pavel Savara (original)
 */
public class RobotPeerSync {

    private final Object syncRoot = new Object();
    private final ReentrantLock syncLock = new ReentrantLock();
    private int read = 0;
    private int write = 0;

    public final Object getSyncRoot() {
        return syncRoot;
    }

    public final void lockRead() {
        syncLock.lock();
        read++;
    }

    public final void lockWrite() {
        syncLock.lock();
        write++;
    }

    public final void unlockRead() {
        read--;
        syncLock.unlock();
    }

    public final void unlockWrite() {
        write--;
        syncLock.unlock();
    }

    public final void checkReadLock() {
        if (!syncLock.isHeldByCurrentThread()) {
            throw new RuntimeException("not locked for reading");
        }
        if (read <= 0 && write <= 0) {
            throw new RuntimeException("bad lock type read");
        }
    }

    public final void checkWriteLock() {
        if (!syncLock.isHeldByCurrentThread()) {
            throw new RuntimeException("not locked for writing");
        }
        if (write <= 0) {
            throw new RuntimeException("bad lock type write");
        }
    }

    public final void checkNoLock() {
        if (syncLock.isHeldByCurrentThread()) {
            throw new RuntimeException("should not lock");
        }
    }

}
