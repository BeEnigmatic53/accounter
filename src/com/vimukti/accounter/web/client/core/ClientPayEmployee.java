package com.vimukti.accounter.web.client.core;

import java.util.List;

public class ClientPayEmployee implements IAccounterCore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Pay Run
	 */
	private ClientPayRun payRun;

	private List<ClientEmployeePayHeadComponent> payHeadComponents;
	
	public ClientPayRun getPayRun() {
		return payRun;
	}

	public void setPayRun(ClientPayRun payRun) {
		this.payRun = payRun;
	}

	public List<ClientEmployeePayHeadComponent> getPayHeadComponents() {
		return payHeadComponents;
	}

	public void setPayHeadComponents(
			List<ClientEmployeePayHeadComponent> payHeadComponents) {
		this.payHeadComponents = payHeadComponents;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setVersion(int version) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccounterCoreType getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setID(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
