package fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import model.Profile;
import nyp.fypj.ninjatrack.R;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import controller.SQLiteController;
import controller.Utility;

@SuppressLint("InflateParams")
public class ProfileFragment extends Fragment {
	
	private TextView tv_name, tv_age, tv_contact_no, tv_email, tv_start_date;
	private EditText et_name, et_age, et_contact_no, et_email;
	private ImageView iv_image, iv_image_edit, iv_dialog;
	private ImageButton btn_edit, btn_done;
	
	private SQLiteController controller;
	private Profile profile;
	private int year, month, day;
	private DatePickerDialog datepicker;
	
	private static int RESULT_LOAD_IMAGE = 1;
	private String imagePath;
    BitmapFactory.Options bitmapOptions;
	
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
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);
        iv_image_edit = (ImageView) rootView.findViewById(R.id.iv_image_edit);
        btn_edit = (ImageButton) rootView.findViewById(R.id.btn_edit);
        btn_done = (ImageButton) rootView.findViewById(R.id.btn_done);
        
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 3;
        
        controller = new SQLiteController(getActivity());
        controller.open();
        profile = controller.retrieveProfile();
        controller.close();
        
        defaultView(profile);
        
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
				editingView();
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
				profile.setImage(imagePath);
				
		        controller.open();
		        controller.updateProfile(profile);
		        controller.close();
		        
				defaultView(profile);
			}
        });
        
        return rootView;
    }
	
	private void editingView() {
        et_name.setText(profile.getName());
        et_age.setText(Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
        et_contact_no.setText(Integer.toString(profile.getContactNo()));
        et_email.setText(profile.getEmail());
        
        try {
            if(profile.getImage() != null && !profile.getImage().equals("")) {
                iv_image.setImageBitmap(BitmapFactory.decodeFile(profile.getImage(), bitmapOptions));
            }
            else {
            	iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile));
            }
        }
        catch(Exception e) {
        	iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile));
        }
        
		tv_name.setVisibility(View.GONE);
		tv_age.setVisibility(View.GONE);
		tv_contact_no.setVisibility(View.GONE);
		tv_email.setVisibility(View.GONE);
		iv_image_edit.setVisibility(View.GONE);
		btn_edit.setVisibility(View.GONE);
		
		et_name.setVisibility(View.VISIBLE);
		et_age.setVisibility(View.VISIBLE);
		et_contact_no.setVisibility(View.VISIBLE);
		et_email.setVisibility(View.VISIBLE);
		iv_image_edit.setVisibility(View.VISIBLE);
		btn_done.setVisibility(View.VISIBLE);
		
		iv_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();
            
            iv_image.setImageBitmap(BitmapFactory.decodeFile(imagePath, bitmapOptions));
        }
	}

	private void defaultView(Profile profile) {
        tv_name.setText(profile.getName());
        tv_age.setText(Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
        tv_contact_no.setText(Integer.toString(profile.getContactNo()));
        tv_email.setText(profile.getEmail());
        tv_start_date.setText(Utility.parseDateToString(profile.getStartDate(), Utility.FORMAT_DD_MMM_YYYY));
        
        try {
            if(profile.getImage() != null && !profile.getImage().equals("")) {
            	imagePath = profile.getImage();
                iv_image.setImageBitmap(BitmapFactory.decodeFile(profile.getImage(), bitmapOptions));
            }
            else {
            	iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile));
            }
        }
        catch(Exception e) {
        	iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile));
        }
        
		tv_name.setVisibility(View.VISIBLE);
		tv_age.setVisibility(View.VISIBLE);
		tv_contact_no.setVisibility(View.VISIBLE);
		tv_email.setVisibility(View.VISIBLE);
		iv_image_edit.setVisibility(View.VISIBLE);
		btn_edit.setVisibility(View.VISIBLE);
		
		et_name.setVisibility(View.GONE);
		et_age.setVisibility(View.GONE);
		et_contact_no.setVisibility(View.GONE);
		et_email.setVisibility(View.GONE);
		iv_image_edit.setVisibility(View.GONE);
		btn_done.setVisibility(View.GONE);
		
		iv_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog dialog = new Dialog(getActivity());
				dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				View contentView = getActivity().getLayoutInflater().inflate(R.layout.image_dialog, null);				
				iv_dialog = (ImageView) contentView.findViewById(R.id.iv_dialog);
				Bitmap bitmap = (BitmapFactory.decodeFile(imagePath, bitmapOptions));
				iv_dialog.setImageBitmap(bitmap);
				dialog.setContentView(contentView);
				dialog.show();
				
				iv_dialog.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
			} 
		});
	}
}
