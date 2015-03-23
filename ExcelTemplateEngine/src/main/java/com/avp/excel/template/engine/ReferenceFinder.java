package com.avp.excel.template.engine;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import ca.canon.fast.utils.GeneralUtil;

/**
 * ReferenceFinder - Detects references in tables within one sheet
 * If table column has property referencedEntity
 * for example: referencedEntity:"ca.canon.fast.model.impl.SalesUploadWeekFct.id"
 * find instance of that class and request for either getter with type List<'CurrentClass'> or getter/setter with type 'CurrentClass'
 * @author Q05459
 *
 */
public class ReferenceFinder {
	private static Logger logger = Logger.getLogger(ReferenceFinder.class);
	/**
	 * what to pass as String content from ExcelTemplateEngine/src/main/java/com/avp/excel/template/engine/ExcelTemplateHelper.java?
	 * at the end of parseDataSheet(HSSFSheet dataSheet) you have access to descriptors and collected data
	 * pass it here! and test in ReferenceFinderTest
	 * private ReferenceFinder(String content) {}
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */

	public static void linkTables(List<TableDescriptor> descriptorTables,	Map<String, ArrayList<Object>> collectedBeansFromTablesAsMap) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		logger.debug("descriptorTables = "+descriptorTables);
		Set<String> setOfBeans = collectedBeansFromTablesAsMap.keySet();
		//scan tables for beans with references
		for (String beanKey : setOfBeans) {
			logger.debug("beanKey = "+beanKey);
			int startClassNameIdx = beanKey.lastIndexOf(".");
			String entityClassName = beanKey.substring(startClassNameIdx+1).toLowerCase();
			ArrayList<Object> table = collectedBeansFromTablesAsMap.get(beanKey);
			TableDescriptor td = getDestcriptor(descriptorTables, beanKey);
			if(td == null)//not possible?
				continue;
			List<ClassProperty> references = td.getReferences();
			for (ClassProperty refClassProperty : references) {
				int startPropertyIdx = refClassProperty.getReferencedEntity().lastIndexOf(".");
				if(startPropertyIdx<0) continue;
				String refClassName = refClassProperty.getReferencedEntity().substring(0,startPropertyIdx);
				String refPropertyName = refClassProperty.getReferencedEntity().substring(startPropertyIdx+1);
				String ptrPropertyName = refClassProperty.getPropertyName();
				List<Object> referedBeans = collectedBeansFromTablesAsMap.get(refClassName);
				//skip linkage if there no reference column
				if(GeneralUtil.isEmpty(referedBeans)) continue;
				//link all objects in table
				for (Object object : table) {
					logger.debug("\tobject = "+object);
					Object dstValue = PropertyUtils.getProperty(object, ptrPropertyName);
					logger.debug("\tRef ptrPropertyName to refClassProperty.getReferencedEntity() = " + dstValue);
					//scan all referedBeans for instance with refPropertyName equal dstValue 
					for(Object referedBean:referedBeans){
						Object refValue = PropertyUtils.getProperty(referedBean, refPropertyName);
						logger.debug("\trefValue = " + refValue);
						if(dstValue.equals(refValue)){
							logger.debug("\treferedBean {"+referedBean+"} link to {" + object+"}");
							// find where I can insert reference 
							PropertyDescriptor[] propertyDescriptors	= PropertyUtils.getPropertyDescriptors(referedBean);
							for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
								logger.debug("\t\tpropertyDescriptor "+propertyDescriptor);
								Class<?> propertyType = propertyDescriptor.getPropertyType();
								/*
								//one to many with name convention
								// we can add child object if in referred parent object we can find List property with name similar name 
								// for example ActualDTO can be added to List<ActualDTO> actualDTOs (Reflection API will see List getActualDTOs())
								if( propertyType==List.class && propertyDescriptor.getName().toLowerCase().indexOf(entityClassName)>=0){
									logger.debug("\t\t"+propertyDescriptor.getName()+ " is List of "+ entityClassName);
									List list = (List)PropertyUtils.getProperty(referedBean, propertyDescriptor.getName());
									list.add(object);
								}*/
								//one to many with lang reflection determining type of generic list 
								//google 'reflection generic collection type' http://stackoverflow.com/questions/1942644/get-generic-type-of-java-util-list
								if( propertyType==List.class ){
									logger.debug("\t\t"+propertyDescriptor.getName()+ " is List of "+ entityClassName);
									Field listField = referedBean.getClass().getDeclaredField(propertyDescriptor.getName());
									ParameterizedType listFieldType = (ParameterizedType) listField.getGenericType();
									Class<?> listClass = (Class<?>) listFieldType.getActualTypeArguments()[0];
									if(listClass == object.getClass()){
										List list = (List)PropertyUtils.getProperty(referedBean, propertyDescriptor.getName());
										list.add(object);
									}
								}
								/*
								 * TODO - <AP> this should work but haven't tested
								//one to one
								if( propertyType==object.getClass() ){
									PropertyUtils.setProperty(referedBean, propertyDescriptor.getName(), object);
								}
								*/
							}
						}
					}//eof for all refered beans
				}//eof for all object in table
			}//eof for all refs
		}//eof for all tables
	}//eof linkTables

	private static TableDescriptor getDestcriptor(	List<TableDescriptor> descriptorTables, String className) {
		for (TableDescriptor tableDescriptor : descriptorTables) {
			if(tableDescriptor.getDefaultClassName().equals(className))
				return tableDescriptor;
		}
		return null;
	}
	
	/*  Not Used
	//http://qussay.com/2013/09/28/handling-java-generic-types-with-reflection/
    //import java.lang.reflect.Type;	
	public static Type[] getParameterizedTypes(Object object) {
		    Type superclassType = object.getClass().getGenericSuperclass();
		    if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
		        return null;
		    }
		    return ((ParameterizedType)superclassType).getActualTypeArguments();
		}
   */

}
