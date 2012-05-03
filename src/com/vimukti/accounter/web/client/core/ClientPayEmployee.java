package com.vimukti.accounter.web.client.core;

import java.util.List;

public class ClientPayEmployee extends ClientTransaction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long employee;
	private long employeeGroup;
	private long payAccount;
	private List<ClientTransactionPayEmployee> payRunComponents;

	@Override
	public String getName() {
		return "PayEmployee";
	}

	@Override
	public String getDisplayName() {
		return "PayEmployee";
	}

	@Override
	public AccounterCoreType getObjectType() {
		return AccounterCoreType.TYPE_PAYEMPLOYEE;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}

	@Override
	public long getID() {
		return id;
	}

	public long getEmployee() {
		return employee;
	}

	public void setEmployee(long employee) {
		this.employee = employee;
	}

	public long getEmployeeGroup() {
		return employeeGroup;
	}

	public void setEmployeeGroup(long employeeGroup) {
		this.employeeGroup = employeeGroup;
	}

	public long getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(long payAccount) {
		this.payAccount = payAccount;
	}

	public List<ClientTransactionPayEmployee> getPayRunComponents() {
		return payRunComponents;
	}

	public void setPayRunComponents(
			List<ClientTransactionPayEmployee> payRunComponents) {
		this.payRunComponents = payRunComponents;
	}

}
