package com.vimukti.accounter.mobile.requirements;

import java.util.List;

import com.vimukti.accounter.core.Unit;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.web.client.core.ClientQuantity;

public abstract class QuantityRequirement extends
		MultiRequirement<ClientQuantity> {

	private static final String VALUE = "itemvalue";
	private static final String UNIT = "itemunit";

	public QuantityRequirement(String requirementName, String enterString,
			String recordName) {
		super(requirementName, enterString, recordName, false, true);
		ClientQuantity clientQuantity = new ClientQuantity();
		clientQuantity.setValue(1.0);
		setDefaultValue(clientQuantity);
	}

	@Override
	protected void setDefaultValues() {
	}

	@Override
	protected void addRequirement(List<Requirement> list) {
		list.add(new AmountRequirement(VALUE, getMessages().pleaseEnter(
				getMessages().adjustmentQty()), getMessages().adjustmentQty()
				+ " " + getMessages().value(), false, true));

		list.add(new ListRequirement<Unit>(UNIT, getMessages().pleaseSelect(
				getMessages().unit()), getMessages().unit(), true, true, null) {

			@Override
			protected String getEmptyString() {
				return getMessages().youDontHaveAny(getMessages().units());
			}

			@Override
			protected String getSetMessage() {
				return null;
			}

			@Override
			protected Record createRecord(Unit value) {
				Record record = new Record(value);
				record.add(getMessages().unitName(), value.getType());
				return record;
			}

			@Override
			protected String getDisplayValue(Unit value) {
				return value == null ? " " : value.getType();
			}

			@Override
			protected void setCreateCommand(CommandList list) {
			}

			@Override
			protected String getSelectString() {
				return null;
			}

			@Override
			protected boolean filter(Unit e, String name) {
				return false;
			}

			@Override
			protected List<Unit> getLists(Context context) {
				return QuantityRequirement.this.getLists(context);
			}
		});
	}

	protected abstract List<Unit> getLists(Context context);

	@Override
	protected String getDisplayValue() {
		Unit clientUnit = getRequirement(UNIT).getValue();
		return getRequirement(VALUE).getValue() + " "
				+ (clientUnit == null ? " " : clientUnit.getType());
	}

	@Override
	protected Result onFinish(Context context) {
		ClientQuantity quantity = getValue();
		quantity.setUnit(((Unit) getRequirement(UNIT).getValue()).getID());
		quantity.setValue((Double) getRequirement(VALUE).getValue());
		return null;
	}

	public void setDefaultUnit(Unit defaultUnit) {
		getRequirement(UNIT).setValue(defaultUnit);
	}
}