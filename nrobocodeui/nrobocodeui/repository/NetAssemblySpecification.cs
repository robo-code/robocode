﻿using System;
using System.IO;
using System.Reflection;
using java.lang;
using java.net;
using robocode;
using nrobocode.robot;
using robocode.io;
using robocode.repository;
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
                Logger.log("Warning:  Unable to determine canonical path for " + file.getPath());
                setFilePath(file.getPath());
            }
            setFileLastModified(file.lastModified());
            setFileLength(file.length());
            setFileType(fileType);
            setFileName(file.getName());
            setName(prefix + FileUtil.getClassName(filename));
            setRobotClassPath(Path.GetDirectoryName(file.getPath()));
            valid = loadProperties(filepath);
        }

        private bool loadProperties(String filepath)
        {
            Assembly assembly = Assembly.LoadFile(filepath);
            Type type = assembly.GetType(name);
            if (type==null)
                return false;

            if (!type.IsSubclassOf(typeof(_RobotBase)))
                return false;

            object[] attributes = type.GetCustomAttributes(false);
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
                    string lname = (attribute as NameAttribute).Name;
                    props.setProperty(ROBOT_NAME, lname);
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

        /*public int CompareTo(object obj)
        {
            IFileSpecification other = obj as IFileSpecification;
            if (other==null)
                return -1;
            other.
        }*/

        public int CompareTo(object obj)
        {
            return base.compareTo(obj);
        }
    }
}
