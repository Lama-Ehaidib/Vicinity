package vicinity.vicinity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

/**
 * Created by macproretina on 2/13/15.
 */

public class TimelineSectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getActivity().startService(new Intent(getActivity(), WiFiService.class));
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        return rootView;
    }
}
