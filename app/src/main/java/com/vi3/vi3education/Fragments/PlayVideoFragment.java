package com.vi3.vi3education.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.R;

public class PlayVideoFragment extends Fragment {
    VideoView videoView;
    int position=-1;
    String order_id;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        view = inflater.inflate(R.layout.play_video_fragment, container, false);
       // getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       // view = inflater.inflate(R.layout.fragment_dashboard, container, false);
      //  getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MainActivity.tvHeaderText.setText("My Course");
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
               /* if(AppSettings.fromPage.equalsIgnoreCase("1"))
                {
                    Intent i = new Intent(getActivity(), DrawerActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                }
                else if(AppSettings.fromPage.equalsIgnoreCase("2"))
                {
                    replaceFragmentWithAnimation(new ProductListingFragment());
                }
                else
                {

                }*/
            }
        });


        videoView=view.findViewById(R.id.video_play);


        Bundle b = getArguments();
        order_id = b.getString("video_url");
    /*    Bundle mBundle = new Bundle();
        mBundle = getArguments();
        mBundle.getString("key", String.valueOf(-1));
        getActivity().getActionBar().hide();*/
        playerVideo();



        return view;
    }

    private void playerVideo() {
        String video_path="https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";
        Uri uri=Uri.parse(video_path);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        MediaController mediaController=new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);


    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
