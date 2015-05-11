package cz.uhk.fim.mygeoalarm;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Petr on 11. 5. 2015.
 */
public class DestinationsListFragmentTab extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_destinations_list, container, false);
        return rootView;
    }
}
