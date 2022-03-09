package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    @Override
    public BigDecimal currentBalance(int userID) {
        String sql = "SELECT balance FROM account WHERE user_id = ?; ";
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userID);
        return balance;
    }

    @Override
    public Transfer transfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, account_from, amount) VALUES (?,?,?,?,?) RETURNING transfer_id;";
        Boolean success = false;
        int transferID = 0;
        Transfer insertedTransfer = null;

//TODO: Need to log errors for negative numbers and stuff
        if (transfer.getAcctFrom() != transfer.getAcctTo() && BigDecimal.valueOf(0).compareTo(transfer.getAmount()) < 0) {
           //Balance must be greater than transfer amount
            if (getAccount(transfer.getAcctFrom()).getBalance().compareTo(transfer.getAmount()) > 0) {
               try{
                    transferID = jdbcTemplate.queryForObject(sql,Integer.class,
                            transfer.getType(),
                            transfer.getStatus(),
                            getAccount(transfer.getAcctTo()).getAcct_id(),
                            getAccount(transfer.getAcctFrom()).getAcct_id(),
                            transfer.getAmount());
                   success = true;
               } catch (Exception e){
                   System.out.println("There Was An Error :(");
               }
            }
    }
        if(success){
            sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_to, account_from, amount FROM transfer WHERE transfer_id = ?";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql,transferID);
            if(result.next()){
                insertedTransfer = mapRowToTransfer(result);
            }

        }

        return insertedTransfer;
    }

//    @Override
//    public boolean postTransfer(Transfer transfer) {
//
//
//        return false;
//    }

    public Account getAccount(int acctId){
        String sql = "select account_id, user_id, balance from account WHERE account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, acctId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new UsernameNotFoundException("Account " + acctId + " was not found.");
    }



    //Mappings!!!!
    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }



    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAcct_id(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setType(rs.getInt("transfer_type_id"));
        transfer.setStatus(rs.getInt("transfer_status_id"));
        transfer.setAcctFrom(rs.getInt("account_to"));
        transfer.setAcctTo(rs.getInt("account_from"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }


}
