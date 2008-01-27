// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

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
