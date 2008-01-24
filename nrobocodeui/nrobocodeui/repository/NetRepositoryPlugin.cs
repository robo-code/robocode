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
using java.io;
using robocode.io;
using robocode.manager;
using robocode.repository;
using robocode.ui;

namespace nrobocodeui.repository
{
    public class NetRepositoryPlugin : IRepositoryPlugin
    {
        public string[] getExtensions()
        {
            return new string[]{".dll"};
        }

        public IFileSpecification createSpecification(RobotRepositoryManager repositoryManager, File file, File rootDir, string prefix, bool developmentVersion)
        {
            String filename = file.getName();
            String fileType = FileUtil.getFileType(filename);

            FileSpecification newSpec = null;
            if (fileType.Equals(".dll"))
            {
                newSpec = new NetAssemblySpecification(file, rootDir, prefix, developmentVersion);
                if (!newSpec.getValid())
                    return null;
                return newSpec;
            }

            return null;
        }

        public void setRobocodeManager(RobocodeManager rm)
        {
            // TODO ZAMO
        }
    }
}
