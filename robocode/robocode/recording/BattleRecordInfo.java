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
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;


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
	public List<BattleResults> results;

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("recordInfo"); {
			writer.writeAttribute("robotCount", robotCount);
			writer.writeAttribute("roundsCount", roundsCount);
			writer.writeAttribute("ver", serialVersionUID);

			battleRules.writeXml(writer, options);

			writer.startElement("rounds"); {
				for (int n : turnsInRounds) {
					writer.startElement("turns"); {
						writer.writeAttribute("value", Integer.toString(n));
					}
					writer.endElement();
				}
			}
			writer.endElement();

			if (results != null) {
				writer.startElement("results"); {
					for (BattleResults result : results) {
						new BattleResultsWrapper(result).writeXml(writer, options);
					}
				}
				writer.endElement();
			}
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

					public XmlSerializable read(XmlReader reader) {
						recordInfo.results = new ArrayList<BattleResults>();
						// prototype
						return new BattleResultsWrapper();
					}

					public void add(XmlSerializable child) {
						recordInfo.results.add((BattleResults) child);
					}

					public void close() {}
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

		public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {}

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
