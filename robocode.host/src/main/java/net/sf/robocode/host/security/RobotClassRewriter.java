/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


/**
 * Rewrites robot class files so file access, reflection, and deserialization done by robots are checked by Robocode.
 * <p>
 * Without the SecurityManager (removed with Java 24), the JDK does not check if a robot is allowed to read or modify
 * a file, which members it uses via reflection, or which classes it deserializes. Hence, the class files of robots
 * are rewritten as follows:
 * <ul>
 * <li>Calls to methods of {@code java.io.File} that modify files or read information about files, e.g.
 * {@code delete()} and {@code exists()}, are replaced with calls to static methods with the same names in
 * {@link robocode.util.RobotFileAccess}, which take the file as the first parameter.</li>
 * <li>Calls to methods that use members via reflection, e.g. {@code Method.invoke()} and {@code Field.get()}, are
 * replaced with calls to static methods with the same names in {@link robocode.util.RobotReflection}, which take the
 * member as the first parameter.</li>
 * <li>Creating a {@code FileInputStream}, {@code FileReader}, or {@code ObjectInputStream} is replaced with creating
 * the corresponding subclass in the {@code robocode.util} package, e.g. {@link robocode.util.RobotFileInputStream},
 * which performs the checks.</li>
 * </ul>
 * The rewrite changes the existing method references in the constant pool, and changes the instructions using them,
 * i.e. {@code invokevirtual} into {@code invokestatic}, and {@code new} of the Java class into {@code new} of the
 * subclass. These instructions have the same length and stack effect, so no other part of the class file needs to be
 * changed. Uses of these classes and methods that cannot be rewritten are rejected by the {@link RobotClassVerifier}.
 *
 * @author Flemming N. Larsen (original)
 */
final class RobotClassRewriter {

	// Instance methods ("class.name" + descriptor) replaced with static methods of the same name in a Robocode class,
	// which take the instance as the first parameter
	static final Map<String, String> STATIC_METHODS = new HashMap<String, String>();

	static {
		addStaticMethods("java/io/File", "robocode/util/RobotFileAccess",
				"delete()Z",
				"mkdir()Z",
				"mkdirs()Z",
				"exists()Z",
				"isFile()Z",
				"isDirectory()Z",
				"isHidden()Z",
				"canRead()Z",
				"canWrite()Z",
				"canExecute()Z",
				"length()J",
				"lastModified()J",
				"list()[Ljava/lang/String;",
				"list(Ljava/io/FilenameFilter;)[Ljava/lang/String;",
				"listFiles()[Ljava/io/File;",
				"listFiles(Ljava/io/FilenameFilter;)[Ljava/io/File;",
				"listFiles(Ljava/io/FileFilter;)[Ljava/io/File;");

		final String reflection = "robocode/util/RobotReflection";

		addStaticMethods("java/lang/Class", reflection,
				"newInstance()Ljava/lang/Object;");
		addStaticMethods("java/lang/reflect/Method", reflection,
				"invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
				"setAccessible(Z)V");
		addStaticMethods("java/lang/reflect/Constructor", reflection,
				"newInstance([Ljava/lang/Object;)Ljava/lang/Object;",
				"setAccessible(Z)V");
		addStaticMethods("java/lang/reflect/Executable", reflection,
				"setAccessible(Z)V");
		addStaticMethods("java/lang/reflect/AccessibleObject", reflection,
				"setAccessible(Z)V");
		addStaticMethods("java/lang/reflect/Field", reflection,
				"setAccessible(Z)V",
				"get(Ljava/lang/Object;)Ljava/lang/Object;",
				"getBoolean(Ljava/lang/Object;)Z",
				"getByte(Ljava/lang/Object;)B",
				"getChar(Ljava/lang/Object;)C",
				"getShort(Ljava/lang/Object;)S",
				"getInt(Ljava/lang/Object;)I",
				"getLong(Ljava/lang/Object;)J",
				"getFloat(Ljava/lang/Object;)F",
				"getDouble(Ljava/lang/Object;)D",
				"set(Ljava/lang/Object;Ljava/lang/Object;)V",
				"setBoolean(Ljava/lang/Object;Z)V",
				"setByte(Ljava/lang/Object;B)V",
				"setChar(Ljava/lang/Object;C)V",
				"setShort(Ljava/lang/Object;S)V",
				"setInt(Ljava/lang/Object;I)V",
				"setLong(Ljava/lang/Object;J)V",
				"setFloat(Ljava/lang/Object;F)V",
				"setDouble(Ljava/lang/Object;D)V");
	}

