package Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Consultations {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("doctorid")
    @Expose
    private String doctorid;
    @SerializedName("paymentid")
    @Expose
    private String paymentid;
    @SerializedName("payment")
    @Expose
    private String payment;
    @SerializedName("typeval")
    @Expose
    private String typeval;
    @SerializedName("appointmentdate")
    @Expose
    private Object appointmentdate;
    @SerializedName("appointmenttime")
    @Expose
    private Object appointmenttime;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("doctorname")
    @Expose
    private String doctorname;
    @SerializedName("contactno")
    @Expose
    private String contactno;
    @SerializedName("zoomid")
    @Expose
    private String zoomid;

    public Consultations(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }

    public String getPaymentid() {
        return paymentid;
    }

    public void setPaymentid(String paymentid) {
        this.paymentid = paymentid;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getTypeval() {
        return typeval;
    }

    public void setTypeval(String typeval) {
        this.typeval = typeval;
    }

    public Object getAppointmentdate() {
        return appointmentdate;
    }

    public void setAppointmentdate(Object appointmentdate) {
        this.appointmentdate = appointmentdate;
    }

    public Object getAppointmenttime() {
        return appointmenttime;
    }

    public void setAppointmenttime(Object appointmenttime) {
        this.appointmenttime = appointmenttime;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDoctorname() {
        return doctorname;
    }

    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getZoomid() {
        return zoomid;
    }

    public void setZoomid(String zoomid) {
        this.zoomid = zoomid;
    }
}
