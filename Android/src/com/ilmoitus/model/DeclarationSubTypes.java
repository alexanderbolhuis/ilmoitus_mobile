package com.ilmoitus.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeclarationSubTypes implements Parcelable {
	private String Name;
	private Long id;
	private double maxCost;
	
	public DeclarationSubTypes(String Name, Long id) {
		this.Name = Name;
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setMaxCost(double maxCost){
		this.maxCost = maxCost;
	}
	
	public double getMaxCost(){
		return maxCost;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
