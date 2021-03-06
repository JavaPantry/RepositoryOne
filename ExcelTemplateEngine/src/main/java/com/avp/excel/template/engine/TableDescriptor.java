package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.canon.fast.utils.GeneralUtil;

/**
 * parse and store table descriptor (TODO - AP - json descriptor? flex json? gson?)
 * collection/map of cells in table 
 * Parse:
 * .{table:start;className:ca.canon.fast.model.impl.SalesUploadWeekFct}
 * OR .{table:start} 
 * OR .{table:end}
 * 
 * table tag has className property it value goes into defaultClassName 
 * defaultClassName = ca.canon.fast.model.impl.SalesUploadWeekFct
 * 
 * foreign key definition:
 * 
 * .{table:start;className:ca.canon.fast.model.A}
 *  ${id}
 * .{table:end}
 * 
 *		.{table:start;className:ca.canon.fast.model.B
 *				fk:afid:ca.canon.fast.model.A.id}
 *			${id}
 *			${afid}
 *		.{table:end}
 *  OR
 *		.{table:start;className:ca.canon.fast.model.B}
 *			${id}
 *			${afid:ca.canon.fast.model.A.id}
 *		.{table:end}
 * 
 * 
 * - what about pure json?
 * 	.{start:true;defaultClassName:ca.canon.fast.model.B}
 *			${propertyName:id, primaryKey:true}
 *			${propertyName:afid,reference:ca.canon.fast.model.A.id}
 *			${propertyName:month,converter:ca.canon.fast.model.MonthConverter}
 *	.{table:end}
 * - that's real BS
 * 
 * 
 * @author ptitchkin
 *
 */
public class TableDescriptor extends Descriptor{

	private static Logger logger = Logger.getLogger(TableDescriptor.class);
	
	private boolean start = false;
	private boolean end = false;
	private String	defaultClassName = null;
	private HashMap<String, ClassProperty>	tablePropertyMap = new HashMap<String, ClassProperty>();

	/**
	 * 	store properties which will not copied from header bean to table entities
	 */
	private String[]						arrayOfExcludedProperties = null;

	private int rowIndex;
	
	
	public void updateExcludeFields(){
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
		
		arrayOfExcludedProperties = new String[exludeFields.size()];
		for (int i = 0; i < arrayOfExcludedProperties.length; i++) {
			arrayOfExcludedProperties[i] = exludeFields.get(i);					
		}
	}

	public TableDescriptor() {
		// dummy Auto-generated constructor stub for jUnit test
	}
	
	public TableDescriptor(String content) {
		content = super.stripDecoration(content);
		String[] tokenStream = content.split(TOKEN_DELIMITER);
		
		String[] tokens = tokenStream[0].split(TAG_VALUE_DELIMITER);
		//skip .{table:end} tag TODO - <AP> need to do something about closing tags when start processing 'inline' tables
		if(tokens[0].equalsIgnoreCase(TABLE) && tokens[1].equalsIgnoreCase(END)){
			end = true;
			return;
		}
		if(tokens[0].equalsIgnoreCase(TABLE) && tokens[1].equalsIgnoreCase(START)){
			start = true;
			if(tokenStream.length > 1){
				String[] className = tokenStream[1].split(TAG_VALUE_DELIMITER);
				if(className[0].equalsIgnoreCase(CLASS_NAME)){
					defaultClassName = className[1];
				}
			}
		}
		logger.debug("Create TableDescriptor ["+this.toString()+"]");
	}
	


	public String getDefaultClassName() {return defaultClassName;}
	public void setDefaultClassName(String defaultClassName) {this.defaultClassName = defaultClassName;}

	public boolean isStart() {return start;}
	public boolean isEnd() {return end;}

	public HashMap<String, ClassProperty> getTablePropertyMap() {return tablePropertyMap;}
	public void setTablePropertyMap(HashMap<String, ClassProperty> tablePropertyMap) {this.tablePropertyMap = tablePropertyMap;}

	public void setRowIndex(int rowIndex) {this.rowIndex = rowIndex;}
	public int getRowIndex() {return rowIndex;}

	public String[] getArrayOfExcludedProperties() {return arrayOfExcludedProperties;}
	public void setArrayOfExcludedProperties(String[] arrayOfExcludedProperties) {this.arrayOfExcludedProperties = arrayOfExcludedProperties;}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableDescriptor [start=").append(start)
				.append(", end=").append(end).append(", defaultClassName=")
				.append(defaultClassName).append(", tablePropertyMap=")
				.append(tablePropertyMap)
				.append(", arrayOfExcludedProperties=")
				.append(Arrays.toString(arrayOfExcludedProperties))
				.append(", rowIndex=").append(rowIndex).append("]");
		return builder.toString();
	}
	/**
	 * 
	 * @return not null list of ClassProperties which are reference to other table (can be empty)
	 */
	public List<ClassProperty> getReferences() {
		List<ClassProperty> references = new ArrayList<ClassProperty>();//tablePropertyMap
		Set<String> keys = tablePropertyMap.keySet();
		for (String key : keys) {
			ClassProperty classProperty = tablePropertyMap.get(key);
			if(!GeneralUtil.isEmpty(classProperty.getReferencedEntity())){
				references.add(classProperty);
			}
		}
		return references;
	}
}
