package com.ilmoitus.model;

public class BaseDeclaration 
{
	private String createdAt;
	private String createdBy;
	private String assignedTo;
	private String comment;
	private double itemsTotalPrice;
	private int itemsCount;
	
	public BaseDeclaration(String createdAt, String createdBy, String assignedTo, String comment
			,double itemsTotalPrice){
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
		this.comment = comment;
		this.itemsTotalPrice = itemsTotalPrice;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public String getComment() {
		return comment;
	}

	public double getItemsTotalPrice() {
		return itemsTotalPrice;
	}

	public int getItemsCount() {
		return itemsCount;
	}
}
