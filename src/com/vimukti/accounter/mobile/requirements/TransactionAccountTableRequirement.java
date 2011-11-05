package com.vimukti.accounter.mobile.requirements;

import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientTAXCode;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.ui.core.DecimalUtil;

public class TransactionAccountTableRequirement extends
		AbstractTableRequirement<ClientTransactionItem> {

	private static final String ACCOUNT = "accountitemaccount";
	private static final String AMOUNT = "accountitemamount";
	private static final String TAXCODE = "accountitemtaxCode";
	private static final String DISCOUNT = "accountitemdiscount";
	private static final String DESCRIPTION = "accountitemdescription";
	private static final String TAX = "accountitemtax";

	public TransactionAccountTableRequirement(String requirementName,
			String enterString, String recordName, boolean isCreatable,
			boolean isOptional, boolean isAllowFromContext) {
		super(requirementName, enterString, recordName, isCreatable,
				isOptional, isAllowFromContext);
	}

	@Override
	protected void addRequirement(List<Requirement> list) {
		list.add(new AccountRequirement(ACCOUNT, getMessages().pleaseSelect(
				getConstants().account()), getConstants().account(), false,
				true, null) {

			@Override
			protected String getSetMessage() {
				return "Account has been Selected";
			}

			@Override
			protected List<ClientAccount> getLists(Context context) {
				return getAccounts(context);
			}

			@Override
			protected String getEmptyString() {
				return getMessages().youDontHaveAny(getConstants().Accounts());
			}
		});

		list.add(new AmountRequirement(AMOUNT, getMessages().pleaseEnter(
				getConstants().amount()), getConstants().amount(), false, true));

		list.add(new AmountRequirement(DISCOUNT, getMessages().pleaseEnter(
				getConstants().discount()), getConstants().discount(), true,
				true));

		list.add(new TaxCodeRequirement(TAXCODE, getMessages().pleaseSelect(
				getConstants().taxCode()), getConstants().taxCode(), false,
				true, null) {
			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				if (getClientCompany().getPreferences().isTrackTax()
						&& getClientCompany().getPreferences()
								.isTaxPerDetailLine()) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected List<ClientTAXCode> getLists(Context context) {
				return getClientCompany().getActiveTaxCodes();
			}

			@Override
			protected boolean filter(ClientTAXCode e, String name) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		list.add(new BooleanRequirement(TAX, true) {

			@Override
			protected String getTrueString() {
				return getConstants().taxable();
			}

			@Override
			protected String getFalseString() {
				return getConstants().taxExempt();
			}

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				if (!(context.getClientCompany().getPreferences().isTrackTax() && context
						.getClientCompany().getPreferences()
						.isTaxPerDetailLine())) {
					return super.run(context, makeResult, list, actions);
				}
				return null;
			}
		});

		list.add(new StringRequirement(DESCRIPTION, getMessages().pleaseEnter(
				getConstants().description()), getConstants().description(),
				true, true));
	}

	protected List<ClientAccount> getAccounts(Context context) {
		return context.getClientCompany().getAccounts();
	}

	@Override
	protected String getEmptyString() {
		return "There are no transaction account items";
	}

	@Override
	protected void getRequirementsValues(ClientTransactionItem obj) {
		ClientAccount account = get(ACCOUNT).getValue();
		obj.setAccount(account.getID());
		String description = get(DESCRIPTION).getValue();
		obj.setDescription(description);
		double amount = get(AMOUNT).getValue();
		obj.setUnitPrice(amount);
		Boolean isTaxable = get(TAX).getValue();
		obj.setTaxable(isTaxable);
		ClientTAXCode taxCode = get(TAXCODE).getValue();
		if (taxCode != null) {
			obj.setTaxCode(taxCode.getID());
		}
		double discount = get(DISCOUNT).getValue();
		obj.setDiscount(discount);
		double lt = obj.getUnitPrice();
		double disc = obj.getDiscount();
		obj.setLineTotal(DecimalUtil.isGreaterThan(disc, 0) ? (lt - (lt * disc / 100))
				: lt);
	}

	@Override
	protected void setRequirementsDefaultValues(ClientTransactionItem obj) {
		get(ACCOUNT).setValue(getClientCompany().getAccount(obj.getAccount()));
		get(DESCRIPTION).setDefaultValue(obj.getDescription());
		get(AMOUNT).setValue(obj.getUnitPrice());
		if (getClientCompany().getPreferences().isTrackTax()
				&& getClientCompany().getPreferences().isTaxPerDetailLine()) {
			get(TAXCODE).setDefaultValue(
					getClientCompany().getTAXCode(obj.getTaxCode()));
		} else {
			get(TAX).setDefaultValue(obj.isTaxable());
		}
		get(DISCOUNT).setDefaultValue(obj.getDiscount());
	}

	@Override
	protected ClientTransactionItem getNewObject() {
		ClientTransactionItem clientTransactionItem = new ClientTransactionItem();
		clientTransactionItem.setType(ClientTransactionItem.TYPE_ACCOUNT);
		return clientTransactionItem;
	}

	@Override
	protected Record createFullRecord(ClientTransactionItem t) {
		Record record = new Record(t);
		record.add("", getClientCompany().getAccount(t.getAccount())
				.getDisplayName());
		record.add("", t.getUnitPrice());
		if (getClientCompany().getPreferences().isTrackTax()
				&& getClientCompany().getPreferences().isTaxPerDetailLine()) {
			record.add("", getClientCompany().getTAXCode(t.getTaxCode())
					.getDisplayName());
		} else {
			if (t.isTaxable()) {
				record.add("", getConstants().taxable());
			} else {
				record.add("", getConstants().taxExempt());
			}
		}
		record.add("", t.getDiscount());
		record.add("", t.getDescription());
		return record;
	}

	@Override
	protected List<ClientTransactionItem> getList() {
		return null;
	}

	@Override
	protected Record createRecord(ClientTransactionItem t) {
		return createFullRecord(t);
	}

	@Override
	protected String getAddMoreString() {
		return getMessages().addMore(getConstants().Accounts());
	}

}
