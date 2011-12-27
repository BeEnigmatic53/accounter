package com.vimukti.accounter.core;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.core.Utility;

/**
 * class used to generate the eTDS filling text file
 * 
 * @author vimukti8
 * 
 */
public class ETDSAnnexuresGenerator {

	private String finalString;

	public ETDSAnnexuresGenerator() {

	}

	/**
	 * when a new Line is started Each Record (including last record) must start
	 * on new line
	 */
	String startNewLine() {
		return "\n";
	}

	/**
	 * line end must end with a newline character. Hex Values : "0D" & "0A".
	 */
	String endLine() {
		return "\n";
	}

	/**
	 * This is a ^ delimited variable field width file. This means that in case
	 * of empty spaces there is no need to provide leading '0' for numerals and
	 * trailing spaces for character fields.
	 */
	String addDelimiter() {
		return "^";
	}

	/**
	 * add the line number before each line
	 * 
	 * @return
	 */
	String addLineNumber() {
		return null;
	}

	/**
	 * Generate header for the file
	 */
	String generateFileHeaderRecord() {

		String headerString;
		// line number
		headerString = "1";

		// "FH" signifying 'File Header'
		headerString = headerString + "FH" + addDelimiter();

		// Value should be "NS1" .
		headerString = headerString + "NS1" + addDelimiter();

		// Value should be R
		headerString = headerString + "R" + addDelimiter();

		headerString = headerString + addTodaysDate() + addDelimiter();

		// Indicates the running sequencenumber for the file. (Should be unique
		// across all the files)
		headerString = headerString + "1" + addDelimiter();

		// Value should be D
		headerString = headerString + "D" + addDelimiter();

		// TAN of Deductor
		headerString = headerString + getTanofDeductor() + addDelimiter();

		// Indicates the number of batches that the file contains.
		headerString = headerString + "1" + addDelimiter();

		// Name of the software used for preparing theQuarterly
		// e-TDS/TCSstatement should be mentioned.
		headerString = headerString + softwareUsedName() + addDelimiter();

		// Record Hash (Not applicable)
		headerString = headerString + addDelimiter();

		// FVU Version (Not applicable)
		headerString = headerString + addDelimiter();

		// File Hash (Not applicable)
		headerString = headerString + addDelimiter();

		// Sam Version (Not applicable)
		headerString = headerString + addDelimiter();

		// SAM Hash (Not applicable)
		headerString = headerString + addDelimiter();

		// SCM Version (Not applicable)
		headerString = headerString + addDelimiter();

		// SCM Hash (Not applicable)
		headerString = headerString + addDelimiter();

		return headerString;
	}

	private String softwareUsedName() {
		return "NSDLRPU2.4";
	}

	private String getTanofDeductor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention the date o fcreation of the file in ddmmyyyy format.
	 * 
	 * @return
	 */
	private String addTodaysDate() {
		FinanceDate date = new FinanceDate();
		return date.toString();
	}

