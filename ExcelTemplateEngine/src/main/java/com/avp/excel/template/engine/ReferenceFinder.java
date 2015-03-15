package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReferenceFinder - Detects references in tables within one sheet
 * If table column has property referencedEntity
 * for example: referencedEntity:"ca.canon.fast.model.impl.SalesUploadWeekFct.id"
 * find instance of that class and request for either getter with type List<'CurrentClass'> or getter/setter with type 'CurrentClass'
 * @author Q05459
 *
 */
public class ReferenceFinder {
	
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

		
	}

}
