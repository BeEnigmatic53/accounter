package com.vimukti.accounter.web.client.ui.serverreports;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.Lists.OpenAndClosedOrders;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.reports.IFinanceReport;

public class PurchaseOpenOrderServerReport extends
		AbstractFinaneReport<OpenAndClosedOrders> {

	private String sectionName;

	private boolean isPurchases;

	public PurchaseOpenOrderServerReport(long startDate, long endDate,
			int generationType) {
		super(startDate, endDate, generationType);
		isPurchases = true;
	}

	public PurchaseOpenOrderServerReport(
			IFinanceReport<OpenAndClosedOrders> reportView) {
		this.reportView = reportView;
		isPurchases = true;
	}

	@Override
	public Object getColumnData(OpenAndClosedOrders record, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (record.getTransactionDate() != null)
				return getDateByCompanyType(record.getTransactionDate());
			else
				break;
		case 1:
			return record.getVendorOrCustomerName();
			// case 2:
			// // if (isPurchases)
			// return record.getDescription();
			// else
			// return record.getAmount();
			// case 2:
			// return ((Double) record.getQuantity()).toString();

		case 2:
			return record.getAmount();

		default:
			break;
		}
		return null;
	}

	@Override
	public int[] getColumnTypes() {
		// if (isPurchases)
		return new int[] { COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT,
				COLUMN_TYPE_AMOUNT };
		// else
		// return new int[] { COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT,
		// COLUMN_TYPE_AMOUNT };
	}

	@Override
	public String[] getColunms() {
		// if (isPurchases)
		return new String[] { getConstants().orderDate(),
				Global.get().vendor(),
				// FinanceApplication.constants().description(),
				// FinanceApplication.constants().quantity(),
				getConstants().amount() };
		// else
		// return new String[] {
		// FinanceApplication.constants().orderDate(),
		// FinanceApplication.constants().supplier(),
		// FinanceApplication.constants().value() };
	}

	@Override
	public String getTitle() {
		return getConstants().purchaseOrderReport();
	}

	@Override
	public void makeReportRequest(long start, long end) {
		// if (this.financeTool == null)
		// return;
		// resetVariables();
		// try {
		// onSuccess(this.financeTool.getOpenPurchaseOrders(start, end));
		// } catch (DAOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void makeReportRequest(int status, ClientFinanceDate start,
			ClientFinanceDate end) {
		// if (status == 1)
		// FinanceApplication.createReportService()
		// .getPurchaseOpenOrderReport(start.getTime(), end.getTime(),
		// this);
		// else if (status == 2)
		// FinanceApplication.createReportService()
		// .getPurchaseCompletedOrderReport(start.getTime(),
		// end.getTime(), this);
		// else if (status == 3)
		// FinanceApplication.createReportService()
		// .getPurchaseCancelledOrderReport(start.getTime(),
		// end.getTime(), this);
		// else
		// FinanceApplication.createReportService()
		// .getPurchaseOrderReport(start.getTime(),
		// end.getTime(), this);

	}

	@Override
	public void processRecord(OpenAndClosedOrders record) {

		int col;
		// if (isPurchases)
		col = 2;
		// else
		// col = 2;
		if (sectionDepth == 0) {
			addSection("", getConstants().total(), new int[] { col });
		} else if (sectionDepth == 1) {
			// First time
			this.sectionName = record.getVendorOrCustomerName();
			addSection(sectionName, getConstants().total(), new int[] { col });
		} else if (sectionDepth == 2) {
			// No need to do anything, just allow adding this record
			if (!sectionName.equals(record.getVendorOrCustomerName())) {
				endSection();
			} else {
				return;
			}

		}
		processRecord(record);
	}

	public void print() {
		// String gridhtml = grid.toString();
		// String headerhtml = grid.getHeader();
		// String footerhtml = grid.getFooter();
		//
		// gridhtml = gridhtml.replaceAll(headerhtml, "");
		// gridhtml = gridhtml.replaceAll(footerhtml, "");
		// headerhtml = headerhtml.replaceAll("td", "th");
		// headerhtml = headerhtml.substring(headerhtml.indexOf("<tr "),
		// headerhtml.indexOf("</tbody>"));
		// footerhtml = footerhtml.substring(footerhtml.indexOf("<tr>"),
		// footerhtml.indexOf("</tbody"));
		// footerhtml = footerhtml.replaceAll("<tr>",
		// "<tr class=\"listgridfooter\">");
		//
		// String firsRow = "<tr class=\"ReportGridRow\">"
		// + "" + "</tr>";
		// String lastRow = "<tr class=\"ReportGridRow\">"
		// + grid.rowFormatter.getElement(grid.getRowCount() - 1)
		// .getInnerHTML() + "</tr>";
		//
		// headerhtml = headerhtml + firsRow;
		// footerhtml = lastRow + footerhtml;
		//
		// gridhtml = gridhtml.replace(firsRow, headerhtml);
		// gridhtml = gridhtml.replace(lastRow, footerhtml);
		// gridhtml = gridhtml.replaceAll("<tbody>", "");
		// gridhtml = gridhtml.replaceAll("</tbody>", "");
		//
		// String dateRangeHtml =
		// "<div style=\"font-family:sans-serif;\"><strong>"
		// + this.getStartDate()
		// + " - "
		// + this.getEndDate() + "</strong></div>";
		//
		// generateReportPDF(this.getTitle(), gridhtml, dateRangeHtml);
	}

	@Override
	public ClientFinanceDate getEndDate(OpenAndClosedOrders obj) {
		return obj.getEndDate();
	}

	@Override
	public ClientFinanceDate getStartDate(OpenAndClosedOrders obj) {
		return obj.getStartDate();
	}

	@Override
	public int getColumnWidth(int index) {
		// if (isPurchases) {
		if (index == 0 || index == 2)
			return 200;

		// }
		// else {
		// if (index == 1)
		// return 300;
		// else if (index == 2)
		// return 200;
		// }
		else
			return -1;
	}

	public int sort(OpenAndClosedOrders obj1, OpenAndClosedOrders obj2, int col) {

		int ret = obj1.getVendorOrCustomerName().toLowerCase()
				.compareTo(obj2.getVendorOrCustomerName().toLowerCase());
		if (ret != 0) {
			return ret;
		}
		switch (col) {

		case 0:
			return obj1.getTransactionDate().compareTo(
					obj2.getTransactionDate());

		case 1:
			return obj1.getVendorOrCustomerName().toLowerCase()
					.compareTo(obj2.getVendorOrCustomerName().toLowerCase());

			// case 2:
			// // if (isPurchases)
			// return obj1.getDescription().toLowerCase().compareTo(
			// obj2.getDescription().toLowerCase());
			// else
			// return Utility_R
			// .compareDouble(obj1.getAmount(), obj2.getAmount());
			// case 2:
			// return Utility_R
			// .compareDouble(obj1.getQuantity(), obj2.getQuantity());

		case 2:
			return UIUtils.compareDouble(obj1.getAmount(), obj2.getAmount());

		}
		return 0;
	}

	public void resetVariables() {
		sectionDepth = 0;
		sectionName = "";
		super.resetVariables();
	}

	@Override
	public String[] getDynamicHeaders() {
		return new String[] { getConstants().orderDate(),
				getConstants().supplier(),
				// FinanceApplication.constants().description(),
				// FinanceApplication.constants().quantity(),
				getConstants().amount() };
	}

}
