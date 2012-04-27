package com.vimukti.accounter.core;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.ui.DataUtils;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

public class PayBillPdfGeneration {

	private final PayBill payBill;
	private final Company company;

	public PayBillPdfGeneration(PayBill payBill, Company company) {
		this.payBill = payBill;
		this.company = company;
	}

	public IContext assignValues(IContext context, IXDocReport report) {

		try {
			PaybillTemplete paybillTemp = new PaybillTemplete();

			paybillTemp.setTitle(Global.get().messages().payBill());
			paybillTemp.setName(payBill.getVendor().getName());
			paybillTemp.setRegisteredAddress(getRegistrationAddress());
			paybillTemp.setNumber(payBill.getNumber());
			paybillTemp.setAccountName(payBill.getPayFrom().getName());
			paybillTemp.setDate(payBill.getDate().toString());
			paybillTemp.setCurrency(payBill.getCurrency().getFormalName());
			paybillTemp.setCheckNo(payBill.getCheckNumber());
			String currencySymbol = payBill.getCurrency().getSymbol();
			paybillTemp.setTotal(DataUtils.getAmountAsStringInCurrency(
					payBill.getTotal(), currencySymbol));
			paybillTemp.setPaymentMethod(payBill.getPaymentMethod());
			paybillTemp.setMemo(payBill.getMemo());
			FieldsMetadata headersMetaData = new FieldsMetadata();
			headersMetaData.addFieldAsList("payments.date");
			headersMetaData.addFieldAsList("payments.number");
			headersMetaData.addFieldAsList("payments.amountPaid");
			headersMetaData.addFieldAsList("payments.name");
			report.setFieldsMetadata(headersMetaData);
			List<TransactionPayBill> transactionPayBills = payBill
					.getTransactionPayBill();
			List<UsedTempletes> templetes = new ArrayList<UsedTempletes>();
			for (TransactionPayBill bill : transactionPayBills) {
				UsedTempletes usedTempletes = new UsedTempletes();
				if (bill.getEnterBill() != null) {
					usedTempletes.setNumber(bill.getEnterBill().getNumber());
					usedTempletes.setDate(Utility.getDateInSelectedFormat(bill
							.getEnterBill().getDate()));
					usedTempletes.setAmountPaid(Utility.decimalConversation(
							bill.getEnterBill().getPayments(), currencySymbol));
				}
				if (bill.getJournalEntry() != null) {
					JournalEntry entry = bill.getJournalEntry();
					usedTempletes.setNumber(entry.getNumber());
					usedTempletes.setDate(Utility.getDateInSelectedFormat(entry
							.getDate()));
					usedTempletes.setAmountPaid(Utility.decimalConversation(
							-entry.getTotal(), currencySymbol));
				}

				templetes.add(usedTempletes);
			}

			context.put("bill", paybillTemp);
			context.put("payments", templetes);
			return context;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getRegistrationAddress() {
		String regestrationAddress = "";
		Address reg = company.getRegisteredAddress();

		if (reg != null) {
			regestrationAddress = (reg.getAddress1()
					+ forUnusedAddress(reg.getStreet(), true)
					+ forUnusedAddress(reg.getCity(), true)
					+ forUnusedAddress(reg.getStateOrProvinence(), true)
					+ forUnusedAddress(reg.getZipOrPostalCode(), true) + forUnusedAddress(
					reg.getCountryOrRegion(), true));
		} else {
			regestrationAddress = (company.getTradingName() + "\n " + ((company
					.getRegistrationNumber() != null && !company
					.getRegistrationNumber().equals("")) ? "\n Company Registration No: "
					+ company.getRegistrationNumber()
					: ""));
		}
		String phoneStr = forNullValue(company.getPreferences().getPhone());
		if (phoneStr.trim().length() > 0) {
			regestrationAddress = regestrationAddress + ",\n"
					+ Global.get().messages().phone() + " : " + phoneStr + "\n";
		}
		String website = forNullValue(company.getPreferences().getWebSite());

		if (website.trim().length() > 0) {
			regestrationAddress = regestrationAddress
					+ Global.get().messages().webSite() + " : " + website;
		}

		return regestrationAddress;

	}

	public String forNullValue(String value) {
		return value != null ? value : "";
	}

	public String forUnusedAddress(String add, boolean isFooter) {
		if (isFooter) {
			if (add != null && !add.equals(""))
				return add + ",";
		} else {
			if (add != null && !add.equals(""))
				return add + "\n";
		}
		return "";
	}

	public class PaybillTemplete {

		private String title;
		private String registeredAddress;
		private String date;
		private String currency;
		private String paymentMethod;
		private String checkNo;
		private String total;
		private String number;
		private String memo;
		private String name;
		private String accountName;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getRegisteredAddress() {
			return registeredAddress;
		}

		public void setRegisteredAddress(String registeredAddress) {
			this.registeredAddress = registeredAddress;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getCheckNo() {
			return checkNo;
		}

		public void setCheckNo(String checkNo) {
			this.checkNo = checkNo;
		}

		public String getTotal() {
			return total;
		}

		public void setTotal(String total) {
			this.total = total;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}
	}

	public class UsedTempletes {

		private String date;

		private String number;

		private String amountPaid;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getAmountPaid() {
			return amountPaid;
		}

		public void setAmountPaid(String amountPaid) {
			this.amountPaid = amountPaid;
		}
	}

}
