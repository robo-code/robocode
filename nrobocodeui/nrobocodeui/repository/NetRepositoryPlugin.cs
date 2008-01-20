using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;
using java.io;
using robocode;
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
