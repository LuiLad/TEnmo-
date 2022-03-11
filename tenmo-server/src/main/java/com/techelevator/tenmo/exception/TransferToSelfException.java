package com.techelevator.tenmo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_ACCEPTABLE, reason = "Cannot transfer to self. ")
public class TransferToSelfException extends Exception{
    private static final long serialVersionUID = 1L;

    public TransferToSelfException(){
        super("Cannot transfer to self. ");
    }

}
