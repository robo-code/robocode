/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.ui.editor.theme.EditorThemeProperties;
import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemePropertyChangeAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;

import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 */
@SuppressWarnings("serial")
public class EditWindow extends JInternalFrame {

	private String fileName;
	private String robotName;
	public boolean modified;
	private final RobocodeEditor editor;
	private final IRepositoryManager repositoryManager;
	private final File robotsDirectory;
	private EditorPanel editorPanel;
	private EditorPane editorPane;

	public EditWindow(IRepositoryManager repositoryManager, RobocodeEditor editor, File robotsDirectory) {
		super("Edit Window", true, true, true, true);
		this.editor = editor;
		this.robotsDirectory = robotsDirectory;
		this.repositoryManager = repositoryManager;
		initialize();
	}

	public EditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = editorPanel.getEditorPane();
			InputMap im = editorPane.getInputMap();

			// FIXME: Replace hack with better solution than using 'ctrl H'
			im.put(KeyStroke.getKeyStroke("ctrl H"), editor.getReplaceAction());
		}	
		return editorPane;
	}

	public String getFileName() {
		return fileName;
	}

	public String getRobotName() {
		return robotName;
	}

	private void initialize() {
		try {
			addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosing(InternalFrameEvent e) {
					if (!modified || fileSave(true)) {
						editor.setLineStatus(-1);
						dispose();
					}
					editor.removeFromWindowMenu(EditWindow.this);
				}

				@Override
				public void internalFrameDeactivated(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}

				@Override
				public void internalFrameIconified(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}
			});
			setFrameIcon(new ImageIcon(EditWindow.class.getResource("/net/sf/robocode/ui/icons/robocode-icon.png")));
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			setSize(750, 500);

			editor.addToWindowMenu(this);

			editorPanel = new EditorPanel();
			setContentPane(editorPanel);

			EditorThemeProperties currentThemeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			Font font = currentThemeProps.getFont();
			editorPanel.setFont(font);

			// Make sure the source editor window gets focus with a blinking cursor
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					editorPanel.getEditorPane().requestFocus();
				}
			});

			EditorThemePropertiesManager.addListener(new EditorThemePropertyChangeAdapter() {
				@Override
				public void onFontChanged(Font newFont) {
					editorPanel.setFont(newFont);
				}
			});

			final JavaDocument document = (JavaDocument) editorPanel.getEditorPane().getDocument();

			document.addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {
					updateModificationState();
				}

				public void insertUpdate(DocumentEvent e) {
					updateModificationState();
				}

				public void changedUpdate(DocumentEvent e) {
					updateModificationState();
				}

				// Bug-361 Problem in the text editor related with the .java file modification
				private void updateModificationState() {
					setModified(editorPanel.getEditorPane().isModified());
				}
			});
		} catch (Throwable e) {
			Logger.logError(e);
		}
	}

	public void setFileName(String newFileName) {
		fileName = newFileName;
		updateTitle();
	}

	public void setRobotName(String newRobotName) {
		robotName = newRobotName;
		updateTitle();
	}

	private void updateTitle() {
		StringBuffer titleBuf = new StringBuffer("Editing");
		if (fileName != null) {
			titleBuf.append(" - ").append(fileName);
		} else if (robotName != null) {
			titleBuf.append(" - ").append(robotName);
		}
		if (modified) {
			titleBuf.append(" *");
		}
		setTitle(titleBuf.toString());
	}

	private void setModified(boolean modified) {
		boolean updated = (modified != this.modified);
		if (updated) {
			this.modified = modified;
			updateTitle();
			editor.setSaveFileMenuItemsEnabled(modified);
		}
	}

	public void compile() {
		if (!fileSave(true, true)) {
			error("You must save before compiling.");
			return;
		}
		if (editor.getCompiler() != null) {
			// The compiler + refresh of the repository is done in a thread in order to avoid the compiler
			// window hanging while compiling. The SwingUtilities.invokeLater() does not do a good job here
			// (window is still hanging). Hence, a real thread running beside the EDT is used, which does a
			// great job, where each each new print from the compiler is written out as soon as it is ready
			// in the output stream.
			new Thread(new Runnable() {
				public void run() {
					if (fileName == null) {
						error("You must save before compiling.");
						return;
					}
					editor.getCompiler().compile(getRobotDir(), fileName);
					repositoryManager.refresh(fileName);
				}
			}).start();
		} else {
			JOptionPane.showMessageDialog(editor, "No compiler installed.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void error(String msg) {
		Object[] options = {
			"OK"
		};

		JOptionPane.showOptionDialog(this, msg, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);
	}

	public boolean fileSave(boolean confirm) {
		return fileSave(confirm, false);
	}

	private boolean fileSave(boolean confirm, boolean mustSave) {
		if (confirm) {
			if (!modified) {
				return true;
			}
			String s = fileName;

			if (s == null) {
				s = robotName;
			}
			if (s == null) {
				s = "This file";
			}
			int ok = JOptionPane.showConfirmDialog(this, s + " has been modified.  Do you wish to save it?",
					"Modified file", JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION) {
				return !mustSave;
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		String fileName = getFileName();

		if (fileName == null) {
			return fileSaveAs();
		}

		String reasonableFilename = getReasonableFilename();

		if (reasonableFilename != null) {
			try {
				String a = new File(reasonableFilename).getCanonicalPath();
				String b = new File(fileName).getCanonicalPath();

				if (!a.equals(b)) {
					int ok = JOptionPane.showConfirmDialog(this,
							fileName + " should be saved in: \n" + reasonableFilename
							+ "\n  Would you like to save it there instead?",
							"Name has changed",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (ok == JOptionPane.CANCEL_OPTION) {
						return false;
					}
					if (ok == JOptionPane.YES_OPTION) {
						return fileSaveAs();
					}
				}
			} catch (IOException e) {
				Logger.logError("Unable to check reasonable filename: ", e);
			}
		}

		BufferedWriter bufferedWriter = null;
		OutputStreamWriter outputStreamWriter = null;
		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(fileName);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
			bufferedWriter = new BufferedWriter(outputStreamWriter);

			getEditorPane().write(bufferedWriter);
			setModified(false);
		} catch (IOException e) {
			error("Cannot write file: " + e);
			return false;
		} finally {
			FileUtil.cleanupStream(bufferedWriter);
		}
		return true;
	}

	private String getRobotDir() {
		String saveDir = robotsDirectory.getPath() + File.separatorChar;

		String text = getEditorPane().getText();
		int pIndex = text.indexOf("package ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(";", pIndex);

			if (pEIndex > 0) {
				String packageTree = text.substring(pIndex + 8, pEIndex) + File.separatorChar;

				packageTree = packageTree.replace('.', File.separatorChar);

				saveDir += packageTree;
			}
		}
		return saveDir;
	}
	
	public boolean fileSaveAs() {
		String javaFileName = null;
		String saveDir = getRobotDir();

		String text = getEditorPane().getText();

		int pIndex = text.indexOf("public class ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(" ", pIndex + 13);

			if (pEIndex > 0) {
				int pEIndex2 = text.indexOf("\n", pIndex + 13);

				if (pEIndex2 > 0 && pEIndex2 < pEIndex) {
					pEIndex = pEIndex2;
				}
				javaFileName = text.substring(pIndex + 13, pEIndex).trim() + ".java";
			} else {
				pEIndex = text.indexOf("\n", pIndex + 13);
				if (pEIndex > 0) {
					javaFileName = text.substring(pIndex + 13, pEIndex).trim() + ".java";
				}
			}
		}

		File f = new File(saveDir);

		if (!f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					"Your robot should be saved in the directory: " + saveDir
					+ "\nThis directory does not exist, would you like to create it?",
					"Create Directory",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.YES_OPTION) {
				if (!f.exists() && !f.mkdirs()) {
					Logger.logError("Cannot create: " + f);
				}
				f = new File(saveDir);
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		JFileChooser chooser;

		chooser = new JFileChooser(f);
		chooser.setCurrentDirectory(f);

		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				return extension.equalsIgnoreCase(".java");
			}

			@Override
			public String getDescription() {
				return "Robots";
			}
		};

		chooser.setFileFilter(filter);

		boolean done = false;

		while (!done) {
			done = true;
			if (javaFileName != null) {
				chooser.setSelectedFile(new File(f, javaFileName));
			}
			int rv = chooser.showSaveDialog(this);
			String robotFileName;

			if (rv == JFileChooser.APPROVE_OPTION) {
				robotFileName = chooser.getSelectedFile().getPath();
				File outFile = new File(robotFileName);

				if (outFile.exists()) {
					int ok = JOptionPane.showConfirmDialog(this,
							robotFileName + " already exists.  Are you sure you want to replace it?", "Warning",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (ok == JOptionPane.NO_OPTION) {
						done = false;
						continue;
					}
					if (ok == JOptionPane.CANCEL_OPTION) {
						return false;
					}
				}
				setFileName(robotFileName);
				fileSave(false);
			} else {
				return false;
			}
		}

		return true;
	}

	public String getPackage() {
		String text = getEditorPane().getText();
		int pIndex = text.indexOf("package ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(";", pIndex);

			if (pEIndex > 0) {
				return text.substring(pIndex + 8, pEIndex);
			}
		}
		return "";
	}

	private String getReasonableFilename() {
		StringBuffer fileName = new StringBuffer(robotsDirectory.getPath()).append(File.separatorChar);
		String javaFileName;
		String packageTree = null;

		String text = getEditorPane().getText();
		StringTokenizer tokenizer = new StringTokenizer(text, " \t\r\n;");
		String token;
		boolean inComment = false;

		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!inComment && (token.equals("/*") || token.equals("/**"))) {
				inComment = true;
			}
			if (inComment && (token.equals("*/") || token.equals("**/"))) {
				inComment = false;
			}
			if (inComment) {
				continue;
			}

			if (packageTree == null && token.equals("package")) {
				packageTree = tokenizer.nextToken();
				if (packageTree == null || packageTree.length() == 0) {
					return null;
				}
				packageTree = packageTree.replace('.', File.separatorChar);
				packageTree += File.separator;
				fileName.append(packageTree);
			}
			if (token.equals("class")) {
				javaFileName = tokenizer.nextToken() + ".java";
				fileName.append(javaFileName);
				return fileName.toString();
			}
		}
		return null;
	}
}
