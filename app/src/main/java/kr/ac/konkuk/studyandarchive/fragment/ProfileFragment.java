package kr.ac.konkuk.studyandarchive.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kr.ac.konkuk.studyandarchive.FollowersActivity;
import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.StartActivity;
import kr.ac.konkuk.studyandarchive.adapters.AdapterThumbs;
import kr.ac.konkuk.studyandarchive.models.ModelPost;
import kr.ac.konkuk.studyandarchive.models.ModelUser;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    // storage
    StorageReference storageReference;
    //path where imgages of user profile and cover will be stored
    // 프로필 사진 및 커버사진이 저장될 path
    String storagePath = "User_Profile_Cover_Imgs/";

    //views from xml
    ImageView avatarIv, coverIv;
    TextView nameTV, emailTv, fieldTv, posts, followers, following;
    FloatingActionButton fab;
    Button archiveBtn, studyBtn, followBtn;

    //progress dialog
    ProgressDialog pd;

    // permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    //for checking profile or cover photo
    String profileOrCoverPhoto;

    //프로필 아이디
    String profileid, hisUid;

    //리사이클러뷰 (등록한 게시글 아이템 썸네일 출력용 변수)
    RecyclerView recyclerView;
    AdapterThumbs adapterThumbs;
    List<ModelPost> postList;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference(); //firebase strage reference

        //현재 사용자의 프로필인지 다른 사용자의 프로필인지 알기위함
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid","none");


        // init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //init views
        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTV = view.findViewById(R.id.nameTv);
//        emailTv = view.findViewById(R.id.emailTv);
        fieldTv = view.findViewById(R.id.fieldTv);
        fab = view.findViewById(R.id.fab);
        posts = view.findViewById(R.id.post_num);
        followers = view.findViewById(R.id.follower_num);
        following = view.findViewById(R.id.following_num);
        archiveBtn = view.findViewById(R.id.my_archives);
        studyBtn = view.findViewById(R.id.my_study);
        followBtn = view.findViewById(R.id.followBtn);


        //init progress dialog
        pd = new ProgressDialog(getActivity());

        //리사이클러뷰
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        adapterThumbs = new AdapterThumbs(getContext(),postList);
        recyclerView.setAdapter(adapterThumbs);

        Log.d(TAG, "누가주인? "+profileid);



        /*
        * 현재 로그인된 유저의 정보를 email 을 통해 가져온다. (uid도 가능)
        * orderByChild 쿼리를 사용해서, 현재 로그인된 사용자의 email(key로 사용) 과 동일한 이메일을 가진 node의 디테일을 보여줌
        * 이것은 모든 노드를 돌며 key에 맞는 노드 찾음.
        * */
//        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                //check until required data get
//                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                    //get data
//                    String name = ""+ds.child("name").getValue();
//                    String email = ""+ds.child("email").getValue();
//                    String field = ""+ds.child("field").getValue();
//                    String image = ""+ds.child("image").getValue();
//                    String cover = ""+ds.child("cover").getValue();
//
//                    //set data
//                    nameTV.setText(name);
////                    emailTv.setText(email);
//                    fieldTv.setText(field);
//                    try{
//                        //if image is received then set
//                        Picasso.get().load(image).into(avatarIv);
//                    }catch(Exception e){
//                        //if there is any exception while getting image then set default
//                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
//                    }
//                    try{
//                        //if image is received then set
//                        Picasso.get().load(cover).into(coverIv);
//                    }catch(Exception e){
//                        //if there is any exception while getting image then set default
////                        Picasso.get().load(R.drawable.ic_default_img_white).into(coverIv);
//                    }
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        userInfo(); // sharedpreference로 받은 profile id 에 맞는 정보 출력
        getFollowers(); //팔로워팔로잉텍스트뷰 세팅
