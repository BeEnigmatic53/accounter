package com.vimukti.accounter.mobile.commands;

import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.requirements.NameRequirement;
import com.vimukti.accounter.mobile.utils.CommandUtils;
import com.vimukti.accounter.web.client.core.ClientShippingTerms;

public class NewShippingTermCommand extends NewAbstractCommand {

	protected static final String SHIPPING_TERMNAME = "shippingTermsName";
	protected static final String DESCRIPTION = "description";

	private ClientShippingTerms shippingTerm;

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {
		list.add(new NameRequirement(SHIPPING_TERMNAME,
				"Enter Shipping Term Name", "Shipping Term", false, true));
		list.add(new NameRequirement(DESCRIPTION, "Enter Description",
				"Description", true, true));
	}

	@Override
	protected String getDeleteCommand(Context context) {
		return shippingTerm != null ? "Delete ShippingTerm "
				+ shippingTerm.getID() : null;
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		if (isUpdate) {
			String string = context.getString();
			if (string.isEmpty()) {
				addFirstMessage(context, "Select a Shipping Term to update.");
				return "Shipping Terms";
			}
			ClientShippingTerms shippingTermsByName = CommandUtils
					.getshippingTermsByNameByName(context.getCompany(), string);
			if (shippingTermsByName == null) {
				addFirstMessage(context, "Select a Shipping Term to update.");
				return "Shipping Terms " + string.trim();
			}
			shippingTerm = shippingTermsByName;
			get(SHIPPING_TERMNAME).setValue(shippingTerm.getName());
			get(DESCRIPTION).setValue(shippingTerm.getDescription());
		} else {
			String string = context.getString();
			if (!string.isEmpty()) {
				get(SHIPPING_TERMNAME).setValue(string);
			}
			shippingTerm = new ClientShippingTerms();
		}
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return shippingTerm.getID() == 0 ? "Shipping Term Commond is activated"
				: "Update Shipping term command activated";
	}

	@Override
	protected String getDetailsMessage() {
		return shippingTerm.getID() == 0 ? "Shppping Term is ready to created with following details."
				: "Shipping term is ready to update with following values";
	}

	@Override
	protected void setDefaultValues(Context context) {
	}

	@Override
	public String getSuccessMessage() {
		return shippingTerm.getID() == 0 ? "New Shipping Term is Created Successfully"
				: "Shipping term updated successfully";
	}

	@Override
	protected Result onCompleteProcess(Context context) {
		shippingTerm.setName((String) get(SHIPPING_TERMNAME).getValue());
		shippingTerm.setDescription((String) get(DESCRIPTION).getValue());
		create(shippingTerm, context);
		markDone();
		return null;
	}
}
