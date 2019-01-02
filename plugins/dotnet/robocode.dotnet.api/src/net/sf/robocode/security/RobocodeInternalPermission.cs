/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Security;
using System.Security.Permissions;
using System.Text;

namespace net.sf.robocode.security
{
#pragma warning disable 1591
    /// <exclude/>
    [Serializable]
    public sealed class RobocodeInternalPermission : CodeAccessPermission, IUnrestrictedPermission
    {
        private bool unrestricted;

        public RobocodeInternalPermission(PermissionState state)
        {
            unrestricted = state == PermissionState.Unrestricted;
        }

        #region IUnrestrictedPermission Members

        public bool IsUnrestricted()
        {
            return unrestricted;
        }

        #endregion

        public override IPermission Copy()
        {
            //Create a new instance of RobocodeInternalPermission with the current
            //value of unrestricted.
            var copy = new RobocodeInternalPermission(PermissionState.None);

            copy.unrestricted = IsUnrestricted();
            //Return the copy.
            return copy;
        }

        public override IPermission Intersect(IPermission target)
        {
            //If nothing was passed, return null.
            if (null == target)
            {
                return null;
            }
            try
            {
                //Create a new instance of RobocodeInternalPermission from the passed object.
                var PassedPermission = (RobocodeInternalPermission) target;

                //If one class has an unrestricted value of false, then the
                //intersection will have an unrestricted value of false.
                //Return the passed class with the unrestricted value of false.
                if (!PassedPermission.unrestricted)
                {
                    return target;
                }
                //Return a copy of the current class if the passed one has
                //an unrestricted value of true.
                return Copy();
            }
                //Catch an InvalidCastException.
                //Throw ArgumentException to notify the user.
            catch (InvalidCastException)
            {
                throw new ArgumentException("Argument_WrongType", GetType().FullName);
            }
        }

        public override bool IsSubsetOf(IPermission target)
        {
            //If nothing was passed and unrestricted is false,
            //then return true. 
            if (null == target)
            {
                return !unrestricted;
            }
            try
            {
                //Create a new instance of RobocodeInternalPermission from the passed object.
                var passedpermission = (RobocodeInternalPermission) target;

                //If unrestricted has the same value in both objects, then
                //one is the subset of the other.
                return unrestricted == passedpermission.unrestricted;
            }
                //Catch an InvalidCastException.
                //Throw ArgumentException to notify the user.
            catch (InvalidCastException)
            {
                throw new ArgumentException("Argument_WrongType", GetType().FullName);
            }
        }

        public override void FromXml(SecurityElement PassedElement)
        {
            //Get the unrestricted value from the XML and initialize 
            //the current instance of unrestricted to that value.
            string element = PassedElement.Attribute("Unrestricted");

            if (null != element)
            {
                unrestricted = Convert.ToBoolean(element);
            }
        }

        public override SecurityElement ToXml()
        {
            //Encode the current permission to XML using the 
            //SecurityElement class.
            var element = new SecurityElement("IPermission");
            Type type = GetType();
            var AssemblyName = new StringBuilder(type.Assembly.ToString());
            AssemblyName.Replace('\"', '\'');
            element.AddAttribute("class", type.FullName + ", " + AssemblyName);
            element.AddAttribute("version", "1");
            element.AddAttribute("Unrestricted", unrestricted.ToString());
            return element;
        }
    }


    /// <exclude/>
    [AttributeUsageAttribute(AttributeTargets.All, AllowMultiple = true)]
    public class RobocodeInternalPermissionAttribute : CodeAccessSecurityAttribute
    {
        public RobocodeInternalPermissionAttribute(SecurityAction action)
            : base(action)
        {
            Unrestricted = false;
        }

        public override IPermission CreatePermission()
        {
            return Unrestricted
                       ? new RobocodeInternalPermission(PermissionState.Unrestricted)
                       : new RobocodeInternalPermission(PermissionState.None);
        }
    }
#pragma warning restore 1591
}