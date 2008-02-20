// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
//  - Initial implementation
// *****************************************************************************
using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Assembly, AllowMultiple = false)]
    public sealed class AuthorNameAttribute : Attribute
    {
        // Fields
        private string m_AuthorName;

        // Methods
        public AuthorNameAttribute(string AuthorName)
        {
            this.m_AuthorName = AuthorName;
        }

        // Properties
        public string AuthorName
        {
            get { return this.m_AuthorName; }
        }
    }
}