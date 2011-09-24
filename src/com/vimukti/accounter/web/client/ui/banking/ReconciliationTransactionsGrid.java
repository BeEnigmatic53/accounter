/**
 * 
 */
package com.vimukti.accounter.web.client.ui.banking;

import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionMakeDeposit;
import com.vimukti.accounter.web.client.core.Utility;
import com.vimukti.accounter.web.client.externalization.AccounterConstants;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.SelectionChangedHandler;
import com.vimukti.accounter.web.client.ui.grids.columns.CheckBoxColumn;

/**
 * @author Prasanna Kumar G
 * 
 */
public class ReconciliationTransactionsGrid extends
		CellTable<ClientTransaction> {

	private AccounterConstants constants = Accounter.constants();
	private ListDataProvider<ClientTransaction> dataProvider = new ListDataProvider<ClientTransaction>();
	private SelectionChangedHandler<ClientTransaction> clearedTransactionCallback;
	private ReconciliationView view;

	/**
	 * Creates new Instance
	 * 
	 * @param view
	 * 
	 * @param valueCallBack
	 */
	public ReconciliationTransactionsGrid(
			ReconciliationView view,
			SelectionChangedHandler<ClientTransaction> clearedTransactionCallback) {
		this.clearedTransactionCallback = clearedTransactionCallback;
		this.view = view;
		initColumns();
		this.dataProvider.addDataDisplay(this);

	}

	private void initColumns() {

		TextColumn<ClientTransaction> date = new TextColumn<ClientTransaction>() {

			@Override
			public String getValue(ClientTransaction object) {
				return object.getDate().toString();
			}
		};

		TextColumn<ClientTransaction> transaction = new TextColumn<ClientTransaction>() {

			@Override
			public String getValue(ClientTransaction object) {
				return Utility.getTransactionName(object.getType());
			}
		};

		TextColumn<ClientTransaction> transactionID = new TextColumn<ClientTransaction>() {

			@Override
			public String getValue(ClientTransaction object) {
				return String.valueOf(object.getNumber());
			}
		};

		TextColumn<ClientTransaction> debit = new TextColumn<ClientTransaction>() {

			@Override
			public String getValue(ClientTransaction object) {
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				if (UIUtils.isMoneyOut(object, getBankAccount().getID())) {
					if (object.isMakeDeposit()) {
						double total = 0.0;
						List<ClientTransactionMakeDeposit> transactionMakeDeposit = object
								.getTransactionMakeDeposit();
						for (ClientTransactionMakeDeposit deposit : transactionMakeDeposit) {
							if (deposit.getAccount() == getBankAccount()
									.getID()) {
								total += deposit.getAmount();
							}
						}
						return String.valueOf(total);
					}
					return String.valueOf(object.getTotal());
				}
				return "";
			}
		};

		TextColumn<ClientTransaction> credit = new TextColumn<ClientTransaction>() {

			@Override
			public String getValue(ClientTransaction object) {
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				if (UIUtils.isMoneyIn(object, getBankAccount().getID())) {
					return String.valueOf(object.getTotal());
				}
				return "";
			}
		};

		CheckBoxColumn<ClientTransaction> clearCheckbox = new CheckBoxColumn<ClientTransaction>() {

			@Override
			public void update(int index, ClientTransaction object,
					Boolean value) {
				clearedTransactionCallback.selectionChanged(object, value);
			}

			@Override
			public Boolean getValue(ClientTransaction object) {
				return false;
			}
		};

		this.addColumn(date, constants.transactionDate());
		this.addColumn(transactionID, constants.transaction());
		this.addColumn(transaction, constants.transactionType());
		this.addColumn(debit, constants.debit());
		this.addColumn(credit, constants.credit());
		if (!view.isInViewMode()) {
			this.addColumn(clearCheckbox, constants.clear());
		}

	}

	/**
	 * @return
	 */
	protected ClientAccount getBankAccount() {
		return view.getData().getAccount();
	}

	public void setData(List<ClientTransaction> data) {
		this.dataProvider.setList(data);
	}

}
