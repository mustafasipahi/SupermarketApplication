package com.application.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.controller.core.UserController;
import com.application.model.CartItem;
import com.application.model.Products;
import com.application.model.money.Currency;
import com.application.services.core.ProductService;

@Component
public class UserControllerImpl implements UserController{

	private Scanner scanner = new Scanner(System.in);

	private List<CartItem> productsinCart;

	@Autowired
	private ProductService productService;

	public void runUser() throws Exception{

		System.out.println("Please Enter Your Name : ");
		String userName = scanner.next();
		if (userName.isEmpty()) {
			System.out.println("Your Name Can't Blank");
			return;
		}
		System.out.println("Please Enter Your Budget : ");
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

			System.out.println(
					"User Operation : \n" + "1.View Cart\n" + "2.Add Product to Cart\n" + "3.Delete Product From Cart\n"
							+ "4.Edit Cart\n" + "5.Remaining Budget\n" + "6.Proceed to Checkout\n" + "q Exit\n");
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
				System.out.println("Select the Product Name You Want to Add to Cart : ");
				String item = scanner.next();
				if (!checkProductNameinDB(item)) {
					System.out.println("You Have Entered an Incorrect Product. Please Try Again..");
					Thread.sleep(2000);
					continue;
				}
				if (checkProductNameinCart(item)) {
					System.out.println("You Have This Product In Your Cart.");
					System.out.println("You Can Edit Your Cart.");
					Thread.sleep(2000);
					continue;
				}
				System.out.println("How Many Quantity Do You Want to Add to Cart : ");
				int quantity = scanner.nextInt();

				if (quantity == 0) {
					System.out.println("Please Enter a Valid Quantity..");
					Thread.sleep(2000);
					continue;
				}
				if (!checkProductPiecesinDB(item, quantity)) {
					System.out.println("Insufficient Stock. You Can Reduce the Quantity or Buy a Different Product");
					Thread.sleep(2000);
					continue;
				}
				if (calculateProductCost(item, quantity) > haveMoney) {
					System.out.println("Your Budget is Insufficient.");
					Thread.sleep(2000);
					continue;
				}
				Products selectedProducts = productService.findByName(item);
				CartItem cartItem = new CartItem(selectedProducts, quantity);
				addItem(cartItem);
				System.out.println("Adding Product to Cart Please Wait..");
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
					Thread.sleep(1000);
					continue;
				}
				if (productsinCart != null) {
					System.out.println("Enter the Name of the Product You Want to Delete : ");
					String name = scanner.next();
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product is Not in Your Cart.");
						continue;
					}
					System.out.println("Enter the Quantity of the Product You Want to Delete : ");
					int quantity = scanner.nextInt();
					if (!checkProductPiecesinCart(name, quantity)) {
						System.out.println("There is Not This Much Product in Your Cart");
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
					System.out.println("Enter the Name of the Product You Want to Edit : ");
					String name = scanner.next();
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product is Not in Your Cart.");
						Thread.sleep(2000);
						continue;
					}
					System.out.println("1.Add Quantity\n" + "2.Delete Quantity\n");
					int addorDelete = scanner.nextInt();
					if (addorDelete == 1) {
						System.out.println("Enter the Quantity of the Product You Want to Edit : ");
						int quantity = scanner.nextInt();
						if (!checkProductPiecesinDB(name, quantity)) {
							System.out.println("Insufficient Stock.");
							Thread.sleep(2000);
							continue;
						}
						editAddItem(name, quantity);
						haveMoney -= calculateProductCost(name, quantity);
					} else if (addorDelete == 2) {
						System.out.println("Enter the Quantity of the Product You Want to Edit : ");
						int quantity = scanner.nextInt();
						if (!checkProductPiecesinCart(name, quantity)) {
							System.out.println("There is Not This Much Product in Your Cart");
							Thread.sleep(2000);
							continue;
						}
						editDeleteItem(name, quantity);
						haveMoney += calculateProductCost(name, quantity);
					} else {
						System.out.println("Incorrect Operation. Please Try Again.");
						continue;
					}
				}
			} else if (operation.equals("5")) {
				System.out.println("Your Budget : " + haveMoney);
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
				System.out.println("1.Do You Confirm the Checkout?\n" + "2.Back\n");
				String result = scanner.next();
				if (result.equals("1")) {
					System.out.println("Please Wait Processing Checkout Process..");
					Thread.sleep(3000);
					checkout(totalprice, haveMoney);
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
				System.out.println("You Pressed an Incorrect Key.Please Try Again..");
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
						System.out.println(quantity + " Pieces Successfully Deleted From "
								+ cartItem.getProduct().getName() + " Product");
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
					System.out.println("There Are No Such Products in Your Cart");
					Thread.sleep(2000);
					return;
				} else if (quantity == calculateProductQuantityinCart(productName)) {
					productsinCart.remove(cartItem);
					System.out.println("Product Successfully Deleted");
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
					+ ((cartItem.getProduct().getPrice()) * cartItem.getQuantity()) + Currency.TL);
		}
		System.out.println("Total Price = " + totalCost() + Currency.TL);
		Thread.sleep(2000);
	}

	public List<Products> getProducts() {
		return productService.getAll();
	}

	public void printProduct() {
		for (Products products : getProducts()) {
			System.out.println("\t" + products.getName() + "\t" + products.getPrice() + Currency.TL + "\t"
					+ products.getQuantity() + " Qty");
		}
	}

	public void checkout(int totalprice, int haveMoney) {
		System.out.println("Received " + totalprice + Currency.TL + " Successfully From Your Budget");
		System.out.println("Thank You For Using Our Super Market Application");
		System.out.println("Remaining Budget : " + haveMoney);
		return;
	}
}
