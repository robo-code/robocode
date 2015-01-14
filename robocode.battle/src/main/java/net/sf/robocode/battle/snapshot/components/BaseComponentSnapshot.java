package net.sf.robocode.battle.snapshot.components;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;

import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IComponentSnapshot;
import robocode.naval.ComponentBase;
import robocode.naval.ComponentType;
import robocode.naval.interfaces.IPaint;
import robocode.naval.interfaces.IRenderImages;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Coordinates;

public abstract class BaseComponentSnapshot implements Serializable, IXmlSerializable, IComponentSnapshot{
	private static final long serialVersionUID = 1618418996968208058L;
	
	protected String shortAttribute = "bc";
	protected String shortName = "basecomponent";

	/** The type of this component. Default: Undefined **/
	protected ComponentType type = ComponentType.UNDEFINED;
	
	/** The relative point, based on the ship's origin, that the component is attached to. **/
	protected Point2D pivot = new Point2D.Double(0.0d, 0.0d);
	
	/** The color that the component should be rendered with. **/
	protected int color;
	
	/** The angle, in radians, of the component. **/
	protected double angle;
	

	
	protected double lastHeading;
	
	protected byte serializeType;
	
	public BaseComponentSnapshot(){
		shortAttribute = "bc";
		shortName = "basecomponent";
	}
	
	/**
	 * Constructs a BaseComponentSnapshot.
	 * This shouldn't be called directly. 
	 * Instead, make a class extending from this class.
	 * @param component The component you want to turn into a snapshot.
	 */
	protected BaseComponentSnapshot(ComponentBase component){
		pivot = component.getPivot();
		color = component.getColor().getRGB();
		angle = component.getAngle();
		type = component.getType();
		lastHeading = component.getLastAngle();
		serializeType = component.getSerializeType();
		
		shortAttribute = "bc";
		shortName = "basecomponent";
	}
	
	/**
	 * Constructs a BaseComponentSnapshot.
	 * Used for serialization purposes.
	 */
	protected BaseComponentSnapshot(Point2D.Double pivot, int color, double angle, ComponentType type, double lastHeading, byte serializeType){
		this.pivot = pivot;
		this.color = color;
		this.angle = angle;
		this.type = type;
		this.lastHeading = lastHeading;
		this.serializeType = serializeType;
		
		shortAttribute = "bc";
		shortName = "basecomponent";
	}
	
	/** {@inheritDoc} */
	@Override
	public ComponentType getType() {
		return type;
	}
	/** {@inheritDoc} */
	@Override
	public Point2D getPivot() {
		return pivot;
	}
	/** {@inheritDoc} */
	@Override
	public double getAngle() {
		return angle;
	}
	/** {@inheritDoc} */
	@Override
	public int getColor() {
		return color;
	}
	/** {@inheritDoc} */
	@Override
	public byte getSerializeType() {
		return serializeType;
	}
	/** {@inheritDoc} */
	@Override
	public double getLastAngle(){
		return lastHeading;
	}
	/** {@inheritDoc} */
	@Override
	public double getGunHeat() {
		return 0;
	}
	
	/**
	 * Get the image that the component uses.
	 * @return The image of the specific component.
	 */
	public abstract IPaint getImage(IRenderImages manager);
	
	/**
	 * Draw the component.
	 * @param g The device that renders the component.
	 * @param image The image manager containing the images.
	 * @param peer The peer for whom to render the component.
	 * @param scanArcs true if scanarcs need to be draw. False otherwise.  (Should be moved to RadarComponent)
	 */
	@Override
	public void paint(Graphics2D g, IRenderImages manager, ITransformable peer, boolean scanArcs) {
		IPaint image = getImage(manager);
		image.setTransform(getAffineTransform(peer));
		image.paint(g);

		render(g, peer, scanArcs);
	}
	
	/**
	 * Do the component specific drawing in here.
	 * @param g The device to draw too.
	 * @param peer The robot to whom the component belongs to.
	 * @param scanArcs Indicates whether or not to draw the scan arcs.
	 */
	public abstract void render(Graphics2D g, ITransformable peer, boolean scanArcs);
	
	/**
	 * {@inheritDoc}
	 */
	public AffineTransform getAffineTransform(ITransformable robot) {
		return Coordinates.getPivotTransform(robot, pivot.getX(), pivot.getY(), angle);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? shortAttribute : shortName); 
			writer.writeAttribute("type", type.toInt(), options.trimPrecision);
			writer.writeAttribute("x", pivot.getX(), options.trimPrecision);
			writer.writeAttribute("y", pivot.getY(), options.trimPrecision);
			writer.writeAttribute("color", Integer.toHexString(color).toUpperCase());
			writer.writeAttribute("angle" , angle, options.trimPrecision);
			writer.writeAttribute("lastangle" , lastHeading, options.trimPrecision);
			writer.writeAttribute("serializetype" , serializeType);
//		writer.endElement();

	}

	/**
	 * {@inheritDoc}
	 */
	public abstract XmlReader.Element readXml(XmlReader reader);
}
