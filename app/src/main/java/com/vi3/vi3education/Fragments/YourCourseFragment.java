package com.vi3.vi3education.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.vi3.vi3education.Model.CartItem;
import com.vi3.vi3education.Model.YourCourseModel;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class YourCourseFragment extends Fragment {

    public static final String your_course = "https://vi3edutech.com/vi3webservices/get_allmyvideo.php";

    RecyclerView recyclerView;
    Dialog dialog;
    View view;
    File director;
    public  static int REQUEST_PERMISSION=1;
    boolean boolean_permission;
    Preferences preferences;
    TextView getCert;

    private List<YourCourseModel> course_list;
    private YourCourseAdapter course;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.your_course_fragment, container, false);
        recyclerView = view.findViewById(R.id.your_courses);
        getCert=view.findViewById(R.id.tvGetCert);

        course_list=new ArrayList<>();

        preferences=new Preferences(getActivity());


        director=new File("/mnt/");
        permisForVideo();

        getCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragmentWithAnimation(new QuizFragment(),"");
            }
        });

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            YourCourseApi();
            ProgressForCourse();
            dialog.show();
        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        return  view;

    }

    private void permisForVideo() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE))){

            }else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        }else{
            /*boolean_permission=true;
            getFile(director);*/


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSION){
            if (grantResults.length>0 &&grantResults [0]==PackageManager.PERMISSION_GRANTED){
                boolean_permission=true;
              //  getFile(director);

            }else {
                Toast.makeText(getActivity(),"Please allow the permissions",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private ArrayList<File> getFile(File director) {
        File listFile[]=director.listFiles();
        if (listFile!=null &&listFile.length>0){
            for (int i=0;i<listFile.length;i++){
                if (listFile[i].isDirectory()){
                    getFile(listFile[i]);
                }
                else
                {
                    boolean_permission=false;
                    if (listFile[i].getName().endsWith(".mp4")){
                        for (int j=0;j<course_list.size();j++){
                            if (course_list.get(j).getCourse_name().equals(listFile[i].getName())){
                                boolean_permission=true;
                            }else{

                            }

                        }
                    }
                }
            }
        }

        return course_list;
    }*/

    private void ProgressForCourse() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_for_load);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
    }

    private void YourCourseApi() {
        StringRequest request = new StringRequest(Request.Method.POST,your_course, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response1",response);
                dialog.cancel();
                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("Success").equalsIgnoreCase("1")) {
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            YourCourseModel list = new YourCourseModel();
                            String video_name = object.getString("video_name");
                            String video_id = object.getString("product_id");
                            String video_image = "https://vi3edutech.com/uploadvideo/" + object.getString("img");
                            String video_url = "https://vi3edutech.com/uploadvideo/"+object.getString("vname");

                            list.setCourse_name(video_name);
                            list.setCourse_id(video_id);
                            list.setCourse_image(video_image);
                            list.setCourse_video_url(video_url);

                            course_list.add(list);
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
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id",preferences.get("user_id"));
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        course = new YourCourseAdapter(course_list, getActivity());
        recyclerView.setAdapter(course);
    }


    //*Recyclerview Adapter*//
    private class YourCourseAdapter extends RecyclerView.Adapter<Holder>  {

        private List<YourCourseModel> mModel;
        private Context mContext;

        public YourCourseAdapter(List<YourCourseModel>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;

        }


        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_course_details_fragment, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvCatName.setText(mModel.get(position).getCourse_name());
            Glide.with(mContext)
                    .load(mModel.get(position).getCourse_image())
                    .into(holder.image);

            Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(course_list.get(position).getCourse_video_url(), MediaStore.Images.Thumbnails.MINI_KIND);
            holder.image.setImageBitmap(bitmap);
            final String video_url=mModel.get(position).getCourse_video_url();
            Log.e("video","url"+video_url);

            holder.cardMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replaceFragmentWithAnimation(new PlayVideoFragment(),video_url);
                   /* Fragment fragment = new Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", holder.getAdapterPosition());
                    fragment.setArguments(bundle);*/
                   /* String id=mModel.get(position).getCategory_id();
                    Log.e("success" ,""+id);
                    Fragment fragment = new Fragment();*/
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
        CardView cardMain;
        ImageView image;
        TextView tvCatName;
        public Holder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.course_image);
            tvCatName = itemView.findViewById(R.id.tvCourseName);
            cardMain = itemView.findViewById(R.id.card_main);
        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment, String video_url) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("video_url",video_url);

        //bundle.putString("order_date_month",);
        fragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
