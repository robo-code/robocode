using System.Runtime.CompilerServices;
using System.Threading;
using IKVM.Attributes;
using java.io;
using java.lang;
using net.sf.robocode.security;
using robocode.control.events;
using Exception=System.Exception;

namespace net.sf.robocode.io
{
    public class Logger : Object
    {
        [Modifiers(Modifiers.Static | Modifiers.Private | Modifiers.Final)] private static StringBuffer logBuffer;
        private static IBattleListener logListener;
        [Modifiers(Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static PrintStream realErr;
        [Modifiers(Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static PrintStream realOut;

        [LineNumberTable(new byte[] {0x9f, 0x86, 0x85, 0x6a, 170})]
        static Logger()
        {
            logBuffer = new StringBuffer();
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        public static void __<clinit>()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {11, 0x6b})]
        public static void logError(Exception t)
        {
            logError(toStackTraceString(t));
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x27, 0x6a, 0x90, 0x90})]
        public static void logError(string s)
        {
            if (logListener == null)
            {
                realErr.println(s);
            }
            else
            {
                logListener.onBattleError(new BattleErrorEvent(s));
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {7, 0x7f, 11})]
        public static void logError(string message, Exception t)
        {
            logError(new StringBuilder().append(message).append(":\n").append(toStackTraceString(t)).toString());
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbf, 0x6b})]
        public static void logMessage(Exception e)
        {
            logMessage(toStackTraceString(e));
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbb, 0x67})]
        public static void logMessage(string s)
        {
            logMessage(s, true);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                       {
                                                                           0x9f, 0x7e, 0x63, 0x6a, 0x66, 0x90, 0x6b,
                                                                           0xaf, 0x6c, 0x8a, 0x6c, 0x75, 0x66, 0x7f, 10,
                                                                           0x90,
                                                                           140, 0x9f, 14
                                                                       })]
        public static void logMessage(string s, bool newline)
        {
            StringBuffer buffer;
            Exception exception;
            if (logListener == null)
            {
                if (newline)
                {
                    realOut.println(s);
                }
                else
                {
                    realOut.print(s);
                    realOut.flush();
                }
                return;
            }
            Monitor.Enter(buffer = logBuffer);
            try
            {
                if (!HiddenAccess.isSafeThread())
                {
                    logBuffer.append(s);
                    logBuffer.append("\n");
                }
                else if (newline)
                {
                    logListener.onBattleMessage(
                        new BattleMessageEvent(new StringBuilder().append(logBuffer).append(s).toString()));
                    logBuffer.setLength(0);
                }
                else
                {
                    logBuffer.append(s);
                }
                Monitor.Exit(buffer);
                return;
            }
            finally
            {
                Monitor.Exit(buffer);
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {3, 0x7f, 11})]
        public static void logMessage(string message, Exception t)
        {
            logMessage(new StringBuilder().append(message).append(":\n").append(toStackTraceString(t)).toString());
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {60, 0x6b})]
        public static void printlnToRobotsConsole(string s)
        {
            //TODO System.@out.println(s);
        }

        public static void setLogListener(IBattleListener logListener)
        {
            Logger.logListener = logListener;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x2f, 0x66, 0x86, 0x66, 0x87, 0x67, 0x66})]
        private static string toStackTraceString(Exception exception1)
        {
            if (exception1 == null)
            {
                return "";
            }
            var @out = new ByteArrayOutputStream();
            var s = new PrintStream(@out);
            Throwable.instancehelper_printStackTrace(exception1, s);
            s.close();
            return @out.toString();
        }
    }
}