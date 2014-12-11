/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fragment;

import nyp.fypj.ninjatrack.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;

public class MusicFragment extends SherlockFragment{

	private static final String ARG_POSITION = "position";

	private int position;

	public static MusicFragment newInstance(int position) {
		MusicFragment f = new MusicFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		FrameLayout fl = new FrameLayout(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.setLayoutParams(params);
		
		switch(position) { 
			case 0:
				View recorderView = inflater.inflate(R.layout.tab_recorder, null);
				fl.addView(recorderView);
				Fragment recorderFragment = new RecorderFragment();
				getFragmentManager().beginTransaction().replace(R.id.tab_recorder, recorderFragment).commit();
				break;
			case 1:
				View drumView = inflater.inflate(R.layout.tab_drum, null);
				fl.addView(drumView);
				Fragment drumFragment = new DrumFragment();
				getFragmentManager().beginTransaction().replace(R.id.tab_drum, drumFragment).commit();
				break;
			case 2:
				View saxophoneView = inflater.inflate(R.layout.tab_saxophone, null);
				fl.addView(saxophoneView);
				Fragment saxophoneFragment = new SaxophoneFragment();
				getFragmentManager().beginTransaction().replace(R.id.tab_saxophone, saxophoneFragment).commit();
				break;
			case 3:
				View handBellView = inflater.inflate(R.layout.tab_handbell, null);
				fl.addView(handBellView);
				Fragment handBellFragment = new HandBellFragment();
				getFragmentManager().beginTransaction().replace(R.id.tab_handbell, handBellFragment).commit();
				break;
		}
		return fl;
	}
}