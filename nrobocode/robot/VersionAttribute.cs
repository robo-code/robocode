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
    public sealed class VersionAttribute : Attribute
    {
        private Version m_Version;

        public VersionAttribute(Version Version)
        {
            this.m_Version = Version;
        }

        public VersionAttribute(string version)
        {
            this.m_Version = new Version(version);
        }

        public Version Version
        {
            get { return this.m_Version; }
        }
    }
}