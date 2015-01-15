package net.sf.robocode.battle.snapshot.components;

import java.awt.Graphics2D;
import java.io.IOException;

import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.naval.ComponentType;
import robocode.naval.MineComponent;
import robocode.naval.interfaces.IPaint;
import robocode.naval.interfaces.IRenderImages;
import robocode.robotinterfaces.ITransformable;

/**
 * Snapshot for a MineComponent.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
public class MineComponentSnapshot extends BaseComponentSnapshot{

	private static final long serialVersionUID = 1387874948621725471L;
	private double mineRecharge;
	
	public MineComponentSnapshot(){
		super();
		shortAttribute = "mc";
		shortName = "minecomponent";
	}
	public MineComponentSnapshot(MineComponent mineComponent){
		super(mineComponent);
		mineRecharge = mineComponent.getMineRecharge();
		shortAttribute = "mc";
		shortName = "minecomponent";
	}
	/**
	 * @returns how "overheated"  the MineComponent is
	 */
	public double getMineRecharge(){
		return mineRecharge;
	}
	/** {@inheritDoc} */
	@Override
	public IPaint getImage(IRenderImages manager) {
		return manager.getColoredMineComponentRenderNavalImage(getColor());
	}
	/** {@inheritDoc} */
	@Override
	public void render(Graphics2D g, ITransformable peer, boolean scanArcs) {		
	}
	
	@Override
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		super.writeXml(writer, options);
			writer.writeAttribute("minerecharge", mineRecharge, options.trimPrecision);
		writer.endElement();
	}
	
	@Override
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("minecomponent", "mc", new XmlReader.Element() {
			public IXmlSerializable read(final XmlReader reader) {
				final MineComponentSnapshot snapshot = new MineComponentSnapshot();
			
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
				
				reader.expect("minerecharge", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.mineRecharge = Integer.parseInt(value);
					}
				});
				
				return snapshot;
			}
		});
	}
}
