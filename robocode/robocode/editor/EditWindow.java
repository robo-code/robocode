/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Rectangle;
import java.util.*;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (4/9/2001 5:15:56 PM)
 * @author: Mathew A. Nelson
 */
public class EditWindow extends javax.swing.JInternalFrame implements CaretListener {

	public RobocodeEditorKit editorKit = null;
	public java.lang.String fileName = null;
	public java.lang.String robotName = null;
	public boolean modified = false;
	private RobocodeEditor editor = null;
	private javax.swing.JEditorPane editorPane = null;
	private javax.swing.JPanel editWindowContentPane = null;
	public javax.swing.JFrame frame = null;
	public File robotsDirectory = null;
	private javax.swing.JScrollPane scrollPane = null;

	/**
	 * Return the editorPane property value.
	 * @return javax.swing.JEditorPane
	 */
	public javax.swing.JEditorPane getEditorPane() {
		if (editorPane == null) {
			try {
				editorPane = new javax.swing.JEditorPane();
				editorPane.setName("editorPane");
				editorPane.setFont(new java.awt.Font("monospaced", 0, 12));
				// editorPane.setBounds(0, 0, 6, 22);
				editorPane.setContentType("text/java");
				editorKit = new RobocodeEditorKit();
				editorPane.setEditorKitForContentType("text/java", editorKit);
				editorPane.setContentType("text/java");
				editorKit.setEditWindow(this);
				editorPane.addCaretListener(this);
				((JavaDocument) editorPane.getDocument()).setEditWindow(this);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return editorPane;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:05:06 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getFileName() {
		return fileName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:55:48 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getRobotName() {
		return robotName;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
					if (!modified) {
						editor.setLineStatus(-1);
						dispose();
					} else if (fileSave(true)) {
						editor.setLineStatus(-1);
						dispose();
					}
					return;
				}
				;
				public void internalFrameDeactivated(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}

				public void internalFrameIconified(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}
			});
			setName("EditWindow");
			setResizable(true);
			setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			setIconifiable(true);
			setClosable(true);
			setMaximum(false);
			setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icon.jpg")));
			setSize(553, 441);
			setMaximizable(true);
			setTitle("Edit Window");
			setContentPane(getEditWindowContentPane());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:05:06 PM)
	 * @param newFileName java.lang.String
	 */
	public void setFileName(java.lang.String newFileName) {
		fileName = newFileName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:56:22 PM)
	 * @param newModified boolean
	 */
	public void setModified(boolean modified) {
	
		if (this.modified == false && modified == true) {
			this.modified = true;
			if (fileName != null) {
				setTitle("Editing - " + fileName + " *");
			} else if (robotName != null) {
				setTitle("Editing - " + robotName + " *");
			} else {
				setTitle("Editing - *");
			}
		} else if (modified == false) {
			this.modified = false;
			if (fileName != null) {
				setTitle("Editing - " + fileName);
			} else if (robotName != null) {
				setTitle("Editing - " + robotName);
			} else {
				setTitle("Editing");
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:55:48 PM)
	 * @param newRobotName java.lang.String
	 */
	public void setRobotName(java.lang.String newRobotName) {
		robotName = newRobotName;
	}

	/**
	 * EditWindow constructor,
	 */
	public EditWindow(RobocodeEditor editor, File robotsDirectory) {
		super();
		initialize();
		this.editor = editor;
		this.robotsDirectory = robotsDirectory;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/10/2001 1:03:12 PM)
	 * @param e javax.swing.event.CaretEvent
	 */
	public void caretUpdate(CaretEvent e) {
		int lineend = getEditorPane().getDocument().getDefaultRootElement().getElementIndex(e.getDot());

		editor.setLineStatus(lineend);

		/*
		 //	log("Caret at position: " + e.getDot() + " mark " + e.getMark());
		 log("Caret at position: " + e.getDot() + " mark " + e.getMark());
		 int linestart = getEditorPane().getDocument().getDefaultRootElement().getElementIndex(e.getMark());
		 log("Caret from line: " + linestart + " to line: " + lineend);
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 12:06:39 PM)
	 */
	public void compile() {
		if (!fileSave(true, true)) {
			error("You must save before compiling.");
			return;
		}

		RobocodeCompiler compiler = editor.getCompiler();

		if (compiler != null) {
			compiler.compile(fileName);
		} else {
			JOptionPane.showMessageDialog(editor, "No compiler installed.", "Error", JOptionPane.ERROR_MESSAGE);
		}

		/*
		 String compilerClassName = "sun.tools.javac.Main";

		 // Get compiler class	
		 Class compilerClass = null;
		 try {
		 compilerClass = Class.forName(compilerClassName);
		 } catch (ClassNotFoundException e) {
		 error("Sorry, class " + compilerClassName + " not installed.  Do you have tools.jar?");
		 return;
		 }

		 OutputStream out = new java.io.ByteArrayOutputStream(4096);

		 // Set constructor parameters
		 Object[] constructorParams = new Object[2];
		 constructorParams[0] = out;
		 constructorParams[1] = "robocode Compiler";

		 // Get constructor parameter classes
		 Class[] constructorParamClasses = new Class[constructorParams.length];
		 for (int i = 0; i < constructorParams.length; i++)
		 {
		 constructorParamClasses[i] = constructorParams[i].getClass();
		 }
		 
		 // Replace PrintStream with OutputStream, or we won't find the constructor.
		 try {
		 constructorParamClasses[0] = Class.forName("java.io.OutputStream");
		 } catch (Exception e) {
		 error("Sorry, could not find class java.io.OutputStream");
		 return;
		 }

		 // Get the constructor itself
		 java.lang.reflect.Constructor constructor = null;
		 try {
		 constructor = compilerClass.getConstructor(constructorParamClasses);
		 } catch (Exception e) {
		 error("Sorry, could not find appropriate constructor: " + e);
		 return;
		 }

		 // Use the constructor to create an instance of the compiler
		 Object compiler = null;
		 try {
		 compiler = constructor.newInstance(constructorParams);
		 } catch (Exception e) {
		 error("Sorry, could not instantiate compiler: " + e);
		 return;
		 }

		 // Set the compile parameters
		 //log(new File(".").getAbsolutePath());
		 String[] compilerParams = new String[5];
		 compilerParams[0] = "-deprecation";
		 compilerParams[1] = "-g";
		 compilerParams[2] = "-classpath";

		 compilerParams[3] = "robocode.jar" + File.pathSeparator + robotPath;
		 compilerParams[4] = fileName;

		 Object[] methodParams = new Object[1];
		 methodParams[0] = compilerParams;

		 // Get the compile parameter classes (only 1 actually, String[])	
		 Class[] methodParamClasses = new Class[methodParams.length];
		 for (int i = 0; i < methodParams.length; i++)
		 {
		 methodParamClasses[i] = methodParams[i].getClass();
		 }

		 // Get the compile method
		 java.lang.reflect.Method method = null;
		 try {
		 method = compilerClass.getMethod("compile",methodParamClasses);
		 } catch (Exception e) {
		 error("Sorry, could not get compile method: " + e);
		 return;
		 }

		 // Finally... invoke the compiler!
		 boolean rv;
		 try {
		 Object returnValue = null;
		 returnValue = method.invoke(compiler,methodParams);
		 try {
		 rv = ((Boolean)returnValue).booleanValue();
		 } catch (ClassCastException e) {
		 error("Sorry, Could not determine return value");
		 return;
		 }
		 } catch (Exception e) {
		 error("Sorry, could not invoke compile method: " + e);
		 return;
		 }

		 String outputTitle;
		 if (rv == true)
		 {
		 outputTitle = "Compiled successfully.";
		 }
		 else
		 {
		 outputTitle = "Compile failed.";
		 }

		 CompilerOutputDialog d;
		 if (frame != null)
		 d = new CompilerOutputDialog(frame,outputTitle,false);
		 else d = new CompilerOutputDialog();
		 d.setText(outputTitle +"\n" + out.toString());
		 d.pack();
		 d.pack();
		 robocode.util.Utils.packCenterShow(frame,d);
		 /*	d.pack();
		 d.show();
		 */
	
		/*
		 try {
		 sun.tools.javac.Main m=new sun.tools.javac.Main(System.err,"My Compiler");
		 String[] b = new String[3];
		 b[0] = "-classpath";
		 b[1] = "robocode.jar";
		 b[2] = fileName;
		 boolean rv = m.compile(b);
		 error ("Compile: " + rv);
		 } catch (Exception e) {
		 log("Caught.");
		 }
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:27:07 PM)
	 * @param msg java.lang.String
	 */
	public void error(String msg) {
		Object[] options = { "OK" };

		JOptionPane.showOptionDialog(this, msg, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/19/2001 3:55:52 PM)
	 * @return boolean - whether cancelled
	 * @param editWindow robocode.editor.EditWindow
	 */
	public boolean fileSave(boolean confirm) {
		return fileSave(confirm, false);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/19/2001 3:55:52 PM)
	 * @return boolean - whether cancelled
	 * @param editWindow robocode.editor.EditWindow
	 */
	public boolean fileSave(boolean confirm, boolean mustSave) {
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
				if (mustSave) {
					return false;
				}
				return true;
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
						if (editor.getManager() != null) {
							editor.getManager().getRobotRepositoryManager().clearRobotList();
						}
						return fileSaveAs();
					}
				}
			} catch (Exception e) {
				log("Unable to check reasonable filename: " + e);
			}
		}
		File f = new File(fileName);

		try {
			FileWriter writer = new FileWriter(f);

			getEditorPane().write(writer);
			setModified(false);
			writer.close();
		} catch (IOException e) {
			error("Cannot write file: " + e);
			return false;
		}
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/19/2001 3:55:52 PM)
	 * @return boolean
	 * @param editWindow robocode.editor.EditWindow
	 */
	public boolean fileSaveAs() {

		String fileName; // = editWindow.getFileName();
		String javaFileName = null;
		String saveDir = null;
		String packageTree = null;

		fileName = robotsDirectory.getPath() + File.separatorChar;
		saveDir = fileName;

		try {
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
					fileName += javaFileName;
				} else {
					pEIndex = text.indexOf("\n", pIndex + 13);
					if (pEIndex > 0) {
						javaFileName = text.substring(pIndex + 13, pEIndex).trim() + ".java";
						fileName += javaFileName;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			error("Could not parse package and class names.");
		}
		
		// }

		File f = new File(saveDir);

		if (!f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					"Your robot should be saved in the directory: " + saveDir
					+ "\nThis directory does not exist, would you like to create it?",
					"Create Directory",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.YES_OPTION) {
				f.mkdirs();
				f = new File(saveDir);
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		JFileChooser chooser;

		chooser = new JFileChooser(f); // .getAbsoluteFile().toString());
		chooser.setCurrentDirectory(f);
		// log("opening at: " + f.getAbsoluteFile().toString());
		
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".java")) {
					return true;
				}
				return false;
			}

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
			String robotFileName = null;

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

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:34:48 PM)
	 * @return robocode.editor.RobocodeEditorKit
	 */
	public RobocodeEditorKit getEditorKit() {
		return editorKit;
	}

	/**
	 * Return the editWindowContentPane
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getEditWindowContentPane() {
		if (editWindowContentPane == null) {
			try {
				editWindowContentPane = new javax.swing.JPanel();
				editWindowContentPane.setName("editWindow");
				editWindowContentPane.setLayout(new java.awt.BorderLayout());
				editWindowContentPane.setDoubleBuffered(true);
				editWindowContentPane.add(getScrollPane(), "Center");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return editWindowContentPane;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:39:53 PM)
	 * @return javax.swing.JFrame
	 */
	public javax.swing.JFrame getFrame() {
		return frame;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/10/2001 12:10:07 PM)
	 * @return java.lang.String
	 */
	public String getPackage() {
		String text = getEditorPane().getText();
		int pIndex = text.indexOf("package ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(";", pIndex);

			if (pEIndex > 0) {
				return text.substring(pIndex + 8, pEIndex);
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 5:42:28 PM)
	 * @return java.lang.String
	 */
	public String getReasonableFilename() {
		String fileName = robotsDirectory.getPath() + File.separatorChar;
		String saveDir = fileName;
		String javaFileName = null;
		String packageTree = null;

		try {
			String text = getEditorPane().getText();
			StringTokenizer tokenizer = new StringTokenizer(text, " \t\r\n;");
			String token = null;
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
					if (packageTree == null || packageTree.equals("")) {
						return null;
					}
					packageTree = packageTree.replace('.', File.separatorChar);
					packageTree += File.separator;
					fileName += packageTree;
					saveDir = fileName;
				}
				if (javaFileName == null && token.equals("class")) {
					javaFileName = tokenizer.nextToken() + ".java";
					fileName += javaFileName;
					return fileName;
				}	
			}

			/*
			 int pIndex = text.indexOf("package ");
			 if (pIndex >= 0)
			 {
			 StringTokenizer tokenizer = new StringTokenizer(text.substring(pIndex)," \t\r\n;");
			 String skip = tokenizer.nextToken();
			 packageTree = tokenizer.nextToken();
			 packageTree = packageTree.replace('.',File.separatorChar);

			 if (packageTree == null || packageTree.equals(""))
			 return null;
			 packageTree += File.separator;
			 fileName += packageTree;
			 saveDir = fileName;
			 } 
			 else return null;
			 
			 pIndex = text.indexOf("class ");
			 if (pIndex >= 0)
			 {
			 StringTokenizer tokenizer = new StringTokenizer(text.substring(pIndex)," \t\r\n;");
			 String skip = tokenizer.nextToken();
			 javaFileName = tokenizer.nextToken() + ".java";
			 fileName += javaFileName;
			 }
			 else
			 return null;
			 */
		} catch (Exception e) {
			return null;
		}

		return null;
		// fileName;
	}

	/**
	 * Return the scrollPane
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getScrollPane() {
		if (scrollPane == null) {
			try {
				scrollPane = new javax.swing.JScrollPane();
				scrollPane.setName("scrollPane");
				scrollPane.setViewportView(getEditorPane());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return scrollPane;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/20/2001 1:29:33 PM)
	 */
	public void scrollToTop() {
		getEditorPane().scrollRectToVisible(new Rectangle(0, 0, 10, 10));

		/*
		 java.awt.EventQueue.invokeLater(new Runnable() {
		 public void run() {
		 getEditorPane().setCaretPosition(0);
		 getEditorPane().requestFocus();
		 getEditorPane().scrollRectToVisible(new Rectangle(0,0,10,10));
		 getJScrollPane1().scrollRectToVisible(new Rectangle(0,0,10,10));
		 //	error("Scrolled to top");
		 }
		 });
		 */	
	
		/* JScrollBar b = getJScrollPane1().getVerticalScrollBar();
		 if (b != null)
		 {
		 int minVal = b.getMinimum();
		 b.setValue(minVal);
		 error("Scrolled to top");
		 }
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:39:53 PM)
	 * @param newFrame javax.swing.JFrame
	 */
	public void setFrame(javax.swing.JFrame newFrame) {
		frame = newFrame;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:27:07 PM)
	 * @param msg java.lang.String
	 */
	public void success(String msg) {
		Object[] options = { "OK" };

		JOptionPane.showOptionDialog(this, msg, "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);

	}
}
