//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:2.0.50727.4927
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace net.sf.robocode.peer {
    
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaInterfaceAttribute()]
    public partial interface IRobotStatics {
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        bool isInteractiveRobot();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        bool isPaintRobot();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        bool isAdvancedRobot();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Z")]
        bool isTeamRobot();
    }
    #endregion
    
    #region Component Designer generated code 
    public partial class IRobotStatics_ {
        
        public new static global::java.lang.Class _class {
            get {
                return global::net.sf.robocode.peer.@__IRobotStatics.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::net.sf.robocode.peer.IRobotStatics))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::net.sf.robocode.peer.IRobotStatics))]
    internal sealed partial class @__IRobotStatics : global::java.lang.Object, global::net.sf.robocode.peer.IRobotStatics {
        
        internal static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _isInteractiveRobot0;
        
        internal static global::net.sf.jni4net.jni.MethodId _isPaintRobot1;
        
        internal static global::net.sf.jni4net.jni.MethodId _isAdvancedRobot2;
        
        internal static global::net.sf.jni4net.jni.MethodId _isTeamRobot3;
        
        protected @__IRobotStatics(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::net.sf.robocode.peer.@__IRobotStatics.staticClass = @__class;
            global::net.sf.robocode.peer.@__IRobotStatics._isInteractiveRobot0 = @__env.GetMethodID(global::net.sf.robocode.peer.@__IRobotStatics.staticClass, "isInteractiveRobot", "()Z");
            global::net.sf.robocode.peer.@__IRobotStatics._isPaintRobot1 = @__env.GetMethodID(global::net.sf.robocode.peer.@__IRobotStatics.staticClass, "isPaintRobot", "()Z");
            global::net.sf.robocode.peer.@__IRobotStatics._isAdvancedRobot2 = @__env.GetMethodID(global::net.sf.robocode.peer.@__IRobotStatics.staticClass, "isAdvancedRobot", "()Z");
            global::net.sf.robocode.peer.@__IRobotStatics._isTeamRobot3 = @__env.GetMethodID(global::net.sf.robocode.peer.@__IRobotStatics.staticClass, "isTeamRobot", "()Z");
        }
        
        public bool isInteractiveRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.peer.@__IRobotStatics._isInteractiveRobot0)));
        }
        
        public bool isPaintRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.peer.@__IRobotStatics._isPaintRobot1)));
        }
        
        public bool isAdvancedRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.peer.@__IRobotStatics._isAdvancedRobot2)));
        }
        
        public bool isTeamRobot() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((bool)(@__env.CallBooleanMethod(this, global::net.sf.robocode.peer.@__IRobotStatics._isTeamRobot3)));
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__IRobotStatics);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "isInteractiveRobot", "isInteractiveRobot0", "()Z"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "isPaintRobot", "isPaintRobot1", "()Z"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "isAdvancedRobot", "isAdvancedRobot2", "()Z"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "isTeamRobot", "isTeamRobot3", "()Z"));
            return methods;
        }
        
        private static bool isInteractiveRobot0(global::System.IntPtr @__envp, global::System.IntPtr @__obj) {
            // ()Z
            // ()Z
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.peer.IRobotStatics @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.peer.IRobotStatics>(@__env, @__obj);
            return ((bool)(@__real.isInteractiveRobot()));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return default(bool);
        }
        
        private static bool isPaintRobot1(global::System.IntPtr @__envp, global::System.IntPtr @__obj) {
            // ()Z
            // ()Z
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.peer.IRobotStatics @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.peer.IRobotStatics>(@__env, @__obj);
            return ((bool)(@__real.isPaintRobot()));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return default(bool);
        }
        
        private static bool isAdvancedRobot2(global::System.IntPtr @__envp, global::System.IntPtr @__obj) {
            // ()Z
            // ()Z
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.peer.IRobotStatics @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.peer.IRobotStatics>(@__env, @__obj);
            return ((bool)(@__real.isAdvancedRobot()));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return default(bool);
        }
        
        private static bool isTeamRobot3(global::System.IntPtr @__envp, global::System.IntPtr @__obj) {
            // ()Z
            // ()Z
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.peer.IRobotStatics @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.peer.IRobotStatics>(@__env, @__obj);
            return ((bool)(@__real.isTeamRobot()));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return default(bool);
        }
        
        internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::net.sf.robocode.peer.@__IRobotStatics(@__env);
            }
        }
    }
    #endregion
}