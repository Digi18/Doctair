package com.app.doctair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sdsmdg.tastytoast.TastyToast;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Pojo.LoginResponse;
import Pojo.LoginResponseModel;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Retrofit;

public class OnBoarding extends AppCompatActivity {

    OnboardAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    List<LoginResponse> loginResponseList  = new ArrayList<>();

    GoogleSignInButton signIn;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth fAuth;
    FirebaseUser user;
    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        fAuth = FirebaseAuth.getInstance();

        prg = new ProgressDialog(OnBoarding.this);
        prg.setMessage("Signing in...");
        prg.setCancelable(false);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        signIn = findViewById(R.id.signIn);

        adapter = new OnboardAdapter(this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{

                prg.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }catch(ApiException e){

                prg.hide();
                TastyToast.makeText(getApplicationContext(),"Unable to Login",TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);

        fAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    saveData();
                }
                else{

                    prg.hide();
                    TastyToast.makeText(getApplicationContext(),"Unable to login with google.",TastyToast.LENGTH_SHORT,
                            TastyToast.ERROR).show();

                }
            }
        });
    }

    private void saveData(){

        user = fAuth.getCurrentUser();

        Retrofit retrofit = RetrofitClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

     //   JSONObject jsonObject = new JSONObject();
     //   jsonObject.addProperty("username",user.getDisplayName());

        apiService.saveUserData(user.getDisplayName(),user.getEmail()).subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(new Observer<LoginResponseModel>() {
                                     @Override
                                     public void onSubscribe(Disposable d) {

                                     }

                                     @Override
                                     public void onNext(LoginResponseModel loginResponseModel) {

                                         loginResponseList = loginResponseModel.getData();

                                         String mail = loginResponseList.get(0).getEmail();
                                         String id = loginResponseList.get(0).getId();

                                         SharedPreferences sharedPreferences = getSharedPreferences("Creds", Context.MODE_PRIVATE);
                                         SharedPreferences.Editor editor = sharedPreferences.edit();
                                         editor.putString("userid",id);
                                         editor.commit();

                                         Intent i = new Intent(OnBoarding.this,MainActivity.class);
                                         startActivity(i);
                                         finish();
                                        // Toast.makeText(getApplicationContext(),id + "" + mail,Toast.LENGTH_SHORT).show();
                                     }

                                     @Override
                                     public void onError(Throwable e) {

                                         Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                     }

                                     @Override
                                     public void onComplete() {

                                     }
                                 });

     }
}