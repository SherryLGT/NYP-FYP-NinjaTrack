package fragment;

import nyp.fypj.ninjatrack.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	
	private TextView tv_name, tv_age, tv_address;
	
	public ProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_age = (TextView) rootView.findViewById(R.id.tv_age);
        tv_address = (TextView) rootView.findViewById(R.id.tv_address);
        
        tv_name.setText("Lee Eng Kiat");
        tv_age.setText("56");
        tv_address.setText("150 Ang Mo Kio Avenue 3");
         
        return rootView;
    }
}
