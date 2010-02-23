#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

namespace robocode.control
{
    /*
    [Serializable]
    partial class RobotSpecification : ISerializable
    {
        protected RobotSpecification(SerializationInfo info, StreamingContext context)
        {
            var handle = (IntPtr)info.GetValue("handle", typeof(IntPtr));
            ((IJvmProxy)this).Init(JNIEnv.ThreadEnv, handle, null);
        }

        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("handle", ((IJvmProxy) this).JvmHandle);
        }
    }*/
}