/**
 * 
 */
package com.qmetry.qaf.automation.support.flutter;

import java.time.Duration;

import com.google.common.collect.ImmutableMap;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebElement;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;

/**
 * @author chirag.jayswal
 *
 */
public final class FlutterUtils {
	private FlutterUtils() {
		// static use only
	}

	public static FlutterElement getPageBackElement() {
		return new FlutterElement(new ByFlutter.PageBack());
	}
	public static void scrollIntoView(QAFExtendedWebElement ele) {
		ele.getWrappedDriver().executeScript("flutter:scrollIntoView", ele, ImmutableMap.of("alignment", 0.1));
	}

	public static Object scrollAndFind(QAFWebElement container, QAFWebElement ele) {
		for (int i = 0; i < 10; i++) {
			try {
				return scrollAndFind(container, ele, -50, -500);
			} catch (Exception e) {
			}
		}
		for (int i = 0; i < 20; i++) {
			try {
				return scrollAndFind(container, ele, 50, 500);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void scrollUntilVisible(QAFWebElement container, QAFWebElement ele, double alignment, double dxScroll,
			double dyScroll, Duration timeout) {
		((QAFExtendedWebElement) ele).getWrappedDriver().executeScript("flutter:scrollUntilVisible", container,
				ImmutableMap.of("item", ele, "alignment", alignment, "dx", dxScroll, "dy", dyScroll, "timeout",
						timeout.toString()));
	}

	private static Object scrollAndFind(QAFWebElement container, QAFWebElement ele, double dx, double dy) {

		((QAFExtendedWebElement) ele).getWrappedDriver().executeScript("flutter:scroll", container,
				ImmutableMap.of("dx", dx, "dy", dy, "durationMilliseconds", 200, "frequency", 60));

		return ((QAFExtendedWebElement) ele).getWrappedDriver().executeScript("flutter:waitFor", ele, 1);
	}

}
