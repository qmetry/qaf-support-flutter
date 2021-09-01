/**
 * 
 */
package com.qmetry.qaf.automation.support.flutter.tests;

import org.hamcrest.Matchers;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qmetry.qaf.automation.support.flutter.ByFlutter;
import com.qmetry.qaf.automation.util.LocatorUtil;
import com.qmetry.qaf.automation.util.Validator;
import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

/**
 * @author chirag.jayswal
 *
 */
public class FlutterLoactorStretegyTestSuite {

	@Test(dataProvider = "flutterTestData")
	public void TestFlutterLoactorStretegy(String stretegyName,String value, By expectedResult) {
		Validator.assertThat(stretegyName + " available",getBundle().containsKey(stretegyName),Matchers.equalTo(true));

		By actual = LocatorUtil.getBy(String.format("%s=%s", stretegyName,value));
		Validator.assertThat(stretegyName + " should be used",actual.toString(),Matchers.equalToIgnoringCase(expectedResult.toString()));
	}
	
	@DataProvider(name = "flutterTestData")
	Object[][] seDp() {
		return new Object[][] {
			// String stretegyName,String value, By expectedResult
			new Object[] { "flutter-text", "my-text", new ByFlutter.Text("my-text") }, 
			new Object[] { "flutter-type", "my-type", new ByFlutter.Type("my-type") }, 
			new Object[] { "flutter-tooltip", "my-tooltip", new ByFlutter.TooltipMessage("my-tooltip") }, 
			new Object[] { "flutter-valuekey", "my-valuekey", new ByFlutter.ValueKey("my-valuekey") }
		};
	}

}
