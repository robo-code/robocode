//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:2.0.50727.4927
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace robocode.control {
    
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaClassAttribute()]
    internal partial class BattleSpecification : global::java.lang.Object, global::java.io.Serializable
    {
        
        internal new static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _getInactivityTime0;
        
        internal static global::net.sf.jni4net.jni.MethodId _getGunCoolingRate1;
        
        internal static global::net.sf.jni4net.jni.MethodId _getBattlefield2;
        
        internal static global::net.sf.jni4net.jni.MethodId _getNumRounds3;
        
        internal static global::net.sf.jni4net.jni.MethodId _getRobots4;
        
        internal static global::net.sf.jni4net.jni.MethodId @__ctorBattleSpecification5;
        
        internal static global::net.sf.jni4net.jni.MethodId @__ctorBattleSpecification6;
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(IJDLrobocode/control/BattlefieldSpecification;[Lrobocode/control/RobotSpecificat" +
            "ion;)V")]
        public BattleSpecification(int par0, long par1, double par2, global::robocode.control.BattlefieldSpecification par3, robocode.control.RobotSpecification[] par4) : 
                base(((global::net.sf.jni4net.jni.JNIEnv)(null))) {
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.ThreadEnv;
            @__env.NewObject(global::robocode.control.BattleSpecification.staticClass, global::robocode.control.BattleSpecification.@__ctorBattleSpecification5, this, global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par0), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par1), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par2), global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par3), global::net.sf.jni4net.utils.Convertor.ParArrayStrongCp2J(@__env, par4));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(ILrobocode/control/BattlefieldSpecification;[Lrobocode/control/RobotSpecificatio" +
            "n;)V")]
        public BattleSpecification(int par0, global::robocode.control.BattlefieldSpecification par1, robocode.control.RobotSpecification[] par2) : 
                base(((global::net.sf.jni4net.jni.JNIEnv)(null))) {
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.ThreadEnv;
            @__env.NewObject(global::robocode.control.BattleSpecification.staticClass, global::robocode.control.BattleSpecification.@__ctorBattleSpecification6, this, global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par0), global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par1), global::net.sf.jni4net.utils.Convertor.ParArrayStrongCp2J(@__env, par2));
        }
        
        protected BattleSpecification(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        public static global::java.lang.Class _class {
            get {
                return global::robocode.control.BattleSpecification.staticClass;
            }
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::robocode.control.BattleSpecification.staticClass = @__class;
            global::robocode.control.BattleSpecification._getInactivityTime0 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "getInactivityTime", "()J");
            global::robocode.control.BattleSpecification._getGunCoolingRate1 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "getGunCoolingRate", "()D");
            global::robocode.control.BattleSpecification._getBattlefield2 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "getBattlefield", "()Lrobocode/control/BattlefieldSpecification;");
            global::robocode.control.BattleSpecification._getNumRounds3 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "getNumRounds", "()I");
            global::robocode.control.BattleSpecification._getRobots4 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "getRobots", "()[Lrobocode/control/RobotSpecification;");
            global::robocode.control.BattleSpecification.@__ctorBattleSpecification5 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "<init>", "(IJDLrobocode/control/BattlefieldSpecification;[Lrobocode/control/RobotSpecificat" +
                    "ion;)V");
            global::robocode.control.BattleSpecification.@__ctorBattleSpecification6 = @__env.GetMethodID(global::robocode.control.BattleSpecification.staticClass, "<init>", "(ILrobocode/control/BattlefieldSpecification;[Lrobocode/control/RobotSpecificatio" +
                    "n;)V");
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()J")]
        public virtual long getInactivityTime() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((long)(@__env.CallLongMethod(this, global::robocode.control.BattleSpecification._getInactivityTime0)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()D")]
        public virtual double getGunCoolingRate() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((double)(@__env.CallDoubleMethod(this, global::robocode.control.BattleSpecification._getGunCoolingRate1)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Lrobocode/control/BattlefieldSpecification;")]
        public virtual global::robocode.control.BattlefieldSpecification getBattlefield() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::robocode.control.BattlefieldSpecification>(@__env, @__env.CallObjectMethodPtr(this, global::robocode.control.BattleSpecification._getBattlefield2));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()I")]
        public virtual int getNumRounds() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((int)(@__env.CallIntMethod(this, global::robocode.control.BattleSpecification._getNumRounds3)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()[Lrobocode/control/RobotSpecification;")]
        public virtual robocode.control.RobotSpecification[] getRobots() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.ArrayStrongJ2Cp<robocode.control.RobotSpecification[], global::robocode.control.RobotSpecification>(@__env, @__env.CallObjectMethodPtr(this, global::robocode.control.BattleSpecification._getRobots4));
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::robocode.control.BattleSpecification(@__env);
            }
        }
    }
    #endregion
}
