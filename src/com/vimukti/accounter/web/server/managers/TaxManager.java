package com.vimukti.accounter.web.server.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Session;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.AccounterServerConstants;
import com.vimukti.accounter.core.Box;
import com.vimukti.accounter.core.ClientConvertUtil;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.core.FiscalYear;
import com.vimukti.accounter.core.Payee;
import com.vimukti.accounter.core.PaymentTerms;
import com.vimukti.accounter.core.ReceiveVATEntries;
import com.vimukti.accounter.core.TAXAdjustment;
import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.core.TAXGroup;
import com.vimukti.accounter.core.TAXItem;
import com.vimukti.accounter.core.TAXItemGroup;
import com.vimukti.accounter.core.TAXRateCalculation;
import com.vimukti.accounter.core.TAXReturn;
import com.vimukti.accounter.core.TAXReturnEntry;
import com.vimukti.accounter.core.Transaction;
import com.vimukti.accounter.core.VATReturnBox;
import com.vimukti.accounter.services.DAOException;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientBox;
import com.vimukti.accounter.web.client.core.ClientTAXReturn;
import com.vimukti.accounter.web.client.core.ClientTAXReturnEntry;
import com.vimukti.accounter.web.client.core.ClientTransactionPayTAX;
import com.vimukti.accounter.web.client.core.reports.VATSummary;
import com.vimukti.accounter.web.client.exception.AccounterException;

public class TaxManager extends Manager {
	public void createVATItemsOfIreland(Company company, Session session,
			TAXAgency collectorGeneral) throws DAOException {

		createVATItem(
				company,
				session,
				true,
				"EC Purchases For Resale (PFR) @ 21%",
				"EC PFR Standard",
				true,
				false,
				collectorGeneral,
				-21,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS,
						company));

