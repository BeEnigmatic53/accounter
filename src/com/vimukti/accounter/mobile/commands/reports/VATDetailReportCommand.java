package com.vimukti.accounter.mobile.commands.reports;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.requirements.ReportResultRequirement;
import com.vimukti.accounter.web.client.core.reports.VATDetail;
import com.vimukti.accounter.web.server.FinanceTool;

public class VATDetailReportCommand extends NewAbstractReportCommand<VATDetail> {

	private String currentsectionName;
	private double accountbalance;

	@Override
	protected void addRequirements(List<Requirement> list) {
		addDateRangeFromToDateRequirements(list);
		list.add(new ReportResultRequirement<VATDetail>() {

			@Override
			protected String onSelection(VATDetail selection, String name) {
				return addCommandOnRecordClick(selection);
			}

			@Override
			protected void fillResult(Context context, Result makeResult) {
				List<VATDetail> records = getRecords();
			}
		});
	}

	protected Record createReportRecord(VATDetail record) {
		Record salesRecord = new Record(record);

		salesRecord.add(getMessages().name(), record.getTransactionName());
		salesRecord.add(getMessages().date(), record.getTransactionDate());
		salesRecord.add(getMessages().number(), record.getTransactionNumber());
		salesRecord.add(
				getMessages().vatRate(),
				record.isPercentage() ? record.getVatRate() + "%" : record
						.getVatRate());
		salesRecord.add(getMessages().netAmount(), record.getNetAmount());
		salesRecord.add(getMessages().amount(), record.getTotal());
		if (!currentsectionName.equals(record.getBoxName())) {
			currentsectionName = record.getBoxName();
			accountbalance = 0.0D;
		}
		salesRecord.add(getMessages().balance(),
				accountbalance += record.getTotal());

		return salesRecord;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	protected List<VATDetail> getRecords() {
		ArrayList<VATDetail> vatDetails = new ArrayList<VATDetail>();
		try {
			vatDetails = new FinanceTool().getReportManager()
					.getVATDetailReport(getStartDate(), getEndDate(),
							getCompanyId());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vatDetails;
	}

	protected String addCommandOnRecordClick(VATDetail selection) {
		return "update transaction " + selection.getTransactionId();
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return getMessages().reportCommondActivated(getMessages().vatDetail());
	}

	@Override
	protected String getDetailsMessage() {
		return getMessages().reportDetails(getMessages().vatDetail());
	}

	@Override
	public String getSuccessMessage() {
		return getMessages().reportCommondClosedSuccessfully(
				getMessages().vatDetail());
	}

}