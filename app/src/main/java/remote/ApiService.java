package remote;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import Pojo.AppointmentsModel;
import Pojo.BookingResponse;
import Pojo.CityModel;
import Pojo.ConsultationModel;
import Pojo.DoctorModel;
import Pojo.OnlineConsultResponse;
import Pojo.PaymentModel;
import Pojo.LoginResponseModel;
import Pojo.PrescriptionDownloadModel;
import Pojo.SpecialityModel;
import Pojo.States;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("get_state")
    Observable<States> allStates();

    @GET("get_city")
    Observable<CityModel> allCity(@Query("state")  String state);

  //  @GET("get_hospital")
  //  Observable<HospitalModel> allHospital(@Query("city") String city);

    @GET("get_speciality")
    Observable<SpecialityModel> getSpeciality(@Query("city") String city);

    @GET("get_doctor")
    Observable<DoctorModel> getDoctors(@Query("city") String city,
                                             @Query("speciality") String speciality);

    @GET("get_payment")
    Observable<PaymentModel> getPayment();

    @POST("signup_login")
    @FormUrlEncoded
    Observable<LoginResponseModel> saveUserData(@Field("username") String username,
                                                @Field("email") String email);

    @POST("online_consultation")
    @FormUrlEncoded
    Observable<OnlineConsultResponse> savePaymentData(@Field("userid") String userid,
                                                      @Field("doctorid") String doctorid,
                                                      @Field("paymentid") String paymentid,
                                                      @Field("payment")  String payment);

    @POST("book_an_appointment")
    @FormUrlEncoded
    Observable<BookingResponse> saveBookingData(@Field("userid") String userid,
                                                @Field("doctorid") String doctorid,
                                                @Field("paymentid") String paymentid,
                                                @Field("payment")  String payment,
                                                @Field("appointmentdate") String appointmentdate,
                                                @Field("appointmenttime") String appointmenttime);

    @GET("get_myappointment")
    Observable<AppointmentsModel> getAppointments(@Query("userid") String userid);

    @GET("get_myconsultant")
    Observable<ConsultationModel> getConsultations(@Query("userid") String userid);

    @GET("get_priscription")
    Observable<PrescriptionDownloadModel> getPrescription(@Query("consultid") String consultid);
}
