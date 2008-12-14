/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IAdvancedEvents;
import robocode.robotinterfaces.IAdvancedRobot;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * A SkippedTurnEvent is sent to {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
 * onSkippedTurn()} when your robot is forced to skipping a turn.
 * You must take an action every turn in order to participate in the game.
 * For example,
 * <pre>
 *    try {
 *        Thread.sleep(1000);
 *    } catch (InterruptedException e) {
 *        // Immediately reasserts the exception by interrupting the caller thread
 *        // itself.
 *        Thread.currentThread().interrupt();
 *    }
 * </pre>
 * will cause many SkippedTurnEvents, because you are not responding to the game.
 * If you receive 30 SkippedTurnEvents, you will be removed from the round.
 * <p/>
 * Instead, you should do something such as:
 * <pre>
 *     for (int i = 0; i < 30; i++) {
 *         doNothing(); // or perhaps scan();
 *     }
 * </pre>
 * <p/>
 * This event may also be generated if you are simply doing too much processing
 * between actions, that is using too much processing power for the calculations
 * etc. in your robot.
 *
 * @author Mathew A. Nelson (original)
 * @see AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
 * @see SkippedTurnEvent
 */
public final class SkippedTurnEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 100; // System event -> cannot be changed!;

	/**
	 * Called by the game to create a new SkippedTurnEvent.
	 */
	public SkippedTurnEvent() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		if (statics.isAdvancedRobot()) {
			IAdvancedEvents listener = ((IAdvancedRobot) robot).getAdvancedEventListener();

			if (listener != null) {
				listener.onSkippedTurn(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final boolean isCriticalEvent() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.SkippedTurnEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			return new SkippedTurnEvent();
		}
	}
}
