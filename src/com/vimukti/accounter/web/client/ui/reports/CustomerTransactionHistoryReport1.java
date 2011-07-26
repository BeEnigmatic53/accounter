package com.vimukti.accounter.web.client.ui.reports;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.reports.TransactionHistory;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.serverreports.CustomerTransactionHistoryServerReport;

@SuppressWarnings("unchecked")
public final class CustomerTransactionHistoryReport1 extends
		AbstractReportView<TransactionHistory> {

	public CustomerTransactionHistoryReport1() {
		this.serverReport = new CustomerTransactionHistoryServerReport(this);
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_DATE_RANGE;
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		Accounter.createReportService().getCustomerTransactionHistory(
				start.getTime(), end.getTime(), this);
	}

	@Override
	public void OnRecordClick(TransactionHistory record) {
		ReportsRPC.openTransactionView(record.getType(),
				record.getTransactionId());
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {

	}

	@Override
	public void onEdit() {

	}

	@Override
	public void print() {
		UIUtils.generateReportPDF(
				Integer.parseInt(String.valueOf(startDate.getTime())),
				Integer.parseInt(String.valueOf(endDate.getTime())), 120, "",
				"");

	}

	@Override
	public void printPreview() {

	}

	public void exportToCsv() {
		UIUtils.exportReport(
				Integer.parseInt(String.valueOf(startDate.getTime())),
				Integer.parseInt(String.valueOf(endDate.getTime())), 120, "",
				"");
	}
}
