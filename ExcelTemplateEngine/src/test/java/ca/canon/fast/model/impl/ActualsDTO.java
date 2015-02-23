package ca.canon.fast.model.impl;

public class ActualsDTO{
	private String	userName;
	private String	itemCode;	
	private String	billTo;
	private String	sellTo;
	
	private int 	year;
	private int 	month;
	
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
				.append(", month=").append(month).append(", actualType=").append(actualType).append("]");
		return builder.toString();
	}


}

