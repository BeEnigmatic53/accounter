package com.vimukti.accounter.web.client.ui.reports;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.core.Lists.DummyDebitor;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallback;

public class ARAgingSummaryReportAction extends Action<DummyDebitor> {

	protected ARAgingSummaryReport report;

	public ARAgingSummaryReportAction() {
		super();
		this.catagory = Accounter.messages().report();

	}

	@Override
	public ImageResource getBigImage() {
		return null;
	}

	@Override
	public ImageResource getSmallImage() {
		return Accounter.getFinanceMenuImages().reports();
	}

	// @Override
	// public ParentCanvas getView() {
	// return this.report;
	// }

	@Override
	public void run() {
		runAsync(data, isDependent);

	}

	public void runAsync(final Object data, final Boolean isDependent) {

		AccounterAsync.createAsync(new CreateViewAsyncCallback() {

			@Override
			public void onCreated() {
				report = new ARAgingSummaryReport();
				MainFinanceWindow.getViewManager().showView(report, data,
						isDependent, ARAgingSummaryReportAction.this);

			}

		});
	}

	// @Override
	// public String getImageUrl() {
	// return "/images/reports.png";
	// }

	@Override
	public String getHistoryToken() {
		return "arAgingSummary";
	}

	@Override
	public String getHelpToken() {
		return "ar-aging-summary";
	}

	@Override
	public String getText() {
		return messages.arAgeingSummary();
	}

}
