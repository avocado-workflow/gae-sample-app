package demo.util;

import demo.model.Measurement;

public interface Profiler {

	void submitMeasurementAsync(Measurement m);
}
