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

			int value = scanner.nextInt();

			if (value == 1) {
				adminOperation();
			}
			if (value == 2) {
				userOperation();
			}
		}
	}

	public void adminOperation() throws Exception {
		adminController.runAdmin();
	}

	public void userOperation() throws Exception {
		userController.runUser();
	}
}
