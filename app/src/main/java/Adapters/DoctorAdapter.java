package Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doctair.BookDoctor;
import com.app.doctair.Payment;
import com.app.doctair.R;
import com.bumptech.glide.Glide;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sdsmdg.tastytoast.TastyToast;


import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import Pojo.Doctor;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> implements PaymentResultWithDataListener {

    Context context;
    List<Doctor> drList;

    public DoctorAdapter(Context context, List<Doctor> drList) {
        this.context = context;
        this.drList = drList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.doctor_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Doctor model = drList.get(position);

        Glide.with(context).load(model.getImage()).placeholder(R.drawable.user).into(holder.drImg);

        holder.drName.setText(model.getName());
        holder.drDesignation.setText(model.getDesignation());
        holder.drExp.setText(model.getExperience());
        holder.drQuali.setText(model.getQualification());

        holder.vidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //    startPayment();

                Intent i = new Intent(context, Payment.class);
                i.putExtra("title","video");
                i.putExtra("name",model.getName());
                i.putExtra("desig",model.getDesignation());
                i.putExtra("drid",model.getId());
                i.putExtra("phone",model.getContactno());
                i.putExtra("jiomeet",model.getJiomeet());
                context.startActivity(i);
              //  ((Activity)context).finish();
            }
        });

        holder.appointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  Intent i = new Intent(context, Payment.class);
                i.putExtra("title","appoint");
                i.putExtra("name",model.getName());
                i.putExtra("desig",model.getDesignation());
                context.startActivity(i);
                ((Activity)context).finish();  */

                Intent i = new Intent(context, BookDoctor.class);
                i.putExtra("drid",model.getId());
                context.startActivity(i);
              //  ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return drList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView drName,drDesignation,drExp,drQuali;
        CircleImageView drImg;
        Button vidButton,appointButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vidButton = itemView.findViewById(R.id.vidButton);
            appointButton = itemView.findViewById(R.id.appointButton);
            drImg = itemView.findViewById(R.id.drImg);
            drName = itemView.findViewById(R.id.drName);
            drDesignation = itemView.findViewById(R.id.drDesignation);
            drExp = itemView.findViewById(R.id.drExp);
            drQuali = itemView.findViewById(R.id.drQuali);
        }
    }

    public void startPayment(){

        Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Doctair.in");
            options.put("description", "Service charge");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9602203875");

            options.put("prefill", preFill);

            co.open((Activity) context, options);
        } catch (Exception e) {
            TastyToast.makeText(context, "Error in payment: " + e.getMessage(), TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            TastyToast.makeText(context, "Payment Successful: " + paymentData.getPaymentId(), TastyToast.LENGTH_LONG,
                                 TastyToast.SUCCESS).show();
        } catch (Exception e) {
            TastyToast.makeText(context,"Some error: " +e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.INFO).show();
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        TastyToast.makeText(context,"Payment error: " + i,TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
    }
}
