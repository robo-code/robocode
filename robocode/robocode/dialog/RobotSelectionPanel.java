/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.Vector;
import java.util.StringTokenizer;

import robocode.repository.*;
import robocode.manager.RobotRepositoryManager;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (8/26/2001 5:19:04 PM)
 * @author: Mathew A. Nelson
 */
public class RobotSelectionPanel extends WizardPanel {

	private AvailableRobotsPanel availableRobotsPanel = null;

	private JPanel selectedRobotsPanel = null;
	private JScrollPane selectedRobotsScrollPane = null;
	private JList selectedRobotsList = null;
	
	private JTree selectedRobotsTree = null;

	private JPanel buttonsPanel = null;
	private JPanel addButtonsPanel = null;

	private JPanel removeButtonsPanel = null;
	private JButton addButton = null;
	private JButton addAllButton = null;
	private JButton removeButton = null;
	private JButton removeAllButton = null;

	private EventHandler eventHandler = new EventHandler();
	private RobotDescriptionPanel descriptionPanel = null;
	private String instructions = null;
	private javax.swing.JLabel instructionsLabel = null;
	private JPanel mainPanel = null;
	private int maxRobots = 1;
	private int minRobots = 1;
	private JPanel numRoundsPanel = null;
	private JTextField numRoundsTextField = null;
	private boolean onlyShowSource = false;
	private boolean onlyShowWithPackage = false;
	private boolean onlyShowRobots = false;
	private boolean onlyShowDevelopment = false;
	private boolean onlyShowPackaged = false;
	private boolean ignoreTeamRobots = false;
	private String preSelectedRobots = null;
	private RobotNameCellRenderer robotNamesCellRenderer = null;
	private FileSpecificationVector selectedRobots = new FileSpecificationVector();
	private boolean showNumRoundsPanel = false;
	private RobotRepositoryManager robotManager = null;
	private boolean listBuilt = false;

