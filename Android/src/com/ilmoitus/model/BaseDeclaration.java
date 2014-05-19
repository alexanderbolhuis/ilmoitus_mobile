package com.ilmoitus.model;

public class BaseDeclaration 
{
	private String createdAt;
	private String createdBy;
	private String assignedTo;
	private String comment;
	private String itemsTotalPrice;
	private String itemsCount;
	
	public BaseDeclaration(String createdAt, String createdBy, String assignedTo, String comment
			,String itemsTotalPrice){
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

	public String getItemsTotalPrice() {
		return itemsTotalPrice;
	}

	public String getItemsCount() {
		return itemsCount;
	}
}
