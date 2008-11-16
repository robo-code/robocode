/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;


import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;
import robocode.util.XmlReader;

import java.io.Serializable;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public class DebugProperty implements Serializable, XmlSerializable {
	private static final long serialVersionUID = 1L;

	public DebugProperty() {}

	public DebugProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String key;
	public String value;

	public void writeXml(XmlWriter writer) throws IOException {
		writer.startElement("debug"); {
			writer.writeAttribute("ver", serialVersionUID);
			writer.writeAttribute("key", key);
			writer.writeAttribute("value", value);
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("debug", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
				final DebugProperty snapshot = new DebugProperty();

				reader.expect("key", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.key = value;
					}
				});

				reader.expect("value", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.value = value;
					}
				});

				return snapshot;
			}
		});
	}
}
