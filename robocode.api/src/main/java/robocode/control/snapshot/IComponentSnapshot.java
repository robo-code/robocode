package robocode.control.snapshot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.naval.ComponentType;
import robocode.naval.interfaces.IPaint;
import robocode.naval.interfaces.IRenderImages;
import robocode.robotinterfaces.ITransformable;

public interface IComponentSnapshot {
	/**
	 * Get the type of the component.
	 * <p/>
	 * <i>The types differ for each class and classes may have multiple types!</i>
	 * @return The type of the component.
	 */
	ComponentType getType();
	
	/**
	 * Get the pivot point of the component as a {@link java.awt.geom.Point2D Point2D} object.
	 * <p/>
	 * <i>The point is relative to that of the ship.</i>
	 * @return The pivot point of the component.
	 */
	Point2D getPivot();
	
	/**
	 * Get the angle of the component; in radians.
	 * @return The angle of the component; in radians.
	 */
	double getAngle();	

	/**
	 * Get the color of the component.
	 * @return The color of the component.
	 */
	int getColor();
	
	double getGunHeat();
	
	byte getSerializeType();
	
	double getLastAngle();
	
	/**
	 * Get the image that the component uses.
	 * @return The image of the specific component.
	 */
	IPaint getImage(IRenderImages manager);
	
	/**
	 * {@inheritDoc}
	 */
	
	void paint(Graphics2D g, IRenderImages manager, ITransformable peer, boolean scanArcs);
	
	/**
	 * Do the component specific drawing in here.
	 * @param g The device to draw too.
	 * @param peer The robot to whom the component belongs to.
	 * @param scanArcs Indicates whether or not to draw the scan arcs.
	 */
	void render(Graphics2D g, ITransformable peer, boolean scanArcs);
}
