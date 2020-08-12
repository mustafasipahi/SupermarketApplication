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
public class AdminControllerImpl implements AdminController{

	private Scanner scanner = new Scanner(System.in);

	@Autowired
	private AdminService adminService;

	@Autowired
	private ProductService productService;

	public void runAdmin() throws Exception{

		while (true) {
			System.out.println("(Admin Name : " + "admin" + "   " + "Password : " + "123)");
			System.out.println("PRESS q to Exit");

			System.out.println("Enter Admin Name : ");
			String name = scanner.next();

			if (name.equals("q")) {
				break;
			}

			System.out.println("Enter Admin Password : ");
			int password = scanner.nextInt();

			for (Admin admin : getAdmin()) {
				if (name.equals(admin.getName()) && admin.getPassword() == password) {
					while (true) {
						System.out.println("Admin Operations : \n" + "1.Get All Product\n" + "2.Add Product\n"
								+ "3.Edit Product\n" + "4.Delete Product\n" + "q Exit");
						String operations = scanner.next();
						if (operations.equals("1")) {
							printProduct();
							Thread.sleep(1000);
						} else if (operations.equals("2")) {
							System.out.println("Product Name : ");
							String productName = scanner.next();
							System.out.println("Product Price : ");
							int productPrice = scanner.nextInt();
							System.out.println("Product Quantity : ");
							int productQuantity = scanner.nextInt();
							Products product = new Products();
							product.setName(productName);
							product.setPrice(productPrice);
							product.setQuantity(productQuantity);
							productService.save(product);
							System.out.println("Product Successfully Registered");
							Thread.sleep(1000);

						} else if (operations.equals("3")) {
							printProduct();
							System.out.println("Select Product to Edit : ");
							int editId = scanner.nextInt();
							Products selectedProducts = productService.findById(editId);
							System.out.println("New Product Name : ");
							String productName = scanner.next();
							System.out.println("New Product Price : ");
							int productPrice = scanner.nextInt();
							System.out.println("New Product Quantity : ");
							int productQuantity = scanner.nextInt();
							productService.delete(selectedProducts);
							selectedProducts.setName(productName);
							selectedProducts.setPrice(productPrice);
							selectedProducts.setQuantity(productQuantity);
							productService.save(selectedProducts);
							System.out.println("Product Successfully Edited");
							Thread.sleep(1000);
						} else if (operations.equals("4")) {
							printProduct();
							System.out.println("Select Product to Delete : ");
							int deleteId = scanner.nextInt();
							Products deleteProduct = productService.findById(deleteId);
							productService.delete(deleteProduct);
							System.out.println("Product Successfully Deleted");
							Thread.sleep(1000);
						} else if (operations.equals("q")) {
							System.out.println("Exiting Admin Operations");
							Thread.sleep(2000);
							return;
						} else {
							System.out.println("You Entered Incorrect Key. Please Try Again.");
						}

					}
				}
			}
			System.out.println("Check Your Username or Password.");
		}
	}

	public List<Admin> getAdmin() {
		return adminService.getAll();
	}

	public void printProduct() {
		for (Products products : getProducts()) {
			System.out.println("\t" + products.getName() + "\t" + products.getPrice() + Currency.EURO + "\t"
					+ products.getQuantity() + " Qty");
		}
	}

	public List<Products> getProducts() {
		return productService.getAll();
	}
}
