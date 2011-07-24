package com.vimukti.accounter.core;

import com.vimukti.accounter.web.client.InvalidOperationException;

/**
 * 
 * @author vimukti16 Not used Yet
 */
@SuppressWarnings("serial")
public class Currency implements IAccounterServerCore {

	int version;

	long id;
	/**
	 * Name of the Currency
	 */
	String name;
	/**
	 * Symbol of the Currency.
	 */
	String symbol;
	/**
	 * Formal Name of the Currency.
	 */
	String formalName;

	

	public Currency() {
		// TODO
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the formalName
	 */
	public String getFormalName() {
		return formalName;
	}

	@Override
	public long getID(){
		return this.id;
	}



	@Override
	public boolean canEdit(IAccounterServerCore clientObject)
			throws InvalidOperationException {
		// TODO Auto-generated method stub
		return true;
	}

}
