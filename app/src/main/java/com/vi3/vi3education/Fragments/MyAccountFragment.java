package com.vi3.vi3education.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

public class MyAccountFragment extends Fragment {
    View view;
    Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.cartlistfragment, container, false);

        preferences = new Preferences(getActivity());
        return  view;
    }
}
