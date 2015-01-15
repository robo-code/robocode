package net.sf.robocode.battle.peer;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static net.sf.robocode.io.Logger.logMessage;
import static robocode.naval.ComponentType.WEAPON_PROW;
import static robocode.naval.ComponentType.WEAPON_STERN;
import static robocode.util.Utils.isNear;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.peer.BulletCommand;
import net.sf.robocode.peer.BulletCommandShip;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.peer.ExecResults;
import net.sf.robocode.peer.MineCommand;
import net.sf.robocode.peer.MineStatus;
import net.sf.robocode.security.HiddenAccess;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedShipEvent;
import robocode.ShipStatus;
import robocode.control.RobotSpecification;
import robocode.control.snapshot.RobotState;
import robocode.naval.ComponentManager;
import robocode.naval.ComponentType;
import robocode.naval.MineComponent;
import robocode.naval.NavalRules;
import robocode.naval.RadarComponent;
import robocode.naval.WeaponComponent;
import robocode.naval.interfaces.IComponent;
import robocode.util.Collision;
import robocode.util.Coordinates;
import robocode.util.Utils;

/**
 * An extension of {@link net.sf.robocode.battle.peer.RobotPeer RobotPeer} to support the ship's movement.
 * @author Thales B.V. / Thomas Hakkers / Jiri waning
 * @version 2.0
 */
public class ShipPeer extends RobotPeer {

	/**
	 * The component manager that holds all the components of this ship.
	 */
	private ComponentManager manager;
	/** The X and Y values of the last turn. Used to fall back on in case of collision */
	private double lastX;
	private double lastY;
	private BoundingRectangle boundingRectangle;
	private AtomicReference<List<MineStatus>> mineUpdates = new AtomicReference<List<MineStatus>>(
			new ArrayList<MineStatus>());
	
	/**
	 * Constructor of the Ship. Same as the constructor of {@link RobotPeer} . Only difference is that this one also initiates the components.
	 * @param battle The Battle the Ship will be fighting in
	 * @param hostManager
	 * @param robotSpecification The properties of the Ship, like its name and jar-file location
	 * @param duplicate To make sure two Ship which are the same don't get the same name. (coolShip(1) and coolShip(2))
	 * @param team	Technically speaking, you can just leave this on null. Team haven't been implemented yet
	 * @param robotIndex The Ship's unique index
	 */
	public ShipPeer(Battle battle, IHostManager hostManager, RobotSpecification robotSpecification,
			int duplicate, TeamPeer team, int robotIndex) {
		super(battle, hostManager, robotSpecification, duplicate, team, robotIndex);
		initComponents();
		boundingRectangle = new BoundingRectangle(0,0,NavalRules.WIDTH, NavalRules.HEIGHT);
		statistics = new ShipStatistics(this, battle.getRobotsCount());

	}
	
	/**
	 * Is here to do a few tests. See TestWallCollision
	 */
	public ShipPeer(){
		super();
		initComponents();
		boundingRectangle = new BoundingRectangle(0,0,NavalRules.WIDTH, NavalRules.HEIGHT);
		statistics = new ShipStatistics(this, battle.getRobotsCount());
	}
	
	/**
	 * Returns the "BoundingBox" of the Ship.
	 * This is in fact not an actual BoundingBox. It's just a box that has the same size as the Ship.
	 * All transformations happen in the {@link Collision} class.
	 */
	@Override
	public BoundingRectangle getBoundingBox(){
		return boundingRectangle;
	}
	
	/**
	 * Initiates the {@link ComponentManager} on the Ship. 
	 * In this case it will create a {@link ComponentManager} that contains a front cannon, a back cannon, a radar and a mine placer.
	 */
	protected void initComponents() {
		if (manager == null) {
			manager = new ComponentManager();
			manager.addComponent(NavalRules.IDX_CENTRAL_RADAR, new RadarComponent(0.0d, NavalRules.CENTRAL_RADAR_OFFSET));
			manager.addComponent(NavalRules.IDX_WEAPON_FRONT, new WeaponComponent(0.0d, NavalRules.FRONT_WEAPON_OFFSET, WEAPON_PROW, 100, 160));
			manager.addComponent(NavalRules.IDX_WEAPON_BACK, new WeaponComponent(0.0d, NavalRules.BACK_WEAPON_OFFSET, WEAPON_STERN, 280, 160));
			manager.addComponent(NavalRules.IDX_MINE_PLACER, new MineComponent(0.0d, NavalRules.MINE_COMPONENT_OFFSET, ComponentType.MINE_STANDARD));
		}
	}
	
