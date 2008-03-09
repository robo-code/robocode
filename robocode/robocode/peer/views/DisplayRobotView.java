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
package robocode.peer.views;

import robocode.peer.IDisplayRobotPeer;
import robocode.robotinterfaces.IBasicEvents;

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public class DisplayRobotView extends ReadingRobotView implements IDisplayRobotView {

    private IDisplayRobotPeer peer;

    public DisplayRobotView(IDisplayRobotPeer peer) {
        super(peer);
        this.peer = peer;
    }

    @Override
    public void cleanup(){
        super.cleanup();
        peer=null;
    }


    public void lockRead() {
        peer.lockRead();
    }

    public void unlockRead() {
        peer.unlockRead();
    }

    public void d_setPaintEnabled(boolean enabled) {
        peer.lockWrite();
        try{
            info.setPaintEnabled(enabled);
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void d_setSGPaintEnabled(boolean enabled) {
        peer.lockWrite();
        try{
            info.setSGPaintEnabled(enabled);
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void d_kill(){
        peer.lockWrite();
        try{
            //TODO ZAMO review again
            peer.getBattleView().b_kill();
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void setScan(boolean v) {
        peer.lockWrite();
        try{
            status.setScan(v);
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void setDuplicate(int d) {
        peer.lockWrite();
        try{
            info.setDuplicate(d);
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void onInteractiveEvent(robocode.Event e){
        peer.lockWrite();
        try{
            peer.getDisplayEventManager().add(e);
        }
        finally {
            peer.unlockWrite();
        }
    }

    public void onPaint(Graphics2D g){
        //Warning: robot is called from UI thread.
        // Security hole ?
        // Synchronization issue for client robot ?
        IBasicEvents listener = peer.getRobot().getBasicEventListener();
        if (listener != null) {
            listener.onPaint(g);
        }
    }

}
