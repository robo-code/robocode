//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:2.0.50727.4016
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace net.sf.robocode.host {
    
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaClassAttribute()]
    public partial class RobotStatics : global::java.lang.Object, global::net.sf.robocode.peer.IRobotStatics {
        
        internal static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _isJuniorRobot0;
        
        internal static global::net.sf.jni4net.jni.MethodId _isInteractiveRobot1;
        
        internal static global::net.sf.jni4net.jni.MethodId _isPaintRobot2;
        
        internal static global::net.sf.jni4net.jni.MethodId _isAdvancedRobot3;
        
        internal static global::net.sf.jni4net.jni.MethodId _isTeamRobot4;
        
        internal static global::net.sf.jni4net.jni.MethodId _isTeamLeader5;
        
        internal static global::net.sf.jni4net.jni.MethodId _isDroid6;
        
        internal static global::net.sf.jni4net.jni.MethodId _getShortName7;
        
        internal static global::net.sf.jni4net.jni.MethodId _getVeryShortName8;
        
        internal static global::net.sf.jni4net.jni.MethodId _getFullClassName9;
        
        internal static global::net.sf.jni4net.jni.MethodId _getShortClassName10;
        
        internal static global::net.sf.jni4net.jni.MethodId _getBattleRules11;
        
        internal static global::net.sf.jni4net.jni.MethodId _getTeammates12;
        
        internal static global::net.sf.jni4net.jni.MethodId _getTeamName13;
        
        internal static global::net.sf.jni4net.jni.MethodId _getContestIndex14;
        
        internal static global::net.sf.jni4net.jni.MethodId _getName15;
        
        internal static global::net.sf.jni4net.jni.MethodId _getIndex16;
        
        internal static global::net.sf.jni4net.jni.MethodId _hashCode17;
        
        internal static global::net.sf.jni4net.jni.MethodId _getClass18;
        
        internal static global::net.sf.jni4net.jni.MethodId _wait19;
        
        internal static global::net.sf.jni4net.jni.MethodId _wait20;
        
        internal static global::net.sf.jni4net.jni.MethodId _wait21;
        
        internal static global::net.sf.jni4net.jni.MethodId _equals22;
        
        internal static global::net.sf.jni4net.jni.MethodId _notify23;
        
        internal static global::net.sf.jni4net.jni.MethodId _notifyAll24;
        
        internal static global::net.sf.jni4net.jni.MethodId _toString25;
        
        internal static global::net.sf.jni4net.jni.MethodId @__ctor26;
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Lrobocode/control/RobotSpecification;IZLrobocode/BattleRules;Ljava/lang/String;L" +
            "java/util/List;II)V")]
        public RobotStatics(global::robocode.control.RobotSpecification par0, int par1, bool par2, global::java.lang.Object par3, global::java.lang.String par4, global::java.util.List par5, int par6, int par7) : 
                base(((global::net.sf.jni4net.jni.JNIEnv)(null))) {
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.ThreadEnv;
            @__env.NewObject(global::net.sf.robocode.host.RobotStatics.staticClass, global::net.sf.robocode.host.RobotStatics.@__ctor26, this, global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par0), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par1), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par2), global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par3), global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par4), global::net.sf.jni4net.utils.Convertor.ParFullC2J<global::java.util.List>(@__env, par5), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par6), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par7));
        }
        
        protected RobotStatics(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        public new static global::java.lang.Class _class {
            get {
                return global::net.sf.robocode.host.RobotStatics.staticClass;
            }
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::net.sf.robocode.host.RobotStatics.staticClass = @__class;
            global::net.sf.robocode.host.RobotStatics._isJuniorRobot0 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isJuniorRobot", "()Z");
            global::net.sf.robocode.host.RobotStatics._isInteractiveRobot1 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isInteractiveRobot", "()Z");
            global::net.sf.robocode.host.RobotStatics._isPaintRobot2 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isPaintRobot", "()Z");
            global::net.sf.robocode.host.RobotStatics._isAdvancedRobot3 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isAdvancedRobot", "()Z");
            global::net.sf.robocode.host.RobotStatics._isTeamRobot4 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isTeamRobot", "()Z");
            global::net.sf.robocode.host.RobotStatics._isTeamLeader5 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isTeamLeader", "()Z");
            global::net.sf.robocode.host.RobotStatics._isDroid6 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "isDroid", "()Z");
            global::net.sf.robocode.host.RobotStatics._getShortName7 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getShortName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getVeryShortName8 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getVeryShortName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getFullClassName9 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getFullClassName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getShortClassName10 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getShortClassName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getBattleRules11 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getBattleRules", "()Lrobocode/BattleRules;");
            global::net.sf.robocode.host.RobotStatics._getTeammates12 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getTeammates", "()[Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getTeamName13 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getTeamName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getContestIndex14 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getContestIndex", "()I");
            global::net.sf.robocode.host.RobotStatics._getName15 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getName", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics._getIndex16 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getIndex", "()I");
            global::net.sf.robocode.host.RobotStatics._hashCode17 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "hashCode", "()I");
            global::net.sf.robocode.host.RobotStatics._getClass18 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "getClass", "()Ljava/lang/Class;");
            global::net.sf.robocode.host.RobotStatics._wait19 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "wait", "()V");
            global::net.sf.robocode.host.RobotStatics._wait20 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "wait", "(JI)V");
            global::net.sf.robocode.host.RobotStatics._wait21 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "wait", "(J)V");
            global::net.sf.robocode.host.RobotStatics._equals22 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "equals", "(Ljava/lang/Object;)Z");
            global::net.sf.robocode.host.RobotStatics._notify23 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "notify", "()V");
            global::net.sf.robocode.host.RobotStatics._notifyAll24 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "notifyAll", "()V");
            global::net.sf.robocode.host.RobotStatics._toString25 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "toString", "()Ljava/lang/String;");
            global::net.sf.robocode.host.RobotStatics.@__ctor26 = @__env.GetMethodID(global::net.sf.robocode.host.RobotStatics.staticClass, "<init>", "(Lrobocode/control/RobotSpecification;IZLrobocode/BattleRules;Ljava/lang/String;L" +
                    "java/util/List;II)V");
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isJuniorRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isJuniorRobot0)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isInteractiveRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isInteractiveRobot1)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isPaintRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isPaintRobot2)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isAdvancedRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isAdvancedRobot3)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isTeamRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isTeamRobot4)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isTeamLeader() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isTeamLeader5)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isDroid() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._isDroid6)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getShortName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getShortName7));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getVeryShortName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getVeryShortName8));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getFullClassName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getFullClassName9));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getShortClassName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getShortClassName10));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Lrobocode/BattleRules;")]
        public virtual global::java.lang.Object getBattleRules() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.lang.Object>(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getBattleRules11));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()[Ljava/lang/String;")]
        public virtual java.lang.String[] getTeammates() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.ArrayStrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getTeammates12));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getTeamName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getTeamName13));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()I")]
        public virtual int getContestIndex() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((int)(@__env.CallIntMethod(this, global::net.sf.robocode.host.RobotStatics._getContestIndex14)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String getName() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getName15));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()I")]
        public virtual int getIndex() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((int)(@__env.CallIntMethod(this, global::net.sf.robocode.host.RobotStatics._getIndex16)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()I")]
        public virtual int hashCode() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((int)(@__env.CallIntMethod(this, global::net.sf.robocode.host.RobotStatics._hashCode17)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/Class;")]
        public global::java.lang.Class getClass() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpClass(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._getClass18));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        public void wait() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.RobotStatics._wait19);
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(JI)V")]
        public void wait(long par0, int par1) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.RobotStatics._wait20, global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par0), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par1));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(J)V")]
        public void wait(long par0) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.RobotStatics._wait21, global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par0));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Ljava/lang/Object;)Z")]
        public virtual bool equals(global::java.lang.Object par0) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.host.RobotStatics._equals22, global::net.sf.jni4net.utils.Convertor.ParFullC2J<global::java.lang.Object>(@__env, par0))));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        public void notify() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.RobotStatics._notify23);
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        public void notifyAll() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.RobotStatics._notifyAll24);
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/lang/String;")]
        public virtual global::java.lang.String toString() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.RobotStatics._toString25));
        }
        
        internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::net.sf.robocode.host.RobotStatics(@__env);
            }
        }
    }
    #endregion
}
