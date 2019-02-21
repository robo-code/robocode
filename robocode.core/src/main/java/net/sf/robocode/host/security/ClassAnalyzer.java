/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.io.Logger;
import robocode.robotinterfaces.IBasicRobot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public final class ClassAnalyzer {
	private final static byte CONSTANT_Class = 7;
	private final static byte CONSTANT_Fieldref = 9;
	private final static byte CONSTANT_Methodref = 10;
	private final static byte CONSTANT_InterfaceMethodref = 11;
	private final static byte CONSTANT_String = 8;
	private final static byte CONSTANT_Integer = 3;
	private final static byte CONSTANT_Float = 4;
	private final static byte CONSTANT_Long = 5;
	private final static byte CONSTANT_Double = 6;
	private final static byte CONSTANT_NameAndType = 12;
	private final static byte CONSTANT_Utf8 = 1;
	private final static byte CONSTANT_MethodHandle = 15;
	private final static byte CONSTANT_MethodType = 16;
	private final static byte CONSTANT_InvokeDynamic = 18;

	/**
	 * ClassAnalyzer constructor comment.
	 */
	public ClassAnalyzer() {
		super();
	}

	static void getReferencedClasses(ByteBuffer classFile, Set<String> collection) {

		List<Integer> classNameIndexes = new ArrayList<Integer>();
		ClassInfo info;

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(classFile.array(), 0, classFile.limit()));
			info = parseClassFile(classNameIndexes, in);
			if (info == null) return;
		} catch (IOException e) {
			return;
		}

		for (Integer classNameIndex : classNameIndexes) {
			String className = info.utf8s[classNameIndex].replace('\\', '.').replace('/', '.');

			if (className.indexOf("[") != 0 && !collection.contains(className)) {
				collection.add(className);
			}
		}
	}

	private static final class ClassInfo {
		String[] utf8s;
		int[] classNames;

		ClassInfo(int constant_pool_count) {
			utf8s = new String[constant_pool_count];
			classNames = new int[constant_pool_count];
		}

		private String getClassName(int info_index) {
			return utf8s[classNames[info_index]];
		}
	}

	private static ClassInfo parseClassFile(List<Integer> classNameIndexes, DataInputStream in) throws IOException {
		 /*
		 http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html

		 4.1 The ClassFile Structure
		 A class file consists of a single ClassFile structure:

		 ClassFile {
		 u4 magic;
		 u2 minor_version;
		 u2 major_version;
		 u2 constant_pool_count;
		 cp_info constant_pool[constant_pool_count-1];
		 u2 access_flags;
		 u2 this_class;
		 u2 super_class;
		 u2 interfaces_count;
		 u2 interfaces[interfaces_count];
		 u2 fields_count;
		 field_info fields[fields_count];
		 u2 methods_count;
		 method_info methods[methods_count];
		 u2 attributes_count;
		 attribute_info attributes[attributes_count];
		 }
		 */

		ClassInfo info;
		long magic = in.readInt();

		if (magic != 0xCAFEBABE) {
			Logger.logError("Not a class file!");
			return null;
		}
		in.readUnsignedShort(); // minor version
		in.readUnsignedShort(); // major version
		int constant_pool_count = in.readUnsignedShort();

		info = new ClassInfo(constant_pool_count);

			/*

			 All constant_pool table entries have the following general format:


			 cp_info {
			 u1 tag;
			 u1 info[];
			 }

			 Constant Type  Value
			 CONSTANT_Class  7
			 CONSTANT_Fieldref  9
			 CONSTANT_Methodref  10
			 CONSTANT_InterfaceMethodref  11
			 CONSTANT_String  8
			 CONSTANT_Integer  3
			 CONSTANT_Float  4
			 CONSTANT_Long  5
			 CONSTANT_Double  6
			 CONSTANT_NameAndType  12
			 CONSTANT_Utf8  1
			 CONSTANT_MethodHandle  15
			 CONSTANT_MethodType  16
			 CONSTANT_InvokeDynamic  18
			 */


		for (int i = 1; i < constant_pool_count; i++) {
			byte tag = in.readByte();

			switch (tag) {

			/*
			 CONSTANT_Class_info {
			 u1 tag;
			 u2 name_index;
			 }
			 */
			case CONSTANT_Class: {
					int name_index = in.readUnsignedShort();

					classNameIndexes.add(name_index);

					info.classNames[i] = name_index;
				}
				break;

			/*
			 CONSTANT_Fieldref_info {
			 u1 tag;
			 u2 class_index;
			 u2 name_and_type_index;
			 }
			 CONSTANT_Methodref_info {
			 u1 tag;
			 u2 class_index;
			 u2 name_and_type_index;
			 }
			 CONSTANT_InterfaceMethodref_info {
			 u1 tag;
			 u2 class_index;
			 u2 name_and_type_index;
			 }
			 CONSTANT_InvokeDynamic_info {
			 u1 tag;
			 u2 bootstrap_method_attr_index;
			 u2 name_and_type_index;
			 }
			 */
			case CONSTANT_Fieldref:
			case CONSTANT_Methodref:
			case CONSTANT_InterfaceMethodref:
			case CONSTANT_InvokeDynamic: {
					in.readUnsignedShort(); // class index
					in.readUnsignedShort(); // name and type index
				}
				break;

			/*
			 CONSTANT_MethodHandle_info {
			 u1 tag;
			 u1 reference_kind;
			 u2 reference_index;
			 }
			 */
			case CONSTANT_MethodHandle: {
					in.readUnsignedByte(); //reference_kind
					in.readUnsignedShort(); // reference_index
				}
				break;

			/*
			 CONSTANT_String_info {
			 u1 tag;
			 u2 string_index;
			 }
			 CONSTANT_MethodType_info {
			 u1 tag;
			 u2 descriptor_index;
			 }
			 */
			case CONSTANT_String:
			case CONSTANT_MethodType: {
					in.readUnsignedShort();
				}
				break;

			/*
			 CONSTANT_Integer_info {
			 u1 tag;
			 u4 bytes;
			 }
			 CONSTANT_Float_info {
			 u1 tag;
			 u4 bytes;
			 }
			 */
			case CONSTANT_Integer:
			case CONSTANT_Float: {
					in.readInt(); // bytes
				}
				break;

			/*
			 CONSTANT_Long_info {
			 u1 tag;
			 u4 high_bytes;
			 u4 low_bytes;
			 }
			 CONSTANT_Double_info {
			 u1 tag;
			 u4 high_bytes;
			 u4 low_bytes;
			 }
			 All 8-byte constants take up two entries in the constant_pool table of the class file. If a CONSTANT_Long_info or CONSTANT_Double_info structure is the item in the constant_pool table at index n, then the next usable item in the pool is located at index n+2. The constant_pool index n+1 must be valid but is considered unusable.2
			 */

			case CONSTANT_Long:
			case CONSTANT_Double: {
					in.readInt(); // high bytes
					in.readInt(); // low bytes
					i++; // see "all 8-byte..." comment above.
				}
				break;

			/*
			 CONSTANT_NameAndType_info {
			 u1 tag;
			 u2 name_index;
			 u2 descriptor_index;
			 }
			 */
			case CONSTANT_NameAndType: {
					in.readUnsignedShort(); // name index
					in.readUnsignedShort(); // descriptor index
				}
				break;

			/*
			 CONSTANT_Utf8_info {
			 u1 tag;
			 u2 length;
			 u1 bytes[length];
			 }
			 */
			case CONSTANT_Utf8: {
					String utf8_string = in.readUTF();

					info.utf8s[i] = utf8_string;
				}
				break;
			} // switch
		} // for i
		return info;
	}

	public interface ByteBufferFunction {
		ByteBuffer get(String binaryName);
	}

	public static final class RobotMainClassPredicate {
		private final HashMap<String, Boolean> cache = new HashMap<String, Boolean>();
		private final HashMap<String, Boolean> isConcrete = new HashMap<String, Boolean>();
		private final ByteBufferFunction fn;

		public RobotMainClassPredicate(ByteBufferFunction fn) {
			this.fn = fn;
		}

		private boolean calcAssignableToRobot(String binaryName) {
			if (binaryName.startsWith("robocode/")) {
				try {
					return IBasicRobot.class.isAssignableFrom(Class.forName(binaryName
							.replace('/', '.')
							.replace('$', '.')));
				} catch (ClassNotFoundException e) {
					Logger.logError(e.toString());
					return false;
				}
			}

			ByteBuffer classFile = fn.get(binaryName);

			if (classFile == null) return false;

			List<Integer> classNameIndexes = new ArrayList<Integer>();
			ClassInfo info;

			try {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(classFile.array(), 0, classFile.limit()));
				info = parseClassFile(classNameIndexes, in);
				if (info == null) return false;

				int access_flags = in.readUnsignedShort();

				isConcrete.put(binaryName, !((access_flags & 0x0200) != 0 | (access_flags & 0x0400) != 0));

				in.readUnsignedShort(); // this_class
				int super_class = in.readUnsignedShort(); // super_class
				int interfaces_count = in.readUnsignedShort(); // interfaces_count

				if (isAssignableToRobot(info.getClassName(super_class))) {
					return true;
				}

				for (int i = 0; i < interfaces_count; ++i) {
					int interface_index = in.readUnsignedShort(); // interface_index
					if (isAssignableToRobot(info.getClassName(interface_index))) {
						return true;
					}
				}

				return false;
			} catch (IOException e) {
				return false;
			}
		}

		private boolean isAssignableToRobot(String binaryName) {
			if (binaryName == null) return false;

			if (binaryName.startsWith("java/")) {
				return false;
			}

			Boolean ret = cache.get(binaryName);
			if (ret != null) return ret;

			cache.put(binaryName, false); // in case malicious circular references

			boolean res = calcAssignableToRobot(binaryName);

			cache.put(binaryName, res);

			return res;
		}

		public boolean isMainClassBinary(String binaryName) {
			if (isAssignableToRobot(binaryName)) {
				Boolean concrete = isConcrete.get(binaryName);
				if (concrete == null) return false;
				return concrete;
			} else {
				return false;
			}
		}

		public boolean isMainClass(String name) {
			return isMainClassBinary(name.replace('.', '/'));
		}
	}
}
