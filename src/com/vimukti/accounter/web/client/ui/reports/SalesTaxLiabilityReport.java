package com.vimukti.accounter.web.client.ui.reports;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.reports.SalesTaxLiability;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.ReportsActionFactory;
import com.vimukti.accounter.web.client.ui.serverreports.SalesTaxLiabilityServerReport;


public class SalesTaxLiabilityReport extends
		AbstractReportView<SalesTaxLiability> {

	public SalesTaxLiabilityReport() {
		this.serverReport = new SalesTaxLiabilityServerReport(this);
	}

	@Override
	public void OnRecordClick(SalesTaxLiability record) {
		UIUtils.runAction(record,
				ReportsActionFactory.getTransactionDetailByTaxItemAction());
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_DATE_RANGE;
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		Accounter.createReportService().getSalesTaxLiabilityReport(
				start.getTime(), end.getTime(), this);
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
				Integer.parseInt(String.valueOf(endDate.getTime())), 144, "",
				"");
	}

	@Override
	public void printPreview() {

	}

	public void exportToCsv() {
		UIUtils.exportReport(
				Integer.parseInt(String.valueOf(startDate.getTime())),
				Integer.parseInt(String.valueOf(endDate.getTime())), 144, "",
				"");
	}

}
