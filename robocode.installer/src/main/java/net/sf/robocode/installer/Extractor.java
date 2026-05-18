/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.installer;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static net.sf.robocode.installer.OsUtil.*;

public final class Extractor {
    private static final String[] SPINNER = {"-", "\\", "|", "/"};

    private Extractor() {
    }

    public static void extract(File installDir, Consumer<String> progress) throws IOException {
        String classResource = Extractor.class.getName().replace('.', '/') + ".class";
        String jarUrl = Extractor.class.getClassLoader().getResource(classResource).toString();
        String jarPath = jarUrl.substring("jar:file:/".length(), jarUrl.indexOf("!/"));

        if (jarPath.indexOf('!') > -1) {
            throw new IOException(jarPath + "\nContains an exclamation point. Please move the file to a different directory.");
        }

        byte[] buf = new byte[2048];
        URL url = new URL("file:/" + jarPath);

        try (InputStream is = url.openStream();
             JarInputStream jarIS = new JarInputStream(is)) {

            JarEntry entry;
            int spin = 0;

            while ((entry = jarIS.getNextJarEntry()) != null) {
                String entryName = entry.getName();

                if (entry.isDirectory()) {
                    if (!entryName.startsWith("net/") && !(entryName.equals("desktop") && isFreeBSD())) {
                        File dir = new File(installDir, entryName);
                        if (!dir.exists() && !dir.mkdirs()) {
                            System.err.println("Can't create dir: " + dir);
                        }
                    }
                } else if (!entryName.startsWith("net/sf/robocode/installer/")) {
                    if (!shouldSkip(entryName)) {
                        File outputFile = new File(installDir, entryName);
                        if (!outputFile.toPath().normalize().startsWith(installDir.toPath().normalize())) {
                            throw new RuntimeException("Bad zip entry: " + entryName);
                        }

                        File parentDir = outputFile.getParentFile();
                        if (!parentDir.exists() && !parentDir.mkdirs()) {
                            System.err.println("Can't create dir: " + parentDir);
                        }

                        int bytes = 0;
                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            int count = 0;
                            int num;
                            while ((num = jarIS.read(buf, 0, 2048)) != -1) {
                                fos.write(buf, 0, num);
                                bytes += num;
                                count++;
                                if (count > 80 && progress != null) {
                                    progress.accept(entryName + " " + SPINNER[spin % 4] + " (" + bytes + " bytes)");
                                    count = 0;
                                    spin++;
                                }
                            }
                        }

                        if (progress != null) {
                            progress.accept(entryName + " (" + bytes + " bytes)");
                        }

                        String entryNameLC = entryName.toLowerCase();
                        if (entryNameLC.endsWith(".sh") || entryNameLC.endsWith(".command")) {
                            dos2unix(outputFile.getAbsolutePath());
                            if (File.separatorChar == '/') {
                                Runtime.getRuntime().exec("chmod 755 " + outputFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean shouldSkip(String entryName) {
        String lc = entryName.toLowerCase();
        boolean isBatFile = lc.endsWith(".bat");
        boolean isShFile = lc.endsWith(".sh");
        boolean isCommandFile = lc.endsWith(".command");
        boolean isDesktopFile = lc.startsWith("desktop/");

        if (File.separatorChar == '/') {
            return isBatFile || (isCommandFile && !isMacOSX()) || (isDesktopFile && !isFreeBSD());
        }
        return isShFile || isCommandFile || isDesktopFile;
    }

    private static void dos2unix(String filepath) throws IOException {
        File tempFile = File.createTempFile("robocode", null);
        try (BufferedReader br = new BufferedReader(new FileReader(filepath));
             FileWriter fw = new FileWriter(tempFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                fw.write(line.replace("\r", ""));
                fw.write('\n');
            }
        }
        Files.move(tempFile.toPath(), Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
    }
}
