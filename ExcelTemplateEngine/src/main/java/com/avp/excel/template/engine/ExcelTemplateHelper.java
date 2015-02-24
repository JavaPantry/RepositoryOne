package com.avp.excel.template.engine;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ca.canon.fast.utils.BeanUtility;
import ca.canon.fast.utils.GeneralUtil;
/**
 * 
 * Please note: - entities that might be detected in spreadsheet must implement equal() and hashCode() 
 * 
 * History:
 * v 0.0.3	ClassProperty extracted from ExcelTemplateHelper
 * v 0.0.4	C-tor accepts type of helper (excel reader or writer)
 * v 0.0.5	Merge GitHub and FaST progect versions into Refactoring branch
 * v 0.0.6  Fix Error: Empty row in collected data from table
 * tag version # 0.1
 * v 0.1.1  Introduce TableDescriptor  
 * v 0.1.2 - New table meta-data format
 *	Introduce default className for 'table' tag
 *	- Should support old fashion 
 *	- in BNF notation   ".{table:start[';'+'className:ca.canon.fast.model.impl.SalesUploadWeekFct']}"
 *	- if .{table:start;className:ca.canon.fast.model.impl.SalesUploadWeekFct} contains className property may be in short form ${itemCode}
 *		otherwise in long ${ca.canon.fast.model.impl.SalesUploadWeekFct.itemCode}
 *	- Example:
 *		.{table:start;className:ca.canon.fast.model.impl.SalesUploadWeekFct}
 *		${itemCode}|${ca.canon.fast.model.impl.SalesUploadWeekFct.billTo}|${sellTo}|${year}|${month},${ca.canon.fast.model.impl.MonthToIntegerConvertor}${dollar1}|${unit1}|${dollar2}|${unit2}|${dollar3}|${unit3}|${dollar4}|${unit4}|${dollar5}|${unit5}
 *		.{table:end;className:ca.canon.fast.model.impl.SalesUploadWeekFct
 * v 0.1.3 - Add multiple table support
 * 
 * @author ptitchkin
 *
 */
public class ExcelTemplateHelper extends Descriptor{
	private static Logger logger = Logger.getLogger(ExcelTemplateHelper.class);

	private static final String ERROR_IN_PARSING = "Error in Excel parsing";

	private static final String DATASHEET_SUFFIX = "_Data";
	private static final String SERVICE_SHEET = "SERVICE_SHEET";

	private static final String PARSE_FOR_READING = "PARSE_FOR_READING"; 
	private static final String PARSE_FOR_WRITING = "PARSE_FOR_WRITING";

	/**
	 * inline enum for spreadsheet type distinct 'XXXXX_Data' and 'SERVICE_SHEET' 
	 * not sure if I need it here at all
	 */
	public enum SpreadsheetType {
		Data(DATASHEET_SUFFIX), 
		MetaData(SERVICE_SHEET);
		private String typeCode;
		
		SpreadsheetType(String type){typeCode = type;}
		public String toString() {return typeCode;}
		@Deprecated
		public String getTypeCode() {return typeCode;}
	}
	
	private HSSFWorkbook workbook;
	private HSSFSheet metaSheet;
	
	private List<TableDescriptor> tables = new ArrayList<TableDescriptor>();
	public List<TableDescriptor> getTables() {return tables;}
	public void setTables(List<TableDescriptor> tables) {this.tables = tables;}  

	/**
	 * 	resultMap and commonBeanAsMap are used to store parsing result done in c-tor ExcelTemplateHelper(File fileToParse) 
	 *	shared between spreadsheet processing
	 */
	private Map<String,ArrayList<Object>> collectedBeansFromTablesAsMap = new HashMap<String, ArrayList<Object>>();
	private Map<String,ArrayList<Object>> commonBeansAsMap = new HashMap<String, ArrayList<Object>>();

	/**
	 * 	store properties which will not copied from header bean to table enteties
	 */
	private String[] excludeArray = null;

	//moved to TableDescriptor: private HashMap<String, ClassProperty> tablePropertyMap = new HashMap<String, ClassProperty>();
	private HashMap<String, ClassProperty> commonPropertyMap = new HashMap<String, ClassProperty>();

