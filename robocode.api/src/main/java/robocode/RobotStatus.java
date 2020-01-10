/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.security.IHiddenStatusHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.io.Serializable;
import java.nio.ByteBuffer;


/**
 * Contains the status of a robot for a specific time/turn returned by
 * {@link StatusEvent#getStatus()}.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.5
 */
public final class RobotStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	private final double energy;
	private final double x;
	private final double y;
	private final double bodyHeading;
	private final double gunHeading;
	private final double radarHeading;
	private final double velocity;
	private final double bodyTurnRemaining;
	private final double radarTurnRemaining;
	private final double gunTurnRemaining;
	private final double distanceRemaining;
	private final double gunHeat;
	private final int others;
	private final int numSentries;
	private final int roundNum;
	private final int numRounds;
	private final long time;

	/**
	 * Returns the robot's current energy.
	 *
	 * @return the robot's current energy
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the X position of the robot
	 * @see #getY()
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the Y position of the robot
	 * @see #getX()
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the direction that the robot's body is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 */
	public double getHeadingRadians() {
		return bodyHeading;
	}

	/**
	 * Returns the direction that the robot's body is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's body is facing, in degrees.
	 */
	public double getHeading() {
		return Math.toDegrees(bodyHeading);
	}

	/**
	 * Returns the direction that the robot's gun is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's gun is facing, in radians.
	 */
	public double getGunHeadingRadians() {
		return gunHeading;
	}

	/**
	 * Returns the direction that the robot's gun is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's gun is facing, in degrees.
	 */
	public double getGunHeading() {
		return Math.toDegrees(gunHeading);
	}

	/**
	 * Returns the direction that the robot's radar is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's radar is facing, in radians.
	 */
	public double getRadarHeadingRadians() {
		return radarHeading;
	}

	/**
	 * Returns the direction that the robot's radar is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's radar is facing, in degrees.
	 */
	public double getRadarHeading() {
		return Math.toDegrees(radarHeading);
	}

	/**
	 * Returns the velocity of the robot measured in pixels/turn.
	 * <p>
	 * The maximum velocity of a robot is defined by {@link Rules#MAX_VELOCITY}
	 * (8 pixels / turn).
	 *
	 * @return the velocity of the robot measured in pixels/turn
	 * @see Rules#MAX_VELOCITY
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * Returns the angle remaining in the robots's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robots's turn, in radians
	 */
	public double getTurnRemainingRadians() {
		return bodyTurnRemaining;
	}

	/**
	 * Returns the angle remaining in the robots's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robots's turn, in degrees
	 */
	public double getTurnRemaining() {
		return Math.toDegrees(bodyTurnRemaining);
	}

	/**
	 * Returns the angle remaining in the radar's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in radians
	 */
	public double getRadarTurnRemainingRadians() {
		return radarTurnRemaining;
	}

	/**
	 * Returns the angle remaining in the radar's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in degrees
	 */
	public double getRadarTurnRemaining() {
		return Math.toDegrees(radarTurnRemaining);
	}

	/**
	 * Returns the angle remaining in the gun's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in radians
	 */
	public double getGunTurnRemainingRadians() {
		return gunTurnRemaining;
	}

	/**
	 * Returns the angle remaining in the gun's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in degrees
	 */
	public double getGunTurnRemaining() {
		return Math.toDegrees(gunTurnRemaining);
	}

	/**
	 * Returns the distance remaining in the robot's current move measured in
	 * pixels.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently moving forwards. Negative values means
	 * that the robot is currently moving backwards.
	 *
	 * @return the distance remaining in the robot's current move measured in
	 *         pixels.
	 */
	public double getDistanceRemaining() {
		return distanceRemaining;
	}

	/**
	 * Returns the current heat of the gun. The gun cannot fire unless this is
	 * 0. (Calls to fire will succeed, but will not actually fire unless
	 * getGunHeat() == 0).
	 * <p>
	 * The amount of gun heat generated when the gun is fired is
	 * 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
	 * by {@link Robot#getGunCoolingRate()}, which is a battle setup.
	 * <p>
	 * Note that all guns are "hot" at the start of each round, where the gun
	 * heat is 3.
	 *
	 * @return the current gun heat
	 * @see Robot#getGunCoolingRate()
	 * @see Robot#fire(double)
	 * @see Robot#fireBullet(double)
	 */
	public double getGunHeat() {
		return gunHeat;
	}

	/**
	 * Returns how many opponents that are left in the current round.
	 *
	 * @return how many opponents that are left in the current round.
	 * @see #getNumSentries()
	 * @since 1.6.2
	 */
	public int getOthers() {
		return others;
	}

	/**
	 * Returns how many sentry robots that are left in the current round.
	 *
	 * @return how many sentry robots that are left in the current round.
	 * @see #getOthers()
	 * @since 1.9.1.0
	 */
	public int getNumSentries() {
		return numSentries;
	}

	/**
	 * Returns the number of rounds in the current battle.
	 *
	 * @return the number of rounds in the current battle
	 * @see #getRoundNum()
	 * @since 1.6.2
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Returns the current round number (0 to {@link #getNumRounds()} - 1) of
	 * the battle.
	 *
	 * @return the current round number of the battle (zero indexed).
	 * @see #getNumRounds()
	 * @since 1.6.2
	 */
	public int getRoundNum() {
		return roundNum;
	}

	/**
	 * Returns the game time of the round, where the time is equal to the current turn in the round.
	 *
	 * @return the game time/turn of the current round.
	 * @since 1.6.2
	 */
	public long getTime() {
		return time;
	}

	private RobotStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading,
			double velocity, double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining,
			double distanceRemaining, double gunHeat, int others, int numSentries, int roundNum, int numRounds, long time) {
		this.energy = energy;
		this.x = x;
		this.y = y;
		this.bodyHeading = bodyHeading;
		this.gunHeading = gunHeading;
		this.radarHeading = radarHeading;
		this.bodyTurnRemaining = bodyTurnRemaining;
		this.velocity = velocity;
		this.radarTurnRemaining = radarTurnRemaining;
		this.gunTurnRemaining = gunTurnRemaining;
		this.distanceRemaining = distanceRemaining;
		this.gunHeat = gunHeat;
		this.others = others;
		this.numSentries = numSentries;
		this.roundNum = roundNum;
		this.numRounds = numRounds;
		this.time = time;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper, IHiddenStatusHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 12 * RbSerializer.SIZEOF_DOUBLE + 4 * RbSerializer.SIZEOF_INT
					+ RbSerializer.SIZEOF_LONG;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			RobotStatus obj = (RobotStatus) object;

			serializer.serialize(buffer, obj.energy);
			serializer.serialize(buffer, obj.x);
			serializer.serialize(buffer, obj.y);
			serializer.serialize(buffer, obj.bodyHeading);
			serializer.serialize(buffer, obj.gunHeading);
			serializer.serialize(buffer, obj.radarHeading);
			serializer.serialize(buffer, obj.velocity);
			serializer.serialize(buffer, obj.bodyTurnRemaining);
			serializer.serialize(buffer, obj.radarTurnRemaining);
			serializer.serialize(buffer, obj.gunTurnRemaining);
			serializer.serialize(buffer, obj.distanceRemaining);
			serializer.serialize(buffer, obj.gunHeat);
			serializer.serialize(buffer, obj.others);
			serializer.serialize(buffer, obj.numSentries);
			serializer.serialize(buffer, obj.roundNum);
			serializer.serialize(buffer, obj.numRounds);
			serializer.serialize(buffer, obj.time);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double energy = buffer.getDouble();
			double x = buffer.getDouble();
			double y = buffer.getDouble();
			double bodyHeading = buffer.getDouble();
			double gunHeading = buffer.getDouble();
			double radarHeading = buffer.getDouble();
			double velocity = buffer.getDouble();
			double bodyTurnRemaining = buffer.getDouble();
			double radarTurnRemaining = buffer.getDouble();
			double gunTurnRemaining = buffer.getDouble();
			double distanceRemaining = buffer.getDouble();
			double gunHeat = buffer.getDouble();
			int others = buffer.getInt();
			int numSentries = buffer.getInt();
			int roundNum = buffer.getInt();
			int numRounds = buffer.getInt();
			long time = buffer.getLong();

			return new RobotStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity, bodyTurnRemaining,
					radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, numSentries, roundNum, numRounds,
					time);
		}

		public RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading, double velocity,
				double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining, double distanceRemaining, double gunHeat, int others,
				int numSentries, int roundNum, int numRounds, long time) {
			return new RobotStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity, bodyTurnRemaining,
					radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, numSentries, roundNum, numRounds,
					time);
		}
	}

}
