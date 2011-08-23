package com.vimukti.accounter.web.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.ValueCallBack;
import com.vimukti.accounter.web.client.core.ClientContact;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientVendor;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.VendorCombo;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.CheckboxItem;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.TextItem;

/**
 * 
 * @author Sai Prasad N
 *
 */
public class VendorMergeDialog extends BaseDialog<ClientCustomer> implements
		AsyncCallback<Void> {

	private DynamicForm form;
	private DynamicForm form1;
	private ValueCallBack<ClientContact> successCallback;
	private VendorCombo vendorCombo;
	private VendorCombo vendorCombo1;
	private TextItem vendorIDTextItem;
	private TextItem vendorIDTextItem1;

	private CheckboxItem status;
	private CheckboxItem status1;
	private TextItem balanceTextItem1;
	private TextItem balanceTextItem;

	private ClientVendor fromclientVendor;
	private ClientVendor toClientVendor;

	public VendorMergeDialog(String title, String descript) {
		super(title, descript);
		setWidth("650px");
		okbtn.setText("Merge");
		createControls();
		center();
	}

	private void createControls() {
		form = new DynamicForm();
		form1 = new DynamicForm();
		form.setWidth("100%");
		form.setHeight("100%");
		form1.setHeight("100%");
		form1.setWidth("100%");
		VerticalPanel layout = new VerticalPanel();
		VerticalPanel layout1 = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		vendorCombo = createCustomerCombo();
		vendorCombo1 = createCustomerCombo1();

		vendorIDTextItem = new TextItem("CustomerID");
		vendorIDTextItem.setHelpInformation(true);

		vendorIDTextItem1 = new TextItem("CustomerID");
		vendorIDTextItem1.setHelpInformation(true);
		vendorIDTextItem.setDisabled(true);
		vendorIDTextItem1.setDisabled(true);

		status = new CheckboxItem("Active");
		status.setValue(false);

		status.setHelpInformation(true);

		status1 = new CheckboxItem("Active");
		status1.setValue(false);
		status1.setHelpInformation(true);

		balanceTextItem = new TextItem("Balance");
		balanceTextItem.setHelpInformation(true);

		balanceTextItem1 = new TextItem("Balance");
		balanceTextItem1.setHelpInformation(true);
		balanceTextItem.setDisabled(true);
		balanceTextItem1.setDisabled(true);

		form.setItems(vendorCombo, vendorIDTextItem, status, balanceTextItem);
		form1.setItems(vendorCombo1, vendorIDTextItem1, status1,
				balanceTextItem1);
		// form.setItems(getTextItems());
		layout.add(form);
		layout1.add(form1);
		horizontalPanel.add(layout);
		horizontalPanel.add(layout1);
		setBodyLayout(horizontalPanel);
	}

	private VendorCombo createCustomerCombo1() {
		vendorCombo1 = new VendorCombo("Vendor To");
		vendorCombo1.setHelpInformation(true);
		vendorCombo1
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientVendor>() {

					@Override
					public void selectedComboBoxItem(ClientVendor selectItem) {
						toClientVendor = selectItem;
						customerSelected1(selectItem);

					}

				});

		return vendorCombo1;
	}

	private VendorCombo createCustomerCombo() {
		vendorCombo = new VendorCombo("Vendor From");
		vendorCombo.setHelpInformation(true);
		vendorCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientVendor>() {

					@Override
					public void selectedComboBoxItem(ClientVendor selectItem) {
						fromclientVendor = selectItem;
						customerSelected(selectItem);

					}

				});

		return vendorCombo;
	}

	private void customerSelected(ClientVendor selectItem) {

		vendorIDTextItem.setValue(String.valueOf(selectItem.getID()));
		balanceTextItem.setValue(String.valueOf(selectItem.getBalance()));
		status.setValue(selectItem.isActive());

	}

	private void customerSelected1(ClientVendor selectItem) {
		vendorIDTextItem1.setValue(String.valueOf(selectItem.getID()));
		balanceTextItem1.setValue(String.valueOf(selectItem.getBalance()));
		status1.setValue(selectItem.isActive());
	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = form.validate();
		result = form1.validate();
		if (fromclientVendor.getID() == toClientVendor.getID()) {
			result.addError(fromclientVendor,
					"Could not move vendor because two vendors are same.");
		}
		return result;

	}

	/**
	 * @return
	 */
	private ClientContact createContact() {
		ClientContact contact = new ClientContact();
		// contact.setName(nameItem.getValue());
		// contact.setTitle(titleItem.getValue());
		// contact.setBusinessPhone(businessPhoneItem.getValue());
		// contact.setEmail(emailItem.getValue());
		return contact;
	}

	/**
	 * @param newContactHandler
	 */
	public void addSuccessCallback(
			ValueCallBack<ClientContact> newContactHandler) {
		this.successCallback = newContactHandler;
	}

	@Override
	protected boolean onOK() {

		if (fromclientVendor.getID() == toClientVendor.getID()) {
			return false;
		}
		Accounter.createHomeService().mergeVendor(fromclientVendor,
				toClientVendor, this);

		return true;

	}

	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Void result) {
		// TODO Auto-generated method stub

	}
}
