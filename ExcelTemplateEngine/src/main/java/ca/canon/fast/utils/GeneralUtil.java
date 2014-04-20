package ca.canon.fast.utils;



import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GeneralUtil {
	public static int find(Object[] array, Object obj){
		if (array == null || obj == null ) return -1;
		for (int i = 0; i < array.length; i++) {
			if(array[i].equals(obj))
				return i;
		}
		return -1;
	}
	public static int find(List list, Object obj) {
		if (list == null || obj == null ) return -1;
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(obj))
				return i;
		}
		return -1;
	}
	
	/**
	 * null safe check empty string
	 * GeneralUtil.isEmpty(str)
	 * @param (str == null || str.trim().length() == 0)
	 * @return
	 */
	public static boolean isEmpty(String str){
		return (str == null || str.trim().length() == 0);
	}
	/**
	 * GeneralUtil.isEmpty(
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(@SuppressWarnings("rawtypes") List list){return (list == null || list.size() == 0);}
	
	public static boolean isEmpty(Object[] array){ return (array == null || array.length == 0);}

	public static boolean isEmpty(Long id) {return (id == null || id == 0L);}
	public static boolean isEmpty(Integer id) {return (id == null || id == 0L);}
	
	/**
	 * comparison null with Long(0L) will return true 
	public static boolean isNullSafeEqual(Long l1,Long l2){
		if(l1==null && l2==null) return true;
		if(l1==null && l2 != null && l2.longValue()==0L) return true;
		if(l2==null && l1 != null && l1.longValue()==0L) return true;
		if(l2.longValue()==l1.longValue()) return true;
		return false;
	}
	 */
	public static boolean isNullSafeEqual(Long l1,Long l2){
		if(l1==null) l1 = new Long(0L);
		if(l2==null) l2 = new Long(0L);
		return l1.equals(l2);
	}

	public static boolean isNullSafeEqual(String s1,String s2){
		if(s1==null) s1 = new String("");
		if(s2==null) s2 = new String("");
		return s1.equals(s2);
	}
	
	public static String renderJsonArrayOfStrings(Object[] arrayOfStrings){
		if(isEmpty(arrayOfStrings))
			return null;
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < arrayOfStrings.length; i++) {
			if(i>0) b.append(",");
			b.append("'").append(arrayOfStrings[i].toString()).append("'");
		}
		b.append("]");
		return b.toString();
	}
	
	/**
	 * add element to java array
	 * 
	 * instead:
	 * 		List<FilterParameter> filters = null;
	 * 		if( !GeneralUtil.isEmpty(filterParameters) ){
	 * 			filters = Arrays.asList(filterParameters);
	 * 		}else{
	 * 			filters = new ArrayList<FilterParameter>();
	 * 		}
	 * 		FilterParameter filter = new FilterParameter();
	 * 		filter.setField("dealerGroupId");
	 * 		filter.setValue(dealerGroupId);
	 * 		filters.add(filter);
	 * 		filterParameters = new FilterParameter[filters.size()];// filters.toArray(); 
	 * 		for (int i = 0; i < filterParameters.length; i++) {
	 * 			filterParameters[i] = filters.get(i);
	 * 		}
	 * 
	 * 		@Test
	 * 		public void testGenericsArray(){
	 * 			FilterParameter[] srcArray = null; //srcArray = new FilterParameter[1];
	 * 			FilterParameter filterParameter = new FilterParameter();
	 * 			filterParameter.setField("a");
	 * 			filterParameter.setValue("avalue");
	 * 			srcArray = GeneralUtil.addToArray(FilterParameter.class, srcArray, filterParameter);
	 * 			assertEquals(1, srcArray.length);
	 * 			//srcArray[0] = filterParameter;
	 * 			FilterParameter filterParameter2 = new FilterParameter();
	 * 			filterParameter2.setField("b");
	 * 			filterParameter2.setValue("bvalue");
	 * 			srcArray = GeneralUtil.addToArray(FilterParameter.class, srcArray, filterParameter2);
	 * 			assertEquals(2, srcArray.length);
	 * 		}
	 * 
	 * 
	 *  http://stackoverflow.com/questions/4013683/creating-generic-arrays-in-java 
	 */
	public static <T> T[] addToArray(Class<T> clazz, T[] srcArray, T el) {
		List<T> list = new ArrayList<T>();
		if(srcArray != null){
			for (int i = 0; i < srcArray.length; i++) {
				list.add(srcArray[i]);
			}
		}
		list.add(el);
		srcArray = (T[]) Array.newInstance(clazz, list.size()); 
		for (int i = 0; i < srcArray.length; i++) {
			srcArray[i] = list.get(i);
		}
		return srcArray;
	}
}
