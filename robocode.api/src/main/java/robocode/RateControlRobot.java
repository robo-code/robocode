/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial implementation
 *******************************************************************************/

package robocode;

/**
 * This robot class allows you to set a rate for each of the robot's movements.
 * 
 * @author Joshua Galecki
 * @since 1.7.1.3
 *
 */
public class RateControlRobot extends AdvancedRobot
{
	private double velocityRate = 0;
	private double turnRate = 0;			//stored as radians
	private double gunRotationRate = 0;		//stored as radians
	private double radarRotationRate = 0;	//stored as radians
	
	/**
	 * Sets the speed the robot will move, in pixels per turn.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot will move backwards
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the robot to move forward 2 pixels per turn
	 *   setVelocityRate(2);
	 * <p/>
	 *   // Set the robot to move backwards 8 pixels per turn
	 *   // (overrides the previous order)
	 *   setVelocityRate(-8);
	 * <p/>
	 *   ...
	 *   // Executes the last setVelocityRate()
	 *   execute();
	 * </pre>
	 * 
	 * @param velocityRate Pixels per turn the robot will move.
	 */
	public void setVelocityRate(double velocityRate) {
		this.velocityRate = velocityRate;
	}
	
	/**
	 * Gets the speed the robot will move, in pixels per turn.
	 * 
	 * @return The speed of the robot in pixels per turn
	 */
	public double getVelocityRate() {
		return velocityRate;
	}
	
	/**
	 * Sets the robot's clockwise (right) rotation per turn, in degrees.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn right 10 degrees per turn
	 *   setTurnRate(10);
	 * <p/>
	 *   // Set the robot to turn left 4 degrees per turn
	 *   // (overrides the previous order)
	 *   setTurnRate(-5);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnRate()
	 *   execute();
	 * </pre>
	 * 
	 * @param turnRate Angle of the clockwise rotation, in degrees.
	 */
	public void setTurnRate(double turnRate) {
		this.turnRate = turnRate * Math.PI / 180;
	}
	
	/**
	 * Gets the robot's clockwise rotation per turn, in degrees.
	 * 
	 * @return Angle of the clockwise rotation, in degrees.
	 */
	public double getTurnRate() {
		return turnRate * 180 / Math.PI;
	}
	
	/**
	 * Sets the robot's clockwise (right) rotation per turn, in radians.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn right pi / 32 radians per turn
	 *   setTurnRateRadians(Math.PI / 32);
	 * <p/>
	 *   // Set the robot to turn left pi / 20 radians per turn
	 *   // (overrides the previous order)
	 *   setTurnRateRadians(-Math.PI / 20);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnRateRadians()
	 *   execute();
	 * </pre>
	 * 
	 * @param turnRate Angle of the clockwise rotation, in radians.
	 */
	public void setTurnRateRadians(double turnRate) {
		this.turnRate = turnRate;
	}
	
	/**
	 * Gets the robot's clockwise rotation per turn, in radians.
	 * 
	 * @return Angle of the clockwise rotation, in radians.
	 */
	public double getTurnRateRadians() {
		return turnRate;
	}
	
	/**
	 * Sets the gun's clockwise (right) rotation per turn, in degrees.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the gun turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn right 15 degrees per turn
	 *   setGunRotationRate(15);
	 * <p/>
	 *   // Set the gun to turn left 9 degrees per turn
	 *   // (overrides the previous order)
	 *   setGunRotationRate(-9);
	 * <p/>
	 *   ...
	 *   // Executes the last setGunRotationRate()
	 *   execute();
	 * </pre>
	 * 
	 * @param gunRotationRate Angle of the clockwise rotation, in degrees.
	 */
	public void setGunRotationRate(double gunRotationRate) {
		this.gunRotationRate = gunRotationRate * Math.PI / 180;
	}
	
	/**
	 * Gets the gun's clockwise rotation per turn, in degrees.
	 * 
	 * @return Angle of the clockwise rotation, in degrees.
	 */
	public double getGunRotationRate() {
		return gunRotationRate * 180 / Math.PI;
	}
	
	/**
	 * Sets the gun's clockwise (right) rotation per turn, in radians.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the gun turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn right pi / 16 radians per turn
	 *   setGunRotationRateRadians(Math.PI / 16);
	 * <p/>
	 *   // Set the gun to turn left pi / 12 radians per turn
	 *   // (overrides the previous order)
	 *   setGunRotationRateRadians(-Math.PI / 12);
	 * <p/>
	 *   ...
	 *   // Executes the last setGunRotationRateRadians()
	 *   execute();
	 * </pre>
	 * 
	 * @param gunRotationRate Angle of the clockwise rotation, in radians.
	 */
	public void setGunRotationRateRadians(double gunRotationRate) {
		this.gunRotationRate = gunRotationRate;
	}
	
	/**
	 * Gets the gun's clockwise rotation per turn, in radians.
	 * 
	 * @return Angle of the clockwise rotation, in radians.
	 */
	public double getGunRotationRateRadians() {
		return gunRotationRate;
	}
	
	/**
	 * Sets the radar's clockwise (right) rotation per turn, in degrees.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the radar turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn right 45 degrees per turn
	 *   setRadarRotationRate(45);
	 * <p/>
	 *   // Set the radar to turn left 15 degrees per turn
	 *   // (overrides the previous order)
	 *   setRadarRotationRate(-15);
	 * <p/>
	 *   ...
	 *   // Executes the last setRadarRotationRate()
	 *   execute();
	 * </pre>
	 * 
	 * @param radarRotationRate Angle of the clockwise rotation, in degrees.
	 */
	public void setRadarRotationRate(double radarRotationRate) {
		this.radarRotationRate = radarRotationRate * Math.PI / 180;
	}
	
	/**
	 * Gets the radar's clockwise rotation per turn, in degrees.
	 * 
	 * @return Angle of the clockwise rotation, in degrees.
	 */
	public double getRadarRotationRate() {
		return radarRotationRate * 180 / Math.PI;
	}
	
	/**
	 * Sets the radar's clockwise (right) rotation per turn, in radians.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the radar turns counterclockwise (left)
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn right pi / 4 radians per turn
	 *   setRadarRotationRateRadians(Math.PI / 4);
	 * <p/>
	 *   // Set the radar to turn left pi / 8 radians per turn
	 *   // (overrides the previous order)
	 *   setRadarRotationRateRadians(-Math.PI / 8);
	 * <p/>
	 *   ...
	 *   // Executes the last setRadarRotationRateRadians()
	 *   execute();
	 * </pre>
	 * 
	 * @param gunRotationRate Angle of the clockwise rotation, in radians.
	 */
	public void setRadarRotationRateRadians(double radarRotationRate) {
		this.radarRotationRate = radarRotationRate;		
	}
	
	/**
	 * Gets the radar's clockwise rotation per turn, in radians.
	 * 
	 * @return Angle of the clockwise rotation, in radians.
	 */
	public double getRadarRotationRateRadians() {
		return radarRotationRate;
	}
	
	/**
	 * Any previous calls to "Movement" functions outside of RateControlRobot, 
	 * such as setAhead(), setTurnLeft(), setRadarTurnRight, etc., will be 
	 * overridden when this is called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void execute()
	{
		setMaxVelocity(velocityRate);
		if (velocityRate > 0)
		{
			setAhead(100);
		}
		else if (velocityRate < 0)
		{
			setBack(100);
		}
		else
		{
			setAhead(0);
		}

		setTurnGunRightRadians(gunRotationRate); 			
		setTurnRadarRightRadians(radarRotationRate); 
		setTurnRightRadians(turnRate);
		
		super.execute();
	}
	
}
