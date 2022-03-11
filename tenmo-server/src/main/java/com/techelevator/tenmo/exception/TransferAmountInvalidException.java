package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Transfer amount cannot be 0 or negative amount. ")
public class TransferAmountInvalidException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferAmountInvalidException() {
        super("Transfer amount cannot be 0 or negative amount. ");
    }

}


