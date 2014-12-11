package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nyp.fypj.ninjatrack.R;

public class SettingFragment extends Fragment {
	
	private TextView tv_bluetooth, tv_ninjatrack;
	
	public SettingFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        
        tv_bluetooth = (TextView) rootView.findViewById(R.id.tv_bluetooth);
        tv_ninjatrack = (TextView) rootView.findViewById(R.id.tv_ninjatrack);
        
        tv_bluetooth.setText("56 : AB : C6 : EU");
        tv_ninjatrack.setText("56 : AB : C6 : EU");
        
        return rootView;
    }
}
