/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.*;


/**
 * This nasty robot tries to interrupt the thread of each opponent robot that it scans.
 * It enumerates the threads recursively of thread group that is a parent of its own
 * thread group to find out, which threads that are active. These threads are all robot
 * threads.
 * 
 * This robot is inspired by the hacker.Destroyer 1.3, which proved a security breach in
 * Robocode 1.7.2.1 Beta. The security breach was reported with:
 * Bug [3021140] Possible for robot to kill other robot threads.
 *
 * The security manager of Robocode must make sure that unsafe (robot) threads cannot
 * access thread groups other than its own thread group within checkAccess(Thread).
 *
 * @author Flemming N. Larsen (original)
 */
public class ThreadGroupAttack extends Robot {
	private Thread[] threads = new Thread[100];

	public void run() {
		runAttack();

		while (true) {
			turnGunLeft(30);
		}
	}

	private void runAttack() {
		try {
			new Thread(new Runnable() {
				public void run() {
					ThreadGroup parentGroup = Thread.currentThread().getThreadGroup().getParent();
	
					while (true) {
						parentGroup.enumerate(threads, true);
						try {
							Thread.sleep(0);
						} catch (InterruptedException ignore) {}
					}
				}
			}).start();
		} catch (Throwable t) {
			t.printStackTrace(out);
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		attackRobotThread(e.getName());
	}

	private void attackRobotThread(String robotName) {
		for (Thread t : threads) {
			if (t != null && robotName.equals(t.getName())) {
				t.interrupt();
				out.println("Interrupted: " + robotName);
			}
		}
	}
}
