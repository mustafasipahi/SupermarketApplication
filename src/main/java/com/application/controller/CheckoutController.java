package com.application.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.application.dto.ProductDTO;
import com.application.model.CartItem;
import com.application.model.money.Currency;

public class CheckoutController {
	
	public static void createExcelFile(ArrayList<ProductDTO> list,int totalPrice,String userName,int haveMoney) {
		
		//Create Excell Work Book
		XSSFWorkbook workbook = new XSSFWorkbook();

		//Create an Excell Sheet 
		XSSFSheet sheet = workbook.createSheet("Kişi Listesi");

		//Set Font of Titles
		XSSFFont headerFont = workbook.createFont();

		//Set Font Color
		headerFont.setColor(IndexedColors.BLUE.getIndex());

		//CellStyle of Titles
		XSSFCellStyle headerStyle = workbook.createCellStyle();

		headerStyle.setFont(headerFont);

		//Prepare Header
		Row headerRow = sheet.createRow(0);
		Cell hname = headerRow.createCell(0);
		hname.setCellValue("Product Name");

		Cell hlastName = headerRow.createCell(1);
		hlastName.setCellValue("Quantity");

		Cell hage = headerRow.createCell(2);
		hage.setCellValue("Price");

		//Add Header Style to Cells
		hname.setCellStyle(headerStyle);
		hlastName.setCellStyle(headerStyle);
		hage.setCellStyle(headerStyle);
		
		for (int i = 0; i < list.size(); i++) {

			//Create a New Line  on Excel Sheet
			Row row = sheet.createRow(i + 1);

			//Create a New Cell for the Releated Line
			Cell name = row.createCell(0);
			name.setCellValue((list.get(i)).getProductName());

			//Create a New Cell for the Releated Line
			Cell lastName = row.createCell(1);
			lastName.setCellValue(list.get(i).getProductQuantity());

			//Create a New Cell for the Releated Line
			Cell age = row.createCell(2);
			age.setCellValue(list.get(i).getProductPrice());

		}
		
		//Create a New Line on Excel Sheet For Total Price
		Row row1 = sheet.createRow(list.size() + 2);
		Cell name1 = row1.createCell(0);
		name1.setCellValue("Total Price : \t" + totalPrice + " " + Currency.EURO);
		
		//Create a New Line on Excel Sheet For Remaining Balance
		Row row = sheet.createRow(list.size() + 4);
		Cell name = row.createCell(0);
		name.setCellValue("Remaining Balance : \t" + haveMoney + " " + Currency.EURO);
		
		Calendar calendar = Calendar.getInstance();
		String date = String.format("%s/%s/%s", calendar.get(Calendar.DATE), 
												calendar.get(Calendar.MONTH)+1, 
												calendar.get(Calendar.YEAR));
		String time = String.format("%s:%s:%s", calendar.get(Calendar.HOUR), 
												calendar.get(Calendar.MINUTE), 
												calendar.get(Calendar.SECOND));
		
		//Create a New Line on Excel Sheet For Date
		Row row2 = sheet.createRow(list.size() + 6);
		Cell name2 = row2.createCell(0);
		name2.setCellValue("Date of Purchase : \t" + date + "    " + time);
		
		//Create a New Line on Excel Sheet For Note
		Row row3 = sheet.createRow(list.size() + 8);
		Cell name3 = row3.createCell(0);
		name3.setCellValue("******Thank You For Choosing Us " + userName + "******");
		
		//Print List on Excel Sheet
		write(workbook);
	}

	public static void write(XSSFWorkbook workbook) {
		try {
			//Save Excell File Location
			FileOutputStream out = new FileOutputStream(new File("YourProduct.xlsx"));
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int totalprice(ArrayList<ProductDTO> products) {
		int totalPrice = 0;
		for (ProductDTO productDTO : products) {
			totalPrice += productDTO.getProductPrice();
		}
		return totalPrice;
	}

	public static ArrayList<ProductDTO> getProductDTOs(List<CartItem> items) {
		
		//Convert Cart items to ProdoctDTO
		ArrayList<ProductDTO> products = new ArrayList<ProductDTO>();
		for (CartItem cartItem : items) {
			ProductDTO productDTO = new ProductDTO();
			productDTO.setProductName(cartItem.getProduct().getName());
			productDTO.setProductQuantity(cartItem.getQuantity());
			productDTO.setProductPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
			products.add(productDTO);
		}
		return products;
	}	
}
