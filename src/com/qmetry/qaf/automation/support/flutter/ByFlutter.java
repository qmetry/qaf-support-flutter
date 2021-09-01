/**
 * 
 */
package com.qmetry.qaf.automation.support.flutter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebElement;
import com.qmetry.qaf.automation.util.JSONUtil;
import com.qmetry.qaf.automation.util.StringUtil;

//import io.appium.java_client.AppiumDriver;

/**
 * @author chirag.jayswal
 *
 */
public class ByFlutter extends By {
	private final Gson gson = new Gson();
	// private String id;
	protected Map<String, Object> rawMap;

	public ByFlutter(String rawMap) {
		this(JSONUtil.toMap(rawMap));
	}

	public ByFlutter(Map<String, Object> rawMap) {
		// id = serialize(rawMap);
		this.rawMap = new HashMap<String, Object>(rawMap);
		// this.rawMap.put("firstMatchOnly", false);

	}

	@SuppressWarnings("unchecked")
	@Override
	public WebElement findElement(SearchContext context) {
		QAFExtendedWebElement ele = new QAFExtendedWebElement(this);
		//((AppiumDriver<WebElement>) ele.getWrappedDriver().getUnderLayingDriver()).context("FLUTTER");
		ele.setId(getId(context));
		try {
			if (WebElement.class.isAssignableFrom(context.getClass())) {
				Object res = ele.getWrappedDriver().executeScript("flutter:scrollUntilVisible", (WebElement) context,
						ele, 0, 0, 5, 5);
			}
			Object res = ele.getWrappedDriver().executeScript("flutter:waitFor", ele, 1);
			if (List.class.isAssignableFrom(res.getClass())) {
				return ((List<WebElement>) res).get(0);
			}

			return (WebElement) res;
		} catch (Exception e) {
			return ele;
			// throw new NoSuchElementException("Cannot locate an element using " +
			// ele.toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WebElement> findElements(SearchContext context) {

		QAFExtendedWebElement  ele = new QAFExtendedWebElement(this);
		//((AppiumDriver<WebElement>) ele.getWrappedDriver().getUnderLayingDriver()).context("FLUTTER");
		ele.setId(getId(context));
		try {
			if (WebElement.class.isAssignableFrom(context.getClass())) {
				Object res = ele.getWrappedDriver().executeScript("flutter:scrollUntilVisible",
						serialize(new Type("ListView").rawMap), serialize(rawMap), 0, 0, 5, 5);
			}
			Object res = ele.getWrappedDriver().executeScript("flutter:waitFor", ele, 1);
			if (List.class.isAssignableFrom(res.getClass())) {
				return (List<WebElement>) res;
			}
			if (WebElement.class.isAssignableFrom(res.getClass())) {
				Arrays.asList(res);
			}

		} catch (Exception e) {
			// Object res = ele.getWrappedDriver().executeScript("flutter:scrollIntoView",
			// ele);

			throw new NoSuchElementException("Cannot locate an element using " + ele.toString());
		}
		return Arrays.asList(ele);

	}

	private String getId(SearchContext context) {
		if (WebElement.class.isAssignableFrom(context.getClass())) {
			Map<String, Object> childElement = new HashMap<String, Object>();
			childElement.put("finderType", "Descendant");
			childElement.put("matchRoot", false);
			childElement.put("firstMatchOnly", true);

			/*
			 * new Type("Center").rawMap.forEach((k, v) -> { childElement.put("of_" + k, v);
			 * });
			 */
			childElement.put("of", gson.toJson(new Type("ListView").rawMap));

			childElement.put("matching", gson.toJson(rawMap));

			/*
			 * childElement.put("of_ELEMENT", ((RemoteWebElement) context).getId());
			 * rawMap.forEach((k, v) -> { childElement.put("matching_" + k, v); });
			 */
			// childElement.put("matching",rawMap);

			return serialize(childElement);
		}
		return serialize(rawMap);
	}

	private String encode(byte[] s) {
		return Base64.getEncoder().encodeToString(s);
	}

	private byte[] getBytes(Object o) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(byteOut);) {
			out.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return byteOut.toByteArray();
		return new Gson().toJson(o).replace('"', '\'').getBytes();
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
