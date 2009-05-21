/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


/**
 * @author Flemming N. Larsen (original)
 */
public class ConstructorThreadAttack extends robocode.AdvancedRobot {

	public ConstructorThreadAttack() {
		runAttack();
		runAttack2();
	}

	private void runAttack() {
		try {
			Attacker a = new Attacker();
			Thread t = new Thread(a);

			t.start();
		} catch (Throwable e) {
			// swallow security exception
			e.printStackTrace(out);
		}
	}

	private void runAttack2() {
		try {
			Attacker a = new Attacker();
			ThreadGroup tg = new ThreadGroup("MyAttack");

			tg.setMaxPriority(10);
			Thread t = new Thread(tg, a);

			t.start();
		} catch (Throwable e) {
			// swallow security exception
			e.printStackTrace(out);
		}
	}

	private class Attacker implements Runnable {

		public synchronized void run() {
			if (Thread.currentThread().getPriority() > 4) {
				out.println("Priority attack");
			}
			runAttack2();

			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
	}
}
