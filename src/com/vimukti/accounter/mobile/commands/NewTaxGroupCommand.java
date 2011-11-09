package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.requirements.NameRequirement;
import com.vimukti.accounter.mobile.requirements.TaxItemsTableRequirement;
import com.vimukti.accounter.web.client.core.ClientTAXGroup;
import com.vimukti.accounter.web.client.core.ClientTAXItem;

public class NewTaxGroupCommand extends NewAbstractCommand {

	private static final String TAX_ITEMS_LIST = "taxItemsList";
	private static final String NAME = "name";

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {
		list.add(new NameRequirement(NAME, getMessages().pleaseEnter(
				getConstants().taxGroup() + " " + getConstants().name()),
				getConstants().taxGroup(), false, true));
		list.add(new TaxItemsTableRequirement(TAX_ITEMS_LIST, getMessages()
				.pleaseSelect(getConstants().taxItemsList()), getConstants()
				.taxItemsList()) {

			@Override
			protected List<ClientTAXItem> getList() {
				// ArrayList<ClientTAXItem> taxItems = getClientCompany()
				// .getTaxItems();
				return null;// taxItems;
			}

		});
	}

	@Override
	protected Result onCompleteProcess(Context context) {
		String name = get(NAME).getValue();
		List<ClientTAXItem> taxItems = get(TAX_ITEMS_LIST).getValue();
		ClientTAXGroup taxGroup = new ClientTAXGroup();
		taxGroup.setName(name);
		taxGroup.setActive(true);
		taxGroup.setPercentage(true);
		taxGroup.setSalesType(true);
		taxGroup.setTaxItems(taxItems);
		create(taxGroup, context);
		return null;

	}

	@Override
	protected String getDetailsMessage() {
		return getMessages().readyToCreate(getConstants().taxGroup());
	}

	@Override
	public String getSuccessMessage() {
		return getMessages().createSuccessfully(getConstants().taxGroup());
	}

	@Override
	protected String getWelcomeMessage() {
		return getMessages().creating(getConstants().taxGroup());
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		return null;
	}

	@Override
	protected void setDefaultValues(Context context) {
	}
}
