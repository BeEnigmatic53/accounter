package com.vimukti.accounter.web.client.ui.vendors;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientCashPurchase;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.Lists.BillsList;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterButton;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.BaseView;
import com.vimukti.accounter.web.client.ui.core.VendorsActionFactory;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;

public class AwaitingAuthorisationView extends BaseView<BillsList> {
	AwaitingAuthorisationgrid grid;
	public boolean isProcessingAdded;

	public AwaitingAuthorisationView() {
		init();
	}

	@Override
	public void init() {
		super.init();
		createControls();
	}

	private void createControls() {
		VerticalPanel panel = new VerticalPanel();
		panel.setSize("100%", "100%");

		initGrid();

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.getElement().getStyle().setMarginTop(15, Unit.PX);

		AccounterButton approve = new AccounterButton(Accounter
				.getVendorsMessages().approveButton());
		approve.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MainFinanceWindow.getViewManager().restoreErrorBox();
				isProcessingAdded = false;
				setAction(VendorsActionFactory.getAwaitingAuthorisationAction());
				// boolean isErrorOccured = checkPayFromAccount();
				List<BillsList> records = getRecordsToApprove();
				// if (!isErrorOccured)
				if (records.size() > 0) {
					updateRecords(records,
							ClientCashPurchase.EMPLOYEE_EXPENSE_STATUS_APPROVED);
					// else
					if (records.size() != grid.getSelectedRecords().size())
						Accounter.showError(Accounter.getVendorsMessages()
								.pleaseSelectPayFromAccount());
				} else {
					Accounter.showInformation(Accounter.getVendorsMessages()
							.norecordstoshow());
				}

			}
		});

		AccounterButton decline = new AccounterButton(Accounter
				.getVendorsMessages().declineButton());
		decline.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MainFinanceWindow.getViewManager().restoreErrorBox();
				isProcessingAdded = false;
				setAction(VendorsActionFactory.getAwaitingAuthorisationAction());
				List<BillsList> records = getRecordsToApprove();
				if (records.size() > 0) {
					updateRecords(grid.getSelectedRecords(),
							ClientCashPurchase.EMPLOYEE_EXPENSE_STATUS_DECLINED);
				} else {
					Accounter.showInformation(Accounter.getVendorsMessages()
							.norecordstoshow());
				}
			}
		});

		AccounterButton delete = new AccounterButton(Accounter
				.getVendorsMessages().delete());
		delete.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MainFinanceWindow.getViewManager().restoreErrorBox();
				isProcessingAdded = false;
				setAction(VendorsActionFactory.getAwaitingAuthorisationAction());
				List<BillsList> records = getRecordsToApprove();
				if (records.size() > 0) {
					updateRecords(grid.getSelectedRecords(),
							ClientCashPurchase.EMPLOYEE_EXPENSE_STATUS_DELETE);
				} else {
					Accounter.showInformation(Accounter.getVendorsMessages()
							.norecordstoshow());
				}
			}
		});

		buttonPanel.add(approve);
		buttonPanel.add(decline);
		buttonPanel.add(delete);
		approve.getElement().getStyle().setMarginLeft(25, Unit.PX);
		approve.setWidth("105px");
		approve.enabledButton(AccounterButton.APPROVE_BUTTON, "approve-image",
				"ibutton1");
		decline.setWidth("95px");
		decline.enabledButton(AccounterButton.DECLINE_BUTTON, "decline-image",
				"ibutton1");
		delete.setWidth("90px");
		delete.enabledButton(AccounterButton.DELETE_BUTTON, "close-image",
				"ibutton1");

		buttonLayout.setVisible(false);
		panel.add(grid);
		panel.add(buttonPanel);
		panel.setCellHorizontalAlignment(buttonPanel, ALIGN_RIGHT);
		mainPanel.add(panel);
		// mainPanel.removeStyleName("main-class-pannel");
		buttonLayout.getElement().getParentElement()
				.removeClassName("bottom-view");
		bottomShadow.getElement().getParentElement()
				.removeClassName("bottom-shadow");

	}

	protected List<BillsList> getRecordsToApprove() {
		List<BillsList> records = new ArrayList<BillsList>();
		for (BillsList r : grid.getSelectedRecords()) {
			if (r.getPayFrom() != 0)
				records.add(r);
		}
		return records;
	}

	protected boolean checkPayFromAccount() {
		boolean isErrorOccured = false;
		List<BillsList> selectedRecords = grid.getSelectedRecords();
		for (BillsList record : selectedRecords) {
			if (record.getPayFrom() == 0) {
				isErrorOccured = true;
			}
		}
		return isErrorOccured;
	}

	private void initGrid() {
		grid = new AwaitingAuthorisationgrid(true);
		grid.init();
		grid.setSize("100%", "100%");
	}

	protected void updateRecords(List<BillsList> records,
			final int expenceStatus) {

		for (BillsList record : records) {
			Accounter.createGETService().getObjectById(
					AccounterCoreType.CASHPURCHASE, record.getTransactionId(),

					new AsyncCallback<ClientCashPurchase>() {

						@Override
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(ClientCashPurchase result) {
							result.setExpenseStatus(expenceStatus);
							setAction(VendorsActionFactory
									.getExpenseClaimsAction(2));
							updateTransactionItems(result);
							alterObject(result);
						}
					});
		}
	}

	void updateTransactionItems(ClientCashPurchase result) {
		for (ClientTransactionItem item : result.getTransactionItems()) {
			item.setID(0);
		}
	}

	@Override
	protected void initRPCService() {
		super.initRPCService();
		String userName = null;
		if (!Accounter.getUser().isAdminUser()) {
			userName = Accounter.getUser().getName();
		} else {
			userName = null;
		}

		Accounter
				.createHomeService()
				.getEmployeeExpensesByStatus(
						userName,
						ClientCashPurchase.EMPLOYEE_EXPENSE_STATUS_SUBMITED_FOR_APPROVAL,
						new AsyncCallback<List<BillsList>>() {

							@Override
							public void onSuccess(List<BillsList> result) {
								for (BillsList list : result)
									grid.addData(list);
							}

							@Override
							public void onFailure(Throwable caught) {

							}
						});
	}

	@Override
	public void setAction(Action action) {
		super.setAction(action);
	}

	@Override
	public void saveSuccess(IAccounterCore object) {
		try {
			if (this.callback != null) {
				this.callback.onSuccess(object);
			}
			if (saveAndClose)
				MainFinanceWindow.getViewManager().closeView(this.getAction(),
						object);
			else {
				if (getAction() instanceof ExpenseClaimsAction)
					((ExpenseClaimsAction) getAction()).run(null, true);
				else
					getAction().run(null, true);
			}
		} catch (Exception e) {
			Accounter.showInformation(((JavaScriptException) e)
					.getDescription());
		}

	}

	@Override
	public List<DynamicForm> getForms() {

		return null;
	}

	@Override
	public void onEdit() {

	}

	@Override
	public void print() {

	}

	@Override
	public void printPreview() {

	}

	@Override
	public void deleteFailed(Throwable caught) {

	}

	@Override
	public void deleteSuccess(Boolean result) {

	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {

	}

	@Override
	protected String getViewTitle() {
		return Accounter.getVendorsMessages().awaitingAuthorisation();
	}

}
