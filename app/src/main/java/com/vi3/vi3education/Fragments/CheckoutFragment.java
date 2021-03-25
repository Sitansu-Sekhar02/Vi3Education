package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.Activity.RazorpayActivity;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.CartItem;
import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class CheckoutFragment extends Fragment  {

    public static final String TAG = CheckoutFragment.class.getSimpleName();
    //view
    View v;

    //recyclerview
    RecyclerView recyclerview;

    private List<CartItem> cartList;
    private CartAdapter cartAdapter;
    public static final String cart_url = "https://vi3edutech.com/vi3webservices/fetch_cart_product.php";
    public static final String apply_couppon = "https://vi3edutech.com/vi3webservices/offline_affiliate.php";


    double Total_price=0;

    //Textview
    TextView tvProceed;
    TextView tvChange;
    TextView tvCartPrice;


    TextView Username,UserAddress;
    TextView checkoutPrice;
    TextView totalAmount;
    EditText et_Coupon;
    TextView tvApplyCoupon;
    Dialog dialog;
    LinearLayout llcartItem;
    ImageView emptyCart;

    //Preference
    Preferences preferences;


    public static double amount=0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_checkout, container, false);

        recyclerview=v.findViewById(R.id.recyclerview);
        cartList=new ArrayList<>();

        tvProceed=v.findViewById(R.id.tvProceed);
        Username =v.findViewById(R.id.tvAddress);
        UserAddress=v.findViewById(R.id.tvPhoneNumber);
        checkoutPrice=v.findViewById(R.id.tvCheckoutprice);
        tvCartPrice=v.findViewById(R.id.tvCartprice);
        et_Coupon=v.findViewById(R.id.EtApplyCoupon);
        tvApplyCoupon=v.findViewById(R.id.tvApply);


        emptyCart = v.findViewById(R.id.gif);
        llcartItem = v.findViewById(R.id.llcartItem);

        totalAmount=v.findViewById(R.id.tvTotalAmount);


        preferences=new Preferences(getActivity());

        Username.setText(preferences.get("name"));
        UserAddress.setText(preferences.get("email"));

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                         replaceFragmentWithAnimation(new CartFragment());

                        return true;
                    }
                }
                return false;
            }
        });


        MainActivity.ivCart.setVisibility(View.GONE);
        MainActivity.tvCount.setVisibility(View.GONE);

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            cartFragmentItem();
            ProgressForCheckout();
            dialog.show();
        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        //setvalue
        MainActivity.tvHeaderText.setText("Checkout");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);
        //Onclick listener
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               replaceFragmentWithAnimation(new CartFragment());
            }
        });

        tvApplyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_Coupon.getText().toString().trim().length() == 0) {
                    et_Coupon.setError("Please enter a valid coupon");
                    et_Coupon.requestFocus();
                }else {
                    AapplyCouponApi();
                    ProgressForCheckout();
                    dialog.show();

                }

            }
        });

        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), RazorpayActivity.class);
                //i.putExtra("price","100");
                amount=Total_price;
                Log.e("rrrr",""+Total_price);
                startActivity(i);


            }
        });


        return v;
    }

    private void AapplyCouponApi() {
        StringRequest request = new StringRequest(Request.Method.POST,apply_couppon, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //et_Coupon.setText("");
                dialog.cancel();
                Log.e("apply code",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("error").equalsIgnoreCase("false"))
                    {
                        preferences.set("referal_code",jsonObject.getString("agentreferalcode"));
                        preferences.commit();
                        Toasty.success(getActivity(),"Coupon applied successfully",Toast.LENGTH_SHORT).show();
                    }else {
                        Toasty.error(getActivity(),"Invalid coupon",Toast.LENGTH_SHORT).show();
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
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("offlinereferalcode",et_Coupon.getText().toString());
                Log.e("code",""+parameters);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }



    private void ProgressForCheckout() {
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


    private void cartFragmentItem() {
        StringRequest request = new StringRequest(Request.Method.POST,cart_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("checkout",response);
                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("Success").equalsIgnoreCase("1")) {
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            CartItem list = new CartItem();
                            String cart_id = object.getString("cart_id");
                            String video_name = object.getString("product_name");
                            String video_id = object.getString("pid");
                            String user_id = object.getString("user_id");
                            String video_image = "https://vi3edutech.com/images/course_images/" + object.getString("image");
                            String video_price = object.getString("price");

                            list.setCart_id(cart_id);
                            list.setUser_id(user_id);
                            list.setVideo_id(video_id);
                            list.setProduct_image(video_image);
                            list.setVideo_name(video_name);
                            list.setVideo_price(video_price);

                            cartList.add(list);

                            double price=Double.parseDouble(video_price);


                            DecimalFormat df = new DecimalFormat("##.###");

                            Total_price = Double.parseDouble(String.valueOf(Total_price + price));
                            Log.e("price add",""+Total_price);
                            checkoutPrice.setText(String.valueOf( Total_price));
                            /*preferences.set("Total_price", String.valueOf(Total_price));
                            Log.e("Total_price",""+preferences.set("Total_price"+Total_price));*/

                            checkoutPrice.setText("Total Amount \u20b9"+(Total_price));

                        }
                    }
                    DecimalFormat df = new DecimalFormat("##.###");

                    totalAmount.setText("\u20b9"+df.format(Total_price));

                    setAdapter();
                }

                catch (JSONException e) {
                    Log.d("JSONException", e.toString());
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
        recyclerview.setLayoutManager(linearLayoutManager);
        cartAdapter = new CartAdapter(cartList, getActivity());
        Log.e("check adapter", "cart adapter" + cartAdapter);
        recyclerview.setAdapter(cartAdapter);
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



    //=============================Adapter====================================================//
    private class CartHolder extends RecyclerView.ViewHolder {
        TextView tvOldPrice;
        TextView cart_item_number;
        TextView tvSave;
        TextView tvProductName,qntyPrice;
        TextView tvFinalprice,tvcartProductSize;
        TextView cart_item_delete;
        ImageView cart_item_image;
        LinearLayout LinkShare;
        TextView tvFblink,tvWhatsapplink;



        public CartHolder(View itemView) {
            super(itemView);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
            tvFinalprice = itemView.findViewById(R.id.price);
            tvSave = itemView.findViewById(R.id.tvSave);
            cart_item_image = itemView.findViewById(R.id.cart_item_image);
            cart_item_delete = itemView.findViewById(R.id.cart_item_delete);
            tvProductName = itemView.findViewById(R.id.tvcartProductName);
            cart_item_image = itemView.findViewById(R.id.cart_item_image);
            cart_item_delete = itemView.findViewById(R.id.cart_item_delete);
            tvProductName = itemView.findViewById(R.id.tvcartProductName);
            //tvFblink=itemView.findViewById(R.id.tvLinkfb);
            tvWhatsapplink=itemView.findViewById(R.id.tvWhatsaplink);
            LinkShare=itemView.findViewById(R.id.linkLinear);
        }
    }

    private class CartAdapter extends RecyclerView.Adapter<CartHolder> {
        private List<CartItem> mModel;
        private Context mContext;

        public CartAdapter(List<CartItem> mModel, Context mContext) {
            this.mModel = mModel;
            this.mContext = mContext;
        }
        public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull final CartHolder holder, final int position) {

            holder.tvProductName.setText(mModel.get(position).getVideo_name());
            Glide.with(mContext)
                    .load(mModel.get(position).getProduct_image())
                    .into(holder.cart_item_image);
            holder.tvFinalprice.setText(mModel.get(position).getVideo_price());

            holder.cart_item_delete.setVisibility(View.GONE);
            holder.LinkShare.setVisibility(View.VISIBLE);

           /* holder.tvFblink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
            final String referal_code=preferences.get("refral");
            Log.e("referalCode",""+referal_code);

            holder.tvWhatsapplink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent shareIntent =   new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareBody="VI3 EDUTECH Pvt.Ltd.";
                    String subject="Referral Code";
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    String app_url = "https://play.google.com/store/apps/details?id=com.vi3.vi3education"+"/"+referal_code;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
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


}