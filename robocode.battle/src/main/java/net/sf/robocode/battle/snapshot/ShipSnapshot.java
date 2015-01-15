package net.sf.robocode.battle.snapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.sf.robocode.battle.peer.ShipPeer;
import net.sf.robocode.battle.snapshot.components.MineComponentSnapshot;
import net.sf.robocode.battle.snapshot.components.RadarComponentSnapshot;
import net.sf.robocode.battle.snapshot.components.WeaponComponentSnapshot;
import net.sf.robocode.peer.DebugProperty;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IComponentSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.IShipSnapshot;
import robocode.control.snapshot.RobotState;
import robocode.naval.ComponentManager;
import robocode.naval.MineComponent;
import robocode.naval.RadarComponent;
import robocode.naval.WeaponComponent;
import robocode.naval.interfaces.IComponent;

/**
 * Snapshot of a Ship. Same as RobotSnapshot, but with ComponentSnapshots added.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
public class ShipSnapshot extends RobotSnapshot implements IShipSnapshot{
	
	private static final long serialVersionUID = 327838376052842201L;
	
	/** The component manager of a ship. **/
	protected ArrayList<IComponentSnapshot> componentManager;
	
	/**
	 * The constructor of the ShipSnapshot. Created from a ShipPeer
	 * @param shipPeer The ShipPeer the Snapshot needs to be made for
	 * @param readoutText {@code true} if the output text from the robot must be included in the snapshot;
	 *                    {@code false} otherwise.
	 */
	public ShipSnapshot(ShipPeer shipPeer, boolean readoutText){
		super(shipPeer, readoutText);
		ComponentManager cp = shipPeer.getComponents();
		ArrayList<IComponent> componentArrayList = cp.getComponentArrayList();
		componentManager = new ArrayList<IComponentSnapshot>();
		for(IComponent component : componentArrayList){
			if(component instanceof WeaponComponent){
				componentManager.add(new WeaponComponentSnapshot((WeaponComponent)component));
			}
			else if(component instanceof RadarComponent){
				componentManager.add(new RadarComponentSnapshot((RadarComponent)component));
			}
			else if(component instanceof MineComponent){
				componentManager.add(new MineComponentSnapshot((MineComponent)component));
			}
		}
		
	}
	
	/**
	 * Empty constructor
	 */
	public ShipSnapshot(){
		super();
	}
	
	/** {@inheritDoc} */
	@Override
	public ArrayList<IComponentSnapshot> getComponents() {
		return componentManager;
	}
	
	/**
	 * Adds a ComponentSnapShot to the list of ComponentSnapshots.
	 * @param component The ComponentSnapshot that needs to be added to the list.
	 */
	public void addComponent(IComponentSnapshot component){
		componentManager.add(component);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? "s" : "ship"); {
			writer.writeAttribute("id", getRobotIndex());
			if (!options.skipNames) {
				writer.writeAttribute("vsName", getVeryShortName());
			}
			if (!options.skipExploded || getState() != RobotState.ACTIVE) {
				writer.writeAttribute(options.shortAttributes ? "s" : "state", getState().toString());
			}
			writer.writeAttribute(options.shortAttributes ? "e" : "energy", getEnergy(), options.trimPrecision);
			writer.writeAttribute("x", getX(), options.trimPrecision);
			writer.writeAttribute("y", getY(), options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "b" : "bodyHeading", getBodyHeading(), options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "v" : "velocity", getVelocity(), options.trimPrecision);
			if (!options.skipNames) {
				writer.writeAttribute("teamName", getTeamName());
				writer.writeAttribute("name", getName());
				writer.writeAttribute("sName", getShortName());
				if (isDroid()) {
					writer.writeAttribute("isDroid", true);
				}
				if (getBodyColor() != ExecCommands.defaultBodyColor) {
					writer.writeAttribute("bodyColor", Integer.toHexString(getBodyColor()).toUpperCase());
				}
				if (scanColor != ExecCommands.defaultScanColor) {
					writer.writeAttribute("scanColor", Integer.toHexString(scanColor).toUpperCase());
				}
			}
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}
			if (!options.skipDebug) {
				if (getOutputStreamSnapshot() != null && getOutputStreamSnapshot().length() != 0) {
					writer.writeAttribute("out", getOutputStreamSnapshot());
				}
				if (getDebugProperties() != null) {
					writer.startElement("debugProperties"); {
						for (DebugProperty prop : getDebugProperties()) {
							prop.writeXml(writer, options);
						}
					}
					writer.endElement();
				}
			}

			((ScoreSnapshot) robotScoreSnapshot).writeXml(writer, options);
		}
		writer.endElement();

	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("ship", "s", new XmlReader.Element() {
			public IXmlSerializable read(final XmlReader reader) {
				final ShipSnapshot snapshot = new ShipSnapshot();

				reader.expect("id", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.robotIndex = Integer.parseInt(value);

						// allows loading of minimalistic XML, which robot names for subsequent turns
						Map<String, Object> context = reader.getContext();

						if (context.containsKey(value)) {
							String n = (String) context.get(value);

							if (snapshot.shortName == null) {
								snapshot.name = n;
							}
							if (snapshot.shortName == null) {
								snapshot.shortName = n;
							}
							if (snapshot.teamName == null) {
								snapshot.teamName = n;
							}
							if (snapshot.veryShortName == null) {
								snapshot.veryShortName = n;
							}
						}
					}
				});

				reader.expect("name", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.name = value;
						Map<String, Object> context = reader.getContext();

						context.put(Integer.toString(snapshot.robotIndex), value);
						if (snapshot.shortName == null) {
							snapshot.shortName = value;
						}
						if (snapshot.teamName == null) {
							snapshot.teamName = value;
						}
						if (snapshot.veryShortName == null) {
							snapshot.veryShortName = value;
						}
					}
				});

				reader.expect("sName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.shortName = value;
					}
				});

				reader.expect("vsName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.veryShortName = value;
					}
				});

				reader.expect("teamName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.teamName = value;
					}
				});

				reader.expect("state", "s", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = RobotState.valueOf(value);
					}
				});

				reader.expect("isDroid", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.isDroid = Boolean.valueOf(value);
					}
				});

				reader.expect("bodyColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.bodyColor = (Long.valueOf(value.toUpperCase(), 16).intValue());
					}
				});

				reader.expect("gunColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("radarColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.radarColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("scanColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.scanColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("energy", "e", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.energy = Double.parseDouble(value);
					}
				});

				reader.expect("velocity", "v", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.velocity = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeat", "h", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeat = Double.parseDouble(value);
					}
				});

				reader.expect("bodyHeading", "b", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.bodyHeading = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeading", "g", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeading = Double.parseDouble(value);
					}
				});

				reader.expect("radarHeading", "r", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.radarHeading = Double.parseDouble(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
					}
				});

				reader.expect("out", new XmlReader.Attribute() {
					public void read(String value) {
						if (value != null && value.length() != 0) {
							snapshot.outputStreamSnapshot = value;
						}
					}
				});

				final XmlReader.Element element = (new ScoreSnapshot()).readXml(reader);

				reader.expect("score", "sc", new XmlReader.Element() {
					public IXmlSerializable read(XmlReader reader) {
						snapshot.robotScoreSnapshot = (IScoreSnapshot) element.read(reader);
						return (ScoreSnapshot) snapshot.robotScoreSnapshot;
					}
				});

				return snapshot;
			}
		});
	}
	
	

}
