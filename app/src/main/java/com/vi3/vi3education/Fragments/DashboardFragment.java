package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.vi3.vi3education.Model.DashboardModel;
import com.vi3.vi3education.R;
import com.vi3.vi3education.adapter.CardPagerAdapter;
import com.vi3.vi3education.extra.AppSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment  extends Fragment {
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        DashboardAdapter customAdapter = new DashboardAdapter(getActivity(), personNames);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
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

        return view;
    }

    private void setAdapter() {
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DashboardAdapter(categoryList,getActivity());
        recyclerView.setAdapter(adapter);*/
    }


    //*Recyclerview Adapter*//
    private class DashboardAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList personNames;
        Context context;
        public DashboardAdapter(Context context, ArrayList personNames) {
            this.context = context;
            this.personNames = personNames;
        }
      /*  //private List<DashboardModel> category;
        private List<DashboardModel> mModel;
        private Context mContext;

        public DashboardAdapter(List<DashboardModel>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;
           // category=new ArrayList<>(mModel);

        }*/

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view_fragment, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {
           // holder.subject.setText(personNames.get(position));
           // holder.video_price.setText(mModel.get(position).getVideo_price());


        }
        public int getItemCount() {
            return personNames.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }
    private class Holder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView subject;
        TextView textView;
        TextView video_price;
        RatingBar ratingBar;

        public Holder(View itemView) {
            super(itemView);

            videoView=itemView.findViewById(R.id.videoView);
            subject=itemView.findViewById(R.id.tv_SubjectName);
            video_price=itemView.findViewById(R.id.price);

        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment,String id) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
