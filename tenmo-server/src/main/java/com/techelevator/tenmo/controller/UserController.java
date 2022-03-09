package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.LoginDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private UserDao userDao;
    private JdbcUserDao jdbcUserDao;

    public UserController(UserDao userDao, JdbcUserDao jdbcUserDao) {
        this.userDao = userDao;
        this.jdbcUserDao = jdbcUserDao;
    }


    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public BigDecimal get(Principal principal) {
        String userName = principal.getName();
        int userID = jdbcUserDao.findIdByUsername(userName);
        BigDecimal balance = jdbcUserDao.currentBalance(userID);

        return balance;
    }


}
