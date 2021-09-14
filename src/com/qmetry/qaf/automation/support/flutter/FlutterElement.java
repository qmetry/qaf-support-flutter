/**
 * 
 */
package com.qmetry.qaf.automation.support.flutter;

import static org.testng.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.ui.util.DynamicWait;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebDriver;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebComponent;

/**
 * @author chirag.jayswal
 *
 */
public class FlutterElement extends QAFWebComponent {

	public FlutterElement(FlutterElement parent, String locator) {
		super(parent, locator);
		assertTrue(getBy() instanceof ByFlutter, "FlutterElement supports only flutter locator strategies");
	}

	public FlutterElement(String locator) {
		super(locator);
		assertTrue(getBy() instanceof ByFlutter, "FlutterElement supports only flutter locator strategies");
	}
	
	public FlutterElement(ByFlutter by) {
		super(new WebDriverTestBase().getDriver(),by);
	}

	public FlutterElement(QAFExtendedWebDriver driver) {
		super(driver);
	}

	@Override
	public boolean isDisplayed() {
		return isPresent();
	}
	
	@Override
	public boolean isPresent() {
		if (StringUtils.isNotBlank(id) && id != "-1" && cacheable) {
			return true;
		}
		try {
			setId(((FlutterElement)((ByFlutter)getBy()).findElement(parentElement)).getId());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void waitForNotPresent(long... timeout) {
		if ((id == null) || (id == "-1")) {
			setId(((ByFlutter)getBy()).getId(parentElement));
		}
		long timeoutDuration = timeout!=null && timeout.length>0?timeout[0]:DynamicWait.getDefaultTimeout()/1000;
		getWrappedDriver().executeAsyncScript("flutter:waitForAbsent", this,timeoutDuration);
		setId(null);
	}

	@Override
	public void waitForPresent(long... timeout) {
		if (!isPresent()) {
			if ((id == null) || (id == "-1")) {
				setId(((ByFlutter) getBy()).getId(parentElement));
			}
			long timeoutDuration = timeout != null && timeout.length > 0 ? timeout[0]
					: DynamicWait.getDefaultTimeout() / 1000;
			try {
				getWrappedDriver().executeAsyncScript("flutter:waitFor", this, timeoutDuration);
				setId(((FlutterElement)((ByFlutter)getBy()).findElement(parentElement)).getId());
			} catch (Exception e) {
				setId(null);
				if (!isPresent())
				throw e;
			}
		}
	}
	
	public void scrollIntoView() {
		getWrappedDriver().executeScript("flutter:scrollIntoView", this, ImmutableMap.of("alignment", 0.1));
	}
	
	public void waitForTappable(long... timeout){
		if ((id == null) || (id == "-1")) {
			setId(((ByFlutter)getBy()).getId(parentElement));
		}
		long timeoutDuration = timeout!=null && timeout.length>0?timeout[0]:DynamicWait.getDefaultTimeout()/1000;
		getWrappedDriver().executeScript("flutter:waitForTappable", this, timeoutDuration);
	}

}
