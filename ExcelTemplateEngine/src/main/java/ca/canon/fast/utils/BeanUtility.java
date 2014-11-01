package ca.canon.fast.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;




/**
 * Help utility wrapper for org.apache.commons.beanutils
 * v 1.0
 * 
 * Unlike apache commons BeanUtil will not overwrite properties with null values
 * @author Q05459
 *
 */
public class BeanUtility {
	private static Logger logger = Logger.getLogger(BeanUtility.class);

	static String[] defaultExludeProperties={"id","version","class"};
	/**
	 * Unlike BeanUtil will not overwrite properties with null values
	 * 
	 * @param src
	 * @param dst
	 * @param exludeProperties if null will use default (use empty array if don't want use even defaultExludeProperties)
	 * if 1st exclude property started with + add all properties to defaultExludeProperties 
	 */
	public static void nullSafeMergeTo(Object src, Object dst, String[] exludeProperties){
		if(exludeProperties == null){
			exludeProperties = defaultExludeProperties;
		}else if (exludeProperties[0].startsWith("+") ){
			String[] incomingExludeProperties = (String[]) ArrayUtils.clone(exludeProperties);
			exludeProperties = defaultExludeProperties;
			for (int i = 0; i < incomingExludeProperties.length; i++) {
				if(incomingExludeProperties[i].startsWith("+")){
					incomingExludeProperties[i] = incomingExludeProperties[i].substring(1);
				}
				exludeProperties = (String[]) ArrayUtils.add(exludeProperties, incomingExludeProperties[i]); 
			}
		}
		try {
			Map<String,Object> srcPropertyMap = PropertyUtils.describe(src);
			Map<String,Object> dstPropertyMap = PropertyUtils.describe(dst);
			Set<String> keys = srcPropertyMap.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				//logger.debug("key = "+key);
				if(ArrayUtils.contains(exludeProperties,key))
					continue;
				if(!dstPropertyMap.containsKey(key))
					continue;
				Object value = PropertyUtils.getProperty(src, key);
				if(value!=null && (value instanceof String) && ((String)value).length()!=0){
					if(PropertyUtils.isWriteable(dst, key))
					PropertyUtils.setProperty(dst, key, value);
				}else if(value!=null && (value instanceof List)){
					List srcList = (List)value;
					Class clazz = value.getClass();
					try {
						List dstList = (List)clazz.newInstance();
						for (Iterator iterator2 = srcList.iterator(); iterator2.hasNext();) {
							Object e = iterator2.next();
							dstList.add(e);
						}
						PropertyUtils.setProperty(dst, key, dstList);
					} catch (InstantiationException e) {
						logger.debug(e,e);
					} 
				}else if(value!=null){
					Object dstValue = PropertyUtils.getProperty(dst, key);
					if(PropertyUtils.isWriteable(dst, key)){
					if((value instanceof Integer) && ((Integer)value).intValue()==0){
						// do nothing to protect not null data
					}else{
						PropertyUtils.setProperty(dst, key, value);
					}
					}
				}
			}
		} catch (IllegalAccessException e) {
			logger.debug(e,e);
		} catch (InvocationTargetException e) {
			logger.debug(e,e);		
		} catch (NoSuchMethodException e) {
			logger.debug(e,e);		
		}
	}

}