	/**
	 * A slightly modified version to the {@link RobotPeer} version
	 * This one also handles the mine-placing commands
	 */
	public void performLoadCommands() {
		super.performLoadCommands();
		placeMines(currentCommands.getMines());
	}
	
	
	/**
	 * Update the gun heat; this actually cools down the gun after it has been fired.
	 * Note that the coolingRate is twice as slow. The idea behind this is the fact 
	 * that Ships are fairly easy to hit. On top of that, Ships are able to use two
	 * weapons. Thus, the guns have been weakened a bit compared to the Robots.
	 */
	protected void updateGunHeat() {
		
		WeaponComponent[] weapons = manager.getComponents(WeaponComponent.class);
		for (WeaponComponent weapon: weapons) {
			weapon.coolDown(getBattleRules().getGunCoolingRate()*0.5);
		}
		MineComponent[] mines = manager.getComponents(MineComponent.class);
		for (MineComponent mine: mines) {
			mine.rechargeMine(getBattleRules().getGunCoolingRate()*0.5);
		}
		
	}
	
	/**
	 * Updates the heading of all the components.
	 */
	protected void updateAllComponents() {
		IComponent[] components = manager.getComponents(IComponent.class);
		for (IComponent component: components) {
			updateComponentHeading(manager.indexOf(component), component);
		}
	}
	
	/**
	 * Update the heading of the given component.
	 * @param idx The index of the component.
	 * @param component The component itself.
	 */
	private void updateComponentHeading(int idx, IComponent component) {
		double turnRemaining = currentCommands.getComponentsTurnRemaining(idx);
		double newTurnRemaining = component.turnRadians(turnRemaining);
		currentCommands.setComponentsTurnRemaining(idx, newTurnRemaining);	

	}
	
	//*** SCAN FUNCTIONS START ***//
	/**
	 * Checks for every Radar on the Ship whether a Ship 
	 * is within its arc.
	 */
	protected void scan(List<RobotPeer> robots){
		
		RadarComponent[] radars = manager.getComponents(RadarComponent.class);
		for (RadarComponent radar: radars) { // For every radar on a ship look if it had a hit!		
			radar.updateScanArc(this);					
			//onScannedShipEvent
			checkForShipsInScan(robots, radar);
		}
		
	}
	
	/**
	 * Called by {@link #scan(List)}  The function used on each seperate Radar
	 * @param robots List of Robots that might be in scan range. Function will check whether they're valid Ships
	 * @param radar	The RadarComponent we're checking
	 */
	private void checkForShipsInScan(List<RobotPeer> robots, RadarComponent radar){
		for (RobotPeer other: robots) {
			if (isValidShip(other)) {
				ShipPeer otherShip = (ShipPeer)other;
				// Determine if the robot is inside the scanArc.
				if (radar.insideScanArc(other)) {
					Point2D origin = radar.getOrigin(this); // The point of the radar...
					double dx = otherShip.getXMiddle() - origin.getX();
					double dy = otherShip.getYMiddle() - origin.getY();
					
					double dist = Math.hypot(dx, dy);
					
					double angle = atan2(dx, -1 * dy);	//dy * -1 because for some reason naval Robocode works with negative y values
						
					IComponent cF = manager.getComponent(NavalRules.IDX_WEAPON_FRONT);
					double bearingF = Coordinates.getParallax(this, cF, other) - Utils.normalRelativeAngle(cF.getAngle());
					bearingF = Utils.normalRelativeAngle(bearingF);
					
					IComponent cB = manager.getComponent(NavalRules.IDX_WEAPON_BACK);
					double bearingB = Coordinates.getParallax(this, cB, other) - Utils.normalRelativeAngle(cB.getAngle());
					bearingB = Utils.normalRelativeAngle(bearingB);
					
					final ScannedShipEvent new_event = new ScannedShipEvent(
							getNameForEvent(other),
							other.getEnergy(),
							bearingF,
							bearingB,
							Utils.normalRelativeAngle( angle  - getBodyHeading()),
							dist,
							other.getBodyHeading(),
							other.getVelocity());
					addEvent(new_event);
				}
			}
		}
	}
	
	//*** SCAN FUNCTIONS ENDS ***//
	
	
	//*** COLLISION WITH WALL METHODS START ***//
	
