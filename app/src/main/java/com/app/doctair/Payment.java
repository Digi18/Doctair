package com.app.doctair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.AppointAdapter;
import Pojo.OnlineConsultResponse;
import Pojo.PaymentModel;
import Pojo.LoginResponseModel;
import Pojo.Pays;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class Payment extends AppCompatActivity implements PaymentResultWithDataListener {

    Button payNow;
    List<Pays> payList =  new ArrayList<>();
    AppointAdapter adapter;
    String title,des,name,drid,paymentid,phone,jiomeet,str,str1;
    TextView bookPrice,videoPrice,drname,drdesig,drMail,drJioMeet;
    CardView drInfo;

    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Checkout.preload(getApplicationContext());

        ActionBar ab = getSupportActionBar();
        assert  ab!= null;
        ab.setTitle("Payment");
        ab.setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        payNow = findViewById(R.id.payNow);
        bookPrice = findViewById(R.id.bookPrice);
        videoPrice = findViewById(R.id.videoPrice);
        drname = findViewById(R.id.drname);
        drdesig = findViewById(R.id.drdesig);
        drInfo = findViewById(R.id.drInfo);
        drMail = findViewById(R.id.drMail);
        drJioMeet = findViewById(R.id.drJioMeet);

        Intent i = getIntent();
        title = i.getStringExtra("title");
        des = i.getStringExtra("desig");
        name = i.getStringExtra("name");
        drid = i.getStringExtra("drid");
        phone = i.getStringExtra("phone");
        jiomeet = i.getStringExtra("jiomeet");

        drMail.setText(phone);
        drJioMeet.setText(jiomeet);

        drname.setText(name);
        drdesig.setText(des);

        if (title.equals("video")){
            getCharge();
            bookPrice.setVisibility(View.INVISIBLE);
        }
        if(title.equals("appoint")){
            getCharge();
            videoPrice.setVisibility(View.INVISIBLE);
        }

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  String no = "120.00";
                String s = no.substring(0, Integer.parseInt(String.valueOf(no.indexOf('.'))));
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();  */
                startPayment();
            }
        });

    }

    public void startPayment(){

        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID("rzp_test_joWp0uCr6wlDLb");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Doctair.in");
            options.put("description", "Consultation fee");
            //You can omit the image option to fetch the image from dashboard
        //    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
          //  options.put("amount", "19900");
            if(title.equals("video")){

                String vidPrice = str.substring(0, Integer.parseInt(String.valueOf(str.indexOf('.')))) + "00";
                options.put("amount", vidPrice);
            }
            if(title.equals("appoint")){
                String appPrice = str1.substring(0, Integer.parseInt(String.valueOf(str1.indexOf('.')))) + "00";
                options.put("amount", appPrice);
            }

            JSONObject preFill = new JSONObject();
            preFill.put("email", user.getEmail());
            preFill.put("contact", user.getPhoneNumber());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            TastyToast.makeText(activity, "Error in payment: " + e.getMessage(), TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

      //  Toast.makeText(getApplicationContext(),paymentData.getPaymentId(),Toast.LENGTH_SHORT).show();
        paymentid = paymentData.getPaymentId();
        saveData();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(getApplicationContext(),i,Toast.LENGTH_SHORT).show();
    }

    private void getCharge(){

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getPayment().subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(new Observer<PaymentModel>() {
                                   @Override
                                   public void onSubscribe(Disposable d) {

                                   }

                                   @Override
                                   public void onNext(PaymentModel paymentModel) {

                                        payList = paymentModel.getData();

                                        if(title.equals("video")){
                                            str = payList.get(0).getOnlineprice();
                                          videoPrice.setText(String.format("Amount payable - %s", str));
                                        }
                                        if(title.equals("appoint")){
                                           str1 = payList.get(0).getBookprice();
                                          bookPrice.setText(String.format("Amount payable - %s", str1));
                                        }
                                   }

                                   @Override
                                   public void onError(Throwable e) {

                                       Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                   }

                                   @Override
                                   public void onComplete() {

                                   }
                               });
    }

    private void saveData(){

        SharedPreferences shared = getSharedPreferences("Creds", Context.MODE_PRIVATE);
        String userid = shared.getString("userid","");

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.savePaymentData(userid,drid,paymentid,str).subscribeOn(Schedulers.io())
                                                             .observeOn(AndroidSchedulers.mainThread())
                                                             .subscribe(new Observer<OnlineConsultResponse>() {
                                                                 @Override
                                                                 public void onSubscribe(Disposable d) {

                                                                 }

                                                                 @Override
                                                                 public void onNext(OnlineConsultResponse onlineConsultResponse) {

                                                                     String res = onlineConsultResponse.getCode();

                                                                     if(res.equals("200")){

                                                                         drInfo.setVisibility(View.VISIBLE);
                                                                         payNow.setVisibility(View.INVISIBLE);
                                                                     }
                                                                 }

                                                                 @Override
                                                                 public void onError(Throwable e) {

                                                                     Toast.makeText(getApplicationContext(),e.getMessage(),
                                                                             Toast.LENGTH_SHORT).show();
                                                                 }

                                                                 @Override
                                                                 public void onComplete() {

                                                                 }
                                                             });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            onBackPressed();
          /*  Intent i = new Intent(Payment.this,MainActivity.class);
            startActivity(i);
            finish();  */
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
     /*   Intent i = new Intent(Payment.this,MainActivity.class);
        startActivity(i);
        finish();  */
    }

}