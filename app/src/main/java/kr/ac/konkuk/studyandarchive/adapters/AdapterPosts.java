package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kr.ac.konkuk.studyandarchive.ColorChips;
import kr.ac.konkuk.studyandarchive.PostDetailActivity;
import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.models.ModelPost;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {



    Context context;
    List<ModelPost> postList;

    // LIKES 디비 node
    private DatabaseReference likeRef;
    private DatabaseReference postRef; //post reference

    boolean mProcessLike = false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    // view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_posts.xml
        ImageView pImageIv;
        TextView uNameTv, uFieldTv, pTitleTv, pStudyTime;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            pImageIv = itemView.findViewById(R.id.post_image);
            uNameTv = itemView.findViewById(R.id.post_writer);
            uFieldTv = itemView.findViewById(R.id.post_field);
            pStudyTime = itemView.findViewById(R.id.post_s_time);
            pTitleTv = itemView.findViewById(R.id.post_name);


        }
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        //get data
        String uid = postList.get(i).getUid();
        String uName = postList.get(i).getuName();
        String uField = postList.get(i).getuField();
        String uDp = postList.get(i).getuDp();
        final String pId = postList.get(i).getpId();
        String pDescription = postList.get(i).getpDescription();
        String pTitle = postList.get(i).getpTitle();
        String pImage = postList.get(i).getpImage();
        String pStudyTime = postList.get(i).getpStudyTime();
        String pTimeStamp = postList.get(i).getpTime();
        String pUrl = postList.get(i).getpUrl();
        String pComments = postList.get(i).getpComments();
        String pLikes = postList.get(i).getpLikes();



        //timestamp 변환 yyyy/mm/dd hh:mm am/pm
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
//        String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();

        Long time = Long.parseLong(pStudyTime);

        int h   = (int)(time /3600000);
        int m = (int)(time - h*3600000)/60000;
        int s= (int)(time - h*3600000- m*60000)/1000 ;
        String pStudyTime_s = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);


        //데이터설정
        myHolder.uNameTv.setText(uName);
        myHolder.pStudyTime.setText(pStudyTime_s);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.uFieldTv.setText(uField);

        //각 컬러칩 색상 입히기
        ColorChips colorChips = new ColorChips(uField ,myHolder.uFieldTv ,context);


        //포스트 이미지 설정
        try{
            Picasso.get().load(pImage).placeholder(R.drawable.test_image).into(myHolder.pImageIv);
        }catch (Exception e){

        }

        /*사용자 프로필 설정
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img_purple).into(myHolder.uPictureIv);
        }catch (Exception e){

        }*/


        // button click handlers
        myHolder.pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 선택하면 포스트 상세 페이지 postdetail activity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pId); // 해당 포스트가 클릭될때 해당 id로 포스트 디테일을 가져옴
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



}