	/**
	 * Function that handles the Collision with Walls.
	 * This function might be a bit shady when someone else tries to read it, so care for me to explain.
	 * There is MOST likely a better way to do this. This function was made at the start of the project 
	 * without a lot of knowledge of the rest of the project. So consider this as a v0.1 or something~ 
	 * 
	 * The idea is that different distances relative to the wall are used depending on the heading and velocity.
	 * If a Ship is going backwards, the distance between the wall and the ship's pivot has to be greater than when going forwards.
	 * Because of this I made the variables (that seriously need a better name)  OFFSET_BASED_ON_HEADING_(X/Y)_(FORWARD/BACKWARD).
	 * All that was left was figuring out from which direction the Ship hit which wall.
	 * At first I tried messing around with velocity. Stuff like if(velocity > 0) : use the forward offset, else : use the back offset
	 * I made this shady variable called bodyHeadingForCollision. I basically divided the heading in 4 segments.
	 * Afterall, if a Ship's heading is 45 degrees, it can basically be hitting any wall. But more specifically,
	 * it can hit the North or West wall only if it's moving forward, and the South or East wall only if it's moving backward.
	 * 
	 * Based on this principle I decided which wall we were colliding with.
	 */
	public void checkWallCollision() {
		boolean hitWall = false;
		double angle = 0;
		
		double bodyHeading = getBodyHeading();
		//To prevent negative headings.
		bodyHeading = Utils.normalAbsoluteAngle(bodyHeading);
				
		double bodyHeadingForCalc = bodyHeading + Math.PI/2;
		//Splitting the body heading in 4 segments to make determining which wall we've hit easier. (Please don't submit to DailyWTF, hehe.)
		// 0 = [0, PI/2]   Which means it could be hitting N or W wall when moving forward. Or S and E wall when moving backward.
		// 1 = [PI/2, PI]  W or S when forward. E or N when moving  backward.
		// 2 = [PI, 3PI/2]  S or E when forward. N or W when moving  backward.
		// 3 = [3PI/2, 0]  E or N when forward. S or W when moving  backward.

		int bodyHeadingForCollision = (int)(bodyHeading / Math.PI *2) %4;
		
		double OFFSET_BASED_ON_HEADING_X_FORWARD = Math.max(NavalRules.HALF_WIDTH_OFFSET, NavalRules.PROW_OFFSET * abs(cos(bodyHeadingForCalc)));
		double OFFSET_BASED_ON_HEADING_Y_FORWARD = Math.max(NavalRules.HALF_WIDTH_OFFSET, NavalRules.PROW_OFFSET * abs(sin(bodyHeadingForCalc)));
		double OFFSET_BASED_ON_HEADING_X_BACKWARD = Math.max(NavalRules.HALF_WIDTH_OFFSET, (NavalRules.PROW_OFFSET + NavalRules.HALF_HEIGHT_OFFSET) * abs(cos(bodyHeadingForCalc)));
		double OFFSET_BASED_ON_HEADING_Y_BACKWARD = Math.max(NavalRules.HALF_WIDTH_OFFSET, (NavalRules.PROW_OFFSET + NavalRules.HALF_HEIGHT_OFFSET) * abs(sin(bodyHeadingForCalc)));
		
		
		if(bodyHeadingForCollision == 0 || bodyHeadingForCollision == 1){
			//Right Wall Forward
			if (getX() >= getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_FORWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(Math.PI / 2 - bodyHeading);
			}
			//Left Wall Backward
			if (getX() <= OFFSET_BASED_ON_HEADING_X_BACKWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(3 * Math.PI / 2 - bodyHeading);
			}
		}
		if(bodyHeadingForCollision == 2 || bodyHeadingForCollision == 3){
			//Right Wall Backward
			if (getX() >= getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_BACKWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(Math.PI / 2 - bodyHeading);
			}
			//Left Wall Forward
			if (getX() <= (int)OFFSET_BASED_ON_HEADING_X_FORWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(3 * Math.PI / 2 - bodyHeading);
			}
		}
		if(bodyHeadingForCollision == 0 || bodyHeadingForCollision == 3){
			//Bottom Wall Backward
			if (getY() >= getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_BACKWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(-bodyHeading);
			}
			//Top Wall Forward
			if (getY() <= OFFSET_BASED_ON_HEADING_Y_FORWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(Math.PI - bodyHeading);
			}
		}
		if(bodyHeadingForCollision == 1 || bodyHeadingForCollision == 2){
			//Bottom Wall Forward
			if (getY() >= getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_FORWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(-bodyHeading);
			}
			//Top Wall Backward
			if (getY() <= OFFSET_BASED_ON_HEADING_Y_BACKWARD) {
				hitWall = true;
				angle = Utils.normalRelativeAngle(Math.PI - bodyHeading);
			}
		}

		if (hitWall) {
			finalizeWallCollision(angle);
			setState(RobotState.HIT_WALL);
			

		}
		helpShipOutOfWall(bodyHeadingForCollision, OFFSET_BASED_ON_HEADING_X_FORWARD, OFFSET_BASED_ON_HEADING_X_BACKWARD,
				OFFSET_BASED_ON_HEADING_Y_FORWARD, OFFSET_BASED_ON_HEADING_Y_BACKWARD);

	}
	
	/**
	 * After we've hit a wall, we want to go back to the position we were in last turn and set the velocity to 0.
	 * @param angle The bearing we've hit the wall at.
	 */
	private void finalizeWallCollision(double angle){
		addEvent(new HitWallEvent(angle));
		setX(lastX);
		setY(lastY);
		setBodyHeading(lastHeading);
		setVelocity(0);
	}
	
