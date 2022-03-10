package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
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

    //Get User Balance
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public BigDecimal get(Principal principal) {
        String userName = principal.getName();
        int userID = jdbcUserDao.findIdByUsername(userName);
        BigDecimal balance = jdbcUserDao.currentBalance(userID);

        return balance;
    }

    //Get List of Users (Make sure User has ID and name)
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> allUsers() {
        List<User> userList = jdbcUserDao.findAll();
        return userList;
    }



    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer addTransfer(@Valid @RequestBody Transfer transfer){
        Transfer transferBalance = jdbcUserDao.transfer(transfer);
        jdbcUserDao.balanceTransfer(transferBalance);
        return transferBalance;

    //TODO: Need to also change user account balances. Only Created transfers so far.

    }




    //Send TE Bucks to User
        //CANNOT send money to yourself
        //CANNOT send more than you have
        //CANNOT send negative or zero TE Bucks
        //Sender Account Decreased
        //Receiver Account Increased
        //Transfer has initial status of approved






}
