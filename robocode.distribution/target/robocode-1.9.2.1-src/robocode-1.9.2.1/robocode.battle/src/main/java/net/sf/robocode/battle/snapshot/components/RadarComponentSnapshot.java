package net.sf.robocode.battle.snapshot.components;

import static java.lang.Math.abs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.io.Serializable;

import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.naval.ComponentType;
import robocode.naval.RadarComponent;
import robocode.naval.interfaces.IPaint;
import robocode.naval.interfaces.IRenderImages;
import robocode.robotinterfaces.ITransformable;
/**
 * Snapshot for a RadarComponent.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
public class RadarComponentSnapshot extends BaseComponentSnapshot{
	private static final long serialVersionUID = 479131002004068884L;
	private SerializableArc scanArc;
	private Color scanColor;
	
	public RadarComponentSnapshot(){
		super();
		shortAttribute = "rc";
		shortName = "radarcomponent";
	}
	public RadarComponentSnapshot(RadarComponent radarComponent){
		super(radarComponent);
//		scanArc = radarComponent.getScanArc();
		scanArc = radarComponent.getScanArc() != null ? new SerializableArc((Arc2D.Double) radarComponent.getScanArc()) : null;
		scanColor = radarComponent.getScanColor();
		
		shortAttribute = "rc";
		shortName = "radarcomponent";

	}

	public SerializableArc getScanArc(){
		return scanArc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPaint getImage(IRenderImages manager) {
		return manager.getColoredRadarRenderNavalImage(getColor());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void render(Graphics2D g, ITransformable peer, boolean scanArcs) {
		if (scanArcs) {
			drawScanArc(g, peer);
		}
	}
	
	/**
	 * Draws the scan arc of the radar.
	 * @param g The device to draw too.
	 * @param peer The robot to whom the radar belongs.
	 */
	private void drawScanArc(Graphics2D g, ITransformable peer) {
		Arc2D.Double arc = (Arc2D.Double)scanArc.create();
		// Composite
		final Composite savedComposite = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		// Color
		g.setColor(scanColor);

		// Drawing
		if (abs(arc.getAngleExtent()) >= .5) {
			g.fill(arc);
		} else {
			g.draw(arc);
		}

		// Restore original(s)
		g.setComposite(savedComposite);
	}
	
	@Override
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		super.writeXml(writer, options);
		writer.writeAttribute("scanColor", Integer.toHexString(scanColor.getRGB()).toUpperCase());
		writer.endElement();
	}
	
	@Override
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("radarcomponent", "rc", new XmlReader.Element() {
			public IXmlSerializable read(final XmlReader reader) {
				final RadarComponentSnapshot snapshot = new RadarComponentSnapshot();
			
				reader.expect("type", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.type = ComponentType.getValue(Integer.parseInt(value));
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.pivot.setLocation(Double.parseDouble(value), 0);
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.pivot.setLocation(snapshot.pivot.getX(), Double.parseDouble(value));
					}
				});

				reader.expect("color", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = (Long.valueOf(value.toUpperCase(), 16).intValue());
					}
				});
				
				reader.expect("angle", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.angle = Double.parseDouble(value);
					}
				});

				reader.expect("lastangle", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.lastHeading = Double.parseDouble(value);
					}
				});

				reader.expect("serializetype", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.serializeType = value.getBytes()[0];
					}
				});
				
				reader.expect("scanColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.scanColor = new Color(Long.valueOf(value.toUpperCase(), 16).intValue());
					}
				});

				return snapshot;
			}
		});
	}
	
	/**
	 * Class used for serializing an Arc2D.double.
	 * The purpose of this class is to overcome various serialization problems with Arc2D to cope with bug in Java 6:
	 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6522514">Bug ID: 6522514</a>.
	 *
	 * @author Pavel Savara
	 */
	private static class SerializableArc implements Serializable {
		private static final long serialVersionUID = 1L;

		public final double x;
		public final double y;
		public final double w;
		public final double h;
		public final double start;
		public final double extent;
		public final int type;

		public SerializableArc(Arc2D.Double arc) {
			x = arc.getX();
			y = arc.getY();
			w = arc.getWidth();
			h = arc.getHeight();
			start = arc.getAngleStart();
			extent = arc.getAngleExtent();
			type = arc.getArcType();
		}

		public Arc2D create() {
			return new Arc2D.Double(x, y, w, h, start, extent, type);
		}
	}
}
