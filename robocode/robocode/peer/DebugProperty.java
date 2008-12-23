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


import robocode.common.IXmlSerializable;
import robocode.common.XmlReader;
import robocode.common.XmlWriter;
import robocode.control.snapshot.IDebugProperty;

import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;


/**
 * @author Pavel Savara (original)
 */
public class DebugProperty implements Serializable, IXmlSerializable, IDebugProperty {
	private static final long serialVersionUID = 1L;

	public DebugProperty() {}

	public DebugProperty(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	private String key;
	private String value;

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("debug"); {
			writer.writeAttribute("key", getKey());
			writer.writeAttribute("value", getValue());
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("debug", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final DebugProperty snapshot = new DebugProperty();

				reader.expect("key", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.setKey(value);
					}
				});

				reader.expect("value", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.setValue(value);
					}
				});

				return snapshot;
			}
		});
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
