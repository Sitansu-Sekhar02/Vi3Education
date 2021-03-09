package com.vi3.vi3education.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.tabs.TabLayout;
import com.vi3.vi3education.Activity.LoginActivity;
import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.DashboardModel;
import com.vi3.vi3education.R;
import com.vi3.vi3education.adapter.CardPagerAdapter;
import com.vi3.vi3education.extra.AppSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class DashboardFragment  extends Fragment implements ExoPlayer.EventListener {

    public static final String dashboard_url = "https://vi3edutech.com/vi3webservices/show_video.php";

    SimpleExoPlayer player;
    String path;
    ViewPager mViewPager;
    //view
    View view;
    ArrayList personNames = new ArrayList<>(Arrays.asList("Person 1"));

    //Tablayout
    //timer
    Timer timer;
    TabLayout tabLayout;
    int currentPage = 0;


    //SearchView searchView;
    Dialog dialog;
    Button viewAllCourse;

    //long
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 2000;

    //Images
    private static final int[] mResources = {
            R.drawable.slid4,
            R.drawable.slid3,
            R.drawable.slid1,
            R.drawable.slid2,
            R.drawable.slid5,
    };
    RecyclerView recyclerView;
    private List<DashboardModel> categoryList;
    private DashboardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homefragment, container, false);

        mViewPager = view.findViewById(R.id.myviewpager);
        tabLayout = view.findViewById(R.id.tabDots);
        recyclerView = view.findViewById(R.id.recyclerView);
        viewAllCourse = view.findViewById(R.id.view_allCourse);


        //Page
        AppSettings.fromPage="1";
        //setTablayout
        tabLayout.setupWithViewPager(mViewPager, true);
        final int NUM_PAGES = 6;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES - 1) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        CardPagerAdapter mCardPagerAdapter =new CardPagerAdapter(getActivity(),mResources) {
            @Override
            protected void onCategoryClick(View view, String str) {

            }
        };
        categoryList = new ArrayList<>();
        //setAdapter();

        mViewPager.setAdapter(mCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);
        mViewPager.setCurrentItem(1, true);
        mViewPager.setPageMargin(10);

        viewAllCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    replaceFragmentWithAnimation(new ViewAllCourseFragment());
                    // ProgressForgotPassword();
                } else {
                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            ProgressDialog();
            dialog.show();
            DashboardApi();
            // ProgressForgotPassword();

        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }



    private void ProgressDialog() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_for_load);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void DashboardApi() {
        StringRequest request = new StringRequest(Request.Method.POST,dashboard_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response1",response);
                // refreshLayout.setRefreshing(false);
                dialog.cancel();

                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("success").equalsIgnoreCase("1")) {
                        Log.e("responsess", "" + JSNobject);
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            DashboardModel list = new DashboardModel();
                            String video_id = Object.getString("id");
                            String video_name = Object.getString("video_name");
                            String video_url= "https://vi3edutech.com/uploadvideo/"+Object.getString("vname");
                            Log.e("video_url",""+video_url);

                            String video_price = Object.getString("price");
                            //String video_rating = Object.getString("vehicle_compony");

                            list.setVideo_id(video_id);
                            list.setSubject_name(video_name);
                            list.setVideo_url(video_url);
                            list.setVideo_price(video_price);

                            categoryList.add(list);
                        }
                    }
                    setAdapter();
                }
                catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }
                //refreshLayout.setRefreshing(false);

            }

        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Log.e("error_response", "" + error);
                // refreshLayout.setRefreshing(false);

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DashboardAdapter(categoryList,getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); //to pause a video because now our video player is not in focus
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                break;
            case ExoPlayer.STATE_IDLE:
                //idle state
                break;
            case ExoPlayer.STATE_READY:
                // dismiss your dialog here because our video is ready to play now
                break;
            case ExoPlayer.STATE_ENDED:
                // do your processing after ending of video
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // show user that something went wrong. I am showing dialog but you can use your way
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Could not able to stream video");
        adb.setMessage("It seems that something is going wrong.\nPlease try again.");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish(); // take out user from this activity. you can skip this
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //player.release();   //it is important to release a player

    }

    //*Recyclerview Adapter*//
    private class DashboardAdapter extends RecyclerView.Adapter<Holder> {


      private List<DashboardModel> mModel;
        private Context mContext;

        public DashboardAdapter(List<DashboardModel>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;
           // category=new ArrayList<>(mModel);
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view_fragment, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {
            holder.subject.setText(mModel.get(position).getSubject_name());
            holder.video_price.setText(mModel.get(position).getVideo_price());

            holder.buy_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                   /* holder.buy_video.setVisibility(View.GONE);
                    holder.rlOutofstock.setVisibility(View.VISIBLE);
                    Log.e("pid_no",""+ allCourse.get(position).getVideo_id());
                    String pid=allCourse.get(position).getVideo_id();
                    String video_name=allCourse.get(position).getVideo_name();
                    Log.e("pid_no",""+ allCourse.get(position).getVideo_name());

                    String video_price=allCourse.get(position).getVideo_price();
                    Log.e("pid_no",""+ allCourse.get(position).getVideo_price());

                    AddtoCart(pid,video_name,video_price);

                    int counter = preferences.getInt("count");
                    int totalcount = counter + 1;
                    preferences.set("count", totalcount);
                    preferences.commit();

                    ShakeAnimation(MainActivity.tvCount);
                    MainActivity.tvCount.setText("" + totalcount);
*/

                }
            });


            String video_path="https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";
            Uri uri=Uri.parse(video_path);
            holder.videoView.setVideoURI(uri);
            //holder.videoView.requestFocus();
           // holder.videoView.start();



            MediaController mediaController=new MediaController(getActivity());
            holder.videoView.setMediaController(mediaController);
            mediaController.setAnchorView(holder.videoView);

           /* path="https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";


            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create a default LoadControl
            LoadControl loadControl = new DefaultLoadControl();
            // 3. Create the player
            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

            SimpleExoPlayerView playerView = (SimpleExoPlayerView)view. findViewById(R.id.videoView);
            playerView.setPlayer(player);
            playerView.setKeepScreenOn(true);
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "ExoPlayer"));

            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(path),
                    dataSourceFactory, extractorsFactory, null, null);
            // Prepare the player with the source.
            player.addListener((ExoPlayer.EventListener) this);
            player.prepare(videoSource);
            playerView.requestFocus();
            player.setPlayWhenReady(true);*/



        }
        public int getItemCount() {
            return mModel.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }
    private class Holder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView subject;
        TextView added;
        TextView video_price;
        RatingBar ratingBar;
        Button buy_video;

        public Holder(View itemView) {
            super(itemView);

            videoView=itemView.findViewById(R.id.videoView);
            subject=itemView.findViewById(R.id.tv_SubjectName);
            video_price=itemView.findViewById(R.id.price);
            buy_video=itemView.findViewById(R.id.buy_now);
            added=itemView.findViewById(R.id.rlOutofstock);

        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
