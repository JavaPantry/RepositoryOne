package com.avp.excel.template.engine;

import ca.canon.fast.utils.GeneralUtil;
/**
 * ClassProperty - cell descriptor
 * input:
 * cell content = ${propertyName:"ca.canon.fast.model.impl.SalesUploadWeekFct.itemCode",referencedEntity:"ca.canon.fast.model.impl.ActualDTO.id"
 * 					,convertorClassName:"ca.canon.fast.model.impl.MonthToIntegerConvertor"}
 * OR without foreign key reference
 * cell content = ${propertyName:"ca.canon.fast.model.impl.SalesUploadWeekFct.itemCode",convertorClassName:"ca.canon.fast.model.impl.MonthToIntegerConvertor"}
 * 
 * OR with default class in table descriptor, no converter and no foreign key reference
 * cell content =  ${propertyName:"itemCode"}
 * @author ptitchkin
 */
public class ClassProperty extends Descriptor{
	private String		className; // used as key
	private String		propertyName;
	private String		referencedEntity;
	private String		convertorClassName;
	private IConvertor	convertor;
	
	/** 
	 * initialize cell descriptor by assigning default class to property and instantiate cell value converter if provided 									
	 */
	public void init(String defaultClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		int propertyStartIdx = propertyName.lastIndexOf(".");
		if(propertyStartIdx != NOT_FOUND){
			className = propertyName.substring(0, propertyStartIdx);
			propertyName = propertyName.substring(propertyStartIdx+1);
		}else{
			className = defaultClassName;//does NOT matter is it NULL or not
			propertyName = propertyName;
		}
		
		if(!GeneralUtil.isEmpty(convertorClassName)){ 
			convertor = (IConvertor) Class.forName(convertorClassName).newInstance();//Class<?>  clazz = Class.forName(convertorClassName);
		}
	}

	public String getClassName() {return className;}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPropertyName() {return propertyName;}//public void setPropertyName(String propertyName) {this.propertyName = propertyName;}
	public IConvertor getConvertor() {return convertor;}//public void setConvertor(IConvertor convertor) {this.convertor = convertor;}
	public String toString() {return "ClassProperty [className=" + className + ", propertyName=" + propertyName + "]";}
	public String getReferencedEntity() {return referencedEntity;}
};

