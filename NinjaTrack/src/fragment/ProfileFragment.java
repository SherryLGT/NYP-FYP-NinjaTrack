package fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import model.Profile;
import nyp.fypj.ninjatrack.R;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import controller.SQLiteController;
import controller.Utility;

public class ProfileFragment extends Fragment {
	
	private TextView tv_name, tv_age, tv_contact_no, tv_email, tv_start_date;
	private EditText et_name, et_age, et_contact_no, et_email;
	private ImageButton btn_edit, btn_done;
	private SQLiteController controller;
	private Profile profile;
	private int year, month, day;
	private DatePickerDialog datepicker;
	
	public ProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_age = (TextView) rootView.findViewById(R.id.tv_age);
        tv_contact_no = (TextView) rootView.findViewById(R.id.tv_contact_no);
        tv_email = (TextView) rootView.findViewById(R.id.tv_email);
        tv_start_date = (TextView) rootView.findViewById(R.id.tv_start_date);
        et_name = (EditText) rootView.findViewById(R.id.et_name);
        et_age = (EditText) rootView.findViewById(R.id.et_age);
        et_contact_no = (EditText) rootView.findViewById(R.id.et_contact_no);
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        btn_edit = (ImageButton) rootView.findViewById(R.id.btn_edit);
        btn_done = (ImageButton) rootView.findViewById(R.id.btn_done);
        
        controller = new SQLiteController(getActivity());
        controller.open();
        profile = controller.retrieveProfile();
        controller.close();
        
        removeEdit(profile);
        
        et_age.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				GregorianCalendar date = profile.getAge();
				year = date.get(Calendar.YEAR);
				month = date.get(Calendar.MONTH);
				day = date.get(Calendar.DAY_OF_MONTH);
				
				datepicker = new DatePickerDialog(getActivity(), null, year, month, day);
				datepicker.show();
				datepicker.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						year = datepicker.getDatePicker().getYear();
						month = datepicker.getDatePicker().getMonth();
						day = datepicker.getDatePicker().getDayOfMonth();
						
						profile.setAge(Utility.parseDateFromIntegers(day, month, year));
						et_age.setText(Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
					}
				});
			}
        });
        
        btn_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setEdit();
			}
        });
        
        btn_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				profile.setName(et_name.getText().toString());
				profile.setAge(Utility.parseDateFromString(et_age.getText().toString(), Utility.FORMAT_DD_MMM_YYYY));
				profile.setContactNo(Integer.parseInt(et_contact_no.getText().toString()));
				profile.setEmail(et_email.getText().toString());
				
				profile.setStartDate(Utility.parseDateFromString(tv_start_date.getText().toString(), Utility.FORMAT_DD_MMM_YYYY));
				
		        controller.open();
		        controller.updateProfile(profile);
		        controller.close();
		        
				removeEdit(profile);
			}
        });
        
        return rootView;
    }
	
	private void setEdit() {
        et_name.setText(profile.getName());
        et_age.setText(Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
        et_contact_no.setText(Integer.toString(profile.getContactNo()));
        et_email.setText(profile.getEmail());
        
		tv_name.setVisibility(View.GONE);
		tv_age.setVisibility(View.GONE);
		tv_contact_no.setVisibility(View.GONE);
		tv_email.setVisibility(View.GONE);
		btn_edit.setVisibility(View.GONE);
		
		et_name.setVisibility(View.VISIBLE);
		et_age.setVisibility(View.VISIBLE);
		et_contact_no.setVisibility(View.VISIBLE);
		et_email.setVisibility(View.VISIBLE);
		btn_done.setVisibility(View.VISIBLE);
	}
	
	private void removeEdit(Profile profile) {		
        tv_name.setText(profile.getName());
        tv_age.setText(Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
        tv_contact_no.setText(Integer.toString(profile.getContactNo()));
        tv_email.setText(profile.getEmail());
        tv_start_date.setText(Utility.parseDateToString(profile.getStartDate(), Utility.FORMAT_DD_MMM_YYYY));
        
		tv_name.setVisibility(View.VISIBLE);
		tv_age.setVisibility(View.VISIBLE);
		tv_contact_no.setVisibility(View.VISIBLE);
		tv_email.setVisibility(View.VISIBLE);
		btn_edit.setVisibility(View.VISIBLE);
		
		et_name.setVisibility(View.GONE);
		et_age.setVisibility(View.GONE);
		et_contact_no.setVisibility(View.GONE);
		et_email.setVisibility(View.GONE);
		btn_done.setVisibility(View.GONE);
	}
}
