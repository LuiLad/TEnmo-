package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transfer {

    @JsonProperty("transfer_id")
    private int id;
    @JsonProperty("transfer_type_id")
    private int type; //send receive
    @JsonProperty("transfer_status_id")
    private int status; //approved, ect.
    @JsonProperty("account_from")
    private int acctFrom;
    @JsonProperty("account_to")
    private int acctTo;
    private BigDecimal amount;
    

    //Constructor


    //Getters & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAcctFrom() {
        return acctFrom;
    }

    public void setAcctFrom(int acctFrom) {
        this.acctFrom = acctFrom;
    }

    public int getAcctTo() {
        return acctTo;
    }

    public void setAcctTo(int acctTo) {
        this.acctTo = acctTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
