using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public unsafe class JavaVM
    {
        private readonly Real* real;
        private Real.AttachCurrentThread attachCurrentThread;
        private Real.AttachCurrentThreadAsDaemon attachCurrentThreadAsDaemon;
        private Real.DestroyJavaVM destroyJavaVM;
        private Real.DetachCurrentThread detachCurrentThread;
        private JNIInvokeInterface functions;
        private Real.GetEnv getEnv;


        internal JavaVM(Real* real)
        {
            this.real = real;
            functions = *(*real).functions;
        }

        public JNIResult AttachCurrentThread(out JNIEnv penv, JavaVMInitArgs* args)
        {
            if (attachCurrentThread == null)
            {
                Util.GetDelegateForFunctionPointer(functions.AttachCurrentThread, ref attachCurrentThread);
            }
            JNIEnv.Real* env;
            JNIResult result = attachCurrentThread.Invoke(real, out env, args);
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
            JNIEnv.Real* env;
            JNIResult result = attachCurrentThreadAsDaemon.Invoke(real, out env, args);
            penv = new JNIEnv(env);
            return result;
        }

        public JNIResult DestroyJavaVM()
        {
            if (destroyJavaVM == null)
            {
                Util.GetDelegateForFunctionPointer(functions.DestroyJavaVM, ref destroyJavaVM);
            }
            return destroyJavaVM.Invoke(real);
        }

        public JNIResult DetachCurrentThread()
        {
            if (detachCurrentThread == null)
            {
                Util.GetDelegateForFunctionPointer(functions.DetachCurrentThread, ref detachCurrentThread);
            }
            return detachCurrentThread.Invoke(real);
        }

        public JNIResult GetEnv(out JNIEnv penv, int version)
        {
            if (getEnv == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetEnv, ref getEnv);
            }
            JNIEnv.Real* env;
            JNIResult result = getEnv.Invoke(real, out env, version);
            penv = new JNIEnv(env);
            return result;
        }

        #region Nested type: Real

        [StructLayout(LayoutKind.Sequential, Size = 4), NativeCppClass]
        public struct Real
        {
            public JNIInvokeInterface* functions;

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult AttachCurrentThread(Real* thiz, out JNIEnv.Real* penv, JavaVMInitArgs* args);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult AttachCurrentThreadAsDaemon(
                Real* thiz, out JNIEnv.Real* penv, JavaVMInitArgs* args);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult DestroyJavaVM(Real* thiz);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult DetachCurrentThread(Real* thiz);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult GetEnv(Real* thiz, out JNIEnv.Real* penv, int version);
        }

        #endregion
    }
}