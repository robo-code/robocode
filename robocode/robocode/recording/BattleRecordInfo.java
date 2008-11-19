/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara & Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.recording;


import robocode.BattleResults;
import robocode.BattleRules;
import robocode.battle.snapshot.ScoreSnapshot;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public class BattleRecordInfo implements Serializable, XmlSerializable {
	private static final long serialVersionUID = 1L;

	public int robotCount;
	public int roundsCount;
	public BattleRules battleRules;
	public Integer[] turnsInRounds;
	public BattleResults[] results;

	public void writeXml(XmlWriter writer) throws IOException {
		writer.startElement("recordInfo"); {
			writer.writeAttribute("robotCount", robotCount);
			writer.writeAttribute("roundsCount", roundsCount);
			writer.writeAttribute("ver", serialVersionUID);

			battleRules.writeXml(writer);

			writer.startElement("rounds"); {
				for (int n : turnsInRounds) {
					writer.startElement("turns"); {
						writer.writeAttribute("value", Integer.toString(n));
					}
					writer.endElement();
				}
			}
			writer.endElement();

			writer.startElement("results"); {
				for (BattleResults result : results) {
					result.writeXml(writer);
				}
			}
			writer.endElement();
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("recordInfo", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
				final BattleRecordInfo recordInfo = new BattleRecordInfo();

				reader.expect("robotCount", new XmlReader.Attribute() {
					public void read(String value) {
						recordInfo.robotCount = Integer.parseInt(value);
					}
				});
				reader.expect("roundsCount", new XmlReader.Attribute() {
					public void read(String value) {
						recordInfo.roundsCount = Integer.parseInt(value);
					}
				});

				final XmlReader.Element element = (new BattleRules()).readXml(reader);

				reader.expect("rules", new XmlReader.Element() {
					public XmlSerializable read(XmlReader reader) {
						recordInfo.battleRules = (BattleRules) element.read(reader);
						return recordInfo.battleRules;
					}
				});

				reader.expect("rounds", new XmlReader.ListElement() {
					ArrayList<Integer> ints = new ArrayList<Integer>();

					public XmlSerializable read(XmlReader reader) {
						// prototype
						return new IntValue("turns");
					}

					public void add(XmlSerializable child) {
						ints.add(((IntValue) child).intValue);
					}

					public void close() {
						recordInfo.turnsInRounds = new Integer[ints.size()];
						ints.toArray(recordInfo.turnsInRounds);
					}
				});

				reader.expect("results", new XmlReader.ListElement() {
					ArrayList<BattleResults> res = new ArrayList<BattleResults>();

					public XmlSerializable read(XmlReader reader) {
						// prototype
						return new BattleResults();
					}

					public void add(XmlSerializable child) {
						res.add((BattleResults) child);
					}

					public void close() {
						recordInfo.results = new BattleResults[res.size()];
						res.toArray(recordInfo.results);
					}
				});
				return recordInfo;
			}
		});
	}

	public class IntValue implements XmlSerializable {
		IntValue(String name) {
			this.name = name;
		}
		private String name;
		public int intValue;

		public void writeXml(XmlWriter writer) throws IOException {}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect(name, new XmlReader.Element() {
				public XmlSerializable read(XmlReader reader) {
					final IntValue recordInfo = new IntValue(name);

					reader.expect("value", new XmlReader.Attribute() {
						public void read(String value) {
							recordInfo.intValue = Integer.parseInt(value);
						}
					});
					return recordInfo;
				}
			});
		}
	}

}
