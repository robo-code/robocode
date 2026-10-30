/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Verifies the class files of robots before they are defined by the {@link RobotClassLoader}.
 * <p>
 * The SecurityManager has been removed with Java 24, so the JDK no longer checks permissions when robot code
 * performs sensitive operations like executing processes, opening sockets, or writing files. Instead, every class,
 * method, and field referenced from a robot class is checked against an allowlist when the robot class is loaded.
 * A robot that references anything that is not allowed is rejected before any of its code is executed.
 * <p>
 * The rules are deliberately an allowlist of packages and classes, so new packages and classes added in future
 * Java versions are denied by default. Inside allowed packages, a denylist removes the dangerous classes and
 * members, e.g. process execution, file writing, reflection, and changing system properties.
 * <p>
 * The rules are applied with all Java versions, i.e. also when the SecurityManager is available.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotClassVerifier {

	/**
	 * Provides the class files of the robot, i.e. classes that are loaded from the robot class path.
	 */
	public interface RobotClassProvider {

		/**
		 * Returns the class file of a robot class.
		 *
		 * @param internalName the internal class name, e.g. "sample/Walls".
		 * @return the class file, or null if the class is not a robot class, e.g. a Java or Robocode API class.
		 */
		ByteBuffer getRobotClassFile(String internalName);
	}

	// Packages (exact names, not including sub packages) that robots are allowed to use classes from
	private static final Set<String> ALLOWED_PACKAGES = new HashSet<String>(Arrays.asList(
			"java/lang",
			"java/lang/annotation",
			"java/lang/ref",
			"java/util",
			"java/util/concurrent",
			"java/util/concurrent/atomic",
			"java/util/concurrent/locks",
			"java/util/function",
			"java/util/random",
			"java/util/regex",
			"java/util/stream",
			"java/util/zip",
			"java/io",
			"java/math",
			"java/nio",
			"java/nio/charset",
			"java/text",
			"java/time",
			"java/time/chrono",
			"java/time/format",
			"java/time/temporal",
			"java/awt/event",
			"java/awt/font",
			"java/awt/geom",
			"java/awt/image",
			"kotlin",
			"kotlin/annotation",
			"kotlin/collections",
			"kotlin/comparisons",
			"kotlin/concurrent",
			"kotlin/contracts",
			"kotlin/coroutines",
			"kotlin/coroutines/intrinsics",
			"kotlin/coroutines/jvm/internal",
			"kotlin/enums",
			"kotlin/internal",
			"kotlin/io",
			"kotlin/jvm",
			"kotlin/jvm/functions",
			"kotlin/jvm/internal",
			"kotlin/jvm/internal/markers",
			"kotlin/math",
			"kotlin/properties",
			"kotlin/random",
			"kotlin/ranges",
			"kotlin/reflect",
			"kotlin/sequences",
			"kotlin/text",
			"kotlin/time"
	));

	// Classes that robots are allowed to use from packages that are otherwise not allowed
	private static final Set<String> ALLOWED_CLASSES = new HashSet<String>(Arrays.asList(
			// Painting
			"java/awt/AlphaComposite",
			"java/awt/BasicStroke",
			"java/awt/Color",
			"java/awt/Composite",
			"java/awt/Dimension",
			"java/awt/Font",
			"java/awt/FontMetrics",
			"java/awt/GradientPaint",
			"java/awt/Graphics",
			"java/awt/Graphics2D",
			"java/awt/Image",
			"java/awt/Insets",
			"java/awt/LinearGradientPaint",
			"java/awt/MultipleGradientPaint",
			"java/awt/Paint",
			"java/awt/Point",
			"java/awt/Polygon",
			"java/awt/RadialGradientPaint",
			"java/awt/Rectangle",
			"java/awt/RenderingHints",
			"java/awt/RenderingHints$Key",
			"java/awt/Shape",
			"java/awt/Stroke",
			"java/awt/Transparency",
			// Harmless reflection utilities, and types describing generic types, which cannot be used for invoking code
			"java/lang/reflect/Array",
			"java/lang/reflect/GenericArrayType",
			"java/lang/reflect/Modifier",
			"java/lang/reflect/ParameterizedType",
			"java/lang/reflect/Type",
			"java/lang/reflect/TypeVariable",
			"java/lang/reflect/WildcardType",
			// Used by the code generated for serializable lambdas
			"java/lang/invoke/SerializedLambda"
	));

	// Classes in allowed packages that robots are not allowed to use at all
	private static final Set<String> DENIED_CLASSES = new HashSet<String>(Arrays.asList(
			"java/lang/ClassLoader",
			"java/lang/Compiler",
			"java/lang/Module",
			"java/lang/ModuleLayer",
			"java/lang/ModuleLayer$Controller",
			"java/lang/Process",
			"java/lang/ProcessBuilder",
			"java/lang/ProcessBuilder$Redirect",
			"java/lang/ProcessHandle",
			"java/lang/ProcessHandle$Info",
			"java/lang/SecurityManager",
			"java/lang/ThreadGroup",
			"java/io/FileOutputStream",
			"java/io/FileWriter",
			"java/io/RandomAccessFile",
			"java/util/ServiceLoader"
	));

	// Prefixes of classes in allowed packages that robots are not allowed to use at all
	private static final String[] DENIED_CLASS_PREFIXES = {
			"kotlin/io/FilesKt" // Kotlin file extension functions, e.g. File.writeText() and File.deleteRecursively()
	};

	// Members (any descriptor) that robots are not allowed to use, written as "class.member"
	private static final Set<String> DENIED_MEMBERS = new HashSet<String>(Arrays.asList(
			// Reflection and class loading. Note that Class.forName(String) is allowed, as it is used by the code that
			// old Java compilers generated for class literals, and as it can only load the classes that the robot class
			// loader allows. Class.forName() with a class loader or module is denied below.
			"java/lang/Class.getClassLoader",
			"java/lang/Class.getConstructor",
			"java/lang/Class.getConstructors",
			"java/lang/Class.getDeclaredConstructor",
			"java/lang/Class.getDeclaredConstructors",
			"java/lang/Class.getDeclaredField",
			"java/lang/Class.getDeclaredFields",
			"java/lang/Class.getDeclaredMethod",
			"java/lang/Class.getDeclaredMethods",
			"java/lang/Class.getEnclosingConstructor",
			"java/lang/Class.getEnclosingMethod",
			"java/lang/Class.getField",
			"java/lang/Class.getFields",
			"java/lang/Class.getMethod",
			"java/lang/Class.getMethods",
			"java/lang/Class.getModule",
			"java/lang/Class.getProtectionDomain",
			"java/lang/Class.getRecordComponents",
			"java/lang/Class.newInstance",
			// Process execution, native code, and JVM shutdown
			"java/lang/Runtime.addShutdownHook",
			"java/lang/Runtime.exec",
			"java/lang/Runtime.exit",
			"java/lang/Runtime.halt",
			"java/lang/Runtime.load",
			"java/lang/Runtime.loadLibrary",
			"java/lang/Runtime.removeShutdownHook",
			"java/lang/System.exit",
			"java/lang/System.inheritedChannel",
			"java/lang/System.load",
			"java/lang/System.loadLibrary",
			// System properties and standard streams
			"java/lang/System.clearProperty",
			"java/lang/System.getProperties",
			"java/lang/System.setErr",
			"java/lang/System.setIn",
			"java/lang/System.setOut",
			"java/lang/System.setProperties",
			"java/lang/System.setProperty",
			"java/lang/System.setSecurityManager",
			// Threads of the game engine and other robots
			"java/lang/Thread.enumerate",
			"java/lang/Thread.getAllStackTraces",
			"java/lang/Thread.getContextClassLoader",
			"java/lang/Thread.resume",
			"java/lang/Thread.setContextClassLoader",
			"java/lang/Thread.setDefaultUncaughtExceptionHandler",
			"java/lang/Thread.stop",
			"java/lang/Thread.suspend",
			// File access. Note that direct calls to the File methods listed in RobotClassRewriter.FILE_METHODS, e.g.
			// delete() and exists(), are replaced with calls that check that the robot is allowed to access the file.
			"java/io/File.canExecute",
			"java/io/File.canRead",
			"java/io/File.canWrite",
			"java/io/File.createNewFile",
			"java/io/File.createTempFile",
			"java/io/File.delete",
			"java/io/File.deleteOnExit",
			"java/io/File.exists",
			"java/io/File.isDirectory",
			"java/io/File.isFile",
			"java/io/File.isHidden",
			"java/io/File.lastModified",
			"java/io/File.length",
			"java/io/File.list",
			"java/io/File.listFiles",
			"java/io/File.mkdir",
			"java/io/File.mkdirs",
			"java/io/File.renameTo",
			"java/io/File.setExecutable",
			"java/io/File.setLastModified",
			"java/io/File.setReadOnly",
			"java/io/File.setReadable",
			"java/io/File.setWritable"
	));

	// Classes that robots are not allowed to extend, as these are replaced with subclasses by the RobotClassRewriter
	private static final Set<String> FINAL_CLASSES = RobotClassRewriter.SUBCLASSES.keySet();

	// Prefixes of members including their descriptor that robots are not allowed to use, written as "class.member(descriptor"
	private static final String[] DENIED_MEMBER_PREFIXES = {
			// Loading classes with another class loader than the robot's, or from a module
			"java/lang/Class.forName(Ljava/lang/String;ZLjava/lang/ClassLoader;",
			"java/lang/Class.forName(Ljava/lang/Module;",
			// Constructors that open a file for writing
			"java/io/PrintStream.<init>(Ljava/lang/String;",
			"java/io/PrintStream.<init>(Ljava/io/File;",
			"java/io/PrintWriter.<init>(Ljava/lang/String;",
			"java/io/PrintWriter.<init>(Ljava/io/File;",
			"java/util/Formatter.<init>(Ljava/lang/String;",
			"java/util/Formatter.<init>(Ljava/io/File;",
			// Constructors and methods that open a file for reading
			"java/util/Scanner.<init>(Ljava/io/File;",
			"java/util/Scanner.<init>(Ljava/nio/file/Path;",
			"java/util/zip/ZipFile.<init>",
			"java/awt/Font.createFont(ILjava/io/File;",
			// Constructors of the classes replaced with subclasses by the RobotClassRewriter. Constructors supported by
			// the subclasses have already been replaced when the class is verified, e.g. FileInputStream(File), so only
			// the unsupported ones are left, e.g. FileInputStream(FileDescriptor).
			"java/io/FileInputStream.<init>",
			"java/io/FileReader.<init>",
			"java/io/ObjectInputStream.<init>",
	};

	// Members that robots are allowed to use in classes that are otherwise not allowed.
	// These are the bootstrap methods used by the Java compiler for lambdas, string concatenation, records, and switches.
	private static final Set<String> ALLOWED_MEMBERS = new HashSet<String>(Arrays.asList(
			"java/lang/invoke/LambdaMetafactory.metafactory",
			"java/lang/invoke/LambdaMetafactory.altMetafactory",
			"java/lang/invoke/StringConcatFactory.makeConcat",
			"java/lang/invoke/StringConcatFactory.makeConcatWithConstants",
			"java/lang/runtime/ObjectMethods.bootstrap",
			"java/lang/runtime/SwitchBootstraps.enumSwitch",
			"java/lang/runtime/SwitchBootstraps.typeSwitch"
	));

	private static final String ROBOCODE_API_PREFIX = "robocode/";
	private static final String ROBOCODE_CONTROL_PREFIX = "robocode/control/";

	// Cache of host classes (as "class.member") that have been checked for denied members
	private static final Map<String, Boolean> deniedHostMemberCache = new ConcurrentHashMap<String, Boolean>();

	private final RobotClassProvider robotClassProvider;
	private final ClassLoader hostClassLoader;

	// Caches for the robot being verified, as the class files of the robot are read from disk
	private final Map<String, Boolean> isRobotClassCache = new HashMap<String, Boolean>();
	private final Map<String, Set<String>> hostAncestorsCache = new HashMap<String, Set<String>>();

	/**
	 * Creates a verifier for the classes of a robot.
	 *
	 * @param robotClassProvider provides the class files of the robot.
	 * @param hostClassLoader    the class loader used for loading Java and Robocode API classes.
	 */
	public RobotClassVerifier(RobotClassProvider robotClassProvider, ClassLoader hostClassLoader) {
		this.robotClassProvider = robotClassProvider;
		this.hostClassLoader = hostClassLoader;
	}

	/**
	 * Verifies a robot class file.
	 *
	 * @param classFile the class file to verify.
	 * @throws SecurityException if the class uses something that robots are not allowed to use.
	 */
	public void verify(ByteBuffer classFile) {
		ClassFile cf;
		try {
			cf = ClassFile.parse(classFile);
		} catch (IOException e) {
			throw new SecurityException("Invalid class file: " + e.getMessage());
		}

		if (cf.superName != null) {
			checkClassAllowed(cf.thisName, cf.superName);

			if (FINAL_CLASSES.contains(cf.superName)) {
				throw new SecurityException(cf.thisName.replace('/', '.') + " is not allowed to extend "
						+ cf.superName.replace('/', '.'));
			}
		}
		for (String interfaceName : cf.interfaceNames) {
			checkClassAllowed(cf.thisName, interfaceName);
		}
		for (MemberRef ref : cf.memberRefs) {
			checkMemberAllowed(cf.thisName, ref);
		}
	}

	private void checkClassAllowed(String className, String usedClass) {
		if (!isRobotClass(usedClass) && !isAllowedHostClass(usedClass)) {
			throw new SecurityException(className.replace('/', '.') + " is not allowed to use " + usedClass.replace('/', '.'));
		}
	}

	private void checkMemberAllowed(String className, MemberRef ref) {
		if (isRobotClass(ref.owner)) {
			// Members of robot classes might be inherited from a Java or Robocode API class
			for (String hostClass : getNearestHostAncestors(ref.owner)) {
				if (isDeniedHostMember(hostClass, ref)) {
					throw notAllowed(className, hostClass, ref);
				}
			}
			return;
		}
		if (ALLOWED_MEMBERS.contains(ref.owner + '.' + ref.name)) {
			return;
		}
		if (!isAllowedHostClass(ref.owner) || isDeniedHostMember(ref.owner, ref)) {
			throw notAllowed(className, ref.owner, ref);
		}
	}

	private static SecurityException notAllowed(String className, String usedClass, MemberRef ref) {
		return new SecurityException(
				className.replace('/', '.') + " is not allowed to use " + usedClass.replace('/', '.') + '.' + ref.name);
	}

	private synchronized boolean isRobotClass(String internalName) {
		if (internalName.startsWith("[")) {
			return false;
		}
		Boolean isRobotClass = isRobotClassCache.get(internalName);

		if (isRobotClass == null) {
			isRobotClass = robotClassProvider.getRobotClassFile(internalName) != null;
			isRobotClassCache.put(internalName, isRobotClass);
		}
		return isRobotClass;
	}

	/**
	 * Returns the nearest Java and Robocode API classes and interfaces that a robot class inherits from.
	 */
	private synchronized Set<String> getNearestHostAncestors(String robotClass) {
		Set<String> hostAncestors = hostAncestorsCache.get(robotClass);

		if (hostAncestors == null) {
			hostAncestors = findNearestHostAncestors(robotClass);
			hostAncestorsCache.put(robotClass, hostAncestors);
		}
		return hostAncestors;
	}

	private Set<String> findNearestHostAncestors(String robotClass) {
		Set<String> hostAncestors = new HashSet<String>();
		Set<String> visited = new HashSet<String>();
		Deque<String> queue = new ArrayDeque<String>();

		queue.add(robotClass);
		while (!queue.isEmpty()) {
			String name = queue.poll();

			if (!visited.add(name)) {
				continue; // Protect against cyclic class hierarchies
			}
			ByteBuffer classFile = robotClassProvider.getRobotClassFile(name);
			if (classFile == null) {
				hostAncestors.add(name);
				continue;
			}
			ClassFile cf;
			try {
				cf = ClassFile.parse(classFile);
			} catch (IOException e) {
				throw new SecurityException("Invalid class file for " + name.replace('/', '.') + ": " + e.getMessage());
			}
			if (cf.superName != null) {
				queue.add(cf.superName);
			}
			queue.addAll(cf.interfaceNames);
		}
		return hostAncestors;
	}

	private static boolean isAllowedHostClass(String internalName) {
		if (internalName.startsWith("[")) {
			return true; // Arrays, e.g. the clone() method of an array
		}
		if (internalName.startsWith(ROBOCODE_API_PREFIX)) {
			return !internalName.startsWith(ROBOCODE_CONTROL_PREFIX);
		}
		if (ALLOWED_CLASSES.contains(internalName)) {
			return true;
		}
		if (DENIED_CLASSES.contains(internalName)) {
			return false;
		}
		for (String prefix : DENIED_CLASS_PREFIXES) {
			if (internalName.startsWith(prefix)) {
				return false;
			}
		}
		int lastSlash = internalName.lastIndexOf('/');

		return lastSlash > 0 && ALLOWED_PACKAGES.contains(internalName.substring(0, lastSlash));
	}

	private static boolean isDeniedMember(String internalClassName, MemberRef ref) {
		if (DENIED_CLASSES.contains(internalClassName)) {
			return true;
		}
		for (String prefix : DENIED_CLASS_PREFIXES) {
			if (internalClassName.startsWith(prefix)) {
				return true;
			}
		}
		if (DENIED_MEMBERS.contains(internalClassName + '.' + ref.name)) {
			return true;
		}
		String member = internalClassName + '.' + ref.name + ref.descriptor;

		for (String prefix : DENIED_MEMBER_PREFIXES) {
			if (member.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a member used via a Java or Robocode API class is denied. The member might be declared in a super class
	 * or interface of the class, so all classes in the class hierarchy declaring a member with the same name is checked.
	 */
	private boolean isDeniedHostMember(final String internalClassName, final MemberRef ref) {
		if (isDeniedMember(internalClassName, ref)) {
			return true;
		}
		if (ref.name.equals("<init>") || ref.name.equals("<clinit>")) {
			return false; // Constructors are not inherited
		}
		final String key = internalClassName + '.' + ref.name + (ref.isField ? "" : "()");
		Boolean denied = deniedHostMemberCache.get(key);

		if (denied == null) {
			denied = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
				public Boolean run() {
					return isDeniedInClassHierarchy(internalClassName, ref);
				}
			});
			deniedHostMemberCache.put(key, denied);
		}
		return denied;
	}

	private boolean isDeniedInClassHierarchy(String internalClassName, MemberRef ref) {
		Class<?> type;
		try {
			type = Class.forName(internalClassName.replace('/', '.'), false, hostClassLoader);
		} catch (ClassNotFoundException e) {
			return false; // The class cannot be loaded by the robot either
		} catch (LinkageError e) {
			return false;
		}

		Set<Class<?>> visited = new HashSet<Class<?>>();
		Deque<Class<?>> queue = new ArrayDeque<Class<?>>();

		queue.add(type);
		while (!queue.isEmpty()) {
			Class<?> c = queue.poll();

			if (!visited.add(c)) {
				continue;
			}
			if (declaresMember(c, ref) && isDeniedMember(c.getName().replace('.', '/'), ref)) {
				return true;
			}
			if (c.getSuperclass() != null) {
				queue.add(c.getSuperclass());
			}
			queue.addAll(Arrays.asList(c.getInterfaces()));
		}
		return false;
	}

	private static boolean declaresMember(Class<?> c, MemberRef ref) {
		try {
			if (ref.isField) {
				for (Field field : c.getDeclaredFields()) {
					if (field.getName().equals(ref.name)) {
						return true;
					}
				}
			} else {
				for (Method method : c.getDeclaredMethods()) {
					if (method.getName().equals(ref.name)) {
						return true;
					}
				}
			}
		} catch (LinkageError e) {
			return true; // Cannot tell, so assume that the class declares the member
		}
		return false;
	}

	/**
	 * Reference to a field or method from the constant pool of a class file.
	 */
	private static final class MemberRef {
		final String owner;
		final String name;
		final String descriptor;
		final boolean isField;

		MemberRef(String owner, String name, String descriptor, boolean isField) {
			this.owner = owner;
			this.name = name;
			this.descriptor = descriptor;
			this.isField = isField;
		}
	}

	/**
	 * The parts of a class file needed for verifying a robot class.
	 * See the Java Virtual Machine Specification, chapter 4: The class File Format.
	 */
	private static final class ClassFile {
		private static final int CONSTANT_Utf8 = 1;
		private static final int CONSTANT_Integer = 3;
		private static final int CONSTANT_Float = 4;
		private static final int CONSTANT_Long = 5;
		private static final int CONSTANT_Double = 6;
		private static final int CONSTANT_Class = 7;
		private static final int CONSTANT_String = 8;
		private static final int CONSTANT_Fieldref = 9;
		private static final int CONSTANT_Methodref = 10;
		private static final int CONSTANT_InterfaceMethodref = 11;
		private static final int CONSTANT_NameAndType = 12;
		private static final int CONSTANT_MethodHandle = 15;
		private static final int CONSTANT_MethodType = 16;
		private static final int CONSTANT_Dynamic = 17;
		private static final int CONSTANT_InvokeDynamic = 18;
		private static final int CONSTANT_Module = 19;
		private static final int CONSTANT_Package = 20;

		String thisName;
		String superName;
		final List<String> interfaceNames = new ArrayList<String>();
		final List<MemberRef> memberRefs = new ArrayList<MemberRef>();

		static ClassFile parse(ByteBuffer classFile) throws IOException {
			DataInputStream in = new DataInputStream(
					new ByteArrayInputStream(classFile.array(), classFile.arrayOffset(), classFile.limit()));

			if (in.readInt() != 0xCAFEBABE) {
				throw new IOException("Not a class file");
			}
			in.readUnsignedShort(); // minor version
			in.readUnsignedShort(); // major version

			int count = in.readUnsignedShort();
			int[] tags = new int[count];
			String[] utf8s = new String[count];
			int[] index1 = new int[count];
			int[] index2 = new int[count];

			for (int i = 1; i < count; i++) {
				int tag = in.readUnsignedByte();
				tags[i] = tag;

				switch (tag) {
				case CONSTANT_Utf8:
					utf8s[i] = in.readUTF();
					break;

				case CONSTANT_Integer:
				case CONSTANT_Float:
					in.readInt();
					break;

				case CONSTANT_Long:
				case CONSTANT_Double:
					in.readLong();
					i++; // 8-byte constants take up two entries in the constant pool
					break;

				case CONSTANT_Class:
				case CONSTANT_String:
				case CONSTANT_MethodType:
				case CONSTANT_Module:
				case CONSTANT_Package:
					index1[i] = in.readUnsignedShort();
					break;

				case CONSTANT_Fieldref:
				case CONSTANT_Methodref:
				case CONSTANT_InterfaceMethodref:
				case CONSTANT_NameAndType:
				case CONSTANT_Dynamic:
				case CONSTANT_InvokeDynamic:
					index1[i] = in.readUnsignedShort();
					index2[i] = in.readUnsignedShort();
					break;

				case CONSTANT_MethodHandle:
					index1[i] = in.readUnsignedByte(); // reference kind
					index2[i] = in.readUnsignedShort(); // reference index
					break;

				default:
					throw new IOException("Unknown constant pool tag: " + tag);
				}
			}

			ClassFile cf = new ClassFile();

			// All member references, including those used by method handles and bootstrap methods, are in the constant pool
			for (int i = 1; i < count; i++) {
				int tag = tags[i];

				if (tag == CONSTANT_Fieldref || tag == CONSTANT_Methodref || tag == CONSTANT_InterfaceMethodref) {
					String owner = className(tags, utf8s, index1, index1[i]);
					int nameAndType = index2[i];

					if (tags[nameAndType] != CONSTANT_NameAndType) {
						throw new IOException("Invalid member reference");
					}
					String name = utf8(tags, utf8s, index1[nameAndType]);
					String descriptor = utf8(tags, utf8s, index2[nameAndType]);

					cf.memberRefs.add(new MemberRef(owner, name, descriptor, tag == CONSTANT_Fieldref));
				}
			}

			in.readUnsignedShort(); // access flags
			cf.thisName = className(tags, utf8s, index1, in.readUnsignedShort());

			int superIndex = in.readUnsignedShort();
			cf.superName = superIndex == 0 ? null : className(tags, utf8s, index1, superIndex);

			int interfacesCount = in.readUnsignedShort();
			for (int i = 0; i < interfacesCount; i++) {
				cf.interfaceNames.add(className(tags, utf8s, index1, in.readUnsignedShort()));
			}
			return cf;
		}

		private static String className(int[] tags, String[] utf8s, int[] index1, int index) throws IOException {
			if (index <= 0 || index >= tags.length || tags[index] != CONSTANT_Class) {
				throw new IOException("Invalid class reference");
			}
			return utf8(tags, utf8s, index1[index]);
		}

		private static String utf8(int[] tags, String[] utf8s, int index) throws IOException {
			if (index <= 0 || index >= tags.length || tags[index] != CONSTANT_Utf8) {
				throw new IOException("Invalid UTF-8 reference");
			}
			return utf8s[index];
		}
	}
}
