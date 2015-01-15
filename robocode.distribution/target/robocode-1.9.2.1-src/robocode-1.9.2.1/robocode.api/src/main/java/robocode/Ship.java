package robocode;


import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Vector;

import robocode.naval.BlindSpot;
import robocode.naval.NavalRules;
import robocode.robotinterfaces.IAdvancedEvents;
import robocode.robotinterfaces.IAdvancedRobot;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IPaintEvents;
import robocode.robotinterfaces.IPaintRobot;
import robocode.robotinterfaces.IShip;
import robocode.robotinterfaces.peer.*;
import robocode.util.Utils;

/**
 * This is the improved version of the {@link Ship} which acts far more like a ship.
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @version 0.3
 * @since 1.8.3.0 Alpha 1
 */
public class Ship extends _RobotBase implements IShip, IBasicEvents4, IPaintEvents, IPaintRobot,  IAdvancedRobot, IAdvancedEvents {
	
	//*** RADAR ***//
	/** {@inheritDoc} **/
	public void setTurnRadarLeftDegrees(double angle){
		rotate(NavalRules.IDX_CENTRAL_RADAR, -angle);
	}
	/** {@inheritDoc} **/
	public void setTurnRadarRightDegrees(double angle){
		rotate(NavalRules.IDX_CENTRAL_RADAR, angle);
	}
	/** {@inheritDoc} **/
	public void setTurnRadarLeftRadians(double angle){
		setTurnRadarLeftDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public void setTurnRadarRightRadians(double angle){
		setTurnRadarRightDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public double getRadarHeadingRadians(){
		return Math.toRadians(getRadarHeadingDegrees());
	}
	/** {@inheritDoc} **/
	public double getRadarHeadingDegrees(){
		return (getComponentHeadingDegrees(NavalRules.IDX_CENTRAL_RADAR) + getBodyHeadingDegrees()) % 360;
	}
	/** {@inheritDoc} **/
	public double getRadarTurnRemainingRadians(){
		return getComponentTurnRemainingRadians(NavalRules.IDX_CENTRAL_RADAR);
	}
	/** {@inheritDoc} **/
	public double getRadarTurnRemainingDegrees(){
		return getComponentTurnRemainingDegrees(NavalRules.IDX_CENTRAL_RADAR);
	}
	
	
	//*** FRONT CANNON ***//
	/** {@inheritDoc} **/
	public void setTurnFrontCannonLeftDegrees(double angle){
		rotate(NavalRules.IDX_WEAPON_FRONT, -angle);
	}
	/** {@inheritDoc} **/
	public void setTurnFrontCannonRightDegrees(double angle){
		rotate(NavalRules.IDX_WEAPON_FRONT, angle);
	}
	/** {@inheritDoc} **/
	public void setTurnFrontCannonLeftRadians(double angle){
		setTurnFrontCannonLeftDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public void setTurnFrontCannonRightRadians(double angle){
		setTurnFrontCannonRightDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public double getFrontCannonHeadingRadians(){
		return Math.toRadians(getFrontCannonHeadingDegrees());
	}
	/** {@inheritDoc} **/
	public double getFrontCannonHeadingDegrees(){
		return (getComponentHeadingDegrees(NavalRules.IDX_WEAPON_FRONT) + getBodyHeadingDegrees()) % 360;
	}
	/** {@inheritDoc} **/
	public BlindSpot getCopyOfBlindSpotFrontCannon(){
		return getBlindSpotComponent(NavalRules.IDX_WEAPON_FRONT);
	}
	/** {@inheritDoc} **/
	public boolean getFrontCannonAtBlindSpot(){
		return getAtBlindSpot(NavalRules.IDX_WEAPON_FRONT);
	}
	/** {@inheritDoc} **/
	public double getFrontCannonTurnRemainingRadians(){
		return getComponentTurnRemainingRadians(NavalRules.IDX_WEAPON_FRONT);
	}
	/** {@inheritDoc} **/
	public double getFrontCannonTurnRemainingDegrees(){
		return getComponentTurnRemainingDegrees(NavalRules.IDX_WEAPON_FRONT);
	}
	
	//***    BACK CANNON     ***//
	/** {@inheritDoc} **/
	public void setTurnBackCannonLeftDegrees(double angle){
		rotate(NavalRules.IDX_WEAPON_BACK, -angle);
	}
	/** {@inheritDoc} **/
	public void setTurnBackCannonRightDegrees(double angle){
		rotate(NavalRules.IDX_WEAPON_BACK, angle);
	}
	/** {@inheritDoc} **/
	public void setTurnBackCannonLeftRadians(double angle){
		setTurnBackCannonLeftDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public void setTurnBackCannonRightRadians(double angle){
		setTurnBackCannonRightDegrees(Math.toDegrees(angle));
	}
	/** {@inheritDoc} **/
	public double getBackCannonHeadingRadians(){
		return Math.toRadians(getBackCannonHeadingDegrees());
	}
	/** {@inheritDoc} **/
	public double getBackCannonHeadingDegrees(){
		return (getComponentHeadingDegrees(NavalRules.IDX_WEAPON_BACK) + getBodyHeadingDegrees()) % 360;
	}
	/** {@inheritDoc} **/
	public BlindSpot getCopyOfBlindSpotBackCannon(){
		return getBlindSpotComponent(NavalRules.IDX_WEAPON_BACK);
	}
	/** {@inheritDoc} **/
	public boolean getBackCannonAtBlindSpot(){
		return getAtBlindSpot(NavalRules.IDX_WEAPON_BACK);
	}
	/** {@inheritDoc} **/
	public double getBackCannonTurnRemainingRadians(){
		return getComponentTurnRemainingRadians(NavalRules.IDX_WEAPON_BACK);
	}
	/** {@inheritDoc} **/
	public double getBackCannonTurnRemainingDegrees(){
		return getComponentTurnRemainingDegrees(NavalRules.IDX_WEAPON_BACK);
	}
	
	
	/** {@inheritDoc} **/
	public void fireFrontCannon(double power) { setFirePower(NavalRules.IDX_WEAPON_FRONT, power); fire(NavalRules.IDX_WEAPON_FRONT); }
	/** {@inheritDoc} **/
	public void fireBackCannon(double power) { setFirePower(NavalRules.IDX_WEAPON_BACK, power); fire(NavalRules.IDX_WEAPON_BACK); }
	
	/** {@inheritDoc} **/
	public void setCourse(double angle) {
		setTurnRightDegrees(Utils.normalRelativeAngleDegrees(angle + getBodyHeadingDegrees()));
	}
	
	/** {@inheritDoc} **/
	public void setBodyColor(Color color){
		if(peer != null){
			peer.setBodyColor(color);
		}
		else{
			uninitializedException();
		}
	}
	
	/** {@inheritDoc} **/
	public void setFrontCannonColor(Color color){
		setComponentColor(color, NavalRules.IDX_WEAPON_FRONT);
	}
	/** {@inheritDoc} **/
	public void setRadarColor(Color color){
		setComponentColor(color, NavalRules.IDX_CENTRAL_RADAR);
	}
	/** {@inheritDoc} **/
	public void setBackCannonColor(Color color){
		setComponentColor(color, NavalRules.IDX_WEAPON_BACK);
	}
	
	/**
	 * Sets the BulletColor for both Front and Back Cannon. In case you're lazy.
	 * @param color The Color you want your bullets to be.
	 */
	public void setBulletColor(Color color){
		setBulletColorBack(color);
		setBulletColorFront(color);
	}
	
	/** {@inheritDoc} **/
	public void setBulletColorBack(Color color){
		if (peer != null) {
			((IBasicShipPeer) peer).setBulletColor(NavalRules.IDX_WEAPON_BACK, color);
		} else {
			uninitializedException();
		}
	}
	
	/** {@inheritDoc} **/
	public void setBulletColorFront(Color color){
		if (peer != null) {
			((IBasicShipPeer) peer).setBulletColor(NavalRules.IDX_WEAPON_FRONT, color);
		} else {
			uninitializedException();
		}
	}
	
	/** {@inheritDoc} **/
	public void setScanColor(Color color){
		if (peer != null) {
			((IBasicShipPeer) peer).setScanColor(NavalRules.IDX_CENTRAL_RADAR, color);
		} else {
			uninitializedException();
		}
	}

	/** {@inheritDoc} **/
	private void setComponentColor(Color color, int index){
		if (peer != null) {
			((IBasicShipPeer) peer).setComponentColor(index, color);
		} else {
			uninitializedException();
		}
	}
	
	//*** MOVEMENT ***//
	/** {@inheritDoc} **/
	public void setTurnLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(-radians);
		} else {
			uninitializedException();
		}
	}
	/** {@inheritDoc} **/
	public void setTurnRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(radians);
		} else {
			uninitializedException();
		}
	}
	/** {@inheritDoc} **/
	public void setTurnLeftDegrees(double angle){
		setTurnLeftRadians(Math.toRadians(angle));
	}
	/** {@inheritDoc} **/
	public void setTurnRightDegrees(double angle){
		setTurnRightRadians(Math.toRadians(angle));
	}

	/** {@inheritDoc} **/
	public void setAdjustComponentForShipTurn(int index, boolean independent){
		if (peer != null) {
			((IBasicShipPeer) peer).setAdjustComponentForShipTurn(index, independent);
		} else {
			uninitializedException();
		}
	}
	
	/** {@inheritDoc} **/
	public void setMaxKnots(double maxKnots) {
		if(peer != null){
			((IAdvancedRobotPeer) peer).setMaxVelocity(maxKnots);
		}
		else{
			uninitializedException();
		}
	}

	/**
	 * Function called internally to turn the Ship's components
	 * @param index The index of the Component
	 * @param angle The angle it needs to turn to the right
	 */
	private void rotate(int index, double angle) {
		if(peer != null){
			((IBasicShipPeer)peer).rotate(index, angle);
		}
		else{
			uninitializedException();
		}
	}

	/**
	 * Function called internally to fire a component at the specified index
	 * @param index The index of the component
	 */
	private Bullet fire(int index) {
		if (peer != null) {
			return ((IBasicShipPeer) peer).fireComponent(index);
		} else {
			uninitializedException();
		}
		return null;
	}

	/**
	 * Function called internally to set the fire power of the component at the specified index.
	 * @param index The index of the component the firepower needs to be set for
	 * @param power The fire power you want the component to be set at.
	 */
	private void setFirePower(int index, double power) {
		if (peer != null) {
			((IBasicShipPeer) peer).setFirePowerComponent(index, power);;
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns the FirePower of the specified component.
	 * Returns 0 if the Component isn't a WeaponComponent.
	 * @param index
	 * @return
	 */
	public double getFirePower(int index){
		if (peer != null) {
			return ((IBasicShipPeer) peer).getFirePowerComponent(index);
		} else {
			uninitializedException();
		}
		return 0.0;
	}
	
	
	public void scan() {
		if(peer != null){
			peer.rescan();
		}
		else{
			uninitializedException();
		}
	}
	
	/**{@inheritDoc}*/
	public void execute() {
		if(peer != null){
			peer.execute();
		}
		else{
			uninitializedException();
		}
	}

	/*
	 * Events that may be implemented by the end-user.
	 */
	/**{@inheritDoc}*/
	@Override public void onBattleEnded(BattleEndedEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onBulletHit(BulletHitEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onBulletHitBullet(BulletHitBulletEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onBulletMissed(BulletMissedEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onDeath(DeathEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onHitByBullet(HitByBulletEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onHitRobot(HitRobotEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onHitWall(HitWallEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onPaint(Graphics2D g) {}
	/**{@inheritDoc}*/
	@Override public void onRobotDeath(RobotDeathEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onRoundEnded(RoundEndedEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onScannedShip(ScannedShipEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onStatus(StatusEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onWin(WinEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onMineHitMine(MineHitMineEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onMineHit(MineHitEvent event) {}
	/**{@inheritDoc}*/
	@Override public void onHitByMine(HitByMineEvent event) {}
	
	/**
	 * @deprecated
	 * Use {@link IBasicEvents4#onScannedShip(ScannedShipEvent)
	 * onScannedShip(ScannedShipEvent event)} instead.
	 */
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {}
	
	/** {@inheritDoc} **/
	public double getVelocity(){
		if (peer != null) {
			return peer.getVelocity();
		}
		uninitializedException();
		return 0; // never called
	}
	/** {@inheritDoc} **/
	public void setAhead(double distance) {
		if (peer != null) {
			((IAdvancedRobotPeer)peer).setMove(distance);
		} else {
			uninitializedException();
		}
	}
	/** {@inheritDoc} **/
	public void setBack(double distance){
		setAhead(-distance);
	}
	/** {@inheritDoc} **/
	public double getDistanceRemaining() {
		if (peer != null) {
			return peer.getDistanceRemaining();
		}
		uninitializedException();
		return 0; // never called
	}
	/** {@inheritDoc} **/
	public double getBodyHeadingRadians(){
		if (peer != null) {
			return peer.getBodyHeading();
		}
		uninitializedException();
		return 0; // never called
	}
	/** {@inheritDoc} **/
	public double getBodyHeadingDegrees(){
		if(peer != null){
			return peer.getBodyHeading() * 180 / Math.PI;
		}
		uninitializedException();
		return 0; // never called
	}
	
	/**
	 * Returns the turnRemaining for the component at the given index in degrees.
	 * @param index The index of the component you want to know the turnRemaining of
	 * @return The amount the given component still needs to turn in degrees.
	 */
	private double getComponentTurnRemainingDegrees(int index){
		return getComponentTurnRemainingRadians(index) * 180 / Math.PI;
	}
	
	/**
	 * Returns the turnRemaining for the component at the given index in radians.
	 * @param index The index of the component you want to know the turnRemaining of
	 * @return The amount the given component still needs to turn in radians.
	 */
	private double getComponentTurnRemainingRadians(int index){
		if(peer != null){
			return ((IBasicShipPeer)peer).getAngleRemainingComponent(index);
		}
		uninitializedException();
		return 0;
	}

	/** {@inheritDoc} **/
	public double getGunHeatComponent(int index){
		if (peer != null) {
			((IBasicShipPeer) peer).getGunHeatComponent(index);
		} else {
			uninitializedException();
		}
		return 0.0;
	}

	/** {@inheritDoc} **/
	public double getComponentHeadingDegrees(int index){
		return Math.toDegrees(getComponentHeadingRadians(index));
	}
	/** {@inheritDoc} **/
	public double getComponentHeadingRadians(int index){
		if (peer != null) {
			return ((IBasicShipPeer) peer).getAngleComponentRadians(index);
		} else {
			uninitializedException();
		}	
		return 0.0;
	}
	/** {@inheritDoc} **/
	public double getX() {
		if (peer != null) {
			return peer.getX();
		}
		uninitializedException();
		return 0; // never called
	}
	/** {@inheritDoc} **/
	public double getXMiddle(){
		return getX() + (NavalRules.PROW_OFFSET * Math.cos(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getXFrontCannon(){
		return getX() + (NavalRules.FRONT_WEAPON_OFFSET * Math.cos(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getXBackCannon(){
		return getX() + (NavalRules.BACK_WEAPON_OFFSET * Math.cos(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getXRadar(){
		return getX() + (NavalRules.CENTRAL_RADAR_OFFSET * Math.cos(getBodyHeadingRadians() + Math.PI/2));
	}

	/** {@inheritDoc} **/
	public double getY() {
		if (peer != null) {
			//THOMA_NOTE: For some reason it was decided that the Y values were reversed. This should make it LOOK as if it's fixed. Might consider reversing all the Y values.
			return getBattleFieldHeight() - peer.getY();
		}
		uninitializedException();
		return 0; // never called
	}
	/** {@inheritDoc} **/
	public double getYMiddle(){
		return getY() - (NavalRules.PROW_OFFSET * Math.sin(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getYFrontCannon(){
		return getY() - (NavalRules.FRONT_WEAPON_OFFSET * Math.sin(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getYBackCannon(){
		return getY() - (NavalRules.BACK_WEAPON_OFFSET * Math.sin(getBodyHeadingRadians() + Math.PI/2));
	}
	
	public double getYRadar(){
		return getY() - (NavalRules.CENTRAL_RADAR_OFFSET * Math.sin(getBodyHeadingRadians() + Math.PI/2));
	}
	
	/** {@inheritDoc} **/
	public double getBattleFieldWidth() {
		if (peer != null) {
			return peer.getBattleFieldWidth();
		}
		uninitializedException();
		return 0; // never called
	}

	/** {@inheritDoc} **/
	public double getBattleFieldHeight() {
		if (peer != null) {
			return peer.getBattleFieldHeight();
		}
		uninitializedException();
		return 0; // never called
	}

	/** {@inheritDoc} **/
	public double getBodyTurnRemainingDegrees() {
		if (peer != null) {
			return Math.toDegrees(peer.getBodyTurnRemaining());
		}
		uninitializedException();
		return 0; // never called	
	}
	
	/** {@inheritDoc} **/
	public double getBodyTurnRemainingRadians() {
		if (peer != null) {
			return peer.getBodyTurnRemaining();
		}
		uninitializedException();
		return 0; // never called	
	}
	
	/** {@inheritDoc} **/
	public long getTime() {
		if (peer != null) {
			return peer.getTime();
		}
		uninitializedException();
		return 0; // never called
	}
	
	/** {@inheritDoc} **/
	public double getEnergy() {
		if (peer != null) {
			return peer.getEnergy();
		}
		uninitializedException();
		return 0; // never called
	}
	
	private BlindSpot getBlindSpotComponent(int index){
		if (peer != null) {
			return ((IBasicShipPeer) peer).getBlindSpotWeapon(index);
		} else {
			uninitializedException();
		}
		return null;
	}
	
	private boolean getAtBlindSpot(int index){
		if (peer != null) {
			return ((IBasicShipPeer) peer).getAtBlindSpot(index);
		} else {
			uninitializedException();
		}
		return false;
	}
	
	//CUSTOM EVENTS
	/**
	 * Registers a custom event to be called when a condition is met.
	 * When you are finished with your condition or just want to remove it you
	 * must call {@link #removeCustomEvent(Condition)}.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Create the condition for our custom event
	 *   Condition triggerHitCondition = new Condition("triggerhit") {
	 *       public boolean test() {
	 *           return (getEnergy() <= trigger);
	 *       }
	 *   }
	 *
	 *   // Add our custom event based on our condition
	 *   <b>addCustomEvent(triggerHitCondition);</b>
	 * </pre>
	 *
	 * @param condition the condition that must be met.
	 * @throws NullPointerException if the condition parameter has been set to
	 *                              {@code null}.
	 * @see Condition
	 * @see #removeCustomEvent(Condition)
	 */
	public void addCustomEvent(Condition condition) {
		if (condition == null) {
			throw new NullPointerException("the condition cannot be null");
		}
		if (peer != null) {
			((IAdvancedRobotPeer) peer).addCustomEvent(condition);
		} else {
			uninitializedException();
		}
	}
	

	/**
	 * Removes a custom event that was previously added by calling
	 * {@link #addCustomEvent(Condition)}.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Create the condition for our custom event
	 *   Condition triggerHitCondition = new Condition("triggerhit") {
	 *       public boolean test() {
	 *           return (getEnergy() <= trigger);
	 *       }
	 *   }
	 *
	 *   // Add our custom event based on our condition
	 *   addCustomEvent(triggerHitCondition);
	 *   ...
	 *   <i>do something with your robot</i>
	 *   ...
	 *   // Remove the custom event based on our condition
	 *   <b>removeCustomEvent(triggerHitCondition);</b>
	 * </pre>
	 *
	 * @param condition the condition that was previous added and that must be
	 *                  removed now.
	 * @throws NullPointerException if the condition parameter has been set to
	 *                              {@code null}.
	 * @see Condition
	 * @see #addCustomEvent(Condition)
	 */
	public void removeCustomEvent(Condition condition) {
		if (condition == null) {
			throw new NullPointerException("the condition cannot be null");
		}
		if (peer != null) {
			((IAdvancedRobotPeer) peer).removeCustomEvent(condition);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Clears out any pending events in the robot's event queue immediately.
	 *
	 * @see #getAllEvents()
	 */
	public void clearAllEvents() {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).clearAllEvents();
		} else {
			uninitializedException();
		}
	}
	
	/**
	 * Returns a vector containing all events currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (Event event : getAllEvents()) {
	 *       if (event instanceof HitRobotEvent) {
	 *           <i>// do something with the event</i>
	 *       } else if (event instanceof HitByBulletEvent) {
	 *           <i>// do something with the event</i>
	 *       }
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all events currently in the robot's queue
	 * @see Event
	 * @see #clearAllEvents()
	 * @see #getStatusEvents()
	 * @see #getScannedRobotEvents()
	 * @see #getBulletHitEvents()
	 * @see #getBulletMissedEvents()
	 * @see #getBulletHitBulletEvents()
	 * @see #getRobotDeathEvents()
	 */
	public Vector<Event> getAllEvents() {
		if (peer != null) {
			return new Vector<Event>(((IAdvancedRobotPeer) peer).getAllEvents());
		}
		uninitializedException();
		return null; // never called
	}
	
	/**
	 * Returns a vector containing all BulletHitBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (BulletHitBulletEvent event : getBulletHitBulletEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitBulletEvents currently in the
	 *         robot's queue
	 * @see #onBulletHitBullet(BulletHitBulletEvent) onBulletHitBullet(BulletHitBulletEvent)
	 * @see BulletHitBulletEvent
	 * @see #getAllEvents()
	 */
	public Vector<BulletHitBulletEvent> getBulletHitBulletEvents() {
		if (peer != null) {
			return new Vector<BulletHitBulletEvent>(((IAdvancedRobotPeer) peer).getBulletHitBulletEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all BulletHitEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (BulletHitEvent event: getBulletHitEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitEvents currently in the robot's
	 *         queue
	 * @see #onBulletHit(BulletHitEvent) onBulletHit(BulletHitEvent)
	 * @see BulletHitEvent
	 * @see #getAllEvents()
	 */
	public Vector<BulletHitEvent> getBulletHitEvents() {
		if (peer != null) {
			return new Vector<BulletHitEvent>(((IAdvancedRobotPeer) peer).getBulletHitEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all BulletMissedEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (BulletMissedEvent event : getBulletMissedEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletMissedEvents currently in the
	 *         robot's queue
	 * @see #onBulletMissed(BulletMissedEvent) onBulletMissed(BulletMissedEvent)
	 * @see BulletMissedEvent
	 * @see #getAllEvents()
	 */
	public Vector<BulletMissedEvent> getBulletMissedEvents() {
		if (peer != null) {
			return new Vector<BulletMissedEvent>(((IAdvancedRobotPeer) peer).getBulletMissedEvents());
		}
		uninitializedException();
		return null; // never called
	}
	
	/**
	 * Returns the current priority of a class of events.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority.
	 * <p/>
	 * Example:
	 * <pre>
	 *   int myHitRobotPriority = getEventPriority("HitRobotEvent");
	 * </pre>
	 * <p/>
	 * The default priorities are, from highest to lowest:
	 * <pre>
	 *   {@link RoundEndedEvent}:      100 (reserved)
	 *   {@link BattleEndedEvent}:     100 (reserved)
	 *   {@link WinEvent}:             100 (reserved)
	 *   {@link SkippedTurnEvent}:     100 (reserved)
	 *   {@link StatusEvent}:           99
	 *   Key and mouse events:  98
	 *   {@link CustomEvent}:           80 (default value)
	 *   {@link MessageEvent}:          75
	 *   {@link RobotDeathEvent}:       70
	 *   {@link BulletMissedEvent}:     60
	 *   {@link BulletHitBulletEvent}:  55
	 *   {@link BulletHitEvent}:        50
	 *   {@link HitByBulletEvent}:      40
	 *   {@link HitWallEvent}:          30
	 *   {@link HitRobotEvent}:         20
	 *   {@link ScannedRobotEvent}:     10
	 *   {@link PaintEvent}:             5
	 *   {@link DeathEvent}:            -1 (reserved)
	 * </pre>
	 *
	 * @param eventClass the name of the event class (string)
	 * @return the current priority of a class of events
	 * @see #setEventPriority(String, int)
	 */
	public int getEventPriority(String eventClass) {
		if (peer != null) {
			return ((IAdvancedRobotPeer) peer).getEventPriority(eventClass);
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns a vector containing all HitByBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing
	 * another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (HitByBulletEvent event : getHitByBulletEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitByBulletEvents currently in the
	 *         robot's queue
	 * @see #onHitByBullet(HitByBulletEvent) onHitByBullet(HitByBulletEvent)
	 * @see HitByBulletEvent
	 * @see #getAllEvents()
	 */
	public Vector<HitByBulletEvent> getHitByBulletEvents() {
		if (peer != null) {
			return new Vector<HitByBulletEvent>(((IAdvancedRobotPeer) peer).getHitByBulletEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all HitRobotEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (HitRobotEvent event : getHitRobotEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitRobotEvents currently in the robot's
	 *         queue
	 * @see #onHitRobot(HitRobotEvent) onHitRobot(HitRobotEvent)
	 * @see HitRobotEvent
	 * @see #getAllEvents()
	 */
	public Vector<HitRobotEvent> getHitRobotEvents() {
		if (peer != null) {
			return new Vector<HitRobotEvent>(((IAdvancedRobotPeer) peer).getHitRobotEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all HitWallEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (HitWallEvent event : getHitWallEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitWallEvents currently in the robot's
	 *         queue
	 * @see #onHitWall(HitWallEvent) onHitWall(HitWallEvent)
	 * @see HitWallEvent
	 * @see #getAllEvents()
	 */
	public Vector<HitWallEvent> getHitWallEvents() {
		if (peer != null) {
			return new Vector<HitWallEvent>(((IAdvancedRobotPeer) peer).getHitWallEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all RobotDeathEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (RobotDeathEvent event : getRobotDeathEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all RobotDeathEvents currently in the robot's
	 *         queue
	 * @see #onRobotDeath(RobotDeathEvent) onRobotDeath(RobotDeathEvent)
	 * @see RobotDeathEvent
	 * @see #getAllEvents()
	 */
	public Vector<RobotDeathEvent> getRobotDeathEvents() {
		if (peer != null) {
			return new Vector<RobotDeathEvent>(((IAdvancedRobotPeer) peer).getRobotDeathEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all ScannedRobotEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (ScannedRobotEvent event : getScannedRobotEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all ScannedRobotEvents currently in the
	 *         robot's queue
	 * @see #onScannedRobot(ScannedRobotEvent) onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 * @see #getAllEvents()
	 */
	public Vector<ScannedRobotEvent> getScannedRobotEvents() {
		if (peer != null) {
			return new Vector<ScannedRobotEvent>(((IAdvancedRobotPeer) peer).getScannedRobotEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a vector containing all StatusEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (StatusEvent event : getStatusEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all StatusEvents currently in the robot's queue
	 * @see #onStatus(StatusEvent) onStatus(StatusEvent)
	 * @see StatusEvent
	 * @see #getAllEvents()
	 * @since 1.6.1
	 */
	public Vector<StatusEvent> getStatusEvents() {
		if (peer != null) {
			return new Vector<StatusEvent>(((IAdvancedRobotPeer) peer).getStatusEvents());
		}
		uninitializedException();
		return null; // never called
	}
	
	/**
	 * Sets the priority of a class of events.
	 * <p/>
	 * Events are sent to the onXXX handlers in order of priority.
	 * Higher priority events can interrupt lower priority events.
	 * For events with the same priority, newer events are always sent first.
	 * Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
	 * priority.
	 * <p/>
	 * Example:
	 * <pre>
	 *   setEventPriority("RobotDeathEvent", 15);
	 * </pre>
	 * <p/>
	 * The default priorities are, from highest to lowest:
	 * <pre>
	 * 	 {@link WinEvent}:             100 (reserved)
	 * 	 {@link SkippedTurnEvent}:     100 (reserved)
	 *   {@link StatusEvent}:           99
	 * 	 {@link CustomEvent}:           80
	 * 	 {@link MessageEvent}:          75
	 * 	 {@link RobotDeathEvent}:       70
	 * 	 {@link BulletMissedEvent}:     60
	 * 	 {@link BulletHitBulletEvent}:  55
	 * 	 {@link BulletHitEvent}:        50
	 * 	 {@link HitByBulletEvent}:      40
	 * 	 {@link HitWallEvent}:          30
	 * 	 {@link HitRobotEvent}:         20
	 * 	 {@link ScannedRobotEvent}:     10
	 *   {@link PaintEvent}:             5
	 * 	 {@link DeathEvent}:            -1 (reserved)
	 * </pre>
	 * <p/>
	 * Note that you cannot change the priority for events with the special
	 * priority value -1 or 100 (reserved) as these event are system events.
	 * Also note that you cannot change the priority of CustomEvent.
	 * Instead you must change the priority of the condition(s) for your custom
	 * event(s).
	 *
	 * @param eventClass the name of the event class (string) to set the
	 *                   priority for
	 * @param priority   the new priority for that event class
	 * @see #getEventPriority(String)
	 * @see #setInterruptible(boolean)
	 * @since 1.5, the priority of DeathEvent was changed from 100 to -1 in
	 *        order to let robots process pending events on its event queue before
	 *        it dies. When the robot dies, it will not be able to process events.
	 */
	public void setEventPriority(String eventClass, int priority) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setEventPriority(eventClass, priority);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Call this during an event handler to allow new events of the same
	 * priority to restart the event handler.
	 * <p/>
	 * <p>Example:
	 * <pre>
	 *   public void onScannedRobot(ScannedRobotEvent e) {
	 *       fire(1);
	 *       <b>setInterruptible(true);</b>
	 *       ahead(100); // If you see a robot while moving ahead,
	 *                   // this handler will start from the top
	 *                   // Without setInterruptible(true), we wouldn't
	 *                   // receive scan events at all!
	 *       // We'll only get here if we don't see a robot during the move.
	 *       out.println("Ok, I can't see anyone");
	 *   }
	 * </pre>
	 *
	 * @param interruptible {@code true} if the event handler should be
	 *                      interrupted if new events of the same priority occurs; {@code false}
	 *                      otherwise
	 * @see #setEventPriority(String, int)
	 * @see Robot#onScannedRobot(ScannedRobotEvent) onScannedRobot(ScannedRobotEvent)
	 */
	public void setInterruptible(boolean interruptible) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setInterruptible(interruptible);
		} else {
			uninitializedException();
		}
	}
	
	/**
	 * Sets the robot to resume the movement stopped by {@link #stop() stop()}
	 * or {@link #setStop()}, if any.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * {@link #execute()} or take an action that executes.
	 *
	 * @see #resume() resume()
	 * @see #stop() stop()
	 * @see #stop(boolean) stop(boolean)
	 * @see #setStop()
	 * @see #setStop(boolean)
	 * @see #execute()
	 */
	public void setResume() {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setResume();
		} else {
			uninitializedException();
		}
	}

	/**
	 * This call is identical to {@link #stop() stop()}, but returns immediately, and
	 * will not execute until you call {@link #execute()} or take an action that
	 * executes.
	 * <p/>
	 * If there is already movement saved from a previous stop, this will have
	 * no effect.
	 * <p/>
	 * This call is equivalent to calling {@code setStop(false)};
	 *
	 * @see #stop() stop()
	 * @see #stop(boolean) stop(boolean)
	 * @see #resume() resume()
	 * @see #setResume()
	 * @see #setStop(boolean)
	 * @see #execute()
	 */
	public void setStop() {
		setStop(false);
	}
	
	/**
	 * This call is identical to {@link #stop(boolean) stop(boolean)}, but
	 * returns immediately, and will not execute until you call
	 * {@link #execute()} or take an action that executes.
	 * <p/>
	 * If there is already movement saved from a previous stop, you can
	 * overwrite it by calling {@code setStop(true)}.
	 *
	 * @param overwrite {@code true} if the movement saved from a previous stop
	 *                  should be overwritten; {@code false} otherwise.
	 * @see #stop() stop()
	 * @see #stop(boolean) stop(boolean)
	 * @see #resume() resume()
	 * @see #setResume()
	 * @see #setStop()
	 * @see #execute()
	 */
	public void setStop(boolean overwrite) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setStop(overwrite);
		} else {
			uninitializedException();
		}
	}
	
	/*
	 * Methods required to make the robot act and respond.
	 */
	/** {@inheritDoc} **/ @Override public Runnable getRobotRunnable() { return this; }
	/** {@inheritDoc} **/ @Override public IBasicEvents getBasicEventListener() { return this; }

	/**
	 * This is the method you have to override to create your own ship.
	 */
	@Override
	public void run() {
		
		
	}

	@Override
	public final IPaintEvents getPaintEventListener() {
		return this;
	}


	/**
	 * {@inheritDoc}
	 */
	public void onSkippedTurn(SkippedTurnEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onCustomEvent(CustomEvent event) {}

	/**
	 * Do not call this method!
	 * <p/>
	 * {@inheritDoc}
	 */
	public final IAdvancedEvents getAdvancedEventListener() {
		return this; // this robot is listening
	}
	
	/**
	 * @param power The power of the mine you want to place
	 */
	public Mine placeMine(double power){
		if (peer != null) {
			return ((IBasicShipPeer) peer).mine(power, NavalRules.IDX_MINE_PLACER);
		} else {
			uninitializedException();
		}
		return null;
	}
	
	public void setMineComponentColor(Color color){
		setComponentColor(color, NavalRules.IDX_MINE_PLACER);
	}
	
	
	//*** DATA ***//
	
	/**
	 * Returns a file representing a data directory for the robot, which can be
	 * written to using {@link RobocodeFileOutputStream} or
	 * {@link RobocodeFileWriter}.
	 * <p/>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 *
	 * @return a file representing the data directory for your robot
	 * @see #getDataFile(String)
	 * @see RobocodeFileOutputStream
	 * @see RobocodeFileWriter
	 */
	public File getDataDirectory() {
		if (peer != null) {
			return ((IAdvancedRobotPeer) peer).getDataDirectory();
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns a file in your data directory that you can write to using
	 * {@link RobocodeFileOutputStream} or {@link RobocodeFileWriter}.
	 * <p/>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 * <p/>
	 * Please notice that the max. size of your data file is set to 200000
	 * (~195 KB).
	 * <p/>
	 * See the {@code sample.SittingDuck} to see an example of how to use this
	 * method.
	 *
	 * @param filename the file name of the data file for your robot
	 * @return a file representing the data file for your robot or null if the data
	 * 		   file could not be created due to an error.
	 * @see #getDataDirectory()
	 * @see RobocodeFileOutputStream
	 * @see RobocodeFileWriter
	 */
	public File getDataFile(String filename) {
		if (peer != null) {
			return ((IAdvancedRobotPeer) peer).getDataFile(filename);
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * Returns the data quota available in your data directory, i.e. the amount
	 * of bytes left in the data directory for the robot.
	 *
	 * @return the amount of bytes left in the robot's data directory
	 * @see #getDataDirectory()
	 * @see #getDataFile(String)
	 */
	public long getDataQuotaAvailable() {
		if (peer != null) {
			return ((IAdvancedRobotPeer) peer).getDataQuotaAvailable();
		}
		uninitializedException();
		return 0; // never called
	}
	
	/**
	 * Does not return until a condition is met, i.e. when a
	 * {@link Condition#test()} returns {@code true}.
	 * <p/>
	 * This call executes immediately.
	 * <p/>
	 * See the {@code sample.Crazy} robot for how this method can be used.
	 *
	 * @param condition the condition that must be met before this call returns
	 * @see Condition
	 * @see Condition#test()
	 */
	public void waitFor(Condition condition) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).waitFor(condition);
		} else {
			uninitializedException();
		}
	}
	
	/**
	 * Returns how many opponents that are left in the current round.
	 *
	 * @return how many opponents that are left in the current round.
	 */
	public int getOthers() {
		if (peer != null) {
			return peer.getOthers();
		}
		uninitializedException();
		return 0; // never called
	}
	
	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, this will have no effect.
	 * <p/>
	 * This method is equivalent to {@code #stop(false)}.
	 *
	 * @see #resume()
	 * @see #stop(boolean)
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, you can overwrite it by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop,
	 *                  you can overwrite it by calling {@code stop(true)}.
	 * @see #resume()
	 * @see #stop()
	 */
	public void stop(boolean overwrite) {
		if (peer != null) {
			((IStandardRobotPeer) peer).stop(overwrite);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately resumes the movement you stopped by {@link #stop()}, if any.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop()
	 * @see #stop(boolean)
	 */
	public void resume() {
		if (peer != null) {
			((IStandardRobotPeer) peer).resume();
		} else {
			uninitializedException();
		}
	}
}
