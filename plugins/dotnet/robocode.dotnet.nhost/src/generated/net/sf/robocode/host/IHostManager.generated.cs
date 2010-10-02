//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:2.0.50727.4927
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace net.sf.robocode.host {
    
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaInterfaceAttribute()]
    internal partial interface IHostManager {
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        void cleanup();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        void initSecurity();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()J")]
        long getRobotFilesystemQuota();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()V")]
        void resetThreadManager();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Ljava/lang/Thread;)V")]
        void addSafeThread(global::java.lang.Object par0);
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Ljava/lang/Thread;)V")]
        void removeSafeThread(global::java.lang.Object par0);
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("()Ljava/io/PrintStream;")]
        global::java.io.PrintStream getRobotOutputStream();
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Lrobocode/control/RobotSpecification;Lnet/sf/robocode/host/RobotStatics;Lnet/sf/" +
            "robocode/peer/IRobotPeer;)Ljava/lang/Object;")]
        global::java.lang.Object createRobotProxy(global::robocode.control.RobotSpecification par0, global::java.lang.Object par1, global::net.sf.robocode.peer.IRobotPeer par2);
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Lnet/sf/robocode/repository/IRobotRepositoryItem;)[Ljava/lang/String;")]
        java.lang.String[] getReferencedClasses(global::net.sf.robocode.repository.IRobotRepositoryItem par0);
        
        [global::net.sf.jni4net.attributes.JavaMethodAttribute("(Lnet/sf/robocode/repository/IRobotRepositoryItem;ZZ)Lnet/sf/robocode/repository/" +
            "RobotType;")]
        global::net.sf.robocode.repository.RobotType getRobotType(global::net.sf.robocode.repository.IRobotRepositoryItem par0, bool par1, bool par2);
    }
    #endregion
    
    #region Component Designer generated code 
    internal partial class IHostManager_
    {
        
        public static global::java.lang.Class _class {
            get {
                return global::net.sf.robocode.host.@__IHostManager.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::net.sf.robocode.host.IHostManager), typeof(global::net.sf.robocode.host.IHostManager_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::net.sf.robocode.host.IHostManager), typeof(global::net.sf.robocode.host.IHostManager_))]
    internal sealed partial class @__IHostManager : global::java.lang.Object, global::net.sf.robocode.host.IHostManager {
        
        internal new static global::java.lang.Class staticClass;
        
        internal static global::net.sf.jni4net.jni.MethodId _cleanup0;
        
        internal static global::net.sf.jni4net.jni.MethodId _initSecurity1;
        
        internal static global::net.sf.jni4net.jni.MethodId _getRobotFilesystemQuota2;
        
        internal static global::net.sf.jni4net.jni.MethodId _resetThreadManager3;
        
        internal static global::net.sf.jni4net.jni.MethodId _addSafeThread4;
        
        internal static global::net.sf.jni4net.jni.MethodId _removeSafeThread5;
        
        internal static global::net.sf.jni4net.jni.MethodId _getRobotOutputStream6;
        
        internal static global::net.sf.jni4net.jni.MethodId _createRobotProxy7;
        
        internal static global::net.sf.jni4net.jni.MethodId _getReferencedClasses8;
        
        internal static global::net.sf.jni4net.jni.MethodId _getRobotType9;
        
        private @__IHostManager(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::net.sf.robocode.host.@__IHostManager.staticClass = @__class;
            global::net.sf.robocode.host.@__IHostManager._cleanup0 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "cleanup", "()V");
            global::net.sf.robocode.host.@__IHostManager._initSecurity1 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "initSecurity", "()V");
            global::net.sf.robocode.host.@__IHostManager._getRobotFilesystemQuota2 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "getRobotFilesystemQuota", "()J");
            global::net.sf.robocode.host.@__IHostManager._resetThreadManager3 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "resetThreadManager", "()V");
            global::net.sf.robocode.host.@__IHostManager._addSafeThread4 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "addSafeThread", "(Ljava/lang/Thread;)V");
            global::net.sf.robocode.host.@__IHostManager._removeSafeThread5 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "removeSafeThread", "(Ljava/lang/Thread;)V");
            global::net.sf.robocode.host.@__IHostManager._getRobotOutputStream6 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "getRobotOutputStream", "()Ljava/io/PrintStream;");
            global::net.sf.robocode.host.@__IHostManager._createRobotProxy7 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "createRobotProxy", "(Lrobocode/control/RobotSpecification;Lnet/sf/robocode/host/RobotStatics;Lnet/sf/" +
                    "robocode/peer/IRobotPeer;)Ljava/lang/Object;");
            global::net.sf.robocode.host.@__IHostManager._getReferencedClasses8 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "getReferencedClasses", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;)[Ljava/lang/String;");
            global::net.sf.robocode.host.@__IHostManager._getRobotType9 = @__env.GetMethodID(global::net.sf.robocode.host.@__IHostManager.staticClass, "getRobotType", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;ZZ)Lnet/sf/robocode/repository/" +
                    "RobotType;");
        }
        
        public void cleanup() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.@__IHostManager._cleanup0);
        }
        
        public void initSecurity() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.@__IHostManager._initSecurity1);
        }
        
        public long getRobotFilesystemQuota() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return ((long)(@__env.CallLongMethod(this, global::net.sf.robocode.host.@__IHostManager._getRobotFilesystemQuota2)));
        }
        
        public void resetThreadManager() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.@__IHostManager._resetThreadManager3);
        }
        
        public void addSafeThread(global::java.lang.Object par0) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.@__IHostManager._addSafeThread4, global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par0));
        }
        
        public void removeSafeThread(global::java.lang.Object par0) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            @__env.CallVoidMethod(this, global::net.sf.robocode.host.@__IHostManager._removeSafeThread5, global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par0));
        }
        
        public global::java.io.PrintStream getRobotOutputStream() {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.io.PrintStream>(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.@__IHostManager._getRobotOutputStream6));
        }
        
        public global::java.lang.Object createRobotProxy(global::robocode.control.RobotSpecification par0, global::java.lang.Object par1, global::net.sf.robocode.peer.IRobotPeer par2) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.FullJ2C<global::java.lang.Object>(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.@__IHostManager._createRobotProxy7, global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par0), global::net.sf.jni4net.utils.Convertor.ParStrongCp2J(par1), global::net.sf.jni4net.utils.Convertor.ParFullC2J<global::net.sf.robocode.peer.IRobotPeer>(@__env, par2)));
        }
        
        public java.lang.String[] getReferencedClasses(global::net.sf.robocode.repository.IRobotRepositoryItem par0) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.ArrayStrongJ2CpString(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.@__IHostManager._getReferencedClasses8, global::net.sf.jni4net.utils.Convertor.ParFullC2J<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, par0)));
        }
        
        public global::net.sf.robocode.repository.RobotType getRobotType(global::net.sf.robocode.repository.IRobotRepositoryItem par0, bool par1, bool par2) {
            global::net.sf.jni4net.jni.JNIEnv @__env = this.Env;
            return global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::net.sf.robocode.repository.RobotType>(@__env, @__env.CallObjectMethodPtr(this, global::net.sf.robocode.host.@__IHostManager._getRobotType9, global::net.sf.jni4net.utils.Convertor.ParFullC2J<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, par0), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par1), global::net.sf.jni4net.utils.Convertor.ParPrimC2J(par2)));
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__IHostManager);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "cleanup", "cleanup0", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "initSecurity", "initSecurity1", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getRobotFilesystemQuota", "getRobotFilesystemQuota2", "()J"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "resetThreadManager", "resetThreadManager3", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "addSafeThread", "addSafeThread4", "(Ljava/lang/Thread;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "removeSafeThread", "removeSafeThread5", "(Ljava/lang/Thread;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getRobotOutputStream", "getRobotOutputStream6", "()Ljava/io/PrintStream;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "createRobotProxy", "createRobotProxy7", "(Lrobocode/control/RobotSpecification;Lnet/sf/robocode/host/RobotStatics;Lnet/sf/" +
                        "robocode/peer/IRobotPeer;)Ljava/lang/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getReferencedClasses", "getReferencedClasses8", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;)[Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getRobotType", "getRobotType9", "(Lnet/sf/robocode/repository/IRobotRepositoryItem;ZZ)Lnet/sf/robocode/repository/" +
                        "RobotType;"));
            return methods;
        }
        
        private static void cleanup0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__real.cleanup();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void initSecurity1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__real.initSecurity();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static long getRobotFilesystemQuota2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()J
            // ()J
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            long @__return = default(long);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__return = ((long)(@__real.getRobotFilesystemQuota()));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static void resetThreadManager3(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__real.resetThreadManager();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void addSafeThread4(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle par0) {
            // (Ljava/lang/Thread;)V
            // (Ljava/lang/Object;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__real.addSafeThread(global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.lang.Object>(@__env, par0));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void removeSafeThread5(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle par0) {
            // (Ljava/lang/Thread;)V
            // (Ljava/lang/Object;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__real.removeSafeThread(global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.lang.Object>(@__env, par0));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static global::net.sf.jni4net.utils.JniHandle getRobotOutputStream6(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Ljava/io/PrintStream;
            // ()Ljava/io/PrintStream;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongCp2J(@__real.getRobotOutputStream());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle createRobotProxy7(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle par0, global::net.sf.jni4net.utils.JniLocalHandle par1, global::net.sf.jni4net.utils.JniLocalHandle par2) {
            // (Lrobocode/control/RobotSpecification;Lnet/sf/robocode/host/RobotStatics;Lnet/sf/robocode/peer/IRobotPeer;)Ljava/lang/Object;
            // (Lrobocode/control/RobotSpecification;Ljava/lang/Object;Lnet/sf/robocode/peer/IRobotPeer;)Ljava/lang/Object;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.FullC2J<global::java.lang.Object>(@__env, @__real.createRobotProxy(global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::robocode.control.RobotSpecification>(@__env, par0), global::net.sf.jni4net.utils.Convertor.StrongJ2Cp<global::java.lang.Object>(@__env, par1), global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.peer.IRobotPeer>(@__env, par2)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle getReferencedClasses8(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle par0) {
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)[Ljava/lang/String;
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;)[Ljava/lang/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.ArrayStrongCp2J(@__env, @__real.getReferencedClasses(global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, par0)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle getRobotType9(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle par0, bool par1, bool par2) {
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;ZZ)Lnet/sf/robocode/repository/RobotType;
            // (Lnet/sf/robocode/repository/IRobotRepositoryItem;ZZ)Lnet/sf/robocode/repository/RobotType;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::net.sf.robocode.host.IHostManager @__real = global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.host.IHostManager>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongCp2J(@__real.getRobotType(global::net.sf.jni4net.utils.Convertor.FullJ2C<global::net.sf.robocode.repository.IRobotRepositoryItem>(@__env, par0), par1, par2));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::net.sf.robocode.host.@__IHostManager(@__env);
            }
        }
    }
    #endregion
}