	/**
	 * Help the Ship out of the wall in case it's stuck.
	 */
	private void helpShipOutOfWall(int bodyHeadingForCollision,
			double OFFSET_BASED_ON_HEADING_X_FORWARD, double OFFSET_BASED_ON_HEADING_X_BACKWARD,
			double OFFSET_BASED_ON_HEADING_Y_FORWARD, double OFFSET_BASED_ON_HEADING_Y_BACKWARD){
		
		if(bodyHeadingForCollision == 0 || bodyHeadingForCollision == 1){
			setX((OFFSET_BASED_ON_HEADING_X_BACKWARD > getX())
					? OFFSET_BASED_ON_HEADING_X_BACKWARD
					: ((getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_FORWARD < getX()) 
							? getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_FORWARD 
								: getX()));
		}
		else if(bodyHeadingForCollision == 2 || bodyHeadingForCollision == 3){
			setX((OFFSET_BASED_ON_HEADING_X_FORWARD > getX())
					? OFFSET_BASED_ON_HEADING_X_FORWARD
					: ((getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_BACKWARD < getX()) 
							? getBattleFieldWidth() - OFFSET_BASED_ON_HEADING_X_BACKWARD
								: getX()));
		}
		if(bodyHeadingForCollision == 0 || bodyHeadingForCollision == 3){
			setY((OFFSET_BASED_ON_HEADING_Y_FORWARD > getY())
					? OFFSET_BASED_ON_HEADING_Y_FORWARD
					: ((getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_BACKWARD < getY()) 
							? getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_BACKWARD
								: getY()));
		}
		else if(bodyHeadingForCollision == 1 || bodyHeadingForCollision == 2){
			setY((OFFSET_BASED_ON_HEADING_Y_BACKWARD > getY())
					? OFFSET_BASED_ON_HEADING_Y_BACKWARD
					: ((getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_FORWARD < getY()) 
							? getBattleFieldHeight() - OFFSET_BASED_ON_HEADING_Y_FORWARD
								: getY()));
		}
	}
	
	//*** COLLISION WITH WALL METHODS END ***//
	
	/**
	 * Perform a move. Practically the same as the method it overrides.
	 * The main differences are that this version records the old X and Y values
	 * and that it updates the components as well.
	 */
	public void performMove(List<RobotPeer> robots, double zapEnergy) {

		// Reset robot state to active if it is not dead
		if (isDead()) {
			return;
		}
		
		lastX = getX();
		lastY = getY();
		
		setState(RobotState.ACTIVE);

		updateGunHeat();
		
		updateOldHeadings();

		updateAllComponents();
		updateMovement();

		// At this point, ship has turned then moved.
		// We could be touching a wall or another ship...
		// First and foremost, we can never go through a wall:
		checkWallCollision();
		// Now check for ship collision
		checkShipCollision(robots);
		
		updateIndependentComponentHeadings();
	
		if (isDead()) {
			return;
		}
		if (zapEnergy != 0) {
			zap(zapEnergy);
		}
	}
	/**
	 * Updates all the older headings, including those of the components.
	 */
	protected void updateOldHeadings(){
		lastHeading = getBodyHeading();
		for(int i = 0; i < ComponentManager.MAX_COMPONENTS; ++i){
			IComponent component = manager.getComponent(i);
			component.setLastAngle(component.getAngle());
		}
	}
	
	//*** SHIP COLLISION METHODS BEGIN***//
	
