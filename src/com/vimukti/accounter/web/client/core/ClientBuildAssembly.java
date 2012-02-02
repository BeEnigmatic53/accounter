package com.vimukti.accounter.web.client.core;

public class ClientBuildAssembly extends ClientTransaction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8215768906353162049L;
	private ClientInventoryAssembly inventoryAssembly;
	private String refNo;
	private Double quantityToBuild;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public AccounterCoreType getObjectType() {
		return AccounterCoreType.BUILD_ASSEMBLY;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}

	@Override
	public long getID() {
		return id;
	}

	public ClientInventoryAssembly getInventoryAssembly() {
		return inventoryAssembly;
	}

	public void setInventoryAssembly(ClientInventoryAssembly inventoryAssembly) {
		this.inventoryAssembly = inventoryAssembly;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Double getQuantityToBuild() {
		return quantityToBuild;
	}

	public void setQuantityToBuild(Double quantityToBuild) {
		this.quantityToBuild = quantityToBuild;
	}

}