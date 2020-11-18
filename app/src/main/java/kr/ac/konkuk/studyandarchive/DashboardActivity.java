package kr.ac.konkuk.studyandarchive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth firebaseAuth;

    //views
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

//        String username = firebaseAuth.getCurrentUser().getDisplayName();


        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");


        //bottom naviation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //home fragment transaction (default, on start)
        actionBar.setTitle("Home"); // actionbar 타이틀 바꾸기
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, fragment1, "");
        ft1.commit();



    }

    // nav bar listener
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            //home fragment transaction
                            actionBar.setTitle("Home"); // actionbar 타이틀 바꾸기
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.container, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            //profile fragment transaction
                            actionBar.setTitle("Profile"); // actionbar 타이틀 바꾸기
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.container, fragment2, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_users:
                            //users fragment transaction
                            actionBar.setTitle("Users"); // actionbar 타이틀 바꾸기
                            UsersFragment fragment3 = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.container, fragment3, "");
                            ft3.commit();
                            return true;
                        case R.id.nav_add:
                            //users fragment transaction
                            actionBar.setTitle("Study"); // actionbar 타이틀 바꾸기
                            RecordFragment fragment4 = new RecordFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.container, fragment4, "");
                            ft4.commit();
                            return true;
                    }

                    return false;
                }
            };


    private  void checkUserStatus(){

        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user !=null){
            //user is signed in stay here
            // 만약 로그인 되어있다면 홈보여주기


        }else{
            //user not signed in, go to main activity
            // 로그인이 안되있다면, 메인으로이동해서 로그인. 회원가입 둘중하게하도록
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onStart() {
        //check on start of app 시작시
        checkUserStatus(); //사용자 로그인 상태 체크
        super.onStart();
    }


    /*inflate option menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /*Handle menu item clicks*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //get item id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus(); //로그인안되면 메인감감
       }
        return super.onOptionsItemSelected(item);
    }


}