	/**
	 * Checks for collision with other ships and reacts to it. Basically avoids clipping with other Ships.
	 * Instead of using the adjustX/adjustY method, I use the previous x and y values to fall back on in case we get stuck in something.
	 * The code would get way too large if I started calculating the adjustments, like how it's done in the original Robocode.
	 * @param robots The List of Ships on the battlefield.
	 */
	protected void checkShipCollision(List<RobotPeer> robots) {
		inCollision = false;
		boolean atFault = false;	// <--- Ship that did the damage
		for (RobotPeer otherRobot : robots) {
			if (isValidShip(otherRobot)) {
				ShipPeer otherShip = (ShipPeer)otherRobot;
				if (Collision.collide(this, otherShip)) {
					// Bounce back
					double angle = atan2(otherShip.getXMiddle() - getXMiddle(), 
										otherShip.getYMiddle() - getYMiddle());	//Make sure that the angle of impact is calculated from the point in the middle, not from the pivot
										
					atFault = false;
					Map<Integer, List<Integer>> pointsOfCollision = Collision.getCollisionPoints(this, otherRobot);
					int hitAnalysis = analyzePointsOfCollision(pointsOfCollision, otherRobot.getVelocity());
					
					//THOMA_NOTE: I might edit the damage a bit depending on which side got hit
					// For example: Take more damage when hit from the front, but take less damage when hit from the side.
					// My plan with this is to give Rammers a better place in the game
					// Since ramming someone in the normal Robocode doesn't tend to be the best strategy
					if (hitAnalysis == FRONT_HIT_SIDE || hitAnalysis == BACK_HIT_SIDE || hitAnalysis == BIG_COLLISION || hitAnalysis == PUSH_HIT) {
						hitShipAndStop();
						this.updateEnergy(-NavalRules.SHIP_HIT_DAMAGE);
						otherRobot.updateEnergy(-NavalRules.SHIP_HIT_DAMAGE);
						atFault = true;
						statistics.scoreRammingDamage(otherRobot.getName());
					}
					else if (hitAnalysis == SIDE_HIT_SIDE 			//Special cases that usually only happen when multiple ships are stuck in a corner
							|| getState() == RobotState.HIT_WALL) {	//In collision AND hitting a wall? Just stop all together. 
						this.updateEnergy(-NavalRules.SHIP_HIT_DAMAGE);
						otherRobot.updateEnergy(-NavalRules.SHIP_HIT_DAMAGE);
						hitShipAndStop();
						statistics.scoreRammingDamage(otherRobot.getName());
					}
					
					//Kill other ship if its energy equals 0
					if (otherShip.getEnergy() == 0) {
						if (otherShip.isAlive()) {
							otherShip.kill();
							final double bonus = statistics.scoreRammingKill(otherShip.getName());
								if (bonus > 0) {
									println(
											"SYSTEM: Ram bonus for killing " + this.getNameForEvent(otherShip) + ": "
											+ (int) (bonus + .5));
								}
						}
					}
					//Create the events
					addEvent(
							new HitRobotEvent(getNameForEvent(otherShip), normalRelativeAngle(angle - getBodyHeading()),
							otherShip.getEnergy(), atFault));
					otherShip.addEvent(
							new HitRobotEvent(getNameForEvent(this),
							normalRelativeAngle(PI + angle - otherShip.getBodyHeading()), getEnergy(), false));				}
			}
		}
		if (inCollision) {
			setState(RobotState.HIT_ROBOT);
		}
	}
	
	
	/**
	 * Checks whether the Ship is valid or not.  (The other Ship can't be null etc.)
	 * @param otherShip The Ship we're checking
	 * @return true if the Ship is valid. False if the Ship is invalid
	 */
	public boolean isValidShip(RobotPeer otherShip){
		if(otherShip == null){
			return false;
		}
		return otherShip != this && !otherShip.isDead() && otherShip instanceof ShipPeer;
	}
		
	private static final int COLLISION_ERROR = -1;	//Happens if there was a collision, but it can't be specified how it got hit.
	private static final int FRONT_HIT_SIDE = 0;	//Happens when the front of the ship hit the side of another Ship
	private static final int BACK_HIT_SIDE = 1;		//Happens when the back of the ship hit the side of another Ship
	private static final int BIG_COLLISION = 2; 	//Happens when both front ends or both back ends collide
	private static final int PUSH_HIT = 3;			//Happens when you're basically pushing the other ship. Front hitting the other Ship's back or vice versa.
	private static final int SIDE_HIT_SIDE = 4;		//Rare case: Sides of 2 ships collide
		
	/**
	 * Analyzes the PointsOfCollision and returns and integer based on where and how it got hit.
	 * The sides of a Ship are like so:     	  1
	 * 											/  \
	 * 										0	|  |  2
	 * 											|__|
	 * 											 3	
	 * So for example: If point 1 of Ship1 hits point 1 of Ship2, then they were sailing head first into eachother.
	 * Reason this method was used was so that different kinds of reaction could be given to different kinds of events.
	 * Like if they hit eachother head first, then they could for example take more damage.
	 * I'm still busy deciding what the damage calculations will be.
	 * @param pointsOfCollision The points of collision between two ships
	 * @return An integer that tells the user how it has been hit. These integers can be found above this function.
	 */
	private int analyzePointsOfCollision(Map<Integer, List<Integer>> pointsOfCollision, double otherVelocity){
		if(getVelocity() > 0 && (pointsOfCollision.get(1).contains(2) || pointsOfCollision.get(1).contains(0))){
			return FRONT_HIT_SIDE;
		}
		else if(getVelocity() < 0 && (pointsOfCollision.get(3).contains(2) || pointsOfCollision.get(3).contains(0))){
			return BACK_HIT_SIDE;
		}
		else if(( getVelocity() > 0 && pointsOfCollision.get(1).contains(1) ) 
				|| ( getVelocity() < 0 && pointsOfCollision.get(3).contains(3) )){
			return BIG_COLLISION;
		}
		else if((getVelocity() > 0 && otherVelocity > 0 && (pointsOfCollision.get(1).contains(3))) 
				|| (getVelocity() < 0 && otherVelocity < 0 && (pointsOfCollision.get(3).contains(1)))){
			return PUSH_HIT;
		}
		else if(pointsOfCollision.get(0).contains(2) || pointsOfCollision.get(2).contains(0)){
			return SIDE_HIT_SIDE;
		}
		else{
			return COLLISION_ERROR;
		}
	}
	
