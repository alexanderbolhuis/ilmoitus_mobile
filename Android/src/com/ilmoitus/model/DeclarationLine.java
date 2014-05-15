package com.ilmoitus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;

public class DeclarationLine implements Serializable
{
	private String datum;
	private String declaratieSoort;
	private Long declaratieSubSoort;
	private int bedrag;
	
	public DeclarationLine(String datum, String declaratieSoort, Long declaratieSubSoort, int bedrag){
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

	public int getBedrag() {
		return bedrag;
	}
}
