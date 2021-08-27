package com.qmetry.qaf.automation.support.flutter;

import com.qmetry.qaf.automation.core.QAFConfigurationListener;
import com.qmetry.qaf.automation.util.PropertyUtil;

/**
 * 
 * @author chirag.jayswal
 *
 */
public class ConfigurationListener implements QAFConfigurationListener{
	//@Override
	public void onLoad(PropertyUtil bundle) {
		bundle.load(getClass(), "flutter-strategy.properties");
	}

	//@Override
	public void onChange() {
	}

}