	/**
	 * generate the Batch Header for the file
	 * 
	 * @return
	 */
	String generateBatchHeaderRecord() {

		String batchHeaderString = null;

		// Running Sequence Number for each line in the file
		batchHeaderString = batchHeaderString + "2" + addDelimiter();

		// Value should be "BH" (Batch Header) for the batch header record
		batchHeaderString = batchHeaderString + "BH" + addDelimiter();

		// Batch Number Value must start with 1.
		batchHeaderString = batchHeaderString + "1" + addDelimiter();

		batchHeaderString = batchHeaderString + getChalanCount()
				+ addDelimiter();

		// Form Number Value must be 26Q.
		batchHeaderString = batchHeaderString + "26Q" + addDelimiter();

		// Transaction Type (Not applicable) NA 0 O No value should be specified
		// Batch Updation Indicator (Not applicable) NA 0 O No value should be
		// specified
		// Original RRR No. (Provisional Receipt Number of REGULAR File) - (Not
		// applicable) NA 0 O No value should be specified
		// Previous RRR Number (Not applicable) NA 0 O No value should be
		// specified
		// RRR Number (Provisional Receipt Number)- (Not applicable) NA 0 O No
		// value should be specified
		// RRR Date (provisional Receipt Date) - (Not applicable) NA 0 O No
		// value should be specified
		// Last TAN of Deductor / Collector ( Used for Verification) (Not
		// applicable) NA 0 O No value should be specified
		batchHeaderString = batchHeaderString + addDelimiter() + addDelimiter()
				+ addDelimiter() + addDelimiter() + addDelimiter()
				+ addDelimiter() + addDelimiter();

		batchHeaderString = batchHeaderString + getTanOfDeductor()
				+ addDelimiter();

		// No value should be specified
		batchHeaderString = batchHeaderString + addDelimiter();

		batchHeaderString = batchHeaderString + getPanOfDeductor()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getAssesmentYear()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getFinancialYear()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getYearQuarter()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getNameDeductor()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorBranch()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorAddress(1)
				+ addDelimiter();
		batchHeaderString = batchHeaderString + getDeductorAddress(2)
				+ addDelimiter();
		batchHeaderString = batchHeaderString + getDeductorAddress(3)
				+ addDelimiter();
		batchHeaderString = batchHeaderString + getDeductorAddress(4)
				+ addDelimiter();
		batchHeaderString = batchHeaderString + getDeductorAddress(5)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getState() + addDelimiter();

		// PIN Code of Deductor
		batchHeaderString = batchHeaderString + getDeductorPINCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorEmailID()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorSTDCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorTelephoneNumber()
				+ addDelimiter();

		batchHeaderString = batchHeaderString
				+ getResponsiblePersonAddressChange() + addDelimiter();

		batchHeaderString = batchHeaderString + getTotalDepositeAmount()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDeductorType()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonName()
				+ addDelimiter();

		batchHeaderString = batchHeaderString
				+ getResponsiblePersonDesignation() + addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonAddress(1)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonAddress(2)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonAddress(3)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonAddress(4)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonAddress(5)
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonState()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonPin()
				+ addDelimiter();

		batchHeaderString = batchHeaderString
				+ getResponsiblePersonValidEmailID() + addDelimiter();

		batchHeaderString = batchHeaderString
				+ getResponsiblePersonMobileNumber() + addDelimiter();

		batchHeaderString = batchHeaderString + getResponsiblePersonSTDCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString
				+ getResponisblePersonTelephoenNumebr() + addDelimiter();

		batchHeaderString = batchHeaderString
				+ getAddressChangeOFResponsiblePerson() + addDelimiter();

		batchHeaderString = batchHeaderString + getTotalDespositeAmount()
				+ addDelimiter();

		// Unmatched challan count
		// Count of Salary Details Records (Not applicable)
		// Batch Total of - Gross Total Income as per Salary Detail (Not
		// applicable)
		batchHeaderString = batchHeaderString + addDelimiter() + addDelimiter()
				+ addDelimiter();

		// AO Approval Value should be "N"
		batchHeaderString = batchHeaderString + "N" + addDelimiter();

		// AO Approval Number CHAR 15 O No value should be specified
		// Last Deductor Type CHAR 1 NA No value should be specified
		batchHeaderString = batchHeaderString + addDelimiter();
		batchHeaderString = batchHeaderString + addDelimiter();

		batchHeaderString = batchHeaderString + getGovtSateName()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getGovtPAOCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getGovtDDOCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getGovtMinistryName()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getMinistryOtherName()
				+ addDelimiter();

		// Filler 2
		batchHeaderString = batchHeaderString + addDelimiter();

		batchHeaderString = batchHeaderString + getGovtPAORegistrationCode()
				+ addDelimiter();

		batchHeaderString = batchHeaderString + getDDORegistationCode()
				+ addDelimiter();

		// Record Hash (Not applicable)
		batchHeaderString = batchHeaderString + addDelimiter();
		return batchHeaderString;
	}

