package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.LoginDTO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private UserDao userDao;
    private JdbcUserDao jdbcUserDao;

    public UserController(UserDao userDao, JdbcUserDao jdbcUserDao) {
        this.userDao = userDao;
        this.jdbcUserDao = jdbcUserDao;
    }

    //Get User By AcctNumber
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public String getUserByAcctId( @PathVariable int id) {
        return jdbcUserDao.findUsernameByAcctId(id);
    }


    //Get User Balance
    @RequestMapping(path = "/accountbal", method = RequestMethod.GET)
    public BigDecimal get(Principal principal) {
        String userName = principal.getName();
        int userID = jdbcUserDao.findIdByUsername(userName);
        BigDecimal balance = jdbcUserDao.currentBalance(userID);

        return balance;
    }

    //Get Account of User Logged In
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account getAccount(Principal principal) {
        String userName = principal.getName();
        int userID = jdbcUserDao.findIdByUsername(userName);
        Account account = jdbcUserDao.getAccount(userID);

        return account;
    }

    //Get Account of User By User Id
    @RequestMapping(path = "/accountUser/{id}", method = RequestMethod.GET)
    public Account getAccountIdByUser(@PathVariable int id) {
        return jdbcUserDao.getAccount(id);
    }

    //Get Account of User By Acct Id
    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public Account getAccountId(@PathVariable int id) {
        return jdbcUserDao.getAccountByAcctId(id);
    }

    //Get List of Users (Make sure User has ID and name)
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> allUsers() {
        List<User> userList = jdbcUserDao.findAll();
        return userList;
    }


    //Transfer between accounts
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer addTransfer(@Valid @RequestBody Transfer transfer)  {
        Transfer transfered = jdbcUserDao.transfer(transfer);
        jdbcUserDao.balanceTransfer(transfered);
        return transfered;
    }


    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransfers(Principal principal)  {
        String userName = principal.getName();
        int userId = jdbcUserDao.findIdByUsername(userName);
        return jdbcUserDao.getTransfers(userId);
    }






    //Send TE Bucks to User
        //CANNOT send money to yourself
        //CANNOT send more than you have
        //CANNOT send negative or zero TE Bucks
        //Sender Account Decreased
        //Receiver Account Increased
        //Transfer has initial status of approved






}