	private static void addStaticMethods(String className, String staticClassName, String... methods) {
		for (String method : methods) {
			STATIC_METHODS.put(className + '.' + method, staticClassName);
		}
	}

	// Classes replaced with subclasses, and the constructor descriptors supported by the subclasses
	static final Map<String, String> SUBCLASSES = new HashMap<String, String>();
	static final Map<String, Set<String>> SUBCLASS_CONSTRUCTORS = new HashMap<String, Set<String>>();

	static {
		addSubclass("java/io/FileInputStream", "robocode/util/RobotFileInputStream",
				"(Ljava/lang/String;)V", "(Ljava/io/File;)V");
		addSubclass("java/io/FileReader", "robocode/util/RobotFileReader",
				"(Ljava/lang/String;)V", "(Ljava/io/File;)V");
		addSubclass("java/io/ObjectInputStream", "robocode/util/RobotObjectInputStream",
				"(Ljava/io/InputStream;)V");
	}

	private static void addSubclass(String className, String subclassName, String... constructorDescriptors) {
		SUBCLASSES.put(className, subclassName);
		SUBCLASS_CONSTRUCTORS.put(className, new HashSet<String>(Arrays.asList(constructorDescriptors)));
	}

	private static final int CONSTANT_Utf8 = 1;
	private static final int CONSTANT_Long = 5;
	private static final int CONSTANT_Double = 6;
	private static final int CONSTANT_Class = 7;
	private static final int CONSTANT_Methodref = 10;
	private static final int CONSTANT_NameAndType = 12;

	private static final int INVOKEVIRTUAL = 0xb6;
	private static final int INVOKESPECIAL = 0xb7;
	private static final int INVOKESTATIC = 0xb8;
	private static final int NEW = 0xbb;

	private RobotClassRewriter() {
	}

