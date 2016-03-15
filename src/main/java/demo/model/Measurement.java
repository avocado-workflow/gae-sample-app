package demo.model;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Measurement {
	@Id
	private Long id;
	private Long startTime;
	private Long endTime;
	private String className;
	private String methodName;
	private Long duration;
	private Date timestamp = new Date();

	public Measurement(String className, String methodName) {
		super();
		this.className = className;
		this.methodName = methodName;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
		this.duration = endTime-startTime;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Long getDuration() {
		return duration;
	}
}
