package com.ilmoitus.model;

import android.graphics.Bitmap;

public class Attachment {
	
	private Bitmap 	attachment;
	private String 	attachmentName;
	
	public Attachment(Bitmap newAttachment, String newAttachmentName)
	{
		setAttachment(newAttachment);
		setAttachmentName(newAttachmentName);
	}
	
	public void setAttachment(Bitmap newAttachment)
	{
		this.attachment = newAttachment;
	}
	
	public void setAttachmentName(String newAttachmentName)
	{
		this.attachmentName = newAttachmentName;
	}
	
	public Bitmap getAttachment()
	{
		return this.attachment;
	}
	
	public String getAttachmentName()
	{
		return this.attachmentName;
	}
}
