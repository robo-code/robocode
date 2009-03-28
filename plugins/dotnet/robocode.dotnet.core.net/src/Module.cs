using net.sf.robocode.serialization;
using robocode.dotnet.core.net.peer;

namespace robocode.dotnet.core.net
{
    public class Module
    {
        static Module()
        {
            RbSerializer.register(typeof (ExecCommands), RbSerializer.ExecCommands_TYPE);
            RbSerializer.register(typeof (BulletCommand), RbSerializer.BulletCommand_TYPE);
            RbSerializer.register(typeof (TeamMessage), RbSerializer.TeamMessage_TYPE);
            RbSerializer.register(typeof (DebugProperty), RbSerializer.DebugProperty_TYPE);
            RbSerializer.register(typeof (ExecResults), RbSerializer.ExecResults_TYPE);
            RbSerializer.register(typeof (BulletStatus), RbSerializer.BulletStatus_TYPE);
        }

        public static void Init()
        {
        }
    }
}