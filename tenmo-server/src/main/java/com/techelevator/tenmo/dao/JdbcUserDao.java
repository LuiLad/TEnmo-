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


    public String findUsernameByAcctId(int acctId) {
        String sql = "SELECT u.username FROM account a Left Join tenmo_user u ON u.user_id = a.user_id WHERE a.user_id = ?;";
        String username = jdbcTemplate.queryForObject(sql, String.class, acctId);
        if (username != null) {
            return username;
        } else {
            return "NotFound";
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()) {
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



    //Transfers

    @Override
    public Transfer transfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, account_from, amount) VALUES (?,?,?,?,?) RETURNING transfer_id;";
        Boolean success = false;
        int transferID = 0;
        Transfer insertedTransfer = null;
        int type = transfer.getType();
        int status = transfer.getStatus();
        int toAcct = transfer.getAcctTo();
        int fromAcct = transfer.getAcctFrom();
        BigDecimal amount = transfer.getAmount();

        if (transfer.getAcctFrom() != transfer.getAcctTo()) {
            if (BigDecimal.valueOf(0).compareTo(transfer.getAmount()) < 0) {

                //Balance must be greater than transfer amount
                if (getAccountByAcctId(transfer.getAcctFrom()).getBalance().compareTo(transfer.getAmount()) > 0) {
                    try {
                         transferID = jdbcTemplate.queryForObject(sql, Integer.class, type,status,toAcct,fromAcct,amount);
                        success = true;
                    } catch (Exception e) {
                        System.out.println("There Was An Error In TryCatch Block On Line 105 :(");

                        //TODO: Throw exceptions
                    } //throw
                } //throw
            }
        }
        if (success) {
            sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_to, account_from, amount FROM transfer WHERE transfer_id = ?";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferID);
            if (result.next()) {
                insertedTransfer = mapRowToTransfer(result);
            }

        }

        return insertedTransfer;
    }

    public boolean balanceTransfer(Transfer transfer) {
        String sql = "UPDATE account SET balance = balance - ? " +
                "WHERE account_id = ?;";
        try {
            jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAcctFrom());
        } catch (DataAccessException e) {
            return false;
        }

        sql = "UPDATE account SET balance = balance + ? " +
                "WHERE account_id = ?;";
        try {
            jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAcctTo());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }


    public List<Transfer> getTransfers(int userId){
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_type_id, transfer_status_id, account_to, account_from, amount \n" +
                "FROM transfer \n" +
                "LEFT JOIN account ON account.account_id = transfer.account_from\n" +
                "LEFT JOIN tenmo_user ON tenmo_user.user_id = account.user_id\n" +
                "WHERE \n" +
                "account.user_id = ?\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "SELECT transfer_type_id, transfer_status_id, account_to, account_from, amount \n" +
                "FROM transfer \n" +
                "LEFT JOIN account ON account.account_id = transfer.account_to\n" +
                "LEFT JOIN tenmo_user ON tenmo_user.user_id = account.user_id\n" +
                "WHERE \n" +
                "account.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }


    public Account getAccount(int userId) {
        String sql = "select account_id, user_id, balance from account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            return mapRowToAccount(rowSet);
        }
        throw new UsernameNotFoundException("User " + userId + " was not found.");
    }

    public Account getAccountByAcctId(int acctId) {
        String sql = "select account_id, user_id, balance from account WHERE account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, acctId);
        if (rowSet.next()) {
            return mapRowToAccount(rowSet);
        }
        throw new UsernameNotFoundException("Account " + acctId + " was not found by acct ID.");
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
