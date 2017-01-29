package com.example;

import java.io.Serializable;

public class Customer implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String firstName;
	public String lastName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
