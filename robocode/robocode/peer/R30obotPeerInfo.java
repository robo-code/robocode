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

import robocode.repository.RobotFileSpecification;
import robocode.manager.NameManager;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.ITeamRobot;
import robocode.robotinterfaces.IInteractiveRobot;
import robocode.peer.robot.RobotMessageManager;
import robocode.Robot;

import java.awt.event.MouseWheelEvent;

/**
 * Set at start never change or very rare, read only, no synchro
 * @author Pavel Savara (original)
 */
public class R30obotPeerInfo extends R20obotPeerComponents{
    private IBasicRobot robot;

    private boolean interactiveTested=false;
    private boolean isInteractive;
    private boolean isDuplicate;
    private boolean isJuniorRobot;
    private boolean isInteractiveRobot;
    private boolean isAdvancedRobot;
    private boolean isTeamRobot;
    private boolean isDroid;
    private boolean isIORobot;
    private boolean paintEnabled;
    private boolean sgPaintEnabled;
    private String name;
    private String shortName;
    private String nonVersionedName;

    public void initInfo(RobotFileSpecification rfs){
        isJuniorRobot=rfs.isJuniorRobot();
        isInteractiveRobot=rfs.isInteractiveRobot();
        isAdvancedRobot=rfs.isAdvancedRobot();
        isTeamRobot=rfs.isTeamRobot();
        isDroid=rfs.isDroid();
    }

    public void setDuplicate(int count) {
        isDuplicate = true;

        String countString = " (" + (count + 1) + ')';

        NameManager cnm = getRobotClassManager().getClassNameManager();

        name = cnm.getFullClassNameWithVersion() + countString;
        shortName = cnm.getUniqueShortClassNameWithVersion() + countString;
        nonVersionedName = cnm.getFullClassName() + countString;
    }

    public void setRobot(IBasicRobot newRobot) {
        robot = newRobot;
        if (robot != null) {
            if (robot instanceof ITeamRobot) {
                setMessageManager(new RobotMessageManager((RobotPeer)this));
            }
            getBattleEventManager().setRobot(newRobot);
        }
    }

    protected void setIORobot(boolean ioRobot) {
        this.isIORobot = ioRobot;
    }

    public void setPaintEnabled(boolean enabled) {
        paintEnabled = enabled;
    }

    public void setSGPaintEnabled(boolean enabled) {
        sgPaintEnabled = enabled;
    }


    protected IBasicRobot getRobot() {
        return robot;
    }



    public boolean isPaintEnabled() {
        return paintEnabled;
    }

    public boolean isSGPaintEnabled() {
        return sgPaintEnabled;
    }

    public boolean isIORobot() {
        return isIORobot;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public boolean isDroid() {
        return isDroid;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IJuniorRobot}; <code>false</code> otherwise.
     */
    public boolean isJuniorRobot() {
        return isJuniorRobot;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IInteractiveRobot}; <code>false</code> otherwise.
     */
    public boolean isInteractiveRobot() {
        return isInteractiveRobot;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IAdvancedRobot}; <code>false</code> otherwise.
     */
    public boolean isAdvancedRobot() {
        return isAdvancedRobot;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.ITeamRobot}; <code>false</code> otherwise.
     */
    public boolean isTeamRobot() {
        return isTeamRobot;
    }

    /**
     * Robot class is forcing IInteractiveEvents into robot inheritance tree.
     * This method is optimizing it away in case that inherited "client" robot
     * is not overriding any of interactive methods/event handlers
     * @return true if the robot is IInteractiveRobot not inherited from Robot or
     * is Robot descendant with any interactive handlers overriden
     */
    public boolean isInteractiveListener() {
        if (!interactiveTested) {
            interactiveTested=true;

            if (!isInteractiveRobot()){
                isInteractive = false;
                return isInteractive;
            }
            IInteractiveRobot r=(IInteractiveRobot)getRobot();
            if (r==null){
                isInteractive = false;
                return isInteractive;
            }
            if (!Robot.class.isAssignableFrom(r.getClass())){
                isInteractive = true;
                return isInteractive;
            }
            isInteractive = testInteractiveHandler(r, "onKeyPressed", java.awt.event.KeyEvent.class)
                    || testInteractiveHandler(r, "onKeyReleased", java.awt.event.KeyEvent.class)
                    || testInteractiveHandler(r, "onKeyTyped", java.awt.event.KeyEvent.class)
                    || testInteractiveHandler(r, "onMouseClicked", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseEntered", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseExited", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMousePressed", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseReleased", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseMoved", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseDragged", java.awt.event.MouseEvent.class)
                    || testInteractiveHandler(r, "onMouseWheelMoved", MouseWheelEvent.class)
                    || testInteractiveHandler(r, "onKeyTyped", java.awt.event.MouseEvent.class)
                    ;
        }
        return isInteractive;
    }

    private boolean testInteractiveHandler(IInteractiveRobot r, String name, Class eventClass) {
        Class c = r.getClass();
        do
        {
            try {
                if (c.getDeclaredMethod(name, eventClass)!=null){
                    return true;
                }

            } catch (NoSuchMethodException e) {
            }
            c = c.getSuperclass();
        }while(c!=Robot.class);
        return false;
    }

    public String getFullClassNameWithVersion() {
        return getRobotClassManager().getClassNameManager().getFullClassNameWithVersion();
    }

    public String getUniqueFullClassNameWithVersion() {
        return getRobotClassManager().getClassNameManager().getUniqueFullClassNameWithVersion();
    }

    public String getName() {
        return (name != null) ? shortName : getRobotClassManager().getClassNameManager().getFullClassNameWithVersion();
    }

    public String getShortName() {
        return (shortName != null)
                ? shortName
                : getRobotClassManager().getClassNameManager().getUniqueShortClassNameWithVersion();
    }

    public String getVeryShortName() {
		return (shortName != null)
				? shortName
				: getRobotClassManager().getClassNameManager().getUniqueVeryShortClassNameWithVersion();
	}

	public String getNonVersionedName() {
		return (nonVersionedName != null)
				? nonVersionedName
				: getRobotClassManager().getClassNameManager().getFullClassName();
	}
}

