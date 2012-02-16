package com.vimukti.accounter.web.client.ui.reports;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.reports.JobProfitability;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.serverreports.JobProfitabilitySummaryServerReport;

public class JobProfitabilitySummaryReport extends
		AbstractReportView<JobProfitability> {

	public JobProfitabilitySummaryReport() {
		this.serverReport = new JobProfitabilitySummaryServerReport(this);
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		Accounter.createReportService().getJobProfitabilitySummaryReport(start,
				end, this);
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_DATE_RANGE;
	}

	@Override
	public void OnRecordClick(JobProfitability record) {
		record.setStartDate(toolbar.getStartDate());
		record.setEndDate(toolbar.getEndDate());
		record.setDateRange(toolbar.getSelectedDateRange());
		ReportsRPC.openTransactionView(record.getTransactionType(),
				record.getTransactionId());
	}

	public void OnClick(JobProfitability record, int rowIndex, int columnIndex) {
		record.setStartDate(toolbar.getStartDate());
		record.setEndDate(toolbar.getEndDate());
		record.setDateRange(toolbar.getSelectedDateRange());
		if(columnIndex ==0)
		{//calls cost method,
			
		}else if(columnIndex == 1)
		{//calls revenue method
			
		}
		
	}

	@Override
	public boolean canPrint() {
		return true;
	}

	@Override
	public boolean canExportToCsv() {
		return true;
	}

	@Override
	public void print() {
	}

	@Override
	public void exportToCsv() {
	}
}
