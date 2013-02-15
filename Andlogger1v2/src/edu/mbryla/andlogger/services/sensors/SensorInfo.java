package edu.mbryla.andlogger.services.sensors;

public class SensorInfo {
	private int type;
	private String databaseTag;
	
	private boolean registered;
	private String latestReading;	
	
	public SensorInfo(int type, String databaseTag) {
		this.type = type;
		this.databaseTag = databaseTag;
		
		this.registered = true;
	}

	public String getDatabaseTag() {
		return databaseTag;
	}

	public boolean isRegistered() {
		return registered;
	}
	
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public int getType() {
		return type;
	}

	public String getLatestReading() {
		return latestReading;
	}
	
	public void updateLatestReading(String newReading) {
		this.latestReading = newReading;
	}
	
	@Override
	public boolean equals(Object si) {
		if(si instanceof SensorInfo)
			if(((SensorInfo)si).getType() == this.type)
				return true;
		
		return false;		
	}
	
	@Override
	public int hashCode() {
		return this.type;
	}
}
