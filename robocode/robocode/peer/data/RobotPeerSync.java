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
package robocode.peer.data;

import robocode.Condition;

/**
 * @author Pavel Savara (original)
 */
public class RobotPeerSync extends RobotPeerLast{

    public final double getDistanceRemainingSync() {
        lockRead();
        try{
            return getDistanceRemaining();
        }
        finally {
            unlockRead();
        }
    }

    public final double getVelocitySync() {
        lockRead();
        try{
            return getVelocity();
        }
        finally {
            unlockRead();
        }
    }

    public final double getTurnRemainingSync(){
        lockRead();
        try{
            return getTurnRemaining();
        }
        finally {
            unlockRead();
        }
    }

    public final double getRadarTurnRemainingSync(){
        lockRead();
        try{
            return getRadarTurnRemaining();
        }
        finally {
            unlockRead();
        }
    }

    public final double getGunTurnRemainingSync(){
        lockRead();
        try{
            return getGunTurnRemaining();
        }
        finally {
            unlockRead();
        }
    }


    

    public final void setWaitConditionSync(Condition waitCondition) {
        lockWrite();
        try{
            setWaitCondition(waitCondition);
        }
        finally {
            unlockWrite();
        }
    }

    public final void setScanSync(boolean scan) {
        lockWrite();
        try{
            setScan(scan);
        }
        finally {
            unlockWrite();
        }
    }
}
