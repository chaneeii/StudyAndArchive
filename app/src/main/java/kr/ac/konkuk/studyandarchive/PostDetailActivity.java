package kr.ac.konkuk.studyandarchive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kr.ac.konkuk.studyandarchive.adapters.AdapterComments;
import kr.ac.konkuk.studyandarchive.models.ModelComment;

public class PostDetailActivity extends AppCompatActivity {

    // user 와 post의 detial 가져오기 위함
    String myUid, myEmail, myName, myDp, myField,
    postId, pLikes, hisDp, hisName, hisField,hisUid,
    pImage;


    boolean mProcessComment = false;
    boolean mProcessLike = false;

    //progressbar
    ProgressDialog pd;


    //views
    ImageView uPictureIv, pImageIv;
    TextView uNameTv, uFieldTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pStudyTimeTv, pLinkTv, pCommentsTv;
    ImageButton moreBtn;
    Button likeBtn, commentBtn;
    LinearLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComment> commentList;
    AdapterComments adapterComments;


    // add comments views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // actionbar and its properties
        ActionBar actiionBar = getSupportActionBar();
        actiionBar.setTitle("study&archive");
        actiionBar.setDisplayShowHomeEnabled(true);
        actiionBar.setDisplayHomeAsUpEnabled(true);

        //intent를 (postid)이용해서 post id 가져오기
        // 이미지 선택하면 포스트 상세 페이지 postdetail activity
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");



