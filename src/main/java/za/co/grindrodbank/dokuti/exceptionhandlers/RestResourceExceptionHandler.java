/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.exceptionhandlers;

import org.openapitools.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import za.co.grindrodbank.dokuti.exceptions.ChecksumFailedException;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.InvalidRequestException;
import za.co.grindrodbank.dokuti.exceptions.NotAuthorisedException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class RestResourceExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestResourceExceptionHandler.class);
    private static final String ERROR_LOG_STR = "Error : {} ";
    
    
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(RuntimeException ex) {
	    LOG.error(ERROR_LOG_STR, ex);
		ErrorResponse error = new ErrorResponse().message(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({InvalidRequestException.class, MaxUploadSizeExceededException.class})
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleInvalidRequestException(RuntimeException ex) {
	    LOG.error(ERROR_LOG_STR, ex);
		ErrorResponse error = new ErrorResponse().message(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotAuthorisedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponse> handleResourceNotAuthorisedException(RuntimeException ex) {
	    LOG.error(ERROR_LOG_STR, ex);
		ErrorResponse error = new ErrorResponse().message(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DatabaseLayerException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleDatabaseLayerException(RuntimeException ex) {
	    LOG.error(ERROR_LOG_STR, ex);
		ErrorResponse error = new ErrorResponse().message(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ChecksumFailedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleChecksumFailedException(RuntimeException ex) {
	    LOG.error(ERROR_LOG_STR, ex);
		ErrorResponse error = new ErrorResponse().message(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
}