	/**
	 * Rewrites a robot class file if needed.
	 *
	 * @param classFile the class file.
	 * @return the rewritten class file, or the same class file if nothing needed to be rewritten.
	 * @throws IOException if the class file is invalid.
	 */
	static ByteBuffer rewrite(ByteBuffer classFile) throws IOException {
		byte[] bytes = new byte[classFile.limit()];
		System.arraycopy(classFile.array(), classFile.arrayOffset(), bytes, 0, bytes.length);

		Reader r = new Reader(bytes);

		if (r.u4() != 0xCAFEBABE) {
			throw new IOException("Not a class file");
		}
		r.u2(); // minor version
		r.u2(); // major version

		final int count = r.u2();
		final int countOffset = r.pos - 2;
		int[] tags = new int[count];
		int[] offsets = new int[count];
		String[] utf8s = new String[count];

		for (int i = 1; i < count; i++) {
			offsets[i] = r.pos;
			tags[i] = r.u1();
			int size = constantSize(r, tags[i], utf8s, i); // must be called before reading r.pos, as it can move it
			r.pos += size;
			if (tags[i] == CONSTANT_Long || tags[i] == CONSTANT_Double) {
				i++; // 8-byte constants take up two entries in the constant pool
			}
		}
		final int constantPoolEnd = r.pos;

		// Find the constants to rewrite
		Map<Integer, Integer> staticMethodNameIndexes = new HashMap<Integer, Integer>(); // methodref -> name index
		Map<Integer, String> staticMethodDescriptors = new HashMap<Integer, String>(); // methodref -> new descriptor
		Map<Integer, String> staticMethodClasses = new HashMap<Integer, String>(); // methodref -> new class
		Map<Integer, String> constructorSubclasses = new HashMap<Integer, String>(); // methodref -> subclass
		Map<Integer, String> classSubclasses = new HashMap<Integer, String>(); // class -> subclass

		for (int i = 1; i < count; i++) {
			if (tags[i] == CONSTANT_Class) {
				String subclass = SUBCLASSES.get(utf8(utf8s, u2(bytes, offsets[i] + 1)));
				if (subclass != null) {
					classSubclasses.put(i, subclass);
				}
			} else if (tags[i] == CONSTANT_Methodref) {
				int classIndex = checkIndex(tags, u2(bytes, offsets[i] + 1), CONSTANT_Class);
				int nameAndTypeIndex = checkIndex(tags, u2(bytes, offsets[i] + 3), CONSTANT_NameAndType);
				String className = utf8(utf8s, u2(bytes, offsets[classIndex] + 1));
				int nameIndex = u2(bytes, offsets[nameAndTypeIndex] + 1);
				String name = utf8(utf8s, nameIndex);
				String descriptor = utf8(utf8s, u2(bytes, offsets[nameAndTypeIndex] + 3));

				String staticClassName = STATIC_METHODS.get(className + '.' + name + descriptor);

				if (staticClassName != null) {
					staticMethodNameIndexes.put(i, nameIndex);
					staticMethodDescriptors.put(i, "(L" + className + ';' + descriptor.substring(1));
					staticMethodClasses.put(i, staticClassName);
				} else if ("<init>".equals(name) && SUBCLASSES.containsKey(className)
						&& SUBCLASS_CONSTRUCTORS.get(className).contains(descriptor)) {
					constructorSubclasses.put(i, SUBCLASSES.get(className));
				}
			}
		}
		if (staticMethodNameIndexes.isEmpty() && constructorSubclasses.isEmpty() && classSubclasses.isEmpty()) {
			return classFile;
		}

		// Append the new constants to the constant pool
		ConstantPoolAppender cp = new ConstantPoolAppender(count);

		Map<Integer, Integer> newMethodClassIndexes = new HashMap<Integer, Integer>(); // methodref -> new class index
		Map<Integer, Integer> newNameAndTypeIndexes = new HashMap<Integer, Integer>(); // methodref -> new name and type

		for (Map.Entry<Integer, Integer> entry : staticMethodNameIndexes.entrySet()) {
			int methodRef = entry.getKey();
			newMethodClassIndexes.put(methodRef, cp.addClass(staticMethodClasses.get(methodRef)));
			newNameAndTypeIndexes.put(methodRef, cp.addNameAndType(entry.getValue(), staticMethodDescriptors.get(methodRef)));
		}
		for (Map.Entry<Integer, String> entry : constructorSubclasses.entrySet()) {
			newMethodClassIndexes.put(entry.getKey(), cp.addClass(entry.getValue()));
		}
		Map<Integer, Integer> newClassIndexes = new HashMap<Integer, Integer>(); // class -> subclass class index
		for (Map.Entry<Integer, String> entry : classSubclasses.entrySet()) {
			newClassIndexes.put(entry.getKey(), cp.addClass(entry.getValue()));
		}
		byte[] newConstants = cp.toByteArray();

		// Build the new class file
		byte[] result = new byte[bytes.length + newConstants.length];

		System.arraycopy(bytes, 0, result, 0, constantPoolEnd);
		System.arraycopy(newConstants, 0, result, constantPoolEnd, newConstants.length);
		System.arraycopy(bytes, constantPoolEnd, result, constantPoolEnd + newConstants.length, bytes.length - constantPoolEnd);

		putU2(result, countOffset, cp.getCount());

		// Change the method references
		for (Map.Entry<Integer, Integer> entry : newMethodClassIndexes.entrySet()) {
			int offset = offsets[entry.getKey()];

			putU2(result, offset + 1, entry.getValue());
			Integer nameAndTypeIndex = newNameAndTypeIndexes.get(entry.getKey());
			if (nameAndTypeIndex != null) {
				putU2(result, offset + 3, nameAndTypeIndex);
			}
		}

		// Change the instructions
		Reader cr = new Reader(result);
		cr.pos = constantPoolEnd + newConstants.length;
		cr.u2(); // access flags
		cr.u2(); // this class
		cr.u2(); // super class
		int interfacesCount = cr.u2();
		cr.pos += 2 * interfacesCount; // interfaces

		skipMembers(cr); // fields

		int methodsCount = cr.u2();
		for (int m = 0; m < methodsCount; m++) {
			cr.pos += 6; // access flags, name index, descriptor index
			int attributesCount = cr.u2();

			for (int a = 0; a < attributesCount; a++) {
				int nameIndex = cr.u2();
				int length = cr.u4();
				int attributeEnd = cr.pos + length;

				if ("Code".equals(utf8s[nameIndex])) {
					cr.pos += 4; // max stack, max locals
					int codeLength = cr.u4();

					rewriteInstructions(result, cr.pos, codeLength, staticMethodNameIndexes.keySet(), newClassIndexes);
				}
				cr.pos = attributeEnd;
			}
		}
		return ByteBuffer.wrap(result);
	}

