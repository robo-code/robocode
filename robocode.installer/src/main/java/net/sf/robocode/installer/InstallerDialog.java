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

public final class InstallerDialog {

    private InstallerDialog() {
    }

    public static File requestInstallDir(File suggestedDir) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Install Robocode");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dialog.setSize(450, 150);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((screenSize.width - 450) / 2, (screenSize.height - 150) / 2);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = new Insets(2, 0, 2, 0);

        mainPanel.add(new JLabel("Install to:"), gbc);

        JTextField dirField = new JTextField(suggestedDir.getAbsolutePath());
        mainPanel.add(dirField, gbc);

        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel licenseNotice = new JLabel("By installing you accept the Eclipse Public License v1.0.");
        licenseNotice.setFont(licenseNotice.getFont().deriveFont(11f));
        mainPanel.add(licenseNotice, gbc);

        JButton proceedBtn = new JButton("Proceed");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.add(proceedBtn);
        btnPanel.add(cancelBtn);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(mainPanel, BorderLayout.NORTH);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(outer, BorderLayout.CENTER);
        dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);

        final File[] result = {null};

        proceedBtn.addActionListener(e -> {
            String path = dirField.getText().trim();
            if (!path.isEmpty()) {
                result[0] = new File(path);
            }
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setDefaultButton(proceedBtn);
        dialog.setVisible(true);
        return result[0];
    }

    public static ProgressDialog showProgress() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 500;
        int height = 100;

        JDialog dialog = new JDialog();
        dialog.setTitle("Installing Robocode...");
        dialog.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
        dialog.setSize(width, height);

        JLabel status = new JLabel("Preparing...", SwingConstants.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.getContentPane().add(status);

        dialog.setVisible(true);
        return new ProgressDialog(dialog, status);
    }

    public static class ProgressDialog {
        private final JDialog dialog;
        private final JLabel status;

        ProgressDialog(JDialog dialog, JLabel status) {
            this.dialog = dialog;
            this.status = status;
        }

        public void update(String text) {
            status.setText(text);
        }

        public void close() {
            dialog.dispose();
        }
    }
}
