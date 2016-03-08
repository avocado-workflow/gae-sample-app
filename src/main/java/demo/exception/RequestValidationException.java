package demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.BAD_REQUEST)
public class RequestValidationException extends RuntimeException {

	private static final long serialVersionUID = 6689816224210750341L;

	public RequestValidationException(String msg) {
		super(msg);
	}
}
