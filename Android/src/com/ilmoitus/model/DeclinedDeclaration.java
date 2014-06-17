package com.ilmoitus.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DeclinedDeclaration extends BaseDeclaration {

	public DeclinedDeclaration(JSONObject declaration) throws JSONException {
		super(declaration.getLong("id"), declaration.getString("created_at"), declaration.getString("created_by"), declaration.getString("assigned_to"), 
				declaration.getString("comment"), declaration.getDouble("items_total_price"));
	}

}
