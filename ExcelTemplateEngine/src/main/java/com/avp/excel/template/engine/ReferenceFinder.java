package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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
	 */
	@Deprecated
	private ReferenceFinder(String content) {
	}

//	public ReferenceFinder(List<TableDescriptor> descriptorTables,	Map<String, ArrayList<Object>> collectedBeansFromTablesAsMap) {
//		// TODO Auto-generated constructor stub
//	}

	public static void linkTables(List<TableDescriptor> descriptorTables,	Map<String, ArrayList<Object>> collectedBeansFromTablesAsMap) {
		logger.debug("descriptorTables = "+descriptorTables);
		Set<String> setOfBeans = collectedBeansFromTablesAsMap.keySet();
		//scan tables for beans with references
		for (String beanKey : setOfBeans) {
			logger.debug("beanKey = "+beanKey);
			ArrayList<Object> table = collectedBeansFromTablesAsMap.get(beanKey);
			TableDescriptor td = getDestcriptor(descriptorTables, beanKey);
			List<ClassProperty> references = td.getReferences();
			if(td == null)
				continue;
			for (Object object : table) {
				logger.debug("\tobject = "+object);
				
			}
		}
	}

	private static TableDescriptor getDestcriptor(	List<TableDescriptor> descriptorTables, String className) {
		for (TableDescriptor tableDescriptor : descriptorTables) {
			if(tableDescriptor.getDefaultClassName().equals(className))
				return tableDescriptor;
		}
		return null;
	}

}
