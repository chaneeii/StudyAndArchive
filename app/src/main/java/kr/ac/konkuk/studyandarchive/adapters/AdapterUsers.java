package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.ac.konkuk.studyandarchive.ColorChips;
import kr.ac.konkuk.studyandarchive.DashboardActivity;
import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.fragment.ProfileFragment;
import kr.ac.konkuk.studyandarchive.models.ModelUser;


// modeluser 내용 리사이클러뷰에 뿌려쥼

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUser> userList;
    FirebaseUser firebaseUser;
    private boolean isfragment;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList, boolean isfragment) {
        this.context = context;
        this.userList = userList;
        this.isfragment = isfragment;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { // i = viewType
        // inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup,false);


        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) { //i = position

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //get data
        final ModelUser user = userList.get(i);
        String userImage = userList.get(i).getImage();
        final String userName = userList.get(i).getName();
        final String userField = userList.get(i).getField();


        //각 컬러칩 색상 입히기
        ColorChips colorChips = new ColorChips(userField,myHolder.mFieldTv,context);


        //setData
        myHolder.btn_folllow.setVisibility(View.VISIBLE);
        myHolder.mNameTv.setText(userName);
        myHolder.mFieldTv.setText(userField);
        try{
            Picasso.get().load(userImage).
                    placeholder(R.drawable.ic_default_img_purple).
                    into(myHolder.mAvatarIv);
        }catch (Exception e){

        }
        isFollowing(user.getUid(), myHolder.btn_folllow);

        
        //handle item click 각 row를 클릭하면 해당 유저페이지로 이동
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, ""+userName, Toast.LENGTH_SHORT).show();
                if(isfragment){
                    SharedPreferences.Editor editor =context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUid());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new ProfileFragment()).addToBackStack(null).commit();
                }else{
                    Intent intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra("uid",user.getUid());
                    context.startActivity(intent);
                }


            }
        });

        // 팔로우 여부 반영, 팔로우안한경우 추가 언팔인 경우 삭제
        myHolder.btn_folllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myHolder.btn_folllow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotifications(user.getUid());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        if(user.getUid().equals(firebaseUser.getUid())){
            myHolder.btn_folllow.setVisibility(View.GONE);
        }




    }

    //알림 , 새로운 팔로우 알림을 Notification에 추가

    private void addNotifications(String userid){

        if( userid != firebaseUser.getUid()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid() );
            hashMap.put("text", "님이 사용자님을 팔로우하기 시작했습니다.");
            hashMap.put("postid","");
            hashMap.put("ispost",false);

            reference.push().setValue(hashMap);
        }

    }

    @Override
    public int getItemCount() {

//        Log.d(TAG, "getItemCount: "+userList.size());
        return userList.size();

    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        public ImageView mAvatarIv;
        public TextView mNameTv, mFieldTv;
        public Button btn_folllow;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv =itemView.findViewById(R.id.avatarIv);
            mNameTv =itemView.findViewById(R.id.nameTv);
            mFieldTv =itemView.findViewById(R.id.fieldTv);
            btn_folllow =itemView.findViewById(R.id.followBtn);


        }
    }

    //팔로우 여부체크
    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    button.setText("following");
                }else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
