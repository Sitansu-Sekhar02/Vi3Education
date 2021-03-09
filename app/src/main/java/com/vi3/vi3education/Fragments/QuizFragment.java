package com.vi3.vi3education.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.R;

public class QuizFragment extends Fragment {
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.quiz_fragment, container, false);

        MainActivity.bottom_nav.setVisibility(View.GONE);

        return v;
    }
}
