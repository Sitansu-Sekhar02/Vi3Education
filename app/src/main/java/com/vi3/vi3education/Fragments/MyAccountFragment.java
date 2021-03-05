package com.vi3.vi3education.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

public class MyAccountFragment extends Fragment {
    View view;
    Preferences preferences;
    TextView email,Phone,Name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        preferences = new Preferences(getActivity());
        email=view.findViewById(R.id.email);
        Phone=view.findViewById(R.id.tvphone);
        Name=view.findViewById(R.id.etname);

        email.setText(preferences.get("email"));
        Phone.setText(preferences.get("contact"));
        Name.setText(preferences.get("name"));

        MainActivity.tvHeaderText.setText("Account");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);
        MainActivity.ivCart.setVisibility(View.GONE);
        MainActivity.bottom_nav.setVisibility(View.GONE);
        MainActivity.tvCount.setVisibility(View.GONE);

        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

            }
        });

        return  view;
    }
}
