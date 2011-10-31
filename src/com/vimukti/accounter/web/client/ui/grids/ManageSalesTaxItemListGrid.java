package com.vimukti.accounter.web.client.ui.grids;

import java.util.List;

import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientTAXAgency;
import com.vimukti.accounter.web.client.core.ClientTAXItem;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;

public class ManageSalesTaxItemListGrid extends BaseListGrid<ClientTAXItem> {

	public ManageSalesTaxItemListGrid(boolean isMultiSelectionEnable) {
		super(isMultiSelectionEnable);
	}

	@Override
	protected void executeDelete(ClientTAXItem object) {
		deleteObject(object);
	}

	@Override
	protected Object getColumnValue(ClientTAXItem obj, int index) {
		switch (index) {
		case 0:
			return obj.getName() != null ? obj.getName() : "";
		case 1:
			return obj.getDescription() != null ? obj.getDescription() : "";
		case 2:
			if (obj.isPercentage())
				return obj.getTaxRate() + "%";
			else
				return amountAsString(
						obj.getTaxRate(),
						getCompany().getCurrency(
								getCompany().getPreferences()
										.getPrimaryCurrency()));
		case 3:
			ClientTAXAgency agency = null;
			if (obj.getTaxAgency() != 0) {
				agency = getCompany().getTaxAgency(obj.getTaxAgency());
			}
			return agency != null ? agency.getName() : "";
		case 4:
			return Accounter.getFinanceMenuImages().delete();
		}
		return "";
	}

	@Override
	public void onDoubleClick(ClientTAXItem obj) {
		if (Accounter.getUser().canDoInvoiceTransactions()) {
			ActionFactory.getNewVatItemAction().run(obj, false);
		}

	}

	@Override
	protected String[] getColumns() {
		return new String[] { Accounter.constants().taxItem(),
				Accounter.constants().description(),
				Accounter.constants().taxRates(),
				Accounter.constants().taxAgency(), "" };
	}

	@Override
	protected int[] setColTypes() {
		return new int[] { ListGrid.COLUMN_TYPE_TEXT,
				ListGrid.COLUMN_TYPE_TEXT, ListGrid.COLUMN_TYPE_DECIMAL_TEXT,
				ListGrid.COLUMN_TYPE_TEXT, ListGrid.COLUMN_TYPE_IMAGE };
	}

	@Override
	protected int getCellWidth(int index) {
		switch (index) {
		case 0:
			return 300;
		case 2:
			return 105;
		case 3:
			return 175;
		case 4:
			return 25;
		}
		return -1;
	}

	public AccounterCoreType getType() {
		return AccounterCoreType.TAXITEM;
	}

	@Override
	protected void onClick(ClientTAXItem obj, int row, int col) {
		if (!Accounter.getUser().canDoInvoiceTransactions())
			return;
		List<ClientTAXItem> records = getRecords();
		if (col == 4)
			showWarnDialog(records.get(row));
	}

	private String getTaxAgencyID(ClientTAXItem obj) {

		ClientTAXAgency agency = null;
		if (obj.getTaxAgency() != 0) {
			if (getPreferences().isTrackTax()) {
				agency = getCompany().getTaxAgency(obj.getTaxAgency());
			}
		}
		return agency != null ? agency.getName() : "";

	}

	@Override
	protected int sort(ClientTAXItem obj1, ClientTAXItem obj2, int index) {
		switch (index) {
		case 1:
			return obj1.getName().toLowerCase()
					.compareTo(obj2.getName().toLowerCase());

		case 2:
			String desc1 = obj1.getDescription() != null ? obj1
					.getDescription() : "";
			String desc2 = obj2.getDescription() != null ? obj2
					.getDescription() : "";
			;
			return desc1.toLowerCase().compareTo(desc2.toLowerCase());
		case 3:
			Double rate1 = obj1.getTaxRate();
			Double rate2 = obj2.getTaxRate();
			return rate1.compareTo(rate2);
		case 4:
			String agency1 = null;
			String agency2 = null;
			// if (FinanceApplication.getCompany().getAccountingType() ==
			// ClientCompany.ACCOUNTING_TYPE_US) {
			// agency1 = getTaxAgency(obj1);
			// agency2 = getTaxAgency(obj2);
			// }
			// agency1 = getTaxAgencyID(obj1);
			// agency2 = getTaxAgencyID(obj2);
			return agency1.toLowerCase().compareTo(agency2.toLowerCase());

		}
		return 0;
	}
}
