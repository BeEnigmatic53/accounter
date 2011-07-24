package com.vimukti.accounter.services;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionTemplate;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.Bank;
import com.vimukti.accounter.core.CashPurchase;
import com.vimukti.accounter.core.CashSales;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.CreditCardCharge;
import com.vimukti.accounter.core.CreditRating;
import com.vimukti.accounter.core.CreditsAndPayments;
import com.vimukti.accounter.core.Currency;
import com.vimukti.accounter.core.Customer;
import com.vimukti.accounter.core.CustomerCreditMemo;
import com.vimukti.accounter.core.CustomerGroup;
import com.vimukti.accounter.core.CustomerRefund;
import com.vimukti.accounter.core.EnterBill;
import com.vimukti.accounter.core.Estimate;
import com.vimukti.accounter.core.Expense;
import com.vimukti.accounter.core.FiscalYear;
import com.vimukti.accounter.core.FixedAsset;
import com.vimukti.accounter.core.Invoice;
import com.vimukti.accounter.core.IssuePayment;
import com.vimukti.accounter.core.Item;
import com.vimukti.accounter.core.ItemGroup;
import com.vimukti.accounter.core.MakeDeposit;
import com.vimukti.accounter.core.PayBill;
import com.vimukti.accounter.core.PayExpense;
import com.vimukti.accounter.core.PaySalesTax;
import com.vimukti.accounter.core.Payee;
import com.vimukti.accounter.core.PaymentTerms;
import com.vimukti.accounter.core.PriceLevel;
import com.vimukti.accounter.core.PurchaseOrder;
import com.vimukti.accounter.core.ReceivePayment;
import com.vimukti.accounter.core.SalesOrder;
import com.vimukti.accounter.core.SalesPerson;
import com.vimukti.accounter.core.ShippingMethod;
import com.vimukti.accounter.core.ShippingTerms;
import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.core.TAXGroup;
import com.vimukti.accounter.core.TaxRates;
import com.vimukti.accounter.core.TransferFund;
import com.vimukti.accounter.core.UnitOfMeasure;
import com.vimukti.accounter.core.User;
import com.vimukti.accounter.core.Vendor;
import com.vimukti.accounter.core.VendorCreditMemo;
import com.vimukti.accounter.core.VendorGroup;
import com.vimukti.accounter.core.WriteCheck;

/**
 * 
 * @author Devesh Satwani
 * 
 */
