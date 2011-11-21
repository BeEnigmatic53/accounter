package com.vimukti.accounter.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vimukti.accounter.core.Client;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.User;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.exception.AccounterException;

public class CancelAccountServlet extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String DELETE_ACCOUNT_CONFORM = "/WEB-INF/cancelform.jsp";
	protected long companyID;
	private String cancelFormList = "/WEB-INF/cancelform.jsp";

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		final HttpSession httpSession = req.getSession();
		String emailID = (String) httpSession.getAttribute(EMAIL_ID);
		if (emailID == null) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				deleteAccount(httpSession);
			}
		}).start();
		redirectExternal(req, resp, COMPANY_STATUS_URL);
	}

	private void deleteAccount(HttpSession httpSession) {
		String emailID = (String) httpSession.getAttribute(EMAIL_ID);
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			httpSession.setAttribute(ACCOUNT_DELETION_STATUS, ACCOUNT_DELETING);
			Client client = getClient(emailID);
			Set<User> users = client.getUsers();
			if (users != null && !users.isEmpty()) {
				final List<Company> list = new ArrayList<Company>();
				Company com = null;
				for (User user : users) {
					if (!user.isDeleted()) {
						List<Object[]> objects = session
								.getNamedQuery(
										"get.CompanyId.Tradingname.and.Country.of.user")
								.setParameter("userId;", user.getID()).list();
						for (Object[] obj : objects) {
							com = new Company();
							com.setId((Long) obj[0]);
							com.getPreferences()
									.setTradingName((String) obj[1]);
							com.getRegisteredAddress().setCountryOrRegion(
									(String) obj[2]);

							list.add(com);
						}
					}
				}
				deleteComapny(httpSession, list);
			}
			client.setDeleted(true);
			session.saveOrUpdate(client);
			transaction.commit();
			httpSession.setAttribute(ACCOUNT_DELETION_STATUS, "Success");
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
			httpSession.setAttribute(ACCOUNT_DELETION_STATUS, "Fail");
		} finally {
			session.close();
			httpSession.setAttribute("DeletionFailureMessage",
					"Internal Error Occured");
		}
	}

	private void deleteComapny(HttpSession httpSession,
			final List<Company> sortedCompanies) throws IOException,
			AccounterException {
		String email = (String) httpSession.getAttribute(EMAIL_ID);
		Long companyID;
		for (Company company : sortedCompanies) {
			companyID = company.getId();
			Session session = HibernateUtil.getCurrentSession();
			boolean canDeleteFromAll = true;
			User user = (User) session.getNamedQuery("user.by.emailid")
					.setParameter("emailID", email)
					.setParameter("company", company).uniqueResult();
			Query query = session.getNamedQuery("get.Admin.Users")
					.setParameter("company", company);
			List<User> adminUsers = query.list();
			for (User adminUser : adminUsers) {
				if (adminUser.getID() != user.getID()) {
					canDeleteFromAll = false;
					break;
				}
			}
			if (user != null && !user.isAdmin()) {
				canDeleteFromAll = false;
			}
			Client client = getClient(email);
			Company serverCompany = null;
			for (User usr : client.getUsers()) {
				if (usr.getCompany().getID() == companyID) {
					serverCompany = usr.getCompany();
				}
			}
			if (canDeleteFromAll) {
				serverCompany.onDelete(session);
				session.delete(serverCompany);
			} else {
				user.setDeleted(true);
				session.saveOrUpdate(user);
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession httpSession = req.getSession();
		String emailID = (String) httpSession.getAttribute(EMAIL_ID);
		if (emailID == null) {
			redirectExternal(req, resp, LOGIN_URL);
			return;
		}

		Session session = HibernateUtil.openSession();
		try {
			Client client = getClient(emailID);
			if (client == null) {
				redirectExternal(req, resp, LOGIN_URL);
				return;
			}

			Set<User> users = client.getUsers();
			if (users != null && !users.isEmpty()) {
				List<Company> list = new ArrayList<Company>();
				for (User user : users) {
					list.add(user.getCompany());
				}
				req.setAttribute(ATTR_COMPANY_LIST, list);
			}
		} catch (Exception e) {
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		dispatch(req, resp, cancelFormList);
	}

}
