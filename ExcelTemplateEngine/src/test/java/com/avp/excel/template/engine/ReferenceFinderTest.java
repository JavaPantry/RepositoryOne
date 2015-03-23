package com.avp.excel.template.engine;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReferenceFinderTest {

	private List<TableDescriptor> descriptorTables;
	private Map<String, ArrayList<Object>> collectedBeansFromTablesAsMap;

	@Before
	public void setUp() throws Exception {
		descriptorTables = new ArrayList<TableDescriptor>();
		collectedBeansFromTablesAsMap = new HashMap<String, ArrayList<Object>>();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReferenceFinder() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ReferenceFinder.linkTables(descriptorTables, collectedBeansFromTablesAsMap);
	}

}
