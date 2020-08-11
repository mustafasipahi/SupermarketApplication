package com.application.services.core;

import java.util.List;

public interface IService<E> {

	public int save(E entity);
	public void delete(E entity);
	public E findById(int id);
	public E findByName(String name);
	public List<E> getAll();
}
