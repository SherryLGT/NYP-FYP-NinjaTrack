package fragment;

import nyp.fypj.ninjatrack.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecorderFragment extends Fragment {
	
	private ProgressBar progress_bar;
	private TextView start_time, end_time;
	
	public RecorderFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_recorder, container, false);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        start_time = (TextView) rootView.findViewById(R.id.start_time);
        end_time = (TextView) rootView.findViewById(R.id.end_time);
        
        progress_bar.setProgress(20);
        start_time.setText("0:00");
        end_time.setText("3:40");
         
        return rootView;
    }
}
