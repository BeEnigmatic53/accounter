package com.vimukti.accounter.servlets;

import java.io.IOException;
import java.security.Security;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.vimukti.accounter.main.LiveServer;
import com.vimukti.accounter.utils.HexUtil;
import com.vimukti.accounter.utils.HibernateUtil;

public class SubscriptionModificationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		CollaberIdentity identity = doLogin(req, resp);

		if (identity == null) {
			dispatchView(req, resp,
					"Bizantra EmailID or Password you entered is incorrect");
		} else if (identity.getClientIdentity().getCompany().expirationDate
				.before(new Date())) {
			dispatchView(req, resp, "Your Company has been expired");
			// TODO Karthik
		} else {
			dispatchView(req, resp, "Company Modified");
		}
	}

	public void dispatchView(HttpServletRequest request,
			HttpServletResponse response, String view) {
		try {
			response.getWriter().write(view);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CollaberIdentity doLogin(HttpServletRequest request,
			HttpServletResponse response) {
		String emailId = null;
		String password = null;
		String companyName = null;
		try {
			JSONObject jsonObject = new JSONObject(request
					.getParameter("jsonObject"));
			emailId = jsonObject.getString("emailId");
			password = jsonObject.getString("password");
			companyName = jsonObject.getString("companyName");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (emailId != null && password != null) {
			password = HexUtil
					.bytesToHex(Security.makeHash(emailId + password));
			CollaberIdentity identity = getIDentity(emailId, password,
					companyName);
			return identity;
		}
		return null;
	}

	private CollaberIdentity getIDentity(String emailId, String password,
			String companyName) {

		if (companyName == null) {
			return null;
		}
		if (!LiveServer.getInstance().isCompanyExists(companyName)) {
			return null;
		}

		Session session = HibernateUtil.openSession(companyName);
		try {
			CollaberIdentity identity = null;
			Query query = session
					.getNamedQuery("getIDentity.from.emailid.and.password");
			query.setParameter("emailid", emailId);
			query.setParameter("password", password);
			identity = (CollaberIdentity) query.uniqueResult();
			return identity;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
}
