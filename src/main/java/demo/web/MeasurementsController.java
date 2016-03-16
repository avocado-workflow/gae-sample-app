package demo.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Measurement;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

	@RequestMapping(method=RequestMethod.GET)
	public Iterable<Measurement> getAllOrderedByTimestamp() {
		// FIXME : just a quick try
		return ObjectifyService.ofy().load().type(Measurement.class).order("-timestamp").limit(100).list();
	}
}
