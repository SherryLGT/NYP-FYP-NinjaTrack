package fragment;

import nyp.fypj.ninjatrack.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	
	private TextView tv_name, tv_age, tv_contact_no, tv_email, tv_start_date;
	
	public ProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_age = (TextView) rootView.findViewById(R.id.tv_age);
        tv_contact_no = (TextView) rootView.findViewById(R.id.tv_contact_no);
        tv_email = (TextView) rootView.findViewById(R.id.tv_email);
        tv_start_date = (TextView) rootView.findViewById(R.id.tv_start_date);
        
        tv_name.setText("Winnie");
        tv_age.setText("6/10/1997");
        tv_contact_no.setText("65501675");
        tv_email.setText("winnie@outlook.com");
        tv_start_date.setText("12/11/2014");
         
        return rootView;
    }
}
