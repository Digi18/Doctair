package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doctair.R;

import java.util.List;

import Pojo.Appointments;
import Pojo.Pays;

public class AppointAdapter extends RecyclerView.Adapter<AppointAdapter.ViewHolder> {

    Context context;
    List<Appointments> appointmentList;

    public AppointAdapter(Context context, List<Appointments> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View v = LayoutInflater.from(context).inflate(R.layout.appoint_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointAdapter.ViewHolder holder, int position) {

        Appointments model = appointmentList.get(position);

        holder.appDate.setText(model.getAppointmentdate());
        holder.appTime.setText(model.getAppointmenttime());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView appDate,appTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appDate  = itemView.findViewById(R.id.appDate);
            appTime = itemView.findViewById(R.id.appTime);
        }
    }
}
