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

import robocode.robotinterfaces.IBasicEvents;
import robocode.*;

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public class R80obotPeerDisplay extends R70obotPeerRobot implements IDisplayRobotPeer {
    public void d_kill(){
        //TODO ZAMO synchronize
    }

    public void d_setScan(boolean v) {
        //TODO ZAMO
        setScan(v);
    }

    public void d_setDuplicate(int d) {
        //TODO ZAMO forward to setDup
        setDuplicate(d);
    }

    public void onInteractiveEvent(robocode.Event e){
        //TODO ZAMO, synchronization issue
        getDisplayEventManager().add(e);
    }

    public void onPaint(Graphics2D g){
        //TODO ZAMO, security hole ?
        //TODO ZAMO, synchronization issue ?
        IBasicEvents listener = getRobot().getBasicEventListener();
        if (listener != null) {
            listener.onPaint(g);
        }
    }
}
