/**
 * 
 */
package com.qmetry.qaf.automation.support.flutter;

import static com.qmetry.qaf.automation.support.flutter.FlutterUtils.scrollAndFind;
import static com.qmetry.qaf.automation.support.flutter.FlutterUtils.scrollIntoView;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebElement;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.qmetry.qaf.automation.util.JSONUtil;
import com.qmetry.qaf.automation.util.StringUtil;

//import io.appium.java_client.AppiumDriver;

/**
 * @author chirag.jayswal
 *
 */
public class ByFlutter extends By {
	private final Log logger = LogFactory.getLog(ByFlutter.class);

	private final Gson gson = new Gson();
	protected Map<String, Object> rawMap;

	public ByFlutter(String rawMap) {
		this(JSONUtil.toMap(rawMap));
	}

	public ByFlutter(Map<String, Object> rawMap) {
		this.rawMap = new HashMap<String, Object>(rawMap);
	}

	@Override
	public WebElement findElement(SearchContext context) {
		FlutterElement ele = new FlutterElement(this);
		// ((AppiumDriver<WebElement>)ele.getWrappedDriver().getUnderLayingDriver()).context("FLUTTER");
		Object res = getId(context);
		ele.setId(res.toString());
		try {
			try {
				res = ele.getWrappedDriver().executeScript("flutter:waitFor", ele, 1);
				scrollIntoView(ele);
			} catch (Exception e) {
				if (context!=null && QAFWebElement.class.isAssignableFrom(context.getClass())) {
					scrollAndFind((QAFWebElement)context, ele);
					scrollIntoView(ele);
				}
			}
			return ele;
		} catch (Exception e) {
			throw new NoSuchElementException("Cannot locate an element using " + ele.toString());
		}
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		logger.warn("Flutter driver doesn't support find list of elemetns. You will get list with only one element.");
		return Arrays.asList((WebElement) findElement(context));
	}


	protected String getId(SearchContext context) {
		if (context!=null && WebElement.class.isAssignableFrom(context.getClass())) {
			Map<String, Object> childElement = new HashMap<String, Object>();
			childElement.put("finderType", "Descendant");
			childElement.put("matchRoot", true);
			childElement.put("firstMatchOnly", true);

			childElement.put("of", new String(Base64.getDecoder().decode(((QAFExtendedWebElement) context).getId())));

			childElement.put("matching", JSONUtil.toString(rawMap));

			return serialize(childElement);
		}
		return serialize(rawMap);
	}

	@Override
	public String toString() {
		return "Flutter: " + JSONUtil.toString(rawMap);
	}

	public static class TooltipMessage extends ByFlutter {

		public TooltipMessage(String text) {
			super(ImmutableMap.of("finderType", "ByTooltipMessage", "text", text));
		}

		@Override
		public String toString() {
			return "Flutter Tooltip Message: " + rawMap.get("text");
		}
	}

	public static class Type extends ByFlutter {

		public Type(String type) {
			super(ImmutableMap.of("finderType", "ByType", "type", type));
		}

		@Override
		public String toString() {
			return "Flutter Type: " + rawMap.get("type");
		}
	}

	public static class Text extends ByFlutter {

		public Text(String text) {
			super(ImmutableMap.of("finderType", "ByText", "text", text));
		}

		@Override
		public String toString() {
			return "Flutter text: " + rawMap.get("text");
		}
	}

	public static class SemanticsLabel extends ByFlutter {

		public SemanticsLabel(String label) {
			super(ImmutableMap.of("finderType", "BySemanticsLabel", "isRegExp", false, "label", label));
		}

		@Override
		public String toString() {
			return "ByFlutter SemanticsLabel: " + rawMap.get("label");
		}
	}
	public static class SemanticsLabelRegEx extends ByFlutter {

		public SemanticsLabelRegEx(String label) {
			super(ImmutableMap.of("finderType", "BySemanticsLabel", "isRegExp", true, "label", label));
		}

		@Override
		public String toString() {
			return "ByFlutter SemanticsLabelRegEx: " + rawMap.get("label");
		}
	}

	public static class ValueKey extends ByFlutter {

		public ValueKey(String val) {
			super(ImmutableMap.of("finderType", "ByValueKey", "keyValueString", val, "keyValueType",
					StringUtil.isNumeric(val) ? "int" : "String"));
		}

		@Override
		public String toString() {
			return "Flutter value key: " + rawMap.get("keyValueString");
		}
	}
	
	public static class PageBack extends ByFlutter {

		public PageBack() {
			super(ImmutableMap.of("finderType", "PageBack"));
		}

		@Override
		public String toString() {
			return "Flutter PageBack";
		}
	}

	private String serialize(Map<String, Object> rawMap) {
		final JsonPrimitive INSTANCE = new JsonPrimitive(false);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		rawMap.forEach((key, value) -> {
			if (value instanceof String || value instanceof Integer || value instanceof Boolean) {
				tempMap.put(key, new JsonPrimitive(String.valueOf(value)));
			} else if (value instanceof JsonElement) {
				tempMap.put(key, value);
			} else {
				tempMap.put(key, INSTANCE);
			}
		});
		String mapJsonStringified = gson.toJson(rawMap);
		return Base64.getEncoder().encodeToString(mapJsonStringified.getBytes());
	}

}
