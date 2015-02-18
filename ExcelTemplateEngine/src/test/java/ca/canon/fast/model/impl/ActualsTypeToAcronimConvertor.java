package ca.canon.fast.model.impl;

import com.avp.excel.template.engine.IConvertor;

/**
 * insert as optional '...,${com.avp.excel.template.engine.MonthToIntegerConvertor}'
 * @author ptitchkin
 *
Sell Trough
Forecast Sell Trough
On Hands
Purchased
Forecast

public interface IActualType {
	public static final String FORECAST_REGULAR = "R";  
	public static final String ACTUAL_SELL_PURCHASED = "P";
	public static final String ACTUAL_FORECAST_SELL_THROUGH = "FT"; //???
	public static final String ACTUAL_SELL_THROUGH = "T";  
	public static final String ACTUAL_SELL_ON_HANDS = "H";  
}


 * 
 */
public class ActualsTypeToAcronimConvertor implements IConvertor {
	private String[] types = {"SELL TROUGH","FORECAST SELL TROUGH","ON HANDS","PURCHASED","FORECAST"}; 
	private String[] acronims = {"T","FT","H","P","R"};
	public Object convert(Object input) {
		String monthName = ((String)input).toUpperCase();
		for (int i = 0; i < types.length; i++) {
			if(monthName.equals(types[i])){
				return acronims[i];
			}
		}
		return acronims[4];
	}

}
