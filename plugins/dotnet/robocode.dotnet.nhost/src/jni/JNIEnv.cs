using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public unsafe class JNIEnv
    {
        #region private members and delegates

        private readonly Real* real;
        private Real.CallVoidMethodA callVoidMethodA;
        private Real.FindClass findClass;
        private JNINativeInterface functions;
        private Real.GetFieldID getFieldID;
        private Real.GetMethodID getMethodID;
        private Real.GetObjectClass getObjectClass;
        private Real.GetVersion getVersion;

        private Real.RegisterNatives registerNatives;

        internal JNIEnv(Real* real)
        {
            this.real = real;
            functions = *(*real).functions;
        }

        #endregion 

        #region JNI methods
        public int GetVersion()
        {
            if (getVersion == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetVersion, ref getVersion);
            }
            return getVersion(real);
        }

        public jclass* FindClass(string name)
        {
            if (findClass == null)
            {
                Util.GetDelegateForFunctionPointer(functions.FindClass, ref findClass);
            }
            return findClass.Invoke(real, name);
        }

        public jclass* GetObjectClass(jobject* obj)
        {
            if (getObjectClass == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetObjectClass, ref getObjectClass);
            }
            return getObjectClass.Invoke(real, obj);
        }

        public jmethodID* GetMethodID(jclass* clazz, string name, string sig)
        {
            if (getMethodID == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetMethodID, ref getMethodID);
            }
            return getMethodID.Invoke(real, clazz, name, sig);
        }

        public jfieldID* GetFieldID(jclass* clazz, string name, string sig)
        {
            if (getFieldID == null)
            {
                Util.GetDelegateForFunctionPointer(functions.GetFieldID, ref getFieldID);
            }
            return getFieldID.Invoke(real, clazz, name, sig);
        }

        public JNIResult RegisterNatives(jclass* clazz, JNINativeMethod* methods, int nMethods)
        {
            if (registerNatives == null)
            {
                Util.GetDelegateForFunctionPointer(functions.RegisterNatives, ref registerNatives);
            }
            return registerNatives.Invoke(real, clazz, methods, nMethods);
        }

        public JNIResult CallVoidMethodA(jobject* obj, jmethodID* methodID, params jvalue[] args)
        {
            if (callVoidMethodA == null)
            {
                Util.GetDelegateForFunctionPointer(functions.CallVoidMethodA, ref callVoidMethodA);
            }
            return callVoidMethodA(real, obj, methodID, args);
        }

        #endregion

        #region Improved methods

        public JNIResult CallVoidMethod(jobject* obj, string method, string sig, params jvalue[] args)
        {
            jclass* objectClass = GetObjectClass(obj);
            if (objectClass != null)
            {
                jmethodID* id = GetMethodID(objectClass, method, sig);
                if (id != null)
                {
                    return CallVoidMethodA(obj, id, args);
                }
            }
            return JNIResult.JNI_ERR;
        }

        #endregion

        #region Nested type: Real

        [StructLayout(LayoutKind.Sequential, Size = 4), NativeCppClass]
        public struct Real
        {
            public JNINativeInterface* functions;

            public JNIEnv Wrap()
            {
                fixed (Real* real1 = &this)
                {
                    return new JNIEnv(real1);
                }
            }

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate int GetVersion(Real* thiz);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult GetJavaVM(Real* thiz, out JavaVM.Real* vm);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate jclass* FindClass(Real* thiz, [MarshalAs(UnmanagedType.LPStr)] string name);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate jclass* GetObjectClass(Real* thiz, jobject* obj);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate jmethodID* GetMethodID(
                Real* thiz, jclass* clazz, [MarshalAs(UnmanagedType.LPStr)] string name,
                [MarshalAs(UnmanagedType.LPStr)] string sig);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate jfieldID* GetFieldID(
                Real* thiz, jclass* clazz, [MarshalAs(UnmanagedType.LPStr)] string name,
                [MarshalAs(UnmanagedType.LPStr)] string sig);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult CallVoidMethodA(
                Real* thiz, jobject* obj, jmethodID* methodID, params jvalue[] args);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult RegisterNatives(Real* thiz, jclass* clazz, JNINativeMethod* methods, int nMethods);

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult UnregisterNatives(Real* thiz, jclass* clazz);
        }

        #endregion

        /*
         
    jclass* DefineClass(const char *name, jobject* loader, const jbyte *buf,
		       jsize len) {
        return functions->DefineClass(this, name, loader, buf, len);
    }

    jmethodID FromReflectedMethod(jobject* method) {
        return functions->FromReflectedMethod(this,method);
    }
    jfieldID FromReflectedField(jobject* field) {
        return functions->FromReflectedField(this,field);
    }

    jobject* ToReflectedMethod(jclass* cls, jmethodID methodID, jboolean isStatic) {
        return functions->ToReflectedMethod(this, cls, methodID, isStatic);
    }

    jclass* GetSuperclass(jclass* sub) {
        return functions->GetSuperclass(this, sub);
    }
    jboolean IsAssignableFrom(jclass* sub, jclass* sup) {
        return functions->IsAssignableFrom(this, sub, sup);
    }

    jobject* ToReflectedField(jclass* cls, jfieldID fieldID, jboolean isStatic) {
        return functions->ToReflectedField(this,cls,fieldID,isStatic);
    }

    jint Throw(jthrowable obj) {
        return functions->Throw(this, obj);
    }
    jint ThrowNew(jclass* clazz, const char *msg) {
        return functions->ThrowNew(this, clazz, msg);
    }
    jthrowable ExceptionOccurred() {
        return functions->ExceptionOccurred(this);
    }
    void ExceptionDescribe() {
        functions->ExceptionDescribe(this);
    }
    void ExceptionClear() {
        functions->ExceptionClear(this);
    }
    void FatalError(const char *msg) {
        functions->FatalError(this, msg);
    }

    jint PushLocalFrame(jint capacity) {
        return functions->PushLocalFrame(this,capacity);
    }
    jobject* PopLocalFrame(jobject* result) {
        return functions->PopLocalFrame(this,result);
    }

    jobject* NewGlobalRef(jobject* lobj) {
        return functions->NewGlobalRef(this,lobj);
    }
    void DeleteGlobalRef(jobject* gref) {
        functions->DeleteGlobalRef(this,gref);
    }
    void DeleteLocalRef(jobject* obj) {
        functions->DeleteLocalRef(this, obj);
    }

    jboolean IsSameObject(jobject* obj1, jobject* obj2) {
        return functions->IsSameObject(this,obj1,obj2);
    }

    jobject* NewLocalRef(jobject* ref) {
        return functions->NewLocalRef(this,ref);
    }
    jint EnsureLocalCapacity(jint capacity) {
        return functions->EnsureLocalCapacity(this,capacity);
    }

    jobject* AllocObject(jclass* clazz) {
        return functions->AllocObject(this,clazz);
    }
    jobject* NewObject(jclass* clazz, jmethodID methodID, ...) {
        va_list args;
	jobject* result;
	va_start(args, methodID);
        result = functions->NewObjectV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jobject* NewObjectV(jclass* clazz, jmethodID methodID,
		       va_list args) {
        return functions->NewObjectV(this,clazz,methodID,args);
    }
    jobject* NewObjectA(jclass* clazz, jmethodID methodID,
		       const jvalue *args) {
        return functions->NewObjectA(this,clazz,methodID,args);
    }

    jboolean IsInstanceOf(jobject* obj, jclass* clazz) {
        return functions->IsInstanceOf(this,obj,clazz);
    }

    jobject* CallObjectMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jobject* result;
	va_start(args,methodID);
	result = functions->CallObjectMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jobject* CallObjectMethodV(jobject* obj, jmethodID methodID,
			va_list args) {
        return functions->CallObjectMethodV(this,obj,methodID,args);
    }
    jobject* CallObjectMethodA(jobject* obj, jmethodID methodID,
			const jvalue * args) {
        return functions->CallObjectMethodA(this,obj,methodID,args);
    }

    jboolean CallBooleanMethod(jobject* obj,
			       jmethodID methodID, ...) {
        va_list args;
	jboolean result;
	va_start(args,methodID);
	result = functions->CallBooleanMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jboolean CallBooleanMethodV(jobject* obj, jmethodID methodID,
				va_list args) {
        return functions->CallBooleanMethodV(this,obj,methodID,args);
    }
    jboolean CallBooleanMethodA(jobject* obj, jmethodID methodID,
				const jvalue * args) {
        return functions->CallBooleanMethodA(this,obj,methodID, args);
    }

    jbyte CallByteMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jbyte result;
	va_start(args,methodID);
	result = functions->CallByteMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jbyte CallByteMethodV(jobject* obj, jmethodID methodID,
			  va_list args) {
        return functions->CallByteMethodV(this,obj,methodID,args);
    }
    jbyte CallByteMethodA(jobject* obj, jmethodID methodID,
			  const jvalue * args) {
        return functions->CallByteMethodA(this,obj,methodID,args);
    }

    jchar CallCharMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jchar result;
	va_start(args,methodID);
	result = functions->CallCharMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jchar CallCharMethodV(jobject* obj, jmethodID methodID,
			  va_list args) {
        return functions->CallCharMethodV(this,obj,methodID,args);
    }
    jchar CallCharMethodA(jobject* obj, jmethodID methodID,
			  const jvalue * args) {
        return functions->CallCharMethodA(this,obj,methodID,args);
    }

    jshort CallShortMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jshort result;
	va_start(args,methodID);
	result = functions->CallShortMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jshort CallShortMethodV(jobject* obj, jmethodID methodID,
			    va_list args) {
        return functions->CallShortMethodV(this,obj,methodID,args);
    }
    jshort CallShortMethodA(jobject* obj, jmethodID methodID,
			    const jvalue * args) {
        return functions->CallShortMethodA(this,obj,methodID,args);
    }

    jint CallIntMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jint result;
	va_start(args,methodID);
	result = functions->CallIntMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jint CallIntMethodV(jobject* obj, jmethodID methodID,
			va_list args) {
        return functions->CallIntMethodV(this,obj,methodID,args);
    }
    jint CallIntMethodA(jobject* obj, jmethodID methodID,
			const jvalue * args) {
        return functions->CallIntMethodA(this,obj,methodID,args);
    }

    jlong CallLongMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jlong result;
	va_start(args,methodID);
	result = functions->CallLongMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jlong CallLongMethodV(jobject* obj, jmethodID methodID,
			  va_list args) {
        return functions->CallLongMethodV(this,obj,methodID,args);
    }
    jlong CallLongMethodA(jobject* obj, jmethodID methodID,
			  const jvalue * args) {
        return functions->CallLongMethodA(this,obj,methodID,args);
    }

    jfloat CallFloatMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jfloat result;
	va_start(args,methodID);
	result = functions->CallFloatMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jfloat CallFloatMethodV(jobject* obj, jmethodID methodID,
			    va_list args) {
        return functions->CallFloatMethodV(this,obj,methodID,args);
    }
    jfloat CallFloatMethodA(jobject* obj, jmethodID methodID,
			    const jvalue * args) {
        return functions->CallFloatMethodA(this,obj,methodID,args);
    }

    jdouble CallDoubleMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	jdouble result;
	va_start(args,methodID);
	result = functions->CallDoubleMethodV(this,obj,methodID,args);
	va_end(args);
	return result;
    }
    jdouble CallDoubleMethodV(jobject* obj, jmethodID methodID,
			va_list args) {
        return functions->CallDoubleMethodV(this,obj,methodID,args);
    }
    jdouble CallDoubleMethodA(jobject* obj, jmethodID methodID,
			const jvalue * args) {
        return functions->CallDoubleMethodA(this,obj,methodID,args);
    }

    void CallVoidMethod(jobject* obj, jmethodID methodID, ...) {
        va_list args;
	va_start(args,methodID);
	functions->CallVoidMethodV(this,obj,methodID,args);
	va_end(args);
    }
    void CallVoidMethodV(jobject* obj, jmethodID methodID,
			 va_list args) {
        functions->CallVoidMethodV(this,obj,methodID,args);
    }
    void CallVoidMethodA(jobject* obj, jmethodID methodID,
			 const jvalue * args) {
        functions->CallVoidMethodA(this,obj,methodID,args);
    }

    jobject* CallNonvirtualObjectMethod(jobject* obj, jclass* clazz,
				       jmethodID methodID, ...) {
        va_list args;
	jobject* result;
	va_start(args,methodID);
	result = functions->CallNonvirtualObjectMethodV(this,obj,clazz,
							methodID,args);
	va_end(args);
	return result;
    }
    jobject* CallNonvirtualObjectMethodV(jobject* obj, jclass* clazz,
					jmethodID methodID, va_list args) {
        return functions->CallNonvirtualObjectMethodV(this,obj,clazz,
						      methodID,args);
    }
    jobject* CallNonvirtualObjectMethodA(jobject* obj, jclass* clazz,
					jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualObjectMethodA(this,obj,clazz,
						      methodID,args);
    }

    jboolean CallNonvirtualBooleanMethod(jobject* obj, jclass* clazz,
					 jmethodID methodID, ...) {
        va_list args;
	jboolean result;
	va_start(args,methodID);
	result = functions->CallNonvirtualBooleanMethodV(this,obj,clazz,
							 methodID,args);
	va_end(args);
	return result;
    }
    jboolean CallNonvirtualBooleanMethodV(jobject* obj, jclass* clazz,
					  jmethodID methodID, va_list args) {
        return functions->CallNonvirtualBooleanMethodV(this,obj,clazz,
						       methodID,args);
    }
    jboolean CallNonvirtualBooleanMethodA(jobject* obj, jclass* clazz,
					  jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualBooleanMethodA(this,obj,clazz,
						       methodID, args);
    }

    jbyte CallNonvirtualByteMethod(jobject* obj, jclass* clazz,
				   jmethodID methodID, ...) {
        va_list args;
	jbyte result;
	va_start(args,methodID);
	result = functions->CallNonvirtualByteMethodV(this,obj,clazz,
						      methodID,args);
	va_end(args);
	return result;
    }
    jbyte CallNonvirtualByteMethodV(jobject* obj, jclass* clazz,
				    jmethodID methodID, va_list args) {
        return functions->CallNonvirtualByteMethodV(this,obj,clazz,
						    methodID,args);
    }
    jbyte CallNonvirtualByteMethodA(jobject* obj, jclass* clazz,
				    jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualByteMethodA(this,obj,clazz,
						    methodID,args);
    }

    jchar CallNonvirtualCharMethod(jobject* obj, jclass* clazz,
				   jmethodID methodID, ...) {
        va_list args;
	jchar result;
	va_start(args,methodID);
	result = functions->CallNonvirtualCharMethodV(this,obj,clazz,
						      methodID,args);
	va_end(args);
	return result;
    }
    jchar CallNonvirtualCharMethodV(jobject* obj, jclass* clazz,
				    jmethodID methodID, va_list args) {
        return functions->CallNonvirtualCharMethodV(this,obj,clazz,
						    methodID,args);
    }
    jchar CallNonvirtualCharMethodA(jobject* obj, jclass* clazz,
				    jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualCharMethodA(this,obj,clazz,
						    methodID,args);
    }

    jshort CallNonvirtualShortMethod(jobject* obj, jclass* clazz,
				     jmethodID methodID, ...) {
        va_list args;
	jshort result;
	va_start(args,methodID);
	result = functions->CallNonvirtualShortMethodV(this,obj,clazz,
						       methodID,args);
	va_end(args);
	return result;
    }
    jshort CallNonvirtualShortMethodV(jobject* obj, jclass* clazz,
				      jmethodID methodID, va_list args) {
        return functions->CallNonvirtualShortMethodV(this,obj,clazz,
						     methodID,args);
    }
    jshort CallNonvirtualShortMethodA(jobject* obj, jclass* clazz,
				      jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualShortMethodA(this,obj,clazz,
						     methodID,args);
    }

    jint CallNonvirtualIntMethod(jobject* obj, jclass* clazz,
				 jmethodID methodID, ...) {
        va_list args;
	jint result;
	va_start(args,methodID);
	result = functions->CallNonvirtualIntMethodV(this,obj,clazz,
						     methodID,args);
	va_end(args);
	return result;
    }
    jint CallNonvirtualIntMethodV(jobject* obj, jclass* clazz,
				  jmethodID methodID, va_list args) {
        return functions->CallNonvirtualIntMethodV(this,obj,clazz,
						   methodID,args);
    }
    jint CallNonvirtualIntMethodA(jobject* obj, jclass* clazz,
				  jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualIntMethodA(this,obj,clazz,
						   methodID,args);
    }

    jlong CallNonvirtualLongMethod(jobject* obj, jclass* clazz,
				   jmethodID methodID, ...) {
        va_list args;
	jlong result;
	va_start(args,methodID);
	result = functions->CallNonvirtualLongMethodV(this,obj,clazz,
						      methodID,args);
	va_end(args);
	return result;
    }
    jlong CallNonvirtualLongMethodV(jobject* obj, jclass* clazz,
				    jmethodID methodID, va_list args) {
        return functions->CallNonvirtualLongMethodV(this,obj,clazz,
						    methodID,args);
    }
    jlong CallNonvirtualLongMethodA(jobject* obj, jclass* clazz,
				    jmethodID methodID, const jvalue * args) {
        return functions->CallNonvirtualLongMethodA(this,obj,clazz,
						    methodID,args);
    }

    jfloat CallNonvirtualFloatMethod(jobject* obj, jclass* clazz,
				     jmethodID methodID, ...) {
        va_list args;
	jfloat result;
	va_start(args,methodID);
	result = functions->CallNonvirtualFloatMethodV(this,obj,clazz,
						       methodID,args);
	va_end(args);
	return result;
    }
    jfloat CallNonvirtualFloatMethodV(jobject* obj, jclass* clazz,
				      jmethodID methodID,
				      va_list args) {
        return functions->CallNonvirtualFloatMethodV(this,obj,clazz,
						     methodID,args);
    }
    jfloat CallNonvirtualFloatMethodA(jobject* obj, jclass* clazz,
				      jmethodID methodID,
				      const jvalue * args) {
        return functions->CallNonvirtualFloatMethodA(this,obj,clazz,
						     methodID,args);
    }

    jdouble CallNonvirtualDoubleMethod(jobject* obj, jclass* clazz,
				       jmethodID methodID, ...) {
        va_list args;
	jdouble result;
	va_start(args,methodID);
	result = functions->CallNonvirtualDoubleMethodV(this,obj,clazz,
							methodID,args);
	va_end(args);
	return result;
    }
    jdouble CallNonvirtualDoubleMethodV(jobject* obj, jclass* clazz,
					jmethodID methodID,
					va_list args) {
        return functions->CallNonvirtualDoubleMethodV(this,obj,clazz,
						      methodID,args);
    }
    jdouble CallNonvirtualDoubleMethodA(jobject* obj, jclass* clazz,
					jmethodID methodID,
					const jvalue * args) {
        return functions->CallNonvirtualDoubleMethodA(this,obj,clazz,
						      methodID,args);
    }

    void CallNonvirtualVoidMethod(jobject* obj, jclass* clazz,
				  jmethodID methodID, ...) {
        va_list args;
	va_start(args,methodID);
	functions->CallNonvirtualVoidMethodV(this,obj,clazz,methodID,args);
	va_end(args);
    }
    void CallNonvirtualVoidMethodV(jobject* obj, jclass* clazz,
				   jmethodID methodID,
				   va_list args) {
        functions->CallNonvirtualVoidMethodV(this,obj,clazz,methodID,args);
    }
    void CallNonvirtualVoidMethodA(jobject* obj, jclass* clazz,
				   jmethodID methodID,
				   const jvalue * args) {
        functions->CallNonvirtualVoidMethodA(this,obj,clazz,methodID,args);
    }

    jobject* GetObjectField(jobject* obj, jfieldID fieldID) {
        return functions->GetObjectField(this,obj,fieldID);
    }
    jboolean GetBooleanField(jobject* obj, jfieldID fieldID) {
        return functions->GetBooleanField(this,obj,fieldID);
    }
    jbyte GetByteField(jobject* obj, jfieldID fieldID) {
        return functions->GetByteField(this,obj,fieldID);
    }
    jchar GetCharField(jobject* obj, jfieldID fieldID) {
        return functions->GetCharField(this,obj,fieldID);
    }
    jshort GetShortField(jobject* obj, jfieldID fieldID) {
        return functions->GetShortField(this,obj,fieldID);
    }
    jint GetIntField(jobject* obj, jfieldID fieldID) {
        return functions->GetIntField(this,obj,fieldID);
    }
    jlong GetLongField(jobject* obj, jfieldID fieldID) {
        return functions->GetLongField(this,obj,fieldID);
    }
    jfloat GetFloatField(jobject* obj, jfieldID fieldID) {
        return functions->GetFloatField(this,obj,fieldID);
    }
    jdouble GetDoubleField(jobject* obj, jfieldID fieldID) {
        return functions->GetDoubleField(this,obj,fieldID);
    }

    void SetObjectField(jobject* obj, jfieldID fieldID, jobject* val) {
        functions->SetObjectField(this,obj,fieldID,val);
    }
    void SetBooleanField(jobject* obj, jfieldID fieldID,
			 jboolean val) {
        functions->SetBooleanField(this,obj,fieldID,val);
    }
    void SetByteField(jobject* obj, jfieldID fieldID,
		      jbyte val) {
        functions->SetByteField(this,obj,fieldID,val);
    }
    void SetCharField(jobject* obj, jfieldID fieldID,
		      jchar val) {
        functions->SetCharField(this,obj,fieldID,val);
    }
    void SetShortField(jobject* obj, jfieldID fieldID,
		       jshort val) {
        functions->SetShortField(this,obj,fieldID,val);
    }
    void SetIntField(jobject* obj, jfieldID fieldID,
		     jint val) {
        functions->SetIntField(this,obj,fieldID,val);
    }
    void SetLongField(jobject* obj, jfieldID fieldID,
		      jlong val) {
        functions->SetLongField(this,obj,fieldID,val);
    }
    void SetFloatField(jobject* obj, jfieldID fieldID,
		       jfloat val) {
        functions->SetFloatField(this,obj,fieldID,val);
    }
    void SetDoubleField(jobject* obj, jfieldID fieldID,
			jdouble val) {
        functions->SetDoubleField(this,obj,fieldID,val);
    }

    jmethodID GetStaticMethodID(jclass* clazz, const char *name,
				const char *sig) {
        return functions->GetStaticMethodID(this,clazz,name,sig);
    }

    jobject* CallStaticObjectMethod(jclass* clazz, jmethodID methodID,
			     ...) {
        va_list args;
	jobject* result;
	va_start(args,methodID);
	result = functions->CallStaticObjectMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jobject* CallStaticObjectMethodV(jclass* clazz, jmethodID methodID,
			      va_list args) {
        return functions->CallStaticObjectMethodV(this,clazz,methodID,args);
    }
    jobject* CallStaticObjectMethodA(jclass* clazz, jmethodID methodID,
			      const jvalue *args) {
        return functions->CallStaticObjectMethodA(this,clazz,methodID,args);
    }

    jboolean CallStaticBooleanMethod(jclass* clazz,
				     jmethodID methodID, ...) {
        va_list args;
	jboolean result;
	va_start(args,methodID);
	result = functions->CallStaticBooleanMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jboolean CallStaticBooleanMethodV(jclass* clazz,
				      jmethodID methodID, va_list args) {
        return functions->CallStaticBooleanMethodV(this,clazz,methodID,args);
    }
    jboolean CallStaticBooleanMethodA(jclass* clazz,
				      jmethodID methodID, const jvalue *args) {
        return functions->CallStaticBooleanMethodA(this,clazz,methodID,args);
    }

    jbyte CallStaticByteMethod(jclass* clazz,
			       jmethodID methodID, ...) {
        va_list args;
	jbyte result;
	va_start(args,methodID);
	result = functions->CallStaticByteMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jbyte CallStaticByteMethodV(jclass* clazz,
				jmethodID methodID, va_list args) {
        return functions->CallStaticByteMethodV(this,clazz,methodID,args);
    }
    jbyte CallStaticByteMethodA(jclass* clazz,
				jmethodID methodID, const jvalue *args) {
        return functions->CallStaticByteMethodA(this,clazz,methodID,args);
    }

    jchar CallStaticCharMethod(jclass* clazz,
			       jmethodID methodID, ...) {
        va_list args;
	jchar result;
	va_start(args,methodID);
	result = functions->CallStaticCharMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jchar CallStaticCharMethodV(jclass* clazz,
				jmethodID methodID, va_list args) {
        return functions->CallStaticCharMethodV(this,clazz,methodID,args);
    }
    jchar CallStaticCharMethodA(jclass* clazz,
				jmethodID methodID, const jvalue *args) {
        return functions->CallStaticCharMethodA(this,clazz,methodID,args);
    }

    jshort CallStaticShortMethod(jclass* clazz,
				 jmethodID methodID, ...) {
        va_list args;
	jshort result;
	va_start(args,methodID);
	result = functions->CallStaticShortMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jshort CallStaticShortMethodV(jclass* clazz,
				  jmethodID methodID, va_list args) {
        return functions->CallStaticShortMethodV(this,clazz,methodID,args);
    }
    jshort CallStaticShortMethodA(jclass* clazz,
				  jmethodID methodID, const jvalue *args) {
        return functions->CallStaticShortMethodA(this,clazz,methodID,args);
    }

    jint CallStaticIntMethod(jclass* clazz,
			     jmethodID methodID, ...) {
        va_list args;
	jint result;
	va_start(args,methodID);
	result = functions->CallStaticIntMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jint CallStaticIntMethodV(jclass* clazz,
			      jmethodID methodID, va_list args) {
        return functions->CallStaticIntMethodV(this,clazz,methodID,args);
    }
    jint CallStaticIntMethodA(jclass* clazz,
			      jmethodID methodID, const jvalue *args) {
        return functions->CallStaticIntMethodA(this,clazz,methodID,args);
    }

    jlong CallStaticLongMethod(jclass* clazz,
			       jmethodID methodID, ...) {
        va_list args;
	jlong result;
	va_start(args,methodID);
	result = functions->CallStaticLongMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jlong CallStaticLongMethodV(jclass* clazz,
				jmethodID methodID, va_list args) {
        return functions->CallStaticLongMethodV(this,clazz,methodID,args);
    }
    jlong CallStaticLongMethodA(jclass* clazz,
				jmethodID methodID, const jvalue *args) {
        return functions->CallStaticLongMethodA(this,clazz,methodID,args);
    }

    jfloat CallStaticFloatMethod(jclass* clazz,
				 jmethodID methodID, ...) {
        va_list args;
	jfloat result;
	va_start(args,methodID);
	result = functions->CallStaticFloatMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jfloat CallStaticFloatMethodV(jclass* clazz,
				  jmethodID methodID, va_list args) {
        return functions->CallStaticFloatMethodV(this,clazz,methodID,args);
    }
    jfloat CallStaticFloatMethodA(jclass* clazz,
				  jmethodID methodID, const jvalue *args) {
        return functions->CallStaticFloatMethodA(this,clazz,methodID,args);
    }

    jdouble CallStaticDoubleMethod(jclass* clazz,
				   jmethodID methodID, ...) {
        va_list args;
	jdouble result;
	va_start(args,methodID);
	result = functions->CallStaticDoubleMethodV(this,clazz,methodID,args);
	va_end(args);
	return result;
    }
    jdouble CallStaticDoubleMethodV(jclass* clazz,
				    jmethodID methodID, va_list args) {
        return functions->CallStaticDoubleMethodV(this,clazz,methodID,args);
    }
    jdouble CallStaticDoubleMethodA(jclass* clazz,
				    jmethodID methodID, const jvalue *args) {
        return functions->CallStaticDoubleMethodA(this,clazz,methodID,args);
    }

    void CallStaticVoidMethod(jclass* cls, jmethodID methodID, ...) {
        va_list args;
	va_start(args,methodID);
	functions->CallStaticVoidMethodV(this,cls,methodID,args);
	va_end(args);
    }
    void CallStaticVoidMethodV(jclass* cls, jmethodID methodID,
			       va_list args) {
        functions->CallStaticVoidMethodV(this,cls,methodID,args);
    }
    void CallStaticVoidMethodA(jclass* cls, jmethodID methodID,
			       const jvalue * args) {
        functions->CallStaticVoidMethodA(this,cls,methodID,args);
    }

    jfieldID GetStaticFieldID(jclass* clazz, const char *name,
			      const char *sig) {
        return functions->GetStaticFieldID(this,clazz,name,sig);
    }
    jobject* GetStaticObjectField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticObjectField(this,clazz,fieldID);
    }
    jboolean GetStaticBooleanField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticBooleanField(this,clazz,fieldID);
    }
    jbyte GetStaticByteField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticByteField(this,clazz,fieldID);
    }
    jchar GetStaticCharField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticCharField(this,clazz,fieldID);
    }
    jshort GetStaticShortField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticShortField(this,clazz,fieldID);
    }
    jint GetStaticIntField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticIntField(this,clazz,fieldID);
    }
    jlong GetStaticLongField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticLongField(this,clazz,fieldID);
    }
    jfloat GetStaticFloatField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticFloatField(this,clazz,fieldID);
    }
    jdouble GetStaticDoubleField(jclass* clazz, jfieldID fieldID) {
        return functions->GetStaticDoubleField(this,clazz,fieldID);
    }

    void SetStaticObjectField(jclass* clazz, jfieldID fieldID,
			jobject* value) {
      functions->SetStaticObjectField(this,clazz,fieldID,value);
    }
    void SetStaticBooleanField(jclass* clazz, jfieldID fieldID,
			jboolean value) {
      functions->SetStaticBooleanField(this,clazz,fieldID,value);
    }
    void SetStaticByteField(jclass* clazz, jfieldID fieldID,
			jbyte value) {
      functions->SetStaticByteField(this,clazz,fieldID,value);
    }
    void SetStaticCharField(jclass* clazz, jfieldID fieldID,
			jchar value) {
      functions->SetStaticCharField(this,clazz,fieldID,value);
    }
    void SetStaticShortField(jclass* clazz, jfieldID fieldID,
			jshort value) {
      functions->SetStaticShortField(this,clazz,fieldID,value);
    }
    void SetStaticIntField(jclass* clazz, jfieldID fieldID,
			jint value) {
      functions->SetStaticIntField(this,clazz,fieldID,value);
    }
    void SetStaticLongField(jclass* clazz, jfieldID fieldID,
			jlong value) {
      functions->SetStaticLongField(this,clazz,fieldID,value);
    }
    void SetStaticFloatField(jclass* clazz, jfieldID fieldID,
			jfloat value) {
      functions->SetStaticFloatField(this,clazz,fieldID,value);
    }
    void SetStaticDoubleField(jclass* clazz, jfieldID fieldID,
			jdouble value) {
      functions->SetStaticDoubleField(this,clazz,fieldID,value);
    }

    jstring NewString(const jchar *unicode, jsize len) {
        return functions->NewString(this,unicode,len);
    }
    jsize GetStringLength(jstring str) {
        return functions->GetStringLength(this,str);
    }
    const jchar *GetStringChars(jstring str, jboolean *isCopy) {
        return functions->GetStringChars(this,str,isCopy);
    }
    void ReleaseStringChars(jstring str, const jchar *chars) {
        functions->ReleaseStringChars(this,str,chars);
    }

    jstring NewStringUTF(const char *utf) {
        return functions->NewStringUTF(this,utf);
    }
    jsize GetStringUTFLength(jstring str) {
        return functions->GetStringUTFLength(this,str);
    }
    const char* GetStringUTFChars(jstring str, jboolean *isCopy) {
        return functions->GetStringUTFChars(this,str,isCopy);
    }
    void ReleaseStringUTFChars(jstring str, const char* chars) {
        functions->ReleaseStringUTFChars(this,str,chars);
    }

    jsize GetArrayLength(jarray array) {
        return functions->GetArrayLength(this,array);
    }

    jobject*Array NewObjectArray(jsize len, jclass* clazz,
				jobject* init) {
        return functions->NewObjectArray(this,len,clazz,init);
    }
    jobject* GetObjectArrayElement(jobject*Array array, jsize index) {
        return functions->GetObjectArrayElement(this,array,index);
    }
    void SetObjectArrayElement(jobject*Array array, jsize index,
			       jobject* val) {
        functions->SetObjectArrayElement(this,array,index,val);
    }

    jbooleanArray NewBooleanArray(jsize len) {
        return functions->NewBooleanArray(this,len);
    }
    jbyteArray NewByteArray(jsize len) {
        return functions->NewByteArray(this,len);
    }
    jcharArray NewCharArray(jsize len) {
        return functions->NewCharArray(this,len);
    }
    jshortArray NewShortArray(jsize len) {
        return functions->NewShortArray(this,len);
    }
    jintArray NewIntArray(jsize len) {
        return functions->NewIntArray(this,len);
    }
    jlongArray NewLongArray(jsize len) {
        return functions->NewLongArray(this,len);
    }
    jfloatArray NewFloatArray(jsize len) {
        return functions->NewFloatArray(this,len);
    }
    jdoubleArray NewDoubleArray(jsize len) {
        return functions->NewDoubleArray(this,len);
    }

    jboolean * GetBooleanArrayElements(jbooleanArray array, jboolean *isCopy) {
        return functions->GetBooleanArrayElements(this,array,isCopy);
    }
    jbyte * GetByteArrayElements(jbyteArray array, jboolean *isCopy) {
        return functions->GetByteArrayElements(this,array,isCopy);
    }
    jchar * GetCharArrayElements(jcharArray array, jboolean *isCopy) {
        return functions->GetCharArrayElements(this,array,isCopy);
    }
    jshort * GetShortArrayElements(jshortArray array, jboolean *isCopy) {
        return functions->GetShortArrayElements(this,array,isCopy);
    }
    jint * GetIntArrayElements(jintArray array, jboolean *isCopy) {
        return functions->GetIntArrayElements(this,array,isCopy);
    }
    jlong * GetLongArrayElements(jlongArray array, jboolean *isCopy) {
        return functions->GetLongArrayElements(this,array,isCopy);
    }
    jfloat * GetFloatArrayElements(jfloatArray array, jboolean *isCopy) {
        return functions->GetFloatArrayElements(this,array,isCopy);
    }
    jdouble * GetDoubleArrayElements(jdoubleArray array, jboolean *isCopy) {
        return functions->GetDoubleArrayElements(this,array,isCopy);
    }

    void ReleaseBooleanArrayElements(jbooleanArray array,
				     jboolean *elems,
				     jint mode) {
        functions->ReleaseBooleanArrayElements(this,array,elems,mode);
    }
    void ReleaseByteArrayElements(jbyteArray array,
				  jbyte *elems,
				  jint mode) {
        functions->ReleaseByteArrayElements(this,array,elems,mode);
    }
    void ReleaseCharArrayElements(jcharArray array,
				  jchar *elems,
				  jint mode) {
        functions->ReleaseCharArrayElements(this,array,elems,mode);
    }
    void ReleaseShortArrayElements(jshortArray array,
				   jshort *elems,
				   jint mode) {
        functions->ReleaseShortArrayElements(this,array,elems,mode);
    }
    void ReleaseIntArrayElements(jintArray array,
				 jint *elems,
				 jint mode) {
        functions->ReleaseIntArrayElements(this,array,elems,mode);
    }
    void ReleaseLongArrayElements(jlongArray array,
				  jlong *elems,
				  jint mode) {
        functions->ReleaseLongArrayElements(this,array,elems,mode);
    }
    void ReleaseFloatArrayElements(jfloatArray array,
				   jfloat *elems,
				   jint mode) {
        functions->ReleaseFloatArrayElements(this,array,elems,mode);
    }
    void ReleaseDoubleArrayElements(jdoubleArray array,
				    jdouble *elems,
				    jint mode) {
        functions->ReleaseDoubleArrayElements(this,array,elems,mode);
    }

    void GetBooleanArrayRegion(jbooleanArray array,
			       jsize start, jsize len, jboolean *buf) {
        functions->GetBooleanArrayRegion(this,array,start,len,buf);
    }
    void GetByteArrayRegion(jbyteArray array,
			    jsize start, jsize len, jbyte *buf) {
        functions->GetByteArrayRegion(this,array,start,len,buf);
    }
    void GetCharArrayRegion(jcharArray array,
			    jsize start, jsize len, jchar *buf) {
        functions->GetCharArrayRegion(this,array,start,len,buf);
    }
    void GetShortArrayRegion(jshortArray array,
			     jsize start, jsize len, jshort *buf) {
        functions->GetShortArrayRegion(this,array,start,len,buf);
    }
    void GetIntArrayRegion(jintArray array,
			   jsize start, jsize len, jint *buf) {
        functions->GetIntArrayRegion(this,array,start,len,buf);
    }
    void GetLongArrayRegion(jlongArray array,
			    jsize start, jsize len, jlong *buf) {
        functions->GetLongArrayRegion(this,array,start,len,buf);
    }
    void GetFloatArrayRegion(jfloatArray array,
			     jsize start, jsize len, jfloat *buf) {
        functions->GetFloatArrayRegion(this,array,start,len,buf);
    }
    void GetDoubleArrayRegion(jdoubleArray array,
			      jsize start, jsize len, jdouble *buf) {
        functions->GetDoubleArrayRegion(this,array,start,len,buf);
    }

    void SetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len,
			       const jboolean *buf) {
        functions->SetBooleanArrayRegion(this,array,start,len,buf);
    }
    void SetByteArrayRegion(jbyteArray array, jsize start, jsize len,
			    const jbyte *buf) {
        functions->SetByteArrayRegion(this,array,start,len,buf);
    }
    void SetCharArrayRegion(jcharArray array, jsize start, jsize len,
			    const jchar *buf) {
        functions->SetCharArrayRegion(this,array,start,len,buf);
    }
    void SetShortArrayRegion(jshortArray array, jsize start, jsize len,
			     const jshort *buf) {
        functions->SetShortArrayRegion(this,array,start,len,buf);
    }
    void SetIntArrayRegion(jintArray array, jsize start, jsize len,
			   const jint *buf) {
        functions->SetIntArrayRegion(this,array,start,len,buf);
    }
    void SetLongArrayRegion(jlongArray array, jsize start, jsize len,
			    const jlong *buf) {
        functions->SetLongArrayRegion(this,array,start,len,buf);
    }
    void SetFloatArrayRegion(jfloatArray array, jsize start, jsize len,
			     const jfloat *buf) {
        functions->SetFloatArrayRegion(this,array,start,len,buf);
    }
    void SetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len,
			      const jdouble *buf) {
        functions->SetDoubleArrayRegion(this,array,start,len,buf);
    }


    jint MonitorEnter(jobject* obj) {
        return functions->MonitorEnter(this,obj);
    }
    jint MonitorExit(jobject* obj) {
        return functions->MonitorExit(this,obj);
    }

    void GetStringRegion(jstring str, jsize start, jsize len, jchar *buf) {
        functions->GetStringRegion(this,str,start,len,buf);
    }
    void GetStringUTFRegion(jstring str, jsize start, jsize len, char *buf) {
        functions->GetStringUTFRegion(this,str,start,len,buf);
    }

    void * GetPrimitiveArrayCritical(jarray array, jboolean *isCopy) {
        return functions->GetPrimitiveArrayCritical(this,array,isCopy);
    }
    void ReleasePrimitiveArrayCritical(jarray array, void *carray, jint mode) {
        functions->ReleasePrimitiveArrayCritical(this,array,carray,mode);
    }

    const jchar * GetStringCritical(jstring string, jboolean *isCopy) {
        return functions->GetStringCritical(this,string,isCopy);
    }
    void ReleaseStringCritical(jstring string, const jchar *cstring) {
        functions->ReleaseStringCritical(this,string,cstring);
    }

    jweak NewWeakGlobalRef(jobject* obj) {
        return functions->NewWeakGlobalRef(this,obj);
    }
    void DeleteWeakGlobalRef(jweak ref) {
        functions->DeleteWeakGlobalRef(this,ref);
    }

    jboolean ExceptionCheck() {
	return functions->ExceptionCheck(this);
    }

    jobject* NewDirectByteBuffer(void* address, jlong capacity) {
        return functions->NewDirectByteBuffer(this, address, capacity);
    }
    void* GetDirectBufferAddress(jobject* buf) {
        return functions->GetDirectBufferAddress(this, buf);
    }
    jlong GetDirectBufferCapacity(jobject* buf) {
        return functions->GetDirectBufferCapacity(this, buf);
    }
         
         */
    }
}