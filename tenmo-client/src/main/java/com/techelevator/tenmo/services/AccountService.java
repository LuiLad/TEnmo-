package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public AccountService() {
    }

    public AccountService(String userId){
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }


    public Account getAccount(int userID){
        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "accountUser/" + userID, HttpMethod.GET, makeAuthEntity(), Account.class);
            Account account = response.getBody();
            return account;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            return null;
        }
    }



//TODO: Need to make method to get list of transfers

//    public Account getTransferList(){
//        try {
//            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "account", HttpMethod.GET, makeAuthEntity(), Account.class);
//            Account account = response.getBody();
//            return account;
//        } catch (RestClientResponseException | ResourceAccessException e) {
//            BasicLogger.log(e.getMessage());
//            return null;
//        }
//    }

    public Transfer[] getTransferHistory() {
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfer", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            Transfer[] transferArray = response.getBody();
            String toFrom = "";
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.format("%-10s \n","Transfers");
            System.out.format("%-10s %-10s %-10s \n", "ID", "From/To","Amount");
            for (int i = 0; i < transferArray.length; i++) {
                if(transferArray[i].getAcctFrom() == getAccount(Math.toIntExact((currentUser.getUser().getId()))).getAcct_id() ){
                    toFrom = "From: " + getUsername(transferArray[i].getAcctTo());
                } else {
                    toFrom = "To:   " + getUsername(transferArray[i].getAcctFrom());

                }
                System.out.printf("%-10s %-10s %-10f \n", transferArray[i].getId(),toFrom,transferArray[i].getAmount());
            }
            System.out.println("---------------------------------------------------------------------------------------");
            return transferArray;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            return null;
        }
    }

    public void getCurrentBalance() {

        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "accountbal", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            BigDecimal currentBalance = response.getBody();
            System.out.println("Your current account balance is: $" + currentBalance);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("There was an error :(");
        }
    }

    public String getUsername(int acctId){
        try {
            ResponseEntity<String> response = restTemplate.exchange(API_BASE_URL + "/user/" + acctId, HttpMethod.GET, makeAuthEntity(), String.class);
            return response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            return null;
        }
    }


    public User[] listUsers() {
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "user", HttpMethod.GET, makeAuthEntity(), User[].class);
            User[] users = response.getBody();

            System.out.println("---------------------------------------------------------------------------------------");
            System.out.format("%-10s \n","Users");
            System.out.format("%-10s %-10s \n", "ID", "Name");
            for (int i = 0; i < users.length; i++) {
                if (!users[i].getUsername().equals(currentUser.getUser().getUsername())) {
                    System.out.format("%-10s %-10s \n", users[i].getId(),users[i].getUsername());
                }
            }
            System.out.println("---------------------------------------------------------------------------------------");
            return users;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("There was an error :(");
            return null;
        }
    }

    //TODO: Build Transfer Call

    public Transfer sendTransfer(Transfer transfer) {

        try {

            Transfer returnedTransfer = restTemplate.postForObject(API_BASE_URL + "transfer", makeAuthEntity(transfer), Transfer.class);

            System.out.println("Success! :)");
            return returnedTransfer;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("There was an error :(");
        }
            return null;
    }





    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeAuthEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);

    }






}