        //init views
        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        uFieldTv = findViewById(R.id.post_field);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pStudyTimeTv = findViewById(R.id.pStudyTimeTv);
        pLinkTv = findViewById(R.id.pLinkTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.more_btn);
        likeBtn = findViewById(R.id.likeBtn);
        commentBtn = findViewById(R.id.commentBtn);
        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);


        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatar);

        loadPostInfo();

        checkUserStatus();

        loadUserInfo();

        setLikes();

        loadComments();



        //send comment 보내기 버튼 클릭
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              postComment();
            }
        });

        //like button click handle
        // 좋아요버튼클릭이벤트리스너
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        //더보기 ... 버튼
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });



    }

    private void loadComments() {
        //layout(Linear) for 리사이클러뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //리사이클러뷰에 레이아웃설정
        recyclerView.setLayoutManager(layoutManager);


        //init commentlist
        commentList = new ArrayList<>();


        // comment를 get하기위한 post 경로
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    commentList.add(modelComment);

                    // myUid와 postId를 comment adpater 생성자의 파라미터로 넘김



                    //setup adapter
                    adapterComments = new AdapterComments(getApplicationContext(), commentList, myUid, postId);
                    //set adapter
                    recyclerView.setAdapter(adapterComments);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void showMoreOptions() {
        //pop 메뉴 생성
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PopupMenu popupMenu =  new PopupMenu(this, moreBtn, Gravity.END);

            //delete/edit 옵션 (현재 로그인된 사용자의 게시글의 경우)
            if(hisUid.equals(myUid)){
                //add items in menu
                popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제하기");
                popupMenu.getMenu().add(Menu.NONE, 1, 2, "수정하기");
            }

            // 각 아이템 선택시
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    int id = menuItem.getItemId();
                    if(id==0){
                        // 삭제하기
                        beginDelete();
                    }
                    else if (id==1){
                        // 수정하기
                        // AddPostActivity를 "editPost"키와 클릭된 포스트이 id로로 시작한다
                        Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                        intent.putExtra("key","editPost");
                        intent.putExtra("editPostId",postId);
                        startActivity(intent);
                    }

                    return false;
                }
            });






        }else{
            Toast.makeText(this, "버전이 낮아 이용 불가능합니다. 안드로이드 키켓이상 지원", Toast.LENGTH_SHORT).show();
        }


    }

    private void beginDelete() {
        deleteWithImage(postId, pImage);
    }

    private void deleteWithImage(final String postId, String pImage) {
        //progres bar
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("삭제하는 중");
        /*
        * Step :
        * 1. image를 url이용해서 삭제
        * 2. post id로 database에서 삭제
        * */

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //이미지가 성공적으로 삭제되면
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    ds.getRef().removeValue(); // pid가 일치하는 파이어베이스 저장소에서 삭제
                                }
                                //삭제되면
                                Toast.makeText(PostDetailActivity.this, "성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //삭제실패하면 더이상 진행못함.
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void setLikes() {
        // 포스트가 로딩될때, 현재 유저가 좋아요 눌럿는지 아닌지 체크함
        final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).hasChild(myUid)){
                    //사용자가 좋아요를 누르면
                    //ui도 적용
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fire_red,0,0,0);

                }else{
                    //좋아요 한번더 눌러서 안좋아하면면
                    //ui도 적용
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fire,0,0,0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void likePost() {
        // post의 전체 좋아요 수를 가져온다.
        // 만약 현재로그인된 사용자가 좋아요 누른적이 없다면 좋아요수를 1증가 아님 1감소
        mProcessLike = true;
        final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessLike){
                    if(snapshot.child(postId).hasChild(myUid)){
                        // 이미 좋아요를 눌었으면, 좋아요 삭제하기
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1)); //개수삭제
                        likeRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;


                    }else {
                        //좋아요를 누르지않았다면, 좋아요표기하기
                        if (pLikes==null){
                            pLikes ="0";
                        }
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1)); //좋아요 추가
                        likeRef.child(postId).child(myUid).setValue("Liked"); // 아무 밸류나 설정해도됨.
                        mProcessLike = false;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("댓글을 추가하는 중");

        // comment edit text에서 댓글 가져오기
        String comment = commentEt.getText().toString().trim();
        //validate
        if(TextUtils.isEmpty(comment)){
            //입력값 없을 때
            Toast.makeText(this, "입력된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        //현재시각
        String timeStamp = String.valueOf(System.currentTimeMillis());

        // 각 포스트는 "comment"라는 child 가짐. 새로운 스키마
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        // hashmap에 넣기
        hashMap.put("cId", timeStamp);
        hashMap.put("comment",comment);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("uid",myUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myDp);
        hashMap.put("uName",myName);
        hashMap.put("uField",myField);

        // db에 해시맵 데이터 넣기
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //추가되면
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "성공적으로 댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //실패시, 추가 안됨
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });



    }


    private void updateCommentCount() {
        //댓글 달리면 증가
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessComment){
                    //댓글 추가되면
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadUserInfo() {
        //get current user info (현재 이용중인 사용자)
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    myName = ""+ds.child("name").getValue();
                    myDp = ""+ds.child("image").getValue();
                    myField = ""+ds.child("field").getValue();

                    //setdata
                    try{
                        // 만약 이미지가 잘 받아와지면
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_default_img_purple).into(cAvatarIv);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_img_purple).into(cAvatarIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadPostInfo() {
        // post id 로  post 가져오기
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 원하는 포스트 나올때까지 검색
                for(DataSnapshot ds : snapshot.getChildren()){
                    //data 겟

                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDescription = ""+ds.child("pDescription").getValue();
                    String pStudyTime = ""+ds.child("pStudyTime").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    String pLink = ""+ds.child("pUrl").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    String commentCount =  ""+ds.child("pComments").getValue();


                    pLikes = ""+ds.child("pLikes").getValue();
                    hisDp = "" + ds.child("uDp").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    hisField = ""+ds.child("uField").getValue();
                    hisUid = ""+ds.child("uid").getValue();

                    //timestamp 변환
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();


                    //study time 변환
                    Long time = Long.parseLong(pStudyTime);
                    int h   = (int)(time /3600000);
                    int m = (int)(time - h*3600000)/60000;
                    int s= (int)(time - h*3600000- m*60000)/1000 ;
                    String pStudyTime_s = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);

                    //set data
                    pTitleTv.setText(pTitle);
                    pStudyTimeTv.setText(pStudyTime_s);
                    pTimeTv.setText(pTime);
                    pDescriptionTv.setText(pDescription);
                    pLinkTv.setText(pLink);
                    pLikesTv.setText(pLikes+" Fires");
                    uNameTv.setText(hisName);
                    uFieldTv.setText(hisField);
                    pCommentsTv.setText(commentCount+" Comments");

                    // 유저 이미지 설정 (포스트주인)
                    //포스트 이미지 설정
                    pImageIv.setVisibility(View.VISIBLE);
                    try{
                        Picasso.get().load(pImage).placeholder(R.drawable.test_image).into(pImageIv);
                    }catch (Exception e){

                    }

                    //유저이미지
                    try{
                        Picasso.get().load(hisDp).placeholder(R.drawable.ic_default_img_purple).into(uPictureIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_img_purple).into(uPictureIv);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void checkUserStatus(){

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user !=null){
            //user is signed in stay here
            // 만약 로그인 되어있다면
            myEmail = user.getEmail();
            myUid = user.getUid();


        }else{
            //user not signed in, go to main activity
            // 로그인이 안되있다면, 메인으로이동해서 로그인. 회원가입 둘중하게하도록
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}