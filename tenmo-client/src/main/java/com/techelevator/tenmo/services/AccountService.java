package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public void getCurrentBalance() {

        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "account", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            BigDecimal currentBalance = response.getBody();
            System.out.println("Your current account balance is: $" + currentBalance);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("There was an error :(");
        }
    }

    public void listUsers() {
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
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("There was an error :(");
        }

    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }


}
