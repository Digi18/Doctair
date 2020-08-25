package com.app.doctair;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdsmdg.tastytoast.TastyToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseUser user;

    TextView profileName;
    Button logOut;

    CircleImageView circleImage;

    private GoogleSignInClient mGoogleSignInClient;

    LinearLayout appointmentCard,supportCard;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        circleImage = view.findViewById(R.id.circleImage);
        profileName = view.findViewById(R.id.profileName);
        logOut = view.findViewById(R.id.logOut);

       // appointmentCard = view.findViewById(R.id.appointmentCard);
        supportCard = view.findViewById(R.id.supportCard);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);

        if(user != null){

            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();

            profileName.setText(name);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.user);

            Glide.with(getContext()).load(photoUrl).apply(requestOptions).into(circleImage);
        }

      /*  appointmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),MyAppointments.class);
                startActivity(i);
            //    getActivity().finish();
            }
        }); */

        supportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"doctair31@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent,"Send email"));

                }catch(ActivityNotFoundException e){

                    TastyToast.makeText(getActivity(),e.getMessage(),TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fAuth != null){

                    mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            fAuth.signOut();

                            SharedPreferences preferences = getActivity().getSharedPreferences("Creds", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();

                            Intent exit  = new Intent(getActivity(), OnBoarding.class);
                            startActivity(exit);
                            getActivity().finish();

                        }
                    });
                }
            }
        });

        return view;
    }
}