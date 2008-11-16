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


import robocode.peer.RobotPeer;
import robocode.peer.RobotState;
import robocode.peer.DebugProperty;
import robocode.robotpaint.Graphics2DProxy;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.awt.geom.Arc2D;
import java.io.IOException;
import java.io.Serializable;
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
 * @since 1.6.1
 */
public final class RobotSnapshot implements Serializable, XmlSerializable {

	private static final long serialVersionUID = 1L;

	// The name of the robot
	private String name;

	// The short name of the robot
	private String shortName;

	// The very short name of the robot
	private String veryShortName;

	// The very short name of the team leader robot (might be null)
	private String teamName;

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
	private int bodyColor;
	// The color of the gun
	private int gunColor;
	// The color of the radar
	private int radarColor;
	// The color of the scan arc
	private int scanColor;
	// The color of the bullet
	// private final Color bulletColor;

	// Flag specifying if this robot is a Droid
	private boolean isDroid;
	// Flag specifying if this robot is a IPaintRobot
	private boolean isPaintRobot;
	// Flag specifying if robot's (onPaint) painting is enabled for the robot
	private boolean isPaintEnabled;
	// Flag specifying if RobocodeSG painting is enabled for the robot
	private boolean isSGPaintEnabled;

	// The scan arc
	private SerializableArc scanArc;

	// The Graphics2D proxy
	private List<Graphics2DProxy.QueuedCall> graphicsCalls;

	private List<DebugProperty> debugProperties;

	// The output print stream proxy
	private String outputStreamSnapshot;

	// Snapshot of score for robot
	private ScoreSnapshot robotScoreSnapshot;

	/**
	 * Constructs a snapshot of the robot.
	 *
	 * @param peer the robot peer to make a snapshot of.
	 */
	public RobotSnapshot(RobotPeer peer) {
		name = peer.getName();
		shortName = peer.getShortName();
		veryShortName = peer.getVeryShortName();
		teamName = peer.getTeamName();

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
		isPaintRobot = peer.isPaintRobot();
		isPaintEnabled = peer.isPaintEnabled();
		isSGPaintEnabled = peer.isSGPaintEnabled();

		scanArc = peer.getScanArc() != null ? new SerializableArc((Arc2D.Double) peer.getScanArc()) : null;

		graphicsCalls = peer.getGraphicsCalls();

		debugProperties = peer.getDebugProperties();

		outputStreamSnapshot = peer.readOutText();

		robotScoreSnapshot = new ScoreSnapshot(peer.getRobotStatistics(), peer.getName());
	}

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the very short name of this robot.
	 *
	 * @return the very short name of this robot.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Returns the very short name of this robot.
	 *
	 * @return the very short name of this robot.
	 */
	public String getVeryShortName() {
		return veryShortName;
	}

	/**
	 * Returns the name of the team or name of the robot if the robot is not a part of a team.
	 *
	 * @return the name of the team or name of the robot if the robot is not a part of a team.
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Returns the robot status.
	 *
	 * @return the robot status.
	 */
	public RobotState getState() {
		return state;
	}

	/**
	 * Returns the energy level.
	 *
	 * @return the energy level.
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the velocity.
	 *
	 * @return the velocity.
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * Returns the body heading in radians.
	 *
	 * @return the body heading in radians.
	 */
	public double getBodyHeading() {
		return bodyHeading;
	}

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heading in radians.
	 */
	public double getGunHeading() {
		return gunHeading;
	}

	/**
	 * Returns the radar heading in radians.
	 *
	 * @return the radar heading in radians.
	 */
	public double getRadarHeading() {
		return radarHeading;
	}

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heat
	 */
	public double getGunHeat() {
		return gunHeat;
	}

	/**
	 * Returns the x coordinate of the robot.
	 *
	 * @return the x coordinate of the robot.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the robot.
	 *
	 * @return the y coordinate of the robot.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the color of the body.
	 *
	 * @return the color of the body.
	 */
	public int getBodyColor() {
		return bodyColor;
	}

