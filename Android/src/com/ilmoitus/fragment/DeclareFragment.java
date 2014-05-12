package com.ilmoitus.fragment;

import com.example.ilmoitus.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DeclareFragment extends Fragment implements View.OnClickListener{

	private OnClickListener listener;
	private Button declareButton;
	private Button mainButton;
	private Button addLineButton;
	
	public interface OnClickListener
	{
		public void onOverviewButtonClick();
		public void onButtonClick();
		public void onAddLineButtonClick();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		View view = inflater.inflate(R.layout.fragment_declare, container, false);
		
		declareButton = (Button) view.findViewById(R.id.buttonDeclare);
		declareButton.setEnabled(false);
		declareButton.setOnClickListener(this);
		
		mainButton = (Button) view.findViewById(R.id.buttonOvervieuw);
		mainButton.setOnClickListener(this);
		
		addLineButton = (Button) view.findViewById(R.id.onAddLineButton);
		addLineButton.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnClickListener) {
			listener = (OnClickListener) activity;
		} 
		else {
			throw new ClassCastException(activity.toString()  + " must implement DeclareFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		listener.onOverviewButtonClick();
		listener.onAddLineButtonClick();
	}
	
}