	/**
	 * Resets the position of the ship and sets the velocity to 0
	 */
	private void hitShipAndStop(){
		inCollision = true;
		setVelocity(0);
		
		setBodyHeading(lastHeading);
		
		setX(lastX);
		setY(lastY);
	}
		
	/**
	 * Resets the position of the ship, but slows the ship down instead.
	 * The idea was that when ramming a Ship from behind with your front,
	 * both of the ships would slow down. What happened instead was that
	 * Ships would pass through eachother when hit from behind
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void hitShipAndSlowDown(ShipPeer otherShip){
		inCollision = true;
		setVelocity(otherShip.getVelocity() * 0.9);	
		
		setBodyHeading(lastHeading);
			
		setX(lastX);
		setY(lastY);
	}
	
	//*** SHIP COLLISION METHODS END***//
	
	/**
	 * Fires the bullets the Ship has yet to fire based on the given BulletCommands.
	 */
	protected void fireBullets(List<BulletCommand> bulletCommands) {
		BulletPeer newBullet = null;
		BulletCommandShip bCommand;
			for (BulletCommand bulletCmd : bulletCommands) {
				if(bulletCmd instanceof BulletCommandShip){
					bCommand = (BulletCommandShip)bulletCmd;
				}
				else{
					System.err.println("ERROR: ShipPeer.fireBullets: BulletCommands for Robots used for Ship");
					break;
				}
				IComponent component = manager.getComponent(bCommand.getIndexComponent());
				WeaponComponent weaponComponent;
				if(component instanceof WeaponComponent){
					weaponComponent = (WeaponComponent)component;
				}
				else{
					System.err.println("ERROR: ShipPeer.fireBullets: Component isn't a weapon");
					break;
				}
				if (weaponComponent.getGunHeat() > 0 || getEnergy() == 0) {
					return;
				}
		
			double firePower = weaponComponent.getFirePower();
			updateEnergy(-firePower);
			weaponComponent.updateGunHeat(NavalRules.getGunHeat(firePower));
			newBullet = new BulletPeer(this, getBattleRules(), bulletCmd.getBulletId(), weaponComponent.getBulletColor().getRGB());
			newBullet.setPower(firePower);
			newBullet.setHeading(weaponComponent.getFireAngle(this));
			newBullet.setX(component.getOrigin(this).getX());
			newBullet.setY(component.getOrigin(this).getY());
			battle.addBullet(newBullet);
		}	
			
	}
	
	/**
	 * Places the Mines the Ship still needs to place based on the given MineCommands
	 * @param mineCommands A list of MineCommands, telling the Ship which Mines still need to be placed.
	 */
	protected void placeMines(List<MineCommand> mineCommands){
		MineComponent[] components = manager.getComponents(MineComponent.class);
		MineComponent mineComponent;
		if(components[0] == null){
			println("SYSTEM: Your ship doesn't use a MineComponent");
			return;
		}
		//So far only uses one. Don't think it'll use more in the future
		mineComponent = components[0];

		MinePeer newMine = null;

		for (MineCommand mineCmd : mineCommands) {
			if (Double.isNaN(mineCmd.getPower())) {
				println("SYSTEM: You cannot call fire(NaN)");
				continue;
			}
			if (mineComponent.getMineRecharge() > 0 || getEnergy() == 0) {
				return;
			}
			Point2D origin = mineComponent.getOrigin(this);
			double mineX = origin.getX() - (Math.sin(getBodyHeading()) * 80 + Math.PI/2);
			double mineY = origin.getY() + (Math.cos(getBodyHeading()) * 80 + Math.PI/2);
			
			//Don't place a mine outside the battlefield
			if(mineX > 0 && mineX < getBattleFieldWidth() && mineY > 0 && mineY < getBattleFieldHeight()){
				double firePower = min(getEnergy(),
						min(max(mineCmd.getPower(), NavalRules.MIN_MINE_POWER), NavalRules.MAX_MINE_POWER));
	
				updateEnergy(-firePower);
				mineComponent.setMineRecharge(NavalRules.getMineRecharge(mineCmd.getPower()));

				newMine = new MinePeer(this, getBattleRules(), mineCmd.getMineId());
				newMine.setPower(mineCmd.getPower());
				newMine.setX(mineX);
				newMine.setY(mineY);
				battle.addMine(newMine);
				mineCommands.remove(mineCmd);
				break;
			}
		}	
	}
	
	/**
	 * To make sure Components keep facing the same direction when the Ship turns.
	 * @param rotation The rotation the Ship is making
	 */
	private void updateIndependentComponentHeadings(){
		IComponent[] components = manager.getComponents(IComponent.class);
		for (int index = 0; index < components.length; ++index) {
			if(currentCommands.isAdjustComponentForShip(index)){
				currentCommands.setComponentsTurnRemaining(index, currentCommands.getComponentsTurnRemaining(index) - (getBodyHeading() - lastHeading) );
			}
		}
	
	}
	
