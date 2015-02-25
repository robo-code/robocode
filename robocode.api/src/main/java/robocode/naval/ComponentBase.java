package robocode.naval;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenComponentHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.naval.interfaces.IComponent;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Coordinates;

/**
 * The abstract class for the basic ship components. 
 * @author Thales B.V. / Jiri Waning
 * @since 1.9.2.2
 */
public abstract class ComponentBase implements IComponent, Serializable {
	private static final long serialVersionUID = 1L;
	
	/** The relative point, based on the ship's origin, that the component is attached to. **/
	private Point2D pivot = new Point2D.Double(0.0d, 0.0d);
	
	/** The color that the component should be rendered with. **/
	private Color color = Color.WHITE;
	
	/** The angle, in radians, of the component. **/
	protected double angle = 0.0d;
	
	/** The type of this component. Default: Undefined **/
	protected ComponentType type = ComponentType.UNDEFINED;
	/** Keeping track of the heading before the current heading. Useful for deciding the scan width **/
	protected double lastHeading = 0;
		
	/**
	 * Creates a new robot component at the specified point.
	 * @param pivot The pivot point of the component.
	 * @param type The type of the child component.
	 */
	public ComponentBase(Point2D pivot, ComponentType type) {
		this(pivot, type, 0.0d);
	}
	
	public double getGunHeat(){
		return 0;
	}
	
	/**
	 * Creates a new robot component at the specified point.
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 * @param type The type of the child component.
	 * @param angle The angle of the component in radians.
	 */
	public ComponentBase(double x, double y, ComponentType type, double angle) {
		this(new Point2D.Double(x, y), type, angle);
	}
	
	/**
	 * Creates a new robot component at the specified point.
	 * @param pivot The pivot point of the component.
	 * @param type The type of the child component.
	 * @param angle The angle of the component in radians.
	 */
	public ComponentBase(Point2D pivot, ComponentType type, double angle) {
		this.pivot = pivot;
		this.angle = angle;
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D getPivot() {
		return pivot;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Point2D getOrigin(ITransformable peer) {
		return Coordinates.getPivot(peer, pivot.getX(), pivot.getY());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setLastAngle(double lastHeading){
		this.lastHeading = lastHeading;
	}
	
	public double getLastAngle(){
		return lastHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAngle() {
		return angle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAngleDegrees() {
		return (angle * 180 / PI);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAngle(double angle) {
		if(angle > 2*PI){
			angle %= 2*PI;
		}		
		this.angle = angle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAngleDegrees(double angle) {
		this.angle = (angle * PI / 180);
	}
	/**
	 * Set the type of the component.
	 * <p/>
	 * <i>The types differ for each <s>child</s> class.</i>
	 * @param type The type of the component.
	 */
	protected void setType(ComponentType type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ComponentType getType() {
		return type;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public abstract double turnRadians(double turnRemaining);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		// The object must be of the same class for this to work!
		if (obj.getClass().equals(this.getClass())) {
			ComponentBase other = (ComponentBase)obj;
			if (other.angle == this.angle) {
				if (other.pivot == this.pivot) {
					if (other.type == this.type) {
						return true; 
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ComponentBase \r\n" +
				"\t pivot == " + pivot.toString() + "\r\n" +
				"\t color == " + color.getRGB() + "\r\n" +
				"\t angle == " + angle + "\r\n" +
				"\t type == " + type + "\r\n";
	}
	
	
	public byte getSerializeType(){
		return 0;
	}
	
	// ======================================================================
	//  Basic serialize for components.
	// ======================================================================
	
	/**
	 * <i><b>Only visible towards subclasses.</b></i>
	 * <p />
	 * This class is used for the serialization of a component. Sub-classes
	 * of this class have to expand on this to serialize the super class.
	 * 
	 * @author Thales B.V. / Jiri Waning
	 * @version 0.1
	 * @since 1.8.3.0 Alpha 1
	 */
	protected abstract static class HiddenComponentHelper implements ISerializableHelper, IHiddenComponentHelper {

		@Override
		public final void update(ComponentBase component) {
			// 1st - Update this class.
			
			
			// 2nd - Update the subclass.
			updateSub(component);
		}
		public abstract void updateSub(ComponentBase component);
		
		/**
		 * Add the size of the base component and request the sub-class to add his additional size too.
		 */
		@Override
		public final int sizeOf(RbSerializer serializer, Object object) {
			int size = 0;
			
			// 1st - Add the size of this class.
			size += 3 * RbSerializer.SIZEOF_DOUBLE; // Pivot(x+y)+Angle
			size += 2 * RbSerializer.SIZEOF_INT; // Color+Type
			
			// 2nd - Add the size of the subclass.
			size += getSize(serializer, object);
			
			return size;
		}
		protected abstract int getSize(RbSerializer serializer, Object object);

		/**
		 * Serialize the base component and request the sub-class to serialize itself too.
		 */
		@Override
		public final void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ComponentBase obj = (ComponentBase)object;
			
			// 1st - Serialize this class.
			serializer.serialize(buffer, obj.pivot.getX());
			serializer.serialize(buffer, obj.pivot.getY());
			serializer.serialize(buffer, obj.angle);
			serializer.serialize(buffer, obj.color.getRGB());
			serializer.serialize(buffer, obj.type.toInt());
			
			// 2nd - Serialize the subclass.
			serializeSub(serializer, buffer, obj);
		}
		protected abstract void serializeSub(RbSerializer serializer, ByteBuffer buffer, Object object);

		/**
		 * De-serialize the base component and request the sub-class to de-serialize itself too.
		 */
		@Override
		public final Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			ComponentBase component = getObject();
			
			// 1st - De-serialize this class.
			component.pivot = new Point2D.Double(
					serializer.deserializeDouble(buffer),
					serializer.deserializeDouble(buffer));
			component.angle = serializer.deserializeDouble(buffer);
			component.color = new Color(serializer.deserializeInt(buffer));
			component.type = ComponentType.getValue(serializer.deserializeInt(buffer));
			
			// 2nd - De-serialize the subclass.
			deserializeSub(serializer, buffer, component);
			
			return component;
		}
		
		/**
		 * De-serialize the subclass of this super class.
		 * @param serializer The serializer that does the transcoding.
		 * @param buffer The buffer in which the data is stored.
		 * @param component The component to whom to restore the values.
		 */
		protected abstract void deserializeSub(RbSerializer serializer, ByteBuffer buffer, Object component);
		
		/**
		 * Get an empty object to work with.
		 * <p/>
		 * <i>The object has to come from the subclass of {@code RobotComponent}.</i>
		 * @return A new empty subclass.
		 */
		protected abstract ComponentBase getObject();
	}
}
