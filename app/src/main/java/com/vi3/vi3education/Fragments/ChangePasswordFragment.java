package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vi3.vi3education.Activity.LoginActivity;
import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.FindByTutor;
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

public class ChangePasswordFragment extends Fragment {
    View view;
    Dialog dialog;
    Preferences preferences;
    EditText newPassword;
    EditText retypePassword;
    EditText et_userName;
    Button reset,cancel;


    public static final String change_passowrd = "https://vi3edutech.com/vi3webservices/forgot_password.php";


    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forgot_password, container, false);

        preferences = new Preferences(getActivity());
        newPassword=view.findViewById(R.id.et_newPassword);
        retypePassword=view.findViewById(R.id.et_Retypepass);
        et_userName=view.findViewById(R.id.et_userName);

        reset=view.findViewById(R.id.reset_pass);
        //cancel=view.findViewById(R.id.cancel);

        et_userName.setText(preferences.get("email"));
        et_userName.setEnabled(false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        // replaceFragmentWithAnimation(new DashboardFragment());

                        return true;
                    }
                }
                return false;
            }
        });

        MainActivity.tvHeaderText.setText("Change Password");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newPassword.getText().toString().trim().length() == 0) {
                    newPassword.setError("Required Field");
                    newPassword.requestFocus();

                } else if (retypePassword.getText().toString().trim().length() == 0) {
                    retypePassword.setError("Required Field");
                    retypePassword.requestFocus();

                } else {

                    if (newPassword.getText().toString().equals(retypePassword.getText().toString())) {
                        ProgressForMain();
                        dialog.show();
                        ChangePassword(et_userName,newPassword);
                    } else {

                        Toasty.error(getActivity(), "Password not match", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

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

    private void ChangePassword(final EditText et_userName, final EditText newPassword) {

            StringRequest request = new StringRequest(Request.Method.POST, change_passowrd, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    dialog.cancel();
                    Log.e("forgot password",response);
                    newPassword.setText("");
                    retypePassword.setText("");

                    try {
                        JSONObject jsonObject=new JSONObject(response);

                        if(jsonObject.getString("success").equalsIgnoreCase("true"))
                        {
                            preferences.set("password",jsonObject.getString("Password"));
                            preferences.commit();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                           //replaceFragmentWithAnimation(new DashboardFragment());
                            Toasty.success(getActivity(),"Password Changed Successfully",Toast.LENGTH_SHORT).show();
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
                    param.put("email", et_userName.getText().toString());
                    param.put("password",newPassword.getText().toString());
                    return param;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
            requestQueue.add(request);

    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}

