package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.Address;
import com.vimukti.accounter.core.ClientConvertUtil;
import com.vimukti.accounter.core.Currency;
import com.vimukti.accounter.core.Customer;
import com.vimukti.accounter.core.NumberUtils;
import com.vimukti.accounter.core.Payee;
import com.vimukti.accounter.core.Vendor;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.mobile.UserCommand;
import com.vimukti.accounter.mobile.requirements.AccountRequirement;
import com.vimukti.accounter.mobile.requirements.AddressRequirement;
import com.vimukti.accounter.mobile.requirements.ApplyCreditsRequirement;
import com.vimukti.accounter.mobile.requirements.ChangeListner;
import com.vimukti.accounter.mobile.requirements.CurrencyAmountRequirement;
import com.vimukti.accounter.mobile.requirements.CurrencyFactorRequirement;
import com.vimukti.accounter.mobile.requirements.CustomerRequirement;
import com.vimukti.accounter.mobile.requirements.DateRequirement;
import com.vimukti.accounter.mobile.requirements.NumberRequirement;
import com.vimukti.accounter.mobile.requirements.StringListRequirement;
import com.vimukti.accounter.mobile.requirements.StringRequirement;
import com.vimukti.accounter.mobile.utils.CommandUtils;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.ClientCurrency;
import com.vimukti.accounter.web.client.core.ClientCustomerPrePayment;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ListFilter;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.core.DecimalUtil;

public class CreateCustomerPrepaymentCommand extends AbstractTransactionCommand {
	ClientCustomerPrePayment prePayment;

	@Override
	protected String initObject(Context context, boolean isUpdate) {

		if (isUpdate) {
			String string = context.getString();
			if (string.isEmpty()) {
				addFirstMessage(
						context,
						getMessages().selectATransactionToUpdate(
								getMessages().payeePrePayment(
										Global.get().Customer())));
				return "receivedPaymentsList";
			}
			prePayment = getTransaction(string,
					AccounterCoreType.CUSTOMERPREPAYMENT, context);

			if (prePayment == null) {
				addFirstMessage(
						context,
						getMessages().selectATransactionToUpdate(
								getMessages().payeePrePayment(
										Global.get().Customer())));
				return "receivedPaymentsList " + string;
			}
			setValues();
		} else {
			String string = context.getString();
			if (!string.isEmpty()) {
				get(NUMBER).setValue(string);
			}
			prePayment = new ClientCustomerPrePayment();
		}
		setTransaction(prePayment);
		get(BILL_TO).setEditable(false);
		return null;
	}

	private void setValues() {
		get(DATE).setValue(prePayment.getDate());
		get(NUMBER).setValue(prePayment.getNumber());
		get(CUSTOMER).setValue(
				CommandUtils.getServerObjectById(prePayment.getCustomer(),
						AccounterCoreType.CUSTOMER));
		get(PAY_FROM).setValue(
				CommandUtils.getServerObjectById(prePayment.getDepositIn(),
						AccounterCoreType.ACCOUNT));
		get(AMOUNT).setValue(prePayment.getTotal());
		get(PAYMENT_METHOD).setValue(prePayment.getpaymentMethod());
		get(CHEQUE_NO).setValue(prePayment.getCheckNumber());
		// get(TO_BE_PRINTED).setValue(prePayment.isToBePrinted());
		get(MEMO).setValue(prePayment.getMemo());
		/* get(CURRENCY_FACTOR).setValue(prePayment.getCurrencyFactor()); */
	}

	@Override
	protected String getWelcomeMessage() {
		return prePayment.getID() == 0 ? getMessages().create(
				getMessages().payeePrePayment(Global.get().Customer()))
				: "Update Customer Prepayment command activated";

	}

	@Override
	protected String getDetailsMessage() {
		return prePayment.getID() == 0 ? getMessages().readyToCreate(
				getMessages().payeePrePayment(Global.get().Customer()))
				: getMessages().readyToUpdate(
						getMessages().payeePrePayment(Global.get().Customer()));
	}

