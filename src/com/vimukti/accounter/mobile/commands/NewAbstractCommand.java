package com.vimukti.accounter.mobile.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.main.ServerGlobal;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.web.client.IGlobal;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.externalization.AccounterConstants;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.server.FinanceTool;
import com.vimukti.accounter.web.server.OperationContext;

public abstract class NewAbstractCommand extends NewCommand {
	public static final String FIRST_MESSAGE = "firstMessage";
	private IGlobal global;
	private AccounterConstants constants;
	private AccounterMessages messages;
	protected static final String AMOUNTS_INCLUDE_TAX = "Amounts Include tax";
	protected static final String COUNTRY = "country";
	protected static final String PHONE = "phone";
	protected static final String EMAIL = "email";
	protected static final String ACTIONS = "actions";
	protected static final int VALUES_TO_SHOW = 5;
	protected static final int COUNTRIES_TO_SHOW = 5;
	protected static final String VIEW_BY = "viewBy";
	protected static final String FROM_DATE = "fromDate";
	protected static final String TO_DATE = "to_date";
	protected static final String DATE_RANGE = "dateRange";

	public NewAbstractCommand() {

	}

	@Override
	public void init() {
		try {
			global = new ServerGlobal();
		} catch (IOException e) {
			e.printStackTrace();
		}
		constants = global.constants();
		messages = global.messages();
		super.init();
	}

	protected AccounterConstants getConstants() {
		return constants;
	}

	protected AccounterMessages getMessages() {
		return messages;
	}

	protected void create(IAccounterCore coreObject, Context context) {
		try {
			if (coreObject.getID() == 0) {
				String clientClassSimpleName = coreObject.getObjectType()
						.getClientClassSimpleName();

				OperationContext opContext = new OperationContext(context
						.getCompany().getID(), coreObject, context
						.getIOSession().getUserEmail());

				opContext.setArg2(clientClassSimpleName);

				new FinanceTool().create(opContext);
			} else {
				String serverClassFullyQualifiedName = coreObject
						.getObjectType().getServerClassFullyQualifiedName();

				OperationContext opContext = new OperationContext(context
						.getCompany().getID(), coreObject, context
						.getIOSession().getUserEmail(),
						String.valueOf(coreObject.getID()),
						serverClassFullyQualifiedName);

				new FinanceTool().update(opContext);
			}
		} catch (AccounterException e) {
			e.printStackTrace();
		}
	}

	protected long getNumberFromString(String string) {
		if (string.isEmpty()) {
			return 0;
		}
		if (string.charAt(0) != '#') {
			return 0;
		}
		string = string.substring(1);
		return Long.parseLong(string);
	}

	@SuppressWarnings("unchecked")
	public void addFirstMessage(Context context, String string) {
		((List<String>) context.getAttribute(FIRST_MESSAGE)).add(string);
	}

	protected List<String> getPaymentMethods() {
		List<String> listOfPaymentMethods = new ArrayList<String>();
		listOfPaymentMethods.add(getConstants().cash());
		listOfPaymentMethods.add(getConstants().cheque());
		listOfPaymentMethods.add(getConstants().creditCard());
		listOfPaymentMethods.add(getConstants().directDebit());
		listOfPaymentMethods.add(getConstants().masterCard());
		listOfPaymentMethods.add(getConstants().onlineBanking());
		listOfPaymentMethods.add(getConstants().standingOrder());
		listOfPaymentMethods.add(getConstants().switchMaestro());
		return listOfPaymentMethods;
	}

	/**
	 * set address Type
	 * 
	 * @param type
	 * @return
	 */
	public int getAddressType(String type) {
		if (type.equalsIgnoreCase("1"))
			return ClientAddress.TYPE_BUSINESS;
		else if (type.equalsIgnoreCase(getConstants().billTo()))
			return ClientAddress.TYPE_BILL_TO;
		else if (type.equalsIgnoreCase(getConstants().shipTo()))
			return ClientAddress.TYPE_SHIP_TO;
		else if (type.equalsIgnoreCase("2"))
			return ClientAddress.TYPE_WAREHOUSE;
		else if (type.equalsIgnoreCase("3"))
			return ClientAddress.TYPE_LEGAL;
		else if (type.equalsIgnoreCase("4"))
			return ClientAddress.TYPE_POSTAL;
		else if (type.equalsIgnoreCase("5"))
			return ClientAddress.TYPE_HOME;
		else if (type.equalsIgnoreCase(Accounter.constants().company()))
			return ClientAddress.TYPE_COMPANY;
		else if (type.equalsIgnoreCase(Accounter.constants()
				.companyregistration()))
			return ClientAddress.TYPE_COMPANY_REGISTRATION;

		return ClientAddress.TYPE_OTHER;
	}
}
