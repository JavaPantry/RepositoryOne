package com.avp.excel.template.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * parse and store table descriptor (TODO - AP - json descriptor? flex json? gson?)
 * collection/map of cells in table 
 * Parse:
 * .{table:start;className:ca.canon.fast.model.impl.SalesUploadWeekFct}
 * 
 * defaultClassName = ca.canon.fast.model.impl.SalesUploadWeekFct
 * @author ptitchkin
 *
 */
public class TableDescriptor {
	private static Logger logger = Logger.getLogger(TableDescriptor.class);
	
	private int		index = 0; //current index in SERVICE_SHEET
	private String	defaultClassName = null;
	

	public TableDescriptor(String content) {
		content = content.substring(2, content.length()-1); // remove starting '.{' and trailing '}'
		String[] descriptors = content.split(";");
		
		String[] cmd = descriptors[0].split(":");
		//if(cmd[0].equalsIgnoreCase("table") && cmd[1].equalsIgnoreCase("start"))
		if(descriptors.length > 1){
			String[] className = descriptors[1].split(":");
			if(className[0].equalsIgnoreCase("className")){
				defaultClassName = className[1];
			}
		}
	}
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public String getDefaultClassName() {return defaultClassName;}
	public void setDefaultClassName(String defaultClassName) {this.defaultClassName = defaultClassName;}

//	private List<TableDescriptor> tables = new ArrayList<TableDescriptor>();
//	public List<TableDescriptor> getTables() {return tables;}
//	public void setTables(List<TableDescriptor> tables) {this.tables = tables;}  
}
