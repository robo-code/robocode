package robocode.naval.interfaces;

import robocode.naval.ComponentManager;

/**
 * Describes methods to distribute components.
 * thoma: IHostingRobotProxy extends from this class so that getComponents can be called in ShipPeer.
 * @see robocode.naval.ManageableComponents
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public interface IManageableComponents {
	
	/**
	 * Get the component manager of the robot.
	 * @return The component manager of the robot.
	 */
	ComponentManager getComponents();
}
