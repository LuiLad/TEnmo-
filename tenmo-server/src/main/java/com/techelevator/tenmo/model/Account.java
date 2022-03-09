package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int acct_id;
    private int userId;
    private BigDecimal balance;
    private Transfer[] transfers;



    public int getAcct_id() {
        return acct_id;
    }

    public void setAcct_id(int acct_id) {
        this.acct_id = acct_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
