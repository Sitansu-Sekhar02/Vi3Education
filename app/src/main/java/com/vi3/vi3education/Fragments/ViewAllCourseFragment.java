package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.DashboardModel;
import com.vi3.vi3education.Model.FindByTutor;
import com.vi3.vi3education.Model.GetAllCourseModel;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ViewAllCourseFragment extends Fragment {

    public static final String cart = "https://vi3edutech.com/vi3webservices/add_to_cartp.php";
    public static final String get_allCourse = "https://vi3edutech.com/vi3webservices/get_all_video.php";

    Dialog dialog;
    RecyclerView recyclerView;
    Preferences preferences;
    private List<GetAllCourseModel> allCourse;
    private GetAllCourseAdapter adapter;
    EditText search_course;

    String video_name;
    String video_price;
    String video_id;


    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_all_category_fragment, container, false);

        recyclerView = view.findViewById(R.id.show_allCategory);
        search_course=view.findViewById(R.id.et_search);
        allCourse=new ArrayList<>();
        preferences = new Preferences(getActivity());


        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            ProgressDialog();
            dialog.show();
            GetAllCourserdApi();
            // ProgressForgotPassword();

        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        search_course.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return  view;
    }

    private void filter(String toString) {
        List<GetAllCourseModel> temp = new ArrayList();
        for(GetAllCourseModel d: allCourse){
            if(d.getVideo_name().toLowerCase().contains(toString.toLowerCase())){
                temp.add(d);
            }else{
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void GetAllCourserdApi() {
        StringRequest request = new StringRequest(Request.Method.POST,get_allCourse, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("get_allcourse",response);
                // refreshLayout.setRefreshing(false);
                dialog.cancel();

                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("success").equalsIgnoreCase("true")) {
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            GetAllCourseModel list = new GetAllCourseModel();
                            String video_id = Object.getString("course_id");
                            String video_name = Object.getString("course_name");
                            String video_image="https://vi3edutech.com/images/course_images/"+ Object.getString("image");
                            String video_price = Object.getString("price");
                            //String video_rating = Object.getString("vehicle_compony");

                            list.setVideo_id(video_id);
                            list.setVideo_name(video_name);
                            list.setVideo_image(video_image);
                            list.setVideo_price(video_price);


                            allCourse.add(list);
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
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new GetAllCourseAdapter(allCourse,getActivity());
        recyclerView.setAdapter(adapter);
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

    //*Recyclerview Adapter*//
    private class GetAllCourseAdapter extends RecyclerView.Adapter<Holder> {

        private List<GetAllCourseModel> category;

        private List<GetAllCourseModel> mModel;
        private Context mContext;

        public GetAllCourseAdapter(List<GetAllCourseModel>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;
            category=new ArrayList<>(mModel);
        }
        public void updateList(List<GetAllCourseModel> list){
            mModel = list;
            notifyDataSetChanged();
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_course, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {
            holder.subject.setText(mModel.get(position).getVideo_name());
            holder.video_price.setText(mModel.get(position).getVideo_price());
            Glide.with(mContext)
                    .load(mModel.get(position).getVideo_image())
                    .into(holder.course_image);
            video_id=allCourse.get(position).getVideo_id();
            video_name=allCourse.get(position).getVideo_name();
            video_price=allCourse.get(position).getVideo_price();


            holder.buy_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                    holder.buy_now.setVisibility(View.GONE);
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

        public Filter getFilter() {
            return filter;
        }
            Filter filter=new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    ArrayList<GetAllCourseModel> filteredList=new ArrayList<>();
                    if (charSequence.toString().isEmpty()){
                        filteredList.addAll(allCourse);
                    }else {
                        for (GetAllCourseModel product:allCourse){
                            if (product.getVideo_name().toLowerCase().contains(charSequence.toString().toLowerCase())){
                                filteredList.add(product);
                            }
                        }
                    }
                    FilterResults filterResults=new FilterResults();
                    filterResults.values=filteredList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mModel.clear();
                    mModel.addAll((Collection<? extends GetAllCourseModel>) filterResults.values);
                    notifyDataSetChanged();
                }
            };

    }

    private void AddtoCart(final String pid, final String video_name, final String video_price ) {
        StringRequest rqst = new StringRequest(Request.Method.POST, cart, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //dialog.cancel();
                Log.e("response11111", response);
                Toasty.success(getActivity(), "Item Added", Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.cancel();
                Log.e("error_response", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("pid", pid);
                param.put("user_id", preferences.get("user_id"));
                param.put("product_name", video_name);
                param.put("price", video_price);

                Log.e("msg", "parameters" + param);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(rqst);
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView subject;
        ImageView course_image;
        TextView video_price;
        RatingBar ratingBar;
        Button buy_now;
        TextView rlOutofstock;

        public Holder(View itemView) {
            super(itemView);

            subject=itemView.findViewById(R.id.tv_subName);
            course_image=itemView.findViewById(R.id.course_image);
            video_price=itemView.findViewById(R.id.tv_Price);
            buy_now=itemView.findViewById(R.id.btn_Buy);
            rlOutofstock=itemView.findViewById(R.id.rlOutofstock);

        }
    }
    public void ShakeAnimation(View view) {
        final Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        view.startAnimation(animShake);
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
