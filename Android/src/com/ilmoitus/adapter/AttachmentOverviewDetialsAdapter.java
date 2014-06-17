package com.ilmoitus.adapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.example.ilmoitus.R;
import com.ilmoitus.activity.ImageFullScreen;
import com.ilmoitus.model.Attachment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttachmentOverviewDetialsAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private Activity activity;
	private ArrayList<Attachment> attachments;
	private String attachmentName;
	private Attachment attachment;

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
		attachment = (Attachment) attachments.get(position);
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
		rowView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bitmap bm = attachment.getAttachment();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] b = baos.toByteArray();
				String base64 = Base64.encodeToString(b, Base64.DEFAULT);
				Intent intent = new Intent(activity, ImageFullScreen.class);
				intent.putExtra("base64", base64);
				activity.startActivity(intent);	
			}	
		});
		return rowView;
	}
}
