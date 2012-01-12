package com.vimukti.accounter.web.client.core;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.ui.Accounter;

public class ClientTDSChalanDetail extends ClientTransaction {

	/**
	 * this class is used to maintain the details of chalan
	 */
	private static final long serialVersionUID = 1L;

	private double incomeTaxAmount;
	private double surchangePaidAmount;
	private double educationCessAmount;
	private double interestPaidAmount;
	private double penaltyPaidAmount;
	private double otherAmount;

	private String paymentSection;
	private long bankChalanNumber;
	private long checkNumber;
	private String bankBsrCode;

	private boolean bookEntry;
	private long dateTaxPaid;
	private int chalanPeriod;
	private long chalanSerialNumber;
	private int formType;
	private int assesmentYearStart;
	private int assessmentYearEnd;

	private long payFrom;

	private boolean etdsFillingDone;
	private String etdsfillingAcknowledgementNo;

	private List<ClientTDSTransactionItem> tdsTransactionItems = new ArrayList<ClientTDSTransactionItem>();

	@Override
	public String getName() {
		return Accounter.getMessages().tdsChallan();
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public AccounterCoreType getObjectType() {
		return AccounterCoreType.TDSCHALANDETAIL;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}

	@Override
	public long getID() {
		return id;
	}

	public double getIncomeTaxAmount() {
		return incomeTaxAmount;
	}

	public void setIncomeTaxAmount(double incomeTaxAmount) {
		this.incomeTaxAmount = incomeTaxAmount;
	}

	public double getSurchangePaidAmount() {
		return surchangePaidAmount;
	}

	public void setSurchangePaidAmount(double surchangePaidAmount) {
		this.surchangePaidAmount = surchangePaidAmount;
	}

	public double getEducationCessAmount() {
		return educationCessAmount;
	}

	public void setEducationCessAmount(double educationCessAmount) {
		this.educationCessAmount = educationCessAmount;
	}

	public double getInterestPaidAmount() {
		return interestPaidAmount;
	}

	public void setInterestPaidAmount(double interestPaidAmount) {
		this.interestPaidAmount = interestPaidAmount;
	}

	public double getPenaltyPaidAmount() {
		return penaltyPaidAmount;
	}

	public void setPenaltyPaidAmount(double penaltyPaidAmount) {
		this.penaltyPaidAmount = penaltyPaidAmount;
	}

	public double getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(double otherAmount) {
		this.otherAmount = otherAmount;
	}

	public String getPaymentSection() {
		return paymentSection;
	}

	public void setPaymentSection(String paymentSectionSelected) {
		this.paymentSection = paymentSectionSelected;
	}

	public long getBankChalanNumber() {
		return bankChalanNumber;
	}

	public void setBankChalanNumber(long bankChalanNumber) {
		this.bankChalanNumber = bankChalanNumber;
	}

	public long getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(long checkNumber) {
		this.checkNumber = checkNumber;
	}

	public boolean isBookEntry() {
		return bookEntry;
	}

	public void setBookEntry(boolean bookEntry) {
		this.bookEntry = bookEntry;
	}

	/**
	 * @return the bankBsrCode
	 */
	public String getBankBsrCode() {
		return bankBsrCode;
	}

	/**
	 * @param bankBsrCode
	 *            the bankBsrCode to set
	 */
	public void setBankBsrCode(String bankBsrCode) {
		this.bankBsrCode = bankBsrCode;
	}

	public long getDateTaxPaid() {
		return dateTaxPaid;
	}

	public void setDateTaxPaid(long dateTaxPaid) {
		this.dateTaxPaid = dateTaxPaid;
	}

	public int getChalanPeriod() {
		return chalanPeriod;
	}

	public void setChalanPeriod(int chalanPeriod) {
		this.chalanPeriod = chalanPeriod;
	}

	public long getChalanSerialNumber() {
		return chalanSerialNumber;
	}

	public void setChalanSerialNumber(long chalanSerialNumber) {
		this.chalanSerialNumber = chalanSerialNumber;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

	public int getAssesmentYearStart() {
		return assesmentYearStart;
	}

	public void setAssesmentYearStart(int assesmentYearStart) {
		this.assesmentYearStart = assesmentYearStart;
	}

	public int getAssessmentYearEnd() {
		return assessmentYearEnd;
	}

	public void setAssessmentYearEnd(int assessmentYearEnd) {
		this.assessmentYearEnd = assessmentYearEnd;
	}

	public List<ClientTDSTransactionItem> getTdsTransactionItems() {
		return tdsTransactionItems;
	}

	public void setTdsTransactionItems(
			List<ClientTDSTransactionItem> tdsTransactionItems) {
		this.tdsTransactionItems = tdsTransactionItems;
	}

	public long getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(long payFrom) {
		this.payFrom = payFrom;
	}

	public boolean isEtdsFillingDone() {
		return etdsFillingDone;
	}

	public void setEtdsFillingDone(boolean etdsFillingDone) {
		this.etdsFillingDone = etdsFillingDone;
	}

	public String getEtdsfillingAcknowledgementNo() {
		return etdsfillingAcknowledgementNo;
	}

	public void setEtdsfillingAcknowledgementNo(
			String etdsfillingAcknowledgementNo) {
		this.etdsfillingAcknowledgementNo = etdsfillingAcknowledgementNo;
	}
}
