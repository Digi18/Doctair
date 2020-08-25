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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import Adapters.ConsultAdapter;
import Pojo.ConsultationModel;
import Pojo.Consultations;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;


public class ConsultFragment extends Fragment {

    ProgressBar consProgress;
    RecyclerView consultRecycler;
    LinearLayout nodataLayout;
    List<Consultations> consultationsList = new ArrayList<>();
    ConsultAdapter adapter;

    public ConsultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consult, container, false);

        consProgress = view.findViewById(R.id.consProgress);
        consultRecycler = view.findViewById(R.id.consultRecycler);
        nodataLayout = view.findViewById(R.id.nodataLayout);

        consultRecycler.setHasFixedSize(true);
        consultRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchData();

        return view;
    }

    private void fetchData(){

        SharedPreferences shared = getActivity().getSharedPreferences("Creds", Context.MODE_PRIVATE);
        String userid = shared.getString("userid","");

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getConsultations(userid).subscribeOn(Schedulers.io())
                                           .observeOn(AndroidSchedulers.mainThread())
                                           .subscribe(new Observer<ConsultationModel>() {
                                               @Override
                                               public void onSubscribe(Disposable d) {

                                               }

                                               @Override
                                               public void onNext(ConsultationModel consultationModel) {

                                                   consultationsList = consultationModel.getData();

                                                   if(consultationsList != null){

                                                       consProgress.setVisibility(View.INVISIBLE);
                                                       adapter  = new ConsultAdapter(getActivity(),consultationsList);
                                                       consultRecycler.setAdapter(adapter);
                                                   }
                                                   else{
                                                       nodataLayout.setVisibility(View.VISIBLE);
                                                       consProgress.setVisibility(View.INVISIBLE);
                                                   }
                                               }

                                               @Override
                                               public void onError(Throwable e) {

                                                   consProgress.setVisibility(View.INVISIBLE);
                                                   nodataLayout.setVisibility(View.VISIBLE);
                                                   TastyToast.makeText(getActivity(),e.getMessage(),TastyToast.LENGTH_SHORT,
                                                           TastyToast.ERROR).show();
                                               }

                                               @Override
                                               public void onComplete() {

                                               }
                                           });

    }
}