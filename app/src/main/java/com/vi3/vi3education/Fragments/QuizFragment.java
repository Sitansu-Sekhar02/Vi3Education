package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import com.vi3.vi3education.Model.QuizModel;
import com.vi3.vi3education.Model.YourCourseModel;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class QuizFragment extends Fragment {
    public static final String quiz_url= "https://vi3edutech.com/vi3webservices/get_allmcq.php";

    TextView tvUsername,Tv_questions;
    Preferences preferences;
    private List<QuizModel> quiz_list;
    String question;
    String video_id;
    String option1;
    String option2;
    String option3;
    String option4;
    String  answer;


    Dialog dialog;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.quiz_fragment, container, false);
        preferences=new Preferences(getActivity());

        quiz_list=new ArrayList<>();



        MainActivity.bottom_nav.setVisibility(View.GONE);
        tvUsername=v.findViewById(R.id.DispName);
        Tv_questions=v.findViewById(R.id.Tv_questions);


        tvUsername.setText("Hello,"+preferences.get("name"));


        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            QuizApi();
            ProgressForQuiz();
            dialog.show();
        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void ProgressForQuiz() {
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

    private void QuizApi() {
        StringRequest request = new StringRequest(Request.Method.POST,quiz_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("quiz_response",response);
                dialog.cancel();
                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("Success").equalsIgnoreCase("true")) {
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("get_allmcq");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            QuizModel list = new QuizModel();
                             question = object.getString("question");
                             video_id = object.getString("video_id");
                             option1 = object.getString("option1");
                             option2 = object.getString("option2");
                             option3 = object.getString("option3");
                             option4 = object.getString("option4");
                             answer = object.getString("correct_answer");


                            list.setQuestions(question);
                            list.setQuiz_id(video_id);
                            list.setOption_1(option1);
                            list.setOption_2(option2);
                            list.setOption_3(option3);
                            list.setOption_4(option4);
                            list.setAnswer(answer);


                            quiz_list.add(list);
                        }
                        Tv_questions.setText(question);


                    }
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
                parameters.put("vid",YourCourseFragment.video_id);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }


}
