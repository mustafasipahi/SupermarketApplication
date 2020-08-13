package com.application.view;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.application.controller.core.AdminController;
import com.application.controller.core.UserController;

@Component
public class Console implements CommandLineRunner {

	private Scanner scanner = new Scanner(System.in);

	@Autowired
	private AdminController adminController;

	@Autowired
	private UserController userController;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("*************WELCOME TO SUPERMARKET*************");
		while (true) {
			System.out.println("PRESS 1 for Admin Login");
			System.out.println("PRESS 2 for User Login");
			String value = scanner.next();
			
			//Integer Check for Input Value
			if (!isInteger(value)) {
				System.out.println("You Entered Incorrect Key. Please Enter Valid Number..");
				Thread.sleep(2000);
				continue;
			}
			//If Input 1, Actions to be Taken
			if (value.equalsIgnoreCase("1")) {
				adminOperation();
			}
			//If Input 2, Actions to be Taken
			else if (value.equalsIgnoreCase("2")) {
				userOperation();
			}
			//If Input is Incorrect, Actions to be Taken
			else {
				System.out.println("Incorrect Operation. Please Try Again..");
				Thread.sleep(2000);
			}
		}
	}

	public void adminOperation() throws Exception {
		adminController.runAdmin();
	}

	public void userOperation() throws Exception {
		userController.runUser();
	}

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
