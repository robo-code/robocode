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


import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.BulletState;
import net.sf.robocode.peer.ExecCommands;
import robocode.peer.ExplosionPeer;
import robocode.peer.BulletPeer;

import java.io.IOException;
import java.util.Dictionary;


/**
 * A bullet snapshot, which is a view of the data for the bullet at a particular
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
public final class BulletSnapshot implements java.io.Serializable, IXmlSerializable, IBulletSnapshot {

	private static final long serialVersionUID = 1L;

	// The name of the robot that owns this bullet
	// private final String ownerName;

	// The bullet state
	private BulletState state;

	// The bullet power
	private double power;

	// The x coordinate
	private double x;
	// The y coordinate
	private double y;

	// The x coordinate for painting (due to offset on robot when bullet hits a robot)
	private double paintX;
	// The y coordinate for painting (due to offset on robot when bullet hits a robot)
	private double paintY;

	// The color of the bullet
	private int color = ExecCommands.defaultBulletColor;

	// The current frame number to display
	private int frame;

	// Flag specifying if this bullet has turned into an explosion
	private boolean isExplosion;

	// Index to which explosion image that must be rendered
	private int explosionImageIndex;

	/**
	 * Constructs a snapshot of the bullet.
	 *
	 * @param peer the bullet peer to make a snapshot of.
	 */
	public BulletSnapshot(BulletPeer peer) {
		// ownerName = peer.getOwner().getName();

		state = peer.getState();

		power = peer.getPower();

		x = peer.getX();
		y = peer.getY();

		paintX = peer.getPaintX();
		paintY = peer.getPaintY();

		color = peer.getColor();

		frame = peer.getFrame();

		isExplosion = peer instanceof ExplosionPeer;
		explosionImageIndex = peer.getExplosionImageIndex();
	}

	/**
	 * Returns the name of the robot that owns this bullet.
	 *
	 * @return the name of the robot that owns this bullet.
	 */
	// public String getOwnerName() {
	// return ownerName;
	// }

	public BulletState getState() {
		return state;
	}

	public double getPower() {
		return power;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getPaintX() {
		return paintX;
	}

	public double getPaintY() {
		return paintY;
	}

	public int getColor() {
		return color;
	}

	public int getFrame() {
		return frame;
	}

	public boolean isExplosion() {
		return isExplosion;
	}

	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	public BulletSnapshot() {}

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("bullet"); {
			writer.writeAttribute("state", state.toString());
			writer.writeAttribute("power", power);
			writer.writeAttribute("x", paintX);
			writer.writeAttribute("y", paintY);
			if (color != ExecCommands.defaultBulletColor) {
				writer.writeAttribute("color", Integer.toHexString(color).toUpperCase());
			}
			if (frame != 0) {
				writer.writeAttribute("frame", frame);
			}
			if (isExplosion) {
				writer.writeAttribute("isExplosion", true);
				writer.writeAttribute("explosion", explosionImageIndex);
			}
			writer.writeAttribute("ver", serialVersionUID);
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("bullet", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final BulletSnapshot snapshot = new BulletSnapshot();

				reader.expect("state", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = BulletState.valueOf(value);
					}
				});

				reader.expect("power", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.power = Double.parseDouble(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
						snapshot.paintX = snapshot.x;
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
						snapshot.paintY = snapshot.y;
					}
				});

				reader.expect("color", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("isExplosion", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.isExplosion = Boolean.parseBoolean(value);
					}
				});

				reader.expect("explosion", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.explosionImageIndex = Integer.parseInt(value);
					}
				});

				reader.expect("frame", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.frame = Integer.parseInt(value);
					}
				});
				return snapshot;
			}
		});

	}
}
