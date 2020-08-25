package com.app.doctair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AppointmentsFragment extends Fragment {

    LinearLayout nodoc;
    ProgressBar progress;
    RecyclerView appointmentRecycler;
    AppointAdapter appointAdapter;
    List<Appointments> appointmentList = new ArrayList<>();

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

              nodoc = view.findViewById(R.id.nodoc);
              progress = view.findViewById(R.id.progress);
              appointmentRecycler = view.findViewById(R.id.appointmentRecycler);

              appointmentRecycler.setHasFixedSize(true);
              appointmentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

              fetchData();

        return view;
    }

    private void fetchData(){

        SharedPreferences shared = getActivity().getSharedPreferences("Creds", Context.MODE_PRIVATE);
        String userid = shared.getString("userid","");

        appointmentRecycler.setHasFixedSize(true);
        appointmentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                                                      appointAdapter = new AppointAdapter(getActivity(),appointmentList);
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
                                                  TastyToast.makeText(getActivity(),e.getMessage(),TastyToast.LENGTH_SHORT,
                                                          TastyToast.ERROR).show();
                                              }

                                              @Override
                                              public void onComplete() {

                                              }
                                          });

    }
}