package com.avp.excel.template.engine;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import ca.canon.fast.model.impl.MonthToIntegerConvertor;

public class ClassPropertyTest {
	private static Logger logger = Logger.getLogger(ClassPropertyTest.class);

	@Test
	public void testClassProperty2() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		logger.debug("ClassPropertyTest starts... ... ...");
		TableDescriptor tableDescriptor = new TableDescriptor();
		String content = "${className:\"\",propertyName:\"ca.canon.fast.model.impl.SalesUploadWeekFct.itemCode\""
				+ ",referencedEntity:\"ca.canon.fast.model.impl.ActualDTO.id\""
				+ ",convertorClassName:\"ca.canon.fast.model.impl.MonthToIntegerConvertor\""
				+ "}";
		content = Descriptor.stripPrefixChar(content);
		ClassProperty classProperty = Descriptor.json2Obj(content, ClassProperty.class);
		assertNotNull("Smartass sayng: - classProperty should NOT be null.", classProperty);
		assertEquals("ca.canon.fast.model.impl.ActualDTO.id", classProperty.getReferencedEntity());
		classProperty.init(null);
		assertEquals("itemCode", classProperty.getPropertyName());
		assertEquals("ca.canon.fast.model.impl.SalesUploadWeekFct", classProperty.getClassName());
		assertTrue(classProperty.getConvertor() instanceof MonthToIntegerConvertor);
		
		
		logger.debug("ClassPropertyTest ends succesfully.");
	}
}
