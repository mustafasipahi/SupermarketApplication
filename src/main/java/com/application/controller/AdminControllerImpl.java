package com.application.controller;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.controller.core.AdminController;
import com.application.model.Admin;
import com.application.model.Products;
import com.application.model.money.Currency;
import com.application.services.core.AdminService;
import com.application.services.core.ProductService;

@Component
public class AdminControllerImpl implements AdminController {	

	@Autowired
	private AdminService adminService;

	@Autowired
	private ProductService productService;
	
	private Scanner scanner = new Scanner(System.in);

	public void runAdmin() throws Exception {

		while (true) {
			//You Must Use to Username and Password
			System.out.println("(Admin Name : " + "admin" + "   " + "Password : " + "123)");
			System.out.println("PRESS q to Exit");

			System.out.println("Enter Admin Name : ");
			String name = scanner.next();

			if (name.equals("q")) {
				break;
			}

			System.out.println("Enter Admin Password : ");
			String oldpassword = scanner.next();

			//Integer Check for Input Value
			if (!isInteger(oldpassword)) {
				System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
				Thread.sleep(2000);
				continue;
			}
			
			//Integer Parse the Input Value
			int password = Integer.parseInt(oldpassword);
			for (Admin admin : getAdmin()) {
				if (name.equalsIgnoreCase(admin.getName()) && admin.getPassword() == password) {
					while (true) {
						System.out.println("Admin Operations : \n" + "1.Get All Product\n" + "2.Add Product\n"
								+ "3.Edit Product\n" + "4.Delete Product\n" + "q Exit");
						String operations = scanner.next();
						
						//If Input 1, Actions to be Taken
						if (operations.equals("1")) {
							
							//Show All Products
							printProduct();
							Thread.sleep(1000);
						} 
						
						//If Input 2, Actions to be Taken
						else if (operations.equals("2")) {
							System.out.println("Product Name : ");
							String productName = scanner.next();
							System.out.println("Product Price : ");
							String productPrice = scanner.next();
							
							//Integer Check for Input Value
							if (!isInteger(productPrice)) {
								System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
								Thread.sleep(2000);
								continue;
							}
							System.out.println("Product Quantity : ");
							String productQuantity = scanner.next();
							
							//Integer Check for Input Value
							if (!isInteger(productQuantity)) {
								System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
								Thread.sleep(2000);
								continue;
							}
							
							//Saving New Product to Database
							Products product = new Products();
							product.setName(productName);
							product.setPrice(Integer.parseInt(productPrice));
							product.setQuantity(Integer.parseInt(productQuantity));
							productService.save(product);
							System.out.println("Product Successfully Registered");
							Thread.sleep(1000);

						} 
						
						//If Input 3, Actions to be Taken
						else if (operations.equals("3")) {
							
							//Show All Products
							printProduct();
							System.out.println("Enter Product Name to Edit : ");
							String editName = scanner.next();
							
							//Query by Product Name
							Products selectedProducts = productService.findByName(editName);
							System.out.println("New Product Name : ");
							String productName = scanner.next();
							System.out.println("New Product Price : ");
							String productPrice = scanner.next();
							
							//Integer Check for Input Value
							if (!isInteger(productPrice)) {
								System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
								Thread.sleep(2000);
								continue;
							}
							System.out.println("New Product Quantity : ");
							String productQuantity = scanner.next();
							
							//Integer Check for Input Value
							if (!isInteger(productQuantity)) {
								System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
								Thread.sleep(2000);
								continue;
							}
							
							//Editing Product to Database
							productService.delete(selectedProducts);
							selectedProducts.setName(productName);
							selectedProducts.setPrice(Integer.parseInt(productPrice));
							selectedProducts.setQuantity(Integer.parseInt(productQuantity));
							productService.save(selectedProducts);
							System.out.println("Product Successfully Edited");
							Thread.sleep(1000);
						} 
						
						//If Input 4, Actions to be Taken
						else if (operations.equals("4")) {
							
							//Show All Products
							printProduct();
							System.out.println("Enter Product Name to Delete : ");
							String deleteName = scanner.next();
							Products deleteProduct = productService.findByName(deleteName);
							
							//Deleting Product to Database
							productService.delete(deleteProduct);
							System.out.println("Product Successfully Deleted");
							Thread.sleep(1000);
						} 
						
						//If Input q, Actions to be Taken
						else if (operations.equals("q")) {
							System.out.println("Exiting Admin Operations");
							Thread.sleep(2000);
							return;
						} else {
							System.out.println("You Entered Incorrect Key. Please Try Again..");
						}

					}
				}
			}
			System.out.println("Check Your Username or Password");
		}
	}

	public List<Admin> getAdmin() {
		return adminService.getAll();
	}

	public void printProduct() {
		for (Products products : getProducts()) {
			System.out.println("\t" + products.getName() + "\t" + "\t" + products.getPrice() + "\t" + Currency.EURO
					+ "\t" + "\t" + "x" + products.getQuantity() + " Qty");
		}
	}

	public List<Products> getProducts() {
		return productService.getAll();
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
