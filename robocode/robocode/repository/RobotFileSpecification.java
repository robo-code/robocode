/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Code cleanup
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode.repository;


import robocode.io.FileUtil;
import robocode.io.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
public class RobotFileSpecification extends FileSpecification implements IRobotFileSpecification {
    private final static String ROBOT_DESCRIPTION = "robot.description";
    private final static String ROBOT_AUTHOR_NAME = "robot.author.name";
    private final static String ROBOT_AUTHOR_EMAIL = "robot.author.email";
    private final static String ROBOT_AUTHOR_WEBSITE = "robot.author.website";
    private final static String ROBOT_JAVA_SOURCE_INCLUDED = "robot.java.source.included";
    private final static String ROBOT_VERSION = "robot.version";
    private final static String ROBOT_CLASSNAME = "robot.classname";
    private final static String ROBOT_WEBPAGE = "robot.webpage";

    private String uid = "";

    protected boolean robotJavaSourceIncluded;
    protected String robotClassPath;

    private boolean isJuniorRobot;
    private boolean isInteractiveRobot;
    private boolean isAdvancedRobot;
    private boolean isTeamRobot;
    private boolean isDroid;

    // Used in RobotRepositoryManager
    protected RobotFileSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
        valid = true;
        String filename = f.getName();
        String filepath = f.getPath();
        String fileType = FileUtil.getFileType(filename);

