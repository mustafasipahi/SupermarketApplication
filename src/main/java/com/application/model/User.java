package com.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int id;
		
	@Column(name = "user_name")
	private String name;
	
	@Column(name = "user_balance")
	private double startingBudget;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getStartingBalance() {
		return startingBudget;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStartingBalance(double startingBudget) {
		this.startingBudget = startingBudget;
	}
}
