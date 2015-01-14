package robocode.naval;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
//import java.nio.ByteBuffer;

import robocode.naval.interfaces.IProjectile;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Collision;
import robocode.util.Coordinates;
//import net.sf.robocode.security.IHiddenComponentHelper;
//import net.sf.robocode.serialization.ISerializableHelper;
//import net.sf.robocode.serialization.RbSerializer;

/**
 * A class that resembles a radar component for on a ship.
 * Thomas:
 * This is the old RadarComponent, so let me tell you a story. 
 * In the old days, the scansize was static, which is why this class go replaced.
 * Also, I had to remove all the serializable thingies, since the components have no need for a HiddenHelper.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public class RadarComponentOld extends ComponentBase {
	private static final long serialVersionUID = 2L;
		
	/**
	 * The scan arc of the radar.
	 */
	private Arc2D.Double scanArc = new Arc2D.Double();
	
	/** Used during the serialization! **/
	private RadarComponentOld() {
		super(new Point2D.Double(0.0d, 0.0d), ComponentType.UNDEFINED, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 */
	public RadarComponentOld(double x, double y) {
		super(new Point2D.Double(x, y), ComponentType.RADAR_LONG_DYNAMIC, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 * @param type The type of radar component.
	 */
	public RadarComponentOld(double x, double y, ComponentType type) {
		super(new Point2D.Double(x, y), type, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param pivot The pivot point of the component.
	 * @param type The type of radar component.
	 */
	public RadarComponentOld(Point2D pivot, ComponentType type) {
		super(pivot, type, 0.0d);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double turnRadians(double turnRemaining) {
		double turnRate = NavalRules.RADAR_TURN_RATE_RADIANS;
		if (turnRemaining > 0) {
			if (turnRemaining < turnRate) {
				angle += turnRemaining; 
				return 0;
			} else {
				angle += turnRate;
				return turnRemaining - turnRate;
			}
		} else if (turnRemaining < 0) {
			if (turnRemaining > -turnRate) {
				angle += turnRemaining;
				return 0;
			} else {
				angle += -turnRate;
				return turnRemaining + turnRate;
			}
		} else{
			return 0;
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
//	@Override
//	protected IPaint getImage(IRenderImages manager) {
//		return manager.getColoredRadarRenderNavalImage(getColor().getRGB());
//	}
	
//	/**
//	 * Creates a hidden radar component helper for accessing hidden methods on this object.
//	 * 
//	 * @return a hidden radar component helper.
//	 */
//	// this method is invisible on RobotAPI
//	static IHiddenComponentHelper createHiddenHelper() {
//		return new HiddenRadarHelper();
//	}
//
//	/**
//	 * Creates a hidden radar component helper for accessing hidden methods on this object.
//	 *
//	 * @return a hidden radar component helper.
//	 */
//	// this class is invisible on RobotAPI
//	static ISerializableHelper createHiddenSerializer() {
//		return new HiddenRadarHelper();
//	}
	
	
	// ======================================================================
	//  Scan arc
	// ======================================================================
	
	/**
	 * {@inheritDoc}
	 */
	protected void render(Graphics2D g, ITransformable peer, boolean scanArcs) {
		if (scanArcs) {
			drawScanArc(g, peer);
		}
	}
	
	/**
	 * Update the scan arc of the radar.
	 * @param peer The peer to whom the radar belongs.
	 */
	public void updateScanArc(ITransformable peer) {
			Point2D pivot = getPivot(); // relative
			Point2D point = Coordinates.getPivot(peer, pivot.getX(), pivot.getY()); // absolute
			scanArc.setArc(
					/* x */point.getX() - NavalRules.RADAR_SCAN_RADIUS,
					/* y */point.getY() - NavalRules.RADAR_SCAN_RADIUS,
					/* w */2 * NavalRules.RADAR_SCAN_RADIUS,
					/* h */2 * NavalRules.RADAR_SCAN_RADIUS,
					/* s */180.0 * ((PI * 2) - (peer.getBodyHeading() + getAngle() - (PI * 3 / 8))) / PI,
					/* e */ NavalRules.RADAR_EXTENT_DEGREES,
					/* t */Arc2D.PIE);
		
	}

	/**
	 * Draws the scan arc of the radar.
	 * @param g The device to draw too.
	 * @param peer The robot to whom the radar belongs.
	 */
	private void drawScanArc(Graphics2D g, ITransformable peer) {
		// Composite
		final Composite savedComposite = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		// Color
		g.setColor(getColor());

		// Drawing
		if (abs(scanArc.getAngleExtent()) >= .5) {
			g.fill(scanArc);
		} else {
			g.draw(scanArc);
		}

		// Restore original(s)
		g.setComposite(savedComposite);
	}
	
	/**
	 * Determines whether or not the {@code peer} is inside the scan arc.
	 * @param peer The peer to determine from if it is inside the scan arc.
	 * @return {@code true} when the peer is in the scan arc; {@code false} otherwise.
	 */
	public boolean insideScanArc(ITransformable peer) {
		return Collision.insideScan(scanArc, peer);
	}
	
	/**
	 * Determines whether or not the {@code projectile} is inside the scan arc.
	 * @param projectile The projectile that has to be inside the scan arc,
	 * @return {@code true} when the projectile is in the scan arc; {@code false} otherwise.
	 */
	public boolean insideScanArc(IProjectile projectile) {
		return Collision.insideScan(scanArc, projectile);
	}
	
	
	
	
	
//	// ======================================================================
//	//  Serialize for this component.
//	// ======================================================================
//
//	// this class is invisible on RobotAPI
//	private static class HiddenRadarHelper extends HiddenComponentHelper {
//
//		/**
//		 * {@inheritDoc}
//		 */
//		public void updateSub(ComponentBase component) {
//			// Update this object...
//		}
//		
//		/**
//		 * {@inheritDoc}
//		 */
//		protected int getSize(RbSerializer serializer, Object object) {
//			int size = RbSerializer.SIZEOF_INT;
//			size += 6 * RbSerializer.SIZEOF_DOUBLE;
//			return size;
//		}
//		
//		/**
//		 * {@inheritDoc}
//		 */
//		protected void serializeSub(RbSerializer serializer, ByteBuffer buffer, Object object) {
//			RadarComponentOld radar = (RadarComponentOld) object;
//			
//			serializer.serialize(buffer, radar.scanArc.x);
//			serializer.serialize(buffer, radar.scanArc.y);
//			serializer.serialize(buffer, radar.scanArc.width);
//			serializer.serialize(buffer, radar.scanArc.height);
//			serializer.serialize(buffer, radar.scanArc.start);
//			serializer.serialize(buffer, radar.scanArc.extent);
//			serializer.serialize(buffer, radar.scanArc.getArcType());
//		}
//		
//		/**
//		 * {@inheritDoc}
//		 */
//		protected void deserializeSub(RbSerializer serializer, ByteBuffer buffer, Object component) {
//			RadarComponentOld radar = (RadarComponentOld) component;
//			
//			radar.scanArc = new Arc2D.Double(
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeDouble(buffer),
//					serializer.deserializeInt(buffer));
//		}
//
//		/**
//		 * {@inheritDoc}
//		 */
//		protected ComponentBase getObject() {
//			return new RadarComponentOld();
//		}
//	}
}
