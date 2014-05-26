package com.ilmoitus.model;

public class BaseDeclaration 
{
	private long id;
	private String createdAt;
	private String createdBy;
	private String assignedTo;
	private String comment;
	private double itemsTotalPrice;
	private int itemsCount;
	
	public BaseDeclaration(long id, String createdAt, String createdBy, String assignedTo, String comment
			,double itemsTotalPrice){
		this.id = id;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
		this.comment = comment;
		this.itemsTotalPrice = itemsTotalPrice;
	}
	
	public long getId(){
		return id;
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
