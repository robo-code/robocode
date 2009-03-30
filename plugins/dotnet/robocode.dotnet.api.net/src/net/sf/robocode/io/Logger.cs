using System;
using System.IO;
using System.Text;
using net.sf.robocode.security;
using robocode.net.sf.robocode.io;

namespace net.sf.robocode.io
{
    public class Logger
    {
        private static readonly StringBuilder logBuffer = new StringBuilder();
        public static readonly TextWriter realErr = Console.Error;
        public static readonly TextWriter realOut = Console.Out;

        private static ILogger logListener;

        public static void logError(Exception t)
        {
            logError(toStackTraceString(t));
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

        public static void logError(string message, Exception t)
        {
            var builder = new StringBuilder();
            builder.Append(message);
            builder.Append(":\n");
            builder.Append(toStackTraceString(t));
            logError(builder.ToString());
        }

        public static void logMessage(Exception e)
        {
            logMessage(toStackTraceString(e));
        }

        public static void logMessage(string s)
        {
            logMessage(s, true);
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
                return;
            }
            lock (logBuffer)
            {
                if (!HiddenAccess.isSafeThread())
                {
                    logBuffer.Append(s);
                    logBuffer.Append("\n");
                }
                else if (newline)
                {
                    logBuffer.Append(s);
                    logListener.logMessage(logBuffer.ToString());
                    logBuffer.Length = 0;
                }
                else
                {
                    logBuffer.Append(s);
                }
            }
        }

        public static void logMessage(string message, Exception t)
        {
            var builder = new StringBuilder();
            builder.Append(message);
            builder.Append(":\n");
            builder.Append(toStackTraceString(t));
            logMessage(builder.ToString());
        }

        public static void printlnToRobotsConsole(string s)
        {
            //TODO System.@Out.println(s);
        }

        private static string toStackTraceString(Exception exception1)
        {
            return exception1.StackTrace;
        }
    }
}