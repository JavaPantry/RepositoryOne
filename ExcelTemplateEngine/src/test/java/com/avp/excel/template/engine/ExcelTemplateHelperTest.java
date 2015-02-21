package com.avp.excel.template.engine;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * Test for branch "Refactoring" 
 * @author ptitchkin
 *
 */
public class ExcelTemplateHelperTest {
	private final static Logger logger = Logger.getLogger(ExcelTemplateHelperTest.class);
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testParseExcelDataExt() throws Exception {
		File forecastTemplate = new File("ForecastTemplateExt.xls");
		ExcelTemplateHelper exelTpl = new ExcelTemplateHelper(forecastTemplate);
		Map<String, Map<String, ArrayList<Object>>> resultMap = exelTpl.parseDataSheets();
	//TODO - <AP> need to validate data	
		Set<String> sheetKeySet = resultMap.keySet();
		for (String sheetKey : sheetKeySet) {
			logger.debug(sheetKey +" data sheet in workbook");
			Map<String, ArrayList<Object>> result = resultMap.get(sheetKey);
			exelTpl.logSheetData(result);
		}//eofor sheetKeySet
	}
	
	@Test
	public void testParseExcelData() throws Exception {
		File forecastTemplate = new File("ForecastTemplate.xls");
		ExcelTemplateHelper exelTpl = new ExcelTemplateHelper(forecastTemplate);
		Map<String, Map<String, ArrayList<Object>>> resultMap = exelTpl.parseDataSheets();
	//TODO - <AP> need to validate data	
		Set<String> sheetKeySet = resultMap.keySet();
		for (String sheetKey : sheetKeySet) {
			logger.debug(sheetKey +" data sheet in workbook");
			Map<String, ArrayList<Object>> result = resultMap.get(sheetKey);
			exelTpl.logSheetData(result);
		}//eofor sheetKeySet
	}
}