        this.developmentVersion = developmentVersion;
        this.rootDir = rootDir;
        if (prefix.length() == 0 && fileType.equals(".jar")) {
            throw new RuntimeException("Robot Specification can only be constructed from a .class file");
        } else if (fileType.equals(".team")) {
            throw new RuntimeException("Robot Specification can only be constructed from a .class file");
        } else if (fileType.equals(".java")) {
            loadProperties(filepath);
            setName(prefix + FileUtil.getClassName(filename));
            setRobotClassPath(rootDir.getPath());
        } else if (fileType.equals(".class")) {
            loadProperties(filepath);
            setName(prefix + FileUtil.getClassName(filename));
            setRobotClassPath(rootDir.getPath());
            if (isDevelopmentVersion()) {
                String jfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".java";
                File jf = new File(jfn);

                if (jf.exists()) {
                    setRobotJavaSourceIncluded(true);
                }
            }
        } else if (fileType.equals(".properties")) {
            loadProperties(filepath);
            setName(prefix + FileUtil.getClassName(filename));
            setRobotClassPath(rootDir.getPath());
        }
    }

    private void loadProperties(String filepath) {
        // Load properties if they exist...
        String pfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".properties";
        File pf = new File(pfn);

        FileInputStream in = null;

        try {
            if (pf.exists()) {
                in = new FileInputStream(pf);

                load(in);
                in.close();
                if (pf.length() == 0) {
                    setRobotVersion("?");
                    if (!developmentVersion) {
                        valid = false;
                    }
                }
            } // Don't accept robots in robotcache without .properties file
            else if (!developmentVersion) {
                valid = false;
            }
        } catch (IOException e) {
            // Oh well.
            Logger.log("Warning:  Could not load properties file: " + pfn);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        setThisFileName(pfn);
        String htmlfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".html";
        File htmlFile = new File(htmlfn);

        if (htmlFile.exists() && (getWebpage() == null || getWebpage().toString().length() == 0)) {
            try {
                setRobotWebpage(htmlFile.toURL());
            } catch (MalformedURLException e) {
                setRobotWebpage(null);
            }
        }
        File classFile = new File(filepath.substring(0, filepath.lastIndexOf(".")) + ".class");

        if (classFile.exists()) {
            setFileLastModified(classFile.lastModified());
            setFileLength(classFile.length());
            setFileType(".class");
            try {
                setFilePath(classFile.getCanonicalPath());
            } catch (IOException e) {
                Logger.log("Warning:  Unable to determine canonical path for " + classFile.getPath());
                setFilePath(classFile.getPath());
            }
            setFileName(classFile.getName());
        }
    }

    @Override
    public void load(FileInputStream in) throws IOException {
        super.load(in);
        authorEmail = props.getProperty(ROBOT_AUTHOR_EMAIL);
        authorName = props.getProperty(ROBOT_AUTHOR_NAME);
        authorWebsite = props.getProperty(ROBOT_AUTHOR_WEBSITE);
        description = props.getProperty(ROBOT_DESCRIPTION);
        version = props.getProperty(ROBOT_VERSION);
        name = props.getProperty(ROBOT_CLASSNAME);
        String w = props.getProperty(ROBOT_WEBPAGE);

        if (w == null) {
            webpage = null;
        } else if (w.length() == 0) {
            webpage = null;
        } else {
            try {
                webpage = new URL(w);
            } catch (MalformedURLException e) {
                try {
                    webpage = new URL("http://" + w);
                } catch (MalformedURLException e2) {
                    webpage = null;
                }
            }
        }
        robotJavaSourceIncluded = Boolean.valueOf(props.getProperty(ROBOT_JAVA_SOURCE_INCLUDED, "false"));
    }

    /**
     * Sets the robotName.
     *
     * @param name The robotName to set
     */
    public void setName(String name) {
        this.name = name;
        props.setProperty(ROBOT_CLASSNAME, name);
    }

    /**
     * Sets the robotDescription.
     *
     * @param robotDescription The robotDescription to set
     */
    public void setRobotDescription(String robotDescription) {
        this.description = robotDescription;
        props.setProperty(ROBOT_DESCRIPTION, robotDescription);
    }

    /**
     * Sets the robotAuthorName.
     *
     * @param robotAuthorName The robotAuthorName to set
     */
    public void setRobotAuthorName(String robotAuthorName) {
        this.authorName = robotAuthorName;
        props.setProperty(ROBOT_AUTHOR_NAME, robotAuthorName);
    }

    /**
     * Sets the robotAuthorEmail.
     *
     * @param robotAuthorEmail The robotAuthorEmail to set
     */
    public void setRobotAuthorEmail(String robotAuthorEmail) {
        this.authorEmail = robotAuthorEmail;
        props.setProperty(ROBOT_AUTHOR_EMAIL, robotAuthorEmail);
    }

    /**
     * Sets the robotAuthorWebsite.
     *
     * @param robotAuthorWebsite The robotAuthorWebsite to set
     */
    public void setRobotAuthorWebsite(String robotAuthorWebsite) {
        this.authorWebsite = robotAuthorWebsite;
        props.setProperty(ROBOT_AUTHOR_WEBSITE, robotAuthorWebsite);
    }

    /**
     * Gets the robotJavaSourceIncluded.
     *
     * @return Returns a boolean
     */
    public boolean getRobotJavaSourceIncluded() {
        return robotJavaSourceIncluded;
    }

    public boolean getNeedsExternalLoader() {
        return false;
    }

    /**
     * Sets the robotJavaSourceIncluded.
     *
     * @param robotJavaSourceIncluded The robotJavaSourceIncluded to set
     */
    public void setRobotJavaSourceIncluded(boolean robotJavaSourceIncluded) {
        this.robotJavaSourceIncluded = robotJavaSourceIncluded;
        props.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, "" + robotJavaSourceIncluded);
    }

    /**
     * Sets the robotVersion.
     *
     * @param robotVersion The robotVersion to set
     */
    public void setRobotVersion(String robotVersion) {
        this.version = robotVersion;
        if (robotVersion != null) {
            props.setProperty(ROBOT_VERSION, robotVersion);
        } else {
            props.remove(ROBOT_VERSION);
        }
    }

    /**
     * Gets the robotClasspath.
     *
     * @return Returns a String
     */
    public String getRobotClassPath() {
        return robotClassPath;
    }

    /**
     * Sets the robotClasspath.
     *
     * @param robotClassPath The robotClasspath to set
     */
    public void setRobotClassPath(String robotClassPath) {
        this.robotClassPath = robotClassPath;
    }

    /**
     * Sets the robotWebpage.
     *
     * @param robotWebpage The robotWebpage to set
     */
    public void setRobotWebpage(URL robotWebpage) {
        this.webpage = robotWebpage;
        if (robotWebpage == null) {
            props.remove(ROBOT_WEBPAGE);
        } else {
            props.setProperty(ROBOT_WEBPAGE, this.webpage.toString());
        }
    }

    /**
     * Gets the uid.
     *
     * @return Returns a String
     */
    @Override
    public String getUid() {
        return uid;
    }

    /**
     * Sets the uid.
     *
     * @param uid The uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isDroid() {
        return isDroid;
    }

    public void setDroid(boolean value) {
        this.isDroid = value;
    }

    public boolean isTeamRobot() {
        return isTeamRobot;
    }

    public void setTeamRobot(boolean value) {
        this.isTeamRobot = value;
    }

    public boolean isAdvancedRobot() {
        return isAdvancedRobot;
    }

    public void setAdvancedRobot(boolean value) {
        this.isAdvancedRobot = value;
    }

    public boolean isInteractiveRobot() {
        return isInteractiveRobot;
    }

    public void setInteractiveRobot(boolean value) {
        this.isInteractiveRobot = value;
    }

    public boolean isJuniorRobot() {
        return isJuniorRobot;
    }

    public void setJuniorRobot(boolean value) {
        this.isJuniorRobot = value;
    }

}
