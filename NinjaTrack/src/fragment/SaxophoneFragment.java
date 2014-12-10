package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nyp.fypj.ninjatrack.R;

public class SaxophoneFragment extends Fragment {
	
	public SaxophoneFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_saxophone, container, false);
         
        return rootView;
    }
}
