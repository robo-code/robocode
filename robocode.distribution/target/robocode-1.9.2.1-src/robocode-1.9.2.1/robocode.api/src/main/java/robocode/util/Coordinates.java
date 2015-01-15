package robocode.util;

import static java.lang.Math.atan2;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import robocode.naval.NavalRules;
import robocode.naval.interfaces.IComponent;
import robocode.naval.interfaces.ICoordinate;
import robocode.naval.interfaces.IProjectile;
import robocode.robotinterfaces.ITransformable;
import robocode.robotinterfaces.ITransformablePeer;

/**
 * This class is responsible for transforming points into exact coordinates
 * using the {@link java.awt.geom.AffineTransform AffineTransform} class.
 * 
 * @author Thales B.V. / Jiri Waning
 * @version 1.1 (Support for the splashscreen!)
 * @since Robocode 1.8.3.0 Alpha 1 / Naval Edition
 */
public class Coordinates {
	/**
	 * {@link #getPivot(ITransformable, double, double)}
	 */
	public static final Point2D getPivot(ITransformable peer, double y) {
		return getPivot(peer, 0, y);
	}
	
	/**
	 * Get the transformed pivot point of an object on the ship.
	 * @param peer The peer for whom to calculate the pivot point.
	 * @param x The X-coordinate relative to that of the ship.
	 * @param y The Y-coordinate relative to that of the ship.
	 * @return The transformed (exact) position of the pivot point.
	 */
	public static final Point2D getPivot(ITransformable peer, double x, double y) {
		return getPivot(peer.getBodyHeading(), peer.getX(), peer.getY(), x, y);
	}
	
	/**
	 * Get the transformed pivot point of the given values.
	 * @param heading The heading of the peer.
	 * @param x1 The X-coordinate of the peer.
	 * @param y1 The Y-coordinate of the peer.
	 * @param x2 The relative X-coordinate of the component.
	 * @param y2 The relative Y-coordinate of the component.
	 * @return An {@code AffineTransform} matching the given values.
	 */
	private static final Point2D getPivot(double heading, double x1, double y1, double x2, double y2) {
		AffineTransform at = new AffineTransform();
		
		// The original 2D coordinates will be transformed and the result will be stored in the output
		Point2D origin = new Point2D.Double(x1, y1);
		Point2D output = new Point2D.Double(0.0d, 0.0d);

		// Order of execution:
		/* 2nd */at.rotate(heading, x1, y1); // Rotate around the ship's pivot rather then 0,0!
		/* 1st */at.translate(x2, y2);

		return at.transform(origin, output);
	}
	
	/**
	 * {@link #getPivotTransform(ITransformable, double, double, double)}
	 */
	public static final AffineTransform getPivotTransform(ITransformable peer, double y, double rotation) {
		return getPivotTransform(peer, 0, y, rotation);
	}
	
	/**
	 * Get the {@link java.awt.geom.AffineTransform AffineTransform}
	 * for the specified coordinates on the peer.
	 * @param peer The peer for whom to get the transformation.
	 * @param x The X-coordinate relative to that of the ship.
	 * @param y The Y-coordinate relative to that of the ship.
	 * @param rotation The amount the pivot has to rotate around its own axis.
	 * @return The {@code AffineTransform} for the specified point.
	 */
	public static final AffineTransform getPivotTransform(ITransformable peer, double x, double y, double rotation) {			
		return getPivotTransform(peer.getBodyHeading(), peer.getX(), peer.getY(), x, y, rotation);
	}
	
	/**
	 * Get the {@link java.awt.geom.AffineTransform AffineTransform} for the
	 * given values.
	 * @param heading The heading of the robot.
	 * @param x1 The X-coordinate of the robot.
	 * @param y1 The Y-coordinate of the robot.
	 * @param x2 The relative X-coordinate of the component.
	 * @param y2 The relative Y-coordinate of the component.
	 * @param rotation The angle of the component.
	 * @return An {@code AffineTranform} representing the given values.
	 */
	public static final AffineTransform getPivotTransform(double heading, double x1, double y1, double x2, double y2, double rotation) {
		AffineTransform at = new AffineTransform();
		
		// Order of execution:
		/* 4th */at.rotate(heading, x1, y1);		// [Peer] Rotate
		/* 3th */at.translate(x1, y1);				// [Peer] Translate
		/* 2nd */at.translate(x2, y2);				// [Component] Translate
		/* 1st */at.rotate(rotation, 0.0d, 0.0d);	// [Component] Rotate
		
		return at;
	}
	
	/**
	 * Get the {@link java.awt.geom.AffineTransform AffineTransform}
	 * for the ShipPeer.
	 * @param peer The peer of whom the get the {@code AffineTransform}.
	 * @return An {@code AffineTransform} for the given Ship.
	 */
	public static final AffineTransform getAffineTransformShip(ITransformablePeer peer) {
		AffineTransform at = new AffineTransform();
		
		at.translate(peer.getX() - NavalRules.HALF_WIDTH_OFFSET, peer.getY() - NavalRules.PROW_OFFSET);
		at.rotate(peer.getBodyHeading(), NavalRules.HALF_WIDTH_OFFSET, NavalRules.PROW_OFFSET);
		return at;
	}
	
	/**
	 * Get the {@link java.awt.geom.AffineTransform AffineTransform} from the given values.
	 * @param heading The heading of the robot.
	 * @param x The X-coordinate of the robot.
	 * @param y The Y-coordinate of the robot.
	 * @return The {@code AffineTransform} matching the given values.
	 */
	public static final AffineTransform getAffineTransform(double heading, double x, double y) {
		return AffineTransform.getRotateInstance(heading, x, y);
	}
	
	/**
	 * Get the parallax (angle) between the component and the projectile.
	 * <p/>
	 * <i>The angle is relative based upon the component. (With angle 0 original facing!)</i>
	 * @param peer The robot to whom the component belongs to.
	 * @param component The component from whom to measure the parallax to a projectile.
	 * @param projectile The incoming projectile from whom to find the parallax.
	 * @return The relative angle between the {@code component} and the {@code projectile}.
	 */
	public static double getParallax(ITransformable peer, IComponent component, IProjectile projectile) {
		return getParallax(peer, component, (ICoordinate) projectile);
	}
	
	/**
	 * Get the parallax (angle) between the component and the other robot.
	 * <p/>
	 * <i>The angle is relative based upon the component. (With angle 0 original facing!)</i>
	 * @param peer The robot to whom the component belongs to.
	 * @param component The component from whom to measure the parallax to a other robot.
	 * @param otherPeer The other robot. (hostile)
	 * @return The relative angle between the {@code component} and the {@code otherPeer}.
	 */
	public static double getParallax(ITransformable peer, IComponent component, ITransformable otherPeer) {
		return getParallax(peer, component, (ICoordinate) otherPeer);
	}
	
	/**
	 * Get the parallax (angle) between the component and the other coordinates.
	 * <p/>
	 * <i>The angle is relative based upon the component. (With angle 0 facing north!)</i>
	 * @param peer The robot to whom the component belongs to.
	 * @param component The component from whom to measure the parallax to the given coordinates.
	 * @param projectile The coordinates from whom to find the parallax.
	 * @return The relative angle between the {@code component} and the {@code other}; in radians.
	 */
	protected static double getParallax(ITransformable peer, IComponent component, ICoordinate other) {
		Point2D origin = component.getOrigin(peer);
		
		double dx = other.getX() - origin.getX();
		double dy = other.getY() - origin.getY();
		double ang = atan2(dx, -dy);
		
		return Utils.normalRelativeAngle(ang - peer.getBodyHeading());
	}
}
