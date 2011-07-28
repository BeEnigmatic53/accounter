package com.vimukti.accounter.web.client.ui.reports;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallBack;
import com.vimukti.accounter.web.client.ui.core.ParentCanvas;

public class VATUncategorisedAmountsReportAction extends Action {

	public VATUncategorisedAmountsReportAction(String text) {
		super(text);
		this.catagory = Accounter.constants().report();
	}

	public VATUncategorisedAmountsReportAction(String text, String iconString) {
		super(text, iconString);
		this.catagory = Accounter.constants().report();
	}

	/**
	 * THIS METHOD DID N'T USED ANY WHERE IN THE PROJECT.
	 */
	@Override
	public ImageResource getBigImage() {
		return null;
	}

	@Override
	public ImageResource getSmallImage() {
		return Accounter.getFinanceMenuImages().reports();
	}

	
	@Override
	public ParentCanvas getView() {
		
		return null;
	}

	@Override
	public void run(Object data, Boolean isDependent) {
		runAsync(data, isDependent);

	}

	public void runAsync(final Object data, final Boolean isDependent) {

		AccounterAsync.createAsync(new CreateViewAsyncCallBack() {

			public void onCreated() {

				try {
					VATUncategorisedAmountsReport report = new VATUncategorisedAmountsReport();
					MainFinanceWindow.getViewManager().showView(report, data,
							isDependent,
							VATUncategorisedAmountsReportAction.this);
				} catch (Throwable t) {
					onCreateFailed(t);
				}

			}

			public void onCreateFailed(Throwable t) {
				// //UIUtils.logError("Failed to Load Report..", t);
			}
		});

	}

	@Override
	public String getImageUrl() {
		return "/images/reports.png";
	}

	@Override
	public String getHistoryToken() {
		
		return "uncategorisedVatAmounts";
	}

}
