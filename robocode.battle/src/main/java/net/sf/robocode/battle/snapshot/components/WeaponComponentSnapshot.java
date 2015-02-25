package net.sf.robocode.battle.snapshot.components;


import java.awt.Graphics2D;
import java.io.IOException;

import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.naval.ComponentType;
import robocode.naval.WeaponComponent;
import robocode.naval.interfaces.IPaint;
import robocode.naval.interfaces.IRenderImages;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Utils;
/**
 * Snapshot for a WeaponComponent.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
public class WeaponComponentSnapshot extends BaseComponentSnapshot{
	private static final long serialVersionUID = -358308844160820664L;
	
	protected double blindSpotStart;
	protected double blindSpotExtent;
	
	/**
	 * The current (over)heat of the gun.
	 */
	protected double gunHeat;
	
	/**
	 * The fire power of the weapon.
	 */
	protected double power;
	
	
	
	public WeaponComponentSnapshot(){
		super();
	}
	
	public WeaponComponentSnapshot(WeaponComponent weaponComponent){
		super(weaponComponent);
		blindSpotStart = weaponComponent.getCopyOfBlindSpot().getStart();
		blindSpotExtent = Utils.normalAbsoluteAngle(weaponComponent.getCopyOfBlindSpot().getEnd() - blindSpotStart);
		gunHeat = weaponComponent.getGunHeat();
		power = weaponComponent.getFirePower();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getGunHeat() {
		return gunHeat;
	}
	public double getPower(){
		return power;
	}
	/**
	 * @returns a snapshot of the BlindSpot of the Weapon.
	 */
	public BlindSpotSnapshot getBlindSpot(){
		return new BlindSpotSnapshot(blindSpotStart, blindSpotExtent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPaint getImage(IRenderImages manager) {
		return manager.getColoredGunRenderNavalImage(getColor());
	}

	/**
	 * {@inheritDoc}
	 */
	public void render(Graphics2D g, ITransformable peer, boolean scanArcs) {
		// Render weapon related items... (Which it does not have ATM.)
	}
	
	@Override
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		super.writeXml(writer, options);
			writer.writeAttribute("gunheat", gunHeat, options.trimPrecision);
			writer.writeAttribute("power", power, options.trimPrecision);
			writer.writeAttribute("blindspotstart", blindSpotStart, options.trimPrecision);
			writer.writeAttribute("blindspotextent", blindSpotExtent, options.trimPrecision);
		writer.endElement();
	}
	
	@Override
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("minecomponent", "mc", new XmlReader.Element() {
			public IXmlSerializable read(final XmlReader reader) {
				final WeaponComponentSnapshot snapshot = new WeaponComponentSnapshot();
			
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
				
				reader.expect("gunheat", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeat = Double.parseDouble(value);
					}
				});
				
				reader.expect("power", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.power = Double.parseDouble(value);
					}
				});
				
				reader.expect("blindspotstart", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.blindSpotStart = Double.parseDouble(value);
					}
				});
				
				reader.expect("blindspotextent", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.blindSpotExtent = Double.parseDouble(value);
					}
				});
				
				return snapshot;
			}
		});
	}
}
