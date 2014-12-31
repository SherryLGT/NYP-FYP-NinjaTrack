package fragment;

import nyp.fypj.ninjatrack.R;
import activity.MainActivity;
import activity.SplashActivity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

public class SettingFragment extends Fragment {
	
	private EditText et_username, et_password;
	private TextView tv_ninjatrack;
	private BootstrapButton btn_login, btn_reconnect;
	
	private BluetoothDevice device;
	
	public SettingFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
		device = MainActivity.device;
		
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        
        et_username = (EditText) rootView.findViewById(R.id.et_username);
        et_password = (EditText) rootView.findViewById(R.id.et_password);
        tv_ninjatrack = (TextView) rootView.findViewById(R.id.tv_ninjatrack);
        btn_login = (BootstrapButton) rootView.findViewById(R.id.btn_login);
        btn_reconnect = (BootstrapButton) rootView.findViewById(R.id.btn_reconnect);
        
        if(device != null) {
        	tv_ninjatrack.setText("Connected");
        	btn_reconnect.setVisibility(View.GONE);
        }
        else {
        	tv_ninjatrack.setText("Not connected");
        }
        
        btn_reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(SplashActivity.mBluetoothLeService.connect(device.getAddress())) {
		        	tv_ninjatrack.setText("Connected");
		        	btn_reconnect.setVisibility(View.GONE);
				}
			}
        });
        
        return rootView;
    }
}
