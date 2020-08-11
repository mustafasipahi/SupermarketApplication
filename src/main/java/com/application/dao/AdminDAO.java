package com.application.dao;

import org.springframework.data.repository.CrudRepository;

import com.application.model.Admin;

public interface AdminDAO extends CrudRepository<Admin, Integer>{

}
