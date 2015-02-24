package robocode.naval.interfaces;

import robocode.Event;
import robocode.HitByMineEvent;
import robocode.MineHitEvent;
import robocode.MineHitMineEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.ScannedShipEvent;
import robocode.robotinterfaces.IBasicEvents3;

/**
 * Third extended version of the {@link IBasicEvents} interface.
 *
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 *
 * @since 1.8.3.0 Alpha 1
 */
public interface IBasicEvents4 extends IBasicEvents3 {	
	/**
	 * This method is called when your robot sees another robot, i.e. when the
	 * robot's radar scan "hits" another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event. (Almost all robots should override this!)
	 * <p/>
	 * This is an extension on the {@link IBasicEvents#onScannedRobot(ScannedRobotEvent) onScannedRobot}
	 * event. Rather then having the bearing from the center of your ship to
	 * the other ship. This event contains the bearings for both the front
	 * and the back canon. The only thing you have to do is rotate them by
	 * the given amount of degrees.
	 *
	 * @param event the scanned-robot event set by the game
	 * @see IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 * @see Event
	 * @see Rules#RADAR_SCAN_RADIUS
	 */
	void onScannedShip(ScannedShipEvent event);
	
	/**
	 * Triggers when your Mine has hit another Mine.
	 * @param event You can use this event to retrieve the Mine that hit and the Mine that got hit.
	 */
	void onMineHitMine(MineHitMineEvent event);
	
	/**
	 * Triggers when your Mine has hit a Ship (Can be your own Ship)
	 * @param event Has information like the Ship you hit, the Mine that hit and the energy left on that Ship.
	 */
	void onMineHit(MineHitEvent event);
	
	/**
	 * Triggers when your Ship get hit by a Mine. 
	 * @param event Contains the Mine that hit you, not much else.
	 */
	void onHitByMine(HitByMineEvent event);
}
