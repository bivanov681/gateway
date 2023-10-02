package com.currency.gateway.exception;

import com.currency.gateway.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = "Internal Server Error";
        String message = "Internal Server Error";

        if (ex instanceof DuplicateRequestException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Duplicate request!";
        }

        if (ex instanceof CurrencyNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            error = "Not Found";
            message = "Currency not found!";
        }

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status.value(), error, message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
