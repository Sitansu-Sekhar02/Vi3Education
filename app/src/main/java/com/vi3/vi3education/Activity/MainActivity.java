package com.vi3.vi3education.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.vi3.vi3education.Fragments.AboutusFragment;
import com.vi3.vi3education.Fragments.CartFragment;
import com.vi3.vi3education.Fragments.ChangePasswordFragment;
import com.vi3.vi3education.Fragments.DashboardFragment;
import com.vi3.vi3education.Fragments.FindByTutorFragment;
import com.vi3.vi3education.Fragments.UpcomingCourseFragment;
import com.vi3.vi3education.Fragments.YourCourseFragment;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    RelativeLayout rr_header;

    public static ImageView iv_menu, ivHome;

    DrawerLayout drawer;
    Dialog dialog;

    public static LinearLayout llmain;
    public static RelativeLayout rlsearchview;

    public static TextView tvCount;

    public static TextView tvHeaderText;

    MenuItem my_account;

    public static TextView marquee;

    public static NavigationView navigationView;
    public static BottomNavigationView  bottom_nav;
    TextView logout;



    String status;

    Preferences preferences;

    public static ImageView ivCart;

    //other
    public static int backPressed = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        logout=findViewById(R.id.nav_logout);


         bottom_nav = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = null;

        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting = "Good Morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting = "Good Afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting = "Good Evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting = "Good Night";

        }
        replaceFragmentWithAnimation(new DashboardFragment());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        Intialize();

        //Setvalue

        tvHeaderText.setText(greeting+"\n"+"Students");

        iv_menu.setOnClickListener(this);

        ivCart.setOnClickListener(this);

        // setvalue
        tvCount.setText(String.valueOf(preferences.getInt("count")));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (navigationView != null) {
            Menu menu = navigationView.getMenu();
            if (preferences.get("login").equalsIgnoreCase("yes")) {

                menu.findItem(R.id.nav_logout).setTitle("Login");
            } else {
                menu.findItem(R.id.nav_logout).setTitle("Logout");
            }
            navigationView.setNavigationItemSelectedListener(this);

        }

    }

    private void Intialize() {
        //Linearlayout
        llmain = findViewById(R.id.llmain);

        //textview
        tvHeaderText = findViewById(R.id.tvHeaderText);
        tvCount = findViewById(R.id.tvCount);

        //imageview
        iv_menu = findViewById(R.id.iv_menu);
        ivCart = findViewById(R.id.ivCart);
        preferences=new Preferences(this);

        //navigationview
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivCart:
                // Progress();
                 replaceFragmentWithAnimation(new CartFragment());
                break;
            case R.id.iv_menu:
                drawer.openDrawer(Gravity.LEFT);
                break;

        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_about) {
            replaceFragmentWithAnimation( new AboutusFragment());
            MainActivity.bottom_nav.setVisibility(View.GONE);
        } else if (id == R.id.privacy_policy) {
            // replaceFragmentWithAnimation(new AboutUsFragment());
            MainActivity.bottom_nav.setVisibility(View.GONE);

        } else if (id == R.id.rate_us) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vi3.vi3education"));
            startActivity(intent);

        }
        else if (id == R.id.nav_changePwrd) {
             replaceFragmentWithAnimation(new ChangePasswordFragment());
            MainActivity.bottom_nav.setVisibility(View.GONE);
        }
        else if (id == R.id.share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Your body here";
            String subject = "Your subject here";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            String app_url = "https://play.google.com/store/apps/details?id=com.vi3.vi3education";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } /*else if (id == R.id.nav_logout) {
            logout();
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertyesno);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        //findId
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvcancel);
        TextView tvReason = (TextView) dialog.findViewById(R.id.textView22);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tvAlertMsg);

        //set value
        tvAlertMsg.setText("Confirmation Alert..!!!");
        tvReason.setText("Are you sure you want to logout?");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        //set listener
        tvYes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                preferences.set("user_id","");
                preferences.commit();
                finishAffinity();
                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    replaceFragmentWithAnimation(new DashboardFragment());
                    return true;
                case R.id.nav_findtutor:
                    replaceFragmentWithAnimation(new FindByTutorFragment());
                    return true;
                case R.id.nav_course:
                    replaceFragmentWithAnimation(new YourCourseFragment());
                    return true;
                case R.id.nav_coming:
                    replaceFragmentWithAnimation(new UpcomingCourseFragment());
                    return true;
            }
            return false;
        }
    };
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() { backPressed = 0;
                }
            }.start();
        }
        if (backPressed == 2) {
            backPressed = 0;
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }



}