	private static int checkIndex(int[] tags, int index, int expectedTag) throws IOException {
		if (index <= 0 || index >= tags.length || tags[index] != expectedTag) {
			throw new IOException("Invalid constant pool index: " + index);
		}
		return index;
	}

	private static String utf8(String[] utf8s, int index) throws IOException {
		if (index <= 0 || index >= utf8s.length || utf8s[index] == null) {
			throw new IOException("Invalid UTF-8 constant pool index: " + index);
		}
		return utf8s[index];
	}

	private static int constantSize(Reader r, int tag, String[] utf8s, int index) throws IOException {
		switch (tag) {
		case CONSTANT_Utf8:
			int length = u2(r.bytes, r.pos);
			utf8s[index] = r.utf8(length);
			return 0; // utf8() has already moved the position
		case 3: // Integer
		case 4: // Float
		case 9: // Fieldref
		case CONSTANT_Methodref:
		case 11: // InterfaceMethodref
		case CONSTANT_NameAndType:
		case 17: // Dynamic
		case 18: // InvokeDynamic
			return 4;
		case CONSTANT_Long:
		case CONSTANT_Double:
			return 8;
		case CONSTANT_Class:
		case 8: // String
		case 16: // MethodType
		case 19: // Module
		case 20: // Package
			return 2;
		case 15: // MethodHandle
			return 3;
		default:
			throw new IOException("Unknown constant pool tag: " + tag);
		}
	}

	private static void skipMembers(Reader r) {
		int count = r.u2();
		for (int i = 0; i < count; i++) {
			r.pos += 6; // access flags, name index, descriptor index
			int attributesCount = r.u2();
			for (int a = 0; a < attributesCount; a++) {
				r.u2(); // name index
				int length = r.u4();
				r.pos += length;
			}
		}
	}

	private static void rewriteInstructions(byte[] code, int start, int length, Set<Integer> staticMethodRefs,
			Map<Integer, Integer> newClassIndexes) throws IOException {
		int end = start + length;
		int pc = start;

		while (pc < end) {
			int opcode = code[pc] & 0xFF;

			if ((opcode == INVOKEVIRTUAL || opcode == INVOKESPECIAL) && staticMethodRefs.contains(u2(code, pc + 1))) {
				code[pc] = (byte) INVOKESTATIC;
			} else if (opcode == NEW) {
				Integer newClassIndex = newClassIndexes.get(u2(code, pc + 1));
				if (newClassIndex != null) {
					putU2(code, pc + 1, newClassIndex);
				}
			}
			pc += instructionLength(code, start, pc);
		}
		if (pc != end) {
			throw new IOException("Invalid code length");
		}
	}

	/**
	 * Returns the length of an instruction, see the Java Virtual Machine Specification, chapter 6.
	 */
	private static int instructionLength(byte[] code, int codeStart, int pc) throws IOException {
		int opcode = code[pc] & 0xFF;

		if (opcode <= 0x0f) { // nop, aconst_null, iconst_<n>, lconst_<n>, fconst_<n>, dconst_<n>
			return 1;
		}
		switch (opcode) {
		case 0x10: // bipush
		case 0x12: // ldc
		case 0xa9: // ret
		case 0xbc: // newarray
			return 2;
		case 0x11: // sipush
		case 0x13: // ldc_w
		case 0x14: // ldc2_w
		case 0x84: // iinc
		case 0xb2: // getstatic
		case 0xb3: // putstatic
		case 0xb4: // getfield
		case 0xb5: // putfield
		case 0xb6: // invokevirtual
		case 0xb7: // invokespecial
		case 0xb8: // invokestatic
		case 0xbb: // new
		case 0xbd: // anewarray
		case 0xc0: // checkcast
		case 0xc1: // instanceof
		case 0xc6: // ifnull
		case 0xc7: // ifnonnull
			return 3;
		case 0xc5: // multianewarray
			return 4;
		case 0xb9: // invokeinterface
		case 0xba: // invokedynamic
		case 0xc8: // goto_w
		case 0xc9: // jsr_w
			return 5;
		case 0xc4: // wide
			return (code[pc + 1] & 0xFF) == 0x84 ? 6 : 4;
		case 0xaa: { // tableswitch
			int p = pc + 1 + padding(codeStart, pc);
			int low = s4(code, p + 4);
			int high = s4(code, p + 8);
			return (p - pc) + 12 + 4 * (high - low + 1);
		}
		case 0xab: { // lookupswitch
			int p = pc + 1 + padding(codeStart, pc);
			int pairs = s4(code, p + 4);
			return (p - pc) + 8 + 8 * pairs;
		}
		default:
			if (opcode >= 0x15 && opcode <= 0x19 || opcode >= 0x36 && opcode <= 0x3a) {
				return 2; // load and store with local variable index
			}
			if (opcode >= 0x99 && opcode <= 0xa8) {
				return 3; // if<cond>, if_<cmp>, goto, jsr
			}
			if (opcode >= 0x1a && opcode <= 0x83 || opcode >= 0x85 && opcode <= 0x98
					|| opcode >= 0xac && opcode <= 0xb1 || opcode >= 0xbe && opcode <= 0xbf
					|| opcode == 0xc2 || opcode == 0xc3) {
				return 1;
			}
			throw new IOException("Invalid opcode: " + opcode);
		}
	}

