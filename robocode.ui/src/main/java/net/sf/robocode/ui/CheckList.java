/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.sf.robocode.util.AlphanumericComparator;


/**
 * A JList containing JCheckBox items.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class CheckList extends JList {

	private DefaultListModel model;

	public CheckList() {
		super();

		initialize();
	}

	private void initialize() {
		model = new DefaultListModel();
		setModel(model);

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new CheckListRenderer());

		// Add a mouse listener to handle changing selection
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				JList list = (JList) event.getSource();

				// Get index of item clicked
				int index = list.locationToIndex(event.getPoint());

				if (index >= 0) {
					if (event.getClickCount() == 2) {		
						CheckListItem item = (CheckListItem) model.getElementAt(index);

						// Toggle selected state
						item.setSelected(!item.isSelected());

						// Repaint cell
						list.repaint(list.getCellBounds(index, index));
					}
				}
			}
		});
	}

	public void clear() {
		model.clear();
	}

	public int getLength() {
		return model.getSize();
	}

	public String get(int index) {
		return ((CheckListItem) model.getElementAt(index)).toString();
	}

	public void add(String label) {
		model.addElement(new CheckListItem(label));
	}

	public void remove(int index) {
		model.remove(index);
	}
	
	public boolean contains(String label) {
		if (label == null) {
			return false;
		}
		for (int i = 0; i < model.getSize(); i++) {
			CheckListItem item = (CheckListItem) model.getElementAt(i);

			if (label.equals(item.toString())) {
				return true;
			}
		}
		return false;
	}

	public void sort() {
		int size = model.getSize();

		if (size > 0) {
			CheckListItem[] items = new CheckListItem[size];

			model.copyInto(items);
			Arrays.sort(items);

			for (int i = 0; i < items.length; i++) {
				model.setElementAt(items[i], i);
			}
		}
	}

	public void setChecked(String label, boolean isChecked) {
		if (label != null) {
			for (int i = 0; i < model.getSize(); i++) {
				CheckListItem item = (CheckListItem) model.getElementAt(i);

				if (label.equals(item.toString())) {
					item.setSelected(isChecked);
					break;
				}
			}
		}
	}
	
	public Collection<String> getUnchecked() {
		Collection<String> unchecked = new HashSet<String>();

		for (int i = 0; i < model.getSize(); i++) {
			CheckListItem item = (CheckListItem) model.getElementAt(i);

			if (!item.isSelected()) {
				unchecked.add(item.toString());
			}
		}
		return unchecked;
	}
	
	public Collection<String> getAll() {
		Collection<String> all = new HashSet<String>();

		for (int i = 0; i < model.getSize(); i++) {
			CheckListItem item = (CheckListItem) model.getElementAt(i);

			all.add(item.toString());
		}
		return all;
	}
}


/**
 * Represents items in the list that can be selected.
 */
class CheckListItem implements Cloneable, Comparable<CheckListItem> {
	private String  label;
	private boolean isSelected = true;

	public CheckListItem(String label) {
		this.label = label;
	}

	private CheckListItem(CheckListItem item) {
		label = item.label;
		isSelected = item.isSelected;
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	// Must be here
	public String toString() {
		return label;
	}

	// Must be here (for sorting)
	public Object clone() {
		return new CheckListItem(this);
	}

	// Must be here (for sorting)
	public int compareTo(CheckListItem item) {
		return new AlphanumericComparator().compare(label, item.label);
	}
}


/**
 * CheckListRenderer handles rendering cells in the list using a check box.
 */
@SuppressWarnings("serial")
class CheckListRenderer extends JCheckBox implements ListCellRenderer {
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
		setEnabled(list.isEnabled());
		setSelected(((CheckListItem) value).isSelected());
		setFont(list.getFont());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());			
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setText(value.toString());
		return this;
	}
}