	/**
	 * TODO - <AP> must be reviewed prior to multi-table support
	 */
	private int rowTableStartIdx = -1;
	
	
	/**
	 * constructor parse template sheet "SERVICE_SHEET"
	 * current implementation support only one table per workbook
	 * 
	 * C-tor collect two maps: 
	 * - Map<String,ArrayList<Object>> resultMap = new HashMap<String, ArrayList<Object>>();
	 * where parser will collect entities from table 
	 * - Map<String,ArrayList<Object>> commonMap = new HashMap<String, ArrayList<Object>>();
	 * where parser will collect entities from header
	 * 
	 * @param fileToParse
	 * @throws Exception
	 */
	public ExcelTemplateHelper(File fileToParse) throws Exception{
		int tableIdx = 0;
		TableDescriptor td = null;
		//only to control embedded tables (table inside table)
		Stack<TableDescriptor> tableStack = new Stack<TableDescriptor>();
		
		try {
				FileInputStream inputWbStream = new FileInputStream(fileToParse);
				//Get the workbook instance for XLS file 
				workbook = new HSSFWorkbook(inputWbStream);
				metaSheet = workbook.getSheet(SpreadsheetType.MetaData.getTypeCode());//getSheet("SERVICE_SHEET");
	
				if ( metaSheet ==null){
					throw new Exception("Template workbook must have 'SERVICE_SHEET' sheet with template");
				}
				Iterator<Row> rowIterator = metaSheet.rowIterator();
				int rowIdx = 0;
				  
				while(rowIterator.hasNext()){
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					int cellIdx = 0;
					while(cellIterator.hasNext()){
						Cell cell = cellIterator.next();
						
						String content = "";
						if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
		                	content = cell.getStringCellValue();
						}
						
						if(GeneralUtil.isEmpty(content)){
							cellIdx++;//don't forget to increment so next pass will point next cell
							continue;
						}
						if(content.startsWith(START_TAG)){ // .{table:start} or .{table:end}
							TableDescriptor tmpTd = new TableDescriptor(content); 
							if(tmpTd.isStart()){
								td = tmpTd;
								rowTableStartIdx=rowIdx+1;
								tables.add(td);
								tableStack.push(td);
							}
							if(tmpTd.isEnd()){
								tableStack.pop();
							}
						}
						
						if(content.startsWith(START_VAR)){  //${ca.canon.fast.web.sales.SalesMonthFctSpreadsheetController$ActualsDTO.userName}
							ClassProperty classProperty = new ClassProperty(td, content);
							String key = new StringBuilder().append(rowIdx).append("_").append(cellIdx).toString(); 
							//if(rowTableStartIdx <= 0){
							if(tableStack.isEmpty()){ //if not inside table store variable in commonPropertyMap 	
								commonPropertyMap.put(key, classProperty);
							}else{
								TableDescriptor currentTableDescriptor = tableStack.peek(); 
								currentTableDescriptor.getTablePropertyMap().put(key, classProperty);
							}
							
							if (!collectedBeansFromTablesAsMap.containsKey(classProperty.getClassName())){
								collectedBeansFromTablesAsMap.put(classProperty.getClassName(),new ArrayList<Object>());
							}
							if (!commonBeansAsMap.containsKey(classProperty.getClassName())){
								commonBeansAsMap.put(classProperty.getClassName(),new ArrayList<Object>());
							}
						}
						cellIdx++; //next pass will point next cell
					}//eof while(cellIterator.hasNext())
					rowIdx++; //next pass will point next row
				}//eof while(rowIterator.hasNext())
				
				//TODO - <AP> collect excludeArray[][] for each TableDescriptor 
				excludeArray = getExcludeFields(tablePropertyMap);
				
				logger.debug("End of template sheet in workbook");
			} catch (FileNotFoundException e) {
				logger.error(e,e);
				throw new Exception(ERROR_IN_PARSING,e);
			} catch (IOException e) {
				logger.error(e,e);
				throw new Exception(ERROR_IN_PARSING,e);
			}
	}

	/**
	 * parse sheets with suffix "_Data" based on template sheet "SERVICE_SHEET" acquired in c-tor
	 * @throws Exception 
	 * 
	 */
	public Map<String, Map<String, ArrayList<Object>>> parseDataSheets() throws Exception {
		int numberOfSheets  = workbook.getNumberOfSheets();
		//List<HSSFSheet> dataSheets = new ArrayList<HSSFSheet>();
		Map<String, Map<String, ArrayList<Object>>> mapOfSheets = new HashMap<String, Map<String, ArrayList<Object>>>();
		for (int sheetIdx = 0; sheetIdx < numberOfSheets; sheetIdx++) {
			String sheetName = workbook.getSheetName(sheetIdx);
			if(sheetName.endsWith(DATASHEET_SUFFIX)){
				Map<String, ArrayList<Object>> objectsFromDataSheet = parseDataSheet(workbook.getSheetAt(sheetIdx));
				String dataName = sheetName.substring(0,sheetName.indexOf(DATASHEET_SUFFIX));
				mapOfSheets.put(dataName, objectsFromDataSheet);
			}
		}
		return mapOfSheets;
	}
	
	/**
	 * parse "Data" sheet based on template sheet "SERVICE_SHEET" acquired in c-tor
	 * 
	 * @param fileToParse   
	 *		HSSFSheet dataSheet = workbook.getSheet("Data");
	 *		HSSFSheet metaSheet = workbook.getSheet("SERVICE_SHEET");
	 *
	 * @return Map<String,ArrayList<Object>> where key is clazzNmae of collected entities with array of entities
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	private Map<String,ArrayList<Object>> parseDataSheet(HSSFSheet dataSheet) throws Exception {
		commonBeansAsMap = resetEntityMap(commonBeansAsMap);
		collectedBeansFromTablesAsMap = resetEntityMap(collectedBeansFromTablesAsMap);
		Object tmpDummyCommonEntity = null;

		try{
			//this.logSheetData(resultMap);
			Iterator<Row> rowIterator = dataSheet.rowIterator();
			int rowIdx = 0;
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				
				if(rowIdx < rowTableStartIdx){
					//collect common fields before table body  
					//assignment any empty row before table body will reset commonEntity 
					//BUT values still accumulate in ?commonBeanAsMap? 
					tmpDummyCommonEntity = pushRowToEntity(commonPropertyMap, commonBeansAsMap, tmpDummyCommonEntity, rowIdx, row);
					rowIdx++;
					continue;
				}
				
				//collect entities from current table body
				Object entity = pushRowToEntity(tablePropertyMap, collectedBeansFromTablesAsMap, null, rowTableStartIdx, row);
				if(entity == null)//TODO - <AP> any other valid way to detect end of data?
					break;
				populateCommonFields(entity);//(commonEntity, entity);
				rowIdx++;
			}
			logger.debug("End of data sheet in workbook");
		} catch (IllegalAccessException e) {
			logger.error(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (InvocationTargetException e) {
			logger.error(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (NoSuchMethodException e) {
			logger.error(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (ClassNotFoundException e) {
			logger.error(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (InstantiationException e) {
			logger.error(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		}
		//this.logSheetData(resultMap);
		return collectedBeansFromTablesAsMap;
	}//eof parseDataSheet(...

	/**
	 * Log data from parsed sheet 
	 * @param objectsFromDataSheet - map of collections for each recognized type
	 */
	public void logSheetData(Map<String, ArrayList<Object>> objectsFromDataSheet){
		Set<String> keySet = objectsFromDataSheet.keySet();
		for (String key : keySet) {
			 ArrayList<Object> entities = objectsFromDataSheet.get(key);
			 for (Object entity : entities) {
				logger.debug(entity.toString());
			}
		}//eofor keySet
	}//eof logSheetData(...
	
	private String[] getExcludeFields(HashMap<String, ClassProperty> tablePropertyMap){
		//common fields are taken from header of the sheet (they exists only in header and are not exists in table)
		//collect exclude fields (fields in tablePropertyMap are excluded from when populate entity from commonEntity)
		List<String> exludeFields = new ArrayList<String>();
		Set<String> propertyKeys = tablePropertyMap.keySet();
		boolean first = true;
		for (String key : propertyKeys) { 
			ClassProperty classProperty = tablePropertyMap.get(key);
			String propertyName = classProperty.getPropertyName();
			if(first){
				propertyName = "+"+propertyName;
				first = false;
			}
			exludeFields.add(propertyName);
		}
		String[] excludeArray = new String[exludeFields.size()];
		for (int i = 0; i < excludeArray.length; i++) {
			excludeArray[i] = exludeFields.get(i);					
		}
		return excludeArray;
	}
	
	private void populateCommonFields(Object entity) { 
		//find same class in commonMap
		Set<String> keys = commonBeansAsMap.keySet();
		for (String key : keys) {
			if(entity.getClass().getName().equals(key)){
				ArrayList<Object> srcList = commonBeansAsMap.get(key);
				Object src = srcList.get(0);
				BeanUtility.nullSafeMergeTo(src, entity, excludeArray);
			}
		}
		
	}
	/**
	 * create instance of object (if not passed in) and populate properties from cells (described in template) 
	 * return null if row is empty (null might trigger end of table)
	 * @param propertyMap
	 * @param collectedBeansMap - either commonBeanAsMap or resultMap
	 * @param entity
	 * @param rowIdx
	 * @param row
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object pushRowToEntity(	Map<String, ClassProperty> propertyMap, 
									Map<String,ArrayList<Object>> collectedBeansMap, 
									Object entity, 
									int rowIdx,  
									Row row)
			throws ClassNotFoundException, InstantiationException,IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		boolean isRowEmpty = true;
		int cellIdx = 0;
		
		Iterator<Cell> cellIterator = row.cellIterator();
		while(cellIterator.hasNext()){
			Cell cell = cellIterator.next();
			String key=""+rowIdx+"_"+cellIdx;
			ClassProperty classProperty = propertyMap.get(key);
			if(classProperty == null){
				cellIdx++;
				continue;
			}
			
			if (entity == null){
				Class<?>  clazz = Class.forName(classProperty.getClassName());
				entity = clazz.newInstance();
			}
			if(!GeneralUtil.isEmpty(cell.toString())){
				isRowEmpty = false;
				pushCellToEntity(entity, cell, classProperty);
				
				//TODO - <AP> what if collectedMap does NOT contain class name? It's not possible because 1-st pass should collect all classes
				ArrayList<Object> entities = collectedBeansMap.get(classProperty.getClassName());
				if(!entities.contains(entity)){
				entities.add(entity);
				}// if cell not empty
			}
			cellIdx++;
		}
		if(isRowEmpty)
			return null;
		return entity;
	}
	private void pushCellToEntity(Object entity, Cell cell, ClassProperty classProperty) throws IllegalAccessException,	InvocationTargetException, NoSuchMethodException {
		switch(cell.getCellType()) {
		    case HSSFCell.CELL_TYPE_BOOLEAN:
		    	Boolean boolValue = new Boolean(cell.getBooleanCellValue());
		    	setProperty(entity, classProperty.getPropertyName(), boolValue);
		        break;
		    case HSSFCell.CELL_TYPE_NUMERIC:
		    	Double doubleValue = new Double(cell.getNumericCellValue());
		    	setProperty(entity, classProperty.getPropertyName(), doubleValue);
		        break;
		    case HSSFCell.CELL_TYPE_STRING:
		    	String value = cell.getStringCellValue();
		    	IConvertor convertor = classProperty.getConvertor();
		    	if(convertor != null){
		    		Object convertedeValue = convertor.convert(value);
		    		setProperty(entity, classProperty.getPropertyName(), convertedeValue);
		    	}else{
		    		setProperty(entity, classProperty.getPropertyName(), value);
		    	}
		        break;
		}
	}
	/**
	 * @param entity
	 * @param propertyMap
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void describePropertiesTypes(Object entity, HashMap<String, ClassProperty> propertyMap) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Set<String> keySet = propertyMap.keySet();
		for (String key : keySet) {
			ClassProperty classProperty = propertyMap.get(key);
			PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(entity, classProperty.getPropertyName());
			if(descriptor == null){
				logger.warn("entity "+entity.getClass().getName()+" does NOT have propertyName '"+classProperty.getPropertyName()+"'");
				continue;
			}
			logger.debug("property '"+classProperty.getPropertyName()+"' have type '"+descriptor.getPropertyType()+"'");
		}
	}
	
	/**
	 * support only String and int/Integer
	 * @param entity
	 * @param propertyName
	 * @param doubleValue
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void setProperty(Object entity, String propertyName, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		IntegerConverter intConverter = new IntegerConverter();
		Object dstValue = null;
		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(entity, propertyName);
		if(descriptor == null){
			logger.warn("entity "+entity.getClass().getName()+" does NOT have propertyName '"+propertyName+"'");
			return;
		}
		Class<?> typeClazz = descriptor.getPropertyType();
		String clazzName = typeClazz.getName().toLowerCase();
		if(clazzName.endsWith("int") || clazzName.endsWith("integer")){
			dstValue = intConverter.convert(Integer.class, value);
		}else if(clazzName.endsWith("string")){
			 StringConverter converter = new  StringConverter();
			 if(value instanceof Double){
				 value = intConverter.convert(Integer.class, value); 
			 }
			 dstValue = converter.convert(String.class, value);
		}
		PropertyUtils.setProperty(entity, propertyName, dstValue);
	}
	private Map<String, ArrayList<Object>> resetEntityMap(Map<String, ArrayList<Object>> mapToReset) {
		Map<String,ArrayList<Object>> newMap = new HashMap<String, ArrayList<Object>>();
		Set<String> keySet = mapToReset.keySet();
		for (String key : keySet) {
			newMap.put(key, new ArrayList<Object>());
		}
		return newMap;
	}

}
