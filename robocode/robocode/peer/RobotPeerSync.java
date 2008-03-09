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
public class RobotPeerSync
{

    private final ReentrantLock syncRoot=new ReentrantLock();
    private int read=0;
    private int write=0;

    public final ReentrantLock getSyncRoot(){
        return syncRoot;
    }


    public final void lockRead(){
        syncRoot.lock();
        read++;
    }

    public final void lockWrite(){
        syncRoot.lock();
        write++;
    }

    public final void unlockRead(){
        read--;
        syncRoot.unlock();
    }

    public final void unlockWrite(){
        write--;
        syncRoot.unlock();
    }

    public final void checkReadLock(){
        if (!syncRoot.isHeldByCurrentThread()){
            throw new RuntimeException("bad lock");
        }
        if (read<=0 && write<=0){
            throw new RuntimeException("bad lock type read");
        }
    }

    public final void checkWriteLock(){
        if (!syncRoot.isHeldByCurrentThread()){
            throw new RuntimeException("bad lock");
        }
        if (write<=0){
            throw new RuntimeException("bad lock type write");
        }
    }

    public final void checkNoLock(){
        if (syncRoot.isHeldByCurrentThread()){
            throw new RuntimeException("should not lock");
        }
    }
    

}
