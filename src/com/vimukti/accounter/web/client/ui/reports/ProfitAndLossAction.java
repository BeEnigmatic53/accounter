package com.vimukti.accounter.web.client.ui.reports;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallBack;
import com.vimukti.accounter.web.client.ui.core.ParentCanvas;

/**
 * 
 * @author Mandeep Singh
 */

public class ProfitAndLossAction extends Action {

	protected ProfitAndLossReport report;

	public ProfitAndLossAction(String text) {
		super(text);
		this.catagory = Accounter.getReportsMessages().report();
	}

	public ProfitAndLossAction(String text, String iconString) {
		super(text, iconString);
		this.catagory = Accounter.getReportsMessages().report();
	}

	public void runAsync(final Object data, final Boolean isDependent) {

		AccounterAsync.createAsync(new CreateViewAsyncCallBack() {

			public void onCreated() {

				try {
					if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK)
						report = new ProfitAndLossUKReport();
					else
						report = new ProfitAndLossReport();
					MainFinanceWindow.getViewManager().showView(report, data,
							isDependent, ProfitAndLossAction.this);
				} catch (Throwable t) {
					onCreateFailed(t);
				}

			}

			public void onCreateFailed(Throwable t) {
				/* UIUtils.logError */System.err
						.println("Failed to Load Report.." + t);
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public ParentCanvas getView() {
		return this.report;
	}

	@Override
	public void run(Object data, Boolean isDependent) {
		runAsync(data, isDependent);
	}

	public ImageResource getBigImage() {
		return null;
	}

	public ImageResource getSmallImage() {
		// return FinanceApplication.getFinanceMenuImages().profitAndLose();
		return Accounter.getFinanceMenuImages().reports();
	}

	@Override
	public String getImageUrl() {
		return "/images/reports.png";
	}

	@Override
	public String getHistoryToken() {
		// TODO Auto-generated method stub
		return "profitAndLoss";
	}

}
