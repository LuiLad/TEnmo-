package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Not enough funds available. ")
public class NotEnoughFundsException extends Exception{
    private static final long serialVersionUID = 1L;

    public NotEnoughFundsException(){
        super("Not enough funds available. ");
    }
}


