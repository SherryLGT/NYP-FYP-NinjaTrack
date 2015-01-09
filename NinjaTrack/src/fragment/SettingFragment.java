package fragment;

import model.Device;
import nyp.fypj.ninjatrack.R;
import activity.DeviceListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	private Device device;
	public static PinFragment pinFragment;
	
	public SettingFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
		device = DeviceListActivity.device;
		
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        
        et_username = (EditText) rootView.findViewById(R.id.et_username);
        et_password = (EditText) rootView.findViewById(R.id.et_password);
        tv_ninjatrack = (TextView) rootView.findViewById(R.id.tv_ninjatrack);
        btn_login = (BootstrapButton) rootView.findViewById(R.id.btn_login);
        btn_reconnect = (BootstrapButton) rootView.findViewById(R.id.btn_reconnect);
        
        if(device != null) {
        	tv_ninjatrack.setText("Connected");
        	btn_reconnect.setVisibility(View.GONE);
        	
        	if(device.getName().equals("BlendMicro")) {
            	tv_ninjatrack.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					pinFragment = new PinFragment(device, DeviceListActivity.redBearService);
    					getActivity().getFragmentManager().beginTransaction().add(R.id.content, pinFragment).commit();
    				}
            	});
        	}
        	else {
        		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        		builder.setMessage("Wrong device connected. Please connect to correct device.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        			@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).create();
        	}
        }
        else {
        	tv_ninjatrack.setText("Not connected");
        }
        
        btn_reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DeviceListActivity.redBearService.connectDevice(device.getAddress(), false);
	        	tv_ninjatrack.setText("Connected");
	        	btn_reconnect.setVisibility(View.GONE);
			}
        });
        
        return rootView;
    }
}
