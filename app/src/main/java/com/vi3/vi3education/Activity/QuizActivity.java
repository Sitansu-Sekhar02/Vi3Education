package com.vi3.vi3education.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vi3.vi3education.Fragments.YourCourseFragment;
import com.vi3.vi3education.Model.QuizModel;
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

public class QuizActivity extends AppCompatActivity {

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
    String quest;
    TextView tv;
    Button submitbutton, quitbutton;
    RadioGroup radio_g;
    RadioButton rb1,rb2,rb3,rb4;
    int flag=0;
    public static int marks=0,correct=0,wrong=0;

    String id;
    Dialog dialog;
    //String answers[]={};
    //String answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_fragment);

        preferences=new Preferences(this);
        Intent intent = getIntent();
         id = intent.getStringExtra("video_id");
         Log.e("iddd",""+id);



        quiz_list=new ArrayList<>();

        tvUsername=findViewById(R.id.DispName);
        Tv_questions=findViewById(R.id.Tv_questions);


        submitbutton=(Button)findViewById(R.id.button3);
        quitbutton=(Button)findViewById(R.id.buttonquit);


        tvUsername.setText("Hello,"+preferences.get("name"));


        if (Utils.isNetworkConnectedMainThred(this)) {
            QuizApi();
            ProgressForQuiz();
            dialog.show();
        } else {
            Toasty.error(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        radio_g=(RadioGroup)findViewById(R.id.answersgrp);
        rb1=(RadioButton)findViewById(R.id.radioButton);
        rb2=(RadioButton)findViewById(R.id.radioButton2);
        rb3=(RadioButton)findViewById(R.id.radioButton3);
        rb4=(RadioButton)findViewById(R.id.radioButton4);
       /* tv.setText(questions[flag]);
        rb1.setText(opt[0]);
        rb2.setText(opt[1]);
        rb3.setText(opt[2]);
        rb4.setText(opt[3]);*/

    }


    private void ProgressForQuiz() {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
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
                            video_id = object.getString("id");
                            option1 = object.getString("option1");
                            option2 = object.getString("option2");
                            option3 = object.getString("option3");
                            option4 = object.getString("option4");
                            answer = object.getString("correct_ans");


                            list.setQuestions(question);
                            list.setQuiz_id(video_id);
                            list.setOption_1(option1);
                            list.setOption_2(option2);
                            list.setOption_3(option3);
                            list.setOption_4(option4);
                            list.setAnswer(answer);

                            quiz_list.add(list);


                            //quest=question;
                        }
                        final String Questions[]={question};
                        final String Opt[]={option1,option2,option3,option4};
                        final String Ans[]={answer};

                        Tv_questions.setText(Questions[flag]);
                        rb1.setText(Opt[0]);
                        rb2.setText(Opt[1]);
                        rb3.setText(Opt[2]);
                        rb4.setText(Opt[3]);

                        submitbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //int color = mBackgroundColor.getColor();
                                //mLayout.setBackgroundColor(color);

                                if(radio_g.getCheckedRadioButtonId()==-1)
                                {
                                    Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                RadioButton uans = (RadioButton) findViewById(radio_g.getCheckedRadioButtonId());
                                String ansText = uans.getText().toString();
//                Toast.makeText(getApplicationContext(), ansText, Toast.LENGTH_SHORT).show();
                if(ansText.equals(Ans[flag])) {
                    correct++;
                    //Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
                else {
                    wrong++;
                    //Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
                }

                flag++;

                /*if (score != null)
                    score.setText(""+correct);*/
                if(flag<Questions.length)
                {
                    tv.setText(Questions[flag]);
                    rb1.setText(Opt[flag*4]);
                    rb2.setText(Opt[flag*4 +1]);
                    rb3.setText(Opt[flag*4 +2]);
                    rb4.setText(Opt[flag*4 +3]);
                }
                else
                {
                    marks=correct;
                    Intent in = new Intent(getApplicationContext(),ResultActivity.class);
                    startActivity(in);
                }
                radio_g.clearCheck();
                            }
                        });

                        quitbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(),ResultActivity.class);
                                startActivity(intent);
                            }
                        });


                    }
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
                // refreshLayout.setRefreshing(false);

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("vid", id);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

}
