/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *     Pavel Savara
 *     - Xml Serialization, refactoring
 *******************************************************************************/
package robocode.battle.snapshot;


import robocode.common.IXmlSerializable;
import robocode.common.XmlReader;
import robocode.common.XmlWriter;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.peer.DebugProperty;
import robocode.peer.ExecCommands;
import robocode.peer.RobotPeer;
import robocode.peer.RobotState;
import robocode.robotpaint.Graphics2DProxy;

import java.awt.geom.Arc2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.List;


/**
 * A robot snapshot, which is a view of the data for the robot at a particular
 * instant in time.
 * </p>
 * Note that this class is implemented as an immutable object. The idea of the
 * immutable object is that it cannot be modified after it has been created.
 * See the <a href="http://en.wikipedia.org/wiki/Immutable_object">Immutable
 * object</a> definition on Wikipedia.
 * </p>
 * Immutable objects are considered to be more thread-safe than mutable
 * objects, if implemented correctly.
 * </p>
 * All member fields must be final, and provided thru the constructor.
 * The constructor <em>must</em> make a deep copy of the data assigned to the
 * member fields and the getters of this class must return a copy of the data
 * that to return.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 * @since 1.6.1
 */
public final class RobotSnapshot implements Serializable, IXmlSerializable, IRobotSnapshot {

	private static final long serialVersionUID = 1L;

	// The name of the robot
	private String name;

	// The short name of the robot
	private String shortName;

	// The very short name of the robot
	private String veryShortName;

	// The very short name of the team leader robot (might be null)
	private String teamName;

	private int contestIndex;

	// The robot state
	private RobotState state;

	// The energy level
	private double energy;

	// The energy level
	private double velocity;

	// The heat level
	private double gunHeat;

	// The body heading in radians
	private double bodyHeading;
	// The gun heading in radians
	private double gunHeading;
	// The radar heading in radians
	private double radarHeading;

	// The x coordinate
	private double x;
	// The y coordinate
	private double y;

	// The color of the body
	private int bodyColor = ExecCommands.defaultBodyColor;
	// The color of the gun
	private int gunColor = ExecCommands.defaultGunColor;
	// The color of the radar
	private int radarColor = ExecCommands.defaultRadarColor;
	// The color of the scan arc
	private int scanColor = ExecCommands.defaultScanColor;
	// The color of the bullet

	// Flag specifying if this robot is a Droid
	private boolean isDroid;
	// Flag specifying if this robot is a IPaintRobot or is asking for getGraphics
	private boolean isPaintRobot;
	// Flag specifying if robot's (onPaint) painting is enabled for the robot
	private boolean isPaintEnabled;
	// Flag specifying if RobocodeSG painting is enabled for the robot
	private boolean isSGPaintEnabled;

	// The scan arc
	private SerializableArc scanArc;

	// The Graphics2D proxy
	private List<Graphics2DProxy.QueuedCall> graphicsCalls;

	private DebugProperty[] debugProperties;

	// The output print stream proxy
	private String outputStreamSnapshot;

	// Snapshot of score for robot
	private IScoreSnapshot robotScoreSnapshot;

	/**
	 * Constructs a snapshot of the robot.
	 *
	 * @param peer the robot peer to make a snapshot of.
	 * @param readoutText true to send text from robot
	 */
	public RobotSnapshot(RobotPeer peer, boolean readoutText) {
		name = peer.getName();
		shortName = peer.getShortName();
		veryShortName = peer.getVeryShortName();
		teamName = peer.getTeamName();
		contestIndex = peer.getContestIndex();

		state = peer.getState();

		energy = peer.getEnergy();
		velocity = peer.getVelocity();
		gunHeat = peer.getGunHeat(); 

		bodyHeading = peer.getBodyHeading();
		gunHeading = peer.getGunHeading();
		radarHeading = peer.getRadarHeading();

		x = peer.getX();
		y = peer.getY();

		bodyColor = peer.getBodyColor();
		gunColor = peer.getGunColor();
		radarColor = peer.getRadarColor();
		scanColor = peer.getScanColor();

		isDroid = peer.isDroid();
		isPaintRobot = peer.isPaintRobot() || peer.isTryingToPaint();
		isPaintEnabled = peer.isPaintEnabled();
		isSGPaintEnabled = peer.isSGPaintEnabled();

		scanArc = peer.getScanArc() != null ? new SerializableArc((Arc2D.Double) peer.getScanArc()) : null;

		graphicsCalls = peer.getGraphicsCalls();

		final List<DebugProperty> dp = peer.getDebugProperties();

		debugProperties = dp != null ? dp.toArray(new DebugProperty[dp.size()]) : null;

		if (readoutText) {
			outputStreamSnapshot = peer.readOutText();
		}

		robotScoreSnapshot = new ScoreSnapshot(peer.getRobotStatistics(), peer.getName());
	}

	public String getName() {
		// used to identify buttons
		return name;
	}

	public String getShortName() {
		// used for text on buttons
		return shortName;
	}

	public String getVeryShortName() {
		// used for drawign text on battleview
		return veryShortName;
	}

	public String getTeamName() {
		return teamName;
	}

	public int getContestantIndex() {
		return contestIndex;
	}

	public RobotState getState() {
		return state;
	}

