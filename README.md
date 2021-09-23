[![License](https://img.shields.io/github/license/qmetry/qaf-support-flutter.svg)](http://www.opensource.org/licenses/mit-license.php)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qmetry/qaf-support-flutter/badge.svg)](https://mvnrepository.com/artifact/com.qmetry/qaf-support-flutter/latest)
[![GitHub tag](https://img.shields.io/github/tag/qmetry/qaf-support-flutter.svg)](https://github.com/qmetry/qaf-support-flutter/tags)
[![javadoc](https://javadoc.io/badge2/com.qmetry/qaf-support-flutter/javadoc.svg)](https://javadoc.io/doc/com.qmetry/qaf-support-flutter)
# qaf-support-flutter
qaf flutter native app support - additional locator strategies for flutter app using [appium-flutter-driver](https://github.com/appium-userland/appium-flutter-driver#appium-flutter-driver)

#### Pre-requisites
- Add [qaf-support-flutter](https://mvnrepository.com/artifact/com.qmetry/qaf-support-flutter/latest) dependency to your project
- Refer [installation and usage](https://github.com/appium-userland/appium-flutter-driver#installation) for setup appium flutter driver.

## Features
 - Custom locator stretegies specific to fluter driver
 - Auto scroll to view
 - Child element support


### locator strategies

| Flutter Driver API | locator strategy | Example |
| - | - | - |
| [bySemanticsLabel](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/bySemanticsLabel.html) | `flutter-label` `flutter-label-regex` | flutter-label=foo |
| [byTooltip](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/byTooltip.html) | `flutter-tooltip` | `flutter-tooltip=Increment` |
| [byType](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/byType.html) | `flutter-type` | `flutter-type=TextField` |
| [byValueKey](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/byValueKey.html) | `flutter-valuekey` | `flutter-valuekey=counter`|
| [text](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/text.html) | `flutter-text` | `flutter-text=foo` |
|rawmap |flutter-rawmap|`flutter-rawmap={"finderType":"PageBack"}`<br/><br/>`flutter-rawmap={"finderType":"Descendant","matching":"{\"finderType\":\"ByType\",\"type\": \"ListView\"}","of":"{\"finderType\":\"ByText\",\"text\": \"element text\"}"}`|
| [descendant](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/descendant.html) | :ok: | using child element (preferred) or `flutter-rawmap` |
| [pageBack](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/pageBack.html) | :ok: | [pageBack()](https://github.com/qmetry/qaf-support-flutter/blob/b2a7bf346b2c0266ca40864f0a7e76cae366e1d4/src/com/qmetry/qaf/automation/support/flutter/ByFlutter.java#L174) |
| [ancestor](https://api.flutter.dev/flutter/flutter_driver/CommonFinders/ancestor.html) | :not tested: |  Using `flutter-rawmap`|



## Limitations
Flutter driver doesn't support finding list of elements.


## Example
```
remote.server=localhost
remote.port=4723
driver.name=fluterDriver
fluter.capabilities.driverClass=io.appium.java_client.android.AndroidDriver
fluter.additional.capabilities= {\
  "app": "<app>",\
  "platformName": "Android",\
  "deviceName": "Android Emulator",\
  "automationName": "Flutter",\
  "retryBackoffTime": 2000,\
  "maxRetryCount": 5\
}
```

```
import static com.qmetry.qaf.automation.support.flutter.FlutterUtils.*;
import static com.qmetry.qaf.automation.ui.webdriver.ElementFactory.$;
import com.qmetry.qaf.automation.support.flutter.FlutterElement;


        @Test
	public void testNextRoute() {
		waitForFirstFrame();
	  FlutterElement parent = new FlutterElement("flutter-tooltip=counter_tooltip");
	  FlutterElement child = new FlutterElement(parent,"flutter-valuekey=counter");
	    
	    System.out.println("Child text:: " + child.getText());
	    
	    parent.waitForPresent();

		FlutterElement nextRoute = new FlutterElement("flutter-text=Go to next route");
		nextRoute.click();
	    parent.waitForNotPresent();

		// nextRoute.verifyNotPresent();

		FlutterElement list = new FlutterElement("flutter-type=ListView");

		list.verifyPresent("ListView Container");

		QAFWebElement entryC = list.findElement("flutter-text=Entry C");
		System.out.println(entryC.getText());

		QAFWebElement entryD = list.findElement("flutter-text=Entry D");
		System.out.println(entryD.getText());
		

		QAFExtendedWebElement textField = new QAFExtendedWebElement(list,"flutter-type=TextField");
		textField.sendKeys("flutter test");
		
		entryD = list.findElement("flutter-text=Entry D");
		entryD.verifyText("Entry D");

		//page back
		getPageBackElement().click();
		$("flutter-valuekey=counter").verifyPresent();
	
	}

        @Test
	public void contextTest() throws IOException {
                Set<String> contexts = ((AppiumDriver<WebElement>) getDriver().getUnderLayingDriver()).getContextHandles();
		System.out.println(contexts);
               //default context is FLUTTER
    
		Object res = getDriver().executeScript("flutter:checkHealth");
		System.out.println("flutter:checkHealth: " + res);
		res = getDriver().executeScript("flutter:getRenderTree");
		System.out.println("flutter:getRenderTree: " + res);
                res = getDriver().executeScript("flutter:getLayerTree");
		System.out.println("flutter:getLayerTree: " + res);
	

		QAFExtendedWebElement increment1 = new QAFExtendedWebElement(
				new ByFlutter("{'finderType':'ByTooltipMessage','text':'Increment'}"));
		increment1.click();
		increment1.click();

		increment1.assertPresent("Increment buttton");
		QAFExtendedWebElement counter = new QAFExtendedWebElement(
				new ByFlutter("{'finderType':'ByValueKey','keyValueString':'counter','keyValueType':'String'}"));

		counter.verifyText("2", "counter");

                // switch to Native 
		switchContext("NATIVE_APP");

		QAFExtendedWebElement content = new QAFExtendedWebElement("id=android:id/content");
		QAFExtendedWebElement increment2 = new QAFExtendedWebElement(content, "accessibility id=Increment");
		increment2.click();
		increment2.verifyVisible("Increment button");
		
		switchContext("FLUTTER");
		counter.verifyText("3", "counter");

		switchContext("NATIVE_APP");
		
		QAFExtendedWebElement content1 = new QAFExtendedWebElement("id=android:id/content");
		content1.verifyPresent("container");
		content1.findElement("accessibility id=Increment").click();

		switchContext("FLUTTER");

		counter.verifyText("4", "counter");

		QAFExtendedWebElement increment3 = new QAFExtendedWebElement("flutter-tooltip=Increment");
		increment3.click();

		counter.verifyText("5", "counter");

		//This will not work because parent is not in flutter context. Both parent and child requied to be from same (native/flutter) context
		//QAFExtendedWebElement increment4 = new QAFExtendedWebElement(content1, "flutter-tooltip=Increment");
		//increment4.click();

	}


  public void switchContext(String contextName) {
		((AppiumDriver<WebElement>) getDriver().getUnderLayingDriver()).context(contextName);
	}
	public void waitForFirstFrame() {
		getDriver().executeScript("flutter:waitForFirstFrame");
	}

```

