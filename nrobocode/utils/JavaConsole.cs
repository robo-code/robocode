using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using java.io;

namespace nrobocode.utils
{
    class JavaConsole :  TextWriter
    {
        private PrintStream stream;
        public JavaConsole(PrintStream stream)
        {
            this.stream = stream;
        }

        public override Encoding Encoding
        {
            get { return Encoding.Unicode; }
        }

        public override void Write(char value)
        {
            stream.print(value);
        }

        public override void Write(string value)
        {
            stream.print(value);
        }

        public override void Write(char[] value)
        {
            stream.print(value);
        }

        public override void WriteLine()
        {
            stream.println();
        }

        public override void WriteLine(string value)
        {
            stream.println(value);
        }
    }
}
