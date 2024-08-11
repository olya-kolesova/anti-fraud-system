package antifraud;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<Object> handleJsonParseException(
            JsonParseException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }




}
