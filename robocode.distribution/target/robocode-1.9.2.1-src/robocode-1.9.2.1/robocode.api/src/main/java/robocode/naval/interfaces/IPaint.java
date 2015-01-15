package robocode.naval.interfaces;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Describes methods used to draw components.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public interface IPaint {
	
	/**
	 * Sets a transformation for the object to paint.
	 * @param Tx The transformation to apply.
	 */
	void setTransform(AffineTransform Tx);
	
	/**
	 * Paints an object.
	 * @param g The device to paint the object to.
	 */
	void paint(Graphics2D g);
}
