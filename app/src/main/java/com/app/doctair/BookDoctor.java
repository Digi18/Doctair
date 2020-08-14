package com.app.doctair;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Pojo.BookingResponse;
import Pojo.PaymentModel;
import Pojo.Pays;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class BookDoctor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
                                                          TimePickerDialog.OnTimeSetListener,
                                                          PaymentResultWithDataListener{

    EditText date,timeFrom,timeTo;
    Button bookDoctor;
    TextView amount;
    int hr,min,myHr,myMin,toHr,toMin,hrs,mins;
    List<Pays> payList =  new ArrayList<>();
    String payment,paymentid,doctorid,appointmentdate,appointmenttime,from,to;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_doctor);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        ActionBar ab = getSupportActionBar();
        assert  ab!= null;
        ab.setTitle("Book appointment");
        ab.setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.date);
        timeFrom = findViewById(R.id.timeFrom);
        timeTo = findViewById(R.id.timeTo);
        bookDoctor = findViewById(R.id.bookDoctor);
        amount = findViewById(R.id.amount);

        Intent i = getIntent();
        doctorid = i.getStringExtra("drid");

        getCharge();

        date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  showTime();
            }
        });

        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  showToTime();
            }
        });

        bookDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(date.getText().toString().equals("")){
                    TastyToast.makeText(getApplicationContext(),"Select date",TastyToast.LENGTH_SHORT,
                                TastyToast.ERROR).show();
                }
                else if(timeFrom.getText().toString().equals("")){
                    TastyToast.makeText(getApplicationContext(),"Select time slot",TastyToast.LENGTH_SHORT,
                            TastyToast.ERROR).show();
                }
                else if(timeTo.getText().toString().equals("")){
                    TastyToast.makeText(getApplicationContext(),"Select time slot",TastyToast.LENGTH_SHORT,
                            TastyToast.ERROR).show();                }
                else{
                   startPayment();
                }
            }
        });
    }

    private void showDatePicker(){

        final Calendar cldr = Calendar.getInstance();
        int dayOfMonth = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,this,year,month,dayOfMonth);
        datePickerDialog.show();
    }

    private void showTime(){

        Calendar c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hr, min, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    private void showToTime(){

        Calendar c = Calendar.getInstance();
        hrs = c.get(Calendar.HOUR_OF_DAY);
        mins = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hrs, mins, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

       String dat = dayOfMonth + " - " + (month+1) + " - " + year;
       appointmentdate = year+"-"+(month+1)+"-"+dayOfMonth;
       date.setText(dat);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        myHr = hourOfDay;
        myMin = minute;

        toHr = hourOfDay;
        toMin = minute;

        if(myHr<12 && myHr>=0){
            timeFrom.setText(myHr+":"+myMin + " AM");
           // appointmenttime = myHr+":"+myMin + " AM";
            from = myHr+":"+myMin + " AM";
        }
        else{
            if(myHr == 12){
                timeFrom.setText(myHr+":"+myMin + " PM");
                //  appointmenttime = myHr+":"+myMin + " PM";
                 from = myHr+":"+myMin + " PM";
            }
            else{
                myHr = myHr-12;
                timeFrom.setText(myHr+":"+myMin + " PM");
                //  appointmenttime = myHr+":"+myMin + " PM";
                 from = myHr+":"+myMin + " PM";
            }
        }


        if(toHr<12 && toHr>=0){
            timeTo.setText(toHr+":"+toMin + " AM");
            to = toHr+":"+toMin + " AM";
        }
        else{
            if(toHr == 12){
                timeTo.setText(toHr+":"+toMin + " PM");
                to = toHr+":"+toMin + " PM";
            }
            else{
                toHr = toHr-12;
                timeTo.setText(toHr+":"+toMin + " PM");
                to = toHr+":"+toMin + " PM";
            }
        }
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
                String appPrice = payment.substring(0, Integer.parseInt(String.valueOf(payment.indexOf('.')))) + "00";
                options.put("amount", appPrice);


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

                            payment = payList.get(0).getBookprice();
                            amount.setText(String.format("Amount payable: %s", payment));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        paymentid = paymentData.getPaymentId();
        saveData();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(getApplicationContext(),i,Toast.LENGTH_SHORT).show();
    }

    private void saveData(){

        SharedPreferences shared = getSharedPreferences("Creds", Context.MODE_PRIVATE);
        String userid = shared.getString("userid","");

      //  Toast.makeText(getApplicationContext(),paymentid,Toast.LENGTH_LONG).show();

      //  Log.d("data",userid + " " + doctorid + " " + paymentid+ " " + " " + " " + payment + " " + " " + appointmentdate + " " + " " + appointmenttime);

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        appointmenttime = timeFrom.getText().toString()+"-"+timeTo.getText().toString();

        apiService.saveBookingData(userid,doctorid,paymentid,payment,appointmentdate,appointmenttime).subscribeOn(Schedulers.io())
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(new Observer<BookingResponse>() {
                                         @Override
                                         public void onSubscribe(Disposable d) {

                                         }

                                         @Override
                                         public void onNext(BookingResponse bookingResponse) {

                                             String res = bookingResponse.getCode();

                                             if(res.equals("200")){
                                                // Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();

                                                 Intent i = new Intent(BookDoctor.this,Thanks.class);
                                                 startActivity(i);
                                                 finish();
                                             }
                                         }

                                         @Override
                                         public void onError(Throwable e) {
                                              TastyToast.makeText(getApplicationContext(),e.getMessage(),TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                                         }

                                         @Override
                                         public void onComplete() {

                                         }
                                     });

    }
}