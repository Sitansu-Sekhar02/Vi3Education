package com.vi3.vi3education.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
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
import androidx.annotation.RequiresApi;
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


public class CheckoutFragment extends Fragment implements PaymentResultListener {

    public static final String TAG = CheckoutFragment.class.getSimpleName();
    //view
    View v;

    //recyclerview
    RecyclerView recyclerview;

    private List<CartItem> cartList;
    private CartAdapter cartAdapter;
    public static final String cart_url = "https://vi3edutech.com/vi3webservices/fetch_cart_product.php";
    public static final String continue_order = "http://vsfastirrigation.com/webservices/checkout_order.php";


    int final_price= 0;
    String product_id;
    //Gridlayoutmanger
    GridLayoutManager mGridLayoutManager;
    double result=0;
    double Total_price=0;
    double razor_pay_price=0;

    //Textview
    TextView tvProceed;
    TextView tvChange;
    TextView tvCartPrice;


    TextView Username,UserAddress;
    TextView checkoutPrice;
    TextView totalAmount;
    Dialog dialog;
    LinearLayout llcartItem;
    ImageView emptyCart;
    double resulOfGst;
    double finalResult;

    double tax_prod_price=0.0;

    double finalProductPrice=0.0;

    double prod_finalPrice=0.0;



    //Preference
    Preferences preferences;

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


        emptyCart = v.findViewById(R.id.gif);
        llcartItem = v.findViewById(R.id.llcartItem);

        totalAmount=v.findViewById(R.id.tvTotalAmount);


        preferences=new Preferences(getActivity());

        Username.setText(preferences.get("name"));
        UserAddress.setText(preferences.get("email"));


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


        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RazorPayCheckout(Total_price);
               /* Intent intent = new Intent(getActivity(), RazorpayActivity.class);
                intent.putExtra("amount", String.valueOf(Total_price));
                startActivity(intent);

                Intent i = new Intent(getActivity(), RazorpayActivity.class);
                startActivity(i);
*/
               /* if (Utils.isNetworkConnectedMainThred(getActivity())) {
                     OrderConfirm();
                     SuccessPopup();
                     dialog.show();
                } else {
                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }*/
                //AdminDashActivity.count++;
            }
        });



        return v;
    }

    private void RazorPayCheckout(double total_price) {
        final CheckoutFragment activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "VI3 EDUTECH PVT.LTD.");
            options.put("description", "Video Charges");// You can omit the image option to fetch the image from dashboard
            // set image of you brand
             options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", total_price * 100);

            JSONObject preFill = new JSONObject();
            // Preset email and phone
            // preFill.put("email", "test@razorpay.com");
            // preFill.put("contact", "123456789");

            options.put("prefill", preFill);

            co.open(getActivity(), options);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
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

    private void  OrderConfirm() {
        StringRequest request = new StringRequest(Request.Method.POST,continue_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("place order",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("success").equalsIgnoreCase("order placed successfully"))
                    {
                        preferences.set("order_id",jsonObject.getString("order_id"));
                        preferences.set("order_date",jsonObject.getString("order_date"));
                        preferences.set("order_total",jsonObject.getString("order_total"));
                        preferences.set("gst_price",jsonObject.getString("gst_price"));
                        preferences.set("total_orderPrice",jsonObject.getString("total_orderPrice"));
                        preferences.commit();
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
                parameters.put("user_id",preferences.get("user_id"));
                //parameters.put("product_price", String.valueOf(tax_prod_price));
                parameters.put("order_total", String.valueOf(new DecimalFormat("##.##").format(finalResult)));
                //parameters.put("subtotal", String.valueOf(result));
                //parameters.put("gst_price",String.valueOf("resulOfGst"));
               // parameters.put("total_orderPrice",String.valueOf(finalResult));

                Log.e("check","wwww"+parameters);
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

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
                            String video_image = "https://vi3edutech.com/uploadvideo/" + object.getString("img");
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
    public void SuccessPopup() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //  dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(R.color.black_trans);
        dialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    preferences.set("count", 0);
                    preferences.commit();
                    //replaceFragmentWithAnimation(new OrderConfirmationFragment());
                }
            }
        };

        timerThread.start();
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            replaceFragmentWithAnimation(new YourCourseFragment());
            /*Dialog dialog = Helper.getSuccessDialog(this);
            TextView textViewGoHome = dialog.findViewById(R.id.textViewGoHome);
            textViewGoHome.setOnClickListener(v -> {
                dialog.dismiss();
                clearCart();
                cartCount = 0;
                textViewCartCount.setVisibility(View.GONE);
                fragmentManager.beginTransaction().replace(R.id.main_content, productsFragment).commit();
            });
            dialog.show();*/
            Toasty.success(getActivity(), s, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toasty.error(getActivity(), "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    //=============================Adapter====================================================//
    private class CartHolder extends RecyclerView.ViewHolder {
        TextView tvOldPrice;
        TextView cart_item_number;
        TextView tvSave;
        ImageButton cart_quant_minus;
        ImageButton cart_quant_add;
        TextView tvProductName,qntyPrice;
        TextView tvFinalprice,tvcartProductSize;
        EditText productQuantity;
        ImageButton checkquantity;
        TextView cart_item_delete;
        ImageView cart_item_image;
        TextView tvDelCharge;
        TextView tvSize;


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