package ca.canon.fast.model.impl;


@SuppressWarnings("serial")
public class SalesUploadWeekFct extends BaseAuditEntity{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actualType == null) ? 0 : actualType.hashCode());
		result = prime * result + ((billTo == null) ? 0 : billTo.hashCode());
		result = prime * result
				+ ((itemCode == null) ? 0 : itemCode.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((sellTo == null) ? 0 : sellTo.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SalesUploadWeekFct other = (SalesUploadWeekFct) obj;
		if (actualType == null) {
			if (other.actualType != null)
				return false;
		} else if (!actualType.equals(other.actualType))
			return false;
		if (billTo == null) {
			if (other.billTo != null)
				return false;
		} else if (!billTo.equals(other.billTo))
			return false;
		if (itemCode == null) {
			if (other.itemCode != null)
				return false;
		} else if (!itemCode.equals(other.itemCode))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (sellTo == null) {
			if (other.sellTo != null)
				return false;
		} else if (!sellTo.equals(other.sellTo))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	private Boolean oddity; 
	private Integer year;
	private Integer month;
	private String	itemCode;	
	private String	billTo;
	private String	sellTo;
	private String	errorMessage;
	private int 	dollar1;
	private int 	unit1;
	private int 	dollar2;
	private int 	unit2;
	private int 	dollar3;
	private int 	unit3;
	private int 	dollar4;
	private int 	unit4;
	private int 	dollar5;
	private int 	unit5;
	private String	actualType;
	
	public SalesUploadWeekFct() {
		super();
		dollar1 = dollar2 = dollar3 = dollar4 = dollar5 
		= unit1 = unit2 = unit3 = unit4 = unit5 = 0;
	}

	public Integer getYear() {return year;}
	public void setYear(Integer year) {this.year = year;}
	public Integer getMonth() {return month;}
	public void setMonth(Integer month) {this.month = month;}
	public String getActualType() {return actualType;}
	public void setActualType(String actualType) {this.actualType = actualType;}
	public Boolean getOddity() {return oddity;}
	public void setOddity(Boolean oddity) {this.oddity = oddity;}
	public int getDollar1() {return dollar1;}
	public void setDollar1(int dollar1) {this.dollar1 = dollar1;}
	public int getUnit1() {return unit1;}
	public void setUnit1(int unit1) {this.unit1 = unit1;}
	public int getDollar2() {return dollar2;}
	public void setDollar2(int dollar2) {this.dollar2 = dollar2;}
	public int getUnit2() {return unit2;}
	public void setUnit2(int unit2) {this.unit2 = unit2;}
	public int getDollar3() {return dollar3;}
	public void setDollar3(int dollar3) {this.dollar3 = dollar3;}
	public int getUnit3() {return unit3;}
	public void setUnit3(int unit3) {this.unit3 = unit3;}
	public int getDollar4() {return dollar4;}
	public void setDollar4(int dollar4) {this.dollar4 = dollar4;}
	public int getUnit4() {return unit4;}
	public void setUnit4(int unit4) {this.unit4 = unit4;}
	public int getDollar5() {return dollar5;}
	public void setDollar5(int dollar5) {this.dollar5 = dollar5;}
	public int getUnit5() {return unit5;}
	public void setUnit5(int unit5) {this.unit5 = unit5;}

	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesUploadWeekFct [year=").append(year)
				.append(", month=").append(month).append(", itemCode=").append(itemCode)
				.append(", sellTo=").append(sellTo)
				.append(", billTo=").append(billTo)
				.append(", errorMessage=").append(errorMessage)
				.append(", dollar1=").append(dollar1).append(", unit1=").append(unit1)
				.append(", dollar2=").append(dollar2).append(", unit2=")
				.append(unit2).append(", dollar3=").append(dollar3)
				.append(", unit3=").append(unit3).append(", dollar4=")
				.append(dollar4).append(", unit4=").append(unit4)
				.append(", dollar5=").append(dollar5).append(", unit5=")
				.append(unit5).append(", actualType=").append(actualType)
				.append("]");
		return builder.toString();
	}

	public String getItemCode() {return itemCode;}
	public void setItemCode(String itemCode) {this.itemCode = itemCode;}
	public String getBillTo() {return billTo;}
	public void setBillTo(String billTo) {this.billTo = billTo;}
	public String getSellTo() {return sellTo;}
	public void setSellTo(String sellTo) {this.sellTo = sellTo;}
	public String getErrorMessage() {return errorMessage;}
	public void setErrorMessage(String errorMessage) {this.errorMessage = errorMessage;}
}