	public double getEnergy() {
		return energy;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getBodyHeading() {
		return bodyHeading;
	}

	public double getGunHeading() {
		return gunHeading;
	}

	public double getRadarHeading() {
		return radarHeading;
	}

	public double getGunHeat() {
		return gunHeat;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getBodyColor() {
		return bodyColor;
	}

	public int getGunColor() {
		return gunColor;
	}

	public int getRadarColor() {
		return radarColor;
	}

	public int getScanColor() {
		return scanColor;
	}

	public boolean isDroid() {
		return isDroid;
	}

	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	/**
	 * Updates a flag specifying if robot's (onPaint) painting is enabled for
	 * the robot.
	 * @param value new value
	 */
	public void overridePaintEnabled(boolean value) {
		isPaintEnabled = value;
	}

	public boolean isSGPaintEnabled() {
		return isSGPaintEnabled;
	}

	/**
	 * Returns the scan arc for the robot.
	 *
	 * @return the scan arc for the robot.
	 */
	public Arc2D getScanArc() {
		return scanArc != null ? (Arc2D) scanArc.clone() : null;
	}

	/**
	 * Returns the Graphics2D queued calls for this robot.
	 *
	 * @return the Graphics2D queued calls for this robot.
	 */
	public java.util.List<Graphics2DProxy.QueuedCall> getGraphicsCalls() {
		return graphicsCalls;
	}

	public DebugProperty[] getDebugProperties() {
		return debugProperties;
	}

	public String getOutputStreamSnapshot() {
		return outputStreamSnapshot;
	}

	/**
	 * Sets new value of out text
	 *
	 * @param text new value
	 */
	public void updateOutputStreamSnapshot(String text) {
		outputStreamSnapshot = text;
	}

	public IScoreSnapshot getScoreSnapshot() {
		return robotScoreSnapshot;
	}

	/**
	 * The purpose of this class it to serialize the Arc2D.Double class,
	 * which does not support the Serializable interface itself.
	 *
	 * @author Flemming N. Larsen
	 * @since 1.6.1
	 */
	private class SerializableArc extends Arc2D.Double implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Public constructor needed for Serialization.
		 */
		public SerializableArc() {
			super();
		}

		/**
		 * Copy constructor used for construction a SerializableArc based
		 * on a Arc2D.Double.
		 *
		 * @param arc the Arc2D.Double object to copy
		 */
		public SerializableArc(Arc2D.Double arc) {
			super(arc.getBounds(), arc.start, arc.extent, arc.getArcType());
		}

	}

	@Override
	public String toString() {
		return shortName + "(" + (int) energy + ") X" + (int) x + " Y" + (int) y + " " + state.toString();
	}

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("robot"); {
			final Object details = options == null ? null : options.get("skipDetails");

			writer.writeAttribute("vsName", veryShortName);
			writer.writeAttribute("state", state.toString());
			writer.writeAttribute("energy", energy);
			writer.writeAttribute("x", x);
			writer.writeAttribute("y", y);
			writer.writeAttribute("bodyHeading", bodyHeading);
			writer.writeAttribute("gunHeading", gunHeading);
			writer.writeAttribute("radarHeading", radarHeading);
			writer.writeAttribute("gunHeat", gunHeat);
			writer.writeAttribute("velocity", velocity);
			writer.writeAttribute("teamName", teamName);
			if (details == null) {
				writer.writeAttribute("name", name);
				writer.writeAttribute("sName", shortName);
				if (isDroid) {
					writer.writeAttribute("isDroid", true);
				}
				if (bodyColor != ExecCommands.defaultBodyColor) {
					writer.writeAttribute("bodyColor", Integer.toHexString(bodyColor).toUpperCase());
				}
				if (gunColor != ExecCommands.defaultGunColor) {
					writer.writeAttribute("gunColor", Integer.toHexString(gunColor).toUpperCase());
				}
				if (radarColor != ExecCommands.defaultRadarColor) {
					writer.writeAttribute("radarColor", Integer.toHexString(radarColor).toUpperCase());
				}
				if (scanColor != ExecCommands.defaultScanColor) {
					writer.writeAttribute("scanColor", Integer.toHexString(scanColor).toUpperCase());
				}
			}
			writer.writeAttribute("ver", serialVersionUID);
			if (outputStreamSnapshot != null && outputStreamSnapshot.length() != 0) {
				writer.writeAttribute("out", outputStreamSnapshot);
			}

			if (debugProperties != null) {
				writer.startElement("debugProperties"); {
					for (DebugProperty prop : debugProperties) {
						prop.writeXml(writer, options);
					}
				}
				writer.endElement();
			}

			((ScoreSnapshot) robotScoreSnapshot).writeXml(writer, options);
		}
		writer.endElement();

	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("robot", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final RobotSnapshot snapshot = new RobotSnapshot();

				reader.expect("name", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.name = value;
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

				reader.expect("state", new XmlReader.Attribute() {
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

				reader.expect("energy", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.energy = Double.parseDouble(value);
					}
				});

				reader.expect("velocity", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.velocity = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeat", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeat = Double.parseDouble(value);
					}
				});

				reader.expect("bodyHeading", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.bodyHeading = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeading", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeading = Double.parseDouble(value);
					}
				});

				reader.expect("radarHeading", new XmlReader.Attribute() {
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

				reader.expect("score", new XmlReader.Element() {
					public IXmlSerializable read(XmlReader reader) {
						snapshot.robotScoreSnapshot = (IScoreSnapshot) element.read(reader);
						return (ScoreSnapshot) snapshot.robotScoreSnapshot;
					}
				});

				return snapshot;
			}
		});
	}

	public RobotSnapshot() {}

}
