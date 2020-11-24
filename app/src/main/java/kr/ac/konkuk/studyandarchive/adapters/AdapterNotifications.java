package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import kr.ac.konkuk.studyandarchive.PostDetailActivity;
import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.fragment.ProfileFragment;
import kr.ac.konkuk.studyandarchive.models.ModelNotification;
import kr.ac.konkuk.studyandarchive.models.ModelPost;
import kr.ac.konkuk.studyandarchive.models.ModelUser;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyHolder>{

    private Context mContext;
    private List<ModelNotification> mNotification;

    public AdapterNotifications(Context mContext, List<ModelNotification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_notifications,viewGroup,false);
        return new AdapterNotifications.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        final ModelNotification notification = mNotification.get(i);

        myHolder.text.setText(notification.getText());
        //알림을 발생시킨 사용자
        getUserInfo(myHolder.image_profile, myHolder.username, notification.getUserid());

        // 만약 포스트로 인한 알림이면, 이미지 썸네일 표기 시키기
        if(notification.isIspost()){
            myHolder.post_image.setVisibility(View.VISIBLE);
            getPostImage(myHolder.post_image, notification.getPostid());
        }else { // 포스트 아님 이미지 뷰 없애기
            myHolder.post_image.setVisibility(View.GONE);
        }

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIspost()){
                    // 포스트일때, 누르면 포스트 상세 페이지 postdetail activity
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postId",notification.getPostid()); // 해당 포스트가 클릭될때 해당 id로 포스트 디테일을 가져옴
                    mContext.startActivity(intent);
                }else{
                    // 포스트 아닐때, 누르면 각 유저페이지로 이동
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new ProfileFragment()).commit();

                }
            }
        });




    }



    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{


        public ImageView image_profile, post_image;
        public TextView username, text;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);

        }
    }


    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                //유저아이콘 설정
//                String uImage = user.getImage();
                try{
                        Picasso.get().load(user.getImage()).into(imageView);
                }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_img_white).into(imageView);
                }
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPost post= snapshot.getValue(ModelPost.class);
                //포스트 미리보기 이미지 설정
                String pImage = post.getpImage();
                try{
                    Picasso.get().load(pImage).placeholder(R.drawable.test_image).into(imageView);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
