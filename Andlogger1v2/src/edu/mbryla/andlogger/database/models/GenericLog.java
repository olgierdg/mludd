package edu.mbryla.andlogger.database.models;

/** Basic log structure
 * 
 * @author mbryla
 * @version 1.0
 */
public class GenericLog {
	private long id;
	private String timestamp;
	private String tag;
	private String data;
	
	public GenericLog(long id, String timestamp, String tag, String data) {
		this.id = id;
		this.timestamp = timestamp;
		this.tag = tag;
		this.data = data;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return this.timestamp + " | " + this.tag + " | " + this.data;
	}
}