	public ComponentManager getComponents(){
		return manager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateMovement() {
		double heading = getBodyHeading();      // Current heading of the ship.
		double distanceRemaining = currentCommands.getDistanceRemaining();
		double turnRemaining = currentCommands.getBodyTurnRemaining();
		double velocity = getVelocity();
		
		
		// Can't turn if there's no velocity
		if(!Utils.isNear(velocity, 0.0))
		{
			double rotation = NavalRules.MAX_TURN_RATE_RADIANS;

			if(turnRemaining == Double.POSITIVE_INFINITY || turnRemaining == Double.NEGATIVE_INFINITY ||
					turnRemaining == Double.MAX_VALUE || turnRemaining == -Double.MAX_VALUE){
				if(turnRemaining == Double.NEGATIVE_INFINITY || turnRemaining == -Double.MAX_VALUE){
					rotation *= -1;
				}
				
				setBodyHeading(heading + rotation);
			}
			else{
				if(rotation > abs(turnRemaining)){
					rotation = abs(turnRemaining);
				}
				//To make sure it's going the right direction
				rotation = signum(turnRemaining) * rotation;
				
				setBodyHeading(heading + rotation);
				currentCommands.setBodyTurnRemaining(turnRemaining - rotation );
			}
			
		}
		
		// 1. Velocity - Acceleration/Deceleration
		{
		
			if (Double.isNaN(distanceRemaining)) {
				distanceRemaining = 0;
			}
			
			velocity = getNewVelocity(velocity, distanceRemaining);
	
			// If we are over-driving our distance and we are now at velocity=0
			// then we stopped.
			if (isNear(velocity, 0) && isOverDriving) {
				currentCommands.setDistanceRemaining(0);
				distanceRemaining = 0;
				isOverDriving = false;
			}
			
			// If we are moving normally and the breaking distance is more
			// than remaining distance, enabled the overdrive flag.
			if (Math.signum(distanceRemaining * velocity) != -1) {
				if (getDistanceTraveledUntilStop(velocity) > Math.abs(distanceRemaining)) {
					isOverDriving = true;
				} else {
					isOverDriving = false;
				}
			}
	
			setVelocity(velocity);
		}
		
		
		
		if(velocity != 0 && distanceRemaining != 0){
			double dx = (velocity * sin(heading));
			double dy = (velocity * cos(heading));
			setX(getX() + dx);
			setY(getY() - dy);
		}
	
		
		currentCommands.setDistanceRemaining(distanceRemaining - velocity);
		
	}
	/**
	 * Returns the X value the middle of the Ship has.
	 * The Ship uses a pivot which is not equal to the middle.
	 * @return The X value of the middle of the Ship.
	 */
	protected double getXMiddle(){
		return getX() + (NavalRules.PROW_OFFSET * Math.cos(getBodyHeading() + Math.PI/2));
	}
	/**
	 * Returns the Y value the middle of the Ship has.
	 * The Ship uses a pivot which is not equal to the middle.
	 * @return The Y value of the middle of the Ship.
	 */
	protected double getYMiddle(){
		return getY() + (NavalRules.PROW_OFFSET * Math.sin(getBodyHeading() + Math.PI/2));
	}
	
	/**
	 * Same as {@link RobotPeer#publishStatus(long)} .
	 * Only difference is that this one creates a ShipStatus instead of a RobotStatus object.
	 */
	public void publishStatus(long currentTurn) {

		final ExecCommands currentCommands = commands.get();

		int others = battle.countActiveParticipants() - (isDead() || isSentryRobot() ? 0 : 1);

		ShipStatus stat = HiddenAccess.createStatus(getEnergy(), getX(), getY(), getBodyHeading(), getVelocity(),
				currentCommands.getBodyTurnRemaining(), 
				 currentCommands.getDistanceRemaining(), others, 
				battle.getRoundNum(), battle.getNumRounds(), battle.getTime(),
				manager);

		status.set(stat);
	}
	
	/**
	 * Same as {@link RobotPeer#startRound(long, int)} .
	 * Only difference is that this one creates a ShipStatus instead of a RobotStatus object.
	 */
	public void startRound(long waitMillis, int waitNanos) {
		Logger.logMessage(".", false);

		statistics.reset();

		ExecCommands newExecCommands = new ExecCommands();

		// Copy the colors from the last commands.
		// Bugfix [2628217] - Robot Colors don't stick between rounds.
		newExecCommands.copyColors(commands.get());

		currentCommands = newExecCommands;

		int others = battle.countActiveParticipants() - (isAlive() ? 1 : 0);
		
		ShipStatus stat = HiddenAccess.createStatus(getEnergy(), getX(), getY(), getBodyHeading(), getVelocity(),
				currentCommands.getBodyTurnRemaining(), 
				 currentCommands.getDistanceRemaining(), others, 
				battle.getRoundNum(), battle.getNumRounds(), battle.getTime(),
				manager);

		status.set(stat);
		robotProxy.startRound(currentCommands, stat);

		synchronized (isSleeping) {
			try {
				// Wait for the robot to go to sleep (take action)
				isSleeping.wait(waitMillis, waitNanos);
			} catch (InterruptedException e) {
				logMessage("Wait for " + getName() + " interrupted.");

				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
		if (!isSleeping() && !battle.isDebugging()) {
			logMessage("\n" + getName() + " still has not started after " + waitMillis + " ms... giving up.");
		}
	}
	
	/**
	 * Ship collision is calculated differently from Robot collision
	 * Which is why this function needed to be extended to work for Ships.
	 */
	protected boolean validSpot(List<RobotPeer> ships){
		//If in collision with a wall
		if(getX() < NavalRules.HALF_HEIGHT_OFFSET || getX() > getBattleFieldWidth() - NavalRules.HALF_HEIGHT_OFFSET
				|| getY() < NavalRules.HALF_HEIGHT_OFFSET || getY() > getBattleFieldHeight() - NavalRules.HALF_HEIGHT_OFFSET){
			return false;
		}
		for (RobotPeer otherShip : ships) {
			if (otherShip != null && otherShip != this) {
				//If in collision with another Ship
				if(Collision.collide(this, otherShip)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns the new velocity based on the current velocity and distance to move.
	 * Copy pasted to work with the NavalRules.
	 *
	 * @param velocity the current velocity
	 * @param distance the distance to move
	 * @return the new velocity based on the current velocity and distance to move
	 * 
	 * This is Patrick Cupka (aka Voidious), Julian Kent (aka Skilgannon), and Positive's method described here:
	 *   http://robowiki.net/wiki/User:Voidious/Optimal_Velocity#Hijack_2
	 */
	private double getNewVelocity(double velocity, double distance) {
		if (distance < 0) {
			// If the distance is negative, then change it to be positive
			// and change the sign of the input velocity and the result
			return -getNewVelocity(-velocity, -distance);
		}

		final double goalVel;

		if (distance == Double.POSITIVE_INFINITY) {
			goalVel = currentCommands.getMaxVelocity();
		} else {
			goalVel = Math.min(getMaxVelocity(distance), currentCommands.getMaxVelocity());
		}

		if (velocity >= 0) {
			return Math.max(velocity - NavalRules.DECELERATION, Math.min(goalVel, velocity + NavalRules.ACCELERATION));
		}
		// else
		return Math.max(velocity - NavalRules.ACCELERATION, Math.min(goalVel, velocity + maxDecel(-velocity)));
	}
	
	/**
	 * Extended from RobotPeer to work with NavalRules instead.
	 * @param distance The distance you want to travel
	 * @return The maximum velocity you'll reach during your travel.
	 */
	private final static double getMaxVelocity(double distance) {
		final double decelTime = Math.max(1, Math.ceil(// sum of 0... decelTime, solving for decelTime using quadratic formula
				(Math.sqrt((4 * 2 / NavalRules.DECELERATION) * distance + 1) - 1) / 2));

		if (decelTime == Double.POSITIVE_INFINITY) {
			return NavalRules.MAX_VELOCITY;
		}

		final double decelDist = (decelTime / 2.0) * (decelTime - 1) // sum of 0..(decelTime-1)
				* NavalRules.DECELERATION;

		return ((decelTime - 1) * NavalRules.DECELERATION) + ((distance - decelDist) / decelTime);
	}
	
	private static double maxDecel(double speed) {
		double decelTime = speed / NavalRules.DECELERATION;
		double accelTime = (1 - decelTime);

		return Math.min(1, decelTime) * NavalRules.DECELERATION + Math.max(0, accelTime) * NavalRules.ACCELERATION;
	}
	
	private List<MineStatus> readoutMines() {
		return mineUpdates.getAndSet(new ArrayList<MineStatus>());
	}
	
	@Override
	public void cleanup(){
		super.cleanup();
		mineUpdates = null;
	}
	
	void addMineStatus(MineStatus mineStatus) {
		if (isAlive()) {
			mineUpdates.get().add(mineStatus);
		}
	}
	
	public final ExecResults executeImpl(ExecCommands newCommands) {
		ExecResults roboResults = super.executeImpl(newCommands);
		roboResults.setMineUpdates(readoutMines());
		return roboResults;
	}
	
	public final ExecResults waitForBattleEndImpl(ExecCommands newCommands) {
		ExecResults roboResults = super.waitForBattleEndImpl(newCommands);
		roboResults.setMineUpdates(readoutMines());
		return roboResults;
	}
}
