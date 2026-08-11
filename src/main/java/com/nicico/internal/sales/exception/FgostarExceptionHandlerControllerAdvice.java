package com.nicico.internal.sales.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionalException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FgostarExceptionHandlerControllerAdvice {

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		log.warn("[{}] {} — {}", status.value(), request.getRequestURI(), message);
		return ResponseEntity.status(status).body(new ErrorResponse(status, message, request.getRequestURI()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> constrainViolationException(
			ConstraintViolationException ex, HttpServletRequest request) {
		String message = ex.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining(", "));
		return build(HttpStatus.UNPROCESSABLE_ENTITY, message, request);
	}

	@ExceptionHandler(TransactionalException.class)
	public ResponseEntity<ErrorResponse> transactionException(
			TransactionalException ex, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
	}

//	@ExceptionHandler(InternalSaleCustomException.ValidationException.class)
//	public ResponseEntity<ErrorResponse> validationException(
//			InternalSaleCustomException.ValidationException ex, HttpServletRequest request) {
//		return build(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), request);
//	}



	@ExceptionHandler(InternalSaleCustomException.DuplicateEntityException.class)
	public ResponseEntity<ErrorResponse> duplicateEntityException(
			InternalSaleCustomException.ValidationException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), request);
	}




	@ExceptionHandler(InternalSaleCustomException.ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> resourceNotFoundException(
			InternalSaleCustomException.ResourceNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

//	@ExceptionHandler(InternalSaleCustomException.AccessDeniedException.class)
//	public ResponseEntity<ErrorResponse> accessDeniedException(
//			InternalSaleCustomException.AccessDeniedException ex, HttpServletRequest request) {
//		return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
//	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> springAccessDeniedException(
			AccessDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
	}


	/// /    @ExceptionHandler(InternalSaleCustomException.ResourceUnauthorizedException.class)
	/// /    public ResponseEntity<ErrorResponse> resourceUnauthorizedException(
	/// /            InternalSaleCustomException.ResourceUnauthorizedException ex, HttpServletRequest request) {
	/// /        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	/// /    }

	@ExceptionHandler(InternalSaleCustomException.FileContentException.class)
	public ResponseEntity<ErrorResponse> fileContentException(
			InternalSaleCustomException.FileContentException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
	}

	@ExceptionHandler(InternalSaleCustomException.ParentNotFoundException.class)
	public ResponseEntity<ErrorResponse> parentNotFoundException(
			InternalSaleCustomException.ParentNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(InternalSaleCustomException.ExcelException.class)
	public ResponseEntity<ErrorResponse> excelException(
			InternalSaleCustomException.ExcelException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(
			Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception at {}", request.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "خطای داخلی سرور", request);
	}

	@Getter
	public static class ErrorResponse {
		private final long timestamp = Instant.now().toEpochMilli();
		private final int status;
		private final String message;
		private final String path;

		public ErrorResponse(HttpStatus httpStatus, String message, String path) {
			this.status = httpStatus.value();
			this.message = message;
			this.path = path;
		}
	}
}