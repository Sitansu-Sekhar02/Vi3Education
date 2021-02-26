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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.FindByTutor;
import com.vi3.vi3education.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FindByTutorFragment extends Fragment {
    View view;
    Dialog dialog;
    public static final String url_findbytutor = "https://vi3edutech.com/vi3webservices/fetch_tutor_details.php";
    private List<FindByTutor> tutors;
    private FindTutoAdapter adapter;
    EditText search_subject;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_tutor_fragment, container, false);

        tutors = new ArrayList<>();
        recyclerView=view.findViewById(R.id.find_tutor);
        search_subject=view.findViewById(R.id.search_subject);


        search_subject.addTextChangedListener(new TextWatcher() {
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

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            jsonRequest();
            ProgressForMain();
            dialog.show();
        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return  view;

    }

    private void filter(String toString) {
        List<FindByTutor> temp = new ArrayList();
        for(FindByTutor d: tutors){
            if(d.getTutor_subject().toLowerCase().contains(toString.toLowerCase())){
                temp.add(d);
            }else{
            }
        }
        //update recyclerview
        adapter.updateList(temp);
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
        StringRequest request = new StringRequest(Request.Method.POST,url_findbytutor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response1",response);
                // refreshLayout.setRefreshing(false);
                dialog.cancel();
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        FindByTutor user = new FindByTutor();

                        String tutor_id=jsonObject.getString("id");
                        String tutor_name=jsonObject.getString("username");
                        String tutor_email=jsonObject.getString("email");
                        String tutor_contact=jsonObject.getString("contactno");
                        String tutor_location=jsonObject.getString("address");
                        String tutor_subject=jsonObject.getString("subject");



                        user.setTutor_id(tutor_id);
                        user.setTutor_name(tutor_name);
                        user.setTutor_email(tutor_email);
                        user.setTutor_contact(tutor_contact);
                        user.setTutor_subject(tutor_subject);
                        user.setTutor_location(tutor_location);

                        tutors.add(user);

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
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FindTutoAdapter(tutors,getActivity());
        recyclerView.setAdapter(adapter);
    }

    //*Recyclerview Adapter*//
    private class FindTutoAdapter extends RecyclerView.Adapter<Holder> implements Filterable {

        private List<FindByTutor> category;
        private List<FindByTutor> mModel;
        private Context mContext;

        public FindTutoAdapter(List<FindByTutor>mModel,Context mContext) {
            this.mModel=mModel;
            this.mContext=mContext;
            category=new ArrayList<>(mModel);

        }
        public void updateList(List<FindByTutor> list){
            mModel = list;
            notifyDataSetChanged();
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.find_tutor_details, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tutorName.setText(mModel.get(position).getTutor_name());
            holder.tutoremail.setText(mModel.get(position).getTutor_email());
            holder.tutorSubject.setText(mModel.get(position).getTutor_subject());
            holder.tutorLocation.setText(mModel.get(position).getTutor_location());
            holder.tutorContact.setText(mModel.get(position).getTutor_contact());

        }
        public int getItemCount() {
            return mModel.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public Filter getFilter() {
            return filter;
        }
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<FindByTutor> filteredList=new ArrayList<>();
                if (charSequence.toString().isEmpty()){
                    filteredList.addAll(tutors);
                }else {
                    for (FindByTutor product:tutors){
                        if (product.getTutor_subject().toLowerCase().contains(charSequence.toString().toLowerCase())){
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
                mModel.addAll((Collection<? extends FindByTutor>) filterResults.values);
                notifyDataSetChanged();
            }
        };


    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tutorName;
        TextView tutoremail;
        TextView tutorSubject;
        TextView tutorLocation;
        TextView tutorContact;


        public Holder(View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.tutor_name);
            tutoremail = itemView.findViewById(R.id.tutor_email);
            tutorContact = itemView.findViewById(R.id.tutor_contact);
            tutorLocation = itemView.findViewById(R.id.tutor_location);
            tutorSubject = itemView.findViewById(R.id.tutor_subject);

        }
    }


}

