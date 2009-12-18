/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Initial implementation based on methods from robocode.util.Utils, which
 *       has been moved to this class
 *******************************************************************************/
using System;
using System.IO;
using System.Text;
using net.sf.robocode.security;

namespace net.sf.robocode.io
{
    /// <summary>
    /// This is a class used for logging.
    ///
    /// @author Flemming N. Larsen (original)
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public class LoggerN
    {
        public static TextWriter realOut = Console.Out;
        public static TextWriter realErr = Console.Error;

        private static object logListener;
        private static readonly StringBuilder logBuffer = new StringBuilder();

        public static void setLogListener(object logListener)
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
                    if (!HiddenAccessN.isSafeThread())
                    {
                        // we just queue it, to not let unsafe thread travel thru system
                        logBuffer.Append(s);
                        logBuffer.Append("\n");
                    }
                    else if (newline)
                    {
                        throw new NotImplementedException();
                        //logListener.onBattleMessage(new BattleMessageEvent(logBuffer + s));
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
                throw new NotImplementedException();
                //logListener.onBattleError(new BattleErrorEvent(s));
            }
        }

        public static void printlnToRobotsConsole(string s)
        {
            // this will get redirected to robot's console
            Console.Out.WriteLine(s);
        }
    }
}
//happy