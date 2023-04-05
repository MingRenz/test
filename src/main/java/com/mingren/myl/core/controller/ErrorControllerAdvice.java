package com.mingren.myl.core.controller;

import com.mingren.myl.core.entity.Result;
import com.mingren.myl.core.exception.UnmessageException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(UnmessageException.class)
    public Result errorHandler(UnmessageException e){
        return Result.error(e.getMessage());
    }

}
