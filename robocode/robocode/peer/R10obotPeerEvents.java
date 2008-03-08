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

import robocode.peer.robot.*;
import robocode.*;

import java.util.List;
import java.io.Serializable;
import java.io.IOException;

/**
 * @author Pavel Savara (original)
 */
public class R10obotPeerEvents extends R01obotPeerSynchronization {

    private EventManager eventManager;
    private RobotMessageManager messageManager;


    protected void setEventManager(EventManager eventManager) {
        this.eventManager=eventManager;
    }

    protected void setMessageManager(RobotMessageManager messageManager) {
        this.messageManager=messageManager;
    }

    
    //////////////////////////////////////
    // Display access 
    //////////////////////////////////////

    public IDisplayEventManager getDisplayEventManager() {
        return eventManager;
    }

    //////////////////////////////////////
    // Battle access 
    //////////////////////////////////////
    public IBattleEventManager getBattleEventManager() {
        return eventManager;
    }

    //////////////////////////////////////
    // Robot access
    //////////////////////////////////////

    public IRobotEventManager getRobotEventManager() {
        return eventManager;
    }

    public RobotMessageManager getMessageManager() {
        return messageManager;
    }



    public void setInterruptible(boolean interruptable) {
        getRobotEventManager().setInterruptible(getRobotEventManager().getCurrentTopEventPriority(), interruptable);
    }

    public void setEventPriority(String eventClass, int priority) {
        getRobotEventManager().setEventPriority(eventClass, priority);
    }

    public int getEventPriority(String eventClass) {
        return getRobotEventManager().getEventPriority(eventClass);
    }

    public void removeCustomEvent(Condition condition) {
        getRobotEventManager().removeCustomEvent(condition);
    }

    public void addCustomEvent(Condition condition) {
        getRobotEventManager().addCustomEvent(condition);
    }

    public void clearAllEvents() {
        getRobotEventManager().clearAllEvents(false);
    }

    public List<Event> getAllEvents() {
        return getRobotEventManager().getAllEvents();
    }

    public List<BulletMissedEvent> getBulletMissedEvents() {
        return getRobotEventManager().getBulletMissedEvents();
    }

    public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
        return getRobotEventManager().getBulletHitBulletEvents();
    }

    public List<BulletHitEvent> getBulletHitEvents() {
        return getRobotEventManager().getBulletHitEvents();
    }

    public List<HitByBulletEvent> getHitByBulletEvents() {
        return getRobotEventManager().getHitByBulletEvents();
    }

    public List<HitRobotEvent> getHitRobotEvents() {
        return getRobotEventManager().getHitRobotEvents();
    }

    public List<HitWallEvent> getHitWallEvents() {
        return getRobotEventManager().getHitWallEvents();
    }

    public List<RobotDeathEvent> getRobotDeathEvents() {
        return getRobotEventManager().getRobotDeathEvents();
    }

    public List<ScannedRobotEvent> getScannedRobotEvents() {
        return getRobotEventManager().getScannedRobotEvents();
    }
    
    public List<MessageEvent> getMessageEvents() {
        return getRobotEventManager().getMessageEvents();
    }

    public void sendMessage(String name, Serializable message) throws IOException {
        if (getMessageManager() == null) {
            throw new IOException("You are not on a team.");
        }
        getMessageManager().sendMessage(name, message);
    }

    public void broadcastMessage(Serializable message) throws IOException {
        if (getMessageManager() == null) {
            throw new IOException("You are not on a team.");
        }
        getMessageManager().sendMessage(null, message);
    }


}
