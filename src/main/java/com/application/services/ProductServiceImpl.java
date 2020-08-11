package com.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.dao.ProductDAO;
import com.application.model.Products;
import com.application.services.core.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductDAO productDAO;

	@Override
	public int save(Products entity) {		
		productDAO.save(entity);
		return entity.getId();
	}

	@Override
	public void delete(Products entity) {
		productDAO.delete(entity);
	}

	@Override
	public Products findById(int id) {
		return productDAO.findByProductId(id);
	}
	
	public Products findByName(String name) {
		if (productDAO.findByBookName(name) != null) {
			return productDAO.findByBookName(name);
		}		
		return null;
	}

	@Override
	public List<Products> getAll() {
		List<Products> products = new ArrayList<Products>();
		products= (List<Products>) productDAO.findAll();		
		return products;
	}
}
