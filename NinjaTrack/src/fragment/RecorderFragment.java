package fragment;

import nyp.fypj.ninjatrack.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class RecorderFragment extends Fragment {
	
	private ProgressBar progressBar;
	
	public RecorderFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_recorder, container, false);
        ProgressBar bar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        bar.setProgress(50);
         
        return rootView;
    }
}
