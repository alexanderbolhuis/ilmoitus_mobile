package com.ilmoitus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;

public class DeclarationLine implements Serializable
{
	private String datum;
	private String declaratieSoort;
	private String declaratieSubSoort;
	private String bedrag;
	private ArrayList<Bitmap> attachments;
	
	public DeclarationLine(String datum, String declaratieSoort, String declaratieSubSoort, String bedrag, ArrayList<Bitmap> attachments){
		this.datum = datum;
		this.declaratieSoort = declaratieSoort;
		this.declaratieSubSoort = declaratieSubSoort;
		this.bedrag = bedrag;
		this.attachments = attachments;
	}
	
	public String getDatum() {
		return datum;
	}

	public String getDeclaratieSoort() {
		return declaratieSoort;
	}

	public String getDeclaratieSubSoort() {
		return declaratieSubSoort;
	}

	public String getBedrag() {
		return bedrag;
	}

	public ArrayList<Bitmap> getAttachments() {
		return attachments;
	}

}
