/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Uses a few threads, which robots are allowed to.
 *
 * @author Flemming N. Larsen (original)
 */
public class ThreadsWithinLimit extends AdvancedRobot {

	public static final String OK_MESSAGE = "Threads OK";

	public void run() {
		final AtomicInteger sum = new AtomicInteger();
		Thread[] threads = new Thread[3];

		for (int i = 0; i < threads.length; i++) {
			final int value = i + 1;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					sum.addAndGet(value);
				}
			});
			threads[i].start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				return;
			}
		}
		if (sum.get() == 6) {
			out.println(OK_MESSAGE);
		}
		for (int i = 0; i < 10; i++) {
			turnLeft(10);
		}
	}
}
