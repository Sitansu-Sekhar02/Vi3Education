package com.vi3.vi3education.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class TutorRegistration extends AppCompatActivity {

    public static final String tutorReg_url = "https://vi3edutech.com/vi3webservices/register_tutor.php";

    CheckBox showPassword;
    TextView tvLogin;
    EditText etPassword;
    Button btn_signup;
    private EditText editTextName;
    private EditText editEmail;
    private EditText editContact;
    private EditText editAddress;
    private EditText editTextSubject;
    AwesomeValidation awesomeValidation;
    Dialog dialog;
    public static int backPressed = 0;

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tutor_signup);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        preferences=new Preferences(this);

        editTextName = (EditText) findViewById(R.id.EditName);
        editEmail = (EditText) findViewById(R.id.EditEmail);
        editContact = (EditText) findViewById(R.id.EtContact);
        editAddress = (EditText) findViewById(R.id.EtAddress);
        editTextSubject = (EditText) findViewById(R.id.Et_subject);


        btn_signup=findViewById(R.id.btnSignup);
        tvLogin=findViewById(R.id.TvLogin);

        //validate form
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.EditName, RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        awesomeValidation.addValidation(this, R.id.EtContact, "[5-9]{1}[0-9]{9}$", R.string.invalid_PhoneNumber);
        awesomeValidation.addValidation(this, R.id.EditEmail, Patterns.EMAIL_ADDRESS, R.string.invalid_Email);
        awesomeValidation.addValidation(this, R.id.EtAddress, RegexTemplate.NOT_EMPTY, R.string.invalid_address);
        awesomeValidation.addValidation(this, R.id.Et_subject, RegexTemplate.NOT_EMPTY, R.string.invalid_subject);


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    Log.d("success", "onResponse:");
                    if (Utils.isNetworkConnectedMainThred(TutorRegistration.this)) {
                        RegistrationValidation();
                        ProgressForSignup();
                        dialog.show();
                    } else {
                        Toasty.error(TutorRegistration.this,"No Internet Connection!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Log.d("error", "onResponse: ");
                }
               /* startActivity(new Intent(TutorRegistration.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorRegistration.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });


    }

    private void ProgressForSignup() {
        dialog = new Dialog(TutorRegistration.this, android.R.style.Theme_Translucent_NoTitleBar);
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

    private void RegistrationValidation() {
        StringRequest request = new StringRequest(Request.Method.POST, tutorReg_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();

                if (response.equals(""))
                    Log.d("success", "onResponse: " + response);
                startActivity(new Intent(TutorRegistration.this, LoginActivity.class));
                Toast.makeText(TutorRegistration.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toasty.error(TutorRegistration.this, "Some error occurred -> " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("username", editTextName.getText().toString());
                parameters.put("email", editEmail.getText().toString());
                parameters.put("contactno", editContact.getText().toString());
                parameters.put("subject", editTextSubject.getText().toString());
                parameters.put("address", editAddress.getText().toString());
                //parameters.put("user_type","customer");
                return parameters;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(TutorRegistration.this);
        rQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(TutorRegistration.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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