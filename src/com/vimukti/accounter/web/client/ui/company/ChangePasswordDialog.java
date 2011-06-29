package com.vimukti.accounter.web.client.ui.company;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.ui.FinanceApplication;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.Accounter;
import com.vimukti.accounter.web.client.ui.core.AccounterValidator;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.core.InvalidEntryException;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.PasswordItem;

@SuppressWarnings("unchecked")
public class ChangePasswordDialog extends BaseDialog {

	private PasswordItem oldPasswordTextItem, newPasswordTextItem,
			confirmNewPasswordTextItem;
	private DynamicForm textItemsForm;
	private VerticalPanel mainPanel;
	private String oldPassword, newPassword, confirmNewPassword;

	public ChangePasswordDialog(String title, String desc) {
		super(title, desc);
		createControls();
	}

	private void createControls() {

		oldPasswordTextItem = new PasswordItem(FinanceApplication
				.getCompanyMessages().oldPassword());
		newPasswordTextItem = new PasswordItem(FinanceApplication
				.getCompanyMessages().newPassword());
		confirmNewPasswordTextItem = new PasswordItem(FinanceApplication
				.getCompanyMessages().confirmNewPassword());

		oldPasswordTextItem.setRequired(true);
		newPasswordTextItem.setRequired(true);
		confirmNewPasswordTextItem.setRequired(true);

		textItemsForm = new DynamicForm();

		mainPanel = new VerticalPanel();

		textItemsForm.setNumCols(2);
		textItemsForm.setCellSpacing(10);
		textItemsForm.setFields(oldPasswordTextItem, newPasswordTextItem,
				confirmNewPasswordTextItem);

		mainPanel.add(textItemsForm);
		okbtn.setText(FinanceApplication.getSettingsMessages().saveButton());

		setBodyLayout(mainPanel);

		okbtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					MainFinanceWindow.getViewManager().restoreErrorBox();
					validate();
				} catch (Exception e) {
				}
				savePassword();
			}
		});

		cancelBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MainFinanceWindow.getViewManager().closeCurrentView();
				removeFromParent();
			}
		});

	}

	protected void savePassword() {

		oldPassword = oldPasswordTextItem.getValue().toString();
		newPassword = newPasswordTextItem.getValue().toString();
		confirmNewPassword = confirmNewPasswordTextItem.getValue().toString();
		String emailID = FinanceApplication.clientIdentity.getEmailAddress();

		if (!(newPassword.toString().length() < 6)) {
			if (newPassword.equals(confirmNewPassword)) {
				FinanceApplication.createHomeService().changePassWord(emailID,
						oldPassword, newPassword, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {

							}

							@Override
							public void onSuccess(Boolean result) {
								if (result) {
									removeFromParent();
									Accounter
											.showInformation("Password Successfully Changed");
								} else {
									MainFinanceWindow
											.getViewManager()
											.showErrorInCurrectDialog(
													"Your Present Password is Wrong");
								}
							}

						});
			} else {
				MainFinanceWindow.getViewManager().showErrorInCurrectDialog(
						"Passwords not matched");
			}
		} else {
			MainFinanceWindow.getViewManager().showErrorInCurrectDialog(
					"Password should contain minimum 6 characters");
		}

	}

	@Override
	public void deleteFailed(Throwable caught) {

	}

	@Override
	public void deleteSuccess(Boolean result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validate() {
		// switch (this.validationCount) {
		// case 1:
		try {
			return AccounterValidator.validateForm(textItemsForm, true);
		} catch (InvalidEntryException e) {
			e.printStackTrace();
		}
		// default:
		return true;
		// }
	}

	@Override
	protected String getViewTitle() {
		return FinanceApplication.getActionsConstants().changePassword();
	}

}
