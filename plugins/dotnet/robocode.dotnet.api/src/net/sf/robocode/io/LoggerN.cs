/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.IO;
using System.Security.Permissions;
using System.Text;
using net.sf.robocode.security;

namespace net.sf.robocode.io
{
#pragma warning disable 1591
    ///<summary>
    ///  This is a class used for logging.
    ///</summary>
    /// <exclude/>
    [RobocodeInternalPermission(SecurityAction.LinkDemand)]
    public class LoggerN
    {
        public static TextWriter realOut = Console.Out;
        public static TextWriter realErr = Console.Error;
        public static TextWriter robotOut = Console.Out;

        private static ILoggerN logListener;
        private static readonly StringBuilder logBuffer = new StringBuilder();
        [ThreadStatic] public static bool IsSafeThread;

        public static void setLogListener(ILoggerN logListener)
        {
            LoggerN.logListener = logListener;
        }

        public static void logMessage(string s)
        {
            logMessage(s, true);
        }

        public static void logMessage(Exception e)
        {
            logMessage(e.StackTrace);
        }

        public static void logMessage(string message, Exception t)
        {
            logMessage(message + ":\n" + t.StackTrace);
        }

        public static void logError(string message, Exception t)
        {
            logError(message + ":\n" + t.StackTrace);
        }

        public static void logError(Exception t)
        {
            logError(t.StackTrace);
        }

        public static void logMessage(string s, bool newline)
        {
            if (logListener == null)
            {
                if (newline)
                {
                    realOut.WriteLine(s);
                }
                else
                {
                    realOut.Write(s);
                    realOut.Flush();
                }
            }
            else
            {
                lock (logBuffer)
                {
                    if (!IsSafeThread)
                    {
                        // we just queue it, to not let unsafe thread travel thru system
                        logBuffer.Append(s);
                        logBuffer.Append("\n");
                    }
                    else if (newline)
                    {
                        logMessage(logBuffer + s, true);
                        logBuffer.Length = 0;
                    }
                    else
                    {
                        logBuffer.Append(s);
                    }
                }
            }
        }

        public static void logError(string s)
        {
            if (logListener == null)
            {
                realErr.WriteLine(s);
            }
            else
            {
                logListener.logError(s);
            }
        }

        public static void WriteLineToRobotsConsole(string s)
        {
            if (robotOut != null)
            {
                robotOut.WriteLine(s);
            }
            else
            {
                logMessage(s);
            }
        }
    }

    /// <exclude/>
    public interface ILoggerN
    {
        void logMessage(string s, bool newline);
        void logError(string s);
    }
}

//happy