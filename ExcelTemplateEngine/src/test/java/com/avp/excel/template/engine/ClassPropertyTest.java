package com.avp.excel.template.engine;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ClassPropertyTest {
	private static Logger logger = Logger.getLogger(ClassPropertyTest.class);
	@Test
	public void testClassProperty() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		TableDescriptor tableDescriptor = new TableDescriptor(); 
		String content = "${propertyName}";
		ClassProperty classProperty = new ClassProperty(null, content);
		assertNotNull("Smartass sayng: - classProperty should NOT be null.", classProperty);
	}
	@Test
	public void testClassProperty2() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		TableDescriptor tableDescriptor = new TableDescriptor(); 
		String content = "${propertyName:ca.canon.fast.model.impl.ActualDTO.id}";
		ClassProperty classProperty = new ClassProperty(null, content);
		assertNotNull("Smartass sayng: - classProperty should NOT be null.", classProperty);
		assertEquals("ca.canon.fast.model.impl.ActualDTO.id", classProperty.getReferencedEntity());
		
	}

}
