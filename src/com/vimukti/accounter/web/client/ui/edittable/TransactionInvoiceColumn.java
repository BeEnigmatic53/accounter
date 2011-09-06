package com.vimukti.accounter.web.client.ui.edittable;

import com.vimukti.accounter.web.client.core.ClientTransactionItem;

public class TransactionInvoiceColumn extends
		AmountColumn<ClientTransactionItem> {

	@Override
	protected double getAmount(ClientTransactionItem row) {
		return row.getInvoiced();
	}

	@Override
	protected void setAmount(ClientTransactionItem row, double value) {
		row.setInvoiced(value);
	}

	@Override
	public int getWidth() {
		return 25;
	}

}
