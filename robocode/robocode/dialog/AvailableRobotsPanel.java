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
package robocode.dialog;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import robocode.util.*;
import robocode.repository.*;

/**
 * Insert the type's description here.
 * Creation date: (8/26/2001 5:19:04 PM)
 * @author: Mathew A. Nelson
 */
public class AvailableRobotsPanel extends javax.swing.JPanel {


	private JScrollPane availableRobotsScrollPane = null;
	private JList availableRobotsList = null;

	EventHandler eventHandler = new EventHandler();
	
class EventHandler implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == true)
			return;
			
		if (e.getSource() == getAvailableRobotsList())
		{
			availableRobotsListSelectionChanged();
		}
		else if (e.getSource() == getAvailablePackagesList())
		{
			availablePackagesListSelectionChanged();
		}
	}
};
	
/**
 * Return the availableRobotsList.
 * @return javax.swing.JList
 */
private javax.swing.JList getAvailableRobotsList() {
	if (availableRobotsList == null) {
		try {
			availableRobotsList = new javax.swing.JList();
			availableRobotsList.setName("availableRobotsList");
			availableRobotsList.setModel(new AvailableRobotsModel());
			availableRobotsList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			robotNamesCellRenderer = new RobotNameCellRenderer();
			availableRobotsList.setCellRenderer(robotNamesCellRenderer);
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// This does not work in Linux under IBM JRE 1.3.0...
					if (e.getClickCount() >= 2) {
						if (e.getClickCount() % 2 == 0)
							if (actionButton != null)
								actionButton.doClick();
					}
				}
			};
			availableRobotsList.addMouseListener(mouseListener);
			availableRobotsList.addListSelectionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return availableRobotsList;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getAvailableRobotsScrollPane() {
	if (availableRobotsScrollPane == null) {
		try {
			availableRobotsScrollPane = new javax.swing.JScrollPane();
			availableRobotsScrollPane.setName("availableRobotsScrollPane");
			availableRobotsScrollPane.setViewportView(getAvailableRobotsList());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return availableRobotsScrollPane;
}

/**
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
private void initialize() {
	try {
		setName("AvailableRobotsPanel");
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),title));
		setLayout(new BorderLayout());
		
		JPanel top = new JPanel();
		top.setLayout(new java.awt.GridLayout(1,2));

		JPanel a = new JPanel();
		a.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(),"Packages"));
		a.setLayout(new BorderLayout());
		a.add(getAvailablePackagesScrollPane(),BorderLayout.CENTER);
		a.setPreferredSize(new Dimension(120,100));
		top.add(a);
		JPanel b = new JPanel();
		b.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(),"Robots"));
		b.setLayout(new BorderLayout());
		b.add(getAvailableRobotsScrollPane(),BorderLayout.CENTER);
		b.setPreferredSize(new Dimension(120,100));
		top.add(b);
		add(top,BorderLayout.CENTER);
		
//		JPanel bot = new JPanel();
//		bot.setBackground(Color.red);
		JLabel f5Label = new JLabel("Press F5 to refresh");
		f5Label.setHorizontalAlignment(JLabel.CENTER);
		add(f5Label,BorderLayout.SOUTH);
/*		bot.setBorder(new EmptyBorder(0,0,0,0));
		bot.add(f5Label);
//		bot.setLayout(new GridLayout(1,1));
		add(bot,BorderLayout.SOUTH);
//		bot.setPreferredSize(f5Label.getPreferredSize());
//		bot.setMaximumSize(f5Label.getPreferredSize());
 * */
	} catch (java.lang.Throwable e) {
		log(e);
	}
}

private void log(Throwable e) {
	Utils.log(e);
}

private void log(String s) {
	Utils.log(s);
}

/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 12:23:35 PM)
 * @param newRobotList java.util.Vector
 */
