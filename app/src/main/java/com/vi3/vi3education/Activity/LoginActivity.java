package com.vi3.vi3education.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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


public class LoginActivity extends AppCompatActivity  {

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    //Preferences
    Preferences preferences;
    public static int backPressed = 0;


    //Textview
    TextView tvSignup;
    Button btnLogin;
    TextView tvLoginButton,tvForgotPassword;
    private EditText email, pass;
    ImageView cancel_popup;

    Dialog dialog;

    public static final String URL = "https://vi3edutech.com/vi3webservices/login.php";
    public static final String forgot_password = "https://vi3edutech.com/vi3webservices/forgot_password.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        btnLogin = findViewById(R.id.btnSignup);
        tvSignup=findViewById(R.id.tv_signup);
        cancel_popup=findViewById(R.id.ivClosePopup);

        tvForgotPassword=findViewById(R.id.tvForgotPassword);
        email = (EditText) findViewById(R.id.et_user_name);
        pass = (EditText) findViewById(R.id.et_password);


        //tvSignupbtn.setOnClickListener(this);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        preferences = new Preferences(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpPopUp();
               /* startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/
            }
        });
        cancel_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelLoginPopup();
            }
        });


        // tvLoginButton.setOnClickListener(this);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.isNetworkConnectedMainThred(LoginActivity.this)) {
                   /* ProgressDialog();
                    dialog.show();
                    ForgotPassword();*/
                    ProgressForgotPassword();

                } else {
                    Toasty.error(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString().trim().length() == 0) {
                    email.setError("EmailId Required");
                    email.requestFocus();

                } else if (pass.getText().toString().trim().length() == 0) {
                    pass.setError("Password Required");
                    pass.requestFocus();


                } else {


                    if (Utils.isNetworkConnectedMainThred(LoginActivity.this)) {

                        ProgressDialog();
                        dialog.show();
                        LoginSuccess();
                    } else {
                        Toasty.error(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });

    }

    private void CancelLoginPopup() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_login);
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
        ImageView popup =  dialog.findViewById(R.id.ivClosePopup);

        //set value
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        //set listener
        popup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void ForgotPassword(EditText retypePass, final EditText newPassword, final EditText userName) {
        StringRequest request = new StringRequest(Request.Method.POST, forgot_password, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("forgot password",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("success").equalsIgnoreCase("true"))
                    {
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        Toasty.success(LoginActivity.this,"Password Changed Successfully",Toast.LENGTH_SHORT).show();
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
                param.put("email",userName.getText().toString());
                param.put("password",newPassword.getText().toString());
                return param;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);

    }

    private void ProgressForgotPassword() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgot_password);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final EditText userName=dialog.findViewById(R.id.et_userName);
        final EditText newPassword=dialog.findViewById(R.id.et_newPassword);
        final EditText retypePass=dialog.findViewById(R.id.et_Retypepass);


        Button tvOk=dialog.findViewById(R.id.reset_pass);
        Button cancel=dialog.findViewById(R.id.cancel);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userName.getText().toString().trim().length() == 0) {
                    userName.setError("Required Field");
                    userName.requestFocus();

                } else if(newPassword.getText().toString().trim().length() == 0) {
                    newPassword.setError("Required Field");
                    newPassword.requestFocus();

                } else if (retypePass.getText().toString().trim().length() == 0) {
                    retypePass.setError("Required Field");
                    retypePass.requestFocus();


                } else {

                    if(newPassword.getText().toString().equals( retypePass.getText().toString())) {
                        //Toasty.success(LoginActivity.this, "password matched", Toast.LENGTH_SHORT).show();
                        newPassword.setText("");
                        retypePass.setText("");
                        ProgressDialog();
                        dialog.show();
                        ForgotPassword(userName,newPassword,retypePass);
                    }else {

                        Toasty.error(LoginActivity.this, "Password not match", Toast.LENGTH_SHORT).show();

                    }
                      /*  newPassword.setText("");
                        retypePass.setText("");
                        ProgressDialog();
                        dialog.show();

                        ForgotPassword(userName,newPassword,retypePass);*/



                }
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
    }

    private void LoginSuccess() {
            StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    dialog.cancel();
                    Log.d("success", "onResponse: "+response);
                    pass.setText("");
                    String res=response;
                    try {
                        JSONObject object=new JSONObject(response);
                        String error=object.getString("error");

                        if(error.equals("false"))
                        {
                            JSONObject user=object.getJSONObject("user");
                            //String user_type=object.getString("usertype");
                            String useremail=user.getString("email");
                            String password=user.getString("password");
                            String user_id=user.getString("id");
                            String name=user.getString("username");
                           // String address=user.getString("address");
                            String contact=user.getString("contactno");

                            preferences.set("user_id",user_id);
                            preferences.set("email",useremail);
                            preferences.set("password",password);
                           // preferences.set("usertype",user_type);
                            preferences.set("name",name);
                           // preferences.set("address",address);
                            preferences.set("contact",contact);

                            preferences.commit();

                            Intent in=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(in);

                        }
                        else{
                            Toasty.warning(getApplicationContext(), "Wrong userId or password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.cancel();
                    Toasty.error(LoginActivity.this, "Some error occurred \n Try again later ", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("email", email.getText().toString());
                    parameters.put("password", pass.getText().toString());
                    return parameters;
                }
            };
            RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
            rQueue.add(request);
        }

    public void ProgressDialog() {
        //Dialog
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


    private void SignUpPopUp() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reg_popup);
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
        TextView tvUser = (TextView) dialog.findViewById(R.id.tvUserReg);
        TextView tvTutor = (TextView) dialog.findViewById(R.id.tvTutorReg);

        //set value
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        //set listener
        tvUser.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
                dialog.dismiss();
            }
        });

        tvTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,TutorRegistration.class));
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
                dialog.dismiss();
            }
        });

    }
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(LoginActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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