	@Override
	protected void setDefaultValues(Context context) {
		get(DATE).setDefaultValue(new ClientFinanceDate());
		get(MEMO).setDefaultValue("");
		get(NUMBER).setDefaultValue(
				NumberUtils.getNextTransactionNumber(
						ClientTransaction.TYPE_CUSTOMER_PREPAYMENT,
						context.getCompany()));

	}

	@Override
	public String getSuccessMessage() {
		return prePayment.getID() == 0 ? getMessages().createSuccessfully(
				getMessages().payeePrePayment(Global.get().Customer()))
				: getMessages().updateSuccessfully(
						getMessages().payeePrePayment(Global.get().Customer()));
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {

		list.add(new CustomerRequirement(CUSTOMER, getMessages().pleaseEnter(
				Global.get().customer()), "Customer", false, true,
				new ChangeListner<Customer>() {

					@Override
					public void onSelection(Customer value) {
						get(BILL_TO).setValue(null);
						Set<Address> addresses = value.getAddress();
						for (Address address : addresses) {
							if (address.getType() == Address.TYPE_BILL_TO) {
								try {
									ClientAddress addr = new ClientConvertUtil()
											.toClientObject(address,
													ClientAddress.class);
									get(BILL_TO).setValue(addr);
								} catch (AccounterException e) {
									e.printStackTrace();
								}
								break;
							}
						}
						try {
							double mostRecentTransactionCurrencyFactor = CommandUtils
									.getMostRecentTransactionCurrencyFactor(
											getCompanyId(), value.getCurrency()
													.getID(),
											new ClientFinanceDate().getDate());
							CreateCustomerPrepaymentCommand.this.get(
									CURRENCY_FACTOR).setValue(
									mostRecentTransactionCurrencyFactor);
						} catch (AccounterException e) {
							e.printStackTrace();
						}
					}
				}) {

			@Override
			protected List<Customer> getLists(Context context) {
				return getCustomers();
			}
		});

		list.add(new NumberRequirement(NUMBER, getMessages().pleaseEnter(
				getMessages().billNo()), getMessages().billNo(), true, true));
		list.add(new DateRequirement(DATE, getMessages().pleaseEnter(
				getMessages().transactionDate()), getMessages()
				.transactionDate(), true, true));
		list.add(new AccountRequirement(PAY_FROM, getMessages().pleaseSelect(
				getMessages().transferTo()), getMessages().transferTo(), false,
				false, null) {

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().payFrom());
			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(new UserCommand("createBankAccount", "Bank"));
				list.add(new UserCommand("createBankAccount",
						"Create Other CurrentAsset Account",
						"Other Current Asset"));
				list.add(new UserCommand("createBankAccount",
						"Create CreditAccount", "CreditAccount"));
				list.add(new UserCommand("createBankAccount",
						"Create FixedAsset Account", "FixedAsset"));
			}

			@Override
			protected List<Account> getLists(Context context) {

				List<Account> filteredList = new ArrayList<Account>();
				for (Account obj : context.getCompany().getAccounts()) {
					if (new ListFilter<Account>() {

						@Override
						public boolean filter(Account e) {
							if (e.getIsActive()
									&& e.getType() == Account.TYPE_BANK) {
								return true;
							}
							return false;
						}
					}.filter(obj)) {
						filteredList.add(obj);
					}
				}
				return filteredList;
			}

			@Override
			protected String getEmptyString() {
				return getMessages().youDontHaveAny(
						getMessages().bankAccounts());
			}

			@Override
			protected boolean filter(Account e, String name) {
				return e.getName().contains(name);
			}
		});
		list.add(new AddressRequirement(BILL_TO, getMessages().pleaseEnter(
				getMessages().billTo()), getMessages().billTo(), true, true));

		list.add(new StringListRequirement(PAYMENT_METHOD, getMessages()
				.pleaseSelect(getMessages().paymentMethod()), getMessages()
				.paymentMethod(), false, true, null) {

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().paymentMethod());
			}

			@Override
			protected String getSelectString() {
				return getMessages()
						.pleaseSelect(getMessages().paymentMethod());
			}

