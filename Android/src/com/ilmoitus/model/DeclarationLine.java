package com.ilmoitus.model;

import java.io.Serializable;

public class DeclarationLine implements Serializable
{
	private long id;
	private String datum;
	private DeclarationTypes declaratieSoort;
	private DeclarationSubTypes declaratieSubSoort;
	private double bedrag;
	
	public DeclarationLine(Long id, String datum, DeclarationTypes declaratieSoort, 
			DeclarationSubTypes declaratieSubSoort, double bedrag){
		this.id = id;
		this.datum = datum;
		this.declaratieSoort = declaratieSoort;
		this.declaratieSubSoort = declaratieSubSoort;
		this.bedrag = bedrag;
	}
	
	public long getId(){
		return id;
	}
	
	public String getDatum() {
		return datum;
	}

	public DeclarationTypes getDeclaratieSoort() {
		return declaratieSoort;
	}

	public DeclarationSubTypes getDeclaratieSubSoort() {
		return declaratieSubSoort;
	}

	public double getBedrag() {
		return bedrag;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public void setDeclaratieSoort(DeclarationTypes declaratieSoort) {
		this.declaratieSoort = declaratieSoort;
	}

	public void setDeclaratieSubSoort(DeclarationSubTypes declaratieSubSoort) {
		this.declaratieSubSoort = declaratieSubSoort;
	}

	public void setBedrag(double bedrag) {
		this.bedrag = bedrag;
	}
	
	
}
