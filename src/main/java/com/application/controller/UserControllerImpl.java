package com.application.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.controller.core.UserController;
import com.application.dto.ProductDTO;
import com.application.model.CartItem;
import com.application.model.Products;
import com.application.model.money.Currency;
import com.application.services.core.ProductService;

@Component
public class UserControllerImpl implements UserController {

	private Scanner scanner = new Scanner(System.in);

	private List<CartItem> productsinCart;

	@Autowired
	private ProductService productService;

	public void runUser() throws Exception {

		System.out.println("Please Enter Your Name : ");
		String userName = scanner.next();
		if (userName.isEmpty()) {
			System.out.println("Your Name Can't be Blank");
			return;
		}
		System.out.println("Please Enter Your Budget (EURO) : ");
		int haveMoney = scanner.nextInt();
		if (haveMoney == 0 || haveMoney < 10) {
			System.out.println("Your Budget is Insufficient");
			return;
		}
		while (true) {

			if (productsinCart == null) {
				productsinCart = new ArrayList<CartItem>();
			}

			System.out.println("Products in Market : ");
			printProduct();

			System.out.println("User Operation : \n" + "1.View Your Cart\n" + "2.Add Product to Your Cart\n"
					+ "3.Delete Product From Your Cart\n" + "4.Edit Your Cart\n" + "5.Your Remaining Balance\n"
					+ "6.Proceed to Checkout\n" + "q Exit Shopping\n");
			String operation = scanner.next();

			if (operation.equals("1")) {
				System.out.println(userName + "'s Cart : ");
				printItem();
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty.");
					Thread.sleep(2000);
					continue;
				}
			} else if (operation.equals("2")) {
				System.out.println("Enter Product Name to Add to Cart : ");
				String item = scanner.next();
				if (!checkProductNameinDB(item)) {
					System.out.println("This Product isn't in Stock. Please Try Different Product. ");
					Thread.sleep(2000);
					continue;
				}
				if (checkProductNameinCart(item)) {
					System.out.println("You Already Have This Product In Your Cart.");
					System.out.println("You Can Edit Your Cart.");
					Thread.sleep(2000);
					continue;
				}
				System.out.println("Enter Quantity You Want to Add to Cart : ");
				int quantity = scanner.nextInt();

				if (quantity == 0) {
					System.out.println("Please Enter a Valid Quantity..");
					Thread.sleep(2000);
					continue;
				}
				if (!checkProductPiecesinDB(item, quantity)) {
					System.out.println("Insufficient Stock. You Can Reduce the Quantity or Enter Different Product");
					Thread.sleep(2000);
					continue;
				}
				if (calculateProductCost(item, quantity) > haveMoney) {
					System.out.println("Your Balance is Insufficient.");
					Thread.sleep(2000);
					continue;
				}
				Products selectedProducts = productService.findByName(item);
				CartItem cartItem = new CartItem(selectedProducts, quantity);
				addItem(cartItem);
				System.out.println("Adding Product to Cart. Please Wait..");
				Thread.sleep(2000);
				System.out.println("Product Successfully Added to Your Cart");
				haveMoney = haveMoney - calculateProductCost(item, quantity);
				selectedProducts.setQuantity(selectedProducts.getQuantity() - quantity);
				productService.save(selectedProducts);
				if (selectedProducts.getQuantity() == 0) {
					productService.delete(selectedProducts);
				}
				Thread.sleep(2000);
			} else if (operation.equals("3")) {
				printItem();
				if (productsinCart.isEmpty()) {
					System.out.println("Your Cart is Empty.");
					Thread.sleep(2000);
					continue;
				}
				if (productsinCart != null) {
					System.out.println("Enter Product Name to Delete : ");
					String name = scanner.next();
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product isn't in Your Cart. Please Try Different Product.");
						Thread.sleep(2000);
						continue;
					}
					System.out.println("Enter Product Quantity to Delete : ");
					int quantity = scanner.nextInt();
					if (!checkProductPiecesinCart(name, quantity)) {
						System.out.println("You Entered Insufficient Quantity");
						continue;
					}
					deleteItem(name, quantity);
					haveMoney = haveMoney + calculateProductCost(name, quantity);
				}
			} else if (operation.equals("4")) {
				System.out.println(userName + "'s Cart : ");
				printItem();
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty.");
					Thread.sleep(2000);
					continue;
				}
				if (productsinCart != null) {
					System.out.println("Enter Product Name to Edit : ");
					String name = scanner.next();
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product isn't in Your Cart. Please try Different Product.");
						Thread.sleep(2000);
						continue;
					}
					System.out.println("1.Add Quantity\n" + "2.Delete Quantity\n");
					int addorDelete = scanner.nextInt();
					if (addorDelete == 1) {
						System.out.println("Enter Product Quantity You Want to Edit : ");
						int quantity = scanner.nextInt();
						if (!checkProductPiecesinDB(name, quantity)) {
							System.out.println("Insufficient Stock.");
							Thread.sleep(2000);
							continue;
						}
						editAddItem(name, quantity);
						System.out.println("Product Successfully Edited");
						Thread.sleep(2000);
						haveMoney -= calculateProductCost(name, quantity);
					} else if (addorDelete == 2) {
						System.out.println("Enter Product Quantity to Edit : ");
						int quantity = scanner.nextInt();
						if (!checkProductPiecesinCart(name, quantity)) {
							System.out.println("You Entered Insufficient Quantity");
							Thread.sleep(2000);
							continue;
						}
						editDeleteItem(name, quantity);
						System.out.println("Product Successfully Edited");
						Thread.sleep(2000);
						haveMoney += calculateProductCost(name, quantity);
					} else {
						System.out.println("Incorrect Operation. Please Try Again.");
						continue;
					}
				}
			} else if (operation.equals("5")) {
				System.out.println("Remaining Balance : " + haveMoney);
				Thread.sleep(2000);
				continue;
			} else if (operation.equals("6")) {
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty.");
					System.out.println("Please Add Product to Your Cart");
					continue;
				}
				System.out.println(userName + "'s Cart : ");
				printItem();
				int totalprice = totalCost();
				System.out.println("1.Checkout Now?\n" + "2.Continue Shopping?\n");
				String result = scanner.next();
				if (result.equals("1")) {
					System.out.println("Processing Your Checkout Now. Please Wait..");
					Thread.sleep(3000);
					checkout(totalprice, haveMoney);
					System.out.println("Printing Your Receipt. Please Wait..");
					printUserCheckout(userName, haveMoney);
					Thread.sleep(2000);
					System.out.println("Exiting the Application. Please wait..");
					saveDBUserCheckout(userName, haveMoney);
					Thread.sleep(2000);
					System.exit(1);
					;
				} else if (result.equals("2")) {
					continue;
				} else {
					System.out.println("Incorrect Operation");
					continue;
				}
			} else if (operation.equals("q")) {
				System.out.println("Exiting User Operations..");
				Thread.sleep(2000);
				break;
			} else {
				System.out.println("You Entered Incorrect Key. Please Try Again..");
				Thread.sleep(2000);
				continue;
			}
		}
	}

	public void addItem(CartItem cartItem) {
		productsinCart.add(cartItem);
	}

	public void deleteItem(String productName, int quantity) throws Exception {
		if (productsinCart != null) {
			for (CartItem cartItem : productsinCart) {
				if (cartItem.getProduct().getName().equals(productName)) {
					if (cartItem.getQuantity() == quantity) {
						productsinCart.remove(cartItem);
						System.out.println(cartItem.getProduct().getName() + " Product Successfully Deleted");
						Products productCart = cartItem.getProduct();
						productCart.setQuantity(productCart.getQuantity() + quantity);
						productService.save(productCart);
						Thread.sleep(2000);
						break;
					}
					if (cartItem.getQuantity() > quantity) {
						cartItem.setQuantity((cartItem.getQuantity()) - quantity);
						System.out.println(
								quantity + " Quantity (" + cartItem.getProduct().getName() + ") Successfully Deleted ");
						Products productDB = productService.findByName(productName);
						productDB.setQuantity(productDB.getQuantity() + quantity);
						productService.save(productDB);
						Thread.sleep(2000);
						break;
					}
				}
			}
		}
	}

	public void editAddItem(String productName, int quantity) throws Exception {
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equals(productName)) {
				int productkQuantity = cartItem.getQuantity();
				int newProductkQuantity = productkQuantity + quantity;
				cartItem.setQuantity(newProductkQuantity);
				if (cartItem.getQuantity() == 0) {
					productsinCart.remove(cartItem);
				}
				Products productDB = productService.findByName(productName);
				productDB.setQuantity(productDB.getQuantity() - quantity);
				productService.save(productDB);
				if (productDB.getQuantity() == 0) {
					productService.delete(productDB);
				}
			}
		}
	}

	public void editDeleteItem(String productName, int quantity) throws Exception {
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equals(productName)) {
				if (quantity > calculateProductQuantityinCart(productName)) {
					System.out.println("You Entered Insufficient Quantity");
					Thread.sleep(2000);
					return;
				} else if (quantity == calculateProductQuantityinCart(productName)) {
					productsinCart.remove(cartItem);
					Products productCart = cartItem.getProduct();
					productCart.setQuantity(productCart.getQuantity() + quantity);
					productService.save(productCart);
					Thread.sleep(2000);
					return;
				} else {
					cartItem.setQuantity(cartItem.getQuantity() - quantity);
					Products productDB = productService.findByName(productName);
					productDB.setQuantity(productDB.getQuantity() + quantity);
					productService.save(productDB);
					System.out.println("Product Successfully Edited");
					Thread.sleep(2000);
					return;
				}
			}
		}
	}

	public int calculateProductQuantityinCart(String productName) {
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equals(productName)) {
				return cartItem.getQuantity();
			}
		}
		return 0;
	}

	public int calculateProductCost(String productName, int quantity) {
		Products product = productService.findByName(productName);
		int cost = product.getPrice() * quantity;
		return cost;
	}

	public boolean checkProductNameinCart(String productName) {
		int result = 0;
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equals(productName)) {
				result++;
			}
		}
		if (result < 1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkProductNameinDB(String productName) {
		Products products = productService.findByName(productName);
		if (products == null) {
			return false;
		}
		return true;
	}

	public boolean checkProductPiecesinDB(String productName, int quantity) {
		Products products = productService.findByName(productName);
		if (products != null) {
			if (products.getQuantity() >= quantity) {
				return true;
			}
		}
		return false;
	}

	public boolean checkProductPiecesinCart(String productName, int quantity) {
		boolean result = false;
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equals(productName)) {
				if (quantity <= cartItem.getQuantity()) {
					result = true;
				}
			}
		}
		return result;
	}

	public int totalCost() {
		int totalPrice = 0;
		for (CartItem cartItem : productsinCart) {
			totalPrice += (cartItem.getProduct().getPrice()) * (cartItem.getQuantity());
		}
		return totalPrice;
	}

	public void printItem() throws Exception {
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct() == null) {
				System.out.println("Your Cart is Empty.");
				return;
			}
			System.out.println(cartItem.getProduct().getName() + " x " + cartItem.getQuantity() + " Qty" + " = "
					+ ((cartItem.getProduct().getPrice()) * cartItem.getQuantity()) + " " +Currency.EURO);
		}
		System.out.println("Total Price = " + totalCost() + " " +Currency.EURO);
		Thread.sleep(2000);
	}

	public List<Products> getProducts() {
		return productService.getAll();
	}

	public void printProduct() {
		for (Products products : getProducts()) {
			System.out.println("\t" + products.getName() + "\t" + "\t" + products.getPrice() + "\t" + Currency.EURO
					+ "\t" + "\t" + "x" + products.getQuantity() + " Qty");
		}
	}

	public void checkout(int totalprice, int haveMoney) {
		System.out.println("Payment Received " + totalprice + Currency.EURO + " Successfully From Your Balance");
		System.out.println("Remaining Balance : " + haveMoney);
		System.out.println("Thank You For Choosing Us!");
		return;
	}

	public void saveDBUserCheckout(String userName, int haveMoney) {
		// TODO
	}

	public void printUserCheckout(String userName, int haveMoney) {

		String username = userName;
		int havemoney = haveMoney;
		int totalPrice = totalCost();
		List<CartItem> checkoutProduct = productsinCart;
		ArrayList<ProductDTO> productDTOs = CheckoutController.getProductDTOs(checkoutProduct);
		CheckoutController.createExcelFile(productDTOs, totalPrice, username, havemoney);
	}
}
