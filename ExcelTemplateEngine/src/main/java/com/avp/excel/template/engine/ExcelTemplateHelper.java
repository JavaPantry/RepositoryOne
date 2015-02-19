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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ca.canon.fast.utils.BeanUtility;
import ca.canon.fast.utils.GeneralUtil;
/**
 * v 1.3	ClassProperty extracted from ExcelTemplateHelper
 * v 1.4	C-tor accepts type of helper (excel reader or writer)
 * v 1.5	Merge GitHub and FaST progect versions into Refactoring branch
 * @author ptitchkin
 *
 */
public class ExcelTemplateHelper {
	private static Logger logger = Logger.getLogger(ExcelTemplateHelper.class);

	private static final String ERROR_IN_PARSING = "Error in Excel parsing";

	private static final String DATASHEET_SUFFIX = "_Data";

	private static final String PARSE_FOR_READING = "PARSE_FOR_READING"; 
	private static final String PARSE_FOR_WRITING = "PARSE_FOR_WRITING";

	public enum SpreadsheetType {
		Data("Data"), 
		MetaData("SERVICE_SHEET");
		private String typeCode;
		
		SpreadsheetType(String type){typeCode = type;}
		public String toString() {return typeCode;}
		@Deprecated
		public String getTypeCode() {return typeCode;}
	}
	
	private HSSFWorkbook workbook;
	private HSSFSheet metaSheet;
	
	private Map<String,ArrayList<Object>> resultMap = new HashMap<String, ArrayList<Object>>();
	private HashMap<String, ClassProperty> tablePropertyMap = new HashMap<String, ClassProperty>();
	private String[] excludeArray = null;
	
	private Map<String,ArrayList<Object>> commonMap = new HashMap<String, ArrayList<Object>>();
	private HashMap<String, ClassProperty> commonPropertyMap = new HashMap<String, ClassProperty>();
	
	private Object commonEntity = null;
	private int rowTableStartIdx = 0;
	
