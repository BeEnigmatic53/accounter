package com.vimukti.accounter.web.client.ui.settings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.ClientBrandingTheme;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.ui.FinanceApplication;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.core.InputDialogHandler;
import com.vimukti.accounter.web.client.ui.core.ViewManager;

@SuppressWarnings("unchecked")
public class NewBrandThemeDialog extends BaseDialog {

	private Label nameLabel, pageSizeLabel, topMarginLabel, bottomMarginLabel,
			addressPadLabel, fontLabel, fontSizeLabel, draftLabel,
			approvedLabel, overdueLabel, creditNoteLabel, statementLabel,
			logoLabel, taxesLabel, termsLabel;
	private RadioButton a4Button, usLetterButton, leftRadioButton,
			rightRadioButton, exclusiveButton, inclusiveButton, cmButton,
			inchButton;
	private VerticalPanel checkBoxPanel, radioButtonPanel,
			check_radio_textAreaPanel, button_textBoxPanel;
	private HorizontalPanel mainLayoutPanel, check_radioPanel, buttonPanel;
	private Button saveButton, cancelButton;
	private CheckBox taxNumItem, headingItem, unitPriceItem, paymentItem,
			columnItem, addressItem, logoItem;
	private TextBox nameBox, topMarginBox, bottomMarginBox, addressPadBox,
			draftBox, approvedBox, overdueBox, creditNoteBox, statementBox,
			paypalTextBox;
	private String[] fontNameArray, fontSizeArray;
	private ListBox fontNameBox, fontSizeBox;
	private HTML paypalEmailHtml, contactDetailHtml;
	private TextArea contactDetailsArea, termsPaymentArea;
	private Label measureLabel;
	private FlexTable textBoxTable;

	public NewBrandThemeDialog(String title, String desc) {
		super(title, desc);
		createControls();
	}

	public NewBrandThemeDialog(String title, String desc,
			ClientBrandingTheme brandingTheme) {
		super(title, desc);
		createControls();
		setBrandingTheme(brandingTheme);
	}

