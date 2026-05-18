/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.installer;

import java.io.*;

import static net.sf.robocode.installer.OsUtil.*;

public final class ShortcutManager {

    private ShortcutManager() {
    }

    public static void create(File installDir) {
        if (isWindowsOS()) {
            createWindows(installDir);
        } else if (isMacOSX()) {
            System.out.println("To start Robocode, browse to " + installDir.getAbsolutePath() + " then double-click robocode.sh");
        } else {
            System.out.println("To start Robocode, enter: " + installDir.getAbsolutePath() + "/robocode.sh");
        }
    }

    private static void createWindows(File installDir) {
        String command = getWindowsCmd() + " cscript.exe ";
        File shortcutMaker = new File(installDir, "makeshortcut.vbs");

        try (PrintStream out = new PrintStream(new FileOutputStream(shortcutMaker))) {
            String escaped = escaped(installDir.getAbsolutePath());

            out.println("Set Shell = CreateObject(\"WScript.Shell\")");
            out.println("Set fso = CreateObject(\"Scripting.FileSystemObject\")");
            out.println("ProgramsPath = Shell.SpecialFolders(\"Programs\")");
            out.println("if (not(fso.folderExists(ProgramsPath + \"\\\\Robocode\"))) Then");
            out.println("  fso.CreateFolder(ProgramsPath + \"\\\\Robocode\")");
            out.println("End If");
            out.println("Set link = Shell.CreateShortcut(ProgramsPath + \"\\\\Robocode\\\\Robocode.lnk\")");
            out.println("link.Arguments = \"\"");
            out.println("link.Description = \"Robocode\"");
            out.println("link.HotKey = \"\"");
            out.println("link.IconLocation = \"" + escaped + "\\\\robocode.ico,0\"");
            out.println("link.TargetPath = \"" + escaped + "\\\\robocode.bat\"");
            out.println("link.WindowStyle = 1");
            out.println("link.WorkingDirectory = \"" + escaped + "\"");
            out.println("link.Save()");
            out.println("DesktopPath = Shell.SpecialFolders(\"Desktop\")");
            out.println("Set link = Shell.CreateShortcut(DesktopPath + \"\\\\Robocode.lnk\")");
            out.println("link.Arguments = \"\"");
            out.println("link.Description = \"Robocode\"");
            out.println("link.HotKey = \"\"");
            out.println("link.IconLocation = \"" + escaped + "\\\\robocode.ico,0\"");
            out.println("link.TargetPath = \"" + escaped + "\\\\robocode.bat\"");
            out.println("link.WindowStyle = 1");
            out.println("link.WorkingDirectory = \"" + escaped + "\"");
            out.println("link.Save()");
        } catch (IOException e) {
            System.err.println("Failed to create shortcut script: " + e);
            return;
        }

        try {
            Process p = Runtime.getRuntime().exec(command + " makeshortcut.vbs", null, installDir);
            int rv = p.waitFor();
            if (rv != 0) {
                System.err.println("Can't create shortcut: " + shortcutMaker);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (!shortcutMaker.delete()) {
                System.err.println("Can't delete: " + shortcutMaker);
            }
        }
    }

    private static String escaped(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                sb.append('\\');
            }
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
