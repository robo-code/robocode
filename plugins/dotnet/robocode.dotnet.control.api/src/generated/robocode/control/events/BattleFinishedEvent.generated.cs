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
    public partial class BattleFinishedEvent : global::robocode.control.events.BattleEvent {
        
        internal new static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _isAborted0;
        
        internal static global::net.sf.jni4net.jni.MethodId @__ctorBattleFinishedEvent1;
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Z)V")]
        public BattleFinishedEvent(bool par0) : 
                base(((global::net.sf.jni4net.jni.JNIEnv)(null))) {
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.ThreadEnv;
            @__env.NewObject(global::robocode.control.events.BattleFinishedEvent.staticClass, global::robocode.control.events.BattleFinishedEvent.@__ctorBattleFinishedEvent1, this, global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par0));
        }
        
        protected BattleFinishedEvent(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        public static global::java.lang.Class _class {
            get {
                return global::robocode.control.events.BattleFinishedEvent.staticClass;
            }
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::robocode.control.events.BattleFinishedEvent.staticClass = @__class;
            global::robocode.control.events.BattleFinishedEvent._isAborted0 = @__env.GetMethodID(global::robocode.control.events.BattleFinishedEvent.staticClass, "isAborted", "()Z");
            global::robocode.control.events.BattleFinishedEvent.@__ctorBattleFinishedEvent1 = @__env.GetMethodID(global::robocode.control.events.BattleFinishedEvent.staticClass, "<init>", "(Z)V");
        }
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        public virtual bool isAborted() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::robocode.control.events.BattleFinishedEvent._isAborted0)));
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::robocode.control.events.BattleFinishedEvent(@__env);
            }
        }
    }
    #endregion
}
