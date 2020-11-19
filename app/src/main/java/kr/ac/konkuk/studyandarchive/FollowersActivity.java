package kr.ac.konkuk.studyandarchive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.ac.konkuk.studyandarchive.adapters.AdapterUsers;
import kr.ac.konkuk.studyandarchive.models.ModelPost;
import kr.ac.konkuk.studyandarchive.models.ModelUser;

public class FollowersActivity extends AppCompatActivity {

    /*
    * 좋아요, 팔로워, 팔로잉 리스트 보여주는 거 3가지 같은 방식이라 하나 재사용함.
    * */


    ActionBar actionBar;
    String id, title;

    List<String> idList;

    RecyclerView recyclerView;
    AdapterUsers userAdapter;
    List<ModelUser> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        //액션바에서 뒤로가기 버튼 활성화
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //리사이클러뷰 + 유저어댑터
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new AdapterUsers(this, userList,false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        switch (title){
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
        }


    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    for (String id : idList){
                        if (user.getUid().equals(id)){
                            userList.add(user);
                        }

                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //액션바에서 뒤로가기 버튼 활성화
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // 이전 엑티비티로 돌아가기
        return super.onSupportNavigateUp();
    }

    /*inflate option menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}