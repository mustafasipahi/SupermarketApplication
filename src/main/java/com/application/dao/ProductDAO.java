package com.application.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.application.model.Products;

@Repository
public interface ProductDAO extends CrudRepository<Products, Integer> {

	//Query by Product Id
	@Query("SELECT p FROM Products p WHERE p.id =:id")
	public Products findByProductId(@Param("id") Integer id);

	//Query by Product Name
	@Query("SELECT p FROM Products p WHERE p.name like %:name%")
	public Products findByBookName(@Param("name") String name);

}
