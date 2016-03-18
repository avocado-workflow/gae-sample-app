package demo.util;

import org.springframework.stereotype.Component;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Measurement;

@Component
public class ProfilerImpl implements Profiler {

	@Override
	public void submitMeasurementAsync(Measurement m) {
		
		m.setEndTime(System.currentTimeMillis()); // TODO : moved here to decrease code pollution with these invocations
		
		ObjectifyService.ofy().save().entity(m);
	}
}
