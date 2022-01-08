package com.ecommerceapi.ecomerceapi.exception;

import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {

    /** Handle Argument Not Valid */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->{

            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INCOMPLETE_DATA);
        responseDTO.setInfo("Incomplete Request");
        responseDTO.setData(errors);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Message Not Readable */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_ERROR);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Method Not Allowed */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INCOMPLETE_DATA);
        responseDTO.setInfo(ex.getMethod() + " Method Not Allowed");
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Media Not Acceptable */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INVALID_FORMAT);
        responseDTO.setInfo("Media Not Acceptable");
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Media Not Support */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INVALID_FORMAT);
        responseDTO.setInfo("Media Not Support");
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle No Handler Found */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String,String> error = new HashMap<>();
        error.put("path", request.getContextPath());
        error.put("message", ex.getMessage());

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
        responseDTO.setInfo("The URL you have reached is not in service at this time (404).");
        responseDTO.setData(error);

        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }

    /** Handle Request Part */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INVALID_FORMAT);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Request Params */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INVALID_FORMAT);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Max Upload */
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleFileSizeException(MaxUploadSizeExceededException ex) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INVALID_FORMAT);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);
    }

    /** Handle Validation */
    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->{

            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_INCOMPLETE_DATA);
        responseDTO.setInfo("Incomplete Request Validation");
        responseDTO.setData(errors);

        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    /** Handle Not Found Data */
    @ExceptionHandler({ResultNotFoundException.class})
    public ResponseEntity<Object> handleResultNotFoundException(ResultNotFoundException ex) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }

    /** Handle Existing Data */
    @ExceptionHandler({ResultExistException.class})
    public ResponseEntity<Object> handleResultExistServiceException(ResultExistException ex){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_EXISTING_DATA);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
    }

    /** Handle Server Error */
    @ExceptionHandler({ResultServiceException.class})
    public ResponseEntity<Object> handleResultServiceException(ResultServiceException ex){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ConstantUtil.STATUS_ERROR_SYSTEM);
        responseDTO.setInfo(ex.getMessage());
        responseDTO.setData(null);

        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
