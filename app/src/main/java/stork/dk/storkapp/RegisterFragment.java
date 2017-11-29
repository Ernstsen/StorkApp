package stork.dk.storkapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mathiasjensen on 29/11/17.
 */
public class RegisterFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static RegisterFragment register(int sectionNumber) {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        return rootView;
    }
}
