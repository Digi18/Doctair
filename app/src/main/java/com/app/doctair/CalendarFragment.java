package com.app.doctair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import Adapters.DoctorAdapter;
import Pojo.AllStates;
import Pojo.City;
import Pojo.CityModel;
import Pojo.Doctor;
import Pojo.DoctorModel;
import Pojo.Speciality;
import Pojo.SpecialityModel;
import Pojo.States;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class CalendarFragment extends Fragment {

    Spinner spinnerState,spinnerCity,spinnerHospital,spinnerSpeciality;

    ImageView docImage;
    TextView docText,city,special;

    ProgressBar prg;

    List<AllStates> stateList = new ArrayList<>();
    List<City> cityList = new ArrayList<>();
    List<Speciality> specialityList = new ArrayList<>();
    List<Doctor> drList = new ArrayList<>();

    ArrayList<String> stateSpinnerList = new ArrayList<>();
    ArrayList<String> citySpinnerList = new ArrayList<>();
 //   ArrayList<String> hosSpinnerList = new ArrayList<>();
    ArrayList<String> speSpinnerList = new ArrayList<>();

    ArrayAdapter<String> stateAdapter,cityAdapter,hosAdapter,speAdapter;

    DoctorAdapter doctorAdapter;

    RecyclerView drRecycler;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Checkout.preload(getActivity());

        spinnerState = view.findViewById(R.id.spinnerState);
        spinnerCity = view.findViewById(R.id.spinnerCity);
     //   spinnerHospital = view.findViewById(R.id.spinnerHospital);
        spinnerSpeciality = view.findViewById(R.id.spinnerSpeciality);
        drRecycler = view.findViewById(R.id.drRecycler);
        docText = view.findViewById(R.id.docText);
        docImage = view.findViewById(R.id.doc);
        prg = view.findViewById(R.id.prg);
        city = view.findViewById(R.id.city);
        special = view.findViewById(R.id.special);

        citySpinnerList.add("Select city");
        speSpinnerList.add("Select speciality");

        cityAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,citySpinnerList);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 1){
                    AllStates allStates = stateList.get(position - 1);
                  //  Toast.makeText(getActivity(),allStates.getId(),Toast.LENGTH_SHORT).show();
                    getCities(allStates.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 1){
                    City city = cityList.get(position - 1);
                   // Toast.makeText(getActivity(),city.getId(),Toast.LENGTH_SHORT).show();
                  //  getHospitals(city.getId());
                    fetchSpeciality(city.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    /*    spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 1){
                    Hospital hospital = hosList.get(position - 1);
                   //  Toast.makeText(getActivity(),hospital.getId(),Toast.LENGTH_SHORT).show();
                    fetchSpeciality(hospital.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });   */

        spinnerSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 1){
                      Speciality speciality = specialityList.get(position - 1);
                    //  Toast.makeText(getActivity(),speciality.getId() + "" + speciality.getCid(),Toast.LENGTH_SHORT).show();
                    fetchDoctors(speciality.getId(),speciality.getCid());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Retrofit retrofit   = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.allStates().subscribeOn(Schedulers.io())
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribe(new Observer<States>() {
                                  @Override
                                  public void onSubscribe(Disposable d) {

                                  }

                                  @Override
                                  public void onNext(States states) {

                                     stateList = states.getData();

                                     stateSpinnerList.add("Select state");


                                    for(int i =0;i<stateList.size();i++){

                                         stateSpinnerList.add(stateList.get(i).getName());
                                     }

                                     stateAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,stateSpinnerList);
                                     spinnerState.setAdapter(stateAdapter);
                                  }

                                  @Override
                                  public void onError(Throwable e) {

                                      TastyToast.makeText(getActivity(),e.getMessage(),TastyToast.LENGTH_SHORT,
                                                               TastyToast.ERROR).show();
                                  }

                                  @Override
                                  public void onComplete() {

                                  }
                              });

        return view;
    }

    private void getCities(String state){

           Retrofit retrofit = RetrofitClient.getInstance();
           ApiService apiService  = retrofit.create(ApiService.class);

           apiService.allCity(state).subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(new Observer<CityModel>() {
                                     @Override
                                     public void onSubscribe(Disposable d) {
                                     }

                                     @Override
                                     public void onNext(CityModel cityModel) {

                                         city.setVisibility(View.INVISIBLE);
                                        cityList = cityModel.getData();

                                        citySpinnerList.clear();

                                        if(cityList != null){

                                            if(cityList.size() > 0){

                                                citySpinnerList.add("Select city");

                                                drRecycler.setVisibility(View.VISIBLE);

                                                for(int i=0;i<cityList.size();i++){
                                                    citySpinnerList.add(cityList.get(i).getName());
                                                }
                                                cityAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,citySpinnerList);
                                                spinnerCity.setAdapter(cityAdapter);
                                            }
                                        }

                                        else{

                                             drRecycler.setVisibility(View.GONE);
                                             Toast.makeText(getActivity(),"City not available",Toast.LENGTH_SHORT).show();
                                         }
                                     }

                                     @Override
                                     public void onError(Throwable e) {

                                     }

                                     @Override
                                     public void onComplete() {
                                     }
                                 });

    }


  /*  private void getHospitals(String id){

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService  = retrofit.create(ApiService.class);

        apiService.allHospital(id).subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new Observer<HospitalModel>() {
                                      @Override
                                      public void onSubscribe(Disposable d) {
                                      }

                                      @Override
                                      public void onNext(HospitalModel hospitalModel) {

                                          hosList = hospitalModel.getData();

                                          hosSpinnerList.clear();

                                          if(hosList != null){

                                              if(hosList.size() > 0){

                                                  hosSpinnerList.add("Select hospital");

                                                  for(int i=0;i<hosList.size();i++){
                                                      hosSpinnerList.add(hosList.get(i).getName());
                                                  }

                                                  hosAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,
                                                                              hosSpinnerList);

                                                  spinnerHospital.setAdapter(hosAdapter);
                                              }
                                          }
                                          else{
                                                  hosSpinnerList.clear();
                                                  Toast.makeText(getActivity(),"No hospital available",Toast.LENGTH_SHORT).show();
                                          }
                                      }

                                      @Override
                                      public void onError(Throwable e) {
                                      }

                                      @Override
                                      public void onComplete() {
                                      }
                                  });
    }  */

    private void fetchSpeciality(String id){

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService  = retrofit.create(ApiService.class);

        apiService.getSpeciality(id).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<SpecialityModel>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                        }

                                        @Override
                                        public void onNext(SpecialityModel specialityModel) {

                                            special.setVisibility(View.INVISIBLE);
                                            specialityList = specialityModel.getData();

                                            speSpinnerList.clear();

                                            if(specialityList != null){
                                                speSpinnerList.add("Select speciality");

                                                for(int i=0;i<specialityList.size();i++){
                                                    speSpinnerList.add(specialityList.get(i).getName());
                                                }
                                             speAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,
                                                          speSpinnerList);

                                                spinnerSpeciality.setAdapter(speAdapter);
                                            }
                                            else{

                                                drRecycler.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(),"Service not available",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                    });
    }

    private void fetchDoctors(String cid,String id){

        prg.setVisibility(View.VISIBLE);
        drRecycler.setVisibility(View.INVISIBLE);

        drRecycler.setHasFixedSize(true);
        drRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService  = retrofit.create(ApiService.class);

        apiService.getDoctors(id,cid).subscribeOn(Schedulers.io())
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(new Observer<DoctorModel>() {
                                         @Override
                                         public void onSubscribe(Disposable d) {

                                         }

                                         @Override
                                         public void onNext(DoctorModel doctorModel) {

                                             drList = doctorModel.getData();

                                             if(drList != null){
                                                 prg.setVisibility(View.INVISIBLE);
                                                 doctorAdapter = new DoctorAdapter(getActivity(),drList);
                                                 drRecycler.setAdapter(doctorAdapter);

                                                 drRecycler.setVisibility(View.VISIBLE);

                                             }else{

                                                 drRecycler.setVisibility(View.GONE);
                                                 prg.setVisibility(View.INVISIBLE);
                                                 docImage.setVisibility(View.VISIBLE);
                                                 docText.setVisibility(View.VISIBLE);
                                                 Toast.makeText(getActivity(),"no doctor",Toast.LENGTH_SHORT).show();
                                             }
                                         }

                                         @Override
                                         public void onError(Throwable e) {

                                             prg.setVisibility(View.INVISIBLE);
                                             Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                         }

                                         @Override
                                         public void onComplete() {

                                         }
                                     });
    }
}