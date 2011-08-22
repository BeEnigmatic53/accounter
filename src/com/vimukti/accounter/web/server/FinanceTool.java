/**
 * 
 */
package com.vimukti.accounter.web.server;

import java.io.NotSerializableException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.AccounterServerConstants;
import com.vimukti.accounter.core.Bank;
import com.vimukti.accounter.core.Box;
import com.vimukti.accounter.core.BrandingTheme;
import com.vimukti.accounter.core.CashPurchase;
import com.vimukti.accounter.core.CashSales;
import com.vimukti.accounter.core.ClientConvertUtil;
import com.vimukti.accounter.core.CloneUtil;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.CompanyPreferences;
import com.vimukti.accounter.core.CreatableObject;
import com.vimukti.accounter.core.CreditCardCharge;
import com.vimukti.accounter.core.CreditRating;
import com.vimukti.accounter.core.CreditsAndPayments;
import com.vimukti.accounter.core.Currency;
import com.vimukti.accounter.core.Customer;
import com.vimukti.accounter.core.CustomerGroup;
import com.vimukti.accounter.core.CustomerPrePayment;
import com.vimukti.accounter.core.CustomerRefund;
import com.vimukti.accounter.core.Depreciation;
import com.vimukti.accounter.core.EnterBill;
import com.vimukti.accounter.core.Entry;
import com.vimukti.accounter.core.Estimate;
import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.core.FinanceLogger;
import com.vimukti.accounter.core.FiscalYear;
import com.vimukti.accounter.core.FixedAsset;
import com.vimukti.accounter.core.FixedAssetHistory;
import com.vimukti.accounter.core.IAccounterServerCore;
import com.vimukti.accounter.core.Item;
import com.vimukti.accounter.core.ItemGroup;
import com.vimukti.accounter.core.JournalEntry;
import com.vimukti.accounter.core.MakeDeposit;
import com.vimukti.accounter.core.NumberUtils;
import com.vimukti.accounter.core.ObjectConvertUtil;
import com.vimukti.accounter.core.PayBill;
import com.vimukti.accounter.core.PaySalesTax;
import com.vimukti.accounter.core.PaySalesTaxEntries;
import com.vimukti.accounter.core.PayVAT;
import com.vimukti.accounter.core.PayVATEntries;
import com.vimukti.accounter.core.Payee;
import com.vimukti.accounter.core.PaymentTerms;
import com.vimukti.accounter.core.PriceLevel;
import com.vimukti.accounter.core.ReceivePayment;
import com.vimukti.accounter.core.ReceiveVAT;
import com.vimukti.accounter.core.ReceiveVATEntries;
import com.vimukti.accounter.core.RecurringTransaction;
import com.vimukti.accounter.core.SalesPerson;
import com.vimukti.accounter.core.ServerConvertUtil;
import com.vimukti.accounter.core.ShippingMethod;
import com.vimukti.accounter.core.ShippingTerms;
import com.vimukti.accounter.core.TAXAdjustment;
import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.core.TAXGroup;
import com.vimukti.accounter.core.TAXItem;
import com.vimukti.accounter.core.TAXItemGroup;
import com.vimukti.accounter.core.TAXRateCalculation;
import com.vimukti.accounter.core.TaxRates;
import com.vimukti.accounter.core.Transaction;
import com.vimukti.accounter.core.TransactionMakeDeposit;
import com.vimukti.accounter.core.TransactionMakeDepositEntries;
import com.vimukti.accounter.core.TransferFund;
import com.vimukti.accounter.core.Unit;
import com.vimukti.accounter.core.User;
import com.vimukti.accounter.core.Util;
import com.vimukti.accounter.core.Utility;
import com.vimukti.accounter.core.VATReturn;
import com.vimukti.accounter.core.VATReturnBox;
import com.vimukti.accounter.core.Vendor;
import com.vimukti.accounter.core.VendorGroup;
import com.vimukti.accounter.core.WriteCheck;
import com.vimukti.accounter.core.change.ChangeTracker;
import com.vimukti.accounter.services.DAOException;
import com.vimukti.accounter.services.IFinanceDAOService;
import com.vimukti.accounter.servlets.BaseServlet;
import com.vimukti.accounter.utils.HexUtil;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.utils.Security;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientMakeDeposit;
import com.vimukti.accounter.web.client.core.ClientPayBill;
import com.vimukti.accounter.web.client.core.ClientQuantity;
import com.vimukti.accounter.web.client.core.ClientRecurringTransaction;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionMakeDeposit;
import com.vimukti.accounter.web.client.core.ClientTransferFund;
import com.vimukti.accounter.web.client.core.ClientUnit;
import com.vimukti.accounter.web.client.core.ClientUser;
import com.vimukti.accounter.web.client.core.ClientUserInfo;
import com.vimukti.accounter.web.client.core.HrEmployee;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.VList;
import com.vimukti.accounter.web.client.core.Lists.BillsList;
import com.vimukti.accounter.web.client.core.Lists.CustomerRefundsList;
import com.vimukti.accounter.web.client.core.Lists.DepreciableFixedAssetsEntry;
import com.vimukti.accounter.web.client.core.Lists.DepreciableFixedAssetsList;
import com.vimukti.accounter.web.client.core.Lists.EstimatesAndSalesOrdersList;
import com.vimukti.accounter.web.client.core.Lists.FixedAssetLinkedAccountMap;
import com.vimukti.accounter.web.client.core.Lists.FixedAssetList;
import com.vimukti.accounter.web.client.core.Lists.FixedAssetSellOrDisposeReviewJournal;
import com.vimukti.accounter.web.client.core.Lists.InvoicesList;
import com.vimukti.accounter.web.client.core.Lists.IssuePaymentTransactionsList;
import com.vimukti.accounter.web.client.core.Lists.KeyFinancialIndicators;
import com.vimukti.accounter.web.client.core.Lists.OpenAndClosedOrders;
import com.vimukti.accounter.web.client.core.Lists.OverDueInvoicesList;
import com.vimukti.accounter.web.client.core.Lists.PayBillTransactionList;
import com.vimukti.accounter.web.client.core.Lists.PayeeList;
import com.vimukti.accounter.web.client.core.Lists.PayeeStatementsList;
import com.vimukti.accounter.web.client.core.Lists.PaymentsList;
import com.vimukti.accounter.web.client.core.Lists.PurchaseOrdersAndItemReceiptsList;
import com.vimukti.accounter.web.client.core.Lists.PurchaseOrdersList;
import com.vimukti.accounter.web.client.core.Lists.ReceivePaymentTransactionList;
import com.vimukti.accounter.web.client.core.Lists.ReceivePaymentsList;
import com.vimukti.accounter.web.client.core.Lists.SalesOrdersList;
import com.vimukti.accounter.web.client.core.Lists.SellingOrDisposingFixedAssetList;
import com.vimukti.accounter.web.client.core.Lists.TempFixedAsset;
import com.vimukti.accounter.web.client.core.reports.AccountBalance;
import com.vimukti.accounter.web.client.core.reports.AccountRegister;
import com.vimukti.accounter.web.client.core.reports.AgedDebtors;
import com.vimukti.accounter.web.client.core.reports.AmountsDueToVendor;
import com.vimukti.accounter.web.client.core.reports.DepositDetail;
import com.vimukti.accounter.web.client.core.reports.ECSalesList;
import com.vimukti.accounter.web.client.core.reports.ECSalesListDetail;
import com.vimukti.accounter.web.client.core.reports.ExpenseList;
import com.vimukti.accounter.web.client.core.reports.MostProfitableCustomers;
import com.vimukti.accounter.web.client.core.reports.ReverseChargeList;
import com.vimukti.accounter.web.client.core.reports.ReverseChargeListDetail;
import com.vimukti.accounter.web.client.core.reports.SalesByCustomerDetail;
import com.vimukti.accounter.web.client.core.reports.SalesTaxLiability;
import com.vimukti.accounter.web.client.core.reports.TransactionDetailByAccount;
import com.vimukti.accounter.web.client.core.reports.TransactionDetailByTaxItem;
import com.vimukti.accounter.web.client.core.reports.TransactionHistory;
import com.vimukti.accounter.web.client.core.reports.TrialBalance;
import com.vimukti.accounter.web.client.core.reports.UncategorisedAmountsReport;
import com.vimukti.accounter.web.client.core.reports.VATDetail;
import com.vimukti.accounter.web.client.core.reports.VATDetailReport;
import com.vimukti.accounter.web.client.core.reports.VATItemDetail;
import com.vimukti.accounter.web.client.core.reports.VATItemSummary;
import com.vimukti.accounter.web.client.core.reports.VATSummary;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.GraphChart;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.company.CompanyPreferencesView;
import com.vimukti.accounter.web.client.ui.core.DecimalUtil;
import com.vimukti.accounter.web.client.ui.reports.CheckDetailReport;

/**
 * @author Fernandez
 * 
 */
public class FinanceTool implements IFinanceDAOService {

	Logger log = Logger.getLogger(FinanceTool.class);

	public FinanceTool() {

	}

	// /**
	// * Called when Finance Workspace is Created...
	// *
	// * @param name
	// * @param toolID
	// * @param space
	// */
	//
	// private void saveObject(IAccounterServerCore serverObject) {
	// Session session = HibernateUtil.getCurrentSession();
	// try {
	//
	// session.save(serverObject);
	//
	// if (serverObject instanceof Company) {
	// this.company = (Company) serverObject;
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * This will Get Called when Create Operation is Invoked by the Client
	 * 
	 * @param createContext
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws AccounterException
	 */
	public long create(OperationContext createContext)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		org.hibernate.Transaction transaction = session.beginTransaction();
		try {
			IAccounterCore data = createContext.getData();
			String userID = createContext.getUserEmail();

			if (data == null) {
				throw new AccounterException(
						AccounterException.ERROR_ILLEGAL_ARGUMENT,
						"Operation Data Found Null...." + createContext);
			}

			Class<IAccounterServerCore> serverClass = ObjectConvertUtil
					.getServerEqivalentClass(data.getClass());
			IAccounterServerCore serverObject;
			try {
				serverObject = serverClass.newInstance();
			} catch (Exception e1) {
				throw new AccounterException(AccounterException.ERROR_INTERNAL);
			}
			serverObject = new ServerConvertUtil().toServerObject(serverObject,
					(IAccounterCore) data, session);

			ObjectConvertUtil.setCompany((IAccounterServerCore) serverObject,
					getCompany());

			// if (serverObject instanceof CreatableObject) {
			// // get the user from user id
			// User user = getCompany().getUserByUserEmail(userID);
			// Timestamp currentTime = new Timestamp(
			// System.currentTimeMillis());
			// ((CreatableObject) serverObject).setCreatedBy(user);
			//
			// ((CreatableObject) serverObject).setCreatedDate(currentTime);
			// ((CreatableObject) serverObject).setLastModifier(user);
			//
			// ((CreatableObject) serverObject)
			// .setLastModifiedDate(currentTime);
			// }
			canEdit(serverObject, data);

			isTransactionNumberExist((IAccounterCore) data);

			session.save(serverObject);
			transaction.commit();
			ChangeTracker.put(serverObject);

			return serverObject.getID();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			transaction.rollback();
			if (e instanceof AccounterException) {
				throw (AccounterException) e;
			} else {
				throw new AccounterException(AccounterException.ERROR_INTERNAL,
						e.getMessage());
			}
		}
	}

	public long inviteUser(OperationContext context) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		org.hibernate.Transaction transaction = session.beginTransaction();
		try {
			IAccounterCore data = context.getData();
			if (data == null) {
				throw new AccounterException(
						AccounterException.ERROR_ILLEGAL_ARGUMENT,
						"Operation Data Found Null...." + data);
			}
			User user = new User((ClientUser) data);
			String email = user.getEmail();
			User userByUserEmail = getUserByUserEmail(email);
			if (userByUserEmail != null) {
				if (userByUserEmail.isDeleted()) {
					userByUserEmail.setDeleted(false);
					userByUserEmail.setUserRole(user.getUserRole());
					userByUserEmail.setPermissions(user.getPermissions());
					userByUserEmail.setCanDoUserManagement(user
							.isCanDoUserManagement());
					user = userByUserEmail;
					session.saveOrUpdate(user);
				}
			} else {
				Company company = getCompany();
				company.addUser(user);
			}
			transaction.commit();
			ClientUser clientObject = new ClientConvertUtil().toClientObject(
					user, ClientUser.class);
			ChangeTracker.put(clientObject.toUserInfo());
			return user.getID();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			transaction.rollback();
			if (e instanceof AccounterException) {
				throw (AccounterException) e;
			} else {
				throw new AccounterException(AccounterException.ERROR_INTERNAL,
						e.getMessage());
			}
		}
	}

	private User getUserByUserEmail(String email) {
		try {
			Session session = HibernateUtil.getCurrentSession();
			Query namedQuery = session.getNamedQuery("user.by.emailid");
			namedQuery.setParameter("emailID", email);
			User user = (User) namedQuery.list().get(0);
			return user;
		} catch (Exception e) {
			return null;
		}
	}

	public long updateUser(OperationContext updateContext)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();

		org.hibernate.Transaction hibernateTransaction = session
				.beginTransaction();
		try {
			IAccounterCore data = updateContext.getData();

			ClientUser clientUser = (ClientUser) data;

			User user = (User) session.get(User.class, clientUser.getID());

			new ServerConvertUtil().toServerObject(user,
					(IAccounterCore) clientUser, session);
			canEdit(user, data);
			session.flush();
			session.saveOrUpdate(user);
			hibernateTransaction.commit();
			ChangeTracker.put(clientUser.toUserInfo());
			return user.getID();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			hibernateTransaction.rollback();
			if (e instanceof AccounterException) {
				throw (AccounterException) e;
			} else {
				throw new AccounterException(AccounterException.ERROR_INTERNAL);
			}
		}

	}

	/**
	 * This will Get Called when Update Operation is Invoked by the Client
	 * 
	 * @param createContext
	 * @throws AccounterException
	 */
	public long update(OperationContext updateContext)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();

		org.hibernate.Transaction hibernateTransaction = session
				.beginTransaction();
		try {
			IAccounterCore data = updateContext.getData();

			if (data == null) {
				throw new AccounterException(
						AccounterException.ERROR_ILLEGAL_ARGUMENT,
						"Operation Data Found Null...." + updateContext);
			}

			Class<?> classforName = ObjectConvertUtil
					.classforName(updateContext.getArg2());

			IAccounterServerCore serverObject = (IAccounterServerCore) session
					.get(classforName, Long.parseLong(updateContext.getArg1()));
			IAccounterServerCore clonedObject = new CloneUtil().clone(null,
					serverObject);

			canEdit(clonedObject, (IAccounterCore) data);

			isTransactionNumberExist((IAccounterCore) data);

			new ServerConvertUtil().toServerObject(serverObject,
					(IAccounterCore) data, session);

			if (serverObject instanceof Transaction) {
				Transaction transaction = (Transaction) serverObject;
				transaction.onEdit((Transaction) clonedObject);

			}
			if (serverObject instanceof Lifecycle) {
				Lifecycle lifecycle = (Lifecycle) serverObject;
				lifecycle.onUpdate(session);
			}

			// if (serverObject instanceof Company) {
			// Company cmp = (Company) serverObject;
			// cmp.toCompany((ClientCompany) command.data);
			// ChangeTracker.put(cmp.toClientCompany());
			// }

			// called this method to save on unsaved objects in
			// session.
			// before going commit. because unsaved objects getting
			// update when transaction commit,
			// Util.loadObjectByStringID(session, command.arg2,
			// command.arg1);

			if ((IAccounterServerCore) serverObject instanceof CreatableObject) {
				// get the user from user id
				((CreatableObject) serverObject).setLastModifier(getCompany()
						.getUserByUserEmail(updateContext.getUserEmail()));

				((CreatableObject) serverObject)
						.setLastModifiedDate(new Timestamp(System
								.currentTimeMillis()));
			}

			session.flush();
			session.saveOrUpdate(serverObject);
			ChangeTracker.put(serverObject);
			hibernateTransaction.commit();
			return serverObject.getID();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			hibernateTransaction.rollback();
			if (e instanceof AccounterException) {
				throw (AccounterException) e;
			} else {
				throw new AccounterException(AccounterException.ERROR_INTERNAL);
			}
		}

	}

	/**
	 * This will Get Called when Delete Operation is Invoked by the Client
	 * 
	 * @param createContext
	 * @throws AccounterException
	 * @throws HibernateException
	 */
	public boolean delete(OperationContext context) throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		org.hibernate.Transaction hibernateTransaction = session
				.beginTransaction();

		String arg1 = (context).getArg1();
		String arg2 = (context).getArg2();

		if (arg1 == null || arg2 == null) {
			throw new AccounterException(
					AccounterException.ERROR_ILLEGAL_ARGUMENT,
					"Delete Operation Cannot be Processed id or cmd.arg2 Found Null...."
							+ context);
		}

		Class<?> clientClass = ObjectConvertUtil.getEqivalentClientClass(arg2);

		Class<?> serverClass = ObjectConvertUtil
				.getServerEqivalentClass(clientClass);

		IAccounterServerCore serverObject = (IAccounterServerCore) session.get(
				serverClass, Long.parseLong(arg1));

		// if (objects != null && objects.size() > 0) {

		// IAccounterServerCore serverObject = (IAccounterServerCore)
		// objects
		// .get(0);

		if (serverObject == null) {

		}
		if (serverObject instanceof FiscalYear) {
			((FiscalYear) serverObject).canDelete((FiscalYear) serverObject);
			session.delete(serverObject);
			// ChangeTracker.put(serverObject);
		} else if (serverObject instanceof User) {
			User user = (User) serverObject;
			user.setDeleted(true);
			session.saveOrUpdate(user);
		} else if (serverObject instanceof RecurringTransaction) {
			session.delete(serverObject);
		} else {
			if (canDelete(serverClass.getSimpleName(), Long.parseLong(arg1))) {
				session.delete(serverObject);
			} else {
				throw new AccounterException(
						AccounterException.ERROR_OBJECT_IN_USE);
			}
		}
		try {
			hibernateTransaction.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			hibernateTransaction.rollback();
			if (e instanceof AccounterException) {
				throw (AccounterException) e;
			} else {
				throw new AccounterException(AccounterException.ERROR_INTERNAL);
			}
		}
		return true;

	}

	private boolean canDelete(String serverClass, long id) {
		String queryName = getCanDeleteQueryName(serverClass);
		Query query = HibernateUtil.getCurrentSession()
				.getNamedQuery(queryName).setParameter("inputId", id);
		return executeQuery(query);
	}

	private String getCanDeleteQueryName(String serverClass) {
		StringBuffer query = new StringBuffer("canDelete");
		query.append(serverClass);
		if (serverClass.equals("TAXItem") || serverClass.equals("TAXGroup")) {
			if (getCompany().getAccountingType() == Company.ACCOUNTING_TYPE_US) {
				query.append("ForUS");
			}
		}
		return query.toString();
	}

	public void updateCompanyPreferences(OperationContext context)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		org.hibernate.Transaction transaction = session.beginTransaction();
		try {
			IAccounterCore data = context.getData();

			if (data == null) {
				throw new AccounterException(
						AccounterException.ERROR_ILLEGAL_ARGUMENT,
						"Update Company Preferences, as the Source Object could not be Found....");
			}

			Company company = getCompany();
			// String IdentiName =
			// this.getSpace().getIDentity().getDisplayName();

			CompanyPreferences serverCompanyPreferences = company
					.getPreferences();

			serverCompanyPreferences = new ServerConvertUtil().toServerObject(
					serverCompanyPreferences, (ClientCompanyPreferences) data,
					session);

			company.setPreferences(serverCompanyPreferences);
			session.update(company);
			transaction.commit();
			// CompanyPreferences serverObject = serverCompanyPreferences;
			ChangeTracker.put(serverCompanyPreferences);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			transaction.rollback();
			throw new AccounterException(AccounterException.ERROR_INTERNAL);
		}
	}

	public Long updateCompany(OperationContext context)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		org.hibernate.Transaction transaction = session.beginTransaction();
		try {
			IAccounterCore data = context.getData();
			if (data == null) {
				throw new AccounterException(
						AccounterException.ERROR_PERMISSION_DENIED,
						"Update Company , as the Source Object could not be Found....");
			}

			Company cmp = Company.getCompany();
			cmp.updatePreferences((ClientCompany) data);

			HibernateUtil.getCurrentSession().update(cmp);
			transaction.commit();
			ChangeTracker.put(cmp.toClientCompany());
			return cmp.getID();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			transaction.rollback();
			throw new AccounterException(AccounterException.ERROR_INTERNAL);
		}

	}

	public void updateCompanyStartDate(OperationContext context)
			throws AccounterException {
		String arg1 = context.getArg1();
		if (arg1 == null || arg1.isEmpty()) {
			throw new AccounterException(
					AccounterException.ERROR_PERMISSION_DENIED,
					"Cann't Update the Compamy StartData with Null or Empty");
		}
		FinanceDate modifiedStartDate = new FinanceDate(Long.parseLong(arg1));

		changeFiscalYearsStartDate(modifiedStartDate);

		Company company = Company.getCompany();

		CompanyPreferences serverCompanyPreferences = company.getPreferences();

		company.setPreferences(serverCompanyPreferences);

		serverCompanyPreferences.setStartDate(modifiedStartDate);
		serverCompanyPreferences.setPreventPostingBeforeDate(modifiedStartDate);
		serverCompanyPreferences.setStartOfFiscalYear(modifiedStartDate);
		// CompanyPreferences serverObject = serverCompanyPreferences;
		HibernateUtil.getCurrentSession().update(company);
		ChangeTracker.put(serverCompanyPreferences);
	}

	public void updateDeprecationStartDate(OperationContext context)
			throws AccounterException {
		String arg1 = context.getArg1();

		if (arg1 == null || arg1.isEmpty()) {
			throw new AccounterException(
					"Cann't Update Deprecation Strart Date with Null or Empty");
		}
		FinanceDate newStartDate = new FinanceDate(Long.parseLong(arg1));
		Company company1 = Company.getCompany();

		CompanyPreferences serverCompanyPreferences1 = company1
				.getPreferences();

		changeDepreciationStartDateTo(newStartDate);

		company1.setPreferences(serverCompanyPreferences1);
		serverCompanyPreferences1.setDepreciationStartDate(newStartDate);
		// CompanyPreferences serverObject = serverCompanyPreferences1;
		HibernateUtil.getCurrentSession().saveOrUpdate(company1);
		ChangeTracker.put(serverCompanyPreferences1);
	}

	public static boolean canEdit(IAccounterServerCore clonedObject,
			IAccounterCore clientObject) throws AccounterException {

		IAccounterServerCore serverObject = new ServerConvertUtil()
				.toServerObject(null, clientObject,
						HibernateUtil.getCurrentSession());

		return serverObject.canEdit(clonedObject);

	}

	//
	// private Company toServerCompany(Company company, ClientCompany
	// clientCompany) {
	// company.setName(clientCompany.getName());
	// company.setTradingName(clientCompany.getTradingName());
	// company.setPhone(clientCompany.getPhone());
	// company.setCompanyEmail(clientCompany.getCompanyEmail());
	// company.setWebSite(clientCompany.getWebSite());
	// company.setFax(clientCompany.getFax());
	// company.setRegistrationNumber(clientCompany.getRegistrationNumber());
	//
	// Set<ClientAddress> clientAddress = new HashSet<ClientAddress>();
	// clientAddress.addAll(clientCompany.getAddresses());
	// Set<Address> setAddress = (Set<Address>) new ServerConvertUtil()
	// .toServerSet(clientAddress, HibernateUtil.getCurrentSession());
	// company.setAddresses(setAddress);
	//
	// company.setTaxId(clientCompany.getTaxId());
	// return company;
	// }

	/*
	 * private void sendExecptionToIdeneity(int command, String arg, Exception
	 * e) { // An Un-Expected Exception has Occured, Create A Serializable //
	 * Custom Exception, to be Notified to Client thru Comet
	 * 
	 * InvalidOperationException exception = e instanceof
	 * InvalidOperationException ? (InvalidOperationException) e : new
	 * InvalidOperationException(command, arg, null); //
	 * exception.setStatus(command); // exception.setID(arg); exception.status =
	 * command;
	 * 
	 * CometStream stream = CometManager.getStream(getSpace().getIDentity()
	 * .getID(), "accounter"); try { stream.put(exception); } catch
	 * (NotSerializableException e1) { e1.printStackTrace(); } }
	 */

	/**
	 * if command type id create, alter and delete, then changes will be add in
	 * chanageTracker,Put changes in comet stream
	 */
	public void putChangesInCometStream() {
		try {
			IAccounterCore[] changes = ChangeTracker.getChanges();
			if (changes != null && changes.length > 0) {
				log.info("Sending Changes From ChangeTracker:" + changes.length);
				Session session = null;
				session = HibernateUtil.getCurrentSession();
				List<User> users = session.getNamedQuery("getAllUsers").list();

				for (User user : users) {
					try {
						CometStream stream = CometManager.getStream(
								user.getEmail(), "accounter");
						if (stream == null) {
							continue;
						}
						for (IAccounterCore obj : changes) {
							if (obj != null) {
								stream.put(obj);
							}
						}
						log.info("Sent " + changes.length + " change to "
								+ user.getEmail());
					} catch (NotSerializableException e) {
						e.printStackTrace();
						log.error("Failed to Process Request", e);

					}
				}
				ChangeTracker.clearChanges();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getServerObjectForid(AccounterCoreType t, long id) {

		Session session = HibernateUtil.getCurrentSession();

		Class<?> serverClass = getClientEquivalentServerClass(t);

		if (serverClass != null) {

			return session.get(serverClass, id);

		}

		return null;
	}

	@Override
	public <T extends IAccounterCore> T getObjectById(AccounterCoreType type,
			long id) throws DAOException, AccounterException {

		Object serverObject = getServerObjectForid(type, id);

		Class<?> serverClass = getClientEquivalentServerClass(type);

		if (serverObject != null) {
			T t = (T) new ClientConvertUtil().toClientObject(serverObject,
					Util.getClientEqualentClass(serverClass));
			if (t instanceof ClientTransaction
					&& getCompany().getAccountingType() == Company.ACCOUNTING_TYPE_UK) {
				Session session = HibernateUtil.getCurrentSession();
				Query query2 = session
						.getNamedQuery("getTAXRateCalculation.by.check.idandvatReturn");
				query2.setParameter(0, t.getID());
				List<?> list = query2.list();
				if (list != null && list.size() > 0)
					((ClientTransaction) t).setCanEdit(false);
			}
			return t;

		}

		return null;
	}

	private Class<?> getClientEquivalentServerClass(AccounterCoreType type) {

		String clientClassName = type.getClientClassSimpleName();

		clientClassName = clientClassName.replaceAll("Client", "");

		Class<?> clazz = null;

		// FIXME if Class class1 if of another package other than,
		// com.vimukti.accounter.core

		try {
			String qualifiedName = "com.vimukti.accounter.core."
					+ clientClassName;
			clazz = Class.forName(qualifiedName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return clazz;
	}

	@Override
	public <T extends IAccounterCore> T getObjectByName(AccounterCoreType type,
			String name) throws DAOException, AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		Class<?> serverClass = getClientEquivalentServerClass(type);

		if (serverClass != null) {

			Query hibernateQuery = session.getNamedQuery(
					"unique.name." + type.getServerClassSimpleName())
					.setString(0, name);

			List objects = hibernateQuery.list();

			if (objects != null && objects.size() > 0 && objects.get(0) != null) {

				return (T) new ClientConvertUtil().toClientObject(
						objects.get(0),
						Util.getClientEqualentClass(serverClass));

			}

		}

		return null;

	}

	public <T extends IAccounterCore> T getServerObjectByName(
			AccounterCoreType type, String name) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		// Class<?> serverClass = getClientEquivalentServerClass(type);

		if (type != null) {

			Query hibernateQuery = session.getNamedQuery(
					"unique.name." + type.getServerClassSimpleName())
					.setString(0, name);

			List objects = hibernateQuery.list();

			if (objects != null && objects.get(0) != null) {

				return (T) objects.get(0);

			}

		}

		return null;

	}

	@Override
	public <T extends IAccounterCore> VList<T> getObjects(AccounterCoreType type)
			throws DAOException, AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		List<T> clientObjects = new VList<T>();

		Class<?> serverClass = getClientEquivalentServerClass(type);

		if (serverClass != null) {

			Query hibernateQuery = session.getNamedQuery("list."
					+ type.getServerClassSimpleName());

			List objects = hibernateQuery.list();

			if (objects != null) {

				for (Object o : objects) {

					clientObjects.add((T) new ClientConvertUtil()
							.toClientObject(o,
									Util.getClientEqualentClass(serverClass)));
				}

			}

		}

		VList<T> arrayList = new VList<T>(clientObjects);
		return arrayList;

	}

	// private long getLongIdForGivenid(AccounterCoreType entity, long account)
	// {
	//
	// Session session = HibernateUtil.getCurrentSession();
	// String hqlQuery = "select entity.id from "
	// + entity.getServerClassFullyQualifiedName()
	// + " entity where entity.id=?";
	// Query query = session.createQuery(hqlQuery).setLong(0, account);
	// List l = query.list();
	// if (l != null && !l.isEmpty() && l.get(0) != null) {
	// return (Long) l.get(0);
	// } else
	// return 0;
	//
	// }

	// private long getLongIdForGivenid(Class clazz,
	// String id) {
	//
	// Session session = HibernateUtil.getCurrentSession();
	// String hqlQuery = "select entity.id from "
	// + clazz.getName()
	// + " entity where entity.id=?";
	// Query query = session.createQuery(hqlQuery).setString(0, id);
	// List l = query.list();
	// if (l != null && l.get(0) != null) {
	// return (Long) l.get(0);
	// } else
	// return 0;
	//
	// }

	/*
	 * @Override public <T extends IAccounterServerCore> Boolean canDelete(
	 * AccounterCoreType clazz, String id) throws DAOException {
	 * 
	 * Session session = HibernateUtil.getCurrentSession(); ObjectConvertUtil
	 * convertUtil = new ObjectConvertUtil();
	 * 
	 * try { if (convertUtil.isFieldExist("isDefault",
	 * Class.forName(clazz.getServerClassFullyQualifiedName()))) { Object object
	 * = session .createQuery( "select entity.isDefault from " +
	 * clazz.getServerClassFullyQualifiedName() + " entity where entity.id=?")
	 * .setParameter(0, id).uniqueResult(); if ((object != null && ((Boolean)
	 * object))) return false; } } catch (HibernateException e) {
	 * e.printStackTrace(); } catch (ClassNotFoundException e) {
	 * e.printStackTrace(); } if
	 * (clazz.getServerClassSimpleName().equals("User")) { return
	 * checkCanDeleteUser(session, clazz, id); }
	 * 
	 * deleteTaxCodeOfTaxItemGroupIfUSversion(session, clazz, id);
	 * 
	 * long inputId = getLongIdForGivenid(clazz, id); String queryName = new
	 * StringBuilder().append("canDelete")
	 * .append(clazz.getServerClassSimpleName()).toString(); Query query =
	 * session.getNamedQuery(queryName).setParameter("inputId", inputId); return
	 * executeQuery(query); }
	 */

	/*
	 * private boolean checkCanDeleteUser(Session session, AccounterCoreType
	 * clazz, String id) { if (identity != null && identity.getID().equals(id))
	 * { sendExecptionToIdeneity(DELETE_ACTION, id, new
	 * InvalidOperationException( "You can't delete your own user")); return
	 * false; } else { Query query = session.createQuery(
	 * "from com.vimukti.accounter.core.User u where u.id =:id")
	 * .setParameter("id", id);
	 * 
	 * User user = (User) query.uniqueResult(); if (user.isAdmin()) {
	 * sendExecptionToIdeneity( DELETE_ACTION, id, new
	 * InvalidOperationException(
	 * "You can't delete Administrator of this organization")); return false; }
	 * } return true; }
	 */
	private boolean executeQuery(Query query) {
		List<?> queryResult = query.list();
		Boolean flag = true;
		if (queryResult != null && queryResult.size() > 0
				&& queryResult.get(0) != null) {
			if (queryResult.get(0) instanceof Long) {
				Long obj = (Long) queryResult.get(0);
				if (obj != null) {
					flag = false;

				}
			} else {
				Object[] result = (Object[]) queryResult.get(0);
				for (Object object : result) {
					if (object != null) {
						flag = false;
						break;
					}
				}
			}

		}
		return flag;
	}

	@Override
	@Deprecated
	/*
	 * The code in this method is shifted to onUpdate of FiscalYear
	 */
	public void alterFiscalYear(FiscalYear fiscalYear) throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();
			List<FiscalYear> list = new VList<FiscalYear>();
			if (fiscalYear.getStartDate().equals(
					fiscalYear.getPreviousStartDate())) {
				session.saveOrUpdate(fiscalYear);
			} else if (!fiscalYear.getStartDate().equals(
					fiscalYear.getPreviousStartDate())) {
				FinanceDate modifiedStartDate = fiscalYear.getStartDate();
				FinanceDate existingLeastStartDate = modifiedStartDate;
				FinanceDate existingHighestEndDate = modifiedStartDate;
				Boolean exist = Boolean.FALSE;
				list = session.getNamedQuery("getFiscalYearf").list();
				if (list.size() > 0) {
					Iterator i = list.iterator();
					if (i.hasNext()) {
						FiscalYear fs = (FiscalYear) i.next();
						existingLeastStartDate = fs.getStartDate();
						existingHighestEndDate = fs.getEndDate();
					}
					i = list.iterator();
					while (i.hasNext()) {
						FiscalYear fs = (FiscalYear) i.next();
						if (modifiedStartDate.after(fs.getStartDate())
								&& modifiedStartDate.before(fs.getEndDate())) {
							exist = Boolean.TRUE;
							break;
						}
						if (fs.getStartDate().before(existingLeastStartDate)) {
							existingLeastStartDate = fs.getStartDate();
						}
						if (fs.getEndDate().after(existingHighestEndDate)) {
							existingHighestEndDate = fs.getEndDate();
						}

					}
					if (!exist) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(modifiedStartDate.getAsDateObject());
						Integer modifiedYear = cal.get(Calendar.YEAR);

						cal.setTime(existingLeastStartDate.getAsDateObject());
						Integer existingLeastYear = cal.get(Calendar.YEAR);

						cal.setTime(existingHighestEndDate.getAsDateObject());
						Integer existingHighestYear = cal.get(Calendar.YEAR);
						if (modifiedStartDate.before(existingLeastStartDate)) {
							int diff = existingLeastYear - modifiedYear;
							for (int k = 0; k < diff; k++) {

								cal.set(modifiedYear + k, 0, 1);
								FinanceDate startDate = (new FinanceDate(
										cal.getTime()));

								cal.set(modifiedYear + k, 11, 31);
								FinanceDate endDate = (new FinanceDate(
										cal.getTime()));

								FiscalYear fs = new FiscalYear();
								fs.setStartDate(startDate);
								fs.setEndDate(endDate);
								fs.setStatus(FiscalYear.STATUS_OPEN);
								fs.setIsCurrentFiscalYear(Boolean.FALSE);
								session.save(fs);
							}

						} else if (modifiedStartDate
								.after(existingHighestEndDate)) {
							int diff = modifiedYear - existingLeastYear;
							for (int k = 1; k <= diff; k++) {
								cal.set(existingLeastYear + k, 0, 1);
								FinanceDate startDate = (new FinanceDate(
										cal.getTime()));

								cal.set(existingLeastYear + k, 0, 1);
								FinanceDate endDate = (new FinanceDate(
										cal.getTime()));
								FiscalYear fs = new FiscalYear();
								fs.setStartDate(startDate);
								fs.setEndDate(endDate);
								fs.setStatus(FiscalYear.STATUS_OPEN);
								fs.setIsCurrentFiscalYear(Boolean.FALSE);
								session.save(fs);
							}
						}

					}
					fiscalYear.setStartDate(fiscalYear.getPreviousStartDate());
					session.saveOrUpdate(fiscalYear);

				}
			} else if (fiscalYear.getStatus() == FiscalYear.STATUS_CLOSE) {
				if (!fiscalYear.getIsCurrentFiscalYear()) {
					throw (new DAOException(
							DAOException.INVALID_REQUEST_EXCEPTION, null));
				} else {
					session.saveOrUpdate(fiscalYear);
				}
			}

		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	// Updated AccounterGUIDAOService methods
	/*
	 * ==========================================================================
	 * =====================
	 */

	@Override
	public VList<EnterBill> getBillsOwed() throws DAOException {
		// Session session = getSessionFactory().openSession();
		// Query query = session.createSQLQuery(
		// "SELECT E.ID, E.VERSION, E.CREATED_DATE, E.MODIFIED_ON, E.VENDOR_ID, E.CONTACT_ID, E.VENDOR_ADDRESS_ID, E.PHONE, E.PAYMENT_TERM_ID, E.DUE_DATE, E.DELIVERY_DATE, E.MEMO, E.REFERENCE, E.TOTAL, E.PAYMENTS, E.BALANCE_DUE, E.ACCOUNTS_PAYABLE_ID FROM ENTER_BILL  E WHERE E.DUE_DATE <= CURRENT_DATE AND E.BALANCE_DUE>0.0"
		// +
		// "");
		//
		//
		// Iterator iterator = query.list().iterator();
		// List<EnterBill> list = new VList<EnterBill>();
		// while(iterator.hasNext()){
		// list.add((EnterBill)iterator.next());
		// }
		// if (list != null) {
		// return list;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		//
		// HibernateTemplate template = getHibernateTemplate();
		//
		// List<EnterBill> list = template
		// .find(
		// "from EnterBill e where e.company.id = ? and e.balanceDue > 0.0 and e.dueDate <= current_date"
		// ,
		// new Object[] { company });
		//
		// if (list != null) {
		// return list;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		return null;
	}

	@Override
	public VList<BillsList> getBillsList(boolean isExpensesList)
			throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();
			// FIXME:: change the sql query to hql query
			Query query = session.getNamedQuery("getBillsList");
			List list = query.list();
			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<BillsList> queryResult = new VList<BillsList>();
				while ((iterator).hasNext()) {

					BillsList billsList = new BillsList();
					object = (Object[]) iterator.next();

					billsList.setTransactionId((Long) object[0]);
					billsList.setType((Integer) object[1]);
					billsList.setDueDate(((Long) object[2]) == null ? null
							: new ClientFinanceDate((Long) object[2]));
					billsList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					billsList.setVendorName((String) object[4]);
					billsList.setOriginalAmount((Double) object[5]);
					billsList.setBalance(((Double) object[6]) == null ? null
							: (Double) object[6]);
					billsList.setVoided((Boolean) object[7]);
					billsList.setStatus((Integer) object[8]);
					billsList.setDate(new ClientFinanceDate((Long) object[9]));
					billsList.setExpenseStatus((Integer) object[10]);

					if (object[4] != null) {
						if (isExpensesList) {
							if (billsList.getType() == ClientTransaction.TYPE_CASH_EXPENSE
									|| billsList.getType() == ClientTransaction.TYPE_CREDIT_CARD_EXPENSE
									|| billsList.getType() == ClientTransaction.TYPE_EMPLOYEE_EXPENSE)
								queryResult.add(billsList);
						} else
							queryResult.add(billsList);
					}
				}
				return new VList<BillsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<IssuePaymentTransactionsList> getChecks() throws DAOException {

		// Session session = getSessionFactory().openSession();
		// Query query = session.createSQLQuery(new StringBuilder().append(
		// " SELECT WC.ID, WC.VERSION, WC.CREATED_DATE, WC.MODIFIED_ON, WC.KEY, WC.PAY_TO_TYPE, WC.ACCOUNT_ID, WC.CUSTOMER_ID, WC.VENDOR_ID, WC.TAX_AGENCY_ID, WC.ADDRESS_ID, WC.AMOUNT, WC.TOTAL, WC.MEMO FROM WRITE_CHECKS WC JOIN TRANSACTION  T ON T.ID = WC.ID AND T.STATUS = 0"
		// ).toString());
		// Iterator iterator = query.list().iterator();
		// List<WriteCheck> list = new VList<WriteCheck>();
		// while(iterator.hasNext()){
		// list.add((WriteCheck)iterator.next());
		// }

		Session session = HibernateUtil.getCurrentSession();
		List<IssuePaymentTransactionsList> issuePaymentTransactionsList = new VList<IssuePaymentTransactionsList>();
		Query query = session.getNamedQuery("getWriteCheck.by.status")
				.setParameter(0,
						Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		List list = query.list();
		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				WriteCheck wc = (WriteCheck) i.next();
				issuePaymentTransaction.setTransactionId(wc.getID());
				issuePaymentTransaction.setType(wc.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate(wc
						.getDate().getDate()));
				issuePaymentTransaction.setNumber(wc.getNumber());
				issuePaymentTransaction.setName((wc.getCustomer() != null) ? wc
						.getCustomer().getName()
						: ((wc.getVendor() != null) ? wc.getVendor().getName()
								: null));
				issuePaymentTransaction.setMemo(wc.getMemo());
				issuePaymentTransaction.setAmount(wc.getAmount());
				issuePaymentTransaction.setPaymentMethod(wc.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);
			}
		}
		query = session.getNamedQuery("getCustomerRefund.by.isvoidandstatus")
				.setParameter(0,
						Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		list = query.list();
		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				CustomerRefund cr = (CustomerRefund) i.next();
				issuePaymentTransaction.setTransactionId(cr.getID());
				issuePaymentTransaction.setType(cr.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate((long) cr
						.getDate().getDate()));
				issuePaymentTransaction.setNumber(cr.getNumber());
				issuePaymentTransaction.setName(cr.getPayTo().getName());
				issuePaymentTransaction.setMemo(cr.getMemo());
				issuePaymentTransaction.setAmount(cr.getTotal());
				issuePaymentTransaction.setPaymentMethod(cr.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);

			}
		}

		query = session.getNamedQuery("getPaySalesTax.by.status").setParameter(
				0, Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		list = query.list();

		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				PaySalesTax pst = (PaySalesTax) i.next();
				issuePaymentTransaction.setTransactionId(pst.getID());
				issuePaymentTransaction.setType(pst.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate(
						(long) pst.getDate().getDate()));
				issuePaymentTransaction.setNumber(pst.getNumber());
				issuePaymentTransaction.setName("TaxAgency Payment");
				// issuePaymentTransaction.setMemo(pst.getMemo());
				issuePaymentTransaction.setAmount(pst.getTotal());
				issuePaymentTransaction
						.setPaymentMethod(pst.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);

			}
		}
		query = session.getNamedQuery("getPayBill.form.status").setParameter(
				"status",
				Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		list = query.list();

		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				PayBill pb = (PayBill) i.next();
				issuePaymentTransaction.setTransactionId(pb.getID());
				issuePaymentTransaction.setType(pb.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate((long) pb
						.getDate().getDate()));
				issuePaymentTransaction.setNumber(pb.getNumber());
				issuePaymentTransaction.setName(pb.getVendor() != null ? pb
						.getVendor().getName() : null);
				issuePaymentTransaction.setMemo(pb.getMemo());
				issuePaymentTransaction.setAmount(pb.getTotal());
				issuePaymentTransaction.setPaymentMethod(pb.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);

			}
		}

		query = session.getNamedQuery("getCreditCardCharge.form.status")
				.setParameter("status",
						Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		list = query.list();

		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				CreditCardCharge cc = (CreditCardCharge) i.next();
				issuePaymentTransaction.setTransactionId(cc.getID());
				issuePaymentTransaction.setType(cc.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate((long) cc
						.getDate().getDate()));
				issuePaymentTransaction.setNumber(cc.getNumber());
				issuePaymentTransaction.setName(cc.getVendor() != null ? cc
						.getVendor().getName() : null);
				issuePaymentTransaction.setMemo(cc.getMemo());
				issuePaymentTransaction.setAmount(cc.getTotal());
				issuePaymentTransaction.setPaymentMethod(cc.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);

			}
		}

		query = session.getNamedQuery("getCashPurchase.form.status")
				.setParameter("status",
						Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
		list = query.list();

		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
				CashPurchase cc = (CashPurchase) i.next();
				issuePaymentTransaction.setTransactionId(cc.getID());
				issuePaymentTransaction.setType(cc.getType());
				issuePaymentTransaction.setDate(new ClientFinanceDate((long) cc
						.getDate().getDate()));
				issuePaymentTransaction.setNumber(cc.getNumber());
				issuePaymentTransaction.setName(cc.getVendor() != null ? cc
						.getVendor().getName() : null);
				issuePaymentTransaction.setMemo(cc.getMemo());
				issuePaymentTransaction.setAmount(cc.getTotal());
				issuePaymentTransaction.setPaymentMethod(cc.getPaymentMethod());

				issuePaymentTransactionsList.add(issuePaymentTransaction);

			}
		}

		if (issuePaymentTransactionsList != null) {
			return new VList<IssuePaymentTransactionsList>(
					issuePaymentTransactionsList);
		} else
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));
	}

	@Override
	public VList<IssuePaymentTransactionsList> getChecks(long account)
			throws DAOException {
		try {
			// Session session = getSessionFactory().openSession();
			// Query query = session.createSQLQuery(new StringBuilder().append(
			// " SELECT WC.ID, WC.VERSION, WC.CREATED_DATE, WC.MODIFIED_ON, WC.KEY, WC.PAY_TO_TYPE, WC.ACCOUNT_ID, WC.CUSTOMER_ID, WC.VENDOR_ID, WC.TAX_AGENCY_ID, WC.ADDRESS_ID, WC.AMOUNT, WC.TOTAL, WC.MEMO FROM WRITE_CHECKS WC JOIN TRANSACTION  T ON T.ID = WC.ID AND  WC.ACCOUNT_ID = "
			// ).append(accountid).append(" AND T.STATUS = 0").toString());
			// // query.setLong(1, accountid);
			// Iterator iterator = query.list().iterator();
			// List<WriteCheck> list = new VList<WriteCheck>();
			// while(iterator.hasNext()){
			// list.add((WriteCheck)iterator.next());
			// }

			Session session = HibernateUtil.getCurrentSession();
			List<IssuePaymentTransactionsList> issuePaymentTransactionsList = new VList<IssuePaymentTransactionsList>();
			Query query = session
					.getNamedQuery("getWriteCheck.by.bankacountIdandstatus")
					.setParameter(0, account)
					.setParameter(
							1,
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			List list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					WriteCheck wc = (WriteCheck) i.next();
					issuePaymentTransaction.setTransactionId(wc.getID());
					issuePaymentTransaction.setType(wc.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) wc.getDate().getDate()));
					issuePaymentTransaction.setNumber(wc.getNumber());
					issuePaymentTransaction
							.setName((wc.getCustomer() != null) ? wc
									.getCustomer().getName()
									: ((wc.getVendor() != null) ? wc
											.getVendor().getName() : wc
											.getTaxAgency().getName()));
					issuePaymentTransaction.setMemo(wc.getMemo());
					issuePaymentTransaction.setAmount(wc.getAmount());
					issuePaymentTransaction.setPaymentMethod(wc
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);
				}
			}

			query = session
					.getNamedQuery(
							"getCustomerRefund.by.payFromand.isvoid.status")
					.setParameter(0, account)
					.setParameter(
							1,
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);

			list = query.list();

			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					CustomerRefund cr = (CustomerRefund) i.next();
					issuePaymentTransaction.setTransactionId(cr.getID());
					issuePaymentTransaction.setType(cr.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) cr.getDate().getDate()));
					issuePaymentTransaction.setNumber(cr.getNumber());
					issuePaymentTransaction.setName(cr.getPayTo().getName());
					issuePaymentTransaction.setMemo(cr.getMemo());
					issuePaymentTransaction.setAmount(cr.getTotal());
					issuePaymentTransaction.setPaymentMethod(cr
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			query = session
					.getNamedQuery("getPaySalesTax.by.payFromand.isvoid.status")
					.setParameter(0, account)
					.setParameter(
							1,
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					PaySalesTax pst = (PaySalesTax) i.next();
					issuePaymentTransaction.setTransactionId(pst.getID());
					issuePaymentTransaction.setType(pst.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) pst.getDate().getDate()));
					issuePaymentTransaction.setNumber(pst.getNumber());
					issuePaymentTransaction.setName("TaxAgency Payment");
					// issuePaymentTransaction.setMemo(pst.getMemo());
					issuePaymentTransaction.setAmount(pst.getTotal());
					issuePaymentTransaction.setPaymentMethod(pst
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			query = session
					.getNamedQuery("getPayBill.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					PayBill pst = (PayBill) i.next();
					issuePaymentTransaction.setTransactionId(pst.getID());
					issuePaymentTransaction.setType(pst.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) pst.getDate().getDate()));
					issuePaymentTransaction.setNumber(pst.getNumber());
					issuePaymentTransaction
							.setName(pst.getVendor() != null ? pst.getVendor()
									.getName() : null);
					issuePaymentTransaction.setMemo(pst.getMemo());
					issuePaymentTransaction.setAmount(pst.getTotal());
					issuePaymentTransaction.setPaymentMethod(pst
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}
			query = session
					.getNamedQuery("getPayVAT.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					PayVAT pv = (PayVAT) i.next();
					issuePaymentTransaction.setTransactionId(pv.getID());
					issuePaymentTransaction.setType(pv.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) pv.getDate().getDate()));
					issuePaymentTransaction.setNumber(pv.getNumber());
					issuePaymentTransaction.setName("VAT Agency Payment");
					issuePaymentTransaction.setMemo(null);
					issuePaymentTransaction.setAmount(pv.getTotal());
					issuePaymentTransaction.setPaymentMethod(pv
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}
			query = session
					.getNamedQuery("getReceiveVAT.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					ReceiveVAT rv = (ReceiveVAT) i.next();
					issuePaymentTransaction.setTransactionId(rv.getID());
					issuePaymentTransaction.setType(rv.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) rv.getDate().getDate()));
					issuePaymentTransaction.setNumber(rv.getNumber());
					issuePaymentTransaction.setName("VAT Agency Payment");
					issuePaymentTransaction.setMemo(null);
					issuePaymentTransaction.setAmount(rv.getTotal());
					issuePaymentTransaction.setPaymentMethod(rv
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			query = session
					.getNamedQuery(
							"getCreditCardCharge.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					CreditCardCharge pst = (CreditCardCharge) i.next();
					issuePaymentTransaction.setTransactionId(pst.getID());
					issuePaymentTransaction.setType(pst.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) pst.getDate().getDate()));
					issuePaymentTransaction.setNumber(pst.getNumber());
					issuePaymentTransaction
							.setName(pst.getVendor() != null ? pst.getVendor()
									.getName() : null);
					issuePaymentTransaction.setMemo(pst.getMemo());
					issuePaymentTransaction.setAmount(pst.getTotal());
					issuePaymentTransaction.setPaymentMethod(pst
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			query = session
					.getNamedQuery("getCashPurchase.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();
			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					CashPurchase pst = (CashPurchase) i.next();
					issuePaymentTransaction.setTransactionId(pst.getID());
					issuePaymentTransaction.setType(pst.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) pst.getDate().getDate()));
					issuePaymentTransaction.setNumber(pst.getNumber());
					issuePaymentTransaction
							.setName(pst.getVendor() != null ? pst.getVendor()
									.getName()
									: (pst.getEmployee() != null ? pst
											.getEmployee().getName() : null));
					issuePaymentTransaction.setMemo(pst.getMemo());
					issuePaymentTransaction.setAmount(pst.getTotal());
					issuePaymentTransaction.setPaymentMethod(pst
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			query = session
					.getNamedQuery(
							"getCustomerPrePayment.form.accountId.and.status")
					.setParameter("accountId", account)
					.setParameter(
							"status",
							Transaction.STATUS_NOT_PAID_OR_UNAPPLIED_OR_NOT_ISSUED);
			list = query.list();

			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					IssuePaymentTransactionsList issuePaymentTransaction = new IssuePaymentTransactionsList();
					CustomerPrePayment cpp = (CustomerPrePayment) i.next();
					issuePaymentTransaction.setTransactionId(cpp.getID());
					issuePaymentTransaction.setType(cpp.getType());
					issuePaymentTransaction.setDate(new ClientFinanceDate(
							(long) cpp.getDate().getDate()));
					issuePaymentTransaction.setNumber(cpp.getNumber());
					issuePaymentTransaction
							.setName(cpp.getCustomer() != null ? cpp
									.getCustomer().getName() : null);
					issuePaymentTransaction.setMemo(cpp.getMemo());
					issuePaymentTransaction.setAmount(cpp.getTotal());
					issuePaymentTransaction.setPaymentMethod(cpp
							.getPaymentMethod());

					issuePaymentTransactionsList.add(issuePaymentTransaction);

				}
			}

			if (issuePaymentTransactionsList != null) {
				return new VList<IssuePaymentTransactionsList>(
						issuePaymentTransactionsList);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<CreditCardCharge> getCreditCardChargesThisMonth(final long date)
			throws DAOException {
		// SELECT * from com.vimukti.accounter.core.CREDIT_CARD_CHARGES CCC JOIN
		// TRANSACTION T ON T.ID =
		// CCC.ID AND T.T_DATE = CURRENT_DATE
		// List<CreditCardCharge> list = template.find(
		// "from CreditCardCharge ccc where ccc.company = ? and  MONTH(ccc.date) = MONTH(?) and YEAR(ccc.date) = YEAR(?)"
		// ,new Object[] {company, date, date});

		Session session = HibernateUtil.getCurrentSession();

		int month = 0;
		int year = 0;
		FinanceDate fdate = new FinanceDate(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(fdate.getAsDateObject());
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);

		Query query = session
				.createSQLQuery(new StringBuilder()
						.append("SELECT CCC.ID, CCC.VERSION, CCC.CREATED_DATE, CCC.MODIFIED_ON, CCC.VENDOR_ID, CCC.CONTACT_ID, "
								+ "CCC.VENDOR_ADDRESS_ID, CCC.PAYMENT_METHOD_ID, CCC.PAYFROM_ACCOUNT_ID, CCC.CHECK_NUMBER, "
								+ "CCC.DELIVERY_DATE, CCC.MEMO, CCC.REFERENCE, CCC.TOTAL FROM CREDIT_CARD_CHARGES CCC JOIN "
								+ "TRANSACTION T ON T.ID = CCC.ID AND MONTH(T.T_DATE) = ")
						.append(month).append(" AND YEAR(T.T_DATE) = ")
						.append(month).toString());
		Iterator iterator = query.list().iterator();
		List<CreditCardCharge> list = new VList<CreditCardCharge>();
		while (iterator.hasNext()) {
			list.add((CreditCardCharge) iterator.next());
		}

		return new VList<CreditCardCharge>(list);
	}

	@Override
	public VList<CreditsAndPayments> getCustomerCreditsAndPayments(long customer)
			throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery(
					"getCreditsAndPayments.by.check.payeeidandbalanceid")
					.setParameter(0, customer);
			List list = query.list();

			if (list != null) {
				return new VList<CreditsAndPayments>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<CustomerRefundsList> getCustomerRefundsList()
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();

			List<CustomerRefundsList> customerRefundsList = new VList<CustomerRefundsList>();
			Query query = session.getNamedQuery("getCustomerRefund");
			List list = query.list();

			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					CustomerRefundsList customerRefund = new CustomerRefundsList();
					CustomerRefund cr = (CustomerRefund) i.next();
					customerRefund.setTransactionId(cr.getID());
					customerRefund.setType(cr.getType());
					customerRefund.setPaymentDate(new ClientFinanceDate(cr
							.getDate().getDate()));
					customerRefund.setIssueDate(new ClientFinanceDate(cr
							.getDate().getDate()));
					customerRefund.setPaymentNumber(cr.getNumber());
					customerRefund.setStatus(cr.getStatus());
					customerRefund.setName((cr.getPayTo() != null) ? cr
							.getPayTo().getName() : null);
					customerRefund
							.setPaymentMethod((cr.getPaymentMethod() != null) ? cr
									.getPaymentMethod() : null);
					customerRefund.setAmountPaid(cr.getTotal());
					customerRefund.setVoided(cr.isVoid());

					customerRefundsList.add(customerRefund);
				}
			}
			query = session.getNamedQuery("getWriteCheck.by.payToType")
					.setParameter(0, WriteCheck.TYPE_CUSTOMER);
			list = query.list();

			if (list != null) {
				Iterator i = list.iterator();
				while (i.hasNext()) {

					CustomerRefundsList customerRefund = new CustomerRefundsList();
					WriteCheck wc = (WriteCheck) i.next();
					customerRefund.setTransactionId(wc.getID());
					customerRefund.setType(wc.getType());
					customerRefund.setPaymentDate(new ClientFinanceDate(wc
							.getDate().getDate()));
					customerRefund.setIssueDate(null);
					customerRefund.setPaymentNumber(wc.getNumber());
					customerRefund.setStatus(wc.getStatus());
					customerRefund.setName((wc.getCustomer() != null) ? wc
							.getCustomer().getName()
							: ((wc.getVendor() != null) ? wc.getVendor()
									.getName()
									: (wc.getTaxAgency() != null ? wc
											.getTaxAgency().getName() : null)));
					customerRefund.setPaymentMethod(null);
					customerRefund.setAmountPaid(wc.getAmount());
					customerRefund.setVoided(wc.isVoid());
					customerRefundsList.add(customerRefund);

				}
			}

			if (customerRefundsList != null) {
				return new VList<CustomerRefundsList>(customerRefundsList);
			} else
				throw new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null);
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Entry> getEntries(long journalEntryId) throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery("getEntry.by.id").setParameter(
					0, journalEntryId);
			List<Entry> list = query.list();

			if (list != null) {
				return new VList<Entry>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Estimate> getEstimates() throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery("getEstimate");
			List<Estimate> list = query.list();

			if (list != null) {
				return new VList<Estimate>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Estimate> getEstimates(long customer) throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery(
					"getEstimate.by.check.id.status").setParameter(0, customer);
			List<Estimate> list = query.list();

			if (list != null) {
				return new VList<Estimate>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<InvoicesList> getInvoiceList() throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();
			// FIXME :: query optimization
			Query query = session.getNamedQuery("getInvoicesList");
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<InvoicesList> queryResult = new VList<InvoicesList>();
				while ((iterator).hasNext()) {

					InvoicesList invoicesList = new InvoicesList();
					object = (Object[]) iterator.next();

					long transactionNumber = (object[0] == null ? 0
							: ((Long) object[0]));
					invoicesList.setTransactionId(transactionNumber);
					invoicesList.setType((Integer) object[1]);
					invoicesList
							.setDate(new ClientFinanceDate((Long) object[2]));
					invoicesList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					invoicesList.setCustomerName((String) object[4]);
					invoicesList.setNetAmount((Double) object[5]);
					invoicesList.setDueDate(((Long) object[6]) == null ? null
							: new ClientFinanceDate((Long) object[6]));
					invoicesList.setTotalPrice((Double) object[7]);
					invoicesList.setBalance(((Double) object[8]) == null ? null
							: (Double) object[8]);
					invoicesList.setVoided((Boolean) object[9]);
					invoicesList.setStatus((Integer) object[10]);

					queryResult.add(invoicesList);
				}
				return new VList<InvoicesList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<JournalEntry> getJournalEntries() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery("getJournalEntry");
			List<JournalEntry> list = query.list();

			if (list != null) {
				return new VList<JournalEntry>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public JournalEntry getJournalEntry(long journalEntryId)
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();

			Query query = session.getNamedQuery("getJournalEntry.by.id")
					.setParameter(0, journalEntryId);
			List<JournalEntry> list = query.list();

			if (list.size() > 0) {
				return list.get(0);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Estimate> getLatestQuotes() throws DAOException {
		// SELECT E1.* FROM ESTIMATE E1 WHERE 10>(SELECT COUNT(*) FROM
		// TRANSACTION E2 WHERE E1.ID<E2.ID)

		try {
			Session session = HibernateUtil.getCurrentSession();
			// FIXME::: query optimization
			Query query = session.getNamedQuery("getLatestQuotes");
			List list2 = query.list();
			Object object[] = null;

			Iterator iterator = list2.iterator();
			List<Estimate> list = new VList<Estimate>();
			while (iterator.hasNext()) {
				object = (Object[]) iterator.next();
				Estimate estimate = new Estimate();
				// TODO :: change the query
				// estimate.setID((object[0] == null ? null : ((Long)
				// object[0])));
				estimate.setDate(new FinanceDate((Long) (object[1])));
				estimate.setCustomer(object[2] != null ? (Customer) session
						.get(Customer.class, ((Long) object[2])) : null);
				estimate.setSalesPerson(object[3] != null ? (SalesPerson) session
						.get(SalesPerson.class, ((Long) object[3])) : null);
				estimate.setTotal((Double) object[4]);
				list.add(estimate);
			}
			if (list != null) {
				return new VList<Estimate>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public Long getNextIssuePaymentCheckNumber(long account)
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery(
					"getNextIssuePaymentCheckNumber").setParameter("accountID",
					account);
			List list = query.list();

			if (list != null) {
				return (list.size() > 0) ? ((Long) list.get(0)) + 1 : 1;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	//
	// @Override
	// public Long getNextTransactionNumber(int transactionType)
	// throws DAOException {
	// try {
	//
	// Session session = HibernateUtil.getCurrentSession();
	// Query query = session.getNamedQuery("getNextTransactionNumber")
	// .setParameter("type", transactionType);
	// List list = query.list();
	//
	// if (list != null) {
	// return (list.size() > 0) ? ((Long) list.get(0)) + 1 : 1;
	// } else
	// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
	// null));
	// } catch (DAOException e) {
	// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
	// }
	// }

	@Override
	public String getNextFixedAssetNumber() throws DAOException {
		// try {
		//
		// Session session = HibernateUtil.getCurrentSession();
		// Query query = session.getNamedQuery("getNextFixedAssetNumber");
		// List list = query.list();
		//
		// if (list != null) {
		// return (list.size() > 0) ? ((Long) list.get(0)) + 1 : 1;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		// } catch (DAOException e) {
		// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		// }
		return NumberUtils.getNextFixedAssetNumber();

	}

	@Override
	public String getNextVoucherNumber() throws DAOException {
		// try {
		//
		// Session session = HibernateUtil.getCurrentSession();
		// Query query = session
		// .createQuery("from com.vimukti.accounter.core.Entry e ");
		// List list1 = query.list();
		//
		// if (list1.size() <= 0) {
		//
		// return this
		// .getNextTransactionNumber(Transaction.TYPE_JOURNAL_ENTRY);
		// }
		//
		// query = session
		// .createQuery("select e.voucherNumber from com.vimukti.accounter.core.Entry e where e.id = (select max(e1.id) from com.vimukti.accounter.core.Entry e1 )");
		// List list = query.list();
		//
		// if (list != null) {
		// return getStringwithIncreamentedDigit(((String) list.get(0)));
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		// } catch (DAOException e) {
		// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		// }
		return NumberUtils.getNextVoucherNumber();
	}

	@Override
	public VList<OverDueInvoicesList> getOverDueInvoices() throws DAOException {
		// try {
		// Session session = getSessionFactory().openSession();
		// Query query = session.createSQLQuery(
		// "SELECT * FROM INVOICE I LEFT JOIN TRANSACTION T ON I.ID = T.ID WHERE T.COMPANY_ID = :C_ID AND I.BALANCE_DUE > 0.0"
		// ).addEntity(Invoice.class);
		// query.setLong("C_ID", company);
		// List<Invoice> list = query.list();
		// // HibernateTemplate template = getHibernateTemplate();
		// // List<Invoice> list = template.find(
		// "from Invoice i where i.company = ? and i.balanceDue > 0.0 and i.dueDate <= current_date"
		// ,new Object[] {company});
		//
		// if (list != null) {
		// return list;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		// } catch (DAOException e) {
		// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		// }

		try {
			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestOverDueInvoices");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<OverDueInvoicesList> queryResult = new VList<OverDueInvoicesList>();
				while ((iterator).hasNext()) {

					OverDueInvoicesList overDueInvoicesList = new OverDueInvoicesList();
					object = (Object[]) iterator.next();

					overDueInvoicesList
							.setTransactionId((object[0] == null ? null
									: ((String) object[0])));
					overDueInvoicesList.setDueDate((object[1] == null ? null
							: (new ClientFinanceDate((Long) object[1]))));
					overDueInvoicesList.setCustomerName((String) object[2]);
					overDueInvoicesList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					overDueInvoicesList.setTotalAmount((Double) object[4]);
					overDueInvoicesList.setPayment((Double) object[5]);

					overDueInvoicesList.setBalanceDue((Double) object[6]);

					queryResult.add(overDueInvoicesList);
				}
				return new VList<OverDueInvoicesList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<PaymentsList> getPaymentsList() throws DAOException {
		List<PaymentsList> queryResult = new VList<PaymentsList>();
		try {
			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getPaymentsList");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();
			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();

				while ((iterator).hasNext()) {

					PaymentsList paymentsList = new PaymentsList();
					object = (Object[]) iterator.next();

					String name = (String) object[6];
					if (name != null) {
						paymentsList.setTransactionId((object[0] == null ? 0
								: ((Long) object[0])));
						paymentsList.setType((Integer) object[1]);
						paymentsList.setPaymentDate((object[2] == null ? null
								: (new ClientFinanceDate(((Long) object[2])))));
						paymentsList.setPaymentNumber((object[3] == null ? null
								: ((String) object[3])));
						paymentsList.setStatus((Integer) object[4]);
						paymentsList.setIssuedDate(new ClientFinanceDate(
								(Long) object[5]));
						paymentsList.setName(name);
						paymentsList.setPaymentMethodName((String) object[7]);
						paymentsList.setAmountPaid((Double) object[8]);
						paymentsList.setVoided((Boolean) object[9]);
						paymentsList
								.setPayBillType(object[10] != null ? (Integer) object[10]
										: 0);

						queryResult.add(paymentsList);
					}
				}
				return new VList<PaymentsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<Item> getPurchaseItems() throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();

			List<Item> list = (List<Item>) session
					.getNamedQuery("getItem.by.check.isIBuyThisItemisTrue");

			if (list != null) {
				return new VList<Item>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<Item> getSalesItems() throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();

			List<Item> list = (List<Item>) session
					.getNamedQuery("getItem.by.checkisISellThisItemisTrue");

			if (list != null) {
				return new VList<Item>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<PayBillTransactionList> getTransactionPayBills()
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getPayBillTransactionsList");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();

			List<PayBillTransactionList> queryResult = new VList<PayBillTransactionList>();

			query = session
					.getNamedQuery("getEntry.by.debitand.balanceDue.orderbyid");
			List<JournalEntry> openingBalanceEntries = query.list();

			for (JournalEntry je : openingBalanceEntries) {
				PayBillTransactionList payBillTransactionList = new PayBillTransactionList();
				payBillTransactionList.setTransactionId(je.getID());
				payBillTransactionList.setType(je.getType());
				payBillTransactionList.setDueDate(new ClientFinanceDate(je
						.getDate().getDate()));
				payBillTransactionList.setBillNumber(je.getNumber());
				payBillTransactionList.setOriginalAmount(je.getDebitTotal());
				payBillTransactionList.setAmountDue(je.getBalanceDue());
				queryResult.add(payBillTransactionList);
			}

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();

				while ((iterator).hasNext()) {

					PayBillTransactionList payBillTransactionList = new PayBillTransactionList();
					object = (Object[]) iterator.next();

					payBillTransactionList.setTransactionId((Long) object[0]);
					payBillTransactionList.setType((Integer) object[1]);
					payBillTransactionList.setDueDate(new ClientFinanceDate(
							(Long) object[2]));
					payBillTransactionList.setVendorName((String) object[3]);
					payBillTransactionList
							.setBillNumber((object[4] == null ? null
									: ((String) object[4])));

					payBillTransactionList
							.setOriginalAmount((Double) object[5]);
					payBillTransactionList.setAmountDue((Double) object[6]);
					payBillTransactionList.setPayment((Double) object[7]);
					payBillTransactionList.setPaymentMethod((String) object[8]);
					queryResult.add(payBillTransactionList);
				}

			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			return new VList<PayBillTransactionList>(queryResult);
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<PayBillTransactionList> getTransactionPayBills(
			final long vendorId) throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery(
					"getEntry.by.vendorId.creditand.balanceDue.orderbyid")
					.setParameter(0, vendorId);

			List<JournalEntry> openingBalanceEntries = query.list();

			List<PayBillTransactionList> queryResult = new VList<PayBillTransactionList>();

			for (JournalEntry je : openingBalanceEntries) {
				PayBillTransactionList payBillTransactionList = new PayBillTransactionList();
				payBillTransactionList.setTransactionId(je.getID());
				payBillTransactionList.setType(je.getType());
				payBillTransactionList.setDueDate(new ClientFinanceDate(je
						.getDate().getDate()));
				payBillTransactionList.setBillNumber(je.getNumber());
				payBillTransactionList.setOriginalAmount(je.getDebitTotal());
				payBillTransactionList.setAmountDue(je.getBalanceDue());
				payBillTransactionList.setDiscountDate(new ClientFinanceDate(je
						.getDate().getDate()));
				payBillTransactionList.setVendorName(je.getEntry().get(1)
						.getVendor().getName());
				queryResult.add(payBillTransactionList);
			}

			query = session
					.getNamedQuery("getPayBillTransactionsListForVendor")
					.setParameter("vendorId", vendorId);
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				while ((iterator).hasNext()) {

					PayBillTransactionList payBillTransactionList = new PayBillTransactionList();
					object = (Object[]) iterator.next();

					payBillTransactionList.setTransactionId((Long) object[0]);
					payBillTransactionList.setType((Integer) object[1]);
					payBillTransactionList.setDueDate(object[2] == null ? null
							: new ClientFinanceDate((Long) object[2]));
					payBillTransactionList.setVendorName((String) object[3]);
					payBillTransactionList
							.setBillNumber((object[4] == null ? null
									: ((String) object[4])));

					payBillTransactionList
							.setOriginalAmount((Double) object[5]);
					payBillTransactionList.setAmountDue((Double) object[6]);
					// payBillTransactionList.setPayment((Double) object[7]);
					payBillTransactionList.setPaymentMethod((String) object[8]);
					payBillTransactionList
							.setDiscountDate(object[9] == null ? null
									: new ClientFinanceDate((Long) object[9]));
					queryResult.add(payBillTransactionList);
				}
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			return new VList<PayBillTransactionList>(queryResult);
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<ReceivePaymentTransactionList> getTransactionReceivePayments(
			long customerId, long paymentDate1) throws DAOException,
			ParseException {
		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			FinanceDate paymentDate = null;
			paymentDate = new FinanceDate(paymentDate1);
			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery(
					"getReceivePaymentTransactionsListForCustomer")
					.setParameter("customerId", customerId);
			List list = query.list();

			// Query query = session.getNamedQuery(
			// "getReceivePaymentTransactionsListForCustomer");
			// query.setLong("customerId", customerId);
			// List list = query.list();

			List<ReceivePaymentTransactionList> queryResult = new VList<ReceivePaymentTransactionList>();

			query = session.getNamedQuery(
					"getEntry.by.customerId.debitand.balanceDue.orderbyid")
					.setParameter(0, customerId);

			List<JournalEntry> openingBalanceEntries = query.list();

			for (JournalEntry je : openingBalanceEntries) {
				ReceivePaymentTransactionList receivePaymentTransactionList = new ReceivePaymentTransactionList();
				receivePaymentTransactionList.setTransactionId(je.getID());
				receivePaymentTransactionList.setType(je.getType());
				receivePaymentTransactionList.setDueDate(new ClientFinanceDate(
						je.getDate().getDate()));
				receivePaymentTransactionList.setNumber(je.getNumber());
				receivePaymentTransactionList.setInvoiceAmount(je
						.getDebitTotal());
				receivePaymentTransactionList.setAmountDue(je.getBalanceDue());
				receivePaymentTransactionList
						.setDiscountDate(new ClientFinanceDate(je.getDate()
								.getDate()));
				queryResult.add(receivePaymentTransactionList);
			}

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();

				while ((iterator).hasNext()) {

					ReceivePaymentTransactionList receivePaymentTransactionList = new ReceivePaymentTransactionList();
					object = (Object[]) iterator.next();

					receivePaymentTransactionList
							.setTransactionId((Long) object[0]);
					receivePaymentTransactionList.setType((Integer) object[1]);
					receivePaymentTransactionList
							.setDueDate(object[2] == null ? null
									: new ClientFinanceDate((Long) object[2]));
					receivePaymentTransactionList
							.setNumber((object[3] == null ? null
									: ((String) object[3])));

					// int discount = (object[8] != null && !paymentDate
					// .after((Long) object[6]))
					// ? ((Integer) object[8])
					// : 0;

					double invoicedAmount = (Double) object[4];
					receivePaymentTransactionList
							.setInvoiceAmount(invoicedAmount);

					double amountDue = (Double) object[5];
					receivePaymentTransactionList.setAmountDue(amountDue);
					receivePaymentTransactionList
							.setDiscountDate(object[6] == null ? null
									: new ClientFinanceDate((Long) object[6]));
					receivePaymentTransactionList
							.setPayment((Double) object[7]);
					queryResult.add(receivePaymentTransactionList);
				}
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			return new VList<ReceivePaymentTransactionList>(queryResult);
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<CreditsAndPayments> getVendorCreditsAndPayments(long vendor)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery(
				"getcreditandPayments.by.Payieeid.and.balance").setParameter(0,
				vendor);
		List<CreditsAndPayments> list = query.list();

		// if (list != null) {
		return new VList<CreditsAndPayments>(list);
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));

	}

	@Override
	public VList<PaymentsList> getVendorPaymentsList() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getVendorPaymentsList");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				VList<PaymentsList> queryResult = new VList<PaymentsList>();
				while ((iterator).hasNext()) {

					PaymentsList vendorPaymentsList = new PaymentsList();
					object = (Object[]) iterator.next();

					String name = (String) object[6];
					if (name != null) {

						vendorPaymentsList
								.setTransactionId((object[0] == null ? null
										: ((Long) object[0])));
						vendorPaymentsList.setType((Integer) object[1]);
						vendorPaymentsList
								.setPaymentDate(new ClientFinanceDate(
										(Long) object[2]));
						vendorPaymentsList
								.setPaymentNumber((object[3] == null ? null
										: ((String) object[3])));
						vendorPaymentsList.setStatus((Integer) object[4]);
						vendorPaymentsList.setIssuedDate(new ClientFinanceDate(
								(Long) object[5]));
						vendorPaymentsList.setName(name);
						vendorPaymentsList
								.setPaymentMethodName((String) object[7]);
						vendorPaymentsList.setAmountPaid((Double) object[8]);
						vendorPaymentsList
								.setPayBillType((Integer) object[9] != null ? (Integer) object[9]
										: 0);
						vendorPaymentsList
								.setVoided(object[10] != null ? (Boolean) object[10]
										: false);

						queryResult.add(vendorPaymentsList);
					}
				}
				return new VList<PaymentsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public boolean isSalesTaxPayableAccount(long accountId) throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getAccount.by.id.and.type")
					.setParameter(0, accountId)
					.setParameter(1, Account.TYPE_OTHER_CURRENT_LIABILITY);
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

	@Override
	public boolean isSalesTaxPayableAccountByName(String accountName)
			throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getAccount.by.name.and.type")
					.setParameter(0, accountName)
					.setParameter(1, Account.TYPE_OTHER_CURRENT_LIABILITY);
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

	@Override
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

	@Override
	public boolean canVoidOrEdit(long invoiceOrVendorBillId)
			throws DAOException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery(
				"get.canVoidOrEditTransaction.from.transactionID")
				.setParameter(0, invoiceOrVendorBillId);
		List list = query.list();

		return (Boolean) list.iterator().next();
	}

	@Override
	public VList<BillsList> getLatestBills() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestBills");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<BillsList> queryResult = new VList<BillsList>();
				while ((iterator).hasNext()) {

					BillsList billsList = new BillsList();
					object = (Object[]) iterator.next();

					billsList.setTransactionId((Long) object[0]);
					billsList.setType((Integer) object[1]);
					billsList
							.setDueDate(new ClientFinanceDate((Long) object[2]));
					billsList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					billsList.setVendorName((String) object[4]);
					billsList.setOriginalAmount((Double) object[5]);
					billsList.setBalance((Double) object[6]);

					queryResult.add(billsList);
				}
				return new VList<BillsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<CashPurchase> getLatestCashPurchases() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestCashPurchases");
			// FIXME ::: check the sql query and change it to hql query if
			// required
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<CashPurchase> list = new VList<CashPurchase>();
			while (iterator.hasNext()) {

				object = (Object[]) iterator.next();
				CashPurchase cashPurchase = new CashPurchase();
				// cashPurchase.setID((object[0] == null ? null
				// : ((Long) object[0])));
				cashPurchase.setDate((new FinanceDate((Long) object[1])));
				cashPurchase.setVendor(object[2] != null ? (Vendor) session
						.get(Vendor.class, ((Long) object[2])) : null);
				cashPurchase.setTotal((Double) object[3]);
				// cashPurchase.setID((object[4] == null ? null
				// : ((String) object[4])));
				list.add(cashPurchase);
			}
			if (list != null) {
				return new VList<CashPurchase>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<CashSales> getLatestCashSales() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestCashSales");
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<CashSales> list = new VList<CashSales>();
			while (iterator.hasNext()) {

				object = (Object[]) iterator.next();
				CashSales cashSale = new CashSales();
				// cashSale.setID((object[0] == null ? null : ((Long)
				// object[0])));
				cashSale.setDate((new FinanceDate((Long) object[1])));
				cashSale.setCustomer(object[2] != null ? (Customer) session
						.get(Customer.class, ((Long) object[2])) : null);
				cashSale.setSalesPerson(object[3] != null ? (SalesPerson) session
						.get(Vendor.class, ((Long) object[3])) : null);
				cashSale.setTotal((Double) object[4]);
				// cashSale.setID((object[5] == null ? null : ((String)
				// object[5])));
				list.add(cashSale);
			}
			if (list != null) {
				return new VList<CashSales>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<WriteCheck> getLatestChecks() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestChecks");
			// FIXME ::: check the sql query and change it to hql query if
			// required try to fix all sql queries
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<WriteCheck> list = new VList<WriteCheck>();
			while (iterator.hasNext()) {

				object = (Object[]) iterator.next();
				WriteCheck writeCheck = new WriteCheck();
				// writeCheck
				// .setID((object[0] == null ? null : ((Long) object[0])));
				writeCheck.setDate((new FinanceDate((Long) object[1])));
				writeCheck.setPayToType((Integer) object[2]);
				writeCheck.setBalance((Double) object[3]);
				writeCheck.setCustomer(object[4] != null ? (Customer) session
						.get(Customer.class, ((Long) object[4])) : null);
				writeCheck.setVendor(object[5] != null ? (Vendor) session.get(
						Vendor.class, ((Long) object[5])) : null);
				writeCheck.setTaxAgency(object[6] != null ? (TAXAgency) session
						.get(TAXAgency.class, ((Long) object[5])) : null);
				writeCheck.setAmount((Double) object[7]);
				// writeCheck.setID((object[8] == null ? null
				// : ((String) object[8])));
				list.add(writeCheck);
			}
			if (list != null) {
				return new VList<WriteCheck>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<CustomerRefund> getLatestCustomerRefunds() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestCustomerRefunds");
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<CustomerRefund> list = new VList<CustomerRefund>();
			while (iterator.hasNext()) {

				object = (Object[]) iterator.next();
				CustomerRefund customerRefund = new CustomerRefund();
				// customerRefund.setID((object[0] == null ? null
				// : ((Long) object[0])));
				customerRefund.setDate(new FinanceDate((Long) object[1]));
				customerRefund.setPayTo(object[2] != null ? (Customer) session
						.get(Customer.class, ((Long) object[2])) : null);
				customerRefund.setTotal((Double) object[3]);
				customerRefund.setBalanceDue((Double) object[3]);
				// customerRefund.setID((object[4] == null ? null
				// : ((String) object[4])));

				list.add(customerRefund);
			}
			if (list != null) {
				return new VList<CustomerRefund>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Customer> getLatestCustomers() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestCustomers");
			List list2 = query.list();

			Object[] object = null;
			Iterator iterator = list2.iterator();
			List<Customer> list = new VList<Customer>();
			while (iterator.hasNext()) {
				object = (Object[]) iterator.next();
				Customer customer = new Customer();
				// customer.setID((object[0] == null ? null : ((Long)
				// object[0])));
				customer.setName((String) object[1]);
				customer.setDate(new FinanceDate((Long) object[2]));
				// customer.setID((String) object[3]);
				list.add(customer);
			}
			if (list != null) {
				return new VList<Customer>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<MakeDeposit> getLatestDeposits() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestDeposits");
			List list2 = query.list();

			Object[] object = null;
			Iterator iterator = list2.iterator();
			List<MakeDeposit> list = new VList<MakeDeposit>();
			while (iterator.hasNext()) {
				object = (Object[]) iterator.next();
				MakeDeposit makeDeposit = new MakeDeposit();
				// makeDeposit.setID((object[0] == null ? null
				// : ((Long) object[0])));
				makeDeposit.setDepositIn(object[1] == null ? null
						: (Account) session.get(Account.class,
								((Long) object[1])));
				makeDeposit.setMemo((String) object[2]);
				makeDeposit.setTotal((object[3] == null ? null
						: ((Double) object[3])));
				makeDeposit.setCashBackAccount(object[4] == null ? null
						: (Account) session.get(Account.class,
								((Long) object[4])));
				makeDeposit.setCashBackMemo((String) object[5]);
				makeDeposit.setCashBackAmount((object[6] == null ? null
						: ((Double) object[6])));
				// makeDeposit.setID((String) object[7]);
				list.add(makeDeposit);

			}
			if (list != null) {
				return new VList<MakeDeposit>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<TransferFund> getLatestFundsTransfer() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestTransferFunds");
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<TransferFund> list = new VList<TransferFund>();
			while (iterator.hasNext()) {

				object = (Object[]) iterator.next();
				TransferFund transferFund = new TransferFund();
				// transferFund.setID((object[0] == null ? null
				// : ((Long) object[0])));
				transferFund.setDate(new FinanceDate((Long) object[1]));
				transferFund
						.setTransferFrom(object[2] != null ? (Account) session
								.get(Account.class, ((Long) object[2])) : null);
				transferFund
						.setTransferTo(object[3] != null ? (Account) session
								.get(Account.class, ((Long) object[3])) : null);
				transferFund.setTotal((Double) object[4]);
				// transferFund.setID((object[5] == null ? null
				// : ((String) object[5])));
				list.add(transferFund);
			}
			if (list != null) {
				return new VList<TransferFund>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Item> getLatestItems() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestItems");
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<Item> list = new VList<Item>();
			while (iterator.hasNext()) {
				object = (Object[]) iterator.next();
				Item item = new Item();
				// item.setID((object[0] == null ? null : ((Long) object[0])));
				item.setName((String) object[1]);
				item.setType((Integer) object[2]);
				item.setSalesPrice((Double) object[3]);
				// item.setID((String) object[4]);
				list.add(item);
			}
			if (list != null) {
				return new VList<Item>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<PaymentsList> getLatestPayments() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestPayments");
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<PaymentsList> queryResult = new VList<PaymentsList>();
				while ((iterator).hasNext()) {

					PaymentsList paymentsList = new PaymentsList();
					object = (Object[]) iterator.next();

					paymentsList.setTransactionId((object[0] == null ? null
							: ((Long) object[0])));
					paymentsList.setType((Integer) object[1]);
					paymentsList.setPaymentDate(new ClientFinanceDate(
							(Long) object[2]));
					paymentsList.setPaymentNumber((object[3] == null ? null
							: ((String) object[3])));
					paymentsList.setStatus((Integer) object[4]);
					paymentsList.setIssuedDate(new ClientFinanceDate(
							(Long) object[5]));
					paymentsList.setName((String) object[6]);
					paymentsList.setPaymentMethodName((String) object[7]);
					paymentsList.setAmountPaid((Double) object[8]);

					queryResult.add(paymentsList);
				}
				return new VList<PaymentsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Vendor> getLatestVendors() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestVendors");
			List list2 = query.list();

			Object object[] = null;
			Iterator iterator = list2.iterator();
			List<Vendor> list = new VList<Vendor>();
			while (iterator.hasNext()) {
				while (iterator.hasNext()) {
					object = (Object[]) iterator.next();
					Vendor vendor = new Vendor();
					// vendor.setID((object[0] == null ? null : ((Long)
					// object[0])));
					vendor.setName((String) object[1]);
					vendor.setDate(new FinanceDate((Long) object[2]));
					// vendor.setID((String) object[3]);
					list.add(vendor);
				}
			}
			if (list != null) {
				return new VList<Vendor>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public Long getNextCheckNumber(long accountId) throws DAOException {

		// try {
		// Session session = HibernateUtil.getCurrentSession();
		// long account = getLongIdForGivenid(AccounterCoreType.ACCOUNT,
		// account2);
		// Query query = session
		// .createQuery(
		// "from com.vimukti.accounter.core.WriteCheck wc  where wc.bankAccount.id = ?")
		// .setParameter(0, account);
		// List list1 = query.list();
		// if (list1.size() <= 0) {
		// return 1L;
		// }
		//
		// query = session
		// .createQuery(
		// "select max(wc.checkNumber) from com.vimukti.accounter.core.WriteCheck wc where wc.bankAccount.id = ? ")
		// .setParameter(0, account);
		// List list = query.list();
		// if (list != null && list.size() > 0) {
		// Object obj = list.get(0);
		// return (obj != null)
		// ? (Long) obj != -1 ? ((Long) obj) + 1 : 1
		// : 1;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));
		// } catch (DAOException e) {
		// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		// }
		return null;
	}

	@Override
	public Long getNextNominalCode(int accountType) throws DAOException {

		try {
			int accountSubBaseType = Utility.getAccountSubBaseType(accountType);
			Session session = HibernateUtil.getCurrentSession();
			Company company = Company.getCompany();
			Integer range[] = company.getNominalCodeRange(accountSubBaseType);

			Query query = session.getNamedQuery(
					"getNextNominalCodeForGivenAccountType").setParameter(
					"subBaseType", accountSubBaseType);
			List list = query.list();
			Long nextNominalCode = (list.size() > 0) ? ((Long) list.get(0)) + 1
					: range[0];
			if (nextNominalCode > range[1]) {
				throw (new DAOException(DAOException.RANGE_EXCEED_EXCEPTION,
						null,
						"Nominal Code Range exceeded. Please delete the existing accounts of this type"));
			}

			if (list != null) {
				return nextNominalCode;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<Account> getTaxAgencyAccounts() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getAccount")
					.setParameter(0, Account.TYPE_INCOME)
					.setParameter(1, Account.TYPE_EXPENSE)
					.setParameter(2, Account.TYPE_COST_OF_GOODS_SOLD);
			List<Account> list = query.list();

			if (list != null) {
				return new VList<Account>(list);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public TransactionMakeDeposit getTransactionMakeDeposit(
			long transactionMakeDepositId) throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery(
					"getTransactionMakeDeposit.by.id").setParameter(0,
					transactionMakeDepositId);
			List list = query.list();

			if (list != null) {
				return (TransactionMakeDeposit) list.get(0);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<ReceivePaymentsList> getReceivePaymentsList()
			throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getReceivePaymentsList");
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				VList<ReceivePaymentsList> queryResult = new VList<ReceivePaymentsList>();
				while ((iterator).hasNext()) {

					ReceivePaymentsList receivePaymentsList = new ReceivePaymentsList();
					object = (Object[]) iterator.next();

					receivePaymentsList
							.setTransactionId((object[0] == null ? null
									: ((Long) object[0])));
					receivePaymentsList.setType((Integer) object[1]);
					receivePaymentsList.setPaymentDate(new ClientFinanceDate(
							object[2] == null ? null : ((Long) object[2])));
					receivePaymentsList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					receivePaymentsList.setCustomerName((String) object[4]);
					receivePaymentsList
							.setPaymentMethodName((String) object[5]);
					receivePaymentsList.setAmountPaid((Double) object[6]);
					receivePaymentsList.setVoided((Boolean) object[7]);
					receivePaymentsList.setStatus((Integer) object[8]);

					queryResult.add(receivePaymentsList);
				}
				return queryResult;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	public VList<ClientTransactionMakeDeposit> getTransactionMakeDeposits()
			throws DAOException {
		List<ClientTransactionMakeDeposit> transactionMakeDepositsList = new VList<ClientTransactionMakeDeposit>();

		Session session = HibernateUtil.getCurrentSession();

		Query query = session
				.getNamedQuery("getTransactionMakeDeposit.by.checking.isDepositedandisVoid");

		List<TransactionMakeDepositEntries> listing = query.list();

		return new VList<ClientTransactionMakeDeposit>(query.list());

		// Query query = session
		// .createQuery(
		// "from com.vimukti.accounter.core.AccountTransaction at where at.account.name = 'Un Deposited Funds' and "
		// +
		// "at.transaction.isDeposited = 'false' and at.transaction.isVoid = 'false' "
		// );
		//
		// List<AccountTransaction> listing = query.list();

		// if (listing != null && listing.size() > 0) {
		//
		// for (TransactionMakeDepositEntries transactionMakeDepositEntry :
		// listing) {
		//
		// MakeDepositTransactionsList makeDepositTransactionsList = new
		// MakeDepositTransactionsList(
		// ((ClientTransaction) new ClientConvertUtil()
		// .toClientObject(
		// transactionMakeDepositEntry.getTransaction(),
		// Util
		// .getClientEqualentClass(transactionMakeDepositEntry
		// .getTransaction()
		// .getClass()))),
		// transactionMakeDepositEntry.getAmount());
		//
		// Payee payee = transactionMakeDepositEntry.getTransaction()
		// .getInvolvedPayee();
		//
		// makeDepositTransactionsList.setCashAccountId(
		// transactionMakeDepositEntry
		// .getAccount().getID());
		// makeDepositTransactionsList.setPaymentMethod(
		// transactionMakeDepositEntry
		// .getTransaction().getPaymentMethod());
		// makeDepositTransactionsList.setReference(transactionMakeDepositEntry
		// .getTransaction().getReference());
		// makeDepositTransactionsList.setPayeeName(transactionMakeDepositEntry
		// .getAccount().getName());
		// if (payee != null) {
		// makeDepositTransactionsList.setPayeeName(payee.getName());
		// makeDepositTransactionsList.setType(payee.getType());
		// if (payee.getType() == Payee.TYPE_CUSTOMER) {
		// makeDepositTransactionsList
		// .setType(Payee.TYPE_CUSTOMER);
		// } else if (payee.getType() == Payee.TYPE_VENDOR) {
		// makeDepositTransactionsList.setType(Payee.TYPE_VENDOR);
		// } else if (payee.getType() == Payee.TYPE_TAX_AGENCY) {
		// makeDepositTransactionsList
		// .setType(Payee.TYPE_TAX_AGENCY);
		// }
		//
		// transactionMakeDepositsList
		// .add(makeDepositTransactionsList);
		// }
		// }
		// }
		// return transactionMakeDepositsList;

	}

	// private void createMakeDepositTransaction(List list,
	// List<MakeDepositTransactionsList> transactionMakeDepositsList)
	// throws DAOException {
	// Object object[] = null;
	//
	// Iterator i = list.iterator();
	// while (i.hasNext()) {
	//
	// object = (Object[]) i.next();
	// MakeDepositTransactionsList makeDepositTransaction = new
	// MakeDepositTransactionsList();
	// String transactionId = (object[0] == null ? null
	// : ((String) object[0]));
	// Integer transactionType = (Integer) object[1];
	// makeDepositTransaction.setTransactionId(transactionId);
	// makeDepositTransaction.setTransactionType(transactionType);
	// makeDepositTransaction.setDate((Long) object[2]);
	// makeDepositTransaction.setNumber((object[3] == null ? null
	// : ((Long) object[3])));
	// makeDepositTransaction.setPaymentMethod((String) object[4]);
	// makeDepositTransaction.setPayeeName((String) object[5]);
	//
	// makeDepositTransaction.setReference((String) object[6]);
	//
	// makeDepositTransaction.setCashAccountId((object[7] == null ? null
	// : ((String) object[7])));
	// makeDepositTransaction
	// .setAmount(object[8] != null ? (Double) object[8] : 0.0);
	// makeDepositTransaction.setType(object[9] == null ? null
	// : (Integer) object[9]);
	// if (!(makeDepositTransaction.getAmount()).equals(0.0)) {
	// transactionMakeDepositsList.add(makeDepositTransaction);
	// }
	// }
	// }

	@Override
	public VList<InvoicesList> getLatestInvoices() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestInvoices");
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<InvoicesList> queryResult = new VList<InvoicesList>();
				while ((iterator).hasNext()) {

					InvoicesList invoicesList = new InvoicesList();
					object = (Object[]) iterator.next();

					long transactionNumber = (object[0] == null ? 0
							: ((Long) object[0]));
					invoicesList.setTransactionId(transactionNumber);
					invoicesList.setType((Integer) object[1]);
					invoicesList
							.setDate(new ClientFinanceDate((Long) object[2]));
					invoicesList.setNumber((object[3] == null ? null
							: ((String) object[3])));
					invoicesList.setCustomerName((String) object[4]);
					invoicesList.setNetAmount((Double) object[5]);
					invoicesList.setDueDate(new ClientFinanceDate(
							(Long) object[6]));
					invoicesList.setTotalPrice((Double) object[7]);
					invoicesList.setBalance((Double) object[8]);

					queryResult.add(invoicesList);
				}
				return new VList<InvoicesList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Item> getLatestPurchaseItems() throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getLatestPurchaseItems");
		List list2 = query.list();

		Object object[] = null;
		Iterator iterator = list2.iterator();
		List<Item> list = new VList<Item>();
		while (iterator.hasNext()) {
			object = (Object[]) iterator.next();
			Item item = new Item();
			item.setName((String) object[1]);
			item.setType((Integer) object[2]);
			item.setSalesPrice((Double) object[3]);
			list.add(item);
		}
		if (list != null) {
			return new VList<Item>(list);
		} else
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));
	}

	@Override
	public VList<ReceivePayment> getLatestReceivePayments() throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getLatestReceivePayments");
		List list2 = query.list();

		Object object[] = null;
		Iterator iterator = list2.iterator();
		List<ReceivePayment> list = new VList<ReceivePayment>();
		while (iterator.hasNext()) {
			object = (Object[]) iterator.next();
			ReceivePayment receivePayment = new ReceivePayment();
			// receivePayment
			// .setID((object[0] == null ? null : ((Long) object[0])));
			receivePayment.setDate(new FinanceDate((Long) object[1]));
			receivePayment.setCustomer(object[2] != null ? (Customer) session
					.get(Customer.class, ((Long) object[2])) : null);
			receivePayment.setAmount((Double) object[3]);
			// receivePayment.setID((String) object[4]);

			list.add(receivePayment);
		}
		if (list != null) {
			return new VList<ReceivePayment>(list);
		} else
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));
	}

	@Override
	public VList<Item> getLatestSalesItems() throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getLatestSalesItems");
		List list2 = query.list();

		Object object[] = null;
		Iterator iterator = list2.iterator();
		List<Item> list = new VList<Item>();
		while (iterator.hasNext()) {
			object = (Object[]) iterator.next();
			Item item = new Item();
			// item.setID((object[0] == null ? null : ((Long) object[0])));
			item.setName((String) object[1]);
			item.setType((Integer) object[2]);
			item.setSalesPrice((Double) object[3]);
			// item.setID((String) object[4]);

			list.add(item);
		}
		if (list != null) {
			return new VList<Item>(list);
		} else
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));
	}

	@Override
	public VList<PaymentsList> getLatestVendorPayments() throws DAOException {
		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("getLatestVendorPayments");
			List list = query.list();

			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<PaymentsList> queryResult = new VList<PaymentsList>();
				while ((iterator).hasNext()) {

					PaymentsList vendorPaymentsList = new PaymentsList();
					object = (Object[]) iterator.next();

					vendorPaymentsList
							.setTransactionId((object[0] == null ? null
									: ((Long) object[0])));
					vendorPaymentsList.setType((Integer) object[1]);
					vendorPaymentsList.setPaymentDate(new ClientFinanceDate(
							(Long) object[2]));
					vendorPaymentsList
							.setPaymentNumber((object[3] == null ? null
									: ((String) object[3])));
					vendorPaymentsList.setStatus((Integer) object[4]);
					vendorPaymentsList.setIssuedDate(new ClientFinanceDate(
							(Long) object[5]));
					vendorPaymentsList.setName((String) object[6]);
					vendorPaymentsList.setPaymentMethodName((String) object[7]);
					vendorPaymentsList.setAmountPaid((Double) object[8]);

					queryResult.add(vendorPaymentsList);
				}
				return new VList<PaymentsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	public void test() throws Exception {
		// HibernateTemplate template = getHibernateTemplate();
		//
		// // List list = template.findByNamedQueryAndNamedParam(
		// // "test", "companyId", company);
		// List list = template.findByNamedQuery("test");
		// if (list != null) {
		// Iterator i = list.iterator();
		// Object o[] = null;
		// while (i.hasNext()) {
		// o = (Object[]) i.next();
		// String accName = (String) o[0];
		// String taName = (String) o[1];
		//
		// }
		// }
	}

	@Override
	public VList<PaySalesTaxEntries> getTransactionPaySalesTaxEntriesList(
			long billsDueOnOrBefore) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session
				.getNamedQuery("getTAXRateCalculation.checkingby.salesLiabilityAccountName.taxDue");

		List<TAXRateCalculation> list = query.list();
		List<PaySalesTaxEntries> resultPaySalesTaxEntries = new VList<PaySalesTaxEntries>();
		FinanceDate financeDate = new FinanceDate(billsDueOnOrBefore);

		if (list != null) {

			for (TAXRateCalculation taxRateCalculation : list) {

				PaySalesTaxEntries paySalesTaxList = new PaySalesTaxEntries();
				paySalesTaxList.setBalance(taxRateCalculation.getTaxDue());
				paySalesTaxList.setAmount(taxRateCalculation.getVatAmount());
				paySalesTaxList.setTaxItem(taxRateCalculation.getTaxItem());
				paySalesTaxList.setTaxAgency(taxRateCalculation.getTaxAgency());
				paySalesTaxList.setTransaction(taxRateCalculation
						.getTransactionItem().getTransaction());
				paySalesTaxList.setTransactionDate(taxRateCalculation
						.getTransactionDate());
				paySalesTaxList.setTaxRateCalculation(taxRateCalculation);

				preParePaySalesTaxEntriesUsingPaymentTerms(
						resultPaySalesTaxEntries, paySalesTaxList, financeDate);
			}
		}

		query = session
				.getNamedQuery("getTAXAdjustment.checkingby.salesLiabilityAccount.nameandbalanceDue");

		List<TAXAdjustment> taxAdjustments = query.list();
		if (list != null) {

			for (TAXAdjustment taxAdjustment : taxAdjustments) {

				PaySalesTaxEntries paySalesTaxList = new PaySalesTaxEntries();
				paySalesTaxList.setBalance(taxAdjustment.getJournalEntry()
						.getBalanceDue());
				paySalesTaxList.setAmount(taxAdjustment.getTotal());
				paySalesTaxList.setTaxAgency(taxAdjustment.getTaxAgency());
				paySalesTaxList.setTransaction(taxAdjustment);
				paySalesTaxList.setTransactionDate(taxAdjustment.getDate());
				paySalesTaxList.setTaxAdjustment(taxAdjustment);

				preParePaySalesTaxEntriesUsingPaymentTerms(
						resultPaySalesTaxEntries, paySalesTaxList, financeDate);
			}
		}

		// List<PaySalesTaxEntries> paySalesTaxEntries = query.list();
		// FinanceDate financeDate = new FinanceDate(billsDueOnOrBefore);
		//
		// List<PaySalesTaxEntries> resultPaySalesTaxEntries = new
		// VList<PaySalesTaxEntries>();
		// for (PaySalesTaxEntries pst : paySalesTaxEntries) {
		//
		// preParePaySalesTaxEntriesUsingPaymentTerms(
		// resultPaySalesTaxEntries, pst, financeDate, pst
		// .getTaxAgency());
		// }

		return new VList<PaySalesTaxEntries>(resultPaySalesTaxEntries);
	}

	private void preParePaySalesTaxEntriesUsingPaymentTerms(
			List<PaySalesTaxEntries> resultPaySalesTaxEntries,
			PaySalesTaxEntries pst, FinanceDate financeDate) {

		Calendar dueCalendar = Calendar.getInstance();
		dueCalendar.setTime(financeDate.getAsDateObject());
		PaymentTerms paymentTerm = pst.getTaxAgency().getPaymentTerm();
		if (paymentTerm.getDue() == 0) {
			dueCalendar.add(Calendar.DAY_OF_MONTH, paymentTerm.getDueDays());
			if (new FinanceDate(dueCalendar.getTime()).compareTo(pst
					.getTransactionDate()) >= 0) {
				resultPaySalesTaxEntries.add(pst);
			}
		} else {

			Calendar transCal = Calendar.getInstance();
			transCal.setTime(pst.getTransactionDate().getAsDateObject());
			transCal.set(Calendar.DAY_OF_MONTH, 01);

			switch (paymentTerm.getDue()) {
			case PaymentTerms.DUE_CURRENT_MONTH:
				verifyCalendarDates(transCal, dueCalendar,
						resultPaySalesTaxEntries, pst);
				break;
			case PaymentTerms.DUE_CURRENT_QUARTER:
				verifyQuarterRange(transCal);
				verifyCalendarDates(transCal, dueCalendar,
						resultPaySalesTaxEntries, pst);
				break;
			case PaymentTerms.DUE_CURRENT_HALF_YEAR:
				int month = transCal.get(Calendar.MONTH);
				month++;
				if (month <= 6) {
					transCal.set(Calendar.MONTH, 6);
				} else {
					transCal.set(Calendar.MONTH, 12);
				}
				verifyCalendarDates(transCal, dueCalendar,
						resultPaySalesTaxEntries, pst);
				break;
			case PaymentTerms.DUE_CURRENT_YEAR:
				transCal.set(Calendar.MONTH, 12);
				verifyCalendarDates(transCal, dueCalendar,
						resultPaySalesTaxEntries, pst);
				break;
			}

		}

		// return resultPaySalesTaxEntries;
	}

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

	private void verifyCalendarDates(Calendar transCal, Calendar dueCalendar,
			List<PaySalesTaxEntries> resultPaySalesTaxEntries,
			PaySalesTaxEntries paySalesTaxEntries) {
		transCal.add(Calendar.MONTH, 01);
		if (transCal.getTime().compareTo(dueCalendar.getTime()) >= 0) {
			resultPaySalesTaxEntries.add(paySalesTaxEntries);
		}
	}

	@Override
	public VList<EstimatesAndSalesOrdersList> getEstimatesAndSalesOrdersList(
			long customerId) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getEstimatesAndSalesOrdersList")
				.setParameter("customerId", customerId);

		List list = query.list();
		List<EstimatesAndSalesOrdersList> esl = new VList<EstimatesAndSalesOrdersList>();

		for (int i = 0; i < list.size(); i++) {

			Object[] obj = (Object[]) list.get(i);
			// for (int j = 0; j < obj.length; j++)
			{
				EstimatesAndSalesOrdersList el = new EstimatesAndSalesOrdersList();
				el.setTransactionId(((Long) obj[0]).longValue());
				el.setType(((Integer) obj[1]).intValue());
				el.setTransactionNumber(((String) obj[2]));
				el.setTotal(((Double) obj[3]).doubleValue());
				el.setDate(new ClientFinanceDate((Long) obj[4]));
				el.setCustomerName((String) obj[5]);
				el.setRemainingTotal(((Double) obj[6]).doubleValue());
				esl.add(el);
			}
		}

		return new VList<EstimatesAndSalesOrdersList>(esl);
	}

	@Override
	public VList<PurchaseOrdersAndItemReceiptsList> getPurchasesAndItemReceiptsList(
			long vendorId) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getPurchasesAndItemReceipts")
				.setParameter("vendorId", vendorId);

		List list = query.list();
		List<PurchaseOrdersAndItemReceiptsList> pil = new VList<PurchaseOrdersAndItemReceiptsList>();

		for (int i = 0; i < list.size(); i++) {

			Object[] obj = (Object[]) list.get(i);
			// for (int j = 0; j < obj.length; j++)
			{
				PurchaseOrdersAndItemReceiptsList el = new PurchaseOrdersAndItemReceiptsList();
				el.setTransactionId((Long) obj[0]);
				el.setType(((Integer) obj[1]).intValue());
				el.setTransactionNumber(((String) obj[2]));
				el.setTotal(((Double) obj[3]).doubleValue());
				el.setDate(new ClientFinanceDate((Long) obj[4]));
				el.setVendorName((String) obj[5]);
				el.setRemainingTotal(((Double) obj[6]).doubleValue());
				pil.add(el);
			}

		}

		return new VList<PurchaseOrdersAndItemReceiptsList>(pil);
	}

	public VList<SalesOrdersList> getSalesOrdersList() throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getSalesOrdersList");

		List list = query.list();
		List<SalesOrdersList> esl = new VList<SalesOrdersList>();

		for (int i = 0; i < list.size(); i++) {

			Object[] obj = (Object[]) list.get(i);
			// for (int j = 0; j < obj.length; j++)
			{
				SalesOrdersList el = new SalesOrdersList();
				el.setTransactionId((Long) obj[0]);
				el.setType(((Integer) obj[1]).intValue());
				el.setNumber(((String) obj[2]));
				el.setTotal(((Double) obj[3]).doubleValue());
				el.setDate(new ClientFinanceDate((Long) obj[4]));
				el.setCustomerName((String) obj[5]);
				el.setPhone((String) obj[6]);
				el.setDueDate(new ClientFinanceDate((Long) obj[7]));
				el.setStatus((Integer) obj[8]);
				esl.add(el);
			}
		}

		return new VList<SalesOrdersList>(esl);
	}

	public VList<PurchaseOrdersList> getPurchaseOrdersList()
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getPurchaseOrdersList");
		// FIXME ::: check the sql query and change it to hql query if required
		List list = query.list();
		List<PurchaseOrdersList> pil = new VList<PurchaseOrdersList>();

		for (int i = 0; i < list.size(); i++) {

			Object[] obj = (Object[]) list.get(i);
			// for (int j = 0; j < obj.length; j++)
			{
				PurchaseOrdersList el = new PurchaseOrdersList();
				el.setTransactionId(((Long) obj[0]).longValue());
				el.setType(((Integer) obj[1]).intValue());
				el.setNumber(((String) obj[2]));
				el.setPurchasePrice(((Double) obj[3]).doubleValue());
				el.setDate(new ClientFinanceDate((Long) obj[4]));
				el.setVendorName((String) obj[5]);
				el.setPhone((String) obj[6]);
				el.setDeliveryDate(new ClientFinanceDate((Long) obj[7]));
				el.setStatus((Integer) obj[8]);
				pil.add(el);
			}
		}

		return new VList<PurchaseOrdersList>(pil);
	}

	@Override
	public VList<PurchaseOrdersList> getNotReceivedPurchaseOrdersList(
			long vendorID) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getNotReceivedPurchaseOrdersList")
				.setParameter("vendorId", vendorID);
		// FIXME ::: check the sql query and change it to hql query if required

		List list = query.list();
		List<PurchaseOrdersList> pil = new VList<PurchaseOrdersList>();

		for (int i = 0; i < list.size(); i++) {

			Object[] obj = (Object[]) list.get(i);
			// for (int j = 0; j < obj.length; j++)
			{
				PurchaseOrdersList el = new PurchaseOrdersList();
				el.setTransactionId((Long) obj[0]);
				el.setType(((Integer) obj[1]).intValue());
				el.setNumber(((String) obj[2]));
				el.setPurchasePrice(((Double) obj[3]).doubleValue());
				el.setDate(new ClientFinanceDate((Long) obj[4]));
				el.setVendorName((String) obj[5]);
				pil.add(el);
			}
		}

		return new VList<PurchaseOrdersList>(pil);
	}

	@Override
	public VList<FixedAssetList> getFixedAssets(int status) throws DAOException {

		List<FixedAssetList> fal = new VList<FixedAssetList>();

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getFixedAsset.by.status")
				.setParameter("status", status);

		List<FixedAsset> list = query.list();
		for (FixedAsset fixedAsset : list) {

			FixedAssetList fixedAssetList = new FixedAssetList();
			fixedAssetList.setID(fixedAsset.getID());
			fixedAssetList
					.setAssetAccount(fixedAsset.getAssetAccount().getID());
			fixedAssetList.setAssetNumber(fixedAsset.getAssetNumber());
			fixedAssetList.setBookValue(fixedAsset.getBookValue());
			fixedAssetList.setName(fixedAsset.getName());
			fixedAssetList.setPurchaseDate(new ClientFinanceDate(fixedAsset
					.getPurchaseDate().getDate()));
			fixedAssetList.setPurchasePrice(fixedAsset.getPurchasePrice());
			fal.add(fixedAssetList);
		}

		return new VList<FixedAssetList>(fal);
	}

	@Override
	public VList<SellingOrDisposingFixedAssetList> getSellingOrDisposingFixedAssets()
			throws DAOException {

		List<SellingOrDisposingFixedAssetList> fal = new VList<SellingOrDisposingFixedAssetList>();

		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getFixedAsset.by.checkingstatus");

		List<FixedAsset> list = query.list();
		for (FixedAsset fixedAsset : list) {

			SellingOrDisposingFixedAssetList fixedAssetList = new SellingOrDisposingFixedAssetList();
			fixedAssetList.setID(fixedAsset.getID());
			fixedAssetList
					.setAssetAccount(fixedAsset.getAssetAccount().getID());
			fixedAssetList.setAssetNumber(fixedAsset.getAssetNumber());
			fixedAssetList.setName(fixedAsset.getName());
			fixedAssetList.setSoldOrDisposedDate(new ClientFinanceDate(
					fixedAsset.getSoldOrDisposedDate().getDate()));
			fixedAssetList.setSalePrice(fixedAsset.getSalePrice());
			fixedAssetList.setLossOrGain(fixedAsset.getLossOrGain());
			fal.add(fixedAssetList);
		}
		return new VList<SellingOrDisposingFixedAssetList>(fal);
	}

	@Override
	public void runDepreciation(long depreciationFrom, long depreciationTo,
			FixedAssetLinkedAccountMap linkedAccounts) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery(
				"getFixedAsset.by.statusAnd.purchaseDate").setParameter(0,
				(new FinanceDate(depreciationTo)));
		List<FixedAsset> fixedAssets = query.list();
		org.hibernate.Transaction tx = session.beginTransaction();
		for (FixedAsset fixedAsset : fixedAssets) {
			Depreciation depreciation = new Depreciation();
			depreciation.setStatus(Depreciation.APPROVE);
			depreciation.setFixedAsset(fixedAsset);
			depreciation.setDepreciateFrom(new FinanceDate(depreciationFrom));
			depreciation.setDepreciateTo(new FinanceDate(depreciationTo));
			session.save(depreciation);
		}

		if (linkedAccounts != null && linkedAccounts.keySet().size() > 0) {
			query = session.getNamedQuery("getAccount.by.idInAccountList")
					.setParameterList("accountsList", linkedAccounts.keySet());
			List<Account> assetAccounts = query.list();
			for (Account assetAccount : assetAccounts) {
				Long changedLinkedAccountID = linkedAccounts.get(assetAccount
						.getID());

				if (changedLinkedAccountID != null) {
					if (!(assetAccount
							.getLinkedAccumulatedDepreciationAccount().getID() == (changedLinkedAccountID))) {
						Account changedLinkedAccount = (Account) session
								.getNamedQuery("getAccount.by.id")
								.setParameter(0, changedLinkedAccountID)
								.uniqueResult();
						assetAccount
								.setLinkedAccumulatedDepreciationAccount(changedLinkedAccount);
						session.saveOrUpdate(assetAccount);
					}
				}

			}

		}
		tx.commit();
	}

	public DepreciableFixedAssetsList getDepreciableFixedAssets(
			long depreciationFrom, long depreciationTo) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		Query query = session.getNamedQuery(
				"getFixedAsset.by.checkStatusand.purchaseDate").setParameter(0,
				(new FinanceDate(depreciationTo)));
		List<FixedAsset> fixedAssets = query.list();

		List<Long> fixedAssetIDs = new VList<Long>();

		DepreciableFixedAssetsList depreciableFixedAssets = new DepreciableFixedAssetsList();

		Map<Long, List<DepreciableFixedAssetsEntry>> accountViceFixedAssets = new HashMap<Long, List<DepreciableFixedAssetsEntry>>();
		for (FixedAsset fixedAsset : fixedAssets) {
			double amountToBeDepreciatedforThisFixedAsset = Double
					.parseDouble(decimalFormat.format(fixedAsset
							.getCalculatedDepreciatedAmount(
									fixedAsset.getPurchaseDate().compareTo(
											new FinanceDate(depreciationFrom)) <= 0 ? (new FinanceDate(
											depreciationFrom)) : fixedAsset
											.getPurchaseDate(),
									(new FinanceDate(depreciationTo)))));
			// if
			// (accountViceFixedAssets.containsKey(fixedAsset.getAssetAccount()//

			// .getName())) {
			// accountViceFixedAssets.put(fixedAsset.getAssetAccount()
			// .getName(), amountToBeDepreciatedforThisFixedAsset
			// + accountViceFixedAssets.get(fixedAsset
			// .getAssetAccount().getName()));
			// } else {
			// accountViceFixedAssets.put(fixedAsset.getAssetAccount()
			// .getName(), amountToBeDepreciatedforThisFixedAsset);
			// }

			DepreciableFixedAssetsEntry depreciableFixedAssetsEntry = new DepreciableFixedAssetsEntry();
			depreciableFixedAssetsEntry.setID(fixedAsset.getID());
			depreciableFixedAssetsEntry.setFixedAssetName(fixedAsset.getName());
			depreciableFixedAssetsEntry
					.setAmountToBeDepreciated(amountToBeDepreciatedforThisFixedAsset);

			fixedAssetIDs.add(fixedAsset.getID());

			if (accountViceFixedAssets.containsKey(fixedAsset.getAssetAccount()
					.getID())) {
				accountViceFixedAssets
						.get(fixedAsset.getAssetAccount().getID()).add(
								depreciableFixedAssetsEntry);
			} else {
				List<DepreciableFixedAssetsEntry> entries = new VList<DepreciableFixedAssetsEntry>();
				entries.add(depreciableFixedAssetsEntry);
				accountViceFixedAssets.put(
						fixedAsset.getAssetAccount().getID(), entries);
			}

		}

		depreciableFixedAssets
				.setAccountViceFixedAssets(accountViceFixedAssets);

		depreciableFixedAssets.setFixedAssetIDs(fixedAssetIDs);

		return depreciableFixedAssets;

	}

	@Override
	public ClientFinanceDate getDepreciationLastDate() throws DAOException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery(
						"getDepreciation.by.check.idandStatus.depreciationFor")
				.setParameter(0, Depreciation.DEPRECIATION_FOR_ALL_FIXEDASSET)
				.setParameter(1, Depreciation.APPROVE);
		List<Depreciation> list = query.list();
		if (list != null && list.size() > 0 && list.get(0) != null) {
			Depreciation dep = list.get(0);
			return new ClientFinanceDate(dep.getDepreciateTo().getDate());
		}
		return null;
	}

	// SURESH
	public void rollBackDepreciation(long rollBackDepreciationTo)
			throws AccounterException {
		rollBackDepreciation(new FinanceDate(rollBackDepreciationTo));
	}

	public void rollBackDepreciation(FinanceDate rollBackDepreciationTo)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getDepreciation.by.ToandStatus")
				.setParameter(0, (rollBackDepreciationTo))
				.setParameter(1, Depreciation.APPROVE);
		List<Depreciation> list = query.list();
		for (Depreciation dep : list) {
			// if (dep.getFixedAsset().getStatus() ==
			// FixedAsset.STATUS_REGISTERED) {
			dep.setStatus(Depreciation.ROLLBACK);
			// Rakesh
			dep.setRollBackDepreciationDate(new FinanceDate(
					rollBackDepreciationTo.getDate()));

			org.hibernate.Transaction t = session.beginTransaction();
			session.saveOrUpdate(dep);
			if (dep instanceof Lifecycle) {
				Lifecycle lifecycle = (Lifecycle) dep;
				lifecycle.onUpdate(session);
			}
			t.commit();
			// }
		}

		// for (Depreciation dep : list) {
		// for (FixedAsset fixedAsset : dep.getFixedAsset()) {
		// if (fixedAsset != null)
		// fixedAsset.getTransactions().clear();
		// }
		// org.hibernate.Transaction t = session.beginTransaction();
		// session.saveOrUpdate(dep);
		//
		// t.commit();
		// }

	}

	// RAKESH

	// @Override
	// public void rollBackDepreciation(Long rollBackDepreciationTo)
	// throws DAOException {
	// Session session = HibernateUtil.getCurrentSession();
	// Query query = session
	// .createQuery(
	// "from com.vimukti.accounter.core.Transaction T wehre t.transactionDate=?")
	// .setParameter(0, rollBackDepreciationTo);
	//
	// List<Transaction> list = query.list();
	// for (Transaction tran : list) {
	// tran.setVoid(true);
	// org.hibernate.Transaction t = session.beginTransaction();
	// session.saveOrUpdate(tran);
	// if (tran instanceof Lifecycle) {
	// Lifecycle lifecycle = (Lifecycle) tran;
	// lifecycle.onUpdate(session);
	// }
	// t.commit();
	//
	// }
	// }

	@Override
	public double getCalculatedDepreciatedAmount(int depreciationMethod,
			double depreciationRate, double purchasePrice,
			long depreciationFrom, long depreciationTo) throws DAOException {

		/**
		 * Just Preparing the Dummy fixed asset object with the following fixed
		 * asset information.
		 */

		FixedAsset fixedAsset = new FixedAsset();
		fixedAsset.setBookValue(purchasePrice);
		fixedAsset.setPurchaseDate(new FinanceDate(depreciationTo));
		fixedAsset.setPurchasePrice(purchasePrice);
		fixedAsset.setDepreciationMethod(depreciationMethod);
		fixedAsset.setDepreciationRate(depreciationRate);

		DecimalFormat decimalFormat = new DecimalFormat("##.##");

		double amountToBeDepreciatedforThisFixedAsset = 0.0;

		/**
		 * In order to calculate the Depreciation amount between two given
		 * dates, the depreciation should be calculated on monthly basis at the
		 * end of each month in the given date range. So to get the end date at
		 * each month, we need to create Calendar Instance on the given from and
		 * to dates.
		 */
		Calendar fromCal = new GregorianCalendar();
		fromCal.setTime(new FinanceDate(depreciationFrom).getAsDateObject());

		Calendar toCal = new GregorianCalendar();
		toCal.setTime(new FinanceDate(depreciationTo).getAsDateObject());

		int maxDay = fromCal.getActualMaximum(Calendar.DAY_OF_MONTH);

		fromCal.set(fromCal.get(Calendar.YEAR), fromCal.get(Calendar.MONTH),
				maxDay);

		/**
		 * This is to get the depreciation start date. Here the purpose of this
		 * depreciation start date is, if this Fixed Asset depreciation method
		 * is Reducing Balances then, we need to reduce the bookvalue of this
		 * Fixed Asset at the end of Each Financial year by the depreciation
		 * amount calculated for this fiscal year.
		 */
		FinanceDate startDate = Company.getCompany().getPreferences()
				.getDepreciationStartDate();
		Calendar startDateCal = new GregorianCalendar();
		startDateCal.setTime(startDate.getAsDateObject());

		fixedAsset
				.setOpeningBalanceForFiscalYear(fixedAsset.getPurchasePrice());

		/**
		 * The following loop should iterate once for each month between the
		 * from date and end date.
		 */

		while (fromCal.getTime().compareTo(toCal.getTime()) <= 0) {

			/**
			 * If the depreciation method is Straight Line then The depreciation
			 * should calculate on the Purchase Price other wise the
			 * Depreciation should calculate on the Book value in that year.
			 */
			double amount = fixedAsset.getDepreciationMethod() == Depreciation.METHOD_STRAIGHT_LINE ? fixedAsset
					.getPurchasePrice() : fixedAsset
					.getOpeningBalanceForFiscalYear();

			/**
			 * To calculate the depreciation amount for this month based on the
			 * depreciation rate.
			 */
			double depreciationAmount = Double.parseDouble(decimalFormat
					.format(amount * fixedAsset.getDepreciationRate() / 1200));

			amountToBeDepreciatedforThisFixedAsset += depreciationAmount;

			/**
			 * Decreasing the Book Value of this Fixed Asset by the calculated
			 * depreciation amount for this month.
			 */
			fixedAsset.setBookValue(fixedAsset.getBookValue()
					- depreciationAmount);

			/**
			 * Updating this Fixed Asset's accumulated depreciation amount by
			 * this calculated depreciation amount for this month.
			 */
			fixedAsset.setAccumulatedDepreciationAmount(fixedAsset
					.getAccumulatedDepreciationAmount() + depreciationAmount);

			/**
			 * Adjusting the from date so that it will hold the next month last
			 * date.
			 */
			int year = fromCal.get(Calendar.YEAR);
			int month = fromCal.get(Calendar.MONTH) + 1;

			fromCal.clear();
			fromCal.set(Calendar.YEAR, year);
			fromCal.set(Calendar.MONTH, month);
			fromCal.set(Calendar.DATE,
					fromCal.getActualMaximum(Calendar.DAY_OF_MONTH));

			/**
			 * Adjusting the opening balance of this Fixed Asset each year after
			 * the start date by the actual bookvalue which represents the
			 * updated bookvalue at the end of each month.
			 */
			if (fromCal.get(Calendar.MONTH) == startDateCal.get(Calendar.MONTH)) {
				fixedAsset.setOpeningBalanceForFiscalYear(fixedAsset
						.getBookValue());
			}

		}

		return amountToBeDepreciatedforThisFixedAsset;
	}

	//
	// @Override
	// public double getCalculatedRollBackDepreciationAmount(
	// Long rollBackDepreciationTo) throws DAOException {
	//
	// Session session = HibernateUtil.getCurrentSession() == null ? Utility
	// .getCurrentSession() : HibernateUtil.getCurrentSession();
	// Query query = session
	// .createQuery(
	// "from com.vimukti.accounter.core.Depreciation d where d.depreciateFrom >= ? and d.status=? "
	// )
	// .setParameter(0, rollBackDepreciationTo).setParameter(1,
	// Depreciation.APPROVE);
	// List<Depreciation> list = query.list();
	// double rollBackDepAmt = 0.0;
	// for (Depreciation dep : list) {
	// // for (FixedAsset fixedAsset : dep.getFixedAssets()) {
	// if (dep.getFixedAsset() != null)
	// for (Transaction trans : dep.getFixedAsset().getTransactions()) {
	// rollBackDepAmt += ((JournalEntry) trans).getDebitTotal();
	// }
	// // }
	// }
	// return rollBackDepAmt;
	// }

	@Override
	public double getCalculatedRollBackDepreciationAmount(
			long rollBackDepreciationTo) throws DAOException {

		Session session = HibernateUtil.getCurrentSession() == null ? Utility
				.getCurrentSession() : HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getDepreciation.by.depreciateFrom")
				.setParameter(0, (new FinanceDate(rollBackDepreciationTo)))
				.setParameter(1, Depreciation.APPROVE);
		List<Depreciation> list = query.list();
		double rollBackDepAmt = 0.0;
		for (Depreciation dep : list) {
			// for (FixedAsset fixedAsset : dep.getFixedAssets()) {
			if (dep.getFixedAsset() != null)
				for (Transaction trans : dep.getFixedAsset().getTransactions()) {
					rollBackDepAmt += ((JournalEntry) trans).getDebitTotal();
				}
			// }
		}
		return rollBackDepAmt;
	}

	//
	// @Override
	// public double getCalculatedRollBackDepreciationAmount(String
	// fixedAssetID,
	// Long rollBackDepreciationTo) throws DAOException {
	//
	// Session session = HibernateUtil.getCurrentSession() == null ? Utility
	// .getCurrentSession() : HibernateUtil.getCurrentSession();
	//
	// Query query = session
	// .createQuery(
	// "select d from com.vimukti.accounter.core.Depreciation d inner join d.fixedAsset where d.depreciateFrom >= ? and d.status=? and d.fixedAsset.id=? group by d.fixedAsset.id"
	// )
	// .setParameter(0, rollBackDepreciationTo).setParameter(1,
	// Depreciation.APPROVE).setParameter(2, fixedAssetID);
	//
	// List<Depreciation> list = query.list();
	// double rollBackDepAmt = 0.0;
	// for (Depreciation dep : list) {
	// // for (FixedAsset fixedAsset : dep.getFixedAssets()) {
	// if (dep.getFixedAsset() != null)
	// for (Transaction trans : dep.getFixedAsset().getTransactions()) {
	// if (!trans.isVoid()) {
	// rollBackDepAmt += ((JournalEntry) trans)
	// .getDebitTotal();
	// }
	// }
	// // }
	// }
	// return rollBackDepAmt;
	// }

	@Override
	public double getCalculatedRollBackDepreciationAmount(long fixedAssetID,
			long rollBackDepreciationTo) throws DAOException {

		Session session = HibernateUtil.getCurrentSession() == null ? Utility
				.getCurrentSession() : HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getFixedAsset.from.id")
				.setParameter(0, fixedAssetID);
		// "select d from com.vimukti.accounter.core.Depreciation d inner join d.fixedAsset where d.depreciateFrom >= ? and d.status=? and d.fixedAsset.id=?").setParameter(0,
		// rollBackDepreciationTo).setParameter(1,Depreciation.APPROVE).setParameter(2,
		// fixedAssetID);

		// "select d from com.vimukti.accounter.core.Depreciation d inner join d.fixedAsset where d.depreciateFrom >= ? and d.status=? and d.fixedAsset.id=? group by d.fixedAsset.id")
		// .setParameter(0, rollBackDepreciationTo).setParameter(1,
		//

		List<FixedAsset> list = query.list();
		double rollBackDepAmt = 0.0;
		for (FixedAsset f : list) {
			// for (FixedAsset fixedAsset : dep.getFixedAssets()) {
			for (Transaction t : f.getTransactions()) {

				if (!t.isVoid()
						&& t.getDate().after(
								new FinanceDate(rollBackDepreciationTo))) {
					rollBackDepAmt += t.getTotal();
				}

			}

		}
		return rollBackDepAmt;
	}

	private double getCalculatedRollBackDepreciationAmount2(long fixedAssetID,
			FinanceDate rollBackDepreciationTo) throws DAOException {

		Session session = HibernateUtil.getCurrentSession() == null ? Utility
				.getCurrentSession() : HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getFixedAsset.from.id")

		.setParameter(0, fixedAssetID);

		List<FixedAsset> list = query.list();
		double rollBackDepAmt = 0.0;
		for (FixedAsset f : list) {

			for (Transaction t : f.getTransactions()) {
				if ((!t.isVoid())
						&& (t.getDate().after(rollBackDepreciationTo))) {

					rollBackDepAmt += t.getTotal();
				}
			}

		}
		return rollBackDepAmt;
	}

	/**
	 * 
	 * This method will give us the total effect of Selling or Disposing a Fixed
	 * Asset, before Sell or Dispose this Fixed Asset.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public FixedAssetSellOrDisposeReviewJournal getReviewJournal(
			TempFixedAsset fixedAsset) throws DAOException {

		/**
		 * In this method we should prepare two maps named disposalSummary and
		 * disposalJournal. Each Map contains several Strings as keys and
		 * corresponing calculated values for them.
		 */

		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
		FixedAssetSellOrDisposeReviewJournal fixedAssetSellOrDisposeReviewJournal = new FixedAssetSellOrDisposeReviewJournal();

		/**
		 * Preparing the keys and values for disposalSummary Map.
		 */
		String purchasedDate = "Purchase "
				+ format.format(fixedAsset.getPurchaseDate());
		String currentAccumulatedDepreciation = "Current accumulated depreciation";
		String depreciationTobePosted = "Depreciation to be posted (";
		String rollBackDepreciation = "rollback deprecaition till ";
		FinanceDate date = Depreciation.getDepreciationLastDate();
		FinanceDate depreciationTillDate = null;
		double depreciationToBePostedAmount = 0.0;
		double rollBackDepreciatinAmount = 0.0;

		FinanceDate lastDepreciationDate = Depreciation
				.getDepreciationLastDate();
		if (lastDepreciationDate == null) {
			lastDepreciationDate = Company.getCompany().getPreferences()
					.getDepreciationStartDate();
		}

		// if (fixedAsset.getPurchaseDate().compareTo(lastDepreciationDate) > 0)
		// {
		// lastDepreciationDate = fixedAsset.getPurchaseDate();
		// }
		Calendar lastDepreciationDateCal = new GregorianCalendar();
		lastDepreciationDateCal.setTime(lastDepreciationDate.getAsDateObject());
		lastDepreciationDateCal.set(Calendar.DAY_OF_MONTH, 01);
		lastDepreciationDateCal.set(Calendar.MONTH,
				lastDepreciationDateCal.get(Calendar.MONTH) + 1);

		if (fixedAsset.isNoDepreciation()) {

			Calendar soldOrDisposedDateCal = new GregorianCalendar();
			soldOrDisposedDateCal.setTime(fixedAsset.getSoldOrDisposedDate()
					.getDateAsObject());

			FinanceDate startDate = Company.getCompany().getPreferences()
					.getDepreciationStartDate();
			Calendar startDateCal = new GregorianCalendar();
			startDateCal.setTime(startDate.getAsDateObject());

			Calendar soldYearStartDateCal = new GregorianCalendar(
					soldOrDisposedDateCal.get(Calendar.YEAR),
					startDateCal.get(Calendar.MONTH),
					startDateCal.get(Calendar.DAY_OF_MONTH));
			soldYearStartDateCal.set(Calendar.MONTH,
					soldYearStartDateCal.get(Calendar.MONTH) - 1);
			soldYearStartDateCal.set(Calendar.DAY_OF_MONTH,
					soldYearStartDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			// if (fixedAsset.getPurchaseDate().compareTo(lastDepreciationDate)
			// <= 0) {

			if (lastDepreciationDate.compareTo(new FinanceDate(
					soldYearStartDateCal.getTime())) < 0) {

				// Run depreciation from last depreciation date to sold year
				// start date.
				FinanceDate depFrom = new FinanceDate(fixedAsset
						.getPurchaseDate().getDate())
						.compareTo(new FinanceDate(lastDepreciationDate
								.getDate())) <= 0 ? new FinanceDate(
						lastDepreciationDateCal.getTime()) : new FinanceDate(
						fixedAsset.getPurchaseDate().getDate());
				depreciationTobePosted += format.format(depFrom);
				depreciationTobePosted += " to";
				depreciationTobePosted += format.format(soldYearStartDateCal
						.getTime());
				depreciationTobePosted += ")";

				depreciationToBePostedAmount = getCalculatedDepreciatedAmount(
						fixedAsset.getDepreciationMethod(),
						fixedAsset.getDepreciationRate(),
						fixedAsset.getPurchasePrice(), depFrom.getDate(),
						new FinanceDate(soldYearStartDateCal.getTime())
								.getDate());
				depreciationToBePostedAmount = Double.parseDouble(decimalFormat
						.format(depreciationToBePostedAmount));

			} else if (lastDepreciationDate.compareTo(new FinanceDate(
					soldYearStartDateCal.getTime())) > 0) {

				// Roll back the Depreciation till the sold year start date.
				rollBackDepreciation += format.format(soldYearStartDateCal
						.getTime());
				rollBackDepreciatinAmount = getCalculatedRollBackDepreciationAmount2(
						fixedAsset.getFixedAssetID(), new FinanceDate(
								soldYearStartDateCal.getTime()));
				rollBackDepreciatinAmount = Double.parseDouble(decimalFormat
						.format(rollBackDepreciatinAmount));

			}
			// }

			depreciationTillDate = new FinanceDate(
					soldYearStartDateCal.getTime());

		} else {
			// this is for depriciation upto given month

			depreciationTillDate = new FinanceDate(fixedAsset
					.getDepreciationTillDate().getDate());

			if (new FinanceDate(fixedAsset.getPurchaseDate().getDate())
					.compareTo(new FinanceDate(depreciationTillDate.getDate())) <= 0) {

				if (lastDepreciationDate.compareTo(depreciationTillDate) < 0) {
					lastDepreciationDateCal.set(Calendar.MONTH,
							lastDepreciationDateCal.get(Calendar.MONTH) - 1);

					FinanceDate depFrom = new FinanceDate(fixedAsset
							.getPurchaseDate().getDate())

					.compareTo(new FinanceDate(lastDepreciationDate.getDate())) <= 0 ? new FinanceDate(

					lastDepreciationDateCal.getTime())
							: new FinanceDate(fixedAsset.getPurchaseDate()
									.getDate());
					depreciationTobePosted += format.format(depFrom);
					depreciationTobePosted += " to";
					depreciationTobePosted += format
							.format(depreciationTillDate);
					depreciationTobePosted += ")";
					depreciationToBePostedAmount = getCalculatedDepreciatedAmount(
							fixedAsset.getDepreciationMethod(),
							fixedAsset.getDepreciationRate(),
							fixedAsset.getPurchasePrice(), depFrom.getDate(),
							depreciationTillDate.getDate());
					depreciationToBePostedAmount = Double
							.parseDouble(decimalFormat
									.format(depreciationToBePostedAmount));
				} else if (lastDepreciationDate.compareTo(depreciationTillDate) > 0) {

					// Roll back the Depreciation upto given Depreciation till
					// date.
					rollBackDepreciation += format.format(depreciationTillDate);
					rollBackDepreciatinAmount = getCalculatedRollBackDepreciationAmount2(
							fixedAsset.getFixedAssetID(), depreciationTillDate);
					rollBackDepreciatinAmount = Double
							.parseDouble(decimalFormat
									.format(rollBackDepreciatinAmount));
				}

			}

		}

		String soldDate = "Sold "
				+ format.format(fixedAsset.getSoldOrDisposedDate());

		/**
		 * Preparing the disposalSummary Map with the above calculated Keys and
		 * corresponding amounts for those keys.
		 */
		Map<String, Double> disposalSummary = new LinkedHashMap<String, Double>();
		disposalSummary.put(purchasedDate, fixedAsset.getPurchasePrice());
		if (!DecimalUtil.isEquals(fixedAsset.getPurchasePrice(),
				fixedAsset.getBookValue())) {
			// disposalSummary.put(currentAccumulatedDepreciation, fixedAsset
			// .getAccumulatedDepreciationAmount());

			disposalSummary
					.put(currentAccumulatedDepreciation, (fixedAsset
							.getPurchasePrice() - fixedAsset.getBookValue()));
		}
		if (!DecimalUtil.isEquals(depreciationToBePostedAmount, 0.0)) {
			disposalSummary.put(depreciationTobePosted,
					depreciationToBePostedAmount);
		}
		if (!DecimalUtil.isEquals(rollBackDepreciatinAmount, 0.0)) {
			disposalSummary
					.put(rollBackDepreciation, rollBackDepreciatinAmount);
		}

		disposalSummary.put(soldDate, fixedAsset.getSalesPrice());

		/**
		 * Preparing the keys and values for disposalJournal Map.
		 */
		String assetAccount = fixedAsset.getAssetAccountName();
		String lessAccoummulatedDepreciation = fixedAsset
				.getLinkedAccumulatedDepreciatedAccountName();
		String salesAccount = fixedAsset.getSalesAccountName();
		String totalCapitalGainString = "Total Capital Gain";
		String gainOnDisposal = "Gain on disposal";
		String lossOnDisposal = "Loss on disposal";

		double salesPrice = fixedAsset.getSalesPrice();
		double purchasePrice = fixedAsset.getPurchasePrice();
		double lessAccumulatedDepreciationAmount = fixedAsset
				.getPurchasePrice() - fixedAsset.getBookValue();

		/**
		 * calculating whether there will be any capital gain or not for this
		 * Sell or Dispose of Fixed Asset.
		 */

		double totalCapitalGain = Double
				.parseDouble(decimalFormat.format(DecimalUtil.isGreaterThan(
						salesPrice, purchasePrice) ? (salesPrice - purchasePrice)
						: 0.0));

		double calculatedLessAccDep = (fixedAsset.getPurchasePrice() - fixedAsset
				.getBookValue())
				+ depreciationToBePostedAmount
				- rollBackDepreciatinAmount;
		// long factor = (long) Math.pow(10, 2);
		calculatedLessAccDep = calculatedLessAccDep * 100;
		long tmp = Math.round(calculatedLessAccDep);
		calculatedLessAccDep = (double) tmp / 100;

		/**
		 * calculating gain/loss for this Sell or Dispose of Fixed Asset. If the
		 * calculated amount is +ve then it represents gain or if the calculated
		 * amount is -ve then it represents loss.
		 */
		double lossOrGainOnDisposal = Double.parseDouble(decimalFormat
				.format((salesPrice + calculatedLessAccDep) - purchasePrice
						- totalCapitalGain));

		/**
		 * Preparing the disposalJournal Map with the above calculated Keys and
		 * corresponding amounts for those keys.
		 */
		Map<String, Double> disposalJournal = new LinkedHashMap<String, Double>();
		disposalJournal.put(assetAccount, fixedAsset.getPurchasePrice());

		if (!DecimalUtil.isEquals(calculatedLessAccDep, 0.0)) {
			disposalJournal.put(lessAccoummulatedDepreciation, -1
					* (calculatedLessAccDep));
		}

		if (salesAccount != null) {
			disposalJournal.put(salesAccount, -1 * salesPrice);
		}

		if (DecimalUtil.isGreaterThan(totalCapitalGain, 0.0)) {
			disposalJournal.put(totalCapitalGainString, totalCapitalGain);
		}
		if (!DecimalUtil.isEquals(lossOrGainOnDisposal, 0.0)) {
			if (DecimalUtil.isGreaterThan(lossOrGainOnDisposal, 0.0)) {
				disposalJournal.put(gainOnDisposal, lossOrGainOnDisposal);
			} else {
				disposalJournal.put(lossOnDisposal, lossOrGainOnDisposal);
			}
		}

		fixedAssetSellOrDisposeReviewJournal
				.setDisposalSummary(disposalSummary);
		fixedAssetSellOrDisposeReviewJournal
				.setDisposalJournal(disposalJournal);
		return fixedAssetSellOrDisposeReviewJournal;

	}

	private void changeDepreciationStartDateTo(FinanceDate newStartDate)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();
		Company company = Company.getCompany();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		// Catching the current Start Long
		FinanceDate startDate = company.getPreferences()
				.getDepreciationStartDate();
		FinanceDate depreciationFirstDate = null;
		Query query = session.getNamedQuery("getDepreciation");
		List<Depreciation> list = query.list();
		if (list != null && list.size() > 0 && list.get(0) != null) {
			Depreciation dep = list.get(0);
			depreciationFirstDate = dep.getDepreciateFrom();
		}

		// Roll-back all the existing depreciation
		if (depreciationFirstDate != null) {
			rollBackDepreciation(depreciationFirstDate);
		}

		query = session.getNamedQuery("getFixedAsset.by.purchaseDate")
				.setParameter(0, (newStartDate));
		List<FixedAsset> assetsList = query.list();
		for (FixedAsset fixedAsset : assetsList) {

			fixedAsset.setAccumulatedDepreciationAmount(0.0);
			fixedAsset.setSoldOrDisposed(false);
			// fixedAsset.setSellingOrDisposingFixedAsset(null);
			fixedAsset.setBookValue(fixedAsset.getPurchasePrice());
			fixedAsset.setSoldOrDisposedDate(null);
			fixedAsset.setAccountForSale(null);
			fixedAsset.setSalePrice(0.0);
			fixedAsset.setNoDepreciation(false);
			fixedAsset.setDepreciationTillDate(null);
			fixedAsset.setNotes(null);
			fixedAsset.setLossOrGainOnDisposalAccount(null);
			fixedAsset.setTotalCapitalGain(null);
			fixedAsset.setLossOrGain(0.0);

			/**
			 * Saving the action into History
			 */
			FixedAssetHistory fixedAssetHistory = new FixedAssetHistory();
			if (fixedAsset.getPurchaseDate().before(newStartDate)) {
				fixedAsset.setStatus(FixedAsset.STATUS_PENDING);

				fixedAssetHistory
						.setActionType(FixedAssetHistory.ACTION_TYPE_ROLLBACK);
				fixedAssetHistory.setActionDate(new FinanceDate());
				fixedAssetHistory
						.setDetails("Fixed Asset Start Long altered from "
								+ format.format(startDate.getAsDateObject())
								+ " to "
								+ format.format(newStartDate.getAsDateObject())
								+ ". All fixed assets journals reversed.");

			} else {

				fixedAssetHistory
						.setActionType(FixedAssetHistory.ACTION_TYPE_NONE);
				fixedAssetHistory.setActionDate(new FinanceDate());
				fixedAssetHistory
						.setDetails("Fixed Asset Start Long altered from "
								+ format.format(startDate.getAsDateObject())
								+ " to "
								+ format.format(newStartDate.getAsDateObject())
								+ ". All fixed assets book values are set");

			}
			fixedAsset.getFixedAssetsHistory().add(fixedAssetHistory);
			session.saveOrUpdate(fixedAsset);
			ChangeTracker.put(fixedAsset);

		}

	}

	// @Override
	// public void changeDepreciationStartDateTo(long newStartDate)
	// throws DAOException {
	// if (newStartDate == 0)
	// return;
	//
	// Command cmd = new Command(UPDATE_DEPRECIATION_STARTDATE, newStartDate
	// + "", null);
	// cmd.data = new ClientConvertUtil().toClientObject(Company.getCompany()
	// .getPreferences(), null);
	//
	// space.sendCommand(cmd, this);
	// }

	@Override
	public VList<ClientFinanceDate> getFinancialYearStartDates()
			throws DAOException {

		List<ClientFinanceDate> startDates = new VList<ClientFinanceDate>();
		Session session = HibernateUtil.getCurrentSession();
		Company company = Company.getCompany();
		FinanceDate depreciationStartDate = company.getPreferences()
				.getDepreciationStartDate();
		Calendar depStartDateCal = new GregorianCalendar();
		depStartDateCal.setTime(depreciationStartDate.getAsDateObject());

		Query query = session
				.getNamedQuery("getFiscalYear.by.check.status.startDate");
		List<FiscalYear> list = query.list();
		for (FiscalYear fs : list) {
			FinanceDate date = fs.getStartDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date.getAsDateObject());
			cal.set(Calendar.DAY_OF_MONTH,
					depStartDateCal.get(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.MONTH, depStartDateCal.get(Calendar.MONTH));
			startDates.add(new ClientFinanceDate(cal.getTime()));
		}
		return new VList<ClientFinanceDate>(startDates);
	}

	@Override
	public VList<ClientFinanceDate> getAllDepreciationFromDates()
			throws DAOException {

		List<ClientFinanceDate> fromDates = new VList<ClientFinanceDate>();
		Session session = HibernateUtil.getCurrentSession();
		Company company = Company.getCompany();
		FinanceDate depreciationStartDate = company.getPreferences()
				.getDepreciationStartDate();
		Calendar depStartDateCal = new GregorianCalendar();
		depStartDateCal.setTime(depreciationStartDate.getAsDateObject());

		FinanceDate tempDate = depreciationStartDate;

		// Query query = session
		// .createQuery(
		// "select distinct(t.transactionDate) from com.vimukti.accounter.core.Transaction t where t.isVoid='false'"
		// );
		Query query = session.getNamedQuery("getdistnct.from.traction.by.date")
				.setParameter(0, Depreciation.APPROVE);
		List list = query.list();
		for (Object dep : list) {
			fromDates.add(new ClientFinanceDate((Long) dep));
		}
		return new VList<ClientFinanceDate>(fromDates);
	}

	// public void changeFiscalYearsStartDateTo(long newStartDate)
	// throws DAOException {
	//
	// if (newStartDate == 0)
	// return;
	//
	// Command cmd = new Command(UPDATE_COMPANY_STARTDATE, newStartDate + "",
	// null);
	// cmd.data = new ClientConvertUtil().toClientObject(Company.getCompany()
	// .getPreferences(), null);
	//
	// space.sendCommand(cmd, this);
	//
	// }

	private void changeFiscalYearsStartDate(FinanceDate modifiedStartDate) {

		Session session = HibernateUtil.getCurrentSession();
		org.hibernate.Transaction transaction = session.beginTransaction();
		// List<FiscalYear> list = new VList<FiscalYear>();

		// FinanceDate existingLeastStartDate = modifiedStartDate;
		// FinanceDate existingHighestEndDate = modifiedStartDate;
		// list = session
		// .createQuery(
		// "from com.vimukti.accounter.core.FiscalYear f order by f.startDate")
		// .list();

		Query query = session.getNamedQuery("getMinStartDateAndMaxEndDate");
		List list = query.list();
		Object[] object = (Object[]) list.get(0);

		FinanceDate existingLeastStartDate = new FinanceDate((Long) object[0]);
		FinanceDate existingHighestEndDate = new FinanceDate((Long) object[1]);

		// if (list.size() > 0) {
		// Iterator<FiscalYear> i = list.iterator();
		// FiscalYear firstFiscalYear = list.get(0) != null ? list.get(0)
		// : null;
		// FiscalYear lastFiscalYear = null;
		// while (i.hasNext()) {
		// lastFiscalYear = (FiscalYear) i.next();
		// }
		// existingLeastStartDate = firstFiscalYear.getPreviousStartDate();
		//
		// existingHighestEndDate = lastFiscalYear.getEndDate();

		// if ((modifiedStartDate.before(existingLeastStartDate) ||
		// modifiedStartDate
		// .after(existingHighestEndDate))) {

		Calendar leastStartDateCal = Calendar.getInstance();
		leastStartDateCal.setTime(existingLeastStartDate.getAsDateObject());

		Calendar highestEndDateCal = Calendar.getInstance();
		highestEndDateCal.setTime(existingHighestEndDate.getAsDateObject());

		Calendar modifiedDateCal = Calendar.getInstance();
		modifiedDateCal.setTime(modifiedStartDate.getAsDateObject());

		if (modifiedStartDate.getDate() >= existingLeastStartDate.getDate()
				&& modifiedStartDate.getDate() <= existingHighestEndDate
						.getDate()) {
			return;
		} else if (modifiedStartDate.getDate() < existingLeastStartDate
				.getDate()) {

			// if (modifiedStartDate.before(existingLeastStartDate)) {

			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(leastStartDateCal.getTime());

			FinanceDate tempDate = existingLeastStartDate;

			// while (tempCal.compareTo(modifiedDateCal) > 0) {
			while (tempDate.getDate() > modifiedStartDate.getDate()) {

				Calendar cal = Calendar.getInstance();

				cal.setTime(tempCal.getTime());
				cal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) - 12);

				FinanceDate startDate = new FinanceDate(cal.getTime());

				cal.clear();
				cal.setTime(tempCal.getTime());
				cal.set(Calendar.DAY_OF_MONTH,
						tempCal.get(Calendar.DAY_OF_MONTH) - 1);

				FinanceDate endDate = new FinanceDate(cal.getTime());

				FiscalYear fs = new FiscalYear(startDate, endDate,
						FiscalYear.STATUS_OPEN, Boolean.FALSE);
				fs.checkIsCurrentFY(fs);
				session.save(fs);
				ChangeTracker.put(fs);

				tempDate = startDate;

				tempCal.setTime(startDate.getAsDateObject());

			}
			// } else if (modifiedStartDate.after(existingHighestEndDate)) {

		} else if (modifiedStartDate.getDate() > existingHighestEndDate
				.getDate()) {

			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(highestEndDateCal.getTime());

			FinanceDate tempDate = existingHighestEndDate;

			// while (tempCal.compareTo(modifiedDateCal) < 0) {

			while (tempDate.getDate() < modifiedStartDate.getDate()) {

				Calendar cal = Calendar.getInstance();

				cal.setTime(tempCal.getTime());
				cal.set(Calendar.DAY_OF_MONTH,
						tempCal.get(Calendar.DAY_OF_MONTH) + 1);

				FinanceDate startDate = new FinanceDate(cal.getTime());

				cal.clear();
				cal.setTime(tempCal.getTime());
				cal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 12);

				FinanceDate endDate = new FinanceDate(cal.getTime());
				FiscalYear fs = new FiscalYear(startDate, endDate,
						FiscalYear.STATUS_OPEN, Boolean.FALSE);
				fs.checkIsCurrentFY(fs);
				session.save(fs);
				ChangeTracker.put(fs);

				tempDate = endDate;

				tempCal.setTime(endDate.getAsDateObject());
			}
		}

		// }
		Utility.updateCurrentFiscalYear();
		transaction.commit();

	}

	/*
	 * ==========================================================================
	 * ================
	 */
	// Updated AccounterReportsDAOService methods
	/*
	 * ==========================================================================
	 * ================
	 */

	@Override
	public VList<AccountBalance> getAccountBalances() throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();
			Query query = session.getNamedQuery("accountbalances");
			List list = query.list();

			if (list.size() > 0) {

				List<AccountBalance> accounterbalances = new VList<AccountBalance>();
				Object[] object = null;
				Iterator it = list.iterator();
				while (it.hasNext()) {

					object = (Object[]) it.next();

					String name = (String) object[0];
					Double balance = object[1] == null ? 0.0
							: ((Double) object[1]).doubleValue();
					int type = object[2] == null ? 0 : Integer
							.parseInt(object[2].toString());

					AccountBalance accountBalance = new AccountBalance(name,
							balance, type);

					accounterbalances.add(accountBalance);

				}
				VList<AccountBalance> arrayList = new VList<AccountBalance>(
						accounterbalances);
				return arrayList;
			}
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));

		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<TrialBalance> getTrialBalance(final FinanceDate startDate,
			final FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getTrialBalance")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TrialBalance> queryResult = new VList<TrialBalance>();
		while ((iterator).hasNext()) {

			TrialBalance t = new TrialBalance();
			object = (Object[]) iterator.next();

			// this condition is to filter unnecessary rows
			// if ((object[6] == null ? 0 : ((Double) object[6]).doubleValue())
			// != 0.0) {
			t.setAccountId(((Long) object[0]));
			t.setAccountName(object[1] == null ? null : (String) object[1]);
			t.setAccountNumber(object[2] == null ? null : (String) object[2]);
			t.setAccountType(object[3] == null ? 0 : ((Integer) object[3])
					.intValue());
			t.setCashFlowCategory(object[4] == null ? 0 : ((Integer) object[4])
					.intValue());

			Account parentAccount = (object[5] == null) ? null
					: (Account) session.get(Account.class,
							((Long) object[5]).longValue());
			if (parentAccount != null) {
				t.setParentAccount(parentAccount.getID());
			}

			t.setAmount(object[6] == null ? 0d : ((Double) object[6])
					.doubleValue());
			if (DecimalUtil.isLessThan(t.getAmount(), 0.0)) {
				t.setCreditAmount(-1 * t.getAmount());

			} else {
				t.setDebitAmount(t.getAmount());
			}

			t.setAccountFlow((String) object[7]);
			queryResult.add(t);

			// }

		}
		return removeUnwantedEntries(new VList<TrialBalance>(queryResult));

		// Object obj = template.execute(new HibernateCallback(){
		// @OverrideAc
		// public Object doInHibernate(Session session)
		// throws HibernateException, SQLException {
		//
		// return new TrialBalanceReport(session,startDate,endDate);
		// }
		// });
		//
		// return obj !=null?(TrialBalanceReport)obj:null;

	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByCustomerDetailReport(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getSalesByCustomerDetail")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();

		return (l != null && l.size() > 0) ? createSalesByCustomerDetailReport(new VList<SalesByCustomerDetail>(
				l)) : null;

	}

	private VList<SalesByCustomerDetail> createSalesByCustomerDetailReport(
			List l) {

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setName((String) object[0]);
			salesByCustomerDetail.setType(object[1] == null ? 0
					: ((Integer) object[1]).intValue());
			salesByCustomerDetail.setDate(new ClientFinanceDate(
					((BigInteger) object[2]).longValue()));

			salesByCustomerDetail.setNumber((String) object[3]);
			/*
			 * Clob cl = (Clob) object[4]; if (cl == null) {
			 * 
			 * salesByCustomerDetail.setMemo("");
			 * 
			 * } else {
			 * 
			 * StringBuffer strOut = new StringBuffer(); String aux; try {
			 * BufferedReader br = new BufferedReader(cl .getCharacterStream());
			 * while ((aux = br.readLine()) != null) strOut.append(aux);
			 * salesByCustomerDetail.setMemo(strOut.toString()); } catch
			 * (java.sql.SQLException e1) {
			 * 
			 * } catch (java.io.IOException e2) {
			 * 
			 * } }
			 */
			salesByCustomerDetail.setMemo((String) object[4]);
			salesByCustomerDetail.setDueDate(object[5] == null ? null
					: new ClientFinanceDate(((BigInteger) object[5])
							.longValue()));
			salesByCustomerDetail.setPaymentTermName((String) object[6]);
			salesByCustomerDetail.setItemName((String) object[7]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[8] == null ? 0 : ((Double) object[8])
					.intValue());
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setUnitPrice(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			salesByCustomerDetail.setAmount(object[10] == null ? 0
					: ((Double) object[10]).doubleValue());
			salesByCustomerDetail.setDeliveryDate(object[11] == null ? null
					: new ClientFinanceDate(((BigInteger) object[11])
							.longValue()));
			salesByCustomerDetail.setIsVoid(object[12] == null ? true
					: ((Boolean) object[12]).booleanValue());
			salesByCustomerDetail.setReference((String) object[13]);
			salesByCustomerDetail.setTransactionId((object[14] == null ? 0
					: (((BigInteger) object[14]).longValue())));
			queryResult.add(salesByCustomerDetail);
		}
		// return prepareSalesPurchaseEntriesForVoid(queryResult);
		return new VList<SalesByCustomerDetail>(queryResult);
	}

	private List<SalesByCustomerDetail> prepareSalesPurchaseEntriesForVoid(
			List<SalesByCustomerDetail> queryResult) {

		List<SalesByCustomerDetail> list = new VList<SalesByCustomerDetail>();
		for (SalesByCustomerDetail detail : queryResult) {

			if (detail.getIsVoid() == true) {
				list.add(detail);

				SalesByCustomerDetail th = new SalesByCustomerDetail();
				th = detail;
				th.setAmount(-1 * th.getAmount());
				list.add(th);
			} else {
				list.add(detail);
			}
		}
		return list;
	}

	@Override
	public VList<Transaction> getRegister(Account account) throws DAOException {
		return null;
	}

	@Override
	public VList<AgedDebtors> getAgedDebtors(final FinanceDate startDate,
			final FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getAgedDebtors")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return prepareAgedDebotOrsorCreditors(new VList<AgedDebtors>(l),
				startDate, endDate);
	}

	@Override
	public VList<AgedDebtors> getAgedDebtors(FinanceDate startDate,
			FinanceDate endDate, int intervalDays, int throughDaysPassOut)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getTransactionDate.by.dates")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		return null;
	}

	@Override
	public VList<AgedDebtors> getAgedCreditors(final FinanceDate startDate,
			final FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getAgedCreditors")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return prepareAgedDebotOrsorCreditors(new VList<AgedDebtors>(l),
				startDate, endDate);

	}

	private VList<AgedDebtors> prepareAgedDebotOrsorCreditors(List list,
			final FinanceDate startDate, final FinanceDate endDate) {
		Object[] object = null;
		Iterator iterator = list.iterator();
		List<AgedDebtors> queryResult = new VList<AgedDebtors>();
		while ((iterator).hasNext()) {

			AgedDebtors agedDebtors = new AgedDebtors();
			object = (Object[]) iterator.next();

			agedDebtors.setName((String) object[0]);
			agedDebtors.setContact((String) object[1]);
			agedDebtors.setPhone((String) object[2]);
			agedDebtors.setType(object[3] == null ? 0 : ((Integer) object[3])
					.intValue());
			agedDebtors.setDate(new ClientFinanceDate((Long) object[4]));
			agedDebtors.setNumber((String) object[5]);
			agedDebtors.setReference((String) object[6]);
			agedDebtors.setDueDate(object[7] == null ? null
					: new ClientFinanceDate((Long) object[7]));
			agedDebtors.setPaymentTermName((String) object[8]);
			agedDebtors.setAmount(object[9] == null ? 0 : ((Double) object[9])
					.doubleValue());
			agedDebtors.setTotal(object[9] == null ? 0 : ((Double) object[9])
					.doubleValue());
			agedDebtors.setIsVoid(object[11] == null ? true
					: ((Boolean) object[11]).booleanValue());
			agedDebtors.setTransactionId((Long) object[12]);
			agedDebtors
					.setMemo(object[13] != null ? (String) object[13] : null);
			long ageing = getAgeing(agedDebtors.getDate(),
					agedDebtors.getDueDate(), endDate);
			int category = getCategory(ageing);
			agedDebtors.setAgeing(ageing);
			agedDebtors.setCategory(category);

			if (agedDebtors.getAmount() != 0)
				queryResult.add(agedDebtors);
		}

		Collections.sort(queryResult, new Comparator<AgedDebtors>() {

			@Override
			public int compare(AgedDebtors arg0, AgedDebtors arg1) {
				return arg0.getCategory() > arg1.getCategory() ? 2 : arg0
						.getCategory() < arg1.getCategory() ? -1 : 0;
			}
		});
		return new VList<AgedDebtors>(queryResult);
	}

	public long getAgeing(ClientFinanceDate transactionDate,
			ClientFinanceDate dueDate, final FinanceDate endDate) {

		long ageing = 0;

		try {

			ClientFinanceDate ageingForDueorTranactionDate;

			if (this.getCompany().getPreferences()
					.getAgeingFromTransactionDateORDueDate() == CompanyPreferencesView.TYPE_AGEING_FROM_DUEDATE)
				ageingForDueorTranactionDate = dueDate;
			else
				ageingForDueorTranactionDate = transactionDate;

			if (!ageingForDueorTranactionDate.after(endDate
					.toClientFinanceDate()))
				ageing = UIUtils.getDays_between(ageingForDueorTranactionDate
						.getDateAsObject(), endDate.toClientFinanceDate()
						.getDateAsObject());

			// if (agedDebtors.getDueDate() != null
			// && !agedDebtors.getDueDate().equals("")
			// && !agedDebtors.getDueDate().after(
			// format.parse(endDate))) {
			//
			// diff = UIUtils.getDays_between(format.parse(endDate),
			// agedDebtors.getDueDate());
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ageing;
	}

	public int getCategory(long days) {
		// if (diff > 0)
		// agedDebtors.setCategory(1);
		if (days >= 0 && days <= 30)
			return 1;
		else if (days > 30 && days <= 60)
			return 2;
		else if (days > 60 && days <= 90)
			return 3;
		else if (days > 90)
			// else if (diff > 90 && diff < 120)
			return 4;
		// else if (diff > 120 && diff < 150)
		// agedDebtors.setCategory(5);
		// else if (diff > 150 && diff < 180)
		// agedDebtors.setCategory(6);
		return 0;
	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByCustomerSummary(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getSalesByCustomerSummary")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setName((String) object[0]);
			salesByCustomerDetail.setGroupName((String) object[1]);
			salesByCustomerDetail.setAmount(object[2] == null ? 0
					: ((Double) object[2]).doubleValue());

			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);

	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByItemDetail(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getSalesByItemDetail")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());
		List l = query.list();

		return createSalesByItemDetail(new VList<SalesByCustomerDetail>(l));

	}

	private VList<SalesByCustomerDetail> createSalesByItemDetail(List l) {
		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setItemType(object[0] == null ? 0
					: ((Integer) object[0]).intValue());
			salesByCustomerDetail.setItemName((String) object[1]);
			salesByCustomerDetail.setType(object[2] == null ? 0
					: ((Integer) object[2]).intValue());
			salesByCustomerDetail.setDate(new ClientFinanceDate(
					((BigInteger) object[3]).longValue()));

			salesByCustomerDetail.setNumber((String) object[4]);

			salesByCustomerDetail.setName((String) object[5]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[6] == null ? 0 : ((Double) object[6])
					.intValue());
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setUnitPrice(object[7] == null ? 0
					: ((Double) object[7]).doubleValue());
			salesByCustomerDetail.setDiscount(object[8] == null ? 0
					: ((Double) object[8]).doubleValue());
			salesByCustomerDetail.setAmount(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			salesByCustomerDetail.setSoOrQuoteNumber((String) object[10]);
			salesByCustomerDetail
					.setDueDate(((BigInteger) object[11]) == null ? null
							: new ClientFinanceDate(((BigInteger) object[11])
									.longValue()));
			salesByCustomerDetail.setDeliveryDate(object[12] == null ? null
					: new ClientFinanceDate(((BigInteger) object[12])
							.longValue()));
			salesByCustomerDetail.setItemGroup((String) object[13]);
			salesByCustomerDetail.setDescription((String) object[14]);
			salesByCustomerDetail.setIsVoid(object[15] == null ? true
					: ((Boolean) object[15]).booleanValue());
			salesByCustomerDetail.setPaymentTermName((String) object[16]);

			/*
			 * Clob cl = (Clob) object[17]; if (cl == null) {
			 * 
			 * salesByCustomerDetail.setMemo("");
			 * 
			 * } else {
			 * 
			 * StringBuffer strOut = new StringBuffer(); String aux; try {
			 * BufferedReader br = new BufferedReader(cl .getCharacterStream());
			 * while ((aux = br.readLine()) != null) strOut.append(aux);
			 * salesByCustomerDetail.setMemo(strOut.toString()); } catch
			 * (java.sql.SQLException e1) {
			 * 
			 * } catch (java.io.IOException e2) {
			 * 
			 * } }
			 */
			salesByCustomerDetail.setMemo((String) object[17]);
			salesByCustomerDetail.setReference((String) object[18]);
			salesByCustomerDetail.setTransactionId(((BigInteger) object[19])
					.longValue());
			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);
	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByItemSummary(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getSalesByItemSummary")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();
			salesByCustomerDetail.setItemType(object[0] == null ? 0
					: (Integer) object[0]);
			salesByCustomerDetail.setItemName((String) object[1]);
			salesByCustomerDetail.setItemGroup((String) object[2]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[3] == null ? 0 : ((Double) object[3])
					.intValue());
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setAmount(object[4] == null ? 0
					: ((Double) object[4]).doubleValue());

			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);

	}

	@Override
	public VList<TransactionHistory> getCustomerTransactionHistory(
			final FinanceDate startDate, final FinanceDate endDate)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();
		ClientFinanceDate date[] = this.getMinimumAndMaximumTransactionDate();
		long start = date[0] != null ? date[0].getDate() : startDate.getDate();
		// Calendar cal = Calendar.getInstance();
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// try {
		// cal.setTime(startDate.getAsDateObject());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// cal.add(Calendar.DAY_OF_MONTH, -1);
		//
		// String end = cal.get(Calendar.YEAR) + "-";
		// end += ((((cal.get(Calendar.MONTH) + 1) + "").length() == 1) ? "0"
		// + cal.get(Calendar.MONTH) : cal.get(Calendar.MONTH) + 1)
		// + "-";
		// end += (((cal.get(Calendar.DAY_OF_MONTH)) + "").length() == 1) ? "0"
		// + cal.get(Calendar.DAY_OF_MONTH) : cal
		// .get(Calendar.DAY_OF_MONTH);

		long end = date[1] != null ? date[1].getDate() : endDate.getDate();

		Query query = session.getNamedQuery("getCustomerTransactionHistory")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("start", start).setParameter("end", end);

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TransactionHistory> queryResult = new VList<TransactionHistory>();
		Set<String> payee = new HashSet<String>();
		Map<String, TransactionHistory> openingBalnaceEntries = new HashMap<String, TransactionHistory>();
		while ((iterator).hasNext()) {

			TransactionHistory transactionHistory = new TransactionHistory();
			object = (Object[]) iterator.next();

			transactionHistory.setName((String) object[0]);
			transactionHistory
					.setType((object[1] == null || ((String) object[16] != null ? (String) object[16]
							: "")
							.equals(AccounterServerConstants.MEMO_OPENING_BALANCE)) ? 0
							: ((Integer) object[1]).intValue());
			transactionHistory.setNumber((String) object[2]);

			transactionHistory.setDate(new ClientFinanceDate(
					((BigInteger) object[3]).longValue()));
			transactionHistory.setInvoicedAmount(object[4] == null ? 0
					: ((Double) object[4]).doubleValue());
			transactionHistory.setPaidAmount(object[5] == null ? 0
					: ((Double) object[5]).doubleValue());
			transactionHistory.setPaymentTerm((String) object[6]);
			transactionHistory
					.setDueDate(((BigInteger) object[7]) == null ? null
							: new ClientFinanceDate(((BigInteger) object[7])
									.longValue()));
			transactionHistory.setDebit(object[8] == null ? 0
					: ((Double) object[8]).doubleValue());
			transactionHistory.setCredit(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			transactionHistory.setDiscount(object[10] == null ? 0
					: ((Double) object[10]).doubleValue());
			transactionHistory.setWriteOff(object[11] == null ? 0
					: ((Double) object[11]).doubleValue());
			transactionHistory.setTransactionId(((BigInteger) object[12])
					.longValue());

			transactionHistory
					.setBeginningBalance((object[13] != null ? ((Double) object[13])
							.doubleValue() : 0.0));
			transactionHistory
					.setIsVoid(object[14] != null ? (Boolean) object[14]
							: false);
			transactionHistory
					.setStatus((object[15] != null) ? (Integer) object[15] : 0);
			transactionHistory.setMemo((String) object[16]);

			Transaction t = (Transaction) getServerObjectForid(
					AccounterCoreType.TRANSACTION,
					transactionHistory.getTransactionId());
			Account account = (t).getEffectingAccount() == null ? t.getPayee() == null ? null
					: t.getPayee().getAccount()
					: t.getEffectingAccount();

			transactionHistory.setAccount(account == null ? "" : account
					.getName());

			if (transactionHistory.getType() == 0) {
				openingBalnaceEntries.put(transactionHistory.getName(),
						transactionHistory);
			} else {

				queryResult.add(transactionHistory);
			}
			payee.add(transactionHistory.getName());
		}

		mergeOpeningBalanceEntries(queryResult, payee, openingBalnaceEntries);

		// return prepareEntriesForVoid(queryResult);
		return new VList<TransactionHistory>(queryResult);
	}

	private List<TransactionHistory> prepareEntriesForVoid(
			List<TransactionHistory> queryResult) {

		List<TransactionHistory> list = new VList<TransactionHistory>();
		for (TransactionHistory transactionHistory : queryResult) {

			if (transactionHistory.getIsVoid() == true) {
				transactionHistory.setInvoicedAmount(0.0);
				transactionHistory.setPaidAmount(0.0);
				list.add(transactionHistory);

				TransactionHistory th = new TransactionHistory();
				th = transactionHistory;
				th.setDebit(transactionHistory.getCredit());
				th.setCredit(transactionHistory.getDebit());
				list.add(th);
			} else {
				list.add(transactionHistory);
			}
		}
		return list;
	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByVendorDetail(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPurchasesByVendorDetail")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		return createPurchasesByVendorDetail(new VList<SalesByCustomerDetail>(l));

	}

	private VList<SalesByCustomerDetail> createPurchasesByVendorDetail(List l) {

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setName((String) object[0]);
			salesByCustomerDetail.setType(object[1] == null ? 0
					: ((Integer) object[1]).intValue());
			salesByCustomerDetail.setDate(new ClientFinanceDate(
					((BigInteger) object[2]).longValue()));
			salesByCustomerDetail.setNumber((String) object[3]);
			salesByCustomerDetail.setPaymentTermName((String) object[4]);
			salesByCustomerDetail
					.setDueDate(((BigInteger) object[5]) == null ? null
							: new ClientFinanceDate(((BigInteger) object[5])
									.longValue()));
			salesByCustomerDetail.setItemName((String) object[6]);
			salesByCustomerDetail.setDescription((String) object[7]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[8] == null ? 0 : ((Double) object[8])
					.intValue());
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setUnitPrice(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			salesByCustomerDetail.setAmount(object[10] == null ? 0
					: ((Double) object[10]).doubleValue());
			salesByCustomerDetail.setDeliveryDate(object[11] == null ? null
					: new ClientFinanceDate(((BigInteger) object[11])
							.longValue()));
			salesByCustomerDetail.setIsVoid(object[12] == null ? true
					: ((Boolean) object[12]).booleanValue());
			salesByCustomerDetail.setReference((String) object[13]);
			salesByCustomerDetail.setTransactionId(((BigInteger) object[14])
					.longValue());
			queryResult.add(salesByCustomerDetail);
		}

		return new VList<SalesByCustomerDetail>(queryResult);
	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByVendorSummary(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPurchasesByVendorSummary")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setName((String) object[0]);
			salesByCustomerDetail.setGroupName((String) object[1]);
			salesByCustomerDetail.setAmount(object[2] == null ? 0
					: ((Double) object[2]).doubleValue());

			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);
	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByItemDetail(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPurchasesByItemDetail")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		return createPurchasesByItemDetail(new VList<SalesByCustomerDetail>(l));

	}

	private VList<SalesByCustomerDetail> createPurchasesByItemDetail(List l) {
		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();

			salesByCustomerDetail.setItemType(object[0] == null ? 0
					: ((Integer) object[0]).intValue());
			salesByCustomerDetail.setItemName((String) object[1]);
			salesByCustomerDetail.setType(object[2] == null ? 0
					: ((Integer) object[2]).intValue());
			salesByCustomerDetail.setNumber((String) object[3]);
			salesByCustomerDetail.setDate(new ClientFinanceDate(
					((BigInteger) object[4]).longValue()));
			salesByCustomerDetail.setPaymentTermName((String) object[5]);
			salesByCustomerDetail.setDueDate(object[6] == null ? null
					: new ClientFinanceDate(((BigInteger) object[6])
							.longValue()));
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[7] == null ? 0 : ((Double) object[7])
					.intValue());
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setUnitPrice(object[8] == null ? 0
					: ((Double) object[8]).doubleValue());
			salesByCustomerDetail.setAmount(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			salesByCustomerDetail.setDeliveryDate(object[10] == null ? null
					: new ClientFinanceDate(((BigInteger) object[10])
							.longValue()));
			salesByCustomerDetail.setIsVoid(object[11] == null ? true
					: ((Boolean) object[11]).booleanValue());
			salesByCustomerDetail.setReference((String) object[12]);
			salesByCustomerDetail.setTransactionId(((BigInteger) object[13])
					.longValue());
			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);
	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByItemSummary(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPurchasesByItemSummary")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesByCustomerDetail> queryResult = new VList<SalesByCustomerDetail>();
		while ((iterator).hasNext()) {

			SalesByCustomerDetail salesByCustomerDetail = new SalesByCustomerDetail();
			object = (Object[]) iterator.next();
			salesByCustomerDetail.setItemType(object[0] == null ? 0
					: (Integer) object[0]);
			salesByCustomerDetail.setItemName((String) object[1]);
			salesByCustomerDetail.setItemGroup((String) object[2]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[3] == null ? 0 : (((Double) object[3])));
			salesByCustomerDetail.setQuantity(quantity);
			salesByCustomerDetail.setAmount(object[4] == null ? 0
					: ((Double) object[4]).doubleValue());

			queryResult.add(salesByCustomerDetail);
		}
		return new VList<SalesByCustomerDetail>(queryResult);

	}

	@Override
	public VList<TransactionHistory> getVendorTransactionHistory(
			final FinanceDate startDate, final FinanceDate endDate)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		ClientFinanceDate date[] = this.getMinimumAndMaximumTransactionDate();
		long start = date[0] != null ? date[0].getDate() : startDate.getDate();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(startDate.getAsDateObject());
		cal.add(Calendar.DAY_OF_MONTH, -1);

		String end = cal.get(Calendar.YEAR) + "-";
		end += ((((cal.get(Calendar.MONTH) + 1) + "").length() == 1) ? "0"
				+ cal.get(Calendar.MONTH) : cal.get(Calendar.MONTH) + 1)
				+ "-";
		end += (((cal.get(Calendar.DAY_OF_MONTH)) + "").length() == 1) ? "0"
				+ cal.get(Calendar.DAY_OF_MONTH) : cal
				.get(Calendar.DAY_OF_MONTH);

		Query query = session.getNamedQuery("getVendorTransactionHistory")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("start", start)
				.setParameter("end", new ClientFinanceDate(end).getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		Set<String> payee = new HashSet<String>();
		Map<String, TransactionHistory> openingBalnaceEntries = new HashMap<String, TransactionHistory>();
		List<TransactionHistory> queryResult = new VList<TransactionHistory>();
		while ((iterator).hasNext()) {

			TransactionHistory transactionHistory = new TransactionHistory();
			object = (Object[]) iterator.next();

			transactionHistory.setName((String) object[0]);
			transactionHistory
					.setType((object[1] == null || ((String) object[7] != null ? (String) object[7]
							: "")
							.equals(AccounterServerConstants.MEMO_OPENING_BALANCE)) ? 0
							: ((Integer) object[1]).intValue());
			transactionHistory.setDate(new ClientFinanceDate(
					((BigInteger) object[2]).longValue()));
			transactionHistory.setNumber((String) object[3]);
			transactionHistory.setInvoicedAmount(object[4] == null ? 0
					: ((Double) object[4]).doubleValue());
			transactionHistory.setPaidAmount(object[5] == null ? 0
					: ((Double) object[5]).doubleValue());
			transactionHistory.setDiscount(object[6] == null ? 0
					: ((Double) object[6]).doubleValue());

			/*
			 * Clob cl = (Clob) object[7]; if (cl == null) {
			 * 
			 * transactionHistory.setMemo("");
			 * 
			 * } else {
			 * 
			 * StringBuffer strOut = new StringBuffer(); String aux; try {
			 * BufferedReader br = new BufferedReader(cl .getCharacterStream());
			 * while ((aux = br.readLine()) != null) strOut.append(aux);
			 * transactionHistory.setMemo(strOut.toString()); } catch
			 * (java.sql.SQLException e1) {
			 * 
			 * } catch (java.io.IOException e2) {
			 * 
			 * } }
			 */

			transactionHistory.setMemo((String) object[7]);
			transactionHistory
					.setDueDate(((BigInteger) object[8]) == null ? null
							: new ClientFinanceDate(((BigInteger) object[8])
									.longValue()));
			transactionHistory.setPaymentTerm((String) object[9]);
			transactionHistory.setDebit(object[10] == null ? 0
					: ((Double) object[10]).doubleValue());
			transactionHistory.setCredit(object[11] == null ? 0
					: ((Double) object[11]).doubleValue());
			transactionHistory.setIsVoid(object[12] == null ? true
					: ((Boolean) object[12]).booleanValue());
			transactionHistory.setReference((String) object[13]);
			transactionHistory.setTransactionId(((BigInteger) object[14])
					.longValue());
			transactionHistory
					.setBeginningBalance((object[15] != null ? (((Double) object[15])
							.doubleValue()) : 0.0));
			transactionHistory
					.setStatus((object[16] != null) ? (Integer) object[16] : 0);

			Transaction t = (Transaction) getServerObjectForid(
					AccounterCoreType.TRANSACTION,
					transactionHistory.getTransactionId());

			if (transactionHistory.getType() == Transaction.TYPE_CREDIT_CARD_EXPENSE)
				transactionHistory.setName(t.getInvolvedPayee().getName());

			Account account = t.getEffectingAccount();
			if (account == null) {
				account = t.getInvolvedPayee().getAccount();
			}
			transactionHistory.setAccount(account.getName());

			if (transactionHistory.getType() == 0) {

				openingBalnaceEntries.put(transactionHistory.getName(),
						transactionHistory);
			} else {
				queryResult.add(transactionHistory);
			}
			payee.add(transactionHistory.getName());

		}

		mergeOpeningBalanceEntries(queryResult, payee, openingBalnaceEntries);

		// return prepareEntriesForVoid(queryResult);
		return new VList<TransactionHistory>(queryResult);

	}

	private void mergeOpeningBalanceEntries(
			List<TransactionHistory> queryResult, Set<String> payee,
			Map<String, TransactionHistory> openingBalnaceEntries) {

		for (String payeeName : payee) {
			int index = -1;
			for (int i = 0; i < queryResult.size(); i++) {
				TransactionHistory entry = queryResult.get(i);
				if (entry.getName().equals(payeeName)) {
					index = i;
					break;
				}
			}

			if (index != -1 && openingBalnaceEntries.containsKey(payeeName)) {
				queryResult.add(index, openingBalnaceEntries.get(payeeName));
			}
		}

	}

	@Override
	public VList<AmountsDueToVendor> getAmountsDueToVendor(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getAmountsDueToVendor")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<AmountsDueToVendor> queryResult = new VList<AmountsDueToVendor>();
		while ((iterator).hasNext()) {

			AmountsDueToVendor amountsDueToVendor = new AmountsDueToVendor();
			object = (Object[]) iterator.next();

			amountsDueToVendor.setName((String) object[0]);
			amountsDueToVendor.setFileAs((String) object[1]);
			amountsDueToVendor.setIsActive(object[2] == null ? true
					: ((Boolean) object[2]).booleanValue());
			amountsDueToVendor.setWebPageAddress((String) object[3]);
			amountsDueToVendor.setTaxAgencySince(object[4] == null ? null
					: new ClientFinanceDate((Long) object[4]));
			amountsDueToVendor.setAmount(object[5] == null ? 0
					: ((Double) object[5]).doubleValue());
			amountsDueToVendor.setAddress((String) object[6]);
			amountsDueToVendor.setCity((String) object[7]);
			amountsDueToVendor.setState((String) object[8]);
			amountsDueToVendor.setZip((String) object[9]);
			amountsDueToVendor.setPhone((String) object[10]);
			amountsDueToVendor.setFax((String) object[11]);
			amountsDueToVendor.setEmail((String) object[12]);
			amountsDueToVendor.setType(object[13] == null ? 0
					: ((Integer) object[13]).intValue());

			queryResult.add(amountsDueToVendor);
		}
		return new VList<AmountsDueToVendor>(queryResult);
	}

	@Override
	public VList<MostProfitableCustomers> getMostProfitableCustomers(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getMostProfitableCustomers")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<MostProfitableCustomers> queryResult = new VList<MostProfitableCustomers>();
		while ((iterator).hasNext()) {

			MostProfitableCustomers mostProfitableCustomers = new MostProfitableCustomers();
			object = (Object[]) iterator.next();

			mostProfitableCustomers.setCustomer((String) object[0]);
			mostProfitableCustomers.setInvoicedAmount(object[1] == null ? 0
					: ((Double) object[1]).doubleValue());
			mostProfitableCustomers.setStandardCost(object[2] == null ? 0
					: ((Double) object[2]).doubleValue());
			mostProfitableCustomers.setMargin(object[3] == null ? 0
					: ((Double) object[3]).doubleValue());
			mostProfitableCustomers.setCustomerGroup((String) object[4]);
			mostProfitableCustomers.setFileAs((String) object[5]);
			if (mostProfitableCustomers.getInvoicedAmount() == 0.0)
				mostProfitableCustomers.setMarginPercentage(0.0);
			else {
				mostProfitableCustomers
						.setMarginPercentage(mostProfitableCustomers
								.getMargin()
								/ mostProfitableCustomers.getInvoicedAmount()
								* 100);
			}
			mostProfitableCustomers.setCost(mostProfitableCustomers
					.getStandardCost()
					+ mostProfitableCustomers.getBilledCost());

			queryResult.add(mostProfitableCustomers);
		}

		query = session.getNamedQuery("getEntry.from.journalEntryType");

		List<JournalEntry> nonInvoicedLines = (List<JournalEntry>) query.list();

		for (MostProfitableCustomers mpc : queryResult) {
			for (JournalEntry je : nonInvoicedLines) {
				Entry e1 = je.getEntry().get(0);
				Entry e2 = je.getEntry().get(1);
				if (e1.getType() == Entry.TYPE_CUSTOMER
						&& e1.getCustomer().getName().equals(mpc.getCustomer())) {
					mpc.setBilledCost(mpc.getBilledCost()
							+ (!DecimalUtil.isEquals(e2.getDebit(), 0.0) ? -1
									* e2.getDebit() : e2.getCredit()));
				} else if (e2.getType() == Entry.TYPE_CUSTOMER
						&& e2.getCustomer().getName().equals(mpc.getCustomer())) {
					mpc.setBilledCost(mpc.getBilledCost()
							+ (!DecimalUtil.isEquals(e1.getDebit(), 0.0) ? -1
									* e1.getDebit() : e1.getCredit()));
				}

			}

		}

		return new VList<MostProfitableCustomers>(queryResult);
	}

	public VList<MostProfitableCustomers> getProfitabilityByCustomerDetail(
			final long customer, FinanceDate startDate, FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getEntry.orderedby.id")
				.setParameter(0, customer);
		List<JournalEntry> nonInvoicedLines = (List<JournalEntry>) query.list();

		List<MostProfitableCustomers> profitabilityByCustomerDetailList = new VList<MostProfitableCustomers>();
		int count = 0;
		if (nonInvoicedLines != null) {
			for (JournalEntry je : nonInvoicedLines) {
				count++;
				for (Entry entry : je.getEntry()) {

					if (entry.getType() == Entry.TYPE_FINANCIAL_ACCOUNT) {
						MostProfitableCustomers listItem = new MostProfitableCustomers();
						if (count == 1) {
							listItem.setTransactionType("Opening Balance");
						} else {
							listItem.setTransactionType("Journal Entry");
						}
						listItem.setCustomer(entry.getMemo());
						listItem.setItemDescriptioin(entry.getMemo());
						listItem.setItemName(entry.getAccount().getName());
						listItem.setTransactionDate(new ClientFinanceDate(je
								.getDate().getDate()));
						listItem.setTransactionNumber(je.getNumber());
						listItem.setBilledCost(!DecimalUtil.isEquals(
								entry.getDebit(), 0.0) ? -1 * entry.getDebit()
								: entry.getCredit());
						listItem.setMargin(!DecimalUtil.isEquals(
								entry.getDebit(), 0.0) ? -1 * entry.getDebit()
								: entry.getCredit());
						listItem.setMarginPercentage(!DecimalUtil.isEquals(
								entry.getDebit(), 0.0) ? -100 : 100);
						profitabilityByCustomerDetailList.add(listItem);
					}
				}
			}
		}

		query = session
				.getNamedQuery("getProfitabilityByCustomerDetail_InvoicedLines")
				.setParameter("customerId", customer)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		while ((iterator).hasNext()) {

			MostProfitableCustomers mostProfitableCustomers = new MostProfitableCustomers();
			object = (Object[]) iterator.next();

			mostProfitableCustomers.setCustomer((String) object[0]);
			mostProfitableCustomers.setTransactionType((String) object[1]);
			mostProfitableCustomers
					.setTransactionDate(object[2] != null ? new ClientFinanceDate(
							(Long) object[2]) : null);
			mostProfitableCustomers
					.setTransactionNumber(object[3] != null ? ((String) object[3])
							: "0");
			mostProfitableCustomers.setItemName((String) object[4]);
			ClientQuantity quantity = new ClientQuantity();
			quantity.setValue(object[5] != null ? ((Double) object[5]) : 0.0);
			mostProfitableCustomers.setQuantity(quantity);
			mostProfitableCustomers.setInvoicedAmount(object[6] == null ? 0
					: ((Double) object[6]).doubleValue());
			mostProfitableCustomers.setStandardCost(object[7] == null ? 0
					: ((Double) object[7]).doubleValue());
			mostProfitableCustomers.setMargin(object[8] == null ? 0
					: ((Double) object[8]).doubleValue());
			mostProfitableCustomers.setMarginPercentage(object[9] == null ? 0
					: ((Double) object[9]).doubleValue());
			mostProfitableCustomers.setCustomerGroup((String) object[10]);
			mostProfitableCustomers.setStatus(object[11] == null ? 0
					: (Integer) object[11]);
			mostProfitableCustomers.setReference((String) object[12]);
			profitabilityByCustomerDetailList.add(mostProfitableCustomers);
		}
		return new VList<MostProfitableCustomers>(
				profitabilityByCustomerDetailList);
	}

	@Override
	public VList<TransactionDetailByTaxItem> getTransactionDetailByTaxItem(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getTransactionDetailByTaxItem")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		if (l.size() > 0) {
			return createTransactionDetailByTaxItemEntries(new VList<TransactionDetailByTaxItem>(
					l));
		} else
			return null;

	}

	@Override
	public VList<TransactionDetailByTaxItem> getTransactionDetailByTaxItem(
			final String taxItemName, final FinanceDate startDate,
			final FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery(
						"getTransactionDetailByTaxItemForParticularTaxItem")
				.setParameter("taxItemName", taxItemName)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		if (l.size() > 0) {
			return createTransactionDetailByTaxItemEntries(new VList<TransactionDetailByTaxItem>(
					l));
		} else
			return null;

	}

	private VList<TransactionDetailByTaxItem> createTransactionDetailByTaxItemEntries(
			List l) {
		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TransactionDetailByTaxItem> queryResult = new VList<TransactionDetailByTaxItem>();
		while ((iterator).hasNext()) {

			TransactionDetailByTaxItem TransactionDetailByTaxItem = new TransactionDetailByTaxItem();
			object = (Object[]) iterator.next();
			TransactionDetailByTaxItem.setTransactionId((Long) object[0]);
			// transactionDetailByTaxcode
			// .setType(object[1]== null ? 0 :((Integer) object[1]).intValue());
			TransactionDetailByTaxItem.setPayeeName((String) object[1]);
			TransactionDetailByTaxItem.setTaxAgencyName((String) object[2]);
			TransactionDetailByTaxItem.setTaxItemName((String) object[3]);
			TransactionDetailByTaxItem.setRate(object[4] == null ? 0
					: ((Double) object[4]).doubleValue());
			TransactionDetailByTaxItem.setTransactionType(object[5] == null ? 0
					: ((Integer) object[5]).intValue());
			TransactionDetailByTaxItem.setDate(new ClientFinanceDate(
					(Long) object[6]));
			TransactionDetailByTaxItem.setNumber((String) object[7]);

			/*
			 * if (object[8] == null || object[8].equals("")) {
			 * 
			 * transactionDetailByTaxcode.setMemo("");
			 * 
			 * } else { Clob cl = (Clob) object[8]; StringBuffer strOut = new
			 * StringBuffer(); String aux; try { BufferedReader br = new
			 * BufferedReader(cl .getCharacterStream()); while ((aux =
			 * br.readLine()) != null) strOut.append(aux);
			 * transactionDetailByTaxcode.setMemo(strOut.toString()); } catch
			 * (java.sql.SQLException e1) {
			 * 
			 * } catch (java.io.IOException e2) {
			 * 
			 * } }
			 */
			TransactionDetailByTaxItem.setMemo((String) object[8]);
			TransactionDetailByTaxItem.setTaxableAmount(((Double) object[9])
					.doubleValue());
			TransactionDetailByTaxItem.setSalesTaxAmount(((Double) object[10])
					.doubleValue());
			TransactionDetailByTaxItem.setIsVoid(((Boolean) object[11])
					.booleanValue());

			queryResult.add(TransactionDetailByTaxItem);
		}
		return new VList<TransactionDetailByTaxItem>(queryResult);
	}

	@Override
	public VList<AccountRegister> getAccountRegister(
			final FinanceDate startDate, final FinanceDate endDate,
			final long accountId) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getAccountRegister")
				.setParameter("accountId", accountId)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<AccountRegister> queryResult = new VList<AccountRegister>();
		while ((iterator).hasNext()) {

			AccountRegister accountRegister = new AccountRegister();
			object = (Object[]) iterator.next();

			accountRegister.setDate(new ClientFinanceDate((Long) object[0]));
			accountRegister
					.setType(object[1] == null ? 0 : (Integer) object[1]);
			accountRegister.setNumber((String) object[2]);
			accountRegister.setAmount(object[3] == null ? 0
					: ((Double) object[3]).doubleValue());
			accountRegister.setPayTo((String) object[4]);
			accountRegister.setCheckNumber(object[5] == null ? null
					: ((String) object[5]));
			accountRegister.setAccount((String) object[6]);
			/*
			 * Clob cl = (Clob) object[7]; if (cl == null) {
			 * 
			 * accountRegister.setMemo("");
			 * 
			 * } else {
			 * 
			 * StringBuffer strOut = new StringBuffer(); String aux; try {
			 * BufferedReader br = new BufferedReader(cl .getCharacterStream());
			 * while ((aux = br.readLine()) != null) strOut.append(aux);
			 * accountRegister.setMemo(strOut.toString()); } catch
			 * (java.sql.SQLException e1) {
			 * 
			 * } catch (java.io.IOException e2) {
			 * 
			 * } }
			 */
			accountRegister.setMemo((String) object[7]);
			accountRegister.setBalance(object[8] == null ? 0
					: ((Double) object[8]).doubleValue());
			accountRegister.setTransactionId((Long) object[9]);
			accountRegister.setVoided(object[10] == null ? false
					: (Boolean) object[10]);

			queryResult.add(accountRegister);
		}
		return new VList<AccountRegister>(queryResult);
	}

	@Override
	public VList<TransactionDetailByAccount> getTransactionDetailByAccount(
			final FinanceDate startDate, final FinanceDate endDate)
			throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();

			List<TransactionDetailByAccount> transactionDetailByAccountList = new VList<TransactionDetailByAccount>();

			Query query = session
					.getNamedQuery("getTransactionDetailByAccount")
					.setParameter("startDate", startDate.getDate())
					.setParameter("endDate", endDate.getDate());

			List list = query.list();

			if (list != null && list.size() > 0) {
				createTransasctionDetailByAccount(list,
						transactionDetailByAccountList);
			}

			if (transactionDetailByAccountList != null) {
				return new VList<TransactionDetailByAccount>(
						transactionDetailByAccountList);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	private void createTransasctionDetailByAccount(List list,
			List<TransactionDetailByAccount> transactionMakeDepositsList)
			throws DAOException {
		Object object[] = null;

		Iterator i = list.iterator();
		while (i.hasNext()) {

			object = (Object[]) i.next();
			TransactionDetailByAccount transactionDetailByAccount = new TransactionDetailByAccount();

			transactionDetailByAccount.setAccountName((object[0] == null ? null
					: ((String) object[0])));
			transactionDetailByAccount.setTransactionType(object[3] == null ? 0
					: (((Integer) object[3]).intValue()));
			transactionDetailByAccount
					.setTotal(object[6] != null ? (Double) object[6] : 0.0);

			if (transactionDetailByAccount.getAccountName().equals(
					AccounterServerConstants.SALES_TAX_VAT_UNFILED)
					&& transactionDetailByAccount.getTransactionType() == Transaction.TYPE_VAT_RETURN) {
				if (transactionDetailByAccount.getTotal() < 0)
					transactionDetailByAccount.setName("Box3");
				if (transactionDetailByAccount.getTotal() > 0)
					transactionDetailByAccount.setName("Box4");

			} else
				transactionDetailByAccount.setName((object[1] == null ? null
						: ((String) object[1])));

			transactionDetailByAccount.setTransactionId((Long) object[2]);

			transactionDetailByAccount
					.setTransactionDate(object[4] == null ? null
							: new ClientFinanceDate((Long) object[4]));
			transactionDetailByAccount.setTransactionNumber((String) object[5]);

			transactionDetailByAccount
					.setTotal(object[6] != null ? (Double) object[6] : 0.0);
			transactionDetailByAccount.setMemo((String) object[7]);
			transactionMakeDepositsList.add(transactionDetailByAccount);

		}
	}

	@Override
	public VList<SalesTaxLiability> getSalesTaxLiabilityReport(
			FinanceDate startDate, FinanceDate endDate)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();
		ClientFinanceDate date[] = this.getMinimumAndMaximumTransactionDate();
		long start = date[0] != null ? date[0].getDate() : startDate.getDate();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(startDate.getAsDateObject());
		cal.add(Calendar.DAY_OF_MONTH, -1);

		String end = cal.get(Calendar.YEAR) + "-";
		end += (cal.get(Calendar.MONTH) + 1) + "-";
		end += cal.get(Calendar.DAY_OF_MONTH);

		Query query = session.getNamedQuery("getSalesTaxLiabilityReport")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("start", start)
				.setParameter("end", end);

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<SalesTaxLiability> queryResult = new VList<SalesTaxLiability>();
		while ((iterator).hasNext()) {

			SalesTaxLiability salesTaxLiability = new SalesTaxLiability();
			object = (Object[]) iterator.next();
			salesTaxLiability.setTaxAgencyName(object[0] == null ? null
					: (String) object[0]);
			salesTaxLiability.setTaxItemName(object[1] == null ? null
					: (String) object[1]);
			salesTaxLiability.setTaxRate(object[2] == null ? 0.0
					: ((Double) object[2]).doubleValue());
			salesTaxLiability.setTaxCollected(object[3] == null ? 0.0
					: ((Double) object[3]).doubleValue());
			salesTaxLiability.setTotalSales(object[4] == null ? 0.0
					: ((Double) object[4]).doubleValue());
			salesTaxLiability.setTaxable(object[5] == null ? 0.0
					: ((Double) object[5]).doubleValue());
			salesTaxLiability.setNonTaxable(object[6] == null ? 0.0
					: ((Double) object[6]).doubleValue());
			// salesTaxLiability.setNonTaxableOther(object[7] == null ?
			// null:(Double)object[7]);
			salesTaxLiability.setBeginningBalance(object[7] == null ? 0.0
					: ((Double) object[7]).doubleValue());
			queryResult.add(salesTaxLiability);
		}
		return new VList<SalesTaxLiability>(queryResult);
	}

	@Override
	public VList<Item> getPurchaseReportItems(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPurchaseReportItems")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<Item> queryResult = new VList<Item>();
		while ((iterator).hasNext()) {

			Item item = new Item();
			object = (Object[]) iterator.next();

			item.setType(object[1] == null ? 0 : ((Integer) object[1])
					.intValue());
			item.setName((String) object[2]);
			queryResult.add(item);
		}
		return new VList<Item>(queryResult);
	}

	@Override
	public VList<Item> getSalesReportItems(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getSalesReportItems")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<Item> queryResult = new VList<Item>();
		while ((iterator).hasNext()) {

			Item item = new Item();
			object = (Object[]) iterator.next();
			item.setType(object[1] == null ? 0 : ((Integer) object[1])
					.intValue());
			item.setName((String) object[2]);
			queryResult.add(item);
		}
		return new VList<Item>(queryResult);
	}

	@Override
	public VList<Customer> getTransactionHistoryCustomers(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getTransactionHistoryCustomers")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<Customer> queryResult = new VList<Customer>();
		while ((iterator).hasNext()) {

			Customer customer = new Customer();
			object = (Object[]) iterator.next();
			customer.setName((String) object[1]);
			queryResult.add(customer);
		}
		return new VList<Customer>(queryResult);
	}

	@Override
	public VList<Vendor> getTransactionHistoryVendors(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getTransactionHistoryVendors")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<Vendor> queryResult = new VList<Vendor>();
		while ((iterator).hasNext()) {

			Vendor vendor = new Vendor();
			object = (Object[]) iterator.next();
			vendor.setName((String) object[1]);
			queryResult.add(vendor);
		}
		return new VList<Vendor>(queryResult);
	}

	@Override
	public ClientFinanceDate[] getMinimumAndMaximumTransactionDate()
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getMinimumAndMaximumTransactionDate");
		List list = query.list();
		Object[] object = null;
		Iterator iterator = list.iterator();
		// List<Vendor> queryResult = new VList<Vendor>();
		ClientFinanceDate startDate = new ClientFinanceDate();
		ClientFinanceDate endDate = new ClientFinanceDate();
		if ((iterator).hasNext()) {

			object = (Object[]) iterator.next();
			startDate = (object[0] == null ? null : new ClientFinanceDate(
					(Long) object[0]));
			endDate = (object[1] == null ? null : new ClientFinanceDate(
					(Long) object[1]));
		}
		return new ClientFinanceDate[] { startDate, endDate };
	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByItemDetail(
			String itemName, FinanceDate startDate, FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getPurchasesByItemDetailForParticularItem")
				.setParameter("itemName", itemName)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l = query.list();
		return createPurchasesByItemDetail(new VList<SalesByCustomerDetail>(l));

	}

	@Override
	public VList<SalesByCustomerDetail> getPurchasesByVendorDetail(
			String vendorName, FinanceDate startDate, FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session
				.getNamedQuery("getPurchasesByVendorDetailForParticularVendor")
				.setParameter("vendorName", vendorName)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return createPurchasesByVendorDetail(new VList<SalesByCustomerDetail>(l));

	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByCustomerDetailReport(
			String customerName, FinanceDate startDate, FinanceDate endDate)
			throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session
				.getNamedQuery("getSalesByCustomerDetailForParticularCustomer")
				.setParameter("customerName", customerName)
				.setParameter("startDate",

				startDate.getDate()).setParameter("endDate", endDate.getDate()))
				.list();

		return createSalesByCustomerDetailReport(new VList<SalesByCustomerDetail>(
				l));

	}

	@Override
	public VList<SalesByCustomerDetail> getSalesByItemDetail(String itemName,
			FinanceDate startDate, FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session
				.getNamedQuery("getSalesByItemDetailForParticularItem")
				.setParameter(

				"itemName", itemName)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return createSalesByItemDetail(new VList<SalesByCustomerDetail>(l));

	}

	@Override
	public VList<TransactionDetailByAccount> getTransactionDetailByAccount(
			String accountName, FinanceDate startDate, FinanceDate endDate)
			throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();

			Query query = session
					.getNamedQuery(
							"getTransactionDetailByAccount_ForParticularAccount")
					.setParameter("accountName", accountName).setParameter(

					"startDate", startDate.getDate())
					.setParameter("endDate", endDate.getDate());

			List<TransactionDetailByAccount> transactionDetailByAccountList = new VList<TransactionDetailByAccount>();
			List list = query.list();

			if (list != null && list.size() > 0) {
				createTransasctionDetailByAccount(list,
						transactionDetailByAccountList);
			}

			if (transactionDetailByAccountList != null) {
				return new VList<TransactionDetailByAccount>(
						transactionDetailByAccountList);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@Override
	public VList<TrialBalance> getBalanceSheetReport(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getBalanceSheet")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		double netIncome = 0.0;
		netIncome = getNetIncome(startDate, endDate,
				"getNetIncome_Closing_postings_Included");

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TrialBalance> queryResult = new VList<TrialBalance>();

		TrialBalance netIncomeTB = new TrialBalance();
		netIncomeTB.setAccountName("P&L Brought Forward/YTD");
		netIncomeTB.setBaseType(Account.BASETYPE_EQUITY);
		netIncomeTB.setAccountNumber("3100");
		netIncomeTB.setAmount(netIncome);

		while ((iterator).hasNext()) {

			TrialBalance t = new TrialBalance();
			object = (Object[]) iterator.next();

			// this condition is to filter unnecessary rows
			// if ((object[6] == null ? 0 : ((Double) object[6]).doubleValue())
			// != 0.0) {

			t.setAccountId(((BigInteger) object[0]).longValue());
			t.setAccountName((String) object[1]);
			t.setAccountNumber((String) object[2]);
			t.setAccountType(object[3] == null ? 0 : ((Integer) object[3])
					.intValue());
			t.setCashFlowCategory(object[4] == null ? 0 : ((Integer) object[4])
					.intValue());
			Account parentAccount = (object[5] == null) ? null
					: (Account) session.get(Account.class,
							((BigInteger) object[5]).longValue());
			if (parentAccount != null) {
				t.setParentAccount(parentAccount.getID());
			}
			t.setAmount(object[6] == null ? 0 : ((Double) object[6])
					.doubleValue());
			t.setAccountFlow((String) object[7]);
			t.setBaseType(object[8] == null ? 0 : (Integer) object[8]);
			t.setSubBaseType(object[9] == null ? 0 : (Integer) object[9]);
			t.setGroupType(object[10] == null ? 0 : (Integer) object[10]);

			queryResult.add(t);

			// }

		}
		// List<TrialBalance> sortedList = sortTheList(queryResult);
		// sortedList.add(netIncomeTB);

		List<TrialBalance> sortedList = getBalanceSheetSorted(queryResult);
		sortedList.add(netIncomeTB);

		return new VList<TrialBalance>(sortedList);

	}

	@Override
	public VList<TrialBalance> getProfitAndLossReport(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		FinanceDate startDate1 = ((FinanceDate) ((session
				.getNamedQuery("getFiscalYear.by.check.isCurrentFiscalYearistrue"))
				.list().get(0)));

		/*
		 * Here endDate1 is used to store the previous month of endDate value
		 */
		int year = endDate.getYear();
		int month = endDate.getMonth() - 1;
		year = (month == 0) ? year - 1 : year;
		month = (month == 0) ? 12 : month;
		FinanceDate endDate1 = new FinanceDate(year, month, 31);

		if (year != startDate1.getYear())
			startDate1 = new FinanceDate(year, 01, 01);
		// + ((month + "").length() == 1 ? "0" + month : month) + "01");

		List l = ((Query) session.getNamedQuery("getProfitAndLoss")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("startDate1", startDate1.getDate())
				.setParameter("endDate1", endDate1.getDate())).list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TrialBalance> queryResult = new VList<TrialBalance>();
		while ((iterator).hasNext()) {

			TrialBalance t = new TrialBalance();
			object = (Object[]) iterator.next();

			// this condition is to filter unnecessary rows
			// if ((object[6] == null ? 0 : ((Double) object[6]).doubleValue())
			// != 0.0) {

			t.setAccountId(((BigInteger) object[0]).longValue());
			t.setAccountName((String) object[1]);
			t.setAccountNumber((String) object[2]);
			t.setAccountType(object[3] == null ? 0 : ((Integer) object[3])
					.intValue());
			t.setCashFlowCategory(object[4] == null ? 0 : ((Integer) object[4])
					.intValue());
			Account parentAccount = (object[5] == null) ? null
					: (Account) session.get(Account.class,
							((BigInteger) object[5]).longValue());
			if (parentAccount != null) {
				t.setParentAccount(parentAccount.getID());
			}
			t.setAmount(object[6] == null ? 0 : ((Double) object[6])
					.doubleValue());
			t.setTotalAmount(object[7] == null ? 0 : ((Double) object[7])
					.doubleValue());
			t.setAccountFlow((String) object[8]);
			t.setBaseType(object[9] == null ? 0 : (Integer) object[9]);
			t.setSubBaseType(object[10] == null ? 0 : (Integer) object[10]);
			t.setGroupType(object[11] == null ? 0 : (Integer) object[11]);

			queryResult.add(t);
			// }

		}
		List<TrialBalance> sortedResult = new VList<TrialBalance>();
		List<TrialBalance> otherExpenseList = new VList<TrialBalance>();

		sortedResult = sortTheList(queryResult);
		int index = 0;
		Iterator iter = sortedResult.listIterator();
		while (iter.hasNext()) {
			TrialBalance tb = (TrialBalance) iter.next();
			if (tb.getAccountType() == ClientAccount.TYPE_COST_OF_GOODS_SOLD)
				index = sortedResult.indexOf(tb);
			if (tb.getAccountType() == ClientAccount.TYPE_OTHER_EXPENSE) {
				otherExpenseList.add(tb);
				iter.remove();
			}
		}

		if (otherExpenseList.size() != 0)
			sortedResult.addAll((index + 1), otherExpenseList);
		return new VList<TrialBalance>(sortedResult);
	}

	private VList<TrialBalance> removeUnwantedEntries(
			List<TrialBalance> sortTheList) {
		VList<TrialBalance> list = new VList<TrialBalance>();
		for (TrialBalance tb : sortTheList) {
			if (!DecimalUtil.isEquals(tb.getAmount(), 0.0)
					|| !DecimalUtil.isEquals(tb.getTotalAmount(), 0.0)
					|| hasChilds(tb, sortTheList)) {
				list.add(tb);
			}
		}
		return list;
	}

	private boolean hasChilds(TrialBalance tb, List<TrialBalance> sortTheList) {

		for (TrialBalance balance : sortTheList) {
			if (balance.getAccountFlow().startsWith(tb.getAccountFlow() + "."))
				return true;
		}

		return false;
	}

	@Override
	public VList<TrialBalance> getCashFlowReport(FinanceDate startDate,
			FinanceDate endDate) throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		ClientFinanceDate date[] = this.getMinimumAndMaximumTransactionDate();
		long start = date[0] != null ? date[0].getDate() : startDate.getDate();
		// Calendar cal = Calendar.getInstance();
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// cal.setTime(startDate.getAsDateObject());

		// cal.add(Calendar.DAY_OF_MONTH, -1);
		//
		// String end = cal.get(Calendar.YEAR) + "-";
		// end += ((((cal.get(Calendar.MONTH) + 1) + "").length() == 1) ? "0"
		// + cal.get(Calendar.MONTH) : cal.get(Calendar.MONTH) + 1)
		// + "-";
		// end += (((cal.get(Calendar.DAY_OF_MONTH)) + "").length() == 1) ? "0"
		// + cal.get(Calendar.DAY_OF_MONTH) : cal
		// .get(Calendar.DAY_OF_MONTH);

		long end = date[1] != null ? date[0].getDate() : endDate.getDate();

		List l = ((Query) session.getNamedQuery("getCashFlowStatement")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("start", start).setParameter("end", end)).list();

		double netIncome = 0.0;
		netIncome = getNetIncome(startDate, endDate, "getNetIncome");

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<TrialBalance> queryResult = new VList<TrialBalance>();
		TrialBalance netIncomeTB = new TrialBalance();
		netIncomeTB.setAccountName("Net Income");
		netIncomeTB.setAmount(netIncome);
		while ((iterator).hasNext()) {

			TrialBalance t = new TrialBalance();
			object = (Object[]) iterator.next();

			// this condition is to filter unnecessary rows
			// if ((object[6] == null ? 0 : ((Double) object[6]).doubleValue())
			// != 0.0) {

			t.setAccountId(((BigInteger) object[0]).longValue());
			t.setAccountName((String) object[1]);
			t.setAccountNumber((String) object[2]);
			t.setAccountType(object[3] == null ? 0 : ((Integer) object[3])
					.intValue());
			t.setCashFlowCategory(object[4] == null ? 0 : ((Integer) object[4])
					.intValue());
			Account parentAccount = (object[5] == null) ? null
					: (Account) session.get(Account.class,
							((BigInteger) object[5]).longValue());
			if (parentAccount != null) {
				t.setParentAccount(parentAccount.getID());
			}
			if (t.getAccountType() == Account.TYPE_ACCOUNT_RECEIVABLE
					|| t.getAccountType() == Account.TYPE_OTHER_CURRENT_ASSET
					|| t.getAccountType() == Account.TYPE_FIXED_ASSET
					|| t.getAccountType() == Account.TYPE_OTHER_ASSET) {

				t.setAmount(object[6] == null ? 0 : -1
						* ((Double) object[6]).doubleValue());
			} else {
				t.setAmount(object[6] == null ? 0 : ((Double) object[6])
						.doubleValue());
			}

			t.setCashAtBeginningOfPeriod((object[7] == null ? 0.0
					: ((Double) object[7]).doubleValue()));
			t.setAccountFlow((String) object[8]);
			t.setBaseType(object[9] == null ? 0 : (Integer) object[9]);
			t.setSubBaseType(object[10] == null ? 0 : (Integer) object[10]);
			t.setGroupType(object[11] == null ? 0 : (Integer) object[11]);

			queryResult.add(t);
			// }

		}
		List<TrialBalance> sortedList = sortTheList(queryResult);
		sortedList.add(0, netIncomeTB);
		return new VList<TrialBalance>(sortedList);

	}

	private double getNetIncome(FinanceDate startDate, FinanceDate endDate,
			String query) {

		Session session = HibernateUtil.getCurrentSession();

		Query q = session.getNamedQuery(query)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate());

		List l1 = q.list();
		double netIncome = 0.0;
		if (l1 != null && l1.size() > 0) {
			if (l1.get(0) != null)
				netIncome = (Double) l1.get(0);
		}
		return netIncome;
	}

	private List<TrialBalance> sortTheList(List<TrialBalance> queryResult) {
		List<TrialBalance> sortedList = new VList<TrialBalance>();

		for (TrialBalance t : queryResult) {

			if (t.getParentAccount() == 0) {

				if (!sortedList.contains(t)) {
					String str = t.getAccountFlow();
					// if (t.getAmount() != 0.0) {
					sortedList.add(t);
					// }
					getChilds(str, queryResult, sortedList);
				}
			}

		}

		return removeUnwantedEntries(sortedList);
	}

	private void getChilds(String str, List<TrialBalance> queryResult,
			List<TrialBalance> sortedList) {

		for (TrialBalance t : queryResult) {

			if (t.getAccountFlow().startsWith(str + ".")) {

				if (!t.getAccountFlow().substring(str.length() + 1)
						.contains(".")) {

					if (!sortedList.contains(t)) {
						// if (t.getAmount() != 0.0) {
						sortedList.add(t);
						// }
						str = t.getAccountFlow();

						getChilds(str, queryResult, sortedList);
					}
				}
			}
		}
		if (str.contains(".")) {

			getChilds(str.substring(0, str.lastIndexOf(".")), queryResult,
					sortedList);
		} else {
			return;
		}

	}

	@Override
	public Map<String, Double> getVATReturnBoxes(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List<FiscalYear> fiscalYears = (List<FiscalYear>) session
				.getNamedQuery("getFisacalyear");
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
					.setParameter(

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

	@Override
	public VATReturn getVATReturnDetails(TAXAgency vatAgency,
			FinanceDate fromDate, FinanceDate toDate) throws DAOException,
			AccounterException {

		if (hasFileVAT(vatAgency, fromDate, toDate)) {
			throw new AccounterException(
					AccounterException.ERROR_ILLEGAL_ARGUMENT);
			// "FileVAT is already done in this period. Choose another VAT period");
		}
		VATReturn vatReturn = new VATReturn();

		List<Box> boxes = createBoxes(vatAgency);

		assignAmounts(vatAgency, boxes, fromDate, toDate);
		adjustAmounts(vatAgency, boxes, fromDate, toDate);

		// for (Box b : boxes) {
		// if (b.getName().equals(
		// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS))
		// b.setName("Box 2 "
		// + AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS);
		// }

		vatReturn.setTaxAgency(vatAgency);
		vatReturn.setBoxes(boxes);
		vatReturn.setVATperiodStartDate(fromDate);
		vatReturn.setVATperiodEndDate(toDate);

		return vatReturn;

	}

	//
	private void adjustAmounts(TAXAgency vatAgency, List<Box> boxes,
			FinanceDate fromDate, FinanceDate toDate) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getTAXAdjustments.by.taxAgencyIdand.Date")
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
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

						if (v.getTaxItem().isSalesType()) {
							if ((!v.getTaxItem()
									.getName()
									.equals(AccounterServerConstants.VAT_ITEM_EC_SALES_GOODS_STANDARD))
									&& (!v.getTaxItem()
											.getName()
											.equals(AccounterServerConstants.VAT_ITEM_EC_SALES_SERVICES_STANDARD))) {
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
						}

						else {
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

	private void assignAmounts(TAXAgency vatAgency, List<Box> boxes,
			FinanceDate fromDate, FinanceDate toDate) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.getNamedQuery("getTAXRateCalculations.by.taxAgencyIdand.Date")
				.setParameter("toDate", toDate)
				.setParameter("vatAgency", vatAgency.getID());
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

	private List<Box> createBoxes(TAXAgency vatAgency) {

		List<Box> boxes = new VList<Box>();

		if (vatAgency.getVATReturn() == VATReturn.VAT_RETURN_UK_VAT) {
			Box b1 = new Box();
			b1.setName(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES);

			Box b2 = new Box();
			b2.setName(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS);

			Box b3 = new Box();
			b3.setName(AccounterServerConstants.UK_BOX3_TOTAL_OUTPUT);

			Box b4 = new Box();
			b4.setName(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES);

			Box b5 = new Box();
			b5.setName(AccounterServerConstants.UK_BOX5_NET_VAT);

			Box b6 = new Box();
			b6.setName(AccounterServerConstants.UK_BOX6_TOTAL_NET_SALES);

			Box b7 = new Box();
			b7.setName(AccounterServerConstants.UK_BOX7_TOTAL_NET_PURCHASES);

			Box b8 = new Box();
			b8.setName(AccounterServerConstants.UK_BOX8_TOTAL_NET_SUPPLIES);

			Box b9 = new Box();
			b9.setName(AccounterServerConstants.UK_BOX9_TOTAL_NET_ACQUISITIONS);

			Box b10 = new Box();
			b10.setName(AccounterServerConstants.UK_BOX10_UNCATEGORISED);

			b1.setBoxNumber(1);
			b2.setBoxNumber(2);
			b3.setBoxNumber(3);
			b4.setBoxNumber(4);
			b5.setBoxNumber(5);
			b6.setBoxNumber(6);
			b7.setBoxNumber(7);
			b8.setBoxNumber(8);
			b9.setBoxNumber(9);
			b10.setBoxNumber(10);

			b1.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b2.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b3.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b4.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b5.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b6.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b7.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b8.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b9.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b10.setTaxRateCalculations(new VList<TAXRateCalculation>());

			boxes.add(b1);
			boxes.add(b2);
			boxes.add(b3);
			boxes.add(b4);
			boxes.add(b5);
			boxes.add(b6);
			boxes.add(b7);
			boxes.add(b8);
			boxes.add(b9);
			boxes.add(b10);
		} else if (vatAgency.getVATReturn() == VATReturn.VAT_RETURN_IRELAND) {

			Box b1 = new Box();
			b1.setName(AccounterServerConstants.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES);

			Box b2 = new Box();
			b2.setName(AccounterServerConstants.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS);

			Box b3 = new Box();
			b3.setName(AccounterServerConstants.IRELAND_BOX3_VAT_ON_SALES);

			Box b4 = new Box();
			b4.setName(AccounterServerConstants.IRELAND_BOX4_VAT_ON_PURCHASES);

			Box b5 = new Box();
			b5.setName(AccounterServerConstants.IRELAND_BOX5_T3_T4_PAYMENT_DUE);

			Box b6 = new Box();
			b6.setName(AccounterServerConstants.IRELAND_BOX6_E1_GOODS_TO_EU);

			Box b7 = new Box();
			b7.setName(AccounterServerConstants.IRELAND_BOX7_E2_GOODS_FROM_EU);

			Box b8 = new Box();
			b8.setName(AccounterServerConstants.IRELAND_BOX8_TOTAL_NET_SALES);

			Box b9 = new Box();
			b9.setName(AccounterServerConstants.IRELAND_BOX9_TOTAL_NET_PURCHASES);

			Box b10 = new Box();
			b10.setName(AccounterServerConstants.IRELAND_BOX10_UNCATEGORISED);

			b1.setBoxNumber(1);
			b2.setBoxNumber(2);
			b3.setBoxNumber(3);
			b4.setBoxNumber(4);
			b5.setBoxNumber(5);
			b6.setBoxNumber(6);
			b7.setBoxNumber(7);
			b8.setBoxNumber(8);
			b9.setBoxNumber(9);
			b10.setBoxNumber(10);

			b1.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b2.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b3.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b4.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b5.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b6.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b7.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b8.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b9.setTaxRateCalculations(new VList<TAXRateCalculation>());
			b10.setTaxRateCalculations(new VList<TAXRateCalculation>());

			boxes.add(b1);
			boxes.add(b2);
			boxes.add(b3);
			boxes.add(b4);
			boxes.add(b5);
			boxes.add(b6);
			boxes.add(b7);
			boxes.add(b8);
			boxes.add(b9);
			boxes.add(b10);
		}
		return boxes;
	}

	@Override
	public VList<VATDetail> getPriorVATReturnVATDetailReport(
			TAXAgency vatAgency, FinanceDate endDate) throws DAOException,
			ParseException {

		Session session = HibernateUtil.getCurrentSession();

		FinanceDate startDate;
		{
			Query q1 = session.getNamedQuery("getVATReturn.by.enddate")
					.setParameter("endDate", endDate);

			VATReturn vatReturn = (VATReturn) q1.uniqueResult();
			if (vatReturn == null) {
				throw new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						new NullPointerException(
								"No VAT Return found in database with VATAgency '"
										+ vatAgency.getName()
										+ "' and End date :"
										+ endDate.toString()));
			}

			startDate = vatReturn.getVATperiodStartDate();

		}

		VATDetailReport vatDetailReport = new VATDetailReport(
				vatAgency.getVATReturn());

		prepareVATDetailReport(vatDetailReport, vatAgency, startDate, endDate);

		return getListOfVATDetails(vatDetailReport);
	}

	private void prepareVATDetailReport(VATDetailReport vatDetailReport,
			TAXAgency taxAgency, FinanceDate startDate, FinanceDate endDate) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = null;

		// /getting entries from VATRAteCalculation
		if (taxAgency != null) {
			query = session
					.getNamedQuery(
							"getTaxCalc.by.TaxAgencyId.and.withOtherDetails")
					.setParameter("taxAgency", taxAgency.getID())
					.setParameter("fromDate", startDate)
					.setParameter("toDate", endDate);
		} else {
			query = session
					.getNamedQuery(
							"getTaxrateCalc.by.TaxAgencyandItem.and.Dates")
					.setParameter("fromDate", startDate)
					.setParameter("toDate", endDate);
		}

		List<TAXRateCalculation> vats = query.list();

		List<VATDetail> vatDetails = new VList<VATDetail>();

		for (TAXRateCalculation v : vats) {

			VATDetail vd = new VATDetail();

			vd.setBoxName(setVATBoxName(v));

			if (v.getTransactionItem().getTransaction().isAmountsIncludeVAT())
				vd.setNetAmount(v.getLineTotal() - v.getVatAmount());
			else
				vd.setNetAmount(v.getLineTotal());

			if (v.getTransactionItem().getTransaction().getInvolvedPayee() != null)
				vd.setPayeeName(v.getTransactionItem().getTransaction()
						.getInvolvedPayee().getName());

			/*
			 * Here all box values other than Vat code EGS (for purchase) of
			 * box2 and Vat code RC (for purchase) of box1 are getting it's
			 * native values
			 */

			if (vd.getBoxName().equals(
					VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)
					|| (vd.getBoxName().equals(
							VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES) && !v
							.isVATGroupEntry())
					|| (vd.getBoxName().equals(
							VATSummary.UK_BOX1_VAT_DUE_ON_SALES) && v
							.getTaxItem().getVatReturnBox().getTotalBox()
							.equals(AccounterServerConstants.BOX_NONE))) {
				double amount = -1 * (v.getVatAmount());
				// double amount = (!v.getTransactionItem().isVoid()) ? (-1 * (v
				// .getVatAmount())) : 0;
				vd.setTotal(amount);
			} else {
				double amount = v.getVatAmount();
				// double amount = (!v.getTransactionItem().isVoid()) ? v
				// .getVatAmount() : 0;
				vd.setTotal(amount);
			}
			vd.setTransactionDate(new ClientFinanceDate(v.getTransactionDate()
					.getDate()));
			vd.setTransactionName(v.getTransactionItem().getTransaction()
					.toString());
			vd.setTransactionNumber(v.getTransactionItem().getTransaction()
					.getNumber());
			vd.setTransactionType(v.getTransactionItem().getTransaction()
					.getType());
			vd.setVatRate(v.getTaxItem().getTaxRate());
			vd.setTransactionId(v.getTransactionItem().getTransaction().getID());
			vd.setPercentage(v.getTaxItem().isPercentage());
			vatDetailReport.getEntries().get(vd.getBoxName()).add(vd);

			// Adding Box2 entries to Box4
			// if (v.getVatItem().getVatReturnBox().getVatBox().equals(
			// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)) {
			//
			// VATDetail vd2 = new VATDetail();
			// vd2
			// .setBoxName(setVATBoxName(AccounterConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES));
			//
			// vd2.setNetAmount(vd.getNetAmount());
			// vd2.setPayeeName(vd.getPayeeName());
			// vd2.setTotal(vd.getTotal());
			// vd2.setTransactionDate(vd.getTransactionDate());
			// vd2.setTransactionName(vd.getTransactionName());
			// vd2.setTransactionNumber(vd.getTransactionNumber());
			// vd2.setTransactionType(vd.getTransactionType());
			// vd2.setVatRate(vd.getVatRate());
			// vd2.setTransactionId(vd.getTransactionId());
			// vd2.setPercentage(v.getVatItem().isPercentage());
			//
			// vatDetailReport.getEntries().get(vd2.getBoxName()).add(vd2);
			// }

			// /////////////////////////////

			// Adding RC vat code transaction entry to Box4
			// if (v.getVatItem().getVatReturnBox().getVatBox().equals(
			// AccounterConstants.UK_BOX1_VAT_DUE_ON_SALES)
			// && (v.getVatItem().getVatReturnBox().getTotalBox()
			// .equals(AccounterConstants.BOX_NONE))) {
			//
			// VATDetail vd3 = new VATDetail();
			// vd3
			// .setBoxName(setVATBoxName(AccounterConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES));
			//
			// vd3.setNetAmount(vd.getNetAmount());
			// vd3.setPayeeName(vd.getPayeeName());
			// vd3.setTotal(vd.getTotal());
			// vd3.setTransactionDate(vd.getTransactionDate());
			// vd3.setTransactionName(vd.getTransactionName());
			// vd3.setTransactionNumber(vd.getTransactionNumber());
			// vd3.setTransactionType(vd.getTransactionType());
			// vd3.setVatRate(vd.getVatRate());
			// vd3.setTransactionId(vd.getTransactionId());
			// vd3.setPercentage(v.getVatItem().isPercentage());
			//
			// vatDetailReport.getEntries().get(vd3.getBoxName()).add(vd3);
			// }
			// /////////////////////////////////////

			VATDetail vd1 = new VATDetail();

			vd1.setBoxName(setTotalBoxName(v));

			if ((vd1.getBoxName().equals(
					AccounterServerConstants.UK_BOX10_UNCATEGORISED) && v
					.getTransactionItem().getTransaction()
					.getTransactionCategory() == Transaction.CATEGORY_CUSTOMER)
					|| !vd1.getBoxName().equals(
							AccounterServerConstants.UK_BOX10_UNCATEGORISED)) {

				if (v.getTransactionItem().getTransaction()
						.isAmountsIncludeVAT()) {
					double totalAmount = v.getLineTotal() - v.getVatAmount();
					// double totalAmount = (!v.getTransactionItem().isVoid()) ?
					// (v
					// .getLineTotal() - v.getVatAmount())
					// : 0;

					vd1.setTotal(totalAmount);
				}
				// vd1.setNetAmount();
				// vd1.setTotal(v.getVatAmount());
				else {
					double totalAmount = v.getLineTotal();
					// double totalAmount = (!v.getTransactionItem().isVoid()) ?
					// v
					// .getLineTotal() : 0;

					vd1.setTotal(totalAmount);
				}
				if (v.getTransactionItem().getTransaction().getInvolvedPayee() != null)
					vd1.setPayeeName(v.getTransactionItem().getTransaction()
							.getInvolvedPayee().getName());
				vd1.setTransactionDate(new ClientFinanceDate(v
						.getTransactionDate().getDate()));
				vd1.setTransactionName(v.getTransactionItem().getTransaction()
						.toString());
				vd1.setTransactionNumber(v.getTransactionItem()
						.getTransaction().getNumber());
				vd1.setTransactionType(v.getTransactionItem().getTransaction()
						.getType());
				// vd1.setVatRate(v.getTransactionItem().getLineTotal());
				vd1.setTransactionId(v.getTransactionItem().getTransaction()
						.getID());

				vatDetailReport.getEntries().get(vd1.getBoxName()).add(vd1);

			}

		}

		{

			// getting entries from VATAdjustment
			if (taxAgency != null) {
				query = session.getNamedQuery(

				"getTaxadjustment.by.allDetails.withOrder")

				.setParameter("fromDate", startDate)
						.setParameter("toDate", endDate)
						.setParameter("taxAgency", taxAgency.getID());
			} else {
				query = session.getNamedQuery(

				"getTaxadjustment.by.betweenDates")

				.setParameter("fromDate", startDate)
						.setParameter("toDate", endDate);
			}

			// Adding adjustment entries to it's Respective boxes, except
			// Uncategorised Tax Amounts box
			List<TAXAdjustment> vas = query.list();

			// double box2Amount = 0.0;

			for (TAXAdjustment v : vas) {

				VATDetail vd = new VATDetail();

				vd.setBoxName(setVATBoxName(v.getTaxItem().getVatReturnBox()
						.getVatBox()));

				if (!vd.getBoxName().equals(
						AccounterServerConstants.UK_BOX10_UNCATEGORISED)) {

					Entry e = v.getJournalEntry().getEntry().get(0);
					if (!DecimalUtil.isEquals(e.getDebit(), 0)) {
						vd.setTotal(e.getDebit());
					} else {
						vd.setTotal(-1 * e.getCredit());
					}

					if (vd.getBoxName().equals("VAT Due on sales (Box 1)"))
						vd.setTotal(-1 * vd.getTotal());

					vd.setPayeeName(e.getAccount().getName());

					vd.setTransactionDate(new ClientFinanceDate(e
							.getEntryDate().getDate()));
					vd.setTransactionName(e.getJournalEntry().toString());
					vd.setTransactionNumber(e.getVoucherNumber());
					vd.setTransactionType(e.getJournalEntryType());
					vd.setTransactionId(e.getJournalEntry().getID());

					vatDetailReport.getEntries().get(vd.getBoxName()).add(vd);
				}

				// Adding Box2 adjustment entries to Box4
				// if (v.getVatItem().getVatReturnBox().getVatBox().equals(
				// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)) {
				// VATDetail vd2 = new VATDetail();
				// vd2
				// .setBoxName(setVATBoxName(AccounterConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES));
				//
				// vd2.setTotal(vd.getTotal());
				// vd2.setPayeeName(vd.getPayeeName());
				// vd2.setTransactionDate(vd.getTransactionDate());
				// vd2.setTransactionName(vd.getTransactionName());
				// vd2.setTransactionNumber(vd.getTransactionNumber());
				// vd2.setTransactionType(vd.getTransactionType());
				// vd2.setTransactionId(vd.getTransactionId());
				//
				// vatDetailReport.getEntries().get(vd2.getBoxName()).add(vd2);
				// }

				// Adding all adjustment entries to Uncategorised Tax Amounts
				// Box
				VATDetail vd1 = new VATDetail();

				vd1.setBoxName(AccounterServerConstants.UK_BOX10_UNCATEGORISED);

				Entry e = v.getJournalEntry().getEntry().get(0);
				if (!DecimalUtil.isEquals(e.getDebit(), 0)) {
					vd1.setTotal(1 * e.getDebit());
				} else {
					vd1.setTotal(-1 * e.getCredit());
				}

				if (v.getTaxItem().getName().equals("EC Sales Goods Standard")
						|| v.getTaxItem().getName()
								.equals("EC Sales Services Standard"))
					vd1.setTotal(-1 * vd1.getTotal());

				vd1.setPayeeName(e.getAccount().getName());

				vd1.setTransactionDate(new ClientFinanceDate(e.getEntryDate()
						.getDate()));
				vd1.setTransactionName(e.getJournalEntry().toString());
				vd1.setTransactionNumber(e.getVoucherNumber());
				vd1.setTransactionType(e.getJournalEntryType());
				vd1.setTransactionId(e.getJournalEntry().getID());

				if (vatDetailReport.getEntries().get(vd1.getBoxName()) != null)
					vatDetailReport.getEntries().get(vd1.getBoxName()).add(vd1);
			}
		}

		// Getting journal entries from VATReturn
		{
			if (taxAgency != null) {
				query = session.getNamedQuery(

				"getVat.by.taxAgency.and.VatPeriod")

				.setParameter("fromDate", startDate)
						.setParameter("toDate", endDate)
						.setParameter("taxAgency", taxAgency.getID());
			} else {
				query = session.getNamedQuery(

				"getVat.by.BetweenendDates")

				.setParameter("fromDate", startDate)
						.setParameter("toDate", endDate);
			}

			List<VATReturn> vatReturns = query.list();

			for (VATReturn v : vatReturns) {

				List<Entry> entries = v.getJournalEntry().getEntry();
				for (Entry e : entries) {
					if ((!e.getAccount()
							.getName()
							.equals(Company.getCompany()
									.getAccountsPayableAccount().getName()))) {
						// && ((e.getDebit() == 0 && e.getCredit() == 0))) {

						if (e.getTaxItem() != null) {
							VATDetail vd = new VATDetail();

							vd.setBoxName(setVATBoxName(e.getTaxItem()
									.getVatReturnBox().getVatBox()));
							// vd.setBoxName("Uncategorised Tax Amounts");

							if (!DecimalUtil.isEquals(e.getDebit(), 0)) {
								vd.setTotal(-1 * e.getDebit());
							} else {
								vd.setTotal(-1 * e.getCredit());
							}

							vd.setTotal(-1 * e.getTotal());
							vd.setPayeeName(e.getAccount().getName());
							vd.setTransactionDate(new ClientFinanceDate(e
									.getEntryDate().getDate()));
							vd.setTransactionName(e.getJournalEntry()
									.toString());
							vd.setTransactionNumber(e.getVoucherNumber());
							vd.setTransactionType(e.getJournalEntryType());
							vd.setTransactionId(e.getJournalEntry().getID());

							// if (vatDetailReport.getEntries().get(
							// vd.getBoxName()) != null)
							// vatDetailReport.getEntries().get(
							// vd.getBoxName()).add(vd);

						} else {
							VATDetail vd = new VATDetail();

							vd.setBoxName(VATDetailReport.IRELAND_BOX10_UNCATEGORISED);

							if (!DecimalUtil.isEquals(e.getDebit(), 0)) {
								vd.setTotal(-1 * e.getDebit());
							} else {
								vd.setTotal(-1 * e.getCredit());
							}

							vd.setTransactionDate(new ClientFinanceDate(e
									.getEntryDate().getDate()));
							vd.setTransactionName(e.getJournalEntry()
									.toString());
							vd.setTransactionNumber(e.getVoucherNumber());
							vd.setTransactionType(e.getJournalEntryType());
							// vatDetailReport.getEntries().get(vd.getBoxName())
							// .add(vd);
						}
					}

				}

				// Adding Filed vat entries to it's Respective boxes, except
				// Box3 and Box5
				Query query1 = session.getNamedQuery("getFiledBoxValues")
						.setParameter("id", v.getID());

				List list = query1.list();
				Object[] object = null;
				Iterator iterator = list.iterator();

				while (iterator.hasNext()) {

					object = (Object[]) iterator.next();

					VATDetail vd = new VATDetail();

					vd.setBoxName(setVATBoxName((String) object[0]));

					// if (((String) object[0])
					// .equals("Box 2 VAT due in this period on acquisitions from other EC member states")
					// && vd.getBoxName().equals(
					// VATSummary.UK_BOX10_UNCATEGORISED))
					// vd
					// .setBoxName(VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS);

					if ((Double) object[1] != 0.0
							&& (!vd.getBoxName().equals(
									VATSummary.UK_BOX3_TOTAL_OUTPUT) && !vd
									.getBoxName().equals(
											VATSummary.UK_BOX5_NET_VAT))) {

						vd.setTotal(-1 * (Double) object[1]);
						vd.setTransactionDate(new ClientFinanceDate(
								(Long) object[2]));
						vd.setTransactionName(v.getJournalEntry().toString());
						vd.setTransactionNumber((String) object[3]);
						vd.setTransactionType(v.getJournalEntry().getEntry()
								.get(0).getJournalEntryType());

						if (vatDetailReport.getEntries().get(vd.getBoxName()) != null)
							vatDetailReport.getEntries().get(vd.getBoxName())
									.add(vd);
					}
				}
			}
		}
	}

	private String setVATBoxName(TAXRateCalculation v) {
		String boxName = null;
		if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES)) {
			boxName = VATSummary.UK_BOX1_VAT_DUE_ON_SALES;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getVatBox()
				.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS;
		} else if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX3_TOTAL_OUTPUT)) {
			boxName = VATSummary.UK_BOX3_TOTAL_OUTPUT;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getVatBox()
				.equals(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)) {
			boxName = VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES;
		} else if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX5_NET_VAT)) {
			boxName = VATSummary.UK_BOX5_NET_VAT;
		} else if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX6_TOTAL_NET_SALES)) {
			boxName = VATSummary.UK_BOX6_TOTAL_NET_SALES;
		} else if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX7_TOTAL_NET_PURCHASES)) {
			boxName = VATSummary.UK_BOX7_TOTAL_NET_PURCHASES;
		} else if (v.getTaxItem().getVatReturnBox().getVatBox()
				.equals(AccounterServerConstants.UK_BOX8_TOTAL_NET_SUPPLIES)) {
			boxName = VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getVatBox()
				.equals(AccounterServerConstants.UK_BOX9_TOTAL_NET_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS;
		} else {
			boxName = VATSummary.UK_BOX10_UNCATEGORISED;
		}

		return boxName;
	}

	private String setTotalBoxName(TAXRateCalculation v) {
		String boxName = null;
		if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES)) {
			boxName = VATSummary.UK_BOX1_VAT_DUE_ON_SALES;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getTotalBox()
				.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS;
		} else if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX3_TOTAL_OUTPUT)) {
			boxName = VATSummary.UK_BOX3_TOTAL_OUTPUT;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getTotalBox()
				.equals(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)) {
			boxName = VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES;
		} else if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX5_NET_VAT)) {
			boxName = VATSummary.UK_BOX5_NET_VAT;
		} else if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX6_TOTAL_NET_SALES)) {
			boxName = VATSummary.UK_BOX6_TOTAL_NET_SALES;
		} else if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX7_TOTAL_NET_PURCHASES)) {
			boxName = VATSummary.UK_BOX7_TOTAL_NET_PURCHASES;
		} else if (v.getTaxItem().getVatReturnBox().getTotalBox()
				.equals(AccounterServerConstants.UK_BOX8_TOTAL_NET_SUPPLIES)) {
			boxName = VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES;
		} else if (v
				.getTaxItem()
				.getVatReturnBox()
				.getTotalBox()
				.equals(AccounterServerConstants.UK_BOX9_TOTAL_NET_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS;
		} else {
			boxName = VATSummary.UK_BOX10_UNCATEGORISED;
		}

		return boxName;
	}

	private String setVATBoxName(String getBoxName) {
		String boxName = null;
		if (getBoxName
				.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES)) {
			boxName = VATSummary.UK_BOX1_VAT_DUE_ON_SALES;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX3_TOTAL_OUTPUT)) {
			boxName = VATSummary.UK_BOX3_TOTAL_OUTPUT;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)) {
			boxName = VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES;
		} else if (getBoxName.equals(AccounterServerConstants.UK_BOX5_NET_VAT)) {
			boxName = VATSummary.UK_BOX5_NET_VAT;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX6_TOTAL_NET_SALES)) {
			boxName = VATSummary.UK_BOX6_TOTAL_NET_SALES;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX7_TOTAL_NET_PURCHASES)) {
			boxName = VATSummary.UK_BOX7_TOTAL_NET_PURCHASES;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX8_TOTAL_NET_SUPPLIES)) {
			boxName = VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES;
		} else if (getBoxName
				.equals(AccounterServerConstants.UK_BOX9_TOTAL_NET_ACQUISITIONS)) {
			boxName = VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS;
		} else {
			boxName = VATSummary.UK_BOX10_UNCATEGORISED;
		}

		return boxName;
	}

	private VList<VATDetail> getListOfVATDetails(VATDetailReport vatDetailReport) {

		List<VATDetail> vatDetails = new VList<VATDetail>();

		LinkedHashMap<String, List<VATDetail>> map = vatDetailReport
				.getEntries();

		if (map.containsKey(VATSummary.UK_BOX10_UNCATEGORISED)
				&& map.get(VATSummary.UK_BOX10_UNCATEGORISED).size() > 0)
			vatDetails.addAll((map.get(VATSummary.UK_BOX10_UNCATEGORISED)));

		// if (map.containsKey(VATDetailReport.IRELAND_BOX10_UNCATEGORISED)
		// && map.get(VATDetailReport.IRELAND_BOX10_UNCATEGORISED).size() > 0)
		// vatDetails.addAll(map
		// .get(VATDetailReport.IRELAND_BOX10_UNCATEGORISED));

		if (map.containsKey(VATSummary.UK_BOX1_VAT_DUE_ON_SALES)
				&& map.get(VATSummary.UK_BOX1_VAT_DUE_ON_SALES).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX1_VAT_DUE_ON_SALES));

		if (map.containsKey(VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)
				&& map.get(VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS).size() > 0)
			vatDetails.addAll(map
					.get(VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS));

		if (map.containsKey(VATSummary.UK_BOX3_TOTAL_OUTPUT)
				&& map.get(VATSummary.UK_BOX3_TOTAL_OUTPUT).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX3_TOTAL_OUTPUT));

		if (map.containsKey(VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)
				&& map.get(VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES).size() > 0)
			vatDetails.addAll(map
					.get(VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES));

		if (map.containsKey(VATSummary.UK_BOX5_NET_VAT)
				&& map.get(VATSummary.UK_BOX5_NET_VAT).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX5_NET_VAT));

		if (map.containsKey(VATSummary.UK_BOX6_TOTAL_NET_SALES)
				&& map.get(VATSummary.UK_BOX6_TOTAL_NET_SALES).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX6_TOTAL_NET_SALES));

		if (map.containsKey(VATSummary.UK_BOX7_TOTAL_NET_PURCHASES)
				&& map.get(VATSummary.UK_BOX7_TOTAL_NET_PURCHASES).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX7_TOTAL_NET_PURCHASES));

		if (map.containsKey(VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES)
				&& map.get(VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES).size() > 0)
			vatDetails.addAll(map.get(VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES));

		if (map.containsKey(VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS)
				&& map.get(VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS).size() > 0)
			vatDetails.addAll(map
					.get(VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS));

		if (map.containsKey(VATDetailReport.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES)
				&& map.get(VATDetailReport.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES)
						.size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES));

		if (map.containsKey(VATDetailReport.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS)
				&& map.get(
						VATDetailReport.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS)
						.size() > 0)
			vatDetails
					.addAll(map
							.get(VATDetailReport.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS));

		if (map.containsKey(VATDetailReport.IRELAND_BOX3_VAT_ON_SALES)
				&& map.get(VATDetailReport.IRELAND_BOX3_VAT_ON_SALES).size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX3_VAT_ON_SALES));

		if (map.containsKey(VATDetailReport.IRELAND_BOX4_VAT_ON_PURCHASES)
				&& map.get(VATDetailReport.IRELAND_BOX4_VAT_ON_PURCHASES)
						.size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX4_VAT_ON_PURCHASES));

		if (map.containsKey(VATDetailReport.IRELAND_BOX5_T3_T4_PAYMENT_DUE)
				&& map.get(VATDetailReport.IRELAND_BOX5_T3_T4_PAYMENT_DUE)
						.size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX5_T3_T4_PAYMENT_DUE));

		if (map.containsKey(VATDetailReport.IRELAND_BOX6_E1_GOODS_TO_EU)
				&& map.get(VATDetailReport.IRELAND_BOX6_E1_GOODS_TO_EU).size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX6_E1_GOODS_TO_EU));

		if (map.containsKey(VATDetailReport.IRELAND_BOX7_E2_GOODS_FROM_EU)
				&& map.get(VATDetailReport.IRELAND_BOX7_E2_GOODS_FROM_EU)
						.size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX7_E2_GOODS_FROM_EU));

		if (map.containsKey(VATDetailReport.IRELAND_BOX8_TOTAL_NET_SALES)
				&& map.get(VATDetailReport.IRELAND_BOX8_TOTAL_NET_SALES).size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX8_TOTAL_NET_SALES));

		if (map.containsKey(VATDetailReport.IRELAND_BOX9_TOTAL_NET_PURCHASES)
				&& map.get(VATDetailReport.IRELAND_BOX9_TOTAL_NET_PURCHASES)
						.size() > 0)
			vatDetails.addAll(map
					.get(VATDetailReport.IRELAND_BOX9_TOTAL_NET_PURCHASES));

		return new VList<VATDetail>(vatDetails);

	}

	@Override
	public VList<VATSummary> getPriorReturnVATSummary(TAXAgency taxAgency,
			FinanceDate endDate) throws DAOException, ParseException {
		Session session = HibernateUtil.getCurrentSession();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Query query = session
				.getNamedQuery("getVATReturn.by.taxagencyandenddate")
				.setParameter("taxAgency", taxAgency.getID())
				.setParameter("endDate", endDate);

		VATReturn vatReturn = (VATReturn) query.uniqueResult();

		if (vatReturn == null) {
			throw new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					new NullPointerException(
							"No VAT Return found in database with VATAgency '"
									+ taxAgency.getName() + "' and End date :"
									+ endDate));
		}

		List<VATSummary> vatSummaries = new VList<VATSummary>();

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

		return new VList<VATSummary>(vatSummaries);
	}

	@Override
	public VList<VATDetail> getVATDetailReport(FinanceDate startDate,
			FinanceDate endDate) throws DAOException, ParseException {

		VATDetailReport vatDetailReport = new VATDetailReport();

		prepareVATDetailReport(vatDetailReport, null, startDate, endDate);

		return getListOfVATDetails(vatDetailReport);
	}

	@Override
	public VList<VATSummary> getVAT100Report(TAXAgency taxAgency,
			FinanceDate fromDate, FinanceDate toDate) throws DAOException,
			ParseException {
		Session session = HibernateUtil.getCurrentSession();

		List<VATSummary> vatSummaries = createRows(taxAgency);

		Query query = session.getNamedQuery(
				"getVATReturn.checkingby.taxagencyidand.dates").setParameter(0,
				taxAgency.getID());

		Object object[] = null;
		List list = query.list();
		Iterator it = list.iterator();
		FinanceDate leastStartDate = null;
		FinanceDate highestEndDate = null;

		while (it.hasNext()) {
			object = (Object[]) it.next();
			leastStartDate = (((FinanceDate) object[0]) == null ? null
					: ((FinanceDate) object[0]));
			highestEndDate = (((FinanceDate) object[1]) == null ? null
					: ((FinanceDate) object[1]));

		}
		// List<Long> vatReturns = new VList<VATReturn>();
		// Long startDate1 = null;
		// Long endDate1 = null;
		// // for (VATReturn v : vatReturns) {
		//
		// if (startDate1 == null || endDate1 == null) {
		// if (startDate1 == null) {
		// startDate1 = v.getVATperiodStartDate();
		// }
		// if (endDate1 == null) {
		// endDate1 = v.getVATperiodEndDate();
		// }
		// } else {
		// if (startDate1.after(v.getVATperiodStartDate())) {
		// startDate1 = v.getVATperiodStartDate();
		// }
		// if (endDate1.before(v.getVATperiodEndDate())) {
		// endDate1 = v.getVATperiodEndDate();
		// }
		// }
		// }

		if (leastStartDate != null && highestEndDate != null) {

			query = session
					.getNamedQuery(
							"getTAXRateCalculation.by.datesand.vatReturn")
					.setParameter("startDate", fromDate)
					.setParameter("endDate", toDate);
			// .setParameter("startDate1",
			// leastStartDate).setParameter("endDate1",
			// highestEndDate);
			// v.transactionDate not between :startDate1 and :endDate1")
		} else {
			query = session.getNamedQuery("getTAXRateCalculation.by.dates")
					.setParameter("startDate", fromDate)
					.setParameter("endDate", toDate);
		}

		List<TAXRateCalculation> vats = query.list();

		// double rcAmount = 0;

		for (TAXRateCalculation v : vats) {

			// if (v.getVatItem().getVatReturnBox().getVatBox().equals(
			// AccounterConstants.UK_BOX1_VAT_DUE_ON_SALES)
			// && (v.getVatItem().getVatReturnBox().getTotalBox()
			// .equals(AccounterConstants.BOX_NONE))) {
			// rcAmount += -1 * (v.getVatAmount());
			// }

			// if (v.getVatItem().getVatReturnBox().getVatBox().equals(
			// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS))
			// rcAmount += -1 * (v.getVatAmount());

			/*
			 * Here all box values other than Vat code EGS (for purchase) of
			 * box2 and Vat code RC (for purchase) of box1 are getting it's
			 * native values
			 */

			for (VATSummary vs : vatSummaries) {

				if (v.getTaxItem().getVatReturnBox().getVatBox()
						.equals(vs.getVatReturnEntryName())) {

					if (v.getTaxItem()
							.getVatReturnBox()
							.getVatBox()
							.equals(AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS)
							|| (v.getTaxItem()
									.getVatReturnBox()
									.getVatBox()
									.equals(AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES) && !v
									.isVATGroupEntry())
							|| (v.getTaxItem()
									.getVatReturnBox()
									.getVatBox()
									.equals(AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES) && v
									.getTaxItem().getVatReturnBox()
									.getTotalBox()
									.equals(AccounterServerConstants.BOX_NONE)))
						vs.setValue(vs.getValue() + (-1 * (v.getVatAmount())));
					else
						vs.setValue(vs.getValue() + v.getVatAmount());
					// if (v
					// .getVatItem()
					// .getVatReturnBox()
					// .getVatBox()
					// .equals(
					// AccounterConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)) {
					// vs.setValue(vs.getValue() + rcAmount);
					// rcAmount = 0.0;
					// }
				}

				if (v.getTaxItem().getVatReturnBox().getTotalBox()
						.equals(vs.getVatReturnEntryName())) {

					vs.setValue(vs.getValue() + v.getLineTotal());

				}
			}
		}

		query = session.getNamedQuery("").setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
				.setParameter("taxAgency", taxAgency.getID());

		List<TAXAdjustment> vas = query.list();
		// double box2Amount = 0.0;

		if (vas != null) {
			for (TAXAdjustment v : vas) {
				for (VATSummary vs : vatSummaries) {

					// if (vs
					// .getVatReturnEntryName()
					// .equals(
					// AccounterConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES)) {
					// vs.setValue(vs.getValue() + box2Amount);
					// box2Amount = 0.0;
					// }

					if (v.getTaxItem().getVatReturnBox().getVatBox()
							.equals(vs.getVatReturnEntryName())) {
						if (v.getIncreaseVATLine())
							vs.setValue(vs.getValue() + v.getTotal());
						else
							vs.setValue(vs.getValue() - v.getTotal());

						// if (v
						// .getVatItem()
						// .getVatReturnBox()
						// .getVatBox()
						// .equals(
						// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS))
						// box2Amount += (v.getIncreaseVATLine() == true ? v
						// .getTotal() : -1 * v.getTotal());
					}

					else if (vs.getVatReturnEntryName().equals(
							"Uncategorised Tax Amounts")) {

						if (v.getTaxItem().isSalesType()) {
							if ((v.getTaxItem().getID() != 3)
									&& (v.getTaxItem().getID() != 4)) {
								if (v.getIncreaseVATLine())
									vs.setValue(vs.getValue() - v.getTotal());
								else
									vs.setValue(vs.getValue() + v.getTotal());

							} else {

								if (v.getIncreaseVATLine())
									vs.setValue(vs.getValue() + v.getTotal());
								else
									vs.setValue(vs.getValue() - v.getTotal());
							}
						}

						else {
							if (v.getIncreaseVATLine())
								vs.setValue(vs.getValue() + v.getTotal());
							else
								vs.setValue(vs.getValue() - v.getTotal());
						}
					}
				}
			}

		}

		// for (VATSummary v : vatSummaries) {
		// if (v.getVatReturnEntryName().equals(
		// AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS))
		// v.setVatReturnEntryName("Box 2 "
		// + AccounterConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS);
		// }

		return new VList<VATSummary>(vatSummaries);
	}

	private List<VATSummary> createRows(TAXAgency taxAgency) {

		List<VATSummary> vatSummaries = new VList<VATSummary>();

		if (taxAgency.getVATReturn() == TAXAgency.RETURN_TYPE_UK_VAT) {
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX1_VAT_DUE_ON_SALES,
					VATSummary.UK_BOX1_VAT_DUE_ON_SALES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX2_VAT_DUE_ON_ACQUISITIONS,
					VATSummary.UK_BOX2_VAT_DUE_ON_ACQUISITIONS, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX3_TOTAL_OUTPUT,
					VATSummary.UK_BOX3_TOTAL_OUTPUT, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX4_VAT_RECLAMED_ON_PURCHASES,
					VATSummary.UK_BOX4_VAT_RECLAMED_ON_PURCHASES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX5_NET_VAT,
					VATSummary.UK_BOX5_NET_VAT, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX6_TOTAL_NET_SALES,
					VATSummary.UK_BOX6_TOTAL_NET_SALES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX7_TOTAL_NET_PURCHASES,
					VATSummary.UK_BOX7_TOTAL_NET_PURCHASES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX8_TOTAL_NET_SUPPLIES,
					VATSummary.UK_BOX8_TOTAL_NET_SUPPLIES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX9_TOTAL_NET_ACQUISITIONS,
					VATSummary.UK_BOX9_TOTAL_NET_ACQUISITIONS, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.UK_BOX10_UNCATEGORISED,
					VATSummary.UK_BOX10_UNCATEGORISED, 0d));
		} else if (taxAgency.getVATReturn() == TAXAgency.RETURN_TYPE_IRELAND_VAT) {
			vatSummaries
					.add(new VATSummary(
							AccounterServerConstants.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES,
							VATSummary.IRELAND_BOX1_VAT_CHARGED_ON_SUPPIES, 0d));
			vatSummaries
					.add(new VATSummary(
							AccounterServerConstants.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS,
							VATSummary.IRELAND_BOX2_VAT_DUE_ON_INTRA_EC_ACQUISITIONS,
							0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX3_VAT_ON_SALES,
					VATSummary.IRELAND_BOX3_VAT_ON_SALES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX4_VAT_ON_PURCHASES,
					VATSummary.IRELAND_BOX4_VAT_ON_PURCHASES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX5_T3_T4_PAYMENT_DUE,
					VATSummary.IRELAND_BOX5_T3_T4_PAYMENT_DUE, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX6_E1_GOODS_TO_EU,
					VATSummary.IRELAND_BOX6_E1_GOODS_TO_EU, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX7_E2_GOODS_FROM_EU,
					VATSummary.IRELAND_BOX7_E2_GOODS_FROM_EU, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX8_TOTAL_NET_SALES,
					VATSummary.IRELAND_BOX8_TOTAL_NET_SALES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX9_TOTAL_NET_PURCHASES,
					VATSummary.IRELAND_BOX9_TOTAL_NET_PURCHASES, 0d));
			vatSummaries.add(new VATSummary(
					AccounterServerConstants.IRELAND_BOX10_UNCATEGORISED,
					VATSummary.IRELAND_BOX10_UNCATEGORISED, 0d));
		}
		return vatSummaries;
	}

	@Override
	public VList<PayVATEntries> getPayVATEntries() {

		List<PayVATEntries> payVATEntries = new Vector<PayVATEntries>();

		Session session = HibernateUtil.getCurrentSession();

		Query query = session
				.getNamedQuery("getVATReturn.by.check.BalanceGraterThanzero");

		List<VATReturn> vatReturns = query.list();

		for (VATReturn v : vatReturns) {

			payVATEntries.add(new PayVATEntries(v));
		}

		return new VList<PayVATEntries>(payVATEntries);

	}

	@Override
	public VList<ReceiveVATEntries> getReceiveVATEntries() {

		List<ReceiveVATEntries> receiveVATEntries = new Vector<ReceiveVATEntries>();

		Session session = HibernateUtil.getCurrentSession();

		Query query = session
				.getNamedQuery("getVATReturn.by.check.BalancelessThanzero");

		List<VATReturn> vatReturns = query.list();

		for (VATReturn v : vatReturns) {

			v.setBalance(-1 * v.getBalance());

			receiveVATEntries.add(new ReceiveVATEntries(v));
		}

		return new VList<ReceiveVATEntries>(receiveVATEntries);

	}

	@Override
	public VList<OpenAndClosedOrders> getOpenSalesOrders(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getOpenSalesOrders")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getClosedSalesOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getClosedSalesOrders")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getCompletedSalesOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getCompletedSalesOrders")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getCanceledSalesOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getCanceledSalesOrders")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getOpenPurchaseOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getOpenPurchaseOrders")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getClosedPurchaseOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getClosedPurchaseOrders")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getCompletedPurchaseOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getCompletedPurchaseOrders")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	public VList<OpenAndClosedOrders> getPurchaseOrders(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();
		List l = ((Query) session.getNamedQuery("getPurchaseOrders")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();
		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	public VList<OpenAndClosedOrders> getSalesOrders(FinanceDate startDate,
			FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();
		List l = ((Query) session.getNamedQuery("getSalesOrders")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();
		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	@Override
	public VList<OpenAndClosedOrders> getCanceledPurchaseOrders(
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		Session session = HibernateUtil.getCurrentSession();

		List l = ((Query) session.getNamedQuery("getCanceledPurchaseOrders")

		.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())).list();

		return prepareQueryResult(new VList<OpenAndClosedOrders>(l));
	}

	private VList<OpenAndClosedOrders> prepareQueryResult(List l) {
		Object[] object = null;
		Iterator iterator = l.iterator();
		List<OpenAndClosedOrders> queryResult = new VList<OpenAndClosedOrders>();
		while ((iterator).hasNext()) {
			OpenAndClosedOrders openAndClosedOrder = new OpenAndClosedOrders();
			object = (Object[]) iterator.next();
			openAndClosedOrder.setTransactionID(((BigInteger) object[0])
					.longValue());
			openAndClosedOrder.setTransactionType((Integer) object[1]);
			openAndClosedOrder.setTransactionDate(new ClientFinanceDate(
					((BigInteger) object[2]).longValue()));
			openAndClosedOrder.setVendorOrCustomerName((String) object[3]);
			// openAndClosedOrder
			// .setQuantity(object[4] != null ? ((BigDecimal) object[4])
			// .intValue() : 0);
			openAndClosedOrder.setAmount(object[4] != null ? (Double) object[4]
					: 0.0);
			queryResult.add(openAndClosedOrder);
		}
		return new VList<OpenAndClosedOrders>(queryResult);
	}

	@Override
	public VList<UncategorisedAmountsReport> getUncategorisedAmountsReport(
			FinanceDate fromDate, FinanceDate toDate) throws ParseException {
		List<UncategorisedAmountsReport> uncategorisedAmounts = new VList<UncategorisedAmountsReport>();

		Session session = HibernateUtil.getCurrentSession();

		// Entries from Sales where Vat Codes EGS and RC are used

		Query query = session.getNamedQuery("getEGSandRCentriesFromSales")
				.setParameter("startDate", fromDate.getDate())
				.setParameter("endDate", toDate.getDate());

		List list2 = query.list();
		Object[] object = null;
		Iterator iterator = list2.iterator();
		while (iterator.hasNext()) {

			object = (Object[]) iterator.next();
			UncategorisedAmountsReport u = new UncategorisedAmountsReport();
			u.setTransactionType((Integer) object[0]);
			u.setDate(new ClientFinanceDate((Long) object[1]));
			u.setTransactionNumber((String) object[2]);
			u.setSourceName((String) object[4]);
			u.setMemo((String) object[5]);
			u.setAmount(object[6] == null ? 0 : (Double) object[6]);

			uncategorisedAmounts.add(u);
		}

		query = session.getNamedQuery(
				"getTAXAdjustment.checkingby.transactionDate").setParameter(
				"endDate", toDate);

		List<TAXAdjustment> vatAdjustments = query.list();

		for (TAXAdjustment v : vatAdjustments) {

			if (v.getJournalEntry().getEntry() != null
					&& v.getJournalEntry().getEntry().size() > 0
					&& !DecimalUtil.isEquals(v.getJournalEntry().getEntry()
							.get(0).getTotal(), 0)) {

				UncategorisedAmountsReport u = new UncategorisedAmountsReport();

				u.setTransactionType(v.getJournalEntry().getType());
				u.setTransactionNumber(v.getJournalEntry().getNumber());
				u.setID(v.getJournalEntry().getID());
				u.setSourceName(v.getJournalEntry().getEntry().get(1)
						.getAccount().getName());
				u.setMemo("VAT Adjustment");
				u.setDate(new ClientFinanceDate(v.getJournalEntry().getDate()
						.getDate()));
				if (v.getTaxItem().isSalesType()) {
					if (v.getIncreaseVATLine()) {

						u.setAmount(-1
								* (v.getJournalEntry().getEntry().get(0)
										.getTotal()));
					} else {
						u.setAmount((v.getJournalEntry().getEntry().get(0)
								.getTotal()));
					}

					if ((v.getTaxItem().getName()
							.equals("EC Sales Services Standard"))
							|| (v.getTaxItem().getName()
									.equals("EC Sales Goods Standard"))) {

						u.setAmount(-1 * u.getAmount());
					}

				} else {
					if (v.getIncreaseVATLine()) {

						u.setAmount((v.getJournalEntry().getEntry().get(0)
								.getTotal()));
					} else {
						u.setAmount(-1
								* (v.getJournalEntry().getEntry().get(0)
										.getTotal()));
					}
				}

				uncategorisedAmounts.add(u);
			}
		}

		// Entries from VATReturn;

		query = session.getNamedQuery(
				"getTAXAdjustment.checkingby.VATperiodEndDate").setParameter(
				"endDate", toDate);

		List<VATReturn> vatReturns = query.list();
		for (VATReturn v : vatReturns) {

			double amount = v.getBoxes().get(v.getBoxes().size() - 1)
					.getAmount();

			if (!DecimalUtil.isEquals(amount, 0)) {

				UncategorisedAmountsReport u = new UncategorisedAmountsReport();

				u.setTransactionType(v.getJournalEntry().getType());
				u.setTransactionNumber(v.getJournalEntry().getNumber());
				u.setID(v.getJournalEntry().getID());
				u.setSourceName(v.getJournalEntry().getEntry().get(0)
						.getAccount().getName());
				u.setMemo("Filed Uncategorised amounts");
				u.setDate(new ClientFinanceDate(v.getJournalEntry().getDate()
						.getDate()));

				// if (amount > 0)
				u.setAmount(-1 * (amount));
				// else
				// u.setAmount(amount);

				uncategorisedAmounts.add(u);
			}
		}

		return new VList<UncategorisedAmountsReport>(uncategorisedAmounts);
	}

	@Override
	public VList<VATItemDetail> getVATItemDetailReport(FinanceDate fromDate,
			FinanceDate toDate) throws DAOException, ParseException {

		List<VATItemDetail> vatItemDetails = new VList<VATItemDetail>();

		Session session = HibernateUtil.getCurrentSession();

		// Entries from the VATRate calculation

		Query query = session
				.getNamedQuery("getTAXAdjustment.by.taxAgencyidanddates")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXRateCalculation> taxRateCalculations = query.list();
		for (TAXRateCalculation v : taxRateCalculations) {

			VATItemDetail vi = new VATItemDetail();
			vi.setAmount(v.getTransactionItem().getLineTotal());
			vi.setDate(new ClientFinanceDate(v.getTransactionDate().getDate()));
			vi.setName(v.getTransactionItem().getTransaction()
					.getInvolvedPayee().getName());
			vi.setTransactionId(v.getTransactionItem().getTransaction().getID());
			vi.setMemo(v.getTransactionItem().getTransaction().getMemo());
			vi.setTransactionNumber(v.getTransactionItem().getTransaction()
					.getNumber());
			vi.setTransactionType(v.getTransactionItem().getTransaction()
					.getType());
			vi.setSalesPrice(vi.getAmount());

			vatItemDetails.add(vi);

		}

		// Entries from the VATAdjustment
		query = session
				.getNamedQuery(
						"getTAXAdjustment.by.dates.orderby.taxItemNameand.TransactionDate")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXAdjustment> vatAdjustments = query.list();
		for (TAXAdjustment v : vatAdjustments) {

			VATItemDetail vi = new VATItemDetail();

			if (v.getIncreaseVATLine()) {
				vi.setAmount(v.getJournalEntry().getTotal());
			} else {
				vi.setAmount(-1 * v.getJournalEntry().getTotal());
			}
			vi.setDate(new ClientFinanceDate(v.getJournalEntry().getDate()
					.getDate()));
			vi.setMemo("VAT Adjustment");
			vi.setName(v.getTaxItem().getTaxAgency().getName());
			vi.setTransactionId(v.getJournalEntry().getID());
			vi.setTransactionNumber(v.getJournalEntry().getNumber());
			vi.setTransactionType(v.getJournalEntry().getType());
			vatItemDetails.add(vi);
		}

		return new VList<VATItemDetail>(vatItemDetails);
	}

	@Override
	public VList<VATItemDetail> getVATItemDetailReport(String taxItemName,
			FinanceDate fromDate, FinanceDate toDate) throws DAOException,
			ParseException {

		List<VATItemDetail> vatItemDetails = new VList<VATItemDetail>();

		Session session = HibernateUtil.getCurrentSession();

		long transactionItemId = 0, vatItemId = 0;

		// Entries from the VATRate calculation

		Query query = session
				.getNamedQuery(
						"getTAXRateCalculation.by.dates.groupedByIdtransactionItem")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate)
				.setParameter("taxItemName", taxItemName);

		List<TAXRateCalculation> taxRateCalculations = query.list();
		for (TAXRateCalculation v : taxRateCalculations) {

			if ((v.getTaxItem().getID() != 4 && v.getTaxItem().getID() != 12 && v
					.getTaxItem().getID() != 14)
					|| (v.getTaxItem().getID() == 4 && v.isVATGroupEntry() == false)
					|| (v.getTaxItem().getID() == 12 && v.isVATGroupEntry() == false)
					|| (v.getTaxItem().getID() == 14 && v.isVATGroupEntry() == false)) {

				if (transactionItemId == 0
						|| transactionItemId != v.getTransactionItem().getID()) {

					VATItemDetail vi = new VATItemDetail();
					double amount = (!v.getTransactionItem().isVoid()) ? v
							.getLineTotal() : 0;
					vi.setAmount(amount);
					vi.setDate(new ClientFinanceDate(v.getTransactionDate()
							.getDate()));

					if (v.getTransactionItem().getTransaction()
							.getInvolvedPayee() != null)
						vi.setName(v.getTransactionItem().getTransaction()
								.getInvolvedPayee().getName());

					else {

						CashPurchase cashPurchase = (CashPurchase) (v
								.getTransactionItem().getTransaction());
						if (cashPurchase.getCashExpenseAccount() != null)
							vi.setName(cashPurchase.getCashExpenseAccount()
									.getName());
						else if (cashPurchase.getEmployee() != null)
							vi.setName(cashPurchase.getEmployee().getName());
					}

					vi.setTransactionId(v.getTransactionItem().getTransaction()
							.getID());
					vi.setMemo(v.getTransactionItem().getTransaction()
							.getMemo());
					vi.setTransactionNumber(v.getTransactionItem()
							.getTransaction().getNumber());
					vi.setTransactionType(v.getTransactionItem()
							.getTransaction().getType());
					vi.setSalesPrice(v.getTransactionItem().getLineTotal());

					transactionItemId = v.getTransactionItem().getID();
					vatItemId = v.getTaxItem().getID();

					vatItemDetails.add(vi);
				}

				// if (transactionItemId == v.getTransactionItem().getID() &&
				// v.getVatItem().getID() == 4) {
				// vi.
				// }

			}

		}

		// Entries from the VATAdjustment
		query = session
				.getNamedQuery("getTAXAdjustment.by.dates.and.taxItemName")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate)
				.setParameter("taxItemName", taxItemName);

		List<TAXAdjustment> vatAdjustments = query.list();
		for (TAXAdjustment v : vatAdjustments) {

			VATItemDetail vi = new VATItemDetail();

			if (v.getIncreaseVATLine()) {
				vi.setAmount(v.getJournalEntry().getTotal());
			} else {
				vi.setAmount(-1 * v.getJournalEntry().getTotal());
			}
			vi.setDate(new ClientFinanceDate(v.getJournalEntry().getDate()
					.getDate()));
			vi.setMemo("VAT Adjustment");
			vi.setName(v.getTaxItem().getTaxAgency().getName());
			vi.setTransactionId(v.getJournalEntry().getID());
			vi.setTransactionNumber(v.getJournalEntry().getNumber());
			vi.setTransactionType(v.getJournalEntry().getType());
			// vatItemDetails.add(vi);
		}

		return new VList<VATItemDetail>(vatItemDetails);
	}

	@Override
	public VList<VATItemSummary> getVATItemSummaryReport(FinanceDate fromDate,
			FinanceDate toDate) throws DAOException, ParseException {

		List<VATItemSummary> vatItemSummaries = new VList<VATItemSummary>();

		List<VATItemDetail> vatItemDetails = new VList<VATItemDetail>();

		Session session = HibernateUtil.getCurrentSession();

		// Getting entries from VATRateCalculation
		Query query = session
				.getNamedQuery(
						"getTAXRateCalculation.by.dates.orderbytaxItem.name")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXRateCalculation> taxRateCalculations = query.list();

		String tempVATItemName = null;
		long transactionItemId = 0;
		VATItemSummary vi = null;

		for (TAXRateCalculation v : taxRateCalculations) {

			if (tempVATItemName == null
					|| (!tempVATItemName.equals(v.getTaxItem().getName()))) {
				// if (transactionItemId == 0
				// || (transactionItemId != v.getTransactionItem().getID())) {

				if (vi != null) {
					vatItemSummaries.add(vi);
				}

				tempVATItemName = v.getTaxItem().getName();
				// transactionItemId = v.getTransactionItem().getID();

				vi = new VATItemSummary();
				vi.setName(tempVATItemName);
				// vi.setAmount(v.getVatAmount());
				vi.setAmount(v.getLineTotal());
				if (v.getTransactionItem() != null
						&& v.getTransactionItem().isVoid()) {
					vi.setAmount(vi.getAmount() - v.getLineTotal());
				}
			} else {

				vi.setAmount(vi.getAmount() + v.getLineTotal());
				if (v.getTransactionItem() != null
						&& v.getTransactionItem().isVoid()) {
					vi.setAmount(vi.getAmount() - v.getLineTotal());
				}
			}

		}
		if (vi != null)
			vatItemSummaries.add(vi);

		return new VList<VATItemSummary>(vatItemSummaries);
	}

	@Override
	public VList<ECSalesListDetail> getECSalesListDetailReport(
			String payeeName, FinanceDate fromDate, FinanceDate toDate)
			throws DAOException, ParseException {

		List<VATItemDetail> vatItemDetails = new VList<VATItemDetail>();

		Session session = HibernateUtil.getCurrentSession();

		// Getting entries from VATRateCalculation
		Query query = session
				.getNamedQuery(
						"getTAXRateCalculation.by.check.taxItemandDates.orderBy.transactionItem")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXRateCalculation> taxRateCalculations = query.list();

		List<ECSalesListDetail> details = new VList<ECSalesListDetail>();

		Map<String, List<ECSalesListDetail>> customerWiseDetail = new LinkedHashMap<String, List<ECSalesListDetail>>();

		boolean isAlreadyPut = false;
		long transactionItemId = 0;
		for (TAXRateCalculation v : taxRateCalculations) {
			if (v.getTransactionItem().getTransaction()
					.getTransactionCategory() == Transaction.CATEGORY_CUSTOMER) {

				if (v.getTransactionItem().getTransaction().getInvolvedPayee() != null
						&& (v.getTransactionItem().getTransaction()
								.getInvolvedPayee().getName())
								.equals(payeeName)) {

					if ((transactionItemId == 0)
							|| (transactionItemId != v.getTransactionItem()
									.getID())) {

						ECSalesListDetail e = new ECSalesListDetail();
						double amount = (!v.getTransactionItem().isVoid()) ? v
								.getLineTotal() : 0;
						e.setAmount(amount);
						e.setDate(new ClientFinanceDate(v.getTransactionDate()
								.getDate()));
						e.setMemo(v.getTransactionItem().getTransaction()
								.getMemo());
						e.setName(v.getTransactionItem().getTransaction()
								.getInvolvedPayee() != null ? v
								.getTransactionItem().getTransaction()
								.getInvolvedPayee().getName() : null);
						e.setSalesPrice(v.getTransactionItem().getLineTotal());
						e.setTransactionId(v.getTransactionItem()
								.getTransaction().getID());
						e.setTransactionNumber(v.getTransactionItem()
								.getTransaction().getNumber());
						e.setTransactionid(v.getTransactionItem()
								.getTransaction().getID());
						e.setTransactionType(v.getTransactionItem()
								.getTransaction().getType());

						if (customerWiseDetail.containsKey(e.getName())) {
							customerWiseDetail.get(e.getName()).add(e);
						} else {
							List<ECSalesListDetail> list = new VList<ECSalesListDetail>();
							list.add(e);
							customerWiseDetail.put(e.getName(), list);
						}

						transactionItemId = v.getTransactionItem().getID();

						// if (v.getTransactionItem().isVoid()) {
						//
						// ECSalesListDetail e2 = new ECSalesListDetail();
						//
						// e2.setAmount(-1 * (v.getLineTotal()));
						// e2.setDate(new
						// ClientFinanceDate(v.getTransactionDate()
						// .getTime()));
						// e2.setMemo(v.getTransactionItem().getTransaction()
						// .getMemo());
						// e2.setName(v.getTransactionItem().getTransaction()
						// .getInvolvedPayee() != null ? v
						// .getTransactionItem().getTransaction()
						// .getInvolvedPayee().getName() : null);
						// e2.setSalesPrice(v.getTransactionItem().getLineTotal());
						// e2.setTransactionId(v.getTransactionItem()
						// .getTransaction().getID());
						// e2.setTransactionNumber(v.getTransactionItem()
						// .getTransaction().getNumber());
						// e2.setTransactionid(v.getTransactionItem()
						// .getTransaction().getID());
						// e2.setTransactionType(v.getTransactionItem()
						// .getTransaction().getType());
						//
						// customerWiseDetail.get(e2.getName()).add(e2);
						// }
					}
				}
			}

		}

		String arr[] = new String[customerWiseDetail.keySet().size()];
		customerWiseDetail.keySet().toArray(arr);
		Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
		for (String a : arr) {
			details.addAll(customerWiseDetail.get(a));
		}

		return new VList<ECSalesListDetail>(details);

	}

	@Override
	public VList<ECSalesList> getECSalesListReport(FinanceDate fromDate,
			FinanceDate toDate) throws DAOException, ParseException {

		List<VATItemDetail> vatItemDetails = new VList<VATItemDetail>();

		Session session = HibernateUtil.getCurrentSession();

		// Getting entries from VATRateCalculation
		Query query = session
				.getNamedQuery("getTAXRateCalculation.by.check.taxItemandDates")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXRateCalculation> taxRateCalculations = query.list();

		List<ECSalesList> details = new VList<ECSalesList>();

		Map<String, Double> customerWiseDetail = new LinkedHashMap<String, Double>();

		long transactionItemId = 0;

		for (TAXRateCalculation v : taxRateCalculations) {
			if (v.getTransactionItem().getTransaction()
					.getTransactionCategory() == Transaction.CATEGORY_CUSTOMER) {

				if ((transactionItemId == 0)
						|| (transactionItemId != v.getTransactionItem().getID())) {

					String customerName;

					customerName = v.getTransactionItem().getTransaction()
							.getInvolvedPayee() != null ? v
							.getTransactionItem().getTransaction()
							.getInvolvedPayee().getName() : null;

					if (customerWiseDetail.containsKey(customerName)) {
						customerWiseDetail.put(customerName,
								(customerWiseDetail.get(customerName) + v
										.getLineTotal()));

					} else {
						customerWiseDetail.put(customerName, v.getLineTotal());

					}
					if (v.getTransactionItem().isVoid()) {
						customerWiseDetail.put(customerName,
								(customerWiseDetail.get(customerName) - v
										.getLineTotal()));
					}

					transactionItemId = v.getTransactionItem().getID();
				}
			}
		}

		String arr[] = new String[customerWiseDetail.keySet().size()];
		customerWiseDetail.keySet().toArray(arr);
		Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
		for (String a : arr) {
			ECSalesList e = new ECSalesList();
			e.setName(a);
			e.setAmount(customerWiseDetail.get(a));
			details.add(e);
		}

		return new VList<ECSalesList>(details);

	}

	@Override
	public VList<ReverseChargeListDetail> getReverseChargeListDetailReport(
			String payeeName, FinanceDate fromDate, FinanceDate toDate)
			throws DAOException, ParseException {
		// /////
		// ////////
		// /////////
		// //////
		// //////

		Session session = HibernateUtil.getCurrentSession();

		Query query = session
				.getNamedQuery("getReverseChargeListDetailReportEntries")
				.setParameter("startDate", fromDate.getDate())
				.setParameter("endDate", toDate.getDate());

		Map<String, List<ReverseChargeListDetail>> maps = new LinkedHashMap<String, List<ReverseChargeListDetail>>();

		List list = query.list();
		Iterator it = list.iterator();
		long tempTransactionItemID = 0;
		while (it.hasNext()) {
			Object[] object = (Object[]) it.next();

			if (((String) object[1]) != null
					&& ((String) object[1]).equals(payeeName)) {

				ReverseChargeListDetail r = new ReverseChargeListDetail();

				r.setAmount((Double) object[0]);
				r.setCustomerName((String) object[1]);
				r.setMemo((String) object[2]);
				r.setName(payeeName);
				r.setNumber((String) object[3]);
				r.setPercentage((Boolean) object[4]);
				r.setSalesPrice((Double) object[5]);
				r.setTransactionId((Long) object[6]);
				r.setTransactionType((Integer) object[7]);
				r.setDate(new ClientFinanceDate((Long) object[9]));

				if (maps.containsKey(r.getCustomerName())) {
					maps.get(r.getCustomerName()).add(r);
				} else {
					List<ReverseChargeListDetail> reverseChargesList = new VList<ReverseChargeListDetail>();
					reverseChargesList.add(r);
					maps.put(r.getCustomerName(), reverseChargesList);
				}

				if (tempTransactionItemID == 0
						|| ((Long) object[12]) != tempTransactionItemID) {

					ReverseChargeListDetail r2 = new ReverseChargeListDetail();

					tempTransactionItemID = ((Long) object[12]);

					r2.setAmount((Double) object[8]);
					r2.setCustomerName((String) object[1]);
					r2.setDate(new ClientFinanceDate((Long) object[9]));
					r2.setMemo((String) object[10]);
					r2.setName((String) object[1]);
					r2.setNumber((String) object[3]);
					r2.setPercentage(false);
					r2.setSalesPrice((Double) object[11]);
					r2.setTransactionId((Long) object[6]);
					r2.setTransactionType((Integer) object[7]);

					if (maps.containsKey(r2.getCustomerName())) {
						maps.get(r2.getCustomerName()).add(r2);
					} else {
						List<ReverseChargeListDetail> reverseChargesList = new VList<ReverseChargeListDetail>();
						reverseChargesList.add(r2);
						maps.put(r2.getCustomerName(), reverseChargesList);
					}
				}
			}
		}
		String[] names = new String[maps.keySet().size()];

		maps.keySet().toArray(names);

		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);

		List<ReverseChargeListDetail> reverseCharges = new VList<ReverseChargeListDetail>();

		for (String s : names) {

			reverseCharges.addAll(maps.get(s));
		}

		return new VList<ReverseChargeListDetail>(reverseCharges);
	}

	@Override
	public VList<ReverseChargeList> getReverseChargeListReport(
			FinanceDate fromDate, FinanceDate toDate) throws DAOException,
			ParseException {
		// /////
		// ////////
		// /////////
		// //////
		// //////

		Session session = HibernateUtil.getCurrentSession();

		// Getting entries from VATRateCalculation
		Query query = session
				.getNamedQuery(
						"getTAXRateCalculation.by.datesand.orderby.transactionItem")
				.setParameter("startDate", fromDate)
				.setParameter("endDate", toDate);

		List<TAXRateCalculation> taxRateCalculations = query.list();

		Map<String, Double> maps = new LinkedHashMap<String, Double>();

		long transactionItemId = 0;

		for (TAXRateCalculation v : taxRateCalculations) {

			if (v.getTransactionItem().getTransaction()
					.getTransactionCategory() == Transaction.CATEGORY_CUSTOMER) {

				String s = v.getTransactionItem().getTransaction()
						.getInvolvedPayee() != null ? v.getTransactionItem()
						.getTransaction().getInvolvedPayee().getName() : null;

				if ((transactionItemId == 0)
						|| (transactionItemId != v.getTransactionItem().getID())) {

					if (maps.containsKey(s)) {
						maps.put(
								s,
								maps.get(s) + v.getVatAmount()
										+ v.getLineTotal());
						if (v.getTransactionItem().isVoid()) {
							maps.put(
									s,
									maps.get(s)
											- (v.getVatAmount() + v
													.getLineTotal()));
						}
					} else {
						maps.put(s, v.getVatAmount() + v.getLineTotal());
						if (v.getTransactionItem().isVoid()) {
							maps.put(
									s,
									maps.get(s)
											- (v.getVatAmount() + v
													.getLineTotal()));
						}
					}

					transactionItemId = v.getTransactionItem().getID();
				}
			}
		}

		String[] names = new String[maps.keySet().size()];

		maps.keySet().toArray(names);

		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);

		List<ReverseChargeList> reverseCharges = new VList<ReverseChargeList>();

		for (String s : names) {

			ReverseChargeList r = new ReverseChargeList();
			r.setAmount(maps.get(s));
			r.setName(s);

			reverseCharges.add(r);
		}

		return new VList<ReverseChargeList>(reverseCharges);
	}

	@Override
	public void createTaxes(int... vatReturnType) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();

		org.hibernate.Transaction t = session.beginTransaction();
		try {
			for (int i : vatReturnType) {
				if (i == VATReturn.VAT_RETURN_IRELAND) {

					Account vatLiabilityAccount = new Account(
							Account.TYPE_OTHER_CURRENT_LIABILITY,
							String.valueOf(getNextNominalCode(Account.TYPE_OTHER_CURRENT_LIABILITY)),
							AccounterServerConstants.VAT_LIABILITY_ACCOUNT_IR,
							true, null, Account.CASH_FLOW_CATEGORY_OPERATING,
							0.0, false, "VAT Liability Account (IR)", 0.0,
							null, true, true, Company.getCompany()
									.getOpeningBalancesAccount(), null, true,
							Company.getCompany().getPreferences()
									.getStartOfFiscalYear());

					session.save(vatLiabilityAccount);

					TAXAgency collectorGeneral = createVATAgency(session,
							vatLiabilityAccount);

					createVATItemsOfIreland(session, collectorGeneral);
					createVATGroupsOfIreland(session);
					createVATCodesOfIreland(session);

				} else if (i == VATReturn.VAT_RETURN_UK_VAT) {

					Account openingBalenceAcount = (Account) session
							.getNamedQuery("unique.name.Account")
							.setString(0,
									AccounterServerConstants.OPENING_BALANCE)
							.list().get(0);
					Account salesTaxVAT = new Account(
							Account.TYPE_OTHER_CURRENT_LIABILITY, "2120",
							AccounterServerConstants.SALES_TAX_VAT_UNFILED,
							true, null, Account.CASH_FLOW_CATEGORY_OPERATING,
							0.0, false, "", 0.0, null, true, false,
							openingBalenceAcount, "113", true, Company
									.getCompany().getPreferences()
									.getStartOfFiscalYear());

					session.save(salesTaxVAT);

				}
			}

			t.commit();

		} catch (HibernateException he) {
			t.rollback();
			throw he;
		}

	}

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

	private void createVATCodesOfIreland(Session session) throws DAOException {
		createVATcodes(session, "ECP", "EC Purch Not Resale (PNFR)", true,
				true, "EC PNFR Standard Group");

		createVATcodes(session, "ECS", "EC Sale / Purch Resale (PFR)", true,
				true, "EC PFR Standard Group", "EC Sales Goods Std");

		createVATcodes(session, "EIR", "Exempt", true, true,
				"Exempt Purchases (IR)", "Exempt Sales (IR)");

		createVATcodes(session, "NIR", "Not Registered", true, true,
				"Not Registered Purchases (IR)", "Not Registered Sales (IR)");

		createVATcodes(session, "RIR", "Sale / Purch Resale (PFR@13.5%)", true,
				true, "Reduced PFR", "Reduced Sales (IR)");

		createVATcodes(session, "RNR", "Purch Not Resale (PNFR@13.5%)", true,
				true, "Reduced PNFR");

		createVATcodes(session, "SIR", "Sale / Purch Resale (PFR@21%)", true,
				true, "Standard PFR", "Standard Sales (IR)");

		createVATcodes(session, "SNR", "Purch Not Resale (PNFR@21%)", true,
				true, "Standard PNFR");

		createVATcodes(session, "ZIR", "Sale / Purch Resale (PFR@0%)", true,
				true, "Zero-Rated PFR", "Zero-Rated Sales (IR)");

		createVATcodes(session, "ZNR", "Purch Not Resale (PNFR@0%)", true,
				true, "Zero-Rated PNFR");

	}

	private void createVATcodes(Session session, String codeName,
			String description, boolean isActive, boolean isTaxable,
			String... vatItems) throws DAOException {

		TAXCode vc = new TAXCode();
		vc.setActive(isActive);
		vc.setDefault(true);
		vc.setDescription(description);
		vc.setName(codeName);
		vc.setTaxable(isTaxable);
		if (vatItems.length > 0)
			vc.setTAXItemGrpForPurchases((TAXItemGroup) getServerObjectByName(
					AccounterCoreType.VATRETURNBOX, vatItems[0]));
		if (vatItems.length > 1)
			vc.setTAXItemGrpForSales((TAXItemGroup) getServerObjectByName(
					AccounterCoreType.VATRETURNBOX, vatItems[1]));

	}

	public void createVATItemsOfIreland(Session session,
			TAXAgency collectorGeneral) throws DAOException {

		createVATItem(
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
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EC_PURCHASES_GOODS));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EC_SALES_GOODS));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EXEMPT_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EXEMPT_SALES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_EXEMPT_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_NOT_REGISTERED_SALES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_SALES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_SALES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_PURCHASES));

		createVATItem(
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
						AccounterServerConstants.IRELAND_DOMESTIC_SALES));

	}

	private void createVATItem(Session session, boolean isActive,
			String description, String name, boolean isPercentage,
			boolean isSalesType, TAXAgency vatAgency, double rate,
			VATReturnBox vatReturnBox) {

		TAXItem vi = new TAXItem();
		vi.setActive(isActive);
		vi.setDescription(description);
		vi.setName(name);
		vi.setPercentage(isPercentage);
		vi.setSalesType(isSalesType);
		vi.setTaxAgency(vatAgency);
		vi.setTaxRate(rate);
		vi.setVatReturnBox(vatReturnBox);
		session.save(vi);

	}

	private void createVATGroupsOfIreland(Session session) throws DAOException {

		createVATGroup(session, "EC PFR Standard Group",
				"EC Purchases For Resale (PFR) Standard Group", true, false,
				"Standard PFR", "EC PFR Standard");

		createVATGroup(session, "EC PFR Zero-Rated Group",
				"EC Purchases For Resale (PFR) Zero-Rated Group", true, false,
				"Zero-Rated PFR", "EC PFR Zero-Rated");

		createVATGroup(session, "EC PNFR Standard Group",
				"EC Purchases Not For Resale (PNFR) Standard Group", true,
				false, "Standard PNFR", "EC PNFR Standard");

		createVATGroup(session, "EC PNFR Zero-Rated Group",
				"EC Purchases Not For Resale (PNFR) Zero-Rated Group", true,
				false, "Zero-Rated PNFR", "EC PNFR Zero-Rated");
	}

	private void createVATGroup(Session session, String groupName,
			String description, boolean isActive, boolean isSalesType,
			String... vatItems) throws DAOException {

		TAXGroup vg = new TAXGroup();
		vg.setActive(isActive);
		vg.setDefault(true);
		vg.setDescription(description);
		vg.setName(groupName);
		vg.setPercentage(true);
		vg.setSalesType(isSalesType);
		List<TAXItem> vats = new VList<TAXItem>();
		double groupRate = 0;
		for (String s : vatItems) {
			TAXItem v = (TAXItem) getServerObjectByName(
					AccounterCoreType.TAXITEM, s);
			vats.add(v);
			groupRate += v.getTaxRate();
		}
		vg.setGroupRate(groupRate);
		vg.setTAXItems(vats);

	}

	@Override
	public KeyFinancialIndicators getKeyFinancialIndicators()
			throws DAOException {

		KeyFinancialIndicators keyFinancialIndicators = new KeyFinancialIndicators();

		Map<String, Map<Integer, Double>> rows = new LinkedHashMap<String, Map<Integer, Double>>();

		Session session = HibernateUtil.getCurrentSession();
		List<Account> accounts = new VList<Account>(session.getNamedQuery(
				"list.Account").list());
		// List<Account> accounts = getCompany().getAccounts();

		Set<Account> sales = new HashSet<Account>();
		Set<Account> directCosts = new HashSet<Account>();
		Set<Account> indirectCosts = new HashSet<Account>();
		Set<Account> bankAccounts = new HashSet<Account>();

		for (Account account : accounts) {
			switch (account.getType()) {
			case Account.TYPE_INCOME:
				sales.add(account);
				break;
			case Account.TYPE_COST_OF_GOODS_SOLD:
				directCosts.add(account);
				break;
			case Account.TYPE_OTHER_EXPENSE:
				directCosts.add(account);
				break;
			case Account.TYPE_EXPENSE:
				indirectCosts.add(account);
				break;
			case Account.TYPE_OTHER_CURRENT_ASSET:
				Long l1 = Long.parseLong(account.getNumber());
				if (l1 >= 1100 && l1 <= 1179)
					bankAccounts.add(account);
				break;
			}
		}

		Map<Integer, Double> salesEntries = new LinkedHashMap<Integer, Double>();
		for (Account account : sales) {
			Map monthViceAmounts = account.getMonthViceAmounts();
			Set<Integer> keys = monthViceAmounts.keySet();
			for (Integer key : keys) {
				if (salesEntries.containsKey(key)) {
					salesEntries.put(key, salesEntries.get(key)
							+ (Double) monthViceAmounts.get(key));
				} else {
					salesEntries.put(key, (Double) monthViceAmounts.get(key));
				}
			}
		}
		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_SALES,
				salesEntries);

		Map<Integer, Double> grossProfitEntries = new LinkedHashMap<Integer, Double>();

		Map<Integer, Double> directCostsEntries = new LinkedHashMap<Integer, Double>();
		for (Account account : directCosts) {
			Map monthViceAmounts = account.getMonthViceAmounts();
			Set<Integer> keys = monthViceAmounts.keySet();
			for (Integer key : keys) {
				if (directCostsEntries.containsKey(key)) {
					directCostsEntries.put(key, directCostsEntries.get(key)
							+ (Double) monthViceAmounts.get(key));
				} else {
					directCostsEntries.put(key,
							(Double) monthViceAmounts.get(key));
				}

				// double salesEntryAmount = 0.0;
				// if (salesEntries.containsKey(key)) {
				// salesEntryAmount = salesEntries.get(key);
				// }
				// double directCostsAmount = 0.0;
				// if (directCostsEntries.containsKey(key)) {
				// directCostsAmount = directCostsEntries.get(key);
				// }
				//
				// // to calculate the gross profit
				// if (grossProfitEntries.containsKey(key)) {
				// grossProfitEntries.put(key, grossProfitEntries.get(key)
				// + (salesEntryAmount - directCostsAmount));
				// } else {
				// grossProfitEntries.put(key, salesEntryAmount
				// - directCostsAmount);
				// }
			}
		}

		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_DIRECT_COSTS,
				directCostsEntries);

		// for (Integer key : salesEntries.keySet()) {
		//
		// double grossProfitAmount = 0.0;
		// if (directCostsEntries.containsKey(key)) {
		//
		// grossProfitAmount = salesEntries.get(key)
		// - directCostsEntries.get(key);
		// } else {
		// grossProfitAmount = salesEntries.get(key);
		// }
		//
		// // to calculate the gross profit
		// if (grossProfitEntries.containsKey(key)) {
		// grossProfitEntries.put(key, grossProfitEntries.get(key)
		// + (grossProfitAmount));
		// } else {
		// grossProfitEntries.put(key, grossProfitAmount);
		// }
		//
		// }
		List<Integer> keysList = session
				.createSQLQuery(
						"select month from ACCOUNT_AMOUNTS group by month order by month")
				.list();
		for (int key : keysList) {
			double salesEntryAmount = 0.0;
			if (salesEntries.containsKey(key)) {
				salesEntryAmount = salesEntries.get(key);
			}
			double directCostsAmount = 0.0;
			if (directCostsEntries.containsKey(key)) {
				directCostsAmount = directCostsEntries.get(key);
			}

			// to calculate the gross profit
			if (grossProfitEntries.containsKey(key)) {
				grossProfitEntries.put(key, grossProfitEntries.get(key)
						+ (salesEntryAmount - directCostsAmount));
			} else {
				grossProfitEntries.put(key, salesEntryAmount
						- directCostsAmount);
			}
		}

		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_GROSS_PROFIT,
				grossProfitEntries);

		Map<Integer, Double> netProfitEntries = new LinkedHashMap<Integer, Double>();

		Map<Integer, Double> indirectCostsEntries = new LinkedHashMap<Integer, Double>();
		for (Account account : indirectCosts) {
			Map monthViceAmounts = account.getMonthViceAmounts();
			Set<Integer> keys = monthViceAmounts.keySet();
			for (Integer key : keys) {
				if (indirectCostsEntries.containsKey(key)) {
					indirectCostsEntries.put(key, indirectCostsEntries.get(key)
							+ (Double) monthViceAmounts.get(key));
				} else {
					indirectCostsEntries.put(key,
							(Double) monthViceAmounts.get(key));
				}
				// to calculate the net profit.

				// double grossProfitAmount = 0.0;
				// if (grossProfitEntries.containsKey(key)) {
				// grossProfitAmount = grossProfitEntries.get(key);
				// }
				// double indirectCostsAmount = 0.0;
				// if (indirectCostsEntries.containsKey(key)) {
				// indirectCostsAmount = indirectCostsEntries.get(key);
				// }
				//
				// if (netProfitEntries.containsKey(key)) {
				// netProfitEntries.put(key, netProfitEntries.get(key)
				// + (grossProfitAmount - indirectCostsAmount));
				// } else {
				// netProfitEntries.put(key, grossProfitAmount
				// - indirectCostsAmount);
				// }

			}
		}
		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_INDIRECT_COSTS,
				indirectCostsEntries);

		// for (Integer key : grossProfitEntries.keySet()) {
		//
		// double netProfitAmount = 0.0;
		// if (indirectCostsEntries.containsKey(key)) {
		//
		// netProfitAmount = grossProfitEntries.get(key) -
		// indirectCostsEntries.get(key);
		// } else {
		// netProfitAmount = grossProfitEntries.get(key);
		// }
		//
		// // to calculate the gross profit
		// if (netProfitEntries.containsKey(key)) {
		// netProfitEntries.put(key, grossProfitEntries.get(key)
		// + (netProfitAmount));
		// } else {
		// netProfitEntries.put(key, netProfitAmount);
		// }
		//
		// }

		for (int key : keysList) {
			double grossProfitAmount = 0.0;
			if (grossProfitEntries.containsKey(key)) {
				grossProfitAmount = grossProfitEntries.get(key);
			}
			double indirectCostsAmount = 0.0;
			if (indirectCostsEntries.containsKey(key)) {
				indirectCostsAmount = indirectCostsEntries.get(key);
			}

			if (netProfitEntries.containsKey(key)) {
				netProfitEntries.put(key, netProfitEntries.get(key)
						+ (grossProfitAmount - indirectCostsAmount));
			} else {
				netProfitEntries.put(key, grossProfitAmount
						- indirectCostsAmount);
			}
		}

		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_NET_PROFIT,
				netProfitEntries);

		Map<Integer, Double> bankEntries = new LinkedHashMap<Integer, Double>();
		for (Account account : bankAccounts) {
			Map monthViceAmounts = account.getMonthViceAmounts();
			Set<Integer> keys = monthViceAmounts.keySet();
			for (Integer key : keys) {
				if (bankEntries.containsKey(key)) {
					bankEntries.put(key, bankEntries.get(key)
							+ (Double) monthViceAmounts.get(key));
				} else {
					bankEntries.put(key, (Double) monthViceAmounts.get(key));
				}
			}
		}
		Map<Integer, Double> empty = new LinkedHashMap<Integer, Double>();
		rows.put("empty", empty);

		rows.put(AccounterServerConstants.FINANCIAL_INDICATOR_BANK_ACCOUNTS,
				bankEntries);
		keyFinancialIndicators.setIndicators(rows);
		return keyFinancialIndicators;
	}

	// @Override
	// public Boolean updateCompanyPreferences(ClientCompanyPreferences
	// preferences)
	// throws DAOException {
	//
	// if (preferences == null)
	// return false;
	//
	// // String id = String.valueOf(preferences.getID());
	//
	// Command cmd = new Command(UPDATE_PREFERENCES, "", null);
	//
	// cmd.data = preferences;
	//
	// cmd.arg1 = company.getID();
	//
	// cmd.arg2 = preferences.getObjectType().getServerClassSimpleName();
	//
	// space.sendCommand(cmd, this);
	//
	// return true;
	//
	// }
	//
	// @Override
	// public Boolean updateCompany(ClientCompany clientCompany)
	// throws DAOException {
	//
	// if (clientCompany == null)
	// return false;
	//
	// // String id = String.valueOf(preferences.getID());
	//
	// Command cmd = new Command(UPDATE_COMPANY, "", null);
	//
	// cmd.data = clientCompany;
	//
	// cmd.arg1 = clientCompany.id;
	//
	// cmd.arg2 = clientCompany.getObjectType().getServerClassSimpleName();
	//
	// space.sendCommand(cmd, this);
	//
	// return true;
	//
	// }

	public ClientCompany getClientCompany(String logInUserEmail)
			throws AccounterException {

		Session session = HibernateUtil.getCurrentSession();

		Company company = getCompany();
		User logInUser = company.getUserByUserEmail(logInUserEmail);
		if (logInUser == null) {
			throw new AccounterException(
					AccounterException.ERROR_PERMISSION_DENIED);
		}

		Hibernate.initialize(company);

		company.setAccounts(getAccountsListBySorted());

		company.setFiscalYears(new VList<FiscalYear>(session.getNamedQuery(
				"list.FiscalYear").list()));

		company.setPayees(new VList<Payee>(session.getNamedQuery("list.Payee")
				.list()));

		company.setItems(new VList<Item>(session.getNamedQuery("list.Item")
				.list()));

		company.setCustomerGroups(new VList<CustomerGroup>(session
				.getNamedQuery("list.CustomerGroup").list()));

		company.setVendorGroups(new VList<VendorGroup>(session.getNamedQuery(
				"list.VendorGroup").list()));

		company.setShippingTerms(new VList<ShippingTerms>(session
				.getNamedQuery("list.ShippingTerms").list()));

		company.setShippingMethods(new VList<ShippingMethod>(session
				.getNamedQuery("list.ShippingMethod").list()));

		company.setPriceLevels(new VList<PriceLevel>(session.getNamedQuery(
				"list.PriceLevel").list()));

		company.setItemGroups(new VList<ItemGroup>(session.getNamedQuery(
				"list.ItemGroup").list()));

		company.setTaxGroups(new VList<TAXGroup>(session.getNamedQuery(
				"list.TAXGroup").list()));

		company.setPaymentTerms(new VList<PaymentTerms>(session.getNamedQuery(
				"list.PaymentTerms").list()));

		company.setCreditRatings(new VList<CreditRating>(session.getNamedQuery(
				"list.CreditRating").list()));

		company.setSalesPersons(new VList<SalesPerson>(session.getNamedQuery(
				"list.SalesPerson").list()));

		company.setTaxCodes(new VList<TAXCode>(session.getNamedQuery(
				"list.TAXCode").list()));

		company.setTaxItems(new VList<TAXItem>(session.getNamedQuery(
				"list.TAXItem").list()));

		company.setTaxItemGroups(new VList<TAXItemGroup>(session.getNamedQuery(
				"list.TAXItemGroups").list()));

		company.setBanks(new VList<Bank>(session.getNamedQuery("list.Bank")
				.list()));

		company.setTaxrates(new VList<TaxRates>(session.getNamedQuery(
				"list.TaxRates").list()));

		company.setFixedAssets(new VList<FixedAsset>(session.getNamedQuery(
				"list.FixedAsset").list()));
		// company
		// .setSellingDisposingFixedAssets(new
		// HashSet<SellingOrDisposingFixedAsset>(
		// session.getNamedQuery(
		// "list.SellingOrDisposingFixedAsset").list()));

		company.setVatReturns(new VList<VATReturn>(session.getNamedQuery(
				"list.VATReturn").list()));
		company.setCurrencies(new HashSet<Currency>(session.getNamedQuery(
				"list.currency").list()));

		company.setTaxAdjustments(new VList<TAXAdjustment>(session
				.getNamedQuery("list.TAXAdjustment").list()));

		// company.setVatCodes(new VList<TAXCode>(session.getNamedQuery(
		// "list.VATCode").list()));

		// company.setVatItemGroups(new VList<TAXItemGroup>(session
		// .getNamedQuery("list.VATItemGroup").list()));

		company.setTaxAgencies(new VList<TAXAgency>(session.getNamedQuery(
				"list.TAXAgency").list()));

		company.setVatBoxes(new HashSet<Box>(session.getNamedQuery("list.Box")
				.list()));
		company.setVatReturnBoxes(new HashSet<VATReturnBox>(session
				.getNamedQuery("list.VATReturnBox").list()));

		company.setBrandingTheme(new VList<BrandingTheme>(session
				.getNamedQuery("list.BrandingTheme").list()));

		company = company.toCompany(company);
		ClientConvertUtil clientConvertUtil = new ClientConvertUtil();
		ClientCompany clientCompany = clientConvertUtil.toClientObject(company,
				ClientCompany.class);

		ClientFinanceDate[] dates = getMinimumAndMaximumTransactionDate();

		clientCompany.setTransactionStartDate(dates[0]);
		clientCompany.setTransactionEndDate(dates[1]);

		clientCompany.setUsersList(getAllEmployees());

		// User logInUSer = (User) session.getNamedQuery("getuser.by.email")
		// .setParameter("email", logInUserEmail).uniqueResult();
		clientCompany.setLoggedInUser(logInUser.getClientUser());

		List list = session.getNamedQuery("get.All.Units").list();

		VList<ClientUnit> units = new VList<ClientUnit>();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Unit unit = (Unit) iterator.next();
			units.add(clientConvertUtil.toClientObject(unit, ClientUnit.class));
		}

		clientCompany.setUnits(units);
		clientCompany.setConfigured(company.isConfigured());

		return clientCompany;
	}

	public List<FinanceLogger> getLog(long id, boolean isNext) {
		if (id == 1)
			return null;
		Session session = HibernateUtil.getCurrentSession();
		List<FinanceLogger> logList = null;

		if (id == -1) {

			Query query = session
					.getNamedQuery("getFinanceLogger.orderby.id.desc");
			query = query.setMaxResults(20);

			logList = query.list();
		} else {
			long i = 0;

			if (!isNext) {
				i = id;
				id += 20;

			} else {
				if (id <= 20)
					i = -1;
				else
					i = id - 20;

			}

			i++;
			Query query = session
					.getNamedQuery(
							"getFinanceLogger.by.checkidwith.lowerrangeandupperrange")
					.setParameter("lowerRange", i)
					.setParameter("upperRange", id);
			query = query.setMaxResults(20);

			logList = query.list();
		}
		// int index = 0;
		//
		// for (FinanceLogger log : logList) {
		// if (log.getID() == id) {
		// index = logList.indexOf(log);
		// break;
		// }
		// }
		//
		// List<FinanceLogger> log = new VList<FinanceLogger>();
		//
		// if (isNext) {
		// for (int i = index + 1; i < index + 20; i++) {
		// if (i <= logList.size())
		// break;
		// log.add(logList.get(i));
		//
		// }
		// } else {
		// for (int i = index - 20; i < index; i++) {
		//
		// log.add(logList.get(i));
		//
		// }
		// }

		return logList;

	}

	public List<FinanceLogger> getLog(String date, long id, boolean isNext) {
		if (id == 1)
			return null;
		FinanceDate date1 = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			date1 = new FinanceDate(format.parse(date));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Session session = HibernateUtil.getCurrentSession();
		List<FinanceLogger> logList = null;

		if (id == -1) {

			try {

				Criteria criteria = session.createCriteria(FinanceLogger.class);
				criteria.add(Restrictions.le("createdDate", date1)).addOrder(
						Order.desc("id"));
				criteria.setMaxResults(20);
				logList = criteria.list();

				// Query query = session
				// .createSQLQuery(
				// "from com.vimukti.accounter.core.FinanceLogger f where f.createdDate = "
				// +date1+" order by f.id desc");
				//
				// logList = query.list();
			} catch (HibernateException e) {
				e.printStackTrace();
			}

		} else {
			long i = 0;

			if (!isNext) {
				i = id;
				id += 20;

			} else {
				if (id <= 20)
					i = -1;
				else
					i = id - 20;

			}

			i++;

			try {
				Criteria criteria2 = session
						.createCriteria(FinanceLogger.class);
				criteria2
						.add(Restrictions.between("id", new Long(i), new Long(
								id)))
						.add(Restrictions.le("createdDate", date1))
						.addOrder(Order.desc("id"));
				criteria2.setMaxResults(20);
				logList = criteria2.list();

				// Query query = session
				// .createQuery(
				// "from com.vimukti.accounter.core.FinanceLogger f where f.id > :lowerRange and f.id <= :upperRange and f.createdDate = ? order by f.id desc"
				// )
				// .setParameter("lowerRange", i).setParameter(
				// "upperRange", id).setParameter(0, new Long());

				// logList = query.list();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
		return logList;
	}

	public boolean hasFileVAT(TAXAgency vatAgency, FinanceDate startDate,
			FinanceDate endDate) {

		Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(
				VATReturn.class);

		List list = criteria.add(Restrictions.ge("VATperiodEndDate", endDate))
				.list();

		if (list != null && list.size() > 0 && list.get(0) != null)
			return true;

		return false;
	}

	@Override
	public String getNextTransactionNumber(int transactionType) {

		// Query query = HibernateUtil
		// .getCurrentSession()
		// .createQuery(
		// "select max(t.id) from com.vimukti.accounter.core.Transaction t where t.type =:transactionType")
		// .setParameter("transactionType", transactionType);
		//
		// List list = query.list();
		//
		// if (list == null || list.size() <= 0 || list.get(0) == null) {
		// return "1";
		// }
		//
		// String prevNumber = getPreviousTransactionNumber(transactionType,
		// (Long) list.get(0));
		//
		// return getStringwithIncreamentedDigit(prevNumber);
		return NumberUtils.getNextTransactionNumber(transactionType);
	}

	public VList<HrEmployee> getHREmployees() {
		Session session = HibernateUtil.getCurrentSession();
		SQLQuery query = session.createSQLQuery(
				"SELECT empd.FULL_NAME as name FROM USERS empd").addScalar(
				"name", Hibernate.STRING);
		List list = query.list();

		// Object[] object = null;
		Iterator iterator = list.iterator();
		List<HrEmployee> hrEmployees = new VList<HrEmployee>();
		while ((iterator).hasNext()) {

			HrEmployee hrEmployee = new HrEmployee();
			// object = (Object[]) iterator.next();
			hrEmployee.setEmployeeName((String) iterator.next());
			// hrEmployee.setEmployeeNum((String) object[1]);

			hrEmployees.add(hrEmployee);
		}
		return new VList<HrEmployee>(hrEmployees);
	}

	// private String getStringwithIncreamentedDigit(String prevNumber) {
	//
	// String incredNumber = "";
	// for (int i = prevNumber.length() - 1; i >= 0; i--) {
	// char ch = prevNumber.charAt(i);
	//
	// if (incredNumber.length() > 0 && !Character.isDigit(ch)) {
	// break;
	// } else if (Character.isDigit(ch)) {
	// incredNumber = incredNumber + ch;
	// }
	//
	// }
	// if (incredNumber.length() > 0) {
	// incredNumber = new StringBuffer(incredNumber).reverse().toString();
	// prevNumber = prevNumber.replace(incredNumber, ""
	// + (Long.parseLong(incredNumber) + 1));
	// }
	// return prevNumber;
	//
	// }

	private String getStringwithIncreamentedDigit(String prevNumber) {

		String incredNumber = "";
		for (int i = prevNumber.length() - 1; i >= 0; i--) {
			char ch = prevNumber.charAt(i);

			if (incredNumber.length() > 0 && !Character.isDigit(ch)) {
				break;
			} else if (Character.isDigit(ch)) {
				incredNumber = incredNumber + ch;
			}

		}
		if (incredNumber.length() > 0) {
			incredNumber = new StringBuffer(incredNumber).reverse().toString();
			prevNumber = prevNumber.replace(incredNumber,
					"" + (Long.parseLong(incredNumber) + 1));
		}
		return prevNumber;

	}

	public String getPreviousTransactionNumber(int transactionType,
			long maxCount) {

		Query query = HibernateUtil.getCurrentSession()
				.getNamedQuery("getTransactionNumber.from.typeandId")
				.setParameter(0, transactionType).setParameter(0, maxCount);

		List list = query.list();

		String num = (list != null) && (list.size() > 0) ? ((String) list
				.get(0)) : "";
		if (num.replaceAll("[\\D]", "").length() > 0) {
			return num;
		} else {
			if (maxCount != 0) {
				maxCount = maxCount - 1;
				return getPreviousTransactionNumber(transactionType, maxCount);
			}
		}

		return "0";
	}

	private boolean isTransactionNumberExist(IAccounterCore object)
			throws AccounterException {
		FlushMode flushMode = HibernateUtil.getCurrentSession().getFlushMode();
		HibernateUtil.getCurrentSession().setFlushMode(FlushMode.COMMIT);

		try {

			if (object instanceof ClientTransaction
					&& !(object instanceof ClientTransferFund)
					&& !(object instanceof ClientMakeDeposit)) {

				ClientTransaction clientObject = (ClientTransaction) object;

				if (clientObject.isVoid())
					return true;

				if (clientObject instanceof ClientPayBill
						&& ((ClientPayBill) clientObject).getPayBillType() == 1) {
					return true;
				}

				if (clientObject.getNumber() == null
						|| clientObject.getNumber().equals(""))
					return true;

				Query query = HibernateUtil
						.getCurrentSession()
						.getNamedQuery("getTransaction.by.check.type.number.id")
						.setParameter(0, clientObject.getType())
						.setParameter(1, clientObject.getNumber())
						.setParameter(2, clientObject.getID());

				List list = query.list();

				if (list != null
						&& list.size() > 0
						&& list.get(0) != null
						&& !(this.getCompany().getPreferences()
								.getAllowDuplicateDocumentNumbers())) {
					throw new AccounterException(
							AccounterException.ERROR_NUMBER_CONFLICT,
							" A Transaction already exists with this number. Please give another one. ");
				}

			}
			return true;

		} finally {
			HibernateUtil.getCurrentSession().setFlushMode(flushMode);
		}
	}

	/**
	 * Returns the Current Company
	 * 
	 * @return
	 */
	public Company getCompany() {
		Session session = HibernateUtil.getCurrentSession();
		return (Company) session.get(Company.class, 1l);
	}

	private VList<Account> getAccountsListBySorted() {
		Session session = HibernateUtil.getCurrentSession();
		VList<Account> list1 = new VList<Account>();
		List<Account> list2 = new VList<Account>();
		VList<Account> list = new VList<Account>(session.getNamedQuery(
				"list.Account").list());
		int sort[] = { 14, 15, 18, 16, 3, 4, 8, 9, 6, 12, 7, 13 };

		int indexof1180 = 0;
		Account undepositedFounds = null;
		Account deposits = null;
		int indexof5220 = 0;
		int indexof1001 = 0;
		for (int i = 0; i < sort.length; i++) {
			Iterator<Account> iterator = list.iterator();
			while (iterator.hasNext()) {
				Account acount = iterator.next();
				if (acount.getNumber().equals("2010")) {
					continue;
				}
				if (acount.getNumber().equals("1175"))
					undepositedFounds = acount;
				if (acount.getNumber().equals("1003"))
					deposits = acount;
				if (sort[i] == acount.getType()) {
					if (acount.getNumber().equals("5900")
							|| acount.getNumber().equals("5920")
							|| acount.getNumber().equals("5930"))
						list2.add(acount);
					else
						list1.add(acount);

					if (acount.getNumber().equals("5220"))
						indexof5220 = list1.indexOf(acount);
					if (acount.getNumber().equals("1180"))
						indexof1180 = list1.indexOf(acount);
					if (acount.getNumber().equals("1001")) {
						indexof1001 = list1.indexOf(acount);
					}
					iterator.remove();
				}
			}
		}
		list1.addAll(list);
		if (getCompany().getAccountingType() == Company.ACCOUNTING_TYPE_UK) {
			if (indexof1180 - 1 > 0) {
				list1.remove(undepositedFounds);
				list1.add(indexof1180 - 1, undepositedFounds);
			}
		}
		if (indexof1001 - 1 > 0) {
			list1.remove(deposits);
			list1.add(indexof1001 + 1, deposits);
		}
		if (!list.isEmpty()) {
			list1.addAll(indexof5220 + 1, list2);
		}

		return list1;
	}

	public static void createView() {
		Session session = HibernateUtil.getCurrentSession();
		session.getNamedQuery("createSalesPurchasesView").executeUpdate();
		session.getNamedQuery("createTransactionHistoryView").executeUpdate();

	}

	public static void createViewsForclient() {
		Session session = HibernateUtil.getCurrentSession();
		session.getNamedQuery("createSalesPurchasesViewForclient")
				.executeUpdate();
		session.getNamedQuery("createTransactionHistoryViewForclient")
				.executeUpdate();
	}

	@Override
	public VList<PayeeList> getPayeeList(int transactionCategory)
			throws DAOException {
		try {
			Session session = HibernateUtil.getCurrentSession();

			FinanceDate currentDate = new FinanceDate();

			Calendar currentMonthStartDateCal = new GregorianCalendar();
			currentMonthStartDateCal.setTime(currentDate.getAsDateObject());
			currentMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar currentMonthEndDateCal = new GregorianCalendar();
			currentMonthEndDateCal.setTime(currentDate.getAsDateObject());
			currentMonthEndDateCal.set(Calendar.DATE, currentMonthEndDateCal
					.getActualMaximum(Calendar.DAY_OF_MONTH));

			Calendar previousFirstMonthStartDateCal = new GregorianCalendar();
			previousFirstMonthStartDateCal.setTime(currentDate
					.getAsDateObject());
			previousFirstMonthStartDateCal.set(Calendar.MONTH,
					previousFirstMonthStartDateCal.get(Calendar.MONTH) - 1);
			previousFirstMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar previousFirstMonthEndDateCal = new GregorianCalendar();
			previousFirstMonthEndDateCal.setTime(currentDate.getAsDateObject());
			previousFirstMonthEndDateCal.set(Calendar.MONTH,
					previousFirstMonthEndDateCal.get(Calendar.MONTH) - 1);
			previousFirstMonthEndDateCal.set(Calendar.DATE,
					previousFirstMonthEndDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			Calendar previousSecondMonthStartDateCal = new GregorianCalendar();
			previousSecondMonthStartDateCal.setTime(currentDate
					.getAsDateObject());
			previousSecondMonthStartDateCal.set(Calendar.MONTH,
					previousSecondMonthStartDateCal.get(Calendar.MONTH) - 2);
			previousSecondMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar previousSecondMonthEndDateCal = new GregorianCalendar();
			previousSecondMonthEndDateCal
					.setTime(currentDate.getAsDateObject());
			previousSecondMonthEndDateCal.set(Calendar.MONTH,
					previousSecondMonthEndDateCal.get(Calendar.MONTH) - 2);
			previousSecondMonthEndDateCal.set(Calendar.DATE,
					previousSecondMonthEndDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			Calendar previousThirdMonthStartDateCal = new GregorianCalendar();
			previousThirdMonthStartDateCal.setTime(currentDate
					.getAsDateObject());
			previousThirdMonthStartDateCal.set(Calendar.MONTH,
					previousThirdMonthStartDateCal.get(Calendar.MONTH) - 3);
			previousThirdMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar previousThirdMonthEndDateCal = new GregorianCalendar();
			previousThirdMonthEndDateCal.setTime(currentDate.getAsDateObject());
			previousThirdMonthEndDateCal.set(Calendar.MONTH,
					previousThirdMonthEndDateCal.get(Calendar.MONTH) - 3);
			previousThirdMonthEndDateCal.set(Calendar.DATE,
					previousThirdMonthEndDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			Calendar previousFourthMonthStartDateCal = new GregorianCalendar();
			previousFourthMonthStartDateCal.setTime(currentDate
					.getAsDateObject());
			previousFourthMonthStartDateCal.set(Calendar.MONTH,
					previousFourthMonthStartDateCal.get(Calendar.MONTH) - 4);
			previousFourthMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar previousFourthMonthEndDateCal = new GregorianCalendar();
			previousFourthMonthEndDateCal
					.setTime(currentDate.getAsDateObject());
			previousFourthMonthEndDateCal.set(Calendar.MONTH,
					previousFourthMonthEndDateCal.get(Calendar.MONTH) - 4);
			previousFourthMonthEndDateCal.set(Calendar.DATE,
					previousFourthMonthEndDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			Calendar previousFifthMonthStartDateCal = new GregorianCalendar();
			previousFifthMonthStartDateCal.setTime(currentDate
					.getAsDateObject());
			previousFifthMonthStartDateCal.set(Calendar.MONTH,
					previousFifthMonthStartDateCal.get(Calendar.MONTH) - 5);
			previousFifthMonthStartDateCal.set(Calendar.DATE, 1);

			Calendar previousFifthMonthEndDateCal = new GregorianCalendar();
			previousFifthMonthEndDateCal.setTime(currentDate.getAsDateObject());
			previousFifthMonthEndDateCal.set(Calendar.MONTH,
					previousFifthMonthEndDateCal.get(Calendar.MONTH) - 5);
			previousFifthMonthEndDateCal.set(Calendar.DATE,
					previousFifthMonthEndDateCal
							.getActualMaximum(Calendar.DAY_OF_MONTH));

			Query query = null;

			if (transactionCategory == Transaction.CATEGORY_CUSTOMER) {

				query = session
						.getNamedQuery("getCustomersList")
						.setParameter(
								"currentMonthStartDateCal",
								new FinanceDate(currentMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"currentMonthEndDateCal",
								new FinanceDate(currentMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthStartDateCal",
								new FinanceDate(previousFirstMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthEndDateCal",
								new FinanceDate(previousFirstMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthStartDateCal",
								new FinanceDate(previousSecondMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthEndDateCal",
								new FinanceDate(previousSecondMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthStartDateCal",
								new FinanceDate(previousThirdMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthEndDateCal",
								new FinanceDate(previousThirdMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthStartDateCal",
								new FinanceDate(previousFourthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthEndDateCal",
								new FinanceDate(previousFourthMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFifthMonthStartDateCal",
								new FinanceDate(previousFifthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFifthMonthEndDateCal",
								new FinanceDate(previousFifthMonthEndDateCal
										.getTime()).getDate());

			} else if (transactionCategory == Transaction.CATEGORY_VENDOR) {

				query = session
						.getNamedQuery("getVendorsList")
						.setParameter(
								"currentMonthStartDateCal",
								new FinanceDate(currentMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"currentMonthEndDateCal",
								new FinanceDate(currentMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthStartDateCal",
								new FinanceDate(previousFirstMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthEndDateCal",
								new FinanceDate(previousFirstMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthStartDateCal",
								new FinanceDate(previousSecondMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthEndDateCal",
								new FinanceDate(previousSecondMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthStartDateCal",
								new FinanceDate(previousThirdMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthEndDateCal",
								new FinanceDate(previousThirdMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthStartDateCal",
								new FinanceDate(previousFourthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthEndDateCal",
								new FinanceDate(previousFourthMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFifthMonthStartDateCal",
								new FinanceDate(previousFifthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFifthMonthEndDateCal",
								new FinanceDate(previousFifthMonthEndDateCal
										.getTime()).getDate());
			}

			List list = query.list();
			if (list != null) {
				Object[] object = null;
				String payeeName = null;
				PayeeList payeeList = null;
				Iterator iterator = list.iterator();
				List<PayeeList> queryResult = new VList<PayeeList>();
				while ((iterator).hasNext()) {

					object = (Object[]) iterator.next();

					if (payeeName != null && ((String) object[1]) != null
							&& payeeName.equals((String) object[1])) {

						payeeList.setCurrentMonth(payeeList.getCurrentMonth()
								+ (Double) object[4]);

						payeeList.setPreviousMonth(payeeList.getPreviousMonth()
								+ (Double) object[5]);

						payeeList.setPreviousSecondMonth(payeeList
								.getPreviousSecondMonth() + (Double) object[6]);

						payeeList.setPreviousThirdMonth(payeeList
								.getPreviousThirdMonth() + (Double) object[7]);

						payeeList.setPreviousFourthMonth(payeeList
								.getPreviousFourthMonth() + (Double) object[8]);

						payeeList.setPreviousFifthMonth(payeeList
								.getPreviousFifthMonth() + (Double) object[9]);

						payeeList.setYearToDate(payeeList.getYearToDate()
								+ (Double) object[10]);

					} else {

						payeeList = new PayeeList();

						payeeList.setActive((object[0] == null ? false
								: ((Boolean) object[0]).booleanValue()));
						payeeName = (String) object[1];
						payeeList.setPayeeName(payeeName);
						payeeList.setType((Integer) object[2]);
						payeeList.setID((Long) object[3]);
						payeeList.setCurrentMonth((Double) object[4]);
						payeeList.setPreviousMonth((Double) object[5]);
						payeeList.setPreviousSecondMonth((Double) object[6]);
						payeeList.setPreviousThirdMonth((Double) object[7]);
						payeeList.setPreviousFourthMonth((Double) object[8]);
						payeeList.setPreviousFifthMonth((Double) object[9]);
						payeeList.setYearToDate((Double) object[10]);
						payeeList.setBalance((Double) object[11]);

						queryResult.add(payeeList);
					}
				}
				return new VList<PayeeList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	private List<TrialBalance> getBalanceSheetSorted(List<TrialBalance> list) {

		VList<TrialBalance> sortedList = new VList<TrialBalance>();

		int sort[] = { 1, 4, 2, 5, 3, 6 };

		for (int i = 0; i < sort.length; i++) {
			Iterator<TrialBalance> iterator = list.iterator();
			while (iterator.hasNext()) {
				TrialBalance trialBalance = iterator.next();
				if (sort[i] == trialBalance.getSubBaseType()
						&& !DecimalUtil.isEquals(trialBalance.getAmount(), 0.0)) {
					sortedList.add(trialBalance);
					iterator.remove();
				}
			}
		}

		return sortedList;
	}

	@Override
	public VList<ExpenseList> getExpenseReportByType(int type,
			FinanceDate startDate, FinanceDate endDate) throws DAOException {
		List list = null;
		Session session = HibernateUtil.getCurrentSession();

		Query query = session.getNamedQuery("getExpenseReportByType")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("type", type);
		list = query.list();

		Object[] object = null;
		Iterator iterator = list.iterator();
		List<ExpenseList> queryResult = new VList<ExpenseList>();
		while ((iterator).hasNext()) {

			ExpenseList expense = new ExpenseList();
			object = (Object[]) iterator.next();

			expense.setTransactionId(object[0] != null ? (Long) object[0]
					: null);
			expense.setTransactionType(object[1] != null ? (Integer) object[1]
					: null);
			expense.setTransactionDate(object[2] != null ? new ClientFinanceDate(
					((Long) object[2])) : null);
			expense.setTransactionNumber(object[3] != null ? (String) object[3]
					: null);
			expense.setMemo(object[4] != null ? (String) object[4] : null);
			expense.setName(object[5] != null ? (String) object[5] : null);
			expense.setTotal(object[6] != null ? (Double) object[6] : null);
			queryResult.add(expense);

		}

		return new VList<ExpenseList>(queryResult);
	}

	@Override
	public String getNextCustomerNumber() {
		return NumberUtils.getNextAutoCustomerNumber();
		// return NumberUtils.getNextCustomerNumber();
	}

	@Override
	public String getNextVendorNumber() throws DAOException {
		return NumberUtils.getNextAutoVendorNumber();
		// return NumberUtils.getNextVendorNumber();
	}

	@Override
	public VList<CheckDetailReport> getCheckDetailReport(long paymentmethod,
			FinanceDate startDate, FinanceDate endDate) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getCheckDetailReport")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("paymentmethod", paymentmethod);
		List list = query.list();
		Object[] object = null;
		Iterator iterator = list.iterator();
		List<CheckDetailReport> queryResult = new VList<CheckDetailReport>();
		while ((iterator).hasNext()) {

			CheckDetailReport checkDetail = new CheckDetailReport();
			object = (Object[]) iterator.next();

			checkDetail.setTransactionId((Long) object[0]);
			checkDetail.setTransactionType(((Integer) object[1]));
			checkDetail.setNumber((String) object[2]);
			checkDetail.setTransactionDate(new ClientFinanceDate(
					((Long) object[3])));
			checkDetail.setPaymentMethod((String) object[4]);
			checkDetail.setPayeeName((String) object[5]);
			checkDetail.setAccountName((String) object[6]);
			checkDetail.setAmount((Double) object[7]);
			checkDetail.setMemo((String) object[8]);

			queryResult.add(checkDetail);

		}

		return new VList<CheckDetailReport>(queryResult);
	}

	@Override
	public VList<DepositDetail> getDepositDetail(FinanceDate startDate,
			FinanceDate endDate) {

		Session session = HibernateUtil.getCurrentSession();
		List list = session.getNamedQuery("getDepositDetail")
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate()).list();
		Map<Long, List<DepositDetail>> map = new LinkedHashMap<Long, List<DepositDetail>>();
		List<DepositDetail> depositDetails = new VList<DepositDetail>();
		Iterator it = list.iterator();
		Long tempTransactionID = null;
		double tempAmount = 0;
		while (it.hasNext()) {
			Object[] object = (Object[]) it.next();
			DepositDetail d = new DepositDetail();
			d.setTransactionId((Long) object[0]);
			d.setTransactionType((Integer) object[1]);
			d.setTransactionNumber((String) object[2]);
			d.setTransactionDate(new ClientFinanceDate((Long) object[3]));
			d.setName((String) object[4]);
			d.setAccountName((String) object[5]);
			if (tempTransactionID == null
					|| (d.getTransactionId() != tempTransactionID)) {
				d.setTotal((Double) object[6]);
				tempAmount = d.getTotal();
			} else {
				// double amount = DecimalUtil.isGreaterThan(tempAmount, 0) ? 1
				// * ((Double) object[6]) : ((Double) object[6]);
				d.setTotal((Double) object[6]);
			}
			if (map.containsKey(d.getTransactionId())) {
				map.get(d.getTransactionId()).add(d);
			} else {
				List<DepositDetail> tempList = new VList<DepositDetail>();
				tempList.add(d);
				map.put(d.getTransactionId(), tempList);
			}
			// depositDetails.add(d);
			tempTransactionID = d.getTransactionId();
		}

		Long[] ids = new Long[map.keySet().size()];
		map.keySet().toArray(ids);
		for (Long s : ids)
			depositDetails.addAll(map.get(s));

		return new VList<DepositDetail>(depositDetails);
	}

	public void deleteTaxCodeOfTaxItemGroupIfUSversion(Session session,
			AccounterCoreType clazz, long id) {

		if (this.getCompany().getAccountingType() == Company.ACCOUNTING_TYPE_US
				&& (clazz.getServerClassSimpleName().equals("TAXItem") || clazz
						.getServerClassSimpleName().equals("TAXGroup"))) {

			Query query = session.getNamedQuery("getTransactionItem.by.id")
					.setParameter("id", id);
			List list = query.list();

			if (list.size() == 0)
				session.getNamedQuery("deleteTAXCode").setParameter(0, id)
						.executeUpdate();
		}
		return;
	}

	@Override
	public VList<PayeeStatementsList> getPayeeStatementsList(long id,
			long transactionDate, FinanceDate fromDate, FinanceDate toDate,
			int noOfDays, boolean isEnabledOfZeroBalBox,
			boolean isEnabledOfLessthanZeroBalBox,
			double lessThanZeroBalanceValue,
			boolean isEnabledOfNoAccountActivity,
			boolean isEnabledOfInactiveCustomer) throws DAOException {

		try {
			Session session = HibernateUtil.getCurrentSession();
			Query query = session
					.getNamedQuery("getCreatableStatementForCustomer")
					.setParameter("startDate", fromDate.getDate())
					.setParameter("endDate", toDate.getDate())
					.setParameter("customerId", id)
					.setParameter("dueDays", noOfDays)
					.setParameter("dontShowZero", isEnabledOfZeroBalBox)
					.setParameter("lessBalance", lessThanZeroBalanceValue)
					.setParameter("isActivePayee", isEnabledOfInactiveCustomer);

			List list = query.list();
			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<PayeeStatementsList> queryResult = new VList<PayeeStatementsList>();
				while ((iterator).hasNext()) {

					PayeeStatementsList statementsList = new PayeeStatementsList();
					object = (Object[]) iterator.next();

					statementsList.setTransactiontype(object[0] == null ? null
							: (Integer) object[0]);
					statementsList.setTransactionNumber((String) object[1]);
					statementsList
							.setTransactionDate(((Long) object[2]) == null ? null
									: new ClientFinanceDate((Long) object[2]));
					statementsList.setDueDate(((Long) object[3]) == null ? null
							: new ClientFinanceDate((Long) object[3]));
					statementsList.setTotal((Double) object[4]);
					statementsList.setBalance((Double) object[5]);
					statementsList
							.setPayeeName(((String) object[6]) == null ? null
									: (String) object[6]);

					ClientAddress clientAddress = new ClientAddress();
					clientAddress
							.setAddress1(((String) object[7]) == null ? null
									: ((String) object[7]));
					clientAddress.setStreet(((String) object[8]) == null ? null
							: ((String) object[8]));
					clientAddress.setCity(((String) object[9]) == null ? null
							: ((String) object[9]));
					clientAddress
							.setStateOrProvinence(((String) object[10]) == null ? null
									: ((String) object[10]));
					clientAddress
							.setCountryOrRegion(((String) object[11]) == null ? null
									: ((String) object[11]));
					clientAddress
							.setZipOrPostalCode(((String) object[12]) == null ? null
									: ((String) object[12]));
					statementsList.setBillingAddress(clientAddress);

					statementsList.setSalesPerson(object[13] == null ? null
							: (String) object[13]);
					statementsList.setShippingMethod(object[14] == null ? null
							: (String) object[14]);
					statementsList.setPaymentTerm(object[15] == null ? null
							: (String) object[15]);
					statementsList.setTransactionId(object[16] == null ? null
							: ((Long) object[16]).longValue());

					long ageing = getAgeing(
							statementsList.getTransactionDate(),
							statementsList.getDueDate(), toDate);
					statementsList.setAgeing(ageing);
					statementsList.setCategory(getCategory(ageing));

					queryResult.add(statementsList);
				}
				return new VList<PayeeStatementsList>(queryResult);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public VList<Double> getGraphPointsforAccount(int chartType, long accountNo)
			throws DAOException {

		try {

			Session session = HibernateUtil.getCurrentSession();
			Query query = null;

			FinanceDate currentDate = new FinanceDate();

			if (chartType == GraphChart.BANK_ACCOUNT_CHART_TYPE) {

				Calendar dateCal[] = new Calendar[4];
				if (dateCal[3] == null)
					dateCal[3] = new GregorianCalendar();

				dateCal[3].setTime(currentDate.getAsDateObject());

				for (int i = 2; i >= 0; i--) {
					if (dateCal[i] == null)
						dateCal[i] = new GregorianCalendar();

					dateCal[i].setTime(dateCal[i + 1].getTime());
					dateCal[i].set(Calendar.DATE,
							dateCal[i].get(Calendar.DATE) - 1);

					if (dateCal[i].get(Calendar.DATE) <= 0) {
						dateCal[i].set(Calendar.MONTH,
								dateCal[i].get(Calendar.MONTH) - 1);
						dateCal[i].set(Calendar.DATE,
								dateCal[i].getActualMaximum(Calendar.DATE)
										- dateCal[i].get(Calendar.DATE));
					}

				}

				query = session
						.getNamedQuery("getPointsForBankAccount")
						.setParameter("accountNo", accountNo)
						.setParameter("previousThreeDaysBackDateCal",
								new FinanceDate(dateCal[0].getTime()).getDate())
						.setParameter("previousTwoDaysBackDateCal",
								new FinanceDate(dateCal[1].getTime()).getDate())
						.setParameter("previousOneDayBackDateCal",
								new FinanceDate(dateCal[2].getTime()).getDate())
						.setParameter("currentDateCal",
								new FinanceDate(dateCal[3].getTime()).getDate());

			}

			if (chartType == GraphChart.ACCOUNTS_RECEIVABLE_CHART_TYPE) {

				Calendar currentMonthStartDateCal = new GregorianCalendar();
				currentMonthStartDateCal.setTime(currentDate.getAsDateObject());
				currentMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar currentMonthEndDateCal = new GregorianCalendar();
				currentMonthEndDateCal.setTime(currentDate.getAsDateObject());
				currentMonthEndDateCal.set(Calendar.DATE,
						currentMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				Calendar previousFirstMonthStartDateCal = new GregorianCalendar();
				previousFirstMonthStartDateCal.setTime(currentDate
						.getAsDateObject());
				previousFirstMonthStartDateCal.set(Calendar.MONTH,
						previousFirstMonthStartDateCal.get(Calendar.MONTH) - 1);
				previousFirstMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar previousFirstMonthEndDateCal = new GregorianCalendar();
				previousFirstMonthEndDateCal.setTime(currentDate
						.getAsDateObject());
				previousFirstMonthEndDateCal.set(Calendar.MONTH,
						previousFirstMonthEndDateCal.get(Calendar.MONTH) - 1);
				previousFirstMonthEndDateCal.set(Calendar.DATE,
						previousFirstMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				Calendar previousSecondMonthStartDateCal = new GregorianCalendar();
				previousSecondMonthStartDateCal.setTime(currentDate
						.getAsDateObject());
				previousSecondMonthStartDateCal
						.set(Calendar.MONTH, previousSecondMonthStartDateCal
								.get(Calendar.MONTH) - 2);
				previousSecondMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar previousSecondMonthEndDateCal = new GregorianCalendar();
				previousSecondMonthEndDateCal.setTime(currentDate
						.getAsDateObject());
				previousSecondMonthEndDateCal.set(Calendar.MONTH,
						previousSecondMonthEndDateCal.get(Calendar.MONTH) - 2);
				previousSecondMonthEndDateCal.set(Calendar.DATE,
						previousSecondMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				Calendar previousThirdMonthStartDateCal = new GregorianCalendar();
				previousThirdMonthStartDateCal.setTime(currentDate
						.getAsDateObject());
				previousThirdMonthStartDateCal.set(Calendar.MONTH,
						previousThirdMonthStartDateCal.get(Calendar.MONTH) - 3);
				previousThirdMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar previousThirdMonthEndDateCal = new GregorianCalendar();
				previousThirdMonthEndDateCal.setTime(currentDate
						.getAsDateObject());
				previousThirdMonthEndDateCal.set(Calendar.MONTH,
						previousThirdMonthEndDateCal.get(Calendar.MONTH) - 3);
				previousThirdMonthEndDateCal.set(Calendar.DATE,
						previousThirdMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				Calendar previousFourthMonthStartDateCal = new GregorianCalendar();
				previousFourthMonthStartDateCal.setTime(currentDate
						.getAsDateObject());
				previousFourthMonthStartDateCal
						.set(Calendar.MONTH, previousFourthMonthStartDateCal
								.get(Calendar.MONTH) - 4);
				previousFourthMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar previousFourthMonthEndDateCal = new GregorianCalendar();
				previousFourthMonthEndDateCal.setTime(currentDate
						.getAsDateObject());
				previousFourthMonthEndDateCal.set(Calendar.MONTH,
						previousFourthMonthEndDateCal.get(Calendar.MONTH) - 4);
				previousFourthMonthEndDateCal.set(Calendar.DATE,
						previousFourthMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				Calendar previousFifthMonthStartDateCal = new GregorianCalendar();
				previousFifthMonthStartDateCal.setTime(currentDate
						.getAsDateObject());
				previousFifthMonthStartDateCal.set(Calendar.MONTH,
						previousFifthMonthStartDateCal.get(Calendar.MONTH) - 5);
				previousFifthMonthStartDateCal.set(Calendar.DATE, 1);

				Calendar previousFifthMonthEndDateCal = new GregorianCalendar();
				previousFifthMonthEndDateCal.setTime(currentDate
						.getAsDateObject());
				previousFifthMonthEndDateCal.set(Calendar.MONTH,
						previousFifthMonthEndDateCal.get(Calendar.MONTH) - 5);
				previousFifthMonthEndDateCal.set(Calendar.DATE,
						previousFifthMonthEndDateCal
								.getActualMaximum(Calendar.DAY_OF_MONTH));

				query = session
						.getNamedQuery("getGraphPointsForDebtors")
						.setParameter(
								"debtorAccountID",
								getCompany().getAccountsReceivableAccount()
										.getID())
						.setParameter(
								"previousFifthMonthStartDateCal",
								new FinanceDate(previousFifthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFifthMonthEndDateCal",
								new FinanceDate(previousFifthMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthStartDateCal",
								new FinanceDate(previousFourthMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFourthMonthEndDateCal",
								new FinanceDate(previousFourthMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthStartDateCal",
								new FinanceDate(previousThirdMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousThirdMonthEndDateCal",
								new FinanceDate(previousThirdMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthStartDateCal",
								new FinanceDate(previousSecondMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousSecondMonthEndDateCal",
								new FinanceDate(previousSecondMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthStartDateCal",
								new FinanceDate(previousFirstMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"previousFirstMonthEndDateCal",
								new FinanceDate(previousFirstMonthEndDateCal
										.getTime()).getDate())
						.setParameter(
								"currentMonthStartDateCal",
								new FinanceDate(currentMonthStartDateCal
										.getTime()).getDate())
						.setParameter(
								"currentMonthEndDateCal",
								new FinanceDate(currentMonthEndDateCal
										.getTime()).getDate());

			}

			if (chartType == GraphChart.ACCOUNTS_PAYABLE_CHART_TYPE) {

				Calendar[] dateCal = new GregorianCalendar[30];
				if (dateCal[0] == null)
					dateCal[0] = new GregorianCalendar();
				dateCal[0].setTime(currentDate.getAsDateObject());

				for (int i = 1; i < 30; i++) {
					if (dateCal[i] == null)
						dateCal[i] = new GregorianCalendar();

					dateCal[i].setTime(dateCal[i - 1].getTime());
					dateCal[i].set(Calendar.DATE,
							dateCal[i].get(Calendar.DATE) + 1);

					if (dateCal[i].get(Calendar.DATE) > dateCal[i]
							.getActualMaximum(Calendar.DATE)) {

						dateCal[i]
								.set(Calendar.DATE,
										dateCal[i].get(Calendar.DATE)
												- dateCal[i]
														.getActualMaximum(Calendar.DATE));
						dateCal[i].set(Calendar.MONTH,
								dateCal[i].get(Calendar.MONTH) + 1);
					}
				}

				query = session
						.getNamedQuery("getGraphPointsForCreditors")
						.setParameter(
								"creditorsAccountID",
								getCompany().getAccountsPayableAccount()
										.getID())
						.setParameter("currentDate",
								new FinanceDate(dateCal[0].getTime()).getDate())
						.setParameter("oneDayAfterToCurrentDate",
								new FinanceDate(dateCal[1].getTime()).getDate())
						.setParameter("twoDaysAfterToCurrentDate",
								new FinanceDate(dateCal[2].getTime()).getDate())
						.setParameter("threeDaysAfterToCurrentDate",
								new FinanceDate(dateCal[3].getTime()).getDate())
						.setParameter("fourDaysAfterToCurrentDate",
								new FinanceDate(dateCal[4].getTime()).getDate())
						.setParameter("fiveDaysAfterToCurrentDate",
								new FinanceDate(dateCal[5].getTime()).getDate())
						.setParameter("sixDaysAfterToCurrentDate",
								new FinanceDate(dateCal[6].getTime()).getDate())
						.setParameter("sevenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[7].getTime()).getDate())
						.setParameter("eightDaysAfterToCurrentDate",
								new FinanceDate(dateCal[8].getTime()).getDate())
						.setParameter("nineDaysAfterToCurrentDate",
								new FinanceDate(dateCal[9].getTime()).getDate())
						.setParameter(
								"tenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[10].getTime())
										.getDate())
						.setParameter(
								"elevenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[11].getTime())
										.getDate())
						.setParameter(
								"twelveDaysAfterToCurrentDate",
								new FinanceDate(dateCal[12].getTime())
										.getDate())
						.setParameter(
								"thirteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[13].getTime())
										.getDate())
						.setParameter(
								"fourteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[14].getTime())
										.getDate())
						.setParameter(
								"fifteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[15].getTime())
										.getDate())
						.setParameter(
								"sixteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[16].getTime())
										.getDate())
						.setParameter(
								"seventeenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[17].getTime())
										.getDate())
						.setParameter(
								"eighteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[18].getTime())
										.getDate())
						.setParameter(
								"nineteenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[19].getTime())
										.getDate())
						.setParameter(
								"twentyDaysAfterToCurrentDate",
								new FinanceDate(dateCal[20].getTime())
										.getDate())
						.setParameter(
								"twentyOneDaysAfterToCurrentDate",
								new FinanceDate(dateCal[21].getTime())
										.getDate())
						.setParameter(
								"twentyTwoDaysAfterToCurrentDate",
								new FinanceDate(dateCal[22].getTime())
										.getDate())
						.setParameter(
								"twentyThreeDaysAfterToCurrentDate",
								new FinanceDate(dateCal[23].getTime())
										.getDate())
						.setParameter(
								"twentyFourDaysAfterToCurrentDate",
								new FinanceDate(dateCal[24].getTime())
										.getDate())
						.setParameter(
								"twentyFiveDaysAfterToCurrentDate",
								new FinanceDate(dateCal[25].getTime())
										.getDate())
						.setParameter(
								"twentySixDaysAfterToCurrentDate",
								new FinanceDate(dateCal[26].getTime())
										.getDate())
						.setParameter(
								"twentySevenDaysAfterToCurrentDate",
								new FinanceDate(dateCal[27].getTime())
										.getDate())
						.setParameter(
								"twentyEightDaysAfterToCurrentDate",
								new FinanceDate(dateCal[28].getTime())
										.getDate())
						.setParameter(
								"twentyNineDaysAfterToCurrentDate",
								new FinanceDate(dateCal[29].getTime())
										.getDate());
				// .setParameter("thirtyDaysAfterToCurrentDate", new
				// FinanceDate(dateCal[30].getTime()).getTime());
				// .setParameter("thirtyOneDaysAfterToCurrentDate", new
				// FinanceDate(dateCal[31].getTime()).getTime());

			}

			if (chartType == GraphChart.EXPENSE_CHART_TYPE)
				query = session.getNamedQuery("getExpenseTotalAmounts");

			List list = query.list();
			if (list != null) {
				Object[] object = null;
				Iterator iterator = list.iterator();
				List<Double> gPoints = new VList<Double>();

				while (iterator.hasNext()) {
					object = (Object[]) iterator.next();

					gPoints.add(object[2] == null ? 0 : (Double) object[2]);
					gPoints.add(object[3] == null ? 0 : (Double) object[3]);
					gPoints.add(object[4] == null ? 0 : (Double) object[4]);
					gPoints.add(object[5] == null ? 0 : (Double) object[5]);

					if (chartType == GraphChart.ACCOUNTS_RECEIVABLE_CHART_TYPE
							|| chartType == GraphChart.ACCOUNTS_PAYABLE_CHART_TYPE) {

						gPoints.add(object[6] == null ? 0 : (Double) object[6]);
						gPoints.add(object[7] == null ? 0 : (Double) object[7]);
						if (chartType == GraphChart.ACCOUNTS_RECEIVABLE_CHART_TYPE) {
							Object res = session
									.getNamedQuery("getDraftInvoicesTotal")
									.setParameter("isInvoices", true)
									.uniqueResult();
							double amount = res == null ? 0 : (Double) res;
							gPoints.add(amount);

							res = session
									.getNamedQuery("getOverDueInvoicesTotal")
									.setParameter("isInvoices", true)
									.setParameter("presentDate",
											(new FinanceDate()).getDate())
									.uniqueResult();
							amount = res == null ? 0 : (Double) res;
							gPoints.add(amount);
						}
					}

					if (chartType == GraphChart.ACCOUNTS_PAYABLE_CHART_TYPE) {

						gPoints.add(object[8] == null ? 0 : (Double) object[8]);
						gPoints.add(object[9] == null ? 0 : (Double) object[9]);
						gPoints.add(object[10] == null ? 0
								: (Double) object[10]);
						gPoints.add(object[11] == null ? 0
								: (Double) object[11]);
						gPoints.add(object[12] == null ? 0
								: (Double) object[12]);
						gPoints.add(object[13] == null ? 0
								: (Double) object[13]);
						gPoints.add(object[14] == null ? 0
								: (Double) object[14]);
						gPoints.add(object[15] == null ? 0
								: (Double) object[15]);
						gPoints.add(object[16] == null ? 0
								: (Double) object[16]);
						gPoints.add(object[17] == null ? 0
								: (Double) object[17]);
						gPoints.add(object[18] == null ? 0
								: (Double) object[18]);
						gPoints.add(object[19] == null ? 0
								: (Double) object[19]);
						gPoints.add(object[20] == null ? 0
								: (Double) object[20]);
						gPoints.add(object[21] == null ? 0
								: (Double) object[21]);
						gPoints.add(object[22] == null ? 0
								: (Double) object[22]);
						gPoints.add(object[23] == null ? 0
								: (Double) object[23]);
						gPoints.add(object[24] == null ? 0
								: (Double) object[24]);
						gPoints.add(object[25] == null ? 0
								: (Double) object[25]);
						gPoints.add(object[26] == null ? 0
								: (Double) object[26]);
						gPoints.add(object[27] == null ? 0
								: (Double) object[27]);
						gPoints.add(object[28] == null ? 0
								: (Double) object[28]);
						gPoints.add(object[29] == null ? 0
								: (Double) object[29]);
						gPoints.add(object[30] == null ? 0
								: (Double) object[30]);
						gPoints.add(object[31] == null ? 0
								: (Double) object[31]);

						Object res = session
								.getNamedQuery("getDraftInvoicesTotal")
								.setParameter("isInvoices", false)
								.uniqueResult();
						double amount = res == null ? 0 : (Double) res;
						gPoints.add(amount);

						res = session
								.getNamedQuery("getOverDueInvoicesTotal")
								.setParameter("isInvoices", false)
								.setParameter("presentDate",
										(new FinanceDate()).getDate())
								.uniqueResult();
						amount = res == null ? 0 : (Double) res;
						gPoints.add(amount);

						// gPoints.add(object[32] == null ? null
						// : (Double) object[32]);

					}

					// double maxValue, minValue;
					// minValue = (object[2] == null) ? 0 : (Double) object[2];
					// maxValue = minValue;
					//
					// for (int i = 2; i < object.length; i++) {
					//
					// if (object[i] != null) {
					// if (maxValue < (Double) object[i])
					// maxValue = (Double) object[i];
					//
					// if (minValue > (Double) object[i])
					// minValue = (Double) object[i];
					// }
					// }
					// gPoints.setMaxPoint(maxValue);
					// gPoints.setMinPoint(minValue);
					// if (accountNo != 0)
					// gPoints.add(Double.valueOf(accountNo));

				}
				return new VList<Double>(gPoints);
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DAOException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	public void createAdminUser(ClientUser user) {
		Session session = HibernateUtil.getCurrentSession();
		org.hibernate.Transaction transaction = session.beginTransaction();
		User admin = new User(user);
		session.save(admin);
		this.getCompany().getUsersList().add(admin);
		session.saveOrUpdate(this);
		transaction.commit();
	}

	@Override
	public VList<BillsList> getEmployeeExpensesByStatus(String employeeName,
			int status) throws DAOException {

		List<BillsList> billsList = new VList<BillsList>();
		Session session = HibernateUtil.getCurrentSession();
		Query query = null;
		if (employeeName != null)
			query = session
					.getNamedQuery(
							"getCashPurchase.by.employeeNmae.expenseStatusandtype")
					.setParameter("employeeName", employeeName)
					.setParameter("expenseStatus", status)
					.setParameter("type", Transaction.TYPE_EMPLOYEE_EXPENSE);
		else
			query = session
					.getNamedQuery("getCashPurchase.by.expenseStatusandtype")
					.setParameter("expenseStatus", status)
					.setParameter("type", Transaction.TYPE_EMPLOYEE_EXPENSE);

		List<CashPurchase> cashpurchase = query.list();
		for (CashPurchase cp : cashpurchase) {
			BillsList bills = new BillsList();
			bills.setTransactionId(cp.getID());
			bills.setOriginalAmount(cp.getTotal());
			bills.setVendorName(cp.getEmployee() != null ? cp.getEmployee()
					.getName() : "");
			bills.setDate(new ClientFinanceDate(cp.getDate().getDate()));
			bills.setExpenseStatus(status);
			bills.setType(Transaction.TYPE_EMPLOYEE_EXPENSE);
			bills.setPayFrom(cp.getPayFrom() != null ? cp.getPayFrom().getID()
					: 0);

			/*
			 * Here, to set transaction created date temporarly using setDueDate
			 * method
			 */
			bills.setDueDate(new ClientFinanceDate(cp.getCreatedDate()
					.getTime()));
			billsList.add(bills);
		}
		return new VList<BillsList>(billsList);
	}

	@Override
	public boolean changeMyPassword(String emailId, String oldPassword,
			String newPassword) throws DAOException {

		Session session = HibernateUtil.openSession(BaseServlet.LOCAL_DATABASE);
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			oldPassword = HexUtil.bytesToHex(Security.makeHash(emailId
					+ oldPassword));
			newPassword = HexUtil.bytesToHex(Security.makeHash(emailId
					+ newPassword));

			Query query = session
					.createSQLQuery(
							"SELECT EMAIL_ID  FROM CLIENT C WHERE C.EMAIL_ID=:emailId AND C.PASSWORD=:password")
					.setParameter("emailId", emailId)
					.setParameter("password", oldPassword);
			String emailID = (String) query.uniqueResult();

			if (emailID == null)
				return false;

			query = session.createSQLQuery("UPDATE CLIENT SET PASSWORD='"
					+ newPassword + "' WHERE EMAIL_ID='" + emailId + "'");
			query.executeUpdate();
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return true;
	}

	public VList<ClientUserInfo> getAllUsers() throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		List<User> financeUsers = session.getNamedQuery("list.User").list();
		List<ClientUserInfo> clientUsers = new VList<ClientUserInfo>();
		for (User user : financeUsers) {
			if (!user.isDeleted()) {
				ClientUser clientUser = new ClientConvertUtil().toClientObject(
						user, ClientUser.class);
				ClientUserInfo userInfo = clientUser.toUserInfo();
				clientUsers.add(userInfo);
			}
		}
		return new VList<ClientUserInfo>(clientUsers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vimukti.accounter.services.IFinanceDAOService#getSalesOrders(boolean)
	 */
	@Override
	public VList<SalesOrdersList> getSalesOrders(boolean orderByDate) {
		// its not using any where
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vimukti.accounter.services.IFinanceDAOService#getPurchaseOrders(boolean
	 * )
	 */
	@Override
	public VList<PurchaseOrdersList> getPurchaseOrders(boolean orderByDate) {
		// its not using any where
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vimukti.accounter.services.IFinanceDAOService#getSalesOrdersForCustomer
	 * (long)
	 */
	@Override
	public VList<SalesOrdersList> getSalesOrdersForCustomer(long customerID) {
		// its not using any where
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vimukti.accounter.services.IFinanceDAOService#getPurchaseOrdersForVendor
	 * (long)
	 */
	@Override
	public VList<SalesOrdersList> getPurchaseOrdersForVendor(long vendorID) {
		// its not using any where
		return null;
	}

	/**
	 * @return
	 * @throws AccounterException
	 */
	public VList<ClientUserInfo> getAllEmployees() throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		List<User> financeUsers = session.getNamedQuery("list.User").list();
		VList<ClientUserInfo> employees = new VList<ClientUserInfo>();
		for (User user : financeUsers) {
			if (!user.isDeleted()) {
				ClientUser clientUser = new ClientConvertUtil().toClientObject(
						user, ClientUser.class);
				ClientUserInfo userInfo = clientUser.toUserInfo();
				employees.add(userInfo);
			}
		}
		return employees;
	}

	@Override
	public VList<PayeeStatementsList> getCustomerStatement(long customer,
			long fromDate, long toDate) {
		Session session = HibernateUtil.getCurrentSession();

		List<PayeeStatementsList> result = new VList<PayeeStatementsList>();

		Query query = session.getNamedQuery("getCustomerPreviousBalance");
		query.setParameter("customerId", customer);
		query.setParameter("fromDate", fromDate);
		Object uniqueResult = query.uniqueResult();
		PayeeStatementsList ob = new PayeeStatementsList();
		ob.setTransactionDate(new ClientFinanceDate(fromDate));
		ob.setTotal((Double) uniqueResult);
		result.add(ob);

		Query query1 = session.getNamedQuery("getCustomerStatement");
		query1.setParameter("customerId", customer);
		query1.setParameter("fromDate", fromDate);
		query1.setParameter("toDate", toDate);

		List l = query1.list();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Object[] object = (Object[]) it.next();
			PayeeStatementsList record = new PayeeStatementsList();
			record.setTransactionId((Long) object[0]);
			record.setTransactionDate(new ClientFinanceDate((Long) object[1]));
			record.setTransactiontype((Integer) object[2]);
			record.setTransactionNumber((String) object[3]);
			record.setTotal((Double) object[4]);

			result.add(record);
		}
		return new VList<PayeeStatementsList>(result);
	}

	public ClientCompanyPreferences getClientCompanyPreferences()
			throws AccounterException {
		ClientCompanyPreferences clientCompanyPreferences = new ClientConvertUtil()
				.toClientObject(getCompany().getPreferences(),
						ClientCompanyPreferences.class);
		return clientCompanyPreferences;
	}

	public VList<ClientRecurringTransaction> getAllRecurringTransactions()
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		List<RecurringTransaction> transactions = session.getNamedQuery(
				"list.RecurringTransaction").list();

		List<ClientRecurringTransaction> clientObjs = new ArrayList<ClientRecurringTransaction>();
		for (RecurringTransaction recurringTransaction : transactions) {
			ClientRecurringTransaction clientObject = new ClientConvertUtil()
					.toClientObject(recurringTransaction,
							ClientRecurringTransaction.class);
			clientObject.setRefTransactionTotal(recurringTransaction
					.getReferringTransaction().getTotal());
			clientObject.setRefTransactionType(recurringTransaction
					.getReferringTransaction().getType());

			// TODO doubt
			clientObject.setReferringTransaction(recurringTransaction
					.getReferringTransaction().getID());

			clientObjs.add(clientObject);
		}

		return new VList<ClientRecurringTransaction>(clientObjs);
	}

	@Override
	public void mergeCustomer(ClientCustomer fromClientCustomer,
			ClientCustomer toClientCustomer) throws DAOException {

		Session session = HibernateUtil.getCurrentSession();
		double mergeBalance = fromClientCustomer.getBalance()
				+ toClientCustomer.getBalance();
		
		SQLQuery sqlQuery = session.createSQLQuery("");
		sqlQuery.executeUpdate();

		
		// Updating
//				session.getNamedQuery("update.merge.Payee.mergeoldbalance.tonewbalance")
//				.setParameter("id", toClientCustomer.getID())
//				.setParameter("balance", mergeBalance).executeUpdate();

//		session.getNamedQuery(
//				"update.CashSale.merge.newCustomerId.to.oldCustomerId")
//				.setParameter("toID", toClientCustomer.getID())
//				.setParameter("fromID", fromClientCustomer.getID())
//				.executeUpdate();
//		session.getNamedQuery(
//				"update.customer.merge.newCustomerId.to.oldCustomerId")
//				.setParameter("toID", toClientCustomer.getID())
//				.setParameter("fromID", fromClientCustomer.getID())
//				.executeUpdate();
//		
//		session.getNamedQuery(
//				"update.CustomerCreditMemo.merge.newCustomerId.to.oldCustomerId")
//				.setParameter("toID", toClientCustomer.getID())
//				.setParameter("fromID", fromClientCustomer.getID())
//				.executeUpdate();
		
		

	}

	@Override
	public void mergeVendor(long fromID, long toID) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void mergeAcoount(long fromID, long toID) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void mergeItem(long fromID, long toID) throws DAOException {
		// TODO Auto-generated method stub

	}

}
