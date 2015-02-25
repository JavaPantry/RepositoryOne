package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * parse and store table descriptor (TODO - AP - json descriptor? flex json? gson?)
 * collection/map of cells in table 
 * Parse:
 * .{table:start;className:ca.canon.fast.model.impl.SalesUploadWeekFct}
 * OR .{table:start} 
 * OR .{table:end}
 * 
 * defaultClassName = ca.canon.fast.model.impl.SalesUploadWeekFct
 * @author ptitchkin
 *
 */
public class TableDescriptor extends Descriptor{

	private static Logger logger = Logger.getLogger(TableDescriptor.class);
	
	private int		index = 0; //current index in SERVICE_SHEET
	
	private boolean start = false;
	private boolean end = false;
	private String	defaultClassName = null;
	private HashMap<String, ClassProperty>	tablePropertyMap = new HashMap<String, ClassProperty>();	
	private String[]						arrayOfExcludedProperties = null;
	
	
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
	}
	
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public String getDefaultClassName() {return defaultClassName;}
	public void setDefaultClassName(String defaultClassName) {this.defaultClassName = defaultClassName;}

	public boolean isStart() {return start;}
	public boolean isEnd() {return end;}

	public HashMap<String, ClassProperty> getTablePropertyMap() {return tablePropertyMap;}
	public void setTablePropertyMap(HashMap<String, ClassProperty> tablePropertyMap) {this.tablePropertyMap = tablePropertyMap;}
}
