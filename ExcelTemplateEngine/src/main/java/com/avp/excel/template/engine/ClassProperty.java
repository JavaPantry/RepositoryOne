package com.avp.excel.template.engine;

import ca.canon.fast.utils.GeneralUtil;

public class ClassProperty extends Descriptor{
	private String		className; // used as key
	private String		propertyName;
	private String		referencedEntity;
	private IConvertor	convertor;
	
	/* 
	 * very naive implementation 
	 * input:	${ca.canon.fast.model.impl.SalesUploadWeekFct.actualType},${ca.canon.fast.model.impl.ActualsTypeToAcronimConvertor}													
	 *	or:		${month},${ca.canon.fast.model.impl.MonthToIntegerConvertor}
	 * propertyName in 1st position might be in full format or just name
	 * 
	 * need to change format
	 * [${ca.canon.fast.model.impl.SalesUploadWeekFct.actualType},${ca.canon.fast.model.impl.ActualsTypeToAcronimConvertor}]
	 * 
	 * TO ['${'+name[:reference][;convertor:convertorClass]+'}']:
	 * [${actualType}]
	 * OR
	 * [${ca.canon.fast.model.impl.SalesUploadWeekFct.actualType}]
	 * OR
	 * [${actualType:referenceClass}]
	 * OR
	 * [${ca.canon.fast.model.impl.SalesUploadWeekFct.actualType,convertor:ca.canon.fast.model.impl.ActualsTypeToAcronimConvertor}]
	 * OR
	 * [${ca.canon.fast.model.impl.SalesUploadWeekFct.actualType,convertor:ca.canon.fast.model.impl.ActualsTypeToAcronimConvertor}]
	 */

	public ClassProperty(TableDescriptor td, String cellContent) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(td != null && !GeneralUtil.isEmpty(td.getDefaultClassName())){
			className = td.getDefaultClassName();
		}
		//expected: ${properetyName},${convertor}
		String descriptors[] = cellContent.split(",");
		String propertyDesctiptor[] = descriptors[0].split(TAG_VALUE_DELIMITER); // ${properetyName} OR ${properetyName:refClassName}
		String fullPropertyName = propertyDesctiptor[0];
		fullPropertyName = stripDecoration(fullPropertyName);

		int propertyStartIdx = fullPropertyName.lastIndexOf(".");
		if(propertyStartIdx != NOT_FOUND){
			className = fullPropertyName.substring(0, propertyStartIdx);
			propertyName = fullPropertyName.substring(propertyStartIdx+1);
		}else{
			propertyName = fullPropertyName;
		}
		
		// process if optional converter provided
		if(descriptors.length>1 && !GeneralUtil.isEmpty(descriptors[1])){
			String convertorName = descriptors[1]; 
			convertorName = stripDecoration(convertorName);
			Class<?>  clazz = Class.forName(convertorName);
			convertor = (IConvertor) clazz.newInstance();
		}
		
	}
	public String getClassName() {return className;}//public void setClassName(String className) {this.className = className;}
	public String getPropertyName() {return propertyName;}//public void setPropertyName(String propertyName) {this.propertyName = propertyName;}
	public IConvertor getConvertor() {return convertor;}
	//public void setConvertor(IConvertor convertor) {this.convertor = convertor;}
	public String toString() {return "ClassProperty [className=" + className + ", propertyName=" + propertyName + "]";}
	
	public String getReferencedEntity() {
		return referencedEntity;
	}
};