package com.ilmoitus.model;

import java.io.Serializable;

public class DeclarationLine implements Serializable
{
	private String datum;
	private String declaratieSoort;
	private Long declaratieSubSoort;
	private double bedrag;
	
	public DeclarationLine(String datum, String declaratieSoort, Long declaratieSubSoort, double bedrag){
		this.datum = datum;
		this.declaratieSoort = declaratieSoort;
		this.declaratieSubSoort = declaratieSubSoort;
		this.bedrag = bedrag;
	}
	
	public String getDatum() {
		return datum;
	}

	public String getDeclaratieSoort() {
		return declaratieSoort;
	}

	public Long getDeclaratieSubSoort() {
		return declaratieSubSoort;
	}

	public double getBedrag() {
		return bedrag;
	}
}
