package ca.canon.fast.model.impl;

import com.avp.excel.template.engine.IConvertor;

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
				return Integer.valueOf(i);//new Integer(i);
			}
		}
		return Integer.valueOf(-1);//new Integer(-1);
	}

}
