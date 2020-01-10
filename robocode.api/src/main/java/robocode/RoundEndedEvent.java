/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicEvents3;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * A RoundEndedEvent is sent to {@link Robot#onRoundEnded(RoundEndedEvent)
 * onRoundEnded()} when a round has ended.
 * You can use the information contained in this event to determine which round that has ended.
 *
 * @see Robot#onRoundEnded(RoundEndedEvent)
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.7.2
 */
public final class RoundEndedEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 110; // System event -> cannot be changed!

	private final int round;
	private final int turns;
	private final int totalTurns;

	/**
	 * Called by the game to create a new RoundEndedEvent.
	 *
	 * @param round the round that has ended (zero-indexed).
	 * @param turns the number of turns that this round reached.
	 * @param totalTurns the total number of turns reached in the battle when this round ended.
	 */
	public RoundEndedEvent(int round, int turns, int totalTurns) {
		this.round = round;
		this.turns = turns;
		this.totalTurns = totalTurns;
	}

	/**
	 * Returns the round that ended (zero-indexed).
	 *
	 * @return the round that ended (zero-indexed).
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Returns the number of turns that this round reached. 
	 *
	 * @return the number of turns that this round reached.
	 */
	public int getTurns() {
		return turns;
	}

	/**
	 * Returns the total number of turns reached in the battle when this round ended. 
	 *
	 * @return the total number of turns reached in the battle when this round ended. 
	 */
	public int getTotalTurns() {
		return totalTurns;
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
	public final int getPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		if (robot != null) {
			IBasicEvents listener = robot.getBasicEventListener();

			if (listener != null && IBasicEvents3.class.isAssignableFrom(listener.getClass())) {
				((IBasicEvents3) listener).onRoundEnded(this);
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
		return RbSerializer.RoundEndedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 3 * RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			RoundEndedEvent event = (RoundEndedEvent) object;
			
			serializer.serialize(buffer, event.round);
			serializer.serialize(buffer, event.turns);
			serializer.serialize(buffer, event.totalTurns);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			int round = serializer.deserializeInt(buffer);
			int turns = serializer.deserializeInt(buffer);
			int totalTurns = serializer.deserializeInt(buffer);

			return new RoundEndedEvent(round, turns, totalTurns);
		}
	}
}
