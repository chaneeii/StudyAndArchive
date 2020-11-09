package kr.ac.konkuk.studyandarchive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    //firebase auth

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");


        //init
        firebaseAuth = FirebaseAuth.getInstance();



    }


    private  void checkUserStatus(){

        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user !=null){
            //user is signed in stay here
            // 만약 로그인 되어있다면 홈보여주기

        }else{
            //user not signed in, go to main activity
            // 로그인이 안되있다면, 메인으로이동해서 로그인. 회원가입 둘중하게하도록
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        //check on start of app 시작시
        checkUserStatus(); //사용자 로그인 상태 체크
        super.onStart();
    }

}






