package com.vimukti.accounter.web.client.ui.customers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.Lists.CustomerRefundsList;
import com.vimukti.accounter.web.client.ui.FinanceApplication;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.SelectCombo;
import com.vimukti.accounter.web.client.ui.core.AccounterWarningType;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.BaseListView;
import com.vimukti.accounter.web.client.ui.core.CustomersActionFactory;
import com.vimukti.accounter.web.client.ui.grids.CustomerRefundListGrid;

/**
 * 
 * @author Fernandez
 * @param <T>
 * 
 */
public class CustomerRefundListView extends BaseListView<CustomerRefundsList> {
	CustomersMessages customerConstants = GWT.create(CustomersMessages.class);
	protected List<CustomerRefundsList> transactions;
	private List<CustomerRefundsList> listOfCustomerRefund;

	private static String NOT_ISSUED = FinanceApplication
			.getCustomersMessages().notIssued();
	private static String ISSUED = FinanceApplication.getCustomersMessages()
			.issued();
	private static String VOID = FinanceApplication.getVendorsMessages()
			.Voided();
	private static String ALL = FinanceApplication.getCustomersMessages().all();
	// private static String DELETED="Deleted";

	private static final int STATUS_NOT_ISSUED = 0;
	private static final int STATUS_PARTIALLY_PAID = 1;
	private static final int STATUS_ISSUED = 2;

	public CustomerRefundListView() {
		super();

	}

	@Override
	protected Action getAddNewAction() {

		return CustomersActionFactory.getCustomerRefundAction();
	}

	@Override
	protected String getAddNewLabelString() {

		return customerConstants.addaNewCustomerRefund();
	}

	@Override
	protected String getListViewHeading() {

		return customerConstants.getCustomersRefundListViewHeading();
	}

	@Override
	protected void initGrid() {
		grid = new CustomerRefundListGrid(false);
		grid.init();

	}

	@Override
	public void initListCallback() {
		super.initListCallback();
		FinanceApplication.createHomeService().getCustomerRefundsList(this);
	}

	@Override
	public void onSuccess(List<CustomerRefundsList> result) {
		super.onSuccess(result);
		listOfCustomerRefund = result;
		filterList(NOT_ISSUED);
		grid.setViewType(NOT_ISSUED);
	}

	@Override
	public void updateInGrid(CustomerRefundsList objectTobeModified) {
		// TODO Auto-generated method stub

	}

	protected SelectCombo getSelectItem() {
		viewSelect = new SelectCombo(FinanceApplication.getCustomersMessages()
				.currentView());
		viewSelect.setHelpInformation(true);
		listOfTypes = new ArrayList<String>();
		listOfTypes.add(NOT_ISSUED);
		listOfTypes.add(ISSUED);
		listOfTypes.add(VOID);
		listOfTypes.add(ALL);
		viewSelect.initCombo(listOfTypes);

		if (UIUtils.isMSIEBrowser())
			viewSelect.setWidth("150px");

		viewSelect.setComboItem(NOT_ISSUED);
		viewSelect
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<String>() {

					@Override
					public void selectedComboBoxItem(String selectItem) {
						if (viewSelect.getSelectedValue() != null) {
							grid.setViewType(viewSelect.getSelectedValue());
							filterList(viewSelect.getSelectedValue());
						}

					}
				});

		return viewSelect;
	}

	@SuppressWarnings("unchecked")
	private void filterList(String text) {

		grid.removeAllRecords();

		for (CustomerRefundsList customerRefund : listOfCustomerRefund) {
			if (text.equals(NOT_ISSUED)) {
				if ((customerRefund.getStatus() == STATUS_NOT_ISSUED || customerRefund
						.getStatus() == STATUS_PARTIALLY_PAID))
					grid.addData(customerRefund);
				continue;
			}
			if (text.equals(ISSUED)) {
				if (customerRefund.getStatus() == STATUS_ISSUED)
					grid.addData(customerRefund);
				continue;
			}
			if (text.equals(VOID)) {
				if (customerRefund.isVoided() && !customerRefund.isDeleted())
					grid.addData(customerRefund);
				continue;
			}
			// if (text.equals(DELETED)) {
			// if (customerRefund.isDeleted())
			// grid.addData(customerRefund);
			// continue;
			// }
			if (text.equals(ALL)) {

				grid.addData(customerRefund);
			}
		}
		if (grid.getRecords().isEmpty()) {
			grid.addEmptyMessage(AccounterWarningType.RECORDSEMPTY);
		}
	}

	@Override
	public void updateGrid(IAccounterCore core) {
		initListCallback();
	}

	@Override
	public void fitToSize(int height, int width) {
		super.fitToSize(height, width);
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void print() {
		// TODO Auto-generated method stub

	}

	@Override
	public void printPreview() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getViewTitle() {
		return FinanceApplication.getCustomersMessages().customerRefunds();
	}
}
