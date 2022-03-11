package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.NotEnoughFundsException;
import com.techelevator.tenmo.exception.TransferAmountInvalidException;
import com.techelevator.tenmo.exception.TransferToSelfException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal currentBalance(int userID);

    Transfer transfer(Transfer transfer) throws TransferToSelfException, TransferAmountInvalidException, NotEnoughFundsException;

 //  boolean postTransfer(Transfer transfer);


}