public void setRobotList(FileSpecificationVector robotListVector) {

	AvailableRobotsPanel.this.robotList = robotListVector;
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {	

	availablePackages.clear();
	availableRobots.clear();

	if (AvailableRobotsPanel.this.robotList == null)
	{
		AvailableRobotsPanel.this.robotList = new FileSpecificationVector();
		availablePackages.add("One moment please...");
		((AvailablePackagesModel)getAvailablePackagesList().getModel()).changed();
		getAvailablePackagesList().clearSelection();
		((AvailableRobotsModel)getAvailableRobotsList().getModel()).changed();
	}
	else
	{
		availablePackages.add("(All)");
		String packageName = null;
		for (int i = 0; i < robotList.size(); i++)
		{
			FileSpecification robotSpecification = (FileSpecification)robotList.elementAt(i);
			if (packageName != null && robotSpecification.getFullPackage() != null && !packageName.equals(robotSpecification.getFullPackage()))
			{
				packageName = robotSpecification.getFullPackage();
				if (!availablePackages.contains(robotSpecification.getFullPackage()))
					availablePackages.add(robotSpecification.getFullPackage());
			}
			else if (robotSpecification.getFullPackage() != null && !robotSpecification.getFullPackage().equals(packageName))
			{
				packageName = robotSpecification.getFullPackage();
				if (!availablePackages.contains(robotSpecification.getFullPackage()))
					availablePackages.add(robotSpecification.getFullPackage());
			}
		}
		availablePackages.add("(No package)");
		
		for (int i = 0; i < robotList.size(); i++)
			availableRobots.add(robotList.elementAt(i));
		((AvailablePackagesModel)getAvailablePackagesList().getModel()).changed();
		getAvailablePackagesList().setSelectedIndex(0);
		((AvailableRobotsModel)getAvailableRobotsList().getModel()).changed();
		getAvailablePackagesList().requestFocus();
	}
		}});
//	getAvailablePackagesList().setSelectedIndex(0);
//	((AvailableRobotsModel)getAvailableRobotsList().getModel()).changed();

}


	private JButton actionButton = null;
	private JList actionList = null;
	private Vector availablePackages = new Vector();
	private JList availablePackagesList = null;
	private JScrollPane availablePackagesScrollPane = null;
	private FileSpecificationVector availableRobots = new FileSpecificationVector();
	private FileSpecificationVector robotList = new FileSpecificationVector();
private RobotNameCellRenderer robotNamesCellRenderer = null;
	private RobotSelectionPanel robotSelectionPanel = null;
	private String title = null;

class AvailablePackagesModel extends javax.swing.AbstractListModel {
	public void changed()
	{
		fireContentsChanged(this,0,getSize());
	}
	
	public int getSize() {
		return availablePackages.size();
	}
	public Object getElementAt(int which)
	{
		return availablePackages.elementAt(which);
	}
}

class AvailableRobotsModel extends javax.swing.AbstractListModel {
	public void changed()
	{
		fireContentsChanged(this,0,getSize());
	}
	
	public int getSize() {
		return availableRobots.size();
	}
	public Object getElementAt(int which)
	{
		try {
			return availableRobots.elementAt(which);
		} catch (ArrayIndexOutOfBoundsException e) {
			// If the view updates while we're updating...
			return "";
		}
	}
}

class RobotNameCellRenderer extends JLabel implements ListCellRenderer {

	 private boolean useShortNames = false;
	 public RobotNameCellRenderer() {
		 setOpaque(true);
	 }
	 public void setUseShortNames(boolean useShortNames)
	 {
	     this.useShortNames = useShortNames;
	 }
	 public Component getListCellRendererComponent(
		 JList list,
		 Object value,
		 int index,
		 boolean isSelected,
		 boolean cellHasFocus)
	 {
		setComponentOrientation(list.getComponentOrientation());
		
		if (isSelected) {
		    setBackground(list.getSelectionBackground());
		    setForeground(list.getSelectionForeground());
		}
		else {
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
		}

		if (useShortNames && value instanceof FileSpecification)
		{
			FileSpecification fileSpecification = (FileSpecification)value;
			if (fileSpecification instanceof TeamSpecification)
				setText("Team: " + fileSpecification.getNameManager().getUniqueShortClassNameWithVersion());
			else
				setText(fileSpecification.getNameManager().getUniqueShortClassNameWithVersion());
	 	}
		else if (value instanceof FileSpecification)
		{
		    FileSpecification fileSpecification = (FileSpecification)value;
		    if (fileSpecification instanceof TeamSpecification)
				setText("Team: " + fileSpecification.getNameManager().getUniqueFullClassNameWithVersion());
		    else
				setText(fileSpecification.getNameManager().getUniqueFullClassNameWithVersion());
		}
		else
			setText(value.toString());

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		//setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

		return this;
	 }
 }

/**
 * NewBattleRobotsTab constructor comment.
 */
