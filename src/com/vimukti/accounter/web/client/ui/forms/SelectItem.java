package com.vimukti.accounter.web.client.ui.forms;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class SelectItem extends FormItem {

	ListBox listBox;
	LinkedHashMap<String, String> propertyValueHashMap;

	@Override
	public Object getValue() {
		if (listBox.getSelectedIndex() >= 0)
			return listBox.getValue(listBox.getSelectedIndex());
		return null;
	}

	@Override
	public String getDisplayValue() {
		return listBox.getItemText(listBox.getSelectedIndex() != -1 ? listBox
				.getSelectedIndex() : 0);

	}

	public SelectItem(String title) {
		setTitle(title);
		listBox = new ListBox();
		this.listBox.setTitle(title);
		this.listBox.setWidth("100%");
	}

	public SelectItem() {
		listBox = new ListBox();
		this.listBox.setWidth("50%");
	}

	@Override
	public void setDefaultValue(String string) {
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.getItemText(i).equalsIgnoreCase(string)) {
				listBox.setItemSelected(i, true);
				return;
			}
		}
	}

	public void setSelected(String string) {
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.getItemText(i).equalsIgnoreCase(string)) {
				listBox.setItemSelected(i, true);
				return;
			}
		}
	}

	public void setHint(String string) {
		// NOTHING TO DO.
	}

	
	public void setMultiple(boolean b) {
		this.listBox.setMultipleSelect(b);

	}

	@Override
	public void setValue(Object value) {
		int valuesCount = this.listBox.getItemCount();
		for (int i = 0; i < valuesCount; i++) {
			if (this.listBox.getItemText(i).equals(value))
				this.listBox.setSelectedIndex(i);
		}
		if (this.listBox.getSelectedIndex() == -1) {
			this.listBox.addItem((String) value);
			this.listBox.setSelectedIndex(0);

		}
	}

	public void setItemValue(Object value) {
		int valuesCount = this.listBox.getItemCount();
		for (int i = 0; i < valuesCount; i++) {
			if (this.listBox.getValue(i).equals(value)) {
				this.listBox.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	public void setValueMap(String... values) {

		listBox.clear();
		for (final String valueString : values) {
			listBox.addItem(valueString);

		}
	}

	public void setValueMap(LinkedHashMap<String, String> propertyvalueMap) {
		listBox.clear();
		this.propertyValueHashMap = propertyvalueMap;
		int index = 0;
		for (Entry<String, String> propertyValue : propertyvalueMap.entrySet()) {
			this.listBox.addItem(propertyValue.getValue());
			index++;
		}

	}

	@Override
	public void addChangeHandler(ChangeHandler changeHandler) {
		this.listBox.addChangeHandler(changeHandler);

	}

	public void setDefaultToFirstOption(boolean b) {
		if (b) {
			// this.listBox.removeItem(0);
			this.listBox.setSelectedIndex(0);
		}
	}

	@Override
	public Widget getMainWidget() {
		return this.listBox;
	}

	// @Override
	// public boolean validate() {
	// return true;
	//
	// }

	public int getSelectedIndex() {
		return this.listBox.getSelectedIndex() != -1 ? this.listBox
				.getSelectedIndex() : 0;

	}

	public String getValue(int index) {
		return this.listBox.getItemText(index);
	}

	@Override
	public void setDisabled(boolean b) {
		// this.getMainWidget().setEnabled(!b);
		this.listBox.setEnabled(!b);
	}

	public void addChangedHandler(ChangeHandler changeHandler) {
		this.listBox.addChangeHandler(changeHandler);
	}
}
