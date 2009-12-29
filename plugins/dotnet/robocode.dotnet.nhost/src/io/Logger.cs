namespace net.sf.robocode.io
{
    partial class Logger : ILoggerN
    {
        public Logger(bool fakeInstance) 
        {
        }

        public void logMessage(string s, bool newline)
        {
            logMessage((java.lang.String) s, newline);
        }

        public void logError(string s)
        {
            logError((java.lang.String)s);
        }
    }
}
