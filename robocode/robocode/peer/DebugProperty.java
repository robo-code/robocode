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
import robocode.peer.serialize.ISerializableHelper;
import robocode.peer.serialize.RbSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.nio.ByteBuffer;


/**
 * @author Pavel Savara (original)
 */
public class DebugProperty implements Serializable, IXmlSerializable {
	private static final long serialVersionUID = 1L;

	public DebugProperty() {}

	public DebugProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String key;
	public String value;

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("debug"); {
			writer.writeAttribute("key", key);
			writer.writeAttribute("value", value);
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("debug", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
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