	private void setBrandingTheme(ClientBrandingTheme brandingTheme) {
		nameBox.setValue(brandingTheme.getThemeName());
		// pageSizeLabel
		topMarginBox.setValue(String.valueOf(brandingTheme.getTopMargin()));
		bottomMarginBox.setValue(String
				.valueOf(brandingTheme.getBottomMargin()));
		addressPadBox.setValue(String
				.valueOf(brandingTheme.getAddressPadding()));
		// fontNameBox.setSelectedIndex()
		// brandingTheme.setFontSize(getFontSize(fontSizeBox.getSelectedIndex()));
		creditNoteBox.setValue(brandingTheme.getCreditMemoTitle());
		overdueBox.setValue(brandingTheme.getOverDueInvoiceTitle());
		statementBox.setValue(brandingTheme.getStatementTitle());
		taxNumItem.setValue(brandingTheme.isShowTaxNumber());
		headingItem.setValue(brandingTheme.isShowColumnHeadings());
		paymentItem.setValue(brandingTheme.isShowPaymentAdviceCut_Away());
		unitPriceItem.setValue(brandingTheme.isShowUnitPrice_And_Quantity());
		columnItem.setValue(brandingTheme.isShowTaxColumn());
		addressItem.setValue(brandingTheme.isShowRegisteredAddress());
		logoItem.setValue(brandingTheme.isShowLogo());
		paypalTextBox.setValue(brandingTheme.getPayPalEmailID());
		termsPaymentArea.setValue(brandingTheme.getTerms_And_Payment_Advice());
		contactDetailsArea.setValue(brandingTheme.getContactDetails());

		// brandingTheme.setLogoAlignmentType(getLogoType());
		// brandingTheme.setShowTaxesAsType(getTaxType());
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {

	}

	private void createControls() {
		mainLayoutPanel = new HorizontalPanel();
		check_radioPanel = new HorizontalPanel();
		check_radio_textAreaPanel = new VerticalPanel();
		button_textBoxPanel = new VerticalPanel();

		check_radioPanel.add(addCheckBoxTableControls());
		check_radioPanel.add(addRadioBoxTableControls());

		check_radio_textAreaPanel.add(check_radioPanel);
		termsLabel = new Label(FinanceApplication.getSettingsMessages()
				.termsLabel());
		termsPaymentArea = new TextArea();
		termsPaymentArea.setStyleName("terms-payment-area");
		check_radio_textAreaPanel.add(termsLabel);
		check_radio_textAreaPanel.add(termsPaymentArea);

		buttonPanel = new HorizontalPanel();
		saveButton = new Button(FinanceApplication.getSettingsMessages()
				.saveButton());
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				okClicked();
			}
		});
		cancelButton = new Button(FinanceApplication.getSettingsMessages()
				.cancelButton());
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelClicked();
			}
		});

		addInputDialogHandler(new InputDialogHandler() {

			@Override
			public boolean onOkClick() {
				ClientBrandingTheme brandingTheme = new ClientBrandingTheme();
				saveValues(brandingTheme);
				ViewManager.getInstance().createObject(brandingTheme,
						NewBrandThemeDialog.this);
				return true;
			}

			private int getPageSize() {
				if (a4Button.isEnabled()) {
					return 1;
				} else {
					return 2;
				}
			}

			private int getLogoType() {
				if (leftRadioButton.isEnabled()) {
					return 1;
				} else {
					return 2;
				}
			}

			private int getTaxType() {
				if (exclusiveButton.isEnabled()) {
					return 1;
				} else {
					return 2;
				}
			}

			private String getFont(int index) {
				switch (index) {
				case 0:
					return FinanceApplication.getSettingsMessages().arial();
				case 1:
					return FinanceApplication.getSettingsMessages().calibri();
				case 2:
					return FinanceApplication.getSettingsMessages().cambria();
				case 3:
					return FinanceApplication.getSettingsMessages().georgia();
				case 4:
					return FinanceApplication.getSettingsMessages().myriad();
				case 5:
					return FinanceApplication.getSettingsMessages().tahoma();
				case 6:
					return FinanceApplication.getSettingsMessages()
							.timesNewRoman();
				case 7:
					return FinanceApplication.getSettingsMessages().trebuchet();

				default:
					return FinanceApplication.getSettingsMessages().arial();
				}

			}

			private String getFontSize(int size) {

				switch (size) {
				case 0:
					return FinanceApplication.getSettingsMessages().point8();
				case 1:
					return FinanceApplication.getSettingsMessages().point9();
				case 2:
					return FinanceApplication.getSettingsMessages().point10();
				case 3:
					return FinanceApplication.getSettingsMessages().point11();
				case 4:
					return FinanceApplication.getSettingsMessages().point12();
				case 5:
					return FinanceApplication.getSettingsMessages().point13();
				case 6:
					return FinanceApplication.getSettingsMessages().point14();
				case 7:
					return FinanceApplication.getSettingsMessages().point15();

				default:
					return FinanceApplication.getSettingsMessages().point8();
				}

			}

			private void saveValues(ClientBrandingTheme brandingTheme) {
				brandingTheme.setThemeName(String.valueOf(nameBox.getValue()));
				brandingTheme.setPageSizeType(getPageSize());
				brandingTheme.setTopMargin(Double.parseDouble(String
						.valueOf(topMarginBox.getValue())));
				brandingTheme.setBottomMargin(Double.parseDouble(String
						.valueOf(bottomMarginBox.getValue())));
				brandingTheme.setAddressPadding(Double.parseDouble(String
						.valueOf(addressPadBox.getValue())));
				brandingTheme.setFont(getFont(fontNameBox.getSelectedIndex()));
				brandingTheme.setFontSize(getFontSize(fontSizeBox
						.getSelectedIndex()));

				brandingTheme.setCreditMemoTitle(String.valueOf(creditNoteBox
						.getValue()));
				brandingTheme.setOverDueInvoiceTitle(String.valueOf(overdueBox
						.getValue()));
				brandingTheme.setStatementTitle(String.valueOf(statementBox
						.getValue()));
				brandingTheme.setShowTaxNumber(taxNumItem.isEnabled());
				brandingTheme.setShowColumnHeadings(headingItem.isEnabled());
				brandingTheme.setShowPaymentAdviceCut_Away(paymentItem
						.isEnabled());
				brandingTheme.setShowUnitPrice_And_Quantity(unitPriceItem
						.isEnabled());
				brandingTheme.setShowTaxColumn(columnItem.isEnabled());
				brandingTheme.setShowRegisteredAddress(addressItem.isEnabled());
				brandingTheme.setShowLogo(logoItem.isEnabled());
				brandingTheme.setPayPalEmailID(String.valueOf(paypalTextBox
						.getValue()));
				brandingTheme.setTerms_And_Payment_Advice(String
						.valueOf(termsPaymentArea.getValue()));
				brandingTheme.setContactDetails(String
						.valueOf(contactDetailsArea.getValue()));
				brandingTheme.setLogoAlignmentType(getLogoType());
				brandingTheme.setShowTaxesAsType(getTaxType());
			}

			@Override
			public void onCancelClick() {
				hide();
			}
		});
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);

		mainLayoutPanel.add(addTextBoxTableControl());
		mainLayoutPanel.add(check_radio_textAreaPanel);

		button_textBoxPanel.add(mainLayoutPanel);
		button_textBoxPanel.add(buttonPanel);

		okbtn.setVisible(false);
		cancelBtn.setVisible(false);
		mainPanel.add(button_textBoxPanel);

	}

	private VerticalPanel addRadioBoxTableControls() {
		radioButtonPanel = new VerticalPanel();

		measureLabel = new Label(FinanceApplication.getSettingsMessages()
				.measure());
		logoLabel = new Label(FinanceApplication.getSettingsMessages()
				.logoAlignment());
		leftRadioButton = new RadioButton(FinanceApplication
				.getSettingsMessages().logoType(), FinanceApplication
				.getSettingsMessages().left());
		rightRadioButton = new RadioButton(FinanceApplication
				.getSettingsMessages().logoType(), FinanceApplication
				.getSettingsMessages().right());
		leftRadioButton.setChecked(true);
		taxesLabel = new Label(FinanceApplication.getSettingsMessages()
				.showTaxesAs());
		exclusiveButton = new RadioButton(FinanceApplication
				.getSettingsMessages().taxType(), FinanceApplication
				.getSettingsMessages().exclusive());
		inclusiveButton = new RadioButton(FinanceApplication
				.getSettingsMessages().taxType(), FinanceApplication
				.getSettingsMessages().inclusive());
		inclusiveButton.setChecked(true);

		contactDetailHtml = new HTML(FinanceApplication.getSettingsMessages()
				.contactDetailsHtml());
		contactDetailsArea = new TextArea();
		contactDetailsArea.setStyleName("contact-deatils-area");
		contactDetailsArea.setText(FinanceApplication.getSettingsMessages()
				.contactAddressValue());

		radioButtonPanel.add(logoLabel);
		radioButtonPanel.add(leftRadioButton);
		radioButtonPanel.add(rightRadioButton);
		radioButtonPanel.add(taxesLabel);
		radioButtonPanel.add(exclusiveButton);
		radioButtonPanel.add(inclusiveButton);
		radioButtonPanel.add(contactDetailHtml);
		radioButtonPanel.add(contactDetailsArea);

		return radioButtonPanel;
	}

	private VerticalPanel addCheckBoxTableControls() {

		taxNumItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showTaxNumber());
		taxNumItem.setChecked(true);
		headingItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showColumnHeadings());
		headingItem.setChecked(true);
		unitPriceItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showUnitPrice());
		unitPriceItem.setChecked(true);
		paymentItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showPaymentAdvice());
		paymentItem.setChecked(true);
		columnItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showTaxColumn());
		columnItem.setChecked(true);
		addressItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showRegisteredAddress());
		addressItem.setChecked(true);
		logoItem = new CheckBox(FinanceApplication.getSettingsMessages()
				.showLogo());
		logoItem.setChecked(true);
		paypalEmailHtml = new HTML(FinanceApplication.getSettingsMessages()
				.paypalEmailHtml());
		paypalTextBox = new TextBox();

		checkBoxPanel = new VerticalPanel();
		checkBoxPanel.add(taxNumItem);
		checkBoxPanel.add(headingItem);
		checkBoxPanel.add(unitPriceItem);
		checkBoxPanel.add(paymentItem);
		checkBoxPanel.add(columnItem);
		checkBoxPanel.add(addressItem);
		checkBoxPanel.add(logoItem);
		checkBoxPanel.add(paypalEmailHtml);
		checkBoxPanel.add(paypalTextBox);

		return checkBoxPanel;

	}

	private FlexTable addTextBoxTableControl() {
		nameLabel = new Label(FinanceApplication.getSettingsMessages().name());
		pageSizeLabel = new Label(FinanceApplication.getSettingsMessages()
				.pageSize());
		topMarginLabel = new Label(FinanceApplication.getSettingsMessages()
				.topMargin());
		bottomMarginLabel = new Label(FinanceApplication.getSettingsMessages()
				.bottomMargin());
		addressPadLabel = new Label(FinanceApplication.getSettingsMessages()
				.addressPadding());
		fontLabel = new Label(FinanceApplication.getSettingsMessages().font());
		fontSizeLabel = new Label(FinanceApplication.getSettingsMessages()
				.fontSize());
		draftLabel = new Label(FinanceApplication.getSettingsMessages()
				.draftInvoiceTitle());
		approvedLabel = new Label(FinanceApplication.getSettingsMessages()
				.approvedInvoiceTitle());
		overdueLabel = new Label(FinanceApplication.getSettingsMessages()
				.overdueInvoiceTitle());
		creditNoteLabel = new Label(FinanceApplication.getSettingsMessages()
				.creditNoteTitle());
		statementLabel = new Label(FinanceApplication.getSettingsMessages()
				.statementTitle());

		nameBox = new TextBox();
		topMarginBox = new TextBox();
		topMarginBox.setText(FinanceApplication.getSettingsMessages()
				.topMarginValue());
		bottomMarginBox = new TextBox();
		bottomMarginBox.setText(FinanceApplication.getSettingsMessages()
				.bottomMarginValue());
		addressPadBox = new TextBox();
		addressPadBox.setText(FinanceApplication.getSettingsMessages()
				.addressPaddingValue());
		draftBox = new TextBox();
		draftBox.setText(FinanceApplication.getSettingsMessages()
				.draftBoxValue());
		approvedBox = new TextBox();
		approvedBox.setText(FinanceApplication.getSettingsMessages()
				.approvedValue());
		overdueBox = new TextBox();
		overdueBox.setText(FinanceApplication.getSettingsMessages()
				.overdueValue());
		creditNoteBox = new TextBox();
		creditNoteBox.setText(FinanceApplication.getSettingsMessages()
				.creditNoteValue());
		statementBox = new TextBox();
		statementBox.setText(FinanceApplication.getSettingsMessages()
				.statement());

		a4Button = new RadioButton(FinanceApplication.getSettingsMessages()
				.pageType(), FinanceApplication.getSettingsMessages().a4());
		usLetterButton = new RadioButton(FinanceApplication
				.getSettingsMessages().pageType(), FinanceApplication
				.getSettingsMessages().usLetter());
		a4Button.setChecked(true);

		cmButton = new RadioButton(FinanceApplication.getSettingsMessages()
				.measureType(), FinanceApplication.getSettingsMessages().cm());
		inchButton = new RadioButton(FinanceApplication.getSettingsMessages()
				.measureType(), FinanceApplication.getSettingsMessages().inch());
		cmButton.setChecked(true);

		fontNameArray = new String[] {
				FinanceApplication.getSettingsMessages().arial(),
				FinanceApplication.getSettingsMessages().calibri(),
				FinanceApplication.getSettingsMessages().cambria(),
				FinanceApplication.getSettingsMessages().georgia(),
				FinanceApplication.getSettingsMessages().myriad(),
				FinanceApplication.getSettingsMessages().tahoma(),
				FinanceApplication.getSettingsMessages().timesNewRoman(),
				FinanceApplication.getSettingsMessages().trebuchet() };
		fontSizeArray = new String[] {
				FinanceApplication.getSettingsMessages().point8(),
				FinanceApplication.getSettingsMessages().point9(),
				FinanceApplication.getSettingsMessages().point10(),
				FinanceApplication.getSettingsMessages().point11(),
				FinanceApplication.getSettingsMessages().point13(),
				FinanceApplication.getSettingsMessages().point14(),
				FinanceApplication.getSettingsMessages().point15() };

		fontNameBox = new ListBox();
		for (int i = 0; i < fontNameArray.length; i++) {
			fontNameBox.addItem(fontNameArray[i]);
		}
		fontNameBox.setSelectedIndex(0);
		fontSizeBox = new ListBox();
		for (int i = 0; i < fontSizeArray.length; i++) {
			fontSizeBox.addItem(fontSizeArray[i]);
		}
		fontSizeBox.setSelectedIndex(0);
		HorizontalPanel measurePanel = new HorizontalPanel();
		measurePanel.add(topMarginBox);
		measurePanel.add(measureLabel);

		HorizontalPanel unitsPanel = new HorizontalPanel();
		unitsPanel.add(bottomMarginBox);
		unitsPanel.add(cmButton);
		unitsPanel.add(inchButton);

		textBoxTable = new FlexTable();
		textBoxTable.setWidget(0, 0, nameLabel);
		textBoxTable.setWidget(0, 1, nameBox);
		textBoxTable.setWidget(1, 0, pageSizeLabel);
		textBoxTable.setWidget(1, 1, a4Button);
		textBoxTable.setWidget(2, 1, usLetterButton);
		textBoxTable.setWidget(3, 0, topMarginLabel);
		textBoxTable.setWidget(3, 1, measurePanel);
		textBoxTable.setWidget(4, 0, bottomMarginLabel);
		textBoxTable.setWidget(4, 1, unitsPanel);
		textBoxTable.setWidget(5, 0, addressPadLabel);
		textBoxTable.setWidget(5, 1, addressPadBox);
		textBoxTable.setWidget(6, 0, fontLabel);
		textBoxTable.setWidget(6, 1, fontNameBox);
		textBoxTable.setWidget(7, 0, fontSizeLabel);
		textBoxTable.setWidget(7, 1, fontSizeBox);
		textBoxTable.setWidget(8, 0, draftLabel);
		textBoxTable.setWidget(8, 1, draftBox);
		textBoxTable.setWidget(9, 0, approvedLabel);
		textBoxTable.setWidget(9, 1, approvedBox);
		textBoxTable.setWidget(10, 0, overdueLabel);
		textBoxTable.setWidget(10, 1, overdueBox);
		textBoxTable.setWidget(11, 0, creditNoteLabel);
		textBoxTable.setWidget(11, 1, creditNoteBox);
		textBoxTable.setWidget(12, 0, statementLabel);
		textBoxTable.setWidget(12, 1, statementBox);

		return textBoxTable;
	}
}
