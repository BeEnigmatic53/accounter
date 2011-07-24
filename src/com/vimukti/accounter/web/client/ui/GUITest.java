package com.vimukti.accounter.web.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.ClientBank;
import com.vimukti.accounter.web.client.core.ClientCashPurchase;
import com.vimukti.accounter.web.client.core.ClientCashSales;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.core.ClientContact;
import com.vimukti.accounter.web.client.core.ClientCreditCardCharge;
import com.vimukti.accounter.web.client.core.ClientCreditRating;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientCustomerCreditMemo;
import com.vimukti.accounter.web.client.core.ClientCustomerGroup;
import com.vimukti.accounter.web.client.core.ClientCustomerRefund;
import com.vimukti.accounter.web.client.core.ClientEmail;
import com.vimukti.accounter.web.client.core.ClientEnterBill;
import com.vimukti.accounter.web.client.core.ClientEstimate;
import com.vimukti.accounter.web.client.core.ClientFax;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientFixedAsset;
import com.vimukti.accounter.web.client.core.ClientInvoice;
import com.vimukti.accounter.web.client.core.ClientIssuePayment;
import com.vimukti.accounter.web.client.core.ClientItem;
import com.vimukti.accounter.web.client.core.ClientItemGroup;
import com.vimukti.accounter.web.client.core.ClientItemReceipt;
import com.vimukti.accounter.web.client.core.ClientMakeDeposit;
import com.vimukti.accounter.web.client.core.ClientPayBill;
import com.vimukti.accounter.web.client.core.ClientPaymentTerms;
import com.vimukti.accounter.web.client.core.ClientPhone;
import com.vimukti.accounter.web.client.core.ClientPriceLevel;
import com.vimukti.accounter.web.client.core.ClientReceivePayment;
import com.vimukti.accounter.web.client.core.ClientShippingMethod;
import com.vimukti.accounter.web.client.core.ClientShippingTerms;
import com.vimukti.accounter.web.client.core.ClientTAXCode;
import com.vimukti.accounter.web.client.core.ClientTAXGroup;
import com.vimukti.accounter.web.client.core.ClientTAXItem;
import com.vimukti.accounter.web.client.core.ClientTAXItemGroup;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionIssuePayment;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.core.ClientTransactionMakeDeposit;
import com.vimukti.accounter.web.client.core.ClientTransactionPayBill;
import com.vimukti.accounter.web.client.core.ClientTransactionReceivePayment;
import com.vimukti.accounter.web.client.core.ClientTransferFund;
import com.vimukti.accounter.web.client.core.ClientVendor;
import com.vimukti.accounter.web.client.core.ClientVendorCreditMemo;
import com.vimukti.accounter.web.client.core.ClientVendorGroup;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.Utility;
import com.vimukti.accounter.web.client.core.Lists.IssuePaymentTransactionsList;
import com.vimukti.accounter.web.client.core.Lists.PayBillTransactionList;
import com.vimukti.accounter.web.client.core.Lists.ReceivePaymentTransactionList;
import com.vimukti.accounter.web.client.ui.core.Accounter;

public class GUITest {
	protected long nextAccNum;
	private double totallinetotal = 0.0;
	double grandTotal = 0.0;
	protected boolean isNonTransaction;

