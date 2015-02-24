package robocode.robotinterfaces.peer;

import java.awt.Color;

import robocode.Bullet;
import robocode.Mine;
import robocode.naval.BlindSpot;

/**
 * An interface that describes all the functions in ShipPeer 
 * that are available the call from Ship.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public interface IBasicShipPeer extends IBasicRobotPeer {
	
	/**
	 * Rotates the component by the specified angle in degrees.
	 * @param index The index of the component that has to rotate.
	 * @param angle The angle to turn the component in degrees.
	 */
	void rotate(int index, double angle);

	
	/**
	 * {@link robocode.naval.WeaponComponent#fire(robocode.robotinterfaces.ITransformable) WeaponComponent.fire()}
	 * @param index The index of the weapon that has to be fired.
	 * @return A Bullet that give a variety of useful variables
	 */
	Bullet fireComponent(int index);
	
	/**
	 * Same as {@link IBasicShipPeer#fireComponent(int) fireComponent()} 
	 * This function only executes once the {@link IBasicShipPeer#execute() execute()} function has been called, instead.
	 * {@link robocode.naval.WeaponComponent#fire(robocode.robotinterfaces.ITransformable) WeaponComponent.fire()}
	 * @param index The index of the weapon that has to be fired.
	 * @return A Bullet that give a variety of useful variables
	 */
	Bullet setFireComponent(int index);		
	
	/**
	 * Returns the angleRemaining of the given index in radians
	 * @param index The index of the component you want to know the remaining angle of
	 * @return The angleRemaining of the given componentindex in radians
	 */
	double getAngleRemainingComponent(int index);
	
	/**
	 * Sets whether the component is independent or not
	 * @param index The index of the component you want to change the dependency of
	 * @param independent true for independent movement, false if you want the component to be stuck to the ship.
	 */
	void setAdjustComponentForShipTurn(int index, boolean independent);
		
	/**
	 * Places a mine with the given power from the component with the given index.
	 * @param power	The power of the mine
	 * @param index The index of the component that drops the mine
	 * @return A {@link Mine} object that contains a variety of useful variables
	 */
	Mine mine(double power, int index);
	/**
	 * Places a mine with the given power from the component with the given index.
	 * Same as {@link IBasicShipPeer#mine(double, int) mine(double, int)}, but
	 * drops the mine after the {@link IBasicShipPeer#execute() execute()} function is called.
	 * @param power	The power of the mine
	 * @param index The index of the component that drops the mine
	 * @return A {@link Mine} object that contains a variety of useful variables
	 */
	Mine setMine(double power, int index);
	
	/**
	 * Sets the color of the specified component
	 * @param index The index of the component
	 * @param color the color you want to give the component
	 */
	void setComponentColor(int index, Color color);
	
	/**
	 * Sets the firepower for the specified component
	 * Only works for WeaponComponents
	 * @param index	The index of the component
	 * @param firepower	The firepower you want to give the component
	 */
	void setFirePowerComponent(int index, double firepower);
	/**
	 * Returns the firepower for the specified component
	 * Only works for WeaponComponents
	 * @param index the index of the component
	 * @return the firePower of the component
	 */
	double getFirePowerComponent(int index);
	
	/**
	 * Returns the gunheat of the specified component
	 * Only works for WeaponComponents
	 * @param index the index of the specified component
	 * @return The gunheat of the component at the specified index.
	 */
	double getGunHeatComponent(int index);
	
	/**
	 * Returns the angle of the specified component in Radians
	 * @param index	The index of the component you want to get the angle from
	 * @return the current angle in radians of the component at the given index
	 */
	double getAngleComponentRadians(int index);
	
	/**
	 * Returns the {@link BlindSpot} of the specified WeaponComponent
	 * @param index The index of the specified WeaponComponent
	 * @return the {@link BlindSpot} of the specified WeaponComponent
	 */
	BlindSpot getBlindSpotWeapon(int index);
	
	/**
	 * Decides whether the specified WeaponComponent is at the edge of its {@link BlindSpot}
	 * @param index The index of the specified WeaponComponent
	 * @return true if at the edge of the {@link BlindSpot}, false otherwise.
	 */
	boolean getAtBlindSpot(int index);
	
	/**
	 * Sets the color of the Scan.
	 * Only works for RadarComponents
	 * @param index The index of the Radar you want to specify the Scan color for
	 * @param color The color you want the give the scan of the Radar.
	 */
	void setScanColor(int index, Color color);
	
	/**
	 * Sets the color for the bullets shot from a {@link WeaponComponent}
	 * Only works for WeaponComponents. Can be set for seperate WeaponComponents.
	 * @param index The index of the {@link WeaponComponent} you want to set the color of the bullets for
	 * @param color The color you want to give the bullets.
	 */
	void setBulletColor(int index, Color color);

}