//        getNrPosts(); //전체 포스트 개수 세팅
        myThumbs();


        if(profileid.equals(user.getUid())){
            followBtn.setVisibility(View.GONE);
        }else{
            checkFollow();
            fab.setVisibility(View.GONE);
        }


        //팔로우 버튼
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = followBtn.getText().toString();

                if(btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(user.getUid()).setValue(true);
                }else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(user.getUid()).removeValue();
                }

            }
        });


        //fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","followers");
                startActivity(intent);
                addNotifications();
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","following");
                startActivity(intent);
                addNotifications();
            }
        });


        return view;
    }


    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    button.setText("FOLLOWING");
                }else{
                    button.setText("+ FOLLOW");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //알림
    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", user.getUid() );
        hashMap.put("text", "님이 회원님을 팔로우합니다.");
        hashMap.put("postid","");
        hashMap.put("ispost",false);

        reference.push().setValue(hashMap);
    }



    //shared reference로 받은 profileid에 해당하는 사용자 정보 세팅
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null){
                    return;
                }

                ModelUser user = dataSnapshot.getValue(ModelUser.class);


                    //get data
                    String name = user.getName();
                    String field = user.getField();
                    String image = user.getImage();
                    String cover = user.getCover();

                    //set data
                    nameTV.setText(name);
