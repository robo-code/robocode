/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import net.sf.robocode.battle.IBattleManagerBase;
import net.sf.robocode.core.ContainerBase;
import org.junit.jupiter.api.Test;
import robocode.control.events.TurnEndedEvent;

import java.lang.reflect.Field;
import java.util.List;


/**
 * Tests that a spurious wakeup does not let a robot run twice in a turn.
 *
 * @author Julian Kent (original)
 */
public class TestSpuriousWakeup extends RobocodeTestBed {
	private Object sleepLock;
	private boolean wokenTwice;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.WokenOncePerTurn";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final int turn = event.getTurnSnapshot().getTurn();

		if (turn >= 10 && turn < 20) {
			try {
				if (sleepLock == null) {
					// The engine has its own class loader, so its classes are looked up by name
					final Object battle = field(ContainerBase.getComponent(IBattleManagerBase.class), "battle");
					final List<?> robots = (List<?>) field(battle, "robots");

					sleepLock = field(robots.get(0), "isSleeping");
				}
				synchronized (sleepLock) {
					sleepLock.notifyAll();
					sleepLock.wait(20);
				}
			} catch (ReflectiveOperationException | InterruptedException e) {
				throw new AssertionError(e);
			}
		}
		if (event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot().contains("Woken twice")) {
			wokenTwice = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(sleepLock != null, "The robot's sleep lock was not found");
		Assert.assertFalse(wokenTwice, "The robot ran twice in a turn after a spurious wakeup");
	}

	private static Object field(Object object, String name) throws ReflectiveOperationException {
		for (Class<?> c = object.getClass(); c != null; c = c.getSuperclass()) {
			try {
				final Field field = c.getDeclaredField(name);

				field.setAccessible(true);
				return field.get(object);
			} catch (NoSuchFieldException e) {
				// look in the superclass
			}
		}
		throw new NoSuchFieldException(name);
	}
}
