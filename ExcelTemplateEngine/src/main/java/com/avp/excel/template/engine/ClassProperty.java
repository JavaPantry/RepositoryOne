package com.avp.excel.template.engine;

import ca.canon.fast.utils.GeneralUtil;

public class ClassProperty{
	private String className;
	private String propertyName;
	private IConvertor convertor;
	
	/* 
	 * TODO - <AP> exctract IConvertor from inputCellDescriptor
	 * very naive implementation 
	 * input: ca.canon.fast.web.sales.SalesMonthFctSpreadsheetController$ActualsDTO.userName
	 * className: ca.canon.fast.web.sales.SalesMonthFctSpreadsheetController$ActualsDTO
	 * propertyName:userName
	 * 
	 */
	public ClassProperty(String inputCellDescriptor) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String descriptors[] = inputCellDescriptor.split(",");
		String fullName = descriptors[0];
		fullName = fullName.substring(2, fullName.length()-1);
		int propertyStartIdx = fullName.lastIndexOf(".");
		className = fullName.substring(0, propertyStartIdx);
		propertyName = fullName.substring(propertyStartIdx+1);
		//TODO - <AP> exctract IConvertor from inputCellDescriptor
		if(descriptors.length>1 && !GeneralUtil.isEmpty(descriptors[1])){
			String convertorName = descriptors[1]; 
			convertorName = convertorName.substring(2, convertorName.length()-1);
			Class<?>  clazz = Class.forName(convertorName);
			convertor = (IConvertor) clazz.newInstance();
		}
	}
	public String getClassName() {return className;}//public void setClassName(String className) {this.className = className;}
	public String getPropertyName() {return propertyName;}//public void setPropertyName(String propertyName) {this.propertyName = propertyName;}
	public IConvertor getConvertor() {return convertor;}
	//public void setConvertor(IConvertor convertor) {this.convertor = convertor;}
	public String toString() {return "ClassProperty [className=" + className + ", propertyName=" + propertyName + "]";}
};