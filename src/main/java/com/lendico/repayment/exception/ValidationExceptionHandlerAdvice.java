package com.lendico.repayment.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@ControllerAdvice(basePackages = "com.lendico.repayment")
@Slf4j
public class ValidationExceptionHandlerAdvice {

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handle(RuntimeException exception) {
        log.warn("Encountered exception while processing request", exception);
        ResponseStatus annotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        HttpStatus status = getStatus(annotation);
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("reason", exception.getMessage());
        body.put("exception", exception.getClass());
        // In real scenario We will add stacktrace for QA/DEV environment only.
        body.put("stacktrace", Arrays.toString(exception.getStackTrace()));
        return new ResponseEntity(body, status);
    }

    private HttpStatus getStatus(ResponseStatus annotation) {
        HttpStatus resultStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (annotation != null) {
            Map<String, Object> attr = AnnotationUtils.getAnnotationAttributes(annotation);
            resultStatus = (HttpStatus)attr.get("value");
        } else {
            log.warn("ResponseCode is not defined, sending HTTP 500 back.");
        }

        return resultStatus;
    }

}
