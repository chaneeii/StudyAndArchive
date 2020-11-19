package kr.ac.konkuk.studyandarchive.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.StartActivity;
import kr.ac.konkuk.studyandarchive.adapters.AdapterPosts;
import kr.ac.konkuk.studyandarchive.models.ModelPost;

import static android.content.ContentValues.TAG;


public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;

    private List<String> followingList;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init
        firebaseAuth =FirebaseAuth.getInstance();

        //recycler view 이랑 속성들
        recyclerView = view.findViewById(R.id.postRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //최신 포스트 먼저 보여주기
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        //init 포스트리스트 초기화
        postList = new ArrayList<>();

        checkFollowing();


        return  view;
    }

    private void checkFollowing(){

        followingList =  new ArrayList<>();

        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                loadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadPosts() {
        // path of 모든 포스트
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //ref애서 데이터 가져오기
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post =snapshot.getValue(ModelPost.class);
                    for(String id : followingList){
                        if(post.getUid().equals(id)){
                            postList.add(post);
                            Log.d(TAG, "onDataChange: "+post);
                            
                            //어댑터
                            adapterPosts = new AdapterPosts(getContext(), postList);
                            //리사이클러뷰
                            recyclerView.setAdapter(adapterPosts);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //에러난 경우
//                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPosts_all() {
        // path of 모든 포스트
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //ref애서 데이터 가져오기
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post =snapshot.getValue(ModelPost.class);

                            postList.add(post);
                            Log.d(TAG, "onDataChange: "+post);

                            //어댑터
                            adapterPosts = new AdapterPosts(getContext(), postList);
                            //리사이클러뷰
                            recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //에러난 경우
//                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private  void searchPosts(final String searchQuery){
        // path of 모든 포스트
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //ref애서 데이터 가져오기
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post =snapshot.getValue(ModelPost.class);

                    if(!post.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            post.getpDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(post);
                    }

                    postList.add(post);
                    Log.d(TAG, "onDataChange: "+post);

                    //어댑터
                    adapterPosts = new AdapterPosts(getContext(), postList);
                    //리사이클러뷰
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //에러난 경우
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



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
            startActivity(new Intent(getActivity(), StartActivity.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu option in framgent
        super.onCreate(savedInstanceState);
    }

    /*inflate option menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //search view title이랑 description으로 검색가능
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //사용자가 search buttton 누르면 호출됨
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                }
                else {
                    checkFollowing();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //사용자가 아무글자나 누르면 호출됨
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                }
                else {
                    checkFollowing();
                }
                return false;
            }
        });



        super.onCreateOptionsMenu(menu, inflater);

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