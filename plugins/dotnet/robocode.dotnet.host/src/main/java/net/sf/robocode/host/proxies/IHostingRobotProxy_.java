// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package net.sf.robocode.host.proxies;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class IHostingRobotProxy_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return net.sf.robocode.host.proxies.IHostingRobotProxy_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        net.sf.robocode.host.proxies.IHostingRobotProxy_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __IHostingRobotProxy extends system.Object implements net.sf.robocode.host.proxies.IHostingRobotProxy {
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;LSystem/Object;)V")
    public native void startRound(net.sf.robocode.peer.ExecCommands par0, robocode.RobotStatus par1);
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void forceStopThread();
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void waitForStopThread();
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void cleanup();
}
//</generated-proxy>
