/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.peer;


import net.sf.robocode.serialization.*;
import robocode.control.snapshot.IDebugProperty;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;


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

	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
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

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			DebugProperty obj = (DebugProperty) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.key) + serializer.sizeOf(obj.value);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			DebugProperty obj = (DebugProperty) object;

			serializer.serialize(buffer, obj.key);
			serializer.serialize(buffer, obj.value);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String key = serializer.deserializeString(buffer);
			String value = serializer.deserializeString(buffer);

			return new DebugProperty(key, value);
		}
	}
}
