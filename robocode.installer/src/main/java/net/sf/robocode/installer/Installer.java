/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.installer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static net.sf.robocode.installer.OsUtil.*;

public final class Installer {

    private static boolean isSilent;

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
            System.setProperty("sun.java2d.noddraw", "true");
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.opengl", "false");
        }

        String message;
        if (install(new File(suggestedDirName))) {
            message = "Installation successful";
        } else {
            message = "Installation cancelled";
        }

        if (!isSilent) {
            JOptionPane.showMessageDialog(null, message);
        } else {
            System.out.println(message);
        }
    }

    private static boolean install(File suggestedDir) {
        if (JAVA_MAJOR_VERSION < 8) {
            String message = "Robocode requires Java 8 (1.8.0) or newer.\n"
                    + "Your system is currently running Java " + JAVA_MAJOR_VERSION + ".\n"
                    + "If you have not installed (or activated) at least\nJava 8, please do so.";
            if (!isSilent) {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.err.println(message);
            return false;
        }

        if (!isSilent) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ignore) {
            }
        } else {
            System.out.println("Headless installation into " + suggestedDir);
            System.out.println("See license at https://robocode.sourceforge.io/license/epl-v10.html");
        }

        File installDir;
        if (isSilent) {
            installDir = suggestedDir;
        } else {
            installDir = InstallerDialog.requestInstallDir(suggestedDir);
            if (installDir == null) {
                return false;
            }
        }

        if (!installDir.exists() && !installDir.mkdirs()) {
            System.err.println("Can't create dir: " + installDir);
            return false;
        }

        deleteOldLibs(installDir);

        InstallerDialog.ProgressDialog progress = null;
        Consumer<String> progressUpdater = null;
        if (!isSilent) {
            progress = InstallerDialog.showProgress();
            final InstallerDialog.ProgressDialog p = progress;
            progressUpdater = p::update;
        }

        try {
            Extractor.extract(installDir, progressUpdater);
        } catch (IOException e) {
            String message = "Installation failed: " + e;
            System.err.println(message);
            if (!isSilent) {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (progress != null) {
                progress.close();
            }
            return false;
        }

        if (progress != null) {
            progress.close();
        }

        ShortcutManager.create(installDir);
        cleanupInstallerClasses(installDir);

        return true;
    }

    private static void deleteOldLibs(File installDir) {
        File libs = new File(installDir, "libs");
        if (libs.exists()) {
            File[] files = libs.listFiles((dir, name) -> {
                String test = name.toLowerCase();
                return test.endsWith(".jar") || test.endsWith(".dll");
            });
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        System.err.println("Can't delete: " + f);
                    }
                }
            }
        }
    }

    private static void cleanupInstallerClasses(File installDir) {
        File installerDir = new File(installDir, "net/sf/robocode/installer");
        if (installerDir.exists()) {
            deleteRecursive(installerDir);
        }
    }

    private static void deleteRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteRecursive(f);
                }
                if (!f.delete()) {
                    System.err.println("Can't delete: " + f);
                }
            }
        }
        if (!dir.delete()) {
            System.err.println("Can't delete: " + dir);
        }
    }
}
