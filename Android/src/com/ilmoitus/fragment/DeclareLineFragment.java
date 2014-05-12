package com.ilmoitus.fragment;

import com.example.ilmoitus.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DeclareLineFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link DeclareLineFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class DeclareLineFragment extends Fragment {

	DeclareLineListener mCallback;
	Spinner spinnerDeclarationTypes, spinnerDeclarationSubTypes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_declare_line, container,
				false);

		spinnerDeclarationTypes = (Spinner) v
				.findViewById(R.id.spinnerDeclarationType);
		spinnerDeclarationSubTypes = (Spinner) v
				.findViewById(R.id.spinnerDeclarationSubType);

		spinnerDeclarationTypes
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {
						mCallback
								.onDeclareLineChanged((String) spinnerDeclarationTypes
										.getAdapter().getItem(position));
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
						// your code here
					}

				});

		return v;
	}

	public interface DeclareLineListener {
		public void onDeclareLineChanged(String Value);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (DeclareLineListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnPersonSelectedListener");
		}
	}

	public void setDate(String date) {
		EditText EditDate = (EditText) getActivity().findViewById(
				R.id.editTextDate);
		EditDate.setText(date);

	}

	public String getCurrency() {
		EditText EditCurrency = (EditText) getActivity().findViewById(
				R.id.editCurrency);
		return EditCurrency.getText().toString();
	}

	public void setDeclarationTypes(String[] values) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, values);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationTypes.setAdapter(adapter);
	}

	public void setDeclarationSubTypes(String[] values) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, values);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDeclarationSubTypes.setAdapter(adapter);
	}

}
