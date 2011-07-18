package com.vimukti.accounter.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.CallbackException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

import com.vimukti.accounter.core.change.ChangeTracker;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.InvalidOperationException;
import com.vimukti.accounter.web.client.core.AccounterCommand;
import com.vimukti.accounter.web.client.core.AccounterCoreType;

/**
 * A VATCode is the entity which actually applies VAT. In a transaction we need
 * to apply the suitable VATCode for the line items involved in it which then
 * calculates the VAT amount for that transaction and notes it into @link
 * VATRateCalculation}
 * 
 * @author Chandan 
 * 
 */
public class TAXCode implements IAccounterServerCore, Lifecycle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1014067769682467798L;
	String stringID;
	long id;
	boolean isImported;

	String name;
	String description;
	boolean isTaxable;
	boolean isActive;
	@ReffereredObject
	TAXItemGroup TAXItemGrpForPurchases;
	@ReffereredObject
	TAXItemGroup TAXItemGrpForSales;

	boolean isECSalesEntry;
	boolean isDefault;
	transient private boolean isOnSaveProccessed;
		
	public TAXCode() {
		
	}
	
	public TAXCode(TAXItemGroup taxItemGroup) {
		this.name = taxItemGroup.name;
		this.description = taxItemGroup.description;
		this.isActive = taxItemGroup.isActive;
		this.TAXItemGrpForSales = taxItemGroup;	
		this.isTaxable = true;
		this.stringID = taxItemGroup.stringID;
		this.isECSalesEntry = false;
		this.TAXItemGrpForPurchases = null;		
	}
	
	/**
	 * @return the isImported
	 */
	public boolean isImported() {
		return isImported;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isTaxable
	 */
	public boolean isTaxable() {
		return isTaxable;
	}

	/**
	 * @param isTaxable
	 *            the isTaxable to set
	 */
	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the vATItemGrpForPurchases
	 */
	public TAXItemGroup getVATItemGrpForPurchases() {
		return TAXItemGrpForPurchases;
	}

	/**
	 * @param itemGrpForPurchases
	 *            the vATItemGrpForPurchases to set
	 */
	public void setTAXItemGrpForPurchases(TAXItemGroup itemGrpForPurchases) {
		TAXItemGrpForPurchases = itemGrpForPurchases;
	}

	/**
	 * @return the vATItemGrpForSales
	 */
	public TAXItemGroup getTAXItemGrpForSales() {
		return TAXItemGrpForSales;
	}

	/**
	 * @param itemGrpForSales
	 *            the vATItemGrpForSales to set
	 */
	public void setTAXItemGrpForSales(TAXItemGroup itemGrpForSales) {
		TAXItemGrpForSales = itemGrpForSales;
	}

	/**
	 * @return the isECSalesEntry
	 */
	public boolean isECSalesEntry() {
		return isECSalesEntry;
	}

	/**
	 * @param isECSalesEntry
	 *            the isECSalesEntry to set
	 */
	public void setECSalesEntry(boolean isECSalesEntry) {
		this.isECSalesEntry = isECSalesEntry;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String getStringID() {
		return this.stringID;
	}

	@Override
	public void setImported(boolean isImported) {
		this.isImported = isImported;
	}

	@Override
	public void setStringID(java.lang.String stringID) {
		this.stringID = stringID;
	}

	@Override
	public boolean onDelete(Session arg0) throws CallbackException {
		FinanceLogger.log("VAT Code with name: {0} has been deleted", this
				.getName());

		AccounterCommand accounterCore = new AccounterCommand();
		accounterCore.setCommand(AccounterCommand.DELETION_SUCCESS);
		accounterCore.setStringID(this.stringID);
		accounterCore.setObjectType(AccounterCoreType.TAX_CODE);
		ChangeTracker.put(accounterCore);
		return false;
	}

	@Override
	public void onLoad(Session arg0, Serializable arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSave(Session session) throws CallbackException {

		if (isImported) {
			return false;
		}
		if (this.isOnSaveProccessed)
			return true;
		this.isOnSaveProccessed = true;

		setIsECsalesEntry();
		ChangeTracker.put(this);
		return false;
	}

	@Override
	public boolean onUpdate(Session session) throws CallbackException {
		setIsECsalesEntry();
		ChangeTracker.put(this);
		return false;
	}

	private void setIsECsalesEntry() {
		
		if (this.getTAXItemGrpForSales() instanceof TAXGroup
				&& Company.getCompany() != null
				&& Company.getCompany().getAccountingType() == Company.ACCOUNTING_TYPE_UK) {

			String vatRetunrnName = ((TAXGroup) this.getTAXItemGrpForSales())
					.getTAXItems().get(0).getVatReturnBox().getName();
			String vatRetunrnName1 = ((TAXGroup) this.getTAXItemGrpForSales())
					.getTAXItems().get(1).getVatReturnBox().getName();

			this.isECSalesEntry = Arrays.asList(
					AccounterConstants.UK_EC_SALES_GOODS,
					AccounterConstants.UK_EC_SALES_SERVICES).containsAll(
					Arrays.asList(vatRetunrnName, vatRetunrnName1));

		}
	}

	@Override
	public boolean canEdit(IAccounterServerCore clientObject)
			throws InvalidOperationException {
		Session session = HibernateUtil.getCurrentSession();
		TAXCode taxCode = (TAXCode) clientObject;
		// Query query = session.createQuery("from VATCode V where V.name=?")
		// .setParameter(0, vatCode.name);
		Query query = session.getNamedQuery("getTAXCodeWithSameName").setParameter("name", this.name)
				.setParameter("id", this.id);
		List list = query.list();
		if (list != null && list.size() > 0) {
			throw new InvalidOperationException(
					"A VATCode already exists with this name");
		}
		return true;
	}
}
