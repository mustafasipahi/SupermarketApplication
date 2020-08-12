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

public class CheckoutController {
	
	public static void createExcelFile(ArrayList<ProductDTO> list,int totalPrice,String userName,int haveMoney) {
		// Excel Calisma Kitabini Olustur
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Excel Sayfasi Olustur
		XSSFSheet sheet = workbook.createSheet("Kişi Listesi");

		// Basliklar icin olusturulacak bicim yapisi icin font nesnesi hazirla
		XSSFFont headerFont = workbook.createFont();

		// Yazi rengini belirle
		headerFont.setColor(IndexedColors.BLUE.getIndex());

		// Basliklar icin bicim nesnesini olustur
		XSSFCellStyle headerStyle = workbook.createCellStyle();

		// Hazirladigin Font nesnesini bicime ekle
		headerStyle.setFont(headerFont);

		// Basliklari Hazirla
		Row headerRow = sheet.createRow(0);
		Cell hname = headerRow.createCell(0);
		hname.setCellValue("Product Name");

		Cell hlastName = headerRow.createCell(1);
		hlastName.setCellValue("Quantity");

		Cell hage = headerRow.createCell(2);
		hage.setCellValue("Price");

		// Olusturulan baslik bicimini hucrelere ekle
		hname.setCellStyle(headerStyle);
		hlastName.setCellStyle(headerStyle);
		hage.setCellStyle(headerStyle);

		// Listeyi Yaz
		for (int i = 0; i < list.size(); i++) {

			// Olusturdugumuz sayfa icerisinde yeni bir satir olustur
			// i+1 yazmamizin nedeni 0. satir yani ilk satira basliklari yazdigimizdan 0 dan
			// baslatmadik
			Row row = sheet.createRow(i + 1);

			// Ilgili satir icin yeni bir hucre olustur
			Cell name = row.createCell(0);
			name.setCellValue((list.get(i)).getProductName());

			Cell lastName = row.createCell(1);
			lastName.setCellValue(list.get(i).getProductQuantity());

			Cell age = row.createCell(2);
			age.setCellValue(list.get(i).getProductPrice());

		}
		
		Row row1 = sheet.createRow(list.size() + 2);
		Cell name1 = row1.createCell(0);
		name1.setCellValue("Total Price : \t" + totalPrice);
		
		Row row = sheet.createRow(list.size() + 2);
		Cell name = row.createCell(0);
		name.setCellValue("Remaining Balance : \t" + haveMoney);
		
		Calendar calendar = Calendar.getInstance();
		String date = String.format("%s/%s/%s", calendar.get(Calendar.DATE), 
												calendar.get(Calendar.MONTH)+1, 
												calendar.get(Calendar.YEAR));
		String time = String.format("%s:%s:%s", calendar.get(Calendar.HOUR), 
												calendar.get(Calendar.MINUTE), 
												calendar.get(Calendar.SECOND));
		
		Row row2 = sheet.createRow(list.size() + 4);
		Cell name2 = row2.createCell(0);
		name2.setCellValue("Date of Purchase : \t" + date + "    " + time);
		
		Row row3 = sheet.createRow(list.size() + 6);
		Cell name3 = row3.createCell(0);
		name3.setCellValue("******Thank You For Choosing Us " + userName + "******");
		
		// Olusturulan Excel Nesnesini Dosyaya Yaz
		write(workbook);
	}

	public static void write(XSSFWorkbook workbook) {
		try {
			FileOutputStream out = new FileOutputStream(new File("./YourProduct.xlsx"));
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
