package com.avp.excel.template.engine;
/**
 * insert as optional '...,${com.avp.excel.template.engine.MonthToIntegerConvertor}'
 * @author ptitchkin
 *
 * January
 * February
 * March
 * April
 * May
 * June
 * July
 * August
 * September
 * October
 * November
 * December
 * 
 */
public class MonthToIntegerConvertor implements IConvertor {
	private String[] months = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"}; 
	public Object convert(Object input) {
		String monthName = ((String)input).toUpperCase();
		for (int i = 0; i < months.length; i++) {
			if(monthName.equals(months[i])){
				return new Integer(i);
			}
		}
		return new Integer(-1);
	}

}