	private static int padding(int codeStart, int pc) {
		return (4 - ((pc - codeStart + 1) % 4)) % 4;
	}

	private static int u2(byte[] b, int offset) {
		return ((b[offset] & 0xFF) << 8) | (b[offset + 1] & 0xFF);
	}

	private static int s4(byte[] b, int offset) {
		return ((b[offset] & 0xFF) << 24) | ((b[offset + 1] & 0xFF) << 16) | ((b[offset + 2] & 0xFF) << 8)
				| (b[offset + 3] & 0xFF);
	}

	private static void putU2(byte[] b, int offset, int value) {
		b[offset] = (byte) (value >> 8);
		b[offset + 1] = (byte) value;
	}

	/**
	 * Appends constants to the end of a constant pool, so the indexes of the existing constants are unchanged.
	 */
	private static final class ConstantPoolAppender {
		private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		private final DataOutputStream out = new DataOutputStream(bytes);
		private final Map<String, Integer> utf8Indexes = new HashMap<String, Integer>();
		private final Map<String, Integer> classIndexes = new HashMap<String, Integer>();
		private final Map<String, Integer> nameAndTypeIndexes = new HashMap<String, Integer>();
		private int count;

		ConstantPoolAppender(int count) {
			this.count = count;
		}

		int getCount() throws IOException {
			if (count > 0xFFFF) {
				throw new IOException("Constant pool is too large");
			}
			return count;
		}

		int addUtf8(String value) throws IOException {
			Integer index = utf8Indexes.get(value);
			if (index == null) {
				index = count++;
				out.writeByte(CONSTANT_Utf8);
				out.writeUTF(value);
				utf8Indexes.put(value, index);
			}
			return index;
		}

		int addClass(String internalName) throws IOException {
			Integer index = classIndexes.get(internalName);
			if (index == null) {
				int nameIndex = addUtf8(internalName);
				index = count++;
				out.writeByte(CONSTANT_Class);
				out.writeShort(nameIndex);
				classIndexes.put(internalName, index);
			}
			return index;
		}

		int addNameAndType(int nameIndex, String descriptor) throws IOException {
			String key = nameIndex + descriptor;
			Integer index = nameAndTypeIndexes.get(key);
			if (index == null) {
				int descriptorIndex = addUtf8(descriptor);
				index = count++;
				out.writeByte(CONSTANT_NameAndType);
				out.writeShort(nameIndex);
				out.writeShort(descriptorIndex);
				nameAndTypeIndexes.put(key, index);
			}
			return index;
		}

		byte[] toByteArray() throws IOException {
			out.flush();
			return bytes.toByteArray();
		}
	}

	private static final class Reader {
		final byte[] bytes;
		int pos;

		Reader(byte[] bytes) {
			this.bytes = bytes;
		}

		int u1() {
			return bytes[pos++] & 0xFF;
		}

		int u2() {
			int value = RobotClassRewriter.u2(bytes, pos);
			pos += 2;
			return value;
		}

		int u4() {
			int value = s4(bytes, pos);
			pos += 4;
			return value;
		}

		String utf8(int length) throws IOException {
			java.io.DataInputStream in = new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes, pos, 2 + length));
			String value = in.readUTF();
			pos += 2 + length;
			return value;
		}
	}
}
