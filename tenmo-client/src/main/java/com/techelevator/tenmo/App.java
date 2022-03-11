package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private final AccountService accountService = new AccountService();


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        accountService.setCurrentUser(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        accountService.getCurrentBalance();
	}

	private void viewTransferHistory() {
        //TODO: Need to allow for getting details of transfer. Can copy sendBucks.
        Transfer[] transferHistory = accountService.getTransferHistory();

    }

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        int idSelection = -1;
        int fromUserId = Math.toIntExact((currentUser.getUser().getId()));
        Account fromUserAcct = accountService.getAccount(fromUserId);

        while (idSelection != 0) {
            User[] users = accountService.listUsers();
            idSelection = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
            for (User user : users) {
                if (user.getId() == idSelection) {
                    Account toUserAcct = accountService.getAccount(idSelection);
                    BigDecimal requestAmt = consoleService.promptForBigDecimal("Enter Amount: ");
                    Transfer newTransfer = new Transfer(2,2,fromUserAcct.getAcct_id(),toUserAcct.getAcct_id(),requestAmt);
                    accountService.sendTransfer(newTransfer);
                }
            }
        } consoleService.pause();
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
