package net.sf.robocode.security;

import robocode.ShipStatus;
import robocode.naval.ComponentManager;

/**
 * Interface for the HiddenHelper of a ShipStatus.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public interface IHiddenShipStatusHelper {
	ShipStatus createStatus(double energy, double x, double y, double bodyHeading, 
			double velocity, double bodyTurnRemaining, 
			double distanceRemaining,  int others, int roundNum, int numRounds, long time, ComponentManager components);
}