		createVATItem(
				company,
				session,
				true,
				"EC Purchases For Resale (PFR) @ 0%",
				"EC PFR Zero-Rated",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS,
						company));

		createVATItem(
				company,
				session,
				true,
				"EC Purchases Not For Resale (PNFR) @ 21%",
				"EC PNFR Standard",
				true,
				false,
				collectorGeneral,
				-21,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS,
						company));

		createVATItem(
				company,
				session,
				true,
				"EC Purchases Not For Resale (PNFR) @ 0%",
				"EC PNFR Zero-Rated",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS,
						company));

		createVATItem(
				company,
				session,
				true,
				"EC Sales Of Goods Standard",
				"EC Sales Goods Std",
				true,
				true,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EC_SALES_GOODS,
						company));

		createVATItem(
				company,
				session,
				true,
				"Exempt Purchases",
				"EC Purchases (IR)",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EXEMPT_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Exempt Sales",
				"Exempt Sales (IR)",
				true,
				true,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EXEMPT_SALES, company));

		createVATItem(
				company,
				session,
				true,
				"Not Registered Purchases",
				"Not Registered Purchases (IR)",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_EXEMPT_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Not Registered Sales",
				"Not Registered Sales (IR)",
				true,
				true,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_NOT_REGISTERED_SALES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Purchases For Resale (PFR) @ 13.5%",
				"Reduced PFR",
				true,
				false,
				collectorGeneral,
				13.5,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Sales @ 13.5%",
				"Reduced Sales (IR)",
				true,
				true,
				collectorGeneral,
				13.5,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_SALES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Purchases For Resale (PFR) @ 21%",
				"Standard PFR",
				true,
				false,
				collectorGeneral,
				21,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Purchases Not For Resale (PNFR) @ 21%",
				"Standard PNFR",
				true,
				false,
				collectorGeneral,
				21,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Sales @ 21%",
				"Standard Sales (IR)",
				true,
				true,
				collectorGeneral,
				21,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_SALES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Purchases For Resale (PFR) @ 0%",
				"Zero-Rated PFR",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Purchases Not For Resale (PNFR) @ 0%",
				"Zero-Rated PNFR",
				true,
				false,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES,
						company));

		createVATItem(
				company,
				session,
				true,
				"Sales @ 0%",
				"Zero-Rated Sales (IR)",
				true,
				true,
				collectorGeneral,
				0,
				(VATReturnBox) getServerObjectByName(
						AccounterCoreType.VATRETURNBOX,
						AccounterServerConstants.IRELAND_DOMESTIC_SALES,
						company));

	}

	public boolean isTaxAgencyAccount(long account) throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery(
					"getTaxAgency.by.liabilityAccountId").setParameter(0,
					account);
			List list = query.list();

			if (list != null) {
				if (list.size() > 0) {
					return true;
				} else
					return false;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	private void createVATItem(Company company, Session session,
			boolean isActive, String description, String name,
			boolean isPercentage, boolean isSalesType, TAXAgency vatAgency,
			double rate, VATReturnBox vatReturnBox) {

		TAXItem vi = new TAXItem(company);
		vi.setActive(isActive);
		vi.setDescription(description);
		vi.setName(name);
		vi.setPercentage(isPercentage);
		vi.setTaxAgency(vatAgency);
		vi.setTaxRate(rate);
		vi.setVatReturnBox(vatReturnBox);
		session.save(vi);

	}

	private void createVATGroupsOfIreland(Session session, Company company)
			throws DAOException {

		createVATGroup(company, session, "EC PFR Standard Group",
				"EC Purchases For Resale (PFR) Standard Group", true, false,
				"Standard PFR", "EC PFR Standard");

		createVATGroup(company, session, "EC PFR Zero-Rated Group",
				"EC Purchases For Resale (PFR) Zero-Rated Group", true, false,
				"Zero-Rated PFR", "EC PFR Zero-Rated");

		createVATGroup(company, session, "EC PNFR Standard Group",
				"EC Purchases Not For Resale (PNFR) Standard Group", true,
				false, "Standard PNFR", "EC PNFR Standard");

		createVATGroup(company, session, "EC PNFR Zero-Rated Group",
				"EC Purchases Not For Resale (PNFR) Zero-Rated Group", true,
				false, "Zero-Rated PNFR", "EC PNFR Zero-Rated");
	}

	private void createVATGroup(Company company, Session session,
			String groupName, String description, boolean isActive,
			boolean isSalesType, String... vatItems) throws DAOException {

		TAXGroup vg = new TAXGroup(company);
		vg.setActive(isActive);
		vg.setDefault(true);
		vg.setDescription(description);
		vg.setName(groupName);
		vg.setPercentage(true);
		List<TAXItem> vats = new ArrayList<TAXItem>();
		double groupRate = 0;
		for (String s : vatItems) {
			TAXItem v = (TAXItem) getServerObjectByName(
					AccounterCoreType.TAXITEM, s, company);
			vats.add(v);
			groupRate += v.getTaxRate();
		}
		vg.setGroupRate(groupRate);
		vg.setTAXItems(vats);

	}

	private void createVATCodesOfIreland(Session session, Company company)
			throws DAOException {
		createVATcodes(session, company, "ECP", "EC Purch Not Resale (PNFR)",
				true, true, "EC PNFR Standard Group");

		createVATcodes(session, company, "ECS", "EC Sale / Purch Resale (PFR)",
				true, true, "EC PFR Standard Group", "EC Sales Goods Std");

		createVATcodes(session, company, "EIR", "Exempt", true, true,
				"Exempt Purchases (IR)", "Exempt Sales (IR)");

		createVATcodes(session, company, "NIR", "Not Registered", true, true,
				"Not Registered Purchases (IR)", "Not Registered Sales (IR)");

		createVATcodes(session, company, "RIR",
				"Sale / Purch Resale (PFR@13.5%)", true, true, "Reduced PFR",
				"Reduced Sales (IR)");

		createVATcodes(session, company, "RNR",
				"Purch Not Resale (PNFR@13.5%)", true, true, "Reduced PNFR");

		createVATcodes(session, company, "SIR",
				"Sale / Purch Resale (PFR@21%)", true, true, "Standard PFR",
				"Standard Sales (IR)");

		createVATcodes(session, company, "SNR", "Purch Not Resale (PNFR@21%)",
				true, true, "Standard PNFR");

		createVATcodes(session, company, "ZIR", "Sale / Purch Resale (PFR@0%)",
				true, true, "Zero-Rated PFR", "Zero-Rated Sales (IR)");

		createVATcodes(session, company, "ZNR", "Purch Not Resale (PNFR@0%)",
				true, true, "Zero-Rated PNFR");

	}

	private void createVATcodes(Session session, Company company,
			String codeName, String description, boolean isActive,
			boolean isTaxable, String... vatItems) throws DAOException {

		TAXCode vc = new TAXCode(company);
		vc.setActive(isActive);
		vc.setDefault(true);
		vc.setDescription(description);
		vc.setName(codeName);
		vc.setTaxable(isTaxable);
		if (vatItems.length > 0)
			vc.setTAXItemGrpForPurchases((TAXItemGroup) getServerObjectByName(
					AccounterCoreType.VATRETURNBOX, vatItems[0], company));
		if (vatItems.length > 1)
			vc.setTAXItemGrpForSales((TAXItemGroup) getServerObjectByName(
					AccounterCoreType.VATRETURNBOX, vatItems[1], company));

	}

	public ClientTAXReturn getVATReturnDetails(TAXAgency taxAgency,
			FinanceDate fromDate, FinanceDate toDate, long companyId)
			throws DAOException, AccounterException {

		Company company = getCompany(companyId);

		TAXReturn taxReturn = new TAXReturn();

		// List<Box> boxes = createBoxes(vatAgency);

		// assignAmounts(vatAgency, boxes, fromDate, toDate, company);
		// adjustAmounts(vatAgency, boxes, fromDate, toDate, company);

		// for (Box b : boxes) {
		// if (b.getName().equals(
		// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS))
		// b.setName("Box 2 "
		// + AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS);
		// }
		taxReturn.setTaxAgency(taxAgency);
		taxReturn.setPeriodStartDate(fromDate);
		taxReturn.setPeriodEndDate(toDate);

		ClientTAXReturn clientObject = new ClientConvertUtil().toClientObject(
				taxReturn, ClientTAXReturn.class);

		List<ClientTAXReturnEntry> taxReturnEntries = getTAXReturnEntries(
				companyId, taxAgency.getID(), fromDate.getDate(),
				toDate.getDate());

		List<ClientBox> boxes = toBoxes(taxReturnEntries, taxAgency);

		clientObject.setTaxReturnEntries(taxReturnEntries);
		clientObject.setBoxes(boxes);
		return clientObject;

	}

	private void adjustAmounts(TAXAgency vatAgency, List<Box> boxes,
			FinanceDate fromDate, FinanceDate toDate, Company company) {

		Session session = HibernateUtil.getCurrentSession();

		// Company company = getCompany(companyId);
		Query query = session
				.getNamedQuery("getTAXAdjustments.by.taxAgencyIdand.Date")
				.setParameter("fromDate", fromDate)
				.setEntity("company", company).setParameter("toDate", toDate)
				.setParameter("vatAgency", vatAgency.getID());

		List<TAXAdjustment> vas = query.list();

		if (vas != null) {
			for (TAXAdjustment v : vas) {
				VATReturnBox vb = v.getTaxItem().getVatReturnBox();
				for (Box b : boxes) {
					if (vb.getVatBox().equals(b.getName())
							|| (b.getName()
									.equals(AccounterServerConstants.UK_BOX10_UNCATEGORISED) && vb
									.getVatBox().equals("NONE"))) {
						// if (v.getIncreaseVATLine())
						// b.setAmount(b.getAmount() + v.getTotal());
						// else
						// b.setAmount(b.getAmount() - v.getTotal());
						// }
						//
						// else if
						// (b.getName().equals(AccounterConstants.UK_BOX10_UNCATEGORISED))
						// {

						if (v.isSales()) {
							if ((!v.getTaxItem()
									.getName()
									.equals(AccounterServerConstants.VAT_ITEM_EC_SALES_GOODS_ZERO_RATED))
									&& (!v.getTaxItem()
											.getName()
											.equals(AccounterServerConstants.VAT_ITEM_EC_SALES_SERVICES_ZERO_RATED))) {
								if (v.getIncreaseVATLine())
									b.setAmount(b.getAmount() + v.getTotal());
								else
									b.setAmount(b.getAmount() - v.getTotal());

							} else {

								if (v.getIncreaseVATLine())
									b.setAmount(b.getAmount() + v.getTotal());
								else
									b.setAmount(b.getAmount() - v.getTotal());
							}
						} else {
							double amount = 0;
							if (v.getIncreaseVATLine())
								amount = v.getTotal();
							else
								amount = -1 * v.getTotal();

							if (vb.getVatBox()
									.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)
									|| vb.getVatBox()
											.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES))
								amount = -1 * amount;

							b.setAmount(b.getAmount() + amount);
						}
					}

				}
			}

			// Reducing File Vat Uncategorised Tax Amount by the existed Filed
			// Vat Uncategorised Tax Amount
			// for (Box b : boxes)
			// if (b.getBoxNumber() == 10 && b.getAmount() != 0)
			// b.setAmount(b.getAmount() - totalFiledVatAmount);
		}

	}

	public ArrayList<ReceiveVATEntries> getReceiveVATEntries(long companyId) {

		List<ReceiveVATEntries> receiveVATEntries = new Vector<ReceiveVATEntries>();
		Company company = getCompany(companyId);
		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery(
				"getVATReturn.by.check.BalancelessThanzero").setEntity(
				"company", company);

		List<TAXReturn> vatReturns = query.list();

		for (TAXReturn v : vatReturns) {

			v.setBalance(-1 * v.getBalance());

			receiveVATEntries.add(new ReceiveVATEntries(v));
		}

		return new ArrayList<ReceiveVATEntries>(receiveVATEntries);

	}

	private void assignAmounts(TAXAgency vatAgency, List<Box> boxes,
			FinanceDate fromDate, FinanceDate toDate, Company company) {

		Session session = HibernateUtil.getCurrentSession();
		// Company company = getCompany(companyId);
		Query query = session
				.getNamedQuery("getTAXRateCalculations.by.taxAgencyIdand.Date")
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
				.setParameter("vatAgency", vatAgency.getID())
				.setEntity("company", company);
		List<TAXRateCalculation> vrc = query.list();

		/*
		 * Here all box values other than Vat code EGS (for purchase) of box2
		 * and Vat code RC (for purchase) of box1 are getting it's native values
		 */

		for (int i = 0; i < vrc.size(); i++) {

			if (vrc.get(i).getTaxItem().getTaxAgency().getName()
					.equals(vatAgency.getName())) {
				TAXRateCalculation taxRateCalculation = vrc.get(i);

				VATReturnBox vb = taxRateCalculation.getTaxItem()
						.getVatReturnBox();
				// int type = vatRateCalculation.getTransactionItem()
				// .getTransaction().getType();
				// double rcAmount = 0;
				// if (vb.getVatBox().equals(
				// AccounterConstants.UK_BOX1_VAT_DUE_ON_SALES)
				// && (vb.getTotalBox()
				// .equals(AccounterConstants.BOX_NONE))) {
				// rcAmount += -1 * (vatRateCalculation.getVatAmount());
				// }

				for (int j = 0; j < boxes.size(); j++) {

					Box box = boxes.get(j);
					if ((box).getName().equals(vb.getTotalBox())) {
						box.setAmount(box.getAmount()
								+ taxRateCalculation.getLineTotal());

					} else if ((box).getName().equals(vb.getVatBox())) {
						if (vb.getVatBox()
								.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)
								|| (vb.getVatBox()
										.equals(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES) && !taxRateCalculation
										.isVATGroupEntry())
								|| (vb.getVatBox()
										.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES) && vb
										.getTotalBox()
										.equals(AccounterServerConstants.BOX_NONE)))
							box.setAmount(box.getAmount()
									+ (-1 * (taxRateCalculation.getVatAmount())));

						else
							box.setAmount(box.getAmount()
									+ (taxRateCalculation.getVatAmount()));

					}
					// if (box.getBoxNumber() == 4) {
					// box.setAmount(box.getAmount() + rcAmount);
					// rcAmount = 0;
					// }
					box.getTaxRateCalculations().add(taxRateCalculation);
				}
			}
		}
	}

	public Map<String, Double> getVATReturnBoxes(FinanceDate startDate,
			FinanceDate endDate, long companyId) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		List<FiscalYear> fiscalYears = (List<FiscalYear>) session
				.getNamedQuery("getFisacalyear").setEntity("company", company);
		FinanceDate actualStartDate = new FinanceDate();
		for (FiscalYear fs : fiscalYears) {
			if (fs.getIsCurrentFiscalYear() == Boolean.TRUE) {

				actualStartDate = fs.getStartDate();
				break;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(actualStartDate.getAsDateObject());
			// startDate = cal.get(Calendar.YEAR) + "-"
			// + (cal.get(Calendar.MONTH) + 1) + "-"
			// + cal.get(Calendar.DAY_OF_MONTH);
			startDate = new FinanceDate(cal.getTime());
			Query query = session
					.getNamedQuery("get_BOX1_VATdueOnSalesAndOtherOutputs")
					.setParameter("companyId", companyId).setParameter(

					"startDate", startDate).setParameter("endDate", endDate);

			List l = query.list();

			Map<String, Double> vatReturnBoxes = new HashMap<String, Double>();
			if (l.size() > 0) {
				vatReturnBoxes.put(
						AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES,
						(Double) l.get(0));
			}
		}
		return null;

	}

	public ArrayList<VATSummary> getPriorReturnVATSummary(TAXAgency taxAgency,
			FinanceDate endDate, long companyId) throws DAOException,
			ParseException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Query query = session
				.getNamedQuery("getVATReturn.by.taxagencyandenddate")
				.setParameter("taxAgency", taxAgency.getID())
				.setParameter("endDate", endDate).setEntity("company", company);

		TAXReturn vatReturn = (TAXReturn) query.uniqueResult();

		if (vatReturn == null) {
			throw new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					new NullPointerException(
							"No VAT Return found in database with VATAgency '"
									+ taxAgency.getName() + "' and End date :"
									+ endDate));
		}

		List<VATSummary> vatSummaries = new ArrayList<VATSummary>();

		vatSummaries.add(new VATSummary(VATSummary.UK_BOX1_VAT_DUE_ON_SALES,
				vatReturn.getBoxes().get(0).getAmount()));
		vatSummaries.add(new VATSummary(
				VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS, vatReturn
						.getBoxes().get(1).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX3_TOTAL_OUTPUT,
				vatReturn.getBoxes().get(2).getAmount()));
		vatSummaries.add(new VATSummary(
				VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES, vatReturn
						.getBoxes().get(3).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX5_NET_VAT, vatReturn
				.getBoxes().get(4).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX6_TOTAL_NET_SALES,
				vatReturn.getBoxes().get(5).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX7_TOTAL_NET_PURCHASES,
				vatReturn.getBoxes().get(6).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES,
				vatReturn.getBoxes().get(7).getAmount()));
		vatSummaries.add(new VATSummary(
				VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS, vatReturn.getBoxes()
						.get(8).getAmount()));
		vatSummaries.add(new VATSummary(VATSummary.UK_BOX10_UNCATEGORISED,
				vatReturn.getBoxes().get(9).getAmount()));

		return new ArrayList<VATSummary>(vatSummaries);
	}

	public List<ClientTransactionPayTAX> getPayTAXEntries(long companyId) {

		List<ClientTransactionPayTAX> payTAXEntries = new ArrayList<ClientTransactionPayTAX>();
		Company company = getCompany(companyId);

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery(
				"getVATReturn.by.check.BalanceGraterThanzero").setEntity(
				"company", company);

		List<TAXReturn> taxReturns = query.list();

		for (TAXReturn taxReturn : taxReturns) {
			ClientTransactionPayTAX cpt = new ClientTransactionPayTAX();
			cpt.setTaxAgency(taxReturn.getTaxAgency().getID());
			cpt.setTAXReturn(taxReturn.getID());
			cpt.setFiledDate(taxReturn.getDate().toClientFinanceDate());
			cpt.setTaxDue(taxReturn.getBalance());
			payTAXEntries.add(cpt);
		}

		return payTAXEntries;

	}

	public ArrayList<Account> getTaxAgencyAccounts(long companyId)
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Company company = getCompany(companyId);
			Query query = session.getNamedQuery("getAccount")
					.setParameter("type1", Account.TYPE_INCOME)
					.setParameter("type2", Account.TYPE_EXPENSE)
					.setParameter("type3", Account.TYPE_COST_OF_GOODS_SOLD)
					.setEntity("company", company);
			List<Account> list = query.list();

			if (list != null) {
				return new ArrayList<Account>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	// public ArrayList<FileTAXEntry> getTransactionPaySalesTaxEntriesList(
	// long billsDueOnOrBefore, long companyId) throws DAOException {
	//
	// Session session = HibernateUtil.getCurrentSession();
	// Company company = getCompany(companyId);
	// Query query = session
	// .getNamedQuery(
	// "getTAXRateCalculation.checkingby.salesLiabilityAccountName.taxDue")
	// .setEntity("company", company);
	//
	// List<TAXRateCalculation> list = query.list();
	// List<FileTAXEntry> resultPaySalesTaxEntries = new
	// ArrayList<FileTAXEntry>();
	// FinanceDate financeDate = new FinanceDate(billsDueOnOrBefore);
	//
	// if (list != null) {
	// long previousTaxItem = 0;
	// long previousTaxAgency = 0;
	// FileTAXEntry paySalesTaxList = new FileTAXEntry();
	// for (TAXRateCalculation taxRateCalculation : list) {
	// long currentTaxAgency = taxRateCalculation.getTaxAgency()
	// .getID();
	// long currentTaxItem = taxRateCalculation.getTaxItem().getID();
	// if (previousTaxAgency != 0
	// && previousTaxItem != 0
	// && (previousTaxAgency != currentTaxAgency || previousTaxItem !=
	// currentTaxItem)) {
	// if (paySalesTaxList.getTaxAgency() != null
	// && paySalesTaxList.getBalance() != 0) {
	// resultPaySalesTaxEntries.add(paySalesTaxList);
	// }
	// paySalesTaxList = new FileTAXEntry();
	// }
	// if (canAddTaxRateCalculationToPaySalesTax(
	// taxRateCalculation.getTaxAgency(), financeDate,
	// taxRateCalculation.getTransactionDate())) {
	// paySalesTaxList.setBalance(paySalesTaxList.getBalance()
	// + taxRateCalculation.getTaxDue());
	// paySalesTaxList.setAmount(paySalesTaxList.getAmount()
	// + taxRateCalculation.getVatAmount());
	// paySalesTaxList.setTaxItem(taxRateCalculation.getTaxItem());
	// paySalesTaxList.setTaxAgency(taxRateCalculation
	// .getTaxAgency());
	// paySalesTaxList.setTaxRateCalculation(taxRateCalculation);
	// paySalesTaxList.setTransactionDate(taxRateCalculation
	// .getTransactionDate());
	// }
	// previousTaxAgency = currentTaxAgency;
	// previousTaxItem = currentTaxItem;
	// }
	// if (paySalesTaxList.getTaxAgency() != null
	// && paySalesTaxList.getBalance() != 0) {
	// resultPaySalesTaxEntries.add(paySalesTaxList);
	// }
	// }
	//
	// query = session
	// .getNamedQuery(
	// "getTAXAdjustment.checkingby.salesLiabilityAccount.nameandbalanceDue")
	// .setEntity("company", company);
	//
	// List<TAXAdjustment> taxAdjustments = query.list();
	// if (list != null) {
	//
	// for (TAXAdjustment taxAdjustment : taxAdjustments) {
	//
	// FileTAXEntry paySalesTaxList = new FileTAXEntry();
	// paySalesTaxList.setBalance(taxAdjustment.getBalanceDue());
	// paySalesTaxList.setAmount(taxAdjustment.getTotal());
	// paySalesTaxList.setTaxAgency(taxAdjustment.getTaxAgency());
	// paySalesTaxList.setTransaction(taxAdjustment);
	// paySalesTaxList.setTransactionDate(taxAdjustment.getDate());
	// paySalesTaxList.setTaxAdjustment(taxAdjustment);
	//
	// preParePaySalesTaxEntriesUsingPaymentTerms(
	// resultPaySalesTaxEntries, paySalesTaxList, financeDate);
	// }
	// }
	//
	// // List<PaySalesTaxEntries> paySalesTaxEntries = query.list();
	// // FinanceDate financeDate = new FinanceDate(billsDueOnOrBefore);
	// //
	// // List<PaySalesTaxEntries> resultPaySalesTaxEntries = new
	// // ArrayList<PaySalesTaxEntries>();
	// // for (PaySalesTaxEntries pst : paySalesTaxEntries) {
	// //
	// // preParePaySalesTaxEntriesUsingPaymentTerms(
	// // resultPaySalesTaxEntries, pst, financeDate, pst
	// // .getTaxAgency());
	// // }
	//
	// return new ArrayList<FileTAXEntry>(resultPaySalesTaxEntries);
	// }

	private boolean canAddTaxRateCalculationToPaySalesTax(TAXAgency taxAgency,
			FinanceDate dueDate, FinanceDate transactionDate) {
		Calendar dueCalendar = Calendar.getInstance();
		dueCalendar.setTime(dueDate.getAsDateObject());
		PaymentTerms paymentTerm = taxAgency.getPaymentTerm();
		if (paymentTerm != null && paymentTerm.getDue() == 0) {
			dueCalendar.add(Calendar.DAY_OF_MONTH, paymentTerm.getDueDays());
			if (new FinanceDate(dueCalendar.getTime())
					.compareTo(transactionDate) >= 0) {
				return true;
			}
		} else {

			Calendar transCal = Calendar.getInstance();
			transCal.setTime(transactionDate.getAsDateObject());

			Calendar payTermCal = Calendar.getInstance();
			payTermCal.setTime(new FinanceDate().getAsDateObject());
			if (paymentTerm != null) {
				switch (paymentTerm.getDue()) {
				case PaymentTerms.DUE_CURRENT_MONTH:
					payTermCal.set(Calendar.MONTH,
							payTermCal.get(Calendar.MONTH) - 1);
					if (transCal.getTime().compareTo(payTermCal.getTime()) <= 0
							&& transCal.getTime().compareTo(
									dueCalendar.getTime()) <= 0) {
						return true;
					}
					break;
				case PaymentTerms.DUE_CURRENT_SIXTY:
					payTermCal.set(Calendar.MONTH,
							payTermCal.get(Calendar.MONTH) - 2);
					if (transCal.getTime().compareTo(payTermCal.getTime()) <= 0
							&& transCal.getTime().compareTo(
									dueCalendar.getTime()) <= 0) {
						return true;
					}
					break;
				case PaymentTerms.DUE_CURRENT_QUARTER:
					payTermCal.set(Calendar.MONTH,
							payTermCal.get(Calendar.MONTH) - 3);
					if (transCal.getTime().compareTo(payTermCal.getTime()) <= 0
							&& transCal.getTime().compareTo(
									dueCalendar.getTime()) <= 0) {
						return true;
					}
					break;
				}
			}
		}
		return false;
	}

	// private void preParePaySalesTaxEntriesUsingPaymentTerms(
	// List<FileTAXEntry> resultPaySalesTaxEntries, FileTAXEntry pst,
	// FinanceDate financeDate) {
	//
	// Calendar dueCalendar = Calendar.getInstance();
	// dueCalendar.setTime(financeDate.getAsDateObject());
	// PaymentTerms paymentTerm = pst.getTaxAgency().getPaymentTerm();
	// if (paymentTerm != null && paymentTerm.getDue() == 0) {
	// dueCalendar.add(Calendar.DAY_OF_MONTH, paymentTerm.getDueDays());
	// if (new FinanceDate(dueCalendar.getTime()).compareTo(pst
	// .getTransactionDate()) >= 0) {
	// resultPaySalesTaxEntries.add(pst);
	// }
	// } else {
	//
	// Calendar transCal = Calendar.getInstance();
	// transCal.setTime(new FinanceDate().getAsDateObject());
	// // transCal.set(Calendar.DAY_OF_MONTH, 01);
	// if (paymentTerm != null) {
	// switch (paymentTerm.getDue()) {
	// case PaymentTerms.DUE_CURRENT_MONTH:
	// transCal.set(Calendar.MONTH,
	// transCal.get(Calendar.MONTH) - 1);
	// verifyCalendarDates(transCal, dueCalendar,
	// resultPaySalesTaxEntries, pst);
	// break;
	// case PaymentTerms.DUE_CURRENT_SIXTY:
	// transCal.set(Calendar.MONTH,
	// transCal.get(Calendar.MONTH) - 2);
	// verifyCalendarDates(transCal, dueCalendar,
	// resultPaySalesTaxEntries, pst);
	// break;
	// case PaymentTerms.DUE_CURRENT_QUARTER:
	// // verifyQuarterRange(transCal);
	// transCal.set(Calendar.MONTH,
	// transCal.get(Calendar.MONTH) - 3);
	// verifyCalendarDates(transCal, dueCalendar,
	// resultPaySalesTaxEntries, pst);
	// break;
	// case PaymentTerms.DUE_CURRENT_HALF_YEAR:
	// // int month = transCal.get(Calendar.MONTH);
	// // month++;
	// // if (month <= 6) {
	// // transCal.set(Calendar.MONTH, 6);
	// // } else {
	// // transCal.set(Calendar.MONTH, 12);
	// // }
	// transCal.set(Calendar.MONTH,
	// transCal.get(Calendar.MONTH) - 6);
	// verifyCalendarDates(transCal, dueCalendar,
	// resultPaySalesTaxEntries, pst);
	// break;
	// case PaymentTerms.DUE_CURRENT_YEAR:
	// // transCal.set(Calendar.MONTH, 12);
	// transCal.set(Calendar.MONTH,
	// transCal.get(Calendar.MONTH) - 12);
	// verifyCalendarDates(transCal, dueCalendar,
	// resultPaySalesTaxEntries, pst);
	// break;
	// }
	// }
	// }
	//
	// // return resultPaySalesTaxEntries;
	// }

	private void verifyQuarterRange(Calendar transCal) {
		int month = transCal.get(Calendar.MONTH);
		month++;
		if (month == 1 || month == 2 || month == 3) {
			transCal.set(Calendar.MONTH, 3);
		} else if (month == 4 || month == 5 || month == 6) {
			transCal.set(Calendar.MONTH, 6);

		} else if (month == 7 || month == 8 || month == 9) {
			transCal.set(Calendar.MONTH, 9);
		} else {
			transCal.set(Calendar.MONTH, 12);
		}
	}

	// private void verifyCalendarDates(Calendar payTermCal, Calendar
	// dueCalendar,
	// List<FileTAXEntry> resultPaySalesTaxEntries,
	// FileTAXEntry paySalesTaxEntries) {
	// Calendar transCal = Calendar.getInstance();
	// transCal.setTime(paySalesTaxEntries.getTransactionDate()
	// .getAsDateObject());
	// if (transCal.getTime().compareTo(payTermCal.getTime()) <= 0
	// && transCal.getTime().compareTo(dueCalendar.getTime()) <= 0) {
	// resultPaySalesTaxEntries.add(paySalesTaxEntries);
	// }
	// }

	private TAXAgency createVATAgency(Session session,
			Account vatLiabilityAccount) {
		TAXAgency collectorGeneral = new TAXAgency();
		collectorGeneral.setName("Collector-General");
		collectorGeneral.setActive(true);
		collectorGeneral.setBalance(0.0);
		collectorGeneral.setDate(new FinanceDate());
		collectorGeneral.setPayeeSince(new FinanceDate());
		collectorGeneral.setPurchaseLiabilityAccount(vatLiabilityAccount);
		collectorGeneral.setSalesLiabilityAccount(vatLiabilityAccount);
		collectorGeneral.setType(Payee.TYPE_TAX_AGENCY);
		collectorGeneral.setVATReturn(TAXAgency.RETURN_TYPE_IRELAND_VAT);
		session.save(collectorGeneral);
		return collectorGeneral;
	}

	public long getLastTaxReturnEndDate(long agencyId, long companyId) {
		Session session = HibernateUtil.getCurrentSession();
		Company company = (Company) session.get(Company.class, companyId);
		Object uniqueResult = session
				.getNamedQuery("get.last.taxReturn.endDate")
				.setParameter("company", company)
				.setParameter("taxAgency", agencyId).uniqueResult();
		if (uniqueResult != null) {
			return ((FinanceDate) uniqueResult).getDate();
		}

		return 0;
	}

	public List<ClientTAXReturnEntry> getTAXReturnEntries(long companyId,
			long taxAgency, long startDate, long endDate)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session
				.getNamedQuery("getTAXRateCalculation.for.TaxReturn")
				.setParameter("companyId", companyId)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.setParameter("taxAgency", taxAgency);

		List<Object[]> list = query.list();
		List<ClientTAXReturnEntry> resultTAXReturnEntries = new ArrayList<ClientTAXReturnEntry>();

		if (list != null) {
			for (Object[] objects : list) {
				ClientTAXReturnEntry taxReturnEntry = new ClientTAXReturnEntry();
				taxReturnEntry.setTaxAmount((Double) objects[0]);
				taxReturnEntry.setNetAmount((Double) objects[1]);
				taxReturnEntry.setTransaction((Long) objects[2]);
				taxReturnEntry.setTransactionType((Integer) objects[3]);
				taxReturnEntry.setTransactionDate((Long) objects[4]);
				taxReturnEntry.setGrassAmount(taxReturnEntry.getNetAmount()
						+ taxReturnEntry.getTaxAmount());

				taxReturnEntry.setTaxItem((Long) objects[5]);
				taxReturnEntry.setTaxAgency((Long) objects[6]);

				taxReturnEntry.setTAXGroupEntry((Boolean) objects[7]);
				resultTAXReturnEntries.add(taxReturnEntry);
			}
		}

		query = session
				.getNamedQuery("getTAXAdjustments.by.taxAgencyIdand.Date")
				.setParameter("fromDate", new FinanceDate(startDate))
				.setEntity("company", company)
				.setParameter("toDate", new FinanceDate(endDate))
				.setParameter("vatAgency", taxAgency);

		List<TAXAdjustment> taxAdjustments = query.list();
		if (list != null) {
			for (TAXAdjustment taxAdjustment : taxAdjustments) {
				ClientTAXReturnEntry taxReturnEntry = new ClientTAXReturnEntry();
				taxReturnEntry.setTaxAmount(taxAdjustment.getTotal());
				taxReturnEntry.setTaxItem(taxAdjustment.getTaxItem().getID());
				taxReturnEntry.setTaxAgency(taxAdjustment.getTaxAgency()
						.getID());
				taxReturnEntry.setTransaction(taxAdjustment.getID());
				taxReturnEntry.setTransactionDate(taxAdjustment.getDate()
						.getDate());
				taxReturnEntry
						.setTransactionType(Transaction.TYPE_ADJUST_SALES_TAX);
				resultTAXReturnEntries.add(taxReturnEntry);
			}
		}

		resultTAXReturnEntries.addAll(getTAXReturnExceptions(companyId,
				taxAgency, startDate, endDate));

		return resultTAXReturnEntries;
	}

	protected List<ClientTAXReturnEntry> getTAXReturnExceptions(long companyId,
			long taxAgency, long startDate, long endDate) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getTAXRateCalculation.for.TaxReturn.Exception")
				.setParameter("companyId", companyId)
				.setParameter("taxAgency", taxAgency);

		List<Object[]> taxReturnEntries = session
				.getNamedQuery("getAllTAXReturnEntries.groupby.transaction.id")
				.setParameter("companyId", companyId)
				.setParameter("taxAgency", taxAgency).list();

		List<Object[]> list = query.list();

		List<ClientTAXReturnEntry> resultTAXReturnEntries = new ArrayList<ClientTAXReturnEntry>();

		Iterator<Object[]> iterator = list.iterator();
		for (Object[] entry : taxReturnEntries) {
			Long transaction = (Long) entry[0];
			ClientTAXReturnEntry newEntry = null;
			while (iterator.hasNext()) {
				Object[] objects = iterator.next();
				double taxAmount = (Double) objects[0];
				double netAmount = (Double) objects[1];
				long transactionID = (Long) objects[2];

				if (transactionID == transaction) {
					newEntry = new ClientTAXReturnEntry();
					newEntry.setFiledTAXAmount((Double) entry[4]);
					newEntry.setTaxAmount(taxAmount - (Double) entry[4]);
					newEntry.setNetAmount(netAmount - (Double) entry[5]);
					newEntry.setGrassAmount(newEntry.getTaxAmount()
							+ newEntry.getNetAmount());
					newEntry.setTransaction(transactionID);
					newEntry.setTransactionType((Integer) objects[3]);
					newEntry.setTransactionDate((Long) objects[4]);
					newEntry.setTaxItem((Long) objects[5]);
					newEntry.setTaxAgency(taxAgency);
					newEntry.setTAXGroupEntry(false);
					resultTAXReturnEntries.add(newEntry);
					iterator.remove();
				}
			}
			if (newEntry == null && (Boolean) entry[3]
					&& (Double) entry[4] != 0) {
				newEntry = new ClientTAXReturnEntry();
				newEntry.setFiledTAXAmount((Double) entry[4]);
				newEntry.setTaxAmount(-(Double) entry[4]);
				newEntry.setNetAmount(-(Double) entry[5]);
				newEntry.setGrassAmount(newEntry.getTaxAmount()
						+ newEntry.getNetAmount());
				newEntry.setTransaction((Long) entry[0]);
				newEntry.setTransactionType((Integer) entry[1]);
				newEntry.setTransactionDate((Long) entry[2]);
				newEntry.setTaxItem((Long) entry[5]);
				newEntry.setTaxAgency(taxAgency);
				newEntry.setTAXGroupEntry(false);
				resultTAXReturnEntries.add(newEntry);
			}
		}

		for (Object[] objects : list) {
			ClientTAXReturnEntry newEntry = new ClientTAXReturnEntry();
			newEntry.setFiledTAXAmount(0);
			newEntry.setTaxAmount((Double) objects[0]);
			newEntry.setNetAmount((Double) objects[1]);
			newEntry.setGrassAmount(newEntry.getTaxAmount()
					+ newEntry.getNetAmount());
			newEntry.setTransaction((Long) objects[2]);
			newEntry.setTransactionType((Integer) objects[3]);
			newEntry.setTransactionDate((Long) objects[4]);
			newEntry.setTaxItem((Long) objects[5]);
			newEntry.setTaxAgency(taxAgency);
			newEntry.setTAXGroupEntry(false);
			resultTAXReturnEntries.add(newEntry);
		}

		return resultTAXReturnEntries;
	}

	public List<ClientTAXReturn> getAllTAXReturns(Long companyID)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyID);
		List<TAXReturn> list = session.getNamedQuery("list.TAXReturns")
				.setEntity("company", company).list();
		for (TAXReturn taxReturn : list) {
			for (TAXReturnEntry entry : taxReturn.getTaxReturnEntries()) {
				Transaction transaction = entry.getTransaction();
				entry.setTransactionDate(transaction.getDate());
				entry.setTransactionType(transaction.getType());
			}
		}
		List<ClientTAXReturn> taxReturns = new ArrayList<ClientTAXReturn>();
		ClientConvertUtil convertUttils = new ClientConvertUtil();
		for (TAXReturn taxReturn : list) {
			ClientTAXReturn clientObject = convertUttils.toClientObject(
					taxReturn, ClientTAXReturn.class);
			if (company.getCountry().equals("United Kingdom")) {
				clientObject.setBoxes(toBoxes(
						clientObject.getTaxReturnEntries(),
						taxReturn.getTaxAgency()));
			}
			taxReturns.add(clientObject);
		}
		return taxReturns;
	}

}
