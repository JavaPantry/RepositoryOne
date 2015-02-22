package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.List;

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
	private boolean valid = false;
	private String	defaultClassName = null;
	

	public TableDescriptor(String content) {
		content = super.stripDecoration(content);
		String[] tokenStream = content.split(TOKEN_DELIMITER);
		
		String[] tokens = tokenStream[0].split(TAG_VALUE_DELIMITER);
		//skip .{table:end} tag TODO - <AP> need to do something about closing tags when start processing 'inline' tables
		if(tokens[0].equalsIgnoreCase(TABLE) && tokens[1].equalsIgnoreCase(END))
			return;
		//if(tokens[0].equalsIgnoreCase(TABLE) && tokens[1].equalsIgnoreCase(START))
		if(tokenStream.length > 1){
			String[] className = tokenStream[1].split(TAG_VALUE_DELIMITER);
			if(className[0].equalsIgnoreCase(CLASS_NAME)){
				defaultClassName = className[1];
			}
		}
		valid = true;
	}
	
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public String getDefaultClassName() {return defaultClassName;}
	public void setDefaultClassName(String defaultClassName) {this.defaultClassName = defaultClassName;}
	public boolean isValid() {return valid;}
	public void setValid(boolean valid) {this.valid = valid;}

//	private List<TableDescriptor> tables = new ArrayList<TableDescriptor>();
//	public List<TableDescriptor> getTables() {return tables;}
//	public void setTables(List<TableDescriptor> tables) {this.tables = tables;}  
}
