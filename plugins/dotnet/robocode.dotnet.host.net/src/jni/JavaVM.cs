using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public unsafe class JavaVM
    {
        private readonly Native* native;
        private Native.AttachCurrentThread attachCurrentThread;
        private Native.AttachCurrentThreadAsDaemon attachCurrentThreadAsDaemon;
        private Native.DestroyJavaVM destroyJavaVM;
        private Native.DetachCurrentThread detachCurrentThread;
        private JNIInvokeInterface functions;
        private Native.GetEnv getEnv;


        internal JavaVM(Native* native)
        {
            this.native = native;
            functions = *(*native).functions;
        }

        public JNIResult AttachCurrentThread(out JNIEnv penv, JavaVMInitArgs* args)
        {
            if (attachCurrentThread == null)
            {
                Util.GetDelegateForFunctionPointer(functions.AttachCurrentThread, ref attachCurrentThread);
            }
            JNIEnv.Native* env;
            JNIResult result = attachCurrentThread.Invoke(native, out env, args);
            penv = new JNIEnv(env);
            return result;
        }

        public JNIResult AttachCurrentThreadAsDaemon(out JNIEnv penv, JavaVMInitArgs* args)
        {
            if (attachCurrentThreadAsDaemon == null)
            {
                Util.GetDelegateForFunctionPointer(functions.AttachCurrentThreadAsDaemon,
                                                   ref attachCurrentThreadAsDaemon);
            }
            JNIEnv.Native* env;
            JNIResult result = attachCurrentThreadAsDaemon.Invoke(native, out env, args);
            penv = new JNIEnv(env);
            return result;
        }

        public JNIResult DestroyJavaVM()
        {
            if (destroyJavaVM == null)
            {
                Util.GetDelegateForFunctionPointer(functions.DestroyJavaVM, ref destroyJavaVM);
            }
            return destroyJavaVM.Invoke(native);
        }

        public JNIResult DetachCurrentThread()
        {
            if (detachCurrentThread == null)
            {
                Util.GetDelegateForFunctionPointer(functions.DetachCurrentThread, ref detachCurrentThread);
            }
            return detachCurrentThread.Invoke(native);
        }

        public JNIResult GetEnv(out JNIEnv penv, int version)
        {
            if (getEnv == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetEnv, ref getEnv);
            }
            JNIEnv.Native* env;
            JNIResult result = getEnv.Invoke(native, out env, version);
            penv = new JNIEnv(env);
            return result;
        }

        #region Nested type

        [StructLayout(LayoutKind.Sequential, Size = 4), NativeCppClass]
        public struct Native
        {
            public JNIInvokeInterface* functions;

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult AttachCurrentThread(Native* thiz, out JNIEnv.Native* penv, JavaVMInitArgs* args);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult AttachCurrentThreadAsDaemon(
                Native* thiz, out JNIEnv.Native* penv, JavaVMInitArgs* args);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult DestroyJavaVM(Native* thiz);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult DetachCurrentThread(Native* thiz);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult GetEnv(Native* thiz, out JNIEnv.Native* penv, int version);
        }

        #endregion
    }
}