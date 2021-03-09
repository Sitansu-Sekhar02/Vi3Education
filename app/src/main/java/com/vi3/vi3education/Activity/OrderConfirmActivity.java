package com.vi3.vi3education.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vi3.vi3education.Fragments.YourCourseFragment;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

public class OrderConfirmActivity extends AppCompatActivity {
    //Textview
    TextView tvViewOrder;
    TextView orderNumber,purchaseDate,billingMail,tvOrderTotal;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_confirmation);

        tvViewOrder=findViewById(R.id.tvViewOrder);
        orderNumber=findViewById(R.id.tvOrderNumber);
        purchaseDate=findViewById(R.id.tvDate);
        billingMail=findViewById(R.id.tvEmail);
        tvOrderTotal=findViewById(R.id.tvOrderTotal);

        preferences=new Preferences(this);
        orderNumber.setText(preferences.get("order_id"));
        purchaseDate.setText(preferences.get("order_date"));
        billingMail.setText(preferences.get("email"));
        tvOrderTotal.setText(preferences.get("order_total"));

        MainActivity.tvHeaderText.setText("Order Confirmation");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);

        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderConfirmActivity.this, MainActivity.class);
                startActivity(i);
                OrderConfirmActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        //Setlistener
        tvViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // replaceFragmentWithAnimation( new YourCourseFragment());
                Intent i = new Intent(OrderConfirmActivity.this, MainActivity.class);
                startActivity(i);
                OrderConfirmActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

            }
        });

        //Activity
        MainActivity.ivCart.setVisibility(View.GONE);
        MainActivity.tvCount.setVisibility(View.GONE);
        MainActivity.bottom_nav.setVisibility(View.GONE);
    }

}