	public <C extends IAccounterCore> void create(final C core,
			final AsyncCallback<String> callBack) {

		Accounter.createGETService().getID(
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						core.setID(result);
						Accounter.createCRUDService().create(
								((IAccounterCore) core), callBack);
					}

					@Override
					public void onFailure(Throwable caught) {
						Accounter
								.showError("Could Not Initialize the id....."
										+ core.getName());
						// dialog.removeFromParent();
					}
				});

	}

	@SuppressWarnings("unchecked")
	public void createSupportList() {
		isNonTransaction = true;
		/* Customer Group */
		ClientCustomerGroup customerGroup = new ClientCustomerGroup();
		customerGroup.setName("CustmrGroup111");
		create(customerGroup, getCreateCallBack(customerGroup));

		/* Vendor Group */
		ClientVendorGroup vendorGroup = new ClientVendorGroup();
		vendorGroup.setName(UIUtils.getVendorString("SupplierGroup1",
				"VendorGroup1"));

		create(vendorGroup, getCreateCallBack(vendorGroup));

		/* PaymentTerms Group */
		ClientPaymentTerms clientPaymentTerms = new ClientPaymentTerms();

		clientPaymentTerms.setName(" Net 1");
		clientPaymentTerms.setDescription("Discount 1");
		clientPaymentTerms.setIfPaidWithIn(30);
		clientPaymentTerms.setDiscountPercent(10);
		clientPaymentTerms.setDueDays(12);
		create(clientPaymentTerms, getCreateCallBack(clientPaymentTerms));
		/* Shipping Method */

		ClientShippingMethod method1 = new ClientShippingMethod();

		method1.setName("ShippingMethod 1");
		method1.setDescription("ShippingMethod1 Desc");
		create(method1, getCreateCallBack(method1));

		ClientShippingMethod method2 = new ClientShippingMethod();

		method2.setName("ShippingMethod 2");
		method2.setDescription("ShippingMethod2 Desc ");
		create(method2, getCreateCallBack(method2));

		/* Shipping Term */

		ClientShippingTerms shippingTerm = new ClientShippingTerms();
		shippingTerm.setName("Shipping term1");
		shippingTerm.setDescription("Shipping term1");
		create(shippingTerm, getCreateCallBack(shippingTerm));

		/* price Level */
		ClientPriceLevel priceLevel1 = new ClientPriceLevel();
		priceLevel1.setName("incrsPriceBy10%");
		priceLevel1.setPercentage(10);
		priceLevel1.setPriceLevelDecreaseByThisPercentage(false);
		create(priceLevel1, getCreateCallBack(priceLevel1));

		ClientPriceLevel priceLevel2 = new ClientPriceLevel();
		priceLevel2.setName("decrsPriceBy10%");
		priceLevel2.setPercentage(10);
		priceLevel2.setPriceLevelDecreaseByThisPercentage(true);
		create(priceLevel2, getCreateCallBack(priceLevel2));

		/* Item Group */
		ClientItemGroup itemGroup = new ClientItemGroup();
		itemGroup.setName("Item Group1");
		create(itemGroup, getCreateCallBack(itemGroup));

		ClientItemGroup itemGroup2 = new ClientItemGroup();
		itemGroup2.setName("Item Group2");
		create(itemGroup2, getCreateCallBack(itemGroup2));

		/* Credit Rating List */
		ClientCreditRating crdRat1 = new ClientCreditRating();
		crdRat1.setName("creditRate1");
		create(crdRat1, getCreateCallBack(crdRat1));

		ClientCreditRating crdRat2 = new ClientCreditRating();
		crdRat2.setName("creditRate2");
		create(crdRat2, getCreateCallBack(crdRat2));

		ClientBank bank1 = new ClientBank();
		bank1.setName("ICICI");
		create(bank1, getCreateCallBack(bank1));

		ClientBank bank2 = new ClientBank();
		bank2.setName("SBI");
		create(bank2, getCreateCallBack(bank2));
	}

	private long getNextAccountNumber(final ClientAccount acc,
			final String name, final int accType) {
		Accounter.createHomeService().getNextNominalCode(accType,
				new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Long result) {

						ClientAccount account = acc;
						if (result != null) {
							nextAccNum = result.longValue();
							if (accType == ClientAccount.TYPE_CASH)
								account.setNumber(nextAccNum + 122 + "");
							else
								account.setNumber(nextAccNum + "");
							account.setName(name);
							account.setIsActive(true);
							account.setConsiderAsCashAccount(false);
							account
									.setCashFlowCategory(ClientAccount.CASH_FLOW_CATEGORY_OPERATING);
							account.setOpeningBalance(20000);
							account.setAsOf(new ClientFinanceDate().getTime());

							setAccountParticulars(account, accType);

							account
									.setComment("Account opening balance:20,000");

							create(account, getCreateCallBack(account));
						}

					}

				});
		return nextAccNum;
	}

	public void createAccounts() {
		isNonTransaction = true;
		String name[] = new String[] { "cahsAcc111", "incomeAcc111",
				"expnsAcc111", "bankAcc111", "creditCardAcc111" };
		int type[] = new int[] { ClientAccount.TYPE_CASH,
				ClientAccount.TYPE_INCOME, ClientAccount.TYPE_EXPENSE,
				ClientAccount.TYPE_BANK, ClientAccount.TYPE_CREDIT_CARD };

		for (int i = 0; i < 5; i++) {
			ClientAccount account = new ClientAccount();
			account.setType(type[i]);
			getNextAccountNumber(account, name[i], type[i]);

		}

	}

	public void setAccountParticulars(ClientAccount account, int accountType) {
		switch (accountType) {
		case ClientAccount.TYPE_BANK:
			account.setBank(Utility.getID(getCompany()
					.getBanks().get(0)));
			account.setBankAccountType(ClientAccount.BANK_ACCCOUNT_TYPE_SAVING);
			account.setBankAccountNumber("icici111");
			account.setIncrease(Boolean.FALSE);
			break;
		case ClientAccount.TYPE_CREDIT_CARD:

			account.setBank(Utility.getID(getObjectByName(Accounter
					.getCompany().getBanks(), "ICICI")));
			account.setCreditLimit(5000);
			account.setCardOrLoanNumber("visa101");
			break;
		}
	}

	<S extends IAccounterCore> S getObject(List<S> list, S object) {
		if (list == null)
			return null;
		for (S s : list) {
			if (s != null && s.getID() != null) {
				if (s.getID().equals(object.getID()))
					return s;
			}
		}
		return null;

	}

	<S extends IAccounterCore> S getObjectByName(List<S> list, String name) {
		if (list == null)
			return null;
		for (S s : list) {
			if (s != null && s.getID() != null) {
				if (s.getName().equals(name))
					return s;
			}
		}
		return null;

	}

	/*
	 * creates two types of addresses Bilto(isSelected=true for vendor);
	 * ShipTo(isSelected=true for customer)
	 */
	@SuppressWarnings("unchecked")
	Set<ClientAddress> getAddresss(int payeeType) {
		LinkedHashMap<Integer, ClientAddress> allAddresses = new LinkedHashMap<Integer, ClientAddress>();

		ClientAddress adrs1 = new ClientAddress();
		adrs1.setType(ClientAddress.TYPE_BILL_TO);
		adrs1.setStreet("SR Nagar");
		adrs1.setStateOrProvinence("AP");
		adrs1.setZipOrPostalCode("500018");
		adrs1.setCountryOrRegion("Uk");
		adrs1.setIsSelected(true);

		ClientAddress adrs2 = new ClientAddress();
		adrs2.setType(ClientAddress.TYPE_SHIP_TO);
		adrs2.setStreet("SR Nagar");
		adrs2.setStateOrProvinence("AP");
		adrs2.setZipOrPostalCode("500018");
		adrs2.setCountryOrRegion("Uk");
		adrs2.setIsSelected(false);

		allAddresses.put(ClientAddress.TYPE_BILL_TO, adrs1);
		allAddresses.put(ClientAddress.TYPE_SHIP_TO, adrs2);
		Set<ClientAddress> toBeSet = new HashSet<ClientAddress>();
		Collection add = allAddresses.values();
		Iterator it = add.iterator();
		while (it.hasNext()) {
			ClientAddress a = (ClientAddress) it.next();
			toBeSet.add(a);
		}
		return toBeSet;
	}

	Set<ClientPhone> getAllPhones() {
		Set<ClientPhone> toBeSetPhones = new HashSet<ClientPhone>();
		ClientPhone ph1 = new ClientPhone();
		ph1.setNumber("0403344");
		ph1.setType(ClientPhone.BUSINESS_PHONE_NUMBER);
		ph1.setIsSelected(true);

		ClientPhone ph2 = new ClientPhone();
		ph2.setNumber("0404455");
		ph2.setType(ClientPhone.MOBILE_PHONE_NUMBER);
		ph2.setIsSelected(true);

		toBeSetPhones.add(ph1);
		toBeSetPhones.add(ph2);

		return toBeSetPhones;
	}

	Set<ClientFax> getAllFaxes() {
		Set<ClientFax> toBeSetFaxes = new HashSet<ClientFax>();
		ClientFax fx1 = new ClientFax();
		fx1.setNumber("060123456");
		fx1.setType(ClientFax.TYPE_BUSINESS);
		fx1.setIsSelected(false);

		ClientFax fx2 = new ClientFax();
		fx2.setNumber("060123456");
		fx2.setType(ClientFax.TYPE_HOME);
		fx2.setIsSelected(true);

		toBeSetFaxes.add(fx1);
		toBeSetFaxes.add(fx2);

		return toBeSetFaxes;
	}

	Set<ClientEmail> getAllEmails() {
		Set<ClientEmail> toBeSetMails = new HashSet<ClientEmail>();
		ClientEmail m1 = new ClientEmail();
		m1.setEmail("mail1@abc.com");
		m1.setType(ClientEmail.TYPE_EMAIL_1);
		m1.setIsSelected(false);

		ClientEmail m2 = new ClientEmail();
		m2.setEmail("mail2@abc.com");
		m2.setType(ClientEmail.TYPE_EMAIL_2);
		m2.setIsSelected(true);

		toBeSetMails.add(m1);
		toBeSetMails.add(m2);

		return toBeSetMails;
	}

	public Set<ClientContact> getContacts() {
		Set<ClientContact> allContacts = new HashSet<ClientContact>();

		ClientContact contact1 = new ClientContact();
		contact1.setPrimary(true);
		contact1.setName("contactName1");
		contact1.setTitle("Home Contact");
		contact1.setBusinessPhone("040666666");
		contact1.setEmail("contactMail@abc.com");

		ClientContact contact2 = new ClientContact();
		contact2.setPrimary(false);
		contact2.setName("contactName2");
		contact2.setTitle("Home Contact");
		contact2.setBusinessPhone("040666666");
		contact2.setEmail("contactMail@abc.com");
		allContacts.add(contact1);
		allContacts.add(contact2);

		return allContacts;

	}

	@SuppressWarnings("unchecked")
	public void createCustomers() {
		isNonTransaction = true;
		String name[] = new String[] { "Customer111", "Customer222" };
		for (int i = 0; i < 2; i++) {
			ClientCustomer customer = new ClientCustomer();
			customer.setName(name[i]);
			customer.setFileAs(name[i]);
			// Setting Addresses
			customer.setAddress(getAddresss(1));

			// Setting Phone
			customer.setPhoneNumbers(getAllPhones());

			// Setting Fax
			customer.setFaxNumbers(getAllFaxes());

			// Setting Email and Internet
			customer.setEmails(getAllEmails());

			// Setting web page Address
			customer.setWebPageAddress("www.abc.com");

			// Setting Active
			customer.setActive(true);

			// Setting customer Since
			customer.setPayeeSince(new ClientFinanceDate().getTime());

			// Setting Balance
			customer.setOpeningBalance(20000);

			// Setting Balance As of
			customer.setBalanceAsOf(new ClientFinanceDate().getTime());

			// Setting Contacts
			customer.setContacts(getContacts());

			// Setting Memo
			customer.setMemo("This is  customer" + i
					+ " with opening balance 20,000");

			// Setting Data from Details Tab

			// Setting SalesPerson
			customer.setSalesPerson(null);

			// Setting Credit Limit

			customer.setCreditLimit(1000);

			// Setting Price Level
			customer.setPriceLevel(Utility.getID(Accounter
					.getCompany().getPriceLevels().get(0)));

			// Setting Credit Rating
			customer.setCreditRating(getCompany()
					.getCreditRatings().get(0).getID());

			// Setting Preferred Shipping Method
			customer.setShippingMethod(getCompany()
					.getShippingMethods().get(0).getID());

			// Setting Preferred Payment Method

			customer.setPaymentMethod(i == 1 ? "Cash" : "Check");

			// Setting Preferred Payment Terms
			customer.setPaymentTerm(Utility.getID(Accounter
					.getCompany().getPaymentsTerms().get(0)));

			// Setting customer Group
			customer.setCustomerGroup(Utility.getID(Accounter
					.getCompany().getCustomerGroups().get(0)));
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_US)
				// Setting Tax Group
				customer.setTaxGroup(Utility.getID(Accounter
						.getCompany().getTaxGroups().get(0)));

			else if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				// setting Vat Code
				customer.setTAXCode(Utility.getID(getObjectByName(
						getCompany().getTaxCodes(), "R")));
				customer.setVATRegistrationNumber("12345");
			}
			create(customer, getCreateCallBack(customer));

		}

	}

	@SuppressWarnings("unchecked")
	public void createCustomerIems() {
		isNonTransaction = true;
		String[] name = new String[] { "nonInventorySoldItm",
				"servcCustomerSoldPurchzdItm" };
		for (int i = 0; i < 2; i++) {
			ClientItem item = new ClientItem();

			item.setActive(true);
			// noninventory=3 servc = 1
			item.setType(i == 0 ? 3 : 1);
			item.setName(name[i]);
			item.setItemGroup(getCompany().getItemGroups()
					.get(0).getID());
			item.setStandardCost(1000);
			if (getCompany().getAccountingType() == 0
					&& i == 0)
				item.setUPCorSKU("Product1");

			if (i == 0)
				item.setWeight(10);

			item.setISellThisItem(true);
			item.setIBuyThisItem(i == 0 ? false : true);

			item.setSalesDescription("Customer:Item Sold for $1000");
			item.setSalesPrice(1000);
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				item.setIncomeAccount(getCompany()
						.getAccountByName("Discounts").getID());
			} else {
				item.setIncomeAccount(getCompany()
						.getAccountByName("incomeAcc111").getID());
			}
			item.setCommissionItem(i == 0 ? false : true);

			if (i == 1) {
				item.setPurchaseDescription("Customer:Item Purcased at $1000");
				item.setPurchasePrice(1000);
				item.setPreferredVendor(getCompany()
						.getVendorByName(
								UIUtils.getVendorString("Supplier111",
										"Vendor111")).getID());
				if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
					item.setExpenseAccount(getCompany()
							.getAccountByName("Discounts Taken").getID());
				} else {
					item.setExpenseAccount(getCompany()
							.getAccountByName("expnsAcc111").getID());
				}

				item.setVendorItemNumber("product" + i);
			}
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_US)
				item.setTaxable(true);
			else
				item.setTaxCode(getObjectByName(
						getCompany().getTaxCodes(), "S")
						.getID());
			create(item, getCreateCallBack(item));
		}
	}

	@SuppressWarnings("unchecked")
	public void createVendors() {
		isNonTransaction = true;

		String vName[] = new String[] { "Vendor111", "Vendor222" };
		for (int i = 0; i < 2; i++) {
			ClientVendor vendor = new ClientVendor();

			vendor.setName(vName[i]);

			// Setting File As
			vendor.setFileAs(vName[i]);

			vendor.setAddress(getAddresss(2));

			// Setting Phone
			vendor.setPhoneNumbers(getAllPhones());

			// Setting Fax
			vendor.setFaxNumbers(getAllFaxes());

			// Setting Email and Internet
			vendor.setEmails(getAllEmails());

			// Setting web page Address
			vendor.setWebPageAddress("mail1@abc.com");

			// Setting Active
			vendor.setActive(true);

			// Setting Vendor Since
			vendor.setPayeeSince(new ClientFinanceDate().getTime());

			// Setting Account Number
			vendor.setAccountNumber(i + "");

			// Setting Balance

			vendor.setOpeningBalance(20000);

			// Setting Balance As of
			vendor.setBalanceAsOf(new ClientFinanceDate().getTime());

			// Setting Contacts

			vendor.setContacts(getContacts());

			// Setting Memo
			vendor.setMemo("This is  Vendor" + i
					+ " with opening balance 20,000");

			// Setting Data from Details Tab

			// Setting Expense Account
			vendor.setExpenseAccount(Utility.getID(Accounter
					.getCompany().getAccounts().get(0)));

			// Setting Credit Limit
			vendor.setCreditLimit(1000);

			// Setting Preferred Shipping Method
			vendor.setShippingMethod(getCompany()
					.getShippingMethods().get(0).getID());

			// Setting Preferred Payment Method

			vendor.setPaymentMethod(i == 1 ? "Cash" : "Check");
			// Setting Preferred Payment Terms
			vendor.setPaymentTerms(Utility.getID(Accounter
					.getCompany().getPaymentsTerms().get(0)));
			// Setting Vendor Group
			vendor.setVendorGroup(Utility.getID(getCompany()
					.getVendorGroups().get(0)));

			vendor.setFederalTaxId("Federal");

			// Setting Account Payable
			vendor.setAccountsPayable(getCompany()
					.getAccountsPayableAccount());
			// Seting opening balance accounts
			vendor.setOpeningBalanceAccount(getCompany()
					.getOpeningBalancesAccount());
			vendor.setAccountsPayable(getCompany()
					.getAccountsPayableAccount());

			// Setting opening balance accounts

			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {

				vendor.setVATRegistrationNumber("123");
			}
			vendor.setTAXCode(Utility.getID(getObjectByName(Accounter
					.getCompany().getTaxCodes(), "R")));

			create(vendor, getCreateCallBack(vendor));
		}
	}

	@SuppressWarnings("unchecked")
	public void createVendorIems() {
		isNonTransaction = true;
		String[] name = new String[] { "nonInventoryPurchaseItm",
				"servcVendorSoldPurchzdItm" };
		for (int i = 0; i < 2; i++) {
			ClientItem item = new ClientItem();

			item.setActive(true);
			// noninventory=3 servc = 1
			item.setType(i == 0 ? 3 : 1);
			item.setName(name[i]);
			item.setItemGroup(getCompany().getItemGroups()
					.get(0).getID());
			item.setStandardCost(1000);
			if (getCompany().getAccountingType() == 0
					&& i == 0)
				item.setUPCorSKU("Product1");

			if (i == 0)
				item.setWeight(10);

			item.setISellThisItem(i == 0 ? false : true);
			item.setIBuyThisItem(true);

			if (i == 1) {
				item.setSalesDescription("Vendor:Product Sold  $1000");
				item.setSalesPrice(1000);
				if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
					item.setIncomeAccount(getCompany()
							.getAccountByName("Discounts").getID());
				} else {
					item.setIncomeAccount(getCompany()
							.getAccountByName("incomeAcc111").getID());
				}
				item.setCommissionItem(true);
			}

			item.setPurchaseDescription("Vendor:Product Purcased at $1000");
			item.setPurchasePrice(1000);
			item.setPreferredVendor(getCompany()
					.getVendorByName("Vendor222").getID());
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				item.setExpenseAccount(getCompany()
						.getAccountByName("Discounts Taken").getID());
			} else {
				item.setExpenseAccount(getCompany()
						.getAccountByName("expnsAcc111").getID());
			}

			item.setVendorItemNumber("product" + i);
			if (getCompany().getAccountingType() == 0)
				item.setTaxable(true);
			else
				item.setTaxCode(getObjectByName(
						getCompany().getTaxCodes(), "S")
						.getID());
			create(item, getCreateCallBack(item));
		}
	}

	public ClientAddress getAddress(Set<ClientAddress> addresses, int adressType) {
		for (ClientAddress adress : addresses) {
			if (adress.getType() == adressType)
				return adress;
		}
		return null;
	}

	double getCalculatedVATFraction(double lineTotal, ClientTAXCode taxCode,
			boolean isAmountIncludesVAT) {
		double vatRate = 0.0;
		// Checking the selected object is VATItem or VATGroup.
		// If it is VATItem,the we should get 'VATRate',otherwise 'GroupRate
		ClientTAXItemGroup vatItemGroup = getCompany()
				.getTAXItemGroup(taxCode.getTAXItemGrpForPurchases());
		
		if (vatItemGroup instanceof ClientTAXItem) {
			// The selected one is VATItem,so get 'VATRate' from 'VATItem'
			if (vatItemGroup != null)
				vatRate = ((ClientTAXItem) vatItemGroup).getTaxRate();
		} else {
			// The selected one is VATGroup,so get 'GroupRate' from
			// 'VATGroup'
			if (vatItemGroup != null)
				vatRate = ((ClientTAXGroup) vatItemGroup).getGroupRate();
		}

		double vat = 0.0;
		if (isAmountIncludesVAT)
			vat = lineTotal - (100 * (lineTotal / (100 + vatRate)));
		else
			vat = lineTotal * vatRate / 100;
		return vat;
	}

	List<ClientTransactionItem> getVendorTransactionItems(
			boolean isAmountIncludeVAT) {
		List<ClientTransactionItem> records = new ArrayList<ClientTransactionItem>();

		ClientTransactionItem record = new ClientTransactionItem();

		record.setType(ClientTransactionItem.TYPE_ITEM);
		// FIXME--need to check the index of the returnd item(since for
		// vendors,only purzditems shud set)
		record.setItem(getObjectByName(
				getCompany().getItems(),
				"servcVendorSoldPurchzdItm").getID());
		record.setDescription("this is product");
		record.setQuantity(1);
		record.setUnitPrice(getObjectByName(
				getCompany().getItems(),
				"servcVendorSoldPurchzdItm").getPurchasePrice());
		record.setLineTotal(record.getUnitPrice() * record.getQuantity());
		record.setTaxCode(getCompany().getItem(
				record.getItem()).getTaxCode());
		record
				.setVATfraction(getCalculatedVATFraction(record.getLineTotal(),
						getCompany().getTAXCode(
								record.getTaxCode()), isAmountIncludeVAT));

		records.add(record);
		if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_US)
			grandTotal = record.getVATfraction() + record.getLineTotal();
		else {
			if (isAmountIncludeVAT) {
				grandTotal = record.getLineTotal() - record.getVATfraction();

			} else {
				grandTotal = record.getLineTotal();
				totallinetotal = grandTotal + record.getVATfraction();
			}
		}

		return records;

	}

	double getTotal() {
		return totallinetotal;
	}

	double getNetAmount() {
		return grandTotal;
	}

	@SuppressWarnings("unchecked")
	public void createCashPurchases() {
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		for (int i = 0; i < 2; i++) {
			ClientCashPurchase cashPurchase = new ClientCashPurchase();

			// Setting Vendor
			cashPurchase.setVendor(getCompany()
					.getVendorByName(vName[i]).getID());

			// Setting Contact
			cashPurchase.setContact(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact());
			cashPurchase.setType(ClientTransaction.TYPE_CASH_PURCHASE);

			// Setting Address
			cashPurchase.setVendorAddress(getAddress((Accounter
					.getCompany().getVendorByName(vName[i]).getAddress()),
					ClientAddress.TYPE_BILL_TO));

			// Setting Phone
			cashPurchase.setPhone(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact()
					.getBusinessPhone());

			// Setting Payment Methods
			cashPurchase.setPaymentMethod(getCompany()
					.getVendorByName(vName[i]).getPaymentMethod());

			// Setting Pay From Account
			cashPurchase.setPayFrom(i == 1 ? getCompany()
					.getAccountByName("cahsAcc111").getID()
					: getCompany().getAccountByName(
							"bankAcc111").getID());

			// Setting Check number
			cashPurchase.setCheckNumber(i == 1 ? "0" : "1");

			// Setting Delivery date
			cashPurchase.setDeliveryDate(new ClientFinanceDate().getTime());

			// Setting Memo
			cashPurchase.setMemo("This is cashPurchaseView");
			// Setting Reference
			cashPurchase.setReference("This is refernce");

			cashPurchase.setAmountsIncludeVAT(i == 0 ? false : true);
			cashPurchase.setTransactionDate(new ClientFinanceDate().getTime());
			cashPurchase.setNumber("" + i + 1);
			cashPurchase
					.setTransactionItems(getVendorTransactionItems(cashPurchase
							.isAmountsIncludeVAT()));
			// Setting Total
			cashPurchase.setTotal(getTotal());

			create(cashPurchase, getCreateCallBack(cashPurchase));
		}

	}

	List<ClientTransactionItem> getCustomerTransactionItems(
			boolean isAmountIncludeVAT, ClientPriceLevel priceLevel) {
		List<ClientTransactionItem> records = new ArrayList<ClientTransactionItem>();

		ClientTransactionItem record = new ClientTransactionItem();

		record.setType(ClientTransactionItem.TYPE_ITEM);
		// FIXME--need to check the index of the returnd item(since for
		// vendors,only purzditems shud set)
		record.setItem(getObjectByName(
				getCompany().getItems(),
				"nonInventorySoldItm").getID());
		record.setDescription("this is product");
		record.setQuantity(1);

		double percentage = priceLevel.getPercentage()
				* (priceLevel.isPriceLevelDecreaseByThisPercentage() ? -1 : 1);
		double salesPrice = getObjectByName(
				getCompany().getItems(),
				"nonInventorySoldItm").getSalesPrice();
		double calcultdUnitPric = salesPrice + salesPrice * (percentage / 100);

		record.setUnitPrice(calcultdUnitPric);
		record.setDiscount(5.0);
		record.setLineTotal((calcultdUnitPric + calcultdUnitPric
				* record.getDiscount() / 100.0)
				* record.getQuantity());
		record.setTaxCode(getCompany().getItem(
				record.getItem()).getTaxCode());
		record
				.setVATfraction(getCalculatedVATFraction(record.getLineTotal(),
						getCompany().getTAXCode(
								record.getTaxCode()), isAmountIncludeVAT));

		// FIXME--need to set taxgroup for each record in US version

		records.add(record);
		if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_US)
			grandTotal = record.getVATfraction() + record.getLineTotal();
		else {
			if (isAmountIncludeVAT) {
				grandTotal = record.getLineTotal() - record.getVATfraction();

			} else {
				grandTotal = record.getLineTotal();
				totallinetotal = grandTotal + record.getVATfraction();
			}
		}

		return records;
	}

	@SuppressWarnings("unchecked")
	public void createQuotes() {
		isNonTransaction = false;
		String name[] = new String[] { "Customer111", "Customer222" };
		for (int i = 0; i < 2; i++) {
			ClientEstimate quote = new ClientEstimate();

			quote.setExpirationDate(new ClientFinanceDate().getTime());
			quote.setCustomer(getCompany()
					.getCustomerByName(name[i]).getID());
			quote.setContact(getCompany().getCustomerByName(
					name[i]).getPrimaryContact());
			quote.setPhone(getCompany().getCustomerByName(
					name[i]).getPrimaryContact().getBusinessPhone());

			quote.setDeliveryDate(new ClientFinanceDate().getTime());

			// quote.setSalesPerson(null);
			quote.setPriceLevel(getCompany()
					.getCustomerByName(name[i]).getPriceLevel());

			quote.setMemo("This is Quote of " + name[i]);

			quote.setAddress(getAddress(getCompany()
					.getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_BILL_TO));

			quote.setReference("This is reference");
			quote.setPaymentTerm(getCompany()
					.getCustomerByName(name[i]).getPaymentTerm());
			quote.setDate(new ClientFinanceDate().getTime());
			quote.setTransactionDate(new ClientFinanceDate().getTime());
			quote.setNumber("" + i + 1);
			quote.setType(ClientTransaction.TYPE_ESTIMATE);
			if (getCompany().getAccountingType() == 1)
				quote.setAmountsIncludeVAT(i == 0 ? false : true);

			quote.setTransactionItems(getCustomerTransactionItems(quote
					.isAmountsIncludeVAT(), getCompany()
					.getPriceLevel(quote.getPriceLevel())));

			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				quote.setNetAmount(getNetAmount());

			} else {
				// FIXME-- need to write for us version
				// quote.setSalesTax();
			}

			quote.setTotal(getTotal());
			create(quote, getCreateCallBack(quote));
		}
	}

	@SuppressWarnings("unchecked")
	public void createInvoices() {
		isNonTransaction = false;
		String name[] = new String[] { "Customer111", "Customer222" };
		for (int i = 0; i < 2; i++) {
			ClientInvoice invoice = new ClientInvoice();
			invoice.setCustomer(getCompany()
					.getCustomerByName(name[i]).getID());

			invoice.setDueDate(new ClientFinanceDate().getTime());
			invoice.setType(ClientTransaction.TYPE_INVOICE);
			invoice.setDeliverydate(new ClientFinanceDate().getTime());
			if (getCompany().getAccountingType() == 0)
				// invoice.setSalesTaxAmount(salesTaxTextNonEditable.getAmount());
				invoice.setContact(getCompany()
						.getCustomerByName(name[i]).getPrimaryContact());
			invoice.setPhone(getCompany().getCustomerByName(
					name[i]).getPrimaryContact().getBusinessPhone());
			invoice.setBillingAddress(getAddress(Accounter
					.getCompany().getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_BILL_TO));
			invoice.setShippingAdress(getAddress(Accounter
					.getCompany().getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_SHIP_TO));
			invoice.setSalesPerson(null);
			invoice.setPaymentTerm(getCompany()
					.getCustomerByName(name[i]).getPaymentTerm());

			invoice.setShippingTerm(getCompany()
					.getShippingTerms().get(0).getID());
			invoice.setShippingMethod(getCompany()
					.getCustomerByName(name[i]).getShippingMethod());
			invoice.setPriceLevel(getCompany()
					.getCustomerByName(name[i]).getPriceLevel());

			invoice.setAmountsIncludeVAT(i == 0 ? true : false);

			invoice.setMemo("This is invoice for" + name[i]);
			invoice.setReference("This is refernce for invoice" + i);

			ClientFinanceDate discountDate = Utility.getCalculatedDiscountDate(
					new ClientFinanceDate(), getCompany()
							.getPaymentTerms(
									getCompany()
											.getCustomerByName(name[i])
											.getPaymentTerm()));
			invoice.setDiscountDate(discountDate.getTime());

			// invoice.setEstimate(selectedEstimateId);
			// if (selectedSalesOrder != null)
			// invoice.setSalesOrder(selectedSalesOrder);

			invoice.setDate(new ClientFinanceDate().getTime());
			invoice.setTransactionDate(new ClientFinanceDate().getTime());
			invoice.setNumber("" + i + 1);

			invoice.setTransactionItems(getCustomerTransactionItems(invoice
					.isAmountsIncludeVAT(), getCompany()
					.getPriceLevel(invoice.getPriceLevel())));
			invoice.setTotal(getTotal());
			invoice.setBalanceDue(getTotal());
			invoice.setPayments(0);

			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_US) {
				// if (taxGroup != null) {
				// for (ClientTransactionItem record : customerTransactionGrid
				// .getRecords()) {
				// record.setTaxGroup(taxGroup.getID());
				//
				// }
				// }
				// invoice.setSalesTaxAmount(this.salesTax);
			} else if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				invoice.setNetAmount(getNetAmount());
			}

			create(invoice, getCreateCallBack(invoice));
		}

	}

	@SuppressWarnings("unchecked")
	public void createCashSales() {
		isNonTransaction = false;
		String name[] = new String[] { "Customer111", "Customer222" };
		for (int i = 0; i < 2; i++) {
			ClientCashSales cashSale = new ClientCashSales();

			cashSale.setCustomer(getCompany()
					.getCustomerByName(name[i]).getID());
			cashSale.setType(ClientTransaction.TYPE_CASH_SALES);
			cashSale.setDepositIn(i == 1 ? getCompany()
					.getAccountByName("cahsAcc111").getID()
					: getCompany().getAccountByName(
							"bankAcc111").getID());
			cashSale.setContact(getCompany()
					.getCustomerByName(name[i]).getPrimaryContact());
			cashSale.setPhone(getCompany()
					.getCustomerByName(name[i]).getPrimaryContact()
					.getBusinessPhone());
			cashSale.setSalesPerson(null);
			cashSale.setBillingAddress(getAddress(Accounter
					.getCompany().getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_BILL_TO));
			cashSale.setShippingAdress(getAddress(Accounter
					.getCompany().getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_SHIP_TO));
			cashSale.setShippingTerm(getCompany()
					.getShippingTerms().get(0).getID());
			cashSale.setShippingMethod(getCompany()
					.getCustomerByName(name[i]).getShippingMethod());
			cashSale.setPaymentMethod(getCompany()
					.getCustomerByName(name[i]).getPaymentMethod());

			cashSale.setDeliverydate(new ClientFinanceDate().getTime());
			cashSale.setPriceLevel((getCompany()
					.getCustomerByName(name[i]).getPriceLevel()));
			cashSale.setMemo("This Cash Sale by: " + name[i]);
			cashSale.setReference("This cashsale " + i);
			cashSale.setTransactionDate(new ClientFinanceDate().getTime());
			cashSale.setNumber("" + i + 1);
			cashSale.setDate(new ClientFinanceDate().getTime());
			cashSale.setAmountsIncludeVAT(i == 0 ? false : true);
			cashSale.setTransactionItems(getCustomerTransactionItems(cashSale
					.isAmountsIncludeVAT(), getCompany()
					.getPriceLevel(cashSale.getPriceLevel())));
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				cashSale.setNetAmount(getNetAmount());
			} else {
				// if (salesTax != null)
				// cashSale.setSalesTax(salesTax);
			}
			cashSale.setTotal(getTotal());

			create(cashSale, getCreateCallBack(cashSale));
		}
	}

	@SuppressWarnings("unchecked")
	public void createCustomerCredits() {
		isNonTransaction = false;

		String name[] = new String[] { "Customer111", "Customer222" };
		for (int i = 0; i < 2; i++) {
			ClientCustomerCreditMemo creditMemo = new ClientCustomerCreditMemo();

			creditMemo.setCustomer(getCompany()
					.getCustomerByName(name[i]).getID());
			creditMemo.setType(ClientTransaction.TYPE_CUSTOMER_CREDIT_MEMO);
			creditMemo.setContact(getCompany()
					.getCustomerByName(name[i]).getPrimaryContact());
			creditMemo.setSalesPerson(null);
			creditMemo.setPhone(getCompany()
					.getCustomerByName(name[i]).getPrimaryContact()
					.getBusinessPhone());
			creditMemo.setBillingAddress(getAddress(Accounter
					.getCompany().getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_BILL_TO));
			creditMemo.setPriceLevel((getCompany()
					.getCustomerByName(name[i]).getPriceLevel()));
			creditMemo.setMemo("This is the credit for " + name[i]);
			creditMemo.setReference("This is ference: " + i);
			creditMemo.setAmountsIncludeVAT(i == 0 ? false : true);
			creditMemo.setDate(new ClientFinanceDate().getTime());
			creditMemo.setNumber("" + i + 1);
			creditMemo.setTransactionItems(getCustomerTransactionItems(
					creditMemo.isAmountsIncludeVAT(), Accounter
							.getCompany().getPriceLevel(
									creditMemo.getPriceLevel())));
			if (getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK) {
				creditMemo.setNetAmount(getNetAmount());
			} else
				// creditMemo.setSalesTax(this.salesTax);

				creditMemo.setTotal(getTotal());

			create(creditMemo, getCreateCallBack(creditMemo));
		}
	}

	@SuppressWarnings("unchecked")
	public void createCustomerRefunds() {
		isNonTransaction = false;
		String name[] = new String[] { "Customer111", "Customer222",
				"Customer222" };
		for (int i = 0; i < 3; i++) {
			ClientCustomerRefund refund = new ClientCustomerRefund();

			refund.setDate(new ClientFinanceDate().getTime());

			refund.setNumber("" + i + 1);

			refund.setPayTo(getCompany().getCustomerByName(
					name[i]).getID());

			refund.setAddress(getAddress(getCompany()
					.getCustomerByName(name[i]).getAddress(),
					ClientAddress.TYPE_BILL_TO));

			refund.setPayFrom(Utility.getID(getCompany()
					.getAccountByName("cahsAcc111")));

			refund.setTotal(1000);
			refund.setBalanceDue(refund.getTotal());
			refund.setCustomerBalance(getCompany()
					.getCustomerByName(name[i]).getBalance()
					+ refund.getTotal());
			refund.setEndingBalance(getCompany()
					.getAccountByName("cahsAcc111").getTotalBalance()
					- refund.getTotal());

			refund.setPaymentMethod(getCompany()
					.getCustomerByName(name[i]).getPaymentMethod());

			refund.setIsToBePrinted(i == 3 ? true : false);
			refund.setCheckNumber(i == 3 ? "-1" : (i == 2 ? "111" : "0"));

			refund.setMemo("This is a refund to: " + name[i]);

			refund.setReference("This is refenrece: " + i);

			refund.setType(ClientTransaction.TYPE_CUSTOMER_REFUNDS);
			refund.setTransactionItems(null);
			create(refund, getCreateCallBack(refund));
		}
	}

	private void getTransactionRecievePayments(
			final ClientReceivePayment receivePayment,
			final ClientCustomer selectedCustomer, final double recievedAmount) {
		long paymentDate = new ClientFinanceDate().getTime();
		Accounter.createHomeService().getTransactionReceivePayments(
				selectedCustomer.getID(), paymentDate,
				new AsyncCallback<List<ReceivePaymentTransactionList>>() {

					private double unusedAmount;

					public void onFailure(Throwable caught) {
						Accounter.showError(Accounter
								.getCustomersMessages()
								.failedToGetRecievePayments()
								+ selectedCustomer.getName());
					}

					@SuppressWarnings("unchecked")
					public void onSuccess(
							List<ReceivePaymentTransactionList> result) {

						ClientReceivePayment clientReceivePayment = receivePayment;
						if (result == null)
							return;
						// totalInoiceAmt = 0.0d;
						// totalDueAmt = 0.0d;

						List<ClientTransactionReceivePayment> records = new ArrayList<ClientTransactionReceivePayment>();

						for (ReceivePaymentTransactionList receivePaymentTransaction : result) {

							ClientTransactionReceivePayment record = new ClientTransactionReceivePayment();

							record
									.setDueDate(receivePaymentTransaction
											.getDueDate() != null ? receivePaymentTransaction
											.getDueDate().getTime()
											: 0);
							record.setNumber(receivePaymentTransaction
									.getNumber());

							record.setInvoiceAmount(receivePaymentTransaction
									.getInvoiceAmount());

							record.setInvoice(receivePaymentTransaction
									.getTransactionId());
							record.setAmountDue(receivePaymentTransaction
									.getAmountDue());
							record
									.setDiscountDate(receivePaymentTransaction
											.getDiscountDate() != null ? receivePaymentTransaction
											.getDiscountDate().getTime()
											: 0);

							record.setCashDiscount(receivePaymentTransaction
									.getCashDiscount());

							record.setWriteOff(receivePaymentTransaction
									.getWriteOff());

							record.setAppliedCredits(receivePaymentTransaction
									.getAppliedCredits());

							record.setPayment(100);

							if (receivePaymentTransaction.getType() == ClientTransaction.TYPE_INVOICE) {
								record.isInvoice = true;
								record.setInvoice(receivePaymentTransaction
										.getTransactionId());
							} else if (receivePaymentTransaction.getType() == ClientTransaction.TYPE_CUSTOMER_REFUNDS) {
								record.isInvoice = false;
								record
										.setCustomerRefund(receivePaymentTransaction
												.getTransactionId());
							} else if (receivePaymentTransaction.getType() == ClientTransaction.TYPE_JOURNAL_ENTRY) {
								record.isInvoice = false;
								record
										.setJournalEntry(receivePaymentTransaction
												.getTransactionId());
							}
							records.add(record);
							clientReceivePayment
									.setTransactionReceivePayment(records);
						}
						double totalPayment = 0.0;
						for (ClientTransactionReceivePayment record : records) {
							totalPayment += record.getPayment();
						}

						unusedAmount = recievedAmount - totalPayment;
						clientReceivePayment.setUnUsedPayments(unusedAmount);

						for (ClientTransactionReceivePayment payment : receivePayment
								.getTransactionReceivePayment()) {
							ClientAccount cashAcc = Accounter
									.getCompany()
									.getAccountByName("cahsAcc111");
							payment.setDiscountAccount(cashAcc.getID());

							ClientAccount wrrittoff = Accounter
									.getCompany().getAccountByName(
											"incomeAcc111");
							payment.setWriteOffAccount(wrrittoff.getID());
							/* No credits are used... */
							payment.setTransactionCreditsAndPayments(null);
							receivePayment.setUnUsedCredits(0);

							create(receivePayment,
									getCreateCallBack(receivePayment));
						}
					}

				});
	}

	public void createRecievePayments() {
		isNonTransaction = false;
		String name[] = new String[] { "Customer111", "Customer222" };
		String depAccs[] = new String[] { "cahsAcc111", "Un Deposited Funds" };
		for (int i = 0; i < 2; i++) {
			ClientReceivePayment receivePayment = new ClientReceivePayment();

			receivePayment.setDate(new ClientFinanceDate().getTime());
			receivePayment.setCustomer(getCompany()
					.getCustomerByName(name[i]).getID());
			receivePayment.setPaymentMethod(getCompany()
					.getCustomerByName(name[i]).getPaymentMethod());
			receivePayment.setDepositIn(getCompany()
					.getAccountByName(depAccs[i]).getID());

			receivePayment.setNumber("" + i + 1);
			receivePayment.setReference("This is reference: " + i);
			receivePayment.setMemo("This payment recieved from: " + name[i]);
			receivePayment.setCustomerBalance(getCompany()
					.getCustomerByName(name[i]).getBalance());

			receivePayment.setTotal(100);

			getTransactionRecievePayments(receivePayment, Accounter
					.getCompany().getCustomerByName(name[i]), receivePayment
					.getTotal());

		}
	}

	private void getPayBillsForVendor(final ClientVendor vendor,
			final ClientPayBill payBl) {
		Accounter.createHomeService().getTransactionPayBills(
				vendor.getID(),
				new AsyncCallback<List<PayBillTransactionList>>() {

					public void onFailure(Throwable caught) {
						// SC
						// .say("Failed to Get List of Transaction Recieve Payments for this Vendor"
						// + vendor.getName());

					}

					@SuppressWarnings("unchecked")
					public void onSuccess(List<PayBillTransactionList> result) {

						ClientPayBill payBill = payBl;

						if (result == null)
							return;
						double totalOrginalAmt = 0.0d;
						double totalDueAmt = 0.0d;
						double totalPayment = 0.0d;
						// preparing ClientTransactionPayBill objects
						List<ClientTransactionPayBill> records = new ArrayList<ClientTransactionPayBill>();
						for (PayBillTransactionList curntRec : result) {
							ClientTransactionPayBill record = new ClientTransactionPayBill();

							if (curntRec.getType() == ClientTransaction.TYPE_ENTER_BILL) {
								record
										.setEnterBill(curntRec
												.getTransactionId());
							} else if (curntRec.getType() == ClientTransaction.TYPE_MAKE_DEPOSIT) {
								record.setTransactionMakeDeposit(curntRec
										.getTransactionId());
							} else if (curntRec.getType() == ClientTransaction.TYPE_JOURNAL_ENTRY) {
								record.setJournalEntry(curntRec
										.getTransactionId());
							}

							record.setBillNumber(curntRec.getBillNumber());

							record.setCashDiscount(curntRec.getCashDiscount());

							record.setAppliedCredits(curntRec.getCredits());

							record
									.setDiscountDate(curntRec.getDiscountDate() != null ? curntRec
											.getDiscountDate().getTime()
											: 0);

							record
									.setDueDate(curntRec.getDueDate() != null ? curntRec
											.getDueDate().getTime()
											: 0);

							record.setOriginalAmount(curntRec
									.getOriginalAmount());
							record.setAmountDue(curntRec.getAmountDue());

							record.setPayment(100);
							ClientVendor vendor = Accounter
									.getCompany().getVendorByName(
											curntRec.getVendorName());
							if (vendor != null)
								record.setVendor(vendor.getID());

							totalOrginalAmt += record.getOriginalAmount();
							totalDueAmt += record.getAmountDue();
							totalPayment += record.getPayment();

							records.add(record);
						}

						// Setting Transactions
						List<ClientTransactionPayBill> selectedRecords = records;

						List<ClientTransactionPayBill> transactionPayBill = new ArrayList<ClientTransactionPayBill>();
						for (ClientTransactionPayBill tpbRecord : selectedRecords) {

							tpbRecord.setAccountsPayable(Accounter
									.getCompany().getAccountsPayableAccount());
							tpbRecord.setPayBill(payBill);

							ClientAccount cashAcc = Accounter
									.getCompany()
									.getAccountByName("cahsAcc111");
							tpbRecord.setDiscountAccount(cashAcc.getID());

							/* No credits are applied */
							// List<ClientTransactionCreditsAndPayments> trpList
							// = (List<ClientTransactionCreditsAndPayments>)
							// gridView
							// .getAttributeAsObject("creditsAndPayments",
							// gridView.indexOf(tpbRecord));
							// if (trpList != null)
							// for (ClientTransactionCreditsAndPayments temp :
							// trpList) {
							// temp.setTransactionPayBill(tpbRecord);
							// }

							tpbRecord.setTransactionCreditsAndPayments(null);
							transactionPayBill.add(tpbRecord);
						}
						payBill.setTransactionPayBill(transactionPayBill);

						double total = 0.0;
						double toBeSetEndingBalance = 0.0;
						for (ClientTransactionPayBill trPb : payBill
								.getTransactionPayBill()) {
							total += trPb.getPayment();
						}
						// Setting Amount
						payBill.setTotal(total);
						ClientAccount payFromAccount = Accounter
								.getCompany().getAccount(payBill.getPayFrom());
						if (payFromAccount.isIncrease()) {
							toBeSetEndingBalance = payFromAccount
									.getTotalBalance()
									+ payBill.getTotal();
						} else {
							toBeSetEndingBalance = payFromAccount
									.getTotalBalance()
									- payBill.getTotal();
						}

						// Setting ending Balance
						payBill.setEndingBalance(toBeSetEndingBalance);

						create(payBill, getCreateCallBack(payBill));
					}

				});
	}

	public void creatVendorPayBills() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		String payFrmAcc[] = new String[] { "cahsAcc111", "Un Deposited Funds" };
		for (int i = 0; i < 2; i++) {
			ClientPayBill payBill = new ClientPayBill();

			// Setting Type of Enter Bill
			payBill.setType(ClientTransaction.TYPE_PAY_BILL);
			payBill.setPayBillType(ClientPayBill.TYPE_PAYBILL);

			// Setting Accounts Payable
			payBill.setAccountsPayable(getCompany()

			.getAccountsPayableAccount());

			// Setting Date
			payBill.setDate(new ClientFinanceDate().getTime());

			// Setting Pay From
			payBill.setPayFrom(getCompany()
					.getAccountByName(payFrmAcc[i]));

			// Setting payment method
			payBill.setPaymentMethod(getCompany()
					.getVendorByName(vName[i]).getPaymentMethod());

			// Setting Bill due or before
			payBill.setBillDueOnOrBefore(new ClientFinanceDate().getTime());
			// Setting Vendor
			payBill.setVendor(getCompany().getVendorByName(
					vName[i]));
			getPayBillsForVendor(getCompany()
					.getVendorByName(vName[i]), payBill);
		}

	}

	@SuppressWarnings("unchecked")
	public void createVendorCreditMemo() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		for (int i = 0; i < 2; i++) {
			ClientVendorCreditMemo vendorCreditMemo = new ClientVendorCreditMemo();

			// Setting Vendor
			vendorCreditMemo.setVendor(getCompany()
					.getVendorByName(vName[i]).getID());

			// Setting Contact
			vendorCreditMemo.setContact(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact());

			vendorCreditMemo.setType(ClientTransaction.TYPE_VENDOR_CREDIT_MEMO);

			// Setting Phone

			vendorCreditMemo.setPhone(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact()
					.getBusinessPhone());

			// Setting Memo
			vendorCreditMemo.setMemo("This is Credit");
			// Setting Reference
			vendorCreditMemo.setReference("This is reference");
			vendorCreditMemo.setAmountsIncludeVAT(i == 0 ? false : true);

			vendorCreditMemo.setTransactionDate(new ClientFinanceDate()
					.getTime());
			vendorCreditMemo.setNumber("" + i + 1);
			vendorCreditMemo.setDate(new ClientFinanceDate().getTime());
			vendorCreditMemo
					.setTransactionItems(getVendorTransactionItems(vendorCreditMemo
							.isAmountsIncludeVAT()));
			// Setting Total
			vendorCreditMemo.setTotal(getTotal());

			create(vendorCreditMemo, getCreateCallBack(vendorCreditMemo));
		}

	}

	@SuppressWarnings("unchecked")
	public void createEnterBill() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		for (int i = 0; i < 2; i++) {
			ClientEnterBill enterBill = new ClientEnterBill();

			// Setting Vendor
			enterBill.setVendor(getCompany()
					.getVendorByName(vName[i]).getID());

			// Setting Contact

			enterBill.setContact(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact());

			enterBill.setType(ClientTransaction.TYPE_ENTER_BILL);

			// Setting Address
			enterBill.setVendorAddress(getAddress((Accounter
					.getCompany().getVendorByName(vName[i]).getAddress()),
					ClientAddress.TYPE_BILL_TO));

			// Setting Phone

			enterBill.setPhone(getCompany().getVendorByName(
					vName[i]).getPrimaryContact().getBusinessPhone());

			// Setting Payment Terms

			enterBill.setPaymentTerm(Utility.getID(Accounter
					.getCompany().getPaymentsTerms().get(0)));

			// Setting Due date
			enterBill.setDueDate(new ClientFinanceDate().getTime());

			// Setting Delivery date

			enterBill.setDeliveryDate(new ClientFinanceDate().getTime());

			// Setting Memo
			enterBill.setMemo("This is Credit");
			// Setting Reference
			enterBill.setReference("This is reference");
			enterBill.setAmountsIncludeVAT(i == 0 ? false : true);

			enterBill.setTransactionDate(new ClientFinanceDate().getTime());
			enterBill.setNumber("" + i + 1);
			enterBill.setDate(new ClientFinanceDate().getTime());
			enterBill.setTransactionItems(getVendorTransactionItems(enterBill
					.isAmountsIncludeVAT()));
			enterBill.setTotal(getTotal());
			enterBill.setBalanceDue1(enterBill.getTotal());

			create(enterBill, getCreateCallBack(enterBill));

		}

	}

	@SuppressWarnings("unchecked")
	public void createVendorPayment() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2", "Vendor2" };
		for (int i = 0; i < 3; i++) {
			ClientPayBill payBill = new ClientPayBill();
			payBill.setVendor(getCompany().getVendorByName(
					vName[i]));

			payBill.setAddress(getAddress((getCompany()
					.getVendorByName(vName[i]).getAddress()),
					ClientAddress.TYPE_BILL_TO));

			payBill.setPayFrom(i == 1 ? getCompany()
					.getAccountByName("cahsAcc111") : Accounter
					.getCompany().getAccountByName("bankAcc111"));

			payBill.setType(ClientTransaction.TYPE_PAY_BILL);

			payBill.setPayBillType(ClientTransaction.TYPE_VENDOR_PAYMENT);

			payBill.setPaymentMethod(getCompany()
					.getVendorByName(vName[i]).getPaymentMethod());

			payBill.setToBePrinted(i == 3 ? true : false);
			payBill.setCheckNumber(String.valueOf((i == 3 ? -1 : (i == 2 ? 111 : 0))));

			// Setting Memo
			payBill.setMemo("This is Credit");
			// Setting Reference
			payBill.setReference("This is reference");

			// Setting UnusedAmount
			payBill.setUnusedAmount(1000);
			payBill.setVendorBalance(getCompany()
					.getVendorByName(vName[i]).getBalance()
					- payBill.getUnusedAmount());
			payBill.setEndingBalance(getCompany()
					.getAccountByName("cahsAcc111").getTotalBalance()
					- payBill.getUnusedAmount());

			payBill.setNumber("" + i + 1);
			payBill.setTransactionDate(new ClientFinanceDate().getTime());
			payBill.setDate(new ClientFinanceDate().getTime());
			payBill.setTransactionItems(null);
			// Setting Total
			payBill.setTotal(0);

			create(payBill, getCreateCallBack(payBill));
		}
	}

	public void createIssuePayments() {
		isNonTransaction = false;
		ClientIssuePayment issuePayment = new ClientIssuePayment();

		issuePayment.setType(ClientTransaction.TYPE_ISSUE_PAYMENT);

		issuePayment.setNumber("" + 1);

		issuePayment.setDate(new ClientFinanceDate().getTime());

		issuePayment.setPaymentMethod("Check");

		issuePayment.setAccount(getCompany()
				.getAccountByName("bankAcc111").getID());
		final ClientIssuePayment isuePaymnt = issuePayment;

		Accounter.createHomeService().getChecks(
				issuePayment.getAccount(),
				new AsyncCallback<List<IssuePaymentTransactionsList>>() {

					public void onFailure(Throwable t) {
						// UIUtils
						// .logError(
						// "Failed to get the IssuePaymentTransactionsList..",
						// t);
					}

					@SuppressWarnings("unchecked")
					public void onSuccess(
							List<IssuePaymentTransactionsList> result) {

						if (result == null) {
							onFailure(null);
							return;
						}
						List<ClientTransactionIssuePayment> transactionIssuePaymentsList = new ArrayList<ClientTransactionIssuePayment>();
						for (IssuePaymentTransactionsList entry : result) {
							ClientTransactionIssuePayment record = new ClientTransactionIssuePayment();
							if (entry.getDate() != null)
								record.setDate(entry.getDate().getTime());
							if (entry.getNumber() != null)
								record.setNumber(entry.getNumber());
							record.setName(entry.getName() != null ? entry
									.getName() : "");
							record.setMemo(entry.getMemo() != null ? entry
									.getMemo() : "");
							if (entry.getAmount() != null)
								record.setAmount(entry.getAmount());
							if (entry.getPaymentMethod() != null)
								record.setPaymentMethod(entry
										.getPaymentMethod());
							entry.setMemo(entry.getMemo());
							record.setRecordType(entry.getType());
							if (record.getRecordType() == ClientTransaction.TYPE_WRITE_CHECK)
								record.setWriteCheck(entry.getTransactionId());
							else if (record.getRecordType() == ClientTransaction.TYPE_CUSTOMER_REFUNDS)
								record.setCustomerRefund(entry
										.getTransactionId());
							record.setID(entry.getTransactionId());
							record.setTransaction(isuePaymnt);

							transactionIssuePaymentsList.add(record);

						}

						// isuePaymnt.setTotal(totalAmount);
						isuePaymnt.setCheckNumber(String.valueOf(222));

						isuePaymnt
								.setTransactionIssuePayment(transactionIssuePaymentsList);

						create(isuePaymnt, getCreateCallBack(isuePaymnt));
					}

				});
	}

	@SuppressWarnings("unchecked")
	public void createItemReceipt() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		for (int i = 0; i < 2; i++) {
			ClientItemReceipt itemReceipt = new ClientItemReceipt();

			// Setting Vendor
			itemReceipt.setVendor(getCompany()
					.getVendorByName(vName[i]).getID());

			itemReceipt.setType(ClientTransaction.TYPE_ITEM_RECEIPT);

			// Setting Contact

			itemReceipt.setContact(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact());

			// Setting Address

			itemReceipt.setVendorAddress(getAddress((Accounter
					.getCompany().getVendorByName(vName[i]).getAddress()),
					ClientAddress.TYPE_BILL_TO));

			// Setting Phone
			itemReceipt.setPhone(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact()
					.getBusinessPhone());

			itemReceipt.setPaymentTerm(Utility.getID(Accounter
					.getCompany().getPaymentsTerms().get(0)));

			itemReceipt.setDeliveryDate(new ClientFinanceDate().getTime());

			itemReceipt.setMemo("This is ItemRecept");
			itemReceipt.setReference("This is Referance");

			itemReceipt.setAmountsIncludeVAT(i == 0 ? false : true);
			itemReceipt.setNumber("" + i + 1);
			itemReceipt.setDate(new ClientFinanceDate().getTime());
			itemReceipt.setTransactionDate(new ClientFinanceDate().getTime());
			itemReceipt
					.setTransactionItems(getVendorTransactionItems(itemReceipt
							.isAmountsIncludeVAT()));
			// Setting Total
			itemReceipt.setTotal(getTotal());
			create(itemReceipt, getCreateCallBack(itemReceipt));

		}

	}

	@SuppressWarnings("unchecked")
	public void createTransferFunds() {
		isNonTransaction = false;

		for (int i = 0; i < 2; i++) {
			ClientTransferFund transferFund = new ClientTransferFund();
			transferFund.setType(ClientTransaction.TYPE_TRANSFER_FUND);

			transferFund.setDate(new ClientFinanceDate().getTime());
			transferFund
					.setMemo("Transfering 1000 from cashAcc111 to Undeposited Account");
			transferFund.setTransferFrom(Utility.getID(Accounter
					.getCompany().getAccountByName("cahsAcc111")));
			transferFund.setTransferTo(Utility.getID(Accounter
					.getCompany().getAccountByName("Un Deposited Funds")));

			transferFund.setTotal(1000);

			create(transferFund, getCreateCallBack(transferFund));
		}

	}

	@SuppressWarnings("unchecked")
	public void createCreditCardCharge() {
		isNonTransaction = false;
		String vName[] = new String[] { "Vendor1", "Vendor2" };
		for (int i = 0; i < 2; i++) {
			ClientCreditCardCharge creditCardCharge = new ClientCreditCardCharge();

			creditCardCharge
					.setType(ClientCreditCardCharge.TYPE_CREDIT_CARD_CHARGE);
			// setting date
			creditCardCharge.setNumber("" + i + 1);

			creditCardCharge.setDate(new ClientFinanceDate().getTime());
			// setting number

			creditCardCharge.setVendor(getCompany()
					.getVendorByName(vName[i]).getID());

			// setting contact
			creditCardCharge.setContact(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact());

			creditCardCharge.setVendorAddress(getAddress((Accounter
					.getCompany().getVendorByName(vName[i]).getAddress()),
					ClientAddress.TYPE_BILL_TO));

			// setting phone

			creditCardCharge.setPhone(getCompany()
					.getVendorByName(vName[i]).getPrimaryContact()
					.getBusinessPhone());

			// Setting payment method

			creditCardCharge.setPaymentMethod(getCompany()
					.getVendorByName(vName[i]).getPaymentMethod());

			// Setting pay from

			creditCardCharge.setPayFrom(i == 1 ? Accounter
					.getCompany().getAccountByName("cahsAcc111").getID()
					: getCompany().getAccountByName(
							"bankAcc111").getID());

			// setting check no

			creditCardCharge.setCheckNumber(i == 1 ? "0" : "121");

			creditCardCharge.setAmountsIncludeVAT(i == 0 ? false : true);

			// setting delivery date
			creditCardCharge.setDeliveryDate(new ClientFinanceDate().getTime());

			// Setting transactions
			creditCardCharge
					.setTransactionItems(getVendorTransactionItems(creditCardCharge
							.isAmountsIncludeVAT()));

			// setting memo
			creditCardCharge.setMemo("This  Credit Card Charge to:" + vName[i]);
			// setting ref
			creditCardCharge.setReference("This is referance: " + i);
			creditCardCharge.setTotal(getTotal());

			create(creditCardCharge, getCreateCallBack(creditCardCharge));

		}
	}

	@SuppressWarnings("unchecked")
	public void createMakeDeposite() {
		isNonTransaction = false;

		String account[] = new String[] { "cahsAcc111", "bankAcc111" };
		for (int i = 0; i < 2; i++) {
			ClientMakeDeposit makeDeposit = new ClientMakeDeposit();

			makeDeposit.setDate(new ClientFinanceDate().getTime());

			// Setting Deposit in
			makeDeposit.setDepositIn(getCompany()
					.getAccountByName(account[i]).getID());

			makeDeposit.setMemo("This is Make Deposite Memo");

			ClientTransactionMakeDeposit records = new ClientTransactionMakeDeposit();
			records.setDate(new ClientFinanceDate().getTime());
			records.setNumber("" + i + 1);
			records.setPaymentMethod(" ");
			records.setType(ClientTransactionMakeDeposit.TYPE_VENDOR);
			records.setVendor(getCompany().getVendorByName(
					"Vendor2").getID());
			records.setReference("This is reference");

			records.setAmount(1000);

			records.setIsNewEntry(true);
			List<ClientTransactionMakeDeposit> listOfTrannsactionMakeDeposits = new ArrayList<ClientTransactionMakeDeposit>();
			listOfTrannsactionMakeDeposits.add(records);
			makeDeposit
					.setTransactionMakeDeposit(listOfTrannsactionMakeDeposits);

			makeDeposit.setCashBackAccount(getCompany()
					.getAccountByName("expnsAcc111").getID());

			makeDeposit.setCashBackMemo("This is a deposit to the account "
					+ account[i]);

			makeDeposit.setCashBackAmount(100);

			makeDeposit.setTotal(getTotal());

			makeDeposit.setType(ClientTransaction.TYPE_MAKE_DEPOSIT);

			create(makeDeposit, getCreateCallBack(makeDeposit));

		}

	}

	// ----------------------------------------------need to check
	@SuppressWarnings("unchecked")
	public void createFixedAssets() {
		isNonTransaction = true;
		String item[] = { "Computer", "BikClientFixedAssete" };
		for (int i = 0; i < 3; i++) {
			ClientFixedAsset asset = new ClientFixedAsset();

			asset.setName(item[i]);
			asset.setAssetNumber("" + i + 1);

			asset.setAssetAccount(i == 1 ? getCompany()
					.getAccountByName("IT Equipment").getID()
					: getCompany().getAccountByName(
							"Vehicles").getID());

			asset.setPurchaseDate(new ClientFinanceDate().getTime());
			asset.setPurchasePrice(1000);
			asset.setDescription("This is Fixed Product");

			asset.setAssetType("Fixed Type");
			asset.setDepreciationRate(i);
			// asset.setDepreciationMethod(ClientAccount.) == 1 ? 0 : 1);

			asset.setDepreciationExpenseAccount((i == 1 ? Accounter
					.getCompany().getAccountByName("IT Equipment")
					.getID() : getCompany()
					.getAccountByName("Vehicles").getID()));

			// asset.setAccumulatedDepreciationAmount(accmulatdDepreciationTxt.getAmount());

			asset.setStatus(ClientFixedAsset.STATUS_REGISTERED);
			create(asset, getCreateCallBack(asset));

		}

	}

	@SuppressWarnings("unchecked")
	public <S extends IAccounterCore> AsyncCallback getCreateCallBack(
			final S core) {
		return new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				// Accounter.showError("SAve Failed");
			}

			public void onSuccess(String result) {
				if (result == null)
					onFailure(new Exception());
				if (isNonTransaction)
					Accounter.showInformation("Save Success.Refresh Now");
			}
		};
	}
}
