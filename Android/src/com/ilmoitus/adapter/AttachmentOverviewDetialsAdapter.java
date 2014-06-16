package com.ilmoitus.adapter;

import java.util.ArrayList;

import com.example.ilmoitus.R;
import com.ilmoitus.model.Attachment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttachmentOverviewDetialsAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<Attachment> attachments;
	private String attachmentName;

	public AttachmentOverviewDetialsAdapter(Activity activity,
			ArrayList<Attachment> attachments) {
		this.activity = activity;
		this.attachments = attachments;
		this.inflator = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return attachments.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Attachment attachment = null;

		if (attachments.get(position).getClass() == Attachment.class) {
			attachment = (Attachment) attachments.get(position);
		}

		View rowView = inflator.inflate(R.layout.attachment_details_view_list, null);
		ImageView image = (ImageView) rowView
				.findViewById(R.id.attachment_detail_image);
		TextView name = (TextView) rowView.findViewById(R.id.attachment_detail_name);

		if (attachment != null) {
			image.setImageBitmap(attachment.getAttachment());
			for (int i = 0; i < attachments.size(); i++) {
				if (attachments.size() - 1 == 0) {
					attachmentName = attachment.getAttachmentName();
				} else {
					attachmentName = attachment.getAttachmentName() + i;
				}
			}
			name.setText(attachmentName);
		}
		return rowView;
	}
}
