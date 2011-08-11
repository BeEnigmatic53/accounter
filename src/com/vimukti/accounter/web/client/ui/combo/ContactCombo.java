package com.vimukti.accounter.web.client.ui.combo;

import com.vimukti.accounter.web.client.core.ClientContact;
import com.vimukti.accounter.web.client.ui.customers.AddNewContactDialog;

public class ContactCombo extends CustomCombo<ClientContact> {

	public ContactCombo(String title) {
		super(title, false, 1);
	}

	public ContactCombo(String contact, boolean b) {
		super(contact, b, 1);
	}

	@Override
	public String getDefaultAddNewCaption() {
		return "-- Add New Contact --";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAddNew() {
		AddNewContactDialog addNewContactDialog = new AddNewContactDialog("contact ",
				" ");

		addNewContactDialog.addCallBack(createAddNewCallBack());
		addNewContactDialog.hide();
		addNewContactDialog.showAddEditTermDialog(null);
	}

	public void setDefaultToFirstOption(boolean b) {
		if (b) {
			this.setComboItem(comboItems.get(0));
		}
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
	}

	@Override
	protected String getDisplayName(ClientContact object) {
		if (object != null)
			return object.getName().toString() != null ? object.getName()
					.toString() : "";
		else
			return "";
	}

	@Override
	protected String getColumnData(ClientContact object, int row, int col) {
		switch (col) {
		case 0:
			return object.getName().toString();
		}
		return null;
	}
}
