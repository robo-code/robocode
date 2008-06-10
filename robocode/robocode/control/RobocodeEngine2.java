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
package robocode.control;

import robocode.battle.events.IBattleListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Pavel Savara (original)
 */
public class RobocodeEngine2 extends RobocodeEngine{
    public RobocodeEngine2(File robocodeHome) {
        super(robocodeHome, null);
    }

    public void addBattleListener(IBattleListener listener) {
        manager.getBattleManager().addListener(listener);
    }

    public void removeBattleListener(IBattleListener listener) {
        manager.getBattleManager().removeListener(listener);
    }

    public RobotSpecification[] getLocalRepository(String selectedRobotList){
        RobotSpecification[] repository = getLocalRepository();

        HashMap<String, RobotSpecification> robotSpecMap = new HashMap<String, RobotSpecification>();

        for (RobotSpecification spec : repository) {
            robotSpecMap.put(spec.getNameAndVersion(), spec);
        }

        String[] selectedRobots = selectedRobotList.split(",");

        List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();

        RobotSpecification spec;

        for (String robot : selectedRobots) {
            spec = robotSpecMap.get(robot);
            if (spec != null) {
                selectedRobotSpecs.add(spec);
            }
        }
        return selectedRobotSpecs.toArray(new RobotSpecification[selectedRobotSpecs.size()]);
    }

    /**
     * Prints out all running thread to the standard system out (console)
     */
    public static void printRunningThreads() {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();

        if (currentGroup == null) {
            return;
        }

        while (currentGroup.getParent() != null) {
            currentGroup = currentGroup.getParent();
        }

        ThreadGroup groups[] = new ThreadGroup[256];
        Thread threads[] = new Thread[256];

        int numGroups = currentGroup.enumerate(groups, true);

        for (int i = 0; i < numGroups; i++) {
            currentGroup = groups[i];
            if (currentGroup.isDaemon()) {
                System.out.print("  ");
            } else {
                System.out.print("* ");
            }
            System.out.println("In group: " + currentGroup.getName());
            int numThreads = currentGroup.enumerate(threads);

            for (int j = 0; j < numThreads; j++) {
                if (threads[j].isDaemon()) {
                    System.out.print("  ");
                } else {
                    System.out.print("* ");
                }
                System.out.println(threads[j].getName());
            }
            System.out.println("---------------");
        }
    }
    
}
