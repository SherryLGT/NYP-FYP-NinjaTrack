package fragment;

import nyp.fypj.ninjatrack.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

public class SettingFragment extends Fragment {
	
	private EditText et_username, et_password;
	private TextView tv_ninjatrack;
	private BootstrapButton btn_login, btn_reconnect;
	
	public SettingFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        
        et_username = (EditText) rootView.findViewById(R.id.et_username);
        et_password = (EditText) rootView.findViewById(R.id.et_password);
        tv_ninjatrack = (TextView) rootView.findViewById(R.id.tv_ninjatrack);
        btn_login = (BootstrapButton) rootView.findViewById(R.id.btn_login);
        btn_reconnect = (BootstrapButton) rootView.findViewById(R.id.btn_reconnect);
        
        tv_ninjatrack.setText("Not connected");
        
        return rootView;
    }
}
