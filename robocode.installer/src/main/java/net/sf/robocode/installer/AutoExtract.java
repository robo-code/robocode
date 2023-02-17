/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.installer;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;


/**
 * Installer for Robocode.
 *
 * @author Mathew A. Nelsen (original)
 * @author Flemming N. Larsen (contributor)
 */
public class AutoExtract implements ActionListener {
    private JDialog licenseDialog;
    private boolean accepted;
    private static final String[] SPINNER = {"-", "\\", "|", "/"};
    private String message = "";
    private static File installDir;
    private static final String osName = System.getProperty("os.name");
    private static final double osVersion = doubleValue(System.getProperty("os.version"));
    private static final int JAVA_MAJOR_VERSION = javaMajorVersion();

    private static double doubleValue(String s) {
        s = s.replaceAll("[^.0-9]", ""); // Remove invalid characters, e.g. "3.0-ARCH" become "3.0"

        int p = s.indexOf(".");

        if (p >= 0) {
            p = s.indexOf(".", p + 1);
        }
        if (p >= 0) {
            s = s.substring(0, p);
        }

        double d = 0.0;

        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            e.printStackTrace(System.err);
        }

        return d;
    }

    private boolean acceptLicense() {
        StringBuffer licenseText = new StringBuffer();

        InputStream is;

        JarFile extractJar = null;
        try {
            extractJar = new JarFile("extract.jar");

            is = extractJar.getInputStream(extractJar.getJarEntry("license/cpl-v10.html"));
        } catch (IOException e) {
            return true;
        } finally {
            if (extractJar != null) {
                try {
                    extractJar.close();
                } catch (IOException ignore) {
                }
            }
        }
        if (is == null || isSilent) {
            return true;
        }

        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = br.readLine();

            while (line != null) {
                licenseText.append(line);
                line = br.readLine();
            }
            return acceptReject(licenseText.toString());

        } catch (IOException e) {
            System.err.println("Could not read line from license file: " + e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean acceptReject(String text) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        licenseDialog = new JDialog();
        licenseDialog.setTitle("License Agreement");
        licenseDialog.setModal(true);
        licenseDialog.setLocation((screenSize.width - 500) / 2, (screenSize.height - 400) / 2);
        licenseDialog.setSize(500, 400);
        JTextPane t = new JTextPane();

        t.setContentType("text/html");
        t.setText(text);
        t.setFont(new Font("Dialog", Font.PLAIN, 12));
        t.setEditable(false);

        JScrollPane s = new JScrollPane();

        s.setViewportView(t);

        licenseDialog.getContentPane().setLayout(new BorderLayout());
        licenseDialog.getContentPane().add(s, BorderLayout.CENTER);

        JPanel p = new JPanel();

        p.setLayout(new BorderLayout());
        JButton b1 = new JButton("Accept");
        JButton b2 = new JButton("Cancel");

        p.add(b1, BorderLayout.WEST);
        p.add(b2, BorderLayout.EAST);

        b1.addActionListener(this);
        b2.addActionListener(this);

        licenseDialog.getContentPane().add(p, BorderLayout.SOUTH);

        licenseDialog.setVisible(true);

        return accepted;
    }

    public void actionPerformed(ActionEvent e) {
        accepted = e.getActionCommand().equals("Accept");
        licenseDialog.dispose();
        licenseDialog = null;
    }

    private boolean extract(File installDir) {
        FileOutputStream fos;
        String entryName;

        byte[] buf = new byte[2048];

        String name = AutoExtract.class.getName().replaceAll("\\.", "/") + ".class";
        String urlJar = AutoExtract.class.getClassLoader().getResource(name).toString();
        String src = urlJar.substring("jar:file:/".length(), urlJar.indexOf("!/"));

        if (src.indexOf('!') > -1) {
            message = src + "\nContains an exclamation point.  Please move the file to a different directory.";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        JarInputStream jarIS = null;

        JDialog statusDialog = null;
        JLabel status = null;
        if (!isSilent) {
            statusDialog = new JDialog();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int width = 500;
            int height = 100;
            statusDialog.setTitle("Installing");
            statusDialog.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
            statusDialog.setSize(width, height);
            status = new JLabel();

            statusDialog.getContentPane().setLayout(new BorderLayout());
            statusDialog.getContentPane().add(status, BorderLayout.CENTER);

            statusDialog.setVisible(true);
        }

        try {
            URL url = new URL("file:/" + src);
            InputStream is = url.openStream();

            jarIS = new JarInputStream(is);

            JarEntry entry;

            while ((entry = jarIS.getNextJarEntry()) != null) {
                int spin = 0;

                entryName = entry.getName();
                if (entry.isDirectory()) {
                    if (!entryName.startsWith("net") && !(entryName.equals("desktop") && isFreeBSD())) {
                        File dir = new File(installDir, entry.getName());
                        if (!dir.exists() && !dir.mkdirs()) {
                            System.err.println("Can't create dir: " + dir);
                        }
                    }
                } else if (!entryName.equals(name)) {

                    // Skip .bat, .sh, and .command files depending on which OS Robocode is installed

                    String entryNameLC = entryName.toLowerCase();

                    boolean skipEntry = false;

                    boolean isBatFile = entryNameLC.length() > ".bat".length() && entryNameLC.endsWith(".bat");
                    boolean isShFile = entryNameLC.length() > ".sh".length() && entryNameLC.endsWith(".sh");
                    boolean isCommandFile = entryNameLC.length() > ".command".length()
                            && entryNameLC.endsWith(".command");
                    boolean isDesktopFile = entryNameLC.startsWith("desktop/");

                    // Unix systems and Mac OS X
                    if (File.separatorChar == '/') {
                        // Skip .bat files under Unix and Mac OS X
                        // Skip .command files under Unix
                        skipEntry = isBatFile || (isCommandFile && !isMacOSX()) || (isDesktopFile && !isFreeBSD());
                    } else {
                        // Under Windows the .sh and .command files are skipped
                        skipEntry = isShFile || isCommandFile || isDesktopFile;
                    }

                    // If we are not skipping the entry, then copy from our .jar into the installation dir
                    if (!skipEntry) {
                        if (status != null) {
                            status.setText(entryName + " " + SPINNER[spin++]);
                        }

                        File outputFile = new File(installDir, entryName);

                        File parentDirectory = new File(outputFile.getParent());
                        if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
                            System.err.println("Can't create dir: " + parentDirectory);
                        }
                        fos = new FileOutputStream(outputFile);

                        int bytes = 0;
                        int count = 0;
                        int num;

                        while ((num = jarIS.read(buf, 0, 2048)) != -1) {
                            fos.write(buf, 0, num);
                            bytes += num;
                            count++;
                            if (count > 80) {
                                if (status != null) {
                                    status.setText(entryName + " " + SPINNER[spin++] + " (" + bytes + " bytes)");
                                }
                                if (spin > 3) {
                                    spin = 0;
                                }
                                count = 0;
                            }
                        }
                        fos.close();

                        if (status != null) {
                            status.setText(entryName + " " + SPINNER[spin++] + " (" + bytes + " bytes)");
                        }

                        if (isShFile || isCommandFile) {
                            String filePath = outputFile.getAbsolutePath();

                            // Remove ^M (MS DOS) characters, if they exists
                            dos2unix(filePath);

                            // Set file permissions for .sh and .command files under Unix and Mac OS X
                            if (File.separatorChar == '/') {
                                // Grant read and execute access for everyone and also write access for the owner of the file
                                Runtime.getRuntime().exec("chmod 755 " + filePath);
                            }
                        }
                    }
                }
            }
            if (statusDialog != null) {
                statusDialog.dispose();
            }
            return true;
        } catch (IOException e) {
            message = "Installation failed: " + e;
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (jarIS != null) {
                try {
                    jarIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isSilent = false;

    public static void main(String[] argv) {
        String suggestedDirName;
        if (argv.length >= 1) {
            suggestedDirName = argv[0];
        } else if (isWindowsOS()) {

            suggestedDirName = System.getenv("SystemDrive") + "\\robocode";
        } else {
            suggestedDirName = System.getProperty("user.home") + File.separator + "robocode" + File.separator;
        }
        if (argv.length >= 2 && "silent".equals(argv[1])) {
            isSilent = true;
        } else if (GraphicsEnvironment.isHeadless()) {
            isSilent = true;
        }

        if (!isSilent) {
            // Fix issue with rendering issues on Windows
            System.setProperty("sun.java2d.noddraw", "true"); // disable DirectDraw -> all operations are performed with GDI
            System.setProperty("sun.java2d.d3d", "false");    // disable the Direct3D pipeline
            System.setProperty("sun.java2d.opengl", "false"); // disable the OpenGL pipeline
        }

        String message;

        if (install(new File(suggestedDirName))) {
            message = "Installation successful";
        } else {
            message = "Installation cancelled";
        }

        // Delete the class file with the installer, and it's parent folders in the robocode home dir
        if (installDir != null) {
            String installerPath = AutoExtract.class.getName().replaceAll("\\.", "/") + "$1.class";

            deleteFileAndParentDirsIfEmpty(new File(installDir, installerPath));
        }

        if (!isSilent) {
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private static boolean install(File suggestedDir) {
        // Verify that the Java version is version 6 (1.6.0) or newer
        if (JAVA_MAJOR_VERSION < 8) {
            String message = "Robocode requires Java 8 (1.8.0) or newer.\n"
                    + "Your system is currently running Java " + JAVA_MAJOR_VERSION + ".\n"
                    + "If you have not installed (or activated) at least\n" + "Java 8, please do so.";

            if (!isSilent) {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.err.println(message);
            return false;
        }

        // Set native look and feel
        if (!isSilent) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable t) {// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
            }
        } else {
            System.out.println("Headless installation into " + suggestedDir);
            System.out.println("See license at https://robocode.sourceforge.io/license/epl-v10.html");
        }
        AutoExtract extractor = new AutoExtract();

        if (extractor.acceptLicense()) {
            boolean done = false;

            while (!done) {
                int rc = isSilent
                        ? JOptionPane.YES_OPTION
                        : JOptionPane.showConfirmDialog(null,
                        "Robocode will be installed in:\n" + suggestedDir + "\nIs this ok?", "Installing Robocode",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (rc == JOptionPane.YES_OPTION) {
                    installDir = suggestedDir;
                    done = true;
                } else if (rc == JOptionPane.NO_OPTION) {
                    Object r = JOptionPane.showInputDialog(null, "Please type in the installation directory",
                            "Installation Directory", JOptionPane.PLAIN_MESSAGE, null, null, suggestedDir);

                    if (r != null) {
                        suggestedDir = new File(((String) r).trim());
                    } else {
                        return false;
                    }
                } else if (rc == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
            }
            if (!installDir.exists() && !installDir.mkdirs()) {
                System.err.println("Can't create dir: " + installDir);
                return false;
            }
            deleteOldLibs(installDir);

            // The .robotcache has been renamed to .data
            File robotsCacheDir = new File(installDir, "robots/.robotcache");
            File robotsDataDir = new File(installDir, "robots/.data");

            if (robotsCacheDir.exists()) {
                // Rename ".robotcache" into ".data"
                robotsCacheDir.renameTo(robotsDataDir);
            }

            // Fix problem with .data starting with a underscore dir by
            // renaming files containing ".data/_" into ".data"
            if (robotsDataDir.exists()) {
                File underScoreDir = new File(robotsDataDir, "_");
                String[] list = underScoreDir.list();

                if (list != null) {
                    for (String fileName : list) {
                        File file = new File(underScoreDir, fileName);

                        file.renameTo(new File(robotsDataDir, fileName));
                    }
                    underScoreDir.delete();
                }
            }

            // Create shortcuts and file associations
            if (extractor.extract(installDir)) {
                extractor.createShortcuts(installDir, "robocode.bat", "Robocode", "Robocode");
                if(!isSilent){
                    extractor.createFileAssociations(installDir);
                }
                return true;
            }
        }
        return false;
    }

    private static void deleteOldLibs(File installDir) {
        File libs = new File(installDir, "libs");

        if (libs.exists()) {
            File[] del = libs.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    String test = name.toLowerCase();

                    return test.endsWith(".jar") || test.endsWith(".dll");
                }
            });

            for (File d : del) {
                if (!d.delete()) {
                    System.err.println("Can't delete: " + d);
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    // Skip directories ending with ".data"
                    if (file.getName().endsWith(".data")) {
                        continue;
                    }
                    try {
                        // Test for symlink and ignore.
                        // Robocode won't create one, but just in case a user does...
                        if (file.getCanonicalFile().getParentFile().equals(dir.getCanonicalFile())) {
                            deleteDir(file);
                            if (file.exists() && !file.delete()) {
                                System.err.println("Can't delete: " + file);
                            }
                        } else {
                            System.out.println("Warning: " + file + " may be a symlink. It has been ignored");
                        }
                    } catch (IOException e) {
                        System.out.println(
                                "Warning: Cannot determine canonical file for " + file + ". It has been ignored");
                    }
                } else {
                    if (file.exists() && !file.delete()) {
                        System.err.println("Can't delete: " + file);
                    }
                }
            }
            return dir.delete();
        }
        return false;
    }

    private void createShortcuts(File installDir, String runnable, String folder, String name) {
        if (osName.toLowerCase().indexOf("win") == 0) {
            if (createWindowsShortcuts(installDir, runnable, folder, name) == false) {
                JOptionPane.showMessageDialog(null,
                        message + "\n" + "To start Robocode, enter the following at a command prompt:\n" + "cd "
                                + installDir.getAbsolutePath() + "\n" + "robocode.bat");
            }
        } else if (osName.toLowerCase().indexOf("mac") == 0) {
            if (osVersion >= 10.1) {
                JOptionPane.showMessageDialog(null,
                        message + "\n" + "To start Robocode, browse to " + installDir + " then double-click robocode.sh\n");
            } else {
                JOptionPane.showMessageDialog(null,
                        message + "\n" + "To start Robocode, enter the following at a command prompt:\n"
                                + installDir.getAbsolutePath() + "/robocode.sh");
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    message + "\n" + "To start Robocode, enter the following at a command prompt:\n"
                            + installDir.getAbsolutePath() + "/robocode.sh");
        }
    }

    private boolean createWindowsShortcuts(File installDir, String runnable, String folder, String name) {
        int rc = isSilent ? JOptionPane.YES_NO_OPTION
                : JOptionPane.showConfirmDialog(null,
                "Would you like to install a shortcut to Robocode in the Start menu? (Recommended)", "Create Shortcuts",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (rc != JOptionPane.YES_OPTION) {
            return false;
        }

        String command = getWindowsCmd() + " cscript.exe ";

        try {
            File shortcutMaker = new File(installDir, "makeshortcut.vbs");
            PrintStream out = new PrintStream(new FileOutputStream(shortcutMaker));

            out.println("WScript.Echo(\"Creating shortcuts...\")");
            out.println("Set Shell = CreateObject (\"WScript.Shell\")");
            out.println("Set fso = CreateObject(\"Scripting.FileSystemObject\")");
            out.println("ProgramsPath = Shell.SpecialFolders(\"Programs\")");
            out.println("if (not(fso.folderExists(ProgramsPath + \"\\\\" + folder + "\"))) Then");
            out.println("	fso.CreateFolder(ProgramsPath + \"\\\\" + folder + "\")");
            out.println("End If");
            out.println("Set link = Shell.CreateShortcut(ProgramsPath + \"\\\\" + folder + "\\\\" + name + ".lnk\")");
            out.println("link.Arguments = \"\"");
            out.println("link.Description = \"" + name + "\"");
            out.println("link.HotKey = \"\"");
            out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\"");
            out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\"");
            out.println("link.WindowStyle = 1");
            out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\"");
            out.println("link.Save()");
            out.println("DesktopPath = Shell.SpecialFolders(\"Desktop\")");
            out.println("Set link = Shell.CreateShortcut(DesktopPath + \"\\\\" + name + ".lnk\")");
            out.println("link.Arguments = \"\"");
            out.println("link.Description = \"" + name + "\"");
            out.println("link.HotKey = \"\"");
            out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\"");
            out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\"");
            out.println("link.WindowStyle = 1");
            out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\"");
            out.println("link.Save()");
            out.println("WScript.Echo(\"Shortcuts created.\")");

            out.close();

            Process p = Runtime.getRuntime().exec(command + " makeshortcut.vbs", null, installDir);
            int rv = p.waitFor();

            if (rv != 0) {
                System.err.println("Can't create shortcut: " + shortcutMaker);
                return false;
            }
            if (!isSilent) {
                JOptionPane.showMessageDialog(null,
                        message + "\n" + "A Robocode program group has been added to your Start menu\n"
                                + "A Robocode icon has been added to your desktop.");
            }
            if (!shortcutMaker.delete()) {
                System.err.println("Can't delete: " + shortcutMaker);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return false;
    }

    private void createFileAssociations(File installDir) {
        if (isWindowsOS()) {
            createWindowsFileAssociations(installDir);
        }
    }

    private void createWindowsFileAssociations(File installDir) {
        int rc = JOptionPane.showConfirmDialog(null,
                "Would you like to create file associations for Robocode in\n"
                        + "the Windows Registry for the file extensions .battle and .br?\n"
                        + "Please notice that you might need to grant permission to add\n"
                        + "the file associations in the Registry, and you must be an\n"
                        + "administrator or granted permission to change the registry.",
                "Create File Associations",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (rc != JOptionPane.YES_OPTION) {
            return;
        }

        File file = null;
        PrintStream out = null;

        try {
            file = new File(installDir + "\\FileAssoc.reg");

            out = new PrintStream(new FileOutputStream(file));

            String installPath = installDir.getAbsolutePath();

            out.print(
                    createWindowsRegistryFileAssociation(installPath, ".battle", "Robocode.BattleSpecification",
                            "Robocode Battle Specification", "-battle"));
            out.print(
                    createWindowsRegistryFileAssociation(installPath, ".br", "Robocode.BattleRecord", "Robocode Battle Record",
                            "-replay"));

            out.close();
            out = null;

            Process p = Runtime.getRuntime().exec(getWindowsCmd() + file.getAbsolutePath(), null, null);
            int rv;

            try {
                rv = p.waitFor();
                if (rv != 0) {
                    System.err.println("Could not create association(s)");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (file != null) {
                if (!file.delete()) {
                    System.err.println("Could not delete the file: " + file);
                }
            }
        }
    }

    private static String createWindowsRegistryFileAssociation(String installDir, String fileExt, String progId, String description, String robocodeCmdParam) {
        StringBuffer sb = new StringBuffer();

        String HKCR = "[HKEY_CLASSES_ROOT\\";

        sb.append("REGEDIT4\n");
        sb.append(HKCR).append(fileExt).append("]\n");
        sb.append("@=\"").append(progId).append("\"\n");
        sb.append(HKCR).append(progId).append("]\n");
        sb.append("@=\"").append(description).append("\"\n");
        sb.append(HKCR).append(progId).append("\\shell]\n");
        sb.append(HKCR).append(progId).append("\\shell\\open]\n");
        sb.append(HKCR).append(progId).append("\\shell\\open\\command]\n");
        sb.append("@=\"").append(getWindowsCmd()).append("\\\"cd \\\"").append(installDir.replaceAll("[\\\\]", "\\\\\\\\")).append("\\\" && robocode.bat ").append(robocodeCmdParam).append(
                " \\\"%1\\\"\\\"\"\n");

        return sb.toString();
    }

    private static String escaped(String s) {
        StringBuffer eascaped = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                eascaped.append('\\');
            }
            eascaped.append(s.charAt(i));
        }
        return eascaped.toString();
    }

    private static boolean isWindowsOS() {
        return osName.startsWith("Windows ");
    }

    private static boolean isMacOSX() {
        return osName.startsWith("Mac OS X");
    }

    private static boolean isFreeBSD() {
        return osName.equals("FreeBSD");
    }

    private static String getWindowsCmd() {
        String os = System.getProperty("os.name");

        return ((os.equals("Windows 95") || os.equals("Windows 98") || os.equals("Windows ME"))
                ? "command.com"
                : "cmd.exe")
                + " /C ";
    }

    /**
     * Deletes a file and afterwards deletes the parent directories that are empty.
     *
     * @param file the file or directory to delete
     * @return true if success
     */
    private static boolean deleteFileAndParentDirsIfEmpty(File file) {
        boolean wasDeleted = false;

        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                wasDeleted = deleteDir(file);
            } else {
                wasDeleted = file.delete();

                File parent = file;

                while (wasDeleted && (parent = parent.getParentFile()) != null) {
                    // Delete parent directory, but only if it is empty
                    File[] files = parent.listFiles();

                    if (files != null && files.length == 0) {
                        wasDeleted = deleteDir(parent);
                    } else {
                        wasDeleted = false;
                    }
                }
            }
        }
        return wasDeleted;
    }

    private static int javaMajorVersion() {
        String version = System.getProperty("java.version");

        String major;

        if (version.startsWith("1.")) {
            major = version.substring(2, version.lastIndexOf('.'));
        } else {
            int index = version.indexOf('.');
            if (index > 0) {
                major = version.substring(0, index);
            } else {
                index = version.indexOf('-');
                if (index > 0) {
                    major = version.substring(0, index);
                } else {
                    major = version;
                }
            }
        }
        return Integer.parseInt(major);
    }

    private static void dos2unix(String filepath) throws IOException {

        File tempFile = File.createTempFile("robocode", null);
        FileWriter fileWriter = null;

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileWriter = new FileWriter(tempFile);

            fileReader = new FileReader(filepath);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replaceAll("\r", "");

                fileWriter.write(line);
                fileWriter.write('\n');
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignore) {
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ignore) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
        Files.move(tempFile.toPath(), Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
    }
}
