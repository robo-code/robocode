/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.IO;
using System.Runtime.Remoting;
using System.Threading;
using Microsoft.Win32.SafeHandles;

namespace net.sf.robocode.dotnet.host.security
{
    internal class QuotaStream : FileStream
    {
        private readonly RobotFileSystemManager manager;

        public QuotaStream(RobotFileSystemManager manager, String path, FileMode mode, FileAccess access,
                           FileShare share)
            : base(path, mode, access, share)
        {
            this.manager = manager;
        }

        [Obsolete]
        public override IntPtr Handle
        {
            get { throw new AccessViolationException(); }
        }

        public override SafeFileHandle SafeFileHandle
        {
            get { throw new AccessViolationException(); }
        }

        public override void SetLength(long value)
        {
            manager.appendQuotaUsed(value - Length);
            base.SetLength(value);
        }

        public override void WriteByte(byte value)
        {
            long delta = Position + 1 - Length;
            if (delta > 0)
            {
                manager.appendQuotaUsed(delta);
            }
            base.WriteByte(value);
        }

        public override void Write(byte[] array, int offset, int count)
        {
            long delta = Position + count - Length;
            if (delta > 0)
            {
                manager.appendQuotaUsed(delta);
            }
            base.Write(array, offset, count);
        }

        public override IAsyncResult BeginWrite(byte[] array, int offset, int numBytes, AsyncCallback userCallback,
                                                object stateObject)
        {
            long delta = Position + numBytes - Length;
            if (delta > 0)
            {
                manager.appendQuotaUsed(delta);
            }
            return base.BeginWrite(array, offset, numBytes, userCallback, stateObject);
        }

        /*public override long Position
        {
            get { return base.Position; }
            set
            {
                base.Position = value;
            }
        }

        public override long Seek(long offset, SeekOrigin origin)
        {
            return base.Seek(offset, origin);
        }*/

        public override ObjRef CreateObjRef(Type requestedType)
        {
            throw new AccessViolationException();
        }

        [Obsolete]
        protected override WaitHandle CreateWaitHandle()
        {
            throw new AccessViolationException();
        }

        public override object InitializeLifetimeService()
        {
            throw new AccessViolationException();
        }

        public override void Lock(long position, long length)
        {
            throw new AccessViolationException();
        }

        public override void Unlock(long position, long length)
        {
            throw new AccessViolationException();
        }
    }
}