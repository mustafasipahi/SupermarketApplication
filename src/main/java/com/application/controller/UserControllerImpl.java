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

	@Autowired
	private ProductService productService;
	
	private Scanner scanner = new Scanner(System.in);
	private List<CartItem> productsinCart;	

	public void runUser() throws Exception {

		System.out.println("Please Enter Your Name : ");
		String userName = scanner.next();
		
		//Check Username is Blank 
		if (userName.isEmpty()) {
			System.out.println("Your Name Can't be Blank");
			return;
		}
		System.out.println("Please Enter Your Budget (EURO) : ");
		String oldhaveMoney = scanner.next();
		
		//Integer Check for Input Value
		if (!isInteger(oldhaveMoney)) {
			System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
			Thread.sleep(2000);
			return;
		}
		
		//Integer Parse the Input Value
		int haveMoney = Integer.parseInt(oldhaveMoney);
		
		//Budget Cannot be Less than 0 and Cannot be Less than 10
		if (haveMoney <= 0 || haveMoney < 10) {
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

			//If Input 1, Actions to be Taken
			if (operation.equals("1")) {
				System.out.println(userName + "'s Cart : ");
				
				//Show All Products in Cart
				printItem();
				
				//Check Cart Empty
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty");
					Thread.sleep(2000);
					continue;
				}
			} 
			
			//If Input 2, Actions to be Taken
			else if (operation.equals("2")) {
				System.out.println("Enter Product Name to Add to Cart : ");
				String item = scanner.next();
				
				//Check Product Exists in Database
				if (!checkProductNameinDB(item)) {
					System.out.println("This Product isn't in Stock. Please Try Different Product..");
					Thread.sleep(2000);
					continue;
				}
				
				//Check Product Name Exists in Cart
				if (checkProductNameinCart(item)) {
					System.out.println("You Already Have This Product In Your Cart");
					System.out.println("You Can Edit Your Cart");
					Thread.sleep(2000);
					continue;
				}
				System.out.println("Enter Quantity You Want to Add to Cart : ");
				String oldquantity = scanner.next();
				
				//Integer Check for Input Value
				if (!isInteger(oldquantity)) {
					System.out.println("You Entered Incorrect Key. Please Enter Valid Number");
					Thread.sleep(2000);
					continue;
				}
				
				//Integer Parse the Entered Value
				int quantity = Integer.parseInt(oldquantity);
				if (quantity == 0) {
					System.out.println("Please Enter a Valid Quantity..");
					Thread.sleep(2000);
					continue;
				}
				
				//Check Product Quantity Exists in Database
				if (!checkProductPiecesinDB(item, quantity)) {
					System.out.println("Insufficient Stock. You Can Reduce the Quantity or Enter Different Product");
					Thread.sleep(2000);
					continue;
				}
				
				//Calculate Product Cost
				//Check Cost is More than Budget
				if (calculateProductCost(item, quantity) > haveMoney) {
					System.out.println("Your Balance is Insufficient");
					Thread.sleep(2000);
					continue;
				}				
				
				Products selectedProducts = productService.findByName(item);
				CartItem cartItem = new CartItem(selectedProducts, quantity);
				
				//Add Products to Cart
				addItem(cartItem);
				System.out.println("Adding Product to Cart. Please Wait..");
				Thread.sleep(2000);
				System.out.println("Product Successfully Added to Your Cart");
				
				//Update Budget
				haveMoney = haveMoney - calculateProductCost(item, quantity);
				
				//Delete the Qty of Product from Database when it is Added to Cart
				selectedProducts.setQuantity(selectedProducts.getQuantity() - quantity);
				productService.save(selectedProducts);
				
				//If Selected Product Quantity less than 1 in The Database, Delete Selected Product From Database
				if (selectedProducts.getQuantity() == 0) {
					productService.delete(selectedProducts);
				}
				Thread.sleep(2000);
			} 
			
			//If Input 3, Actions to be Taken
			else if (operation.equals("3")) {
				
				//Show All Products in Cart
				printItem();
				
				//Check Cart Empty
				if (productsinCart.isEmpty()) {
					System.out.println("Your Cart is Empty");
					Thread.sleep(2000);
					continue;
				}
				if (productsinCart != null) {
					System.out.println("Enter Product Name to Delete : ");
					String name = scanner.next();
					
					//Check Product Name Exists in Cart
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product isn't in Your Cart. Please Try Different Product..");
						Thread.sleep(2000);
						continue;
					}
					System.out.println("Enter Product Quantity to Delete : ");
					String oldquantity = scanner.next();
					
					//Integer Check for Input Value
					if (!isInteger(oldquantity)) {
						System.out.println("You Entered Incorrect Key. Please Enter Valid Number..");
						Thread.sleep(2000);
						continue;
					}
					
					//Integer Parse the Input Value
					int quantity = Integer.parseInt(oldquantity);
					
					//Check Product Quantity in Cart
					if (!checkProductPiecesinCart(name, quantity)) {
						System.out.println("You Entered Insufficient Quantity");
						Thread.sleep(2000);
						continue;
					}
					
					//Delete Product From Cart
					deleteItem(name, quantity);
					
					//Update Budget
					haveMoney = haveMoney + calculateProductCost(name, quantity);
				}
			} 
			
			//If Input 4, Actions to be Taken
			else if (operation.equals("4")) {
				System.out.println(userName + "'s Cart : ");
				
				//Show All Products in Cart
				printItem();
				
				//Check Cart Empty
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty");
					Thread.sleep(2000);
					continue;
				}
				if (productsinCart != null) {
					System.out.println("Enter Product Name to Edit : ");
					String name = scanner.next();
					
					//Check Product Name Exists in Cart
					if (!checkProductNameinCart(name)) {
						System.out.println("This Product isn't in Your Cart. Please Try Different Product..");
						Thread.sleep(2000);
						continue;
					}
					System.out.println("1.Add Quantity\n" + "2.Delete Quantity\n");
					String oldaddorDelete = scanner.next();
					
					//Integer Check for Input Value
					if (!isInteger(oldaddorDelete)) {
						System.out.println("You Entered Incorrect Key. Please Enter Valid Number..");
						Thread.sleep(2000);
						continue;
					}
					
					//Integer Parse the Input Value
					int addorDelete = Integer.parseInt(oldaddorDelete);
					if (addorDelete == 1) {
						System.out.println("Enter Product Quantity You Want to Edit : ");
						String oldquantity = scanner.next();
						
						//Integer Check for Input Value
						if (!isInteger(oldquantity)) {
							System.out.println("You Entered Incorrect Key. Please Enter Valid Number..");
							Thread.sleep(2000);
							continue;
						}
						
						//Integer Parse the Input Value
						int quantity = Integer.parseInt(oldquantity);
						
						//Check Product Quantity Exists in Database
						if (!checkProductPiecesinDB(name, quantity)) {
							System.out.println("Insufficient Stock");
							Thread.sleep(2000);
							continue;
						}
						
						//Update Product in Cart
						editAddItem(name, quantity);
						System.out.println("Product Successfully Edited");
						Thread.sleep(2000);
						
						//Update Budget
						haveMoney -= calculateProductCost(name, quantity);
					} else if (addorDelete == 2) {
						System.out.println("Enter Product Quantity to Edit : ");
						String oldquantity = scanner.next();
						
						//Integer Check for Input Value
						if (!isInteger(oldquantity)) {
							System.out.println("You Entered Incorrect Key. Please Enter Valid Number..");
							Thread.sleep(2000);
							continue;
						}
						
						//Integer Parse the Input Value
						int quantity = Integer.parseInt(oldquantity);
						
						//Check Product Quantity in Cart
						if (!checkProductPiecesinCart(name, quantity)) {
							System.out.println("You Entered Insufficient Quantity");
							Thread.sleep(2000);
							continue;
						}
						
						//Update Product in Cart
						editDeleteItem(name, quantity);
						System.out.println("Product Successfully Edited");
						Thread.sleep(2000);
						
						//Update Budget
						haveMoney += calculateProductCost(name, quantity);
					} else {
						System.out.println("Incorrect Operation. Please Try Again..");
						Thread.sleep(2000);
						continue;
					}
				}
			} 
			
			//If Input 5, Actions to be Taken
			else if (operation.equals("5")) {
				
				//Show Remaining Budget
				System.out.println("Remaining Balance : " + haveMoney);
				Thread.sleep(2000);
				continue;
			} 
			
			//If Input 6, Actions to be Taken
			else if (operation.equals("6")) {
				
				//Check Cart Empty
				if (productsinCart.isEmpty()) {
					System.out.println(userName + "'s Cart is Empty");
					System.out.println("Please Add Product to Your Cart");
					Thread.sleep(2000);
					continue;
				}
				System.out.println(userName + "'s Cart : ");
				
				//Show All Products in Cart
				printItem();
				
				//Calculate Total Cost in Cart
				int totalprice = totalCost();
				System.out.println("1.Checkout Now?\n" + "2.Continue Shopping?\n");
				String result = scanner.next();
				
				//Complete Your Shopping
				if (result.equals("1")) {
					System.out.println("Processing Your Checkout Now. Please Wait..");
					Thread.sleep(3000);
					checkout(totalprice, haveMoney);
					System.out.println("Printing Your Receipt. Please Wait..");
					
					//Print Receipt
					printUserCheckout(userName, haveMoney);
					Thread.sleep(2000);
					System.out.println("Exiting the Application. Please Wait..");
					
					//Save User to Database
					saveUserCheckoutDB(userName, haveMoney);
					Thread.sleep(2000);
					System.exit(1);
				} else if (result.equals("2")) {
					continue;
				} else {
					System.out.println("Incorrect Operation");
					continue;
				}
			} 
			
			//If Input q, Actions to be Taken
			else if (operation.equals("q")) {
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
				if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
					if (cartItem.getQuantity() == quantity) {
						
						//If Product Qty is is Less than 1 in Cart, Delete Product from Cart
						productsinCart.remove(cartItem);
						System.out.println(cartItem.getProduct().getName() + " Product Successfully Deleted");
						Products productCart = cartItem.getProduct();
						
						//Add the Qty of Product to Database when it is Deleted From Cart
						productCart.setQuantity(productCart.getQuantity() + quantity);
						productService.save(productCart);
						Thread.sleep(2000);
						break;
					}
					if (cartItem.getQuantity() > quantity) {
						
						//Update Cart 
						cartItem.setQuantity((cartItem.getQuantity()) - quantity);
						System.out.println(
								quantity + " Quantity (" + cartItem.getProduct().getName() + ") Successfully Deleted");
						
						//Add the Qty of Product to Database when it is Deleted From Cart
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
			if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
				int productkQuantity = cartItem.getQuantity();
				int newProductkQuantity = productkQuantity + quantity;
				
				//Update Cart 
				cartItem.setQuantity(newProductkQuantity);
				if (cartItem.getQuantity() == 0) {
					productsinCart.remove(cartItem);
				}
				
				//Delete the Qty of Product from Database when it is Added to Cart
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
			if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
				if (quantity > calculateProductQuantityinCart(productName)) {
					System.out.println("You Entered Insufficient Quantity");
					Thread.sleep(2000);
					return;
				} else if (quantity == calculateProductQuantityinCart(productName)) {
					
					//Update Cart
					productsinCart.remove(cartItem);
					
					//Add the Qty of Product to Database when it is Deleted From Cart
					Products productCart = cartItem.getProduct();
					productCart.setQuantity(productCart.getQuantity() + quantity);
					productService.save(productCart);
					Thread.sleep(2000);
					return;
				} else {
					
					//Add the Qty of Product to Database when it is Deleted From Cart
					cartItem.setQuantity(cartItem.getQuantity() - quantity);
					Products productDB = productService.findByName(productName);
					productDB.setQuantity(productDB.getQuantity() + quantity);
					productService.save(productDB);
					Thread.sleep(2000);
					return;
				}
			}
		}
	}

	public int calculateProductQuantityinCart(String productName) {
		for (CartItem cartItem : productsinCart) {
			if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
				
				//Calculate Quantity of Product in Cart
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
			if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
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
			if (cartItem.getProduct().getName().equalsIgnoreCase(productName)) {
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
			
			//Show Product in Cart
			System.out.println(cartItem.getProduct().getName() + " x " + cartItem.getQuantity() + " Qty" + " = "
					+ ((cartItem.getProduct().getPrice()) * cartItem.getQuantity()) + " " + Currency.EURO);
		}
		
		//Show Product Cost in Cart
		System.out.println("Total Price = " + totalCost() + " " + Currency.EURO);
		Thread.sleep(2000);
	}

	public List<Products> getProducts() {
		return productService.getAll();
	}

	public void printProduct() {
		
		//Show Products in Database
		for (Products products : getProducts()) {
			System.out.println("\t" + products.getName() + "\t" + "\t" + products.getPrice() + "\t" + Currency.EURO
					+ "\t" + "\t" + "x" + products.getQuantity() + " Qty");
		}
	}

	public void checkout(int totalprice, int haveMoney) {
		System.out.println("Payment Received " + totalprice + " " +Currency.EURO + " Successfully From Your Balance");
		System.out.println("Remaining Balance : " + haveMoney);
		System.out.println("Thank You For Choosing Us!");
	}

	public void saveUserCheckoutDB(String userName, int haveMoney) {
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

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
