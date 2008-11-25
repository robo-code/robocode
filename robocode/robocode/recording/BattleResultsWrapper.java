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
package robocode.recording;


import java.io.IOException;
import java.util.Dictionary;

import robocode.BattleResults;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;


/**
 * This class is used for wrapping a robocode.BattleResults object and provides
 * methods for XML serialization that are hidden from the BattleResults class,
 * which is a part of the public Robot API for Robocode. 
 *
 * @author Flemming N. Larsen (original)
 * @see robocode.BattleResults
 * @see robocode.util.XmlSerializable
 * @since 1.6.2
 */
public class BattleResultsWrapper extends BattleResults implements XmlSerializable {

	private static final long serialVersionUID = BattleResults.serialVersionUID;

	/**
	 * Constructs this object.
	 */
	public BattleResultsWrapper() {
		super(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Constructs this object.
	 */
	public BattleResultsWrapper(BattleResults results) {
		super(results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(),
				results.getLastSurvivorBonus(), results.getBulletDamage(), results.getBulletDamageBonus(),
				results.getRamDamage(), results.getRamDamageBonus(), results.getFirsts(), results.getSeconds(),
				results.getThirds());
	}

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("result"); {
			writer.writeAttribute("teamLeaderName", teamLeaderName);
			writer.writeAttribute("rank", rank);
			writer.writeAttribute("score", score);
			writer.writeAttribute("survival", survival);
			writer.writeAttribute("lastSurvivorBonus", lastSurvivorBonus);
			writer.writeAttribute("bulletDamage", bulletDamage);
			writer.writeAttribute("bulletDamageBonus", bulletDamageBonus);
			writer.writeAttribute("ramDamage", ramDamage);
			writer.writeAttribute("ramDamageBonus", ramDamageBonus);
			writer.writeAttribute("firsts", firsts);
			writer.writeAttribute("seconds", seconds);
			writer.writeAttribute("thirds", thirds);
			writer.writeAttribute("ver", serialVersionUID);
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("result", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
				final BattleResultsWrapper rules = new BattleResultsWrapper();

				reader.expect("rank", new XmlReader.Attribute() {
					public void read(String value) {
						rules.rank = Integer.parseInt(value);
					}
				});
				reader.expect("score", new XmlReader.Attribute() {
					public void read(String value) {
						rules.score = Double.parseDouble(value);
					}
				});
				reader.expect("survival", new XmlReader.Attribute() {
					public void read(String value) {
						rules.survival = Double.parseDouble(value);
					}
				});
				reader.expect("lastSurvivorBonus", new XmlReader.Attribute() {
					public void read(String value) {
						rules.lastSurvivorBonus = Double.parseDouble(value);
					}
				});
				reader.expect("bulletDamage", new XmlReader.Attribute() {
					public void read(String value) {
						rules.bulletDamage = Double.parseDouble(value);
					}
				});
				reader.expect("bulletDamageBonus", new XmlReader.Attribute() {
					public void read(String value) {
						rules.bulletDamageBonus = Double.parseDouble(value);
					}
				});
				reader.expect("ramDamage", new XmlReader.Attribute() {
					public void read(String value) {
						rules.ramDamage = Double.parseDouble(value);
					}
				});
				reader.expect("ramDamageBonus", new XmlReader.Attribute() {
					public void read(String value) {
						rules.ramDamageBonus = Double.parseDouble(value);
					}
				});
				reader.expect("firsts", new XmlReader.Attribute() {
					public void read(String value) {
						rules.firsts = Integer.parseInt(value);
					}
				});
				reader.expect("seconds", new XmlReader.Attribute() {
					public void read(String value) {
						rules.seconds = Integer.parseInt(value);
					}
				});
				reader.expect("thirds", new XmlReader.Attribute() {
					public void read(String value) {
						rules.thirds = Integer.parseInt(value);
					}
				});

				return rules;
			}
		});
	}
}
