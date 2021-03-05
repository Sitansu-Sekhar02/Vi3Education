package com.vi3.vi3education.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.vi3.vi3education.Activity.MainActivity;
import com.vi3.vi3education.Activity.Utils;
import com.vi3.vi3education.Model.CartItem;
import com.vi3.vi3education.Model.GetAllCourseModel;
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


public class CartFragment extends Fragment {


    //recyclerview
    RecyclerView recyclerView;

    Preferences preferences;
    int final_price= 0;
    double Total_price=0.0;
    double tax_prod_price=0.0;

    double finalProductPrice=0.0;

    double prod_finalPrice=0.0;

    String product_id;
    String cart_id;

    Dialog dialog;
    double result;
    //view
    View view;

    private List<CartItem> cartList;
    private CartAdapter cartAdapter;
    public static final String cart_url = "https://vi3edutech.com/vi3webservices/fetch_cart_product.php";
    public static final String cart_delete = "https://vi3edutech.com/vi3webservices/delete_cart_item.php";
    public  static  final  String qnty_update="http://vsfastirrigation.com/webservices/update_quantity.php";


    //Gridlayout
    GridLayoutManager mGridLayoutManager;

    //Textview
    TextView tvProceed;
    TextView tvCartPrice;
    ImageView emptyCart;
    Button shopNow;
   //EditText productQuantity;

    LinearLayout llcartItem;
    LinearLayout cartEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.cartlistfragment, container, false);

        preferences=new Preferences(getActivity());

        //DrawerActivity

        MainActivity.tvHeaderText.setText("My Cart");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24);
        MainActivity.ivCart.setVisibility(View.GONE);
        MainActivity.bottom_nav.setVisibility(View.GONE);
        MainActivity.tvCount.setVisibility(View.GONE);

        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
               /* if(AppSettings.fromPage.equalsIgnoreCase("1"))
                {
                    Intent i = new Intent(getActivity(), DrawerActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                }
                else if(AppSettings.fromPage.equalsIgnoreCase("2"))
                {
                    replaceFragmentWithAnimation(new ProductListingFragment());
                }
                else
                {

                }*/
            }
        });

        cartList=new ArrayList<>();

        recyclerView= view.findViewById(R.id.recyclerview);
        tvProceed= view.findViewById(R.id.tvProceed);
        tvCartPrice=view.findViewById(R.id.tvCartprice);
        emptyCart = view.findViewById(R.id.gif);
        llcartItem = view.findViewById(R.id.llcartItem);
        shopNow=view.findViewById(R.id.shopNow);
        cartEmpty=view.findViewById(R.id.empty_cart);
        //productQuantity=view.findViewById(R.id.quantity);

        shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaceFragmentWithAnimation(new DashboardFragment());
            }
        });
        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            cartFragmentItem();
            ProgressForCart();
            dialog.show();
        } else {
           // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();

        }

        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    replaceFragmentWithAnimation(new CheckoutFragment());
                } else {
                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void cartFragmentItem(){
        StringRequest request = new StringRequest(Request.Method.POST,cart_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("cart",response);
                //progressDialog.cancel();
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


                           /* prod_finalPrice=(price*tax)/100;
                            tax_prod_price=prod_finalPrice+price;
                            Log.e("tax",""+prod_finalPrice);

                            finalProductPrice= Double.parseDouble(String.valueOf(tax_prod_price));

                            Log.e("fff",""+finalProductPrice);*/
                            DecimalFormat df = new DecimalFormat("##.###");

                            Total_price = Double.parseDouble(String.valueOf(Total_price + price));
                            Log.e("price add",""+Total_price);
                            tvCartPrice.setText(String.valueOf( Total_price));
                            tvCartPrice.setText("Total Amount \u20b9"+(Total_price));

                        }
                    }

                    setAdapter();
                }

                catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }
               // Log.e("sss","cartlist"+cartList);

                if (cartList.isEmpty()){
                    llcartItem.setVisibility(View.GONE);
                    cartEmpty.setVisibility(View.VISIBLE);
                } else {
                    llcartItem.setVisibility(View.VISIBLE);
                    cartEmpty.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();

                // progressDialog.cancel();
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
            recyclerView.setLayoutManager(linearLayoutManager);
            cartAdapter = new CartAdapter(cartList, getActivity());
            recyclerView.setAdapter(cartAdapter);

    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvOldPrice;
        TextView cart_item_number;
        TextView tvSave;
        TextView tvProductName,qntyPrice;
        TextView tvFinalprice,tvSize,tvcartProductSize;
        EditText productQuantity;
        TextView cart_item_delete;
        ImageView cart_item_image;

        public Holder(View itemView) {
            super(itemView);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
            tvFinalprice = itemView.findViewById(R.id.price);
            tvSave = itemView.findViewById(R.id.tvSave);
            cart_item_image = itemView.findViewById(R.id.cart_item_image);
            cart_item_delete = itemView.findViewById(R.id.cart_item_delete);
            tvProductName = itemView.findViewById(R.id.tvcartProductName);



        }
    }

    private class CartAdapter extends RecyclerView.Adapter<Holder> {
        //ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        private List<CartItem> mModel;
        private Context mContext;

        public CartAdapter(List<CartItem> mModel, Context mContext) {
            this.mModel = mModel;
            this.mContext = mContext;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvProductName.setText(mModel.get(position).getVideo_name());
            Glide.with(mContext)
                    .load(mModel.get(position).getProduct_image())
                    .into(holder.cart_item_image);
            holder.tvFinalprice.setText(mModel.get(position).getVideo_price());



            holder.cart_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCartItem();
                    product_id=cartList.get(position).getVideo_id();
                    mModel.remove(position);
                    notifyDataSetChanged();


                   /* int count = Integer.parseInt(holder.cart_item_number.getText().toString());
                    if (count > 1) {
                        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);
                        count=count-1;

                        int counter=preferences.getInt("count");
                        int totalcount=counter-1;
                        preferences.set("count",totalcount);
                        preferences.commit();
                        MainActivity.tvCount.setText(""+totalcount);
                        holder.cart_item_number.setText(String.valueOf(count));

                    } else {
                        int counter = preferences.getInt("count");
                        int totalcount = counter - 1;
                        preferences.set("count", totalcount);
                        preferences.commit();
                        MainActivity.tvCount.setText("" + totalcount);
                        //deleteCartItem(mModel.get(getAdapterPosition()).getProduct_id());
                    }*/

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



    private void ProgressForCart() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setContentView(R.layout.progress_for_cart);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);

    }

    private void deleteCartItem() {
        StringRequest request = new StringRequest(Request.Method.POST,cart_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // dialog.cancel();
                Toasty.success(getActivity(),"Item Deleted Successfully",Toast.LENGTH_SHORT).show();
                replaceFragmentWithAnimation(new CartFragment());
                Log.e("delete",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.cancel();
                Log.e("error_response", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("user_id",preferences.get("user_id"));
                params.put("pid",product_id);
                Log.e("delete",""+params);
                return params;
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
    public void ShakeAnimation(View view)
    {
        final Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        view.startAnimation(animShake);
    }

}