// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package net.sf.robocode.repository;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class IRobotRepositoryItem_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return net.sf.robocode.repository.IRobotRepositoryItem_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        net.sf.robocode.repository.IRobotRepositoryItem_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __IRobotRepositoryItem extends system.Object implements net.sf.robocode.repository.IRobotRepositoryItem {
    
    protected __IRobotRepositoryItem(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isInteractiveRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isPaintRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isAdvancedRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isTeamRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isJuniorRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isStandardRobot();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isDroid();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/net/URL;")
    public native java.net.URL getRobotClassPath();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getWritableDirectory();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getReadableDirectory();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getRobotLanguage();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isValid();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()J")
    public native long getLastModified();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getDescription();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getRobocodeVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/net/URL;")
    public native java.net.URL getWebpage();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getAuthorName();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isTeam();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isInJar();
    
    @net.sf.jni4net.attributes.ClrMethod("(Z)V")
    public native void setValid(boolean par0);
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean getJavaSourceIncluded();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getRootFile();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/net/URL;")
    public native java.net.URL getFullUrl();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/net/URL;")
    public native java.net.URL getPropertiesUrl();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isDevelopmentVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getFullPackage();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getRelativePath();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getRootPackage();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getFullClassNameWithVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getUniqueFullClassNameWithVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getUniqueShortClassNameWithVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getUniqueVeryShortClassNameWithVersion();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getFullClassName();
    
    @net.sf.jni4net.attributes.ClrMethod("()Ljava/lang/String;")
    public native java.lang.String getShortClassName();
    
    @net.sf.jni4net.attributes.ClrMethod("()Lrobocode/control/RobotSpecification;")
    public native robocode.control.RobotSpecification createRobotSpecification();
    
    @net.sf.jni4net.attributes.ClrMethod("(Ljava/io/OutputStream;Ljava/net/URL;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V")
    public native void storeProperties(java.io.OutputStream par0, java.net.URL par1, java.lang.String par2, java.lang.String par3, java.lang.String par4, boolean par5);
    
    @net.sf.jni4net.attributes.ClrMethod("(Ljava/io/OutputStream;)V")
    public native void storeProperties(java.io.OutputStream par0);
    
    @net.sf.jni4net.attributes.ClrMethod("(Ljava/lang/Object;)I")
    public native int compareTo(java.lang.Object par0);
}
//</generated-proxy>
