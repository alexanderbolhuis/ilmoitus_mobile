package com.ilmoitus.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;



public class DatePickerFragment extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {
	
	OnDateSelectedListener mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
    	Bundle args = getArguments();
    	
    	int year, month, day;
    	
    	year = args.getInt("year", 0);
    	month = args.getInt("month", 0);
    	day = args.getInt("day", 0);
    	
    	if(year == 0 || month == 0 || day == 0)
    	{
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
    	}
    	
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnDateSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnDateSelectedListener");
		}
	}

    
	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnDateSelectedListener {
		/** Called by HeadlinesFragment when a list item is selected */
		public void onDateSelected(int year, int month, int day);
	}

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
    	// Notify the parent activity of selected item
 		mCallback.onDateSelected(year, month, day);
    }
}