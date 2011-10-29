/**
 * 
 */
package com.vimukti.accounter.mobile;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TimerTask;

import org.hibernate.Session;

import com.vimukti.accounter.core.AccounterThreadLocal;
import com.vimukti.accounter.core.Client;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.IMUser;
import com.vimukti.accounter.core.User;
import com.vimukti.accounter.main.CompanyPreferenceThreadLocal;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.server.FinanceTool;

/**
 * @author Prasanna Kumar G
 * 
 */
public class MobileSession {

	public static final long SESSION_TIME_OUT_PERIOD = 24 * 60 * 60 * 1000;// 1day

	private static final String LAST_MESSAGE = "lastMessage";

	private Client client;
	private long companyID;
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private Command currentCommand;
	private boolean isExpired;
	private Stack<UserMessage> commandStack = new Stack<UserMessage>();
	private TimerTask task;
	private User user;

	private Session hibernateSession;

	private boolean isAuthenicated;

	private String lastReply = "";

	/**
	 * Creates new Instance
	 */
	public MobileSession() {
	}

	public String getUserEmail() {
		return getClient().getEmailId();
	}

	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Object getLast(RequirementType customer) {
		return attributes.get(customer);
	}

	public void setLast(RequirementType customer, Object obj) {
		this.attributes.put(customer, obj);
	}

	/**
	 * Returns the Current Processing Command
	 * 
	 * @return
	 */
	public Command getCurrentCommand() {
		return this.currentCommand;
	}

	/**
	 * Sets the CurrentCommand
	 * 
	 * @param command
	 */
	public void setCurrentCommand(Command command) {
		this.currentCommand = command;
	}

	public boolean isExpired() {
		return this.isExpired;
	}

	public void setExpired(boolean value) {
		this.isExpired = value;
	}

	public void await() {
		refresh();
		this.task = new TimerTask() {

			@Override
			public void run() {
				setExpired(true);
			}
		};
		AccounterTimer.INSTANCE.schedule(task, SESSION_TIME_OUT_PERIOD);
	}

	public void refresh() {
		if (this.task != null) {
			task.cancel();
		}
	}

	public void refreshCurrentCommand() {
		// Set the Present Command From the stack
		while (!commandStack.isEmpty()) {
			UserMessage userMessage = commandStack.pop();
			if (userMessage == null) {
				continue;
			}
			Command pop = userMessage.getCommand();
			if (pop == null) {
				break;
			}
			if (!pop.isDone()) {
				setCurrentCommand(pop);
				setLastMessage(userMessage);
				commandStack.push(userMessage);
				return;
			}
		}
		setCurrentCommand(null);
	}

	/**
	 * @return
	 */
	public User getUser() {
		return user;
	}

	public Company getCompany() {
		return (Company) hibernateSession.get(Company.class, companyID);
	}

	/**
	 * @param session
	 */
	public void sethibernateSession(Session session) {
		this.hibernateSession = session;
	}

	public Session getHibernateSession() {
		return this.hibernateSession;
	}

	/**
	 * @return
	 */
	public UserMessage getLastMessage() {
		return (UserMessage) getAttribute(LAST_MESSAGE);
	}

	/**
	 * @param result
	 */
	public void setLastMessage(UserMessage userMessage) {
		setAttribute(LAST_MESSAGE, userMessage);
	}

	/**
	 * Return the from field of the Message
	 * 
	 * @return
	 */
	public IMUser getFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getCompanyID() {
		return companyID;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompanyID(long company) {
		this.companyID = company;
	}

	public void addCommand(Command command) {
		if (currentCommand != command) {
			UserMessage lastMessage = getLastMessage();
			commandStack.push(lastMessage);
			setCurrentCommand(command);
		}
	}

	/**
	 * Removes an Attribute from the Context
	 * 
	 * @param name
	 * @return
	 */
	public Object removeAttribute(String name) {
		return attributes.remove(name);
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void reloadObjects() {
		if (client != null) {
			client = (Client) hibernateSession
					.get(Client.class, client.getID());
		}
		if (user != null) {
			user = (User) hibernateSession.get(User.class, user.getID());
			AccounterThreadLocal.set(user);
		}
		Company company = getCompany();
		if (company != null) {
			ClientCompanyPreferences preferences;
			try {
				preferences = new FinanceTool().getCompanyManager()
						.getClientCompanyPreferences(company);
				CompanyPreferenceThreadLocal.set(preferences);
			} catch (AccounterException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isAuthenticated() {
		return isAuthenicated;
	}

	public void setAuthentication(boolean b) {
		isAuthenicated = b;
	}

	public String getLastReply() {
		return lastReply;
	}

	public void setLastReply(String lastReply) {
		this.lastReply = lastReply;
	}
}
