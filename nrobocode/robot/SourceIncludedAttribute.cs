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
    public sealed class SourceIncludedAttribute : Attribute
    {
        // Fields
        private bool m_SourceIncluded;

        // Methods
        public SourceIncludedAttribute(bool SourceIncluded)
        {
            this.m_SourceIncluded = SourceIncluded;
        }

        // Properties
        public bool SourceIncluded
        {
            get { return this.m_SourceIncluded; }
        }
    }
}