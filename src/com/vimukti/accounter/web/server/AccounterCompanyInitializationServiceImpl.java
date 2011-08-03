/**
 * 
 */
package com.vimukti.accounter.web.server;

import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vimukti.accounter.core.Client;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.ServerCompany;
import com.vimukti.accounter.core.User;
import com.vimukti.accounter.main.Server;
import com.vimukti.accounter.servlets.BaseServlet;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.IAccounterCompanyInitializationService;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.settings.RolePermissions;

/**
 * @author Prasanna Kumar G
 * 
 */
public class AccounterCompanyInitializationServiceImpl extends
		RemoteServiceServlet implements IAccounterCompanyInitializationService {

	@Override
	public ClientCompany createCompany(int accountType)
			throws AccounterException {
		// Validating User Session
		HttpSession httpSession = getHttpSession();
		String emailID = (String) httpSession
				.getAttribute(BaseServlet.EMAIL_ID);
		if (emailID == null) {
			throw new AccounterException(
					AccounterException.ERROR_INVALID_USER_SESSION);
		}
		Session serverSession = HibernateUtil
				.openSession(Server.LOCAL_DATABASE);
		Transaction transaction = serverSession.beginTransaction();

		// Getting ServerCompany From Session
		ServerCompany serverCompany = getServerCompany();

		try {

			// Creating Database
			Query query = serverSession.createSQLQuery("CREATE SCHEMA company"
					+ serverCompany.getID());
			query.executeUpdate();
			transaction.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			transaction.rollback();
		}

		Session companySession = HibernateUtil.openSession(Server.COMPANY
				+ serverCompany.getID(), true);
		Transaction companyTransaction = companySession.beginTransaction();
		try {
			// Creating User
			User user = getClientHttpSession();
			companySession.save(user);

			Company company = new Company();
			company.setCreatedBy(user);
			company.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			companySession.save(company);

			companyTransaction.commit();

			// Returning ClientCompany
			return company.toClientCompany();
		} catch (Exception e) {
			// FIXME Revert Transaction made with ServerSession
			e.printStackTrace();
			companyTransaction.rollback();
		}

		return null;
	}

	/**
	 * Returns User Object From Client HttpSession
	 */
	private User getClientHttpSession() {
		String userCookie = getCookie(BaseServlet.USER_COOKIE);
		if (userCookie == null) {
			return null;
		}
		String[] split = userCookie.split(",");
		Session session = HibernateUtil.getCurrentSession();
		Client client = (Client) session.getNamedQuery("getClient.by.mailId")
				.setString(BaseServlet.EMAIL_ID, split[0]).uniqueResult();
		User user = client.toUser();
		user.setUserRole(RolePermissions.ADMIN);
		user.setAdmin(true);
		return user;
	}

	/**
	 * Returns ServerCompany From ClientSession
	 * 
	 * @return
	 */
	private ServerCompany getServerCompany() {
		String cid = getCookie(BaseServlet.COMPANY_COOKIE);

		Session session = HibernateUtil.getCurrentSession();
		return (ServerCompany) session.getNamedQuery("getServerCompany.by.id")
				.setParameter("id", Long.parseLong(cid)).uniqueResult();

	}

	private String getCookie(String cookieName) {
		Cookie[] clientCookies = getThreadLocalRequest().getCookies();
		if (clientCookies != null) {
			for (Cookie cookie : clientCookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Current HttpSesstion
	 * 
	 * @return
	 */
	protected HttpSession getHttpSession() {
		return getThreadLocalRequest().getSession();
	}

}