	private void resetParser(){
		commonEntity = null;
		
		Map<String,ArrayList<Object>> newCommonMap = new HashMap<String, ArrayList<Object>>();
		Set<String> keySet = commonMap.keySet(); //= new HashMap<String, ArrayList<Object>>();
		for (String key : keySet) {
			newCommonMap.put(key, new ArrayList<Object>());
		}
		commonMap = newCommonMap;
		
		Map<String,ArrayList<Object>> newResultMap = new HashMap<String, ArrayList<Object>>();
		keySet = resultMap.keySet();//resultMap = new HashMap<String, ArrayList<Object>>();
		for (String key : keySet) {
			newResultMap.put(key, new ArrayList<Object>());
		}
		resultMap = newResultMap;
		
	}
	/**
	 * constructor parse template sheet "SERVICE_SHEET"
	 * current implementation support only one table per workbook
	 * 
	 * @param fileToParse
	 * @throws Exception
	 */
	public ExcelTemplateHelper(File fileToParse) throws Exception{
		try {
			FileInputStream inputWbStream = new FileInputStream(fileToParse);
			//Get the workbook instance for XLS file 
			workbook = new HSSFWorkbook(inputWbStream);
			
			metaSheet = workbook.getSheet(SpreadsheetType.MetaData.getTypeCode());//getSheet("SERVICE_SHEET");
			if ( metaSheet ==null){
				throw new Exception("Template workbook must have 'SERVICE_SHEET' sheet with template");
			}
			Iterator<HSSFRow> rowIterator = metaSheet.rowIterator();
			int rowIdx = 0;
			rowTableStartIdx = 0;
			  
			while(rowIterator.hasNext()){
				HSSFRow row = rowIterator.next();
				Iterator<HSSFCell> cellIterator = row.cellIterator();
				int cellIdx = 0;
				while(cellIterator.hasNext()){
					HSSFCell cell = cellIterator.next();
					
					String content = "";
					if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
	                	content = cell.getStringCellValue();
					}
					
					if(GeneralUtil.isEmpty(content)){
						cellIdx++;//don't forget to increment so next pass will point next cell
						continue;
					}
					if(content.startsWith(".{")){ // .{table:start} or .{table:end}
						content = content.substring(2, content.length()-1);
						String[] cmd = content.split(":");
						if(cmd[0].equalsIgnoreCase("table") && cmd[1].equalsIgnoreCase("start"))
							rowTableStartIdx=rowIdx+1;		
					}
					if(content.startsWith("${")){  //${ca.canon.fast.web.sales.SalesMonthFctSpreadsheetController$ActualsDTO.userName}
						//TODO - <AP> pass whole content to ClassProperty(content);
						//content = content.substring(2, content.length()-1);
						ClassProperty classProperty = new ClassProperty(content);
						if(rowTableStartIdx==0){
							commonPropertyMap.put(""+rowIdx+"_"+cellIdx, classProperty);
						}else{
							tablePropertyMap.put(""+rowIdx+"_"+cellIdx, classProperty);
						}
						if (!resultMap.containsKey(classProperty.getClassName())){
							resultMap.put(classProperty.getClassName(),new ArrayList<Object>());
						}
						if (!commonMap.containsKey(classProperty.getClassName())){
							commonMap.put(classProperty.getClassName(),new ArrayList<Object>());
						}
					}
					cellIdx++; //next pass will point next cell
				}//eof while(cellIterator.hasNext())
				rowIdx++; //next pass will point next row
			}//eof while(rowIterator.hasNext())
			
			//store properties which will not copied from header bean
			excludeArray = getExcludeFields(tablePropertyMap);
			logger.debug("End of template sheet in workbook");
			} catch (FileNotFoundException e) {
				logger.debug(e,e);
				throw new Exception(ERROR_IN_PARSING,e);
			} catch (IOException e) {
				logger.debug(e,e);
				throw new Exception(ERROR_IN_PARSING,e);
			}
	}

	/**
	 * 
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
				//TODO - <AP> - resetParser destroy data collected on previous pass. WTF?!!!  
				resetParser();
				Map<String, ArrayList<Object>> objectsFromDataSheet = parseDataSheet(workbook.getSheetAt(sheetIdx));
				String dataName = sheetName.substring(0,sheetName.indexOf(DATASHEET_SUFFIX));
				mapOfSheets.put(dataName, objectsFromDataSheet);
			}
		}
		return mapOfSheets;
	}

	/**
	 * 
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
	public Map<String,ArrayList<Object>> parseDataSheet(HSSFSheet dataSheet) throws Exception {
		try{
			//ArrayList<Object> entities = new ArrayList<Object>();
			Iterator<HSSFRow> rowIterator = dataSheet.rowIterator();
			int rowIdx = 0;
			while(rowIterator.hasNext()){
				HSSFRow row = rowIterator.next();
				if(rowIdx < rowTableStartIdx){
					//collect common fields before table body  TODO - <AP> because of assigment any empty row before table body will reset commonEntity
					commonEntity = pushRowToEntity(commonPropertyMap, commonMap, commonEntity, rowIdx, row);
					rowIdx++;
					continue;
				}
				//collect entities from table body
				Object entity = pushRowToEntity(tablePropertyMap, resultMap, null, rowTableStartIdx, row);
				if(entity == null)//TODO - <AP> any other valid way to detect end of data?
					break;
				populateCommonFields(entity);
				rowIdx++;
			}
			logger.debug("End of data sheet in workbook");
		} catch (IllegalAccessException e) {
			logger.debug(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (InvocationTargetException e) {
			logger.debug(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (NoSuchMethodException e) {
			logger.debug(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (ClassNotFoundException e) {
			logger.debug(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		} catch (InstantiationException e) {
			logger.debug(e,e);
			throw new Exception(ERROR_IN_PARSING,e);
		}
		return resultMap;
	}

	
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
		Set<String> keys = commonMap.keySet();
		for (String key : keys) {
			if(entity.getClass().getName().equals(key)){
				ArrayList<Object> srcList = commonMap.get(key);
				Object src = srcList.get(0);
				BeanUtility.nullSafeMergeTo(src, entity, excludeArray);
			}
		}
		
	}
	/**
	 * create instance of object (if not passed in) and populate properties from cells (described in template) 
	 * return null if row is empty (null might trigger end of table)
	 * @param propertyMap
	 * @param collectedMap
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
	private Object pushRowToEntity(HashMap<String, ClassProperty> propertyMap, 
											Map<String,ArrayList<Object>> collectedMap, 
											Object entity, 
											int rowIdx,  
											HSSFRow row) throws ClassNotFoundException, InstantiationException, 
																IllegalAccessException, InvocationTargetException, 
																NoSuchMethodException{
		boolean isRowEmpty = true;
		int cellIdx = 0;
		
		Iterator<HSSFCell> cellIterator = row.cellIterator();
		while(cellIterator.hasNext()){
			HSSFCell cell = cellIterator.next();
			String key=""+rowIdx+"_"+cellIdx;
			ClassProperty classProperty = propertyMap.get(key);
			if(classProperty == null){
				cellIdx++;
				continue;
			}
			
			if (entity == null){
				Class<?>  clazz = Class.forName(classProperty.getClassName());
				entity = clazz.newInstance();
				//TODO - <AP> what if collectedMap does NOT contain class name? It's not possible because 1-st pass should collect all classes 
				ArrayList<Object> entities = collectedMap.get(classProperty.getClassName());
				entities.add(entity);
			}
			if(!GeneralUtil.isEmpty(cell.toString())){
				isRowEmpty = false;
				pushCellToEntity(entity, cell, classProperty);
			}
			cellIdx++;
		}
		if(isRowEmpty)
			return null;
		return entity;
	}
	private void pushCellToEntity(Object entity, HSSFCell cell, ClassProperty classProperty) throws IllegalAccessException,	InvocationTargetException, NoSuchMethodException {
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
}
