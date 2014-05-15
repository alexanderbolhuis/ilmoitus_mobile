package com.ilmoitus.model;

public class DeclarationSubTypes {
	private String Name;
	private Long id;
	
	public DeclarationSubTypes(String Name, Long id) {
		this.Name = Name;
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public Long getId() {
		return id;
	}
	
	public String toString()
	{
		return Name;
	}
}
