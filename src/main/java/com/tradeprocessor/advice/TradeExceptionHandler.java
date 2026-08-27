package com.tradeprocessor.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.tradeprocessor.domain.ErrorResponse;
import com.tradeprocessor.exception.TradeValidationException;
import com.tradeprocessor.exception.UnsupportedInstrumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class TradeExceptionHandler {
  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJson(
      org.springframework.http.converter.HttpMessageNotReadableException e,
      HttpServletRequest request) {
    ErrorResponse body = ErrorResponse.of(400, "Bad Request",
        "Malformed request body: " + e.getMostSpecificCause().getMessage(),
        request.getRequestURI());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(TradeValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(TradeValidationException e,
      HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.of(400, "Bad Request", e.getMessage(), request.getRequestURI());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(UnsupportedInstrumentException.class)
  public ResponseEntity<ErrorResponse> handleUnsupported(UnsupportedInstrumentException e,
      HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.of(422, "Unprocessable Entity", e.getMessage(), request.getRequestURI());
    return ResponseEntity.unprocessableEntity().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception e, HttpServletRequest request) {
    log.error("Unexpected error processing request {}", request.getRequestURI(), e);
    ErrorResponse body =
        ErrorResponse.of(500, "Internal Server Error", "Unexpected error", request.getRequestURI());
    return ResponseEntity.internalServerError().body(body);
  }
}
