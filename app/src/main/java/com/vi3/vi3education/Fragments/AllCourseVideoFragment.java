package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.FindByTutor;
import com.vi3.vi3education.Model.YourCourseModel;
import com.vi3.vi3education.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AllCourseVideoFragment extends Fragment {

    View view;
    Dialog dialog;
    public static final String videos_url = "https://vi3edutech.com/vi3webservices/get_allmyvideo1.php";
    private List<YourCourseModel> videos;
    private AllCourseVideo adapter;
    String course_id;
    String video_id;
    String video_image;
    String course_name;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.your_course_fragment, container, false);

        videos = new ArrayList<>();
        recyclerView=view.findViewById(R.id.your_courses);

        Bundle b = getArguments();
        video_id = b.getString("course_id");
        video_image=b.getString("video_image");
        course_name=b.getString("course_name");



        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            jsonRequest();
            ProgressForMain();
            dialog.show();
        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return  view;

    }

    private void ProgressForMain() {
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

    private void jsonRequest() {
        StringRequest request = new StringRequest(Request.Method.POST,videos_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response1",response);
                // refreshLayout.setRefreshing(false);
                dialog.cancel();
                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("Success").equalsIgnoreCase("1")) {
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            YourCourseModel user = new YourCourseModel();

                            course_id = jsonObject.getString("product_id");
                            String course_video = "https://vi3edutech.com/uploadvideo/UploadAllVideo/"+jsonObject.getString("path");
                            //String course_image = jsonObject.getString("email");


                            user.setCourse_id(course_id);
                            user.setCourse_video_url(course_video);
                            user.setCourse_image(video_image);
                            //user.setCourse_image(course_image);
                            videos.add(user);

                        }
                    }
                    setAdapter();
                }
                catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }

            }

        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Log.e("error_response", "" + error);

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("product_id",video_id);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }

    private void setAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AllCourseVideo(videos,getActivity());
        recyclerView.setAdapter(adapter);
    }

    //*Recyclerview Adapter*//
    private class AllCourseVideo extends RecyclerView.Adapter<Holder> {

        private List<YourCourseModel> mModel;
        private Context mContext;


        public AllCourseVideo(List<YourCourseModel>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;

        }


        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_allvideos, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.sub_name.setText(course_name);
            final String video_url=mModel.get(position).getCourse_video_url();
            Glide.with(mContext)
                    .load(mModel.get(position).getCourse_image())
                    .into(holder.imageThumb);

            holder.cardMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    replaceFragmentWithAnimation(new PlayVideoFragment(),video_url);

                }
            });

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
        TextView sub_name;
        CardView cardMain;
        ImageView imageThumb;
        RelativeLayout favorite;
        LinearLayout rate;



        public Holder(View itemView) {
            super(itemView);
            sub_name = itemView.findViewById(R.id.tv_SubjectName);
            cardMain = itemView.findViewById(R.id.cardVideo);
            imageThumb = itemView.findViewById(R.id.imageThumnail);
            favorite=itemView.findViewById(R.id.fav);
            rate=itemView.findViewById(R.id.rate);




        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment, String video_url) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("video_url",video_url);
        fragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
