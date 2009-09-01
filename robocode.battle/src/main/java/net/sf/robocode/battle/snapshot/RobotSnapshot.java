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
 *     Joshua Galecki
 *     - changed one scan arc to a list of scan arcs 
 *******************************************************************************/
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.peer.DebugProperty;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.RobotState;

import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.List;


/**
 * A snapshot of a robot at a specific time instant in a battle.
 * The snapshot contains a snapshot of the robot data at that specific time.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 * @author Joshua Galecki (contributor)
 * @since 1.6.1
 */
public final class RobotSnapshot implements Serializable, IXmlSerializable, IRobotSnapshot {

	private static final long serialVersionUID = 1L;

	/** The name of the robot */
	private String name;

	/** The short name of the robot */
	private String shortName;

	/** Very short name of the robot */
	private String veryShortName;

	/** Very short name of the team leader robot (might be null) */
	private String teamName;

	/** The contestant index for the robot */
	private int contestantIndex;

	/** The robot state */
	private RobotState state;

	/** The energy level of the robot */
	private double energy;

	/** The velocity of the robot */
	private double velocity;

	/** The gun heat level of the robot */
	private double gunHeat;

	/** The body heading in radians */
	private double bodyHeading;

	/** The gun heading in radians */
	private double gunHeading;

	/** The radar heading in radians */
	private double radarHeading;

	/** The X position */
	private double x;

	/** The Y position */
	private double y;

	/** The ARGB color of the body */
	private int bodyColor = ExecCommands.defaultBodyColor;

	/** The ARGB color of the gun */
	private int gunColor = ExecCommands.defaultGunColor;

	/** The ARGB color of the radar */
	private int radarColor = ExecCommands.defaultRadarColor;

	/** The ARGB color of the scan arc */
	private int scanColor = ExecCommands.defaultScanColor;

	/** Flag specifying if this robot is a Droid */
	private boolean isDroid;

	/** Flag specifying if this robot is a IPaintRobot or is invoking getGraphics() */
	private boolean isPaintRobot;

	/** Flag specifying if painting is enabled for this robot */
	private boolean isPaintEnabled;

	/** Flag specifying if RobocodeSG painting is enabled for this robot */
	private boolean isSGPaintEnabled;

	/** Snapshot of the scan arc */
	private Area scanArc;

	/** Snapshot of the object with queued calls for Graphics object */
	private Object graphicsCalls;

	/** Snapshot of debug properties */
	private DebugProperty[] debugProperties;

	/** Snapshot of the output print stream for this robot */
	private String outputStreamSnapshot;

	/** Snapshot of score of the robot */
	private IScoreSnapshot robotScoreSnapshot;

	/**
	 * Creates a snapshot of a robot that must be filled out with data later.
	 */
	public RobotSnapshot() {}

	/**
	 * Creates a snapshot of a robot.
	 *
	 * @param robot the robot to make a snapshot of.
	 * @param readoutText {@code true} if the output text from the robot must be included in the snapshot;
	 *                    {@code false} otherwise.
	 */
	public RobotSnapshot(RobotPeer robot, boolean readoutText) {
		name = robot.getName();
		shortName = robot.getShortName();
		veryShortName = robot.getVeryShortName();
		teamName = robot.getTeamName();
		contestantIndex = robot.getContestIndex();

		state = robot.getState();

		energy = robot.getEnergy();
		velocity = robot.getVelocity();
		gunHeat = robot.getGunHeat(); 

		bodyHeading = robot.getBodyHeading();
		gunHeading = robot.getGunHeading();
		radarHeading = robot.getRadarHeading();

		x = robot.getX();
		y = robot.getY();

		bodyColor = robot.getBodyColor();
		gunColor = robot.getGunColor();
		radarColor = robot.getRadarColor();
		scanColor = robot.getScanColor();

		isDroid = robot.isDroid();
		isPaintRobot = robot.isPaintRobot() || robot.isTryingToPaint();
		isPaintEnabled = robot.isPaintEnabled();
		isSGPaintEnabled = robot.isSGPaintEnabled();

		if (robot.getScanArc() != null) {
			scanArc = robot.getScanArea();
		} else {
			scanArc = null;
		}

		graphicsCalls = robot.getGraphicsCalls();

		final List<DebugProperty> dp = robot.getDebugProperties();

		debugProperties = dp != null ? dp.toArray(new DebugProperty[dp.size()]) : null;

		if (readoutText) {
			outputStreamSnapshot = robot.readOutText();
		}

		robotScoreSnapshot = new ScoreSnapshot(robot.getName(), robot.getRobotStatistics());
	}

	@Override
	public String toString() {
		return shortName + "(" + (int) energy + ") X" + (int) x + " Y" + (int) y + " " + state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	// Used to identify buttons
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	// Used for text on buttons
	public String getShortName() {
		return shortName;
	}

	/**
	 * {@inheritDoc}
	 */
	// Used for drawing the name of the robot on the battle view
	public String getVeryShortName() {
		return veryShortName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getContestantIndex() {
		return contestantIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public RobotState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getBodyHeading() {
		return bodyHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getGunHeading() {
		return gunHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getRadarHeading() {
		return radarHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getGunHeat() {
		return gunHeat;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getY() {
		return y;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getBodyColor() {
		return bodyColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getGunColor() {
		return gunColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRadarColor() {
		return radarColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getScanColor() {
		return scanColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDroid() {
		return isDroid;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	/**
	 * Sets the flag specifying if painting is enabled for the robot.
	 *
	 * @param isPaintEnabled {@code true} if painting must be enabled;
	 *                       {@code false} otherwise.
	 */
	public void setPaintEnabled(boolean isPaintEnabled) {
		this.isPaintEnabled = isPaintEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSGPaintEnabled() {
		return isSGPaintEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public DebugProperty[] getDebugProperties() {
		return debugProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOutputStreamSnapshot() {
		return outputStreamSnapshot;
	}

	/**
	 * Sets the snapshot of the output print stream for this robot.
	 *
	 * @param outputStreamSnapshot new output print stream snapshot.
	 */
	public void setOutputStreamSnapshot(String outputStreamSnapshot) {
		this.outputStreamSnapshot = outputStreamSnapshot;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScoreSnapshot getScoreSnapshot() {
		return robotScoreSnapshot;
	}

	/**
	 * Returns the scan arc snapshot for the robot.
	 *
	 * @return the scan arc snapshot for the robot.
	 */

	// TODO: backwards compatbility
	// public List<Arc2D> getScanArc() {
	// return scanArc != null ? scanArc.create() : null;
	// }

	public Area getScanArc() {
		if (scanArc != null) {
			return scanArc;
		}
		return null;
	}

	/**
	 * Returns the object with queued calls for Graphics object.
	 *
	 * @return the object with queued calls for Graphics object.
	 */
	public Object getGraphicsCalls() {
		return graphicsCalls;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Class used for serializing an Arc2D.double.
	 * The purpose of this class is to overcome various serialization problems with Arc2D to cope with bug in Java 6:
	 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6522514">Bug ID: 6522514</a>.
	 *
	 * @author Pavel Savara
	 */
	private class SerializableArc implements Serializable {
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