//                    emailTv.setText(email);
                    fieldTv.setText(field);
                    try{
                        //if image is received then set
                        Picasso.get().load(image).into(avatarIv);
                    }catch(Exception e){
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                    try{
                        //if image is received then set
                        Picasso.get().load(cover).into(coverIv);
                    }catch(Exception e){
                        //if there is any exception while getting image then set default
//                        Picasso.get().load(R.drawable.ic_default_img_white).into(coverIv);
                    }


                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    followBtn.setText("following");
                }else{
                    followBtn.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){

        Log.d(TAG, "getFollowers: "+profileid);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void getNrPosts(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int i = 0;
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    ModelPost post = snapshot.getValue(ModelPost.class);
////                    String currentUid = post.getpUid();
////                    hisUid = ""+ dataSnapshot.child("uid").getValue();
//                    if(post.getUid().equals(profileid)){
//                        i++;
//                    }
//                }
//
//                posts.setText(""+i);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void myThumbs(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                int i = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren() ){
                    ModelPost post = snapshot.getValue(ModelPost.class);
//                    String hisUid = ""+ dataSnapshot.child("uid").getValue();
                    if(post.getUid().equals(profileid)){
                        postList.add(post);
                        i++;
                    }
                }
                Collections.reverse(postList);
                adapterThumbs.notifyDataSetChanged();
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    // STORAGE PERMISSION
    private boolean checkStoragePermission(){
        /*
        * storage 권한 체크
        * return true : 가능
        * return false : 불가능
        * */
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    // CAMERA PERMISSION
    private boolean checkCameraPermission(){
        /*
         * storage 권한 체크
         * return true : 가능
         * return false : 불가능
         * */
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }
    private void requestCameraPermission(){
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    // 프로필 정보 수정시 *** 옵션
    private void showEditProfileDialog() {
        /* Show Dialog containing option
        * 1) 프로필 수정
        * 2) 커버 사진 수정
        * 3) 이름 수정
        * 4) field 수정
        * */

        // options to show in dialog
        String options[] = {"프로필 사진 업데이트", "커버 사진 업데이트", "이름 업데이트", "학습분야 업데이트"};
        //alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set Title
        builder.setTitle("업데이트할 항목을 고르세요");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    // 프로필 사진 수정
                    pd.setMessage("프로필 사진 업데이트");
                    profileOrCoverPhoto = "image"; // 프로필 사진을 수정하겟다.
                    showImagePicDialog();

                }else if(which ==1){
                    // 커버 사진 수정
                    pd.setMessage("커버 사진 업데이트"); // 커버사진을 바꿀때, value를 적절히 할당
                    profileOrCoverPhoto ="cover";
                    showImagePicDialog();

                }
                else if(which ==2){
                    // 이름 수정
                    pd.setMessage("이름 업데이트");
                    // method를 호출할때 "name"을 key로 넘긴다. for database에 해당 value를 업데이트 하기 위해서
                    showTextUpdateDialog("name");

                }
                else if(which ==3){
                    // 스터디 필드 수정
                    pd.setMessage("학습분야 업데이트");
//                    showFieldUpdateDialog(); 드랍다운스타일
                    showFieldOfStudyUpdateDialog();

                }else{

                }
            }
        });
        //create and show dialog
        builder.create().show();

    }

    //리스트스타일
    private void showFieldOfStudyUpdateDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        final String[] fieldList = getResources().getStringArray(R.array.fieldList);

        final int[] selectedIndex = {0};

        dialog.setTitle("학습분야를 선택하세요")
                .setSingleChoiceItems(fieldList, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedIndex[0] =which;
                            }
                        })
                .setPositiveButton("선택완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // edit text로 부터 text input하기
                        String value = fieldList[selectedIndex[0]];
                        // 사용자가 인풋을 입ㄺ햇는지 아닌지 체크
                        if(! value.isEmpty()){
                            pd.show();
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("field", value);

                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 업데이트 됨
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "학습분야가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 업데이트 실패
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                        else{
                            Toast.makeText(getActivity(), "입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();



    }

    // 드랍다운스타일
    private void showFieldUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_study_field, null);
        builder.setTitle("What are you studying?");
        final Spinner mSpinner =(Spinner) mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.fieldList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        builder.setView(mView);

        builder.setPositiveButton("선택완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if(! mSpinner.getSelectedItem().toString().equalsIgnoreCase("학습분야 선택")){
//                    Toast.makeText(getActivity(), "학습분야: "+mSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                }


                // edit text로 부터 text input하기
                String value = mSpinner.getSelectedItem().toString();
                // 사용자가 인풋을 입ㄺ햇는지 아닌지 체크
                if(! mSpinner.getSelectedItem().toString().equalsIgnoreCase("학습분야 선택")){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("field", value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 업데이트 됨
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "학습분야가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                }
                else{
                    Toast.makeText(getActivity(), "입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();



    }




    private void showTextUpdateDialog(final String key) {
        /*
        * key는 db의 어느 필드를 바꿀지 해당 키를 의미함. ex. key 가 name이면 name필드의 값 변경
        * */

        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle("Update" + key);


        // layout 설정
//        LinearLayout linearLayout = new LinearLayout(getActivity());
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setPadding(10,10,10,10);

        //edit text 추가
        final EditText editText = new EditText(getActivity());
        editText.setHint("사용하시려는 "+key+"을 입력하세요");

        builder.setView(editText);

        //add buttons in Dialog
        builder.setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // edit text로 부터 text input하기
                final String value = editText.getText().toString().trim();
                // 사용자가 인풋을 입ㄺ햇는지 아닌지 체크
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 업데이트 됨
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                    //만약 사용자가 이름을 바꾸면, 이것은 과거 포스트에도 변경된 이름을 적용할수있도록 함
                    if(key.equals("name")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query = ref.orderByChild("uid").equalTo(user.getUid());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    String child = ds.getKey();
                                    snapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    //만약 사용자가 이름을 바꾸면, 이것은 과거 댓글에도 변경된 이름을 적용할수있도록 함
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String child = ds.getKey();
                                if (snapshot.child(child).hasChild("Comments")){
                                    String child1 = ""+snapshot.child(child).getKey();
                                    Query child2 = FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(user.getUid());
                                    child2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                String child = ds.getKey();
                                                snapshot.getRef().child(child).child("uName").setValue(value);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }
                else{
                    Toast.makeText(getActivity(), "입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // create and show dialog
        builder.create().show();
   }


    private void showImagePicDialog() {
        // 카메라와 갤러리 선택이 가능한 다이얼로그 show
        // 카메라 : 카메라 & storage 퍼미션필요
        // 갤러리 : storage 퍼미션 필요

        // options to show in dialog
        String options[] = {"카메라", "갤러리"};
        //alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set Title
        builder.setTitle("이미지를 어디서 가져올까요?");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    // 카메라로 촬영

                    if(! checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }

                }else if(which ==1){
                    // 갤러리에서 선택

                    if(! checkStoragePermission()){
                        requestStoragePermission();;
                    }else{
                        pickFromGallery();
                    }

                }
            }

        });
        //create and show dialog
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /* 이 메소드는 사용자가 퍼비션 리퀘스트 다이얼로그에 허락하거나 거부하면 호출되어 각 경우를 핸들링함.
        * */

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                // 카메라 사용시, 카메라 및 스토리지 권한 체크 먼저 한다.
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        // permission 이 승인되면
                        pickFromCamera();
                    }else{
                        // permission이 거부되면
                        Toast.makeText(getActivity(), "카메라 및 저장공간 사용 권한을 승인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                // 갤러리 사용시, 스토리지 권한 체크 먼저 한다.
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        // permission 이 승인되면
                        pickFromGallery();
                    }else{
                        // permission이 거부되면
                        Toast.makeText(getActivity(), "저장공간의 사용 권한을 승인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카메라나 갤러리에서 이미지 선택후 호출됨
        if (resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                // 이미지가 갤러리에서 선택된 경우, 이미지의 uri get 한다.
                image_uri = data.getData();

                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                // 이미지가 카메라에서 선택된 경우, 이미지의 uri get 한다.

                uploadProfileCoverPhoto(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri image_uri) {

        //show progress
        pd.show();


        /* 프로필 사진과 커버사진을 위해 각자 함수 만드는 대신 , 한개를 같이 사용하겟다.
        * to add check ill, string 변수를 더하고
        * 사용자가 "프로필 사진 변경" 클릭시"image"라는 value가지고
        * " 커버 사진 변경" 클릭시 "cover" 라는 value 가짐.
        *
        * 여기서, image - > 각 사용자의 프로필 사진 url을 포함하는 key
        * cover - >  커버사진 key
        * */

        //firebase storage 내부에서 이미지가 저장될 path 와 이름
        // e.g Users_Profile_Cover_Imgs/image_e123ssdnadiaskla.jpg
        // e.g Users_Profile_Cover_Imgs/cover_e123ssdnadiaskla.jpg
        String filePathAndName = storagePath +""+profileOrCoverPhoto +"_"+user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 이미지가 storage에 업로드되면, 이것의 url 을 get하고 user database에 저장한다.
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //이미지가 잘 업로드 되었는지 url이 잘 받아졋는지 확인
                        if(uriTask.isSuccessful()){
                            // image uploaded
                            // add/update url in 사용자 데이터 베이스
                            HashMap<String, Object> results = new HashMap<>();
                            /*
                            * 첫번째 파라미터 profileOrCoverPhoto 는 "image" 또는 "cover" 라는 값 가지고 있어 이것으로 프로필 사진인지 커버사진인지 저장 뭐할지 아는 key이다.
                            *  두번째 파라미더는 firebase storage에 저장된 image의 url을 가지고 있다. 이 url은 "image" 인지 "cover"인지에 따라 구분되 저장된다.
                           * */
                            results.put(profileOrCoverPhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 사용자 디비안의 url이 성공적으로 추가됨
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "성공적으로 이미지를 업데이트 했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 사용자 디비안의 url 추가 실패
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "이미지 업데이트 도중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }else{
                            // 에러 발생
                            pd.dismiss();
                            Toast.makeText(getActivity(), "이미지 업로드 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 에러 발생시, 에러 메세지를 가져와 보여주고 progress dialog를 지운다.
                         pd.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        
                    }
                });



    }

    private void pickFromCamera() {
        // Intent of Picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        // put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
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
        menu.findItem(R.id.action_search).setVisible(false);
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