package com.ilmoitus.model;

import java.io.Serializable;

public class DeclarationLine implements Serializable
{
	private long id;
	private String datum;
	private String declaratieSoort;
	private Long declaratieSubSoort;
	private double bedrag;
	
	public DeclarationLine(Long id, String datum, String declaratieSoort, Long declaratieSubSoort, double bedrag){
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

	public String getDeclaratieSoort() {
		return declaratieSoort;
	}

	public Long getDeclaratieSubSoort() {
		return declaratieSubSoort;
	}

	public double getBedrag() {
		return bedrag;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public void setDeclaratieSoort(String declaratieSoort) {
		this.declaratieSoort = declaratieSoort;
	}

	public void setDeclaratieSubSoort(Long declaratieSubSoort) {
		this.declaratieSubSoort = declaratieSubSoort;
	}

	public void setBedrag(double bedrag) {
		this.bedrag = bedrag;
	}
	
	
}