	/**
	 * Optional for deductor type Central Govt. (A), State Govt. (S), Statutory
	 * Body - Central Govt. (D), Statutory Body - State Govt. (E), Autonomous
	 * body - Central Govt. (G), Autonomous body - State Govt. (H), Local
	 * Authority - Central Govt. (L) & Local Authority - State Govt. (N). For
	 * other deductor type no value should be provided.
	 * 
	 * @return
	 */
	private String getDDORegistationCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Optional for deductor type Central Govt. (A), State Govt. (S), Statutory
	 * Body - Central Govt. (D), Statutory Body - State Govt. (E), Autonomous
	 * body - Central Govt. (G), Autonomous body - State Govt. (H), Local
	 * Authority - Central Govt. (L) & Local Authority - State Govt. (N). For
	 * other deductor type no value should be provided.
	 * 
	 * @return
	 */
	private String getGovtPAORegistrationCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * If numeric code '99' (i.e. Other) is provided in Ministry Name field then
	 * value in Ministry Name "Other" field should be provided
	 * 
	 * @return
	 */
	private String getMinistryOtherName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Numeric code for Ministry name should be provided. For list of Ministry
	 * name codes, refer to the Annexure 3 below. Mandatory for deductor type
	 * Central Govt (A), Statutory body - Central Govt. (D) & Autonomous body -
	 * Central Govt. (G). Optional for deductor type Statutory body - State
	 * Govt. (E), Autonomous body - State Govt. (H), Local Authority - Central
	 * Govt. (L) & Local Authority -State Govt. (N). For other deductor type no
	 * value should be provided.
	 * 
	 * @return
	 */
	private String getGovtMinistryName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mandatory for deductor type Central Government (A). Optional for deductor
	 * type State Government (S), Statutory body - Central Govt. (D), Statutory
	 * body - State Govt. (E), Autonomous body - Central Govt. (G), Autonomous
	 * body - State Govt. (H), Local Authority -Central Govt. (L) & Local
	 * Authority - State Govt. (N). For other deductor type no value should be
	 * provided.
	 * 
	 * @return
	 */
	private String getGovtDDOCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mandatory for central govt (A). Optional for deductor type State Govt.
	 * (S), Statutory body - Central Govt. (D), Statutory body - State Govt.
	 * (E), Autonomous body - Central Govt. (G), Autonomous body - State Govt.
	 * (H), Local Authority - Central Govt. (L) & Local Authority - State Govt.
	 * (N). For other deductor type no value should be provided.
	 * 
	 * @return
	 */
	private String getGovtPAOCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Numeric code for state should be mentioned as per Annexure 5. Mandatory
	 * if deductor type is State Govt. (code S), Statutory body - State Govt.
	 * (code E), Autonomous body - State Govt. code H) and Local Authority -
	 * State Govt. (code N). For other deductor category no value should be
	 * provided.
	 * 
	 * @return
	 */
	private String getGovtSateName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention the Total of Deposit Amount as per Challan.The value here should
	 * be same as sum of values in field 'Total of Deposit Amount as per
	 * Challan' in the 'Challan Detail' record ( please refer to the Challan
	 * Detail' record section below ). Paisa Field (Decimal Value) of the Amount
	 * must be 00.
	 * 
	 * @return
	 */
	private String getTotalDespositeAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Change of Address of Responsible person since last Return CHAR 1 M "Y" if
	 * address has changed after filing last return, "N" otherwise.
	 * 
	 * @return
	 */
	private String getAddressChangeOFResponsiblePerson() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention telephone number if value present in field no.44 (Responsible
	 * Person's STD code). Either mobile no. should be provided or Telephone no.
	 * and STD code of deductor or responsible person should be provided.
	 * 
	 * @return
	 */
	private String getResponisblePersonTelephoenNumebr() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention STD code if value present in field no.45 (Responsible Person's
	 * Tel-Phone No.).
	 * 
	 * @return
	 */
	private String getResponsiblePersonSTDCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention 10 digit mobile no. Mandatory for Deductor category other than
	 * Central Govt. and State Govt. For deductor category Central Govt. and
	 * State Govt. either mobile no. should be provided or Telephone no. and STD
	 * code of deductor or responsible person should be provided.
	 * 
	 * @return
	 */
	private String getResponsiblePersonMobileNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Valid E-mail should be provided. 1. Email format must be checked -atleast @
	 * and '.' should be mentioned. 2. Both @ and '.' should be preceded and
	 * succeeded by atleast one character. 3. At least one '.' should come after
	 * '@'. 4. All printable characters allowed except '^' and space. E-mail id
	 * of deductor/collector or person responsible for deducting/collecting tax
	 * should be provided.
	 * 
	 * @return
	 */
	private String getResponsiblePersonValidEmailID() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Responsible Person's PIN INTEGER 6 M PIN Code of Responsible Person .
	 * 
	 * @return
	 */
	private String getResponsiblePersonPin() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Responsible Person's State INTEGER 2 M Numeric code for state. For list
	 * of State codes, refer to the Annexure below.
	 * 
	 * @return
	 */
	private String getResponsiblePersonState() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Responsible Person's Address1 CHAR 25 M Mention the address of the
	 * responsible Person . Responsible Person's Address2 CHAR 25 O Length <= 25
	 * . Responsible Person's Address3 CHAR 25 O Length <= 25 . Responsible
	 * Person's Address4 CHAR 25 O Length <= 25 . Responsible Person's Address5
	 * CHAR 25 O Length <= 25 .
	 * 
	 * @param i
	 * @return
	 */
	private String getResponsiblePersonAddress(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 34 Designation of the Person responsible for Deduction CHAR 20 M Mention
	 * the designation of Person responsible.
	 * 
	 * @return
	 */
	private String getResponsiblePersonDesignation() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Name of Person responsible for Deduction CHAR 75 M Mention the Name of
	 * Person responsible for Deduction on behalf of the deductor.
	 * 
	 * @return
	 */
	private String getResponsiblePersonName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Deductor Type CHAR 1 M Deductor category code to be mentioned as per
	 * Annexure 4
	 * 
	 * @return
	 */
	private String getDeductorType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Batch Total of - Total of Deposit Amount as per Challan INTEGER 15 M
	 * Mention the Total of Deposit Amount as per Challan.The value here should
	 * be same as sum of values in field 'Total of Deposit Amount as per
	 * Challan' in the 'Challan Detail' record ( please refer to the Challan
	 * Detail' record section below ). Paisa Field (Decimal Value) of the Amount
	 * must be 00.
	 * 
	 * @return
	 */
	private String getTotalDepositeAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Change of Address of Responsible person since last Return CHAR 1 M "Y" if
	 * address has changed after filing last return, "N" otherwise.
	 * 
	 * @return
	 */
	private String getResponsiblePersonAddressChange() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention telephone number if value present in field no.29 (Employer /
	 * Deductor's STD code). Either mobile no. should be provided or Telephone
	 * no. and STD code of deductor or responsible person should be provided.
	 * 
	 * @return
	 */
	private String getDeductorTelephoneNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention STD code if value present in field no.30 (Employer / Deductor's
	 * Tel-Phone No.).
	 * 
	 * @return
	 */
	private String getDeductorSTDCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Valid E-mail should be provided. 1. Email format must be checked -atleast @
	 * and '.' should be mentioned. 2. Both @ and '.' should be preceded and
	 * succeeded by atleast one character. 3. At least one '.' should come after
	 * '@'. 4. All printable characters allowed except '^' and space. E-mail id
	 * of deductor/collector or person responsible for deducting/collecting tax
	 * should be provided.
	 * 
	 * @return
	 */
	private String getDeductorEmailID() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDeductorPINCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Numeric code for state. For list of State codes, refer to the Annexure 1
	 * below.
	 * 
	 * @return
	 */
	private String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention the address of the Deductor. Length <= 25. Length <= 25. Length
	 * <= 25. Length <= 25.
	 * 
	 * @param i
	 * @return
	 */
	private String getDeductorAddress(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Branch/Division of Deductor its optional
	 * 
	 * @return
	 */
	private String getDeductorBranch() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Mention the Name of the Deductor I.e. Deductor who deducts tax.
	 * 
	 * @return
	 */
	private String getNameDeductor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Valid values Q1, Q2, Q3, Q4 of the financial Year.
	 * 
	 * @return
	 */
	private String getYearQuarter() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Financial year e.g. value should be 200506 for Financial Yr 2005-06.
	 * 'Assessment year' - 'Financial Year' must be = 1. The financial Year
	 * cannot be a future financial year
	 */
	private String getFinancialYear() {

		return null;
	}

	/**
	 * Assessment year e.g. value should be 200607 for assessment yr 2006-07 ` *
	 * 
	 * @return
	 */
	private String getAssesmentYear() {

		return null;
	}

	/**
	 * Mandatory to mention the PAN of the Deductor. If deductor is not //
	 * required to have a PAN mention PANNOTREQD
	 * 
	 * @return
	 */
	private String getPanOfDeductor() {
		return null;
	}

	/**
	 * Mention the 10 Character TAN of the deductor. Should be all CAPITALS.
	 * 
	 * @return
	 */
	private String getTanOfDeductor() {
		// Mention the 10 Character TAN of the deductor. Should be all CAPITALS.

		return null;
	}

	/**
	 * Count of total number of challans/transfer vouchers contained within the
	 * batch.
	 * 
	 * @return
	 */
	private String getChalanCount() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * returns the state code according to the state name provided
	 * 
	 * @param stateName
	 * @return
	 */
	String getStateCode(String stateName) {

		String stateCode = "1";
		List<String> statesName = new ArrayList<String>();
		statesName = Utility.getStatesList();
		int code = 1;
		for (String string : statesName) {
			if (string.equals(stateName)) {
				stateCode = Integer.toString(code);
			} else if (string.equals("OTHERS")) {
				stateCode = Integer.toString(99);
			}
			code++;
		}
		return stateCode;

	}

	/**
	 * Returns the section code depending on the section name.
	 * 
	 * @param sectionName
	 * @return
	 */
	String getSectionCode(String sectionName) {

		List<String> sectionNamesList = new ArrayList<String>();
		sectionNamesList = Utility.getSectionNames();

		String codeReturned = null;

		List<String> sectionCodesList = new ArrayList<String>();
		sectionCodesList = Utility.getSectionCodes();

		for (int i = 0; i < sectionNamesList.size(); i++) {
			if (sectionName.equals(sectionNamesList.get(i))) {
				codeReturned = sectionCodesList.get(i);
			}
		}
		return codeReturned;
	}

	/**
	 * this returns the ministry code depending on the ministry name
	 * 
	 * @param sectionName
	 * @return
	 */
	String getMinistryCode(String ministryName) {

		String ministryCode = "1";
		List<String> ministryNameList = new ArrayList<String>();
		ministryNameList = Utility.getMinistryType();
		int code = 1;
		for (String string : ministryNameList) {
			if (string.equals(ministryName)) {
				ministryCode = Integer.toString(code);
			} else if (string.equals("Others")) {
				ministryCode = Integer.toString(99);
			}
			code++;
		}
		return ministryCode;
	}

	/**
	 * returns the deductor value depeding on the deductor type
	 * 
	 * @param sectionName
	 * @return
	 */
	String getDeductorValue(String sectionName) {
		return sectionName;
	}

}
