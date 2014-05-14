package com.ilmoitus.model;

public class DeclarationTypes {
	private String Name;
	private String id;
	
	public DeclarationTypes(String Name, String id) {
		this.Name = Name;
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public String getId() {
		return id;
	}
	
	public String toString()
	{
		return Name;
	}
}