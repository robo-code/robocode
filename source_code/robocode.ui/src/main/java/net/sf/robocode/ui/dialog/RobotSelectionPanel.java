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
 *     - Added keyboard mnemonics to buttons
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Code cleanup
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Bugfixed buildRobotList(), which checked if selectedRobots was set
 *       instead of preSelectedRobots. Robots were not selected when loading a
 *       battle
 *     - Replaced synchronizedList on list for selectedRobots with a
 *       CopyOnWriteArrayList in order to prevent ConcurrentModificationException
 *       when accessing this list via Iterators using public methods to this
 *       class
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class RobotSelectionPanel extends WizardPanel {

	private AvailableRobotsPanel availableRobotsPanel;

	private JPanel selectedRobotsPanel;
	private JScrollPane selectedRobotsScrollPane;
	private JList selectedRobotsList;

	private JPanel buttonsPanel;
	private JPanel addButtonsPanel;
	private JPanel removeButtonsPanel;
	private JButton addButton;
	private JButton addAllButton;
	private JButton removeButton;
	private JButton removeAllButton;

	private final EventHandler eventHandler = new EventHandler();
	private RobotDescriptionPanel descriptionPanel;
	private String instructions;
	private JLabel instructionsLabel;
	private JPanel mainPanel;
	private int maxRobots = 1;
	private int minRobots = 1;
	private JPanel numRoundsPanel;
	private JTextField numRoundsTextField;
	private boolean onlyShowSource;
	private boolean onlyShowWithPackage;
	private boolean onlyShowRobots;
	private boolean onlyShowDevelopment;
	private boolean onlyShowInJar;
	private boolean ignoreTeamRobots;
	private String preSelectedRobots;
	private final List<AvailableRobotsPanel.ItemWrapper> selectedRobots = new ArrayList<AvailableRobotsPanel.ItemWrapper>();
	private boolean showNumRoundsPanel;
	private final ISettingsManager properties;
	private final IRepositoryManager repositoryManager;
	private boolean nummerOfRoundSetByGame;

	public RobotSelectionPanel(ISettingsManager properties, IRepositoryManager repositoryManager) {
		super();
		this.properties = properties;
		this.repositoryManager = repositoryManager;
	}

	public void setup(int minRobots, int maxRobots,
			boolean showNumRoundsPanel, String instructions, boolean onlyShowSource, boolean onlyShowWithPackage,
			boolean onlyShowRobots, boolean onlyShowDevelopment, boolean onlyShowInJar, boolean ignoreTeamRobots,
			String preSelectedRobots) {
		this.showNumRoundsPanel = showNumRoundsPanel;
		this.minRobots = minRobots;
		this.maxRobots = maxRobots;
		this.instructions = instructions;
		this.onlyShowSource = onlyShowSource;
		this.onlyShowWithPackage = onlyShowWithPackage;
		this.onlyShowRobots = onlyShowRobots;
		this.onlyShowDevelopment = onlyShowDevelopment;
		this.onlyShowInJar = onlyShowInJar;

		this.ignoreTeamRobots = ignoreTeamRobots;
		this.preSelectedRobots = preSelectedRobots;
		initialize();
	}

	private class EventHandler implements ActionListener, ListSelectionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddAllButton()) {
				addAllButtonActionPerformed();
			} else if (e.getSource() == getAddButton()) {
				addButtonActionPerformed();
			} else if (e.getSource() == getRemoveAllButton()) {
				removeAllButtonActionPerformed();
			} else if (e.getSource() == getRemoveButton()) {
				removeButtonActionPerformed();
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getSelectedRobotsList()) {
				selectedRobotsListSelectionChanged();
			}
		}
	}

	private void addAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		for (AvailableRobotsPanel.ItemWrapper selected : availableRobotsPanel.getAvailableRobots()) {
			selectedRobots.add(selected);
		}

		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}

		availableRobotsPanel.getAvailableRobotsList().requestFocus();
	}

	private void addButtonActionPerformed() {
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) getSelectedRobotsList().getModel();
		List<AvailableRobotsPanel.ItemWrapper> moves = availableRobotsPanel.getSelectedRobots();

		for (AvailableRobotsPanel.ItemWrapper move : moves) {
			selectedRobots.add(new AvailableRobotsPanel.ItemWrapper(move.getItem()));
		}

		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}

		availableRobotsPanel.getAvailableRobotsList().requestFocus();
	}

	private JButton getAddAllButton() {
		if (addAllButton == null) {
			addAllButton = new JButton();
			addAllButton.setText("Add All ->");
			addAllButton.setMnemonic('l');
			addAllButton.setDisplayedMnemonicIndex(5);
			addAllButton.addActionListener(eventHandler);
		}
		return addAllButton;
	}

	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add ->");
			addButton.setMnemonic('A');
			addButton.addActionListener(eventHandler);
		}
		return addButton;
	}

	private JPanel getAddButtonsPanel() {
		if (addButtonsPanel == null) {
			addButtonsPanel = new JPanel();
			addButtonsPanel.setLayout(new GridLayout(2, 1));
			addButtonsPanel.add(getAddButton());
			addButtonsPanel.add(getAddAllButton());
		}
		return addButtonsPanel;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new BorderLayout(5, 5));
			buttonsPanel.setBorder(BorderFactory.createEmptyBorder(21, 5, 5, 5));
			buttonsPanel.add(getAddButtonsPanel(), BorderLayout.NORTH);
			if (showNumRoundsPanel) {
				buttonsPanel.add(getNumRoundsPanel(), BorderLayout.CENTER);
			}
			buttonsPanel.add(getRemoveButtonsPanel(), BorderLayout.SOUTH);
		}
		return buttonsPanel;
	}

	private JButton getRemoveAllButton() {
		if (removeAllButton == null) {
			removeAllButton = new JButton();
			removeAllButton.setText("<- Remove All");
			removeAllButton.setMnemonic('v');
			removeAllButton.setDisplayedMnemonicIndex(7);
			removeAllButton.addActionListener(eventHandler);
		}
		return removeAllButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("<- Remove");
			removeButton.setMnemonic('m');
			removeButton.setDisplayedMnemonicIndex(5);
			removeButton.addActionListener(eventHandler);
		}
		return removeButton;
	}

	private JPanel getRemoveButtonsPanel() {
		if (removeButtonsPanel == null) {
			removeButtonsPanel = new JPanel();
			removeButtonsPanel.setLayout(new GridLayout(2, 1));
			removeButtonsPanel.add(getRemoveButton());
			removeButtonsPanel.add(getRemoveAllButton());
		}
		return removeButtonsPanel;
	}

	public String getSelectedRobotsAsString() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < selectedRobots.size(); i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(selectedRobots.get(i).getItem().getUniqueFullClassNameWithVersion());
		}
		return sb.toString();
	}

	public List<IRepositoryItem> getSelectedRobots() {
		List<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (AvailableRobotsPanel.ItemWrapper item : selectedRobots) {
			res.add(item.getItem());
		}
		return res;
	}

	private JList getSelectedRobotsList() {
		if (selectedRobotsList == null) {
			selectedRobotsList = new JList();
			selectedRobotsList.setModel(new SelectedRobotsModel());
			selectedRobotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						removeButtonActionPerformed();
					}
					if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
						contextMenuActionPerformed();
					}
				}
			};

			selectedRobotsList.addMouseListener(mouseListener);
			selectedRobotsList.addListSelectionListener(eventHandler);
		}
		return selectedRobotsList;
	}

	private JPanel getSelectedRobotsPanel() {
		if (selectedRobotsPanel == null) {
			selectedRobotsPanel = new JPanel();
			selectedRobotsPanel.setLayout(new BorderLayout());
			selectedRobotsPanel.setPreferredSize(new Dimension(120, 100));
			selectedRobotsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Robots"));
			selectedRobotsPanel.add(getSelectedRobotsScrollPane(), BorderLayout.CENTER);
		}
		return selectedRobotsPanel;
	}

	private JScrollPane getSelectedRobotsScrollPane() {
		if (selectedRobotsScrollPane == null) {
			selectedRobotsScrollPane = new JScrollPane();
			selectedRobotsScrollPane.setViewportView(getSelectedRobotsList());
		}
		return selectedRobotsScrollPane;
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		add(getInstructionsLabel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getDescriptionPanel(), BorderLayout.SOUTH);
		setVisible(true);
		showInstructions();
		refreshRobotList(false);
	}

	private void removeAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		selectedRobots.clear();
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		showInstructions();
	}

	private void contextMenuActionPerformed() {}

	private void removeButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();
		int sel[] = selectedList.getSelectedIndices();

		for (int i = 0; i < sel.length; i++) {
			selectedRobots.remove(sel[i] - i);
		}
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() < minRobots || selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		} else {
			showInstructions();
		}
	}

	class SelectedRobotsModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return selectedRobots.size();
		}

		public Object getElementAt(int which) {
			return selectedRobots.get(which);
		}
	}

	public AvailableRobotsPanel getAvailableRobotsPanel() {
		if (availableRobotsPanel == null) {
			availableRobotsPanel = new AvailableRobotsPanel(getAddButton(), "Available Robots", getSelectedRobotsList(),
					this);
		}
		return availableRobotsPanel;
	}

	private RobotDescriptionPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			descriptionPanel = new RobotDescriptionPanel();
			descriptionPanel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
		}
		return descriptionPanel;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			if (instructions != null) {
				instructionsLabel.setText(instructions);
			}
		}
		return instructionsLabel;
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(550, 300));
			GridBagLayout layout = new GridBagLayout();

			mainPanel.setLayout(layout);

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.fill = GridBagConstraints.BOTH;
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
		}
		return mainPanel;
	}

	public int getNumRounds() {
		try {
			return Integer.parseInt(getNumRoundsTextField().getText());
		} catch (NumberFormatException e) {
			int numRounds = properties.getNumberOfRounds();

			getNumRoundsTextField().setText("" + numRounds);
			return numRounds;
		}
	}

	private JPanel getNumRoundsPanel() {
		if (numRoundsPanel == null) {
			numRoundsPanel = new JPanel();
			numRoundsPanel.setLayout(new BoxLayout(numRoundsPanel, BoxLayout.Y_AXIS));
			numRoundsPanel.setBorder(BorderFactory.createEmptyBorder());
			numRoundsPanel.add(new JPanel());
			JPanel j = new JPanel();

			j.setLayout(new BoxLayout(j, BoxLayout.Y_AXIS));
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					"Number of Rounds");

			j.setBorder(border);
			j.add(getNumRoundsTextField());
			j.setPreferredSize(new Dimension(border.getMinimumSize(j).width, j.getPreferredSize().height));
			j.setMinimumSize(j.getPreferredSize());
			j.setMaximumSize(j.getPreferredSize());
			numRoundsPanel.add(j);
			numRoundsPanel.add(new JPanel());
		}
		return numRoundsPanel;
	}

	private JTextField getNumRoundsTextField() {
		final ISettingsManager props = properties;

		if (numRoundsTextField == null) {
			numRoundsTextField = new JTextField();
			numRoundsTextField.setAutoscrolls(false);

			// Center in panel
			numRoundsTextField.setAlignmentX((float) .5);
			// Center text in text field
			numRoundsTextField.setHorizontalAlignment(SwingConstants.CENTER);

			// Add document listener
			numRoundsTextField.getDocument().addDocumentListener(new DocumentListener() {

				public void changedUpdate(DocumentEvent e) {}

				public void insertUpdate(DocumentEvent e) {
					handleChange();
				}

				public void removeUpdate(DocumentEvent e) {
					handleChange();
				}

				private void handleChange() {
					// Ignore the change if the 'number of rounds' was set by the game, not the user
					if (nummerOfRoundSetByGame) {
						return;
					}
					
					// Here we assume that the user made the change
					try {
						int numRounds = Integer.parseInt(numRoundsTextField.getText());

						props.setNumberOfRounds(numRounds); // Update the user settings
					} catch (NumberFormatException ignored) {}
				}
			});
		}

		return numRoundsTextField;
	}

	public int getSelectedRobotsCount() {
		return selectedRobots.size();
	}

	@Override
	public boolean isReady() {
		return (getSelectedRobotsCount() >= minRobots && getSelectedRobotsCount() <= maxRobots);
	}

	public void refreshRobotList(final boolean withClear) {

		final Runnable runnable = new Runnable() {
			public void run() {
				try {
					setBusyPointer(true);
					repositoryManager.refresh(withClear);

					List<IRepositoryItem> robotList = repositoryManager.filterRepositoryItems(onlyShowSource,
							onlyShowWithPackage, onlyShowRobots, onlyShowDevelopment, false, ignoreTeamRobots, onlyShowInJar);

					getAvailableRobotsPanel().setRobotList(robotList);
					if (preSelectedRobots != null && preSelectedRobots.length() > 0) {
						setSelectedRobots(preSelectedRobots);
						preSelectedRobots = null;
					}
				} finally {
					setBusyPointer(false);
				}
			}
		};

		SwingUtilities.invokeLater(runnable);
	}

	private static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	private void setBusyPointer(boolean enabled) {
		setCursor(enabled ? BUSY_CURSOR : DEFAULT_CURSOR);
	}

	private void selectedRobotsListSelectionChanged() {
		int sel[] = getSelectedRobotsList().getSelectedIndices();

		if (sel.length == 1) {
			availableRobotsPanel.clearSelection();
			IRepositoryItem robotSpecification = ((AvailableRobotsPanel.ItemWrapper) getSelectedRobotsList().getModel().getElementAt(sel[0])).getItem();

			showDescription(robotSpecification);
		} else {
			showDescription(null);
		}
	}

	public void setNumRounds(int numRounds) {
		// Set flag that 'number of rounds' was set by the game, not user
		nummerOfRoundSetByGame = true;

		getNumRoundsTextField().setText("" + numRounds);

		// Clear flag for 'number of rounds'
		nummerOfRoundSetByGame = false;
	}

	private void setSelectedRobots(String selectedRobotsString) {
		if (selectedRobotsString != null) {
			for (IRepositoryItem item: repositoryManager.getSelectedSpecifications(selectedRobotsString)) {
				this.selectedRobots.add(new AvailableRobotsPanel.ItemWrapper(item));
			}
		}
		((SelectedRobotsModel) getSelectedRobotsList().getModel()).changed();
		fireStateChanged();
	}

	public void showDescription(IRepositoryItem robotSpecification) {
		getDescriptionPanel().showDescription(robotSpecification);
	}

	public void showInstructions() {
		if (instructions != null) {
			instructionsLabel.setText(instructions);
			instructionsLabel.setVisible(true);
		} else {
			instructionsLabel.setVisible(false);
		}
	}

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
