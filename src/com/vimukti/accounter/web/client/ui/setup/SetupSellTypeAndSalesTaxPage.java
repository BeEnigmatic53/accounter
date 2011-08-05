package com.vimukti.accounter.web.client.ui.setup;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.ui.CustomLabel;

public class SetupSellTypeAndSalesTaxPage extends AbstractSetupPage {
	private static final String SELL_TYPES = "Sell Type";
	private static final String SALES_TAX = "Sales Tax";
	private RadioButton serviceOnlyRadioButton, productOnlyRadioButton,
			bothserviceandprductRadioButton, yesRadioButton, noRadioButton;

	@Override
	public String getHeader() {
		return this.accounterConstants.whatDoYouSell();
	}

	@Override
	public VerticalPanel getPageBody() {

		VerticalPanel mainVerticalPanel = new VerticalPanel();

		serviceOnlyRadioButton = new RadioButton(SELL_TYPES,
				accounterConstants.services_labelonly());
		mainVerticalPanel.add(serviceOnlyRadioButton);
		mainVerticalPanel
				.add(new CustomLabel(accounterConstants.servicesOnly()));
		mainVerticalPanel.add(new CustomLabel(""));

		productOnlyRadioButton = new RadioButton(SELL_TYPES,
				accounterConstants.products_labelonly());
		mainVerticalPanel.add(productOnlyRadioButton);
		mainVerticalPanel
				.add(new CustomLabel(accounterConstants.productsOnly()));
		mainVerticalPanel.add(new CustomLabel(""));

		bothserviceandprductRadioButton = new RadioButton(SELL_TYPES,
				accounterConstants.bothservicesandProduct_labelonly());
		mainVerticalPanel.add(bothserviceandprductRadioButton);
		mainVerticalPanel.add(new CustomLabel(accounterConstants
				.bothServicesandProducts()));
		mainVerticalPanel.add(new CustomLabel(""));

		mainVerticalPanel.add(new CustomLabel(accounterConstants
				.doyouchargesalestax()));
		yesRadioButton = new RadioButton(SALES_TAX, accounterConstants.yes());
		noRadioButton = new RadioButton(SALES_TAX, accounterConstants.no());
		mainVerticalPanel.add(yesRadioButton);
		mainVerticalPanel.add(noRadioButton);

		return mainVerticalPanel;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub

	}

}
