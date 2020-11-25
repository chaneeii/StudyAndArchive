package kr.ac.konkuk.studyandarchive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //views
    EditText mEmailEt, mPasswordEdit, mUsernameEdit;
    Button mRegisterBtn;
    TextView mHaveAccountTv;

    // progressbar to display while registering user
    ProgressDialog progressDialog;

    // FirebaseAuth 인스턴스 선언
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Actionbar and its title
        // 액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //enable back button
        // 뒤로가기 버튼 활성화
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEdit = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
        mUsernameEdit = findViewById(R.id.usernameEt);

        // Initialize Firebase Auth
        // 파이어베이스 auth 초기화
        mAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("회원가입 처리 중입니다.");

        //handle register btn click
        // 회원가입 버튼 누르면
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEdit.getText().toString().trim();
                String username = mUsernameEdit.getText().toString().trim();
                //validate
                //유효성검사
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focuss to email edit  text
                    // 유효한 이메일 주소인지 확인
                    mEmailEt.setError("유효하지 않은 이메일 주소입니다.");
                    mEmailEt.setFocusable(true);
                }
                else if(password.length()<6){
                    //set error and focuss to password et
                    // 비밀번호 글자수 확인
                    mPasswordEdit.setError("비밀번호는 최소 6글자 이상이여야 합니다.");
                    mPasswordEdit.setFocusable(true);
                }
                else {
                    registerUser(email,password,username); //회원가입
                }

            }
        });

        //handle login texview click listener
        // 로그인할지
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });




    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        // 사용자가 이미 로그인되어있다면,
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    //회원가입
    private void registerUser(String email, String password, final String username) {
        // 이메일과 비밀번호가 유효한다면, progress dialog를 보여주고 사용자 등록을 시작한다.
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dissmiss dialog and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            // (after firebase realtiem db & storage is set), Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //when user is registered store user info in firebase realtime database too
                            // using HASH MAP
                            // hash맵을 이용하여 회원가입 정보 firebase 저장소에 업로드
                            HashMap<Object, String> hashMap = new HashMap<>();
                            // put info in hashmap 해시맵을 사용해서 사용자 정보 담기
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name",username); // 나중에 추가할 예쩡
                            hashMap.put("phone","");
                            hashMap.put("image","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRQEdoqnWbsHEyqwdFv4iUu5Ug5XpFZWFL5g&usqp=CAU");
                            hashMap.put("bio","");
                            hashMap.put("field","설정안됨");
                            hashMap.put("cover",""); //커버사진
                            //firebase database instance
                            // 파이어베이스 인스턴스 가져오기
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            // DB Users 를 경로로 설정하고
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            // hashmap집어넣기
                            reference.child(uid).setValue(hashMap);





                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다.\n"+user.getEmail(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            // 회원가입 실패시
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress dialog and get and show the error message
                // 에러발생시
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed(); //go previous activity

        return super.onSupportNavigateUp();
    }
}