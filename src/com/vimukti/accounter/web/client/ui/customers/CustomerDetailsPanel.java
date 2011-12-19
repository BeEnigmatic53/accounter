package com.vimukti.accounter.web.client.ui.customers;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.forms.AmountLabel;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.LabelItem;

public class CustomerDetailsPanel extends VerticalPanel {
	ClientCustomer selectedCustomer;
	LabelItem name, email, currency, fax, customersince, webpageadress;
	AmountLabel balance, openingBalance;
	protected static final AccounterMessages messages = Global.get().messages();

	public CustomerDetailsPanel(ClientCustomer clientCustomer) {
		this.selectedCustomer = clientCustomer;
		createControls();
		if (clientCustomer != null) {
			showCustomerDetails(clientCustomer);
		}

	}

	private void createControls() {

		name = new LabelItem();
		name.setTitle(messages.name());

		email = new LabelItem();
		email.setTitle(messages.email());

		balance = new AmountLabel(messages.balance());

		currency = new LabelItem();
		currency.setTitle(messages.currency());

		fax = new LabelItem();
		fax.setTitle(messages.faxNumber());

		customersince = new LabelItem();
		customersince.setTitle(messages.payeeSince(Global.get().Customer()));

		webpageadress = new LabelItem();
		webpageadress.setTitle(messages.webPageAddress());

		openingBalance = new AmountLabel(messages.balanceAsOf());

		DynamicForm leftform = new DynamicForm();
		DynamicForm rightform = new DynamicForm();

		leftform.setFields(name, balance, openingBalance, currency);

		rightform.setFields(customersince, email, fax, webpageadress);

		HorizontalPanel hp = new HorizontalPanel();
		HorizontalPanel headingPanel = new HorizontalPanel();
		headingPanel.addStyleName("customers_detail_panel");
		Label heading = new Label(messages.payeeDetails(Global.get()
				.Customers()));
		headingPanel.add(heading);
		add(headingPanel);
		hp.add(leftform);
		hp.add(rightform);
		leftform.setCellSpacing(10);
		rightform.setCellSpacing(10);
		hp.setCellWidth(leftform, "50%");
		hp.setCellWidth(rightform, "50%");

		add(hp);
		headingPanel.setWidth("100%");
		hp.setWidth("100%");
		this.setWidth("100%");
		hp.getElement().getParentElement().addClassName("details-Panel");
	}

	protected void showCustomerDetails(ClientCustomer selectedCustomer) {
		if (selectedCustomer != null) {

			name.setValue(selectedCustomer.getName());

			email.setValue(selectedCustomer.getEmail());

			balance.setAmount(selectedCustomer.getBalance());

			currency.setValue(Accounter.getCompany()
					.getCurrency(selectedCustomer.getCurrency())
					.getFormalName());

			fax.setValue(selectedCustomer.getFaxNo());

			customersince.setValue(new ClientFinanceDate(selectedCustomer
					.getPayeeSince()).toString());

			webpageadress.setValue(selectedCustomer.getWebPageAddress());

			openingBalance.setAmount(selectedCustomer.getOpeningBalance());

		}
	}

	public void setCustomer(ClientCustomer customer) {
		this.selectedCustomer = customer;
	}

	public ClientCustomer getCustomer() {
		return selectedCustomer;
	}

}