	class EventHandler implements ActionListener, ListSelectionListener, KeyListener, HierarchyListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RobotSelectionPanel.this.getAddAllButton()) {
				addAllButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getAddButton()) {
				addButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getRemoveAllButton()) {
				removeAllButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getRemoveButton()) {
				removeButtonActionPerformed();
			}
		}
		;
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == true) {
				return;
			}
			
			if (e.getSource() == getSelectedRobotsList()) {
				selectedRobotsListSelectionChanged();
			}

			/*
			 if (e.getSource() == getAvailableRobotsList() || e.getSource() == getSelectedRobotsList())
			 {
			 JList list = (JList)e.getSource();
			 //log(e.getSource() + " selection changed to " + list.getMinSelectionIndex() + "," + list.getMaxSelectionIndex());
			 if (list.getMinSelectionIndex() == list.getMaxSelectionIndex())
			 {
			 RobotClassManager classManager = (RobotClassManager)list.getModel().getElementAt(e.getFirstIndex());
			 getDescriptionLabel().setText(classManager.getName());
			 }
			 }
			 */
		}

		public void keyPressed(KeyEvent e) {
			System.out.println("Key pressed: " + e);
		}

		public void keyReleased(KeyEvent e) {
			System.out.println("Key released: " + e);
		}

		public void keyTyped(KeyEvent e) {
			System.out.println("Key typed: " + e);
		}

		public void hierarchyChanged(HierarchyEvent e) {
			if (!listBuilt && isShowing()) {
				// log("Building robot list.");
				listBuilt = true;
				buildRobotList();
			}
		}
	}


	;
	
	private RobotSelectionPanel() {}

	/**
	 * NewBattleRobotsTab constructor comment.
	 */
	public RobotSelectionPanel(RobotRepositoryManager robotManager, int minRobots, int maxRobots, boolean showNumRoundsPanel, String instructions, boolean onlyShowSource, boolean onlyShowWithPackage, boolean onlyShowRobots, boolean onlyShowDevelopment, boolean onlyShowPackaged, boolean ignoreTeamRobots, String preSelectedRobots) {
		super();
		this.showNumRoundsPanel = showNumRoundsPanel;
		this.minRobots = minRobots;
		this.maxRobots = maxRobots;
		this.instructions = instructions;
		this.onlyShowSource = onlyShowSource;
		this.onlyShowWithPackage = onlyShowWithPackage;
		this.onlyShowRobots = onlyShowRobots;
		this.onlyShowDevelopment = onlyShowDevelopment;
		this.onlyShowPackaged = onlyShowPackaged;
		this.ignoreTeamRobots = ignoreTeamRobots;
		this.preSelectedRobots = preSelectedRobots;
		this.robotManager = robotManager;
		initialize();
		showInstructions();
	}

	/**
	 * Comment
	 */
	private void addAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		FileSpecificationVector availableRobots = availableRobotsPanel.getAvailableRobots();

		for (int i = 0; i < availableRobots.size(); i++) {
			selectedRobots.add(availableRobots.elementAt(i));
		}
		availableRobotsPanel.clearSelection();
	
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}
		return;
	}

	/**
	 * Comment
	 */
	private void addButtonActionPerformed() {
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) getSelectedRobotsList().getModel();

		FileSpecificationVector moves = availableRobotsPanel.getSelectedRobots();

		for (int i = 0; i < moves.size(); i++) {
			// if (moves.elementAt(i) instanceof RobotSpecification)
			selectedRobots.add(moves.elementAt(i));
			// else if (moves.elementAt(i) instanceof TeamSpecification)
			// {
			// log("Add team: " + moves.elementAt(i));
			// }
		}

		availableRobotsPanel.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}
		return;
	}

	/**
	 * Return the addAllButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getAddAllButton() {
		if (addAllButton == null) {
			try {
				addAllButton = new javax.swing.JButton();
				addAllButton.setName("addAllButton");
				addAllButton.setText("Add All ->");
				addAllButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return addAllButton;
	}

	/**
	 * Return the addButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getAddButton() {
		if (addButton == null) {
			try {
				addButton = new javax.swing.JButton();
				addButton.setName("addButton");
				addButton.setText("Add ->");
				addButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return addButton;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getAddButtonsPanel() {
		if (addButtonsPanel == null) {
			try {
				addButtonsPanel = new javax.swing.JPanel();
				addButtonsPanel.setName("addButtonsPanel");

				addButtonsPanel.setLayout(new java.awt.GridLayout(2, 1));
				
				addButtonsPanel.add(getAddButton());
				addButtonsPanel.add(getAddAllButton());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return addButtonsPanel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			try {
				buttonsPanel = new javax.swing.JPanel();
				buttonsPanel.setName("buttonsPanel");

				buttonsPanel.setLayout(new java.awt.BorderLayout(5, 5));
				buttonsPanel.setBorder(BorderFactory.createEmptyBorder(21, 5, 5, 5));
				buttonsPanel.add(getAddButtonsPanel(), java.awt.BorderLayout.NORTH);

				if (showNumRoundsPanel) {
					buttonsPanel.add(getNumRoundsPanel(), BorderLayout.CENTER);
				}
			
				buttonsPanel.add(getRemoveButtonsPanel(), java.awt.BorderLayout.SOUTH);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return buttonsPanel;
	}

	/**
	 * Return the removeAllButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getRemoveAllButton() {
		if (removeAllButton == null) {
			try {
				removeAllButton = new javax.swing.JButton();
				removeAllButton.setName("removeAllButton");
				removeAllButton.setText("<- Remove All");
				removeAllButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return removeAllButton;
	}

	/**
	 * Return the removeButton property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getRemoveButton() {
		if (removeButton == null) {
			try {
				removeButton = new javax.swing.JButton();
				removeButton.setName("removeButton");
				removeButton.setText("<- Remove");
				removeButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return removeButton;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getRemoveButtonsPanel() {
		if (removeButtonsPanel == null) {
			try {
				removeButtonsPanel = new javax.swing.JPanel();
				removeButtonsPanel.setName("removeButtonsPanel");

				removeButtonsPanel.setLayout(new java.awt.GridLayout(2, 1));
					
				removeButtonsPanel.add(getRemoveButton());
				removeButtonsPanel.add(getRemoveAllButton());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return removeButtonsPanel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 6:37:01 PM)
	 */
	public String getSelectedRobotsAsString() {
		String s = "";

		for (int i = 0; i < selectedRobots.size(); i++) {
			if (i != 0) {
				s += ",";
			}
			s += ((FileSpecification) selectedRobots.elementAt(i)).getNameManager().getUniqueFullClassNameWithVersion();
		}
		// System.out.println("Returning selected robots: " + s);
		return s;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 6:37:01 PM)
	 */
	public FileSpecificationVector getSelectedRobots() {
		return selectedRobots;
	}

	/**
	 * Return the selectedRobotsList.
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getSelectedRobotsList() {
		if (selectedRobotsList == null) {
			try {
				selectedRobotsList = new javax.swing.JList();
				selectedRobotsList.setName("JSelectedList");
				selectedRobotsList.setModel(new SelectedRobotsModel());
				selectedRobotsList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				robotNamesCellRenderer = new RobotNameCellRenderer();
				selectedRobotsList.setCellRenderer(robotNamesCellRenderer);
				MouseListener mouseListener = new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							removeButtonActionPerformed();
						}
						if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
							contextMenuActionPerformed();
						}
					}
				};

				selectedRobotsList.addMouseListener(mouseListener);
				selectedRobotsList.addListSelectionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return selectedRobotsList;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getSelectedRobotsPanel() {
		if (selectedRobotsPanel == null) {
			try {
				selectedRobotsPanel = new javax.swing.JPanel();
				selectedRobotsPanel.setName("selectedRobotsPanel");
				selectedRobotsPanel.setLayout(new java.awt.BorderLayout());
				selectedRobotsPanel.setPreferredSize(new Dimension(120, 100));
				selectedRobotsPanel.setBorder(
						BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Robots"));
				selectedRobotsPanel.add(getSelectedRobotsScrollPane(), java.awt.BorderLayout.CENTER);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return selectedRobotsPanel;
	}

	/**
	 * Return the selectedRobotsScrollPane property value.
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getSelectedRobotsScrollPane() {
		if (selectedRobotsScrollPane == null) {
			try {
				selectedRobotsScrollPane = new javax.swing.JScrollPane();
				selectedRobotsScrollPane.setName("selectedRobotsScrollPane");
				selectedRobotsScrollPane.setViewportView(getSelectedRobotsList());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return selectedRobotsScrollPane;
	}

	/**
	 * Return the Page property value.
	 * @return javax.swing.JPanel
	 */
	private void initialize() {
		try {
			setName("Robots");
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setLayout(new BorderLayout());
			add(getInstructionsLabel(), BorderLayout.NORTH);
			add(getMainPanel(), BorderLayout.CENTER);
			add(getDescriptionPanel(), BorderLayout.SOUTH);
			this.addHierarchyListener(eventHandler);
			this.setVisible(true);
		} catch (java.lang.Throwable e) {
			log(e);
		}
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
	 * Comment
	 */
	private void removeAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		selectedRobots.clear();
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		showInstructions();
		return;
	}

	private void contextMenuActionPerformed() {}

	/**
	 * Comment
	 */
	private void removeButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		Vector removeVec = new Vector();
		int sel[] = selectedList.getSelectedIndices();

		for (int i = 0; i < sel.length; i++) {
			selectedRobots.remove(sel[i] - i);
			// removeVec.add(selectedRobots.elementAt(sel[i]));
		}

		/* for (int i = 0; i < removeVec.size(); i++)
		 {
		 selectedRobots.remove(removeVec.elementAt(i));
		 }*/

		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() < minRobots || selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		} else {
			showInstructions();
		}
		return;
	}

	class RobotNameCellRenderer extends JLabel implements ListCellRenderer {

		private boolean useShortNames = false;
		public RobotNameCellRenderer() {
			setOpaque(true);
		}

		public void setUseShortNames(boolean useShortNames) {
			this.useShortNames = useShortNames;
		}

		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			setComponentOrientation(list.getComponentOrientation());
		
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			if (useShortNames && value instanceof FileSpecification) {
				FileSpecification robotSpecification = (FileSpecification) value;

				if (value instanceof TeamSpecification) {
					setText("Team: " + robotSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				} else {
					setText(robotSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				}
			} else if (value instanceof FileSpecification) {
				FileSpecification robotSpecification = (FileSpecification) value;

				if (value instanceof TeamSpecification) {
					setText("Team: " + robotSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				} else {
					setText(robotSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				}
			} else {
				setText("??" + value.toString());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());
		
			// setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
	}


	class SelectedRobotsModel extends javax.swing.AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}
	
		public int getSize() {
			return selectedRobots.size();
		}

		public Object getElementAt(int which) {
			return selectedRobots.elementAt(which);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/30/2001 1:38:51 PM)
	 * @param robocode robocode.Robocode
	 */
	public void buildRobotList() {
		new Thread(new Runnable() {
			public void run() {
				// try {Thread.sleep(100);} catch (InterruptedException e) {}
				getAvailableRobotsPanel().setRobotList(null); // new FileSpecificationVector());
				// System.out.println("nulled it.");
				FileSpecificationVector v = RobotSelectionPanel.this.robotManager.getRobotRepository().getRobotSpecificationsVector(onlyShowSource, onlyShowWithPackage, onlyShowRobots, onlyShowDevelopment, onlyShowPackaged, ignoreTeamRobots);

				// System.out.println("got it.");
				getAvailableRobotsPanel().setRobotList(v);
				// System.out.println("set it.");
			
				if (selectedRobots != null && !selectedRobots.equals("")) {
					// System.out.println("setting selected.");
					setSelectedRobots(getAvailableRobotsPanel().getRobotList(), preSelectedRobots);
					preSelectedRobots = null;
				}
				// System.out.println("Done building robot list.");
			}
		}).start();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	public AvailableRobotsPanel getAvailableRobotsPanel() {
		if (availableRobotsPanel == null) {
			try {
				availableRobotsPanel = new AvailableRobotsPanel(getAddButton(), "Available Robots",
						getSelectedRobotsList(), this);
				availableRobotsPanel.setName("availableRobotsPanel");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return availableRobotsPanel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private RobotDescriptionPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			try {
				descriptionPanel = new RobotDescriptionPanel(robotManager.getManager());
				descriptionPanel.setName("descriptionPanel");
				descriptionPanel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
				// descriptionPanel.setPreferredSize(new Dimension(descriptionPanel.getPreferredSize,66));
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return descriptionPanel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			try {
				instructionsLabel = new javax.swing.JLabel();
				instructionsLabel.setName("instructionsLabel");
				if (instructions != null) {
					instructionsLabel.setText(instructions);
				}
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return instructionsLabel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			try {
				mainPanel = new javax.swing.JPanel();
				mainPanel.setName("mainPanel");

				mainPanel.setPreferredSize(new Dimension(550, 300));
				GridBagLayout layout = new GridBagLayout();

				mainPanel.setLayout(layout);
			
				GridBagConstraints constraints = new GridBagConstraints();

				constraints.fill = java.awt.GridBagConstraints.BOTH;
				constraints.weightx = 2;
				constraints.weighty = 1;
				constraints.anchor = GridBagConstraints.NORTHWEST;
				constraints.gridwidth = 2;
				layout.setConstraints(getAvailableRobotsPanel(), constraints);
				mainPanel.add(getAvailableRobotsPanel());

				constraints.gridwidth = 1;
				constraints.weightx = 0;
				constraints.weighty = 0;
				constraints.anchor = GridBagConstraints.CENTER;
				layout.setConstraints(getButtonsPanel(), constraints);
				mainPanel.add(getButtonsPanel());

				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.weightx = 1;
				constraints.weighty = 1;
				constraints.anchor = GridBagConstraints.NORTHWEST;
				layout.setConstraints(getSelectedRobotsPanel(), constraints);
				mainPanel.add(getSelectedRobotsPanel());

			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return mainPanel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 7:59:58 PM)
	 * @return int
	 */
	public int getNumRounds() {
		try {
			return Integer.parseInt(getNumRoundsTextField().getText());
		} catch (NumberFormatException e) {
			return 10;
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 8:41:29 PM)
	 */
	private JPanel getNumRoundsPanel() {
		if (numRoundsPanel == null) {
			try {
				numRoundsPanel = new javax.swing.JPanel();
				numRoundsPanel.setName("numRoundsPanel");
				numRoundsPanel.setLayout(new BoxLayout(numRoundsPanel, BoxLayout.Y_AXIS));
				numRoundsPanel.setBorder(BorderFactory.createEmptyBorder());
				numRoundsPanel.add(new JPanel());
			
				JPanel j = new JPanel();

				j.setLayout(new BoxLayout(j, BoxLayout.Y_AXIS));
				TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
						"Number of Rounds");

				j.setBorder(border);
				j.add(getNumRoundsTextField());
				// System.out.println(border.getMinimumSize(j));
				j.setPreferredSize(new Dimension(border.getMinimumSize(j).width, j.getPreferredSize().height));
				j.setMinimumSize(j.getPreferredSize());
				j.setMaximumSize(j.getPreferredSize());
				// j.setMaximumSize(new Dimension(150,50));
				// j.setMinimumSize(new Dimension(150,50));
				// j.setPreferredSize(new Dimension(150,50));
				numRoundsPanel.add(j);
				numRoundsPanel.add(new JPanel());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return numRoundsPanel;
	}

	/**
	 * Return the numRoundsTextField
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getNumRoundsTextField() {
		if (numRoundsTextField == null) {
			try {
				numRoundsTextField = new javax.swing.JTextField();
				numRoundsTextField.setName("numRoundsTextField");
				numRoundsTextField.setAutoscrolls(false);
				numRoundsTextField.setPreferredSize(new Dimension(50, 16));
				numRoundsTextField.setMaximumSize(new Dimension(50, 16));
				numRoundsTextField.setMinimumSize(new Dimension(50, 16));
				// Center in panel
				numRoundsTextField.setAlignmentX((float) .5);
				// Center text in textfield
				numRoundsTextField.setHorizontalAlignment(JTextField.CENTER);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return numRoundsTextField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 6:37:01 PM)
	 */
	public int getSelectedRobotsCount() {
		return selectedRobots.size();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:44:02 PM)
	 * @return boolean
	 */
	public boolean isReady() {
		return (getSelectedRobotsCount() >= minRobots && getSelectedRobotsCount() <= maxRobots);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/30/2001 1:38:51 PM)
	 * @param robocode robocode.Robocode
	 */
	public void refreshRobotList() {
		getAvailableRobotsPanel().setRobotList(null); // new FileSpecificationVector());
		RobotSelectionPanel.this.robotManager.clearRobotList();
		buildRobotList();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 3:53:26 PM)
	 */
	private void selectedRobotsListSelectionChanged() {
		int sel[] = getSelectedRobotsList().getSelectedIndices();

		if (sel.length == 1) {
			availableRobotsPanel.clearSelection();
			FileSpecification robotSpecification = (FileSpecification) getSelectedRobotsList().getModel().getElementAt(
					sel[0]);

			showDescription(robotSpecification);
		} else {
			showDescription(null);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/26/2001 7:59:58 PM)
	 * @return int
	 */
	public void setNumRounds(int numRounds) {
		getNumRoundsTextField().setText("" + numRounds);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 2:47:58 PM)
	 * @param selectedRobotsString java.lang.String
	 */
	private void setSelectedRobots(FileSpecificationVector robotList, String selectedRobotsString) {
		if (selectedRobotsString != null) {
			StringTokenizer tokenizer;

			tokenizer = new StringTokenizer(selectedRobotsString, ",");
			if (robotList == null) {
				log(new RuntimeException("Cannot add robots to a null robots list!"));
				return;
			}
			this.selectedRobots.clear();
			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				for (int i = 0; i < robotList.size(); i++) {
					if (((FileSpecification) robotList.elementAt(i)).getNameManager().getUniqueFullClassNameWithVersion().equals(
							bot)) {
						this.selectedRobots.add(robotList.elementAt(i));
						break;
					}
				}
			}
		}
		((SelectedRobotsModel) getSelectedRobotsList().getModel()).changed();
		fireStateChanged();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 5:11:50 PM)
	 * @param classManager robocode.peer.robot.RobotClassManager
	 */
	public void showDescription(FileSpecification robotSpecification) {
		getDescriptionPanel().showDescription(robotSpecification);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 6:06:46 PM)
	 */
	public void showInstructions() {
		if (instructions != null) {
			instructionsLabel.setText(instructions);
			instructionsLabel.setVisible(true);
		} else {
			instructionsLabel.setVisible(false);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 6:06:46 PM)
	 */
	public void showWrongNumInstructions() {

		if (minRobots == maxRobots) {
			if (minRobots == 1) {
				instructionsLabel.setText("Please select exactly 1 robot.");
			} else {
				instructionsLabel.setText("Please select exactly " + minRobots + " robots.");
			}
		} else {
			instructionsLabel.setText("Please select between " + minRobots + " and " + maxRobots + " robots.");
		}
		instructionsLabel.setVisible(true);
	}
}
