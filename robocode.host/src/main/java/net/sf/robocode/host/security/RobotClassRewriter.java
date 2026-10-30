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
 * Rewrites robot class files so file modifications are checked by Robocode.
 * <p>
 * Without the SecurityManager (removed with Java 24), the JDK does not check if a robot is allowed to delete a file
 * or create a directory. Robots are only allowed to do this in their own data directory, so calls to
 * {@code java.io.File.delete()}, {@code mkdir()}, and {@code mkdirs()} are replaced with calls to the static methods
 * with the same names in {@link robocode.util.RobotFileAccess}, which perform the checks.
 * <p>
 * The rewrite changes the existing method reference in the constant pool to the static method, and changes the
 * invoke instructions using it into {@code invokestatic}. These instructions have the same length and stack effect,
 * so no other part of the class file needs to be changed.
 *
 * @author Flemming N. Larsen (original)
 */
final class RobotClassRewriter {

	private static final String FILE_CLASS = "java/io/File";
	private static final Set<String> FILE_METHODS = new HashSet<String>(Arrays.asList("delete", "mkdir", "mkdirs"));
	private static final String FILE_METHOD_DESCRIPTOR = "()Z";

	private static final String ACCESS_CLASS = "robocode/util/RobotFileAccess";
	private static final String ACCESS_METHOD_DESCRIPTOR = "(Ljava/io/File;)Z";

	private static final int CONSTANT_Utf8 = 1;
	private static final int CONSTANT_Long = 5;
	private static final int CONSTANT_Double = 6;
	private static final int CONSTANT_Class = 7;
	private static final int CONSTANT_Methodref = 10;
	private static final int CONSTANT_NameAndType = 12;

	private static final int INVOKEVIRTUAL = 0xb6;
	private static final int INVOKESPECIAL = 0xb7;
	private static final int INVOKESTATIC = 0xb8;

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

		// Find the method references to rewrite, and the names used by them
		Map<Integer, Integer> methodRefNameIndexes = new LinkedHashMap<Integer, Integer>();

		for (int i = 1; i < count; i++) {
			if (tags[i] != CONSTANT_Methodref) {
				continue;
			}
			int classIndex = u2(bytes, offsets[i] + 1);
			int nameAndTypeIndex = u2(bytes, offsets[i] + 3);
			int classNameIndex = u2(bytes, offsets[classIndex] + 1);
			int nameIndex = u2(bytes, offsets[nameAndTypeIndex] + 1);
			int descriptorIndex = u2(bytes, offsets[nameAndTypeIndex] + 3);

			if (FILE_CLASS.equals(utf8s[classNameIndex]) && FILE_METHODS.contains(utf8s[nameIndex])
					&& FILE_METHOD_DESCRIPTOR.equals(utf8s[descriptorIndex])) {
				methodRefNameIndexes.put(i, nameIndex);
			}
		}
		if (methodRefNameIndexes.isEmpty()) {
			return classFile;
		}

		// Append the new constants to the constant pool
		ByteArrayOutputStream newConstants = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(newConstants);
		int nextIndex = count;

		final int accessClassNameIndex = nextIndex++;
		out.writeByte(CONSTANT_Utf8);
		out.writeUTF(ACCESS_CLASS);

		final int accessClassIndex = nextIndex++;
		out.writeByte(CONSTANT_Class);
		out.writeShort(accessClassNameIndex);

		final int descriptorIndex = nextIndex++;
		out.writeByte(CONSTANT_Utf8);
		out.writeUTF(ACCESS_METHOD_DESCRIPTOR);

		Map<Integer, Integer> nameAndTypeIndexes = new HashMap<Integer, Integer>();

		for (int nameIndex : new HashSet<Integer>(methodRefNameIndexes.values())) {
			nameAndTypeIndexes.put(nameIndex, nextIndex++);
			out.writeByte(CONSTANT_NameAndType);
			out.writeShort(nameIndex);
			out.writeShort(descriptorIndex);
		}
		out.flush();

		if (nextIndex > 0xFFFF) {
			throw new IOException("Constant pool is too large");
		}

		// Build the new class file
		byte[] result = new byte[bytes.length + newConstants.size()];

		System.arraycopy(bytes, 0, result, 0, constantPoolEnd);
		System.arraycopy(newConstants.toByteArray(), 0, result, constantPoolEnd, newConstants.size());
		System.arraycopy(bytes, constantPoolEnd, result, constantPoolEnd + newConstants.size(), bytes.length - constantPoolEnd);

		putU2(result, countOffset, nextIndex);

		// Change the method references to the static methods
		for (Map.Entry<Integer, Integer> entry : methodRefNameIndexes.entrySet()) {
			int offset = offsets[entry.getKey()];

			putU2(result, offset + 1, accessClassIndex);
			putU2(result, offset + 3, nameAndTypeIndexes.get(entry.getValue()));
		}

		// Change the instructions invoking the method references into invokestatic
		Reader cr = new Reader(result);
		cr.pos = constantPoolEnd + newConstants.size();
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

					rewriteInstructions(result, cr.pos, codeLength, methodRefNameIndexes.keySet());
				}
				cr.pos = attributeEnd;
			}
		}
		return ByteBuffer.wrap(result);
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

	private static void skipMembers(Reader r) throws IOException {
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

	private static void rewriteInstructions(byte[] code, int start, int length, Set<Integer> methodRefs) throws IOException {
		int end = start + length;
		int pc = start;

		while (pc < end) {
			int opcode = code[pc] & 0xFF;

			if ((opcode == INVOKEVIRTUAL || opcode == INVOKESPECIAL) && methodRefs.contains(u2(code, pc + 1))) {
				code[pc] = (byte) INVOKESTATIC;
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
