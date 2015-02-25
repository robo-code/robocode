package robocode.naval;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

import robocode.robotinterfaces.ITransformable;
import robocode.util.Collision;
import robocode.util.Coordinates;
import net.sf.robocode.security.IHiddenComponentHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * A class that resembles a radar component for on a ship.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public class RadarComponent extends ComponentBase {
	private static final long serialVersionUID = 2L;
		
	/**
	 * The scan arc of the radar.
	 */
	private Arc2D.Double scanArc = new Arc2D.Double();
	private Color scanColor = Color.RED;
	
	
	/** Used during the serialization! **/
	private RadarComponent() {
		super(new Point2D.Double(0.0d, 0.0d), ComponentType.UNDEFINED, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 */
	public RadarComponent(double x, double y) {
		super(new Point2D.Double(x, y), ComponentType.RADAR_LONG_DYNAMIC, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 * @param type The type of radar component.
	 */
	public RadarComponent(double x, double y, ComponentType type) {
		super(new Point2D.Double(x, y), type, 0.0d);
	}
	
	/**
	 * Creates a new radar component for on the robot.
	 * @param pivot The pivot point of the component.
	 * @param type The type of radar component.
	 */
	public RadarComponent(Point2D pivot, ComponentType type) {
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
	
	public Color getScanColor(){
		return scanColor;
	}
	
	public void setScanColor(Color color){
		this.scanColor = color;
	}
	
	/**
	 * Creates a hidden radar component helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden radar component helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenComponentHelper createHiddenHelper() {
		return new HiddenRadarHelper();
	}

	/**
	 * Creates a hidden radar component helper for accessing hidden methods on this object.
	 *
	 * @return a hidden radar component helper.
	 */
	// this class is invisible on RobotAPI
	static ISerializableHelper createHiddenSerializer() {
		return new HiddenRadarHelper();
	}
	
	
	// ======================================================================
	//  Scan arc
	// ======================================================================

	
	public Arc2D.Double getScanArc(){
		return scanArc;
	}
	
	/**
	 * Update the scan arc of the radar.
	 * @param shipPeer The peer to whom the radar belongs.
	 */
	public void updateScanArc(ITransformable shipPeer) {
			Point2D pivot = getPivot(); // relative
			Point2D point = Coordinates.getPivot(shipPeer, pivot.getX(), pivot.getY()); // absolute
			scanArc.setArc(
					/* x */point.getX() - NavalRules.RADAR_SCAN_RADIUS,
					/* y */point.getY() - NavalRules.RADAR_SCAN_RADIUS,
					/* w */2 * NavalRules.RADAR_SCAN_RADIUS,
					/* h */2 * NavalRules.RADAR_SCAN_RADIUS,
					/* s */180.0 * ((PI/2) - (shipPeer.getBodyHeading() + getAngle())) / PI,
					/* e */ Math.toDegrees(angle - lastHeading),
					/* t */Arc2D.PIE);
		
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
	public boolean insideScanArc(Point2D projectile) {
		return Collision.insideScan(scanArc, projectile.getX(), projectile.getY());
	}
	
	
	public byte getSerializeType(){
		return RbSerializer.RadarComponent_TYPE;
	}
	
	// ======================================================================
	//  Serialize for this component.
	// ======================================================================

	// this class is invisible on RobotAPI
	private static class HiddenRadarHelper extends HiddenComponentHelper {

		/**
		 * {@inheritDoc}
		 */
		public void updateSub(ComponentBase component) {
			// Update this object...
		}
		
		/**
		 * {@inheritDoc}
		 */
		protected int getSize(RbSerializer serializer, Object object) {
			int size = RbSerializer.SIZEOF_INT;
			size += 6 * RbSerializer.SIZEOF_DOUBLE;
			return size;
		}
		
		/**
		 * {@inheritDoc}
		 */
		protected void serializeSub(RbSerializer serializer, ByteBuffer buffer, Object object) {
			RadarComponent radar = (RadarComponent) object;
			
			serializer.serialize(buffer, radar.scanArc.x);
			serializer.serialize(buffer, radar.scanArc.y);
			serializer.serialize(buffer, radar.scanArc.width);
			serializer.serialize(buffer, radar.scanArc.height);
			serializer.serialize(buffer, radar.scanArc.start);
			serializer.serialize(buffer, radar.scanArc.extent);
			serializer.serialize(buffer, radar.scanArc.getArcType());
		}
		
		/**
		 * {@inheritDoc}
		 */
		protected void deserializeSub(RbSerializer serializer, ByteBuffer buffer, Object component) {
			RadarComponent radar = (RadarComponent) component;
			
			radar.scanArc = new Arc2D.Double(
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer),
					serializer.deserializeInt(buffer));
		}

		/**
		 * {@inheritDoc}
		 */
		protected ComponentBase getObject() {
			return new RadarComponent();
		}
	}
}
