import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.avp.excel.template.engine.ClassPropertyTest;
import com.avp.excel.template.engine.ExcelTemplateHelperTest;
import com.avp.excel.template.engine.ReferenceFinderTest;

@RunWith(Suite.class)
@SuiteClasses({
	ReferenceFinderTest.class
	,ExcelTemplateHelperTest.class
	,ClassPropertyTest.class
	//DealerGroupControllerTest.class
	//FastMonthForecastLifeCycleTest.class 
				//BeanUtilityTest.class
				//CommonProductActionTest.class 
				//SalesMonthFctControllerTest.class 
				//,ProductByAccountEditControllerTest.class 
				//,CommonSalesActionTest.class 
	})
public class ExcelTemplateEngineAllTests {
	 
}
