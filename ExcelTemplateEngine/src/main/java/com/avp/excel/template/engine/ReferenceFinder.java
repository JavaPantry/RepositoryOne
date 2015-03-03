package com.avp.excel.template.engine;

/**
 * ReferenceFinder - Detects references in tables within one sheet
 * If table column has property referencedEntity
 * for example: referencedEntity:"ca.canon.fast.model.impl.SalesUploadWeekFct.id"
 * find instance of that class and request for either getter with type List<'CurrentClass'> or getter/setter with type 'CurrentClass'
 * @author Q05459
 *
 */
public class ReferenceFinder {
	
	/**
	 * 
	 */
	public ReferenceFinder(String content) {
	}

}