public class AccounterDAOService extends HibernateDaoSupport implements
		IAccounterDAOService {

	TransactionTemplate transactionTemplate;

	public void setTransactionTemplate(TransactionTemplate template) {
		this.transactionTemplate = template;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean checkLogin(String email, String password)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(

			"from User u where u.email=? and u.passwordSha1Hash=?",

			new Object[] { email, password });
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Account getAccount(long companyId, String accountName)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Account a where a.name = ? and a.company.id = ? ",
					new Object[] { accountName, companyId });

			if (list.size() > 0) {
				Account account = new Account();
				account = (Account) list.get(0);
				return account;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Deprecated
	@Override
	public Account getAccount(long companyId, long accountId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Account a where a.id = ? and a.company.id = ? ",
					new Object[] { accountId, companyId });

			if (list.size() > 0) {
				Account account = new Account();
				account = (Account) list.get(0);
				return account;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Account> getAccounts(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Account> list = template
					.find(
							" from Account a where a.company.id = ? order by a.type, a.number",
							new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Account> getAccounts(long companyId, int type)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Account> list = template
					.find(
							" from Account a where a.company.id = ? and a.type = ?  order by a.type, a.number",
							new Object[] { companyId, type });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CashPurchase getCashPurchase(long companyId, long cashPurchaseId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CashPurchase cp where cp.id = ? and cp.company.id = ? ",
							new Object[] { cashPurchaseId, companyId });

			if (list.size() > 0) {
				CashPurchase cashPurchase = new CashPurchase();
				cashPurchase = (CashPurchase) list.get(0);
				return cashPurchase;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@Override
	public CashSales getCashSales(final long companyId, final long cashSalesId)
			throws DAOException {
		try {
			@SuppressWarnings("unused")
			int a = 0;
			HibernateTemplate template = getHibernateTemplate();
			CashSales cashSales = (CashSales) template
					.execute(new HibernateCallback() {

						@Override
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							return session
									.createQuery(
											"from CashSales cs where cs.id = :id and cs.company.id = :companyID ")
									.setLong("id", cashSalesId).setLong(
											"companyID", companyId)
									.uniqueResult();
						}
					});

			if (cashSales != null) {
				return cashSales;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Expense getExpense(long companyId, long expenseId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Expense e where e.id = ? and e.company.id = ? ",
					new Object[] { expenseId, companyId });

			if (list.size() > 0) {
				Expense expense = new Expense();
				expense = (Expense) list.get(0);
				return expense;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PayExpense getPayExpense(long companyId, long payExpenseId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PayExpense cs where cs.id = ? and cs.company.id = ? ",
							new Object[] { payExpenseId, companyId });

			if (list.size() > 0) {
				PayExpense expense = new PayExpense();
				expense = (PayExpense) list.get(0);
				return expense;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public PaySalesTax getPaySalesTax(long companyId, long id)
			throws DAOException {

		HibernateTemplate template = getHibernateTemplate();

		List<PaySalesTax> list = template.find(
				"from PaySalesTax cs where cs.id = ? and cs.company.id = ? ",
				new Object[] { id, companyId });

		if (list.size() > 0) {
			PaySalesTax pt = new PaySalesTax();
			pt = (PaySalesTax) list.get(0);
			return pt;
		} else
			throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
					null));

	}

	@SuppressWarnings("unchecked")
	public List<PaySalesTax> getPaySalesTaxes(long companyId)
			throws DAOException {

		HibernateTemplate template = getHibernateTemplate();

		List<PaySalesTax> list = template.find(
				"from PaySalesTax cs where cs.company.id = ? ",
				new Object[] { companyId });

		// if (list.size() > 0) {
		return (List<PaySalesTax>) list;
		// } else
		// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
		// null));

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CashSales> getCashSales(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			// ArrayList cashSalesList=new ArrayList<CashSales>();
			List<CashSales> list = template.find(
					"from CashSales cs where cs.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// CashSales cashSales = new CashSales();
			// cashSales = (CashSales) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerRefund> getCustomerRefunds(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CustomerRefund> list = template.find(
					"from CustomerRefund cs where cs.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List<EnterBill> getEnterBills(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			// ArrayList cashSalesList=new ArrayList<CashSales>();
			List<EnterBill> list = template.find(
					"from EnterBill e where e.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// CashSales cashSales = new CashSales();
			// cashSales = (CashSales) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List<CashPurchase> getCashPurchases(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			// ArrayList cashSalesList=new ArrayList<CashSales>();
			List<CashPurchase> list = template.find(
					"from CashPurchase c where c.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// CashSales cashSales = new CashSales();
			// cashSales = (CashSales) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List<PayBill> getPayBills(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			// ArrayList cashSalesList=new ArrayList<CashSales>();
			List<PayBill> list = template.find(
					"from PayBill p where p.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// CashSales cashSales = new CashSales();
			// cashSales = (CashSales) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List<MakeDeposit> getMakeDeposits(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			// ArrayList cashSalesList=new ArrayList<CashSales>();
			List<MakeDeposit> list = template.find(
					"from MakeDeposit m where m.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// CashSales cashSales = new CashSales();
			// cashSales = (CashSales) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List<WriteCheck> getWriteChecks(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<WriteCheck> list = template.find(
					"from WriteCheck w where w.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TAXCode> getTaxCodes(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<TAXCode> list = template.find(
					" from TAXCode t where t.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditCardCharge> getCreditCardCharges(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CreditCardCharge> list = template.find(
					" from CreditCardCharge c where c.company.id = ? ",
					new Object[] { companyId });

			// if (list != null) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TransferFund> getTransferFunds(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<TransferFund> list = template.find(
					" from TransferFund t where t.company.id = ? ",
					new Object[] { companyId });

			// if (list != null) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> getCompanies(long user) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Company> list = template
					.find(
							"select comp from Company comp, User u where comp in(select i from u.companies i) and  u.id=?  ",
							new Object[] { user });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUserByCompany(long userId, long company) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<User> list = template.find(
					"from User where id = ? and company.id = ?", new Object[] {
							userId, company });
			if (list != null && list.size() > 0 && list.size() < 2) {
				return list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e, e
					.getMessage()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Company getCompany(long user) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Company> list = template
					.find(
							"from Company comp where comp.id = (select u.company.id from User u where u.id = ?)",
							new Object[] { user });

			if (list != null && list.size() > 0) {

				return list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e, e
					.getMessage()));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Company getCompany(String name) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Company> list = template.find("from Company where name = ?)",
					new Object[] { name });

			if (list != null && list.size() > 0) {

				return list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e, e
					.getMessage()));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public CreditRating getCreditRating(long companyId, String creditRatingName)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CreditRating cr where cr.creditRatingName = ? and cr.company.id = ? ",
							new Object[] { creditRatingName, companyId });

			if (list.size() > 0) {
				CreditRating creditRating = new CreditRating();
				creditRating = (CreditRating) list.get(0);
				return creditRating;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public CreditRating getCreditRating(long companyId, long creditRatingId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CreditRating cr where cr.id = ? and cr.company.id = ? ",
							new Object[] { creditRatingId, companyId });

			if (list.size() > 0) {
				CreditRating creditRating = new CreditRating();
				creditRating = (CreditRating) list.get(0);
				return creditRating;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditRating> getCreditRatings(long companyId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CreditRating> list = template.find(
					"from CreditRating cr where cr.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public CreditCardCharge getCreditCardCharge(long companyId,
			long creditCardChargeId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CreditCardCharge cr where cr.id = ? and cr.company.id = ? ",
							new Object[] { creditCardChargeId, companyId });

			if (list.size() > 0) {
				CreditCardCharge creditCardCharge = new CreditCardCharge();
				creditCardCharge = (CreditCardCharge) list.get(0);
				return creditCardCharge;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Currency> getCurrencies(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Currency> list = template.find(
					"from Currency c where c.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Currency getCurrency(long companyId, String currencyName)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from Currency c where c.currencyName = ? and cr.company.id = ? ",
							new Object[] { currencyName, companyId });

			if (list.size() > 0) {
				Currency currency = new Currency();
				currency = (Currency) list.get(0);
				return currency;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Currency getCurrency(long companyId, long currencyId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Currency c where c.id = ? and cr.company.id = ? ",
					new Object[] { currencyId, companyId });

			if (list.size() > 0) {
				Currency currency = new Currency();
				currency = (Currency) list.get(0);
				return currency;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Customer getCustomer(long companyId, String customerName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Customer c where c.name = ? and c.company.id = ? ",
					new Object[] { customerName, companyId });

			if (list.size() > 0) {
				Customer customer = new Customer();
				customer = (Customer) list.get(0);
				return customer;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Customer getCustomer(long companyId, long customerId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Customer c where c.id = ? and c.company.id = ? ",
					new Object[] { customerId, companyId });

			if (list.size() > 0) {
				Customer customer = new Customer();
				customer = (Customer) list.get(0);
				return customer;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerCreditMemo getCustomerCreditMemo(long companyId,
			long customerCreditMemoId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CustomerCreditMemo cm where cm.id = ? and cm.company.id = ? ",
							new Object[] { customerCreditMemoId, companyId });

			if (list.size() > 0) {
				CustomerCreditMemo creditMemo = new CustomerCreditMemo();
				creditMemo = (CustomerCreditMemo) list.get(0);
				return creditMemo;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendorCreditMemo> getVendorCreditMemos(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<VendorCreditMemo> list = template.find(
					"from VendorCreditMemo cm where cm.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			return list;

			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerCreditMemo> getCustomerCreditMemos(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CustomerCreditMemo> list = template.find(
					"from CustomerCreditMemo cm where cm.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerGroup getCustomerGroup(long companyId,
			String customerGroupName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CustomerGroup cg where cg.name = ? and cg.company.id = ? ",
							new Object[] { customerGroupName, companyId });

			if (list.size() > 0) {
				CustomerGroup type = new CustomerGroup();
				type = (CustomerGroup) list.get(0);
				return type;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerGroup getCustomerGroup(long companyId, long customerGroupId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CustomerGroup cg where cg.id = ? and cg.company.id = ? ",
							new Object[] { customerGroupId, companyId });
			if (list.size() > 0) {
				CustomerGroup group = new CustomerGroup();
				group = (CustomerGroup) list.get(0);
				return group;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerGroup> getCustomerGroups(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CustomerGroup> list = template.find(
					" from CustomerGroup cg where cg.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerRefund getCustomerRefunds(long companyId,
			long customerRefundsId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CustomerRefund cr where cr.id = ? and cr.company.id = ? ",
							new Object[] { customerRefundsId, companyId });
			if (list.size() > 0) {
				CustomerRefund customerRefunds = new CustomerRefund();
				customerRefunds = (CustomerRefund) list.get(0);
				return customerRefunds;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomers(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Customer> list = template.find(
					" from Customer c where c.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public EnterBill getEnterBill(long companyId, long enterBillId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from EnterBill b where b.id = ? and b.company.id = ? ",
					new Object[] { enterBillId, companyId });

			if (list.size() > 0) {
				EnterBill enterBill = new EnterBill();
				enterBill = (EnterBill) list.get(0);
				return enterBill;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Estimate getEstimate(long companyId, long estimateId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Estimate e where e.id = ? and e.company.id = ? ",
					new Object[] { estimateId, companyId });

			if (list.size() > 0) {
				Estimate estimate = new Estimate();
				estimate = (Estimate) list.get(0);
				return estimate;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TAXGroup> getTaxGroups(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<TAXGroup> list = template.find(
					" from TAXGroup t where t.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Invoice getInvoice(long companyId, long invoiceId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Invoice i where i.id = ? and i.company.id = ? ",
					new Object[] { invoiceId, companyId });

			if (list.size() > 0) {
				Invoice invoice = new Invoice();
				invoice = (Invoice) list.get(0);
				return invoice;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> getInvoices(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Invoice> list = template.find(
					"from Invoice i where i.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public IssuePayment getIssuePayment(long companyId, long issuePaymentId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from IssuePayment i where i.id = ? and i.company.id = ? ",
					new Object[] { issuePaymentId, companyId });

			if (list.size() > 0) {
				IssuePayment issuePayment = new IssuePayment();
				issuePayment = (IssuePayment) list.get(0);
				return issuePayment;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Item getItem(long companyId, String itemName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from Item item where item.name = ? and item.company.id = ? ",
							new Object[] { itemName, companyId });

			if (list.size() > 0) {
				Item item = new Item();
				item = (Item) list.get(0);
				return item;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Item getItem(long companyId, long itemId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from Item item where item.id = ? and item.company.id = ? ",
							new Object[] { itemId, companyId });

			if (list.size() > 0) {
				Item item = new Item();
				item = (Item) list.get(0);
				return item;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemGroup getItemGroup(long companyId, String itemGroupName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ItemGroup ig where ig.name = ? and ig.company.id = ? ",
							new Object[] { itemGroupName, companyId });

			if (list.size() > 0) {
				ItemGroup itemGroup = new ItemGroup();
				itemGroup = (ItemGroup) list.get(0);
				return itemGroup;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemGroup getItemGroup(long companyId, long itemGroupId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from ItemGroup ig where ig.id = ? and ig.company.id = ? ",
					new Object[] { itemGroupId, companyId });

			if (list.size() > 0) {
				ItemGroup itemGroup = new ItemGroup();
				itemGroup = (ItemGroup) list.get(0);
				return itemGroup;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemGroup> getItemGroups(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<ItemGroup> list = template.find(
					" from ItemGroup ig where ig.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Item> getItems(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Item> list = template.find(
					" from Item item where item.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public MakeDeposit getMakeDeposit(long companyId, long makeDepositId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from MakeDeposit md where md.id = ? and md.company.id = ? ",
							new Object[] { makeDepositId, companyId });

			if (list.size() > 0) {
				MakeDeposit makeDeposit = new MakeDeposit();
				makeDeposit = (MakeDeposit) list.get(0);
				return makeDeposit;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PayBill getPayBill(long companyId, long payBillId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from PayBill pb where pb.id = ? and pb.company.id = ? ",
					new Object[] { payBillId, companyId });

			if (list.size() > 0) {
				PayBill payBill = new PayBill();
				payBill = (PayBill) list.get(0);
				return payBill;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentTerms getPaymentTerms(long companyId, String paymentTermsName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PaymentTerms st where st.name = ? and st.company.id = ? ",
							new Object[] { paymentTermsName, companyId });

			if (list.size() > 0) {
				PaymentTerms paymentTerms = new PaymentTerms();
				paymentTerms = (PaymentTerms) list.get(0);
				return paymentTerms;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentTerms getPaymentTerms(long companyId, long paymentTermsId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PaymentTerms st where st.id = ? and st.company.id = ? ",
							new Object[] { paymentTermsId, companyId });

			if (list.size() > 0) {
				PaymentTerms paymentTerms = new PaymentTerms();
				paymentTerms = (PaymentTerms) list.get(0);
				return paymentTerms;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTerms> getPaymentTerms(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<PaymentTerms> list = template.find(
					" from PaymentTerms st where st.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {

				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PriceLevel getPriceLevel(long companyId, String priceLevelName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PriceLevel pl where pl.name = ? and pl.company.id = ? ",
							new Object[] { priceLevelName, companyId });

			if (list.size() > 0) {
				PriceLevel priceLevel = new PriceLevel();
				priceLevel = (PriceLevel) list.get(0);
				return priceLevel;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PriceLevel getPriceLevel(long companyId, long priceLevelId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PriceLevel pl where pl.id = ? and pl.company.id = ? ",
							new Object[] { priceLevelId, companyId });

			if (list.size() > 0) {
				PriceLevel priceLevel = new PriceLevel();
				priceLevel = (PriceLevel) list.get(0);
				return priceLevel;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PriceLevel> getPriceLevels(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<PriceLevel> list = template.find(
					" from PriceLevel pl where pl.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {

				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PurchaseOrder getPurchaseOrder(long companyId, long purchaseOrderId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from PurchaseOrder p where p.id = ? and p.company.id = ? ",
							new Object[] { purchaseOrderId, companyId });

			if (list.size() > 0) {
				PurchaseOrder purchaseOrder = new PurchaseOrder();
				purchaseOrder = (PurchaseOrder) list.get(0);
				return purchaseOrder;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaxRates> getRates(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<TaxRates> list = template.find(
					" from TaxRates tr where tr.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {

				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReceivePayment getReceivePayment(long companyId,
			long receivePaymentId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ReceivePayment rp where rp.id = ? and rp.company.id = ? ",
							new Object[] { receivePaymentId, companyId });

			if (list.size() > 0) {
				ReceivePayment receivePayment = new ReceivePayment();
				receivePayment = (ReceivePayment) list.get(0);
				return receivePayment;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	public List<ReceivePayment> getReceivePayments(long companyId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<ReceivePayment> list = template.find(
					"from ReceivePayment rp where rp.company.id = ? ",
					new Object[] { companyId });

			// if (list.size() > 0) {
			// ReceivePayment receivePayment = new ReceivePayment();
			// receivePayment = (ReceivePayment) list.get(0);
			return list;
			// } else
			// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
			// null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public SalesOrder getSalesOrder(long companyId, long salesOrderId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from SalesOrder s where s.id = ? and s.company.id = ? ",
					new Object[] { salesOrderId, companyId });

			if (list.size() > 0) {
				SalesOrder salesOrder = new SalesOrder();
				salesOrder = (SalesOrder) list.get(0);
				return salesOrder;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public SalesPerson getSalesPerson(long companyId, String salesPersonName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from SalesPerson sr where sr.firstName = ? and sr.company.id = ? ",
							new Object[] { salesPersonName, companyId });

			if (list.size() > 0) {
				SalesPerson salesPerson = new SalesPerson();
				salesPerson = (SalesPerson) list.get(0);
				return salesPerson;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SalesPerson getSalesPerson(long companyId, long salesPersonId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from SalesPerson sr where sr.id = ? and sr.company.id = ? ",
							new Object[] { salesPersonId, companyId });

			if (list.size() > 0) {
				SalesPerson salesRep = new SalesPerson();
				salesRep = (SalesPerson) list.get(0);
				return salesRep;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SalesPerson> getSalesPersons(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<SalesPerson> list = template.find(
					" from SalesPerson sr where sr.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShippingMethod getShippingMethod(long companyId,
			String shippingMethodName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ShippingMethod sm where sm.name = ? and sm.company.id = ? ",
							new Object[] { shippingMethodName, companyId });

			if (list.size() > 0) {
				ShippingMethod shippingMethod = new ShippingMethod();
				shippingMethod = (ShippingMethod) list.get(0);
				return shippingMethod;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShippingMethod getShippingMethod(long companyId,
			long shippingMethodId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ShippingMethod sm where sm.id = ? and sm.company.id = ? ",
							new Object[] { shippingMethodId, companyId });

			if (list.size() > 0) {
				ShippingMethod shippingMethod = new ShippingMethod();
				shippingMethod = (ShippingMethod) list.get(0);
				return shippingMethod;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ShippingMethod> getShippingMethods(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<ShippingMethod> list = template.find(
					" from ShippingMethod sr where sr.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShippingTerms getShippingTerms(long companyId,
			String shippingTermsName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ShippingTerms sm where sm.name = ? and sm.company.id = ? ",
							new Object[] { shippingTermsName, companyId });

			if (list.size() > 0) {
				ShippingTerms shippingTerms = new ShippingTerms();
				shippingTerms = (ShippingTerms) list.get(0);
				return shippingTerms;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShippingTerms getShippingTerms(long companyId, long shippingTermsId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from ShippingTerms sm where sm.id = ? and sm.company.id = ? ",
							new Object[] { shippingTermsId, companyId });

			if (list.size() > 0) {
				ShippingTerms shippingTerms = new ShippingTerms();
				shippingTerms = (ShippingTerms) list.get(0);
				return shippingTerms;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ShippingTerms> getShippingTerms(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<ShippingTerms> list = template.find(
					" from ShippingTerms sr where sr.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TAXAgency> getTaxAgencies(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<TAXAgency> list = template.find(
					" from TAXAgency ta where ta.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXAgency getTaxAgency(long companyId, String taxAgencyName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from TAXAgency ta where ta.name = ? and ta.company.id = ? ",
							new Object[] { taxAgencyName, companyId });

			if (list.size() > 0) {
				TAXAgency TAXAgency = new TAXAgency();
				TAXAgency = (TAXAgency) list.get(0);
				return TAXAgency;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXAgency getTaxAgency(long companyId, long taxAgencyID)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from TAXAgency ta where ta.id = ? and ta.company.id = ? ",
					new Object[] { taxAgencyID, companyId });

			if (list.size() > 0) {
				TAXAgency TAXAgency = new TAXAgency();
				TAXAgency = (TAXAgency) list.get(0);
				return TAXAgency;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXCode getTaxCode(long companyId, String taxCodeName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from TAXCode tc where tc.name = ? and tc.company.id = ? ",
					new Object[] { taxCodeName, companyId });

			if (list.size() > 0) {
				TAXCode taxCode = new TAXCode();
				taxCode = (TAXCode) list.get(0);
				return taxCode;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXCode getTaxCode(long companyId, long taxCodeID)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from TAXCode tc where tc.id = ? and tc.company.id = ? ",
					new Object[] { taxCodeID, companyId });

			if (list.size() > 0) {
				TAXCode taxCode = new TAXCode();
				taxCode = (TAXCode) list.get(0);
				return taxCode;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXGroup getTaxGroup(long companyId, String taxGroupName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from TAXGroup tg where tg.name = ? and tg.company.id = ? ",
							new Object[] { taxGroupName, companyId });

			if (list.size() > 0) {
				TAXGroup taxGroup = new TAXGroup();
				taxGroup = (TAXGroup) list.get(0);
				return taxGroup;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAXGroup getTaxGroup(long companyId, long taxGroupID)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from TAXGroup tg where tg.id = ? and tg.company.id = ? ",
					new Object[] { taxGroupID, companyId });

			if (list.size() > 0) {
				TAXGroup taxGroup = new TAXGroup();
				taxGroup = (TAXGroup) list.get(0);
				return taxGroup;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TaxRates getTaxRates(long companyId, Double rate)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from TaxRates tg where tg.rate = ? and tg.company.id = ? ",
							new Object[] { rate, companyId });

			if (list.size() > 0) {
				TaxRates taxRate = new TaxRates();
				taxRate = (TaxRates) list.get(0);
				return taxRate;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TaxRates getTaxRates(long companyId, long taxRateID)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from TaxRates tg where tg.id = ? and tg.company.id = ? ",
					new Object[] { taxRateID, companyId });

			if (list.size() > 0) {
				TaxRates taxRate = new TaxRates();
				taxRate = (TaxRates) list.get(0);
				return taxRate;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TransferFund getTransferFund(long companyId, long transferFundId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from TransferFund tf where tf.id = ? and tf.company.id = ? ",
							new Object[] { transferFundId, companyId });

			if (list.size() > 0) {
				TransferFund transferFund = new TransferFund();
				transferFund = (TransferFund) list.get(0);
				return transferFund;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnitOfMeasure getUnitOfMeasure(long companyId,
			String unitOfMeasureName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from UnitOfMeasure u where u.name = ? and u.company.id = ? ",
							new Object[] { unitOfMeasureName, companyId });

			if (list.size() > 0) {
				UnitOfMeasure unitofmeasure = new UnitOfMeasure();
				unitofmeasure = (UnitOfMeasure) list.get(0);
				return unitofmeasure;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnitOfMeasure getUnitOfMeasure(long companyId, long unitOfMeasureId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from UnitOfMeasure u where u.id = ? and u.company.id = ? ",
							new Object[] { unitOfMeasureId, companyId });

			if (list.size() > 0) {
				UnitOfMeasure unitofmeasure = new UnitOfMeasure();
				unitofmeasure = (UnitOfMeasure) list.get(0);
				return unitofmeasure;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UnitOfMeasure> getUnitOfMeasures(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<UnitOfMeasure> list = template.find(
					" from UnitOfMeasure u where u.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUser(long userID) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();
			User example = new User();
			example.setID(userID);
			List list = template.find("from User u where u.id=?",
					new Object[] { userID });
			if (list.size() > 0) {
				return (User) list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUser(String userName) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find("from User u where u.email= ? ",
					new Object[] { userName });
			if (list.size() > 0) {
				return (User) list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUserByDomainURL(String domainURL) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find("from User u where u.domainURL= ? ",
					new Object[] { domainURL });
			if (list.size() > 0) {
				return (User) list.get(0);
			} else {
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
			}
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<User> list = template.find(
					" from User u where u.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vendor getVendor(long companyId, String vendorName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Vendor v where v.name = ? and v.company.id = ? ",
					new Object[] { vendorName, companyId });

			if (list.size() > 0) {
				Vendor vendor = new Vendor();
				vendor = (Vendor) list.get(0);
				return vendor;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vendor getVendor(long companyId, long vendorId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from Vendor v where v.id = ? and v.company.id = ? ",
					new Object[] { vendorId, companyId });

			if (list.size() > 0) {
				Vendor vendor = new Vendor();
				vendor = (Vendor) list.get(0);
				return vendor;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public VendorCreditMemo getVendorCreditMemo(long companyId,
			long vendorrCreditMemoId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from VendorCreditMemo v where v.id = ? and v.company.id = ? ",
							new Object[] { vendorrCreditMemoId, companyId });

			if (list.size() > 0) {
				VendorCreditMemo vendorrCreditMemo = new VendorCreditMemo();
				vendorrCreditMemo = (VendorCreditMemo) list.get(0);
				return vendorrCreditMemo;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public VendorGroup getVendorGroup(long companyId, String vendorGroupName)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from VendorGroup v where v.name = ? and v.company.id = ? ",
							new Object[] { vendorGroupName, companyId });

			if (list.size() > 0) {
				VendorGroup vendorType = new VendorGroup();
				vendorType = (VendorGroup) list.get(0);
				return vendorType;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public VendorGroup getVendorGroup(long companyId, long vendorGroupId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from VendorGroup v where v.id = ? and v.company.id = ? ",
					new Object[] { vendorGroupId, companyId });

			if (list.size() > 0) {
				VendorGroup vendorType = new VendorGroup();
				vendorType = (VendorGroup) list.get(0);
				return vendorType;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendorGroup> getVendorGroups(long companyId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<VendorGroup> list = template.find(
					" from VendorGroup vt where vt.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Vendor> getVendors(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Vendor> list = template.find(
					" from Vendor v where v.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public WriteCheck getwriterCheck(long companyId, long writeCheckId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from WriteCheck wc where wc.id = ? and wc.company.id = ? ",
							new Object[] { writeCheckId, companyId });

			if (list.size() > 0) {
				WriteCheck writeCheck = new WriteCheck();
				writeCheck = (WriteCheck) list.get(0);
				return writeCheck;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	/**
	 * Report Related
	 */

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteAccount(long account) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deleteaccount",
					"id", account);

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteCompany(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deletecompany",
					"id", companyId);

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteCreditRating(CreditRating creditRating)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletecreditrating", "id", creditRating.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteCustomer(Customer customer) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletecustomer", "id", customer.getID());

			if (list.size() > 0) {
				return false;
			} else
				// deleteCustomer(customer);
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteCustomerGroup(CustomerGroup customerGroup)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletecustomergroup", "id", customerGroup.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteItem(Item item) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deleteitem",
					"id", item.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteItemGroup(ItemGroup itemGroup) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deleteitemgroup", "id", itemGroup.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeletePaymentTerms(PaymentTerms paymentTerms)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deleteterms",
					"id", paymentTerms.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeletePriceLevel(PriceLevel priceLevel)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletepricelevel", "id", priceLevel.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteSalesPerson(SalesPerson salesPerson)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletesalesrep", "id", salesPerson.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteShippingMethod(ShippingMethod shippingMethod)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deleteshippingmethod", "id", shippingMethod.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteShippingTerms(ShippingTerms shippingTerms)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deleteshippingterms", "id", shippingTerms.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteTaxAgency(TAXAgency TAXAgency) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletetaxagency", "id", TAXAgency.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteTaxCode(TAXCode taxCode) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deletetaxcode",
					"id", taxCode.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteTaxGroup(TAXGroup taxGroup) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletetaxgroup", "id", taxGroup.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteTaxRates(TaxRates taxRates) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletetaxrates", "id", taxRates.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteUnitOfMeasure(UnitOfMeasure unitOfMeasure)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deleteunitofmeasure", "id", unitOfMeasure.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteUser(long user) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deleteuser",
					"id", user);

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteVendor(Vendor vendor) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam("deletevendor",
					"id", vendor.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canDeleteVendorGroup(VendorGroup vendorGroup)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.findByNamedQueryAndNamedParam(
					"deletevendortype", "id", vendorGroup.getID());

			if (list.size() > 0) {
				return false;
			} else
				return true;

		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Bank> getBanks(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Bank> list = template.find(
					" from Bank b where b.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditsAndPayments> getCreditsAndPayments(long companyId)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<CreditsAndPayments> list = template
					.find(
							"from CreditsAndPayments cp where cp.transaction.company.id = ? ",
							new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public CreditsAndPayments getCreditAndPayment(long companyId, String
	// memo)
	// throws DAOException {
	//
	// try {
	// HibernateTemplate template = getHibernateTemplate();
	//
	// List list = template
	// .find(
	// "from CreditsAndPayments cp where cp.memo = ? and cp.company.id = ? ",
	// new Object[] { memo, companyId });
	//
	// if (list.size() > 0) {
	// CreditsAndPayments creditsAndPayments = new CreditsAndPayments();
	// creditsAndPayments = (CreditsAndPayments) list.get(0);
	// return creditsAndPayments;
	// } else
	// throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
	// null));
	// } catch (DataAccessException e) {
	// throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
	// }
	//
	// }

	@SuppressWarnings("unchecked")
	@Override
	public CreditsAndPayments getCreditAndPayment(long companyId, long id)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from CreditsAndPayments cp where cp.id = ? and cp.transaction.company.id = ? ",
							new Object[] { id, companyId });

			if (list.size() > 0) {
				CreditsAndPayments creditsAndPayments = new CreditsAndPayments();
				creditsAndPayments = (CreditsAndPayments) list.get(0);
				return creditsAndPayments;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Payee> getPayee(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<Payee> list = template.find(
					" from Payee p where p.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FiscalYear> getFiscalYears(long companyId) throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List<FiscalYear> list = template.find(
					" from FiscalYear f where f.company.id = ? ",
					new Object[] { companyId });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public FiscalYear getFiscalYear(long companyId, long yearId)
			throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template.find(
					"from FiscalYear f where f.id = ? and f.company.id = ? ",
					new Object[] { yearId, companyId });

			if (list.size() > 0) {
				FiscalYear fiscalYear = new FiscalYear();
				fiscalYear = (FiscalYear) list.get(0);
				return fiscalYear;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Expense> getUnPaidExpense(long companyId) throws DAOException {
		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from Expense e where e.company.id = ? and (e.status = ? or (e.status = ? and e.isAuthorised = true) ",
							new Object[] { companyId,
									Expense.STATUS_PARTIALLY_PAID,
									Expense.STATUS_APPROVED });

			if (list != null) {
				return list;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

	@SuppressWarnings("unchecked")
	public List getTestResult() throws Exception {

		HibernateTemplate template = getHibernateTemplate();
		// List l = (List) template.execute(new HibernateCallback(){
		// @Override
		// public Object doInHibernate(Session session)
		// throws HibernateException, SQLException {
		// Criteria criteria =
		// session.createCriteria(Transaction.class).setFirstResult(2).setMaxResults(4)
		// .setProjection(Projections.projectionList()
		// .add(Projections.rowCount())
		// .add(Projections.sum("total"))
		// .add(Projections.max("total"))
		// .add(Projections.groupProperty("type"))
		// ).addOrder(Order.desc("type"));
		// List l = criteria.list();
		//				
		// List l2 = criteria.setFirstResult(1).setMaxResults(5).list();
		//				
		// return l;
		// }});

		final List l = (List) template.execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery("from ? entiy ")
						.setParameter(0, "TAXAgency");
				return query.list();
			}
		});

		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FixedAsset getFixedAsset(long companyId, long fixedAssetID)
			throws DAOException {

		try {
			HibernateTemplate template = getHibernateTemplate();

			List list = template
					.find(
							"from FixedAsset fa where fa.id = ? and fa.company.id = ? ",
							new Object[] { fixedAssetID, companyId });

			if (list.size() > 0) {
				FixedAsset fixedAsset = new FixedAsset();
				fixedAsset = (FixedAsset) list.get(0);
				return fixedAsset;
			} else
				throw (new DAOException(DAOException.INVALID_REQUEST_EXCEPTION,
						null));
		} catch (DataAccessException e) {
			throw (new DAOException(DAOException.DATABASE_EXCEPTION, e));
		}
	}

}