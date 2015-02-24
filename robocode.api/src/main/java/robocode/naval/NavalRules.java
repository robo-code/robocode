package robocode.naval;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import robocode.Robot;

/**
 * Class that discribes the rules of Naval Robocode.
 * Also contains a few variables regarding the Ships.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class NavalRules {

	// Hide the constructor in the Javadocs as it should not be used
	private NavalRules() {}
	
	/**
	 * The width and height of a regular ship.
	 */
	public static final int
			WIDTH = 40,
			HEIGHT = 207,
			HALF_WIDTH_OFFSET = (WIDTH >> 1),
			HALF_HEIGHT_OFFSET = (HEIGHT >> 1);
	
	/**
	 * The Pivot Point is this much pixels from the center of the ship into the prow.
	 */
	public static final double PROW_OFFSET = 50.0d;
	
	/**
	 * The offsets from the Pivot point(PROW_OFFSET), for the different components.
	 */
	public static final double
			CENTRAL_RADAR_OFFSET = HALF_HEIGHT_OFFSET - PROW_OFFSET, // center, center    PivotY - central radar Y = radar Y
			FRONT_WEAPON_OFFSET = 34.0d - PROW_OFFSET, // center, front 
			BACK_WEAPON_OFFSET = HEIGHT - PROW_OFFSET - 24.0d, // center, back
			MINE_COMPONENT_OFFSET = HEIGHT - PROW_OFFSET - 40.0d;   
	// HEIGHT - PROW_OFFSET = edge of ship    At moment, mines are placed 80 pixels from the minecomponent. So 40 pixels from the edge.
	// So, if I want to make mines with a power of 5 viable for ramming, I need to make sure the blast radius doesn't exceed 40. (So I'll go with 35)
	
	/**
	 * The indices of the different components as used by the AdvancedShip class.
	 */
	public static final int
			IDX_CENTRAL_RADAR = 0,
			IDX_WEAPON_FRONT = 1,
			IDX_WEAPON_BACK = 2,
			IDX_MINE_PLACER = 3;

	/**
	 * The acceleration of a Ship, i.e. the increase of velocity when the
	 * Ship moves forward, which is 1 pixel/turn.
	 */
	public static final double ACCELERATION = 1;

	/**
	 * The deceleration of a Ship, i.e. the decrease of velocity when the
	 * Ship moves backwards (or brakes), which is 2 pixels/turn.
	 */
	public static final double DECELERATION = 0.8;

	/**
	 * The maximum velocity of a Ship, which is 15.4 pixels/turn.
	 */	
	public static final double MAX_VELOCITY = 15.4;

	/**
	 * The radar scan radius, which is 1200 pixels.
	 * Ships that are more than 1200 pixels away cannot be seen on the radar.
	 */
	public static final double RADAR_SCAN_RADIUS = 1200;

	/**
	 * The minimum bullet power, i.e the amount of energy required for firing a
	 * bullet, which is 0.1 energy points.
	 */
	public static final double MIN_BULLET_POWER = 0.1;

	/**
	 * The maximum bullet power, i.e. the maximum amount of energy that can be
	 * transferred to a bullet when it is fired, which is 3 energy points.
	 */
	public static final double MAX_BULLET_POWER = 3;
	
	/**
	 * The minimum mine power. Minepower can't be set lower than this
	 */
	public static final double MIN_MINE_POWER = 5;
	
	/**
	 * The maximum mine power. Minepower can't be set higher than this.
	 */
	public static final double MAX_MINE_POWER = 15;

	/**
	 * The maximum turning rate of the Ship, in degrees, which is
	 * 1 degrees/turn.
	 * Note, that the turn rate of the robot depends on it's velocity.
	 *
	 * @see #MAX_TURN_RATE_RADIANS
	 * @see #getTurnRate(double)
	 * @see #getTurnRateRadians(double)
	 */
	public static final double MAX_TURN_RATE = 1; 

	/**
	 * The maximum turning rate of the Ship measured in radians instead of
	 * degrees.
	 *
	 * @see #MAX_TURN_RATE
	 * @see #getTurnRate(double)
	 * @see #getTurnRateRadians(double)
	 */
	public static final double MAX_TURN_RATE_RADIANS = Math.toRadians(MAX_TURN_RATE);

	/**
	 * The turning rate of the gun measured in degrees, which is
	 * 8 degrees/turn.
	 * Note, that if setAdjustGunForRobotTurn(true) has been called, the gun
	 * turn is independent of the robot turn.
	 * In this case the gun moves relatively to the screen. If
	 * setAdjustGunForRobotTurn(false) has been called or
	 * setAdjustGunForRobotTurn() has not been called at all (this is the
	 * default), then the gun turn is dependent on the robot turn, and in this
	 * case the gun moves relatively to the robot body.
	 *
	 * @see #GUN_TURN_RATE_RADIANS
	 * @see Robot#setAdjustGunForRobotTurn(boolean)
	 */
	public static final double GUN_TURN_RATE = 8;

	/**
	 * The turning rate of the gun measured in radians instead of degrees.
	 *
	 * @see #GUN_TURN_RATE
	 */
	public static final double GUN_TURN_RATE_RADIANS = Math.toRadians(GUN_TURN_RATE);

	/**
	 * The turning rate of the radar measured in degrees, which is
	 * 15 degrees/turn.
	 * Note, that if setAdjustRadarForRobotTurn(true) and/or
	 * setAdjustRadarForGunTurn(true) has been called, the radar turn is
	 * independent of the robot and/or gun turn. If both methods has been set to
	 * true, the radar moves relatively to the screen.
	 * If setAdjustRadarForRobotTurn(false) and/or setAdjustRadarForGunTurn(false)
	 * has been called or not called at all (this is the default), then the
	 * radar turn is dependent on the robot and/or gun turn, and in this case
	 * the radar moves relatively to the gun and/or Ship body.
	 *
	 * @see #RADAR_TURN_RATE_RADIANS
	 * @see Robot#setAdjustGunForRobotTurn(boolean)
	 * @see Robot#setAdjustRadarForGunTurn(boolean)
	 */
	public static final double RADAR_TURN_RATE = 15;
	
	/**
	 * The turning rate of the radar measured in radians instead of degrees.
	 *
	 * @see #RADAR_TURN_RATE
	 */
	public static final double RADAR_TURN_RATE_RADIANS = Math.toRadians(RADAR_TURN_RATE);
	
	
	public static final double RADAR_EXTENT_DEGREES = 40;
	
	public static final double RADAR_EXTENT_RADIANS = Math.toRadians(RADAR_EXTENT_DEGREES);


	/**
	 * The amount of damage taken when a robot hits or is hit by another robot,
	 * which is 0.6 energy points.
	 */
	public static final double SHIP_HIT_DAMAGE = 0.6;

	/**
	 * The amount of bonus given when a robot moving forward hits an opponent
	 * robot (ramming), which is 1.2 energy points.
	 */
	public static final double SHIP_HIT_BONUS = 1.2;

	/**
	 * Returns the turn rate of a Ship given a specific velocity measured in
	 * degrees/turn.
	 *
	 * @param velocity the velocity of the robot.
	 * @return turn rate in degrees/turn.
	 * @see #getTurnRateRadians(double)
	 */
	public static double getTurnRate(double velocity) {
		return MAX_TURN_RATE - 0.75 * abs(velocity);
	}

	/**
	 * Returns the turn rate of a Ship given a specific velocity measured in
	 * radians/turn.
	 *
	 * @param velocity the velocity of the robot.
	 * @return turn rate in radians/turn.
	 * @see #getTurnRate(double)
	 */
	public static double getTurnRateRadians(double velocity) {
		return Math.toRadians(getTurnRate(velocity));
	}

	/**
	 * Returns the amount of damage taken when Ship hits a wall with a
	 * specific velocity.
	 *
	 * @param velocity the velocity of the Ship.
	 * @return wall hit damage in energy points.
	 */
	public static double getWallHitDamage(double velocity) {
		return max(abs(velocity) / 2 - 1, 0);
	}

	/**
	 * This value is reduced, since Ships are easier to hit (I mean, bigger hitbox + 2 weapons)
	 * 
	 * Returns the amount of damage of a bullet given a specific bullet power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet damage in energy points.
	 */
	public static double getBulletDamage(double bulletPower) {
		double damage = 4 * bulletPower;

		if (bulletPower > 1) {
			damage += 1.2 * (bulletPower - 1);
		}
		return damage;
	}

	/**
	 * Returns the amount of bonus given when a Ship's bullet hits an opponent
	 * Ship given a specific bullet power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet hit bonus in energy points.
	 */
	public static double getBulletHitBonus(double bulletPower) {
		return 2 * bulletPower;
	}

	/**
	 * Returns the speed of a bullet given a specific bullet power measured in pixels/turn.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet speed in pixels/turn
	 */
	public static double getBulletSpeed(double bulletPower) {
		bulletPower = Math.min(Math.max(bulletPower, MIN_BULLET_POWER), MAX_BULLET_POWER);
		return 20 - 3 * bulletPower;
	}

	/**
	 * Returns the heat produced by firing the gun given a specific bullet
	 * power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return gun heat
	 */
	public static double getGunHeat(double bulletPower) {
		return 1 + (bulletPower / 2);
	}
	
	public static double getMineRecharge(double minePower){
		return 2 + (minePower / 5);
	}
	
	/**
	 * Returns the amount of damage of a mine given a specific mine power.
	 *
	 * @param minePower the energy power of the bullet.
	 * @return mine damage in energy points.
	 */
	public static double getMineDamage(double minePower) {
		double damage = 3 * minePower;

		if (minePower == 15) {
			damage += 5;
		}
		return damage;
	}
	
	/**
	 * Returns the amount of bonus given when a robot's mine hits an opponent
	 * Ship given a specific mine power.
	 *
	 * @param minePower the energy power of the mine.
	 * @return mine hit bonus in energy points.
	 */
	public static double getMineHitBonus(double minePower) {
		return 3 * minePower;
	}
	
	/**
	 * Experimental: Blastradius
	 * Mine blast radius = mine_Radius + blastRadius
	 */
//	public static final double BLAST_RADIUS = 50;
	
	public static double getBlastRadius(double minePower){
		return minePower * 7;
	}
}
