package kr.ac.konkuk.studyandarchive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    ActionBar actionBar;

    //permissions constants
    // 권한
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //uri of picked image
    Uri image_uri = null;

    //progressbar
    ProgressDialog pd;

    // views
    EditText titleEt, descriptionEt, linkEt;
    ImageView imageIv;
    Button uploadBtn;
    TextView timeView;

    //user info dp-> image
    String name, uid, field, email, dp ;


    //timer
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Archive");
        //액션바에서 뒤로가기 버튼 활성화
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        // post에 포함하기 위한 current user 정보 가져오기
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    dp = ""+ds.child("image").getValue();
                    field = ""+ds.child("field").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //init views
        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        linkEt = findViewById(R.id.pLinkEt);
        imageIv = findViewById(R.id.pImageIv);
        uploadBtn = findViewById(R.id.pArchiveBtn);
        timeView = findViewById(R.id.timeView);


        //timer---------------------------
        Intent archiveIntent = getIntent();
        time = archiveIntent.getExtras().getLong("study_time_long");

        int h   = (int)(time /3600000);
        int m = (int)(time - h*3600000)/60000;
        int s= (int)(time - h*3600000- m*60000)/1000 ;
        String t = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);

        timeView.setText(t);
        //timer---------------------------


        //get image from camera/gallery on Click
        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                showImagePickDialog();
            }
        });




        //upload button click listener
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data (time,title,description) from edit texts
                String studytime = Long.toString(time);
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                String url = linkEt.getText().toString().trim();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(AddPostActivity.this, "타이틀을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(description)){
                    Toast.makeText(AddPostActivity.this, "포스팅 내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }

                if (image_uri==null){
                    //사진없이 포스팅 하는 경우
//                    uploadData(studytime,description,url);
                    Toast.makeText(AddPostActivity.this, "사진을 첨부해 주세요", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(url)){
                    Toast.makeText(AddPostActivity.this, "포스팅 내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                    uploadData(studytime,title,description,String.valueOf(image_uri),"No Url");
                }


            }
        });



    }


    private void uploadData(final String studytime, final String title, final String description, String uri, final String url) {

        pd.setMessage("포스팅을 업로드 하는 중");
        pd.show();

        // post-image name, post-id, post-publish-time을 위한 timestamp
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/"+"post_"+timeStamp;


        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 이미지가 성공적으로 파이어 베이스에 업로드됨
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();

                        if(uriTask.isSuccessful()){
                            //성공적으로 파이어베이스 데베에 url넣음
                            // url is received upload post to firebase database

                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put post info
                            hashMap.put("uid",uid);
                            hashMap.put("uName",name);
                            hashMap.put("uEmail",email);
                            hashMap.put("uField",field);
                            hashMap.put("uDp",dp);
                            hashMap.put("pId",timeStamp);
                            hashMap.put("pTitle",title);
                            hashMap.put("pDescription",description);
                            hashMap.put("pImage",downloadUri);
                            hashMap.put("pStudyTime",studytime);
                            hashMap.put("pUrl",url);
                            hashMap.put("pTime",timeStamp);


                            //post 데이터 저장할 곳 레퍼런스
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            //이 레퍼런스에 데이터 넣기
                            ref.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //db에 포스팅 데이터 넣기 성공
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, "성공적으로 게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();

                                            titleEt.setText("");;
                                            descriptionEt.setText("");
                                            imageIv.setImageURI(null);
                                            image_uri=null;
                                            linkEt.setText("");
                                            timeView.setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //db에 포스팅 데이터 넣기 실패
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed uploading image
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void showImagePickDialog() {
        // 카메라와 갤러리 선택이 가능한 다이얼로그 show
        // 카메라 : 카메라 & storage 퍼미션필요
        // 갤러리 : storage 퍼미션 필요
        //options(camera, galley) to show in dialog
        String[] options = {"카메라", "갤러리"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지를 어디서 가져올까요?");
        //set options dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // item 클릭 handle
                if (which ==0){
                    //카메라
                    if(! checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                }
                if (which ==1){
                    //갤러리
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


    // STORAGE PERMISSION
    private boolean checkStoragePermission(){
        /*
         * storage 권한 체크
         * return true : 가능
         * return false : 불가능
         * */
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);
    }

    // CAMERA PERMISSION
    private boolean checkCameraPermission(){
        /*
         * storage 권한 체크
         * return true : 가능
         * return false : 불가능
         * */
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }

    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        // Intent of Picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        // put image uri
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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




    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private  void checkUserStatus(){

        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user !=null){
            //user is signed in stay here
            // 만약 로그인 되어있다면
            email = user.getEmail();
            uid = user.getUid();

        }else{
            //user not signed in, go to main activity
            // 로그인이 안되있다면, 메인으로이동해서 로그인. 회원가입 둘중하게하도록
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

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



   // HANDLE PERMISSION
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
                        Toast.makeText(this, "카메라 및 저장공간 사용 권한을 승인해주세요", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "저장공간의 사용 권한을 승인해주세요", Toast.LENGTH_SHORT).show();
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

                //set to image view 이미지 뷰에 설정하기
                imageIv.setImageURI(image_uri);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                // 이미지가 카메라에서 선택된 경우, 이미지의 uri get 한다.

                imageIv.setImageURI(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }




}