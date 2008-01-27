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
using java.net;
using robocode;
using nrobocode.robot;
using robocode.io;
using robocode.repository;
using robocode.robotinterfaces;
using File=java.io.File;
using IOException=java.io.IOException;
using String=System.String;

namespace nrobocodeui.repository
{
    public class NetAssemblySpecification : FileSpecification, IRobotSpecification
    {
	    private static String ROBOT_DESCRIPTION = "robot.description";
	    private static String ROBOT_AUTHOR_NAME = "robot.author.name";
	    private static String ROBOT_AUTHOR_EMAIL = "robot.author.email";
	    private static String ROBOT_AUTHOR_WEBSITE = "robot.author.website";
	    private static String ROBOT_JAVA_SOURCE_INCLUDED = "robot.java.source.included";
	    private static String ROBOT_VERSION = "robot.version";
	    private static String ROBOT_CLASSNAME = "robot.classname";
	    private static String ROBOT_WEBPAGE = "robot.webpage";
        private static String ROBOT_NAME = "robot.name";

        protected bool robotJavaSourceIncluded;
        protected String robotClassPath;
        private bool teamRobot;
        private bool droid;

        public NetAssemblySpecification(File file, java.io.File rootDir, String prefix, bool developmentVersion)
        {
            valid = true;
            String filename = file.getName();
            String filepath = file.getPath();
            String fileType = FileUtil.getFileType(filename);

            this.developmentVersion = false;
            this.rootDir = rootDir;

            try
            {
                setFilePath(file.getCanonicalPath());
            }
            catch (IOException e)
            {
                Logger.log("Warning:  Unable to determine canonical path for " + file.getPath() +  e.toString());
                setFilePath(file.getPath());
            }
            setFileLastModified(file.lastModified());
            setFileLength(file.length());
            setFileType(fileType);
            setFileName(file.getName());
            setName(prefix + FileUtil.getClassName(filename));
            setRobotClassPath(Path.GetDirectoryName(file.getPath()));
            valid = loadProperties(filepath, prefix);
        }

        private bool loadProperties(string filepath, string prefix)
        {
            Assembly assembly = Assembly.LoadFile(filepath);

            //guess default name
            name =  prefix + Path.GetFileNameWithoutExtension(filepath);
            Type type = null;

            object[] attributes = assembly.GetCustomAttributes(false);
            foreach (Attribute attribute in attributes)
            {
                if (attribute is AuthorNameAttribute)
                {
                    authorName = (attribute as AuthorNameAttribute).AuthorName;
                    props.setProperty(ROBOT_AUTHOR_NAME, authorName);
                }
                else if (attribute is AuthorEmailAttribute)
                {
                    authorEmail = (attribute as AuthorEmailAttribute).AuthorEmail;
                    props.setProperty(ROBOT_AUTHOR_EMAIL, name);
                }
                else if (attribute is AuthorWebSiteAttribute)
                {
                    authorWebsite = (attribute as AuthorWebSiteAttribute).AuthorWebSite;
                    props.setProperty(ROBOT_AUTHOR_WEBSITE, name);
                }
                else if (attribute is NameAttribute)
                {
                    NameAttribute na = attribute as NameAttribute;
                    name = na.Name;
                    type = na.Type;
                    props.setProperty(ROBOT_NAME, name);
                }
                else if (attribute is DescriptionAttribute)
                {
                    description = (attribute as DescriptionAttribute).Description;
                    props.setProperty(ROBOT_DESCRIPTION, description);
                }
                else if (attribute is SourceIncludedAttribute)
                {
                    robotJavaSourceIncluded = (attribute as SourceIncludedAttribute).SourceIncluded;
                    props.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, robotJavaSourceIncluded.ToString().ToLower());
                }
                else if (attribute is VersionAttribute)
                {
                    Version vversion = (attribute as VersionAttribute).Version;
                    version = vversion.ToString();
                    props.setProperty(ROBOT_VERSION, version);
                }
                else if (attribute is WebPageAttribute)
                {
                    string url = (attribute as WebPageAttribute).webPage;
                    this.webpage = new URL(url);
                    props.setProperty(ROBOT_WEBPAGE, url);
                }
            }

            //try default
            if (type==null)
                type = assembly.GetType(name);

            if (type == null)
                return false;

            if (!typeof(IRobotBase).IsAssignableFrom(type))
                return false;
            return true;
        }

        public void setRobotClassPath(String robotClassPath)
        {
            this.robotClassPath = robotClassPath;
        }

        public void setRobotJavaSourceIncluded(bool robotJavaSourceIncluded)
        {
            this.robotJavaSourceIncluded = robotJavaSourceIncluded;
            props.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, "" + robotJavaSourceIncluded);
        }

        public void setRobotAuthorName(string str)
        {
            throw new NotImplementedException();
        }

        public void setRobotWebpage(URL url)
        {
            throw new NotImplementedException();
        }

        public void setRobotVersion(string str)
        {
            throw new NotImplementedException();
        }

        public void setName(String name)
        {
            this.name = name;
            props.setProperty(ROBOT_CLASSNAME, name);
        }

        public override string getUid()
        {
            return getFilePath();
        }

        public void setUid(string str)
        {
            //TODO ZAMO
        }

        public void setDroid(bool droid)
        {
            this.droid = droid;
        }

        public bool isDroid()
        {
            return droid;
        }

        public string getRobotClassPath()
        {
            return robotClassPath;
        }

        public void setTeamRobot(bool b)
        {
            teamRobot = b;
        }

        public bool getRobotJavaSourceIncluded()
        {
            return robotJavaSourceIncluded;
        }

        public bool getNeedsExternalLoader()
        {
            return robotJavaSourceIncluded;
        }

        public void setRobotDescription(string str)
        {
            throw new NotImplementedException();
        }

        public int CompareTo(object obj)
        {
            //case sensitive compare to
            return base.compareTo(obj);
        }
    }
}
