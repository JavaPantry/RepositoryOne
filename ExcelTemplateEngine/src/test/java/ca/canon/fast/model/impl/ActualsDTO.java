package ca.canon.fast.model.impl;

public class ActualsDTO{
	private String	userName;
	private String	itemCode;	
	private String	billTo;
	private String	sellTo;
	
	private int 	year;
	private int 	month;
	
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
	private String  actualType;
	public ActualsDTO(){}

	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}
	public String getItemCode() {return itemCode;}
	public void setItemCode(String itemCode) {this.itemCode = itemCode;}
	public int getYear() {return year;}
	public void setYear(int year) {this.year = year;}
	public int getMonth() {return month;}
	public void setMonth(int month) {this.month = month;}
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
	public String getActualType() {return actualType;}
	public void setActualType(String actualType) {this.actualType = actualType;}
	public String getBillTo() {	return billTo;}
	public void setBillTo(String billTo) {this.billTo = billTo;}
	public String getSellTo() {return sellTo;}
	public void setSellTo(String sellTo) {this.sellTo = sellTo;}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActualsDTO [userName=").append(userName)
				.append(", itemCode=").append(itemCode)
				.append(", billTo=").append(billTo)
				.append(", sellTo=").append(sellTo)
				.append(", year=").append(year)
				.append(", month=").append(month).append(", dollar1=")
				.append(dollar1).append(", unit1=").append(unit1)
				.append(", dollar2=").append(dollar2).append(", unit2=")
				.append(unit2).append(", dollar3=").append(dollar3)
				.append(", unit3=").append(unit3).append(", dollar4=")
				.append(dollar4).append(", unit4=").append(unit4)
				.append(", dollar5=").append(dollar5).append(", unit5=")
				.append(unit5).append(", actualType=").append(actualType).append("]");
		return builder.toString();
	}


}

