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
 *******************************************************************************/
package robocode.battle.snapshot;


import robocode.peer.BulletPeer;
import robocode.peer.BulletState;
import robocode.peer.ExplosionPeer;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.io.IOException;


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
 * @since 1.6.1
 */
public final class BulletSnapshot implements java.io.Serializable, XmlSerializable {

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
	private int color;

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

	/**
	 * Returns the bullet state.
	 *
	 * @return the bullet state.
	 */
	public BulletState getState() {
		return state;
	}

	/**
	 * Returns the bullet power.
	 *
	 * @return the bullet power.
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Returns the x coordinate of the bullet.
	 *
	 * @return the x coordinate of the bullet.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the bullet.
	 *
	 * @return the y coordinate of the bullet.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the x coordinate where to paint the bullet.
	 *
	 * @return the x coordinate where to paint the bullet.
	 */
	public double getPaintX() {
		return paintX;
	}

	/**
	 * Returns the y coordinate where to paint the bullet.
	 *
	 * @return the y coordinate where to paint the bullet.
	 */
	public double getPaintY() {
		return paintY;
	}

	/**
	 * Returns the color of the bullet.
	 *
	 * @return the color of the bullet.
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Returns the frame number to display.
	 *
	 * @return the frame number to display.
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * Returns the flag specifying if this bullet has turned into an explosion.
	 *
	 * @return {@code true} if this bullet is now an explosion; {@code false}
	 *         otherwise
	 */
	public boolean isExplosion() {
		return isExplosion;
	}

	/**
	 * Returns the index to which explosion image that must be rendered.
	 *
	 * @return the index to which explosion image that must be rendered.
	 */
	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	public BulletSnapshot() {}

	public void writeXml(XmlWriter writer) throws IOException {
		writer.startElement("bullet"); {
			writer.writeAttribute("ver", serialVersionUID);
			writer.writeAttribute("state", state.toString());
			writer.writeAttribute("power", power);
			writer.writeAttribute("color", color);
			writer.writeAttribute("x", x);
			writer.writeAttribute("y", y);
			writer.writeAttribute("frame", frame);
			writer.writeAttribute("isExplosion", isExplosion);
			if (isExplosion) {
				writer.writeAttribute("explosion", explosionImageIndex);
			}
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("bullet", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
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
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
					}
				});

				reader.expect("color", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = Integer.parseInt(value);
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
