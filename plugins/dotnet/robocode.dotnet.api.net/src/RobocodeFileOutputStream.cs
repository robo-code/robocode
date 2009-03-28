namespace robocode
{
    /*public class RobocodeFileOutputStream : OutputStream
    {
        private string fileName;
        private readonly FileOutputStream @Out;

        [MethodImpl(MethodImplOptions.NoInlining), Throws(new[] {"java.io.IOException"}),
         LineNumberTable(new byte[] {6, 0x6c})]
        public RobocodeFileOutputStream(File file) : this(file.getPath())
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x10, 0x66})]
        public RobocodeFileOutputStream(FileDescriptor fdObj)
        {
            Throwable.__<suppressFillInStackTrace>();
            throw new RobotException("Creating a RobocodeFileOutputStream with a FileDescriptor is not supported.");
        }

        [MethodImpl(MethodImplOptions.NoInlining), Throws(new[] {"java.io.IOException"}),
         LineNumberTable(new byte[] {0x1c, 0x68})]
        public RobocodeFileOutputStream(string fileName) : this(fileName, false)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 120, 0x43, 0x68, 0x90, 0x66, 0xb0, 110}),
         Throws(new[] {"java.io.IOException"})]
        public RobocodeFileOutputStream(string fileName, bool append)
        {
            var num = (int) append;
            var base2 = (IThreadManagerBase) ContainerBase.getComponent(ClassLiteral<IThreadManagerBase>.Value);
            if (base2 == null)
            {
                Throwable.__<suppressFillInStackTrace>();
                throw new RobotException("ThreadManager cannot be null!");
            }
            @Out = base2.createRobotFileStream(fileName, (bool) num);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x38, 0x6b}),
         Throws(new[] {"java.io.IOException"})]
        public override sealed void close()
        {
            @Out.close();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Throws(new[] {"java.io.IOException"}),
         LineNumberTable(new byte[] {0x43, 0x6b})]
        public override sealed void flush()
        {
            @Out.flush();
        }

        public string getName()
        {
            return fileName;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Throws(new[] {"java.io.IOException"}),
         LineNumberTable(new byte[] {0x58, 0x6c})]
        public override sealed void write(byte[] b)
        {
            @Out.write(b);
        }

        [MethodImpl(MethodImplOptions.NoInlining), Throws(new[] {"java.io.IOException"}),
         LineNumberTable(new byte[] {0x70, 0x6c})]
        public override sealed void write(int b)
        {
            @Out.write(b);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {100, 110}),
         Throws(new[] {"java.io.IOException"})]
        public override sealed void write(byte[] b, int off, int len)
        {
            @Out.write(b, off, len);
        }
    }*/
}