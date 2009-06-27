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
 *     Matthew Reeder
 *     - Changes to facilitate Undo/Redo
 *     - Support for line numbers
 *     - Window menu
 *     - Launching the Replace dialog using ctrl+H
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class EditWindow extends JInternalFrame implements CaretListener {

	private String fileName;
	private String robotName;
	public boolean modified;
	private final RobocodeEditor editor;
	private final IRepositoryManager repositoryManager;
	private JEditorPane editorPane;
	private JPanel editWindowContentPane;
	private final File robotsDirectory;
	private JScrollPane scrollPane;

	private LineNumbers lineNumbers;

	public EditWindow(IRepositoryManager repositoryManager, RobocodeEditor editor, File robotsDirectory) {
		super();
		this.editor = editor;
		this.robotsDirectory = robotsDirectory;
		this.repositoryManager = repositoryManager;
		initialize();
	}

	public JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setFont(new Font("monospaced", 0, 12));
			RobocodeEditorKit editorKit = new RobocodeEditorKit();

			editorPane.setEditorKitForContentType("text/java", editorKit);
			editorPane.setContentType("text/java");
			editorKit.setEditWindow(this);
			editorPane.addCaretListener(this);
			((JavaDocument) editorPane.getDocument()).setEditWindow(this);

			InputMap im = editorPane.getInputMap();

			// read: hack.
			im.put(KeyStroke.getKeyStroke("ctrl H"), editor.getReplaceAction()); // FIXME: Replace hack with better solution?
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
			this.addInternalFrameListener(new InternalFrameAdapter() {
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
			setResizable(true);
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			setIconifiable(true);
			setClosable(true);
			setMaximum(false);
			setFrameIcon(new ImageIcon(EditWindow.class.getResource("/net/sf/robocode/ui/icons/robocode-icon.png")));
			setSize(553, 441);
			setMaximizable(true);
			setTitle("Edit Window");
			setContentPane(getEditWindowContentPane());
			editor.addToWindowMenu(this);
		} catch (Throwable e) {
			Logger.logError(e);
		}
	}

	public void setFileName(String newFileName) {
		fileName = newFileName;
	}

	public void setModified(boolean modified) {
		if (modified && !this.modified) {
			this.modified = true;
			if (fileName != null) {
				setTitle("Editing - " + fileName + " *");
			} else if (robotName != null) {
				setTitle("Editing - " + robotName + " *");
			} else {
				setTitle("Editing - *");
			}
		} else if (!modified) {
			this.modified = false;
			if (fileName != null) {
				setTitle("Editing - " + fileName);
			} else if (robotName != null) {
				setTitle("Editing - " + robotName);
			} else {
				setTitle("Editing");
			}
		}

		editor.setSaveFileMenuItemsEnabled(modified);
	}

	public void setRobotName(String newRobotName) {
		robotName = newRobotName;
	}

	public void caretUpdate(CaretEvent e) {
		int lineend = getEditorPane().getDocument().getDefaultRootElement().getElementIndex(e.getDot());

		editor.setLineStatus(lineend);
	}

	public void compile() {
		if (!fileSave(true, true)) {
			error("You must save before compiling.");
			return;
		}
		RobocodeCompiler compiler = editor.getCompiler();

		if (compiler != null) {
			compiler.compile(fileName);
			repositoryManager.refresh(fileName);
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

		FileWriter writer = null;

		try {
			writer = new FileWriter(new File(fileName));
			getEditorPane().write(writer);
			setModified(false);
		} catch (IOException e) {
			error("Cannot write file: " + e);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {}
			}
		}
		return true;
	}

	public boolean fileSaveAs() {
		String javaFileName = null;
		String packageTree;

		String fileName = robotsDirectory.getPath() + File.separatorChar;
		String saveDir = fileName;

		String text = getEditorPane().getText();
		int pIndex = text.indexOf("package ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(";", pIndex);

			if (pEIndex > 0) {
				packageTree = text.substring(pIndex + 8, pEIndex) + File.separatorChar;
				packageTree = packageTree.replace('.', File.separatorChar);

				fileName += packageTree;
				saveDir = fileName;
			}
		}

		pIndex = text.indexOf("public class ");
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
					Logger.logError("Can't create" + f);
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

	private JPanel getEditWindowContentPane() {
		if (editWindowContentPane == null) {
			editWindowContentPane = new JPanel();
			editWindowContentPane.setLayout(new BorderLayout());
			editWindowContentPane.setDoubleBuffered(true);
			editWindowContentPane.add(getScrollPane(), "Center");
		}
		return editWindowContentPane;
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
		String fileName = robotsDirectory.getPath() + File.separatorChar;
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
				fileName += packageTree;
			}
			if (token.equals("class")) {
				javaFileName = tokenizer.nextToken() + ".java";
				fileName += javaFileName;
				return fileName;
			}
		}
		return null;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
			scrollPane.setRowHeaderView(getLineNumbers());
		}
		return scrollPane;
	}

	private LineNumbers getLineNumbers() {
		if (lineNumbers == null) {
			lineNumbers = new LineNumbers(getEditorPane());
		}
		return lineNumbers;
	}

	public void undo() {
		((JavaDocument) getEditorPane().getDocument()).undo();
		repaint();
	}

	public void redo() {
		((JavaDocument) getEditorPane().getDocument()).redo();
		repaint();
	}
}
