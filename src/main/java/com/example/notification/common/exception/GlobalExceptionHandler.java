package com.example.notification.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource ;
    private String getMessage(String key) {
        return messageSource.getMessage(
                key,
                null,
                "System error, please try again later",
                LocaleContextHolder.getLocale()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = getMessage("MethodArgumentNotValidException.message") ;
        List<String> systemMessage = exception
                .getBindingResult()
                .getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList()) ;
        int code = 400 ;
        ErrorResponse errorResponse = new ErrorResponse(message,systemMessage,code) ;
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST) ;
    }


    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException exception) {
        String message = getMessage("BusinessException.message") ;
        List<String> systemMessage = exception.getMessage().lines().toList() ;
        int code = 400 ;
        ErrorResponse errorResponse = new ErrorResponse(message,systemMessage,code) ;
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST) ;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception exception) {
        String message = getMessage("Exception.message") ;
        List<String> systemMessage = exception.getMessage().lines().toList();
        int code = 500 ;
        ErrorResponse errorResponse = new ErrorResponse(message,systemMessage,code) ;
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

}
