package com.application.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.dao.AdminDAO;
import com.application.model.Admin;
import com.application.services.core.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminDAO adminDAO;

	@Override
	@Transactional
	public int save(Admin entity) {
		return 0;
	}

	@Override
	@Transactional
	public void delete(Admin entity) {

	}

	@Override
	public Admin findById(int id) {
		return null;
	}

	@Override
	public Admin findByName(String name) {
		return null;
	}

	@Override
	public List<Admin> getAll() {
		List<Admin> admins = (List<Admin>) adminDAO.findAll();
		return admins;
	}
}
