package com.vimukti.accounter.web.client.ui.vendors;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.ParentCanvas;

/**
 * 
 * @author Raj Vimal
 */

public class RecordExpensesAction extends Action {

	public RecordExpensesAction(String text) {
		super(text);
	}

	public RecordExpensesAction(String text, String iconString) {
		super(text, iconString);
	}

	
	@Override
	public ParentCanvas getView() {

		return null;
	}

	@Override
	public void run(Object data, Boolean isDependent) {
		new SelectExpenseType().show();

	}

	public ImageResource getBigImage() {
		return null;
	}

	public ImageResource getSmallImage() {
		return Accounter.getFinanceMenuImages().recordExpenses();
	}

	@Override
	public String getImageUrl() {

		return "/images/record_expenses.png";
	}

	@Override
	public String getHistoryToken() {

		return "recordExpenses";
	}

}
