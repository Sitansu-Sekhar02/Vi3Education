package com.vi3.vi3education.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;


public class OrderConfirmationFragment extends Fragment {

    //view
    View v;

    //Textview
    TextView tvViewOrder;
    TextView orderNumber,purchaseDate,billingMail,tvOrderTotal;
    Preferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        tvViewOrder=v.findViewById(R.id.tvViewOrder);
        orderNumber=v.findViewById(R.id.tvOrderNumber);
        purchaseDate=v.findViewById(R.id.tvDate);
        billingMail=v.findViewById(R.id.tvEmail);
        tvOrderTotal=v.findViewById(R.id.tvOrderTotal);

        preferences=new Preferences(getActivity());
        orderNumber.setText(preferences.get("order_id"));
        purchaseDate.setText(preferences.get("order_date"));
        billingMail.setText(preferences.get("email"));
        tvOrderTotal.setText(preferences.get("order_total"));

        MainActivity.tvHeaderText.setText("Order Confirmation");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);

        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        //Setlistener
        tvViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentWithAnimation( new YourCourseFragment());

            }
        });

        //Activity
        MainActivity.ivCart.setVisibility(View.GONE);
        MainActivity.tvCount.setVisibility(View.GONE);
        MainActivity.bottom_nav.setVisibility(View.GONE);
        return v;
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}