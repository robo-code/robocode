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
import robocode.robotinterfaces.IBasicEvents2;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * A BattleEndedEvent is sent to {@link Robot#onBattleEnded(BattleEndedEvent)
 * onBattleEnded()} when the battle is ended.
 * You can use the information contained in this event to determine if the
 * battle was aborted and also get the results of the battle.
 *
 * @see BattleResults
 * @see Robot#onBattleEnded(BattleEndedEvent)
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public final class BattleEndedEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 100; // System event -> cannot be changed!

	private final boolean aborted;
	private final BattleResults results;

	/**
	 * Called by the game to create a new BattleEndedEvent.
	 *
	 * @param aborted {@code true} if the battle was aborted; {@code false} otherwise.
	 * @param results the battle results
	 */
	public BattleEndedEvent(boolean aborted, BattleResults results) {
		this.aborted = aborted;
		this.results = results;
	}

	/**
	 * Checks if this battle was aborted.
	 *
	 * @return {@code true} if the battle was aborted; {@code false} otherwise.
	 */
	public boolean isAborted() {
		return aborted;
	}

	/**
	 * Returns the battle results.
	 *
	 * @return the battle results.
	 */
	public BattleResults getResults() {
		return results;
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

			if (listener != null && IBasicEvents2.class.isAssignableFrom(listener.getClass())) {
				((IBasicEvents2) listener).onBattleEnded(this);
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
		return RbSerializer.BattleEndedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BattleEndedEvent obj = (BattleEndedEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_BOOL
					+ serializer.sizeOf(RbSerializer.BattleResults_TYPE, obj.results);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BattleEndedEvent obj = (BattleEndedEvent) object;

			serializer.serialize(buffer, obj.aborted);
			serializer.serialize(buffer, RbSerializer.BattleResults_TYPE, obj.results);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			boolean aborted = serializer.deserializeBoolean(buffer);
			BattleResults results = (BattleResults) serializer.deserializeAny(buffer);

			return new BattleEndedEvent(aborted, results);
		}
	}
}