public AvailableRobotsPanel(JButton actionButton, String title, JList actionList, RobotSelectionPanel robotSelectionPanel) {
	super();
	this.title = title;
	this.actionButton = actionButton;
	this.actionList = actionList;
	this.robotSelectionPanel = robotSelectionPanel;
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 3:53:26 PM)
 */
private void availablePackagesListSelectionChanged() {
	int sel[] = getAvailablePackagesList().getSelectedIndices();
	availableRobots.clear();
	if (sel.length == 1)
	{
		robotNamesCellRenderer.setUseShortNames(true);
		getAvailablePackagesList().scrollRectToVisible(getAvailablePackagesList().getCellBounds(sel[0],sel[0]));
	}
	else
		robotNamesCellRenderer.setUseShortNames(false);
	
	for (int i = 0; i < sel.length; i++)
	{
		String selectedPackage = (String)availablePackages.elementAt(sel[i]);
		//log("Looking for robots at " + sel[i] + " - in package: " + selectedPackage);
		if (selectedPackage.equals("(All)"))
		{
			robotNamesCellRenderer.setUseShortNames(false);
			availableRobots.clear();
			for (int j = 0; j < robotList.size(); j++)
			{
				availableRobots.add(robotList.elementAt(j));
			}
			break;
		}
		// else single package.
		else
		{
			for (int j = 0; j < robotList.size(); j++)
			{
				FileSpecification robotSpecification = (FileSpecification)robotList.elementAt(j);
				if (robotSpecification.getFullPackage() == null)
				{
					if (selectedPackage.equals("(No package)"))
					{
						//log("Adding: " + classManager.getFullClassName());
						availableRobots.add(robotSpecification);
					}
				}
				else
				{
					if (robotSpecification.getFullPackage().equals(selectedPackage))
					{
						//log("Adding: " + classManager.getFullClassName());
						availableRobots.add(robotSpecification);
					}
				}
			}
		}
	}
	((AvailableRobotsModel)getAvailableRobotsList().getModel()).changed();
	if (availableRobots.size() > 0)
	{
		//selectedRobotsList.clearSelection();
		availableRobotsList.setSelectedIndex(0);
		availableRobotsListSelectionChanged();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 3:53:26 PM)
 */
private void availableRobotsListSelectionChanged() {
	int sel[] = getAvailableRobotsList().getSelectedIndices();
	if (sel.length == 1)
	{
		if (actionList != null)
			actionList.clearSelection();
		FileSpecification robotSpecification = (FileSpecification)getAvailableRobotsList().getModel().getElementAt(sel[0]);
		if (robotSelectionPanel != null)
			robotSelectionPanel.showDescription(robotSpecification);
	}
	else
	{
		if (robotSelectionPanel != null)
			robotSelectionPanel.showDescription(null);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 4:09:39 PM)
 */
public void clearSelection() {
	getAvailableRobotsList().clearSelection();
	((AvailableRobotsModel)getAvailableRobotsList().getModel()).changed();
}

/**
 * Return the availableRobotsList.
 * @return javax.swing.JList
 */
private javax.swing.JList getAvailablePackagesList() {
	if (availablePackagesList == null) {
		try {
			availablePackagesList = new javax.swing.JList();
			availablePackagesList.setName("availablePackagesList");
			availablePackagesList.setModel(new AvailablePackagesModel());
			availablePackagesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			availablePackagesList.addListSelectionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return availablePackagesList;
}

/**
 * Return the availablePackagesScrollPane
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getAvailablePackagesScrollPane() {
	if (availablePackagesScrollPane == null) {
		try {
			availablePackagesScrollPane = new javax.swing.JScrollPane();
			availablePackagesScrollPane.setName("availablePackagesScrollPane");
			availablePackagesScrollPane.setViewportView(getAvailablePackagesList());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return availablePackagesScrollPane;
}

/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 4:08:22 PM)
 * @return java.util.Vector
 */
public FileSpecificationVector getAvailableRobots() {
	return availableRobots;
}

/**
 * Insert the method's description here.
 * Creation date: (12/29/2000 12:23:35 PM)
 * @param newRobotList java.util.Vector
 */
public FileSpecificationVector getRobotList() {
	return robotList;
}

/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 4:14:54 PM)
 */
public FileSpecificationVector getSelectedRobots() {
	int sel[] = getAvailableRobotsList().getSelectedIndices();
	FileSpecificationVector moves = new FileSpecificationVector();
	for (int i = 0; i < sel.length; i++)
	{
		moves.add(availableRobots.elementAt(sel[i]));
	}
	return moves;

}
}