package com.app.doctair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import Adapters.AppointAdapter;
import Pojo.Appointments;
import Pojo.AppointmentsModel;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class MyAppointments extends AppCompatActivity {

    RecyclerView appointmentRecycler;
    ProgressBar progress;
    AppointAdapter appointAdapter;
    List<Appointments> appointmentList = new ArrayList<>();
    LinearLayout nodoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("My appointments");

        appointmentRecycler = findViewById(R.id.appointmentRecycler);
        progress = findViewById(R.id.progress);
        nodoc = findViewById(R.id.nodoc);

        fetchAppointments();
    }

    private void fetchAppointments(){

        SharedPreferences shared = getSharedPreferences("Creds", Context.MODE_PRIVATE);
        String userid = shared.getString("userid","");

        appointmentRecycler.setHasFixedSize(true);
        appointmentRecycler.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getAppointments(userid).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<AppointmentsModel>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(AppointmentsModel appointmentsModel) {

                                            appointmentList = appointmentsModel.getData();

                                            if(appointmentList != null){

                                                progress.setVisibility(View.INVISIBLE);
                                                appointAdapter = new AppointAdapter(MyAppointments.this,appointmentList);
                                                appointmentRecycler.setAdapter(appointAdapter);
                                            }
                                            else{

                                                progress.setVisibility(View.INVISIBLE);
                                                nodoc.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                            progress.setVisibility(View.INVISIBLE);
                                            TastyToast.makeText(getApplicationContext(),e.getMessage(),TastyToast.LENGTH_SHORT,
                                                       TastyToast.ERROR).show();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){

         /*   Intent i = new Intent(MyAppointments.this,AccountFragment.class);
            startActivity(i);
            finish();  */
           onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
     super.onBackPressed();
      /*  Intent i = new Intent(MyAppointments.this,AccountFragment.class);
        startActivity(i);
        finish();   */
    }
}