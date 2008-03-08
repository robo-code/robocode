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

import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.data.RobotPeerLast;
import robocode.peer.data.RobotPeerSync;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Pavel Savara (original)
 */
public class R01obotPeerSynchronization extends RobotPeerSync {
    //private ReentrantReadWriteLock syncRoot=new ReentrantReadWriteLock();
    public RobotPeerSync info;
    /*
    public final void lockRead(){
        info.lockRead();
    }

    public final void lockWrite(){
        info.lockWrite();
    }

    public final void unlockRead(){
        info.unlockRead();
    }

    public final void unlockWrite(){
        info.unlockWrite();
    }

    public final void checkReadLock(){
        info.checkReadLock();
    }

    public final void checkWriteLock(){
        info.checkWriteLock();
    }

    public final void checkNoLock(){
        info.checkNoLock();
    }
    */
}
