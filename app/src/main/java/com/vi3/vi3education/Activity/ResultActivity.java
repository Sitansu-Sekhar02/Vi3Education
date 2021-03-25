package com.vi3.vi3education.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class ResultActivity extends AppCompatActivity {
    TextView tv, tv2, tv3;
    Button btnRestart,btnGetCert;
    public static int backPressed = 0;
    Dialog dialog;
    public static final String sub_result = "https://vi3edutech.com/vi3webservices/mcqtestresult.php";
    Preferences preferences;
    String corAns;
    String wrongAns;
    String score;
    String finalScore;
    String totalQues;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        preferences = new Preferences(this);


        tv = (TextView)findViewById(R.id.tvres);
        tv2 = (TextView)findViewById(R.id.tvres2);
        tv3 = (TextView)findViewById(R.id.tvres3);
        btnRestart = (Button) findViewById(R.id.btnRestart);
        btnGetCert=findViewById(R.id.btnGetCert);

        StringBuffer sb = new StringBuffer();
        sb.append("Correct answers: " + QuizActivity.correct + "\n");


        StringBuffer sb2 = new StringBuffer();
        sb2.append("Wrong Answers: " + QuizActivity.wrong + "\n");
        StringBuffer sb3 = new StringBuffer();
        sb3.append("Final Score: " + QuizActivity.correct + "\n");

        corAns= String.valueOf(QuizActivity.correct);
        wrongAns= String.valueOf(QuizActivity.wrong);
        score= String.valueOf(QuizActivity.correct*2);
        totalQues= String.valueOf(QuizActivity.size);


        Log.e("score",""+score);


        tv.setText(sb);
        tv2.setText(sb2);
        tv3.setText(sb3);
       // int finalScore=2;
       // int num1 = sb3;

        //final  String Score= String.valueOf(sb3*finalScore);
       // Log.e("resultFinal",""+Score);

        QuizActivity.correct=0;
        QuizActivity.wrong=0;

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
            }
        });


        btnGetCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnectedMainThred(ResultActivity.this)) {
                    Result();
                    ProgressForCart();
                    dialog.show();
                } else {
                    // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    Toasty.error(ResultActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

                }


            }
        });
    }

    private void ProgressForCart() {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
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

    private void Result() {
        StringRequest request = new StringRequest(Request.Method.POST, sub_result, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("submit",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("Success").equalsIgnoreCase("true"))
                    {
                        Intent i = new Intent(ResultActivity.this, MainActivity.class);
                        startActivity(i);
                        ResultActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        Toasty.success(ResultActivity.this,"Certificate will send to your E_mail id",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Log.e("error_response", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("user_id", preferences.get("user_id"));
                param.put("total_attempt", totalQues);
                param.put("result", "pass");
                param.put("score", score);
                param.put("correct_question", corAns);
                param.put("incorect_question", wrongAns);

                return param;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(ResultActivity.this);
        requestQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(ResultActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    backPressed = 0;

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
