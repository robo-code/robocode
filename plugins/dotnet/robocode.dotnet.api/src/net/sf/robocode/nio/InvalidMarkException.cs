/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Runtime.Serialization;

namespace net.sf.robocode.nio
{
#pragma warning disable 1591

    /// <exclude/>
    [Serializable]
    public class InvalidMarkException : Exception
    {
        public InvalidMarkException()
        {
        }

        public InvalidMarkException(string message)
            : base(message)
        {
        }

        public InvalidMarkException(string message, Exception inner)
            : base(message, inner)
        {
        }

        protected InvalidMarkException(SerializationInfo info,
                                       StreamingContext context)
            : base(info, context)
        {
        }
    }

    /// <exclude/>
    [Serializable]
    public class BufferUnderflowException : Exception
    {
        public BufferUnderflowException()
        {
        }

        public BufferUnderflowException(string message)
            : base(message)
        {
        }

        public BufferUnderflowException(string message, Exception inner)
            : base(message, inner)
        {
        }

        protected BufferUnderflowException(SerializationInfo info,
                                           StreamingContext context)
            : base(info, context)
        {
        }
    }

    /// <exclude/>
    [Serializable]
    public class BufferOverflowException : Exception
    {
        public BufferOverflowException()
        {
        }

        public BufferOverflowException(string message)
            : base(message)
        {
        }

        public BufferOverflowException(string message, Exception inner)
            : base(message, inner)
        {
        }

        protected BufferOverflowException(SerializationInfo info,
                                          StreamingContext context)
            : base(info, context)
        {
        }
    }

    /// <exclude/>
    [Serializable]
    public class ReadOnlyBufferException : Exception
    {
        public ReadOnlyBufferException()
        {
        }

        public ReadOnlyBufferException(string message)
            : base(message)
        {
        }

        public ReadOnlyBufferException(string message, Exception inner)
            : base(message, inner)
        {
        }

        protected ReadOnlyBufferException(SerializationInfo info,
                                          StreamingContext context)
            : base(info, context)
        {
        }
    }
}