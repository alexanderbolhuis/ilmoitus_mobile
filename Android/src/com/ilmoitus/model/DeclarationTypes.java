package com.ilmoitus.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeclarationTypes implements Parcelable {
	private String Name;
	private Long id;
	
	public DeclarationTypes(String Name, Long id) {
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

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {

	}
}