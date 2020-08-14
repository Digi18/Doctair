package com.app.doctair;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Thanks extends AppCompatActivity {

    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.hide();

       done = findViewById(R.id.done);

       done.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent i = new Intent(Thanks.this,MyAppointments.class);
               startActivity(i);
               finish();
           }
       });
    }
}