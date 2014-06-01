package com.ilmoitus.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ClosedDeclaration extends BaseDeclaration
{
	private String lockedAt;
	private String supervisorComment;
	
	public ClosedDeclaration(JSONObject declaration) throws JSONException{
		super(declaration.getLong("id"), declaration.getString("created_at"), declaration.getString("created_by"), declaration.getString("assigned_to"), 
				declaration.getString("comment"), declaration.getDouble("items_total_price"));
		this.lockedAt = declaration.getString("locked_at");
		this.supervisorComment = declaration.getString("supervisor_comment");
	}
	
	public String getLockedAt() {
		return lockedAt;
	}

	public String getSupervisorComment() {
		return supervisorComment;
	}
}
