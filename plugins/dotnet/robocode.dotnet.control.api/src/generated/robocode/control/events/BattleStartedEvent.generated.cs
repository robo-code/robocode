//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:2.0.50727.4927
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace robocode.control.events {
    
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaClassAttribute()]
    internal partial class BattleStartedEvent : global::robocode.control.events.BattleEvent
    {
        
        internal new static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _getBattleRules0;
        
        internal static global::net.sf.jni4net.jni.MethodId _isReplay1;
        
        internal static global::net.sf.jni4net.jni.MethodId _getRobotsCount2;
        
        internal static global::net.sf.jni4net.jni.MethodId @__ctorBattleStartedEvent3;
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Lrobocode/BattleRules;IZ)V")]
        public BattleStartedEvent(global::java.lang.Object par0, int par1, bool par2) : 
                base(((global::net.sf.jni4net.jni.JNIEnv)(null))) {
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.ThreadEnv;
            @__env.NewObject(global::robocode.control.events.BattleStartedEvent.staticClass, global::robocode.control.events.BattleStartedEvent.@__ctorBattleStartedEvent3, this, global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par0), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par1), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par2));
        }
        
        protected BattleStartedEvent(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        public static global::java.lang.Class _class {
            get {
                return global::robocode.control.events.BattleStartedEvent.staticClass;
            }
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::robocode.control.events.BattleStartedEvent.staticClass = @__class;
            global::robocode.control.events.BattleStartedEvent._getBattleRules0 = @__env.GetMethodID(global::robocode.control.events.BattleStartedEvent.staticClass, "getBattleRules", "()Lrobocode/BattleRules;");
            global::robocode.control.events.BattleStartedEvent._isReplay1 = @__env.GetMethodID(global::robocode.control.events.BattleStartedEvent.staticClass, "isReplay", "()Z");
            global::robocode.control.events.BattleStartedEvent._getRobotsCount2 = @__env.GetMethodID(global::robocode.control.events.BattleStartedEvent.staticClass, "getRobotsCount", "()I");
            global::robocode.control.events.BattleStartedEvent.@__ctorBattleStartedEvent3 = @__env.GetMethodID(global::robocode.control.events.BattleStartedEvent.staticClass, "<init>", "(Lrobocode/BattleRules;IZ)V");
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Lrobocode/BattleRules;")]
        public virtual global::java.lang.Object getBattleRules() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.lang.Object>(@__env, @__env.CallObjectMethodPtr(this, global::robocode.control.events.BattleStartedEvent._getBattleRules0));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isReplay() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::robocode.control.events.BattleStartedEvent._isReplay1)));
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()I")]
        public virtual int getRobotsCount() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((int)(@__env.CallIntMethod(this, global::robocode.control.events.BattleStartedEvent._getRobotsCount2)));
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::robocode.control.events.BattleStartedEvent(@__env);
            }
        }
    }
    #endregion
}
