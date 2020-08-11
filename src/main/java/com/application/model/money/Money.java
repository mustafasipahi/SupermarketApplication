package com.application.model.money;

public class Money {

	private double value;
	private Currency currency;
	
	public Money(double value,Currency currency) {
		this.value = value;
		this.currency = currency;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	public double getValue() {
		return value;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Money [value=" + value + ", currency=" + currency + "]";
	}	
}
