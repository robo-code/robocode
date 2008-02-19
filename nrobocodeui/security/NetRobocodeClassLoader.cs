// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using System.IO;
using System.Reflection;
using java.lang;
using nrobocodeui.repository;
using robocode.manager;
using robocode.peer.robot;
using robocode.repository;
using robocode.security;

namespace nrobocodeui.security
{
    public class NetRobocodeClassLoader : ClassLoader, IRobocodeClassLoader
    {
        private RobotClassManager robotClassManager;
        
        public void init(RobotClassManager rcm)
        {
            robotClassManager = rcm;
        }

        public bool couldLoad(IRobotFileSpecification irs)
        {
            return (irs is NetAssemblySpecification);
        }

        public Class loadRobotClass(string str, bool b)
        {
            IRobotFileSpecification specification = robotClassManager.getRobotSpecification();

            //TODO ZAMO security
            Assembly assembly = Assembly.LoadFile(specification.getFilePath());
            Type type = assembly.GetType(specification.getFullClassName());
            Class clazz2 = ikvm.runtime.Util.getClassFromTypeHandle(type.TypeHandle);

            robotClassManager.setUid(type.AssemblyQualifiedName);

            return clazz2;
        }

        public new Class loadClass(string name, bool resolve)
        {
            return base.loadClass(name, resolve);
        }

        public void cleanup()
        {
            throw new NotImplementedException();
        }

        public string getRootPackageDirectory()
        {
            throw new NotImplementedException();
        }

        public string getClassDirectory()
        {
            return Path.GetDirectoryName(robotClassManager.getRobotSpecification().getFilePath());
        }

        public string getRootDirectory()
        {
            throw new NotImplementedException();
        }

        public void setRobocodeManager(RobocodeManager rm)
        {
        }
    }
}