			@Override
			protected List<String> getLists(Context context) {

				/*
				 * Map<String, String> paymentMethods =
				 * context.getClientCompany() .getPaymentMethods(); List<String>
				 * paymentMethod = new ArrayList<String>(
				 * paymentMethods.values());
				 */
				String payVatMethodArray[] = new String[] {
						getMessages().cash(), getMessages().creditCard(),
						getMessages().check(), getMessages().directDebit(),
						getMessages().masterCard(),
						getMessages().onlineBanking(),
						getMessages().standingOrder(),
						getMessages().switchMaestro() };
				List<String> wordList = Arrays.asList(payVatMethodArray);
				return wordList;
			}

			@Override
			protected String getEmptyString() {
				return getMessages().youDontHaveAny(
						getMessages().paymentMethod());
			}
		});
		list.add(new CurrencyAmountRequirement(AMOUNT, getMessages()
				.pleaseEnter(getMessages().amount()), getMessages().amount(),
				false, true) {

			@Override
			protected Currency getCurrency() {
				Customer customer = (Customer) CreateCustomerPrepaymentCommand.this
						.get(CUSTOMER).getValue();
				return customer.getCurrency();
			}
		});

		list.add(new StringRequirement(CHEQUE_NO, getMessages().pleaseEnter(
				getMessages().checkNo()), getMessages().checkNo(), true, true) {
			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				return super.run(context, makeResult, list, actions);
			}
		});
		list.add(new CurrencyFactorRequirement(CURRENCY_FACTOR, getMessages()
				.pleaseEnter(getMessages().currencyFactor()), getMessages()
				.currencyFactor()) {
			@Override
			protected Currency getCurrency() {
				Customer customer = (Customer) CreateCustomerPrepaymentCommand.this
						.get(CUSTOMER).getValue();
				return customer.getCurrency();
			}
		});
		list.add(new StringRequirement(MEMO, getMessages().pleaseEnter(
				getMessages().memo()), getMessages().memo(), true, true));

	}

	@Override
	protected Result onCompleteProcess(Context context) {
		ClientFinanceDate date = get(DATE).getValue();
		prePayment.setDate(date.getDate());
		String number = get(NUMBER).getValue();
		prePayment.setNumber(number);
		Customer customer = get(CUSTOMER).getValue();
		prePayment.setCustomer(customer.getID());
		Account depositIn = get(PAY_FROM).getValue();
		prePayment.setDepositIn(depositIn.getID());
		double amount = get(AMOUNT).getValue();
		prePayment.setTotal(amount);
		String paymentMethod = get(PAYMENT_METHOD).getValue();
		prePayment.setPaymentMethod(paymentMethod);
		String checkNum = get(CHEQUE_NO).getValue();
		prePayment.setCheckNumber(checkNum);
		// Boolean tobePrinted = get(TO_BE_PRINTED).getValue();
		// prePayment.setToBePrinted(tobePrinted);
		String memo = get(MEMO).getValue();

		prePayment.setMemo(memo);
		prePayment.setStatus(ClientCustomerPrePayment.STATUS_OPEN);
		prePayment.setType(ClientTransaction.TYPE_CUSTOMER_PREPAYMENT);
		prePayment.setCurrency(customer.getCurrency().getID());
		prePayment.setCurrencyFactor((Double) get(CURRENCY_FACTOR).getValue());
		adjustBalance(amount, customer, prePayment, context);
		create(prePayment, context);
		return null;
	}

	private void adjustBalance(double amount, Customer customer,
			ClientCustomerPrePayment customerPrePayment, Context context) {
		double enteredBalance = amount;

		if (DecimalUtil.isLessThan(enteredBalance, 0)
				|| DecimalUtil.isGreaterThan(enteredBalance, 1000000000000.00)) {
			enteredBalance = 0D;
		}
		if (customer != null) {
			customerPrePayment.setCustomerBalance(customer.getBalance()
					- enteredBalance);

		}

	}

	@Override
	protected Payee getPayee() {
		return null;
	}

}