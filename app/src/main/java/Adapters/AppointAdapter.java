package Adapters;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doctair.R;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Pojo.Appointments;
import Pojo.Pays;
import Pojo.PresDownload;
import Pojo.PrescriptionDownloadModel;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class AppointAdapter extends RecyclerView.Adapter<AppointAdapter.ViewHolder> {

    Context context;
    List<Appointments> appointmentList;
    List<PresDownload> presList = new ArrayList<>();

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

        final Appointments model = appointmentList.get(position);

        holder.appDate.setText(model.getAppointmentdate());
        holder.appTime.setText(model.getAppointmenttime());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downloadData(model.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView appDate,appTime;
        ImageView download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appDate  = itemView.findViewById(R.id.appDate);
            appTime = itemView.findViewById(R.id.appTime);
            download = itemView.findViewById(R.id.download);
        }
    }

    private void downloadData(String id){

        final ProgressDialog prg = new ProgressDialog(context);
        prg.setTitle("Downloading...");
        prg.setCancelable(false);
        prg.show();

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getPrescription(id).subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(new Observer<PrescriptionDownloadModel>() {
                                          @Override
                                          public void onSubscribe(Disposable d) {

                                          }

                                          @Override
                                          public void onNext(PrescriptionDownloadModel prescriptionDownloadModel) {

                                              prg.dismiss();
                                              presList = prescriptionDownloadModel.getData();

                                              int code = prescriptionDownloadModel.getCode();

                                              if(String.valueOf(code).equals("200")){

                                                  String img = presList.get(0).getImage();


                                               //   Toast.makeText(context,img,Toast.LENGTH_SHORT).show();
                                                /*  try {

                                                      prg.dismiss();
                                                      URL url = new URL("http://doctair.in/public/common_images/" + img);
                                                      Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                      MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,img,"");
                                                  } catch (MalformedURLException e) {
                                                      e.printStackTrace();
                                                  } catch (IOException e) {
                                                      e.printStackTrace();
                                                  }  */
                                                  String downloadUrlOfImage = "http://doctair.in/public/common_images/" + img;

                                                  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                                                          getAbsolutePath() + "/" + "Doctair" + "/";

                                                  File direct = new File(downloadUrlOfImage);

                                                  if(!direct.exists()) direct.mkdir();

                                                  DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                  Uri downloadUri = Uri.parse(downloadUrlOfImage);
                                                  DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                                                  request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                                                  .setTitle("Downloading " + img)
                                                  .setMimeType("image/jpeg")
                                                  .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                  .setDestinationInExternalFilesDir(context, String.valueOf(direct),img);

                                                  dm.enqueue(request);
                                              }
                                              else{
                                                  Toast.makeText(context,"No prescription found",Toast.LENGTH_SHORT).show();
                                              }
                                          }

                                          @Override
                                          public void onError(Throwable e) {

                                              prg.dismiss();
                                              TastyToast.makeText(context,e.getMessage(),TastyToast.LENGTH_LONG,
                                                      TastyToast.ERROR).show();
                                          }

                                          @Override
                                          public void onComplete() {

                                          }
                                      });
    }
}
