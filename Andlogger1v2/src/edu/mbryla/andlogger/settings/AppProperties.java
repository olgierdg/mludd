package edu.mbryla.andlogger.settings;

import java.util.Properties;

public class AppProperties {
	public static Properties timingProperties;
	public static Properties previewProperties;	
	
	static {
		previewProperties = new Properties();
		previewProperties.put("preview-rate-ms", Long.valueOf(3 * 1000));
		previewProperties.put("preview-logs-limit", 15);
		
		timingProperties = new Properties();		
		timingProperties.put("collect-wifi-rate-ms", Long.valueOf(40 * 1000));
		timingProperties.put("collect-bt-rate-ms", Long.valueOf(20 * 1000));		
		timingProperties.put("collect-sensors-rate-ms", Long.valueOf(5 * 1000));
		timingProperties.put("collect-location-rate-ms", Long.valueOf(1 * 60 * 1000));
	}
}