	/**
	 * Returns the color of the gun.
	 *
	 * @return the color of the gun.
	 */
	public int getGunColor() {
		return gunColor;
	}

	/**
	 * Returns the color of the radar.
	 *
	 * @return the color of the radar.
	 */
	public int getRadarColor() {
		return radarColor;
	}

	/**
	 * Returns the color of the scan arc.
	 *
	 * @return the color of the scan arc.
	 */
	public int getScanColor() {
		return scanColor;
	}

	/**
	 * Returns the color of the bullet.
	 *
	 * @return the color of the bullet.
	 */
	// public Color getBulletColor() {
	// return deepCopy(bulletColor);
	// }

	/**
	 * Returns a flag specifying if this robot is a Droid.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	public boolean isDroid() {
		return isDroid;
	}

	/**
	 * Returns a flag specifying if this robot is an IPaintRobot.
	 *
	 * @return {@code true} if this robot is a an IPaintRobot; {@code false}
	 *         otherwise.
	 */
	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	/**
	 * Returns a flag specifying if robot's (onPaint) painting is enabled for
	 * the robot.
	 *
	 * @return {@code true} if the paintings for this robot is enabled;
	 *         {@code false} otherwise.
	 */
	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	/**
	 * Returns a flag specifying if RobocodeSG painting is enabled for the
	 * robot.
	 *
	 * @return {@code true} if RobocodeSG painting is enabled for this robot;
	 *         {@code false} otherwise.
	 */
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

	/**
	 * @return list of debug properties
	 */
	public java.util.List<DebugProperty> getDebugPropeties() {
		return debugProperties;
	}

	/**
	 * Returns the output print stream snapshot for this robot.
	 *
	 * @return the output print stream snapshot for this robot.
	 */
	public String getOutputStreamSnapshot() {
		return outputStreamSnapshot;
	}

	/**
	 * Returns snapshot of score for robot
	 *
	 * @return snapshot of score for robot
	 */
	public ScoreSnapshot getRobotScoreSnapshot() {
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

		@Override
		public String toString() {
			return shortName + "(" + (int) energy + ") X" + (int) x + " Y" + (int) y + " " + state.toString();
		}

	}

	public void writeXml(XmlWriter writer) throws IOException {
		writer.startElement("robot"); {
			writer.writeAttribute("ver", serialVersionUID);
			writer.writeAttribute("name", name);
			writer.writeAttribute("sName", shortName);
			writer.writeAttribute("vsName", veryShortName);
			writer.writeAttribute("teamName", teamName);
			writer.writeAttribute("state", state.toString());

			writer.writeAttribute("energy", energy);
			writer.writeAttribute("velocity", velocity);
			writer.writeAttribute("gunHeat", gunHeat);
			writer.writeAttribute("bodyHeading", bodyHeading);
			writer.writeAttribute("gunHeading", gunHeading);
			writer.writeAttribute("radarHeading", radarHeading);
			writer.writeAttribute("x", x);
			writer.writeAttribute("y", y);

			if (debugProperties != null) {
				writer.startElement("debugProperties"); {
					for (DebugProperty prop : debugProperties) {
						prop.writeXml(writer);
					}
				}
				writer.endElement();
			}

			writer.writeAttribute("out", outputStreamSnapshot);

			robotScoreSnapshot.writeXml(writer);
		}
		writer.endElement();

	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("robot", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
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
						snapshot.outputStreamSnapshot = value;
					}
				});

				final XmlReader.Element element = (new ScoreSnapshot()).readXml(reader);

				reader.expect("score", new XmlReader.Element() {
					public XmlSerializable read(XmlReader reader) {
						snapshot.robotScoreSnapshot = (ScoreSnapshot) element.read(reader);
						return snapshot.robotScoreSnapshot;
					}
				});

				return snapshot;
			}
		});
	}

	public RobotSnapshot() {}

}
