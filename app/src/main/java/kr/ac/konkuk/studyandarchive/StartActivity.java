package kr.ac.konkuk.studyandarchive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    //views
    Button mRegisterBtn, mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);

        // handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //start Register activity
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));

            }
        });

        //handle login button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start LoginActivity
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });



    }
}