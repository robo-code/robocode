//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace net.sf.robocode.dotnet.repository.root {
    
    
    #region Component Designer generated code 
    public partial class DllRootHelper_ {
        
        public static global::java.lang.Class _class {
            get {
                return global::net.sf.robocode.dotnet.repository.root.@__DllRootHelper.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::net.sf.robocode.dotnet.repository.root.DllRootHelper), typeof(global::net.sf.robocode.dotnet.repository.root.DllRootHelper_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::net.sf.robocode.dotnet.repository.root.DllRootHelper), typeof(global::net.sf.robocode.dotnet.repository.root.DllRootHelper_))]
    internal sealed partial class @__DllRootHelper : global::java.lang.Object {
        
        internal new static global::java.lang.Class staticClass;
        
        private @__DllRootHelper(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::net.sf.robocode.dotnet.repository.root.@__DllRootHelper.staticClass = @__class;
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__DllRootHelper);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "Refresh", "Refresh0", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "findItems", "findItems1", "(Ljava/lang/String;)[Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "GetRobotType", "GetRobotType2", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;)Lnet/sf/robocode/repository/Ro" +
                        "botType;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "GetDllFileName", "GetDllFileName3", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;)Ljava/lang/String;"));
            return methods;
        }
        
        private static void Refresh0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.dotnet.repository.root.DllRootHelper.Refresh();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static global::net.sf.jni4net.utils.JniHandle findItems1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle dllPath) {
            // (Ljava/lang/String;)[Ljava/lang/String;
            // (LSystem/String;)[LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            @__return = global::net.sf.jni4net.utils.Convertor.ArrayStrongC2JString(@__env, global::net.sf.robocode.dotnet.repository.root.DllRootHelper.findItems(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, dllPath)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle GetRobotType2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle robotRepositoryItem) {
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)Lnet/sf/robocode/repository/RobotType;
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)Lnet/sf/robocode/repository/RobotType;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            @__return = global::net.sf.jni4net.utils.Convertor.StrongCp2J(global::net.sf.robocode.dotnet.repository.root.DllRootHelper.GetRobotType(global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, robotRepositoryItem)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle GetDllFileName3(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle robotRepositoryItem) {
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)Ljava/lang/String;
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, global::net.sf.robocode.dotnet.repository.root.DllRootHelper.GetDllFileName(global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, robotRepositoryItem)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::net.sf.robocode.dotnet.repository.root.@__DllRootHelper(@__env);
            }
        }
    }
    #endregion
}
